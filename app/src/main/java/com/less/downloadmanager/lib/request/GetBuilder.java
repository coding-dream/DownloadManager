package com.less.downloadmanager.lib.request;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/9/16.
 */

public class GetBuilder extends RequestBuilder{

    @Override
    protected RequestBuilder addParams(String key, String val) {
        if (mParams == null) {
            mParams = new HashMap();
        }
        mParams.put(key,val);
        return this;
    }

    @Override
    public RequestCall build() {

        return new GetRequest(mUri,mFolder,mName).build();
    }
}
