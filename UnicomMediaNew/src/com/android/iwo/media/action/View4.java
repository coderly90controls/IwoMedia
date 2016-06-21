package com.android.iwo.media.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iwo.media.activity.AdWebActivity;
import com.android.iwo.media.activity.EventVideoListView;
import com.android.iwo.media.activity.RecommendedVideoListActivity;
import com.android.iwo.media.activity.VideoDetailActivity;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.view.ChildViewPager;
import com.android.iwo.media.view.ChildViewPager.OnSingleTouchListener;
import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.view.HorizontalListView;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.adapter.ViewPageAdapter;
import com.test.iwomag.android.pubblico.util.ACache;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.FileCache;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 首頁
 */
public class View4 extends ViewTab implements OnClickListener {
	ListView listView;
	private View view;
	public Context mContext;
	private IwoAdapter mAdapter;
	private IwoAdapter[] hAdapter;
	protected int num = 0;
	protected ViewPageAdapter adApter;
	private Thread mThread = null;
	private View mHead;
	private View mFooter;
	private RelativeLayout head_gallery;
	private ChildViewPager adpage;
	// TODO 轮播图暂时需要，等轮播图接口指定后，根据情况替换
	private String id = "1";
	private float scale = 0;
	private boolean isrun = true;

	private ArrayList<HashMap<String, String>> hData = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private ArrayList<ArrayList<HashMap<String, String>>> mAdData = new ArrayList<ArrayList<HashMap<String, String>>>();

	private boolean listRefresh = false;
	private boolean dataEmpty = true;
	protected CommonDialog mLoadBar;
	ACache mCache;

	public View4(Context context) {
		mContext = context;
		view = View.inflate(mContext, R.layout.view4_layout, null);
		mCache = ACache.get(mContext);
		mLoadBar = new CommonDialog(mContext);
		init();
		if (!StringUtil.isEmpty(mCache.getAsString(Constants.VIDEO_CHANNEL))) {
			dataEmpty = false;
			mLoadBar.setMessage("数据加载中...");
			mLoadBar.show();
			new GetData2().execute();
		} else {
			new GetData().execute();
		}

	}

