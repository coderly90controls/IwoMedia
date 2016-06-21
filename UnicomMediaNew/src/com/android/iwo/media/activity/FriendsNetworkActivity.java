package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;

public class FriendsNetworkActivity extends BaseActivity {
	private ListView listView;
	private IwoAdapter mAdapter;
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singleton_listview);
		id = getIntent().getStringExtra("id");
		new GetData().execute();
		setTitle("他（她）的人脉");
		listView = (ListView) findViewById(R.id.singleton_list);
		setBack(null);
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
	}

	private void init() {

		mAdapter = new IwoAdapter(this, mData) {
			ArrayList<HashMap<String, String>> jobData = new ArrayList<HashMap<String, String>>();
			StringBuilder sbBuilder = new StringBuilder();

			@Override
			public View getView(int position, View v, ViewGroup parent) {
				HashMap<String, String> map = mData.get(position);
				if (null == v) {
					v = mInflater.inflate(R.layout.view_friends_network, null);
				}
				ImageView frends_network_item_img = (ImageView) v
						.findViewById(R.id.frends_network_item_img);
				frends_network_item_img.setImageResource(setNetworkImg(map
						.get("user_trade")));
				jobData.clear();
				jobData.addAll(DataRequest.getArrayListFromJSONArrayString(map
						.get("job")));
				sbBuilder.delete(0, sbBuilder.length());
				for (Map<String, String> map2 : jobData) {
					sbBuilder.append(map2.get("num") + "名"
							+ map2.get("user_job") + "、");
					Logger.i(sbBuilder.toString());
				}

				setItem(v, R.id.frends_network_item_text, sbBuilder
						.deleteCharAt(sbBuilder.length() - 1).toString());
				return v;
			}
		};
		listView.setCacheColorHint(0);
		listView.setDividerHeight(0);
		listView.setAdapter(mAdapter);
	}

	protected class GetData extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Void... params) {
			return DataRequest.getArrayListFromUrl_Base64(
					getUrl(AppConfig.NEW_FR_GET_CONTACTS), id);
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (null != result) {
				mData.addAll(result);
				init();
				findViewById(R.id.singleton_list).setVisibility(View.VISIBLE);
			} else {
				FriendsNetworkActivity.this.makeText("暂无数据");
			}
			mLoadBar.dismiss();
		}
	}

	private int setNetworkImg(String str) {
		if ("政府".equals(str)) {
			return R.drawable.zhengfu;
		} else if ("金融业".equals(str)) {
			return R.drawable.jinrong;
		} else if ("IT/互联网".equals(str)) {
			return R.drawable.hulianwang;
		} else if ("文化/传媒".equals(str)) {
			return R.drawable.wentijiaoy;
		} else if ("贸易零售".equals(str)) {
			return R.drawable.maoyils;
		} else if ("文体教育".equals(str)) {
			return R.drawable.wentijiaoy;
		} else if ("制造业".equals(str)) {
			return R.drawable.zhizaoye;
		} else if ("交通运输".equals(str)) {
			return R.drawable.jiaotongyuns;
		} else if ("服务业".equals(str)) {
			return R.drawable.fuwu;
		} else {
			return R.drawable.weizhi;
		}

	}
}
