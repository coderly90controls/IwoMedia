package com.test.iwomag.android.pubblico.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.test.iwomag.android.pubblico.encoder.BASE64Decoder;

/**
 * 提供众多网络请求相关的静态工程方法, 大部分使用的是GlobalContext中的网络请求池
 * 部分未使用的是保存原作者设计
 */
public class HttpConnector {
	private static final String TAG = "HttpConnector";
	private static final int CONNECTIONTIMEOUT = 5000;
	private static final int SOTIMEOUT = 5000;
	
	public static InputStream getInputStream(String url) {
		GlobalContext gctx = GlobalContext.getInstance();
		HttpGet get = new HttpGet(url);
		InputStream is = null;
		try {
			HttpResponse resp = gctx.getHttpClient().execute(get);
			HttpEntity entity = resp.getEntity();
			if(entity != null) {
				is = entity.getContent();
				Logger.verbose("request url : " + url);
			}
		} catch (Exception e) {
			get.abort();
			Logger.error("request url : " + url + " exception:" + e.getMessage(), e);
		}
		return is;
	}
	
	public static byte[] getFromUrl(String url) {
		GlobalContext gctx = GlobalContext.getInstance();
		HttpGet get = new HttpGet(url);
		byte[] bytes = null;
		try {
			HttpResponse resp = gctx.getHttpClient().execute(get);
			HttpEntity entity = resp.getEntity();
			if(entity != null) {
				bytes = EntityUtils.toByteArray(entity);
				Logger.verbose("request url : " + url);
			}
		} catch (Exception e) {
			get.abort();
			Logger.error("request url : " + url + " exception : " + e.getMessage(), e);
		}catch (OutOfMemoryError e) {
			Logger.i("http request out of memory");
		}
		return bytes;
	}
	
	public static String getStringFromUrl_Base64(String url, String encoding) {
		byte[] result = getFromUrl(url);
		BASE64Decoder der = new BASE64Decoder();
		try {
			return new String(der.decodeBuffer(new String(result, StringUtil.isEmpty(encoding) ? encoding : Config.ENCODING)));
		} catch (IOException e) {
			return null;
		}
	}
	
	public static String getStringFromUrl(String url, String encoding) {
		byte[] result = getFromUrl(url);
		if(result != null) {
			try {
				return new String(result, StringUtil.isEmpty(encoding) ? encoding : Config.ENCODING);
			} catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}
	public static String getStringFromUrl_Base64(String url) {
			return getStringFromUrl_Base64(url, Config.ENCODING);
	}
	public static String getStringFromUrl(String url) {
		return getStringFromUrl(url, Config.ENCODING);
	}
	
	/**
	 * @param url
	 * @param data
	 * @param isMultiPart
	 * @return
	 */
	public static String postData(String url, Map<String, String> data) {
		HttpPost post = new HttpPost(url);
		HttpEntity entity = null;
		try {
			entity = createEntity(data);
		} catch (UnsupportedEncodingException e) {
		}
		if(entity == null) {
			return null;
		}
		post.setEntity(entity);
		HttpClient client = GlobalContext.getInstance().getHttpClient();
		HttpResponse resp = null;
		try {
			resp = client.execute(post);
			Logger.i("返回结果："+ resp.getStatusLine().getStatusCode());
			if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String response = EntityUtils.toString(resp.getEntity(), Config.ENCODING);
				return response;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
		return null;
	}
	
	
	/**
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static HttpEntity createEntity (Map<String, String> data) throws UnsupportedEncodingException {
		ArrayList<NameValuePair> fields = new ArrayList<NameValuePair>();
		for(String key: data.keySet()) {
			fields.add(new BasicNameValuePair(key, String.valueOf(data.get(key))));
		}
		return new UrlEncodedFormEntity(fields, Config.ENCODING);
	}
	
	/**
	 * 像url中post数据
	 * @param url
	 * @param data
	 * @param isMultiPart
	 * @return
	 */
	public static String postData(String url, Map<String, Object> data, boolean isMultiPart) {
//		Logger.verbose(TAG, "上传数据 url=" + url + " data=" + data);
		HttpPost post = new HttpPost(url);
		HttpEntity entity = null;
		try {
			entity = createEntity(data, isMultiPart);
		} catch (UnsupportedEncodingException e) {
//			Logger.error("UnsupportedEncodingException", e);
		}
		if(entity == null) {
			return null;
		}
		post.setEntity(entity);
		HttpClient client = GlobalContext.getInstance().getHttpClient();
		HttpResponse resp = null;
		try {
			resp = client.execute(post);
			if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String response = EntityUtils.toString(resp.getEntity(), Config.ENCODING);
//				Logger.verbose(TAG, "response:" + response);
				return response;
			}
		} catch (ClientProtocolException e) {
//			Logger.error("ClientProtocolException", e);
		} catch (IOException e) {
//			Logger.error( "IOException", e);
		}
		return null;
	}
	
	public static HttpEntity createEntity (Map<String, Object> data, boolean isMultiPart) throws UnsupportedEncodingException {
		HttpEntity result = null;

			ArrayList<NameValuePair> fields = new ArrayList<NameValuePair>();
			for(String key: data.keySet()) {
				fields.add(new BasicNameValuePair(key, String.valueOf(data.get(key))));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(fields, Config.ENCODING);
			result = entity;
		return result;
	}
	
	/**
	 * httpget请求
	 * @return String
	 */
	public static String httpGet(String url) {
		HttpClient httpclient = null;
		HttpResponse httpResponse = null;
		HttpGet httpRequest = null;
		try{
			// 查询请求
			httpRequest = new HttpGet(url);
			//设置参数
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					CONNECTIONTIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParameters, SOTIMEOUT);

			// 取得HttpClient对象
			httpclient = new DefaultHttpClient(httpParameters);
			// 请求HttpClient，取得HttpResponse
			httpResponse = httpclient.execute(httpRequest);
			// 请求成功
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 取得返回的字符串
				return retrieveInputStream(httpResponse.getEntity());
				//return EntityUtils.toString(httpResponse.getEntity());
			} else {
				return null;
			}
		} 
		catch (Exception e) {
			return null;
		}
			
	}
	private static String retrieveInputStream(HttpEntity httpEntity) {
		//Long l = httpEntity.getContentLength();
		int length = (int) httpEntity.getContentLength();
		//the number of bytes of the content, or a negative number if unknown. If the content length is known but exceeds Long.MAX_VALUE, a 
		//length==-1，下面这句报错，println needs a message
		//System.out.println("length = "+length);
		if (length < 0) length = 10000;
		StringBuffer stringBuffer = new StringBuffer(length);
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent(), HTTP.UTF_8);
			char buffer[] = new char[length];
			int count;
			while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
				stringBuffer.append(buffer, 0, count);
			}
			inputStreamReader.close();

		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return stringBuffer.toString();
	}
	/**
	 * httpPost请求
	 * @return String
	 */
	public static String httpPost(String url, Map<String, String> data) {
		try{
			// 查询请求
			HttpPost httpRequest = new HttpPost(url);
			HttpEntity entity = null;
			entity = createEntity(data);
			httpRequest.setEntity(entity);
			//设置参数
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					CONNECTIONTIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParameters, SOTIMEOUT);
			// 取得HttpClient对象
			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			// 请求HttpClient，取得HttpResponse
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			// 请求成功
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 取得返回的字符串
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				// Log.i(TAG, "httpGet.strResult=" + strResult);
				return strResult;
			} else {
				Log.e(TAG, "Pos请求错误!");
				return null;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}
}