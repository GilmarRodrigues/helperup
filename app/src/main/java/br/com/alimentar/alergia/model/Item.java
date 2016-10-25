package br.com.alimentar.alergia.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gilmar on 24/10/16.
 */

public class Item implements Parcelable{
    public static final String KEY = "item";
    private int iconId;
    private String label;

    public Item(int iconId, String label) {
        this.iconId = iconId;
        this.label = label;
    }

    protected Item(Parcel in) {
        iconId = in.readInt();
        label = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public int getIconId() {
        return iconId;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(iconId);
        dest.writeString(label);
    }
}
