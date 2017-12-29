package org.coreocto.dev.hf.androidclient.bean;

import android.net.Uri;

public class UploadItem {
    private Uri uri;

    private Status status;

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

    public enum Status {
        PENDING,
        FINISHED,
        ERROR
    }

}
