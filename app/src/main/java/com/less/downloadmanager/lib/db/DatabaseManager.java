package com.less.downloadmanager.lib.db;

/**
 * Created by Administrator on 2017/9/16.
 */

import android.content.Context;

import com.less.downloadmanager.lib.bean.ThreadInfo;

/** 相当于Service -> Dao -> Database */
public class DatabaseManager {
    private static DatabaseManager sDataBaseManager;
    private ThreadInfoDao threadInfoDao;

    public DatabaseManager(Context context){
        threadInfoDao = new ThreadInfoDao(context);
    }

    public static DatabaseManager getInstance(Context context) {
        if (sDataBaseManager == null) {
            synchronized (sDataBaseManager) {
                if (sDataBaseManager == null) {
                    sDataBaseManager = new DatabaseManager(context);
                }
            }
        }
        return sDataBaseManager;
    }

    public synchronized void add(ThreadInfo threadInfo){
        threadInfoDao.add(threadInfo);
    }

    public synchronized void delete(String tag) {
        threadInfoDao.delete(tag);
    }

    public synchronized void update(String tag, int threadId, long finished) {
        threadInfoDao.update(tag, threadId, finished);
    }
}
