package com.android.iwo.media.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.iwo.media.apk.activity.*;

public class DriveTest extends Activity implements OnClickListener {
	private ImageView iBack;
	private WebView web;
	private String web_url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drive_test);
		iBack = (ImageView) findViewById(R.id.iback);
		TextView title=(TextView)findViewById(R.id.text_title);
		title.setText(getIntent().getStringExtra("title_name"));
		iBack.setOnClickListener(this);
		web = (WebView) findViewById(R.id.web);
		web.getSettings().setJavaScriptEnabled(true);
		web.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		web_url=getIntent().getStringExtra("web_url");
		web.loadUrl(web_url);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == iBack) {
			DriveTest.this.finish();
		}
	}

}
