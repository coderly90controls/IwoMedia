package com.android.iwo.media.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.android.iwo.media.view.DragImageView;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.Logger;

public class ChatBitmapActivity extends BaseActivity {

	private int window_width, window_height;// 控件宽度
	private DragImageView dragImageView;// 自定义控件
	private int state_height;// 状态栏的高度

	private ViewTreeObserver viewTreeObserver;
	private Bitmap bit;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_img);
		/** 获取可見区域高度 **/
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();

		dragImageView = (DragImageView) findViewById(R.id.div_main);
		Bitmap bmp = ReadBitmapById(this, getIntent().getStringExtra("path"), window_width, window_height);
		// 设置图片
		if(bmp == null){
			makeText("图片地址不对");
			finish();
			return;
		}
		dragImageView.setImageBitmap(bmp);
		dragImageView.setmActivity(this);// 注入Activity.
		// 测量状态栏高度 
		viewTreeObserver = dragImageView.getViewTreeObserver();
		viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				Logger.i("------" + state_height);
				if (state_height == 0) {
					// 获取状况栏高度
					Rect frame = new Rect();
					getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
					state_height = frame.top;
					dragImageView.setScreen_H(window_height - state_height);
					dragImageView.setScreen_W(window_width);
				}

			}
		});
		
		LinearLayout img_laLayout = (LinearLayout)findViewById(R.id.img_layout);
		img_laLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if(bit != null){
					bit.recycle();
					bit = null;
				}
				finish();
			}
		});
	}

	/***
	 * 根据资源文件获取Bitmap
	 * 
	 * @param context
	 * @param drawableId
	 * @return
	 */
	private Bitmap ReadBitmapById(Context context, String path, int screenWidth, int screenHight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.ARGB_8888;
		options.inInputShareable = true;
		options.inPurgeable = true;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return getBitmap(bitmap, screenWidth, screenHight);
	}

	/***
	 * 等比例压缩图片
	 * 
	 * @param bitmap
	 * @param screenWidth
	 * @param screenHight
	 * @return
	 */
	private Bitmap getBitmap(Bitmap bitmap, int screenWidth, int screenHight) {
		if(bitmap == null) return null;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scale = (float) screenWidth / w;

		// 保证图片不变形.
		matrix.postScale(scale, scale);
		// w,h是原图的属性.
		try {
			return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		} catch (Exception e) {
			return Bitmap.createBitmap(bitmap, 0, 0, 100, 100, matrix, true);
		}
	}
	@Override
	public void onBackPressed() {
		if(bit != null){
			bit.recycle();
			bit = null;
		}
		super.onBackPressed();
	}
}
