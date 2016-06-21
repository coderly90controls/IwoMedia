package com.android.iwo.media.activity;



import com.android.iwo.media.apk.activity.R;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;


public class AnimationActivity extends BaseActivity {
	private ImageView te;
	private static final int SHOW_TIME_MIN =0;
	private AnimationDrawable ob;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_animation);
		init();
	}

	private void init() {
		te = (ImageView) findViewById(R.id.textview);
		te.setBackgroundResource(R.anim.kaiji_iwo);
		ob = (AnimationDrawable) te.getBackground();
		ob.start();
		int duration = 0;   
		       for(int i=0;i<ob.getNumberOfFrames();i++){   
		           duration += ob.getDuration(i);   
		      }   
		       
		       duration=duration+SHOW_TIME_MIN;
		       Handler handler = new Handler();   
		        handler.postDelayed(new Runnable() {   
		            public void run() {   
		            	Intent intent = new Intent(AnimationActivity.this, ModelActivity.class);
		        		startActivity(intent);
		        		finish();
		            }   
		        }, duration);   
	}


}
