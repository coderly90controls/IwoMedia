package com.android.iwo.media.dao;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataBaseDao {

	private static DataBaseDao dao=null;
	private Context context; 
	private  DataBaseDao(Context context) { 
		this.context=context;
	}
	public static  DataBaseDao getInstance(Context context){
		if(dao==null){
			dao=new DataBaseDao(context); 
		}
		return dao;
	}
	
	public  SQLiteDatabase getConnection() {
		SQLiteDatabase sqliteDatabase = null;
		try { 
			sqliteDatabase= new DBHelper(context).getWritableDatabase();
		} catch (Exception e) {  
		}
		return sqliteDatabase;
	}
	
	
	/**
	 * 获取数据库所有id
	 */
	public synchronized ArrayList<String> getVideoIds(){
	     ArrayList<String> arrayList = new ArrayList<String>();
	 	 SQLiteDatabase database = getConnection();
	     Cursor cursor = database.query("Download", new String[]{"video_id"}, null, null, null, null, null);
	     if(cursor!=null){
	    	 while(cursor.moveToNext()){
	    			String id_db = cursor.getString(cursor.getColumnIndex("video_id"));
	    			arrayList.add(id_db);
	    	 }
	     }
	     
	     if(cursor!=null){
	    	 cursor.close();
	     }
	     database.close();
		return arrayList;
	}
	
	
	/**
	 * 下载存入视频数据
	 */
	public synchronized void initDownloadData(String id,String tid,String name,String url_content,String pic,String taskname2,int downloadStatus ,int downloadSpeed,int treeid){
		 SQLiteDatabase database = getConnection();
	     ContentValues values =  new ContentValues();
	     values.put("video_id", id);//视频id
	     values.put("urlTotal", tid);///视频章节id
	     values.put("filename", name);//视频名字
	     values.put("url", url_content);//视频播放地址
	     values.put("picture", pic);//视频的图片地址
	     values.put("foldername", taskname2);//视频章节名称
	     values.put("downloadStatus", downloadStatus);//下载暂停按钮状态状态 0下载 1暂停
	     values.put("isfinish", 0);//是否在下载 0初始化下载 1下载完成 2正在下载
	     values.put("downloadedSize", 0);//下载完成度
	     values.put("mapKey", -1);//下载到第几个url
	     values.put("downloadpos", 0);//下载下载进度
	     values.put("downloadPercent", 0);//下载百分比
	     values.put("downloadSpeed",downloadSpeed);//下载了多少
	     values.put("treeid",treeid);//课程id
	     database.insert("Download", null, values);
	     database.close(); 
		
	}
	
	
	/**
	 * 更新下载视频的完成度和下载第几个视频信息
	 */
	
	 public synchronized void updateCompeleteSize(String id,int compeleteSize,int downloadIngindex,int downloadSpeed){
		//根据视频id更新数据库视频完成度信息
		 SQLiteDatabase database = getConnection();
		 
		 ContentValues contentValues = new ContentValues();
		 contentValues.put("downloadedSize", compeleteSize);
		 contentValues.put("mapKey", downloadIngindex);
		 contentValues.put("downloadSpeed", downloadSpeed);
		 contentValues.put("downloadSpeed", downloadSpeed);
		 contentValues.put("isfinish", 0);
		 database.update("Download", contentValues,"video_id=?", new String[]{id});
		 database.close();
		
	 }
	 
	/**
	 * 更新视频下载进度到数据库 
	 */
	 
	 
	 public synchronized void updateProgress(String id ,String progress,Float size){
		 
		 SQLiteDatabase database = getConnection();
		 ContentValues contentValues = new ContentValues();
    	 contentValues.put("downloadpos", progress);
    	 contentValues.put("downloadPercent", size);
    	 contentValues.put("isfinish", 2);
    	 database.update("Download", contentValues,"video_id=?", new String[]{id});
    	 database.close();
		 
	 }
	 
	 /**
	  * 查询下载中的视频
	  */
	 public synchronized ArrayList<HashMap<String, String>> query_DownLoad_Ing(String isfinish_lv,String download){
		  ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String,String>>();
		  SQLiteDatabase database = getConnection();
		  Cursor cursor = null;
		  //查询isfinish为0的信息
		  if(download.equals("下载中")){
			   cursor = database.query("Download", new String[]{"video_id","urlTotal","filename","url","picture","foldername","downloadStatus"
				         ,"isfinish","downloadedSize","downloadpos","downloadPercent","mapKey","downloadSpeed","treeid"}, "isfinish!=?", new String[]{isfinish_lv}, null, null, null);
		  }else if(download.equals("下载完成")){
			   cursor = database.query("Download", new String[]{"video_id","urlTotal","filename","url","picture","foldername","downloadStatus"
				         ,"isfinish","downloadedSize","downloadpos","downloadPercent","mapKey","downloadSpeed","treeid"}, "isfinish=?", new String[]{isfinish_lv}, null, null, "_id desc");
		  }
		
		  if(cursor!=null){
			  while(cursor.moveToNext()){
				 HashMap<String, String> hashMap = new HashMap<String, String>();
				 int _id = cursor.getInt(cursor.getColumnIndex("video_id"));
				 int urlTotal = cursor.getInt(cursor.getColumnIndex("urlTotal"));
				 String filename = cursor.getString(cursor.getColumnIndex("filename"));
				 String url = cursor.getString(cursor.getColumnIndex("url"));
				 String picture = cursor.getString(cursor.getColumnIndex("picture"));
				 String foldername = cursor.getString(cursor.getColumnIndex("foldername"));
				 int downloadStatus = cursor.getInt(cursor.getColumnIndex("downloadStatus"));
				 String downloadedSize = cursor.getString(cursor.getColumnIndex("downloadedSize"));
				 int isfinish = cursor.getInt(cursor.getColumnIndex("isfinish"));
				 String mapKey = cursor.getString(cursor.getColumnIndex("mapKey"));
				 int downloadpos = cursor.getInt(cursor.getColumnIndex("downloadpos"));
				 int downloadPercent = cursor.getInt(cursor.getColumnIndex("downloadPercent"));
				 int downloadSpeed = cursor.getInt(cursor.getColumnIndex("downloadSpeed"));
				 int treeid = cursor.getInt(cursor.getColumnIndex("treeid"));
				 
				 hashMap.put("id", String.valueOf(_id));
				 hashMap.put("tid", String.valueOf(urlTotal));
				 hashMap.put("name",filename);
				 hashMap.put("url_content", url);
				 hashMap.put("picture", picture);
				 hashMap.put("taskname",foldername);
				 hashMap.put("downloadStatus",String.valueOf(downloadStatus));
				 hashMap.put("downloadedSize",downloadedSize);
				 hashMap.put("isfinish",String.valueOf(isfinish));
				 hashMap.put("mapKey",mapKey);
				 hashMap.put("downloadpos",String.valueOf(downloadpos));
				 hashMap.put("downloadPercent",String.valueOf(downloadPercent));
				 hashMap.put("downloadSpeed",String.valueOf(downloadSpeed));
				 hashMap.put("treeid",String.valueOf(treeid));
				  
				 arrayList.add(hashMap);
			  }
			  
		  }
		  
		if (cursor != null) {
			cursor.close();
		}
		database.close();	
	   return arrayList;
		 
	 }
	 
	 
	 /**
	  * 下载完成更新数据库信息
	  */
	 public void updata_DownloagIng_Finsh(String id,String url,int downloadedSize){
		  SQLiteDatabase database = getConnection();
		  ContentValues contentValues = new ContentValues();
		  contentValues.put("isfinish", 1);
		  contentValues.put("url", url);
		  contentValues.put("downloadedSize", downloadedSize);
		  database.update("Download", contentValues,"video_id=?", new String[]{id});
	      database.close();
		 
	 }
	 
	 /**
	  * 根据id查询finsh状态
	  */
	 
	 public int quary_finsh(String id){
		   int finshStatus = 0; 
		   SQLiteDatabase database = getConnection();
		   Cursor cursor = database.query("Download", new String[]{"isfinish"}, "video_id=?", new String[]{id}, null, null, null);
		  
		  if(cursor!=null){
			  if(cursor.moveToNext()){
				  finshStatus = cursor.getInt(cursor.getColumnIndex("downloadStatus"));
			  }
			  
		  }
		  
		  if(cursor!=null){
			  cursor.close();
		  }
		  
		  database.close();
		  return finshStatus;
		  
	 }
	 
	 
	 /**
	  * 更新下载,暂停按钮下载状态
	  */
	 
	 public void updata_DownStatus(String id ,int status){
		  SQLiteDatabase database = getConnection();
		  ContentValues contentValues = new ContentValues();
		  contentValues.put("downloadStatus", status);
		  database.update("Download", contentValues,"video_id=?", new String[]{id});
	      database.close();
	 }
	 
	 
	 /**
	  * 查询所有下载状态
	  */
  	 public ArrayList<String> query_All_DownStatus(){
  		 
  		   SQLiteDatabase database = getConnection();
  		   Cursor cursor = database.query("Download", new String[]{"downloadStatus"}, null, null, null, null, null);
  		   ArrayList<String> arrayList = new ArrayList<String>();
  		 if(cursor!=null){
  			 while(cursor.moveToNext()){
	    			String downloadStatus = cursor.getString(cursor.getColumnIndex("downloadStatus"));
	    			arrayList.add(downloadStatus);
	    	 }
  			 
  		 }
  		 
  		 if(cursor!=null){
  			 cursor.close();
  		 }
  		 database.close();
  		 
		return arrayList;
  	 }
  	 
  	 /**
  	  * 根据id查询mapkey和downloadedSize
  	  */
  	 
  	 public ArrayList<String> query_contion_size(String id){
  	     ArrayList<String> arrayList = new ArrayList<String>();
		   SQLiteDatabase database = getConnection();
		   Cursor cursor = database.query("Download", new String[]{"mapKey","downloadedSize","downloadSpeed"}, "video_id=?", new String[]{id}, null, null, null);
	
		 if(cursor!=null){
			 while(cursor.moveToNext()){
	    			String mapKey = cursor.getString(cursor.getColumnIndex("mapKey"));
	    			String downloadedSize = cursor.getString(cursor.getColumnIndex("downloadedSize"));
	    			int downloadSpeed = cursor.getInt(cursor.getColumnIndex("downloadSpeed"));
	    			arrayList.add(mapKey);
	    			arrayList.add(downloadedSize);
	    			arrayList.add(String.valueOf(downloadSpeed));
	    	 }
			 
		 }
		 
		 if(cursor!=null){
			 cursor.close();
		 }
		 database.close();
		 
		return arrayList;
	 }
  	 
  	/**
  	 * 根据id删除数据
  	 */
  	 
  	 public void delete_item(String id){
  		  SQLiteDatabase database = getConnection();
  		  database.delete("Download", "video_id=?", new String[]{id});
  		  database.close();
  	 }
  	 
  	 /**
  	  * 根据id查询视频名字
  	  */
  	 public String query_item_filename(String id){
  	   String filenames = null ;
  	   SQLiteDatabase database = getConnection();
  	   Cursor cursor = database.query("Download", new String[]{"filename"}, "video_id=?", new String[]{id}, null, null, null);
  	    if(cursor!=null){
			 if(cursor.moveToNext()){
				 filenames = cursor.getString(cursor.getColumnIndex("filename"));
    	  }
			 
		 }
  	    
	  	  if(cursor!=null){
				 cursor.close();
			 }
			 database.close();
		 
  	    
		return filenames;
  	   
  	 }
  	 
  	 
  	 /**
  	  * 根据id查询下载状态
  	  */
  	 public int query_item_item(String id){
  	   int isfinish = 0;
  	   SQLiteDatabase database = getConnection();
  	   Cursor cursor = database.query("Download", new String[]{"isfinish"}, "video_id=?", new String[]{id}, null, null, null);
  	    if(cursor!=null){
			 if(cursor.moveToNext()){
				 isfinish = cursor.getInt(cursor.getColumnIndex("isfinish"));
    	  }
			 
		 }
  	    
	  	  if(cursor!=null){
				 cursor.close();
			 }
			 database.close();
		 
  	    
		return isfinish;
  	   
  	 }
  	 
  	 
  	 /**
  	  * 根据id获取主键
  	  */
  	 public int query_Key_Id(String id){
  		 int key_id = 0;
  		 SQLiteDatabase database = getConnection();
  		 Cursor cursor = database.query("Download", new String[]{"_id"}, "video_id=?", new String[]{id}, null, null, null); 
  	      if(cursor!=null){
  				 if(cursor.moveToNext()){
  					key_id = cursor.getInt(cursor.getColumnIndex("_id"));
  	    	   }
  				 
  			}
  	      
  	    if(cursor!=null){
			 cursor.close();
		 }
		 database.close();
		 
		  return key_id;
  	 }
 
  	 
  	 
  	 
  	 
  	 /**
  	  * @param isfinish_lv
  	  * @param key_id
  	  * @return
  	  * 查询下载列表 大于下载完成id的所有视频
  	  */
	 public synchronized ArrayList<HashMap<String, String>> query_Next_DownLoad_Ing(String isfinish_lv,String key_id){
		  ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String,String>>();
		  SQLiteDatabase database = getConnection();
		  Cursor cursor = null;
		  
		   cursor = database.query("Download", new String[]{"_id","video_id","urlTotal","filename","url","picture","foldername","downloadStatus"
			         ,"isfinish","downloadedSize","downloadpos","downloadPercent","mapKey","downloadSpeed","treeid"}, "isfinish!=? and _id>?", new String[]{isfinish_lv,key_id}, null, null, null);
			
		  if(cursor!=null){
			  while(cursor.moveToNext()){
				 HashMap<String, String> hashMap = new HashMap<String, String>();
				 int _id = cursor.getInt(cursor.getColumnIndex("video_id"));
				 int urlTotal = cursor.getInt(cursor.getColumnIndex("urlTotal"));
				 String filename = cursor.getString(cursor.getColumnIndex("filename"));
				 String url = cursor.getString(cursor.getColumnIndex("url"));
				 String picture = cursor.getString(cursor.getColumnIndex("picture"));
				 String foldername = cursor.getString(cursor.getColumnIndex("foldername"));
				 int downloadStatus = cursor.getInt(cursor.getColumnIndex("downloadStatus"));
				 String downloadedSize = cursor.getString(cursor.getColumnIndex("downloadedSize"));
				 int isfinish = cursor.getInt(cursor.getColumnIndex("isfinish"));
				 String mapKey = cursor.getString(cursor.getColumnIndex("mapKey"));
				 int downloadpos = cursor.getInt(cursor.getColumnIndex("downloadpos"));
				 int downloadPercent = cursor.getInt(cursor.getColumnIndex("downloadPercent"));
				 int downloadSpeed = cursor.getInt(cursor.getColumnIndex("downloadSpeed"));
				 int treeid = cursor.getInt(cursor.getColumnIndex("treeid"));
				 
				 hashMap.put("id", String.valueOf(_id));
				 hashMap.put("tid", String.valueOf(urlTotal));
				 hashMap.put("name",filename);
				 hashMap.put("url_content", url);
				 hashMap.put("picture", picture);
				 hashMap.put("taskname",foldername);
				 hashMap.put("downloadStatus",String.valueOf(downloadStatus));
				 hashMap.put("downloadedSize",downloadedSize);
				 hashMap.put("isfinish",String.valueOf(isfinish));
				 hashMap.put("mapKey",mapKey);
				 hashMap.put("downloadpos",String.valueOf(downloadpos));
				 hashMap.put("downloadPercent",String.valueOf(downloadPercent));
				 hashMap.put("downloadSpeed",String.valueOf(downloadSpeed));
				 hashMap.put("treeid",String.valueOf(treeid));
				  
				 arrayList.add(hashMap);
			  }
			  
		  }
		  
		if (cursor != null) {
			cursor.close();
		}
		database.close();	
	   return arrayList;
		 
	 }
	 
}
