package org.coreocto.dev.hf.androidclient.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class FileInfo implements Parcelable {
    public String name;
    public int type;
    private String feiv;

    protected FileInfo(Parcel in) {
        name = in.readString();
        type = in.readInt();
        feiv = in.readString();
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel in) {
            return new FileInfo(in);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };

    public String getFeiv() {
        return feiv;
    }

    public void setFeiv(String feiv) {
        this.feiv = feiv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(type);
        dest.writeString(feiv);
    }
}
