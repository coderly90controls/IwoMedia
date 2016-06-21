package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
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

import com.android.iwo.media.action.ActivityUtil;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.Constants;
import com.android.iwo.media.activity.EventVideoListView.GetData;
import com.android.iwo.media.apk.activity.*;
import com.android.iwo.media.view.ChildViewPager;
import com.android.iwo.media.view.IwoToast;
import com.android.iwo.media.view.ChildViewPager.OnSingleTouchListener;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.adapter.ViewPageAdapter;
import com.test.iwomag.android.pubblico.util.ACache;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class RecommendedVideoListActivity extends BaseActivity implements
		OnClickListener {

	ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	protected ListView listView;
	protected IwoAdapter mAdapter;
	private String video_id, search_string;
	private ACache mCache;
	private boolean isCache = true;
	private ArrayList<HashMap<String, String>> hData = new ArrayList<HashMap<String, String>>();
	private View mHead;
	private RelativeLayout head_gallery;
	private ChildViewPager adpage;
	protected ViewPageAdapter adApter;
	private boolean scrollOn = false, dataResult = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singleton_listview);
		mCache = ACache.get(mContext);
		search_string = getIntent().getStringExtra("search_string");
		if (StringUtil.isEmpty(search_string)) {
			video_id = getIntent().getStringExtra("video_id");
		} else {
			video_id = "search";
		}

		// mCache.put(Constants.RECOMMENDED_VIDEO_LIST+video_id);
		// 设置intent传过来的数据video_name为标题
		// setTitle(getIntent().getStringExtra("video_name"));
		findViewById(R.id.title_img).setVisibility(View.VISIBLE);
		listView = (ListView) findViewById(R.id.singleton_list);

		setBack(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
		findViewById(R.id.syte_jiazai_gengduo).setVisibility(View.GONE);
		init();
		new GetData().execute(video_id);
	}

	static class ViewHolder {
		TextView name, synopsis, comments_text, share_text, play_text, des;
		ImageView comments, play, share, head, item_img;
	}

	private void init() {

		mHead = View.inflate(mContext, R.layout.list_head, null);
		head_gallery = (RelativeLayout) mHead.findViewById(R.id.head_gallery);
		head_gallery.setVisibility(View.VISIBLE);
		adpage = (ChildViewPager) mHead.findViewById(R.id.gallery);
		// listView.addHeaderView(mHead);
		listView.setDividerHeight(0);
		listView.setSelector(new ColorDrawable(0x00000000));
		String hDataString = mCache.getAsString(Constants.VIDEO_ADVERTISING
				+ video_id);
		if (!StringUtil.isEmpty(hDataString)) {
			ArrayList<HashMap<String, String>> list = DataRequest
					.getArrayListFromJSONArrayString(hDataString);
			if (list != null)
				hData.addAll(list);
			// initTopGallery(hData);
		}

		// new GetHData().execute(); // 获取 头部轮播图信息，
		String cache = mCache.getAsString(Constants.RECOMMENDED_VIDEO_LIST
				+ video_id);
		Logger.i("cache-" + cache);
		if (!StringUtil.isEmpty(cache)) {
			mData = DataRequest.getArrayListFromJSONArrayString(cache);
			if (mData == null)
				mData = new ArrayList<HashMap<String, String>>();
			isCache = true;
		} else {
			mLoadBar.setMessage("数据加载中...");
			mLoadBar.show();
		}
		mAdapter = new IwoAdapter((Activity) mContext, mData) {

			int[] d_img = { R.drawable.d_pinglun, R.drawable.d_bofang,
					R.drawable.d_fenxiang };

			@Override
			public View getView(int position, View v, ViewGroup parent) {

				View view = v;
				final ViewHolder holder;
				if (view == null) {
					view = mInflater.inflate(
							R.layout.layout_recmmended_video_list_item, null);
					holder = new ViewHolder();

					holder.name = (TextView) view.findViewById(R.id.name);
					holder.synopsis = (TextView) view
							.findViewById(R.id.synopsis);
					holder.comments_text = (TextView) view
							.findViewById(R.id.comments_text);
					holder.share_text = (TextView) view
							.findViewById(R.id.share_text);
					holder.play_text = (TextView) view
							.findViewById(R.id.play_text);
					holder.des = (TextView) view.findViewById(R.id.des);

					holder.head = (ImageView) view.findViewById(R.id.head);
					holder.item_img = (ImageView) view
							.findViewById(R.id.item_img);
					holder.comments = (ImageView) view
							.findViewById(R.id.comments);
					holder.play = (ImageView) view.findViewById(R.id.play);
					holder.share = (ImageView) view.findViewById(R.id.share);

					view.setTag(holder);
				} else {
					holder = (ViewHolder) view.getTag();
				}

				ArrayList<ImageView> list = new ArrayList<ImageView>();
				list.add(holder.comments);
				list.add(holder.play);
				list.add(holder.share);
				setItemViewImg(list, d_img);

				Map<String, String> map = mData.get(position);
				LoadBitmap bitmap = new LoadBitmap();
				if (!StringUtil.isEmpty(map.get("head_img"))) {
					holder.head.setTag(position);
					bitmap.loadImage(
							(String) mData.get((Integer) holder.head.getTag())
									.get("head_img"), holder.head);
				} else {
					holder.head.setBackgroundColor(getResources().getColor(
							R.color.comm_green_color));
					holder.head.setImageResource(R.drawable.xiaomishubai);
				}

				// if (!StringUtil.isEmpty(map.get("nickname"))) {
				// holder.name.setTag(map.get("nickname"));
				// ///setItem(holder.name, (String) holder.name.getTag());
				// } else {
				// //setItem(holder.name, "爱握小秘书");
				// }
				if ("day".equals(getMode())) {
					if ("1".equals(map.get("is_user"))) {
						holder.head.setTag(position);
						holder.head
								.setOnClickListener(RecommendedVideoListActivity.this);
					}
				}

				// 获取标题置为空，使它不能获取标题
				setItem(holder.synopsis, map.get(""));
				setImgSize(holder.item_img, 14, 27 / 48.0f, 1);
				bitmap.loadImage(map.get("img_url_2"), holder.item_img);

				setItem(holder.comments_text, StringUtil.isEmpty(map
						.get("ping_count")) ? "0" : map.get("ping_count"));
				setItem(holder.share_text, StringUtil.isEmpty(map
						.get("share_count")) ? "0" : map.get("share_count"));
				setItem(holder.play_text, StringUtil.isEmpty(map
						.get("play_count")) ? "0" : map.get("play_count"));
				setItem(holder.des, map.get("name"));
				return view;
			}
		};
		listView.setCacheColorHint(0);
		listView.setDividerHeight(0);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, String> map = mData.get(position
						- listView.getHeaderViewsCount());

//				Intent intent = new Intent(mContext, VideoDetailActivity.class);
//				intent.putExtra("video_id", map.get("id"));
//				intent.putExtra("nickname", map.get("nickname"));
//				intent.putExtra("create_time", map.get("create_time"));
//				intent.putExtra("head_img", map.get("head_img"));
//				mContext.startActivity(intent);
			}
		});
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						View lastItemView = (View) listView.getChildAt(listView
								.getChildCount() - 1);
						if ((listView.getBottom()) == lastItemView.getBottom()
								+ 44 * dm.density) {
							if (scrollOn) {
								scrollOn = false;// 防止用户异步请求时，多次滑动，造成的多次重复加载数据。

								Logger.i("你执行了没有？");
								new GetData().execute(video_id);
							} else {
								if (mData.size() < 10) {
									IwoToast.makeText(mContext, "没有更多内容")
											.show();
								} else {
									if (dataResult) {
										IwoToast.makeText(mContext, "加载更多中")
												.show();
									} else {
										IwoToast.makeText(mContext, "没有更多内容")
												.show();
									}
								}
							}
						}
					}

				}
			}

			@Override
			public void onScroll(AbsListView view, int first, int visible,
					int total) {

				int lastItem = first + visible;
				if (lastItem == total) {

				}
			}
		});
	}

	private void setItemViewImg(List<ImageView> list, int[] d_img ) {
		int[] img;
		img = d_img;
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setImageResource(img[i]);
		}
	}

	/**
	 * 获取推荐视频列表
	 */
	protected class GetData extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			String data = null;
			if (isCache) {
				if ("search".equals(params[0])) {

					data = DataRequest.getStringFromURL_Base64(
							getUrl(AppConfig.NEW_UN_SEARCH_VIDEO_LIST),
							search_string, "1", "10");
				} else {
					data = DataRequest.getStringFromURL_Base64(
							getUrl(AppConfig.NEW_V_GET_VIDEO_LIST), params[0],
							"1", "10");
				}

				if (!StringUtil.isEmpty(data)) {
					mCache.put(Constants.RECOMMENDED_VIDEO_LIST + video_id,
							data);
				}
			} else {
				if ("search".equals(params[0])) {
					data = DataRequest.getStringFromURL_Base64(
							getUrl(AppConfig.NEW_UN_SEARCH_VIDEO_LIST),
							search_string, getStart(mData.size()), "10");
				} else {
					data = DataRequest.getStringFromURL_Base64(
							getUrl(AppConfig.NEW_V_GET_VIDEO_LIST), params[0],
							getStart(mData.size()), "10");
				}

			}
			return DataRequest.getArrayListFromJSONArrayString(data);
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			Logger.i("onPostExecute" + isCache + (result == null));
			if (null != result) {
				if (isCache) {
					isCache = false;
					mData.clear();
				}

				mData.addAll(result);
				mAdapter.notifyDataSetChanged();

				if (result.size() < 10) {
					scrollOn = false;
					dataResult = false;
				} else {
					scrollOn = true;
					dataResult = true;
				}
			} else {
				dataResult = false;
				if (isCache) {
					isCache = false;
					String recommended_video_list = mCache
							.getAsString(Constants.RECOMMENDED_VIDEO_LIST
									+ video_id);
					if (StringUtil.isEmpty(recommended_video_list)) {

					}
				}
			}

			mLoadBar.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		int position;
		Map<String, String> map = null;
		Intent intent = null;
		switch (v.getId()) {
		case R.id.head: // 用户头像
//			position = (Integer) v.getTag();
//			map = mData.get(position);
//			map.get("user_id");
//			intent = new Intent(mContext, FriendDetail.class);
//			intent.putExtra("video_id", map.get("user_id"));
//			mContext.startActivity(intent);
			break;
		case R.id.item_img: // 视频图片

			break;
		}
	}

	/**
	 * 获取轮播图数据
	 */
	protected class GetHData extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
		String str;

		@Override
		protected void onPreExecute() {
			String cache = mCache.getAsString(Constants.VIDEO_ADVERTISING
					+ video_id);
			if (!StringUtil.isEmpty(cache)) {
				hData.clear();
				hData.addAll(DataRequest.getArrayListFromJSONArrayString(cache));
				// initTopGallery(hData);
			}
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Void... params) {
			// str =
			// DataRequest.getStringFromURL_Base64(getUrl(AppConfig.NEW_V_GET_AD),
			// "video_list_" + video_id);
			str = DataRequest.getStringFromURL_Base64(
					getUrl(AppConfig.NEW_V_GET_AD), "video_share_list_"
							+ video_id);
			mCache.put(Constants.VIDEO_ADVERTISING + video_id, str);
			return DataRequest.getArrayListFromJSONArrayString(str);
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (null != result) {
				hData.clear();
				hData.addAll(result);
				// initTopGallery(hData);
				Logger.i("首页轮播图:" + result.toString());
			} else {
				listView.removeHeaderView(mHead);
			}
		}
	}

	private int current = 0;
	protected int num = 0;
	private boolean isrun = true;
	private Thread mThread = null;

	protected void initTopGallery(final ArrayList<HashMap<String, String>> data) {
		setAd();
		LinearLayout point = (LinearLayout) mHead
				.findViewById(R.id.tiao_daohang);
		point.removeAllViews();
		final ImageView indicator_iv[] = new ImageView[data.size()];
		for (int i = 0; i < data.size() && data.size() > 1; i++) {
			indicator_iv[i] = new ImageView(mContext);
			indicator_iv[i].setImageResource(R.drawable.souye_no_heidian);
			indicator_iv[i].setPadding(0, 0, 10, 0);
			point.addView(indicator_iv[i]);
		}
		if (data.size() > 1) {
			indicator_iv[0].setImageResource(R.drawable.souye_yes_huangdian);
		}

		adpage.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				current = position;
				num = position;
				for (int i = 0; i < data.size() && data.size() > 1; i++) {
					indicator_iv[i]
							.setImageResource(R.drawable.souye_no_heidian);
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
				// if (!StringUtil.isEmpty(map.get("ad_url")) &&
				// map.get("ad_url").contains("/v/info?")) {
				if (!StringUtil.isEmpty(map.get("ad_url"))
						&& map.get("ad_url").contains("/share/video_info?")) {
					String str = map.get("ad_url");
					if (!StringUtil.isEmpty(str)) {
						Logger.i("广告位：" + map.get("ad_url"));
//						Intent intent = new Intent(mContext,
//								VideoDetailActivity.class);
//						intent.putExtra("video_id", map.get("ad_url")
//								.split("=")[1]);
//						((Activity) mContext).startActivity(intent);
					}
					// } else if (!StringUtil.isEmpty(map.get("ad_url")) &&
					// map.get("ad_url").contains("/v/video_list?")) {
				} else if (!StringUtil.isEmpty(map.get("ad_url"))
						&& map.get("ad_url").contains("/share/video_list?")) {
					String str = map.get("ad_url");
					if (!StringUtil.isEmpty(str)) {
						Logger.i("广告位：" + map.get("ad_url"));
						Intent intent = new Intent(mContext,
								RecommendedVideoListActivity.class);
						intent.putExtra("video_id", map.get("ad_url")
								.split("=")[1]);
						intent.putExtra("video_name", map.get("ad_text"));
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

	@Override
	public void onBackPressed() {
		if (!StringUtil.isEmpty(getIntent().getStringExtra("push"))) {
			if (ActivityUtil.getInstance().isclose("ModelActivity")) {
				startActivity(new Intent(mContext, ModelActivity.class));
			}
		}
		super.onBackPressed();
	}
}
