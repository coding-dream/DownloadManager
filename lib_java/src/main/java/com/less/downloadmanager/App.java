package com.less.downloadmanager;

import com.less.downloadmanager.lib.DownloadException;
import com.less.downloadmanager.lib.DownloadManager;
import com.less.downloadmanager.lib.request.Callback;
import com.less.downloadmanager.lib.request.FileCallBack;
import com.less.downloadmanager.lib.request.GetBuilder;
import com.less.downloadmanager.lib.request.RequestCall;

import java.io.File;

public class App {

    private static final String PATH_DEST = "F:\\";

    public static void main(String[] args) {
        String name = "I Knew You Were Trouble - Taylor Swift.mp4";
        String url = "http://112.253.22.151/6/k/r/g/s/krgsoayshzaaeisycjrboohohceasy/hc.yinyuetai.com/BE87013B952979AF2852C6E8FCEB9071.flv?sc=9bc9b57781422a20&br=778&vid=564876&aid=122&area=US&vst=0&ptp=mv&rd=yinyuetai.com";
        String tag = String.valueOf(url.hashCode());

//        invoke1(name,url,tag);
//        invoke2(name,url,tag);
        invoke3(name,url,tag);
    }

    private static void invoke1(String name,String url,String tag) {
        new GetBuilder()
                .name(name)
                .folder(new File(PATH_DEST))
                .uri(url)
                .tag(tag)
                .build()
                .execute(new FileCallBack() {
                    @Override
                    public void onStart(String tag) {
                        System.out.println("onStart");
                    }

                    @Override
                    public void onDownloadProgress(String tag, long finished, long totalLength, int percent) {
                        System.out.println("onDownloadProgress: " + percent);
                    }

                    @Override
                    public void onDownloadFailed(DownloadException e) {
                        System.out.println("onDownloadFailed");
                    }

                    @Override
                    public void onDownloadCompleted(File file) {
                        System.out.println("onDownloadCompleted: " + file.getAbsolutePath());
                    }
                });
    }

    private static void invoke2(String name,String url,String tag) {
        RequestCall call = new GetBuilder()
                .name(name)
                .folder(new File(PATH_DEST))
                .uri(url)
                .tag(tag)
                .build();

        DownloadManager.getInstance().start(call,new Callback() {
            @Override
            public void onStart(String tag) {
                System.out.println("begin ===========>");
            }

            @Override
            public void onDownloadProgress(String tag,long finished, long totalLength, int percent) {
                System.out.println("finished: " + finished + " totalLength: " + totalLength + " percent: " + percent);
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
                System.out.println("============》 onDownloadFailed" + e.getErrorCode() + " " + e.getErrorMessage());
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
    }

    private static void invoke3(String name,String url, String tag) {
        RequestCall call = new GetBuilder()
                .name(name)
                .folder(new File(PATH_DEST))
                .uri(url)
                .tag(tag)
                .build();
        DownloadManager.getInstance().start(call,new FileCallBack() {

            @Override
            public void onStart(String tag) {
                System.out.println("re begin ===========>");
            }

            @Override
            public void onDownloadProgress(String tag,long finished, long totalLength, int percent) {
                System.out.println("re finished: " + finished + " totalLength: " + totalLength + " percent: " + percent);
            }

            @Override
            public void onDownloadFailed(DownloadException e) {
                System.out.println("re ============》 onDownloadFailed " + e);
            }

            @Override
            public void onDownloadCompleted(File file) {
                System.out.println("re ============》 onDownloadCompleted: " + file.getAbsolutePath());
            }
        });
    }
}
