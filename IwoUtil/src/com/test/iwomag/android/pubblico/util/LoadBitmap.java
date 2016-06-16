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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;

public class LoadBitmap {

	public static LoadBitmap loadBitmap = null;

	public static LoadBitmap getIntence() {
		if (loadBitmap == null) {
			loadBitmap = new LoadBitmap();
		}

		return new LoadBitmap();
	}

	public void setCacheKey(String key) {
		/*
		 * Iterator<?> it = bitmapCache.entrySet().iterator(); boolean hasKey =
		 * false; while (it.hasNext()) { HashMap.Entry entry = (HashMap.Entry)
		 * it.next(); String k = (String) entry.getKey(); if (key.equals(k)) {
		 * hasKey = true; break; } } bitCache_key = key; if (!hasKey) {
		 * bitmapCache.put(bitCache_key, new BitmapCache()); } else
		 * bitmapCache.put(bitCache_key, new BitmapCache());
		 */
	}

	public void clean(String key) {
		/*
		 * Logger.i("clean key = " + bitCache_key); if (bitmapCache.get(key) !=
		 * null) bitmapCache.get(key).remove(); bitmapCache.remove(key);
		 */
	}

	private void cacheAdd(Bitmap bitmap) {
		/*
		 * Logger.i("add key = " + bitCache_key); if
		 * (bitmapCache.get(bitCache_key) != null)
		 * bitmapCache.get(bitCache_key).add(bitmap);
		 */
	}

	/**
	 * 控制图片大小的下载
	 * 
	 * @param size
	 * @param url
	 * @return
	 */
	public Bitmap downloadBitmap(int size, String url) {
		if (StringUtil.isEmpty(url))
			return null;
		GlobalContext gctx = GlobalContext.getInstance();
		HttpGet getRequest = new HttpGet(url);
		Logger.i("图片下载size" + size + "--url" + url);
		try {
			HttpResponse response = gctx.getHttpClient().execute(getRequest);
			int statusCode = 0;
			if (response != null) {
				statusCode = response.getStatusLine().getStatusCode();
			}
			if (statusCode != HttpStatus.SC_OK) {
				Logger.e("请求图片地址错误 " + statusCode + "：url=" + url);
				// return null;
			}

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					SoftReference<Bitmap> bitmap = null;
					try {
						BitmapFactory.Options option = new BitmapFactory.Options();
						option.inSampleSize = size; // 设置图片的大小
						bitmap = new SoftReference<Bitmap>(BitmapFactory.decodeStream(inputStream, null, option));// 文件流

					} catch (OutOfMemoryError e) {
						bitmap = null;
					}
					FileCache.getInstance().onCreateBitmap(url, bitmap);
					if (inputStream != null) {
						inputStream.close();
					}
					cacheAdd(bitmap.get());
					return bitmap.get();
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			Logger.i("请求图片异常" + e.toString());
		} catch (OutOfMemoryError e) {
			System.gc();
		} finally {
			Logger.i("---dfsdfsdfsdf");
			getRequest.abort();
			try {
				getRequest.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			getRequest = null;
		}
		return null;
	}

	/**
	 * 下载图片的类
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap downloadBitmap(String url) {
		if (StringUtil.isEmpty(url))
			return null;
		GlobalContext gctx = GlobalContext.getInstance();
		//Logger.i("图片下载 url = " + url);
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
			}

			HttpEntity entity = response.getEntity();
			//Logger.i("entity = " + (entity != null));
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					SoftReference<Bitmap> bitmap = null;
					// BitmapFactory.Options option = new
					// BitmapFactory.Options();
					// option.inSampleSize = Config.SIZE; //将图片设为原来宽高的1/2，防止内存溢出
					bitmap = new SoftReference<Bitmap>(BitmapFactory.decodeStream(inputStream));// 文件流
					FileCache.getInstance().onCreateBitmap(url, bitmap);
					cacheAdd(bitmap.get());
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
		} catch (Exception e) {
			if(getRequest != null)
			getRequest.abort();
			Logger.e("图片请求异常" + url + e.toString());
		} catch (OutOfMemoryError e) {
			Logger.e("图片请求内存溢出");
			System.gc();
		}

		try {
			if(getRequest != null){
				getRequest.abort();
				getRequest.clone();
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		getRequest = null;
		return null;
	}

	public void loadImage(String url, LoadBitmapCallBack callBack) {
		if (StringUtil.isEmpty(url)) {
			return;
		}
		Bitmap bitmap = FileCache.getInstance().onGetBitmap(url);
		if (bitmap != null) {
			callBack.callBack(bitmap);
			//BitmapUtil.setImageBitmap(imageView, bitmap);
			//cacheAdd(bitmap);
			return;
		}
		new BitmapDownloader(url, callBack).execute();
	}

	/**
	 * 控制图片的大小
	 * 
	 * @param size
	 * @param url
	 * @param imageView
	 */
	public void loadImage(int size, String url, ImageView imageView) {
		if (StringUtil.isEmpty(url)) {
			return;
		}
		Bitmap bitmap = FileCache.getInstance().onGetBitmap(url);
		if (bitmap != null && imageView != null) {
			BitmapUtil.setImageBitmap(imageView, bitmap);
			cacheAdd(bitmap);
			return;
		}
		if (cancelPotentialDownload(url, imageView)) {
			BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, size);
			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
			imageView.setImageDrawable(downloadedDrawable);
			task.execute(url);
		}
	}

