package org.coreocto.dev.hf.androidclient.bean;

import java.util.List;

public class SearchResponse extends Response {

    private Integer count;
    private List<FileInfo> files;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }

}

