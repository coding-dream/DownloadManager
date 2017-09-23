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
        db.execSQL("create table " + TABLE_NAME + "(_id integer primary key autoincrement, id integer, tag text, uri text, start long, end long, finished long)");
    }

    public static void dropTable(SQLiteDatabase db) {
        L.d("dropTable");
        db.execSQL("drop table if exists " + TABLE_NAME);
    }

    @Override
    public void add(ThreadInfo info) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into "
                        + TABLE_NAME
                        + "(id, tag, uri, start, end, finished) values(?, ?, ?, ?, ?, ?)",
                new Object[]{info.getId(), info.getTag(), info.getUri(), info.getStart(), info.getEnd(), info.getFinished()});
    }

    @Override
    public void delete(String tag) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from "
                        + TABLE_NAME
                        + " where tag = ?",
                new Object[]{tag});
    }

    @Override
    public void update(String tag, int threadId, long finished) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update "
                        + TABLE_NAME
                        + " set finished = ?"
                        + " where tag = ? and id = ? ",
                new Object[]{finished, tag, threadId});
    }

    @Override
    public List<ThreadInfo> find(String tag) {
        List<ThreadInfo> list = new ArrayList<ThreadInfo>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from "
                        + TABLE_NAME
                        + " where tag = ?",
                new String[]{tag});
        while (cursor.moveToNext()) {
            ThreadInfo info = new ThreadInfo();
            info.setId(cursor.getInt(cursor.getColumnIndex("id")));
            info.setTag(cursor.getString(cursor.getColumnIndex("tag")));
            info.setUri(cursor.getString(cursor.getColumnIndex("uri")));
            info.setEnd(cursor.getLong(cursor.getColumnIndex("end")));
            info.setStart(cursor.getLong(cursor.getColumnIndex("start")));
            info.setFinished(cursor.getLong(cursor.getColumnIndex("finished")));
            list.add(info);
        }
        cursor.close();
        return list;
    }

    @Override
    public List<ThreadInfo> findAll() {
        // nothing to do
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
