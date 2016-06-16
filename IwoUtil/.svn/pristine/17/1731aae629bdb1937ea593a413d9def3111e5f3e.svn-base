package com.test.iwomag.android.pubblico.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class LoadBmp {

	public static LoadBmp loadBitmap = null;

	private ArrayList<String> mListUrl = new ArrayList<String>();
	private CallBack backlistener = null;
	public interface CallBack{
		public void back(Bitmap bmp);
	}
	public static LoadBmp getIntence() {
		if (loadBitmap == null) {
			loadBitmap = new LoadBmp();
		}

		return loadBitmap;
	}
	public void setOnCallBack(CallBack listener){
		if(backlistener != null){
			backlistener = listener;
		}
	}
	/**
	 * 下载网络图片
	 */
	public void getImageURI(String path) throws Exception {
		String name = FileCache.getInstance().onSettingName(path);
		File file = new File(FileCache.getInstance().CACHE_PATH + "/" + name);
		Logger.i("path" + FileCache.getInstance().CACHE_PATH + "/" + name);
		// 如果图片存在本地缓存目录，则不去服务器下载
		if (!file.exists()) {
			// 从网络上获取图片
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			}
			conn.disconnect();
			conn = null;
		}
	}

	public void loadImage(String url, ImageView imageView) {
		if (StringUtil.isEmpty(url)) {
			return;
		}
		String path = FileCache.getInstance().CACHE_PATH + "/" + FileCache.getInstance().onSettingName(url);
		Bitmap bitmap = decodeBitmapFromSD(path, imageView.getWidth(), imageView.getHeight());
		if (bitmap != null && imageView != null) {
			if(backlistener != null){
				backlistener.back(bitmap);
			}else 
				BitmapUtil.setImageBitmap(imageView, bitmap);
			return;
		}
		new GetImage(url, imageView);
	}
	
	public void loadImage(String url, ImageView imageView, Bitmap def) {
		if (StringUtil.isEmpty(url)) {
			BitmapUtil.setImageBitmap(imageView, def);
			return;
		}
		Logger.i("--图片加载url=" + url);
		String path = FileCache.getInstance().CACHE_PATH + "/" + FileCache.getInstance().onSettingName(url);
		Bitmap bitmap = decodeBitmapFromSD(path, imageView.getWidth(), imageView.getHeight());
		if (bitmap != null && imageView != null) {
			if(backlistener != null){
				backlistener.back(bitmap);
			}else 
				BitmapUtil.setImageBitmap(imageView, bitmap);
			return;
		}
		new GetImage(url, imageView, def);
	}

	private class GetImage extends Thread{
		private String mUrl = "";
		private ImageView imageView;
		private Bitmap mdef;
		public GetImage(String url, ImageView iView, Bitmap def){
			for(int i=0; i< mListUrl.size(); i++){
				if(url.equals(mListUrl.get(i))){
					Logger.i("加载中");
					return;
				}
			}
			mdef = def;
			mListUrl.add(url);
			imageView = iView;
			mUrl = url;
			this.start();
		}
		
		public GetImage(String url, ImageView iView){
			for(int i=0; i< mListUrl.size(); i++){
				if(url.equals(mListUrl.get(i))){
					Logger.i("加载中");
					return;
				}
			}
			mListUrl.add(url);
			imageView = iView;
			mUrl = url;
			this.start();
		}
		
		@Override
		public void run() {
			super.run();
			try {
				getImageURI(mUrl);
			} catch (Exception e) {
			}
			
			Message msg = new Message();
			msg.what = 0;
			handler.sendMessage(msg);
		}
		
		public void onPost(){
			Logger.i("加载完");
			mListUrl.remove(mUrl);
			String path = FileCache.getInstance().CACHE_PATH + "/" + FileCache.getInstance().onSettingName(mUrl);
			Bitmap bitmap = decodeBitmapFromSD(path, imageView.getWidth(), imageView.getHeight());
			if (bitmap != null && imageView != null) {
				if(backlistener != null){
					backlistener.back(bitmap);
				}else 
					BitmapUtil.setImageBitmap(imageView, bitmap);
			}else {
				if(imageView != null){
					BitmapUtil.setImageBitmap(imageView, mdef);
				}
			}
		}
		
		private Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what == 0){
					onPost();
				}
			};
		};
	}
	
	public Bitmap decodeBitmapFromSD(String path, int reqWidth, int reqHeight) {
		File bit = new File(path);
		if (!bit.exists())
			return null;
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		Logger.i("--size = " + options.inSampleSize);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		try {
			return BitmapFactory.decodeFile(path, options);
		} catch (OutOfMemoryError e) {
		}
		return null;
	}
	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		if(reqHeight == 0 || reqWidth == 0) return 1;
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		Logger.i("req :" + reqHeight + ";" + reqWidth);
		Logger.i("opt :" + options.outHeight + ";" + options.outWidth);
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
}
