package com.less.downloadmanager.lib.util;

import com.less.downloadmanager.lib.Constants;

public class L {
    public static void d(String msg) {
    	if(Constants.CONFIG.DEBUG){
    		System.out.println(msg);
    	}
    }

    public static void e(String msg) {
        if (Constants.CONFIG.DEBUG) {
            System.err.println(msg);
        }
    }
}
