package lbs.lab.macintent;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

// ************************************************************
// ** Read and edit this file *********************************
// ************************************************************

/**
 * Activity that should get data via an Intent, then exfiltrate it through the browser.
 */
public class MainActivity extends AppCompatActivity {

    // unique code for the request Intent
    private static final int CODE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * This function is called when the button is clicked, and should start the process
     * of exploiting the vulnerable MACLocation application through an Intent.
     *
     * @param view - the button clicked
     */
    public void act(View view) {
        // TODO
        Intent i = new Intent();
        startActivityForResult(i, CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // once the MACLocation Activity has received the intent in act()
        // we have to handle the data we receive back
        // do this in the same way we exfiltrated data before
        // TODO
        Intent exfiltrateIntent = new Intent(Intent.ACTION_VIEW);
        exfiltrateIntent.setData(Uri.parse("https://www.google.com"));
        startActivity(exfiltrateIntent);
    }
}
