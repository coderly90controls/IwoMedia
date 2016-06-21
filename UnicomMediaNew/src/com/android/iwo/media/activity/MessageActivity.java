package com.android.iwo.media.activity;

import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MessageActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		init();
	}

	private void init() {
		setBack(null);
		setTitle("消息");
		ImageView imageView = (ImageView)findViewById(R.id.mst_img);
		String type = getIntent().getStringExtra("msgType");
		if("1".equals(type)){//文字
			TextView text = (TextView)findViewById(R.id.msg_tex);
			text.setText(getIntent().getStringExtra("con"));
		}else if("2".equals(type)){//图片
			setImageItem(imageView, getIntent().getStringExtra("con"));
		}else if("3".equals(type)){//视频
			imageView.setBackgroundResource(R.drawable.default_s_16_9);
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					loadMp4(getIntent().getStringExtra("con"));
				}
			});
		}
	}
	
	protected void loadMp4(String url) {
		if (StringUtil.isEmpty(url))
			return;
		Uri uri = Uri.parse(url);
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.setDataAndType(uri, "video/mp4");
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Toast.makeText(this, "视频地址不对", Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void setImageItem(ImageView view, String con) {
		Logger.i("map.get(content)" + con);
		if (!StringUtil.isEmpty(con)) {
			String[] url = con.split(",");
			if (url.length == 6) {
				LoadBitmap.getIntence().loadImage(url[0], view);
				if (!"0".equals(url[1])) {
					setImgSize(view, 10, Float.valueOf(url[2]) / Float.valueOf(url[1]), 1);
				}
			}
		}
	}
}
