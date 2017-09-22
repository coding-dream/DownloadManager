package com.less.downloadmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.less.downloadmanager.lib.DownloadException;
import com.less.downloadmanager.lib.DownloadManager;
import com.less.downloadmanager.lib.request.Callback;
import com.less.downloadmanager.lib.request.FileCallBack;
import com.less.downloadmanager.lib.request.GetBuilder;
import com.less.downloadmanager.lib.request.RequestCall;
import com.less.downloadmanager.lib.request.StringCallback;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "http://www.java1234.com";

        // 方式一
        new GetBuilder()
                .mName("功夫")
                .folder(new File("/sdcard/"))
                .uri(url)
                .build()
                .execute(this,new FileCallBack() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onDownloadProgress(long finished, long totalLength, int percent) {

                    }

                    @Override
                    public void onDownloadFailed(DownloadException e) {

                    }

                    @Override
                    public void onDownloadCompleted(File file) {

                    }
                });

        // 方式二
        RequestCall call = new GetBuilder()
                .mName("功夫")
                .folder(new File("/sdcard/"))
                .uri("http://www.java1234.com")
                .build();
        DownloadManager.getInstance(this).execute(call, new StringCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onDownloadFailed(DownloadException e) {

            }

            @Override
            public void onDownloadCompleted(String s) {

            }
        });


        String tag = String.valueOf(url.hashCode());

        DownloadManager.getInstance(this).pause(tag);
    }
}
