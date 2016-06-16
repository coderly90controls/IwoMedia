package com.test.iwomag.android.pubblico.adapter;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * ViewPager加载图片时及时回收图片
 */
public class IwoPageAdapter extends PagerAdapter{
	public List<Bitmap> mList = new ArrayList<Bitmap>();
	public List<View> mViews;
	public int mSize = 0;

	public IwoPageAdapter(List<View> views) {
		mViews = views;
		mSize = mViews.size();// 构造方法，参数是我们的页卡，这样比较方便。
		for(int i=0; i< mSize; i++){
			mList.add(null);
		}
	}

	@Override
	public int getCount() {
		return mSize;// 返回页卡的数量
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;// 官方提示这样写
	}
}
