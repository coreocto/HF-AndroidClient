package org.coreocto.dev.hf.androidclient.benchmark;

public class BenchmarkResult {
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_ITEM = 0;

    private long time = 0;
    private String type = null;
    private String batchId = null;
    private long id = 0;
    private int displayType;
    private int dataSize;
    private int runCnt;

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public int getRunCnt() {
        return runCnt;
    }

    public void setRunCnt(int runCnt) {
        this.runCnt = runCnt;
    }

    public int getDisplayType() {
        return displayType;
    }

    public void setDisplayType(int displayType) {
        this.displayType = displayType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
}
