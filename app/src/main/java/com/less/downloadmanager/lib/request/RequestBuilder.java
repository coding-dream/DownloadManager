package com.less.downloadmanager.lib.request;

import android.content.Context;

import java.io.File;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/16.
 */

public abstract class RequestBuilder<T extends RequestBuilder> {
    protected String mUri;

    protected File mFolder;

    protected CharSequence mName;

    protected Map<String, String> mParams;

    public T uri(String uri){
        this.mUri = uri;
        return (T) this;
    }

    public T folder(File folder){
        this.mFolder = folder;
        return (T) this;
    }

    public T mName(CharSequence name) {
        this.mName = name;
        return (T) this;
    }

    protected abstract RequestBuilder<T> addParams(String key, String val);

    public abstract RequestCall build();
}
