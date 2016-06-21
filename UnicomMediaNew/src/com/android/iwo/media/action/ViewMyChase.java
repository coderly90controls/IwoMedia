package com.android.iwo.media.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.iwo.media.activity.ListBrandActivity;
import com.android.iwo.media.activity.MeRecordActivity;
import com.android.iwo.media.apk.activity.R;
import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.view.XListView;
import com.android.iwo.media.view.XListView.IXListViewListener;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.NetworkUtil;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;

;

/**
 * 推荐收藏
 * 
 * @author 123
 * 
 */
public class ViewMyChase extends ViewTab {

	protected CommonDialog mLoadBar;

	public ViewMyChase(Context context) {
		mContext = context;
		view = View.inflate(mContext, R.layout.view_my_chase_layout, null);
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		mLoadBar = new CommonDialog(mContext);
		init();

	}

	static class ViewHolder {
		TextView synopsis, des, des_new;
		ImageView item_img;
	}

	@Override
	protected void init() {
		if (NetworkUtil.isWIFIConnected(mContext)) {
			new GetData_new().execute();
		}
		listView = (XListView) view
				.findViewById(R.id.view_my_chase_layout_list);
		mAdapter = new IwoAdapter((Activity) mContext, mData) {

			@Override
			public View getView(int position, View v, ViewGroup parent) {
				View view = v;
				final ViewHolder holder;
				if (view == null) {
					view = mInflater.inflate(
							R.layout.layout_recmmended_video_list_item, null);
					holder = new ViewHolder();
					holder.synopsis = (TextView) view
							.findViewById(R.id.synopsis);
					holder.des = (TextView) view.findViewById(R.id.des);
					holder.des_new = (TextView) view
							.findViewById(R.id.video_title_name);
					holder.item_img = (ImageView) view
							.findViewById(R.id.item_img);
					view.setTag(holder);
				} else {
					holder = (ViewHolder) view.getTag();
				}

				Map<String, String> map = mData.get(position);
				holder.synopsis.setVisibility(View.GONE);
				holder.des.setVisibility(View.GONE);
				LoadBitmap.getIntence().loadImage(map.get("ch_logo"),
						holder.item_img);
				setImgSize(holder.item_img, 14, 27 / 48.0f, 1);
				setItem(holder.des_new, map.get("ch_name"));
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
				Intent intent = new Intent(mContext, ListBrandActivity.class);
				intent.putExtra("id", map.get("id"));
				intent.putExtra("name", map.get("ch_name"));
				mContext.startActivity(intent);
			}
		});

		listView.setXListViewListener(new IXListViewListener() {
			public void onRefresh() {
				new GetData_new().execute();
			}
		});
	}

	private class GetData_new extends
			AsyncTask<String, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_UN_GET_SHARE_LIST_NEW), "124");
		}

		protected void onPostExecute(HashMap<String, String> result) {
			super.onPostExecute(result);
			listView.stopRefresh();
			if (result != null && "1".equals(result.get("code"))) {
				ArrayList<HashMap<String, String>> list = DataRequest
						.getArrayListFromJSONArrayString(result.get("data"));
				if (list != null && list.size() > 0) {
					Logger.i("经典收藏数据" + list.toString());
					listView.setVisibility(View.VISIBLE);
					view.findViewById(R.id.moren_mychase_i).setVisibility(
							View.GONE);
					mData.clear();
					mData.addAll(list);
					mAdapter.notifyDataSetChanged();
				}

			} else {
				listView.setVisibility(View.GONE);
				view.findViewById(R.id.moren_mychase_i).setVisibility(
						View.VISIBLE);

				Bitmap bitmap = android.graphics.BitmapFactory.decodeResource(
						mContext.getResources(), R.drawable.moren_mychase);
				int screenWidth = PreferenceUtil.getInt(mContext,
						"screenWidth", 720);
				ImageView moren_mychase_i = (ImageView) view
						.findViewById(R.id.moren_mychase_img);
				if (bitmap != null) {
					Bitmap bitmap2 = BitmapUtil.getBitmap(bitmap,
							screenWidth - 36, (screenWidth - 36) * 190 / 318);
					if (bitmap2 != null) {
						BitmapUtil.setImageBitmap(moren_mychase_i, bitmap2);
					}
				}

				Logger.i("没有获取到推荐数据");
			}
		}

	}

}
