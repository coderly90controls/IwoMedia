package com.android.iwo.media.action;

import java.util.Set;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;

public class PushAction {
	private Context mContext;
	private static PushAction action = null;
	
	public static PushAction getInstance(Context context){
		if(action == null)
			action = new PushAction(context);
		return action;
	}
	public PushAction(Context context) {
		mContext = context;
	}
	
	public void init(){
		String id = PreferenceUtil.getString(mContext, "user_name", "");
		Logger.e("alias = " + id);
		JPushInterface.init(mContext);
		JPushInterface.setAliasAndTags(mContext, id, null, mAliasCallback);
		JPushInterface.resumePush(mContext);
	}
	
	public void stop(){
		JPushInterface.stopPush(mContext);
	}
	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Logger.i(logs);
				break;
			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Logger.e(logs);
				if (isConnected(mContext)) {
					mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
				} else {
					Logger.e("No network");
				}
				break;

			default:
				logs = "Failed with errorCode = " + code;
				Logger.e(logs);
			}
		}
	};

	public static boolean isConnected(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conn.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	private static final int MSG_SET_ALIAS = 1001;

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SET_ALIAS:
				Logger.e("Set alias in handler.");
				JPushInterface.setAliasAndTags(mContext, (String) msg.obj, null, mAliasCallback);
				break;
			default:
				Logger.e("Unhandled msg - " + msg.what);
			}
		}
	};
}
