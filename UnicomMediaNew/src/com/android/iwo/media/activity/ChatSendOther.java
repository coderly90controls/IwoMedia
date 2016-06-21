package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iwo.media.action.ActivityUtil;
import com.android.iwo.media.action.DBhelper;
import com.android.iwo.media.action.FaceAction;
import com.android.iwo.media.action.IwoSQLiteHelper;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.chat.XmppClient;
import com.android.iwo.media.chat.XmppPacketExtension;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 消息转发
 */
public class ChatSendOther extends BaseActivity {
	private long time_str = 0;
	private ArrayList<HashMap<String, String>> list;
	private ArrayList<HashMap<String, String>> nearlist = new ArrayList<HashMap<String,String>>();
	private ArrayList<HashMap<String, String>> searchList;
	private String userID = "";
	private IwoAdapter adapter;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_chat_send_other);
		init();
	}

	private void init() {
		setTitle("转发给好友");
		setBack(null);
		DBhelper per = new DBhelper(this, IwoSQLiteHelper.FRIEND_TAB);
		DBhelper db = new DBhelper(mContext, IwoSQLiteHelper.MESSAGE_TAB);
		
		list = db.select("tab_msg" + PreferenceUtil.getString(mContext, "user_name", ""), "timestamp", true);

		if (list == null)
			list = new ArrayList<HashMap<String, String>>();
		for(int i=0; i<list.size(); i++){
			list.get(i).put("nick", "" + list.get(i).get("nick_name"));
			list.get(i).put("avatar", "" + list.get(i).get("head_img"));
			list.get(i).put("name", "" + list.get(i).get("user_id"));
		}
		nearlist.addAll(list);
		searchList = per.select("tab_" + getPre("user_name"));;
		//Logger.i("好友列表：" + searchList);
		//Logger.i("好友列表：" + nearlist);
		db.close();
		per.close();
		adapter = new IwoAdapter(this, list) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null)
					v = mInflater.inflate(R.layout.chat_send_other_item, parent, false);
				HashMap<String, String> map = list.get(position);
				setItem(v, R.id.name, StringUtil.isEmpty(map.get("nick")) ? "" : map.get("nick"));
				setItem(v, R.id.sear_name, map.get("search_name"));
				LoadBitmap.getIntence().loadImage(map.get("avatar"), (ImageView) v.findViewById(R.id.head));
				return v;
			}
		};
		ListView listView = (ListView) findViewById(R.id.list);
		listView.setSelector(new ColorDrawable(0x00000000));

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final Dialog dialog = new Dialog(mContext, R.style.dialog_style);
				//View view2 = View.inflate(mContext, R.layout.chat_send_dialog, null);
				LayoutInflater inflater = LayoutInflater.from(mContext);
				View view2 = inflater.inflate(R.layout.chat_send_dialog, null);
				//setImgSize(view2, 40, 1);
				
				dialog.setCancelable(true);// 不可以用“返回键”取消
				dialog.setContentView(view2, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
				
				TextView name = (TextView)view2.findViewById(R.id.name);
				TextView ok = (TextView)view2.findViewById(R.id.ok);
				TextView cancle = (TextView)view2.findViewById(R.id.cancle);
				
				final HashMap<String, String> map1 = list.get(position);
				Logger.i("map1 " + map1);
				name.setText(map1.get("nick"));
				OnClickListener listener = new OnClickListener() {
					public void onClick(View v) {
						switch (v.getId()) {
						case R.id.ok:
							HashMap<String, String> map = (HashMap<String, String>) getIntent().getSerializableExtra("map");
							map.put("user_id", map1.get("name"));
							map.put("timestamp", "" + System.currentTimeMillis()/1000);
							map.put("head_img", getPre("user_head"));
							map.put("user_name", map1.get("name"));
							map.put("nick_name", map1.get("nick"));
							
							map.put("send", "1");
							
							ActivityUtil.getInstance().delete("ChatActivity");
							ActivityUtil.getInstance().delete("PictureViewActivity");
							userID = map1.get("name");
							Intent intent = new Intent(mContext, ChatActivity.class);
							intent.putExtra("name", map1.get("nick"));
							intent.putExtra("userID", map1.get("name"));
							intent.putExtra("head_img", map1.get("avatar"));
							startActivity(intent);
							finish();
							String msg = map.get("msg_tex");
							Logger.i("----" + map);
							Logger.i("----====" + msg);
							if("2".equals(map.get("type"))){
								msg = map.get("richbody");
							}else if("4".equals(map.get("type"))){
								msg = (msg + "," + map.get("richbody")).replace("[视频],", "");
								map.put("msg_tex", msg.split(",")[0]);
								map.put("richbody", msg.split(",")[1]);
							}
							Logger.i("----====" + msg);
							send(map);
							new GetData().execute(map.get("type"), msg, map.get("duartion"));
							break;
						case R.id.cancel:
							break;
						}
						dialog.dismiss();
					}
				};
				ok.setOnClickListener(listener);
				cancle.setOnClickListener(listener);
				dialog.show();
			}
		});
		
		final EditText search = (EditText)findViewById(R.id.search);
		search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				search(search.getText().toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	private void search(String key){
		list.clear();
		Logger.i("key" + key +"---");
		if(StringUtil.isEmpty(key)){
			list.addAll(nearlist);
			adapter.notifyDataSetChanged();
			return;
		}
		ArrayList<HashMap<String, String>> l = new ArrayList<HashMap<String,String>>();
		for(int i=0; i<searchList.size(); i++){
			HashMap<String, String> map = searchList.get(i);
			if(map.get("nick").contains(key) || map.get("user_name").contains(key)){
				if(map.get("user_name").contains(key)&&!map.get("nick").contains(key))
					map.put("search_name", "昵称：" + map.get("user_name"));
				l.add(map);
			}
		}
		
		list.addAll(l);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 1.文字，表情。2.图片。3.语音。4.地理位置
	 */
	private class GetData extends AsyncTask<String, Void, Boolean> {
		String type = "", chat_con = "";

		@Override
		protected Boolean doInBackground(String... arg0) {
			// 消息发送
			type = arg0[0];
			chat_con = arg0[1];
			boolean t = false;
			XmppPacketExtension xmp = new XmppPacketExtension();
			xmp.setCategory(type);
			xmp.setTimestamp(System.currentTimeMillis() / 1000 + "");
			if ("1".equals(type)) {
				t = XmppClient.getInstance(mContext).sendMessage(userID, chat_con, xmp);
			} else if ("2".equals(type)) {
				xmp.setRichbody(FaceAction.GetImageStr(mContext, chat_con));
				t = XmppClient.getInstance(mContext).sendMessage(userID, "[图片]", xmp);// FaceAction.GetImageStr(chat_con)
			} else if ("3".equals(type)) {
				xmp.setDuartion(arg0[2] + "");
				//audioAction.getVoicePath();
				xmp.setRichbody(FaceAction.GetImageStr(mContext, chat_con));
				t = XmppClient.getInstance(mContext).sendMessage(userID, "[语音]", xmp);
			} else if ("4".equals(type)) {
				xmp.setRichbody(chat_con);
				t = XmppClient.getInstance(mContext).sendMessage(userID, "[视频]", xmp);
			} else if ("5".equals(type)) {
				xmp.setRichbody("");
				t = XmppClient.getInstance(mContext).sendMessage(userID, getPre("address"), xmp);
			}
			chat_con = "";
			return t;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result)
				makeText("转发失败");
			else {
				Toast.makeText(mContext, "转发成功", Toast.LENGTH_SHORT).show();
			}
			
		}
	}
	
	private void send(HashMap<String, String> map) {
		DBhelper helper = new DBhelper(this, IwoSQLiteHelper.MESSAGE_TAB);
		
		String table = "tab_" + getUid() + map.get("user_id");
		ArrayList<HashMap<String, String>> list2 = helper.select(table);
		if(list2 != null){
			for (int i = 0; i < list2.size(); i++) {
				if (!StringUtil.isEmpty(list2.get(i).get("time"))) {
					if (!StringUtil.isEmpty(list2.get(i).get("timestamp")))
						try {
							time_str = Long.valueOf(list2.get(i).get("timestamp"));
						} catch (Exception e) {
						}
					break;
				}
			}
		}
		
		HashMap<String, String> list = getTime(map);
		helper.insert(table, list);
		helper.close();
	}

	private HashMap<String, String> getTime(HashMap<String, String> data) {
		long t = 0;
		if (!StringUtil.isEmpty(data.get("timestamp")))
			try {
				t = Long.valueOf(data.get("timestamp"));
			} catch (Exception e) {
			}
		Logger.i(time_str + "==" + t + "==" + (-time_str + t));
		if ((-time_str + t) > 2 * 60) {
			time_str = t;
			data.put("time", "" + t);
		} else {
			data.put("time", "");
		}

		return data;
	}
}
