package com.less.downloadmanager.lib.request;

import com.less.downloadmanager.lib.DownloadException;
import com.less.downloadmanager.lib.util.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by Administrator on 2017/9/18.
 */

public abstract class StringCallback extends Callback<String>{

    @Override
    public abstract void onStart(String tag);

    @Override
    public  void onDownloadProgress(String tag,long finished, long totalLength, int percent){}

    @Override
    public void onDownloadPaused() {}

    @Override
    public void onDownloadCanceled() {}

    @Override
    public abstract void onDownloadFailed(DownloadException e);


    @Override
    public String parseResponse(File file) {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            StringBuffer buffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line + "\r\n");
            }
            bufferedReader.close();
            fileReader.close();
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public abstract void onDownloadCompleted(String s); // 此形参是parseResponse方法的返回值。
}
