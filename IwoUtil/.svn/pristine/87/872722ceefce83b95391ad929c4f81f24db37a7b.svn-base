package com.test.iwomag.android.pubblico.util;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/**
 *网络请求管理
 */
public class GlobalContext {
	private static GlobalContext instance;
	private HttpClient httpClient = null;

	private GlobalContext(){
		initHttpClient();
	}
	public static GlobalContext getInstance() {
		if(instance == null) {
			instance = new GlobalContext();
		}
		return instance;
	}
	
	@Override
	public Object clone() {
		throw new RuntimeException("GlobalContext should not be cloned!");
	}
	
	private void initHttpClient() {
		HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(params, 100);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        
        // Create and initialize scheme registry 
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        
        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        httpClient = new DefaultHttpClient(cm, params);
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
	}
	
	private void shutdownHttpClient() {
		httpClient.getConnectionManager().shutdown();
	}
	
	public HttpClient getHttpClient() {
		if(this.httpClient == null) {
			initHttpClient();
		}
		return this.httpClient;
	}

	public void destroy() {
		shutdownHttpClient();
	}
}
