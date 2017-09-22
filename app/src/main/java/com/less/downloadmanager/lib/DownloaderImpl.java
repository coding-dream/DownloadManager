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

import java.io.File;
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
                mStatus = DownloadStatus.STATUS_CONNECTING;// 判断isRunning时需要
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
                onDownloadPaused();
            }

            @Override
            public void onConnectCanceled() {
                deleteFromDB();
                deleteFile();
                mStatus = DownloadStatus.STATUS_CANCELED;
                mPlatform.execute(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onConnectCanceled();
                    }
                });
                onDestroy();
            }

            @Override
            public void onConnectFailed(final DownloadException de) {
                if (mConnectTask.isCanceled()) {
                    // despite connection is failed, the entire downloader is canceled
                    onConnectCanceled();
                } else if (mConnectTask.isPaused()) {
                    // despite connection is failed, the entire downloader is paused
                    onDownloadPaused();
                } else {
                    mStatus = DownloadStatus.STATUS_FAILED;
                    mPlatform.execute(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.onConnectFailed(de);
                        }
                    });
                    onDestroy();
                }
            }
        });
        mExecutor.execute(mConnectTask);
    }

    @Override
    public void pause() {
        // 停止连接任务
        if (mConnectTask != null) {
            mConnectTask.pause();// mConnectTask.pause() 设置ConnectTask中的标志,正在网络连接过程中 if(flag) 触发回调.
        }
        // 停止下载任务组
        for (DownloadTask task : mDownloadTasks) {
            task.pause();// task.pause() 设置task.pause()中的标志,正在下载过程中, if(flag) 触发回调onDownloadPaused,由于多线程,会触发多次,但由于不满足isAllPaused
        }
        // 由于task是一组任务,for(task : tasks) 不满足isAllPaused,执行到此处才满足isAllPaused,此时主动调用onDownloadPaused才执行方法体回调给前端（当所有线程都停下来,取消操作只需回调给用户一次即可）。
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
        // nothing to do
    }

    @Override
    public void onDownloadProgress(final long finished, final long length) {
        final int percent = (int) (finished * 100 / length);
        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                mCallback.onDownloadProgress(finished, length, percent);
            }
        });
    }

    @Override
    public void onDownloadCompleted() {
        if (isAllComplete()) {
            deleteFromDB();
            mStatus = DownloadStatus.STATUS_COMPLETED;
            mPlatform.execute(new Runnable() {
                @Override
                public void run() {
                    mCallback.onDownloadCompleted();
                }
            });
            onDestroy();
        }
    }

    @Override
    public void onDownloadPaused() {
        if (isAllPaused()) {
            mStatus = DownloadStatus.STATUS_PAUSED;
            mPlatform.execute(new Runnable() {
                @Override
                public void run() {
                    mCallback.onDownloadPaused();
                }
            });
            onDestroy();
        }
    }

    @Override
    public void onDownloadCanceled() {
        if (isAllCanceled()) {
            deleteFromDB();
            deleteFile();
            mStatus = DownloadStatus.STATUS_CANCELED;
            mPlatform.execute(new Runnable() {
                @Override
                public void run() {
                    mCallback.onDownloadCanceled();
                }
            });
            onDestroy();
        }
    }

    @Override
    public void onDownloadFailed(final DownloadException de) {
        if (isAllFailed()) {
            mStatus = DownloadStatus.STATUS_FAILED;
            mPlatform.execute(new Runnable() {
                @Override
                public void run() {
                    mCallback.onDownloadFailed(de);
                }
            });
            onDestroy();
        }
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

    /**  */
    private boolean isAllComplete() {
        boolean allFinished = true;
        for (DownloadTask task : mDownloadTasks) {
            if (!task.isComplete()) {
                allFinished = false;
                break;
            }
        }
        return allFinished;
    }

    /**
     * isAllComplete,isAllFailed 和isAllPaused,isAllCanceled 稍有不一样的地方是,后者可以手动暂停或取消一个下载(即所有Task),
     * 但是前者则有可能一个下载 中的某个task(thread)出现错误,这样就好像下载到某一个位置卡住了一样,由于保存到数据库,所以暂停一下
     * 再继续下载(从数据库重新构建threads)就OK了,如果一个出现故障->程序设计为《下载失败》的话 就失去多线程断点下载的意义了。
     */
    private boolean isAllFailed() {
        boolean allFailed = true;
        for (DownloadTask task : mDownloadTasks) {
            if (task.isDownloading()) {
                allFailed = false;
                break;
            }
        }
        return allFailed;
    }

    private boolean isAllPaused() {
        boolean allPaused = true;
        for (DownloadTask task : mDownloadTasks) {
            if (task.isDownloading()) {
                allPaused = false;
                break;
            }
        }
        return allPaused;
    }

    private boolean isAllCanceled() {
        boolean allCanceled = true;
        for (DownloadTask task : mDownloadTasks) {
            if (task.isDownloading()) {
                allCanceled = false;
                break;
            }
        }
        return allCanceled;
    }

    private void deleteFromDB() {
        mDBManager.delete(mTag);
    }

    private void deleteFile() {
        File file = new File(mDownloadInfo.getDir(), mDownloadInfo.getName());
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }
}
