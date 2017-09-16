package com.less.downloadmanager.lib.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by Administrator on 2017/9/16.
 */

public interface BaseDao<T> {
    void add(T t);
    void delete(String tag);
    void update(String tag, int threadId, long finished);
    List<T> find(String tag);
    List<T> findAll();
    boolean exists(String tag, int threadId);
}
