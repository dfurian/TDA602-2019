package lbs.lab.maclocation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// *******************************************************************
// *** This file does not need to be read, and should not be edited **
// *******************************************************************

/**
 * ItemListOpenHelper has the lower-level interactions with the database,
 * such as creating it, inserting, querying, clearing, and getting the count.
 */
public class ItemListOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = ItemListOpenHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String ITEM_LIST_TABLE = "item_entries";
    private static final String DATABASE_NAME = "itemlist";

    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_INFO = "info";

    private static final String ITEM_LIST_TABLE_CREATE =
            "CREATE TABLE " + ITEM_LIST_TABLE + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY, " +
                    KEY_TITLE + " TEXT, " +
                    KEY_INFO + " TEXT )";

    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public ItemListOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ITEM_LIST_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG,"Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + ITEM_LIST_TABLE);
        onCreate(db);
    }

    public Item query(int position) {
        String query = "SELECT * FROM " + ITEM_LIST_TABLE +
                " ORDER BY " + KEY_TITLE + " ASC, " + KEY_INFO + " ASC " +
                "LIMIT " + position + ",1";
        Cursor cursor = null;
        Item entry = new Item("ERROR", "ERROR");
        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.rawQuery(query, null);
            cursor.moveToFirst();
            entry.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
            entry.setInfo(cursor.getString(cursor.getColumnIndex(KEY_INFO)));
        } catch (Exception e) {
            Log.d(TAG, "EXCEPTION! " + e);
        } finally {
            cursor.close();
            return entry;
        }
    }

    public long insert(Item item) {
        long newId = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, item.getTitle());
        values.put(KEY_INFO, item.getInfo());
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            newId = mWritableDB.insert(ITEM_LIST_TABLE, null, values);
        } catch (Exception e) {
            Log.d(TAG, "INSERT EXCEPTION! " + e.getMessage());
        }
        return newId;
    }

    public long count() {
        if (mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(mReadableDB, ITEM_LIST_TABLE);
    }

    public void clear() {
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            mWritableDB.execSQL("DELETE FROM " + ITEM_LIST_TABLE);
        } catch (Exception e) {
            Log.d(TAG, "DELETE EXCEPTION! " + e.getMessage());
        }
    }
}
