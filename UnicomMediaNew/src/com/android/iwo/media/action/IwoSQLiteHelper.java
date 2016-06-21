package com.android.iwo.media.action;

import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class IwoSQLiteHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "iwoshare.db"; // 数据库名称

	public static final int DB_VERSION = 1; // 数据库版本号
	private Context mContext;

	public IwoSQLiteHelper(Context ctx) {
		super(ctx, DB_NAME, null, DB_VERSION);
		mContext = ctx;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

	public void alter(SQLiteDatabase db, String table, String key) {
		String sql = "alter table " + table + " add " + key + " char";
		db.execSQL(sql);
	}

	/**
	 * 判断某张表是否存在
	 * 
	 * @param tableName
	 * @return
	 */
	public boolean tableIsExist(String tableName) {
		boolean result = false;

		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.getReadableDatabase();
			String sql = "select count(*) as c from Sqlite_master where type ='table' and name ='" + tableName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return result;
	}

	/**
	 * 判断某张表是否存在
	 * 
	 * @param tableName
	 * @param db
	 * @return
	 */
	public boolean tableIsExistDB(String tableName, SQLiteDatabase db) {
		boolean result = false;
		if (tableName == null) {
			return false;
		}
		if (db == null) {
			return false;
		}
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + tableName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 消息列表数据库表
	 */
	public final static String[] MESSAGE_TAB = { "user_id", "user_name", "nick_name",// 用户id
			"head_img",// 用户头像
			"msg_tex",// 消息内容
			"type",// 消息类型
			"timestamp",// 消息发送时间
			"send", // 发送的成功状态
			"duartion", "richbody", "sex", "time", "num",// 显示消息的个数
			"isread" };// 是否已读

	/**
	 * 好友列表
	 */
	public final static String[] FRIEND_TAB = { "jid", "name",// 用户id
			"subscription",// 用户头像
			"nick",// 消息内容
			"avatar",// 消息类型
			"age",// 消息发送时间
			"sex", // 发送的成功状态
			"signature", "videoImg", "type", "user_name" };

	/**
	 * 邀请列表
	 */
	public final static String[] INVITE_FROM = { "tel", "type" };// type =
																	// 1，邀请的，
																	// 2是被邀请的
	/**
	 * 被邀请列表
	 */
	public final static String[] INVITE_TO = { "tel" };

	public final static String[] GROUP = { "id", "name", "description", "maxNumber", "avatar", "area", "city", "category", "isShield" };

	public final static String[] GROUP_MEMBER = { "jid", "username" };
}
