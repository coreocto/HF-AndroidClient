package org.coreocto.dev.hf.androidclient.bean;

import java.util.List;

public class SearchResponse extends Response {

    private Integer count;
    private List<String> files;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

}

