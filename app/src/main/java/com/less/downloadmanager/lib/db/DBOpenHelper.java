package com.less.downloadmanager.lib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.less.downloadmanager.lib.bean.ThreadInfo;

/**
 * Created by Administrator on 2017/9/16.
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "download.db";
    private static final int DB_VERSION = 1;
    private Context mContext;
    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ThreadInfoDao.getInstance(mContext).createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        ThreadInfoDao.getInstance(mContext).dropTable(db);
        ThreadInfoDao.getInstance(mContext).createTable(db);
    }
}
