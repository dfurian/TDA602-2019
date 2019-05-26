package the.least.permissions;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

interface WifiListener {
    void onSniffed(String data);
}

class WifiSniffer extends AsyncTask<String, Void, String> {
    private WifiListener listener;

    WifiSniffer(WifiListener listener) {
        super();
        this.listener = listener;
        Log.d(TAG, "created");
    }

    private static final String TAG = "WifiSniffer";

    @Override
    protected String doInBackground(String... webServiceUrl) {
        Log.d(TAG, "doInBackground start");
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(webServiceUrl[0]).openConnection();
            if (connection.getResponseCode() == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String readLine;
                while ((readLine = bufferedReader.readLine()) != null) {
                    response.append(readLine);
                }
                return response.toString();
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception while invoking the wi-fi webservice", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String wifiData) {
        Log.d(TAG, "onPostExecute start with data " + wifiData);
        super.onPostExecute(wifiData);
        listener.onSniffed(wifiData);
    }
}