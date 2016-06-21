package com.android.iwo.media.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.iwo.users.RegistActivity;
import com.android.iwo.users.UserLoginStep1;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;

/**
 * 登录/注册 引导界面
 * 
 * @author ZhangZhanZhong
 * 
 */
public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//setPubParame();
		init();
	}

	private void init() {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.login) {// 调到登录
					Intent intent = new Intent(MainActivity.this, UserLoginStep1.class);
					intent.putExtra("from", "yes");
					startActivityForResult(intent, 20140115);
				} else if (v.getId() == R.id.regist) {// 调到注册
					startActivityForResult(new Intent(MainActivity.this, RegistActivity.class), 20140115);
				}
			}
		};

		findViewById(R.id.login).setOnClickListener(listener);
		findViewById(R.id.regist).setOnClickListener(listener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 20140115 && resultCode == RESULT_OK) {
				PreferenceUtil.setString(MainActivity.this, "video_model", "day");
				Intent	intent = new Intent(this, ModelActivity.class);
			
			if (intent != null) {
				startActivity(intent);
				finish();
			}
		}
	}

	@Override
	public void onBackPressed() {
		System.exit(0);
		finish();
	}
}
