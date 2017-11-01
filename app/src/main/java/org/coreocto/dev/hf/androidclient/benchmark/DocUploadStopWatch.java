package org.coreocto.dev.hf.androidclient.benchmark;

public class DocUploadStopWatch extends StopWatch {
    protected long fileSize;

    public DocUploadStopWatch(String name, long fileSize) {
        super(name);
        this.fileSize = fileSize;
    }

    public String toString() {
        return super.toString() + "," + fileSize;
    }
}
