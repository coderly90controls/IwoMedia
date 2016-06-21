package com.android.iwo.media.activity;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.iwo.media.action.ActivityUtil;
import com.android.iwo.media.action.ConnectionSevice;
import com.android.iwo.media.action.PushAction;
import com.android.iwo.media.apk.activity.*;
import com.android.iwo.media.chat.XmppClient;
import com.android.iwo.users.UserLogin;
import com.test.iwomag.android.pubblico.util.AppUtil;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class DialogActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		referrals();
	}

	/**
	 * 下线通知
	 */
	public void referrals() {
		TextView title = (TextView) findViewById(R.id.title);
		TextView name = (TextView) findViewById(R.id.name);
		TextView ok = (TextView) findViewById(R.id.ok);
		TextView cancle = (TextView) findViewById(R.id.cancle);

		title.setText("下线通知");
		name.setText("连接被关闭，当前账号已在其它地方登录。");
		ok.setText("登录");
		cancle.setText("退出");
		ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Logger.i("下线通知 ok");
				String name = PreferenceUtil.getString(mContext, "user_name",
						"");
				if (!StringUtil.isEmpty(name)) {
					Calendar c = Calendar.getInstance();
					Intent intent = new Intent(mContext, ConnectionSevice.class);
					intent.putExtra("type", "dialog");
					PendingIntent pi = PendingIntent.getBroadcast(mContext, 0,
							intent, 0);
					AlarmManager am = (AlarmManager) mContext
							.getSystemService(Activity.ALARM_SERVICE);
					am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);// 设置闹钟
					am.setRepeating(AlarmManager.RTC_WAKEUP,
							c.getTimeInMillis(), (10 * 1000), pi);// 重复设置
				}
				finish();
			}
		});
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Logger.i("下线通知 cancle");
				PreferenceUtil.setString(mContext, "user_id", "");
				PreferenceUtil.setString(mContext, "real_name", "");
				PreferenceUtil.setString(mContext, "user_pass", "");
				PreferenceUtil.setString(mContext, "user_city_id", "");
				PreferenceUtil.setString(mContext, "user_nickname", "");
				PreferenceUtil.setString(mContext, "n_user_id", "");
				PreferenceUtil.setString(mContext, "n_real_name", "");
				PreferenceUtil.setString(mContext, "n_user_city_id", "");
				PreferenceUtil.setString(mContext, "n_user_nickname", "");
				PreferenceUtil.setString(mContext, "video_model", "");
				PreferenceUtil.setString(mContext, "user_city_id", "");
				PreferenceUtil.setString(mContext, "user_area_id", "");
				PreferenceUtil.setString(mContext, "user_area", "");
				PreferenceUtil.setString(mContext, "user_position", "");
				PreferenceUtil.setString(mContext, "user_industry_id", "");
				PreferenceUtil.setString(mContext, "user_position_id", "");
				PreferenceUtil.setString(mContext, "nightloginset", "");
				PreferenceUtil.setString(mContext, "dayloginset", "");
				PreferenceUtil.setString(mContext, "user_area_id", "");
				PreferenceUtil.setString(mContext, "area_name", "");
				AppUtil.END = "";
				ActivityUtil.getInstance().deleteAll();
				mContext.startActivity(new Intent(mContext, UserLogin.class));
				XmppClient.getInstance(mContext).colseConn();
				NotificationManager notificationManager = (NotificationManager) mContext
						.getSystemService("notification");
				notificationManager.cancel(10010);
				PushAction.getInstance(mContext).stop();
				finish();
			}
		});
		Logger.i("下线通知");
	}
}
