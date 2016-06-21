package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import com.android.iwo.media.apk.activity.R;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;

public class SelFriendActivity extends BaseActivity {

	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> mTemp = new ArrayList<HashMap<String, String>>();

	private ListView listView;
	private IwoAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tel_add);
		init();
	}

	private void init() {
		listView = (ListView) findViewById(R.id.list);
		listView.setDividerHeight(0);
		setTitle("添加手机联系人");
		mAdapter = new IwoAdapter(this, mData) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null)
					v = mInflater
							.inflate(R.layout.tel_list_item, parent, false);

				HashMap<String, String> map = mData.get(position);
				setItem(v, R.id.name, map.get("name"));
				setItem(v, R.id.tel, map.get("tel"));
				return v;
			}
		};

		listView.setAdapter(mAdapter);
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

	private class GetTel extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Void... params) {
			return getTel();
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (result != null) {
				mData.addAll(result);
				mTemp.addAll(result);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	private ArrayList<HashMap<String, String>> getTel() {
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> tel = null;
		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
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
				tel = new HashMap<String, String>();
				tel.put("name", contact.trim());
				tel.put("tel",
						PhoneNumber.trim().replace(" ", "").replace("+86", "")
								.replace("-", ""));
				data.add(tel);
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
