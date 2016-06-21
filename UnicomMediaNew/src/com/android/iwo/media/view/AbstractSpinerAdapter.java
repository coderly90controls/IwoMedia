package com.android.iwo.media.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.iwo.media.apk.activity.*;

public class AbstractSpinerAdapter extends BaseAdapter {

	private String mKey = "";
	public static interface IOnItemSelectListener {
		public void onItemClick(int pos);
	};

	private List<HashMap<String, String>> mObjects = new ArrayList<HashMap<String, String>>();

	private LayoutInflater mInflater;

	public AbstractSpinerAdapter(Context context) {
		init(context);
	}

	public void refreshData(List<HashMap<String, String>> objects, int selIndex) {
		mObjects = objects;
		if (selIndex < 0) {
			selIndex = 0;
		}
		if (selIndex >= mObjects.size()) {
			selIndex = mObjects.size() - 1;
		}
	}

	private void init(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setKey(String key) {
		mKey = key;
	}

	@Override
	public int getCount() {

		return mObjects.size();
	}

	@Override
	public Object getItem(int pos) {
		return mObjects.get(pos).get(mKey);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.spiner_item_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.mTextView = (TextView) convertView.findViewById(R.id.textView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		String item = (String) getItem(pos);
		viewHolder.mTextView.setText(item);

		return convertView;
	}

	public static class ViewHolder {
		public TextView mTextView;
	}
}
