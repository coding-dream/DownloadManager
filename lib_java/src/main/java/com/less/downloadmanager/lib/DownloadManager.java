package com.less.downloadmanager.lib;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.less.downloadmanager.lib.bean.DownloadInfo;
import com.less.downloadmanager.lib.bean.ThreadInfo;
import com.less.downloadmanager.lib.db.DatabaseManager;
import com.less.downloadmanager.lib.request.Callback;
import com.less.downloadmanager.lib.request.RequestCall;
import com.less.downloadmanager.lib.util.L;
import com.less.downloadmanager.lib.util.Platform;

public class DownloadManager {
    public static final String TAG = DownloadManager.class.getSimpleName();
    private static DownloadManager sDownloadManager;
    private Platform mPlatform;
    private Map<String, Downloader> mDownloaderMap;
    private DatabaseManager databaseManager;
    private ExecutorService mExecutorService;

    private DownloadManager() {
        // 初始化某些参数
        mPlatform = Platform.get();
        mDownloaderMap = new LinkedHashMap<>();
        mExecutorService = Executors.newFixedThreadPool(Config.DEFAULT_MAX_THREAD_NUMBER);
        databaseManager = DatabaseManager.getInstance();
    }

    public static DownloadManager getInstance(){
    	if (sDownloadManager == null) {
            synchronized (DownloadManager.class) {
                if (sDownloadManager == null) {
                    sDownloadManager = new DownloadManager();
                }
            }
        }
        return sDownloadManager;
    }


    /** UI线程池 */
    public Executor getDelivery() {
        return mPlatform.makeCallbackExecutor();
    }

    public void start(RequestCall call,Callback callback){
        String tag = call.getRequest.mTag;
        if(notRunning(tag)){
            Downloader downloader = new DownloaderImpl(call,mPlatform,callback,mExecutorService,databaseManager,tag);
            mDownloaderMap.put(tag, downloader);
            downloader.start();
        }

    }
    /**
     * 当downloader onConnectCanceled,
     * onConnectFailed,
     * onDownloadCompleted,
     * onDownloadPaused,
     * onDownloadCanceled,
     * onDownloadFailed 等操作时候执行removeDownloader */
    public void removeDownloader(String tag) {
        if (mDownloaderMap.containsKey(tag)) {
            mDownloaderMap.remove(tag);
        }
    }

    public void pause(String tag) {
        if (mDownloaderMap.containsKey(tag)) {
            Downloader downloader = mDownloaderMap.get(tag);
            if (downloader != null) {
                if (downloader.isRunning()) {
                    downloader.pause();
                }
            }
        }
    }

    public void pauseAll() {
        for (Downloader downloader : mDownloaderMap.values()) {
            if (downloader != null) {
                if (downloader.isRunning()) {
                    downloader.pause();
                }
            }
        }
    }

    /** 取消某个文件下载 Downloader -》 多个DownloadTasks（多个ThreadInfo） */
    public void cancel(String tag) {
        if (mDownloaderMap.containsKey(tag)) {
            Downloader downloader = mDownloaderMap.get(tag);
            if (downloader != null) {
                downloader.cancel();
            }
            mDownloaderMap.remove(tag);
        }
    }
    /** 取消所有文件下载 Downloader */
    public void cancelAll() {
        for (Downloader downloader : mDownloaderMap.values()) {
            if (downloader != null) {
                if (downloader.isRunning()) {
                    downloader.cancel();
                }
            }
        }
    }

    private boolean notRunning(String tag) {
        if (mDownloaderMap.containsKey(tag)) {
            Downloader downloader = mDownloaderMap.get(tag);
            if (downloader != null) {
                if (downloader.isRunning()) {
                    L.e("Task has been started!");
                    return false;
                } else {
                    throw new IllegalStateException("Downloader instance with same tag has not been destroyed!");
                }
            }
        }
        return true;
    }

    /** 额外API: 获取数据库中已下载文件的信息. */
    public DownloadInfo getDownloadInfo(String tag) {
        List<ThreadInfo> threadInfos = databaseManager.getThreadInfos(tag);
        DownloadInfo downloadInfo = null;
        if (!threadInfos.isEmpty()) {
            int finished = 0;
            int total = 0;
            for (ThreadInfo info : threadInfos) {
                finished += info.getFinished();
                total += (info.getEnd() - info.getStart());
            }
            int progress = (int) ((long) finished * 100 / total);

            downloadInfo = new DownloadInfo();
            downloadInfo.setFinished(finished);
            downloadInfo.setLength(total);
            downloadInfo.setProgress(progress);
        }
        return downloadInfo;
    }
    /** 额外API: 相应tag的downloader是否正在在运行 */
    public boolean isRunning(String tag) {
        if (mDownloaderMap.containsKey(tag)) {
            Downloader downloader = mDownloaderMap.get(tag);
            if (downloader != null) {
                return downloader.isRunning();
            }
        }
        return false;
    }
}
