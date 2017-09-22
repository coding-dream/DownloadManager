package com.less.downloadmanager.lib;

/** 下载执行类 */
public interface Downloader {
    boolean isRunning();

    void start();

    void pause();

    void cancel();
}
