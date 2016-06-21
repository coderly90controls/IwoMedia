package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.Constants;
import com.android.iwo.media.apk.activity.*;
import com.android.iwo.media.view.HorizontalListView;
import com.android.iwo.media.view.IwoToast;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.ACache;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.FileCache;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchActivity extends BaseActivity {
	private ACache mCache;
	private HorizontalListView discover_item_horizontal;
	ArrayList<HashMap<String, String>> adapterData = new ArrayList<HashMap<String, String>>();
	protected IwoAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_search_layout);
		setBack(null);
		findViewById(R.id.title_img).setVisibility(View.VISIBLE);
		discover_item_horizontal = (HorizontalListView) findViewById(R.id.discover_item_horizontal);
		mCache = ACache.get(mContext);
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		new getPG().execute();
		new GetData().execute();
		// new GetData_jptj().execute();
		init();
	}

	protected void init() {
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		mAdapter = new IwoAdapter((Activity) mContext, adapterData) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				View adapterView = null;
				if (v == null) {
					adapterView = mInflater.inflate(
							R.layout.activity_discover_item_item, null);
				} else {
					adapterView = v;
				}
				Map<String, String> map = adapterData.get(position);
				Logger.i("MAP 中的 搜索内容数据：" + map);
				ImageView imgImageView = (ImageView) adapterView
						.findViewById(R.id.discover_item_item_img);
				LoadBitmap.getIntence().loadImage(map.get("img_url"),
						imgImageView);
				// setImgSize(imgImageView, 0, 270 / 300f, 2.25f);
				setImgSize(imgImageView, 40, 1.0f, 3, true);
				TextView text = (TextView) adapterView
						.findViewById(R.id.discover_item_item_text);
				text.setText(map.get("name"));
				imgImageView.setTag(position);
				return adapterView;

			}
		};
		discover_item_horizontal.setAdapter(mAdapter);

		discover_item_horizontal
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						HashMap<String, String> map = adapterData.get(position);
						Intent intent = new Intent(SearchActivity.this,
								VideoDetailActivity_new.class);
						intent.putExtra("edit_id","122");//fcz
						intent.putExtra("video_id", map.get("id"));
						intent.putExtra("ch_name", "热门搜索");
						startActivity(intent);
					}
				});
		mAdapter.notifyDataSetChanged();

		findViewById(R.id.search_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						EditText search_edit = (EditText) findViewById(R.id.search_edit);
						String searchString = search_edit.getText().toString();

						if (!StringUtil.isEmpty(searchString)) {
							// 跳到新写的搜索结果类
							// Intent intent = new Intent(mContext,
							// RecommendedVideoListActivity.class);
							Intent intent = new Intent(mContext,
									SearchResultActivity.class);
							intent.putExtra("search_string", searchString);
							intent.putExtra("video_name", searchString);
							mContext.startActivity(intent);
						} else {
							IwoToast.makeText(mContext, "搜索内容不能为空").show();
						}
					}
				});
		// 搜索列表。
		// findViewById(R.id.discover_item_layout).setOnClickListener(new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(mContext,
		// RecommendedVideoListActivity.class);
		// intent.putExtra("video_id", "215");
		// intent.putExtra("video_name", "热门搜索");
		// mContext.startActivity(intent);
		// }
		// });

	}

	private class GetData extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Void... params) {
			String str = "";
			// if ("day".equals(getMode())) {
			// 白天模式访问的接口
			str = DataRequest
					.getStringFromURL_Base64(
							getUrl(AppConfig.NEW_UN_GET_CHANNEL_VIDEO), "122",
							"1", "10");
			if (!StringUtil.isEmpty(str)) {
				ArrayList<HashMap<String, String>> list = DataRequest
						.getArrayListFromJSONArrayString(str);
				Logger.i("搜索内容数据：" + list);
				if (list != null) {
					mCache.put(Constants.ME_SEARCH_LIST_VIDEO, str);
					return list;
				}

			}

			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			String search_list_video = mCache
					.getAsString(Constants.ME_SEARCH_LIST_VIDEO);
			mLoadBar.dismiss();
			if (result != null) {
				adapterData.addAll(result);
			} else if (!StringUtil.isEmpty(search_list_video)) {
				adapterData.addAll(DataRequest
						.getArrayListFromJSONArrayString(search_list_video));
			}
			init();
		}
	}

	protected int setImgSize(ImageView item, int del, float size, float n) {
		int width = (int) ((dm.widthPixels - (int) (del * scale + 0.5f)) / n);
		android.view.ViewGroup.LayoutParams params = item.getLayoutParams();
		params.height = (int) (width * size);
		params.width = width;
		return params.height;
	}

	// 获取精品列表信息
	private class GetData_jptj extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			String str = DataRequest.getStringFromURL_Base64(
					getUrl(AppConfig.NEW_UN_GET_CHANNEL_VIDEO), "216", "1",
					"10");
			Logger.i("精品推荐数据：" + str);
			if (!StringUtil.isEmpty(str)) {
				ArrayList<HashMap<String, String>> list = DataRequest
						.getArrayListFromJSONArrayString(str);
				if (list != null) {
					mCache.put(Constants.ME_SEARCH_LIST_VIDEO_JPTJ, str);
					return list;
				}
			}
			return null;
		}

		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			super.onPostExecute(result);
			if (result != null) {
				initView(result);
			}
		}

	}

	public void initView(ArrayList<HashMap<String, String>> result) {
		if (result == null || result.size() == 0) {
			Logger.i("没获取到精品视频信息");
			return;
		}
		// 精品列表
		// findViewById(R.id.m_discover_item_layout).setOnClickListener(new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(mContext,
		// RecommendedVideoListActivity.class);
		// intent.putExtra("video_id", "216");
		// intent.putExtra("video_name", "精品搜索");
		// mContext.startActivity(intent);
		//
		// }
		// });
		ImageView search_renmen_1 = (ImageView) findViewById(R.id.search_renmen_1);
		ImageView search_renmen_2 = (ImageView) findViewById(R.id.search_renmen_2);
		ImageView search_renmen_3 = (ImageView) findViewById(R.id.search_renmen_3);
		if (result.size() >= 3) {
			// getImager(result, 0, search_renmen_1,R.drawable.a304_384);
			// getImager(result, 1, search_renmen_2,R.drawable.a266_192);
			// getImager(result, 2, search_renmen_3,R.drawable.a266_192);

			getIma_new(result, 0, search_renmen_1, R.drawable.a304_384);
			getIma_new(result, 1, search_renmen_2, R.drawable.a266_192);
			getIma_new(result, 2, search_renmen_3, R.drawable.a266_192);
			OnClick(search_renmen_1, result, 0);
			OnClick(search_renmen_2, result, 1);
			OnClick(search_renmen_3, result, 2);
		}

	}

	@SuppressLint("NewApi")
	private void getIma_new(ArrayList<HashMap<String, String>> result, int i,
			ImageView se, int id_n) {
		if (result.size() < 3) {
			return;
		}
		String string = result.get(i).get("img_play_url");
		if (!StringUtil.isEmpty(string)) {
			LoadBitmap.getIntence().loadImage(string, se);
		} else {
			se.setBackground(getResources().getDrawable(id_n));
		}

	}

	// 点击事件
	private void OnClick(ImageView search,
			final ArrayList<HashMap<String, String>> re, final int i) {
		search.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (re.size() < 3) {
					return;
				}
				Intent intent = new Intent(SearchActivity.this,
						VideoDetailActivity_new.class);
				intent.putExtra("video_id", re.get(i).get("id"));
				intent.putExtra("ch_name", "精品推荐");
				startActivity(intent);
			}
		});

	}

	@SuppressLint("NewApi")
	private void getImager(ArrayList<HashMap<String, String>> result, int i,
			final ImageView se, final int id) {
		final String uri = result.get(i).get("img_play_url");// 取中图
		if (!StringUtil.isEmpty(uri)) {
			Bitmap bitmap = FileCache.getInstance().onGetBitmap(uri);
			if (bitmap != null) {
				Drawable drawable = new BitmapDrawable(bitmap);
				Logger.i("drawable数据" + drawable.toString());
				if (drawable != null) {
					se.setBackground(drawable);
				}
			} else {
				new Handler().postAtTime(new Runnable() {

					@Override
					public void run() {
						Bitmap bitmap = FileCache.getInstance()
								.onGetBitmap(uri);
						if (bitmap != null) {
							Drawable drawable = new BitmapDrawable(bitmap);
							Logger.i("drawable数据" + drawable.toString());
							if (drawable != null) {
								se.setBackground(drawable);
							}
						} else {
							new Handler().postAtTime(new Runnable() {

								@Override
								public void run() {
									Bitmap bitmap = FileCache.getInstance()
											.onGetBitmap(uri);
									if (bitmap != null) {
										Drawable drawable = new BitmapDrawable(
												bitmap);
										Logger.i("drawable数据"
												+ drawable.toString());
										if (drawable != null) {
											se.setBackground(drawable);
										}
									} else {
										se.setBackground(getResources()
												.getDrawable(id));
									}
								}
							}, 30 * 1000);
						}
					}
				}, 10 * 1000);
			}
		} else {
			se.setBackground(getResources().getDrawable(id));
		}
	}

	private class getPG extends
			AsyncTask<String, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			// TODO Auto-generated method stub
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_V_GET_VIDEO_CHANNEL), "6");
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			super.onPostExecute(result);

			if (result != null && "1".equals(result.get("code"))) {
				ArrayList<HashMap<String, String>> list = DataRequest
						.getArrayListFromJSONArrayString(result.get("data"));
				ArrayList<HashMap<String, String>> re = new ArrayList<HashMap<String, String>>();
				for (int i = 0; i < list.size(); i++) {
					if ("122".equals(list.get(i).get("id"))
							|| "216".equals(list.get(i).get("id"))) {
						re.add(list.get(i));
					}
				}
				setView(re);
			}
		}

	}

	public void setView(ArrayList<HashMap<String, String>> list) {
		TextView discover_item_text = (TextView) findViewById(R.id.discover_item_text);
		TextView discover_item_text_1 = (TextView) findViewById(R.id.discover_item_text_1);
		ImageView discover_item_img = (ImageView) findViewById(R.id.discover_item_img);
		ImageView discover_item_img_1 = (ImageView) findViewById(R.id.discover_item_img_1);

		setItem_new(discover_item_text, discover_item_img, 0, list);

	}

	private void setItem_new(TextView text, ImageView img, int i,
			ArrayList<HashMap<String, String>> list) {
		if (list.size() >= 1) {
			if (!StringUtil.isEmpty(list.get(i).get("ch_logo"))) {
				Logger.i("111");
				text.setVisibility(View.GONE);
				img.setVisibility(View.VISIBLE);
				LoadBitmap.getIntence().loadImage(list.get(i).get("ch_logo"),
						img);
			} else {
				Logger.i("222");
				text.setVisibility(View.VISIBLE);
				img.setVisibility(View.GONE);
				if (!StringUtil.isEmpty(list.get(i).get("ch_name"))) {
					Logger.i("333");
					text.setText(list.get(i).get("ch_name"));
				}
			}
		}

	}

}
