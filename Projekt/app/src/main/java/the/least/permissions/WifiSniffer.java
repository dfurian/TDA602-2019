package the.least.permissions;

import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

interface WifiListener {
    void onSniffed(String data);
}

class WifiSniffer extends AsyncTask<NetworkInfo.DetailedState, Void, String> {
    private WifiListener listener;

    WifiSniffer(WifiListener listener) {
        super();
        this.listener = listener;
        Log.d(TAG, "created");
    }

    private static final String TAG = "WifiSniffer";

    @Override
    protected String doInBackground(NetworkInfo.DetailedState... strings) {
        Log.d(TAG, "doInBackground start");
        return strings[0].toString();
    }

    @Override
    protected void onPostExecute(String wifiData) {
        Log.d(TAG, "onPostExecute start with data " + wifiData);
        super.onPostExecute(wifiData);
        listener.onSniffed(wifiData);
    }
}