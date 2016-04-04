package com.ncu.tianyan.util;


/**
 * 字符串工具类，进行字符串的切割、拼装等操作
 * @author Administrator
 */
public class StringUtils {
	/**
	 * 将字符串进行切割并将切割后得到的数组返回
	 * @param str
	 * @return String[]	locInfo 切割后的字符串数组
	 */
	public static String[] splitStr(String str){
		String[] locInfo = str.split("&");
		return locInfo;
	}
}
