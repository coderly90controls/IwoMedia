package com.android.iwo.media.action;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.iwo.media.activity.FriendDetail;
import com.android.iwo.media.activity.TelAddActivity;
import com.android.iwo.media.chat.XmppClient;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.qun.activity.CreateQun;
import com.android.iwo.media.qun.activity.QunInfoActivity;
import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.view.IwoToast;
import com.android.iwo.media.view.SwipeListView;
import com.android.iwo.media.view.XListView.IXListViewListener;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.adapter.ViewPageAdapter;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.NetworkUtil;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 好友
 */
public class View1 extends ViewTab {

	private DBhelper per;
	private final int REQUEST_FRIENDDETAIL = 5191819;
	private SwipeListView list_view;
	private LinearLayout view_add;

	public View1(Context context) {
		per = new DBhelper(context, IwoSQLiteHelper.FRIEND_TAB);
		mContext = context;
		view = View.inflate(mContext, R.layout.view1_layout, null);
		mHead = View.inflate(mContext, R.layout.view1_list_head, null);
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		scale = mContext.getResources().getDisplayMetrics().density;
		init();
		new GetData().execute();
	}

	protected void setImageView(ImageView imageView, int resId) {
		Bitmap bitmap = BitmapUtil.decodeResource(mContext, resId);
		if (bitmap != null)
			BitmapUtil.setImageBitmap(imageView, bitmap);
	}

	CommonDialog commonDialog = null;

