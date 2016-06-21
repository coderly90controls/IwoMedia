package com.android.iwo.media.view;

import com.test.iwomag.android.pubblico.util.Logger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class ListItemView extends HorizontalScrollView {

	private float mLastMotionX;
	private int width = 0;
	private OnOpen openListener;

	public ListItemView(Context context) {
		super(context);
	}

	public ListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setWidth(int w) {
		width = w;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final float x = ev.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);
			mLastMotionX = x;
			scrollBy(deltaX, 0);
			break;
		case MotionEvent.ACTION_UP:
			int sx = getScrollX();
			Logger.i(width + "--getScrollX= " + sx);
			if (sx >= 0 && sx <= width / 2) {
				// Fling enough to move left
				scrollTo(-sx, 0);
			} else {
				scrollTo(width, 0);
				if(openListener!= null)
					openListener.open();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	public void setOnOpen(OnOpen n){
		openListener = n;
	}
	
	public interface OnOpen{
		public void open();
	}
}
