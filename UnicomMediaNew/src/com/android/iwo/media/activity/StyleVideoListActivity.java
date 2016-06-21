package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import com.android.iwo.media.action.AppConfig;

import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.ACache;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class StyleVideoListActivity extends BaseActivity {
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
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
	private LinearLayout more;

	private int screenWidth;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singleton_listview);
		listView = (ListView) findViewById(R.id.singleton_list);
		listView.setDivider(null);// 将ListView分线去掉
		mCache = ACache.get(mContext);
		video_id = getIntent().getStringExtra("video_id");
		setTitle(getIntent().getStringExtra("video_name"));
		setBack_text(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		Logger.i("视频的00:" + video_id);
		new GetData().execute(video_id);
		init();
	}

	private void init() {
		findViewById(R.id.syte_jiazai_xian).setVisibility(View.VISIBLE);
		screenWidth = PreferenceUtil.getInt(mContext, "screenWidth", 100) - 18 - 18;
		more = (LinearLayout) findViewById(R.id.syte_jiazai_gengduo);
		more.setVisibility(View.GONE);// 不需要加载更多了
	}

	// （比如在电影里面 ）获得多少个频道
	private class GetData extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			HashMap<String, String> data = DataRequest
					.getHashMapFromUrl_Base64(
							getUrl(AppConfig.NEW_V_GET_VIDEO_CHANNEL_SYTE_NEW),
							params[0]);
			Logger.i(data.toString());
			if (data != null && "1".equals(data.get("code"))) {
				Logger.i("0000");
				HashMap<String, String> list = DataRequest
						.getHashMapFromJSONObjectString(data.get("data"));
				String str = DataRequest.getStringFromURL_Base64(
						getUrl(AppConfig.NEW_V_GET_VIDEO_LIST), list.get("id"),
						"1", "10");
				if (!StringUtil.isEmpty(str)) {
					if (!StringUtil.isEmpty(str)) {
						list.put("data", str);
						Logger.i("1111");
					}
					mData.add(list);
					return mData;
				}

			}
			return null;
		}

		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			super.onPostExecute(result);
			mLoadBar.dismiss();
			if (result != null) {
				initView(result);
			} else {
				makeText("暂无数据");
			}

		}

		// 界面显示
		public void initView(
				final ArrayList<HashMap<String, String>> arrayList_shi) {
			Logger.i("样式正在显示界面" + arrayList_shi);
			adapter = new IwoAdapter(StyleVideoListActivity.this, arrayList_shi) {

				private viewHolder holder;

				@Override
				public int getCount() {

					return arrayList_shi.size();
				}

				@SuppressLint("NewApi")
				@Override
				public View getView(int position, View v, ViewGroup parent) {
					if (v == null) {
						holder = new viewHolder();
						v = View.inflate(StyleVideoListActivity.this,
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
						holder.j_1 = (ImageView) v
								.findViewById(R.id.sy_jintian_1);
						holder.j_2 = (ImageView) v
								.findViewById(R.id.sy_jintian_2);
						holder.j_3 = (ImageView) v
								.findViewById(R.id.sy_jintian_3);
						holder.j_4 = (ImageView) v
								.findViewById(R.id.sy_jintian_4);

						// 样式2的图片
						holder.z_1 = (ImageView) v
								.findViewById(R.id.sy_zuotian_1);
						holder.z_2 = (ImageView) v
								.findViewById(R.id.sy_zuotian_2);
						holder.z_3 = (ImageView) v
								.findViewById(R.id.sy_zuotian_3);
						holder.z_4 = (ImageView) v
								.findViewById(R.id.sy_zuotian_4);
						holder.z_5 = (ImageView) v
								.findViewById(R.id.sy_zuotian_5);

						// 样式3的图片
						holder.q_1 = (ImageView) v
								.findViewById(R.id.sy_qiantian_1);
						holder.q_2 = (ImageView) v
								.findViewById(R.id.sy_qiantian_2);
						holder.q_3 = (ImageView) v
								.findViewById(R.id.sy_qiantian_3);
						holder.q_4 = (ImageView) v
								.findViewById(R.id.sy_qiantian_4);
						holder.q_5 = (ImageView) v
								.findViewById(R.id.sy_qiantian_5);
						holder.q_6 = (ImageView) v
								.findViewById(R.id.sy_qiantian_6);
						holder.q_7 = (ImageView) v
								.findViewById(R.id.sy_qiantian_7);
						holder.q_8 = (ImageView) v
								.findViewById(R.id.sy_qiantian_8);
						holder.q_9 = (ImageView) v
								.findViewById(R.id.sy_qiantian_9);
						// 默认的图片
						holder.moren = (ImageView) v
								.findViewById(R.id.syte_moren_ima);
						v.setTag(holder);
					} else {
						holder = (viewHolder) v.getTag();
					}
					if (arrayList_shi != null) {

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
				int pm_width, int fenmu, int fenzi, int size_fenzi,
				int size_fenmu, int width_dong, int height_dong) {
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

		protected void OnClick(
				final ArrayList<HashMap<String, String>> arrayList, View v,
				final int n) {
			int i = arrayList.size();
			if (n >= i) {
				return;
			}
			final Intent intent = new Intent(StyleVideoListActivity.this,
					VideoDetailActivity_new.class);

			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Logger.i("第" + n + "图片");
					intent.putExtra("video_id", arrayList.get(n).get("id"));
				//	intent.putExtra("is_ad", getIntent().getStringExtra("is_ad"));
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

	}
}
