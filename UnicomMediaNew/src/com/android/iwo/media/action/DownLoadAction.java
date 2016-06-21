package com.android.iwo.media.action;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.test.iwomag.android.pubblico.util.FileCache;
import com.test.iwomag.android.pubblico.util.Logger;

public class DownLoadAction {

	private int downloadedSize = 0;
	private int fileSize = 0;
	private Context mContext;
	private String path;

	public void download(Context context, String url) {
		mContext = context;
		// 获取SD卡目录
		String dowloadDir = FileCache.getInstance().CACHE_PATH + "/download/";
		File file = new File(dowloadDir);
		// 创建下载目录
		if (!file.exists()) {
			file.mkdirs();
		}

		// 读取下载线程数，如果为空，则单线程下载
		int downloadTN = 5;
		// 如果下载文件名为空则获取Url尾为文件名

		String fileName = "video" + System.currentTimeMillis() + ".mp4";
		path = dowloadDir + fileName;
		// 启动文件下载线程
		new downloadTask(url, Integer.valueOf(downloadTN), dowloadDir
				+ fileName).start();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 当收到更新视图消息时，计算已完成下载百分比，同时更新进度条信息
			int progress = (Double
					.valueOf((downloadedSize * 1.0 / fileSize * 100)))
					.intValue();
			if (progress == 100) {
				Toast.makeText(mContext, "视频已下载到" + path, Toast.LENGTH_LONG)
						.show();
				Logger.i("下载完成！");
			} else {
				Logger.i("当前进度:" + progress + "%");
			}
		}
	};

	/**
	 * @author ideasandroid 主下载线程
	 */
	public class downloadTask extends Thread {
		private int blockSize, downloadSizeMore;
		private int threadNum = 5;
		String urlStr, threadNo, fileName;

		public downloadTask(String urlStr, int threadNum, String fileName) {
			this.urlStr = urlStr;
			this.threadNum = threadNum;
			this.fileName = fileName;
		}

		@Override
		public void run() {
			FileDownloadThread[] fds = new FileDownloadThread[threadNum];
			try {
				URL url = new URL(urlStr);
				URLConnection conn = url.openConnection();
				// 获取下载文件的总大小
				fileSize = conn.getContentLength();
				// 计算每个线程要下载的数据量
				blockSize = fileSize / threadNum;
				// 解决整除后百分比计算误差
				downloadSizeMore = (fileSize % threadNum);
				File file = new File(fileName);
				for (int i = 0; i < threadNum; i++) {
					// 启动线程，分别下载自己需要下载的部分
					FileDownloadThread fdt = new FileDownloadThread(url, file,
							i * blockSize, (i + 1) * blockSize - 1);
					fdt.setName("Thread" + i);
					fdt.start();
					fds[i] = fdt;
				}
				boolean finished = false;
				while (!finished) {
					// 先把整除的余数搞定
					downloadedSize = downloadSizeMore;
					finished = true;
					for (int i = 0; i < fds.length; i++) {
						downloadedSize += fds[i].getDownloadSize();
						if (!fds[i].isFinished()) {
							finished = false;
						}
					}
					// 通知handler去更新视图组件
					handler.sendEmptyMessage(0);
					// 休息1秒后再读取下载进度
					sleep(1000);
				}
			} catch (Exception e) {

			}

		}
	}
}
