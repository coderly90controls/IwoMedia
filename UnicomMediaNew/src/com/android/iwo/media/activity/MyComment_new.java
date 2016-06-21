package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.R;
import com.android.iwo.media.view.XListView;
import com.android.iwo.media.view.XListView.IXListViewListener;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.DateUtil;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.NetworkUtil;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class MyComment_new extends BaseActivity {
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private XListView listview;
	private IwoAdapter adapter;
	private boolean isOne = true;// 第一次加载
	private boolean isData = false;
	private boolean isToast = true;
	private LinearLayout bootom;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycomment_new);
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		if (NetworkUtil.isWIFIConnected(mContext)) {
			new GetData().execute();
		} else {
			mLoadBar.dismiss();
			makeText("请检查网络");
		}
		init();
	}

	@SuppressLint("ResourceAsColor")
	private void init() {
		bootom = (LinearLayout) findViewById(R.id.bootom);
		setBack_text(null);
		setTitle("我的评论");
		listview = (XListView) findViewById(R.id.view_my_comment_layout_list);
		adapter = new IwoAdapter(this, mData) {
			private viewHolder holder;

			@Override
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null) {
					holder = new viewHolder();
					LayoutInflater inflater = LayoutInflater.from(mContext);
					v = inflater.inflate(R.layout.my_comment_item, parent,
							false);
					holder.user_name = (TextView) v
							.findViewById(R.id.comm_user_name);
					holder.create_time = (TextView) v
							.findViewById(R.id.comment_time);
					holder.zan_count = (TextView) v
							.findViewById(R.id.count_zan);
					holder.content = (TextView) v
							.findViewById(R.id.user_comment);
					holder.comment_about = (TextView) v
							.findViewById(R.id.com_about);
					holder.user_img = (ImageView) v.findViewById(R.id.user_img);
					v.setTag(holder);
				} else {
					holder = (viewHolder) v.getTag();
				}
				if (mData != null && mData.size() > 0) {
					HashMap<String, String> map = mData.get(position);
					Logger.i("map===>" + map);
					if (!StringUtil.isEmpty(getPre("Umeng"))) {
						String user_nick = map.get("user_nick");
						if (user_nick.length() > 6) {
							holder.user_name.setText(user_nick.substring(0, 5));
						} else {
							holder.user_name.setText(user_nick);
						}
					}else {
						if (StringUtil.isEmpty(getPre("nick_name"))) {
							holder.user_name.setText(getPre("user_name"));
						}else {
							if (getPre("nick_name").length()>6) {
								holder.user_name.setText(getPre("nick_name").substring(0, 5));
							}else {
								holder.user_name.setText(getPre("nick_name"));
							}
							
						}
					}

					holder.create_time.setText(DateUtil.getTime(map
							.get("create_time")));
					if (!StringUtil.isEmpty(map.get("praise_count"))) {
						holder.zan_count.setText(map.get("praise_count"));// 获取赞数
					} else {
						holder.zan_count.setText("0");// 获取赞数
					}
					holder.content.setText(map.get("comm_content"));
					holder.comment_about.setText(map.get("video_name"));
					LoadBitmap.getIntence().loadImage(map.get("head_img"),
							holder.user_img);
				}

				return v;
			}

		};
		listview.setAdapter(adapter);
		listview.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				isOne = true;
				new GetData().execute();
				Logger.i("下拉涮新完成");
			}
		});
		listview.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if (mData.size() > 0) {
							if (mData.size() % 10 != 0) {
								makeText("没有更多评论");
								return;
							}
							if (!isData) {
								makeText("没有更多评论");
								return;
							}
							bootom.setVisibility(View.VISIBLE);
							isOne = false;
							new GetData().execute();
						}
					}
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				String id = null;
				id = mData.get(pos - listview.getHeaderViewsCount()).get(
						"entity_id");
				Intent intent = new Intent(mContext,
						VideoDetailActivity_new.class);
				intent.putExtra("video_id", id);
				startActivity(intent);

			}
		});
	}

	private class GetData extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		private int size_ll;

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			if (isOne) {

				return DataRequest.getHashMapFromUrl_Base64(
						getUrl(AppConfig.REQUEST_MY_COMMENT), "1", "10");
			}
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.REQUEST_MY_COMMENT),
					getStart(mData.size()), "10");

		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			mLoadBar.dismiss();
			listview.stopRefresh();
			if (bootom.getVisibility() == View.VISIBLE) {
				bootom.setVisibility(View.GONE);
			}
			if (result != null && "1".equals(result.get("code"))) {
				ArrayList<HashMap<String, String>> list = DataRequest
						.getArrayListFromJSONArrayString(result.get("data"));
				if (!StringUtil.isEmpty(list) && list.size() > 0) {
					Logger.i("list评论：=====>" + list);
					isData = true;
					findViewById(R.id.moren_mycomment_default).setVisibility(
							View.GONE);
					findViewById(R.id.view_my_chase_xian).setVisibility(
							View.GONE);
					listview.setVisibility(View.VISIBLE);
					if (isOne) {
						mData.clear();
					}
					mData.addAll(list);
					size_ll = mData.size();
					Logger.i("共几个评论" + size_ll);
					adapter.notifyDataSetChanged();
				} else {
					isData = false;
					if (isOne) {
						ImageView moren_mycomment = (ImageView) findViewById(R.id.moren_mycomment_default);
						moren_mycomment.setVisibility(View.VISIBLE);
						Bitmap bitmap = android.graphics.BitmapFactory
								.decodeResource(getResources(),
										R.drawable.moren_mychase);
						int screenWidth = PreferenceUtil.getInt(
								MyComment_new.this, "screenWidth", 720);
						if (bitmap != null) {
							Bitmap bitmap2 = BitmapUtil.getBitmap(bitmap,
									screenWidth - 36,
									(screenWidth - 36) * 190 / 318);
							if (bitmap2 != null) {
								BitmapUtil.setImageBitmap(moren_mycomment,
										bitmap2);
							}
						}
						listview.setVisibility(View.GONE);
						findViewById(R.id.view_my_chase_xian).setVisibility(
								View.VISIBLE);
						if (isToast) {
							makeText("没有评论");
						}
					}
				}
			}

		}
	}

	public class viewHolder {
		TextView user_name, create_time, zan_count, content, comment_about;
		ImageView user_img;
	}

}
