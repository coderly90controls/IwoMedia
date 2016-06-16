package com.android.iwo.util.bitmapcache;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class SyncBitmap {
	private static ImageMemoryCache memoryCache = null;
	private static ImageFileCache fileCache = null;
	private LoadCallBack callBack;
	public static SyncBitmap loadBitmap = null;

	public static SyncBitmap getIntence(Context context) {
		if (loadBitmap == null) {
			loadBitmap = new SyncBitmap();
			memoryCache = new ImageMemoryCache(context);
			fileCache = new ImageFileCache();
		}

		return loadBitmap;
	}

	/**
	 * 获得一张图片,从三个地方获取,首先是内存缓存,然后是文件缓存,最后从网络获取 *
	 **/
	private Bitmap getBitmap(String url) {
		// 从内存缓存中获取图片
		Bitmap result = memoryCache.getBitmapFromCache(url);
		if (result == null) {
			// 文件缓存中获取
			result = fileCache.getImage(url);
			if (result == null) {
				// 从网络获取
				result = ImageGetFromHttp.downloadBitmap(url);
				if (result != null) {
					fileCache.saveBitmap(result, url);
					memoryCache.addBitmapToCache(url, result);
				}
			} else {
				// 添加到内存缓存
				memoryCache.addBitmapToCache(url, result);
			}
		}
		return result;
	}

	/**
	 * 请求图片并回调
	 * 
	 * @param url
	 * @param imageView
	 */
	public void loadImage(String url, ImageView imageView, LoadCallBack back) {
		if (StringUtil.isEmpty(url)) {
			return;
		}
		callBack = back;
		if (cancelPotentialDownload(url, imageView)) {
			BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
			imageView.setImageDrawable(downloadedDrawable);
			task.execute(url);
		}
	}

	/**
	 * 请求图片
	 * 
	 * @param url
	 * @param imageView
	 */
	public void loadImage(String url, ImageView imageView) {
		if (StringUtil.isEmpty(url)) {
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

	/**
	 * 如果ImageView中在加载中，则取消
	 * 
	 * @param url
	 * @param imageView
	 * @return
	 */
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

	/**
	 * 加载图片的异步类
	 */
	private class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private String url;
		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		protected Bitmap doInBackground(String... params) {
			url = params[0];
			return getBitmap(url);
		}

		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
				if (this == bitmapDownloaderTask) {
					if (callBack != null) {
						callBack.callBack(imageView, bitmap);
					} else {
						BitmapUtil.setImageBitmap(imageView, bitmap);
					}
				}
			}
		}
	}

	public interface LoadCallBack {
		public void callBack(ImageView imageView, Bitmap bit);
	}

	/** 
     * 压缩图片 
     * @param bitmap 源图片 
     * @param width 想要的宽度 
     * @param height 想要的高度 
     * @param isAdjust 是否自动调整尺寸, true图片就不会拉伸，false严格按照你的尺寸压缩 
     * @return Bitmap 
     */
	public static Bitmap reduce(Bitmap bitmap, int width, int height) {
		// 如果想要的宽度和高度都比源图片小，就不压缩了，直接返回原图
		if (bitmap.getWidth() < width && bitmap.getHeight() < height) {
			return bitmap;
		}
		// 根据想要的尺寸精确计算压缩比例, 方法详解：public BigDecimal divide(BigDecimal divisor,
		// int scale, int roundingMode);
		// scale表示要保留的小数位, roundingMode表示如何处理多余的小数位，BigDecimal.ROUND_DOWN表示自动舍弃
		float sx = new BigDecimal(width).divide(new BigDecimal(bitmap.getWidth()), 4, BigDecimal.ROUND_DOWN).floatValue();
		float sy = new BigDecimal(height).divide(new BigDecimal(bitmap.getHeight()), 4, BigDecimal.ROUND_DOWN).floatValue();
		// 如果想自动调整比例，不至于图片会拉伸
		sx = (sx < sy ? sx : sy);
		sy = sx;// 哪个比例小一点，就用哪个比例

		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy);// 调用api中的方法进行压缩，就大功告成了
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}
}
