package com.test.iwomag.android.pubblico.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class LoadBitmapA {

	public static LoadBitmapA loadBitmap = null;

	public static LoadBitmapA getIntence() {
		if (loadBitmap == null) {
			loadBitmap = new LoadBitmapA();
		}

		return new LoadBitmapA();
	}

	/**
	 * 下载图片的类
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap downloadBitmap(Context context, String url, long time) {
		if(!NetworkUtil.isWIFIConnected(context)){
			Logger.i("没网" + url);
			return null;
		}
		if (StringUtil.isEmpty(url))
			return null;
		GlobalContext gctx = GlobalContext.getInstance();
		Logger.i("图片下载 url = " + url);
		HttpGet getRequest = null;
		try {
			getRequest = new HttpGet(url);
			HttpResponse response = gctx.getHttpClient().execute(getRequest);
			int statusCode = 0;
			if (response != null) {
				statusCode = response.getStatusLine().getStatusCode();
			}
			if (statusCode != HttpStatus.SC_OK) {
				Logger.w("请求图片地址出错：" + "Error " + statusCode + " while retrieving bitmap from " + url);
			} else {

				HttpEntity entity = response.getEntity();
				Logger.i("entity = " + (entity != null));
				if (entity != null) {
					InputStream inputStream = null;
					try {
						inputStream = entity.getContent();
						SoftReference<Bitmap> bitmap = null;

						bitmap = new SoftReference<Bitmap>(BitmapFactory.decodeStream(inputStream));// 文件流
						FileCache.getInstance().onCreateBitmap(url, bitmap);

						return bitmap.get();
					} finally {
						if (inputStream != null) {
							inputStream.close();
						}
						entity.consumeContent();
						entity = null;
						inputStream = null;
					}
				}
			}
		} catch (Exception e) {
			if (getRequest != null)
				getRequest.abort();
			Logger.e("图片请求异常" + url + e.toString());
		} catch (OutOfMemoryError e) {
			Logger.e("图片请求内存溢出");
			System.gc();
		}

		try {
			if (getRequest != null) {
				getRequest.abort();
				getRequest.clone();
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		getRequest = null;
		time = 2 * time + time;
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
		}
		Logger.i("刷新图片的次数" + time);
		if (time > 30)
			return null;
		return downloadBitmap(context, url, time);
	}

	public void loadImage(Context context, String url, ImageView imageView) {
		if (StringUtil.isEmpty(url)) {
			return;
		}
		Bitmap bitmap = FileCache.getInstance().onGetBitmap(url);
		if (bitmap != null && imageView != null) {
			BitmapUtil.setImageBitmap(imageView, bitmap);

			return;
		}
		if (cancelPotentialDownload(url, imageView)) {
			BitmapDownloaderTask task = new BitmapDownloaderTask(context, imageView);
			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
			imageView.setImageDrawable(downloadedDrawable);
			task.execute(url);
		}
	}

	private BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	private class DownloadedDrawable extends ColorDrawable {
		private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

		public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
			super(Color.TRANSPARENT);
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
		}

		public BitmapDownloaderTask getBitmapDownloaderTask() {
			return bitmapDownloaderTaskReference.get();
		}
	}

	private boolean cancelPotentialDownload(String url, ImageView imageView) {
		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
				bitmapDownloaderTask.cancel(true);
			} else {
				return false;
			}
		}
		return true;
	}

	private class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private String url;
		private final WeakReference<ImageView> imageViewReference;
		private Context context;
		public BitmapDownloaderTask(Context con, ImageView imageView) {
			context = con;
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			url = params[0];
			return downloadBitmap(context, params[0], 2);
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
				if (this == bitmapDownloaderTask) {
					BitmapUtil.setImageBitmap(imageView, bitmap);
				}
			}
		}
	}

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
}
