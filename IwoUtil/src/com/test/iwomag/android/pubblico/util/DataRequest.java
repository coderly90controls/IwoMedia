package com.test.iwomag.android.pubblico.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.test.iwomag.android.pubblico.encoder.BASE64Decoder;

public class DataRequest {
	
	public static ArrayList<HashMap<String, String>> getList_64(String url, Object... parameter) {
		HashMap<String, String> map = getHashMapFromUrl_Base64(url, parameter);
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		if(map != null){
			if("1".equals(map.get("code"))){
				list = getArrayListFromJSONArrayString(map.get("data"));
				if(list != null && list.size() !=0){
					return list;
				}
			}
		}
		return null;
	}
	
	public static HashMap<String, String> getMap_64(String url, Object... parameter) {
		HashMap<String, String> map = getHashMapFromUrl_Base64(url, parameter);
		HashMap<String, String> list = new HashMap<String,String>();
		if(map != null){
			if("1".equals(map.get("code"))){
				list = getHashMapFromJSONObjectString(map.get("data"));
				if(list != null){
					return list;
				}
			}
		}
		return null;
	}

	public static HashMap<String, String> getHashMapFromUrl_Base64(String url, Object... parameter) {
		String response = getStringFromURL_Base64(url, parameter);
		return getHashMapFromJSONObjectString(response);
	}

	public static HashMap<String, String> getHashMapFromUrl(String url, Object... parameter) {
		String response = getStringFromURL(url, parameter);
		return getHashMapFromJSONObjectString(response);
	}

