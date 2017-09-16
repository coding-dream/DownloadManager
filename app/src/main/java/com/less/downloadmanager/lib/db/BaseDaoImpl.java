package com.less.downloadmanager.lib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2017/9/16.
 */

public abstract class BaseDaoImpl<T> implements BaseDao<T>{
    protected String TAG = this.getClass().getSimpleName();// 获取真实子类的TAG名称
    public BaseDaoImpl(Context context){
        mHelper = new DBOpenHelper(context);
    }
    private DBOpenHelper mHelper;

    protected SQLiteDatabase getWritableDatabase() {
        return mHelper.getWritableDatabase();
    }

    protected SQLiteDatabase getReadableDatabase() {
        return mHelper.getReadableDatabase();
    }

    public void closeDB() {
        mHelper.close();
    }

}
