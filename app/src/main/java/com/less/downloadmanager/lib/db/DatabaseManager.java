package com.less.downloadmanager.lib.db;

/**
 * Created by Administrator on 2017/9/16.
 */

import android.content.Context;

import com.less.downloadmanager.lib.bean.ThreadInfo;

import java.util.List;

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

    public List<ThreadInfo> getThreadInfos(String tag) {
        return threadInfoDao.find(tag);
    }

    /**
     * 使用2个条件找到 相应的ThreadInfo,因为每个文件下载所对应的线程数组(字段threadId 【非主键】都是1,2,3,4,5等),
     * 而字段tag标识url,准确找到某个ThreadInfo
     */
    public boolean exists(String tag, int threadId) {
        return threadInfoDao.exists(tag, threadId);
    }
}
