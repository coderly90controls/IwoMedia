package com.android.iwo.share;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.lenovo.R;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class Tencent{

	private String AppKey = "801511632";
	private String AppSecret = "2d9c7f985c6db19fed54767dd76ec4cc";
	private String CallBackUrl = "http://www.iwomedia.com";
	private TenUser user = null;
	private WebView mWebView;
	private Context mContext;
	private String mContent;
	private String mUrl;
	private boolean isShare = false;
	public void share(Context context, String u, String data) {
		mContent = data;
		mUrl = u + "【视频分享】";
		if(StringUtil.isEmpty(mContent)){
			Toast.makeText(context, "分享的内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		mContext = context;
		if (!StringUtil.isEmpty(PreferenceUtil.getString(mContext, "ten_access_token", ""))) {

			long nowTime = System.currentTimeMillis();
			long getTime = Long.valueOf(PreferenceUtil.getString(mContext, "ten_get_token_time", ""))
					+ Long.valueOf(PreferenceUtil.getString(mContext, "ten_expires_in", ""))*1000;
			if (nowTime > getTime) {
				Toast.makeText(mContext, "你的授权已经过期，需要重新授权", Toast.LENGTH_SHORT).show();
			} else {
				user = new TenUser();
				new SendWeb().execute();
				return;
			}
		}
		autho();
	}

	private void autho (){
		final CommonDialog dialog = new CommonDialog(mContext, "加载授权页面");
		dialog.show();
		//((Activity) mContext).findViewById(R.id.author_web).setVisibility(View.GONE);
		mWebView = (WebView) ((Activity) mContext).findViewById(R.id.author_web);
		String url = "https://open.t.qq.com/cgi-bin/oauth2/authorize?client_id={0}&response_type=code&redirect_uri={1}&wap=2";

		mWebView.loadUrl(DataRequest.getUrl(url, AppKey, CallBackUrl));
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
		mWebView.requestFocus();
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Logger.i("start"+ url);
				if (url.contains(CallBackUrl + "/?")) {
					String[] data = url.replace(CallBackUrl + "/?", "").split("&");
					HashMap<String, String> map = new HashMap<String, String>();
					String str = "";
					for (int i = 0; i < data.length; i++) {
						str = data[i];
						if (str.split("=").length > 1)
							map.put(str.split("=")[0], str.split("=")[1]);
					}
					Logger.i("map" + "map = " + map.toString());
					mWebView.setVisibility(View.GONE);
					mWebView.clearView();
					new GetToken().execute(map.get("code"));
				}
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				Logger.i("finish" + "" + url);
				if(!isShare)
				mWebView.setVisibility(View.VISIBLE);
				super.onPageFinished(view, url);
				dialog.dismiss();
			}
		});
	}
	private class GetToken extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String url = "https://open.t.qq.com/cgi-bin/oauth2/access_token?client_id={0}&client_secret={1}&redirect_uri={2}&grant_type=authorization_code&code={3}";
			return DataRequest.getStringFromHttpsUrl(url, AppKey, AppSecret, CallBackUrl, params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				Logger.i("---" + "token" + result);
				String[] data = result.split("&");
				HashMap<String, String> map = new HashMap<String, String>();
				String str = "";
				for (int i = 0; i < data.length; i++) {
					str = data[i];
					if (str.split("=").length > 1)
						map.put(str.split("=")[0], str.split("=")[1]);
				}
				Logger.i("---" + "access token = " + map.toString());

				PreferenceUtil.setString(mContext, "ten_access_token", map.get("access_token"));
				PreferenceUtil.setString(mContext, "ten_openid", map.get("openid"));
				PreferenceUtil.setString(mContext, "ten_refresh_token", map.get("refresh_token"));
				PreferenceUtil.setString(mContext, "ten_expires_in", map.get("expires_in"));
				PreferenceUtil.setString(mContext, "ten_name", map.get("name"));
				PreferenceUtil.setString(mContext, "ten_get_token_time", System.currentTimeMillis() + "");
				user = new TenUser(map);
				mWebView.setVisibility(View.GONE);
				new SendWeb().execute();
				Toast.makeText(mContext, "授权成功", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class SendWeb extends AsyncTask<Void, Void, String>{

		private CommonDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new CommonDialog(mContext, "正在提交分享...");
			dialog.show();
		}
		@Override
		protected String doInBackground(Void... params) {
			String url = "https://open.t.qq.com/api/t/add";
			List <NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("format", "json"));
			param.add(new BasicNameValuePair("content", mContent + mUrl));
			param.add(new BasicNameValuePair("oauth_consumer_key", AppKey));
			param.add(new BasicNameValuePair("access_token", user.access_token));
			param.add(new BasicNameValuePair("openid", user.openid));
			param.add(new BasicNameValuePair("oauth_version", "2.a"));
			param.add(new BasicNameValuePair("access_token", user.access_token));
			Logger.i("--" + "user = " + user.toString());
			return DataRequest.postStringFromHttpsUrl(url, param);
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(!StringUtil.isEmpty(result)){
				Logger.i("---" + "发布微博" + result);
				HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(result);
				if(!"0".equals(map.get("ret"))){
					if("36".equals(map.get("errcode"))){
						PreferenceUtil.setString(mContext, "ten_access_token", "");
						Toast.makeText(mContext, "您已经取消授权，需要重新授权后才能分享", Toast.LENGTH_SHORT).show();
						autho();
						return;
					}
					Toast.makeText(mContext, "分享失败", Toast.LENGTH_SHORT).show();
					return ;
				}
				Logger.i("---" + "发布微博" + result);
				Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
				if(mWebView != null)
					mWebView.setVisibility(View.GONE);
				isShare = true;
			}
			
			if(dialog != null) dialog.dismiss();
		}
	}

	class TenUser {
		public String access_token = "";
		public String openid = "";
		public String refresh_token = "";
		public String expires_in = "";
		public String name = "";

		public TenUser() {
			access_token = getString("ten_access_token");
			openid = getString("ten_openid");
			refresh_token = getString("ten_refresh_token");
			expires_in = getString("ten_expires_in");
			name = getString("ten_name");
		}

		public TenUser(HashMap<String, String> map) {
			access_token = map.get("access_token");
			openid = map.get("openid");
			refresh_token = map.get("refresh_token");
			expires_in = map.get("expires_in");
			name = map.get("name");
		}

		private String getString(String key) {
			return PreferenceUtil.getString(mContext, key, "");
		}
		
		public String toString(){
			return access_token + "-" +openid + "-" +refresh_token + "-" +expires_in+ "-" +name;
		}
	}
}
