package org.coreocto.dev.hf.androidclient.benchmark;

public class IndexUploadStopWatch extends StopWatch {
    protected long fileSize;

    public IndexUploadStopWatch(String name, long fileSize) {
        super(name);
        this.fileSize = fileSize;
    }

    public String toString() {
        return super.toString() + "," + fileSize;
    }
}
