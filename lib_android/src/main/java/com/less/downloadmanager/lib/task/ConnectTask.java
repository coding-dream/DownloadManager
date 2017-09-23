package com.less.downloadmanager.lib.task;

/**
 * Created by Limitless on 2017/9/22.
 * 首次连接获取下载文件length的Task
 */

public interface ConnectTask extends Runnable{
    void pause();

    void cancel();

    boolean isConnecting();

    boolean isConnected();

    boolean isPaused();

    boolean isCanceled();

    boolean isFailed();
}
