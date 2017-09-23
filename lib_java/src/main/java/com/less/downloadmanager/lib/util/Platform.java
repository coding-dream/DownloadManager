package com.less.downloadmanager.lib.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public  class Platform {
    private static final Platform PLATFORM = findPlatform();
    private static Executor executor;
    public static Platform get() {
        return PLATFORM;
    }

    private static Platform findPlatform() {
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
}
