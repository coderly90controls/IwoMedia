package com.android.iwo.media.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.ChatUtils;
import com.android.iwo.media.action.DBhelper;
import com.android.iwo.media.action.IwoSQLiteHelper;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.chat.XmppClient;
import com.android.iwo.util.bitmapcache.SyncBitmap;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 白天查询好友结果
 * 
 * @author abc
 * 
 */
public class AddFriResActivtity extends BaseActivity {

	private String tel = "";
	private HashMap<String, String> user = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_res);
		init();
	}

	private void init() {
		tel = getIntent().getStringExtra("user_id");
		setBack(null);
		new GetData().execute();
	}

	private void setView(HashMap<String, String> map) {
		if (map == null)
			return;
		findViewById(R.id.layout).setVisibility(View.VISIBLE);
		setItem(R.id.name, map.get("nick_name") + " " + (StringUtil.isEmpty(map.get("age"))?"":map.get("age")));
		setItem(R.id.address, map.get("city_name"));
		setItem(R.id.say, map.get("sign"));
		ImageView head = (ImageView) findViewById(R.id.head);
		SyncBitmap.getIntence(mContext).loadImage(map.get("head_img"), head);

		setTitle(map.get("nick_name"));
		TextView btn = (TextView) findViewById(R.id.add);
		TextView delete = (TextView) findViewById(R.id.delete);
		delete.setVisibility(View.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new AddF().execute();
			}
		});
		delete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new Delete().execute();
			}
		});
	}

	private class GetData extends AsyncTask<Void, Void, HashMap<String, String>> {
		
		@Override
		protected void onPreExecute() {
			mLoadBar.show();
		}
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getMap_64(getUrl(AppConfig.NEW_USER_FIND), tel);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				user.putAll(result);
				setView(result);
			} else {
				makeText("暂无数据");
			}
			mLoadBar.dismiss();
		}
	}

	/**
	 * 添加好友
	 */
	private class AddF extends AsyncTask<Void, Void, Boolean> {
		protected Boolean doInBackground(Void... params) {
			return XmppClient.getInstance(mContext).friendAddDel(true, getUserTel(), tel);
		}

		protected void onPostExecute(Boolean result) {
			if (result) {
				ChatUtils.addFriend(mContext, user.get("user_name"), user.get("head_img"), user.get("nick_name"));
				finish();
			}
		}
	}

	/**
	 * 拒绝添加好友
	 */
	private class Delete extends AsyncTask<Void, Void, Boolean> {
		protected Boolean doInBackground(Void... params) {
			return XmppClient.getInstance(mContext).friendAddDel(false, getUserTel(), tel);
		}

		protected void onPostExecute(Boolean result) {
			if (result) {
				DBhelper db = new DBhelper(mContext, IwoSQLiteHelper.MESSAGE_TAB);
				db.delete("tab_msg" + PreferenceUtil.getString(mContext, "user_name", ""), "user_name", tel);
				mContext.sendBroadcast(new Intent("com.android.broadcast.receiver.media.refresh.CHAT_REFRESH_SHARE"));
				db.close();
				finish();
			}
		}
	}
}
