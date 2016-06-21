package com.android.iwo.media.activity;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.android.iwo.media.apk.activity.R;

public class QrcodeActivity extends BaseActivity implements OnClickListener {
	private ImageView qrImgImageView;
//	private Share mShare;
	Bitmap qrCodeBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_qrcode);

		init();

	}

	private void init() {
		setTitle("我的二维码");
		setBack(null);

		setMode(getMode());
		qrImgImageView = (ImageView) findViewById(R.id.iv_qr_image);

		// try {
		// qrCodeBitmap = EncodingHandler.createQRCode(getUserTel(), 350);
		// qrImgImageView.setImageBitmap(qrCodeBitmap);
		//
		// mShare = new Share(QrcodeActivity.this, qrCodeBitmap);
		// mShare.setCon("下载爱握视频，扫描我的二维码可添加我为好友");
		// mShare.setUrl("http://plat.kazhu365.com/");
		//
		// } catch (WriterException e) {
		// e.printStackTrace();
		// }
		findViewById(R.id.qrcode_save).setOnClickListener(this);
		findViewById(R.id.qrcode_share).setOnClickListener(this);
	}

	private void setMode(String mode) {
		if ("day".equals(mode)) {
			findViewById(R.id.qrcode_bottom).setBackgroundColor(
					getResources().getColor(R.color.comm_green_color));
		} else if ("night".equals(mode)) {
			findViewById(R.id.qrcode_bottom).setBackgroundColor(
					getResources().getColor(R.color.comm_pink_color));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.qrcode_share:
//			mShare.setShare();
			break;
		case R.id.qrcode_save:
			File file = new File("/sdcard/");
			if (!file.exists())
				file.mkdirs();
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(
						file.getPath() + "/qrcode.jpg");
				qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						fileOutputStream);
				fileOutputStream.close();
				System.out.println("saveBmp is here");
				makeText("保存二维码成功");
			} catch (Exception e) {
				makeText("保存二维码失败");
			}
			break;

		default:
			break;
		}

	}
}
