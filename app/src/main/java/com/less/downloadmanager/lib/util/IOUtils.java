package com.less.downloadmanager.lib.util;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

public class IOUtils {

    public static final void closeQuietly(Closeable closeable)  {
        if (closeable != null) {
            synchronized (IOUtils.class) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void flushQuietly(Flushable flushable) {
        if (flushable == null) return;
        try {
            flushable.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
