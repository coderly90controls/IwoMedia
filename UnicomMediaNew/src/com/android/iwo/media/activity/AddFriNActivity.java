package com.android.iwo.media.activity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.R;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 黑夜添加群好友
 */
public class AddFriNActivity extends BaseActivity {

	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> mSearch = new ArrayList<HashMap<String, String>>();
	private ListView listView;
	private IwoAdapter adapter;
	private String id = "";
	private int n = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addqun_mer_n);
		init();
	}

	private void init() {
		setTitle("邀请好友");
		setBack(null);
		id = getIntent().getStringExtra("id");
		listView = (ListView) findViewById(R.id.list);
		listView.setDividerHeight(0);
		listView.setSelector(new ColorDrawable(0x00000000));

		adapter = new IwoAdapter(this, mData) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null)
					v = mInflater.inflate(R.layout.list_n_add_friend_item,
							parent, false);
				final HashMap<String, String> map = mData.get(position);
				setItem(v, R.id.name, map.get("nickname_b"));
				setItem(v, R.id.desc, map.get("sign"));
				setImageView(v, R.id.head, map.get("head_img"));
				LinearLayout add = (LinearLayout) v.findViewById(R.id.add);
				TextView add_tex = (TextView) v.findViewById(R.id.add_tex);
				ImageView add_img = (ImageView) v.findViewById(R.id.img);
				add.setClickable(false);
				final int pos = position;
				if (StringUtil.isEmpty(map.get("send"))) {
					add.setClickable(true);
					add.setBackgroundResource(R.drawable.bg_white_line);
					add_img.setBackgroundResource(R.drawable.yaoqing);
					add_tex.setText("邀请");
					add.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							n = pos;
							new Send().execute(map.get("user_id_b"));
						}
					});
				} else {
					add.setBackgroundColor(0xffffffff);
					add_img.setBackgroundResource(R.drawable.yiyaoqing);
					add_tex.setText("已邀请");
				}

				return v;
			}
		};

		listView.setAdapter(adapter);
		new GetData().execute();
		setSearch();
	}

	private void setSearch() {
		final EditText search = (EditText) findViewById(R.id.search);
		search.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				setEdit(search.getText().toString());
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void setEdit(String key) {
		if (StringUtil.isEmpty(key)) {
			mData.clear();
			mData.addAll(mSearch);
			adapter.notifyDataSetChanged();
		}
		ArrayList<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < mSearch.size(); i++) {
			if (key.equals(mSearch.get(i).get("user_name_b"))) {
				temp.add(mSearch.get(i));
			}
		}

		if (temp.size() != 0) {
			mData.clear();
			mData.addAll(temp);
			adapter.notifyDataSetChanged();
		}
	}

	private class GetData extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Void... params) {
			return DataRequest.getArrayListFromUrl_Base64(
					getUrl(AppConfig.NEW_GET_FRIEND_GUAN), getUid(), "friends");
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (result != null) {
				mData.clear();
				mData.addAll(result);
				mSearch.clear();
				mSearch.addAll(result);
				adapter.notifyDataSetChanged();
			}
		}
	}

	private class Send extends AsyncTask<String, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_GROUP_INVITEUSER), id, params[0]);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					mData.get(n).put("send", "t");
					makeText("发送邀请成功");
				} else {
					makeText(result.get("msg"));
				}
			}
		}
	}
}
