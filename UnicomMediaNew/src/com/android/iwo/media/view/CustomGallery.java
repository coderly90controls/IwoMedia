package com.android.iwo.media.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.Gallery;

public class CustomGallery  extends Gallery implements OnGestureListener{
	private ViewPager mPager;

	public ViewPager getmPager() {
		return mPager;
	}

	public void setmPager(ViewPager mPager) {
		this.mPager = mPager;
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CustomGallery(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 *            &nbsp;&nbsp;&nbsp; &nbsp; &nbsp;&nbsp;&nbsp;
	 */
	public CustomGallery(Context context, AttributeSet attrs) {
		super(context, attrs); 
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(mPager != null)
		mPager.requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(mPager != null)
		mPager.requestDisallowInterceptTouchEvent(true);
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mPager != null)
		mPager.requestDisallowInterceptTouchEvent(true);
		return super.onTouchEvent(event);
	}
}
