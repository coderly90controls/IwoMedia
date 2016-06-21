package com.android.iwo.media.activity;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.FileCache;
import com.test.iwomag.android.pubblico.util.Logger;

public class IwoVideoActivity extends BaseActivity implements SurfaceHolder.Callback, SensorEventListener {

	private ImageView video_over;
	private LinearLayout video_close;
	private RelativeLayout topOver;
	private RelativeLayout topStart;
	private RelativeLayout topFinish;
	private ImageView video_start;
	private LinearLayout video_finish;
	private LinearLayout video_return;
	private ImageView video_preview;
	private TextView video_textview;
	private Camera camera;
	private SurfaceView surfaceview;// 显示视频的控件

	private String OUTPUT_FILE = Environment.getExternalStorageDirectory() + "/videooutput.3gp";
	private String urlPath;
	private int progressBar = 0;
	// private SeekBar skb_video = null;
	private MediaPlayer m = null;
	private Timer mTimer = new Timer();

	private SensorManager manager;
	private boolean mediarecorderStop = true;
	private SurfaceHolder holder;
	private boolean bIfPreview = false;
	
	private SurfaceView sv_view;
	private boolean isRecording;
	private MediaRecorder mediaRecorder;

	// Message msg = updateBarHandler.obtainMessage();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		getWindow().setFormat(PixelFormat.TRANSPARENT);
		// 设置横屏显示
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		// 选择支持半透明模式,在有surfaceview的activity中使用。

		setContentView(R.layout.activity_main_video);
		OUTPUT_FILE = FileCache.getInstance().CACHE_PATH + "/a" + System.currentTimeMillis()+".mp4";
		manager = (SensorManager) getSystemService(SENSOR_SERVICE);

		init();

