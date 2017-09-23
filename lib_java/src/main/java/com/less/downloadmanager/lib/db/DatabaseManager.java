package com.less.downloadmanager.lib.db;

import java.util.List;

import com.less.downloadmanager.lib.bean.ThreadInfo;

/** 相当于Service -> Dao -> Database */
public class DatabaseManager {
    private static DatabaseManager sDataBaseManager;
    private ThreadInfoDao threadInfoDao;

    public DatabaseManager(){
        threadInfoDao = new ThreadInfoDao();

        createTable();
    }

    private synchronized void createTable() {
    	threadInfoDao.createTable();
	}

	public static DatabaseManager getInstance() {
        if (sDataBaseManager == null) {
            synchronized (DatabaseManager.class) {
                if (sDataBaseManager == null) {
                    sDataBaseManager = new DatabaseManager();
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

    public synchronized List<ThreadInfo> getThreadInfos(String tag) {
        return threadInfoDao.find(tag);
    }

    /**
     * 使用2个条件找到 相应的ThreadInfo,因为每个文件下载所对应的线程数组(字段threadId 【非主键】都是1,2,3,4,5等),
     * 而字段tag标识url,准确找到某个ThreadInfo
     */
    public synchronized boolean exists(String tag, int threadId) {
        return threadInfoDao.exists(tag, threadId);
    }
}
