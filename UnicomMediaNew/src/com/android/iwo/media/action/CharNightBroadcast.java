package com.android.iwo.media.action;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.iwo.media.apk.activity.R;
import com.test.iwomag.android.pubblico.util.Logger;

public class CharNightBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(intent.getAction().equals("com.neter.broadcast.receiver.media.night.CHAT_BROADCAST_SHARE")){
			if (intent != null) {
				Logger.i(intent.toString());
				Bundle bundle = intent.getBundleExtra("data");
				Logger.i("----CharBroadcast" + bundle);
				if(bundle == null) return;
				// 1.得到NotificationManager
//				NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//				// 2.实例化一个通知，指定图标、概要、时间
//				Notification n = new Notification(R.drawable.ic_launcher, bundle.getString("user_name") + ":" + bundle.getString("body"), System.currentTimeMillis());
//				// 3.指定通知的标题、内容和intent
//				Intent in = new Intent(context, NightChat.class);
//				in.putExtra("name", bundle.getString("user_name"));
//				in.putExtra("userID", bundle.getString("userID"));
//				in.putExtra("head_img", bundle.getString("head_img"));
//				intent.putExtra("send_name", bundle.getString("nick_name"));
//				intent.putExtra("send_head", bundle.getString("head_img"));
//				PendingIntent pi = PendingIntent.getActivity(context, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
//				n.setLatestEventInfo(context, bundle.getString("user_name"), bundle.getString("body"), pi);
//				// 指定声音
//				n.defaults = Notification.DEFAULT_ALL;
//				n.flags |=  Notification.FLAG_AUTO_CANCEL;
//				// 4.发送通知
//				nm.notify(0, n);
			}
		}else if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE_SHARE")){
			
		}
	}

}
