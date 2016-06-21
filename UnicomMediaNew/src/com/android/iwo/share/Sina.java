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
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.lenovo.R;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class Sina {

	private String AppKey = "2146502146";
	private String AppSecret = "ef12aebcaf77c56d911c03ce3afbea2c";
	private String CallBackUrl = "http://www.iwomedia.com";
	private SinaUser user = null;
	private WebView mWebView;
	private Context mContext;
	private String mContent;
	private String mUrl;
	private boolean isShare = false;

	public void share(Context context, String u, String data) {
		mContent = data;
		mUrl = u + "【视频分享】";
		if (StringUtil.isEmpty(mContent)) {
			Toast.makeText(context, "分享的内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		mContext = context;
		if (!StringUtil.isEmpty(PreferenceUtil.getString(mContext, "sina_access_token", ""))) {

			long nowTime = System.currentTimeMillis();
			long getTime = Long.valueOf(PreferenceUtil.getString(mContext, "sina_get_token_time", ""))
					+ Long.valueOf(PreferenceUtil.getString(mContext, "sina_expires_in", "")) * 1000;
			if (nowTime > getTime) {
				Toast.makeText(mContext, "你的授权已经过期，需要重新授权", Toast.LENGTH_SHORT).show();
			} else {
				user = new SinaUser();
				new SendWeb().execute();
				return;
			}
		}
		oauth();
	}

	private void oauth() {
		final CommonDialog dialog = new CommonDialog(mContext, "加载授权页面");
		dialog.show();
		//((Activity) mContext).findViewById(R.id.author_web_ten).setVisibility(View.GONE);
		mWebView = (WebView) ((Activity) mContext).findViewById(R.id.author_web);
		String url = "https://api.weibo.com/oauth2/authorize?client_id={0}&display=mobile&redirect_uri={1}";

		mWebView.loadUrl(DataRequest.getUrl(url, AppKey, CallBackUrl));
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
		mWebView.requestFocus();
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.i("start", "" + url);
				if (url.contains(CallBackUrl + "/?")) {
					String[] data = url.replace(CallBackUrl + "/?", "").split("&");
					HashMap<String, String> map = new HashMap<String, String>();
					String str = "";
					for (int i = 0; i < data.length; i++) {
						str = data[i];
						if (str.split("=").length > 1)
							map.put(str.split("=")[0], str.split("=")[1]);
					}
					Log.i("map", "map = " + map.toString());
					mWebView.setVisibility(View.GONE);
					mWebView.clearView();
					new GetToken().execute(map.get("code"));
				}
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				Log.i("finish", "" + url);
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
			String url = "https://api.weibo.com/oauth2/access_token?";
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("client_id", AppKey));
			param.add(new BasicNameValuePair("client_secret", AppSecret));
			param.add(new BasicNameValuePair("grant_type", "authorization_code"));
			param.add(new BasicNameValuePair("code", params[0]));
			param.add(new BasicNameValuePair("redirect_uri", CallBackUrl));
			return DataRequest.postStringFromHttpsUrl(url, param);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				Log.i("---", "token" + result);
				// {"access_token":"2.00bmJrED9UvITCfe64a87d1eUMfy5B","remind_in":"122053","expires_in":122053,"uid":"2820172833"}

				HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(result);
				if (map == null)
					return;
				Log.i("---", "access token = " + map.toString());

				PreferenceUtil.setString(mContext, "sina_access_token", map.get("access_token"));
				PreferenceUtil.setString(mContext, "sina_remind_in", map.get("remind_in"));
				PreferenceUtil.setString(mContext, "sina_uid", map.get("uid"));
				PreferenceUtil.setString(mContext, "sina_expires_in", map.get("expires_in"));
				PreferenceUtil.setString(mContext, "sina_get_token_time", System.currentTimeMillis() + "");
				user = new SinaUser(map);
				if(mWebView != null)
					mWebView.setVisibility(View.GONE);
				new SendWeb().execute();
				Toast.makeText(mContext, "授权成功", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class SendWeb extends AsyncTask<Void, Void, String> {

		private CommonDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new CommonDialog(mContext, "正在提交分享...");
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String url = "https://api.weibo.com/2/statuses/update.json";
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("status", mContent + mUrl));
			param.add(new BasicNameValuePair("access_token", user.access_token));
			Log.i("--", "user = " + user.toString());
			return DataRequest.postStringFromHttpsUrl(url, param);
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("---", "发布微博" + result);
			if(dialog != null) dialog.dismiss();
			if (!StringUtil.isEmpty(result)) {
				HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(result);
				if (!StringUtil.isEmpty(map.get("error_code"))) {
					if ("21332".equals(map.get("error_code"))) {
						Toast.makeText(mContext, "您已经取消授权，需要重新授权后才能分享", Toast.LENGTH_SHORT).show();
						PreferenceUtil.setString(mContext, "sina_access_token", "");
						oauth();
						return;
					}else if("20019".equals(map.get("error_code"))){
						Toast.makeText(mContext, "一分钟内不能多次分享", Toast.LENGTH_SHORT).show();
						return;
					}
					Toast.makeText(mContext, "分享失败", Toast.LENGTH_SHORT).show();
					return;
				}
				Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
				if(mWebView != null)
					mWebView.setVisibility(View.GONE);
				isShare = true;
			} else {
				Toast.makeText(mContext, "分享失败", Toast.LENGTH_SHORT).show();
			}
		}
	}

	class SinaUser {
		public String access_token = "";
		public String remind_in = "";
		public String expires_in = "";
		public String uid = "";

		public SinaUser() {
			access_token = getString("sina_access_token");
			remind_in = getString("sina_remind_in");
			expires_in = getString("sina_expires_in");
			uid = getString("sina_uid");
		}

		public SinaUser(HashMap<String, String> map) {
			access_token = map.get("access_token");
			remind_in = map.get("remind_in");
			expires_in = map.get("expires_in");
			uid = map.get("uid");
		}

		private String getString(String key) {
			return PreferenceUtil.getString(mContext, key, "");
		}

		public String toString() {
			return access_token + "-" + remind_in + "-" + "-" + expires_in + "-" + uid;
		}
	}
}
