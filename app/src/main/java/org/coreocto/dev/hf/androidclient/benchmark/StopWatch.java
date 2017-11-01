package org.coreocto.dev.hf.androidclient.benchmark;

public class StopWatch {
    protected long startTime;
    protected long endTime;
    protected String name;

    public StopWatch() {
        this.startTime = -1;
        this.endTime = -1;
    }

    public StopWatch(String name) {
        this();
        this.name = name;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime;
    }

    public void stop() {
        this.endTime = System.currentTimeMillis();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name + "," + startTime + "," + endTime;
    }
}
