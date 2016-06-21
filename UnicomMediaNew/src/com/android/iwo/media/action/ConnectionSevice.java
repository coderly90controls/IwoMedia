package com.android.iwo.media.action;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.android.iwo.media.chat.XmppClient;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;

public class ConnectionSevice extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				String username = PreferenceUtil.getString(context, "user_name", "");
				String password = PreferenceUtil.getString(context, "user_pass", "");
				if (username != null && password != null) {
					Logger.i("TaxiConnectionListener-尝试登录");
					// 连接服务器
					if (XmppClient.getInstance(context).login(username)) {
						Logger.i("TaxiConnectionListener-登录成功");
						Intent intent = new Intent(context, ConnectionSevice.class);
						PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
						AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
						am.cancel(pi);
						if ("dialog".equals(intent.getStringExtra("type"))) {
							handler.sendMessage(new Message());
						}
					}
				}
			}
		}).start();
	}
}
