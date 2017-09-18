package com.less.downloadmanager.lib.request;

import com.less.downloadmanager.lib.DownloadManager;

import java.io.File;

/**
 * Created by Administrator on 2017/9/18.
 */

public abstract class FileCallBack extends Callback<File>{

    @Override
    public File parseNetworkResponse(int id) throws Exception {
        return saveFile();
    }

    public File saveFile(){
        // io 流操作

        // UI 线程
        DownloadManager.getInstance().getDelivery().execute(null);

        return new File("");
    }
}
