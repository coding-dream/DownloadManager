package com.less.downloadmanager;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.less.downloadmanager.lib.DownloadException;
import com.less.downloadmanager.lib.DownloadManager;
import com.less.downloadmanager.lib.request.Callback;
import com.less.downloadmanager.lib.request.FileCallBack;
import com.less.downloadmanager.lib.request.GetBuilder;
import com.less.downloadmanager.lib.request.RequestCall;

public class MainActivity {

	public static void main(String[] args) {
		download();
	}

    private static void download() {
//        String url = "http://yinyueshiting.baidu.com/data2/music/100627216/100575177118800128.mp3?xcode=7ceaf9dbc343b4c65342a3548728ab76";
        String url = "http://220.194.199.176/4/k/l/p/d/klpdruzqjxgpkyoxeudmpjqvnwazxp/hc.yinyuetai.com/7348015EA9536F7A49FDD32FA0B025B2.mp4?sc=1e26e64ef11e8626&br=781&vid=3048701&aid=32393&area=ML&vst=0&ptp=mv&rd=yinyuetai.com";
        String tag = String.valueOf(url.hashCode());

        // 方式一
        /*
        new GetBuilder()
                .name("JOKER_山本彩.mp4")
                .folder(new File("F:/"))
                .uri(url)
                .tag(tag)
                .build()
                .execute(new Callback() {
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
		*/

        // 方式二(需要暂停取消的情况)
        RequestCall call = new GetBuilder()
        .name("仓木麻衣.mp4")
        .folder(new File("F:/"))
        .uri(url)
        .tag(tag)
        .build();

        DownloadManager.getInstance().start(call,new Callback() {
            @Override
            public void onStart(String tag) {
                System.out.println("begin===========>");
            }

            @Override
            public void onDownloadProgress(String tag,long finished, long totalLength, int percent) {
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

        try {
			Thread.sleep(5000);
	        DownloadManager.getInstance().pause(tag);
			Thread.sleep(5000);

			DownloadManager.getInstance().start(call,new FileCallBack() {

				@Override
				public void onStart(String tag) {
					System.out.println("re begin===========>");
				}

				@Override
				public void onDownloadProgress(String tag,long finished, long totalLength, int percent) {
	            	System.out.println("re finished: " + finished + " totalLength:" + totalLength + "percent: " + percent);
				}

				@Override
				public void onDownloadFailed(DownloadException e) {
					System.out.println("re ============》 onDownloadFailed" + e);
				}

				@Override
				public void onDownloadCompleted(File file) {
					 System.out.println("re ============》 onDownloadCompleted");
				}
			});

		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

    }
}
