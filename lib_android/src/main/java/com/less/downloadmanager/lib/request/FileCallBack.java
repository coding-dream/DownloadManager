package com.less.downloadmanager.lib.request;

import com.less.downloadmanager.lib.DownloadException;
import com.less.downloadmanager.lib.DownloadManager;

import java.io.File;

/**
 * Created by Administrator on 2017/9/18.
 */

public abstract class FileCallBack extends Callback<File>{

    @Override
    public abstract void onStart(String tag) ;

    @Override
    public abstract void onDownloadProgress(String tag,long finished, long totalLength, int percent);

    @Override
    public abstract void onDownloadPaused();

    @Override
    public abstract void onDownloadCanceled();

    @Override
    public abstract void onDownloadFailed(DownloadException e);

    @Override
    public File parseResponse(File file) {
        // 对原始文件进行操作(暂时不作修改)
        // ...
        return file;
    }

    @Override
    public abstract void onDownloadCompleted(File file);

}
