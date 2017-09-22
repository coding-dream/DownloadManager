package com.less.downloadmanager.lib.task;

import com.less.downloadmanager.lib.DownloaderImpl;
import com.less.downloadmanager.lib.bean.DownloadInfo;
import com.less.downloadmanager.lib.bean.ThreadInfo;
import com.less.downloadmanager.lib.db.DatabaseManager;
import com.less.downloadmanager.lib.listenter.OnDownloadListener;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/16.
 */

public class MultiDownloadTask extends DownloadTaskImpl{
    private DatabaseManager mDBManager;

    public MultiDownloadTask(DownloadInfo mDownloadInfo, ThreadInfo info, DatabaseManager databaseManager, OnDownloadListener onDownloadListener) {
        super(mDownloadInfo, info, onDownloadListener);
        this.mDBManager = databaseManager;
    }


    @Override
    protected void insertIntoDB(ThreadInfo info) {
        if (!mDBManager.exists(info.getTag(), info.getId())) {
            mDBManager.add(info);
        }
    }

    @Override
    protected int getResponseCode() {
        return HttpURLConnection.HTTP_PARTIAL;
    }

    @Override
    protected void updateDB(ThreadInfo info) {
        mDBManager.update(info.getTag(), info.getId(), info.getFinished());
    }

    @Override
    protected Map<String, String> getHttpHeaders(ThreadInfo info) {
        Map<String, String> headers = new HashMap<String, String>();
        long start = info.getStart() + info.getFinished();
        long end = info.getEnd();
        headers.put("Range", "bytes=" + start + "-" + end);
        return headers;
    }

    @Override
    protected RandomAccessFile getRandomAccessFile(File dir, String name, long offset) throws IOException {
        File file = new File(dir, name);
        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
        raf.seek(offset);
        return raf;
    }

    @Override
    protected String getTag() {
        return this.getClass().getSimpleName();
    }
}