		initCamera();
	}

	private void init() {
		// TODO
		// 第一个头部文件的实例化
		topOver = (RelativeLayout) this.findViewById(R.id.topOver);
		// 录制键
		video_over = (ImageView) this.findViewById(R.id.video_over);
		video_over.setOnClickListener(new TestVideoListener());

		// 关闭页面键
		video_close = (LinearLayout) this.findViewById(R.id.video_close);
		video_close.setOnClickListener(new TestVideoListener());

		// 第二个头部文件的实例化
		topStart = (RelativeLayout) this.findViewById(R.id.topStart);
		// 录制完成键
		video_start = (ImageView) this.findViewById(R.id.video_start);
		video_start.setOnClickListener(new TestVideoListener());

		// 读秒的Text
		video_textview = (TextView) this.findViewById(R.id.video_textview);
		// 中间播放按钮
		video_preview = (ImageView) this.findViewById(R.id.video_preview);

		video_preview.setOnClickListener(new TestVideoListener());
		topFinish = (RelativeLayout) this.findViewById(R.id.topFinish);
		video_return = (LinearLayout) this.findViewById(R.id.video_return);
		video_return.setOnClickListener(new TestVideoListener());
		video_finish = (LinearLayout) this.findViewById(R.id.video_finish);
		video_finish.setOnClickListener(new TestVideoListener());
		initSurfaceView();

	}

	private void initSurfaceView() {
		surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);
		holder = surfaceview.getHolder();// 取得holder
		holder.addCallback(this); // holder加入回调接口
		// setType必须设置，要不出错.
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	class TestVideoListener implements OnClickListener {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.video_over:

				progressBar = 15;

				if (m != null) {
					m.release();
					m = null;
				}
				try {

					if (mediaRecorder == null) {

						mediaRecorder = new MediaRecorder();
					}
					if (camera == null) {
						camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

					}

					if (camera != null) {
						camera.setDisplayOrientation(0);

						camera.unlock();
						mediaRecorder.setCamera(camera);
					}
			        
					mediaRecorder.setOrientationHint(0);
					mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
					//mediaRecorder.setVideoSize(height, width);
					//mediaRecorder.setVideoFrameRate(1);
					mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
					mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
					mediaRecorder.setMaxDuration(30000);
					mediaRecorder.setPreviewDisplay(holder.getSurface());
					mediaRecorder.setOutputFile(OUTPUT_FILE);
					mediaRecorder.prepare();
					Logger.i("OUTPUT_FILE" + OUTPUT_FILE);
					mediaRecorder.start();
				} catch (Exception e) {
					Log.e("Exception-MainActivity", e.toString());
					e.printStackTrace();
				}

				if (mTimer == null) {
					mTimer = new Timer();
				}
				mTimer.schedule(new TimerTask() {
					@Override
					public void run() {

						progressBar--;

						if (progressBar < 0) {
							Message message = new Message();
							message.what = 2;
						} else {
							Message message = new Message();
							message.what = 1;
						}

					}
				}, 0, 1000);
				topOver.setVisibility(View.GONE);
				topStart.setVisibility(View.VISIBLE);
				topFinish.setVisibility(View.GONE);
				video_textview.setVisibility(View.VISIBLE);
				break;
			case R.id.video_start:
				mTimer.cancel();
				mTimer = null;
				mTimer = new Timer();
				topOver.setVisibility(View.GONE);
				topStart.setVisibility(View.GONE);
				topFinish.setVisibility(View.VISIBLE);
				video_preview.setVisibility(View.VISIBLE);
				Log.i("", "stop----");
				if (mediaRecorder != null) {
					// 释放资源

					// 停止录制
					if (mediarecorderStop) {
						mediaRecorder.stop();

					}
					mediaRecorder.release();
					camera.release();
					camera = null;
					mediaRecorder = null;
				}
				video_textview.setVisibility(View.GONE);
				break;
			case R.id.video_preview:

				video_preview.setVisibility(View.GONE);
				topFinish.setVisibility(View.GONE);

				m = new MediaPlayer();
				m.reset();// 恢复到未初始化的状态
				Uri mUri = Uri.parse(OUTPUT_FILE);

				m = MediaPlayer.create(IwoVideoActivity.this, mUri);// 读取视频
				// 设置屏幕

				m.setDisplay(holder);
				m.start();
				m.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						topFinish.setVisibility(View.VISIBLE);

					}
				});
				break;

			case R.id.video_finish:
				Uri mUrl = Uri.parse(OUTPUT_FILE);
				urlPath = mUrl.getPath();
				Log.i("URL_PATH", urlPath);
				Intent intent = new Intent();
				intent.putExtra("video_url", urlPath);
				setResult(RESULT_OK, intent);
				finish();
				break;
			case R.id.video_return:
				video_preview.setVisibility(View.GONE);
				topOver.setVisibility(View.VISIBLE);
				topStart.setVisibility(View.GONE);
				topFinish.setVisibility(View.GONE);

				break;
			case R.id.video_close:
				if (mTimer != null) {
					mTimer.cancel();
					mTimer = null;
				}
				finish();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera = Camera.open();// 打开硬件摄像头，这里导包得时候一定要注意是android.hardware.Camera
			camera.setPreviewDisplay(surfaceview.getHolder());// 通过SurfaceView显示取景画面
			camera.startPreview();// 开始预览
			Toast.makeText(this, "请横屏录制视频，以免视频歪斜", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			if (camera != null) {
				camera.release();
			}
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// surfaceDestroyed的时候同时对象设置为null

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		surfaceview = null;

		holder = null;
		if (mediaRecorder != null) {
			mediaRecorder.release();
			mediaRecorder = null;
		}
		if (camera != null) {
			camera.release();
			camera = null;
		}

	}

	Bundle bundle = new Bundle();

	@Override
	public void onSensorChanged(SensorEvent event) {
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	private void initCamera() {

		if (bIfPreview) {
			camera.stopPreview();// stopCamera();
		}
		if (null != camera) {
			try {
				/* Camera Service settings */
				Camera.Parameters parameters = camera.getParameters();

				parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);
				// 设置拍照和预览图片大小
				parameters.setPictureSize(640, 480); // 指定拍照图片的大小
				// parameters.setPreviewSize(mPreviewWidth, mPreviewHeight); //
				// 指定preview的大小
				// 这两个属性 如果这两个属性设置的和真实手机的不一样时，就会报错

				// 横竖屏镜头自动调整
				if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
					parameters.set("orientation", "portrait"); //
					parameters.set("rotation", 90); // 镜头角度转90度（默认摄像头是横拍）
					camera.setDisplayOrientation(90); // 在2.2以上可以使用
				} else// 如果是横屏
				{
					parameters.set("orientation", "landscape"); //
					camera.setDisplayOrientation(0); // 在2.2以上可以使用
				}

				/* 视频流编码处理 */
				// 添加对视频流处理函数

				// 设定配置参数并开启预览
				camera.setParameters(parameters); // 将Camera.Parameters设定予Camera
				camera.startPreview(); // 打开预览画面
				bIfPreview = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	protected void start() {
		try {
			File file = new File("/sdcard/video.mp4");
			if (file.exists()) {
				// 如果文件存在，删除它，演示代码保证设备上只有一个录音文件
				file.delete();
			}

			mediaRecorder = new MediaRecorder();
			mediaRecorder.reset();
			// 设置音频录入源
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置视频图像的录入源
			mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			// 设置录入媒体的输出格式
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			// 设置音频的编码格式
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			// 设置视频的编码格式
			mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
			// 设置视频的采样率，每秒4帧
			//mediaRecorder.setVideoFrameRate(4);
			// 设置录制视频文件的输出路径
			mediaRecorder.setOutputFile(file.getAbsolutePath());
			// 设置捕获视频图像的预览界面
			mediaRecorder.setPreviewDisplay(sv_view.getHolder().getSurface());

			mediaRecorder.setOnErrorListener(new OnErrorListener() {

				@Override
				public void onError(MediaRecorder mr, int what, int extra) {
					// 发生错误，停止录制
					mediaRecorder.stop();
					mediaRecorder.release();
					mediaRecorder = null;
					isRecording = false;
					//Toast.makeText(, "录制出错", 0).show();
				}
			});

			// 准备、开始
			mediaRecorder.prepare();
			mediaRecorder.start();

			isRecording = true;
			//Toast.makeText(MainActivity.this, "开始录像", 0).show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void stop() {
		if (isRecording) {
			// 如果正在录制，停止并释放资源
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
			isRecording = false;
			//Toast.makeText(MainActivity.this, "停止录像，并保存文件", 0).show();
		}
	}

	@Override
	protected void onDestroy() {
		if (isRecording) {
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
		}
		super.onDestroy();
	}
}