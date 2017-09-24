package com.less.downloadmanager.lib;

import android.content.Context;

import com.less.downloadmanager.lib.bean.DownloadInfo;
import com.less.downloadmanager.lib.bean.ThreadInfo;
import com.less.downloadmanager.lib.db.DatabaseManager;
import com.less.downloadmanager.lib.request.Callback;
import com.less.downloadmanager.lib.request.RequestCall;
import com.less.downloadmanager.lib.util.L;
import com.less.downloadmanager.lib.util.Platform;

import java.util.LinkedHashMap;
import java.util.List;
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

    private DownloadManager(Context context) {
        // 初始化某些参数
        mPlatform = Platform.get();
        mDownloaderMap = new LinkedHashMap<>();
        mExecutorService = Executors.newFixedThreadPool(Constants.CONFIG.DEFAULT_MAX_THREAD_NUMBER);
        if (context != null) {
            databaseManager = DatabaseManager.getInstance(context);
        }
    }

    protected static DownloadManager getInstance(){
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

    public void start(RequestCall call,Callback callback){
        String tag = call.getRequest.mTag;
        if(notRunning(tag)){
            Downloader downloader = new DownloaderImpl(call,mPlatform,callback,mExecutorService,databaseManager,tag);
            mDownloaderMap.put(tag, downloader);
            downloader.start();
        }

    }
    /**
     * 当downloader (6个)
     * onConnectCanceled(connect task),
     * onConnectFailed(connect task),
     *
     * onDownloadCompleted(all task),
     * onDownloadPaused(all task),
     * onDownloadCanceled(all task),
     * onDownloadFailed(all task)
     * 等操作时候执行removeDownloader */
    public void removeDownloader(String tag) {
        if (mDownloaderMap.containsKey(tag)) {
            mDownloaderMap.remove(tag);
        }
    }

    /** little bug (already fix):
     *
     * 在测试过程中，发现如果快速不断交替点击start,pause
     * 会出现2个以上的Downloader同时下载并回调的情况
     * 分析记录:原bug代码
     *    public void pause(String tag) {
     *        if (mDownloaderMap.containsKey(tag)) {
     *           Downloader downloader = mDownloaderMap.get(tag);
     *               if (downloader != null) {
     *                   if (downloader.isRunning()) {
     *                        downloader.pause();
     *                    }
     *                }
     *               mDownloaderMap.remove(tag);// 重复remove 导致bug
     *       }
     *    }
     * Android&JavaFx等平台在同一线程内,点击btn1,btn2,事件执行都是顺序完成的(等同代码执行顺序),
     * downloader.pause();仅仅是设置标志位(alltask.pause())
     * start pause 几乎同时点击的情况下产生bug,
     * 突发情况: 同时快速点击start-> pause-> 在ConnectTask时期被paused(而此种情况下回调是异步的,见DownloaderImpl.pause方法)
     * 虽然不容易描述,但是mDownloaderMap.remove(tag)会导致start方法的mDownloaderMap.put(tag, downloader);两次以上,
     * 其中一次如果已经启动线程就不再受控制了.
     */
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
                    L.w("Task has been started!");
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
