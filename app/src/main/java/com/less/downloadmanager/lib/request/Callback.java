package com.less.downloadmanager.lib.request;

import com.less.downloadmanager.lib.DownloadException;

/**
 * Created by Administrator on 2017/9/18.
 */

public abstract class Callback<T> {
    /** UI Thread */
    public void onStart() {}

    /** UI Thread */
    public void onConnecting() {}

    /** UI Thread */
    public void onConnectCanceled(){};

    /** UI Thread */
    public void onConnected(long length,boolean isAcceptRanges){};

    /** UI Thread */
    public void onConnectFailed(DownloadException e){};

    /** UI Thread */
    public void onDownloadPaused(){};

    /** UI Thread
     * @param finished 已下载
     * @param totalLength 文件总长度
     * @param percent 百分比
     */
    public void onDownloadProgress(long finished, long totalLength , int percent) {}

    public void onDownloadCompleted() {}

    public void onDownloadCanceled() {}

    public void onDownloadFailed(DownloadException e) {}

    /** Thread Pool */
    public abstract T parseNetworkResponse(int id) throws Exception;

    public abstract void onError(Exception e, int id);

    public abstract void onResponse(T response, int id);

    public static Callback CALLBACK_DEFAULT = new Callback() {

        @Override
        public Object parseNetworkResponse(int id) throws Exception {
            // nothing to do
            return null;
        }

        @Override
        public void onError(Exception e, int id) {
            // nothing to do
        }

        @Override
        public void onResponse(Object response, int id) {
            // nothing to do
        }
    };
}
