package com.apps.yecotec.fridgetracker.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kenruizinoue on 9/10/17.
 */

public class Section implements Parcelable{

    public int sectionId;
    public String sectionName;
    public int sectionColor;

    public Section(int sectionId, String sectionName, int sectionColor) {
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.sectionColor = sectionColor;
    }

    protected Section(Parcel in) {
        sectionId = in.readInt();
        sectionName = in.readString();
        sectionColor = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(sectionId);
        parcel.writeString(sectionName);
        parcel.writeInt(sectionColor);
    }

    public static final Creator<Section> CREATOR = new Creator<Section>() {
        @Override
        public Section createFromParcel(Parcel in) {
            return new Section(in);
        }

        @Override
        public Section[] newArray(int size) {
            return new Section[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


}
