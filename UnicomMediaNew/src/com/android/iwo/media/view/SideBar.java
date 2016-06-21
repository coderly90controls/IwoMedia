package com.android.iwo.media.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SideBar extends LinearLayout {
	public static char[] l = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
			'X', 'Y', 'Z' };;
	private SectionIndexer sectionIndexter = null;
	private ListView list;
	private TextView mDialogText;
	private int m_nItemHeight = 25;
	private Context mContext;

	public SideBar(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	private void init() {
		setTextLength();
		setView();
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public void setListView(ListView _list) {
		list = _list;
		sectionIndexter = (SectionIndexer) _list.getAdapter();
	}

	public void setTextView(TextView mDialogText) {
		this.mDialogText = mDialogText;
	}

	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if(mDialogText == null) return true;
		int i = (int) event.getY();
		int idx = i / m_nItemHeight;
		if (idx >= l.length) {
			idx = l.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			mDialogText.setVisibility(View.VISIBLE);
			mDialogText.setText("" + l[idx]);
			if (sectionIndexter == null) {
				sectionIndexter = (SectionIndexer) list.getAdapter();
			}
			int position = sectionIndexter.getPositionForSection(l[idx]);
			if (position == -1) {
				return true;
			}
			list.setSelection(position);
		} else {
			mDialogText.setVisibility(View.INVISIBLE);
		}
		return true;
	}

	/*
	 * protected void onDraw(Canvas canvas) { Paint paint = new Paint();
	 * paint.setColor(0xff595c61); paint.setTextSize(18);
	 * paint.setTextAlign(Paint.Align.CENTER); float widthCenter =
	 * getMeasuredWidth() / 2; for (int i = 0; i < l.length; i++) {
	 * canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight + (i *
	 * m_nItemHeight), paint); } super.onDraw(canvas); }
	 */

	private void setView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		this.setOrientation(LinearLayout.VERTICAL);
		this.setLayoutParams(params);
		TextView text = null;
		for (int i = 0; i < l.length; i++) {
			text = new TextView(mContext);
			text.setText(String.valueOf(l[i]));
			text.setTextSize(15);
			text.setTextColor(0xff000000);
			text.setGravity(Gravity.CENTER);
			text.setHeight(m_nItemHeight - 1);
			Log.i("size", "--" + text.getHeight());
			this.addView(text);
		}
	}

	private void setTextLength() {
		float scale = mContext.getResources().getDisplayMetrics().density;
		DisplayMetrics dm;
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		m_nItemHeight = (int) ((dm.heightPixels - (38 * scale + 0.5)) / l.length);
	}
}
