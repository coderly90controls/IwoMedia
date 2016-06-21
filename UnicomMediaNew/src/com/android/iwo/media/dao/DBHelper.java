package com.android.iwo.media.dao;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

	private static final int VERSION = 3;
	private static final String NAME = "iwovideo.db";
//	private static DBHelper mInstance;
	
	public DBHelper(Context context) {
		super(context, NAME, null, VERSION);
	}
	
	
//	/**
//	 * @param context
//	 * @return
//	 * 单例创建数据库
//	 */
//	public synchronized static DBHelper getInstance(Context context) {
//		if(mInstance == null) {
//			mInstance = new DBHelper(context);
//		}
//		return mInstance;
//	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		//数据字段没有添加全，以后写到再添加，比如：记录视频播放到什么位置，treeid等待。
		String sql = "CREATE TABLE IF NOT EXISTS Download (_id integer primary key autoincrement," +
				     "filename string, url string, picture string, urlTotal int,downloadpos int, currentLength double," +
				     "downloadSpeed int, downloadPercent int," +
				     "downloadStatus int, isfinish int, downloadedSize string," +
				     "foldername string, localurl string, mapKey string)"; // downloadstatus string 
		
		String sql2 = "CREATE TABLE IF NOT EXISTS video_record(_id integer primary key autoincrement," +"treeId int, videoName string, chapterName string, playUrl string, tid string,videoid string,currentPosition string)";
		
		String sql3 = "ALTER TABLE Download ADD COLUMN [treeid] INT(11) DEFAULT 0";
		String sql4 = "ALTER TABLE Download ADD COLUMN [video_id] INT(11) DEFAULT 0";
		
		db.execSQL(sql);
		db.execSQL(sql2);
		db.execSQL(sql3);
		db.execSQL(sql4);
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v("tangcy", "数据库版本："+"oldVersion"+oldVersion+"newVersion"+newVersion);
		if(newVersion==2){
			Log.v("tangcy", "数据库升级到了第二版本");
			String sql2 = "CREATE TABLE IF NOT EXISTS video_record(_id integer primary key autoincrement," +"treeId int, videoName string, chapterName string, playUrl string, tid string,videoid string,currentPosition string)";
			db.execSQL(sql2);
			
		}else if(newVersion==3){
			Log.v("tangcy", "数据库升级到了第三版本");
			db.execSQL("ALTER TABLE "+"Download"+" ADD COLUMN [treeid] INT(11) DEFAULT 0;");
			db.execSQL("ALTER TABLE "+"Download"+" ADD COLUMN [video_id] INT(11) DEFAULT 0;");
		}
		
	}
	
		
}
