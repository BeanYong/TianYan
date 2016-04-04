package com.ncu.tianyan.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import android.content.Context;

import com.ncu.tianyan.SMSBroadcastReceiver;

/**
 * 本类是文件的操作类，实现经纬度信息在本地文件的读取、写入、清空与查询
 * @author 1054639005@qq.com
 */
public class FileUtils {
	/**
	 * 当接收到短信后，将经纬度写入saveInfo.ini
	 * @param context
	 */
	public static void writeInfo(Context context){
		File file = new File(context.getFilesDir(),"saveInfo.ini");
		try {
			if(!file.exists())
				file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(("&"+SMSBroadcastReceiver.wdString+"&"+SMSBroadcastReceiver.jdString+"&").getBytes());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 当获取到短信后，进入app后获取位置信息
	 * @param context
	 * @return
	 */
	public static String readInfo(Context context){
		String str = null,temp = null;
		File file = new File(context.getFilesDir(),"saveInfo.ini");
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			while((temp = br.readLine()) != null){
				str += temp;
			}
			fis.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * 当程序完全退出后，删除saveInfo.ini，在onDestroy方法中调用
	 * @param context
	 */
	public static void clearInfo(Context context){
		File file = new File(context.getFilesDir(),"saveInfo.ini");
		if(file.exists())
			file.delete();
	}
	
	/**
	 * 查询saveInfo.ini，返回saveInfo.ini中的内容
	 * @param context
	 * @return
	 */
	public static String queryInfo(Context context){
		String str = null,temp = null;
		File file = new File(context.getFilesDir(),"saveInfo.ini");
		if(!file.exists())
			return null;
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			while((temp = br.readLine()) != null){
				str += temp;
			}
			fis.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * 判断是否存在saveInfo.ini文件
	 * @param context
	 * @return 
	 */
	public static Boolean isInfoExists(Context context){
		File file = new File(context.getFilesDir(),"saveInfo.ini");
		return file.exists();
	}
}
