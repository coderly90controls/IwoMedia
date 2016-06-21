package com.android.iwo.media.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.FileCache;
import com.test.iwomag.android.pubblico.util.Logger;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder;
import android.net.LocalServerSocket;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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

public class VideoActivity extends Activity implements SurfaceHolder.Callback, SensorEventListener {

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
	private MediaRecorder mediarecorder;
	private Camera camera;
	private SurfaceView surfaceview;// 显示视频的控件

	private LocalServerSocket lss;

	private String OUTPUT_FILE = Environment.getExternalStorageDirectory() + "/videooutput.3gp";
	private String urlPath;
	boolean isPlay = false;// 视频
	int progressBar = 0;
	// private SeekBar skb_video = null;
	MediaPlayer m = null;
	private Timer mTimer = new Timer();

	SensorManager manager;
	private boolean mediarecorderStop = true;
	SurfaceHolder holder;
	private boolean bIfPreview = false;
	int limit = -100;
	Handler updateBarHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				video_textview.setText("" + progressBar + "s");
			}
			if (msg.what == 2) {
				if (mediarecorder != null) {
					// 释放资源

					// 停止录制
					if (mediarecorderStop) {
						mediarecorder.stop();

					}
					mediarecorder.release();
					camera.release();
					camera = null;
					mediarecorder = null;
				}

				mTimer.cancel();
				mTimer = null;
				mTimer = new Timer();

				topOver.setVisibility(View.GONE);
				topStart.setVisibility(View.GONE);
				topFinish.setVisibility(View.VISIBLE);
				video_textview.setVisibility(View.GONE);
				video_preview.setVisibility(View.VISIBLE);
			}
			if (msg.what == 4) {
				// initCamera();

			}
			if (msg.what == 0) {
				Bundle bundle = msg.getData();
				int x = bundle.getInt("X");
				int y = bundle.getInt("Y");
				int z = bundle.getInt("Z");

				// if((z>=0&&z<=90)&&(y>=0&&y>=-90)||(z>=0&&z<=90)&&(y>=-180&&y<=-90)){
				// if(y<-90){
				// limit=((y+90)*-1)+z;
				// }
				// if(y>-90){
				// limit=(y*-1)+(90-z);
				// }
				// }

				if ((y < 5 && y > -5) && (z > 60 && z < 90)) {
					limit = 0;
					Log.i("角度-----------", "" + limit + "°");
				}
				if ((z >= 0 && z <= 90) && (y <= 0 && (y >= -90 || (y > 100 && y >= -160)))) {

					limit = 90 - z;
					Log.i("角度-----------", "" + limit + "°");
				}
				if ((z < 5 && z > -5) && (y < -49 && y > -110)) {
					limit = 90;
					Log.i("角度-----------", "" + limit + "°");
				}

				if ((z < 0 && z > -90) && (y <= 0 && (y > -90 || (y < -130 && y >= -160)))) {
					limit = 90 + (z * -1);
					Log.i("角度-----------", "" + limit + "°");
				}

				if ((y < 5 && y > -5) && (z < -60 && z > -90)) {
					limit = 180;
					Log.i("角度-----------", "" + limit + "°");
				}

				if ((z < 0 && z >= -90) && (y >= 0 && (y < 90 || (y > 130 && y < 160)))) {
					limit = 180 + (90 - (z * -1));
					Log.i("角度-----------", "" + limit + "°");
				}

				if ((z < 5 && z > -5) && (y < 90 && y > 50)) {
					limit = 270;
					Log.i("角度-----------", "" + limit + "°");
				}
				if (z > 0 && z < 90 && (y >= 0 && (y < 90 || (y > 130 && y < 160)))) {
					limit = 270 + z;
					Log.i("角度-----------", "" + limit + "°");

				}
				if (y < 7 && y > -7 && (z > 60 && z < 80)) {
					limit = 0;
					Log.i("角度-----------", "" + limit + "°");
				}
				if (limit == -100) {
					limit = 0;
					Log.i("角度-----------", "" + limit + "°");
				}
			}

			// Log.i("角度-----------", ""+limit+"°");
			// 将要执行的线程放入到队列当中
			updateBarHandler.removeMessages(0);

			// MainActivity.this.msg = updateBarHandler.obtainMessage();

		}
	};

	// Message msg = updateBarHandler.obtainMessage();
	public void onCreate(Bundle savedInstanceState) {
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		getWindow().setFormat(PixelFormat.TRANSPARENT);
		// 设置横屏显示
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		// 选择支持半透明模式,在有surfaceview的activity中使用。

		setContentView(R.layout.activity_main_video);
		OUTPUT_FILE = FileCache.getInstance().CACHE_PATH + "/a" + System.currentTimeMillis() + ".mp4";
		manager = (SensorManager) getSystemService(SENSOR_SERVICE);

		init();

		initCamera();
		// ----------定时器记录播放进度---------//

		// skb_video = (SeekBar) this.findViewById(R.id.SeekBar02);
	}

	// private void initCamera(){
	// if(mediarecorder==null){
	//
	// mediarecorder = new MediaRecorder();
	// }
	// if(c==null){
	// c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
	// c.setDisplayOrientation(0);
	//
	// c.unlock();
	// c.startPreview();
	// c.
	// }
	//
	// }

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
		// mTimer = new Timer();
		// mTimerTask = new TimerTask() {
		// @Override
		// public void run() {
		//
		// seekbar.setProgress(progressBar);
		// progressBar++;
		//
		// if(progressBar==150){
		// Message message=new Message();
		// message.what=2;
		// updateBarHandler.sendMessage(message);
		//
		// }
		//
		//
		//
		// }
		// };

		initSurfaceView();

	}

	private void initSurfaceView() {
		surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);
		// WindowManager wm = (WindowManager)
		// this.getSystemService(Context.WINDOW_SERVICE);
		//
		// int width = wm.getDefaultDisplay().getWidth();
		// ViewGroup.LayoutParams topOverParams=topOver.getLayoutParams();
		// ViewGroup.LayoutParams params=surfaceview.getLayoutParams();
		// params.width=width-topOverParams.height;
		// params.height=ViewGroup.LayoutParams.FILL_PARENT;
		// surfaceview.setLayoutParams(params);
		holder = surfaceview.getHolder();// 取得holder
		holder.addCallback(this); // holder加入回调接口
		// setType必须设置，要不出错.
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	protected void onResume() {

		super.onResume();

		List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ORIENTATION);

		if (sensors.size() > 0) {
			Sensor sensor = sensors.get(0);
			// 注册SensorManager
			// this->接收sensor的实例
			// 接收传感器类型的列表
			// 接受的频率
			manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
		}

		Message mesg = new Message();
		mesg.what = 4;

		updateBarHandler.sendMessage(mesg);
	}

	class TestVideoListener implements OnClickListener {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.video_over:

				progressBar = 15;

				isPlay = false;
				if (m != null) {
					m.release();
					// m.reset();
					m = null;
				}

				try {// http://www.zhihu.com/question/21117222

					if (mediarecorder == null) {

						mediarecorder = new MediaRecorder();
					}
					if (camera == null) {
						camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

					}

					if (camera != null) {
						camera.setDisplayOrientation(0);

						camera.unlock();
						mediarecorder.setCamera(camera);
					}

					DisplayMetrics displaysMetrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
					int width = displaysMetrics.widthPixels;
					int height = displaysMetrics.heightPixels;
					mediarecorder.setOrientationHint(0);
					mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					// 设置视频图像的录入源
					mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
					// 设置录入媒体的输出格式
					mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
					// 设置音频的编码格式
					mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
					// 设置视频的编码格式
					mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
					// 设置视频的采样率，每秒4帧
					// mediaRecorder.setVideoFrameRate(4);
					mediarecorder.setMaxDuration(30000);
					mediarecorder.setPreviewDisplay(holder.getSurface());
					mediarecorder.setOutputFile(OUTPUT_FILE);
					mediarecorder.setOnErrorListener(new OnErrorListener() {
						@Override
						public void onError(MediaRecorder mr, int what, int extra) {
							// 发生错误，停止录制
							mediarecorder.stop();
							mediarecorder.release();
							mediarecorder = null;
							Toast.makeText(VideoActivity.this, "录制出错", 0).show();
						}
					});
					mediarecorder.prepare();
					mediarecorder.start();
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
							updateBarHandler.sendMessage(message);

						} else {
							Message message = new Message();
							message.what = 1;
							updateBarHandler.sendMessage(message);
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
				//
				// if(m!=null){
				// m.stop();
				// }
				//

				if (mediarecorder != null) {
					// 释放资源

					// 停止录制
					if (mediarecorderStop) {
						mediarecorder.stop();

					}
					mediarecorder.release();
					camera.release();
					camera = null;

					mediarecorder = null;
				}
				video_textview.setVisibility(View.GONE);
				break;
			case R.id.video_preview:

				video_preview.setVisibility(View.GONE);
				topFinish.setVisibility(View.GONE);
				isPlay = true;

				m = new MediaPlayer();
				m.reset();// 恢复到未初始化的状态
				Uri mUri = Uri.parse(OUTPUT_FILE);

				m = MediaPlayer.create(VideoActivity.this, mUri);// 读取视频
				// skb_video.setMax(m.getDuration());// 设置SeekBar的长度
				// m.setAudioStreamType(AudioManager.STREAM_MUSIC);
				// 设置屏幕
				int videowidth = surfaceview.getWidth();

				m.setDisplay(holder);
				// try {
				// m.prepare();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				m.start();
				m.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						// video_preview.setVisibility(View.VISIBLE);
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

				VideoActivity.this.finish();

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
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);// 得到窗口管理器
			Display display = wm.getDefaultDisplay();// 得到当前屏幕
			// Camera.Parameters parameters = camera.getParameters();//得到摄像头的参数
			// parameters.setPreviewSize(display.getWidth(),
			// display.getHeight());//设置预览照片的大小
			// parameters.setPreviewFrameRate(3);//设置每秒3帧
			// parameters.setPictureFormat(PixelFormat.JPEG);//设置照片的格式
			// parameters.setJpegQuality(85);//设置照片的质量
			// parameters.setPictureSize(display.getHeight(),
			// display.getWidth());//设置照片的大小，默认是和 屏幕一样大
			// camera.setParameters(parameters);
			camera.setPreviewDisplay(surfaceview.getHolder());// 通过SurfaceView显示取景画面
			camera.startPreview();// 开始预览
			Toast.makeText(this, "请横屏录制视频，以免视频歪斜", 3000).show();
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
		if (mediarecorder != null) {
			mediarecorder.release();
			mediarecorder = null;
		}
		if (camera != null) {
			camera.release();
			camera = null;
		}

	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {

		final double ASPECT_TOLERANCE = 0.1;

		double targetRatio = (double) w / h;

		if (sizes == null)
			return null;

		Size optimalSize = null;

		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size

		for (Size size : sizes) {

			double ratio = (double) size.width / size.height;

			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;

			if (Math.abs(size.height - targetHeight) < minDiff) {

				optimalSize = size;

				minDiff = Math.abs(size.height - targetHeight);

			}

		}

		// Cannot find the one match the aspect ratio, ignore the requirement

		if (optimalSize == null) {

			minDiff = Double.MAX_VALUE;

			for (Size size : sizes) {

				if (Math.abs(size.height - targetHeight) < minDiff) {

					optimalSize = size;

					minDiff = Math.abs(size.height - targetHeight);

				}

			}

		}

		return optimalSize;

	}

	Bundle bundle = new Bundle();

	@Override
	public void onSensorChanged(SensorEvent event) {
		//
		// // 接受方向感应器的类型
		// if (event.sensor.getType() == Sensor.TYPE_ORIENTATION)
		// {
		//
		// // 这里我们可以得到数据，然后根据需要来处理
		// float x = event.values[SensorManager.DATA_X];
		// float y = event.values[SensorManager.DATA_Y];
		// float z = event.values[SensorManager.DATA_Z];
		//
		//
		//
		// msg.setData(bundle);
		// bundle.putInt("X", (int) x);
		// bundle.putInt("Y", (int) y);
		// bundle.putInt("Z", (int) z);
		// msg.what=0;
		//
		//
		// }
		//

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Log.i("方向-----------", ""+degree);

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

				List<Size> pictureSizes = camera.getParameters().getSupportedPictureSizes();
				List<Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();
				List<Integer> previewFormats = camera.getParameters().getSupportedPreviewFormats();
				List<Integer> previewFrameRates = camera.getParameters().getSupportedPreviewFrameRates();

				Size psize = null;
				for (int i = 0; i < pictureSizes.size(); i++) {
					psize = pictureSizes.get(i);

				}
				for (int i = 0; i < previewSizes.size(); i++) {
					psize = previewSizes.get(i);

				}
				Integer pf = null;
				for (int i = 0; i < previewFormats.size(); i++) {
					pf = previewFormats.get(i);

				}

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

				// 【调试】设置后的图片大小和预览大小以及帧率
				Camera.Size csize = camera.getParameters().getPreviewSize();

				csize = camera.getParameters().getPictureSize();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {

		super.onPause();
		if (mTimer != null) {
			mTimer.cancel();
			mTimer.cancel();
			mTimer = null;
		}

		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		if (surfaceview != null) {
			surfaceview.clearFocus();
		}

	}

}