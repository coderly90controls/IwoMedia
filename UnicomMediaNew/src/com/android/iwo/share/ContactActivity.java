package com.android.iwo.share;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import net.sourceforge.pinyin4j.PinyinHelper;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.iwo.media.activity.BaseActivity;
import com.android.iwo.media.apk.activity.*;
import com.android.iwo.media.view.SideBar;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.FileCache;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class ContactActivity extends BaseActivity {

	private ListView lvContact;
	//private SideBar indexBar;
	//private WindowManager mWindowManager;
	//private TextView mDialogText;
	private ContactAdapter adapter;
	private ArrayList<HashMap<String, String>> telData = new ArrayList<HashMap<String, String>>();

	private boolean iscanback = false;
	private final String TEL_CACHE = "tel_cache";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_main);
		//mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		loading = (TextView) findViewById(R.id.loading);
		FileCache cache = new FileCache();
		String str = cache.readStringFormSD(TEL_CACHE);
		if (!StringUtil.isEmpty(str)) {
			ArrayList<HashMap<String, String>> tel = DataRequest.getArrayListFromJSONArrayString(str);
			if (tel != null) {
				String da = getIntent().getStringExtra("data");
				Logger.i("sdfsdfsdf" + da);
				if (da != null)
					for (HashMap<String, String> map : tel) {
						Logger.i("qqq" + map.get("tel"));
						if (da.contains(map.get("tel")))
							map.put("click", "t");
						else {
							map.put("click", "f");
						}
					}
				else {
					for (HashMap<String, String> map : tel) {
						map.put("click", "f");
					}
				}
				telData.addAll(tel);
				iscanback = true;
			}
		}
		new GetData().execute();
		findView();
	}
 
	private void findView() {
		lvContact = (ListView) this.findViewById(R.id.lvContact);
		adapter = new ContactAdapter(this);
		lvContact.setAdapter(adapter);
		//indexBar = (SideBar) findViewById(R.id.sideBar);
		//indexBar.setListView(lvContact);
		//mDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.share_list_position, null);
		//mDialogText.setVisibility(View.INVISIBLE);
		//WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
		//		WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		//				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
		//mWindowManager.addView(mDialogText, lp);
		//indexBar.setTextView(mDialogText);
		lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				ImageView ivSel = (ImageView) v.findViewById(R.id.contactitem_select_cb);
				if ("t".equals(telData.get(position).get("click"))) {
					telData.get(position).put("click", "f");
					ivSel.setBackgroundResource(R.drawable.share_ico_select);
				} else {
					telData.get(position).put("click", "t");
					ivSel.setBackgroundResource(R.drawable.share_ico_selecton1);
				}
			}
		});

		setBack(null);
		ImageView right = (ImageView) findViewById(R.id.right_img1);
		right.setImageResource(R.drawable.yes);
		right.setVisibility(View.VISIBLE);
		setTitle("选择");
		right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String result = "";
				String back = "";
				for (HashMap<String, String> map : telData) {
					if ("t".equals(map.get("click"))) {
						result += map.get("tel") + ";";
						back += map.get("name") + "_" + map.get("tel") + ";";
					}
				}
				Logger.i("---" + result);
				if (!StringUtil.isEmpty(result)) {
					Intent intent = new Intent();
					intent.putExtra("data", result);
					intent.putExtra("data_back", back);
					setResult(RESULT_OK, intent);
				}
				finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (!iscanback)
			return;
		setResult(RESULT_CANCELED);
		finish();
	}

	class ContactAdapter extends BaseAdapter implements SectionIndexer {
		private Context mContext;

		public ContactAdapter(Context mContext) {
			this.mContext = mContext;
		}

		@Override
		public int getCount() {
			return telData.size();
		}

		@Override
		public Object getItem(int position) {
			return telData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final String nickName = telData.get(position % telData.size()).get("name");
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.share_contact_item, null);
				viewHolder = new ViewHolder();
				viewHolder.tvCatalog = (TextView) convertView.findViewById(R.id.contactitem_catalog);
				// viewHolder.ivAvatar = (ImageView)
				// convertView.findViewById(R.id.contactitem_avatar_iv);
				viewHolder.tvNick = (TextView) convertView.findViewById(R.id.contactitem_nick);
				viewHolder.ivSel = (ImageView) convertView.findViewById(R.id.contactitem_select_cb);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			/*String catalog = telData.get(position).get("name_");
			if (position == 0) {
				viewHolder.tvCatalog.setVisibility(View.VISIBLE);
				viewHolder.tvCatalog.setText(catalog);
			} else {
				String lastCatalog = getPinYinHeadChar(telData.get(position - 1).get("name").substring(0, 1));
				
				Logger.i("catalog" + catalog);
				if (!StringUtil.isEmpty(catalog) && catalog.equals(lastCatalog)) {
					viewHolder.tvCatalog.setVisibility(View.GONE);
				} else {
					viewHolder.tvCatalog.setVisibility(View.VISIBLE);
					viewHolder.tvCatalog.setText(catalog);
				}
			}*/

			// viewHolder.ivAvatar.setImageResource(R.drawable.share_default_avatar);
			viewHolder.tvNick.setText(nickName + ":" + telData.get(position).get("tel"));
			if ("t".equals(telData.get(position).get("click"))) {
				viewHolder.ivSel.setBackgroundResource(R.drawable.share_ico_selecton1);
			} else {
				viewHolder.ivSel.setBackgroundResource(R.drawable.share_ico_select);
			}
			return convertView;
		}

		class ViewHolder {
			TextView tvCatalog;//
			// ImageView ivAvatar;//
			TextView tvNick;//
			ImageView ivSel;//
		}

		@Override
		public int getPositionForSection(int section) {
			for (int i = 0; i < telData.size(); i++) {
				String l = (telData.get(i).get("name_"));
				char firstChar = l.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		@Override
		public Object[] getSections() {
			return null;
		}
	}

	/*private void sortName(ArrayList<HashMap<String, String>> data) {
		Collections.sort(data, new ComPare());
		int size = SideBar.l.length;
		HashMap<String, String> map = null;
		String name = "";
		Logger.i("sort" + data);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < data.size(); j++) {
				name = getPinYinHeadChar(data.get(j).get("name").substring(0, 1));
				if (("" + SideBar.l[i]).equals(name)) {
					map = new HashMap<String, String>();
					map.putAll(data.get(j));
					map.put("name_", name);
					telData.add(map);
				}
			}
		}
		Logger.i("sort--" + data);
	}*/

	private TextView loading;

	private class GetData extends AsyncTask<Void, Void, Void> {

		private ArrayList<HashMap<String, String>> tel = null;

		@Override
		protected void onPreExecute() {
			if (loading != null && telData.size() == 0)
				loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			tel = getTel();
			Logger.i("add tel = " + tel.size() + "---" + telData.size() + 1);
			/*if (telData.size() == 0) {
				//sortName(tel);
			} else {
				if (tel != null && tel.size() != telData.size() + 1) {
					Logger.i("add tel");
					telData.clear();
					//sortName(tel);
				}
			}*/

			if(tel != null){
				telData.clear();
				telData.addAll(tel);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (loading != null)
				loading.setVisibility(View.GONE);
			if (0 != telData.size()) {
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
				// findView();
				Logger.i("-0-" + telData);

				String tel_num = DataRequest.getJsonFromArrayList(telData);
				Logger.i("-1-" + tel_num);
				if (!StringUtil.isEmpty(tel_num)) {
					FileCache cache = new FileCache();
					cache.writeStringToSD(TEL_CACHE, tel_num);
				}

				iscanback = true;
				adapter.notifyDataSetChanged();
			} else {

			}
			return false;
		}
	});

	private ArrayList<HashMap<String, String>> getTel() {
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> tel = null;
		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		while (cursor != null && cursor.moveToNext()) {
			int nameFieldColumnIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			String contact = cursor.getString(nameFieldColumnIndex);
			String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);

			while (phone != null && !phone.isClosed() && phone.moveToNext()) {
				String PhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				tel = new HashMap<String, String>();
				tel.put("name", contact.trim());
				tel.put("tel", PhoneNumber.trim().replace(" ", "").replace("+86", "").replace("-", ""));
				tel.put("click", "f");
				data.add(tel);
			}
			if (phone != null)
				phone.close();
		}
		Log.i("data", "data size" + data.size());
		if (cursor != null)
			cursor.close();
		if (tel != null) {
			String da = this.getIntent().getStringExtra("data");
			if (da != null)
				for (HashMap<String, String> map : data) {
					if (da.contains(map.get("tel")))
						map.put("click", "t");
				}
		
			/*ArrayList<HashMap<String, String>> da = (ArrayList<HashMap<String, String>>) this.getIntent().getSerializableExtra("data");
			if (da != null)
				for (HashMap<String, String> map : data) {
					for (HashMap<String, String> map2 : da) {
						if (map.get("tel").equals(map2.get("tel"))) {
							map.put("click", "t");
							break;
						}
					}
				}*/
		}
		return checkSameTel(data);
	}

	private ArrayList<HashMap<String, String>> checkSameTel(ArrayList<HashMap<String, String>> da) {
		if (da == null)
			return null;
		HashMap<String, String> map1 = null;
		HashMap<String, String> map2 = null;
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		boolean isAdd = true;
		for (int i = 0; i < da.size() - 1; i++) {
			map1 = da.get(i);
			for (int j = i + 1; j < da.size(); j++) {
				map2 = da.get(j);
				if (map1.get("name").equals(map2.get("name")) && map1.get("tel").equals(map2.get("tel"))) {
					isAdd = false;
					break;
				}
			}
			if (isAdd) {
				result.add(map1);
			} else
				isAdd = true;
		}
		if (da.size() > 0)
			result.add(da.get(da.size() - 1));
		return result;
	}

	/*public static String getPinYinHeadChar(String str) {
		String convert = "";
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {
				convert += pinyinArray[0].charAt(0);
			} else {
				convert += word;
			}
		}
		return convert.toUpperCase();
	}

	private class ComPare implements Comparator {
		@Override
		public int compare(Object lhs, Object rhs) {
			return ((HashMap<String, String>) lhs).get("name").compareTo(((HashMap<String, String>) rhs).get("name"));
		}
	}*/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);

	}
}