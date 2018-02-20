package org.coreocto.dev.hf.androidclient.benchmark;

@Deprecated
public class AddTokenStopWatch extends StopWatch {
    protected int wordCount;

    public AddTokenStopWatch(String name) {
        super(name);
    }

    public AddTokenStopWatch(){
        super();
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public String toString() {
        return super.toString() + "," + wordCount;
    }
}
