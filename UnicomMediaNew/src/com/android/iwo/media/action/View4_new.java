package com.android.iwo.media.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.iwo.media.activity.AdWebActivity;
import com.android.iwo.media.activity.BrandActivity;
import com.android.iwo.media.activity.Brand_pageActivity;
import com.android.iwo.media.activity.StyleVideoList1Activity;
import com.android.iwo.media.activity.StyleVideoListActivity;
import com.android.iwo.media.activity.VideoDetailActivity_new;
import com.android.iwo.media.activity.barActivity;
import com.android.iwo.media.apk.activity.R;
import com.android.iwo.media.view.ChildViewPager;
import com.android.iwo.media.view.ChildViewPager.OnSingleTouchListener;
import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.view.HorizontalListView;
import com.android.iwo.media.view.IwoToast;
import com.android.iwo.util.view.refresh.PullToRefreshBase;
import com.android.iwo.util.view.refresh.PullToRefreshBase.OnRefreshListener;
import com.android.iwo.util.view.refresh.PullToRefreshScrollView;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.adapter.ViewPageAdapter;
import com.test.iwomag.android.pubblico.util.ACache;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.FileCache;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.NetworkUtil;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 首页
 */
public class View4_new extends ViewTab implements OnClickListener {
	private View view;
	public Context mContext;
	protected int num = 0;
	protected ViewPageAdapter adApter;
	private Thread mThread = null;
	private View mHead;
	private RelativeLayout head_gallery;
	private ChildViewPager adpage;
	private String id = "1";
	private boolean isrun = true;
	private ArrayList<HashMap<String, String>> hData = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> m_DataA = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> na_Data = new ArrayList<HashMap<String, String>>();
	private LinearLayout view_content;
	protected CommonDialog mLoadBar;
	private ACache mCache;
	private PullToRefreshScrollView scrollView;
	private long lastTimemGoToPlayListListener;// 频繁点之
	private static long CLICK_INTERVAL = 800;
	private String title_textString[] = { "喜", "怒", "哀", "惧", "爱", "恶", "欲" };
	private int img_w[] = { R.drawable.s_img_01, R.drawable.s_img_02,
			R.drawable.s_img_03, R.drawable.s_img_04, R.drawable.s_img_05,
			R.drawable.s_img_06, R.drawable.s_img_07, R.drawable.s_img_08 };
	private int img_z[] = { R.drawable.s_img_01_z, R.drawable.s_img_02_z,
			R.drawable.s_img_03_z, R.drawable.s_img_04_z,
			R.drawable.s_img_05_z, R.drawable.s_img_06_z,
			R.drawable.s_img_07_z, R.drawable.s_img_08_z };
	private HashSet<String> hash_tell = new HashSet<String>(); // 装id

