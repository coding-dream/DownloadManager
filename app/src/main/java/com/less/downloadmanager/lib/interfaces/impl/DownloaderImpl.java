package com.less.downloadmanager.lib.interfaces.impl;

import com.less.downloadmanager.lib.interfaces.Downloader;

/**
 * Created by Administrator on 2017/9/16.
 */

public class DownloaderImpl implements Downloader{
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
}