	public void loadImage(String url, ImageView imageView) {
		if (StringUtil.isEmpty(url)) {
			return;
		}
		Bitmap bitmap = FileCache.getInstance().onGetBitmap(url);
		if (bitmap != null && imageView != null) {
			BitmapUtil.setImageBitmap(imageView, bitmap);
			cacheAdd(bitmap);
			return;
		}
		if (cancelPotentialDownload(url, imageView)) {
			BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
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

	private class BitmapDownloader extends AsyncTask<String, Void, Bitmap> {
		private String url;
		private LoadBitmapCallBack callBack;

		public BitmapDownloader(String url, LoadBitmapCallBack back) {
			this.url = url;
			this.callBack = back;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = FileCache.getInstance().onGetBitmap(url);
			if (bitmap != null) {
				return bitmap;
			} else {
				return downloadBitmap(url);
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}
			cacheAdd(bitmap);
			if (callBack != null)
				callBack.callBack(bitmap);
		}
	}

	private class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private String url;
		private int size = 1;
		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		public BitmapDownloaderTask(ImageView imageView, int size) {
			imageViewReference = new WeakReference<ImageView>(imageView);
			this.size = size;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			url = params[0];
			if (size == 1)
				return downloadBitmap(params[0]);
			else {
				return downloadBitmap(size, params[0]);
			}
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

	public interface LoadBitmapCallBack {
		public void callBack(Bitmap bit);
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

	/**
	 *图片压缩成100*100
	 */
	public static Bitmap toCompressionBitmap(String path, int width, int height) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        newOpts.inJustDecodeBounds = true;//只读边,不读内容  
        Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);  
        
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        float hh = height*1.0f;//  
        float ww = width*1.0f;//  
        int be = 1;  
        if (w > h && w > ww) {  
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {  
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0)  
            be = 1;  
        newOpts.inSampleSize = be;//设置采样率  
          
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设  
        newOpts.inPurgeable = true;// 同时设置才会有效  
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收  
          
        bitmap = BitmapFactory.decodeFile(path, newOpts);
        return bitmap;  
	}
	
	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;

		// 无非就是计算圆形区域
		if (width <= height) {
			roundPx = width / 2;
			float clip = (height - width) / 2;
			top = clip;
			bottom = width + clip;
			left = 0;
			right = width;

			height = width;

			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;

			left = clip;
			right = height + clip;
			top = 0;
			bottom = height;

			width = height;

			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		paint.setColor(0xFFFFFFFF);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}
	
	public static Bitmap getimage(String srcPath) {  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空  
          
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
        float hh = 200f;//这里设置高度为800f  
        float ww = 150f;//这里设置宽度为480f  
        /*if(w <= h){
        	hh = 200;
        	ww = 1.0f*w/h * 200;
        }else{
        	hh = 1.0f*h/w *200;
        	ww = 200;
        }*/
        	
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;//be=1表示不缩放  
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0)  
            be = 1;  
        newOpts.inSampleSize = be;//设置缩放比例  
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
        return bitmap;//压缩好比例大小后再进行质量压缩  
    } 
	
	public void loadImage(final ImageView imageView, final String url){
		loadImage(url, new LoadBitmapCallBack() {
			public void callBack(Bitmap bit) {
				if(bit != null){
					imageView.setImageBitmap(bit);
				}else {
					new Handler().postAtTime(new Runnable() {
						
						@Override
						public void run() {
							loadImage(url, new LoadBitmapCallBack() {
								public void callBack(Bitmap bit) {
									if(bit != null){
										imageView.setImageBitmap(bit);
									}else {
										new Handler().postAtTime(new Runnable() {
											
											@Override
											public void run() {
												loadImage(url, new LoadBitmapCallBack() {
													public void callBack(Bitmap bit) {
														if(bit != null){
															imageView.setImageBitmap(bit);
														}else {
															new Handler().postAtTime(new Runnable() {
																
																@Override
																public void run() {
																	loadImage(url, new LoadBitmapCallBack() {
																		public void callBack(Bitmap bit) {
																			if(bit != null){
																				imageView.setImageBitmap(bit);
																			}
																		}
																	});
																}
															}, 30*1000);
														}
													}
												});
											}
										}, 10*1000);
									}
								}
							});
						}
					}, 3000);
				}
			}
		});
	}
}
