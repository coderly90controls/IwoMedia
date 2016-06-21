package com.android.iwo.media.action;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.iwo.media.activity.AddFriResActivtity;
import com.android.iwo.media.activity.ChatActivity;

import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.view.IwoDialog;
import com.android.iwo.media.view.XListView;
import com.android.iwo.media.view.XListView.IXListViewListener;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.DateUtil;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 消息
 */
public class View2 extends ViewTab {

	private DBhelper db;
	public boolean run = true;

	public View2(Context context) {
		mContext = context;
		view = View.inflate(mContext, R.layout.view2_layout, null);
		init();
	}

	@Override
	protected void init() {
		db = new DBhelper(mContext, IwoSQLiteHelper.MESSAGE_TAB);
		listView = (XListView) view.findViewById(R.id.list);
		listView.setCacheColorHint(0);
		listView.setSelector(new ColorDrawable(0x00000000));
		listView.setDividerHeight(0);
		mAdapter = new IwoAdapter((Activity) mContext, mData) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null)
					v = mInflater.inflate(R.layout.view2_list_item, parent, false);
				final HashMap<String, String> map = mData.get(position);
				setItem(v, R.id.name, map.get("nick_name"));
				setItem(v, R.id.time_tex, map.get("timestamp"));
				setItem(v, R.id.hous, map.get("time"));
				if (!StringUtil.isEmpty(map.get("num"))) {
					v.findViewById(R.id.num).setVisibility(View.VISIBLE);
					setItem(v, R.id.num, map.get("num"));
				} else
					v.findViewById(R.id.num).setVisibility(View.GONE);
				setItem(v, R.id.des, map.get("msg_tex"));

				ImageView head = (ImageView) v.findViewById(R.id.head);
				if (StringUtil.isEmpty(map.get("head_img"))) {
					head.setImageResource(R.drawable.ico_default);
				} else {
					LoadBitmap.getIntence().loadImage(map.get("head_img"), head);
				}

				// setImageView(v, R.id.head, map.get("head_img"),
				// R.drawable.ico_default);
				LinearLayout time_layout = (LinearLayout) v.findViewById(R.id.time);
				if (StringUtil.isEmpty(map.get("timestamp"))) {
					time_layout.setVisibility(View.GONE);
				} else {
					time_layout.setVisibility(View.VISIBLE);
				}
				TextView ok = (TextView) v.findViewById(R.id.ok);
				ok.setVisibility(View.GONE);
				if ("-1".equals(map.get("type"))) {// 好友申请
					ok.setVisibility(View.VISIBLE);
					ok.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							new Thread(new Runnable() {
								public void run() {
									Logger.i("同意好友申请");
									ChatUtils.addFriend(mContext, map.get("user_name"), map.get("head_img"), map.get("nick_name"));
								}
							}).start();
						}
					});
				} else if ("2".equals(map.get("type"))) {
					setItem(v, R.id.des, "[图片]");
				} else if ("3".equals(map.get("type"))) {
					setItem(v, R.id.des, "[语音]");
				} else if ("4".equals(map.get("type"))) {
					setItem(v, R.id.des, "[视频]");
				} else if ("5".equals(map.get("type"))) {
					setItem(v, R.id.des, "[位置]");
				}
				return v;
			}
		};
		listView.setAdapter(mAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				HashMap<String, String> map = mData.get(arg2 - listView.getHeaderViewsCount());
				Logger.i("map = " + map);
				Intent intent = null;
				if ("-1".equals(map.get("type"))) {
					intent = new Intent(mContext, AddFriResActivtity.class);
					intent.putExtra("user_id", map.get("user_name"));
					mContext.startActivity(intent);
				} else {
					intent = new Intent(mContext, ChatActivity.class);
					intent.putExtra("name", map.get("nick_name"));
					intent.putExtra("userID", map.get("user_id"));
					intent.putExtra("head_img", map.get("head_img"));
					Logger.i("user_id" + map.get("user_name"));
					((Activity) mContext).startActivityForResult(intent, 10010);
				}
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final HashMap<String, String> map = mData.get(position - listView.getHeaderViewsCount());
				IwoDialog dialog = new IwoDialog(mContext);
				dialog.setText(map.get("nick_name"), "删除该聊天");
				dialog.setOnClickListener(new IwoDialog.OnClickListener() {
					public void click(Dialog dialog) {
						db.delete("tab_msg" + PreferenceUtil.getString(mContext, "user_name", ""), "user_id", map.get("user_id"));
						db.delete("tab_" + getUid() + map.get("user_id"));
						refresh();
						dialog.dismiss();
					}
				});
				dialog.show();
				return true;
			}
		});
		listView.setXListViewListener(new IXListViewListener() {
			public void onRefresh() {
				if (!isrefresh) {
					isrefresh = true;
					new GetData().execute();
				}
			}
		});
		new GetData().execute();
	}

	@Override
	protected ArrayList<HashMap<String, String>> doInBack(String... params) {
		return db.select("tab_msg" + PreferenceUtil.getString(mContext, "user_name", ""), "timestamp", true);
	}

	@Override
	protected void onPostExe(ArrayList<HashMap<String, String>> result) {
		listView.stopRefresh();
		Logger.i("消息数据：" + result);
		mData.clear();
		if (result != null) {
			setData(result);
		} else {
			if (mMsg != null) {
				mMsg.upData(false);
			}
		}
		if (isrefresh) {
			listView.stopRefresh();
			isrefresh = false;
		}
		mAdapter.notifyDataSetChanged();
	}

	private void setData(ArrayList<HashMap<String, String>> result) {
		if (result == null) {
			if (mMsg != null) {
				mMsg.upData(false);
				return;
			}
		}

		String times = "";
		String today = DateUtil.format("yyyy-MM-dd", System.currentTimeMillis());
		boolean msg = false;
		for (int i = 0; i < result.size(); i++) {
			HashMap<String, String> map = result.get(i);
			String time = DateUtil.format("yyyy-MM-dd", map.get("timestamp") + "000");
			map.put("time", DateUtil.format("HH:mm", map.get("timestamp") + "000"));
			if (times.equals(time)) {
				map.put("timestamp", "");
			} else {
				times = time;
				if (time.equals(today)) {
					map.put("timestamp", "今天");
				} else
					map.put("timestamp", time);
			}
			if (!StringUtil.isEmpty(map.get("num")) || "-1".equals(map.get("type"))) {
				msg = true;
			}
			mData.add(map);
		}
		if (result == null || result.size() == 0)
			msg = false;
		if (mMsg != null) {
			mMsg.upData(msg);
		}
		Logger.i("消息列表数据" + mData);
	}

	public void refresh() {
		isrefresh = true;
		new GetData().execute();
	}

	public UpDateMsg mMsg;

	public void setUpDateMsg(UpDateMsg m) {
		if (m != null)
			mMsg = m;
	}

	public interface UpDateMsg {
		public void upData(boolean up);
	}
}
