package lbs.lab.maclocation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

// ****************************************************************************
// *** Read this file, but only edit its contents in the 3rd part of the lab **
// ****************************************************************************

/**
 * DatabaseActivity is an Activity, but does not have a GUI.
 * Instead, it is an interface to access the database of stored items.
 */
public class DatabaseActivity extends AppCompatActivity {

    // String keys for labelling data coming in or out
    public static final String ITEM_ACTION = "ITEM_ACTION";
    public static final String SET_ITEMS_ACTION = "SET_ITEMS_ACTION";
    public static final String GET_ITEMS_ACTION = "GET_ITEMS_ACTION";
    public static final String ITEMS_SET = "ITEMS_SET";
    public static final String ITEMS_GET = "ITEMS_GET";

    public static final String TYPE = DatabaseActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent sourceIntent = getIntent();
        Intent resultIntent = new Intent();

        String s = getCallingActivity().getShortClassName();
        String t = getPackageManager().getLaunchIntentForPackage(getPackageName()).getComponent().getShortClassName();
        String u = sourceIntent.getType();

        if (sourceIntent.getType() == null || !sourceIntent.getType().equals(TYPE) || !getCallingActivity().getShortClassName().equals(
                getPackageManager().getLaunchIntentForPackage(getPackageName()).getComponent().getShortClassName())) {
            setResult(Activity.RESULT_CANCELED, resultIntent);
        } else {
            // different actions based on what the Intent used to start this activity contains
            String action = sourceIntent.getStringExtra(ITEM_ACTION);
            ItemListOpenHelper mDB;
            switch (action) {
                case SET_ITEMS_ACTION: // set the database to a list of items
                    mDB = new ItemListOpenHelper(this);
                    mDB.clear();
                    // get the list of items from the source Intent
                    ArrayList<Item> items = sourceIntent.getParcelableArrayListExtra(ITEMS_SET);
                    for (Item i : items) {
                        mDB.insert(i);
                    }
                    setResult(Activity.RESULT_OK, resultIntent);
                    break;
                case GET_ITEMS_ACTION: // get the list of items from the database
                    mDB = new ItemListOpenHelper(this);
                    ArrayList<Item> returned = new ArrayList<>();
                    long count = mDB.count();
                    for (int i = 0; i < count; i++) {
                        returned.add(mDB.query(i));
                    }
                    // put the list of items into the result Intent
                    resultIntent.putExtra(DatabaseActivity.ITEMS_GET, returned);
                    setResult(Activity.RESULT_OK, resultIntent);
                    break;
                default:
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                    break;
            }
        }
        finish();
    }
}
