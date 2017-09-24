package com.less.downloadmanager;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.less.downloadmanager.lib.DownloadException;
import com.less.downloadmanager.lib.DownloadManager;
import com.less.downloadmanager.lib.request.Callback;
import com.less.downloadmanager.lib.request.FileCallBack;
import com.less.downloadmanager.lib.request.GetBuilder;
import com.less.downloadmanager.lib.request.RequestCall;
import com.less.downloadmanager.lib.request.StringCallback;
import com.less.downloadmanager.lib.util.L;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.pb_download);
    }

    @Override
    public void onClick(View view) {
        String name = "中岛美雪.mp4";
        String url = "http://220.194.199.176/4/k/l/p/d/klpdruzqjxgpkyoxeudmpjqvnwazxp/hc.yinyuetai.com/7348015EA9536F7A49FDD32FA0B025B2.mp4?sc=1e26e64ef11e8626&br=781&vid=3048701&aid=32393&area=ML&vst=0&ptp=mv&rd=yinyuetai.com";
        String tag = String.valueOf(url.hashCode());

        switch (view.getId()) {
            case R.id.btn_start:
                start(name,url,tag);
                break;
            case R.id.btn_pause:
                pause(tag);
                break;
            case R.id.btn_cancel:
                cancel(tag);
            default:
                break;

        }
    }

    private void start(String name,String url,String tag) {
        File folder = Environment.getExternalStorageDirectory();

        // 方式二(文本类型的内容获取)
        RequestCall call = new GetBuilder()
                .name(name)
                .folder(folder)
                .uri(url)
                .tag(tag)
                .build();
        DownloadManager.getInstance(this).start(call, new FileCallBack() {
            @Override
            public void onStart() {
                L.d("=====> onStart");
                show("=====> onStart");
            }

            @Override
            public void onDownloadProgress(long finished, long totalLength, int percent) {
                L.d("=====> onDownloadProgress: " + percent);
                progressBar.setProgress(percent);
            }

            @Override
            public void onDownloadPaused() {
                L.d("=====> onDownloadPaused: " );
                show("=====> onDownloadPaused");
            }

            @Override
            public void onDownloadCanceled() {
                L.d("=====> onDownloadCanceled: " );
                show("=====> onDownloadCanceled");
            }

            @Override
            public void onDownloadFailed(DownloadException e) {
                L.d("=====> onDownloadFailed: " + e.getErrorMessage());
                show("=====> onDownloadFailed: " + e.getErrorMessage());
            }

            @Override
            public void onDownloadCompleted(File file) {
                L.d("=====> onDownloadCompleted: " + file.getAbsolutePath());
                show("=====> onDownloadCompleted " + file.getAbsolutePath());
            }
        });
    }

    private void pause(String tag) {
        DownloadManager.getInstance(this).pause(tag);
    }

    private void cancel(String tag) {
        DownloadManager.getInstance(this).cancel(tag);
    }

    private void show(String message) {
        Toast.makeText(MainActivity.this,message, Toast.LENGTH_SHORT).show();
    }
}
