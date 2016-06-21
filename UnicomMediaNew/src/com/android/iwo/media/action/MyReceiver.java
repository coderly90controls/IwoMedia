package com.android.iwo.media.action;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

import com.android.iwo.media.activity.AdWebActivity;
import com.android.iwo.media.activity.ModelActivity;
import com.android.iwo.media.activity.RecommendedVideoListActivity;
import com.android.iwo.media.activity.VideoDetailActivity_new;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
			// send the Registration Id to your server...
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			Log.d(TAG, "[MyReceiver] 用户点击打开了通知" + getValue(bundle));

			HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(getValue(bundle));
			String str = map.get("url");
			Logger.i("推送接收到的数据" + map);
			Logger.i("转化拿到的数据" + str);
			Logger.i("推送接收到的数据getValue:" + getValue(bundle));
			Intent intent2 = null;
			if (StringUtil.isEmpty(str)) {
				if (ActivityUtil.getInstance().isclose("ModelActivity"))
					intent2 = new Intent(context, ModelActivity.class);
			} else if (str.contains("syte")) {
				String[] strs = str.split(",");
				Logger.i("推送视频详情" + strs[1]);
				intent2 = new Intent(context, VideoDetailActivity_new.class);
				if (strs.length >= 2) {
					intent2.putExtra("video_id", strs[1]);
				}
				if (strs.length >= 3) {
					intent2.putExtra("ch_name", strs[2]);
				}
			} else if (str.contains("v/video_list?id=")) {
				String[] strs = str.split("=");
				intent2 = new Intent(context, RecommendedVideoListActivity.class);
				intent2.putExtra("video_id", strs[1]);
				intent2.putExtra("video_name", "视频分享");
			} else {
				intent2 = new Intent(context, AdWebActivity.class);
				intent2.putExtra("url", str);
				intent2.putExtra("title", "视频分享");
			}
			// JPushInterface.reportNotificationOpened(context,
			// bundle.getString(JPushInterface.EXTRA_MSG_ID));
			if (intent2 != null) {
				intent2.putExtra("push", "push");
				intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent2);
			}

			// TODO 在这里推
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA) + "" + getValue(bundle));

		} else {
			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	private String getValue(Bundle bundle) {
		for (String key : bundle.keySet()) {
			if (key.contains("EXTRA"))
				return bundle.getString(key);
		}
		return null;
	}

	protected void setPubParame(String arency_id) {

	}
}
