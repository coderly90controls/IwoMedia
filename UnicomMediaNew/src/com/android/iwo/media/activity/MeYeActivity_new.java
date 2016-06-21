package com.android.iwo.media.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.R;
import com.android.iwo.media.dao.Download_Manager_Actvity;
import com.android.iwo.media.view.CircleImageView;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

@SuppressLint("NewApi")
public class MeYeActivity_new extends BaseActivity {
	private LinearLayout camel;
	private String path;
	private String uid, up, mark = "2";
	private String headString;
	List<String> map = new ArrayList<String>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_me_layout_3_new);
		init();
	}

	private void init() {
			LinearLayout my_up=(LinearLayout)findViewById(R.id.me_Text_all_download);
			my_up.setVisibility(View.VISIBLE);
			my_up.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(mContext, Download_Manager_Actvity.class);
					startActivity(intent);
					
				}
			});
		
		
		findViewById(R.id.me_Text_all).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Logger.i("最近观看");
						startActivity(new Intent(mContext,
								MeRecordActivity.class));
					}
				});
		findViewById(R.id.me_Text_all_shouc).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Logger.i("最近收藏");
						startActivity(new Intent(mContext,
								MychareActivity.class));

					}
				});
		findViewById(R.id.me_Text_all_pl).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Logger.i("最近评论");
						startActivity(new Intent(mContext, MyComment_new.class));
					}
				});
		headString = getPre("head_img");
		CircleImageView head = (CircleImageView) findViewById(R.id.me_yonghu_head);
		Logger.i("头像地址：" + headString);
		if (StringUtil.isEmpty(headString)) {
			head.setImageResource(R.drawable.user_edit);
		} else {
			LoadBitmap.getIntence().loadImage(headString, head);
		}
		textname = (TextView) findViewById(R.id.me_layout_textname);
		if (!StringUtil.isEmpty(getPre("nick_name"))) {
			textname.setText(getPre("nick_name"));
		} else {
			textname.setText(getPre("user_name"));
		}
		textphone = (TextView) findViewById(R.id.me_layout_user_phone);
		if (!StringUtil.isEmpty(getPre("user_name"))) {
			textphone.setText(getPre("user_name"));
		}
		if (!StringUtil.isEmpty(getPre("Umeng"))) {
			textphone.setText(getPre("Umeng"));
		}
		textsex = (ImageView) findViewById(R.id.me_layout_sex);
		if (!StringUtil.isEmpty(getPre("sex"))) {
			if ("1".equals(getPre("sex"))) {
				// textsex.setText("男");
				textsex.setBackground(getResources()
						.getDrawable(R.drawable.man));
			} else {
				// textsex.setText("女");
				textsex.setBackground(getResources().getDrawable(
						R.drawable.woman));
			}
		}
		setBack(null);
		ImageView right = (ImageView) findViewById(R.id.right_img1);
		right.setVisibility(View.VISIBLE);
		BitmapUtil.setImageResource(right, R.drawable.setting1);
		right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						UserSetListActivity.class);
				startActivityForResult(intent, 911);
			}
		});
		camel = (LinearLayout) findViewById(R.id.camer);
		// 图片的点击
		findViewById(R.id.me_yonghu_head).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						camel.setVisibility(View.VISIBLE);
						setCamel();
					}

					private void setCamel() {
						LinearLayout btn1 = (LinearLayout) findViewById(R.id.nor);
						LinearLayout btn2 = (LinearLayout) findViewById(R.id.model);
						LinearLayout btn3 = (LinearLayout) findViewById(R.id.cancle);
						TextView btn_text1 = (TextView) findViewById(R.id.nor_text);
						TextView btn_text2 = (TextView) findViewById(R.id.model_text);
						btn_text1.setText("拍摄");
						btn_text2.setText("选择本地");
						uid = PreferenceUtil.getString(mContext, "user_id", "");
						up = PreferenceUtil
								.getString(mContext, "user_name", "");
						OnClickListener listener = new OnClickListener() {
							@Override
							public void onClick(View v) {
								switch (v.getId()) {
								case R.id.nor: // 照片拍摄
									Intent intent2 = new Intent(
											MediaStore.ACTION_IMAGE_CAPTURE);
									Uri uri = Uri.fromFile(new File(Environment
											.getExternalStorageDirectory(),
											"xiaoma.jpg"));
									path = uri.getPath();
									intent2.putExtra(MediaStore.EXTRA_OUTPUT,
											uri);
									startActivityForResult(intent2, 2);
									camel.setVisibility(View.GONE);
									break;
								case R.id.model:// 图库选择
									Intent intent = new Intent(
											Intent.ACTION_PICK, null);
									intent.setDataAndType(
											MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
											"image/*");
									startActivityForResult(intent, 111);
									camel.setVisibility(View.GONE);
									break;
								case R.id.cancle:
									camel.setVisibility(View.GONE);
									break;
								default:
									break;
								}
							}
						};
						btn1.setOnClickListener(listener);
						btn2.setOnClickListener(listener);
						btn3.setOnClickListener(listener);

					}
				});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 911) {
			if (!StringUtil.isEmpty(getPre("nick_name"))) {
				textname.setText(getPre("nick_name"));
			}
			if (!StringUtil.isEmpty(getPre("user_name"))) {
				textphone.setText(getPre("user_name"));
			}
			if (!StringUtil.isEmpty(getPre("Umeng"))) {
				textphone.setText(getPre("Umeng"));
			}
			if (!StringUtil.isEmpty(getPre("sex"))) {
				if ("1".equals(getPre("sex"))) {
					// textsex.setText("男");
					textsex.setBackground(getResources().getDrawable(
							R.drawable.man));
				} else {
					textsex.setBackground(getResources().getDrawable(
							R.drawable.woman));
					// textsex.setText("女");
				}
			}
		}
		Logger.i("奇怪");
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 111:
				startPhotoZoom(data.getData());
				break;
			case 2:
				File temp = new File(Environment.getExternalStorageDirectory()
						+ "/xiaoma.jpg");
				startPhotoZoom(Uri.fromFile(temp));
				break;
			case 3:
				if (data != null) {
					setPicToView(data);
				}
				break;
			}
			// 这个回调方法解决在我的主页进入设计资料时 回退来 不第一时间显示改的资料

		}
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			saveMyBitmap("head_img", photo);
			mLoadBar.setMessage("上传头像中···");
			mLoadBar.show();
			new SendMsg().execute(path);
		}
	}

	public void saveMyBitmap(String bitName, Bitmap mBitmap) {
		File f = new File(Environment.getExternalStorageDirectory(), bitName
				+ ".png");
		path = f.getPath();
		Logger.i(path);
		try {
			f.createNewFile();
		} catch (IOException e) {
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String heString = "";
	private TextView textname;
	private TextView textphone;
	private ImageView textsex;

	private class SendMsg extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {
			Logger.i("sfsd" + params[0]);
			String string = DataRequest.getStringFrom_base64(DataRequest
					.SendFile(getUrl(AppConfig.SEND_HEAD_IMG) + "cut=2",
							"ufile", params[0]));
			Logger.i("dasdsa " + string);
			if (!StringUtil.isEmpty(string)) {
				HashMap<String, String> map = DataRequest
						.getHashMapFromJSONObjectString(string);
				if (map != null) {
					return heString = map.get("uploadFileUrl_b");
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!StringUtil.isEmpty(result)) {
				CircleImageView head = (CircleImageView) findViewById(R.id.me_yonghu_head);
				LoadBitmap.getIntence().loadImage(result, head);
				Logger.i(" 首页图片地址： " + heString);
				PreferenceUtil.setString(mContext, "head_img", heString);
				// 上传图片成功保存在到服务器
				new SendMsg2().execute(result);
				makeText("恭喜您，头像上传成功");

			} else {
				makeText("上传头像失败");
			}

			if (mLoadBar != null) {
				mLoadBar.dismiss();
			}
		}
	}

	private class SendMsg2 extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			return DataRequest.getStringFromURL_Base64(
					getUrl(AppConfig.SEND_IMG_CUT_TMP) + "&uid="
							+ getPre("user_id") + "&up=" + getPre("user_name"),
					params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
		}
	}

}
