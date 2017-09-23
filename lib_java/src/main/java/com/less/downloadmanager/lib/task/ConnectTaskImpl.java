package com.less.downloadmanager.lib.task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.less.downloadmanager.lib.Constants;
import com.less.downloadmanager.lib.DownloadException;
import com.less.downloadmanager.lib.bean.DownloadStatus;
import com.less.downloadmanager.lib.listenter.OnConnectListener;
import com.less.downloadmanager.lib.util.TextUtils;

/**
 * Created by Administrator on 2017/9/22.
 */

public class ConnectTaskImpl implements ConnectTask {
    private String mUri;
    private OnConnectListener mOnConnectListener;

    private volatile int mStatus;

    private volatile int tempStatus = 0;

    private volatile long mStartTime;

    public ConnectTaskImpl(String uri, OnConnectListener listener) {
        this.mUri = uri;
        this.mOnConnectListener = listener;
    }
    @Override
    public void run() {
        mStatus = DownloadStatus.STATUS_CONNECTING;
        mOnConnectListener.onConnecting();
        try {
            executeConnection();
        } catch (DownloadException e) {
            handleDownloadException(e);
        }
    }

    private void executeConnection() throws DownloadException {
        mStartTime = System.currentTimeMillis();
        HttpURLConnection httpConnection = null;
        final URL url;
        try {
            url = new URL(mUri);
        } catch (MalformedURLException e) {
            throw new DownloadException(DownloadStatus.STATUS_FAILED, "Bad url.", e);
        }
        try {
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setConnectTimeout(Constants.HTTP.CONNECT_TIME_OUT);
            httpConnection.setReadTimeout(Constants.HTTP.READ_TIME_OUT);
            httpConnection.setRequestMethod(Constants.HTTP.GET);
            httpConnection.setRequestProperty("Range", "bytes=" + 0 + "-");
            final int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200
                parseResponse(httpConnection, false);
            } else if (responseCode == HttpURLConnection.HTTP_PARTIAL) { // 206
                parseResponse(httpConnection, true);
            } else {
                throw new DownloadException(DownloadStatus.STATUS_FAILED, "UnSupported response code:" + responseCode);
            }
        } catch (ProtocolException e) {
            throw new DownloadException(DownloadStatus.STATUS_FAILED, "Protocol error", e);
        } catch (IOException e) {
            throw new DownloadException(DownloadStatus.STATUS_FAILED, "IO error", e);
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
    }

    private void parseResponse(HttpURLConnection httpConnection, boolean isAcceptRanges) throws DownloadException {

        final long length;
        String contentLength = httpConnection.getHeaderField("Content-Length");
        if (TextUtils.isEmpty(contentLength) || contentLength.equals("0") || contentLength.equals("-1")) {
            length = httpConnection.getContentLength();
        } else {
            length = Long.parseLong(contentLength);
        }

        if (length <= 0) {
            throw new DownloadException(DownloadStatus.STATUS_FAILED, "length <= 0");
        }

        if (tempStatus == DownloadStatus.STATUS_CANCELED) {
            // cancel
            throw new DownloadException(DownloadStatus.STATUS_CANCELED, "Connection Canceled!");
        } else if (tempStatus == DownloadStatus.STATUS_PAUSED) {
            // paused
            throw new DownloadException(DownloadStatus.STATUS_PAUSED, "Connection Paused!");
        }

        //Successful
        mStatus = DownloadStatus.STATUS_CONNECTED;
        long timeDelta = System.currentTimeMillis() - mStartTime;
        System.out.println("连接时长:" + timeDelta);
        mOnConnectListener.onConnected(length, isAcceptRanges);
    }

    private void handleDownloadException(DownloadException e) {
        switch (e.getErrorCode()) {
            case DownloadStatus.STATUS_FAILED:
                synchronized (mOnConnectListener) {
                    mStatus = DownloadStatus.STATUS_FAILED;
                    mOnConnectListener.onConnectFailed(e);
                }
                break;
            case DownloadStatus.STATUS_PAUSED:
                synchronized (mOnConnectListener) {
                    mStatus = DownloadStatus.STATUS_PAUSED;
                    mOnConnectListener.onConnectPaused();
                }
                break;
            case DownloadStatus.STATUS_CANCELED:
                synchronized (mOnConnectListener) {
                    mStatus = DownloadStatus.STATUS_CANCELED;
                    mOnConnectListener.onConnectCanceled();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown state");
        }
    }
    /** 设置 */
    @Override
    public void pause() {
        tempStatus = DownloadStatus.STATUS_PAUSED;
    }

    @Override
    public void cancel() {
        tempStatus = DownloadStatus.STATUS_CANCELED;
    }
    /**  判断 */
    @Override
    public boolean isConnecting() {
        return mStatus == DownloadStatus.STATUS_CONNECTING;
    }

    @Override
    public boolean isConnected() {
        return mStatus == DownloadStatus.STATUS_CONNECTED;
    }

    @Override
    public boolean isPaused() {
        return mStatus == DownloadStatus.STATUS_PAUSED;
    }

    @Override
    public boolean isCanceled() {
        return mStatus == DownloadStatus.STATUS_CANCELED;
    }

    @Override
    public boolean isFailed() {
        return mStatus == DownloadStatus.STATUS_FAILED;
    }
}
