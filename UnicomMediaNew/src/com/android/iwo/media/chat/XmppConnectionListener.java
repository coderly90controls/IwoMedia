package com.android.iwo.media.chat;

import java.util.Calendar;

import org.jivesoftware.smack.ConnectionListener;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.android.iwo.media.action.ConnectionSevice;
import com.android.iwo.media.activity.DialogActivity;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 聊天连接事件监听
 */
public class XmppConnectionListener implements ConnectionListener {

	private Context context;

	public XmppConnectionListener(Context c) {
		context = c;
	}

	@Override
	public void connectionClosed() {
		Logger.i("connectionClosed-连接关闭");
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		Logger.i("connectionClosedOnError-连接异常关闭" + e.toString());
		
		if("stream:error (conflict)".equals(e.toString())){
			Intent intent = new Intent(context, DialogActivity.class);
			context.startActivity(intent);
		}else {
			login();
		}
	}
	
	/**
	 * 重新登录
	 */
	private void login(){
		String name = PreferenceUtil.getString(context, "user_name", "");
		if (!StringUtil.isEmpty(name)) {
			Calendar c = Calendar.getInstance();
			Intent intent = new Intent(context, ConnectionSevice.class);
			PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
			AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
			am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);// 设置闹钟
			am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), (10 * 1000), pi);// 重复设置
		}
	}

	@Override
	public void reconnectingIn(int arg0) {
		Logger.i("XmppConnectionListener : reconnectingIn");
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
		Logger.i("XmppConnectionListener : reconnectionFailed");
	}

	@Override
	public void reconnectionSuccessful() {
		Logger.i("XmppConnectionListener : reconnectionSuccessful");
	}

}
