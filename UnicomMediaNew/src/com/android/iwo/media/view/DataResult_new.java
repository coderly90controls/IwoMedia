package com.android.iwo.media.view;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.TreeMap;
import org.json.JSONException;
import org.json.JSONObject;
import com.test.iwomag.android.pubblico.util.AppUtil;
import com.test.iwomag.android.pubblico.util.Config;
import com.test.iwomag.android.pubblico.util.HttpConnector;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class DataResult_new {
	public static TreeMap<String, String> getTreeMapFromUrl_Base64(String url, Object... parameter) {
		String response = getStringFromURL_Base64(url, parameter);
		return getTreeMapFromJSONObjectString(response);
	}

	private static TreeMap<String, String> getTreeMapFromJSONObjectString(String json) {
		if (StringUtil.isEmpty(json))
			return null;
		try {
			return getTreeMapFromJSONObject(new JSONObject(json));
		} catch (JSONException e) {
			return null;
		}
	}
	/**
	 * 从JSONObject中返回HashMap的值
	 * 
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static TreeMap<String, String> getTreeMapFromJSONObject(JSONObject json) {
		if (json == null)
			return null;
		TreeMap<String, String> tempMap = new TreeMap<String, String>();
		for (Iterator<String> it = json.keys(); it.hasNext();) {
			String key = it.next();
			String value = json.optString(key, "");
			tempMap.put(key, StringUtil.isEmpty(value)?"":value);
		}
		return tempMap;
	}
	/**
	 * 通过url地址获取对应的字符串,base64加密
	 * 
	 * @param url
	 * @param parameter
	 *            url中使用到的参数
	 */
	public static String getStringFromURL_Base64(String url, Object... parameter) {
		return getString(true, url, parameter);
	}
	/**
	 * 通过url地址获取对应的字符串
	 * 
	 * @param type
	 *            true加密，false没加密
	 * @param url
	 * @param parameter
	 * @return
	 */
	private static String getString(boolean type, String url, Object... parameter) {
		url =getUrl(url, parameter) + AppUtil.END;
		Logger.i("url = " + url);
		String response = "";
		try {
			if (type) {
				response = HttpConnector.getStringFromUrl_Base64(url.toString());
			} else {
				response = HttpConnector.getStringFromUrl(url.toString());
			}
		} catch (Exception e) {
			Logger.i("error = " + e.toString());
			return null;
		}
		if(!StringUtil.isEmpty(response) && response.startsWith("(")){
			response = response.substring(0, 1);
			response = response.substring(response.length()-1, response.length());
		}
		return response;
	}

	public static String getUrl(String url, Object... parameter) {
		int size = parameter.length;
		for (int i = 0; i < size; i++) {
			try {
				if (StringUtil.isEmpty(parameter[i]))
					parameter[i] = "";
				else
					parameter[i] = URLEncoder.encode((String) parameter[i], Config.ENCODING);
			} catch (UnsupportedEncodingException e) {
				Logger.e("getArrayFormUrl --- the unsupported encoding exception");
				return null;
			}
		}
		url = (String) MessageFormat.format(url, parameter);
		Logger.e("url = " + url);
		return url;
	}
}
