package com.android.iwo.media.dao;

import java.io.File;
import java.text.DecimalFormat;

import android.util.Log;

public class FileUtil {
	/**
	 * 检查文件夹是否已经创建
	 * @param folderPath // 文件夹路径
	 * @return
	 */
	public static boolean isExistFolder(String folderPath){
		File file = new File(folderPath);
		if(file.exists()){
			return true;
		}
		return false;
	}
	/**
	 * @param path
	 *            文件夹路径
	 */
	public static void isExist(String folderPath) {
		File file = new File(folderPath);
		// 判断文件夹是否存在,如果不存在则创建文件夹
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	/**
	 * 创建文件夹
	 * @param folderPath
	 * @return
	 */
	public static boolean createFolder(String folderPath){
		if(isExistFolder(folderPath))
			return true;
		File file = new File(folderPath);
		file.mkdirs();
		return false;
	}
	
	
	
	/**
	 * 删除文件夹及文件夹里的内容
	 * @param folderPath
	 * @return
	 */
	public static boolean deleteFolder(String folderPath){
		if(!isExistFolder(folderPath))
			return false;
		File file = new File(folderPath);
		File[] files = file.listFiles();
		for(File f : files)
		{
			if(f.isDirectory())
			{
				deleteFolder(f.getAbsolutePath());
			}else {
				f.delete();
			}
		}
		if(file.delete())
			return true;
		return false;
	}
	/**
	 * 删除指定文件
	 * @param filePath
	 * @return
	 */
	public static boolean deleteFile(String filePath){
		File f = new File(filePath);
		if(f.exists() && f.isFile())
		{
			f.delete();
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * 转换文件
	 * @param fileS
	 * @return
	 */
    public static String FormetFileSize(long fileS) { 
    	Log.v("tangcy", "下载完成视频文件的大小："+fileS);
        DecimalFormat df = new DecimalFormat("#.00"); 
        String fileSizeString = ""; 
        if (fileS < 1024) { 
            fileSizeString = df.format((double) fileS) + "B"; 
        } else if (fileS < 1048576) { 
            fileSizeString = df.format((double) fileS / 1024) + "K"; 
        } else if (fileS < 1073741824) { 
            fileSizeString = df.format((double) fileS / 1048576) + "M"; 
        } else { 
            fileSizeString = df.format((double) fileS / 1073741824) + "G"; 
        } 
        return fileSizeString; 
    } 
}
