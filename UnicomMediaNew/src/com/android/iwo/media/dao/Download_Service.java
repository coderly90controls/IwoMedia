package com.android.iwo.media.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import com.android.iwo.media.apk.activity.R;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

public class Download_Service extends IntentService {
	private SharedPreferences sp;
	private PublicUtils publicUtils;
	private NotificationManager mNotificationManager;
	private ArrayList<String> arrayList;
	private MyReceiver receiver;
	private static final int INIT = 1;// 定义三种下载的状态：初始化状态，正在下载状态，暂停状态
	private static final int PAUSE = 3;
	private int state = INIT;
	private Boolean ism3u8 = true;
	private Notification mNotification;
	// 状态栏通知显示的view
	private RemoteViews remoteViews;
	// 通知的id
	private final int notificationID = 1;
	private String downlodUrl; // 要下载的视频地址
	private String mapKey;// 下载到第几个视频
	private String downloadedSize; // 下载完成度
	private String id;
	private String urlTotal;
	private String filename;// 视频名
	private String url_vi;
	private String picture;
	private String foldername;
	private long allurlsize;// 所有url的大小的和。
	private String downloadSpeed;
	// 更新状态栏的下载进度
	private final int updateProgress = 1;
	// 下载成功
	private final int downloadSuccess = 2;
	// 下载失败
	private final int downloadError = 3;
	// 下载暂停
	private final int downloadPause = 4;
	// 2g/3g不可下载
	private final int MoreNet = 4;
	// sd卡空间
	private final int SdCardSpace = 5;
	private int allSize;// 视频总数量
	private int downloadIngindex;// 正在下载第几个视频
	private int finshSize; // 已经下载了多少个视频。
	private int compeleteSize;// 每个视频的下载完成度
	private int filesize;// fcz 每个Url文件大小
	private int length_url;
	private int start;
	private int startPosition;
	/** 开始时间 */
	private long startTime;
	/** 当前时间 */
	private long curTime;
	/** 下载的 平均网速 */
	private int downloadNetworkSpeed = 0;
	/** 下载用的时间 */
	private int usedTime = 0;
	private String m3u8String = ".m3u8";
	// private boolean isMp4=false;
	private int maxSize;
	private FileDownloader loader;
	private boolean isDs = false;

