package com.android.iwo.media.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public class LinearLayoutForListView extends LinearLayout {
	private ListAdapter adapter;
	private OnClickListener onClickListener = null;
	private OnTouchListener onTouchListener = null;

	/**
	 * 绑定布局
	 */
	public void bindLinearLayout() {
		int count = adapter.getCount();

		for (int i = 0; i < count; i++) {
			View v = adapter.getView(i, null, null);
			v.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			/*
			 * v.setOnTouchListener(this.onTouchListener);
			 * v.setOnClickListener(this.onClickListener);
			 */
			v.setId(i);
			addView(v, i);
		}
	}

	public LinearLayoutForListView(Context context) {
		super(context);
	}

	public LinearLayoutForListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 获取Adapter
	 * 
	 * @return adapter
	 */
	public ListAdapter getAdpater() {
		return adapter;
	}

	/**
	 * 设置数据
	 * 
	 * @param adpater
	 */
	public void setAdapter(ListAdapter adpater) {
		this.adapter = adpater;
		bindLinearLayout();
	}

	/**
	 * 获取点击事件
	 * 
	 * @return
	 */
	public OnClickListener getOnclickListner() {
		return onClickListener;
	}

	/**
	 * 设置点击事件
	 * 
	 * @param onClickListener
	 */
	public void setOnclickLinstener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public OnTouchListener getOnTouchListener() {
		return onTouchListener;
	}

	public void setOnTouchListener(OnTouchListener onTouchListener) {
		this.onTouchListener = onTouchListener;
	}
}
