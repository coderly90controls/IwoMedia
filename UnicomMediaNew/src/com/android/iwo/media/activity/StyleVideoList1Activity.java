package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.Constants;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.ACache;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.FileCache;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class StyleVideoList1Activity extends BaseActivity {

	protected ListView listView;
	private String video_id;
	// 用来装全部视频的集合
	private ArrayList<ArrayList<HashMap<String, String>>> mAdData = new ArrayList<ArrayList<HashMap<String, String>>>();
	// 用来装频道 3天
	ArrayList<HashMap<String, String>> mMap = new ArrayList<HashMap<String, String>>();

	// 用来装是配的数据
	ArrayList<HashMap<String, String>> adapter_Map = new ArrayList<HashMap<String, String>>();
	// 记录当前的频道个数
	private int size;
	private IwoAdapter adapter;
	// 从第一个加载
	private int size_jia = 0;
	private LinearLayout more;
	private String zt_order;
	private String active_name;
	private boolean isSyte;// 区分2中模式 ture 是有加载更多的 false 没加载更多
	private int screenWidth;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singleton_listview);
		listView = (ListView) findViewById(R.id.singleton_list);
		listView.setDivider(null);// 将ListView分线去掉
		mCache = ACache.get(mContext);
		video_id = getIntent().getStringExtra("video_id");
		// setTitle(getIntent().getStringExtra("video_name"));
		setTitle("专题");
		setBack_text(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		Logger.i("视频的" + video_id);
		zt_order = getIntent().getStringExtra("zt_order");
		active_name = getIntent().getStringExtra("active_name");
		if (!StringUtil.isEmpty(zt_order)) {
			Logger.i("11111111");
			isSyte = false;
			new mDataVi_new().execute(video_id);
		} else {
			isSyte = true;
			new GetData().execute(video_id);
		}

		init();
	}

	private void init() {
		findViewById(R.id.syte_jiazai_xian).setVisibility(View.VISIBLE);
		screenWidth = PreferenceUtil.getInt(mContext, "screenWidth", 100) - 18 - 18;
		more = (LinearLayout) findViewById(R.id.syte_jiazai_gengduo);
		if (!isSyte) {
			more.setVisibility(View.GONE);// 不需要加载更多了
		}
		more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (size_jia < size) {
					size_jia = size_jia + 1;
					Logger.i("请求的：000" + size_jia);
					new mDataVi().execute(mMap.get(size_jia).get("id"));

				} else if (size_jia == size) {
					makeText("没有更多频道");
					more.setVisibility(View.GONE);
				}

			}
		});
	}

	// （比如在电影里面 ）获得多少个频道
	private class GetData extends
			AsyncTask<String, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			HashMap<String, String> data = null;
			data = DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_V_GET_VIDEO_CHANNEL_SYTE), params[0]);

			// data =
			// DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_GET_VIDEO_CHANNEL_SYTE_NEW),
			// params[0]);

			if (data != null && "1".equals(data.get("code"))) {
				mCache.put(Constants.VIDEO_CHANNEL_SYTE, data.get("data"));
				Logger.i("缓存数据....." + data.get("data"));
				/*
				 * if (data != null) { ArrayList<HashMap<String, String>> list =
				 * DataRequest
				 * .getArrayListFromJSONArrayString(data.get("data")); if
				 * (list.size() > 1) { // 后面添加 主要是服务器没数据 int[] strs = new
				 * int[list.size()]; //int[] strs = new int[10];
				 * Logger.i("频道列表：" + list.size()); try { for (int i = 0; i <
				 * list.size(); i++) { HashMap<String, String> map1 =
				 * list.get(i); String channelItem =
				 * DataRequest.getStringFromURL_Base64
				 * (getUrl(AppConfig.NEW_V_GET_VIDEO_LIST), map1.get("id"), "1",
				 * "10"); mCache.put(Constants.VIDEO_CHANNEL + i, channelItem);
				 * ArrayList<HashMap<String, String>> listItem =
				 * DataRequest.getArrayListFromJSONArrayString(channelItem); if
				 * (listItem != null) { Logger.i("频道列biao222");
				 * mAdData.add(listItem); // initView(listItem);
				 * 
				 * strs[i] = -1; // Logger.i("要创建的数组：" + strs[i]); } else {
				 * strs[i] = i; // Logger.i("要创建的数组：" + strs[i]); } } for (int i
				 * = strs.length - 1; i >= 0; i--) { Logger.i("要删除的数组：" +
				 * strs[i]); if (strs[i] == i) { list.remove(i); } } } catch
				 * (Exception e) {
				 * 
				 * } } }
				 */

			}
			return data;
		}

		protected void onPostExecute(HashMap<String, String> result) {
			super.onPostExecute(result);
			if (result != null && "1".equals(result.get("code"))) { // 先拿第一个数据
				String string = result.get("data");

				Logger.i("拿到数据是什么" + string);
				if (!StringUtil.isEmpty(string)) {
					// 主要是后台没添加数据进来
					ArrayList<HashMap<String, String>> list_str = DataRequest
							.getArrayListFromJSONArrayString(result.get("data"));
					Logger.i("频道信息11" + list_str.toString());
					Logger.i("频道信息个数" + list_str.size());
					if (list_str != null) {
						size = list_str.size();
						if (size == 1) {
							more.setVisibility(View.GONE);
						}
						size = size - 1;
						mMap.addAll(list_str);
						Logger.i("频道个数" + mMap.size());
						Logger.i("获取视频的ID号" + mMap.get(0).get("id"));
						// String idString = mMap.get(0).get("id");
						new mDataVi().execute(mMap.get(size_jia).get("id"));

						/*
						 * ArrayList<HashMap<String, String>> ss =
						 * mAdData.get(0); if(ss!=null){ initView(ss); }
						 */

					} else {
						mLoadBar.dismiss();
						more.setVisibility(View.GONE);
						makeText("暂无数据");

					}

				} else {
					mLoadBar.dismiss();
					more.setVisibility(View.GONE);
					makeText("暂无数据");
				}

			}
		}
	}

	// 获取视频信息
	private class mDataVi extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected void onPreExecute() {
			mLoadBar.setMessage("数据加载中");
			mLoadBar.show();
			super.onPreExecute();
		}

		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			String string = DataRequest.getStringFromURL_Base64(
					getUrl(AppConfig.NEW_V_GET_VIDEO_LIST), params[0], "1",
					"10");
			if (!StringUtil.isEmpty(string)) {
				ArrayList<HashMap<String, String>> list = DataRequest
						.getArrayListFromJSONArrayString(string);
				if (list != null) {
					// 自定义Map 集合
					return getMap(mMap, string, size_jia);
				}

			}
			return null;
		}

		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			mLoadBar.dismiss();
			super.onPostExecute(result);
			if (result != null) {
				Logger.i("显示界面");
				initView(result);
				adapter.notifyDataSetChanged();
			} else {
				makeText("暂无数据");
				more.setVisibility(View.GONE);
			}
		}

	}

	public ArrayList<HashMap<String, String>> getMap(
			ArrayList<HashMap<String, String>> maps, String str, int n) {
		HashMap<String, String> m = new HashMap<String, String>();
		String id = maps.get(n).get("id");
		String ch_name = maps.get(n).get("ch_name");
		String style_ico = maps.get(n).get("style_ico");
		String create_time = maps.get(n).get("create_time");
		String ch_date = maps.get(n).get("ch_date");
		m.put("data", str);
		m.put("id", id);
		m.put("ch_name", ch_name);
		m.put("style_ico", style_ico);
		m.put("create_time", create_time);
		m.put("ch_date", ch_date);
		adapter_Map.add(m);

		return adapter_Map;
	}

	// 界面显示
	public void initView(final ArrayList<HashMap<String, String>> arrayList_shi) {
		Logger.i("样式正在显示界面" + arrayList_shi);
		adapter = new IwoAdapter(StyleVideoList1Activity.this, arrayList_shi) {

			private viewHolder holder;

			@Override
			public int getCount() {
				if (!isSyte) {
					return 1;
				}
				return arrayList_shi.size();
			}

			@SuppressLint("NewApi")
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null) {
					holder = new viewHolder();
					v = View.inflate(StyleVideoList1Activity.this,
							R.layout.activity_si_si_sy_ss, null);
					holder.syte1 = (LinearLayout) v
							.findViewById(R.id.dis_item_syte1);
					holder.syte2 = (LinearLayout) v
							.findViewById(R.id.dis_item_syte2);
					holder.syte3 = (LinearLayout) v
							.findViewById(R.id.dis_item_syte3);
					holder.syte4 = (LinearLayout) v
							.findViewById(R.id.syte_moren);
					holder.date1 = (TextView) v
							.findViewById(R.id.dis_item_date_jintian_1);
					holder.date2 = (TextView) v
							.findViewById(R.id.dis_item_date_jintian_2);
					holder.date3 = (TextView) v
							.findViewById(R.id.dis_item_date_jintian_3);
					holder.date4 = (TextView) v
							.findViewById(R.id.dis_item_date_moren);
					holder.time_1 = (TextView) v
							.findViewById(R.id.sy_textring_jin);
					holder.time_2 = (TextView) v
							.findViewById(R.id.sy_textring_zuo);
					holder.time_3 = (TextView) v
							.findViewById(R.id.sy_textring_qian);
					holder.time_moren = (TextView) v
							.findViewById(R.id.sy_textring_moren_date);

					// 样式1的图片
					holder.j_1 = (ImageView) v.findViewById(R.id.sy_jintian_1);
					holder.j_2 = (ImageView) v.findViewById(R.id.sy_jintian_2);
					holder.j_3 = (ImageView) v.findViewById(R.id.sy_jintian_3);
					holder.j_4 = (ImageView) v.findViewById(R.id.sy_jintian_4);

					// 样式2的图片
					holder.z_1 = (ImageView) v.findViewById(R.id.sy_zuotian_1);
					holder.z_2 = (ImageView) v.findViewById(R.id.sy_zuotian_2);
					holder.z_3 = (ImageView) v.findViewById(R.id.sy_zuotian_3);
					holder.z_4 = (ImageView) v.findViewById(R.id.sy_zuotian_4);
					holder.z_5 = (ImageView) v.findViewById(R.id.sy_zuotian_5);

					// 样式3的图片
					holder.q_1 = (ImageView) v.findViewById(R.id.sy_qiantian_1);
					holder.q_2 = (ImageView) v.findViewById(R.id.sy_qiantian_2);
					holder.q_3 = (ImageView) v.findViewById(R.id.sy_qiantian_3);
					holder.q_4 = (ImageView) v.findViewById(R.id.sy_qiantian_4);
					holder.q_5 = (ImageView) v.findViewById(R.id.sy_qiantian_5);
					holder.q_6 = (ImageView) v.findViewById(R.id.sy_qiantian_6);
					holder.q_7 = (ImageView) v.findViewById(R.id.sy_qiantian_7);
					holder.q_8 = (ImageView) v.findViewById(R.id.sy_qiantian_8);
					holder.q_9 = (ImageView) v.findViewById(R.id.sy_qiantian_9);
					// 默认的图片
					holder.moren = (ImageView) v
							.findViewById(R.id.syte_moren_ima);
					v.setTag(holder);
				} else {
					holder = (viewHolder) v.getTag();
				}
				if (arrayList_shi != null) {
					if (isSyte) {
						HashMap<String, String> map = arrayList_shi
								.get(position);
						final ArrayList<HashMap<String, String>> arrayList = DataRequest
								.getArrayListFromJSONArrayString(map
										.get("data"));
						String date_name = map.get("ch_name");// 频道名
						String timeStr = map.get("ch_date");// 时间
						Logger.i(timeStr + "时间");

						Logger.i("样式占满屏幕大小" + screenWidth);
						if ("1".equals(map.get("style_ico"))) {
							Logger.i("样式11");
							setMoe(holder.syte1, true);
							setMoe(holder.syte2, false);
							setMoe(holder.syte3, false);
							setMoe(holder.syte4, false);
							holder.date1.setText(date_name);
							if (!StringUtil.isEmpty(timeStr)) {
								holder.time_1.setText(timeStr);
							}
							// 210,141 284，360,
							setImager(holder.j_1, arrayList, 0,
									R.drawable.b360_284, screenWidth, 570, 360,
									284, 360, 0, 0);
							setImager(holder.j_2, arrayList, 2,
									R.drawable.b211_143, screenWidth, 570,
									210 - 2, 141, 210 - 2, 0, 0);
							setImager(holder.j_3, arrayList, 3,
									R.drawable.b211_143, screenWidth, 570,
									210 - 2, 141, 210 - 2, 0, 0);
							setImager(holder.j_4, arrayList, 1,
									R.drawable.b570_424, screenWidth, 1, 1,
									424, 570, 0, 0);
							OnClick(arrayList, holder.j_1, 0);
							OnClick(arrayList, holder.j_2, 2);
							OnClick(arrayList, holder.j_3, 3);
							OnClick(arrayList, holder.j_4, 1);

						}
						if ("2".equals(map.get("style_ico"))) {
							Logger.i("样式22");
							holder.date2.setText(date_name);
							if (!StringUtil.isEmpty(timeStr)) {
								holder.time_2.setText(timeStr);
							}
							setMoe(holder.syte1, false);
							setMoe(holder.syte2, true);
							setMoe(holder.syte3, false);
							setMoe(holder.syte4, false);

							// 2 .3 号图片调换 420,278 285(宽),348(高) 高是分子
							setImager(holder.z_1, arrayList, 3,
									R.drawable.c150_139, screenWidth - 2, 570,
									150, 139, 150, 0, 0);
							setImager(holder.z_2, arrayList, 4,
									R.drawable.c150_139, screenWidth - 2, 570,
									150, 139, 150, 0, 0);
							setImager(holder.z_3, arrayList, 0,
									R.drawable.c420_278, screenWidth - 2, 570,
									420, 278, 420, 0, 0);
							setImager(holder.z_4, arrayList, 1,
									R.drawable.c285_348, screenWidth - 2, 2, 1,
									348, 285, 15, 0);
							setImager(holder.z_5, arrayList, 2,
									R.drawable.c285_348, screenWidth - 2, 2, 1,
									348, 285, 15, 0);
							OnClick(arrayList, holder.z_1, 3);
							OnClick(arrayList, holder.z_2, 4);
							OnClick(arrayList, holder.z_3, 0);
							OnClick(arrayList, holder.z_4, 1);
							OnClick(arrayList, holder.z_5, 2);

						}
						if ("3".equals(map.get("style_ico"))) {
							holder.date3.setText(date_name);
							if (!StringUtil.isEmpty(timeStr)) {
								holder.time_3.setText(timeStr);
							}
							Logger.i("样式33");
							setMoe(holder.syte1, false);
							setMoe(holder.syte2, false);
							setMoe(holder.syte3, true);
							setMoe(holder.syte4, false);

							setImager(holder.q_1, arrayList, 1,
									R.drawable.a190_190, screenWidth - 4, 3, 1,
									1, 1, 6, 0);
							setImager(holder.q_2, arrayList, 2,
									R.drawable.a190_190, screenWidth - 4, 3, 1,
									1, 1, 6, 0);
							setImager(holder.q_3, arrayList, 3,
									R.drawable.a190_190, screenWidth - 4, 3, 1,
									1, 1, 6, 0);
							setImager(holder.q_4, arrayList, 4,
									R.drawable.a190_190, screenWidth - 2, 3, 1,
									1, 1, 6, 0);
							setImager(holder.q_5, arrayList, 0,
									R.drawable.d380_380, screenWidth - 2, 3, 2,
									1, 1, 0, 10);
							setImager(holder.q_6, arrayList, 5,
									R.drawable.a190_190, screenWidth - 2, 3, 1,
									1, 1, 6, 0);
							setImager(holder.q_7, arrayList, 6,
									R.drawable.a190_190, screenWidth - 4, 3, 1,
									1, 1, 6, 0);
							setImager(holder.q_8, arrayList, 7,
									R.drawable.a190_190, screenWidth - 4, 3, 1,
									1, 1, 6, 0);
							setImager(holder.q_9, arrayList, 8,
									R.drawable.a190_190, screenWidth - 4, 3, 1,
									1, 1, 6, 0);
							// 点击方法
							OnClick(arrayList, holder.q_1, 1);
							OnClick(arrayList, holder.q_2, 2);
							OnClick(arrayList, holder.q_3, 3);
							OnClick(arrayList, holder.q_4, 4);
							OnClick(arrayList, holder.q_5, 0);
							OnClick(arrayList, holder.q_6, 5);
							OnClick(arrayList, holder.q_7, 6);
							OnClick(arrayList, holder.q_8, 7);
							OnClick(arrayList, holder.q_9, 8);

						}
						if ("0".equals(map.get("style_ico"))) {
							holder.date4.setText(timeStr);
							if (!StringUtil.isEmpty(timeStr)) {
								holder.time_moren.setText(timeStr);
							}
							setMoe(holder.syte1, false);
							setMoe(holder.syte2, false);
							setMoe(holder.syte3, false);
							setMoe(holder.syte4, true);
							if (arrayList.size() >= 1) {

								String url = arrayList.get(0).get("img_url_2");
								if (!StringUtil.isEmpty(url)) {
									LoadBitmap.getIntence().loadImage(url,
											holder.moren);
								} else {
									holder.moren.setBackground(getResources()
											.getDrawable(R.drawable.f570_352));
								}
								OnClick(arrayList, holder.moren, 0);
							}
						}
					} else {
						if ("1".equals(zt_order)) {
							Logger.i("样式11");
							setMoe(holder.syte1, true);
							setMoe(holder.syte2, false);
							setMoe(holder.syte3, false);
							setMoe(holder.syte4, false);
							holder.date1.setText(getIntent().getStringExtra(
									"video_name"));
							holder.time_1.setText(active_name);
							// 210,141 284，360,
							setImager(holder.j_1, arrayList_shi, 0,
									R.drawable.b360_284, screenWidth, 570, 360,
									284, 360, 0, 0);
							setImager(holder.j_2, arrayList_shi, 2,
									R.drawable.b211_143, screenWidth, 570,
									210 - 2, 141, 210 - 2, 0, 0);
							setImager(holder.j_3, arrayList_shi, 3,
									R.drawable.b211_143, screenWidth, 570,
									210 - 2, 141, 210 - 2, 0, 0);
							setImager(holder.j_4, arrayList_shi, 1,
									R.drawable.b570_424, screenWidth, 1, 1,
									424, 570, 0, 0);
							OnClick(arrayList_shi, holder.j_1, 0);
							OnClick(arrayList_shi, holder.j_2, 2);
							OnClick(arrayList_shi, holder.j_3, 3);
							OnClick(arrayList_shi, holder.j_4, 1);

						}
						if ("2".equals(zt_order)) {
							Logger.i("样式22");
							holder.date2.setText(getIntent().getStringExtra(
									"video_name"));
							holder.time_2.setText(active_name);
							setMoe(holder.syte1, false);
							setMoe(holder.syte2, true);
							setMoe(holder.syte3, false);
							setMoe(holder.syte4, false);

							// 2 .3 号图片调换 420,278 285(宽),348(高) 高是分子
							setImager(holder.z_1, arrayList_shi, 3,
									R.drawable.c150_139, screenWidth - 2, 570,
									150, 139, 150, 0, 0);
							setImager(holder.z_2, arrayList_shi, 4,
									R.drawable.c150_139, screenWidth - 2, 570,
									150, 139, 150, 0, 0);
							setImager(holder.z_3, arrayList_shi, 0,
									R.drawable.c420_278, screenWidth - 2, 570,
									420, 278, 420, 0, 0);
							setImager(holder.z_4, arrayList_shi, 1,
									R.drawable.c285_348, screenWidth - 2, 2, 1,
									348, 285, 15, 0);
							setImager(holder.z_5, arrayList_shi, 2,
									R.drawable.c285_348, screenWidth - 2, 2, 1,
									348, 285, 15, 0);
							OnClick(arrayList_shi, holder.z_1, 3);
							OnClick(arrayList_shi, holder.z_2, 4);
							OnClick(arrayList_shi, holder.z_3, 0);
							OnClick(arrayList_shi, holder.z_4, 1);
							OnClick(arrayList_shi, holder.z_5, 2);

						}
						if ("3".equals(zt_order)) {
							holder.date3.setText(getIntent().getStringExtra(
									"video_name"));
							holder.time_3.setText(active_name);

							Logger.i("样式33");
							setMoe(holder.syte1, false);
							setMoe(holder.syte2, false);
							setMoe(holder.syte3, true);
							setMoe(holder.syte4, false);

							setImager(holder.q_1, arrayList_shi, 1,
									R.drawable.a190_190, screenWidth - 4, 3, 1,
									1, 1, 6, 0);
							setImager(holder.q_2, arrayList_shi, 2,
									R.drawable.a190_190, screenWidth - 4, 3, 1,
									1, 1, 6, 0);
							setImager(holder.q_3, arrayList_shi, 3,
									R.drawable.a190_190, screenWidth - 4, 3, 1,
									1, 1, 6, 0);
							setImager(holder.q_4, arrayList_shi, 4,
									R.drawable.a190_190, screenWidth - 2, 3, 1,
									1, 1, 6, 0);
							setImager(holder.q_5, arrayList_shi, 0,
									R.drawable.d380_380, screenWidth - 2, 3, 2,
									1, 1, 0, 10);
							setImager(holder.q_6, arrayList_shi, 5,
									R.drawable.a190_190, screenWidth - 2, 3, 1,
									1, 1, 6, 0);
							setImager(holder.q_7, arrayList_shi, 6,
									R.drawable.a190_190, screenWidth - 4, 3, 1,
									1, 1, 6, 0);
							setImager(holder.q_8, arrayList_shi, 7,
									R.drawable.a190_190, screenWidth - 4, 3, 1,
									1, 1, 6, 0);
							setImager(holder.q_9, arrayList_shi, 8,
									R.drawable.a190_190, screenWidth - 4, 3, 1,
									1, 1, 6, 0);
							// 点击方法
							OnClick(arrayList_shi, holder.q_1, 1);
							OnClick(arrayList_shi, holder.q_2, 2);
							OnClick(arrayList_shi, holder.q_3, 3);
							OnClick(arrayList_shi, holder.q_4, 4);
							OnClick(arrayList_shi, holder.q_5, 0);
							OnClick(arrayList_shi, holder.q_6, 5);
							OnClick(arrayList_shi, holder.q_7, 6);
							OnClick(arrayList_shi, holder.q_8, 7);
							OnClick(arrayList_shi, holder.q_9, 8);

						}
					}
				}

				return v;
			}

		};

		listView.setAdapter(adapter);

	}

	// 图片适配 screenWidth,569,284,284/358
	@SuppressLint("NewApi")
	private void setImager(ImageView in,
			ArrayList<HashMap<String, String>> list, int n, int id,
			int pm_width, int fenmu, int fenzi, int size_fenzi, int size_fenmu,
			int width_dong, int height_dong) {
		int i = list.size();
		if (n >= i) { // 判断角标越界异常
			return;
		}
		String url = list.get(n).get("img_play_url");
		if (!StringUtil.isEmpty(url)) {
			LoadBitmap.getIntence().loadImage(url, in);
		} else {
			in.setBackground(getResources().getDrawable(id));
		}
		android.view.ViewGroup.LayoutParams params = in.getLayoutParams();
		int width = pm_width * fenzi / fenmu - width_dong;
		int x = dm.widthPixels;
		params.width = width;
		params.height = width * size_fenzi / size_fenmu - height_dong;
		Logger.i("宽" + width + "高" + (width * size_fenzi / size_fenmu)
				+ "分辨率获取的宽" + x);
	}

	protected void OnClick(final ArrayList<HashMap<String, String>> arrayList,
			View v, final int n) {
		int i = arrayList.size();
		if (n >= i) {
			return;
		}
		final Intent intent = new Intent(StyleVideoList1Activity.this,
				VideoDetailActivity_new.class);

		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Logger.i("第" + n + "图片");
				intent.putExtra("video_id", arrayList.get(n).get("id"));
				intent.putExtra("edit_id",video_id);//fcz
				startActivity(intent);
			}
		});

	}

	private class viewHolder {
		LinearLayout syte1;
		LinearLayout syte2;
		LinearLayout syte3;
		LinearLayout syte4;
		TextView date1, date2, date3, date4;
		ImageView j_1, j_2, j_3, j_4;
		ImageView z_1, z_2, z_3, z_4, z_5;
		ImageView q_1, q_2, q_3, q_4, q_5, q_6, q_7, q_8, q_9, moren;
		TextView time_1, time_2, time_3, time_moren;
	}

	private void setMoe(View v, boolean or) {
		if (or) {
			v.setVisibility(View.VISIBLE);
		} else {
			v.setVisibility(View.GONE);
		}
	}

	@SuppressLint("NewApi")
	private void setbitmap(final ImageView in, final String str) {
		Logger.i("图片地址" + str);
		if (!StringUtil.isEmpty(str)) {
			// http://admin.iwo.kazhu365.com/images/95/81/1413450967920_m.jpg
			Bitmap bitmap = FileCache.getInstance().onGetBitmap(str);
			// Bitmap bitmap = LoadBitmap.getIntence().downloadBitmap(str);
			// Bitmap bitmap = getBit(str);
			Logger.i("bitmap显示....1" + bitmap);

			if (bitmap != null) {
				Logger.i("图片显示....");
				Drawable drawable = new BitmapDrawable(bitmap);
				Logger.i("drawable数据" + drawable.toString());
				if (drawable != null) {
					in.setBackground(drawable);
				} else {
					Logger.i("bitmap显示....111222");
				}

			} else {
				Logger.i("图片显示....2()");
				new Handler().postAtTime(new Runnable() {
					@Override
					public void run() {
						Bitmap bitmap1 = FileCache.getInstance().onGetBitmap(
								str);
						if (bitmap1 != null) {
							Logger.i("图片显示....22()");
							Drawable drawable1 = new BitmapDrawable(bitmap1);
							Logger.i("drawable11数据" + drawable1.toString());
							if (drawable1 != null) {
								in.setBackground(drawable1);
							}
						} else {
							Logger.i("图片显示....33()");
							new Handler().postAtTime(new Runnable() {

								@Override
								public void run() {
									Bitmap bitmap2 = FileCache.getInstance()
											.onGetBitmap(str);
									if (bitmap2 != null) {
										Logger.i("图片显示....33()");
										Drawable drawable2 = new BitmapDrawable(
												bitmap2);
										Logger.i("drawable11数据"
												+ drawable2.toString());
										if (drawable2 != null) {
											in.setBackground(drawable2);
										}

									} else {
										in.setBackground(getResources()
												.getDrawable(
														R.drawable.souye_weizhi));
									}
								}
							}, 30 * 1000);
						}
					}
				}, 10 * 1000);
			}

		} else {
			Logger.i("图片的uri有问题");
			BitmapUtil.setImageResource(in, R.drawable.souye_weizhi);
		}
	}

	private class mDataVi_new extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {

			String str = DataRequest.getStringFromURL_Base64(
					getUrl(AppConfig.NEW_V_GET_VIDEO_LIST), params[0], "1",
					"10");
			return DataRequest.getArrayListFromJSONArrayString(str);

		}

		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			mLoadBar.dismiss();
			super.onPostExecute(result);
			if (result != null) {
				initView(result);
				adapter.notifyDataSetChanged();
			} else {
				makeText("暂无数据");
				more.setVisibility(View.GONE);
			}
		}

	}

}
