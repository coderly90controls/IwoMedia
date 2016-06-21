package com.android.iwo.media.preview.picture;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

import com.android.iwo.media.action.ActivityUtil;
import com.android.iwo.media.apk.activity.*;

public class PictureViewActivity extends FragmentActivity implements OnClickListener {

	// 屏幕宽度
	public static int screenWidth;
	// 屏幕高度
	public static int screenHeight;
	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.picture_view_activity);
		ActivityUtil.getInstance().add(this.getClass().getSimpleName(), this);
		initViews();
		

	}

	private void initViews() {
		screenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindow().getWindowManager().getDefaultDisplay().getHeight();
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		default:
			break;
		}

	}

}