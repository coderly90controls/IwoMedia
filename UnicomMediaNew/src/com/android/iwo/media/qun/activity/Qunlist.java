package com.android.iwo.media.qun.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.android.iwo.media.activity.BaseActivity;
import com.android.iwo.media.activity.FriendDetail;
import com.android.iwo.media.lenovo.R;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.DataRequest;

/**
 * 群成员
 */
public class Qunlist extends BaseActivity {

	private ListView mlView;
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private IwoAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_);
		init();
	}

	private void init() {
		setTitle(getIntent().getStringExtra("name") + "群成员");
		setBack(null);
		String data = getIntent().getStringExtra("data");

		ArrayList<HashMap<String, String>> list = DataRequest.getArrayListFromJSONArrayString(data);

		if (list != null) {
			mData.addAll(list);
		}
		mlView = (ListView) findViewById(R.id.list);
		mlView.setCacheColorHint(0);
		mlView.setDividerHeight(0);
		mAdapter = new IwoAdapter(this, mData) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {

				if (v == null)
					v = mInflater.inflate(R.layout.list_qun_mer_man_item, parent, false);
				final HashMap<String, String> map = mData.get(position);
				setItem(v, R.id.name, map.get("nickname"));
				setItem(v, R.id.desc, map.get("sign"));
				setImageView(v, R.id.head, map.get("head_img"));
				Button del = (Button) v.findViewById(R.id.delete);
				View line = v.findViewById(R.id.line);
				del.setVisibility(View.GONE);
				line.setVisibility(View.GONE);

				return v;
			}
		};
		mlView.setAdapter(mAdapter);
		mlView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(mContext, FriendDetail.class);
				intent.putExtra("attention", "4");
				intent.putExtra("id", mData.get(arg2).get("id"));
				startActivity(intent);
			}
		});
	}
}
