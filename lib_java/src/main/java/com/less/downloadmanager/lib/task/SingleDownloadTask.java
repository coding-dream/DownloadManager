package com.less.downloadmanager.lib.task;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.Map;

import com.less.downloadmanager.lib.bean.DownloadInfo;
import com.less.downloadmanager.lib.bean.ThreadInfo;
import com.less.downloadmanager.lib.listenter.OnDownloadListener;
import com.less.downloadmanager.lib.util.L;

/**
 * Created by Administrator on 2017/9/16.
 */

public class SingleDownloadTask extends DownloadTaskImpl {

    public SingleDownloadTask(DownloadInfo downloadInfo, ThreadInfo threadInfo, OnDownloadListener listener) {
        super(downloadInfo, threadInfo, listener);
    }

    @Override
    protected void insertIntoDB(ThreadInfo info) {
        // don't support
    	L.d("SingleDownloadTask: insertIntoDB-> not implement");
    }

    @Override
    protected int getResponseCode() {
        return HttpURLConnection.HTTP_OK;
    }

    @Override
    protected void updateDB(ThreadInfo info) {
        // needn't Override this
    }

    @Override
    protected Map<String, String> getHttpHeaders(ThreadInfo info) {
        // not have header
        return null;
    }

    @Override
    protected RandomAccessFile getRandomAccessFile(File dir, String name, long offset) throws IOException {
        File file = new File(dir, name);
        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
        raf.seek(0);
        return raf;
    }
}
