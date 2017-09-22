package com.less.downloadmanager.lib;

import android.content.Context;

import com.less.downloadmanager.lib.db.DatabaseManager;
import com.less.downloadmanager.lib.request.Callback;
import com.less.downloadmanager.lib.request.RequestCall;
import com.less.downloadmanager.lib.util.L;
import com.less.downloadmanager.lib.util.Platform;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager {
    public static final String TAG = DownloadManager.class.getSimpleName();
    private static DownloadManager sDownloadManager;
    private Platform mPlatform;
    private Map<String, Downloader> mDownloaderMap;
    private DatabaseManager databaseManager;
    private ExecutorService mExecutorService;

    public DownloadManager(Context context) {
        // 初始化某些参数
        mPlatform = Platform.get();
        mDownloaderMap = new LinkedHashMap<>();
        mExecutorService = Executors.newFixedThreadPool(Config.DEFAULT_MAX_THREAD_NUMBER);
        if (context != null) {
            databaseManager = DatabaseManager.getInstance(context);
        }
    }

    public static DownloadManager getInstance(){
        return getInstance(null);
    }

    public static DownloadManager getInstance(Context context) {
        if (sDownloadManager == null) {
            synchronized (DownloadManager.class) {
                if (sDownloadManager == null) {
                    sDownloadManager = new DownloadManager(context);
                }
            }
        }
        return sDownloadManager;
    }

    /** UI线程池 */
    public Executor getDelivery() {
        return mPlatform.makeCallbackExecutor();
    }

    public void execute(RequestCall call,Callback callback){
        CharSequence url = call.getRequest.mUri;// 使用url.hashcode作Tag
        String tag = String.valueOf(url.hashCode());
        if(!alearyRunning(tag)){
            Downloader downloader = new DownloaderImpl(call,mPlatform,callback,mExecutorService,databaseManager,tag);
            mDownloaderMap.put(tag, downloader);
        }

    }
    /**
     * 当downloader onConnectCanceled,
     * onConnectFailed,
     * onDownloadCompleted,
     * onDownloadPaused,
     * onDownloadCanceled,
     * onDownloadFailed 等操作时候执行onDestroyed */
    public void onDestroyed(final String key, Downloader downloader) {
        if (mDownloaderMap.containsKey(key)) {
            mDownloaderMap.remove(key);
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
            mDownloaderMap.remove(tag);
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

    private boolean alearyRunning(String tag) {
        if (mDownloaderMap.containsKey(tag)) {
            Downloader downloader = mDownloaderMap.get(tag);
            if (downloader != null) {
                if (downloader.isRunning()) {
                    L.w("Task has been started!");
                    return true;
                } else {
                    throw new IllegalStateException("Downloader instance with same tag has not been destroyed!");
                }
            }
        }
        return false;
    }

    public void deleteData(String tag) {
        databaseManager.delete(tag);
    }
}