	public View4_new(Context context) {

		mContext = context;
		view = View.inflate(mContext, R.layout.view4_new, null);
		view_content = (LinearLayout) view.findViewById(R.id.view_content);
		mCache = ACache.get(mContext);
		mLoadBar = new CommonDialog(mContext);
		scrollView = (PullToRefreshScrollView) view
				.findViewById(R.id.pull_refresh_scrollview);
		init();

		scrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				// mCache.put(Constants.VIDEO_ADVERTISING, "");//清空大图的缓存
				// mCache.put(Constants.VIDEO_CHANNEL, "");//清空视频缓冲
				if (NetworkUtil.isWIFIConnected(mContext)) {
					mCache.clear();
					hData.clear();
					m_DataA.clear();
					new GetHData().execute();
				} else {
					IwoToast.makeText(mContext, "网络连接异常").show();
					scrollView.onRefreshComplete();
				}
				new GetCacheData().execute();
			}
		});

	}

	@SuppressLint("NewApi")
	protected void init() {
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		screenHeight_fill = PreferenceUtil
				.getInt(mContext, "screenHeight", 100);
		screenWidth_fill = PreferenceUtil.getInt(mContext, "screenWidth", 100);
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		horizontal_layout = View.inflate(mContext, R.layout.na_hori, null);
		na_Data = getArrlist();
		setNa(na_Data);

		ViewTreeObserver ob = horizontal_layout.getViewTreeObserver();
		ob.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				horizontal_layout.getViewTreeObserver()
						.removeGlobalOnLayoutListener(this);
				int width = horizontal_layout.getWidth();
				Logger.i("导航条宽：" + width);

			}
		});
		mHead = View.inflate(mContext, R.layout.list_head, null);

		ViewTreeObserver observer = mHead.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				mHead.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				int width = mHead.getWidth();
				Logger.i("头部宽：" + width);

			}
		});

		head_gallery = (RelativeLayout) mHead.findViewById(R.id.head_gallery);
		head_gallery.setVisibility(View.VISIBLE);
		adpage = (ChildViewPager) mHead.findViewById(R.id.gallery);
		view_content.addView(mHead);
		String hDataString = mCache.getAsString(Constants.VIDEO_ADVERTISING);
		if (!StringUtil.isEmpty(hDataString)) {
			ArrayList<HashMap<String, String>> list = DataRequest
					.getArrayListFromJSONArrayString(hDataString);
			if (list != null)
				hData.addAll(list);
			initTopGallery(hData);
		}
		// new Test().execute();
		new GetCacheData().execute();
		if (NetworkUtil.isWIFIConnected(mContext)) {
			new GetHData().execute(); // 获取 头部轮播图信息，
		}
	}

	private void setView(final ArrayList<HashMap<String, String>> list) {
		Logger.i("真正显示界面的数据" + list.toString());
		if (list == null || list.size() == 0)
			return;
		view_content.removeAllViews();
		view_content.addView(mHead);
		view_content.addView(horizontal_layout);
		for (int i = 0; i < list.size(); i++) {
			View v = View.inflate(mContext, R.layout.activity_discover_item,
					null);
			final int viewPosition = i;
			final Map<String, String> map = list.get(i);

			LoadBitmap bitmap = new LoadBitmap();
			ImageView img = (ImageView) v.findViewById(R.id.discover_item_img);
			if (!StringUtil.isEmpty(map.get("ch_logo"))) {
				img.setVisibility(View.VISIBLE);
				bitmap.loadImage(map.get("ch_logo"), img);
			}
			TextView textView = (TextView) v
					.findViewById(R.id.discover_item_text);
			// TextView textView_is = (TextView)
			// v.findViewById(R.id.discover_item_text_is);
			// TextView more = (TextView)
			// v.findViewById(R.id.discover_item_more);
			// if ("224".equals(map.get("id"))) {
			// more.setVisibility(View.GONE);
			// }
			// textView.setText(map.get("ch_name"));
			textView.setVisibility(View.GONE);// 隐藏首页频道名字
			// textView_is.setText(map.get("note"));
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
					if ("123".equals(map.get("id"))) {
						intent = new Intent(mContext, Brand_pageActivity.class);
						mContext.startActivity(intent);
						return;
					}

					// intent = new Intent(mContext, MoreActivity.class);
					// intent.putExtra("id", map.get("id"));
					// intent.putExtra("video_name", map.get("ch_name"));
					intent = new Intent(mContext, barActivity.class);
					intent.putExtra("id", map.get("id"));
					intent.putExtra("video_name", map.get("ch_name"));
					intent.putExtra("syte", "1");
					mContext.startActivity(intent);
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
					Map<String, String> map = adData.get(position);
					LoadBitmap bitmap = new LoadBitmap();
					ImageView imgImageView = (ImageView) v
							.findViewById(R.id.discover_item_item_img);
					TextView text = (TextView) v
							.findViewById(R.id.discover_item_item_text);
					TextView pinfen = (TextView) v
							.findViewById(R.id.discover_item_item_pinfen);
					// 左上角的图片
					if ("zt".equals(map.get("video_desc"))) {
						v.findViewById(R.id.specialtopics).setVisibility(
								View.VISIBLE);
					} else {
						v.findViewById(R.id.specialtopics).setVisibility(
								View.GONE);
					}
					String pid = map.get("pid");
					if (pid != null && "123".equals(pid)) {
						if (!StringUtil.isEmpty(map.get("ch_logo"))) {
							bitmap.loadImage(map.get("ch_logo"), imgImageView);
						} else {
							imgImageView.setBackground(mContext.getResources()
									.getDrawable(R.drawable.pp_280_170));
						}
						text.setVisibility(View.INVISIBLE);
						// text.setText(map.get("ch_name"));

						android.view.ViewGroup.LayoutParams params = imgImageView
								.getLayoutParams();
						int wid = screenWidth_fill - 18 - 18 - 2;
						int width = wid * (wid / 2 - 11) / wid;
						params.width = width;
						params.height = width * 170 / 280;
						
					} else {
						if (!StringUtil.isEmpty(map.get("ping_fen"))) {
							pinfen.setVisibility(View.VISIBLE);
							pinfen.getBackground().setAlpha(90);
							pinfen.setText(map.get("ping_fen"));
						} else {
							pinfen.setVisibility(View.GONE);
						}
						if (!StringUtil.isEmpty(map.get("img_url"))) {
							bitmap.loadImage(map.get("img_url"), imgImageView);
						} else {
							imgImageView.setBackground(mContext.getResources()
									.getDrawable(R.drawable.souye_weizhi));
						}
						text.setText(map.get("name"));
						setImgSize_new(imgImageView, 40, 1.0f, 3);
						android.view.ViewGroup.LayoutParams params_text = text.getLayoutParams();
						params_text.width=(screenWidth_fill-40)/3;

					}
					imgImageView.setTag(R.id.discover_item_item_img_tag1,
							position);
					imgImageView.setTag(R.id.discover_item_item_img_tag2,
							viewPosition);

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
			if ("123".equals(map.get("id"))) {
				hListView.setVisibility(View.GONE);
				hListView_new.setVisibility(View.VISIBLE);
				hListView_new.setAdapter(adapter);
				hListView_new.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						Map<String, String> map_new = adData.get(position);
						Intent intent = new Intent(mContext, BrandActivity.class);
							intent.putExtra("video_name",
									map_new.get("ch_name"));
							intent.putExtra("id", map_new.get("id"));
						mContext.startActivity(intent);
					}

				});

			} else {
				hListView.setVisibility(View.VISIBLE);
				hListView_new.setVisibility(View.GONE);
				hListView.setAdapter(adapter);
				hListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = null;
						Map<String, String> map_xew = adData.get(position);
						String video_desc = map_xew.get("video_desc");
							if ("zt".equals(video_desc)) {
								intent = new Intent(mContext,
										StyleVideoList1Activity.class);
								intent.putExtra("video_id", map_xew.get("id"));// 传的是频道ID
								intent.putExtra("video_name",
										map_xew.get("name"));// 专题名
								intent.putExtra("zt_order",
										map_xew.get("zt_order"));// 样式
								intent.putExtra("active_name",
										map_xew.get("active_name"));
							} else {
								intent = new Intent(mContext,
										VideoDetailActivity_new.class);
								intent.putExtra("edit_id",map.get("id"));//fcz
								intent.putExtra("video_id", map_xew.get("id"));
								intent.putExtra("nickname",
										map_xew.get("nickname"));
								intent.putExtra("create_time",
										map_xew.get("create_time"));
								intent.putExtra("head_img",
										map_xew.get("head_img"));
								intent.putExtra("ch_name",
										map_xew.get("video_type"));
							}
						mContext.startActivity(intent);
					}

				});

			}
			android.view.ViewGroup.LayoutParams params_hListView = hListView
					.getLayoutParams();
			android.view.ViewGroup.LayoutParams params_hListView_new = hListView_new
					.getLayoutParams();
			int wid = screenWidth_fill - 18 - 18 - 2;
			int width = wid * (wid / 2 - 11) / wid;
			params_hListView_new.height = width * 170 / 280;
			params_hListView.height =(screenWidth_fill-40)/3+40;
			view_content.addView(v);
		}
	}

	public View getView() {
		return view;
	}

	/**
	 * 获取轮播图数据
	 * 
	 * @author hanpengyuan
	 * 
	 */
	protected class GetHData extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
		String str;

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Void... params) {
			str = DataRequest.getStringFromURL_Base64(
					getUrl(AppConfig.NEW_V_GET_AD), "video_share_top");
			Logger.i("轮暴图" + str);
			Logger.i("轮暴图缓存" + mCache.getAsString(Constants.VIDEO_ADVERTISING));
			// if(StringUtil.isEmpty(str)&&StringUtil.isEmpty(mCache.getAsString(Constants.VIDEO_ADVERTISING))){
			// Logger.i("删掉头部");
			// view_content.removeView(mHead);
			// }
			if (StringUtil.isEmpty(mCache
					.getAsString(Constants.VIDEO_ADVERTISING))) {
				mCache.put(Constants.VIDEO_ADVERTISING, str);
				return DataRequest.getArrayListFromJSONArrayString(str);
			} else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (null != result) {
				hData.clear();
				hData.addAll(result);
				initTopGallery(hData);
				Logger.i("首页轮播图:去适配");
				Logger.i("首页轮播图:" + result.toString());
			} else {
				if (!StringUtil.isEmpty(str)) {
					mCache.put(Constants.VIDEO_ADVERTISING, str);
				}
			}
		}
	}

	/**
	 * 在线获取频道数据
	 */
	protected class GetData extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
		private String str;

		protected ArrayList<HashMap<String, String>> doInBackground(
				Void... params) {
			HashMap<String, String> map = DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_V_GET_VIDEO_CHANNEL), "6");
			ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
			if (null != map) {
				if ("1".equals(map.get("code"))) {
					str = map.get("data");
					Logger.i("获取在线数据" + str);
					if (StringUtil.isEmpty(mCache
							.getAsString(Constants.VIDEO_CHANNEL))) { // 如果没有缓存
						Logger.i("当没有缓冲数据 来到这一步");
						if (!StringUtil.isEmpty(str)) {
							mCache.put(Constants.VIDEO_CHANNEL, str);
						}

						ArrayList<HashMap<String, String>> list = DataRequest
								.getArrayListFromJSONArrayString(str);

						String brand_str = DataRequest
								.getStringFromURL_Base64(getUrl(AppConfig.NEW_V_GET_VIDEO_LIST_BRAND));
						Logger.i(list.size() + "频道个数");
						for (int i = 0; list != null && i < list.size(); i++) {
							HashMap<String, String> data = list.get(i);
							if ("123".equals(data.get("id"))) { // 品牌单独去获取数据
								Logger.i("品牌单独数据" + brand_str);
								mCache.put(Constants.VIDEO_CHANNEL + i,
										brand_str);
								data.put("data", brand_str);
								result.add(data);
								continue;
							}
							String channelItem = DataRequest
									.getStringFromURL_Base64(
											getUrl(AppConfig.NEW_V_GET_VIDEO_LIST_NEW),
											data.get("id"));
							Logger.i("缓存视频列表数据" + channelItem + "..."
									+ data.get("id"));
							if (StringUtil.isEmpty(channelItem)
									|| "122".equals(data.get("id"))
									|| "124".equals(data.get("id"))
									|| "202".equals(data.get("id"))) {
								continue;
							}
							mCache.put(Constants.VIDEO_CHANNEL + i, channelItem);
							data.put("data", channelItem);
							result.add(data);
						}
						return result;
					} else {
						return null;
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			mLoadBar.dismiss();
			scrollView.onRefreshComplete();
			if (result != null) {
				m_DataA.clear();
				m_DataA.addAll(result);
				setView(m_DataA);

			} else { // 有缓存 或者请求不到数据
				if (!StringUtil.isEmpty(str)) {
					mCache.put(Constants.VIDEO_CHANNEL, str);
				}
			}
		}
	}

	/**
	 * 添加缓存数据
	 */
	protected class GetCacheData extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Void... params) {
			ArrayList<HashMap<String, String>> list = DataRequest
					.getArrayListFromJSONArrayString(mCache
							.getAsString(Constants.VIDEO_CHANNEL));
			if (list != null) {
				Logger.i("缓存频道数据：" + list.toString());
			}

			// 自己组的集合数据
			ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
			for (int i = 0; list != null && i < list.size(); i++) {
				HashMap<String, String> map = list.get(i);
				String channelItem = mCache.getAsString(Constants.VIDEO_CHANNEL
						+ i);
				map.put("data", channelItem);
				result.add(map);
			}
			return result;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			mLoadBar.dismiss();
			Logger.i("获取缓存数据:" + result.toString());
			m_DataA.addAll(result);
			setView(m_DataA);
			if (NetworkUtil.isWIFIConnected(mContext)) {
				new GetData().execute();
			}
		}
	}

	private int current = 0;
	private View horizontal_layout;
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
			// indicator_iv[i].setImageResource(R.drawable.hui);
			indicator_iv[i].setImageResource(R.drawable.souye_no_heidian);// 未选中图片
			indicator_iv[i].setPadding(0, 0, 10, 0);
			point.addView(indicator_iv[i]);
		}
		if (data.size() > 1) {
			// indicator_iv[0].setImageResource(R.drawable.lan);
			indicator_iv[0].setImageResource(R.drawable.souye_yes_huangdian);// 选中图片
		}

		adpage.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				current = position;
				num = position;
				for (int i = 0; i < data.size() && data.size() > 1; i++) {
					// indicator_iv[i].setImageResource(R.drawable.hui);
					indicator_iv[i]
							.setImageResource(R.drawable.souye_no_heidian);// 未选中图片
				}
				if (data != null && data.size() != 0)
					// indicator_iv[position %
					// data.size()].setImageResource(R.drawable.lan);
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

	private void setNa(final ArrayList<HashMap<String, String>> list) {
		if (list == null || list.size() == 0) {
			return;
		}
		Logger.i("12580:" + list.toString());
		HorizontalListView head_listView = (HorizontalListView) horizontal_layout
				.findViewById(R.id.discover_item_horizontal);
		android.view.ViewGroup.LayoutParams params = head_listView
				.getLayoutParams();

		final IwoAdapter adapter = new IwoAdapter((Activity) mContext, list) {

			@Override
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null) {
					v = View.inflate(mContext, R.layout.item_navigation, null);
				}
				HashMap<String, String> map = list.get(position);
				ImageView imageView = (ImageView) v.findViewById(R.id.na_new);
				if (hash_tell.contains(map.get("id"))) {
					BitmapUtil
							.setBackgroundResource(imageView, img_z[position]);
				} else {
					BitmapUtil
							.setBackgroundResource(imageView, img_w[position]);
				}
				android.view.ViewGroup.LayoutParams params = imageView
						.getLayoutParams();

				params.height = screenWidth_fill / 6;
				if ("202".equals(map.get("id"))) {
					params.width = (screenWidth_fill / 6) * 119 / 101;
				} else {
					params.width = (screenWidth_fill / 6) * 93 / 91;
				}

				return v;
			}

		};
		head_listView.setAdapter(adapter);
		params.height = screenWidth_fill / 6;
		head_listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if (!hash_tell.contains(list.get(pos).get("id"))) {
					hash_tell.clear();
					hash_tell.add(list.get(pos).get("id"));
					adapter.notifyDataSetChanged();
				}
				Intent intent = new Intent(mContext, barActivity.class);
				intent.putExtra("id", list.get(pos).get("id"));
				intent.putExtra("video_name", list.get(pos).get("ch_name"));
				if ("202".equals(list.get(pos).get("id"))) {
					intent.putExtra("syte", "1");
				}
				mContext.startActivity(intent);
			}
		});

	}

	private void setAd() {
		setAdImgSize(adpage, 36, 163 / 289.0f);
		ArrayList<View> views = new ArrayList<View>();
		views.clear();
		View view1 = null;
		for (final HashMap<String, String> map : hData) {
			view1 = View.inflate(mContext, R.layout.gallery_list_item, null);
			ImageView imageView = (ImageView) view1.findViewById(R.id.img);
			ImageView ad_left_img = (ImageView) view1
					.findViewById(R.id.ad_left_img);
			String ad_url = map.get("ad_url");
			if (!StringUtil.isEmpty(ad_url) && ad_url.contains("channelInfo")||!StringUtil.isEmpty(map.get("ad_image_s"))) {
				ad_left_img.setVisibility(View.VISIBLE);
				if(StringUtil.isEmpty(map.get("ad_image_s"))){
					ad_left_img.setBackgroundResource(R.drawable.ad_left_img);
				}
				else{
					LoadBitmap.getIntence().loadImage(ad_left_img, map.get("ad_image_s"));
				}
			}
			setImgSize_new(imageView, 36, 163 / 289.0f, 1);
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
						if (map.get("ad_url").split("=").length >= 2) {
							intent.putExtra("video_id", map.get("ad_url")
									.split("=")[1]);
						}
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
								StyleVideoListActivity.class);
						if (map.get("ad_url").split("=").length >= 3) {
							if (map.get("ad_url").split("=")[1].split("&").length >= 1) {
								intent.putExtra("video_id", map.get("ad_url")
										.split("=")[1].split("&")[0]);
							}
							intent.putExtra("video_name", map.get("ad_url")
									.split("=")[2]);
						}
						//intent.putExtra("is_ad", "1");
						((Activity) mContext).startActivity(intent);
					}

				} else if (!StringUtil.isEmpty(map.get("ad_url"))
						&& map.get("ad_url").contains("/brand_list")) { // 跳品牌
					Intent intent = new Intent(mContext, BrandActivity.class);
					if (map.get("ad_url").split("=").length >= 3) {
						if (map.get("ad_url").split("=")[1].split("&").length >= 1) {
							intent.putExtra(
									"id",
									map.get("ad_url").split("=")[1].split("&")[0]);
						}
						intent.putExtra("video_name",
								map.get("ad_url").split("=")[2]);
					}
				//	intent.putExtra("is_ad", "1");
					((Activity) mContext).startActivity(intent);
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

	protected void setCache(String name, String cache) {
		FileCache.getInstance().writeStringToSD(name + id, cache);
	}

	protected void setAdImgSize(View item, int del, float size, int n) {
		int width = (dm.widthPixels - (int) (del * scale + 0.5f)) / n;
		LayoutParams params = item.getLayoutParams();
		if (params != null) {
			params.width = width;
			params.height = (int) (width * size);
		}
	}

	protected void setAdImgSize(View item, int del, float size) {
		int width = (dm.widthPixels - del);
		LayoutParams params = item.getLayoutParams();
		if (params != null) {
			params.width = width;
			params.height = (int) (width * size);
		}
	}

	protected void setImageView_(ImageView imageView, String url) {
		if (!StringUtil.isEmpty(url)) {
			imageView.setVisibility(View.VISIBLE);
			LoadBitmap.getIntence().loadImage(url.replace("\\", ""), imageView);
		} else {
			BitmapUtil.setImageResource(imageView, R.drawable.head_img_bg);
		}
	}

	protected String getUid() {
		return PreferenceUtil.getString(mContext, "user_id", "");
	}

	protected String getMode() {
		return PreferenceUtil.getString(mContext, "video_model", "day");
	}

	protected String getUrl(String url) {
		return AppConfig.GetUrl(mContext, url);
	}

	@Override
	public void onClick(View v) {
	}

	// 这个有点2 待优化
	private ArrayList<HashMap<String, String>> getArrlist() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("id", "202");
		map1.put("ch_name", "七日谈");
		list.add(map1);
		HashMap<String, String> map_1 = new HashMap<String, String>();
		map_1.put("id", "115");
		map_1.put("ch_name", title_textString[0]);
		list.add(map_1);
		HashMap<String, String> map_2 = new HashMap<String, String>();
		map_2.put("id", "121");
		map_2.put("ch_name", title_textString[1]);
		list.add(map_2);
		HashMap<String, String> map_3 = new HashMap<String, String>();
		map_3.put("id", "120");
		map_3.put("ch_name", title_textString[2]);
		list.add(map_3);
		HashMap<String, String> map_4 = new HashMap<String, String>();
		map_4.put("id", "146");
		map_4.put("ch_name", title_textString[3]);
		list.add(map_4);
		HashMap<String, String> map_5 = new HashMap<String, String>();
		map_5.put("id", "118");
		map_5.put("ch_name", title_textString[4]);
		list.add(map_5);
		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("id", "117");
		map2.put("ch_name", title_textString[5]);
		list.add(map2);
		HashMap<String, String> map3 = new HashMap<String, String>();
		map3.put("id", "116");
		map3.put("ch_name", title_textString[6]);
		list.add(map3);
		return list;
	}

}
