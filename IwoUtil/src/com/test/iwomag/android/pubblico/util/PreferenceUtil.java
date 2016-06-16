/**
 * 
 */
package com.test.iwomag.android.pubblico.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtil {
	private static final String NAME = "iwovideo_table";//iwomag_table

	public static void setSaveItem(Context context, String id, String value) {
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_WORLD_WRITEABLE);
		// 存入数据
		Editor editor = sp.edit();
		editor.putString("item" + id, value);
		editor.commit();
	}

	public static String getSaveItem(Context context, String id) {
		// 获取SharedPreferences对象
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_WORLD_WRITEABLE);
		if (sp != null) {
			return sp.getString("item" + id, "");
		} else {
			return "";
		}
	}

	public static boolean getBoolean(Context context, String key, boolean defValue) {
		// 获取SharedPreferences对象
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_WORLD_WRITEABLE);
		return sp.getBoolean(key, defValue);
	}

	public static void setBoolean(Context context, String key, boolean value) {
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_WORLD_WRITEABLE);
		// 存入数据
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static int getInt(Context context, String key, int defValue) {
		// 获取SharedPreferences对象
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_WORLD_WRITEABLE);
		return sp.getInt(key, defValue);
	}

	public static void setInt(Context context, String key, int value) {
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_WORLD_WRITEABLE);
		// 存入数据
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	public static String getString(Context context, String key, String defValue) {
		// 获取SharedPreferences对象
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_WORLD_WRITEABLE);
		return sp.getString(key, defValue);
	}

	public static void setString(Context context, String key, String value) {
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_WORLD_WRITEABLE);
		// 存入数据
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static long getLong(Context context, String key, long defValue) {
		// 获取SharedPreferences对象
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_WORLD_WRITEABLE);
		return sp.getLong(key, defValue);
	}

	public static void setLong(Context context, String key, long value) {
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_WORLD_WRITEABLE);
		// 存入数据
		Editor editor = sp.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public static float getFloat(Context context, String key, float defValue) {
		// 获取SharedPreferences对象
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_WORLD_WRITEABLE);
		return sp.getFloat(key, defValue);
	}
	
	public static void setFloat(Context context, String key, float value) {
		SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_WORLD_WRITEABLE);
		// 存入数据
		Editor editor = sp.edit();
		editor.putFloat(key, value);
		editor.commit();
	}
}
