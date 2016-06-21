package com.android.iwo.media.qun.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.iwo.media.action.Constants;
import com.android.iwo.media.action.DBhelper;
import com.android.iwo.media.action.IwoSQLiteHelper;
import com.android.iwo.media.activity.BaseActivity;
import com.android.iwo.media.chat.GroupInfo;
import com.android.iwo.media.chat.UserInfo;
import com.android.iwo.media.chat.XmppClient;
import com.android.iwo.media.lenovo.R;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class QunAddMember extends BaseActivity {

	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	private ListView listView;
	private IwoAdapter mAdapter;
	private String ids = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addqun_member);

		init();
	}

	private void init() {
		setTitle("邀请群成员");
		setBack(null);

		ImageView right = (ImageView) findViewById(R.id.right_img);
		right.setVisibility(View.VISIBLE);
		right.setBackgroundResource(R.drawable.yes);
		right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!StringUtil.isEmpty(ids)) {
					new Add().execute();
				} else {
					makeText("请选择要邀请的好友");
				}
			}
		});
		listView = (ListView) findViewById(R.id.list);
		listView.setCacheColorHint(0);
		listView.setDividerHeight(0);
		listView.setSelector(new ColorDrawable(0x00000000));

		mAdapter = new IwoAdapter(this, mData) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null)
					v = mInflater.inflate(R.layout.list_add_qun, parent, false);
				HashMap<String, String> map = mData.get(position);
				setItem(v, R.id.tel, map.get("name"));
				setItem(v, R.id.name, map.get("nick"));
				setImageView(v, R.id.head, map.get("avatar"));
				ImageView add = (ImageView) v.findViewById(R.id.add);
				if (StringUtil.isEmpty(map.get("click")) || "f".equals(map.get("click"))) {
					BitmapUtil.setBackgroundResource(add, R.drawable.tianjiahaoyou);
				} else {
					BitmapUtil.setBackgroundResource(add, R.drawable.yesred);
				}
				return v;
			}
		};

		listView.setAdapter(mAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
				ImageView add = (ImageView) v.findViewById(R.id.add);
				HashMap<String, String> map = mData.get(arg2);
				if (StringUtil.isEmpty(map.get("click")) || "t".equals(map.get("click"))) {
					mData.get(arg2).put("click", "f");
					BitmapUtil.setBackgroundResource(add, R.drawable.tianjiahaoyou);
					ids = ids.replace(map.get("user_id_b") + ",", "");
				} else {
					mData.get(arg2).put("click", "t");
					BitmapUtil.setBackgroundResource(add, R.drawable.yesred);
					ids += map.get("user_id_b") + ",";
				}
			}
		});

		new Getdata().execute();
		setSearch();
	}

	private void setSearch() {
		final EditText search = (EditText) findViewById(R.id.search);
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!StringUtil.isEmpty(search.getText().toString())) {
					getSearchData(search.getText().toString());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void getSearchData(String str) {
		ArrayList<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < data.size() && !StringUtil.isEmpty(str); i++) {
			String tel = data.get(i).get("user_name_b");
			// String nick = searchData.get(i).get("nickname_b");
			if (!StringUtil.isEmpty(tel) && tel.contains(str)) {
				temp.add(data.get(i));
			}
		}
		mData.clear();
		if (temp.size() == 0) {
			mData.addAll(data);
		} else {
			mData.addAll(temp);
		}
		mAdapter.notifyDataSetChanged();
	}

	private class Getdata extends AsyncTask<Void, HashMap<String, String>, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
			DBhelper per = new DBhelper(mContext, IwoSQLiteHelper.FRIEND_TAB);
			return per.select("tab_" + PreferenceUtil.getString(mContext, "user_name", ""));
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (result != null) {
				mData.addAll(result);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	private class Add extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			GroupInfo info = new GroupInfo();
			info.setName(getIntent().getStringExtra("name"));
			info.setCategory("1");
			info.setCity("2");
			info.setArea("3");
			info.setMaxNumber("500");
			info.setDescription("12132312");
			info.setAvatar("http://www.baidu.com/img/bdlogo.gif");

			ArrayList<UserInfo> infos = new ArrayList<UserInfo>();
			for (HashMap<String, String> map : mData) {
				if ("t".equals(map.get("click"))) {
					UserInfo userInfo = new UserInfo();
					userInfo.setAge(map.get("age"));
					userInfo.setAvatar(map.get("avatar"));
					userInfo.setJid(Constants.CHAT_TYPE + map.get("jid"));
					userInfo.setName(map.get("name"));
					userInfo.setNick(map.get("nick"));
					userInfo.setSex(map.get("sex"));
					userInfo.setSubscription(map.get("subscription"));
					userInfo.setSignature(map.get("signature"));
					infos.add(userInfo);
				}
			}

			UserInfo userInfo = new UserInfo();
			userInfo.setAge("");
			userInfo.setAvatar(getPre("user_head"));
			userInfo.setJid(Constants.CHAT_TYPE + getPre("user_name"));
			userInfo.setName(getPre("real_name"));
			userInfo.setNick(getPre("user_nickname"));
			userInfo.setSex("");
			userInfo.setSubscription("");
			userInfo.setSignature("");
			infos.add(userInfo);

			return XmppClient.getInstance(mContext).joinXml(info, infos);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				makeText("创建成功");
				onBackPressed();
			} else {
				makeText("邀请失败");
			}
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}
}