	public Download_Service() {
		super("ServiceDownloader");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.i("ser_onCreate");
		sp = getSharedPreferences(ConstantsDownload.share_pre_name, Context.MODE_PRIVATE);
		publicUtils = new PublicUtils(this);
		// 初始化通知管理器
				mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		
		arrayList = new ArrayList<String>();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		receiver = new MyReceiver();

		IntentFilter filter = new IntentFilter();
		filter.addAction(publicUtils.PAUSE);

		// 注册广播
		registerReceiver(receiver, filter);

		url_vi = intent.getStringExtra("url");
		if (url_vi.contains(m3u8String)) {// fcz
			reset();
			DataBaseDao.getInstance(Download_Service.this).updata_DownStatus(id, 1);
		}

		id = intent.getStringExtra("_id");
		Logger.i("开始下载视频" + id);
		// 根据视频id跟新视频下载状态

		allurlsize = sp.getLong("allurlsize" + id, 0);
		Logger.i("已经下载视频的总大小：" + allurlsize);
		urlTotal = intent.getStringExtra("urlTotal");
		filename = intent.getStringExtra("filename");// 视频名
		Logger.i("下载的URL：" + url_vi);
		picture = intent.getStringExtra("picture");
		foldername = intent.getStringExtra("foldername");
		mapKey = intent.getStringExtra("mapKey");
		downloadedSize = intent.getStringExtra("downloadedSize");
		downloadSpeed = intent.getStringExtra("downloadSpeed");
		// downlodUrl = PublicUtils.createEncryptM3u8Url(url);
		downlodUrl = url_vi;
		Logger.i("上次下载到第" + mapKey + "视频" + "下载的URL：" + downlodUrl);

		return super.onStartCommand(intent, flags, startId);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == updateProgress) {// 更新下载进度
				Logger.i("hander更新进度");
				if (finshSize > 0) {
					float size = (float) finshSize * 100 / (float) allSize;
					DecimalFormat format = new DecimalFormat("0.00");
					String progress = format.format(size);
					remoteViews.setTextViewText(R.id.tv_already_download, "已下载" + progress + "%");
					remoteViews.setProgressBar(R.id.ProgressBarDown, 100, (int) size, false);
					mNotification.contentView = remoteViews;
					mNotificationManager.notify(notificationID, mNotification);
					DataBaseDao.getInstance(Download_Service.this).updateProgress(id, progress, size);

				}

			} else if (msg.what == downloadSuccess) {// 下载完成
				Logger.i("hander下载视频完成啦");
				remoteViews.setTextViewText(R.id.tv_already_download, "下载完成");
				remoteViews.setProgressBar(R.id.ProgressBarDown, 100, 100, false);
				mNotification.contentView = remoteViews;
				mNotificationManager.notify(notificationID, mNotification);
				// mNotification.defaults = Notification.DEFAULT_SOUND; //有默认的声音
				// mNotification.flags = Notification.FLAG_AUTO_CANCEL; //
				// 点击后自动消除
				mNotification.contentView = remoteViews;
				mNotificationManager.cancel(notificationID);
				// 根据id更新数据库,完成状态 与播放地址
				Logger.i("下载完成视频总大小为：" + allurlsize);
				DataBaseDao.getInstance(Download_Service.this).updata_DownloagIng_Finsh(id,
						ConstantsDownload.path_m3u8 + "/" + urlTotal + id + ".m3u8", Integer.parseInt(String.valueOf(allurlsize)));

				// 根据视频id跟新视频下载状态
				DataBaseDao.getInstance(Download_Service.this).updata_DownStatus(id, 0);
				;

				Intent intent_finsh = new Intent();
				intent_finsh.setAction(publicUtils.FINSH);
				intent_finsh.putExtra("id", id);
				sendBroadcast(intent_finsh);

				int query_Key_Id = DataBaseDao.getInstance(Download_Service.this).query_Key_Id(id);
				ArrayList<HashMap<String, String>> query_Next_DownLoad_IngList = DataBaseDao.getInstance(Download_Service.this)
						.query_Next_DownLoad_Ing("1", String.valueOf(query_Key_Id));
				// 如果query_Next_DownLoad_IngList的长度大于1那么自动下载他下面一个，否则下载第一个
				if (query_Next_DownLoad_IngList.size() >= 1) {

					HashMap<String, String> hashMap = query_Next_DownLoad_IngList.get(0);
					ArrayList<String> query_contion_size = DataBaseDao.getInstance(Download_Service.this).query_contion_size(
							hashMap.get("id"));
					Intent intent_next = new Intent(Download_Service.this, Download_Service.class);
					intent_next.putExtra("_id", hashMap.get("id"));// 视频id
					intent_next.putExtra("urlTotal", hashMap.get("tid"));// 视频章节id
					intent_next.putExtra("filename", hashMap.get("name"));// 视频名字
					intent_next.putExtra("url", hashMap.get("url_content"));// 视频播放地址
					intent_next.putExtra("picture", hashMap.get("pic"));// 视频的图片地址
					intent_next.putExtra("foldername", hashMap.get("taskname"));// 视频章节名称
					intent_next.putExtra("mapKey", query_contion_size.get(0));// 下载到第几个url
					intent_next.putExtra("downloadedSize", query_contion_size.get(1));// 下载完
																						// 成度
					intent_next.putExtra("downloadSpeed", query_contion_size.get(2));// 文件写入了多少
					Download_Service.this.startService(intent_next);
				}

			} else if (msg.what == downloadError) {// 下载失败
				Logger.i("hander下载失败");
				// 存下目前下载好的文件大小
				sp.edit().putLong("allurlsize" + id, allurlsize).commit();
				// 根据视频id更新视频完成度信息
				// 下载失败得不到compeleteSize与downloadIngindexd的正确值分别为0 和-1
				DataBaseDao.getInstance(Download_Service.this).updateCompeleteSize(id, compeleteSize, downloadIngindex, filesize);

				remoteViews.setTextViewText(R.id.tv_already_download, "下载失败，网络连接异常。");
				remoteViews.setProgressBar(R.id.ProgressBarDown, 100, 100, false);
				mNotification.contentView = remoteViews;
				mNotificationManager.notify(notificationID, mNotification);
				// mNotification.defaults = Notification.DEFAULT_SOUND; //有默认的声音
				// mNotification.flags = Notification.FLAG_AUTO_CANCEL; //
				// 点击后自动消除
				mNotification.contentView = remoteViews;
				mNotificationManager.cancel(notificationID);
				// 根据视频id跟新视频下载状态
				DataBaseDao.getInstance(Download_Service.this).updata_DownStatus(id, 0);
				;

				Intent intent_fail = new Intent();
				intent_fail.setAction(publicUtils.FAIL);
				intent_fail.putExtra("id", id);
				sendBroadcast(intent_fail);

			} else if (msg.what == downloadPause) {// 下载暂停
				// 根据视频id更新视频完成度信息
				Logger.i("hander下载暂停");
				Logger.i("暂停时更新的视频id:" + id);
				// 存下目前下载好的文件大小
				sp.edit().putLong("allurlsize" + id, allurlsize).commit();
				DataBaseDao.getInstance(Download_Service.this).updateCompeleteSize(id, compeleteSize, downloadIngindex, filesize);
				mNotificationManager.cancel(notificationID);

			} else if (msg.what == MoreNet) { // 网络变换时

			} else if (msg.what == SdCardSpace) {// sd卡空间不足
				Logger.i("handersd卡空间不足");
				remoteViews.setTextViewText(R.id.tv_already_download, "下载失败，SD卡空间不足");
				remoteViews.setProgressBar(R.id.ProgressBarDown, 100, 100, false);
				mNotification.contentView = remoteViews;
				mNotificationManager.notify(notificationID, mNotification);
				mNotification.defaults = Notification.DEFAULT_SOUND; // 有默认的声音
				mNotification.flags = Notification.FLAG_AUTO_CANCEL; // 点击后自动消除
				mNotification.contentView = remoteViews;
				mNotificationManager.notify(notificationID, mNotification);
				if(url_vi.contains(m3u8String)){
					DataBaseDao.getInstance(Download_Service.this).updateCompeleteSize(id, compeleteSize, downloadIngindex, filesize);
				}
				stopSelf();
			} else if (msg.what == 11) {
				if(isDs){
					return;
				}
				int size = msg.getData().getInt("size");
				remoteViews.setProgressBar(R.id.ProgressBarDown, maxSize, size, false);
				mNotification.contentView = remoteViews;
				mNotificationManager.notify(notificationID, mNotification);
				float num = (float) size / (float) maxSize;
				int result = (int) (num * 100);
				remoteViews.setTextViewText(R.id.tv_already_download, result + "%"); // 显示下载进度百分比
				if (size == maxSize) {
					Toast.makeText(getApplicationContext(), "下载完成", 1).show();
					mNotification.contentView = remoteViews;
					mNotificationManager.notify(notificationID, mNotification);
					// 点击后自动消除
					mNotification.contentView = remoteViews;
					mNotificationManager.cancel(notificationID);
					DataBaseDao.getInstance(Download_Service.this).updata_DownloagIng_Finsh(id, url_vi,
							Integer.parseInt(String.valueOf(maxSize)));

					// 根据视频id跟新视频下载状态
					DataBaseDao.getInstance(Download_Service.this).updata_DownStatus(id, 0);
					FileService fileService = new FileService(getApplicationContext());
					int lastIndexOf = url_vi.lastIndexOf("/");
					String substring = url_vi.substring(lastIndexOf);
					String content_name = ConstantsDownload.path_m3u8 + substring;
					fileService.delete(content_name);

					Intent intent_finsh = new Intent();
					intent_finsh.setAction(publicUtils.FINSH);
					intent_finsh.putExtra("id", id);
					sendBroadcast(intent_finsh);

					Logger.i("下载完了");
				}

			} else if (msg.what == 22) {
				Toast.makeText(getApplicationContext(), "下载失败MP4", 1).show();
			}
		}

	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		Logger.i("onDestroy");
		unregisterReceiver(receiver);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// 显示通知
		setUpNotification();
		if (!url_vi.contains(m3u8String)) {
			Logger.i("mp4下载 不走m3u8");

			DownVideoMp4();

			return;
		}

		ism3u8 = true;
		HttpURLConnection urlConnection = null;
		RandomAccessFile randomAccessFile = null; // fcz
		try {
			// 下载m3u8文件
			// 构造URL
			URL url = new URL(downlodUrl);
			// 打开连接
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			// android
			// 2.2版本以上HttpURLConnection跟服务交互采用了"gzip"压缩,如果不加入下面设置httpURLConnection.getContentLength()会返回-1
			httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
			// 获取总文件的长度
			int contentLength = httpURLConnection.getContentLength();
			// 获得输入流
			InputStream is = httpURLConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("output")) {
					String keyUrl = getUrl(line);
					Logger.i("key的地址:" + keyUrl);
					arrayList.add(keyUrl);
					continue;
				}

			}

		} catch (Exception e) {
			Logger.i("创建集合失败");
			ism3u8 = false;
			handler.sendEmptyMessage(downloadError);
		}

		// 依次下载视频
		try {

			// 视频总数量
			Logger.i("集合的数据" + arrayList.toString());
			allSize = arrayList.size();
			Logger.i("视频数为" + allSize);
			for (int i = 0; i < arrayList.size(); i++) {
				compeleteSize = 0;
				if (i >= Integer.parseInt(mapKey)) {
					String video_url = arrayList.get(i);
					Logger.i("下载中" + video_url);
					// 根据分段视频的url循环下载文件
					URL urls = new URL(video_url);
					HttpURLConnection httpURLConnection_url = (HttpURLConnection) urls.openConnection();
					// android 2.2版本以上HttpURLConnection跟服务交互采用了"gzip"压缩
					httpURLConnection_url.setRequestProperty("Accept-Encoding", "identity");
					httpURLConnection_url.setRequestMethod("GET");
					length_url = httpURLConnection_url.getContentLength();
					allurlsize += length_url;
					Logger.i("更新视频文件大小" + allurlsize);
					downloadIngindex = i;
					filesize = length_url;
					// 判断sd卡是否有足够空间下载
					if (!PublicUtils.isEnoughForDownload(length_url)) {
						handler.sendEmptyMessage(SdCardSpace);
					}

					InputStream is_ruls = null;
					// 创建urls存放的文件夹
					File path_urls_file = new File(ConstantsDownload.path_urls + urlTotal + id);

					if (!path_urls_file.exists()) {
						path_urls_file.mkdirs();
					}

					// 创建url文件
					File downloadFile_urls = new File(ConstantsDownload.path_urls + urlTotal + id + "//" + i);

					if (downloadFile_urls.exists()) {
						downloadFile_urls.delete();
					}

					urlConnection = (HttpURLConnection) urls.openConnection();
					urlConnection.setRequestProperty("Accept-Encoding", "identity");
					urlConnection.setRequestMethod("GET");

					if (i == Integer.parseInt(mapKey)) {
						Logger.i("视频文件大小" + length_url);
						Logger.i("上次下载结束时的位置downloadedSize" + downloadedSize);
						Logger.i("++downloadSpeed" + downloadSpeed);
						// 设置继续下载的位置
						if (downloadSpeed.equals("-1") && downloadedSize.equals("0")) {
							// 断网后从新联网继续下载
							File pfile = new File(ConstantsDownload.path_urls + urlTotal + "//" + i);// 创建临时文件
							if (pfile.exists() && pfile.length() > 0) {// 读出临时文件中存储的已经下载到的位置

								FileInputStream fis = new FileInputStream(pfile);
								BufferedReader br = new BufferedReader(new InputStreamReader(fis));
								String strsize = br.readLine();
								start = Integer.parseInt(strsize);
								Logger.i("读取下载之前的" + start);
								startPosition += start;// 开始读取时的起始位置
								start = startPosition;
								br.close();

								urlConnection.setRequestProperty("Range", "bytes=" + (startPosition) + "-" + (length_url - 1));
							}

						} else {
							// 暂停后继续下载
							urlConnection.setRequestProperty("Range",
									"bytes=" + (Integer.parseInt(downloadedSize)) + "-" + (Integer.parseInt(downloadSpeed) - 1));
						}

					}

					randomAccessFile = new RandomAccessFile(downloadFile_urls, "rwd");

					if (i == Integer.parseInt(mapKey)) {
						// 定位文件指针到 downloadedSize 位置
						randomAccessFile.setLength(filesize);
						randomAccessFile.seek(Integer.parseInt(downloadedSize));
						Logger.i("继续下载未下载完的视频");
					} else {

						randomAccessFile.setLength(length_url);
						randomAccessFile.seek(0);
					}

					is_ruls = urlConnection.getInputStream();
					byte[] bs_urls = new byte[1024];
					// 读取到的数据长度
					int len_url = -1;
					// 输出的文件流
					// OutputStream os_urls = new
					// FileOutputStream(downloadFile_urls);
					// 开始读取
					startTime = System.currentTimeMillis();
					while ((len_url = is_ruls.read(bs_urls)) != -1) {
						randomAccessFile.write(bs_urls, 0, len_url);
						// os_urls.write(bs_urls, 0, len_url);
						compeleteSize += len_url;

						if (state == PAUSE) {
							// 下载暂停
							handler.sendEmptyMessage(downloadPause);
							Logger.i("下载暂停不在走下载循环");

							return;

						}
					}

					if (is_ruls != null) {
						is_ruls.close();
					}
					finshSize = i;
					Logger.i("已经下载完第" + i + "个视频" + "--" + finshSize);
					// 发送消息通知状态栏更新进度
					handler.sendEmptyMessage(updateProgress);

					Intent intent_broad = new Intent();
					intent_broad.setAction(publicUtils.DOWNING);

					if (finshSize > 0) {
						curTime = System.currentTimeMillis();
						usedTime = (int) ((curTime - startTime) / 1000);
						if (usedTime == 0) {
							usedTime = 1;
						}
						// 下载速度信息
						downloadNetworkSpeed = (compeleteSize / usedTime) / 1024;
						float size = 0;
						String progress = null;
						DecimalFormat format = new DecimalFormat("0.00");
						size = (float) finshSize * 100 / (float) allSize;
						progress = format.format(size);
						Logger.i("视频大小：" + size + "百分比：" + progress);
						intent_broad.putExtra("progress", size);
						intent_broad.putExtra("baifenbi", progress);
						intent_broad.putExtra("downloadNetworkSpeed", downloadNetworkSpeed);
						intent_broad.putExtra("id", id);
						intent_broad.putExtra("maxsize", 100);
						sendBroadcast(intent_broad);

					}

				}
			}

		} catch (Exception e) {
			// 下载失败不写入m3u8文件
			ism3u8 = false;
			Logger.i("下载文件失败.........刚开始");
			handler.sendEmptyMessage(downloadError);
			e.printStackTrace();
		}

		if (ism3u8) {
			createLocalM3u8File(downlodUrl);
		}

	}

	// fcz
	private String getUrl(String str) {
		String[] split = url_vi.split("/");
		if (split.length > 0) {
			return url_vi.replace(split[split.length - 1], str);
		}
		return str;
	}

	/*
	 * 创建本地m3u8文件
	 */
	private boolean createLocalM3u8File(String str_downlodUrl) {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;

		try {
			// 从网络读取m3u8文件
			URL net = new URL(str_downlodUrl);
			is = net.openStream();
			isr = new InputStreamReader(is, "UTF-8");
			br = new BufferedReader(isr);
			// String tempPath = URLDecoder.decode(name, "utf-8");

			// 创建m3u8存放的文件夹
			File path_urls_file = new File(ConstantsDownload.path_m3u8);
			if (!path_urls_file.exists()) {
				path_urls_file.mkdirs();
			}
			// 创建m3u8文件
			File downloadFile_key = new File(ConstantsDownload.path_m3u8 + "//" + urlTotal + id + ".m3u8");

			if (downloadFile_key.exists()) {
				downloadFile_key.delete();
			}

			fos = new FileOutputStream(downloadFile_key);
			osw = new OutputStreamWriter(fos, "UTF-8");
			bw = new BufferedWriter(osw);

			StringBuffer sb = new StringBuffer();
			String head = "#EXTM3U\n#EXT-X-VERSION:3\n#EXT-X-MEDIA-SEQUENCE:0\n#EXT-X-ALLOW-CACHE:YES\n#EXT-X-TARGETDURATION:13\n";

			String lenth = "#EXTINF:";
			String url = "http://127.0.0.1:12345" + ConstantsDownload.path_urls + urlTotal + id + "/";
			String segment = "#EXT-X-DISCONTINUITY";
			String end = "#EXT-X-ENDLIST";
			Logger.i("开始写入m3u8文件");
			bw.write(head);
			String line = "";
			int i = 0;
			while ((line = br.readLine()) != null) {

				if (line.startsWith("#EXTINF:")) {
					// 每段视频时长
					int pos = line.indexOf(":");
					String time = line.substring(pos + 1);
					sb.append(lenth).append(time).append("\n");
				}
				if (line.contains(segment)) {
					sb.append(segment).append("\n");
				}
				if (line.startsWith("output")) {
					sb.append(url).append(i++).append("\n");
				}
			}
			Logger.i("写入的m3u8：" + sb.toString());
			bw.write(sb.toString());
			bw.write(end);

			Logger.i("下载全部完成");
			// 下载完成
			handler.sendEmptyMessage(downloadSuccess);
			return true;
		} catch (Exception e) {
			Logger.i("下载m3u8文件失败");
			handler.sendEmptyMessage(downloadError);
			e.printStackTrace();

		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (osw != null) {
					osw.close();
				}

				if (fos != null) {
					fos.close();
				}

				if (br != null) {
					br.close();
				}

				if (isr != null) {
					isr.close();
				}

				if (is != null) {
					is.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;

	}

	// 显示通知
	private void setUpNotification() {
		Logger.i("我被调用了");
		mNotification = new Notification();
		mNotification.icon = android.R.drawable.stat_sys_download;// 设置通知消息的图标
		mNotification.tickerText = "正在下载...";// 设置通知消息的标题
		mNotification.flags = Notification.FLAG_ONGOING_EVENT; // 放置在"正在运行"栏目中
		remoteViews = new RemoteViews(getPackageName(), R.layout.dowload_notifi);
		remoteViews.setTextViewText(R.id.iv_notify_name, filename); // 视频名字
		remoteViews.setTextViewText(R.id.tv_already_download, "0%"); // 显示下载进度百分比
		remoteViews.setProgressBar(R.id.ProgressBarDown, 100, 0, false);// 设置进度条最大100
																		// 默认0
		// 指定个性化视图
		mNotification.contentView = remoteViews;
		Intent intent = new Intent(Download_Service.this, Download_Manager_Actvity.class);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 指定内容意图
		mNotification.contentIntent = contentIntent;

		mNotificationManager.notify(notificationID, mNotification);
	}

	/**
	 * 广播接收器
	 */
	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.i("接受广播暂");
			String url = intent.getStringExtra("url");
			if (intent.getAction().equals(publicUtils.PAUSE)) {
				if (!StringUtil.isEmpty(url)) {
					if (url.contains(m3u8String)) {
						pause();
					} else {
						if (loader != null) {
							Logger.i("接受广播暂停mp4下载");
							loader.exit();
							isDs = true;
							DataBaseDao.getInstance(Download_Service.this).updata_DownStatus(id, 0);
							mNotificationManager.cancel(notificationID);
						}
					}
				}

			}
		}

	}

	// 设置暂停
	public void pause() {
		state = PAUSE;
	}

	// 重置下载状态
	public void reset() {
		state = INIT;
	}

	// 服务MP4 3GP 下载
	public void DownVideoMp4() {
		DataBaseDao.getInstance(getApplicationContext()).updata_DownStatus(id, 1);
		download();
	}

	public void download() {
		try {

			String path_mp4 = ConstantsDownload.path_m3u8;
			File saveDir_file = new File(path_mp4);
			loader = new FileDownloader(getApplicationContext(), url_vi, saveDir_file, 3);
			maxSize = loader.getFileSize();
			// 判断sd卡是否有足够空间下载
			if (!PublicUtils.isEnoughForDownload(maxSize)) {
				handler.sendEmptyMessage(SdCardSpace);
			}
			Logger.i("文件大小" + maxSize);
			loader.download(new DownloadProgressListener() {
				public void onDownloadSize(int size, int downloadNetworkSpeed) {
					Logger.i("下载大小：" + size);
					Message msg = new Message();
					msg.what = 11;
					msg.getData().putInt("size", size);
					handler.sendMessage(msg);
					float num = (float) size / (float) maxSize;
					int result = (int) (num * 100);
					Intent intent_broad = new Intent();
					intent_broad.setAction(publicUtils.DOWNING);
					intent_broad.putExtra("progress", Float.valueOf(size + ""));
					intent_broad.putExtra("baifenbi", result + "");
					intent_broad.putExtra("picture", picture);
					intent_broad.putExtra("url", url_vi);
					intent_broad.putExtra("id", id);
					intent_broad.putExtra("downloadNetworkSpeed", downloadNetworkSpeed);
					intent_broad.putExtra("maxsize", maxSize);// mp4.3gp
					if (isDs) {
						intent_broad.putExtra("downloadStatus", "0");
					}
					sendBroadcast(intent_broad);

				}

			});
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(22);
		}

	}

}
