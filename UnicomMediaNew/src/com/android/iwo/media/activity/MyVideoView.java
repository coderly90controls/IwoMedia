package com.android.iwo.media.activity;

import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class MyVideoView extends BaseActivity {
	private VideoView vv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		init();
	}

	private void init() {
		vv = (VideoView) findViewById(R.id.vv_video);
		String before = getIntent().getStringExtra("uri_z");
		Logger.i("广告：" + before);
		if (!StringUtil.isEmpty(before)) {
			Uri uri_pre = Uri.parse(before);
			vv.setVideoURI(uri_pre);

			vv.start();

			vv.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {

					mp.start();
				}
			});

			vv.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					setResult(119);
					finish();
				}
			});

		}
	}
}
