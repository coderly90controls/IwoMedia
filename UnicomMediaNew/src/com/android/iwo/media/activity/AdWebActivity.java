package com.android.iwo.media.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.iwo.media.action.ActivityUtil;
import com.android.iwo.media.apk.activity.R;

import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 广告位Web
 * 
 * @author abc
 * 需要传入的参数：
 *   url  路径
 *   title 显示的名字
 * 
 */
public class AdWebActivity extends BaseActivity {
	private WebView webView;
	private String mUrl = "";
	//private boolean load = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		init();  
	}

	private void init() {
		setTitle(this.getIntent().getStringExtra("title"));
		setBack_text(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
				
			}
		});
		
		mLoadBar.show();
		mUrl = this.getIntent().getStringExtra("url");
		Logger.i("----" + mUrl);
		webView = (WebView) findViewById(R.id.web);
		webView.clearCache(true);
		webView.loadUrl(mUrl);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
		webSettings.setAppCacheEnabled(false);
		webView.requestFocus();
		webView.requestFocusFromTouch();
		webView.getSettings().setBlockNetworkImage(false);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				Logger.i("---onPageStarted" + url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (mLoadBar != null)
					mLoadBar.dismiss();
			}

		});
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 101:
			if (resultCode == 101) {
				webView.loadUrl(data.getStringExtra("url"));
			}

			break;
		default:
			break;
		}
		//load = true;
	}

	@Override
	public void onBackPressed() {
		Logger.i("0000外来");
		webView.goBack();
		if (webView.canGoBack()) {
			Logger.i("1111外来");
			webView.goBack();
		} 
		else if (!StringUtil.isEmpty(getIntent().getStringExtra("push"))) {
			Logger.i("2222外来");
			if (ActivityUtil.getInstance().isclose("ModelActivity")) {
				Logger.i("3333外来");
				startActivity(new Intent(mContext, ModelActivity.class));
			}
			super.onBackPressed();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if(keyCode==KeyEvent.KEYCODE_BACK){
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
