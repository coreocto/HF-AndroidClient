package org.coreocto.dev.hf.androidclient.benchmark;

@Deprecated
public class DocEncryptStopWatch extends StopWatch {
    protected long fileSize;

    public DocEncryptStopWatch(String name, long fileSize) {
        super(name);
        this.fileSize = fileSize;
    }

    public String toString() {
        return super.toString() + "," + fileSize;
    }
}
