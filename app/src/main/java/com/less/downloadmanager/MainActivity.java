package com.less.downloadmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.less.downloadmanager.lib.DownloadManager;
import com.less.downloadmanager.lib.request.GetBuilder;
import com.less.downloadmanager.lib.request.StringCallback;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetBuilder()
                .mName("功夫")
                .folder(new File("/sdcard/"))
                .uri("http://www.java1234.com")
                .build()
                .execute(new StringCallback() {
            @Override
            public void onError(Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

            }
        });

    }
}
