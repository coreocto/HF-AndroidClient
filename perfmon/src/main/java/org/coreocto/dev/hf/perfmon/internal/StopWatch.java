package org.coreocto.dev.hf.perfmon.internal;

/**
 * Class representing a StopWatch for measuring time.
 */
public class StopWatch {
    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    private long startTime;
    private long endTime;
    private long elapsedTime;

    public StopWatch() {
        //empty
    }

    private void reset() {
        startTime = 0;
        endTime = 0;
        elapsedTime = 0;
    }

    public void start() {
        reset();
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        if (startTime != 0) {
            endTime = System.currentTimeMillis();
            elapsedTime = endTime - startTime;
        } else {
            reset();
        }
    }

    public long getTotalTimeMillis() {
        return (elapsedTime != 0) ? (endTime - startTime) : 0;
    }
}

