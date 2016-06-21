package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.R;
import com.android.iwo.media.view.ChildViewPager;
import com.android.iwo.media.view.HorizontalListView;
import com.android.iwo.media.view.IwoToast;
import com.android.iwo.media.view.ChildViewPager.OnSingleTouchListener;
import com.android.iwo.util.view.refresh.PullToRefreshBase;
import com.android.iwo.util.view.refresh.PullToRefreshScrollView;
import com.android.iwo.util.view.refresh.PullToRefreshBase.OnRefreshListener;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.adapter.ViewPageAdapter;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.NetworkUtil;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*
 * 点导航条进入的界面
 * */
public class barActivity extends BaseActivity {
	private String id;
	private ArrayList<HashMap<String, String>> m_DataA = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> hData = new ArrayList<HashMap<String, String>>();
	private PullToRefreshScrollView scrollView;
	private View mHead;
	protected ViewPageAdapter adApter;
	private Thread mThread = null;
	private LinearLayout view_content;
	private RelativeLayout head_gallery;
	private ChildViewPager adpage;
	protected int num = 0;
	private boolean isrun = true;
	private boolean isHead = false;
	private long lastTimemGoToPlayListListener;// 频繁点之
	private static long CLICK_INTERVAL = 800;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view4_new);
		String name = getIntent().getStringExtra("video_name");
		setTitle(name);
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		id = getIntent().getStringExtra("id");
		Logger.i("传过来的ID" + id);
		init();
	}

	private void init() {
		screenHeight_fill = PreferenceUtil
				.getInt(mContext, "screenHeight", 100);
		screenWidth_fill = PreferenceUtil.getInt(mContext, "screenWidth", 100);
		setBack(null);
		view_content = (LinearLayout) findViewById(R.id.view_content);
		mHead = View.inflate(mContext, R.layout.list_head, null);
		scrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		head_gallery = (RelativeLayout) mHead.findViewById(R.id.head_gallery);
		head_gallery.setVisibility(View.VISIBLE);
		adpage = (ChildViewPager) mHead.findViewById(R.id.gallery);
		view_content = (LinearLayout) findViewById(R.id.view_content);
		view_content.addView(mHead);
		if (NetworkUtil.isWIFIConnected(mContext)) {
			new GetHData().execute(); // 获取 头部轮播图信息，
			new GetData().execute(id);
		}
		scrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				if (NetworkUtil.isWIFIConnected(mContext)) {

					hData.clear();
					m_DataA.clear();
					Logger.i("涮新完成");
					new GetHData().execute();
					new GetData().execute(id);
				} else {
					IwoToast.makeText(mContext, "网络连接异常").show();
					scrollView.onRefreshComplete();
				}

			}
		});
	}

	protected class GetData extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			String syte = getIntent().getStringExtra("syte");
			HashMap<String, String> map = null;
			if (!StringUtil.isEmpty(syte)) {
				map = DataRequest.getHashMapFromUrl_Base64(
						getUrl(AppConfig.NEW_DAO_HANG_PIN_DAOSTRING),
						params[0], "1", "10");
			} else {
				map = DataRequest.getHashMapFromUrl_Base64(
						getUrl(AppConfig.NEW_DAO_HANG_PIN_DAOSTRING),
						params[0], "1", "2");
			}
			if (map != null && "1".equals(map.get("code"))) {
				ArrayList<HashMap<String, String>> list = DataRequest
						.getArrayListFromJSONArrayString(map.get("data"));
				ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
				if (list != null) {
					for (int i = 0; i < list.size(); i++) {
						HashMap<String, String> hashMap = list.get(i);
						String str_data = null;
						if ("zt".equals(hashMap.get("note"))) {
							HashMap<String, String> base64 = DataRequest
									.getHashMapFromUrl_Base64(
											getUrl(AppConfig.NEW_DAO_HANG_PIN_DAOSTRING),
											list.get(i).get("id"), "1", "10");
							if (base64 != null
									&& "1".equals(base64.get("code"))) {
								str_data = base64.get("data");
								Logger.i("频道数据" + str_data);
							}
						} else {
							str_data = DataRequest
									.getStringFromURL_Base64(
											getUrl(AppConfig.NEW_DAO_HANG_PIN_DAOSTRING_ZI),
											list.get(i).get("id"), "1", "10");
						}
						if (!StringUtil.isEmpty(str_data)) {
							hashMap.put("data", str_data);
						}
						result.add(hashMap);
					}
					return result;
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			mLoadBar.dismiss();
			scrollView.onRefreshComplete();
			if (result != null) {
				Logger.i("品牌全部数据：" + result);
				m_DataA.clear();
				m_DataA.addAll(result);
				setView(m_DataA);
			}
		}
	}

	public void setView(ArrayList<HashMap<String, String>> list) {
		if (list == null || list.size() == 0)
			return;
		view_content.removeAllViews();
		if (isHead) {
			view_content.addView(mHead);
		}
		for (int i = 0; i < list.size(); i++) {
			View v = View.inflate(mContext, R.layout.activity_discover_item,
					null);
			final Map<String, String> map = list.get(i);
			// LoadBitmap bitmap = new LoadBitmap();
			// ImageView img = (ImageView)
			// v.findViewById(R.id.discover_item_img);
			// if (!StringUtil.isEmpty(map.get("ch_logo"))) {
			// img.setVisibility(View.VISIBLE);
			// bitmap.loadImage(map.get("ch_logo"), img);
			// }
			TextView textView = (TextView) v
					.findViewById(R.id.discover_item_text_is);
			textView.setVisibility(View.VISIBLE);
			textView.setText(map.get("ch_name"));
			View discover_item_layout = v
					.findViewById(R.id.discover_item_layout);
			discover_item_layout.setTag(i);
			discover_item_layout.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					long time = System.currentTimeMillis();
					if (time - lastTimemGoToPlayListListener < CLICK_INTERVAL) {
						return;
					}
					lastTimemGoToPlayListListener = time;
					Intent intent = null;
					if ("zt".equals(map.get("note"))) {
						intent = new Intent(mContext,
								StyleVideoList1Activity.class);
						intent.putExtra("video_id", map.get("id"));// 传的是频道ID
						intent.putExtra("video_name", map.get("ch_name"));// 专题名
					} else {
						intent = new Intent(mContext, ListBrandActivity.class);
						intent.putExtra("id", map.get("id"));
						intent.putExtra("name", map.get("ch_name"));
					}
					if (intent != null) {
						startActivity(intent);
					}

				}
			});

			HorizontalListView hListView = (HorizontalListView) v
					.findViewById(R.id.discover_item_horizontal);
			HorizontalListView hListView_new = (HorizontalListView) v
					.findViewById(R.id.discover_item_horizontal_new);

			final ArrayList<HashMap<String, String>> adData = DataRequest
					.getArrayListFromJSONArrayString(map.get("data"));
			if (adData == null || adData.size() == 0)
				continue;
			IwoAdapter adapter = new IwoAdapter((Activity) mContext, adData) {
				@SuppressLint("NewApi")
				@Override
				public View getView(int position, View v, ViewGroup parent) {
					v = mInflater.inflate(
							R.layout.activity_discover_item_item_main, null);
					Map<String, String> hastmap = adData.get(position);
					LoadBitmap bitmap = new LoadBitmap();
					ImageView imgImageView = (ImageView) v
							.findViewById(R.id.discover_item_item_img);
					TextView text = (TextView) v
							.findViewById(R.id.discover_item_item_text);
					TextView pinfen = (TextView) v
							.findViewById(R.id.discover_item_item_pinfen);
					// 左上角的图片
					if ("zt".equals(map.get("note"))) {
						v.findViewById(R.id.specialtopics).setVisibility(
								View.VISIBLE);
						if (!StringUtil.isEmpty(hastmap.get("ch_logo"))) {
							bitmap.loadImage(hastmap.get("ch_logo"),
									imgImageView);
						} else {
							imgImageView.setBackground(mContext.getResources()
									.getDrawable(R.drawable.souye_weizhi));
						}
						text.setText(hastmap.get("ch_name"));
					} else {
						v.findViewById(R.id.specialtopics).setVisibility(
								View.GONE);
						if (!StringUtil.isEmpty(hastmap.get("img_url"))) {
							bitmap.loadImage(hastmap.get("img_url"),
									imgImageView);
						} else {
							imgImageView.setBackground(mContext.getResources()
									.getDrawable(R.drawable.souye_weizhi));
						}
						text.setText(hastmap.get("name"));
					}

					if (!StringUtil.isEmpty(hastmap.get("ping_fen"))) {
						pinfen.setVisibility(View.VISIBLE);
						pinfen.getBackground().setAlpha(90);
						pinfen.setText(hastmap.get("ping_fen"));
					} else {
						pinfen.setVisibility(View.GONE);
					}

					setImgSize(imgImageView, 40, 1.0f, 3, true);
					v.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							switch (event.getAction()) {
							case MotionEvent.ACTION_MOVE:
								event.setLocation(event.getX(),
										event.getY() - 500);
								break;
							}
							return false;
						}
					});
					return v;
				}
			};
			hListView.setVisibility(View.VISIBLE);
			hListView_new.setVisibility(View.GONE);
			hListView.setAdapter(adapter);
			hListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = null;
					Map<String, String> map_new = adData.get(position);
					if ("zt".equals(map.get("note"))) {
						Logger.i("点专题");
						// intent = new Intent(mContext,
						// ListBrandActivity.class);
						// intent.putExtra("id", map.get("id"));
						// intent.putExtra("name", map.get("ch_name"));
						intent = new Intent(mContext,
								StyleVideoList1Activity.class);
						intent.putExtra("video_id", map_new.get("id"));// 传的是频道ID
						intent.putExtra("video_name", map_new.get("ch_name"));// 专题名
						intent.putExtra("zt_order", map_new.get("style_ico"));// 样式
						intent.putExtra("active_name", map_new.get("ch_date"));
					} else {
						intent = new Intent(mContext,
								VideoDetailActivity_new.class);
						intent.putExtra("video_id", map_new.get("id"));
						intent.putExtra("edit_id",map.get("id"));//fcz
						intent.putExtra("nickname", map_new.get("nickname"));
						intent.putExtra("create_time",
								map_new.get("create_time"));
						intent.putExtra("head_img", map_new.get("head_img"));
						intent.putExtra("ch_name", map_new.get("video_type"));

					}
					if (intent != null) {
						mContext.startActivity(intent);
					}

				}

			});
			android.view.ViewGroup.LayoutParams params_hListView = hListView
					.getLayoutParams();
			if(i==list.size()-1){
				params_hListView.height =(screenWidth_fill-40)/3+60;
			}
			else{
				params_hListView.height =(screenWidth_fill-40)/3+40;
			}
			view_content.addView(v);
		}
	}

	protected class GetHData extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
		String str;

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Void... params) {
			String tString = "video_share_list_" + id;
			str = DataRequest.getStringFromURL_Base64(
					getUrl(AppConfig.NEW_V_GET_AD), tString);
			Logger.i("轮暴图品牌" + str);
			return DataRequest.getArrayListFromJSONArrayString(str);

		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (null != result) {
				hData.clear();
				hData.addAll(result);
				isHead = true;
				initTopGallery(hData);
			}
		}
	}

	private int current = 0;
	private int screenHeight_fill;
	private int screenWidth_fill;

	protected void initTopGallery(final ArrayList<HashMap<String, String>> data) {
		setAd();
		LinearLayout point = (LinearLayout) mHead
				.findViewById(R.id.tiao_daohang);
		point.removeAllViews();
		final ImageView indicator_iv[] = new ImageView[data.size()];
		for (int i = 0; i < data.size() && data.size() > 1; i++) {
			indicator_iv[i] = new ImageView(mContext);
			indicator_iv[i].setImageResource(R.drawable.souye_no_heidian);// 未选中图片
			indicator_iv[i].setPadding(0, 0, 10, 0);
			point.addView(indicator_iv[i]);
		}
		if (data.size() > 1) {
			indicator_iv[0].setImageResource(R.drawable.souye_yes_huangdian);// 选中图片
		}

		adpage.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				current = position;
				num = position;
				for (int i = 0; i < data.size() && data.size() > 1; i++) {
					indicator_iv[i]
							.setImageResource(R.drawable.souye_no_heidian);// 未选中图片
				}
				if (data != null && data.size() != 0)
					indicator_iv[position % data.size()]
							.setImageResource(R.drawable.souye_yes_huangdian);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		final Handler handler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == 0) {
					if (num >= hData.size()) {
						num = 0;
					}
					adpage.setCurrentItem(num);
					num++;
				}
				return false;
			}
		});

		isrun = true;
		if (mThread == null) {
			mThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						while (isrun) {
							Thread.sleep(3500);
							Message msg = new Message();
							msg.what = 0;
							handler.sendMessage(msg);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			mThread.start();
		}
	}

	private void setAd() {
		setAdImgSize(adpage, 0, 0.5f, 1);
		ArrayList<View> views = new ArrayList<View>();
		views.clear();
		View view1 = null;
		for (final HashMap<String, String> map : hData) {
			view1 = View.inflate(mContext, R.layout.gallery_list_item, null);
			ImageView imageView = (ImageView) view1.findViewById(R.id.img);
			setImgSize(imageView, 0, 0.5f, 1);
			// setImageView_(imageView, map.get("head_img"));
			setImageView_(imageView, map.get("ad_image"));
			views.add(view1);
		}
		adApter = new ViewPageAdapter(views);
		adpage.setAdapter(adApter);
		adpage.setOnSingleTouchListener(new OnSingleTouchListener() {
			@Override
			public void onSingleTouch() {
				HashMap<String, String> map = hData.get(current % hData.size());
				if (!StringUtil.isEmpty(map.get("ad_url"))
						&& map.get("ad_url").contains("/video_info")) {
					String str = map.get("ad_url");
					if (!StringUtil.isEmpty(str)) {
						Logger.i("广告位：" + map.get("ad_url"));
						Intent intent = new Intent(mContext,
								VideoDetailActivity_new.class);
						intent.putExtra("video_id", map.get("ad_url")
								.split("=")[1]);
						intent.putExtra("ch_name", map.get("ad_text"));
						intent.putExtra("is_ad", "1");
						((Activity) mContext).startActivity(intent);
					}
				} else if (!StringUtil.isEmpty(map.get("ad_url"))
						&& map.get("ad_url").contains("/channelInfo")) { // 跳到频道页
					String str = map.get("ad_url"); // 跳到样式主题
					if (!StringUtil.isEmpty(str)) {
						Logger.i("广告位：" + map.get("ad_url"));
						Intent intent = new Intent(mContext,
								StyleVideoList1Activity.class);
						intent.putExtra("video_id", map.get("ad_url")
								.split("=")[1].split("&")[0]);
						intent.putExtra("video_name",
								map.get("ad_url").split("=")[2]);
						((Activity) mContext).startActivity(intent);
					}

				} else {
					try {
						Intent intent = new Intent(mContext,
								AdWebActivity.class);
						intent.putExtra("url", map.get("ad_url"));
						intent.putExtra("title", map.get("ad_text"));
						mContext.startActivity(intent);
						Logger.i(map.get("ad_url"));
					} catch (Exception e) {
						Logger.i(e.toString());
						Toast.makeText(mContext, "广告地址：" + map.get("ad_url"),
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	protected void setImageView_(ImageView imageView, String url) {
		if (!StringUtil.isEmpty(url)) {
			imageView.setVisibility(View.VISIBLE);
			LoadBitmap.getIntence().loadImage(url.replace("\\", ""), imageView);
		} else {
			BitmapUtil.setImageResource(imageView, R.drawable.head_img_bg);
		}
	}

	protected void setAdImgSize(View item, int del, float size, int n) {
		int width = (dm.widthPixels - (int) (del * scale + 0.5f)) / n;
		LayoutParams params = item.getLayoutParams();
		if (params != null) {
			params.width = width;
			params.height = (int) (width * size);
		}
	}
}
