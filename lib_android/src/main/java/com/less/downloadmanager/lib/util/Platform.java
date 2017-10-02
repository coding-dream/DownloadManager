package com.less.downloadmanager.lib.util;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public  class Platform {
    private static final Platform PLATFORM = findPlatform();
    private static Executor executor;
    public static Platform get() {
        return PLATFORM;
    }

    private static Platform findPlatform() {
        try {
            Class.forName("android.os.Build");
            if (Build.VERSION.SDK_INT != 0) {
                return new Android(); // 创建Android平台的UI线程池
            }
        } catch (ClassNotFoundException ignored) {
        }
        return new Platform();// 创建Java平台的线程池
    }

    public Executor makeCallbackExecutor() {
        if (executor == null) {
            executor = Executors.newCachedThreadPool();
        }
        return executor;
    }

    /** execute 线程 */
    public void execute(Runnable runnable) {
        makeCallbackExecutor().execute(runnable);
    }

    static class Android extends Platform {
        @Override
        public Executor makeCallbackExecutor() {
            if (executor != null) {
                return executor;
            }
            return executor = new Executor() {
                private Handler handler = new Handler(Looper.getMainLooper());

                @Override
                public void execute(Runnable runnable) {
                    handler.post(runnable);
                }
            };
        }
    }
}
