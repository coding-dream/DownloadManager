package com.less.downloadmanager.lib.request;

/**
 * Created by Administrator on 2017/9/18.
 */

public abstract class Callback<T> {
    /** UI Thread */
    public void onStart() {}

    /** UI Thread */
    public void onConnecting() {}

    /** UI Thread */
    public void onConnected(long length,boolean isAcceptRanges){};

    /** UI Thread */
    public void inProgress(float progress, long total , int id) {}

    /** Thread Pool Thread */
    public abstract T parseNetworkResponse(int id) throws Exception;

    public abstract void onError(Exception e, int id);

    public abstract void onResponse(T response, int id);

    public static Callback CALLBACK_DEFAULT = new Callback() {

        @Override
        public Object parseNetworkResponse(int id) throws Exception {
            // nothing to do
            return null;
        }

        @Override
        public void onError(Exception e, int id) {
            // nothing to do
        }

        @Override
        public void onResponse(Object response, int id) {
            // nothing to do
        }
    };
}