	/**
	 * 从JSONArray中得到ArrayList<HashMap<String,String>>的值
	 */
	public static ArrayList<HashMap<String, String>> getArrayListFromJSONArray(JSONArray array) {
		if (array == null)
			return null;
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		int arLength = array.length();
		HashMap<String, String> map = null;
		try {
			for (int i = 0; i < arLength; i++) {
				map = getHashMapFromJSONObject(array.getJSONObject(i));
				if (map != null)
					result.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 从JSONArray中得到ArrayList<HashMap<String,String>>的值
	 */
	public static ArrayList<HashMap<String, String>> getArrayListFromJSONArrayString(String str) {
		if (StringUtil.isEmpty(str))
			return null;
		try {
			return getArrayListFromJSONArray(new JSONArray(str));
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * 获取URL接口返回的数据，并转换为ArrayList结构，base64加密
	 * 
	 * @param url
	 * @param parameter
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> getArrayListFromUrl_Base64(String url, Object... parameter) {
		return getlist(true, url, parameter);
	}

	/**
	 * 获取URL接口返回的数据，并转换为ArrayList结构，无加密
	 * 
	 * @param url
	 * @param parameter
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> getArrayListFromUrl(String url, Object... parameter) {
		return getlist(false, url, parameter);
	}

	private static ArrayList<HashMap<String, String>> getlist(boolean type, String url, Object... parameter) {
		String str = "";
		if (type) {
			str = getStringFromURL_Base64(url, parameter);
		} else {
			str = getStringFromURL(url, parameter);
		}
		return getArrayListFromJSONArrayString(str);
	}

	/**
	 * 从JSONObject中返回HashMap的值
	 * 
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, String> getHashMapFromJSONObject(JSONObject json) {
		if (json == null)
			return null;
		HashMap<String, String> tempMap = new HashMap<String, String>();
		for (Iterator<String> it = json.keys(); it.hasNext();) {
			String key = it.next();
			String value = json.optString(key, "");
			tempMap.put(key, StringUtil.isEmpty(value)?"":value);
		}
		return tempMap;
	}

	/**
	 * 从String中获得HashMap
	 * 
	 * @param json
	 * @return
	 */
	public static HashMap<String, String> getHashMapFromJSONObjectString(String json) {
		if (StringUtil.isEmpty(json))
			return null;
		try {
			return getHashMapFromJSONObject(new JSONObject(json));
		} catch (JSONException e) {
			return null;
		}
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
	 * 通过url地址获取对应的字符串,无base64加密
	 * 
	 * @param url
	 * @param parameter
	 * @return
	 */
	public static String getStringFromURL(String url, Object... parameter) {
		return getString(false, url, parameter);
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

	private static final int SET_CONNECTION_TIMEOUT = 5 * 1000;
	private static final int SET_SOCKET_TIMEOUT = 20 * 1000;

	public static String postStringFromHttpUrl(String url, HashMap<String, String> data) {
		return HttpConnector.postData(url, data);
	}

	public static String postStringFromHttpsUrl(String url, List<NameValuePair> param) {
		String str = "";

		HttpPost httppost = new HttpPost(url);
		try {
			httppost.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			Logger.i("error = UnsupportedEncodingException");
			return null;
		}
		HttpClient client = getNewHttpClient_new();
		HttpResponse response;
		try {
			response = client.execute(httppost);
			Logger.i("response.getStatusLine().getStatusCode()" + response.getStatusLine().getStatusCode());
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				str = EntityUtils.toString(response.getEntity());
			} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
				str = EntityUtils.toString(response.getEntity());
			}
		} catch (ClientProtocolException e) {
			Logger.i("error = ClientProtocolException");
			return null;
		} catch (IOException e) {
			Logger.i("error = IOException");
			return null;
		}
		Logger.i("result----------------" + str);
		return str;
	}

	public static String getStringFromHttpsUrl(String url, Object... parameter) {
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
		Logger.i("url+++++=" + url);
		String str = "";

		HttpGet httpget = new HttpGet(url);
		HttpClient client = getNewHttpClient_new();
		// HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		try {
			response = client.execute(httpget);
			// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			// {
			str = EntityUtils.toString(response.getEntity());
			// }
		} catch (ClientProtocolException e) {
			Logger.e("getArrayFormUrl --- the ClientProtocolException exception");
			return null;
		} catch (IOException e) {
			Logger.e("getArrayFormUrl --- the IOException exception");
			return null;
		}
		return str;
	}

	public static HttpClient getNewHttpClient_new() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory_new(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			HttpConnectionParams.setConnectionTimeout(params, SET_CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, SET_SOCKET_TIMEOUT);
			HttpClient client = new DefaultHttpClient(ccm, params);

			return client;
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	private static class MySSLSocketFactory_new extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory_new(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public static String getPost(String url, List<NameValuePair> params) {
		/* 建立HTTPost对象 */
		String strResult = "";
		HttpPost httpRequest = new HttpPost(url);

		try {
			/* 添加请求参数到请求对象 */
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			/* 发送请求并等待响应 */
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 读返回数据 */
				strResult = EntityUtils.toString(httpResponse.getEntity());
				Logger.i("------" + strResult);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strResult;
	}

	/**
	 * 
	 * @param url
	 *            请求的URL
	 * @param key
	 *            传入的关键字
	 * @param path
	 *            文件路径
	 */
	public static String SendFile(String url, String key, String path) {
		HtmlFetch ch = new HtmlFetch();
		ch.setUsePostMethod(true);
		ch.referer = url;
		ch.addFile(key, path);
		String str = "";
		try {
			str = ch.getHtml(url);
		} catch (HttpException e) {
			Logger.e(e.toString());
		} catch (IOException e) {
			Logger.e(e.toString());
		} catch (Exception e) {
			Logger.e(e.toString());
		}
		Logger.i("----" + str);
		return str;
	}

	public static String getStringFrom_base64(String value) {
		if (StringUtil.isEmpty(value))
			return "";
		BASE64Decoder der = new BASE64Decoder();
		try {
			return new String(der.decodeBuffer(value));
		} catch (IOException e) {
			return null;
		}
	}

	public static String getJsonFromArrayList(ArrayList<HashMap<String, String>> data) {
		String result = "[";
		for (HashMap<String, String> map : data) {
			if (!"[".equals(result))
				result += ",{";
			else {
				result += "{";
			}
			for (String key : map.keySet()) {
				result += "\"" + key + "\":\"" + map.get(key) + "\",";
			}
			if (result.length() > 1)
				result = result.substring(0, result.length() - 1);
			result += "}";
		}

		Logger.i("json to list" + result);
		return result + "]";
	}
}
