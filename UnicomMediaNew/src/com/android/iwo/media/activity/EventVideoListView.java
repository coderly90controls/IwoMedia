package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.util.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.a.bm;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.Constants;
import com.android.iwo.media.activity.RecommendedVideoListActivity.GetData;
import com.android.iwo.media.apk.activity.*;
import com.android.iwo.media.view.IwoToast;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.ACache;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.DateUtil;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class EventVideoListView extends BaseActivity implements OnClickListener {

	ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	protected ListView listView;
	protected IwoAdapter mAdapter;
	private boolean scrollOn = false;
	ACache mCache;
	private boolean loading = true, loaded = false, dataResult = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_listview);
		mCache = ACache.get(mContext);
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		if (!StringUtil.isEmpty(mCache.getAsString(Constants.EVENT_VIDEO_LIST))) {
			new GetData2().execute();
		} else {
			new GetData().execute();
		}
		// mCache.put(Constants.RECOMMENDED_VIDEO_LIST+video_id);
		setTitle(getIntent().getStringExtra("video_name"));
		listView = (ListView) findViewById(R.id.singleton_list);

		setBack(null);
		init();
	}

	static class ViewHolder {
		TextView synopsis, action_des, action_info, action_time, action_more;
		ImageView item_img;
	}

	private void init() {

		mAdapter = new IwoAdapter((Activity) mContext, mData) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				// TODO Auto-generated method stub

				View view = v;
				final ViewHolder holder;
				if (view == null) {
					view = mInflater.inflate(
							R.layout.layout_event_video_list_item, null);
					holder = new ViewHolder();

					holder.synopsis = (TextView) view
							.findViewById(R.id.synopsis);
					holder.action_des = (TextView) view
							.findViewById(R.id.action_des);
					holder.action_info = (TextView) view
							.findViewById(R.id.action_info);
					holder.action_time = (TextView) view
							.findViewById(R.id.action_time);
					holder.action_more = (TextView) view
							.findViewById(R.id.action_more);

					holder.item_img = (ImageView) view
							.findViewById(R.id.item_img);
					view.setTag(holder);
				} else {
					holder = (ViewHolder) view.getTag();
				}
				final Map<String, String> map = mData.get(position);

				setItem(holder.synopsis, map.get("title"));
				setItem(holder.action_des, map.get("content"));
				setItem(holder.action_info, map.get("to_explain"));
				if (!StringUtil.isEmpty(map.get("start_time"))
						&& !StringUtil.isEmpty(map.get("end_time"))) {
					setItem(holder.action_time,
							DateUtil.format("yyyy-MM-dd HH:mm:ss",
									"yyyy年MM月dd日", map.get("start_time"))
									+ "--"
									+ DateUtil.format("yyyy-MM-dd HH:mm:ss",
											"yyyy年MM月dd日", map.get("end_time")));
				}

				setImgSize(holder.item_img, 14, 27 / 48.0f, 1);
				LoadBitmap.getIntence().loadImage(map.get("img_url"),
						holder.item_img);
				if (!StringUtil.isEmpty(map.get("url"))) {
					holder.action_more.setVisibility(View.VISIBLE);
					holder.action_more
							.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									Intent intent = new Intent(mContext,
											AdWebActivity.class);
									intent.putExtra("url", map.get("url"));
									intent.putExtra("title", map.get("title"));
									startActivity(intent);
								}
							});
				} else {
					holder.action_more.setVisibility(View.GONE);
				}
				return view;

			}
		};
		listView.setCacheColorHint(0);
		listView.setDividerHeight(0);
		listView.setSelector(new ColorDrawable(0x00000000));
		listView.setAdapter(mAdapter);
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
								loaded = false;
								Logger.i("你执行了没有？");
								new GetData().execute();
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
				Logger.i("lastItem -" + lastItem + "    :     total -" + total);
				if (lastItem == total) {

				}
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			Map<String, String> map = null;
			Intent intent = null;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				map = mData.get(position);
				intent = new Intent(mContext,
						RecommendedVideoListActivity.class);
				intent.putExtra("video_id", map.get("ch_id"));
				intent.putExtra("video_name", map.get("title"));
				mContext.startActivity(intent);

			}

		});
	}

	/**
	 * 获取推荐视频列表
	 * 
	 * @author hanpengyuan
	 * 
	 */
	protected class GetData extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			String mapData, data = "";
			if (loading) {
				mapData = DataRequest.getStringFromURL_Base64(
						getUrl(AppConfig.VIDEO_GET_TOPIC_LIST), "1", "10");
				if (!StringUtil.isEmpty(mapData)) {
					HashMap<String, String> map = DataRequest
							.getHashMapFromJSONObjectString(mapData);
					if (map != null) {
						data = map.get("data");
						if (!StringUtil.isEmpty(map.get("data"))) {
							mCache.put(Constants.EVENT_VIDEO_LIST,
									map.get("data"));
						}

					}
				}
				if (loaded) {
					loaded = false;
					return null;
				} else {
					return DataRequest.getArrayListFromJSONArrayString(data);
				}

			}
			data = DataRequest.getStringFromURL_Base64(
					getUrl(AppConfig.VIDEO_GET_TOPIC_LIST),
					getStart(mData.size()), "10");
			if (!StringUtil.isEmpty(data)) {
				HashMap<String, String> map = DataRequest
						.getHashMapFromJSONObjectString(data);
				if (map != null) {
					data = map.get("data");
					return DataRequest.getArrayListFromJSONArrayString(data);
				} else {
					return null;
				}

			} else {
				return null;
			}

		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (null != result) {
				mData.addAll(result);
				mAdapter.notifyDataSetChanged();
				findViewById(R.id.singleton_list).setVisibility(View.VISIBLE);
				if (result.size() < 10) {
					scrollOn = false;
					dataResult = false;
				} else {
					scrollOn = true;
					dataResult = true;
				}
			} else {

				dataResult = false;

			}
			loading = false;
			mLoadBar.dismiss();
		}
	}

	protected class GetData2 extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			String data = mCache.getAsString(Constants.EVENT_VIDEO_LIST);
			if (!StringUtil.isEmpty(data)) {
				return DataRequest.getArrayListFromJSONArrayString(data);
			} else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (null != result) {
				mData.addAll(result);
				mAdapter.notifyDataSetChanged();
				loaded = true;
			}
			scrollOn = true;
			new GetData().execute();
		}
	}

	@Override
	public void onClick(View v) {
		int position;
		switch (v.getId()) {
		case R.id.item_img: // 视频图片
			break;
		}
	}

}
