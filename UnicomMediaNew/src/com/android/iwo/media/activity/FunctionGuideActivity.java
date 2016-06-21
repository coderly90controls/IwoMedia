package com.android.iwo.media.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.adapter.ViewPageAdapter;

public class FunctionGuideActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_function_guide);
		init();
		setTitle("功能指导");
		setBack(null);
	}

	private void init() {

		initPage();
	}

	private void initPage() {
		ViewPager page = (ViewPager) findViewById(R.id.page);
		page.setVisibility(View.VISIBLE);
		ArrayList<View> lists = new ArrayList<View>();
		View view = null;
		//int ids[] = { R.drawable.in_1, R.drawable.in_2, R.drawable.in_3};
		for (int i = 0; i < 3; i++) {
			view = new ImageView(this);
		//	view.setBackgroundResource(ids[i]);

			lists.add(view);
		}
		ViewPageAdapter adapter = new ViewPageAdapter(lists);
		page.setAdapter(adapter);

		/*page.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (arg0 == 3 && arg1 == 0.0) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}
					finish();
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});*/
	}
}
