package com.less.downloadmanager.lib.util;

public class TextUtils {

	public static boolean isEmpty(String text){
		if(text == null || text.length() == 0){
			return true;
		}
		return false;
	}

}
