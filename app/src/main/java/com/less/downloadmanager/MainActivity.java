package com.less.downloadmanager;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.less.downloadmanager.lib.DownloadException;
import com.less.downloadmanager.lib.DownloadManager;
import com.less.downloadmanager.lib.request.FileCallBack;
import com.less.downloadmanager.lib.request.GetBuilder;
import com.less.downloadmanager.lib.request.RequestCall;
import com.less.downloadmanager.lib.util.L;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ProgressBar progressBar1;
    private ProgressBar progressBar2;

    private String tag1 = "";
    private String tag2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start1).setOnClickListener(this);
        findViewById(R.id.btn_pause1).setOnClickListener(this);
        findViewById(R.id.btn_cancel1).setOnClickListener(this);

        findViewById(R.id.btn_start2).setOnClickListener(this);
        findViewById(R.id.btn_pause2).setOnClickListener(this);
        findViewById(R.id.btn_cancel2).setOnClickListener(this);

        progressBar1 = (ProgressBar) findViewById(R.id.pb_download1);
        progressBar2 = (ProgressBar) findViewById(R.id.pb_download2);
    }

    @Override
    public void onClick(View view) {
        String name1 = "I Knew You Were Trouble - Taylor Swift.mp4";
        String url1 = "http://112.253.22.151/6/k/r/g/s/krgsoayshzaaeisycjrboohohceasy/hc.yinyuetai.com/BE87013B952979AF2852C6E8FCEB9071.flv?sc=9bc9b57781422a20&br=778&vid=564876&aid=122&area=US&vst=0&ptp=mv&rd=yinyuetai.com";
        tag1 = String.valueOf(url1.hashCode());

        String name2 = "张韶涵音乐合集.mp4";
        String url2 = "http://112.253.22.164/1/c/q/z/y/cqzytmgcimzgqqlxklzmnokygcfrhw/hc.yinyuetai.com/81D1015A07599335AF9DC12F4845748B.mp4?sc=6c87686cc1c1a7ac&br=785&vid=2786192&aid=168&area=HT&vst=3&ptp=mv&rd=yinyuetai.com";
        tag2 = String.valueOf(url2.hashCode());

        switch (view.getId()) {
            case R.id.btn_start1:
                start(name1,url1,tag1);
                break;
            case R.id.btn_pause1:
                pause(tag1);
                break;
            case R.id.btn_cancel1:
                cancel(tag1);

            case R.id.btn_start2:
                start(name2,url2,tag2);
                break;
            case R.id.btn_pause2:
                pause(tag2);
                break;
            case R.id.btn_cancel2:
                cancel(tag2);
                break;
            default:
                break;

        }
    }

    private void start(String name,String url,String tag) {
        File folder = Environment.getExternalStorageDirectory();

        // 方式二(文本类型的内容获取)
        final RequestCall call = new GetBuilder()
                .name(name)
                .folder(folder)
                .uri(url)
                .tag(tag)
                .build();
        DownloadManager.getInstance(this).start(call, new FileCallBack() {
            @Override
            public void onStart(String tag) {
                L.d("=====> onStart " + tag);
                show("=====> onStart " + tag);
            }

            @Override
            public void onDownloadProgress(String tag,long finished, long totalLength, int percent) {
                L.d("=====> onDownloadProgress: " + percent);
                if (tag.equals(tag1)) {
                    progressBar1.setProgress(percent);
                } else if (tag.equals(tag2)) {
                    progressBar2.setProgress(percent);
                }
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
