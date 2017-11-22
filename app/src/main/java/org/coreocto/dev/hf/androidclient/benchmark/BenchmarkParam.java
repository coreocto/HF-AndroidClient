package org.coreocto.dev.hf.androidclient.benchmark;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkParam {
    private List<String> includedTestIds;
    private int dataSize;
    private int runCnt;
    //private boolean allocMem;
    //private boolean explicitGc;

    public BenchmarkParam(int dataSize, int runCnt/*, boolean allocMem, boolean explicitGc*/) {
        this.dataSize = dataSize;
        this.runCnt = runCnt;
        this.includedTestIds = new ArrayList<>();
        //this.allocMem = allocMem;
        //this.explicitGc = explicitGc;
    }

//    public boolean isExplicitGc() {
//        return explicitGc;
//    }

//    public boolean isAllocMem() {
//        return allocMem;
//    }

    public int getDataSize() {
        return dataSize;
    }

    public int getRunCnt() {
        return runCnt;
    }

    public void addTest(String testId) {
        this.includedTestIds.add(testId);
    }

    public int getTestCnt() {
        return includedTestIds.size();
    }

    public List<String> getTestIds() {
        return includedTestIds;
    }
}
