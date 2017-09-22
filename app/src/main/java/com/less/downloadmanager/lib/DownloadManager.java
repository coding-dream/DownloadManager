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

public class DownloadManager {
    public static final String TAG = DownloadManager.class.getSimpleName();
    private static DownloadManager sDownloadManager;
    private Config mConfig;
    private Platform mPlatform;
    private Map<String, Downloader> mDownloaderMap;
    private DatabaseManager databaseManager;
    private ExecutorService mExecutorService;

    public DownloadManager(Context context,Config config) {
        if (config == null) {
            mConfig = new Config();
        } else {
            mConfig = config;
        }
        // 初始化某些参数
        mPlatform = Platform.get();
        mDownloaderMap = new LinkedHashMap<>();
        if (context != null) {
            databaseManager = DatabaseManager.getInstance(context);
        }
    }

    public static DownloadManager getInstance(){
        return getInstance(null);
    }

    public static DownloadManager getInstance(Context context) {
        return getInstance(context,null);
    }

    public static DownloadManager getInstance(Context context,Config config) {
        if (sDownloadManager == null) {
            synchronized (DownloadManager.class) {
                if (sDownloadManager == null) {
                    sDownloadManager = new DownloadManager(context,config);
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
        String url = call.getRequest.mUri;// 使用url.hashcode作Tag
        String tag = String.valueOf(url.hashCode());
        if(!alearyRunning(tag)){
            Downloader downloader = new DownloaderImpl(call,mPlatform,callback,mExecutorService,databaseManager,tag,mConfig);
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

    /** 取消某个下载任务 */
    public void cancel(String tag) {
        if (mDownloaderMap.containsKey(tag)) {
            Downloader downloader = mDownloaderMap.get(tag);
            if (downloader != null) {
                downloader.cancel();
            }
            mDownloaderMap.remove(tag);
        }
    }
    /** 取消所有下载任务 */
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
