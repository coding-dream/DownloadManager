package com.less.downloadmanager.lib.db;

/**
 * Created by Administrator on 2017/9/16.
 */

public abstract class BaseDaoImpl<T> implements BaseDao<T>{
    protected String TAG = this.getClass().getSimpleName();// 获取真实子类的TAG名称
}
