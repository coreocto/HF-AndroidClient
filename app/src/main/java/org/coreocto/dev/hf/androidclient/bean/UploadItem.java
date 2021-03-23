package org.coreocto.dev.hf.androidclient.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class UploadItem implements Parcelable {
    private Uri uri;

    private Status status;

    protected UploadItem(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
    }

    public UploadItem() {

    }

    public static final Creator<UploadItem> CREATOR = new Creator<UploadItem>() {
        @Override
        public UploadItem createFromParcel(Parcel in) {
            return new UploadItem(in);
        }

        @Override
        public UploadItem[] newArray(int size) {
            return new UploadItem[size];
        }
    };

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
    }

    public enum Status {
        PENDING,
        FINISHED,
        ERROR
    }

}
