package com.android.iwo.media.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.test.iwomag.android.pubblico.util.Logger;

import android.app.Activity;

public class ActivityUtil {

	public static HashMap<String, Activity> activity = new HashMap<String, Activity>();
	public static ActivityUtil util = null;
	
	public static ActivityUtil getInstance(){
		if(util == null) util = new ActivityUtil();
		return util;
	}
	public void add(String name, Activity a){
		activity.put(name, a);
	}
	
	public void delete(String name){
		Activity a = activity.get(name);
		if(a != null && !a.isFinishing()){
			a.finish();
		}
		Logger.i("delete" + name);
		activity.put(name, null);
	}
	/**
	 * 判断Activity是否关闭
	 * @param name
	 * @return
	 */
	public boolean isclose(String name){
		Activity a = activity.get(name);
		if(a != null && !a.isFinishing()){
			return false;
		}
		return true;
	}
	
	/**
	 * 关闭所有Activity
	 */
	public void deleteAll(){
		Set<String> keys = activity.keySet();
		Iterator<String> itKeys = keys.iterator();
		while (itKeys.hasNext()) {
			String key = itKeys.next();
			delete(key);
		}
	}
	
	public void deleteAll(String del){
		Set<String> keys = activity.keySet();
		Iterator<String> itKeys = keys.iterator();
		while (itKeys.hasNext()) {
			String key = itKeys.next();
			if(!key.equals(del))
				delete(key);
		}
	}
}
