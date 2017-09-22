package com.less.downloadmanager.lib.listenter;

import com.less.downloadmanager.lib.DownloadException;

/**
 * Created by Administrator on 2017/9/22.
 */

public interface OnConnectListener {
    void onConnecting();

    void onConnected(long time, long length, boolean isAcceptRanges);

    void onConnectPaused();

    void onConnectCanceled();

    void onConnectFailed(DownloadException de);
}
