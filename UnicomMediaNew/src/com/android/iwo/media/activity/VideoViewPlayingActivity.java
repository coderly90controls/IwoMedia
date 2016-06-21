package com.android.iwo.media.activity;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.controller.DrawHandler.Callback;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.mseekBar;
import com.android.iwo.media.action.mseekBar.OnSeekBarChangeListener_new;
import com.android.iwo.media.activity.MyComment_new.viewHolder;
import com.android.iwo.media.apk.activity.R;
import com.android.iwo.media.dao.DataBaseDao;
import com.android.iwo.media.dao.Download_Service;
import com.android.iwo.media.dao.PublicUtils;
import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.NetworkUtil;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/*
 * 播放广告 1，带广告的视频2，普通视频3  “syte”  标题 “title”
 * */
public class VideoViewPlayingActivity extends BaseActivity implements OnPreparedListener, OnCompletionListener, OnErrorListener,
		OnInfoListener, OnPlayingBufferCacheListener, OnClickListener {
	private String syte;
	private final String TAG = "VideoViewPlayingActivity";

	/**
	 * 您的ak 00
	 */
	private String AK = "3a5E0fPa9E9SQuI50ypHwRF7";
	/**
	 * 您的sk的前16位
	 */
	private String SK = "A4X6a2i578dIgeQ3";
	// VHHmYdXkw7OBIFBG
	private String mVideoSource = null;

	private ImageButton mPlaybtn = null;
	private ImageButton mPrebtn = null;
	private ImageButton mForwardbtn = null;

	private LinearLayout mController = null;

	private SeekBar mProgress = null;
	private TextView mDuration = null;
	private TextView mCurrPostion = null;
	private boolean onClikright_textview = true;
	private BatteryReceiver mBatteryReceiver; // 监听声音的广告
	/**
	 * 记录播放位置
	 */
	private int mLastPos = 0;

	/**
	 * 播放状态
	 */
	private enum PLAYER_STATUS {// 空闲 播放 准备好
		PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
	}

	private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;

	private BVideoView mVV = null;

	private EventHandler mEventHandler;
	private HandlerThread mHandlerThread;

	private final Object SYNC_Playing = new Object();

	private WakeLock mWakeLock = null;
	private static final String POWER_LOCK = "VideoViewPlayingActivity";

	private boolean mIsHwDecode = false;

	private boolean isShare = true;
	private PopupWindow popWindow;

	private final int EVENT_PLAY = 0;
	private final int UI_EVENT_UPDATE_CURRPOSITION = 1;
	private final int TIME = 8668;// 多长时间自动隐藏底部
	private boolean isAnthology = false, isSc = false;
	private String xxString = "http://114.247.0.113/v/m3u8/20150115192120/ci_sha_jin_zheng_en.mp4";// cs剧集播放器
	private boolean isJJ = false;
	private ArrayList<HashMap<String, String>> Series = new ArrayList<HashMap<String, String>>();
	private List<String> text_id = new ArrayList<String>();// 聚集颜色改变
	private int size;
	private int textColor_m = 0xffbcbbbc, textColor_z = 0xff94c894;
	private boolean isrun = true;
	private boolean isSend = false;
	private boolean isFul_mark=false;//是否显示全屏

	private ArrayList<HashMap<String, String>> commentnData = new ArrayList<HashMap<String, String>>();

	class EventHandler extends Handler {
		public EventHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EVENT_PLAY:
				/**
				 * 如果已经播放了，等待上一次播放结束
				 */
				if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
					synchronized (SYNC_Playing) {
						try {
							SYNC_Playing.wait();
							Logger.i("等待玩家状态闲置:");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				/*
				 * 播放器显示风格
				 */
				if (syte != null) {
					if ("1".equals(syte)) {
						mUIHandler.sendEmptyMessage(33);
						mController.setVisibility(View.INVISIBLE);
						vi_play_left_layout.setVisibility(View.INVISIBLE); // 影藏分享
																			// 下载
																			// fcz
						media_progress_layout.setVisibility(View.INVISIBLE);
						// TextView
						// daoshu=(TextView)findViewById(R.id.vi_daoshu);
						// daoshu.setVisibility(View.VISIBLE);
						// initTime(daoshu);
						barShow = false;
					} else {
						mUIHandler.sendEmptyMessageDelayed(22, TIME);
					}
				}
				/**
				 * 设置播放url
				 */
				Logger.i("百度Url：" + mVideoSource);
				mVV.setVideoPath(mVideoSource);
				isJJ = false;// 每次播放 都设定播放完自动关掉播放器
				/**
				 * 续播，如果需要如此
				 */
				if (mLastPos > 0) {

					mVV.seekTo(mLastPos);
					mLastPos = 0;
				}

				/**
				 * 显示或者隐藏缓冲提示
				 */
				mVV.showCacheInfo(true);

				/**
				 * 开始播放
				 */
				mVV.start();

				mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
				break;
			default:
				break;
			}
		}
	}

	Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			/**
			 * 更新进度及时间
			 */
			case UI_EVENT_UPDATE_CURRPOSITION:
				int currPosition = mVV.getCurrentPosition();
				int duration = mVV.getDuration();
				// Logger.i("当前时间："+currPosition+"总时间："+duration);
				updateTextViewWithTimeFormat(mCurrPostion, currPosition);
				updateTextViewWithTimeFormat(mDuration, duration);
				mProgress.setMax(duration);
				mProgress.setProgress(currPosition);

				mUIHandler.sendEmptyMessageDelayed(UI_EVENT_UPDATE_CURRPOSITION, 200);
				break;
			case 22:
				// zly
				if (isShare == false) {
					popWindow.dismiss(); // Close the Pop Window
					setImg(R.id.play_share, R.drawable.vi_share);
					setTextColor(R.id.play_share_text, textColor_m);
					isShare = true;
				}

				mController.setVisibility(View.INVISIBLE);
				videoview_top.setVisibility(View.INVISIBLE);
				vi_play_left_layout.setVisibility(View.INVISIBLE); // 影藏分享 下载
																	// fcz
				media_progress_layout.setVisibility(View.INVISIBLE);
				gr_layout.setVisibility(View.INVISIBLE);
				if (anthology.getVisibility() == View.VISIBLE) {
					anthology.setBackgroundResource(R.drawable.vi_play_anthology);
					isAnthology = false;
				}
				barShow = false;
				break;
			case 33:
				videoview_top.setVisibility(View.INVISIBLE);
				barShow = false;
				break;
			case 44:
				String str = msg.getData().getString("comm_content");
				addDanmaku(false, str);
				break;

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.controllerplaying);
		video_Id = this.getIntent().getStringExtra("video_id");
		orShare = getIntent().getStringExtra("orshare");// 1 是收藏 0 没收藏
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		video_map = (HashMap<String, String>) getIntent().getSerializableExtra("data");
		Logger.i("详情:" + video_map);
		ArrayList<String> videoIds = DataBaseDao.getInstance(VideoViewPlayingActivity.this).getVideoIds();
		Logger.i("数据库m3u8的ID：" + videoIds);
		if ("1".equals(orShare)) {
			isSc = true;
			setImg(R.id.play_shouc, R.drawable.vi_shouc_z);
			setTextColor(R.id.play_shouc_text, textColor_z);
		} else {
			isSc = false;
			setImg(R.id.play_shouc, R.drawable.vi_shouc);
			setTextColor(R.id.play_shouc_text, textColor_m);
		}
		Logger.i("播放器ID" + video_Id + "收藏状态" + orShare);

		danmakuView = (IDanmakuView) findViewById(R.id.sv_danmaku);
		DanmakuGlobalConfig.DEFAULT.setDanmakuStyle(DanmakuGlobalConfig.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(false);
		if (danmakuView != null) {
			mParser = createParser(this.getResources().openRawResource(R.raw.comments));
			danmakuView.setCallback(new Callback() {

				@Override
				public void updateTimer(DanmakuTimer timer) {

				}

				@Override
				public void prepared() {
					danmakuView.start();
				}
			});
			danmakuView.prepare(mParser);
			// danmakuView.showFPS(true);
			danmakuView.enableDanmakuDrawingCache(true);
			((View) danmakuView).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					updateControlBar(!barShow);

				}
			});
		}

		new GetJJ().execute(video_Id);// 播放器获取剧集
		new GetCommentData().execute();// 获取评论
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, POWER_LOCK);
		syte = getIntent().getStringExtra("syte");
		mIsHwDecode = getIntent().getBooleanExtra("isHW", false);
		Uri uriPath = getIntent().getData();
		if (null != uriPath) {
			String scheme = uriPath.getScheme();
			if (null != scheme) {
				mVideoSource = uriPath.toString();
			} else {
				mVideoSource = uriPath.getPath();
			}
		}

		initUI();

		/**
		 * 开启后台事件处理线程
		 */
		mHandlerThread = new HandlerThread("event handler thread", Process.THREAD_PRIORITY_BACKGROUND);
		mHandlerThread.start();
		mEventHandler = new EventHandler(mHandlerThread.getLooper());

	}

	/**
	 * 初始化界面
	 */
	private void initUI() {
		mtextButton = (TextView) findViewById(R.id.play_btn_tm);
		comments_popup = (RelativeLayout) findViewById(R.id.comments_popup);
		comments_popup_edit = (EditText) findViewById(R.id.comments_popup_edit);

		send_tm = (TextView) findViewById(R.id.send_tm);
		mtextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isrun) {
					mtextButton.setText("弹幕");
					danmakuView.hide();

				} else {
					mtextButton.setText("关闭");
					danmakuView.show();

				}
				isrun = !isrun;

			}
		});
		send_tm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isSend) {
					send_tm.setText("发布");
					comments_popup.setVisibility(View.GONE);
					imm.hideSoftInputFromWindow(comments_popup_edit.getWindowToken(), 0);
				} else {
					comments_popup.setVisibility(View.VISIBLE);
					send_tm.setText("取消");
					comments_popup_edit.setFocusable(true);
					comments_popup_edit.setFocusableInTouchMode(true);
					comments_popup_edit.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(comments_popup_edit, InputMethodManager.RESULT_SHOWN);
					imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
				}
				isSend = !isSend;
			}
		});
		findViewById(R.id.comments_popup_text).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String str = comments_popup_edit.getText().toString();
				if (!StringUtil.isEmpty(str)) {
					addDanmaku(false, str);
					comments_popup.setVisibility(View.GONE);
					imm.hideSoftInputFromWindow(comments_popup_edit.getWindowToken(), 0);
					send_tm.setText("发布");
					isSend = false;
				} else {
					makeText("内容不能为空");
				}
			}
		});
		// 左边文字颜色
		registerBroadcast();
		gr = (GridView) findViewById(R.id.gridview);
		gr_layout = (RelativeLayout) findViewById(R.id.gr_layout);
		adapter = new Myadapter();
		gr.setAdapter(adapter);
		mPlaybtn = (ImageButton) findViewById(R.id.play_btn);
		videoview_top = (LinearLayout) findViewById(R.id.vi_top);
		mController = (LinearLayout) findViewById(R.id.controlbar);
		vi_play_left_layout = (LinearLayout) findViewById(R.id.vi_play_left_layout);
		media_progress_layout = (LinearLayout) findViewById(R.id.media_progress_layout);
		mProgress = (SeekBar) findViewById(R.id.media_progress);
		mDuration = (TextView) findViewById(R.id.time_total);
		mCurrPostion = (TextView) findViewById(R.id.time_current);
		parse_img = (ImageView) findViewById(R.id.video_parse_img);
		TextView name_title = (TextView) findViewById(R.id.vi_title);
		name_title.setText(getIntent().getStringExtra("title"));
		registerCallbackForControl();
		anthology = (ImageView) findViewById(R.id.anthology);
		vi_play_share = (LinearLayout) findViewById(R.id.vi_play_share);
		vi_play_shouc = (LinearLayout) findViewById(R.id.vi_play_shouc);
		vi_play_cache = (LinearLayout) findViewById(R.id.vi_play_cache);
		vi_play_va = (ImageView) findViewById(R.id.vi_play_va);// 声音
		bar = (mseekBar) findViewById(R.id.media_progress_volume);
		am = (AudioManager) VideoViewPlayingActivity.this.getSystemService(Context.AUDIO_SERVICE);
		int sun = am.getStreamVolume(AudioManager.STREAM_MUSIC);// 获取当前的音量
		if (sun == 0) {
			vi_play_va.setBackgroundResource(R.drawable.volume_null);
		} else {
			vi_play_va.setBackgroundResource(R.drawable.volume);
		}
		bar.setProgress(sun);
		bar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)); // SEEKBAR设置为音量的最大阶数
		bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener_new() {

			@Override
			public void onStopTrackingTouch(mseekBar vBar) {
				mUIHandler.sendEmptyMessageDelayed(22, TIME);
			}

			@Override
			public void onStartTrackingTouch(mseekBar vBar) {
				mUIHandler.removeMessages(22);
			}

			@Override
			public void onProgressChanged(mseekBar vBar, int progress, boolean fromUser) {
				if (progress == 0) {
					vi_play_va.setBackgroundResource(R.drawable.volume_null);
				} else {
					vi_play_va.setBackgroundResource(R.drawable.volume);
				}
				am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

			}
		});
		findViewById(R.id.jj_xian_01).setOnClickListener(this);
		findViewById(R.id.jj_xian_02).setOnClickListener(this);
		findViewById(R.id.jj_xian_03).setOnClickListener(this);
		findViewById(R.id.jj_xian_04).setOnClickListener(this);
		findViewById(R.id.vi_return).setOnClickListener(this);
		findViewById(R.id.video_parse_img_delete).setOnClickListener(this);
		anthology.setOnClickListener(this);
		vi_play_share.setOnClickListener(this);
		vi_play_shouc.setOnClickListener(this);
		vi_play_cache.setOnClickListener(this);
		gr.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {

				HashMap<String, String> map = null;
				if ("1".equals(mark)) {
					if (size <= pos) {
						return;
					}
					map = Series.get(pos);
				}
				if ("2".equals(mark)) {
					if (size <= pos + 25) {
						return;
					}
					map = Series.get(pos + 25);
				}
				if ("3".equals(mark)) {
					if (size <= pos + 50) {
						return;
					}
					map = Series.get(pos + 50);
				}
				if ("4".equals(mark)) {
					if (size <= (pos + 75)) {
						return;
					}
					map = Series.get(pos + 75);
				}

				if (!text_id.contains(map.get("id"))) {
					text_id.clear();
					text_id.add(map.get("id"));
					adapter.notifyDataSetChanged();
				}
				isJJ = true;
				if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
					mVV.stopPlayback();
				}
				String str = map.get("series_url");

				// 发起一次新的播放任务

				if (mEventHandler.hasMessages(EVENT_PLAY))
					mEventHandler.removeMessages(EVENT_PLAY);
				Uri uriPath = Uri.parse(str);
				if (null != uriPath) {
					String scheme = uriPath.getScheme();
					if (null != scheme) {
						mVideoSource = uriPath.toString();
					} else {
						mVideoSource = uriPath.getPath();
					}
					syte = "2";
					mEventHandler.sendEmptyMessage(EVENT_PLAY);
				}
				mUIHandler.sendEmptyMessageDelayed(22, TIME);
			}
		});
		/**
		 * 设置ak及sk的前16位
		 */
		BVideoView.setAKSK(AK, SK);

		/**
		 * 获取BVideoView对象
		 */
		mVV = (BVideoView) findViewById(R.id.video_view);
		ful_mark = (TextView)findViewById(R.id.ful_mark);
		ful_mark.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int mode=2;
				if(isFul_mark){
					ful_mark.setText("全屏");
				}
				else{
					mode=1;
					ful_mark.setText("原画");
				}
				mVV.setVideoScalingMode(mode);
				isFul_mark=!isFul_mark;
			}
		});
		/**
		 * 注册listener
		 */
		mVV.setOnPreparedListener(this);
		mVV.setOnCompletionListener(this);
		mVV.setOnErrorListener(this);
		mVV.setOnInfoListener(this);

		/**
		 * 设置解码模式
		 */
		mVV.setDecodeMode(mIsHwDecode ? BVideoView.DECODE_HW : BVideoView.DECODE_SW);

	}

	// 广告倒计时
	private void initTime(final TextView te) {
		final Handler handler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				String data = (String) msg.getData().get("key");
				te.setText("广告还有" + data + "秒");

				return false;
			}
		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				// int ii = mVV.getDuration();
				// int i = mVV.getCurrentPosition();
				int i = 80;
				// Logger.i("总时长："+i+"12:"+ii);
				while (i > 0) {
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putString("key", "" + i);
					message.setData(bundle);
					message.what = i;
					handler.sendMessage(message);
					i--;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	/**
	 * 为控件注册回调处理函数
	 */
	private void registerCallbackForControl() {
		mPlaybtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String image = getIntent().getStringExtra("image");
				Logger.i("暂停图片" + image);
				if (mVV.isPlaying()) {
					// mPlaybtn.setImageResource(R.drawable.play_btn_style);
					mPlaybtn.setBackgroundResource(R.drawable.play_btn_style);
					if (!StringUtil.isEmpty(image)) {
						findViewById(R.id.video_parse_layout).setVisibility(View.VISIBLE);
						LoadBitmap.getIntence().loadImage(image, parse_img);
					}
					/**
					 * 暂停播放
					 */
					mVV.pause();
				} else {
					// mPlaybtn.setImageResource(R.drawable.pause_btn_style);
					mPlaybtn.setBackgroundResource(R.drawable.pause_btn_style);
					findViewById(R.id.video_parse_layout).setVisibility(View.GONE);

					/**
					 * 继续播放
					 */
					mVV.resume();
				}

			}
		});

		OnSeekBarChangeListener osbc1 = new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				// Log.v(TAG, "progress: " + progress);
				updateTextViewWithTimeFormat(mCurrPostion, progress);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				/**
				 * SeekBar开始seek时停止更新
				 */
				mUIHandler.removeMessages(22);
				mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				int iseekPos = seekBar.getProgress();
				/**
				 * SeekBark完成seek时执行seekTo操作并更新界面
				 * 
				 */
				mVV.seekTo(iseekPos);
				Logger.i("seek to " + iseekPos);
				mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
				mUIHandler.sendEmptyMessageDelayed(22, TIME);
			}
		};
		mProgress.setOnSeekBarChangeListener(osbc1);

	}

	private void updateTextViewWithTimeFormat(TextView view, int second) {
		int hh = second / 3600;
		int mm = second % 3600 / 60;
		int ss = second % 60;
		String strTemp = null;
		if (0 != hh) {
			strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
		} else {
			strTemp = String.format("%02d:%02d", mm, ss);
		}
		view.setText(strTemp);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 退出播放器时销毁popWindow，否则报错误:java.lang.IllegalArgumentException: View not
		// attached to window manager
		if (popWindow == null) {
			Logger.i("此处为null。。。");
		} else {
			if (popWindow.isShowing()) {
				Logger.i("++++.oumeiyou?????");
				popWindow.dismiss();
			}
		}
		/**
		 * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
		 */
		if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
			mLastPos = mVV.getCurrentPosition();
			mVV.stopPlayback();
		}

		if (danmakuView != null && danmakuView.isPrepared()) {
			danmakuView.pause();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Logger.i("onResume");
		if (null != mWakeLock && (!mWakeLock.isHeld())) {
			mWakeLock.acquire();
		}
		/**
		 * 发起一次播放任务,当然您不一定要在这发起
		 */
		mEventHandler.sendEmptyMessage(EVENT_PLAY);
		if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
			danmakuView.resume();
		}

	}

	private long mTouchTime;
	private boolean barShow = true;
	private ImageView parse_img;
	private LinearLayout videoview_top;
	private String video_Id;
	private HashMap<String, String> video_map;
	private GridView gr;
	private ImageView anthology;
	private LinearLayout vi_play_left_layout;
	private LinearLayout vi_play_share;
	private LinearLayout vi_play_shouc;
	private LinearLayout vi_play_cache;
	private ImageView vi_play_va;
	private LinearLayout media_progress_layout;
	private Myadapter adapter;
	private RelativeLayout gr_layout;

	final String appID = "wx7369dbf242da5ae3";
	final String appSecret = "e7fe1c897e07e68118b264ddab35ebf4";
	final UMSocialService mVideoController_share = UMServiceFactory.getUMSocialService("com.umeng.share");

	private void myVideoShareSet() {

		// 设置分享图片, 参数2为图片的url地址
		mVideoController_share.setShareMedia(new UMImage(mContext, video_map.get("img_url_2")));

		mVideoController_share.setShareContent(video_map.get("name") + "：" + video_map.get("note") + "http://iwotv.cn/share/video_info?id="
				+ video_map.get("id") + "&ch_id=" + video_map.get("ch_id"));// mapVideo.get("video_url")

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (syte != null && "1".equals(syte)) {
			if (event.getAction() == MotionEvent.ACTION_DOWN)
				mTouchTime = System.currentTimeMillis();
			else if (event.getAction() == MotionEvent.ACTION_UP) {
				long time = System.currentTimeMillis() - mTouchTime;
				if (time < 400) {
					if (barShow) {
						videoview_top.setVisibility(View.INVISIBLE);
						mUIHandler.removeMessages(33);
					} else {
						videoview_top.setVisibility(View.VISIBLE);
						mUIHandler.sendEmptyMessageDelayed(33, TIME);
					}
					barShow = !barShow;
				}
			}

			return true;
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mTouchTime = System.currentTimeMillis();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			long time = System.currentTimeMillis() - mTouchTime;
			if (time < 400) {
				if (gr_layout.getVisibility() == View.VISIBLE) { // 当剧集显示的时候
					isAnthology = false;
					anthology.setBackgroundResource(R.drawable.vi_play_anthology);
					barShow = false;
					videoview_top.setVisibility(View.INVISIBLE);
					gr_layout.setVisibility(View.INVISIBLE);
					mController.setVisibility(View.INVISIBLE);
					media_progress_layout.setVisibility(View.INVISIBLE);
					vi_play_left_layout.setVisibility(View.INVISIBLE); // 影藏分享
																		// 下载
					// zly
					if (isShare == false) {
						popWindow.dismiss(); // Close the Pop Window
						setImg(R.id.play_share, R.drawable.vi_share);
						setTextColor(R.id.play_share_text, textColor_m);
						isShare = true;
					}
					return true;
				} else {
					updateControlBar(!barShow);
				}
			}
		}

		return true;
	}

	public void updateControlBar(boolean show) {
		if (comments_popup.getVisibility() == View.VISIBLE) {
			comments_popup.setVisibility(View.GONE);
			imm.hideSoftInputFromWindow(comments_popup_edit.getWindowToken(), 0);
			send_tm.setText("发布");
			isSend = false;
		}
		if (show) {
			mController.setVisibility(View.VISIBLE);
			videoview_top.setVisibility(View.VISIBLE);
			vi_play_left_layout.setVisibility(View.VISIBLE); // 影藏分享 下载 fcz
			media_progress_layout.setVisibility(View.VISIBLE);
			mUIHandler.sendEmptyMessageDelayed(22, TIME);
		} else {
			mUIHandler.removeMessages(22);
			vi_play_left_layout.setVisibility(View.INVISIBLE);
			mController.setVisibility(View.INVISIBLE);
			vi_play_left_layout.setVisibility(View.INVISIBLE); // 影藏分享 下载 fcz
			videoview_top.setVisibility(View.INVISIBLE);
			media_progress_layout.setVisibility(View.INVISIBLE);

			// zly
			if (isShare == false) {
				popWindow.dismiss(); // Close the Pop Window
				setImg(R.id.play_share, R.drawable.vi_share);
				setTextColor(R.id.play_share_text, textColor_m);
				isShare = true;
			}
		}
		barShow = show;
	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		/**
		 * 退出后台事件处理线程
		 */
		unRegisterBroadcast();
		mHandlerThread.quit();
		if (danmakuView != null) {
			danmakuView.release();
			danmakuView = null;
		}
		Logger.i("退出后台事件处理线程");
	}

	@Override
	public boolean onInfo(int what, int extra) {
		// TODO Auto-generated method stub
		switch (what) {
		/**
		 * 开始缓冲
		 */
		case BVideoView.MEDIA_INFO_BUFFERING_START:
			Logger.i("开始缓冲");
			break;
		/**
		 * 结束缓冲
		 */
		case BVideoView.MEDIA_INFO_BUFFERING_END:
			Logger.i("结束缓冲");
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 当前缓冲的百分比， 可以配合onInfo中的开始缓冲和结束缓冲来显示百分比到界面
	 */
	@Override
	public void onPlayingBufferCache(int percent) {

		Logger.i("当前缓冲的百分比:"+percent);

	}

	/**
	 * 播放出错
	 */
	@Override
	public boolean onError(int what, int extra) {
		// TODO Auto-generated method stub
		Logger.i("onError");
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
		mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
		setResult(23);
		finish();
		return true;
	}

	/**
	 * 播放完成
	 */
	@Override
	public void onCompletion() {
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
		mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
		if (syte != null && "1".equals(syte)) {
			if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
				mVV.stopPlayback();
			}
			/**
			 * 发起一次新的播放任务
			 */
			mEventHandler.removeMessages(EVENT_PLAY);
			String url = getIntent().getStringExtra("video_url");
			Uri uriPath = Uri.parse(url);
			if (null != uriPath) {
				String scheme = uriPath.getScheme();
				if (null != scheme) {
					mVideoSource = uriPath.toString();
				} else {
					mVideoSource = uriPath.getPath();
				}
				syte = "2";
				mEventHandler.sendEmptyMessage(EVENT_PLAY);
			}

		} else {
			// 对剧集判断
			if (!isJJ) {
				if (danmakuView != null) {
					danmakuView.release();
					danmakuView = null;
				}
				Intent data = new Intent();
				data.putExtra("orshare", orShare);
				setResult(333, data);
				finish();
			}
		}
	}

	/**
	 * 准备播放就绪
	 */
	@Override
	public void onPrepared() {
		Logger.i("准备缓存视频onPrepared：");
		mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
		mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
	}

	private boolean is_01 = false, is_02 = false, is_03 = false, is_04 = false;
	private boolean isData = false;// 判断是不是满一页
	private String mark = "1";
	private String orShare;
	private AudioManager am;
	private mseekBar bar;
	// private TextView mtextView;
	private TextView mtextButton;
	private IDanmakuView danmakuView;
	private BaseDanmakuParser mParser;
	private TextView send_tm;
	private RelativeLayout comments_popup;
	private EditText comments_popup_edit;
	private InputMethodManager imm;
	private TextView ful_mark;

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.jj_xian_01:
			if (is_01) {
				return;
			}
			setbooelan();
			if (size > 25) {
				isData = true;
			} else {
				isData = false;
			}
			is_01 = true;
			setColormoren();
			setTextColor(R.id.jj_xian_01, textColor_z);
			mark = "1";
			adapter.notifyDataSetChanged();
			break;
		case R.id.jj_xian_02:
			if (is_02) {
				return;
			}
			setbooelan();
			if (size > 50) {
				isData = true;
			} else {
				isData = false;
			}
			is_02 = true;
			setColormoren();
			setTextColor(R.id.jj_xian_02, textColor_z);
			mark = "2";
			adapter.notifyDataSetChanged();
			break;
		case R.id.jj_xian_03:
			if (is_03) {
				return;
			}
			setbooelan();
			if (size > 75) {
				isData = true;
			} else {
				isData = false;
			}
			is_03 = true;
			setColormoren();
			setTextColor(R.id.jj_xian_03, textColor_z);
			mark = "3";
			adapter.notifyDataSetChanged();
			break;
		case R.id.jj_xian_04:
			if (is_04) {
				return;
			}
			setbooelan();
			isData = false;
			is_04 = true;
			setColormoren();
			setTextColor(R.id.jj_xian_04, textColor_z);
			mark = "4";
			adapter.notifyDataSetChanged();
			break;
		case R.id.vi_return:
			Intent data = new Intent();
			data.putExtra("orshare", orShare);
			setResult(333, data);
			finish();
			break;
		case R.id.video_parse_img_delete:
			findViewById(R.id.video_parse_layout).setVisibility(View.GONE);
			break;
		case R.id.anthology:
			if (!isAnthology) {
				mUIHandler.removeMessages(22);
				barShow = true;
				gr_layout.setVisibility(View.VISIBLE);
				anthology.setBackgroundResource(R.drawable.vi_play_anthology_z);
				// 对显示卡配置

			} else {
				anthology.setBackgroundResource(R.drawable.vi_play_anthology);
				gr_layout.setVisibility(View.GONE);
				mUIHandler.sendEmptyMessageDelayed(22, TIME);
			}
			isAnthology = !isAnthology;
			break;
		case R.id.vi_play_share:
			if (isShare) {
				Logger.i("要分享" + video_map);
				setImg(R.id.play_share, R.drawable.vi_share_z);
				setTextColor(R.id.play_share_text, textColor_z);
				showPopWindow(mContext, mVV);
			} else {
				popWindow.dismiss(); // Close the Pop Window
				setImg(R.id.play_share, R.drawable.vi_share);
				setTextColor(R.id.play_share_text, textColor_m);
			}
			isShare = !isShare;
			// Intent intent = new Intent(Intent.ACTION_SEND);
			// intent.setType("text/plain");
			// intent.putExtra(Intent.EXTRA_SUBJECT, "EXTRA_SUBJECT");
			// intent.putExtra(Intent.EXTRA_TEXT, "分享的内容...");
			// startActivity(Intent.createChooser(intent, "爱握分享"));
			break;
		case R.id.vi_play_shouc:
			if (onClikright_textview) {
				onClikright_textview = false;
				if (isSc) {
					setImg(R.id.play_shouc, R.drawable.vi_shouc);
					setTextColor(R.id.play_shouc_text, textColor_m);
				} else {
					setImg(R.id.play_shouc, R.drawable.vi_shouc_z);
					setTextColor(R.id.play_shouc_text, textColor_z);
				}
				isSc = !isSc;
				new setVideoChaesOr().execute();
			}
			break;
		case R.id.vi_play_cache:// 下载

			// 首先判断数据库有没有这个视频的ID
			if (NetworkUtil.isWIFIConnected(mContext)) {
				DownVideo(video_Id, video_map.get("ch_id"), video_map.get("name"), video_map.get("video_url"), video_map.get("img_url_2"),
						video_map.get("ping_fen"));
			} else {
				makeText("只在wifi下支持下载");
			}

			break;
		}

	}

	private void setImg(int id, int resid) {
		ImageView img = (ImageView) findViewById(id);
		img.setBackgroundResource(resid);
	}

	private class Myadapter extends BaseAdapter {

		@Override
		public int getCount() {

			return 25;
		}

		@Override
		public Object getItem(int position) {
			if ("2".equals(mark)) {
				return position + 1 + 25;
			}
			if ("3".equals(mark)) {
				return position + 1 + 50;
			}
			if ("4".equals(mark)) {
				return position + 1 + 75;
			}
			return position + 1;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(VideoViewPlayingActivity.this);
			convertView = inflater.inflate(R.layout.gridview_item, null);
			if (size % 25 > position || isData) {
				TextView textView = (TextView) convertView.findViewById(R.id.gr_item_text);
				HashMap<String, String> hashMap = null;
				if ("1".equals(mark)) {
					hashMap = Series.get(position);
				} else if ("2".equals(mark)) {
					hashMap = Series.get(position + 25);
				} else if ("3".equals(mark)) {
					hashMap = Series.get(position + 50);
				} else if ("4".equals(mark)) {
					hashMap = Series.get(position + 75);
				}

				if (text_id.contains(hashMap.get("id"))) {
					textView.setTextColor(textColor_z);
				} else {
					textView.setTextColor(textColor_m);// 默认颜色
				}
				DecimalFormat Df = new DecimalFormat("00");// 用这个工具 可以补0
				textView.setText(Df.format(getItem(position)));

			}
			return convertView;
		}
	}

	private class GetJJ extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {

			return DataRequest.getArrayListFromUrl_Base64(getUrl(AppConfig.NEW_V_GET_VIDEO_LIST_NEW_SERIES), params[0]);
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			super.onPostExecute(result);
			if (result != null && result.size() > 0) {
				anthology.setVisibility(View.VISIBLE);
				Series.clear();
				Series.addAll(result);
				size = Series.size();
				// size = 78;
				adapter.notifyDataSetChanged();

				is_01 = true;
				if (size <= 25) {
					isData = false;
					setTextColor(R.id.jj_xian_01, textColor_z);
					setTexttext(R.id.jj_xian_01, size, 1);
				}
				if (size > 25 && size <= 50) {
					isData = true;
					findViewById(R.id.jj_xian_02).setVisibility(View.VISIBLE);
					setTextColor(R.id.jj_xian_01, textColor_z);
					setTexttext(R.id.jj_xian_01, 25, 1);
					setTextColor(R.id.jj_xian_02, textColor_m);
					setTexttext(R.id.jj_xian_02, size, 26);
				} else if (size > 50 && size <= 75) {
					isData = true;
					findViewById(R.id.jj_xian_02).setVisibility(View.VISIBLE);
					findViewById(R.id.jj_xian_03).setVisibility(View.VISIBLE);
					setTextColor(R.id.jj_xian_01, textColor_z);
					setTexttext(R.id.jj_xian_01, 25, 1);
					setTextColor(R.id.jj_xian_02, textColor_m);
					setTexttext(R.id.jj_xian_02, 50, 26);
					setTextColor(R.id.jj_xian_03, textColor_m);
					setTexttext(R.id.jj_xian_03, size, 51);
				} else if (size > 75) {
					isData = true;
					findViewById(R.id.jj_xian_02).setVisibility(View.VISIBLE);
					findViewById(R.id.jj_xian_03).setVisibility(View.VISIBLE);
					findViewById(R.id.jj_xian_04).setVisibility(View.VISIBLE);
					setTextColor(R.id.jj_xian_01, textColor_z);
					setTexttext(R.id.jj_xian_01, 25, 1);
					setTextColor(R.id.jj_xian_02, textColor_m);
					setTexttext(R.id.jj_xian_02, 50, 26);
					setTextColor(R.id.jj_xian_03, textColor_m);
					setTexttext(R.id.jj_xian_03, 75, 51);
					setTextColor(R.id.jj_xian_04, textColor_m);
					setTexttext(R.id.jj_xian_04, size, 76);
				}
			} else {
				anthology.setVisibility(View.GONE);
			}
		}
	}

	public void setTextColor(int id, int color) {
		TextView te = (TextView) findViewById(id);
		te.setTextColor(color);
	}

	public void setTexttext(int id, int end, int start) {
		TextView te = (TextView) findViewById(id);
		te.setText(start + "-" + end);
	}

	private void setColormoren() {
		setTextColor(R.id.jj_xian_01, textColor_m);
		setTextColor(R.id.jj_xian_02, textColor_m);
		setTextColor(R.id.jj_xian_03, textColor_m);
		setTextColor(R.id.jj_xian_04, textColor_m);
	};

	private void setbooelan() {
		is_01 = false;
		is_02 = false;
		is_03 = false;
		is_04 = false;
	}

	private class setVideoChaesOr extends AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			if ("1".equals(orShare)) {
				orShare = "0";
			} else if ("0".equals(orShare)) {
				orShare = "1";
			}
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_UN_VIDEO_GET_SHARE), video_Id, orShare, null);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					if ("1".equals(orShare)) {
						makeText("收藏成功");
						setImg(R.id.play_shouc, R.drawable.vi_shouc_z);
						setTextColor(R.id.play_shouc_text, textColor_z);
					} else {
						makeText("取消收藏");
						setImg(R.id.play_shouc, R.drawable.vi_shouc);
						setTextColor(R.id.play_shouc_text, textColor_m);
					}
				}
			} else {
				if ("1".equals(orShare)) {
					orShare = "0";
				} else if ("0".equals(orShare)) {
					orShare = "1";
				}
			}
			onClikright_textview = true;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent data = new Intent();
			data.putExtra("orshare", orShare);
			setResult(333, data);
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 这是一个启动分享框的方法
	 * 
	 * @param context
	 * @param parent
	 */
	private void showPopWindow(Context context, View parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View vPopWindow = inflater.inflate(R.layout.mypop, null, false);
		// 1、宽400 高230 2、400 374
		popWindow = new PopupWindow(vPopWindow, 400, 230, false);
		myVideoShareSet();
		vPopWindow.findViewById(R.id.iv_sina).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mVideoController_share.getConfig().setSsoHandler(new SinaSsoHandler());
				shareWithPlatform(mContext, SHARE_MEDIA.SINA);
				popWindow.dismiss();
			}
		});
		vPopWindow.findViewById(R.id.iv_wechat).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 添加微信平台
				UMWXHandler wxHandler = new UMWXHandler(mContext, appID, appSecret);
				wxHandler.addToSocialSDK();
				// 设置微信好友分享内容
				WeiXinShareContent weixinContent = new WeiXinShareContent();
				// 设置分享文字
				weixinContent.setShareContent(video_map.get("name") + "：" + video_map.get("note") + "http://iwotv.cn/share/video_info?id="
						+ video_map.get("id") + "&ch_id=" + video_map.get("ch_id"));
				// 设置title
				weixinContent.setTitle("爱握视频");
				// 设置分享内容跳转URL
				weixinContent.setTargetUrl("http://iwotv.cn/share/video_info?id=" + video_map.get("id") + "&ch_id="
						+ video_map.get("ch_id"));//
				// 设置分享图片
				weixinContent.setShareImage(new UMImage(mContext, video_map.get("img_url_2")));
				mVideoController_share.setShareMedia(weixinContent);
				shareWithPlatform(mContext, SHARE_MEDIA.WEIXIN);
				popWindow.dismiss();
			}
		});
		vPopWindow.findViewById(R.id.iv_wefriends).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// 添加微信朋友圈
				UMWXHandler wxCircleHandler = new UMWXHandler(mContext, appID, appSecret);
				wxCircleHandler.setToCircle(true);
				wxCircleHandler.addToSocialSDK();
				// 设置微信朋友圈分享内容
				CircleShareContent circleMedia = new CircleShareContent();
				circleMedia.setShareContent(video_map.get("name") + "：" + video_map.get("note") + "http://iwotv.cn/share/video_info?id="
						+ video_map.get("id") + "&ch_id=" + video_map.get("ch_id"));
				// 设置朋友圈title
				circleMedia.setTitle("爱握视频");
				circleMedia.setShareImage(new UMImage(mContext, video_map.get("img_url_2")));
				circleMedia.setTargetUrl("http://iwotv.cn/share/video_info?id=" + video_map.get("id") + "&ch_id=" + video_map.get("ch_id"));// mapVideo.get("video_url")
				mVideoController_share.setShareMedia(circleMedia);
				shareWithPlatform(mContext, SHARE_MEDIA.WEIXIN_CIRCLE);
				popWindow.dismiss();
			}
		});

		popWindow.setAnimationStyle(R.style.PopupAnimation);
		// 78，-62
		popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);// 显示为 左侧位置
		// popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);//显示为 中间
		// popWindow.setFocusable(true);
		// popWindow.setTouchable(true);
		popWindow.update();

	}

	private void shareWithPlatform(final Context context, SHARE_MEDIA plat) {
		// TODO Auto-generated method stub
		mVideoController_share.postShare(context, plat, new SnsPostListener() {
			@Override
			public void onStart() {
				Toast.makeText(context, "开始分享", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
				if (eCode == 200) {
					Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
				} else {
					String eMsg = "";
					if (eCode == -101) {
						eMsg = "没有授权";
					}
					Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	// 下载
	private void DownVideo(String video_Id, String ch_id, String video_name, String video_url, String video_img_url, String ping_fen) {
		if (!PublicUtils.hasSdcard()) {
			makeText("内容卡不存在");
			return;
		}
		// 获得数据库中所以的视频id
		ArrayList<String> videoIds = DataBaseDao.getInstance(VideoViewPlayingActivity.this).getVideoIds();
		Logger.i("数据库的ID：" + videoIds);
		if (!videoIds.contains(video_Id)) {
			// 查询数据库中所以下载状态
			ArrayList<String> query_All_DownStatus = DataBaseDao.getInstance(VideoViewPlayingActivity.this).query_All_DownStatus();
			if (query_All_DownStatus.contains("1")) { // “1”
				Logger.i("不把视频数据传给服务");
			} else {
				Intent intent = new Intent(VideoViewPlayingActivity.this, Download_Service.class);
				intent.putExtra("_id", video_Id);// 视频id
				intent.putExtra("urlTotal", ch_id);// 父ID
				intent.putExtra("filename", video_name);// 视频名
				intent.putExtra("url", video_url);// 视频播放地址
				intent.putExtra("picture", video_img_url);// 视频的图片地址
				intent.putExtra("foldername", ping_fen);// 视频评分
				intent.putExtra("mapKey", "-1");// 下载到第几个url
				intent.putExtra("downloadedSize", "0");// 下载完成度
				intent.putExtra("downloadSpeed", "0");// 文件写入了多少
				mContext.startService(intent);

			}
			// 将数据添加到数据库
			DataBaseDao.getInstance(VideoViewPlayingActivity.this).initDownloadData(video_Id, ch_id, video_name, video_url, video_img_url,
					ping_fen, 0, 0, 0);
			makeText("成功添加至下载列表..");

		} else {
			makeText("已经在下载列表了");
		}

	}

	/**
	 * 注册广播
	 */
	private void registerBroadcast() {
		mBatteryReceiver = new BatteryReceiver();
		IntentFilter intent = new IntentFilter();
		intent.addAction("android.media.VOLUME_CHANGED_ACTION");
		registerReceiver(mBatteryReceiver, intent);

	}

	/**
	 * 销毁广播
	 */
	private void unRegisterBroadcast() {
		if (mBatteryReceiver != null)
			unregisterReceiver(mBatteryReceiver);
	}

	private class BatteryReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int valume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
			bar.setProgress(valume);
			bar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)); // SEEKBAR设置为音量的最大阶数

		}

	}

	private class GetCommentData extends AsyncTask<String, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			// "103065"
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_VIDEO_COMMENTS_LIST_DAY), video_Id);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			super.onPostExecute(result);
			if (result != null && "1".equals(result.get("code"))) { // “comm_content”
				ArrayList<HashMap<String, String>> list = DataRequest.getArrayListFromJSONArrayString(result.get("data"));
				if (list != null && list.size() > 0) {
					commentnData.clear();
					commentnData.addAll(list);
					addGetCommentData(commentnData);
				}
			}

		}

	}

	private void addGetCommentData(ArrayList<HashMap<String, String>> list) {
		if (list == null || list.size() == 0) {
			return;
		}
		Logger.i("评论信息：" + list.toString());
		new MyTask(list).start();

	}

	private class MyTask extends Thread {
		private ArrayList<HashMap<String, String>> list;

		public MyTask(ArrayList<HashMap<String, String>> list) {
			this.list = list;
		}

		public void run() {
			for (int i = 0; i < list.size(); i++) {
				try {
					if (i > 0) {
						Thread.sleep(3000);
					}
					Message msg = new Message();
					msg.what = 44;
					msg.getData().putString("comm_content", list.get(i).get("comm_content"));
					mUIHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	};

	private void addDanmaku(boolean islive, String str) {
		BaseDanmaku danmaku = DanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
		danmaku.text = str;
		// danmaku.padding = 5;
		danmaku.priority = 1;
		danmaku.isLive = islive;
		danmaku.time = danmakuView.getCurrentTime() + 1200;
		danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
		// danmaku.textColor = Color.RED;
		danmaku.textShadowColor = Color.WHITE;
		// danmaku.underlineColor = Color.GREEN;
		// danmaku.borderColor = Color.GREEN;
		danmakuView.addDanmaku(danmaku);
	}

	private BaseDanmakuParser createParser(InputStream stream) {

		if (stream == null) {
			return new BaseDanmakuParser() {

				@Override
				protected Danmakus parse() {
					return new Danmakus();
				}
			};
		}

		ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

		try {
			loader.load(stream);
		} catch (IllegalDataException e) {
			e.printStackTrace();
		}
		BaseDanmakuParser parser = new BiliDanmukuParser();
		IDataSource<?> dataSource = loader.getDataSource();
		parser.load(dataSource);
		return parser;

	}
}
