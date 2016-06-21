package com.android.iwo.media.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.*;
import com.android.iwo.media.view.ArrayWheelAdapter;
import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.view.IwoToast;
import com.android.iwo.media.view.WheelView;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class MyHomePageNightActivity extends BaseActivity implements
		OnClickListener {
	private HashMap<String, String> userData = new HashMap<String, String>();
	private ImageView user_img;
	private TextView nick_name, sex, age, signature;
	private String uid, up, mark = "1";
	private String sex_text, back_sex, textnike;
	private LinearLayout camel;
	String path;
	CommonDialog commonDialog = null;
	private float density;
	private boolean orEdit = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_home_page_night);

		init();
		uid = getUid();
		up = getUserTel();
		setBack(this);
		setNightTitle("我的主页");
		setViewClick();
		new GetUserData().execute();
	}

	private void init() {
		density = getResources().getDisplayMetrics().density;
		camel = (LinearLayout) findViewById(R.id.camer);
		user_img = (ImageView) findViewById(R.id.user_img);
		nick_name = (TextView) findViewById(R.id.user_nickname);
		sex = (TextView) findViewById(R.id.user_sex);
		age = (TextView) findViewById(R.id.user_age);
		signature = (TextView) findViewById(R.id.user_signature);
	}

	private void setViewClick() {
		setClick(R.id.user_img);
		setClick(R.id.layout_2);
		setClick(R.id.layout_3);
		setClick(R.id.layout_4);
		setClick(R.id.layout_5);
	}

	@Override
	public void onClick(View v) {
		int[] clikViews = null;
		switch (v.getId()) {
		case R.id.user_img:// 头像
			camel.setVisibility(View.VISIBLE);
			setCamel(this);
			break;

		case R.id.left_img:// 返回键
			onBackPressed();
			break;
		case R.id.layout_3:// 性别
			showMyDialog(new String[] { "男", "女" }, "2").show();
			break;
		case R.id.layout_4:// 年龄
			OnClickListener clickListener_6 = new OnClickListener() {
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.determine:
						EditText edit = (EditText) v.getTag();
						// EditText v
						String textnike = edit.getText().toString();
						if (StringUtil.isEmpty(textnike)) {
							makeText("年龄不可为空");
							return;
						} else if (!StringUtil.isAge(textnike)) {
							makeText("年龄填写为  1~120 岁");
						} else {
							orEdit = true;
							age.setText(textnike);
						}
						commonDialog.dismiss();
						break;
					case R.id.cancel:
						commonDialog.dismiss();
						break;
					default:
						break;
					}
				}
			};
			clikViews = new int[] { R.id.determine, R.id.cancel };
			commonDialog = new CommonDialog(MyHomePageNightActivity.this,
					clickListener_6, R.layout.loading_dialog_edittext_age,
					clikViews, R.id.edit_view);
			commonDialog.show();
			break;
		case R.id.layout_5:// 个性签名
			OnClickListener clickListener_7 = new OnClickListener() {
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.determine:
						EditText edit = (EditText) v.getTag();
						// EditText v
						textnike = edit.getText().toString();
						if (StringUtil.isEmpty(textnike)) {
							makeText("个性签名不可为空");
							return;
						} else {
							orEdit = true;
							signature.setText(textnike);
							PreferenceUtil.setString(mContext,
									"night_signature", textnike);
						}
						commonDialog.dismiss();
						break;
					case R.id.cancel:
						commonDialog.dismiss();
						break;
					default:
						break;
					}
				}
			};
			clikViews = new int[] { R.id.determine, R.id.cancel };
			commonDialog = new CommonDialog(MyHomePageNightActivity.this,
					clickListener_7,
					R.layout.loading_dialog_edittext_signature, clikViews,
					R.id.edit_view, textnike, "1");
			commonDialog.show();
			break;
		default:
			break;
		}

	}

	private void setClick(int viewID) {
		findViewById(viewID).setOnClickListener(this);
	}

	/**
	 * 获取用户信息
	 * 
	 * @author ZhangZhanZhong
	 * 
	 */
	private class GetUserData extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			String u_id = getUid();
			if ("day".equals(getMode())) {
				return DataRequest
						.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_MY_INFO));
			} else if ("night".equals(getMode())) {
				return DataRequest.getHashMapFromUrl_Base64(
						getUrl(AppConfig.NEW_FR_N_GET_USER_INF), u_id);
			}
			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					userData.putAll(DataRequest
							.getHashMapFromJSONObjectString(result.get("data")));
					setUser();
					findViewById(R.id.night_my_home)
							.setVisibility(View.VISIBLE);
				}
			}
		}

		/**
		 * 设置用户信息。
		 */
		private void setUser() {
			LoadBitmap bitmap = new LoadBitmap();
			bitmap.loadImage(userData.get("head_img"), user_img);
			PreferenceUtil.setString(mContext, "n_user_head",
					userData.get("head_img"));
			nick_name.setText(userData.get("nick_name"));
			sex_text = userData.get("sex");
			if ("2".equals(sex_text)) {
				sex.setText("女");
			} else {
				sex.setText("男");
			}
			age.setText(userData.get("age"));
			textnike = userData.get("sign");
			signature.setText(textnike);
			PreferenceUtil.setString(mContext, "night_signature", textnike);
		}
	}

	public void setCamel(final Activity activity) {
		LinearLayout btn1 = (LinearLayout) activity.findViewById(R.id.nor);
		LinearLayout btn2 = (LinearLayout) activity.findViewById(R.id.model);
		LinearLayout btn3 = (LinearLayout) activity.findViewById(R.id.cancle);
		TextView btn_text1 = (TextView) activity.findViewById(R.id.nor_text);
		TextView btn_text2 = (TextView) activity.findViewById(R.id.model_text);
		btn_text1.setText("照片拍摄");
		btn_text2.setText("图库选择");
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.nor:
					Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					Uri uri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), "xiaoma.jpg"));
					path = uri.getPath();
					intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					startActivityForResult(intent2, 2);

					camel.setVisibility(View.GONE);
					break;
				case R.id.model:

					Intent intent = new Intent(Intent.ACTION_PICK, null);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
			default:
				break;
			}
		}

	}

	/**
	 * 编辑上传头像
	 * 
	 * @author hanpengyuan
	 * 
	 */
	private class SendMsg extends AsyncTask<String, Void, String> {
		private String heString = "";

		@Override
		protected String doInBackground(String... params) {
			String string = DataRequest.SendFile(
					getUrl(AppConfig.SEND_HEAD_IMG) + "cut=2", "ufile",
					params[0]);
			if (!StringUtil.isEmpty(string)) {
				HashMap<String, String> map = DataRequest
						.getHashMapFromJSONObjectString(DataRequest
								.getStringFrom_base64(string));
				if (map != null) {
					Logger.i("-ddd--" + map.toString());
					heString = map.get("uploadFileUrl_b");
					return DataRequest.getStringFromURL_Base64(
							getUrl(AppConfig.SEND_IMG_CUT_TMP) + "&uid=" + uid
									+ "&up=" + up, heString, mark);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!StringUtil.isEmpty(result)) {
				HashMap<String, String> map = DataRequest
						.getHashMapFromJSONObjectString(result);
				if ("1".equals(map.get("code"))) {
					Logger.i(" 首页图片地址： " + heString);
					LoadBitmap.getIntence().loadImage(heString, user_img);
					IwoToast.makeText(mContext, "上传头像成功").show();
					orEdit = true;
					PreferenceUtil.setString(mContext, "n_user_head", heString);
				}
				Logger.i("---" + result.toString());
			} else {
				IwoToast.makeText(mContext, "上传头像失败").show();
			}
			if (!StringUtil.isEmpty(textnike)) {
				signature.setText(textnike);
			} else {
				PreferenceUtil.getString(mContext, "night_signature", "");
				signature.setText(textnike);
			}

		}

	}

	private class GetData extends
			AsyncTask<String, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_FR_USER_INFO_NIGHT), sex_text,
					getNoNullText(age), getNoNullText(signature));
		}

		private String getNoNullText(TextView view) {
			if (view != null) {
				if (null == view.getText()) {
					return "nbsp_null";
				} else if ("".equals(view.getText().toString().trim())) {
					return "nbsp_null";
				}
				return view.getText().toString();
			}
			return "nbsp_null";
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				makeText(result.get("msg"));
			}
			Intent intent = new Intent();
			intent.putExtra("sex", "" + back_sex);
			intent.putExtra("user_night", nick_name.getText().toString());
			intent.putExtra("user_head_img",
					PreferenceUtil.getString(mContext, "n_user_head", ""));
			intent.putExtra("sign", signature.getText().toString());
			intent.putExtra("age", age.getText().toString());
			setResult(RESULT_OK, intent);
			finish();
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

	@Override
	public void onBackPressed() {
		if (orEdit) {
			new GetData().execute();
		} else {
			finish();
		}

	}

	private Dialog showMyDialog(final String[] hour, String id) {
		final Dialog dialog = new Dialog(this, R.style.myDialogTheme);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.BOTTOM);
		View view = View
				.inflate(this, R.layout.view_user_set_time_dialog, null);
		final WheelView clockadd_hour_l = (WheelView) view
				.findViewById(R.id.clockadd_hour_l);
		clockadd_hour_l.setDensity(density);
		clockadd_hour_l.setScreenAdapter();
		clockadd_hour_l.setAdapter(new ArrayWheelAdapter<String>(hour));
		clockadd_hour_l.setVisibleItems(5);
		// if (shop_selected_item == 0) {
		// clockadd_hour_l.setCurrentItem(0);
		// } else {
		// clockadd_hour_l.setCurrentItem(shop_selected_item);
		// }

		dialog.setContentView(view);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.uset_set_time_cancel:
					dialog.cancel();
					break;
				case R.id.uset_set_time_determine:
					dialog.cancel();
					orEdit = true;
					int id = clockadd_hour_l.getCurrentItem();// 选中的item
					sex_text = String.valueOf(id + 1);
					back_sex = String.valueOf(id + 1);
					Logger.i("性别：" + sex_text + "---" + id);
					if ("2".equals(sex_text)) {
						sex.setText("女");
					} else {
						sex.setText("男");
					}
					break;
				default:
					break;
				}
			}
		};
		view.findViewById(R.id.uset_set_time_cancel).setOnClickListener(
				listener);
		view.findViewById(R.id.uset_set_time_determine).setOnClickListener(
				listener);
		return dialog;
	}

}
