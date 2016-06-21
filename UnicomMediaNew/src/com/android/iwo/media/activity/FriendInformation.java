package com.android.iwo.media.activity;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 此界面需传好友ID
 * 
 * @author hanpengyuan
 * 
 */
public class FriendInformation extends BaseActivity {
	private String id = "";
	private String name = "";
	private HashMap<String, String> userData = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_information);
		id = getIntent().getStringExtra("id");
		name = getIntent().getStringExtra("name");
		init();
		setBack(null);
		setTitle("详细资料");
		id = getIntent().getStringExtra("id");
		// TODO 测试数据，以后释放
		// name = getIntent().getStringExtra("name");
		new Infor().execute();
	}

	private void init() {
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
	}

	private void setUser() {
		setItem(R.id.edit_1, userData.get("user_name"));// 账号
		setItem(R.id.edit_2, userData.get("nick_name"));
		if ("1".equals(userData.get("sex"))) {
			setItem(R.id.edit_4, "男");
		} else if ("2".equals(userData.get("sex"))) {
			setItem(R.id.edit_4, "女");
		} else {
			setItem(R.id.edit_4, "男");
		}
		String area_name = StringUtil.isEmpty(userData.get("area_name")) ? "暂无区域"
				: userData.get("area_name");
		String city_name = StringUtil.isEmpty(userData.get("city_name")) ? "暂无城市"
				: userData.get("city_name");

		String user_trade = StringUtil.isEmpty(userData.get("user_trade")) ? "暂无职业"
				: userData.get("user_trade");
		String user_job = StringUtil.isEmpty(userData.get("user_job")) ? "暂无职位"
				: userData.get("user_job");
		setItem(R.id.edit_8, user_trade + "," + user_job);
		setItem(R.id.edit_6, city_name + "," + area_name);
		setItem(R.id.edit_5, userData.get("age"));
		// setItem(R.id.edit_6, userData.get("city_id"));
		setItem(R.id.edit_7, userData.get("sign"));

		setItem(R.id.edit_9, userData.get("user_company"));
		setItem(R.id.edit_10, userData.get("user_school"));
		setItem(R.id.edit_11, userData.get("user_fond"));

		// text5 = (EditText) findViewById(R.id.edit_5);//
		// text6 = (EditText) findViewById(R.id.edit_6);// 所在地
		// text7 = (EditText) findViewById(R.id.edit_7);// 个性签名
		// text8 = (EditText) findViewById(R.id.edit_8);// 职业
		// text9 = (EditText) findViewById(R.id.edit_9);// 公司
		// text10 = (EditText) findViewById(R.id.edit_10);// 学校
		// text11 = (EditText) findViewById(R.id.edit_11);// 爱好
		//
		// text5.setText(mData.get("age"));
		//
		// // text6.setText(mData.get("city_id"));
		// text7.setText(mData.get("sign"));
		// text8.setText(mData.get("user_profession"));
		// text9.setText(mData.get("user_company"));
		// text10.setText(mData.get("user_school"));
		// text11.setText(mData.get("user_fond"));

	}

	private class Infor extends AsyncTask<Void, Void, HashMap<String, String>> {
		protected HashMap<String, String> doInBackground(Void... params) {
			// ArrayList<HashMap<String, String>> list=
			// DataRequest.getArrayListFromUrl_Base64(AppConfig.NEW_FR_GET_FRIEND_INFO
			// + AppConfig.END, id);
			String userInfo = null;
			Map<String, String> map;
			Map<String, String> itemMap;
			if ("day".equals(getMode())) {
				map = DataRequest.getHashMapFromUrl_Base64(
						getUrl(AppConfig.NEW_FR_GET_FRIEND_INFO), id, name);
			} else {
				map = DataRequest.getHashMapFromUrl_Base64(
						getUrl(AppConfig.NEW_FR_GET_FRIEND_INFO), id, name);
			}
			if (map != null) {
				if ("1".equals(map.get("code"))) {
					itemMap = DataRequest.getHashMapFromJSONObjectString(map
							.get("data"));
					if (itemMap != null) {
						return DataRequest
								.getHashMapFromJSONObjectString(itemMap
										.get("friend"));
					}
				}

			}
			return null;
		}

		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				Logger.i("好友信息：" + result.toString());
				userData.putAll(result);
				setUser();
				findViewById(R.id.user_edit_scroll).setVisibility(View.VISIBLE);
			} else {
			}
			mLoadBar.dismiss();
		}
	}

}
