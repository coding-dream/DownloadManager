package com.less.downloadmanager.lib.request;

import android.content.Context;

import com.less.downloadmanager.lib.DownloadManager;

/**
 * Created by Administrator on 2017/9/18.
 */

public class RequestCall {
    public GetRequest getRequest;

    public RequestCall(GetRequest getRequest) {
        this.getRequest = getRequest;
    }

    public void execute(Context context,Callback callback) {
        DownloadManager.getInstance(context).execute(this,callback);// 仍然交给DownLoadManager调用，有点EventBus的味道
    }
}
