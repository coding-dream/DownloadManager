package com.less.downloadmanager.lib.bean;

import java.io.RandomAccessFile;

/**
 * Created by Administrator on 2017/9/16.
 */

public class ThreadInfo {
    private int id;
    private String tag;// 下载文件的标识,多线程下载共用此tag.
    private String uri;// 文件url
    private long start;// RandomAccessFile 下载开始位置
    private long end;// RandomAccessFile 下载结束位置
    private long finished;// 本线程已下载

    public ThreadInfo() {
    }

    public ThreadInfo(int id, String tag, String uri, long finished) {
        this.id = id;
        this.tag = tag;
        this.uri = uri;
        this.finished = finished;
    }

    public ThreadInfo(int id, String tag, String uri, long start, long end, long finished) {
        this.id = id;
        this.tag = tag;
        this.uri = uri;
        this.start = start;
        this.end = end;
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }
}
