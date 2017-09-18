package com.less.downloadmanager.lib.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.less.downloadmanager.lib.bean.ThreadInfo;
import com.less.downloadmanager.lib.util.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/16.
 */

public class ThreadInfoDao extends BaseDaoImpl<ThreadInfo>{
    private static final String TABLE_NAME = ThreadInfo.class.getSimpleName();
    private static ThreadInfoDao instance;

    public ThreadInfoDao(Context context) {
        super(context);
    }
    /** 这里Dao如果用单例,注意context对象的释放,建议使用ApplicationContext对象 */
    public static ThreadInfoDao getInstance(Context context){
        if (instance == null) {
            synchronized (ThreadInfo.class) {
                if (instance == null) {
                    instance = new ThreadInfoDao(context);
                }
            }
        }
        return instance;
    }

    public static void createTable(SQLiteDatabase db) {
        L.d("createTable");
    }

    public static void dropTable(SQLiteDatabase db) {
        L.d("dropTable");
    }

    @Override
    public void add(ThreadInfo threadInfo) {

    }

    @Override
    public void delete(String tag) {

    }

    @Override
    public void update(String tag, int threadId, long finished) {

    }

    @Override
    public List<ThreadInfo> find(String tag) {
        return null;
    }

    @Override
    public List<ThreadInfo> findAll() {
        return null;
    }

    @Override
    public boolean exists(String tag, int threadId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "
                        + TABLE_NAME
                        + " where tag = ? and id = ?",
                new String[]{tag, threadId + ""});
        boolean isExists = cursor.moveToNext();
        cursor.close();
        return isExists;
    }
}
