package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.Constants;
import com.android.iwo.media.apk.activity.R;
import com.android.iwo.media.view.ChildViewPager;
import com.android.iwo.media.view.DataResult_new;
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
import com.test.iwomag.android.pubblico.util.StringUtil;

public class MoreActivity extends BaseActivity {
	private TreeMap<String, String> m_DataA = new TreeMap<String, String>();
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
		setBack(null);
		mHead = View.inflate(mContext, R.layout.list_head, null);
		scrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		head_gallery = (RelativeLayout) mHead.findViewById(R.id.head_gallery);
		head_gallery.setVisibility(View.VISIBLE);
		adpage = (ChildViewPager) mHead.findViewById(R.id.gallery);
		view_content = (LinearLayout) findViewById(R.id.view_content);
		view_content.addView(mHead);

		// String hDataString =
		// mCache.getAsString(Constants.VIDEO_ADVERTISING_NEW);
		// if (!StringUtil.isEmpty(hDataString)) {
		// ArrayList<HashMap<String, String>> list =
		// DataRequest.getArrayListFromJSONArrayString(hDataString);
		// if (list != null)
		// hData.addAll(list);
		// initTopGallery(hData);
		// }
		if (NetworkUtil.isWIFIConnected(mContext)) {
			new GetHData().execute(); // 获取 头部轮播图信息，
			new GetData().execute(id);
		}
		scrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				if (NetworkUtil.isWIFIConnected(mContext)) {
					mCache.clear();
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

	// 获取在线数据
	protected class GetData extends
			AsyncTask<String, Void, TreeMap<String, String>> {

		protected TreeMap<String, String> doInBackground(String... params) {
			// String
			// uriString="192.168.0.136：8086/share/check_validate?type=get_brand_channel_video&ch_id={0}";
			return DataResult_new.getTreeMapFromUrl_Base64(
					getUrl(AppConfig.NEW_V_GET_VIDEO_CHANNEL_BRAND_MORE),
					params[0]);
			// return DataResult_new.getTreeMapFromUrl_Base64(uriString,
			// params[0]);

		}

		@Override
		protected void onPostExecute(TreeMap<String, String> result) {
			mLoadBar.dismiss();
			scrollView.onRefreshComplete();
			if (result != null) {
				Logger.i("品牌全部数据：" + result);
				m_DataA.clear();
				m_DataA.putAll(result);
				setView(m_DataA);
			}
		}
	}

	public void setView(TreeMap<String, String> list) {
		if (list == null || list.size() == 0)
			return;
		Logger.i("品牌数据：" + list.toString());
		view_content.removeAllViews();
		if (!isHead) {
			view_content.addView(mHead);
		}
		Set<String> keySet = list.keySet();
		for (String key : keySet) {
			View view = View.inflate(mContext, R.layout.activity_discover_item,
					null);
			// view.findViewById(R.id.discover_item_more).setVisibility(View.GONE);
			// TextView textView = (TextView)
			// view.findViewById(R.id.discover_item_text);
			// TextView textView_is = (TextView)
			// view.findViewById(R.id.discover_item_text_is);
			ImageView discover_item_img = (ImageView) view
					.findViewById(R.id.discover_item_img);
			TextView text = (TextView) view
					.findViewById(R.id.discover_item_text_is);
			final String[] split = key.split(",");
			final String id_list = split[0];// key ID
			if (split.length == 2) {
				text.setVisibility(View.VISIBLE);
				text.setText(split[1]);
			}
			if (split.length == 3) {
				if (!StringUtil.isEmpty(split[3])) {
					text.setVisibility(View.GONE);
					discover_item_img.setVisibility(View.VISIBLE);
					LoadBitmap.getIntence().loadImage(split[3],
							discover_item_img);
				}
			}
			View discover_item_layout = view
					.findViewById(R.id.discover_item_layout);
			discover_item_layout.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(mContext,
							ListBrandActivity.class);
					intent.putExtra("id", id_list);
					intent.putExtra("id_page", id);
					intent.putExtra("name", split[1]);
					intent.putExtra("syte", "1");
					startActivity(intent);
				}
			});
			HorizontalListView hListView = (HorizontalListView) view
					.findViewById(R.id.discover_item_horizontal);
			hListView.setVisibility(View.VISIBLE);
			final ArrayList<HashMap<String, String>> adData = DataRequest
					.getArrayListFromJSONArrayString(list.get(key));
			if (adData == null || adData.size() == 0)
				continue;
			IwoAdapter adapter = new IwoAdapter((Activity) mContext, adData) {
				@SuppressLint("NewApi")
				@Override
				public View getView(int position, View v, ViewGroup parent) {
					v = mInflater.inflate(
							R.layout.activity_discover_item_item_main, null);
					Map<String, String> map = adData.get(position);
					TextView pinfen = (TextView) v
							.findViewById(R.id.discover_item_item_pinfen);
					if (!StringUtil.isEmpty(map.get("ping_fen"))) {
						pinfen.setVisibility(View.VISIBLE);
						pinfen.getBackground().setAlpha(90);
						pinfen.setText(map.get("ping_fen"));
					} else {
						pinfen.setVisibility(View.GONE);
					}
					LoadBitmap bitmap = new LoadBitmap();
					ImageView imgImageView = (ImageView) v
							.findViewById(R.id.discover_item_item_img);
					if (!StringUtil.isEmpty(map.get("img_url"))) {
						bitmap.loadImage(map.get("img_url"), imgImageView);
					} else {
						imgImageView.setBackground(mContext.getResources()
								.getDrawable(R.drawable.pp_280_170));
					}
					setImgSize(imgImageView, 40, 1.0f, 3, true);
					TextView text = (TextView) v
							.findViewById(R.id.discover_item_item_text);
					text.setText(map.get("name"));
					imgImageView.setTag(R.id.discover_item_item_img_tag1,
							position);

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
			hListView.setAdapter(adapter);
			hListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Map<String, String> map = adData.get(position);
					Intent intent = null;
					intent = new Intent(mContext, VideoDetailActivity_new.class);
					intent.putExtra("video_id", map.get("id"));
					intent.putExtra("nickname", map.get("nickname"));
					intent.putExtra("create_time", map.get("create_time"));
					intent.putExtra("head_img", map.get("head_img"));
					intent.putExtra("ch_name", map.get("name"));

					mContext.startActivity(intent);

				}

			});
			view_content.addView(view);
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
			Logger.i("轮暴图缓存品11111："
					+ mCache.getAsString(Constants.VIDEO_ADVERTISING_NEW));
			if (StringUtil.isEmpty(str)
					&& StringUtil.isEmpty(mCache
							.getAsString(Constants.VIDEO_ADVERTISING_NEW))) {
				Logger.i("删掉头部");
				isHead = true;
			}
			if (StringUtil.isEmpty(mCache
					.getAsString(Constants.VIDEO_ADVERTISING_NEW))) {
				// mCache.put(Constants.VIDEO_ADVERTISING_NEW, str);
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
			} else {
				if (!StringUtil.isEmpty(str)) {
					// mCache.put(Constants.VIDEO_ADVERTISING_NEW, str);
				}
			}
		}
	}

	private int current = 0;
	private String id;

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
