package com.less.downloadmanager.lib;

/** 下载执行类 */
public interface Downloader {
    interface OnDownloaderDestroyedListener {
        void onDestroyed(String key, Downloader downloader);
    }

    boolean isRunning();

    void start();

    void pause();

    void cancel();

    void onDestroy();
}
