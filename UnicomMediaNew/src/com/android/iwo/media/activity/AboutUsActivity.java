package com.android.iwo.media.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.android.iwo.media.apk.activity.R;

public class AboutUsActivity extends BaseActivity {
	private WebView wb_aboutus_content;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		// setTitle("关于我们");
		setBack_text(null);
		findViewById(R.id.title_img).setVisibility(View.VISIBLE);

		// mCache = ACache.get(mContext);

		wb_aboutus_content = (WebView) findViewById(R.id.wb_aboutus_content);
		// 取消垂直 和水平进度条显示
		wb_aboutus_content.setVerticalScrollBarEnabled(false);
		wb_aboutus_content.setHorizontalScrollBarEnabled(false);
		wb_aboutus_content.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		// 支持JavaScrip
		wb_aboutus_content.getSettings().setJavaScriptEnabled(true);
		wb_aboutus_content.getSettings()
				.setJavaScriptCanOpenWindowsAutomatically(true);// 允许js弹出窗口
		wb_aboutus_content.getSettings().setDomStorageEnabled(true);
		wb_aboutus_content.requestFocus();

		wb_aboutus_content.loadUrl("file:///android_asset/about_us.html");
	}

}
