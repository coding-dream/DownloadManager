package com.less.downloadmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.less.downloadmanager.lib.DownloadException;
import com.less.downloadmanager.lib.DownloadManager;
import com.less.downloadmanager.lib.request.Callback;
import com.less.downloadmanager.lib.request.FileCallBack;
import com.less.downloadmanager.lib.request.GetBuilder;
import com.less.downloadmanager.lib.request.RequestCall;
import com.less.downloadmanager.lib.request.StringCallback;
import com.less.downloadmanager.lib.util.L;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download();
            }
        });
    }

    private void download() {
        String url = "http://220.194.199.176/3/k/d/t/k/kdtkitfgpnckofxelsmuqcszwoofpz/hc.yinyuetai.com/5F9F015E739DD025A8453AB56AFF9AFD.mp4?sc=c8ba91e189b49af0&br=779&vid=3040202&aid=35421&area=JP&vst=0&ptp=mv&rd=yinyuetai.com";
        String tag = String.valueOf(url.hashCode());
        // 方式一
        new GetBuilder()
                .name("JOKER_山本彩")
                .folder(new File("/sdcard/"))
                .uri(url)
                .tag(tag)
                .build()
                .execute(this, new Callback() {
                    @Override
                    public void onStart() {
                        System.out.println("begin===========>");
                    }

                    @Override
                    public void onDownloadProgress(long finished, long totalLength, int percent) {
                        System.out.println("finished: " + finished + " totalLength:" + totalLength + "percent: " + percent);
                    }

                    @Override
                    public void onDownloadPaused() {
                        System.out.println("============》 onDownloadPaused");
                    }

                    @Override
                    public void onDownloadCanceled() {
                        System.out.println("============》 onDownloadCanceled");
                    }

                    @Override
                    public void onDownloadFailed(DownloadException e) {
                        System.out.println("============》 onDownloadFailed" + e);
                    }

                    @Override
                    public Object parseResponse(File file) {
                        System.out.println("============》 parseResponse");
                        return null;
                    }

                    @Override
                    public void onDownloadCompleted(Object o) {
                        System.out.println("============》 onDownloadCompleted");
                    }
                });
                /*
                .execute(this,new FileCallBack() {
                    @Override
                    public void onStart() {
                        L.d("开始下载");
                    }

                    @Override
                    public void onDownloadProgress(long finished, long totalLength, int percent) {
                        L.d("finished: " + finished + " totalLength:" + totalLength + "percent: " + percent);
                    }

                    @Override
                    public void onDownloadFailed(DownloadException e) {
                        L.d("下载失败");
                        e.printStackTrace();
                    }

                    @Override
                    public void onDownloadCompleted(File file) {
                        L.d("下载完成" + file.getAbsolutePath());
                    }
                });
        */

        /*
        // 方式二(文本类型的内容获取)
        RequestCall call = new GetBuilder()
                .name("功夫")
                .folder(new File("/sdcard/"))
                .uri("http://www.java1234.com/a/javabook/javabase/2017/0916/8936.html")
                .tag("xxx")
                .build();
        DownloadManager.getInstance(this).start(call, new StringCallback() {
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
        */
    }
}
