package com.less.downloadmanager.lib;

import com.less.downloadmanager.lib.bean.DownloadInfo;
import com.less.downloadmanager.lib.bean.DownloadStatus;
import com.less.downloadmanager.lib.bean.ThreadInfo;
import com.less.downloadmanager.lib.db.DatabaseManager;
import com.less.downloadmanager.lib.listenter.OnConnectListener;
import com.less.downloadmanager.lib.listenter.OnDownloadListener;
import com.less.downloadmanager.lib.request.Callback;
import com.less.downloadmanager.lib.request.RequestCall;
import com.less.downloadmanager.lib.task.ConnectTask;
import com.less.downloadmanager.lib.task.ConnectTaskImpl;
import com.less.downloadmanager.lib.task.DownloadTask;
import com.less.downloadmanager.lib.task.MultiDownloadTask;
import com.less.downloadmanager.lib.task.SingleDownloadTask;
import com.less.downloadmanager.lib.util.Platform;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/** 下载执行类 */
public class DownloaderImpl implements Downloader, OnDownloadListener{
    private RequestCall mCall;

    private Platform mPlatform;

    private Executor mExecutor;

    private Callback mCallback;

    private DatabaseManager mDBManager;

    private String mTag;

    // ================= //
    private int mStatus;

    private DownloadInfo mDownloadInfo;

    private ConnectTask mConnectTask; // 连接Task

    private List<DownloadTask> mDownloadTasks;// 下载 Tasks（每一个文件下载 -> 多个线程的DownloadTasks）

    public DownloaderImpl(RequestCall call, Platform platform, Callback callback, ExecutorService executorService, DatabaseManager databaseManager, String tag) {
        this.mCall = call;
        this.mPlatform = platform;
        this.mCallback = callback;
        this.mExecutor = executorService;
        this.mDBManager = databaseManager;
        this.mTag = tag;

        mDownloadInfo = new DownloadInfo(call.getRequest.mName, call.getRequest.mUri, call.getRequest.mFolder);
        mDownloadTasks = new LinkedList<>();
    }

    @Override
    public boolean isRunning() {
        return mStatus == DownloadStatus.STATUS_STARTED
                || mStatus == DownloadStatus.STATUS_CONNECTING
                || mStatus == DownloadStatus.STATUS_CONNECTED
                || mStatus == DownloadStatus.STATUS_PROGRESS;
    }

    @Override
    public void start() {
        mStatus = DownloadStatus.STATUS_STARTED;
        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                mCallback.onStart();
            }
        });
        mConnectTask = new ConnectTaskImpl(mCall.getRequest.mUri, new OnConnectListener() {
            @Override
            public void onConnecting() {
                mStatus = DownloadStatus.STATUS_CONNECTING;
                mPlatform.execute(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onConnecting();
                    }
                });
            }

            @Override
            public void onConnected(final long length, final boolean isAcceptRanges) {
                if (mConnectTask.isCanceled()) {
                    // despite connection is finished, the entire downloader is canceled
                    onConnectCanceled();
                } else {
                    mStatus = DownloadStatus.STATUS_CONNECTED;
                    mPlatform.execute(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.onConnected(length,isAcceptRanges);
                        }
                    });

                    // =============== 下载 ===============
                    mDownloadInfo.setAcceptRanges(isAcceptRanges);
                    mDownloadInfo.setLength(length);
                    download(length, isAcceptRanges);
                }
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
        });
        mExecutor.execute(mConnectTask);
    }

    @Override
    public void pause() {
        // 停止连接任务
        if (mConnectTask != null) {
            mConnectTask.pause();
        }
        // 停止下载任务组
        for (DownloadTask task : mDownloadTasks) {
            task.pause();
        }
        //
        if (mStatus != DownloadStatus.STATUS_PROGRESS) {
            onDownloadPaused();
        }
    }

    @Override
    public void cancel() {
        // 取消连接任务
        if (mConnectTask != null) {
            mConnectTask.cancel();
        }
        // 取消下载任务组
        for (DownloadTask task : mDownloadTasks) {
            task.cancel();
        }
        //
        if (mStatus != DownloadStatus.STATUS_PROGRESS) {
            onDownloadCanceled();
        }
    }

    @Override
    public void onDestroy() {

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

    // ===================== 下载 =====================
    private void download(long length, boolean acceptRanges) {
        mStatus = DownloadStatus.STATUS_PROGRESS;
        initDownloadTasks(length, acceptRanges);
        // start tasks
        for (DownloadTask downloadTask : mDownloadTasks) {
            mExecutor.execute(downloadTask);
        }
    }

    private void initDownloadTasks(long length, boolean acceptRanges) {
        mDownloadTasks.clear();
        if (acceptRanges) {
            List<ThreadInfo> threadInfos = getMultiThreadInfos(length);
            // init finished
            int finished = 0;
            for (ThreadInfo threadInfo : threadInfos) {
                finished += threadInfo.getFinished();
            }
            mDownloadInfo.setFinished(finished);
            for (ThreadInfo info : threadInfos) {
                mDownloadTasks.add(new MultiDownloadTask(mDownloadInfo, info, mDBManager,this));
            }
        } else {
            ThreadInfo info = getSingleThreadInfo();
            mDownloadTasks.add(new SingleDownloadTask(mDownloadInfo, info, this));
        }
    }

    private List<ThreadInfo> getMultiThreadInfos(long length) {
        // init threadInfo from db
        final List<ThreadInfo> threadInfos = mDBManager.getThreadInfos(mTag);
        if (threadInfos.isEmpty()) {
            int threadNum = Config.DEFAULT_THREAD_NUMBER;
            for (int i = 0; i < threadNum; i++) {
                // calculate average
                final long average = length / threadNum;
                final long start = average * i;
                final long end;
                if (i == threadNum - 1) {
                    end = length;
                } else {
                    end = start + average - 1;
                }
                // 每个downloader 的线程组的tag是一样的，表示同一个任务(好似外键)
                ThreadInfo threadInfo = new ThreadInfo(i, mTag, mCall.getRequest.mUri, start, end, 0);
                threadInfos.add(threadInfo);
            }
        }
        return threadInfos;
    }

    private ThreadInfo getSingleThreadInfo() {
        ThreadInfo threadInfo = new ThreadInfo(0, mTag, mCall.getRequest.mUri, 0);
        return threadInfo;
    }


}
