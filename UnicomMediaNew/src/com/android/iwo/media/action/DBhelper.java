package com.android.iwo.media.action;

import java.util.ArrayList;
import java.util.HashMap;

import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBhelper {
	private Context mContext = null;
	private IwoSQLiteHelper mHelper = null;
	private SQLiteDatabase DB = null;
	private String TAB = "";
	private int mNum = 0;
	private boolean isread = false;
	public String[][][] TABLES = { { {}, {} }, {} };
	//private String[] tables_Title = { "msgId", "receiveOrSend", "createTime", "msgType", "content", "isRead", "code", "media_length",
	//		"is_group", "is_mark", "is_mode", "user_id", "nickname", "head_img", "toUserId", "toNickName", "toHeadImg" };
	private String[] tables_Title = null;//消息对话中显示的时间（两分钟显示的）
			
		
	public DBhelper(Context context, String[] tab) {
		mContext = context;
		mHelper = new IwoSQLiteHelper(mContext);
		DB = mHelper.getWritableDatabase();
		tables_Title = tab;
	}
	
	/**
	 * 建表
	 */
	private void create_table(String table) {
		if (!mHelper.tableIsExist(TAB + table)) {
			String sql = "create table " + TAB + table + "(_id integer PRIMARY KEY AUTOINCREMENT," ;
			for(int i=0; i< tables_Title.length; i++){
				if(i == tables_Title.length-1)
					sql += tables_Title[i] + " char)";
				else 
					sql += tables_Title[i] + " char,";
			}
			DB.execSQL(sql);
		}
	}
	
	/**
	 * 插入数据
	 * @param table
	 * @param map
	 */
	public void insert (String table, HashMap<String, String> map){
		create_table(table);
		String sql = "insert into " + TAB + table + "(";
		for(int i=0; i< tables_Title.length; i++){
			if(i == tables_Title.length -1){
				sql += tables_Title[i] + ")";
			}else 
				sql += tables_Title[i] + ",";
		}
		
		sql += " values (";
		
		for(int i=0; i< tables_Title.length; i++){
			String key = map.get(tables_Title[i]);
			if(!StringUtil.isEmpty(key)){
				key = key.replace("'", "\'");
			}
			if(i == tables_Title.length -1){
				sql += "'" + key+"'" + ")";
			}else 
				sql += "'" +key + "',";
			
		}
		Logger.e("sql=" + sql);
		DB.execSQL(sql);
	}
	
	/**
	 * 更具key排序查询
	 * @param table
	 * @param key 表中的字段
	 * @param by true 降序， false 升序
	 * @return
	 */
	public ArrayList<HashMap<String, String>> select(String table, String key, boolean by){
		create_table(table);
		String sql = "SELECT * FROM " + TAB + table + " order by "+ key;
		sql += by?" desc":" asc";
		return sel(sql);
	}
	/**
	 * 查询数据库
	 * @param table
	 * @param n
	 */
	public ArrayList<HashMap<String, String>> select(String table, int n){
		create_table(table);
		if(!isread){
			Cursor cursor = DB.rawQuery("SELECT * FROM " + TAB + table, null);
			Logger.e(n + "---num = " + cursor.getCount());
			mNum = cursor.getCount();
			isread = true;
		}
		long end = 0;
		String sql = "";
		if(mNum > 10){
			end = mNum-10;
			mNum -= 10;
			sql = "SELECT * FROM " + TAB + table + " limit "+end+"," + end+10;
		}else {
			end = mNum;
			if(end == 0) return null;
			sql = "SELECT * FROM " + TAB + table + " limit 0," + end;
			mNum = 0;
		}
		return sel(sql);
	}
	
	/**
	 * 查询表里所有数据
	 * @param table
	 * @return
	 */
	public ArrayList<HashMap<String, String>> select(String table){
		create_table(table);
		String sql = "SELECT * FROM " + TAB + table;
		return sel(sql);
	}
	
	/**
	 * 查询语句
	 * @param sql
	 * @return
	 */
	private ArrayList<HashMap<String, String>> sel(String sql){
		Logger.e("sql = " + sql);
		Cursor cursor = DB.rawQuery(sql, null);
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> map = null;
		while (cursor != null && cursor.moveToNext()) {
			map = new HashMap<String, String>();
			for(int i=0; i<tables_Title.length; i++){
				map.put(tables_Title[i], cursor.getString(cursor.getColumnIndex(tables_Title[i])));
				
			}
			data.add(map);
		}
		
		if(cursor != null) {
			cursor.close();
			cursor = null;
		}
		if(data.size() == 0)
			return null;
		else 
			return data;
	}
	
	public ArrayList<HashMap<String, String>> select(String table, String... par){
		create_table(table);
		String sql = "SELECT * FROM " + TAB + table + " where ";
		for(int i=0; i<par.length; i++){
			sql += par[i] + " = '" + par[++i] + "' and ";
		}
		sql = sql.substring(0, sql.length()-5);
		return sel(sql);
	}
	
	public HashMap<String, String> selectTable(String table, String... par){
		ArrayList<HashMap<String, String>> list = select(table, par);
		if(list != null && list.size()>0) return list.get(0);
		return null;
	}
	/**
	 * 删除
	 * @param table
	 * @param key
	 * @param value
	 */
	public void delete(String table, String key, String value){
		create_table(table);
		String sql = "delete from " + TAB+ table + " where " + key + " = '" + value + "'";
		try {
			Logger.e("sql" + sql);
			DB.execSQL(sql);
		} catch (Exception e) {
			Logger.e("删除数据有错：" + e.toString());
		}
	}
	
	/**
	 * 更新表
	 * @param table 表明
	 * @param key 修改的字段名
	 * @param value 修改的值
	 * @param id 消息id
	 */
	public void update(String table, String key, String value, String where, String w){
		create_table(table);
		//update 表名 set 字段名=值 where 条件子句。如：update person set name=‘传智‘ where id=10
		String sql = "update " + TAB+ table + " set "+ key + " = '" + value +"' where " + where + "='" + w +"'";
		DB.execSQL(sql);
	}
	
	
	public void delete(String table){
		create_table(table);
		//update 表名 set 字段名=值 where 条件子句。如：update person set name=‘传智‘ where id=10
		String sql = "delete from " + table;
		Logger.e("delete " + sql);
		DB.execSQL(sql);
	}
	
	public void close(){
		if(DB != null){
			DB.close();
			DB = null;
		}
	}
}