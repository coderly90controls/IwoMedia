package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/*
 * "id" ,"name"，"syte"   不为null是more进来
 * */
public class ListBrandActivity extends BaseActivity {
	private String id;
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private IwoAdapter adapter;
	private ListView listview;
	private LinearLayout bottom;
	private boolean isone = true;// 是不是第一次请求
	private boolean isData;// 是付有数据
	private String syte;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brand_list);
		String title_name = getIntent().getStringExtra("name");
		id = getIntent().getStringExtra("id");
		syte = getIntent().getStringExtra("syte");
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		new GetData().execute(id);
		setTitle(title_name);
		init();
	}

	private void init() {
		setBack(null);
		listview = (ListView) findViewById(R.id.listview);
		listview.setDivider(null);// 去掉线
		bottom = (LinearLayout) findViewById(R.id.syte_jiazai_gengduo);
		adapter = new IwoAdapter(ListBrandActivity.this, mData) {

			private ViewHolder holder;

			@Override
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null) {
					holder = new ViewHolder();
					v = View.inflate(mContext,
							R.layout.layout_recmmended_video_list_item, null);
					holder.img = (ImageView) v.findViewById(R.id.item_img);
					holder.title_name = (TextView) v.findViewById(R.id.des);
					holder.des = (TextView) v
							.findViewById(R.id.video_title_name);
					holder.synopsis = (TextView) v.findViewById(R.id.synopsis);
					v.setTag(holder);
				} else {
					holder = (ViewHolder) v.getTag();
				}
				holder.synopsis.setVisibility(View.GONE);
				if (mData != null && mData.size() > 0) {
					final HashMap<String, String> map = mData.get(position);
					LoadBitmap.getIntence().loadImage(map.get("img_url_2"),
							holder.img);
					holder.title_name.setVisibility(View.GONE);
					holder.des.setVisibility(View.VISIBLE);
					holder.des.setText(map.get("name"));
					holder.img.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									VideoDetailActivity_new.class);
							intent.putExtra("video_id", map.get("id"));
							intent.putExtra("ch_name", map.get("name"));
							intent.putExtra("edit_id",id);
							mContext.startActivity(intent);

						}
					});
				}

				return v;
			}

		};
		listview.setAdapter(adapter);
		listview.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						int size = mData.size();
						if (size % 10 == 0 && size > 0) {
							// bottom.setVisibility(View.VISIBLE);
							if (isData) {
								isone = false;
								new GetData().execute(id);
							} else {
								makeText("没有更多内容");
							}
						} else {
							makeText("没有更多内容");
						}

					}
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
	}

	private class ViewHolder {
		ImageView img;
		TextView title_name, synopsis, des;

	}

	private class GetData extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			String id_page = getIntent().getStringExtra("id_page");
			if (StringUtil.isEmpty(syte)) {
				if (isone) {
					Logger.i("第一次加载");
					return DataRequest.getArrayListFromUrl_Base64(
							getUrl(AppConfig.NEW_V_GET_VIDEO_LIST_BRAND_DE),
							params[0], "1", "10");
				}
				return DataRequest.getArrayListFromUrl_Base64(
						getUrl(AppConfig.NEW_V_GET_VIDEO_LIST_BRAND_DE),
						params[0], getStart(mData.size()), "10");
			}
			if (isone) {
				Logger.i("第一次加载");

				return DataRequest.getArrayListFromUrl_Base64(
						getUrl(AppConfig.NEW_V_GET_VIDEO_LIST_BRAND_DE_MORE),
						id_page, "0", "10", params[0]);
			}
			return DataRequest.getArrayListFromUrl_Base64(
					getUrl(AppConfig.NEW_V_GET_VIDEO_LIST_BRAND_DE_MORE),
					id_page, getStart(mData.size()), "10", params[0]);
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			super.onPostExecute(result);
			mLoadBar.dismiss();
			if (result != null) {
				isData = true;
				Logger.i("品牌列表：" + result.toString());
				if (isone) {
					mData.clear();
				}
				mData.addAll(result);
				adapter.notifyDataSetChanged();
			} else {
				isData = false;
				if (isone) {
					makeText("连接服务器失败");
				}
			}
		}

	}

}
