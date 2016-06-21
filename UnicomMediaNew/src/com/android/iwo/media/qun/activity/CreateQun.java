package com.android.iwo.media.qun.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.activity.BaseActivity;

import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.view.AbstractSpinerAdapter.IOnItemSelectListener;
import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.view.SpinerPopWindow;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class CreateQun extends BaseActivity {

	private SpinerPopWindow window1, window2, window3;
	private List<HashMap<String, String>> list1, list2, list3;
	private TextView text1, text2, text3;
	private RelativeLayout type_layout, address_1_layout, address_2_layout;
	private String cate_id = "", city_id = "", area_id = "";
	private EditText name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_qun);
		init();
	}

	private void init() {
		mLoadBar = new CommonDialog(this, "加载数据");
		mLoadBar.show();
		list1 = new ArrayList<HashMap<String, String>>();
		list2 = new ArrayList<HashMap<String, String>>();
		list3 = new ArrayList<HashMap<String, String>>();

		name = (EditText) findViewById(R.id.name);
		type_layout = (RelativeLayout) findViewById(R.id.type_layout);
		address_1_layout = (RelativeLayout) findViewById(R.id.address_1_layout);
		address_2_layout = (RelativeLayout) findViewById(R.id.address_2_layout);

		text1 = (TextView) findViewById(R.id.type);
		text2 = (TextView) findViewById(R.id.address_1);
		text3 = (TextView) findViewById(R.id.address_2);

		setBack(null);
		window1 = new SpinerPopWindow(this);
		window1.setKey("dict_name");
		window1.refreshData(list1, 0);
		window1.setItemListener(new IOnItemSelectListener() {

			@Override
			public void onItemClick(int pos) {
				cate_id = list1.get(pos).get("id");
				setItem(pos, text1, list1);
			}
		});

		window2 = new SpinerPopWindow(this);
		window2.setKey("dict_name");
		window2.refreshData(list2, 0);
		window2.setItemListener(new IOnItemSelectListener() {

			@Override
			public void onItemClick(int pos) {
				city_id = list2.get(pos).get("id");
				list3.clear();
				ArrayList<HashMap<String, String>> list = DataRequest.getArrayListFromJSONArrayString(list2.get(pos).get("children"));
				if (list != null) {
					list3.addAll(list);
					area_id = list3.get(0).get("id");
				}
				window3.refreshData(list3, 0);
				setItem(pos, text2, list2);
				setItem(0, text3, list3);
			}
		});

		window3 = new SpinerPopWindow(this);
		window3.setKey("dict_name");
		window3.refreshData(list3, 0);
		window3.setItemListener(new IOnItemSelectListener() {

			@Override
			public void onItemClick(int pos) {
				area_id = list3.get(pos).get("id");
				setItem(pos, text3, list3);
			}
		});

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.type_layout && window1 != null) {
					window1.setWidth(type_layout.getWidth());
					window1.showAsDropDown(type_layout);
				} else if (v.getId() == R.id.address_1_layout && window2 != null) {
					window2.setWidth(address_1_layout.getWidth());
					window2.showAsDropDown(address_1_layout);
				} else if (v.getId() == R.id.address_2_layout && window3 != null) {
					window3.setWidth(address_2_layout.getWidth());
					window3.showAsDropDown(address_2_layout);
				}
			}
		};

		type_layout.setOnClickListener(listener);
		address_1_layout.setOnClickListener(listener);
		address_2_layout.setOnClickListener(listener);

		TextView next = (TextView) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (StringUtil.isEmpty(name.getText().toString())) {
					makeText("请输入群名称");
					return;
				}
				Intent intent = new Intent(mContext, QunAddMember.class);
				intent.putExtra("name", name.getText().toString());
				startActivityForResult(intent, 10010);
			}
		});
		new Get_Dict_List().execute();
		new Get_City_List().execute();
	}

	private void setItem(int pos, TextView tex, List<HashMap<String, String>> list12) {
		Logger.i("pos" + pos + "---" + list12.size());
		if (pos >= 0 && pos <= list12.size()) {
			String value = list12.get(pos).get("dict_name");
			Logger.i("value " + value);
			tex.setText(value);
		}
	}

	private class Get_Dict_List extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
			return DataRequest.getArrayListFromUrl_Base64(getUrl(AppConfig.NEW_GET_DICT_LIST), "103");
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (result != null && result.size() != 0) {
				ArrayList<HashMap<String, String>> list = DataRequest.getArrayListFromJSONArrayString(result.get(0).get("children"));
				list1.addAll(list);
				window1.refreshData(list1, 0);
				setItem(0, text1, list1);
				cate_id = list1.get(0).get("id");
			}
		}
	}

	private class Get_City_List extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
			return DataRequest.getArrayListFromUrl_Base64(getUrl(AppConfig.NEW_GET_CITY_LIST), "101");
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (result != null && result.size() != 0) {
				ArrayList<HashMap<String, String>> list = DataRequest.getArrayListFromJSONArrayString(result.get(0).get("children"));
				list2.addAll(list);
				window2.refreshData(list2, 0);
				setItem(0, text2, list2);
				city_id = list2.get(0).get("id");

				list3.clear();
				ArrayList<HashMap<String, String>> list1 = DataRequest.getArrayListFromJSONArrayString(list2.get(0).get("children"));
				if (list1 != null)
					list3.addAll(list1);
				window3.refreshData(list3, 0);
				setItem(0, text3, list3);
				area_id = list3.get(0).get("id");
			}

			mLoadBar.dismiss();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 10010 && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}
}
