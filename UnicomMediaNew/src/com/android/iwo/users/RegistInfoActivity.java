package com.android.iwo.users;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.activity.BaseActivity;
import com.android.iwo.media.activity.ModelActivity;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.AppUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class RegistInfoActivity extends BaseActivity {

	private EditText mName, mPass;
	// private String imgUrl;
	// private String mark = "1";
	private static String mTel;
	private String path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist_info);
		setTitle("填写昵称");
		Intent intent = getIntent();
		mTel = intent.getStringExtra("tel");
		PreferenceUtil.setString(mContext, "mTel", mTel);
		Logger.i(" 手机号    " + PreferenceUtil.getString(mContext, "mTel", ""));
		setBack_text(null);
		init();
	}

	private void init() {
		mName = (EditText) findViewById(R.id.nick_name);
		mPass = (EditText) findViewById(R.id.pass_word);

		findViewById(R.id.ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isRight()) {
					new GetData().execute();
				}
			}
		});
		setImage();
	}

	private boolean isRight() {
		String msg = "";
		boolean ok = true;
		if (StringUtil.isEmpty(heString)) {
			msg = "请设置您的头像";
			ok = false;
		} else if (StringUtil.isEmpty(mName.getText().toString())) {
			msg = "请设置您的昵称";
			ok = false;
		} else if (StringUtil.isEmpty(mPass.getText().toString())) {
			msg = "请设置您的密码";
			ok = false;
		} else if (!StringUtil.isPassWord(mPass.getText().toString())) {
			msg = "6-20位数字或英文字符";
			ok = false;
		}
		if (!StringUtil.isEmpty(msg))
			makeText(msg);
		return ok;
	}

	private void setImage() {
		ImageView head = (ImageView) findViewById(R.id.head);
		head.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(mContext).setTitle("设置头像").setItems(new String[] { "相机拍摄", "相册选取" }, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "xiaoma.jpg"));
							path = uri.getPath();
							intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							startActivityForResult(intent2, 2);

						} else if (which == 1) {
							Intent intent = new Intent(Intent.ACTION_PICK, null);
							intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
							startActivityForResult(intent, 111);
						}
						dialog.dismiss();
					}

				}).show();
			}
		});
	}

	private class GetData extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected void onPreExecute() {
			mLoadBar.setMessage("数据加载...");
			mLoadBar.show();
		}

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_USER_INFO), heString, mName.getText().toString().replaceAll("\\s", ""), mPass.getText()
					.toString(), mTel);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(result.get("data"));
					PreferenceUtil.setString(mContext, "mTel", "");
					PreferenceUtil.setString(mContext, "pass1122", mPass.getText().toString());
					HashMap<String, String> day = null;
					HashMap<String, String> night = null;
					if (map != null) {
						day = DataRequest.getHashMapFromJSONObjectString(map.get("dinfo"));
						night = DataRequest.getHashMapFromJSONObjectString(map.get("ninfo"));
					}
					Logger.i("  注册数据：" + result.toString());
					if (day != null) {

						PreferenceUtil.setString(mContext, "user_name", day.get("user_name"));
						PreferenceUtil.setString(mContext, "user_id", day.get("id"));
						PreferenceUtil.setString(mContext, "real_name", day.get("real_name"));
						PreferenceUtil.setString(mContext, "user_pass", day.get("user_pass"));
						PreferenceUtil.setString(mContext, "user_head", day.get("head_img"));
						PreferenceUtil.setString(mContext, "user_city_id", day.get("city_id"));
						PreferenceUtil.setString(mContext, "user_nickname", day.get("nick_name"));
						PreferenceUtil.setString(mContext, "user_bg_img_id", day.get("bg_img"));
						PreferenceUtil.setString(mContext, "user_area_id", day.get("area_id"));
						PreferenceUtil.setString(mContext, "area_name", day.get("area_name"));
						PreferenceUtil.setString(mContext, "user_industry_id", day.get("area_id"));
						PreferenceUtil.setString(mContext, "user_position_id", day.get("area_name"));
						PreferenceUtil.setString(mContext, "user_area", "");
						PreferenceUtil.setString(mContext, "of_pass", day.get("user_pass"));
						// 登陆之后设置公用参数设置公共参数。
						AppUtil.END = "";
						setPubParame();
					}
					if (night != null) {
						PreferenceUtil.setString(mContext, "n_user_id", night.get("id"));
						PreferenceUtil.setString(mContext, "n_real_name", night.get("real_name"));
						PreferenceUtil.setString(mContext, "n_user_head", night.get("head_img"));
						PreferenceUtil.setString(mContext, "n_user_city_id", night.get("city_id"));
						PreferenceUtil.setString(mContext, "user_bg_img_id", day.get("bg_img"));
						PreferenceUtil.setString(mContext, "n_user_nickname", night.get("nick_name"));
						PreferenceUtil.setString(mContext, "user_area_id", day.get("area_id"));
						PreferenceUtil.setString(mContext, "n_of_pass", day.get("user_pass"));
					}
					PreferenceUtil.setString(mContext, "login_tel", mTel);
					new SendMsg2().execute();
					setInfor(day, night);
					
					startActivity(new Intent(mContext, ModelActivity.class));
				} else {
					makeText(result.get("msg"));
				}
			}

			mLoadBar.dismiss();
		}
	}

	private void setInfor(HashMap<String, String> day, HashMap<String, String> night) {
		boolean day_b = true, night_n = false;

		if (day == null)
			day_b = false;
		else {
			if (StringUtil.isEmpty(day.get("head_img")))
				day_b = false;

			if (StringUtil.isEmpty(day.get("nickname")))
				day_b = false;

			if (StringUtil.isEmpty(day.get("birthday")))
				day_b = false;

			if (StringUtil.isEmpty(day.get("sex")))
				day_b = false;

			if (StringUtil.isEmpty(day.get("city_id")))
				day_b = false;
		}

		if (day_b)
			PreferenceUtil.setString(mContext, "dayloginset", "yes");
		else {
			PreferenceUtil.setString(mContext, "dayloginset", "");
		}

		if (night == null)
			night_n = false;
		else {
			if (StringUtil.isEmpty(night.get("head_img")))
				night_n = false;

			if (StringUtil.isEmpty(night.get("nickname")))
				night_n = false;

			if (StringUtil.isEmpty(night.get("birthday")))
				night_n = false;

			if (StringUtil.isEmpty(night.get("sex")))
				night_n = false;

			if (StringUtil.isEmpty(night.get("city_id")))
				night_n = false;
		}
		if (night_n)
			PreferenceUtil.setString(mContext, "nightloginset", "yes");
		else {
			PreferenceUtil.setString(mContext, "nightoginset", "");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 111:
				startPhotoZoom(data.getData());
				break;
			case 2:
				File temp = new File(Environment.getExternalStorageDirectory() + "/xiaoma.jpg");
				startPhotoZoom(Uri.fromFile(temp));
				break;
			case 3:
				if (data != null) {
					setPicToView(data);
				}
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private String heString = "";

	private class SendMsg extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {
			Logger.i("sfsd" + params[0]);
			String string = DataRequest.getStringFrom_base64(DataRequest.SendFile(getUrl(AppConfig.SEND_HEAD_IMG) + "cut=2", "ufile", params[0]));
			Logger.i("dasdsa " + string);
			if (!StringUtil.isEmpty(string)) {
				HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(string);
				if (map != null) {
					return heString = map.get("uploadFileUrl_b");
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!StringUtil.isEmpty(result)) {
				ImageView head = (ImageView) findViewById(R.id.head);
				LoadBitmap.getIntence().loadImage(heString, head);
			} else {
				makeText("上传头像失败");
			}

			if (mLoadBar != null) {
				mLoadBar.dismiss();
			}
		}
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("提示").setMessage("信息未完成是否退出？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
				System.exit(0);
				dialog.dismiss();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

	private class SendMsg2 extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			return DataRequest.getStringFromURL_Base64(getUrl(AppConfig.SEND_IMG_CUT_TMP) + "&uid=" + getPre("user_id") + "&up=" + getPre("user_name"), heString);
		}

		@Override
		protected void onPostExecute(String result) {
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
		File f = new File(Environment.getExternalStorageDirectory(), bitName + ".png");
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

}
