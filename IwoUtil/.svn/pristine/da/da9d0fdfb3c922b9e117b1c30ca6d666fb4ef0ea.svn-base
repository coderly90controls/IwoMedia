package com.test.iwomag.android.pubblico.util;

import java.io.File;
import java.io.FileOutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.widget.Toast;

import com.android.iwo.util.bitmapcache.ImageFileCache;
import com.android.iwo.util.bitmapcache.SyncBitmap;

/**
 * 调用Android系统的一些方法
 */
@SuppressLint("NewApi")
public class AndroidUtils {

	public final static int VIDEO = 100002;

	/**
	 * 调用系统拍摄视频功能
	 * 
	 * @param activity
	 */
	public static String video(Activity activity) {
		String path = "";
		// 执行拍照前，应该先判断SD卡是否存在
		String SDState = Environment.getExternalStorageState();
		if (SDState.equals(Environment.MEDIA_MOUNTED)) {
			Intent intent = null;
			intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			path = FileCache.getInstance().CACHE_PATH + "/CAPTURE_" + System.currentTimeMillis() + ".mp4";
			Logger.i("视频地址：" + path);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
			activity.startActivityForResult(intent, VIDEO);
		} else {
			Toast.makeText(activity, "内存卡不存在", Toast.LENGTH_LONG).show();
		}

		return path;
	}

	/**
	 * 获取视频第一帧图片
	 * @param filePath
	 * @return
	 */
	public static Bitmap getBitmapsFromVideo(String filePath) {
		if(StringUtil.isEmpty(filePath)) return null;
		try {
			
			ImageFileCache cache = new ImageFileCache();
			Bitmap bitmap;
			bitmap = cache.getImage(filePath);
			if(bitmap != null) return bitmap;
			bitmap = ThumbnailUtils.createVideoThumbnail(filePath, Thumbnails.MINI_KIND);
			/*MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			retriever.setDataSource(filePath);
			bitmap = retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
			String path = Environment.getExternalStorageDirectory() + File.separator + ".jpg";
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(path);
				bitmap.compress(CompressFormat.JPEG, 40, fos);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			bitmap = SyncBitmap.reduce(bitmap, 400, 400);
			cache.saveBitmap(bitmap, filePath);
			return bitmap;
		} catch (Exception e) {
			return null;
		}
		
	}

	/**
	 * 播放视频
	 * @param url
	 * @param context
	 */
	public static void loadMp4(String url, Activity context) {
		if (StringUtil.isEmpty(url)) {
			Toast.makeText(context, "视频地址为空", Toast.LENGTH_SHORT).show();
			return;
		}

		Uri uri = Uri.parse(url);
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.setDataAndType(uri, "video/mp4");
			context.startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Toast.makeText(context, "视频地址不对", Toast.LENGTH_SHORT).show();
		}
	}
	
	
}
