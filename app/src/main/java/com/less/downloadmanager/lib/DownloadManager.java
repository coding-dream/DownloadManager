package com.less.downloadmanager.lib;

import com.less.downloadmanager.lib.util.Platform;

/**
 * Created by Administrator on 2017/9/16.
 */

public class DownloadManager {
    public static final String TAG = DownloadManager.class.getSimpleName();
    private static DownloadManager sDownloadManager;
    private Config mConfig;
    private Platform mPlatform;

    public DownloadManager(Config config) {
        if (config == null) {
            mConfig = new Config();
        } else {
            mConfig = config;
        }
        mPlatform = Platform.get();
    }

    public static DownloadManager getInstance(Config config) {
        if (sDownloadManager == null) {
            synchronized (DownloadManager.class) {
                if (sDownloadManager == null) {
                    sDownloadManager = new DownloadManager(config);
                }
            }
        }
        return sDownloadManager;
    }

}
