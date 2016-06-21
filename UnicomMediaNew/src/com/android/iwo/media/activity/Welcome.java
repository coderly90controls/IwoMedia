package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.MessageQueue.IdleHandler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.Constants;
import com.android.iwo.media.apk.activity.*;
import com.android.iwo.users.RegistActivity;
import com.android.iwo.users.UserLogin;
import com.android.iwo.users.UserLoginStep1;
import com.android.iwo.util.bitmapcache.SyncBitmap;
import com.test.iwomag.android.pubblico.adapter.ViewPageAdapter;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LocationManage;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.NetworkUtil;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

public class Welcome extends BaseActivity {
	private ImageView load_img;
	private ImageView animation;

	// private String url;
	private int screenWidth;

	// private boolean isBack = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		// 频幕
		Display display = getWindowManager().getDefaultDisplay();
		int screenHeight = display.getHeight();
		screenWidth = display.getWidth();
		PreferenceUtil.setInt(mContext, "screenHeight", screenHeight);
		PreferenceUtil.setInt(mContext, "screenWidth", screenWidth);
		Logger.i("高：" + screenHeight + "宽：" + screenWidth);

		load_img = (ImageView) findViewById(R.id.load_img);
		animation = (ImageView) findViewById(R.id.iwo_animation);
		// url = PreferenceUtil.getString(mContext, "start_img", "");
		if (NetworkUtil.isWIFIConnected(mContext)) {
			new GetAppUrl().execute();
		}
		RelativeLayout img = (RelativeLayout) findViewById(R.id.img);
		img.setVisibility(View.VISIBLE);
		findViewById(R.id.bottom).setVisibility(View.GONE);

		// if (PreferenceUtil.getBoolean(this, "page", true)) {
		// initPage();
		// } else {
		// RelativeLayout img = (RelativeLayout) findViewById(R.id.img);
		// img.setVisibility(View.VISIBLE);
		// findViewById(R.id.bottom).setVisibility(View.GONE);
		// if (StringUtil.isEmpty(getUid()) &&
		// !StringUtil.isEmpty(getUserTel())) {
		// new Thread(new Runnable() {
		// public void run() {
		// try {
		// Thread.sleep(3000);
		// } catch (InterruptedException e) {
		// }
		// findViewById(R.id.bottom).setVisibility(View.GONE);
		// Intent intent = new Intent(mContext, UserLogin.class);
		// intent.putExtra("from", "yes");
		// startActivity(intent);
		// }
		// }).start();
		//
		// } else if (StringUtil.isEmpty(getUid()) &&
		// StringUtil.isEmpty(getUserTel())) {
		// findViewById(R.id.bottom).setVisibility(View.VISIBLE);
		// } else if (!StringUtil.isEmpty(getUid())) {

		new Thread(new Runnable() {

			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
				String str = null;
				if (screenWidth > 720) {
					Logger.i("大于720频幕的手机：");
					str = "2";
				} else {
					str = "1";
				}
				if (NetworkUtil.isWIFIConnected(mContext)) {
					new GetImage().execute(str);// 开机启动图
				} else {
					PreferenceUtil.setString(mContext, "video_model", "day");
					Intent intent = new Intent(Welcome.this,
							ModelActivity.class);
					startActivity(intent);
					finish();
				}

				// new getAnimation().execute();
			}
		}).start();
		// }
		// }
		// init();
		if (NetworkUtil.isWIFIConnected(mContext)) {
			initAddress();
		}
		addShortcut();
	}

	private void init() {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.login) {// 调到登录
					Intent intent = new Intent(mContext, UserLoginStep1.class);
					intent.putExtra("from", "yes");
					startActivity(intent);
				} else if (v.getId() == R.id.regist) {// 调到注册
					startActivityForResult(new Intent(mContext,
							RegistActivity.class), 20140115);
				}
			}
		};

		findViewById(R.id.login).setOnClickListener(listener);
		findViewById(R.id.regist).setOnClickListener(listener);
	}

	private void initPage() {
		ViewPager page = (ViewPager) findViewById(R.id.page);
		page.setVisibility(View.VISIBLE);
		ArrayList<View> lists = new ArrayList<View>();
		View view = null;
		int ids[] = { R.drawable.in_1, R.drawable.in_2, R.drawable.in_3 };
		for (int i = 0; i < 3; i++) {
			view = new ImageView(this);
			view.setBackgroundResource(ids[i]);

			lists.add(view);
		}
		ViewPageAdapter adapter = new ViewPageAdapter(lists);
		page.setAdapter(adapter);

		page.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (arg0 == 2 && arg1 == 0.0) {
					PreferenceUtil.setBoolean(Welcome.this, "page", false);
					move();
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	private void move() {

		String model = getPre("video_model");
		Logger.i("开机启动：" + model + "---" + getPre("nightloginset"));

		// if (StringUtil.isEmpty(model) || StringUtil.isEmpty(getUid())) {
		// try {
		// Thread.sleep(2000);
		// } catch (InterruptedException e) {
		// }
		//
		// } else

		RelativeLayout img = (RelativeLayout) findViewById(R.id.img);
		img.setVisibility(View.VISIBLE);
		ViewPager page = (ViewPager) findViewById(R.id.page);
		page.setVisibility(View.GONE);
		PreferenceUtil.setString(mContext, "video_model", "day");
		Intent intent = new Intent(Welcome.this, ModelActivity.class);
		// intent = new Intent(Welcome.this, ModelActivity.class);
		startActivity(intent);
		finish();

	}

	private void initAddress() {
		final LocationManage manage = new LocationManage(this);
		new Thread(new Runnable() {

			@Override
			public void run() {
				manage.getLocation(mContext);
			}
		}).start();

		new SetAddress().execute(getPre("address_lng"), getPre("address_lat"));
	}

	private class SetAddress extends AsyncTask<String, Void, Void> {
		protected Void doInBackground(String... params) {
			DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_LON_LAT),
					params[0], params[1]);
			return null;
		}
	}

	private class GetImage extends
			AsyncTask<String, Void, HashMap<String, String>> {

		protected HashMap<String, String> doInBackground(String... params) {
			String base64 = DataRequest.getStringFromURL_Base64(
					getUrl(AppConfig.VIDEO_GET_AD), params[0]);
			return DataRequest.getHashMapFromJSONObjectString(base64);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			Logger.i("王城线程");
			if (result != null) {
				PreferenceUtil.setString(mContext, "start_img", "");
				SyncBitmap.getIntence(mContext).loadImage(
						result.get("ad_image"), load_img);
			}
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
					move();
				}
			}).start();

		}
	}

	/**
	 * 获取系统的APP_url
	 * 
	 * @author hanpengyuan
	 * 
	 */
	private class GetAppUrl extends
			AsyncTask<Void, Void, HashMap<String, String>> {
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest
					.getHashMapFromUrl_Base64(AppConfig.VIDEO_GET_APP_URL);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null && "1".equals(result.get("code"))) {
				HashMap<String, String> map = DataRequest
						.getHashMapFromJSONObjectString(result.get("data"));
				Logger.i("map数据：---  " + result.toString());
				if (map != null) {
					PreferenceUtil.setString(mContext, Constants.GET_APP_URL,
							map.get(Constants.GET_APP_URL));
					PreferenceUtil.setString(mContext,
							Constants.GET_APP_CHAT_PORT,
							map.get(Constants.GET_APP_CHAT_PORT));
					PreferenceUtil.setString(mContext,
							Constants.GET_APP_CHAT_IP,
							map.get(Constants.GET_APP_CHAT_IP));
				}

			}
		}
	}

}
