package com.android.iwo.media.activity;

import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

import android.R.integer;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue.IdleHandler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.SeekBar.OnSeekBarChangeListener;

//来自啊福播放器  uri_z  image
@SuppressLint("NewApi")
public class MyVideoView_fcz extends BaseActivity implements OnClickListener {
	private VideoView vv;
	private String uri_after, uri_path, image;
	private View mControlView;
	private PopupWindow mControlerPopupWindow;
	private boolean isevent = true;
	private boolean ispause;
	private static final int HIDE_CONTROLER = 1;
	/**
	 * 多常时间隐藏控制面板
	 */
	private static int TIME = 6868;
	/**
	 * 手机或模拟器的屏幕宽
	 */
	private static int screenWidth = 0;
	/**
	 * 手机或模拟器的屏幕高
	 */
	private static int screenHeight = 0;
	/**
	 * 控制面板屏幕的高
	 */
	private static int controlViewHeight = 0;
	private LinearLayout bottom;
	private ImageView pause;
	private SeekBar sb;
	private ImageView imager;
	/**
	 * 刷新播放时间和拖动条位置消息
	 */
	private final static int PROGRESS_CHANGED = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initWindows();
		setContentView(R.layout.activity_video);
		init();
		initContronlView();
		initVideoView();
		startPlay();
	}

	// 监听
	private void initVideoView() {

		vv.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				int i = vv.getDuration();
				Logger.i("当前的时间：" + i);
				// 视频文件和seekBar关联：视频有多长那么seekBar拖动也在这个范围内
				sb.setMax(i);

				i /= 1000;
				int minute = i / 60;
				int hour = minute / 60;
				int second = i % 60;
				minute %= 60;

				vv.start();

				hideControllerDelay(TIME);

				mHandler.sendEmptyMessage(PROGRESS_CHANGED);

			}
		});
		vv.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				if (vv != null) {
					finish();
					vv.stopPlayback();
				}

			}
		});
	}

	// 开始播放
	private void startPlay() {
		mControlerPopupWindow.update(0, 0, 0, 0);
		if (!StringUtil.isEmpty(uri_path) && vv != null) {
			Uri uri_pre = Uri.parse(uri_path);
			vv.setVideoURI(uri_pre);
		}
		pause.setBackground(getResources().getDrawable(R.drawable.icon_pause));
	}

	// 加载界面进来
	private void initContronlView() {

		mControlView = getLayoutInflater().inflate(R.layout.controler_view,
				null);
		mControlerPopupWindow = new PopupWindow(mControlView);
		// 加载控制面板
		Looper.myQueue().addIdleHandler(new IdleHandler() {

			@Override
			public boolean queueIdle() {

				// TODO Auto-generated method stub
				if (mControlerPopupWindow != null && vv.isShown()) {
					mControlerPopupWindow.showAtLocation(vv, Gravity.BOTTOM, 0,
							0);

					mControlerPopupWindow.update(0, 0, screenWidth,
							controlViewHeight);

				}

				return false;
			}
		});
		getScreenSize();
		bottom = (LinearLayout) mControlView.findViewById(R.id.bottom_linear);
		pause = (ImageView) mControlView.findViewById(R.id.btnPlayUrl);
		sb = (SeekBar) mControlView.findViewById(R.id.skbProgress);
		pause.setOnClickListener(this);
		sb.setOnClickListener(this);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			// 当拖动发生改变的时候执行
			@Override
			public void onProgressChanged(SeekBar seekbar, int progress,
					boolean fromUser) {

				if (fromUser) {
					vv.seekTo(progress);

				}
			}

			// 当拖动刚触动的时候执行
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				mHandler.removeMessages(HIDE_CONTROLER);
			}

			// 当拖动刚触动的时候执行
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
			}
		});
	}

	/**
	 * 获得手机或模拟器屏蔽高和宽，并计算好控制面板的高度
	 */
	private void getScreenSize() {
		Display display = getWindowManager().getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
		controlViewHeight = screenHeight / 8;

	}

	private void initWindows() {
		// 设置没有标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// 保持背光常亮的设置
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// 保持背光常亮的设置(例外一种)
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// regListener();退出关闭Activity

	}

	private void init() {
		image = getIntent().getStringExtra("image");
		uri_path = getIntent().getStringExtra("uri_z");
		Logger.i("广告：" + uri_path);
		uri_after = getIntent().getStringExtra("after");
		vv = (VideoView) findViewById(R.id.vv_video);
		imager = (ImageView) findViewById(R.id.vv_imager);
		vv.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				Toast.makeText(getApplicationContext(), "视频播放完了", 1).show();
				finish();
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnPlayUrl:
			if (ispause) {
				vv.start();
				imager.setVisibility(View.GONE);
				pause.setBackground(getResources().getDrawable(
						R.drawable.icon_pause));
			} else {
				vv.pause();
				pause.setBackground(getResources().getDrawable(
						R.drawable.icon_play));
				// 暂停状态
				if (!StringUtil.isEmpty(image)) {
					imager.setVisibility(View.VISIBLE);

					LoadBitmap.getIntence().loadImage(image, imager);
				}
			}
			ispause = !ispause;
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			TIME = 6868;
			if (isevent) { // 控件是不是显示状态 true是显示
				// hideController()
				mControlerPopupWindow.update(0, 0, 0, 0);
			} else {
				mControlerPopupWindow.update(0, 0, screenWidth,
						controlViewHeight);
				hideControllerDelay(TIME);
			}
			isevent = !isevent;
			return true;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 发送信息，告诉控制面板在多少秒后隐藏。
	 */
	private void hideControllerDelay(int n) {
		mHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, n);
	}

	/**
	 * 用于处理拖动调进度更新、播放时间进度更新、隐藏控制面板消息
	 */
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {

			case PROGRESS_CHANGED:
				// Log.d(TAG, "The handler thread id = "
				// + Thread.currentThread().getId() + "\n");

				// 得到当前播放位置
				int i = vv.getCurrentPosition();

				// 更新播放进度
				sb.setProgress(i);

				int j = vv.getBufferPercentage();

				int secondaryProgress = (j * sb.getMax() / 100);
				// 更新缓冲了多少，通常播放网络资源时候用到
				sb.setSecondaryProgress(secondaryProgress);
				// else {
				// 这里我们不需要第2进度，所以为0
				// sb.setSecondaryProgress(0);
				// }

				i /= 1000;
				int minute = i / 60;
				int hour = minute / 60;
				int second = i % 60;
				minute %= 60;

				sendEmptyMessageDelayed(PROGRESS_CHANGED, 1000);
				break;

			case HIDE_CONTROLER:
				hideController();

				break;

			}

			super.handleMessage(msg);
		}
	};

	protected void onDestroy() {

		if (mControlerPopupWindow.isShowing()) {
			mControlerPopupWindow.dismiss();
		}

		mHandler.removeMessages(PROGRESS_CHANGED);
		mHandler.removeMessages(HIDE_CONTROLER);
		if (vv.isPlaying()) {
			vv.stopPlayback();
		}

		super.onDestroy();
	}

	/**
	 * 隐藏控制面板
	 */
	private void hideController() {

		// 隐藏控制面板
		if (mControlerPopupWindow.isShowing()) {
			mControlerPopupWindow.update(0, 0, 0, 0);
			isevent = false;
		}

	}

}
