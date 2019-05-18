package boku.no.nopermission;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {

    private static final String TAG = "ScreenReceiver";
    public static boolean screenActive = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received intent " + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenActive = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenActive = true;
        }
    }
}