package com.android.iwo.media.dao;

import java.io.File;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * 内存卡 管理类
 * @author abc
 *
 */

public class PublicUtils {
	public  final String DOWNING = "android.intent.action.DOWNING";
	public  final String FINSH = "android.intent.action.FINSH";
	public  final String FAIL = "android.intent.action.FAIL";
	public  final String PAUSE = "android.intent.action.PAUSE";
	public  final String PAUSE_mp4 = "android.intent.action.PAUSE.mp";
	public  final String PAUSE_mp4_start = "android.intent.action.PAUSE.mp.start";
	private SharedPreferences userSp;
	private SharedPreferences.Editor userEditor;
	public PublicUtils(Context context) {
		userSp = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
		userEditor = userSp.edit();
		
	}
	/**
	 * 检查SDcard是否存在
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * SD卡总容量
	 */
	public static long getSDAllSize() {
		//取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		//获取单个数据块的大小（Byte）
		long blockSize = sf.getBlockSize();
		//获取所有数据块数
		long allBlocks = sf.getBlockCount();
		//返回SD卡的大小
		return (allBlocks * blockSize)/1024/1024;
	}
	
	/**
	 * SD卡剩余空间
	 */
	public static long getSDFreeSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		//获取单个数据块的大小（Byte）
		long blockSize = sf.getBlockSize();
		//空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocks();
		//返回SD卡空闲大小
		//return (freeBlocks * blockSize);//返回单位为Byte
		//return (freeBlocks * blockSize)/1024;//返回单位为KB
		return (freeBlocks * blockSize)/1024/1024; //返回单位为MB
	}
	
	
	
	/**
	 * 判断sd卡是否有足够的空间下载
	 */
	public static boolean isEnoughForDownload(long downloadSize){  
		
	    StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());  
	   //sd卡分区数  
	    int blockCounts = statFs.getBlockCount();  
	 
	     Log.e("ray", "blockCounts" + blockCounts);  
  
	    //sd卡可用分区数  
	    int avCounts = statFs.getAvailableBlocks();  
	  
	    Log.e("ray", "avCounts" + avCounts); 
	    //一个分区数的大小  
	    long blockSize = statFs.getBlockSize();  
	  
	    Log.e("ray", "blockSize" + blockSize);  
	  
	    //sd卡可用空间  
	    long spaceLeft = avCounts * blockSize;  
	 
	    Log.e("ray", "spaceLeft" + spaceLeft);  
	  
	    Log.e("ray", "downloadSize" + downloadSize);  
	  
	    if (spaceLeft < downloadSize){  
	        return false;  
	    }  
	  
	    return true;  
	}  

	public String getDownloadFlag() {
		return userSp.getString(ConstantsDownload.OPEN_MOVE, "");
	}
}
