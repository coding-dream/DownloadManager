package com.less.downloadmanager.lib.request;

import java.io.File;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/18.
 */

public class GetRequest { // Builder模式分离为两个类
    public String mUri;

    public File mFolder;

    public String mName;

    public String mTag;

    protected Map<String, String> mParams;

    public GetRequest(String uri, File folder, String name,String tag) {
        this.mUri = uri;
        this.mFolder = folder;
        this.mName =  name;
        this.mTag = tag;
    }

    public RequestCall build() {
        return new RequestCall(this);
    }
}
