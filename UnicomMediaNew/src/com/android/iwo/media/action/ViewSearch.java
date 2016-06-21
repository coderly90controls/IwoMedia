package com.android.iwo.media.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.iwo.media.activity.AdWebActivity;
import com.android.iwo.media.activity.RecommendedVideoListActivity;
import com.android.iwo.media.activity.VideoDetailActivity;
import com.android.iwo.media.activity.newPasswordActivity;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.view.HorizontalListView;
import com.android.iwo.media.view.IwoToast;
import com.android.iwo.media.view.XListView;
import com.android.iwo.media.view.XListView.IXListViewListener;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.ACache;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.DateUtil;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class ViewSearch extends ViewTab {

	protected CommonDialog mLoadBar;
	private ACache mCache;
	private HorizontalListView discover_item_horizontal;
	ArrayList<HashMap<String, String>> adapterData = new ArrayList<HashMap<String, String>>();

	public ViewSearch(Context context) {
		mContext = context;
		view = View.inflate(mContext, R.layout.view_search_layout, null);
		discover_item_horizontal = (HorizontalListView) view.findViewById(R.id.discover_item_horizontal);
		mCache = ACache.get(mContext);
		new GetData().execute();
	}

	@Override
	protected void init() {
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		mAdapter = new IwoAdapter((Activity) mContext, adapterData) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				View adapterView = null;
				if (v == null) {
					adapterView = mInflater.inflate(R.layout.activity_discover_item_item, null);
				} else {
					adapterView = v;
				}
				Map<String, String> map = adapterData.get(position);
				Logger.i("MAP 中的 搜索内容数据：" + map);
				LoadBitmap bitmap = new LoadBitmap();
				ImageView imgImageView = (ImageView) adapterView.findViewById(R.id.discover_item_item_img);
				bitmap.loadImage(map.get("img_url"), imgImageView);
				setImgSize(imgImageView, 0, 173 / 300.0f, 2.3f);
				TextView text = (TextView) adapterView.findViewById(R.id.discover_item_item_text);
				text.setText(map.get("name"));
				imgImageView.setTag(position);
				return adapterView;

			}
		};
		discover_item_horizontal.setAdapter(mAdapter);
		discover_item_horizontal.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map<String, String> map = adapterData.get(position);
				Intent intent = new Intent(mContext, VideoDetailActivity.class);
				intent.putExtra("video_id", map.get("id"));
				intent.putExtra("nickname", map.get("nickname"));
				intent.putExtra("create_time", map.get("create_time"));
				intent.putExtra("head_img", map.get("head_img"));
				mContext.startActivity(intent);

			}

		});
		mAdapter.notifyDataSetChanged();

		view.findViewById(R.id.search_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText search_edit = (EditText) view.findViewById(R.id.search_edit);
				String searchString = search_edit.getText().toString();

				if (!StringUtil.isEmpty(searchString)) {
					Intent intent = new Intent(mContext, RecommendedVideoListActivity.class);
					intent.putExtra("search_string", searchString);
					intent.putExtra("video_name", searchString);
					mContext.startActivity(intent);
				} else {
					IwoToast.makeText(mContext, "搜索内容不可为空").show();
				}
			}
		});
		// 搜索列表。
		view.findViewById(R.id.discover_item_layout).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, RecommendedVideoListActivity.class);
				intent.putExtra("video_id", "200");
				intent.putExtra("video_name", "搜索");
				mContext.startActivity(intent);
			}
		});

	}

	@Override
	protected ArrayList<HashMap<String, String>> doInBack(String... params) {
		String str = "";
		// if ("day".equals(getMode())) {
		// 白天模式访问的接口
		str = DataRequest.getStringFromURL_Base64(getUrl(AppConfig.NEW_UN_GET_CHANNEL_VIDEO), "200", "1", "20");
		if (!StringUtil.isEmpty(str)) {
			ArrayList<HashMap<String, String>> list = DataRequest.getArrayListFromJSONArrayString(str);
			Logger.i("搜索内容数据：" + list);
			if (list != null) {
				mCache.put(Constants.ME_SEARCH_LIST_VIDEO, str);
				return list;
			}

		}

		return null;

	}

	@Override
	protected void onPostExe(ArrayList<HashMap<String, String>> result) {
		String search_list_video = mCache.getAsString(Constants.ME_SEARCH_LIST_VIDEO);
		if (result != null) {
			adapterData.addAll(result);
		} else if (!StringUtil.isEmpty(search_list_video)) {
			adapterData.addAll(DataRequest.getArrayListFromJSONArrayString(search_list_video));
		}
		init();
	}
}
