package com.less.downloadmanager.lib.request;

import com.less.downloadmanager.lib.DownloadException;

import java.io.File;

/**
 * Created by Administrator on 2017/9/18.
 */

public abstract class Callback<T> {
    /**
     * UI Thread
     */
    public abstract void onStart();

    /** UI Thread */
    public void onConnecting() {}

    /** UI Thread */
    public void onConnected(long length,boolean isAcceptRanges){};

    /** UI Thread */
    public void onConnectPaused() {
        // nothing to do(because downloaderImpl.pause() callback.onDownloadPaused()  )
    }

    /** UI Thread */
    public void onConnectCanceled(){
        onDownloadCanceled();// 把onConnectCanceled 当做 onDownloadCanceled 处理
    };

    /** UI Thread */
    public void onConnectFailed(DownloadException e){
        onDownloadFailed(e);// 把onConnectFailed 当做 onDownloadFailed 处理
    };

    /** UI Thread
     * @param finished 已下载
     * @param totalLength 文件总长度
     * @param percent 百分比
     */
    public abstract void onDownloadProgress(long finished, long totalLength , int percent);

    /** UI Thread */
    public abstract void onDownloadPaused();

    public abstract void onDownloadCanceled();

    public abstract void onDownloadFailed(DownloadException e);

    public abstract T parseResponse(File file);

    public abstract void onDownloadCompleted(T t);

    public static Callback CALLBACK_DEFAULT = new Callback() {

        @Override
        public void onStart() {

        }

        @Override
        public void onDownloadProgress(long finished, long totalLength, int percent) {

        }

        @Override
        public void onDownloadPaused() {

        }

        @Override
        public void onDownloadCompleted(Object object) {

        }

        @Override
        public void onDownloadCanceled() {

        }

        @Override
        public void onDownloadFailed(DownloadException e) {

        }

        @Override
        public Object parseResponse(File file) {

            return null;
        }
    };
}
