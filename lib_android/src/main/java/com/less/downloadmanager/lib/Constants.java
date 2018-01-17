package com.less.downloadmanager.lib;

public class Constants {

    public static final class CONFIG {
        public static final boolean DEBUG = true;
        public static final int DEFAULT_MAX_THREAD_NUMBER = 10;// 线程池的最大线程数量
        public static final int DEFAULT_THREAD_NUMBER = 3;// 每个DownLoader下载器的 线程数量
    }

    public static final class HTTP {
        public static final int CONNECT_TIME_OUT = 10 * 1000;
        public static final int READ_TIME_OUT = 10 * 1000;
        public static final String GET = "GET";
    }
}
