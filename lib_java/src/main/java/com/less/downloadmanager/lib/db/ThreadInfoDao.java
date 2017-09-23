package com.less.downloadmanager.lib.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.less.downloadmanager.lib.bean.ThreadInfo;

/**
 * Created by Administrator on 2017/9/16.
 */

public class ThreadInfoDao extends BaseDaoImpl<ThreadInfo>{
    private static final String TABLE_NAME = ThreadInfo.class.getSimpleName();
    private static ThreadInfoDao instance;

    /** 这里Dao如果用单例,注意context对象的释放,建议使用ApplicationContext对象 */
    public static ThreadInfoDao getInstance(){
        if (instance == null) {
            synchronized (ThreadInfo.class) {
                if (instance == null) {
                    instance = new ThreadInfoDao();
                }
            }
        }
        return instance;
    }

    public void createTable() {
        String sql = String.format("create table if not exists %s(_id integer primary key autoincrement, id integer, tag text, uri text, start long, end long, finished long)", TABLE_NAME);
        SqlHelper.execSQL(sql, null);
    }

    public void dropTable() {
    	String sql = String.format("drop table if exists %s", TABLE_NAME);
        SqlHelper.execSQL(sql, null);
    }

    @Override
    public void add(ThreadInfo info) {
    	String [] params = new String[]{info.getId()+"", info.getTag(), info.getUri(), info.getStart()+"", info.getEnd()+"", info.getFinished()+""};
    	SqlHelper.execSQL("insert into "
                + TABLE_NAME
                + "(id, tag, uri, start, end, finished) values(?, ?, ?, ?, ?, ?)", params);
    }

    @Override
    public void delete(String tag) {
    	SqlHelper.execSQL("delete from "
                + TABLE_NAME
                + " where tag = ?", new String[]{tag});
    }

    @Override
    public void update(String tag, int threadId, long finished) {
    	SqlHelper.execSQL("update "
                + TABLE_NAME
                + " set finished = ?"
                + " where tag = ? and id = ? ", new String[]{finished+"", tag, threadId+""});
    }

    @Override
    public List<ThreadInfo> find(String tag) {
        List<ThreadInfo> list = new ArrayList<ThreadInfo>();

        List<Map<String,String>> results = SqlHelper.rawSQLMapList("select * from "
                + TABLE_NAME
                + " where tag = ?", new String[]{tag});

        for(Map<String,String> result : results){
        	ThreadInfo info = new ThreadInfo();
        	info.setId(Integer.parseInt(result.get("id")));
        	info.setTag(result.get("tag"));
        	info.setUri(result.get("uri"));
        	info.setEnd(Long.parseLong(result.get("end")));
        	info.setStart(Long.parseLong(result.get("start")));
        	info.setFinished(Long.parseLong(result.get("finished")));
        	list.add(info);
        }

        return list;
    }

    @Override
    public List<ThreadInfo> findAll() {
        // nothing to do
        return null;
    }

    @Override
    public boolean exists(String tag, int threadId) {
    	List<Map<String,String>> results = SqlHelper.rawSQLMapList("select * from "
                        + TABLE_NAME
                        + " where tag = ? and id = ?", new String[]{tag, threadId + ""});
    	if(results.size() > 0){
    		return true;
    	}
    	return false;
    }
}
