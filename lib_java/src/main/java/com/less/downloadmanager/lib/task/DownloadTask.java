package com.less.downloadmanager.lib.task;

/**
 * Created by Limitless on 2017/9/22.
 * 连接Task完成之后-> 下载Task
 */

public interface DownloadTask extends Runnable{
    void pause();

    void cancel();

    boolean isDownloading();

    boolean isComplete();

    boolean isPaused();

    boolean isCanceled();

    boolean isFailed();
}
