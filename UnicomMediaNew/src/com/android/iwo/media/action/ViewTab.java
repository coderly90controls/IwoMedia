package com.android.iwo.media.action;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.android.iwo.media.view.XListView;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;

public class ViewTab {
	protected View view;
	protected Context mContext;
	protected View mHead;
	protected XListView listView;
	protected IwoAdapter mAdapter;
	protected float scale = 0;
	protected ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	protected boolean isrefresh = true;
	protected DisplayMetrics dm;

	protected void init() {
	}

	public View getView() {
		return view;
	}

	protected ArrayList<HashMap<String, String>> doInBack(String... params) {
		return null;
	}

	protected void onPostExe(ArrayList<HashMap<String, String>> result) {

	}

	protected void onPreExe() {

	}

	protected class GetData extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected void onPreExecute() {
			onPreExe();
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
			return doInBack();
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			onPostExe(result);
		}
	}
	protected String getPre(String key) {
		return PreferenceUtil.getString(mContext, key, "");
	}
	protected String getStart(int size) {
		String result = "";
		if (size == 0) {
			return "1";
		} else if (size < 10 && size > 0)
			return "2";
		else if (size / 10 > 0) {
			return ((size % 10 > 0) ? (size / 10 + 2) : (size / 10 + 1)) + "";
		}
		return result;
	}

	/**
	 * 
	 * @param item
	 * @param del
	 * @param size
	 * @param n
	 * @return
	 */
	protected int setImgSize(ImageView item, int del, float size, float n) {
		int width = (int) ((dm.widthPixels - (int) (del * scale + 0.5f)) / n);
		android.view.ViewGroup.LayoutParams params = item.getLayoutParams();
		params.height = (int) (width * size);
		params.width = width;
		return params.height;

	}

	protected int setImgSize(ImageView item, int del, float size, int n) {
		int width = (dm.widthPixels - (int) (del * scale + 0.5f)) / n;
		android.view.ViewGroup.LayoutParams params = item.getLayoutParams();
		params.height = (int) (width * size);
		params.width = width;
		return params.height;
	}
	/*fcz
	 * */
	protected int setImgSize_new(ImageView item, int del, float size, int n) {
		int width = (dm.widthPixels - del) / n;
		android.view.ViewGroup.LayoutParams params = item.getLayoutParams();
		params.height = (int) (width * size);
		params.width = width;
		return params.height;
	}

	protected String getUid() {
		if ("day".equals(getMode())) {
			return PreferenceUtil.getString(mContext, "user_id", "");
		} else {
			return PreferenceUtil.getString(mContext, "n_user_id", "");
		}
	}

	protected String getMode() {
		return PreferenceUtil.getString(mContext, "video_model", "day");
	}

	protected String getUrl(String url) {
		if (mContext != null) {
			return AppConfig.GetUrl(mContext, url);
		}
		return url;
	}
//将数据暴露出去
	public ArrayList<HashMap<String, String>> getmData() {
		return mData;
	}
}
