package com.less.downloadmanager.lib;

import com.less.downloadmanager.lib.bean.DownloadInfo;
import com.less.downloadmanager.lib.db.DatabaseManager;
import com.less.downloadmanager.lib.listenter.OnConnectListener;
import com.less.downloadmanager.lib.listenter.OnDownloadListener;
import com.less.downloadmanager.lib.request.Callback;
import com.less.downloadmanager.lib.request.RequestCall;
import com.less.downloadmanager.lib.task.ConnectTask;
import com.less.downloadmanager.lib.task.DownloadTask;
import com.less.downloadmanager.lib.util.Platform;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/** 下载执行类 */
public class DownloaderImpl implements Downloader, OnConnectListener, OnDownloadListener{
    private RequestCall mCall;

    private Platform mPlatform;

    private Executor mExecutor;

    private Callback mCallback;

    private DatabaseManager mDBManager;

    private String mTag;

    private Config mConfig;

    private OnDownloaderDestroyedListener mListener;

    private int mStatus;

    private DownloadInfo mDownloadInfo;

    private ConnectTask mConnectTask;

    private List<DownloadTask> mDownloadTasks;

    public DownloaderImpl(RequestCall call, Platform platform, Callback callback, ExecutorService mExecutorService, DatabaseManager databaseManager, String tag, Config mConfig) {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onConnected(long time, long length, boolean isAcceptRanges) {

    }

    @Override
    public void onConnectPaused() {

    }

    @Override
    public void onConnectCanceled() {

    }

    @Override
    public void onConnectFailed(DownloadException de) {

    }

    @Override
    public void onDownloadConnecting() {

    }

    @Override
    public void onDownloadProgress(long finished, long length) {

    }

    @Override
    public void onDownloadCompleted() {

    }

    @Override
    public void onDownloadPaused() {

    }

    @Override
    public void onDownloadCanceled() {

    }

    @Override
    public void onDownloadFailed(DownloadException de) {

    }
}
