package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.FileCache;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class MytellAddActivity extends BaseActivity {
	private ListView listView;
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private HashSet<String> hash_tell = new HashSet<String>();// 用来装选择的电话号码
	private IwoAdapter mAdapter;
	private CheckBox chekbox;
	private String contactId;
	private final String TEL_CACHE = "tel_cache";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tel_add);
		init();
	}

	private void init() {

		findViewById(R.id.tel_add_xuanzhong).setOnClickListener(
				new OnClickListener() {

					private Cursor phone_cuCursor;

					@Override
					public void onClick(View v) {
						// 将选中的号码回调回去
						if (hash_tell.size() >= 1) {
							Intent data = new Intent();
							Object[] array = hash_tell.toArray();
							Logger.i(array[0] + "");
							StringBuilder sb = new StringBuilder();
							for (int i = 0; i < array.length; i++) {
								phone_cuCursor = getContentResolver()
										.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
												null,
												ContactsContract.CommonDataKinds.Phone.CONTACT_ID
														+ "=" + array[i], null,
												null);
								while (phone_cuCursor != null
										&& !phone_cuCursor.isClosed()
										&& phone_cuCursor.moveToNext()) {
									String phone1 = phone_cuCursor.getString(phone_cuCursor
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
									if (phone1 != null) {
										phone1.trim().replace(" ", "")
												.replace("+86", "")
												.replace("-", "");
										sb.append(phone1);
										sb.append(",");
									} else {
										sb.append("12580");
									}
								}

							}

							phone_cuCursor.close();

							String string = sb.deleteCharAt(sb.length() - 1)
									.toString();
							data.putExtra("tell", string);
							setResult(119, data);
							finish();
						} else {
							makeText("请选择号码！");
						}
					}
				});
		findViewById(R.id.tel_delete_xuanzhong).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 清空那个集合就可以了
						hash_tell.clear();
						mAdapter.notifyDataSetChanged();
					}
				});
		setBack(null);
		listView = (ListView) findViewById(R.id.list);
		listView.setDividerHeight(0);
		setTitle("添加手机联系人");

		String str = FileCache.getInstance().readStringFormSD(TEL_CACHE);
		if (!StringUtil.isEmpty(str)) {
			ArrayList<HashMap<String, String>> cache_list = DataRequest
					.getArrayListFromJSONArrayString(str);
			mData.addAll(cache_list);
			if (hash_tell.size() > 0) {
				hash_tell.clear();
			}
		}
		/*
		 * else { ArrayList<HashMap<String, String>> list2 = getTel(); // String
		 * cache = // FileCache.getInstance().readStringFormSD(TEL_CACHE); //
		 * ArrayList<HashMap<String, String>> list = //
		 * DataRequest.getArrayListFromJSONArrayString(cache); if (list2 !=
		 * null) { mData.addAll(list2); // mLoadBar.dismiss(); } }
		 */
		mAdapter = new IwoAdapter(MytellAddActivity.this, mData) {

			@Override
			public View getView(int position, View v, ViewGroup parent) {
				View view = null;
				if (v == null) {
					view = mInflater.inflate(R.layout.tel_list_item, parent,
							false);
				} else {
					view = v;
				}

				HashMap<String, String> map = mData.get(position);
				setItem(view, R.id.name, map.get("name"));
				setItem(view, R.id.tel, map.get("tel"));

				chekbox = (CheckBox) view.findViewById(R.id.yaoqing_fenshare);

				if (hash_tell.contains(map.get("id"))) {
					chekbox.setChecked(true);
				} else {
					chekbox.setChecked(false);
				}
				return view;
			}

		};
		listView.setSelector(new ColorDrawable(0x000000));
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Map<String, String> map = (Map<String, String>) mAdapter
						.getItem(position);

				String ContactId = map.get("id");

				if (hash_tell.contains(ContactId))// 如果包涵了 说明是打勾状态 在点要实现去勾状态
				{
					hash_tell.remove(ContactId);
				} else {
					hash_tell.add(ContactId);
				}
				mAdapter.notifyDataSetChanged();
			}
		});
		new GetData().execute();

	}

	private ArrayList<HashMap<String, String>> getTel() {
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> tel = null;
		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		String my = getUserTel();
		while (cursor != null && cursor.moveToNext()) {
			int nameFieldColumnIndex = cursor
					.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			String contact = cursor.getString(nameFieldColumnIndex);
			contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			Cursor phone = cr.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ contactId, null, null);

			while (phone != null && !phone.isClosed() && phone.moveToNext()) {
				String PhoneNumber = phone
						.getString(phone
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				if (!PhoneNumber.equals(my)) {
					tel = new HashMap<String, String>();
					tel.put("id", contactId);
					tel.put("name", contact.trim());
					tel.put("tel",
							PhoneNumber.trim().replace(" ", "")
									.replace("+86", "").replace("-", ""));

					data.add(tel);
					// hash_tell.add(contactId);

				}
			}
			if (phone != null)
				phone.close();
		}
		Log.i("data", "电话号码id" + hash_tell.toString());
		Log.i("data", "电话号码个数" + data.size());
		if (cursor != null)
			cursor.close();
		return data;
	}

	// ----------------------------
	private class GetData extends AsyncTask<Void, Void, Void> {

		private ArrayList<HashMap<String, String>> tel = null;

		@Override
		protected void onPreExecute() {
			if (mData.size() == 0) {
				mLoadBar.setMessage("添加联系人...");
				mLoadBar.show();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			tel = getTel();
			Logger.i("add tel = " + tel.size() + "---" + mData.size() + 1);

			if (tel != null) {
				mData.clear();
				mData.addAll(tel);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mLoadBar.dismiss();
			if (0 != mData.size()) {
				Logger.i("add tel---");
				Message msg = new Message();
				msg.what = 0;
				mHandler.sendMessage(msg);
			}
		}
	}

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == 0) {
				Logger.i("-0-" + mData);

				String tel_num = DataRequest.getJsonFromArrayList(mData);
				Logger.i("-1-" + tel_num);
				if (!StringUtil.isEmpty(tel_num)) {
					FileCache cache = new FileCache();
					cache.writeStringToSD(TEL_CACHE, tel_num);
				}

				mAdapter.notifyDataSetChanged();
			} else {

			}
			return false;
		}
	});

}
