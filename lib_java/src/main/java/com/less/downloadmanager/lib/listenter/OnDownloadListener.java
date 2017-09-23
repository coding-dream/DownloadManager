package com.less.downloadmanager.lib.listenter;

import com.less.downloadmanager.lib.DownloadException;

/**
 * Created by Administrator on 2017/9/22.
 */

public interface OnDownloadListener {
    void onDownloadConnecting();

    void onDownloadProgress(long finished, long length);

    void onDownloadCompleted();

    void onDownloadPaused();

    void onDownloadCanceled();

    void onDownloadFailed(DownloadException de);
}
