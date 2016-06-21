package com.android.iwo.media.activity;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.LoadBitmap;

/**
 * 播放视频是跳转到---->自定义的播放器
 * 
 * @author zly
 * 
 */
public class Test_videoplayer extends Activity implements OnTouchListener {
	private SurfaceView surfaceView;
	private ImageView btnPlayUrl;
	private SeekBar skbProgress;
	private Player player;
	private ImageView imageView;
	private String uri_image;
	private String uri_path;
	private String uri_after;
	private View bottom_linear;
	private boolean bottom_linearIsshow = true;
	private boolean mySwitch = true;
	private boolean isPlay;
	private ImageView back_img;
	static int clickNum = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_player);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 常亮
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView1);
		imageView = (ImageView) findViewById(R.id.imageview);
		back_img = (ImageView) findViewById(R.id.fanhui_img);
		back_img.setOnClickListener(mfinish);

		uri_image = getIntent().getStringExtra("image");
		uri_path = getIntent().getStringExtra("uri_z");
		uri_after = getIntent().getStringExtra("after");

		bottom_linear = findViewById(R.id.bottom_linear);
		surfaceView.setOnTouchListener(this);

		btnPlayUrl = (ImageView) this.findViewById(R.id.btnPlayUrl);
		btnPlayUrl.setOnClickListener(mClickListener);

		skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
		skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		player = new Player(surfaceView, skbProgress);

	}

	private OnClickListener mfinish = new OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}
	};

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			clickNum++;
			if (isPlay) {
				isPlay = false;
				btnPlayUrl.setImageResource(R.drawable.icon_pause);
				player.pause();
				LoadBitmap.getIntence().loadImage(uri_image, imageView);
			} else if (mySwitch == true) {
				findViewById(R.id.text_note).setVisibility(View.GONE);
				isPlay = true;
				btnPlayUrl.setImageResource(R.drawable.icon_play);
				imageView.setVisibility(View.INVISIBLE);
				if (clickNum == 1) {
					player.playUrl(uri_path);
				} else {
					player.start();
				}

			}

		}
	};

	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
		int progress;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			this.progress = progress * player.mediaPlayer.getDuration()
					/ seekBar.getMax();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			player.mediaPlayer.seekTo(progress);
		}
	}

	/**
	 * // ---------------player--------------
	 * 
	 * @author zly
	 * 
	 */

	public class Player implements OnBufferingUpdateListener,
			OnCompletionListener, MediaPlayer.OnPreparedListener,
			SurfaceHolder.Callback, OnSeekCompleteListener {
		private int videoWidth;
		private int videoHeight;
		public MediaPlayer mediaPlayer;
		private SurfaceHolder surfaceHolder;
		private SeekBar skbProgress;
		private Timer mTimer = new Timer();

		public Player(SurfaceView surfaceView, SeekBar skbProgress) {
			this.skbProgress = skbProgress;
			surfaceHolder = surfaceView.getHolder();
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			mTimer.schedule(mTimerTask, 0, 1000);
		}

		/*******************************************************
		 * ͨ��ʱ����Handler�����½����
		 ******************************************************/
		TimerTask mTimerTask = new TimerTask() {
			@Override
			public void run() {
				if (mediaPlayer == null)
					return;
				if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
					handleProgress.sendEmptyMessage(0);
				}
			}
		};

		Handler handleProgress = new Handler() {
			public void handleMessage(Message msg) {

				int position = mediaPlayer.getCurrentPosition();
				int duration = mediaPlayer.getDuration();

				if (duration > 0) {
					long pos = skbProgress.getMax() * position / duration;
					skbProgress.setProgress((int) pos);
				}
			};
		};

		// *****************************************************

		public void playUrl(String videoUrl) {
			if (mediaPlayer != null) {

				try {
					mediaPlayer.reset();
					mediaPlayer.setDataSource(videoUrl);
					mediaPlayer.prepare();
					mediaPlayer.start();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		public void start() {
			mediaPlayer.start();
		}

		public void pause() {
			if (mediaPlayer.isPlaying()) {
				imageView.setVisibility(View.VISIBLE);
				mediaPlayer.pause();
			} else {
				imageView.setVisibility(View.INVISIBLE);
				mediaPlayer.start();
			}
		}

		public void stop() {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.reset();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			Log.e("mediaPlayer", "surface changed");
		}

		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
			try {
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setDisplay(surfaceHolder);
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setOnBufferingUpdateListener(this);
				mediaPlayer.setOnPreparedListener(this);
			} catch (Exception e) {
				Log.e("mediaPlayer", "error", e);
			}
			Log.e("mediaPlayer", "surface created");
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			Log.e("mediaPlayer", "surface destroyed");
		}

		@Override
		/**  
		 * ͨ��onPrepared����  
		 */
		public void onPrepared(MediaPlayer mp) {
			videoWidth = mediaPlayer.getVideoWidth();
			videoHeight = mediaPlayer.getVideoHeight();
			if (videoHeight != 0 && videoWidth != 0) {
				mp.start();
			}
			// player.playUrl(uri_path);
			Log.e("mediaPlayer", "onPrepared");
		}

		@Override
		public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
			skbProgress.setSecondaryProgress(bufferingProgress);
			int currentProgress = skbProgress.getMax()
					* mediaPlayer.getCurrentPosition()
					/ mediaPlayer.getDuration();
			Log.e(currentProgress + "% play", bufferingProgress + "% buffer");

			while (currentProgress == 99) {
				mySwitch = false;
				arg0.reset();
				player.playUrl(uri_after);
				bottom_linear.setVisibility(View.GONE);
				back_img.setVisibility(View.GONE);
				break;
			}
		}

		@Override
		public void onSeekComplete(MediaPlayer mp) {
			mp.start();

		}

		/**
		 * 完成时mediaplayer释放资源
		 */
		@Override
		public void onCompletion(MediaPlayer mp) {
			stop();
			Log.i("onCompletion",
					"--------------------onCompletion--------------------->>>>>>>>>>>>");
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (bottom_linearIsshow) {
			bottom_linearIsshow = false;
			bottom_linear.setVisibility(View.INVISIBLE);
			back_img.setVisibility(View.INVISIBLE);
		} else if (mySwitch == true) {
			bottom_linearIsshow = true;
			bottom_linear.setVisibility(View.VISIBLE);
			back_img.setVisibility(View.VISIBLE);
		}

		return false;
	}

}