	@Override
	protected void init() {
		view_add = (LinearLayout) view.findViewById(R.id.view_add);
		view_add.addView(mHead);
		list_view = (SwipeListView) view.findViewById(R.id.list);

		list_view.setDividerHeight(0);
		list_view.setSelector(new ColorDrawable(0x00000000));
		list_view.setRightViewWidth((int) (74 * scale));
		// list_view.addHeaderView(mHead);
		mAdapter = new IwoAdapter((Activity) mContext, mData) {
			@Override
			public View getView(final int position, View v, ViewGroup parent) {
				if (v == null)
					v = View.inflate(mContext, R.layout.view1_list_item, null);
				final HashMap<String, String> map = mData.get(position);
				setItem(v, R.id.name, StringUtil.isEmpty(map.get("nick")) ? "" : map.get("nick"));

				ImageView img_2 = (ImageView) v.findViewById(R.id.img_2);
				img_2.setVisibility(View.GONE);
				if (!StringUtil.isEmpty(map.get("videoImg"))) {
					img_2.setVisibility(View.VISIBLE);
					LoadBitmap.getIntence().loadImage(map.get("videoImg"), img_2);
					// setImageView(v, R.id.img_2, map.get("videoImg"),
					// R.drawable.video_zhanwei);
				}

				LinearLayout del = (LinearLayout) v.findViewById(R.id.del);
				del.setVisibility(View.VISIBLE);
				del.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						OnClickListener clickListener_7 = new OnClickListener() {

							@Override
							public void onClick(View v) {
								int id = v.getId();
								if (id == R.id.determine) {
									new Delete().execute("" + map.get("name"));
									commonDialog.dismiss();
								} else if (id == R.id.cancel) {
									commonDialog.dismiss();
								} else {
								}
							}
						};
						int[] clikViews = new int[] { R.id.determine, R.id.cancel };
						commonDialog = new CommonDialog(mContext, "是否要删除该好友？", clickListener_7, R.layout.loading_dialog_text, clikViews, R.id.tipText_view);
						commonDialog.show();
					}
				});
				// Logger.i("好友头像：" + map.get("avatar"));
				LoadBitmap.getIntence().loadImage(map.get("avatar"), (ImageView) v.findViewById(R.id.img));
				// setImageView(v, R.id.img, map.get("avatar"),
				// R.drawable.ico_default);
				return v;
			}
		};

		list_view.setXListViewListener(new IXListViewListener() {

			public void onRefresh() {
				if (!isrefresh) {

					isrefresh = true;
					String name = PreferenceUtil.getString(mContext, "user_name", "");
					XmppClient.getInstance(mContext).updateFriend(name);
					XmppClient.getInstance(mContext).getQunMember(name);
				}
				if (!NetworkUtil.isWIFIConnected(mContext)) {
					list_view.stopRefresh();
				}
			}
		});
		list_view.setAdapter(mAdapter);
		list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(mContext, FriendDetail.class);
				intent.putExtra("name", mData.get(position - list_view.getHeaderViewsCount()).get("name").replace(Constants.CHAT_TYPE, ""));
				((Activity) mContext).startActivityForResult(intent, REQUEST_FRIENDDETAIL);
			}
		});
		setAddFriend();
	}

	public void refresh() {
		isrefresh = true;
		new GetData().execute();
	}

	protected void setAdImgSize(View item, int del, float size, int n) {
		int width = (dm.widthPixels - (int) (del * scale + 0.5f)) / n;
		LayoutParams params = item.getLayoutParams();
		if (params != null) {
			params.width = width;
			params.height = (int) (width * size);
		}
	}

	private void setAddFriend() {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.create_friend) {
					Intent intent = new Intent(mContext, TelAddActivity.class);
					((Activity) mContext).startActivityForResult(intent, 10002);
				} else if (v.getId() == R.id.create_qun) {
					Intent intent = new Intent(mContext, CreateQun.class);
					((Activity) mContext).startActivity(intent);
					// XmppClient.getInstance(mContext).getQunMember(PreferenceUtil.getString(mContext,
					// "user_name", ""));
				}
			}
		};
		mHead.findViewById(R.id.create_friend).setOnClickListener(listener);
		mHead.findViewById(R.id.create_qun).setOnClickListener(listener);
	}

	@Override
	protected ArrayList<HashMap<String, String>> doInBack(String... params) {
		return per.select("tab_" + PreferenceUtil.getString(mContext, "user_name", ""));
	}

	@Override
	protected void onPostExe(ArrayList<HashMap<String, String>> result) {
		Logger.e("好友列表" + result);
		mData.clear();
		if (result != null) {
			isrefresh = false;
			mData.addAll(result);
		}
		mAdapter.notifyDataSetChanged();
		// init();
		if (mData != null && mData.size() > 0) {
			// view_add.setVisibility(View.GONE);
			mHead.findViewById(R.id.create_friend).setVisibility(View.GONE);
			// mHead.findViewById(R.id.create_qun).setVisibility(View.VISIBLE);
			view_add.setVisibility(View.GONE);
		} else {
			mHead.findViewById(R.id.create_friend).setVisibility(View.VISIBLE);
			view_add.setVisibility(View.VISIBLE);
		}
		// setGroupList();
		list_view.stopRefresh();
		isrefresh = false;
	}

	private void setGroupList() {
		DBhelper group = new DBhelper(mContext, IwoSQLiteHelper.GROUP);
		String group_table = "group_" + PreferenceUtil.getString(mContext, "user_name", "");
		final ArrayList<HashMap<String, String>> data = group.select(group_table);

		if (data == null) {
			mHead.findViewById(R.id.create_qun).setVisibility(View.VISIBLE);
			mHead.findViewById(R.id.qun).setVisibility(View.GONE);
			return;
		} else {
			mHead.findViewById(R.id.create_qun).setVisibility(View.GONE);
			mHead.findViewById(R.id.qun).setVisibility(View.VISIBLE);
		}
		view_add.setVisibility(View.VISIBLE);
		ViewPager pager = (ViewPager) mHead.findViewById(R.id.qun);
		LinearLayout point = (LinearLayout) mHead.findViewById(R.id.qun_point);
		point.removeAllViews();
		point.removeAllViews();
		ArrayList<View> views = new ArrayList<View>();
		final ImageView indicator_iv[] = new ImageView[data.size()];
		for (int i = 0; i < data.size(); i++) {
			View view = View.inflate(mContext, R.layout.list_item_qun, null);
			setItem(view, R.id.name, data.get(i).get("name"));
			setItem(view, R.id.type, data.get(i).get("category"));
			setItem(view, R.id.address, data.get(i).get("area") + data.get(i).get("city"));
			LoadBitmap.getIntence().loadImage(data.get(i).get("avatar"), (ImageView) view.findViewById(R.id.img));
			views.add(view);

			final int id = i;
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, QunInfoActivity.class);
					intent.putExtra("id", data.get(id).get("id"));
					((Activity) mContext).startActivityForResult(intent, 10010);
				}
			});

			if (data.size() != 1) {
				indicator_iv[i] = new ImageView(mContext);
				indicator_iv[i].setBackgroundColor(0xff9d9d9d);
				indicator_iv[i].setScaleType(ImageView.ScaleType.CENTER_CROP);

				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (8 * scale), (int) (2 * scale));
				lp.setMargins(0, 0, (int) (8 * scale), 0);
				indicator_iv[i].setLayoutParams(lp);
				point.addView(indicator_iv[i]);
			}
		}
		ViewPageAdapter adapter = new ViewPageAdapter(views);
		pager.setAdapter(adapter);
		if (data.size() == 1)
			return;
		if (indicator_iv.length > 0)
			indicator_iv[0].setBackgroundColor(0xff57a7fc);
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int arg0) {
				for (int i = 0; i < indicator_iv.length; i++) {
					indicator_iv[i].setBackgroundColor(0xff9d9d9d);
				}
				indicator_iv[arg0].setBackgroundColor(0xff57a7fc);
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	private void setItem(View view, int id, String value) {
		TextView textView = (TextView) view.findViewById(id);
		if (!StringUtil.isEmpty(value))
			textView.setText(value);
		else {
			textView.setText("");
		}
	}

	/**
	 * 删除好友
	 */
	private class Delete extends AsyncTask<String, Void, Boolean> {
		String name = "";

		protected Boolean doInBackground(String... params) {
			name = params[0];
			return XmppClient.getInstance(mContext).removeUser(params[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				per.delete("tab_" + PreferenceUtil.getString(mContext, "user_name", ""), "name", name);
				DBhelper per = new DBhelper(mContext, IwoSQLiteHelper.MESSAGE_TAB);
				per.delete("tab_msg" + PreferenceUtil.getString(mContext, "user_name", ""), "user_id", name);

				per.delete("tab_" + getUid() + name);// 删除消息记录
				per.close();

				DBhelper invite_from = new DBhelper(mContext, IwoSQLiteHelper.INVITE_FROM);
				invite_from.delete("invite_from", "tel", name);
				invite_from.close();
				mContext.sendBroadcast(new Intent("com.android.broadcast.receiver.media.refresh.CHAT_REFRESH_SHARE"));
				IwoToast.makeText(mContext, "删除成功").show();
				list_view.hiddenRight();
			} else {
				IwoToast.makeText(mContext, "删除失败").show();
			}
		}
	}
}
