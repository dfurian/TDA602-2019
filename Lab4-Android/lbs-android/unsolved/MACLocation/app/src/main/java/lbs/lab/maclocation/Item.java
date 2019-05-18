package lbs.lab.maclocation;

import android.os.Parcel;
import android.os.Parcelable;

// ***************************************************
// *** Read this file, but do not edit its contents **
// ***************************************************

/**
 * Item describes one data point, with two Strings (title, info) as the information each point holds.
 * Implements Parcelable so it can be written and read when sent between Activities in Intents.
 */
public class Item implements Parcelable {

    private String title;
    private String info;

    public Item(String title, String info) {
        this.title = title;
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    // ******************************************
    // ********* Parcelable Implementation ******
    // ******************************************

    public Item(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }

    };

    public void readFromParcel(Parcel in) {
        title = in.readString();
        info = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(info);
    }
}
