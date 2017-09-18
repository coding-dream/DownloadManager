package com.less.downloadmanager.lib.request;

import com.less.downloadmanager.lib.DownloadManager;

/**
 * Created by Administrator on 2017/9/18.
 */

public class RequestCall {
    private GetRequest getRequest;

    public RequestCall(GetRequest getRequest) {
        this.getRequest = getRequest;
    }

    public void execute(Callback callback) {
        if (callback != null) {
            callback.onBefore(132353);
        }
        DownloadManager.getInstance().execute(this,callback);// 仍然交给DownLoadManager调用，有点EventBus的味道
    }
}
