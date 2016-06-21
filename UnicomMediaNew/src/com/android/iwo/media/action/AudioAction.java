package com.android.iwo.media.action;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.widget.ImageView;

import com.android.iwo.media.apk.activity.R;
import com.pocketdigi.utils.FLameUtils;
import com.test.iwomag.android.pubblico.util.FileCache;
import com.test.iwomag.android.pubblico.util.Logger;

/**
 * 语音发送
 */
public class AudioAction {
	/**
	 * 用于语音播放 private MediaPlayer mPlayer = null;
	 */
	/** 用于完成录音 */
	private String PATH = "";
	/** 语音文件保存路径 */
	public String mFileName = "", mFimeMp3 = "";
	boolean isRecording = false;
	private short[] mBuffer;
	private AudioRecord mRecorder;

	private MediaPlayer mPlayer = null;
	private AnimationDrawable ani;
	private ImageView img;

	public AudioAction() {
		PATH = FileCache.getInstance().CACHE_PATH;
		initRecorder();
		startVoice();
		stopVoice();
	}

	/** 开始录音 */
	public void startVoice() {
		// 设置录音保存路径
		try {
			if (mRecorder != null) {
				mRecorder.release();
				mRecorder = null;
			}
			initRecorder();
			String path = PATH + "/mp3_" + System.currentTimeMillis();
			mFileName = path + ".raw";
			mFimeMp3 = path + ".mp3";
			Logger.i("录音");
			isRecording = true;
			mRecorder.startRecording();
			startBufferedWrite(new File(mFileName));
		} catch (Exception e) {
			Logger.e("录音失败" + e.toString());
		}
	}

	/** 停止录音 */
	public boolean stopVoice() {
		isRecording = false;
		try {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
			File file = new File(mFileName);

			if (file.exists() && file.length() == 0)
				file.delete();
			return true;
		} catch (Exception e) {
			Logger.e("停止录音：" + e.toString());
			return false;
		}
	}

	private String type = "";

	public void readVoice(String path, AnimationDrawable a, ImageView i,
			final boolean t) {
		Logger.i("----播放语音" + path);
		try {
			if (ani != null) {
				ani.stop();
				ani = null;
			}
			if (img != null) {
				img.setBackgroundColor(0x00000000);
				if ("1".equals(type)) {
					img.setImageResource(R.drawable.ico_chat_voice_send_read);
				} else if ("2".equals(type)) {
					img.setImageResource(R.drawable.ico_chat_voice_get_read);
				}
				img = null;
			}
			type = t ? "1" : "2";
			ani = a;
			img = i;
			if (mPlayer.isPlaying())
				mPlayer.stop();
			mPlayer.reset();
			mPlayer.setDataSource(path);
			mPlayer.prepare();
			mPlayer.start();
			img.setImageDrawable(new ColorDrawable(0x00000000));
			img.setBackgroundDrawable(ani);
			ani.start();
			mPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					if (ani != null)
						ani.stop();
					if (img != null) {
						img.setBackgroundColor(0x00000000);
						if ("1".equals(type)) {
							img.setImageResource(R.drawable.ico_chat_voice_send_read);
						} else {
							img.setImageResource(R.drawable.ico_chat_voice_get_read);
						}
					}
				}
			});
		} catch (IOException e) {
			Logger.e("AudioAction : 播放失败" + e.toString());
			if ("1".equals(type)) {
				img.setImageResource(R.drawable.ico_chat_voice_send_read);
			} else {
				img.setImageResource(R.drawable.ico_chat_voice_get_read);
			}
		}
	}

	/**
	 * 停止播放
	 */
	public void stopPlay() {
		if (mPlayer != null && mPlayer.isPlaying())
			mPlayer.stop();
	}

	public void getVoicePath() {
		try {
			FLameUtils lameUtils = new FLameUtils(1, 16000, 96);
			System.out.println(lameUtils.raw2mp3(mFileName, mFimeMp3));
			System.out.print("----232323" + 1111 + mFimeMp3);
			File file = new File(mFileName);
			if (file.exists())
				file.delete();
		} catch (Exception e) {
			Logger.i("Exception:" + e.toString());
		} catch (Error e) {
			Logger.i("Error:" + e.toString());
		}

	}

	/**
	 * 初始化Recorder
	 */
	public void initRecorder() {
		int bufferSize = AudioRecord.getMinBufferSize(16000,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		mBuffer = new short[bufferSize];
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 16000,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
				bufferSize);
		mPlayer = new MediaPlayer();
	}

	/**
	 * 写入到文件
	 * 
	 * @param file
	 */
	private void startBufferedWrite(final File file) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				DataOutputStream output = null;
				try {
					output = new DataOutputStream(new BufferedOutputStream(
							new FileOutputStream(file)));
					while (isRecording) {
						int readSize = mRecorder.read(mBuffer, 0,
								mBuffer.length);
						for (int i = 0; i < readSize; i++) {
							output.writeShort(mBuffer[i]);
						}
					}
				} catch (IOException e) {
				} finally {
					if (output != null) {
						try {
							output.flush();
						} catch (IOException e) {
						} finally {
							try {
								output.close();
							} catch (IOException e) {
							}
						}
					}
				}
			}
		}).start();
	}
}
