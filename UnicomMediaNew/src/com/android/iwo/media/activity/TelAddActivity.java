package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.FileCache;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class TelAddActivity extends BaseActivity {

	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> mTemp = new ArrayList<HashMap<String, String>>();

	private ListView listView;
	private IwoAdapter mAdapter;
	private final String TEL_CACHE = "tel_cache";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tel_add);
		new GetSharePath().execute();
		init();
	}

	private void init() {
		mLoadBar.setMessage("添加联系人...");
		mLoadBar.show();
		setBack(null);
		listView = (ListView) findViewById(R.id.list);
		listView.setDividerHeight(0);
		setTitle("添加手机联系人");
		String cache = FileCache.getInstance().readStringFormSD(TEL_CACHE);
		ArrayList<HashMap<String, String>> list = DataRequest
				.getArrayListFromJSONArrayString(cache);
		if (list != null) {
			mData.addAll(list);
			mTemp.addAll(list);
		}
		mAdapter = new IwoAdapter(this, mData) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null)
					v = mInflater
							.inflate(R.layout.tel_list_item, parent, false);

				HashMap<String, String> map = mData.get(position);
				setItem(v, R.id.name, map.get("name"));
				setItem(v, R.id.tel, map.get("tel"));
				ImageView btn = (ImageView) v.findViewById(R.id.yaoqing);
				if ("1".equals(map.get("type"))) {
					btn.setBackgroundResource(R.drawable.tel_ok);
				} else if ("2".equals(map.get("type"))) {
					btn.setBackgroundResource(R.drawable.tel_add);
				}
				return v;
			}
		};

		listView.setSelector(new ColorDrawable(0x000000));
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new AbsListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				HashMap<String, String> map = mData.get(position);
				final String tel = map.get("tel");
				if (getUserTel().equals(tel)) {
					makeText("不能邀请自己");
					return;
				}
				if ("1".equals(map.get("type"))) {

				} else if ("2".equals(map.get("type"))) {

					final Handler handler = new Handler(new Handler.Callback() {
						public boolean handleMessage(Message msg) {
							if (msg.what == 1) {
								makeText("已发送邀请");
								mAdapter.notifyDataSetChanged();
							} else
								makeText("发送邀请失败");
							return false;
						}
					});
					new Thread(new Runnable() {
						public void run() {
						
						}
					}).start();

				} else {
					try {
						Uri smsToUri = Uri.parse("smsto:");
						Intent sendIntent = new Intent(Intent.ACTION_VIEW,
								smsToUri);
						sendIntent.putExtra("address", tel); //
						// 电话号码，这行去掉的话，默认就没有电话
						sendIntent.putExtra("sms_body",
								"视频分享是一款视频社交工具，不仅可以拍摄微视频，还可以视频交友，您也来吧! 下载"
										+ sharePath);
						sendIntent.setType("vnd.android-dir/mms-sms");
						startActivity(sendIntent);
					} catch (android.content.ActivityNotFoundException e) {
						Logger.i("发送短信消息：" + e.toString());
						makeText("无发送短信功能");
					}
				}
			}
		});
		new GetTel().execute();

		final EditText search = (EditText) findViewById(R.id.search);
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = search.getText() != null ? search.getText()
						.toString() : "";
				setSearch(str);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	String sharePath = "";

	protected class GetSharePath extends
			AsyncTask<Void, Void, HashMap<String, String>> {
		String str;

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest
					.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_DOWNLOAD));

		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (null != result) {
				if ("1".equals(result.get("code"))) {
					sharePath = result.get("data");
				}

			}

		}
	}

	private void setSearch(String tel) {
		mData.clear();
		for (int i = 0; i < mTemp.size(); i++) {
			HashMap<String, String> map = mTemp.get(i);
			if (map.get("tel").contains(tel) || map.get("name").contains(tel)) {
				mData.add(map);
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	private class GetTel extends AsyncTask<Void, Void, HashMap<String, String>> {
		ArrayList<HashMap<String, String>> tel = null;

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			tel = getTel();
			String contacts = "";
			if (tel != null) {
				for (HashMap<String, String> map : tel) {
					if (StringUtil.isPhone(map.get("tel")))
						contacts += map.get("tel") + ",";
				}
			}
			if (StringUtil.isEmpty(contacts))
				return null;
			return DataRequest.getMap_64(
					getUrl(AppConfig.VIDEO_IMPORT_CONTACTS),
					contacts.subSequence(0, contacts.length() - 1));
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				HashMap<String, String> register = DataRequest
						.getHashMapFromJSONObjectString(result.get("register"));
				ArrayList<HashMap<String, String>> not = null;
				ArrayList<HashMap<String, String>> yes = null;
				if (register != null
						&& !StringUtil.isEmpty(register.get("notfriend"))) {
					not = DataRequest.getArrayListFromJSONArrayString(register
							.get("notfriend"));
				}

				if (register != null
						&& !StringUtil.isEmpty(register.get("friend"))) {
					yes = DataRequest.getArrayListFromJSONArrayString(register
							.get("friend"));
				}
				Logger.i("yes" + yes);
				Logger.i("not" + not);

				ArrayList<HashMap<String, String>> yes_tel = new ArrayList<HashMap<String, String>>();
				ArrayList<HashMap<String, String>> not_tel = new ArrayList<HashMap<String, String>>();
				if (yes != null || not != null) {
					for (int i = 0; tel != null && i < tel.size(); i++) {
						HashMap<String, String> map = null;
						for (int j = 0; yes != null && j < yes.size(); j++) {
							if (tel.get(i).get("tel")
									.equals(yes.get(j).get("user_name"))) {
								tel.get(i).put("type", "-1");
								map = new HashMap<String, String>();

								map.put("tel", tel.get(i).get("tel"));
								map.put("name", tel.get(i).get("name"));
								map.put("type", "1");
								yes_tel.add(map);
								yes.remove(j);
								j--;
							}
						}

						for (int j = 0; not != null && j < not.size(); j++) {
							if (tel.get(i).get("tel")
									.equals(not.get(j).get("user_name"))) {
								tel.get(i).put("type", "-1");
								map = new HashMap<String, String>();
								map.put("tel", tel.get(i).get("tel"));
								map.put("name", tel.get(i).get("name"));
								map.put("type", "2");
								not_tel.add(map);
								not.remove(j);
								j--;
							}
						}
					}

					for (int i = 0; tel != null && i < tel.size(); i++) {
						if ("-1".equals(tel.get(i).get("type"))) {
							tel.remove(i);
							i--;
						}
					}
				}
				Logger.i("tel" + tel);
				mData.clear();
				mTemp.clear();
				if (tel != null) {
					tel.addAll(0, not_tel);
					tel.addAll(0, yes_tel);

					mData.addAll(0, tel);
					mTemp.addAll(0, tel);
					mAdapter.notifyDataSetChanged();
				}
				String cache = "[";
				String value = "";
				for (HashMap<String, String> map : mData) {
					value = "{\"tel\":\"" + map.get("tel") + "\",";
					value += "\"name\":\"" + map.get("name") + "\",";
					value += "\"type\":\"" + map.get("type") + "\"}";
					cache += value + ",";
				}
				cache = cache.substring(0, cache.length() - 1);
				cache += "]";
				Logger.i("缓存：" + cache);
				FileCache.getInstance().writeStringToSD(TEL_CACHE, cache);
			} else {
				makeText("读取联系人失败");
			}
			mLoadBar.dismiss();
		}
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
			String ContactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			Cursor phone = cr.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ ContactId, null, null);

			while (phone != null && !phone.isClosed() && phone.moveToNext()) {
				String PhoneNumber = phone
						.getString(phone
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				if (!PhoneNumber.equals(my)) {
					tel = new HashMap<String, String>();
					tel.put("name", contact.trim());
					tel.put("tel",
							PhoneNumber.trim().replace(" ", "")
									.replace("+86", "").replace("-", ""));
					data.add(tel);
				}
			}
			if (phone != null)
				phone.close();
		}
		Log.i("data", "data size" + data.size());
		if (cursor != null)
			cursor.close();
		return data;
	}

}