	@SuppressLint("NewApi")
	protected void init() {
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		listView = (ListView) view.findViewById(R.id.view3_layout_list);
		listView.setCacheColorHint(0);
		listView.setDividerHeight(0);
		
		// TODO 添加发现里的轮播图。----------------------------------------
		mHead = View.inflate(mContext, R.layout.list_head, null);
		head_gallery = (RelativeLayout) mHead.findViewById(R.id.head_gallery);
		head_gallery.setVisibility(View.VISIBLE);
		adpage = (ChildViewPager) mHead.findViewById(R.id.gallery);
		// ---------------------------------------------------------------

		listView.addHeaderView(mHead);
		mFooter = View.inflate(mContext, R.layout.list_view4_footer, null);
		listView.addFooterView(mFooter);
		mFooter.findViewById(R.id.order).setOnClickListener(this);

		String hDataString = mCache.getAsString(Constants.VIDEO_ADVERTISING);
		if (!StringUtil.isEmpty(hDataString)) {
			ArrayList<HashMap<String, String>> list = DataRequest.getArrayListFromJSONArrayString(hDataString);
			if (list != null)
				hData.addAll(list);
			initTopGallery(hData);
		}
		new GetHData().execute(); // 获取 头部轮播图信息，
		mAdapter = new IwoAdapter((Activity) mContext, mData) {
			HorizontalListView hListView = null;

			@Override
			public View getView(int position, View v, ViewGroup parent) {
				final int viewPosition = position;
				Map<String, String> map = mData.get(position);
				if (null == v) {
					v = mInflater.inflate(R.layout.activity_discover_item, null);
				}
				// if ("night".equals(getMode())) {
				// v.findViewById(R.id.fen_ge_line).setBackgroundResource(R.color.comm_pink_color);
				// } else {
				// }
				LoadBitmap bitmap = new LoadBitmap();
				ImageView img = (ImageView) v.findViewById(R.id.discover_item_img);
				bitmap.loadImage(map.get("ch_logo"), img);
				TextView textView = (TextView) v.findViewById(R.id.discover_item_text);
				textView.setText(map.get("ch_name"));
				View discover_item_layout = v.findViewById(R.id.discover_item_layout);
				discover_item_layout.setTag(position);
				discover_item_layout.setOnClickListener(View4.this);
				hListView = (HorizontalListView) v.findViewById(R.id.discover_item_horizontal);
				// TODO
				if (hAdapter != null) {
					if (mAdData.size() > 1) {
						final ArrayList<HashMap<String, String>> adData = mAdData.get(position);
						hAdapter[position % hAdapter.length] = new IwoAdapter((Activity) mContext, adData) {
							@Override
							public View getView(int position, View v, ViewGroup parent) {
								v = mInflater.inflate(R.layout.activity_discover_item_item, null);
								Map<String, String> map = adData.get(position);
								LoadBitmap bitmap = new LoadBitmap();
								ImageView imgImageView = (ImageView) v.findViewById(R.id.discover_item_item_img);
								bitmap.loadImage(map.get("img_url"), imgImageView);
								setImgSize(imgImageView, 0, 173 / 300.0f, 2.3f);
								TextView text = (TextView) v.findViewById(R.id.discover_item_item_text);
								text.setText(map.get("name"));
								imgImageView.setTag(R.id.discover_item_item_img_tag1, position);
								imgImageView.setTag(R.id.discover_item_item_img_tag2, viewPosition);

								v.setOnTouchListener(new OnTouchListener() {
									@Override
									public boolean onTouch(View v, MotionEvent event) {
										switch (event.getAction()) {
										case MotionEvent.ACTION_DOWN:
											break;
										case MotionEvent.ACTION_MOVE:
											event.setLocation(event.getX(), event.getY() - 500);
											break;
										case MotionEvent.ACTION_UP:
											break;

										default:
											break;
										}
										return false;
									}
								});
								return v;
							}
						};
						hListView.setAdapter(hAdapter[position]);
						hListView.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								Map<String, String> map = adData.get(position);
								Intent intent = new Intent(mContext, VideoDetailActivity.class);
								intent.putExtra("video_id", map.get("id"));
								intent.putExtra("nickname", map.get("nickname"));
								intent.putExtra("create_time", map.get("create_time"));
								intent.putExtra("head_img", map.get("head_img"));
								mContext.startActivity(intent);

							}

						});
						hAdapter[position].notifyDataSetChanged();
					}
				}
				return v;
			}
		};

		listView.setCacheColorHint(0);
		listView.setDividerHeight(0);
		listView.setAdapter(mAdapter);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int first, int visible, int total) {
				if ((first + visible == total)) {
				}
			}
		});
		// listView.setXListViewListener(new IXListViewListener() {
		//
		// @Override
		// public void onRefresh() {
		// listRefresh = true;
		// // new GetData().execute();
		// }
		// });

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
	protected class GetHData extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
		String str;

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {

			str = DataRequest.getStringFromURL_Base64(getUrl(AppConfig.NEW_V_GET_AD), "video_share_top");
			if (StringUtil.isEmpty(mCache.getAsString(Constants.VIDEO_ADVERTISING))) {
				mCache.put(Constants.VIDEO_ADVERTISING, str);
				return DataRequest.getArrayListFromJSONArrayString(str);
			} else {
				return null;
			}

		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			new RequestAdvertising().execute();
			if (null != result) {
				hData.clear();
				hData.addAll(result);
				initTopGallery(hData);
				Logger.i("首页轮播图:" + result.toString());
			} else {
				if (!StringUtil.isEmpty(str)) {
					mCache.put(Constants.VIDEO_ADVERTISING, str);
				}
			}
		}
	}

	/**
	 * 模拟访问首页广告。
	 * 
	 * @author hanpengyuan
	 * 
	 */
	protected class RequestAdvertising extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			for (HashMap<String, String> map : hData) {
				if (!StringUtil.isEmpty(map.get("ad_url")) && !map.get("ad_url").contains("/v/info?")) {
					Logger.i("首页轮播图广告位:" + map.get("ad_url"));
					DataRequest.getStringFromURL(getUrl(map.get("ad_url")), "");
				}
			}
			return null;
		}

	}

	/**
	 * 获取多少个频道
	 * 
	 * @author hanpengyuan
	 * 
	 */
	protected class GetData extends AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected void onPreExecute() {
		}

		private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
		private ArrayList<ArrayList<HashMap<String, String>>> mAdData = new ArrayList<ArrayList<HashMap<String, String>>>();

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			HashMap<String, String> map = DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_GET_VIDEO_CHANNEL));
			if (null != map) {
				if ("1".equals(map.get("code"))) {
					Logger.i(map.toString());
					mCache.put(Constants.VIDEO_CHANNEL, map.get("data"));
					ArrayList<HashMap<String, String>> list = DataRequest.getArrayListFromJSONArrayString(map.get("data"));
					if (list != null) {
						int[] strs = new int[list.size()];
						Logger.i("频道列表：" + list.size());
						try {
							for (int i = 0; i < list.size(); i++) {
								HashMap<String, String> map1 = list.get(i);
								String channelItem = DataRequest.getStringFromURL_Base64(getUrl(AppConfig.NEW_V_GET_VIDEO_LIST),
										map1.get("id"), "1", "10");
								mCache.put(Constants.VIDEO_CHANNEL + i, channelItem);
								ArrayList<HashMap<String, String>> listItem = DataRequest.getArrayListFromJSONArrayString(channelItem);
								if (listItem != null) {
									Logger.i("sadsads  " + listItem.size());
									// if (dataEmpty) {
									mAdData.add(listItem);
									// }

									Logger.i(listItem.toString());
									Logger.i("" + listItem.size());
									strs[i] = -1;
									Logger.i("要创建的数组：" + strs[i]);
								} else {
									strs[i] = i;
									Logger.i("要创建的数组：" + strs[i]);
								}
							}
							for (int i = strs.length - 1; i >= 0; i--) {
								Logger.i("要删除的数组：" + strs[i]);
								if (strs[i] == i) {
									list.remove(i);
								}
							}
						} catch (Exception e) {

						}
						// if (dataEmpty) {
						mData.addAll(list);
						hAdapter = new IwoAdapter[mAdData.size()];
						// }
					}

				}
			}
			return map;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {

			// if (dataEmpty) {

			if (null != result) {
				if ("1".equals(result.get("code"))) {
					View4.this.mData.clear();
					View4.this.mAdData.clear();
					View4.this.mData.addAll(mData);
					View4.this.mAdData.addAll(mAdData);
					view.findViewById(R.id.view3_layout_list).setVisibility(View.VISIBLE);
					mAdapter.notifyDataSetChanged();
				}
			}
			// }
		}

	}

	protected class GetData2 extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			ArrayList<HashMap<String, String>> list = DataRequest.getArrayListFromJSONArrayString(mCache
					.getAsString(Constants.VIDEO_CHANNEL));
			if (list != null) {
				mData.clear();
				mAdData.clear();
				int[] strs = new int[list.size()];
				for (int i = 0; i < list.size(); i++) {
					String channelItem = mCache.getAsString(Constants.VIDEO_CHANNEL + i);
					ArrayList<HashMap<String, String>> listItem = DataRequest.getArrayListFromJSONArrayString(channelItem);
					if (listItem != null) {
						Logger.i("频道数据===" + listItem);
						mAdData.add(listItem);
						strs[i] = -1;
					} else {
						strs[i] = i;
					}
				}
				for (int i = strs.length - 1; i >= 0; i--) {
					Logger.i("要删除的数组：" + strs[i]);
					if (strs[i] == i) {
						list.remove(i);
					}
				}
				mData.addAll(list);

				hAdapter = new IwoAdapter[mAdData.size()];
			}

			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (!StringUtil.isEmpty(mCache.getAsString(Constants.VIDEO_CHANNEL))) {
				mAdapter.notifyDataSetChanged();
				view.findViewById(R.id.view3_layout_list).setVisibility(View.VISIBLE);
			}
			new GetData().execute();
			mLoadBar.dismiss();
		}

	}

	private int current = 0;

	// 轮播图需要实现的方法---------------------------------------------------
	/**
	 * 底部条目
	 * 
	 * @param data
	 */
	protected void initTopGallery(final ArrayList<HashMap<String, String>> data) {
		setAd();
		LinearLayout point = (LinearLayout) mHead.findViewById(R.id.tiao_daohang);
		final ImageView indicator_iv[] = new ImageView[data.size()];
		for (int i = 0; i < data.size() && data.size() > 1; i++) {
			indicator_iv[i] = new ImageView(mContext);
			indicator_iv[i].setImageResource(R.drawable.hui);//未选中图片
			indicator_iv[i].setPadding(0, 0, 10, 0);
			point.addView(indicator_iv[i]);
		}
		if (data.size() > 1) {
			indicator_iv[0].setImageResource(R.drawable.lan);//选中图片
		}

		adpage.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				current = position;
				num = position;
				for (int i = 0; i < data.size() && data.size() > 1; i++) {
					indicator_iv[i].setImageResource(R.drawable.hui);
				}
				if (data != null && data.size() != 0)
					indicator_iv[position % data.size()].setImageResource(R.drawable.lan);
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

	/**
	 * 轮播图添加图片的
	 */
	private void setAd() {
		setAdImgSize(adpage, 0, 0.5f, 1);
		ArrayList<View> views = new ArrayList<View>();
		View view1 = null;
		for (final HashMap<String, String> map : hData) {
			view1 = View.inflate(mContext, R.layout.gallery_list_item, null);
			ImageView imageView = (ImageView) view1.findViewById(R.id.img);
			setImgSize(imageView, 0, 0.5f, 1);
			setImageView_(imageView, map.get("head_img"));
			setImageView_(imageView, map.get("ad_image"));
			views.add(view1);
		}
		adApter = new ViewPageAdapter(views);
		adpage.setAdapter(adApter);
		adpage.setOnSingleTouchListener(new OnSingleTouchListener() {
			@Override
			public void onSingleTouch() {
				HashMap<String, String> map = hData.get(current % hData.size());
				if (!StringUtil.isEmpty(map.get("ad_url")) && map.get("ad_url").contains("/share/video_info?")) {
					String str = map.get("ad_url");
					if (!StringUtil.isEmpty(str)) {
						Logger.i("广告位：" + map.get("ad_url"));
						Intent intent = new Intent(mContext, VideoDetailActivity.class);
						intent.putExtra("video_id", map.get("ad_url").split("=")[1]);
						((Activity) mContext).startActivity(intent);
					}
				} else if (!StringUtil.isEmpty(map.get("ad_url")) && map.get("ad_url").contains("/share/video_list?")) {
					String str = map.get("ad_url");
					if (!StringUtil.isEmpty(str)) {
						Logger.i("广告位：" + map.get("ad_url"));
						Intent intent = new Intent(mContext, RecommendedVideoListActivity.class);
						intent.putExtra("video_id", map.get("ad_url").split("=")[1]);
						intent.putExtra("video_name", map.get("ad_text"));
						((Activity) mContext).startActivity(intent);
					}

				} else {
					try {
						Intent intent = new Intent(mContext, AdWebActivity.class);
						intent.putExtra("url", map.get("ad_url"));
						intent.putExtra("title", map.get("ad_text"));
						mContext.startActivity(intent);
						Logger.i(map.get("ad_url"));
					} catch (Exception e) {
						Logger.i(e.toString());
						Toast.makeText(mContext, "广告地址：" + map.get("ad_url"), Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	// -----------------------------------------------------------------------

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

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.discover_item_item_img:
			Map<String, String> map = mAdData.get((Integer) v.getTag(R.id.discover_item_item_img_tag2)).get(
					(Integer) v.getTag(R.id.discover_item_item_img_tag1));
			intent = new Intent(mContext, VideoDetailActivity.class);
			intent.putExtra("video_id", map.get("id"));
			intent.putExtra("nickname", map.get("nickname"));
			intent.putExtra("create_time", map.get("create_time"));
			intent.putExtra("head_img", map.get("head_img"));
			break;
		case R.id.discover_item_layout:
			Map<String, String> map2 = null;
			if (mData.size() > 0) {
				map2 = mData.get((Integer) v.getTag());
			}
			if (map2 != null) {
				Logger.i("  数据 ：" + map2.toString());
				if ("100".equals(map2.get("id"))) {
					intent = new Intent(mContext, EventVideoListView.class);
					intent.putExtra("video_name", map2.get("ch_name"));
				} else {
					intent = new Intent(mContext, RecommendedVideoListActivity.class);
					intent.putExtra("video_id", map2.get("id"));
					intent.putExtra("video_name", map2.get("ch_name"));
				}
			}

			break;

		case R.id.order: // 跳到订购界面。

			break;
		default:
			break;

		}
		if (intent != null) {
			mContext.startActivity(intent);
		}

	}

	protected String getUrl(String url) {
		return AppConfig.GetUrl(mContext, url);
	}
}
