package com.android.iwo.media.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.NetworkUtil;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MeYeActivity extends BaseActivity {
	private LinearLayout camel;
	private String path;
	private String uid, up, mark = "2";
	private HashMap<String, String> mData = new HashMap<String, String>();
	private ArrayList<HashMap<String, String>> adapterData = new ArrayList<HashMap<String, String>>();
	private MyAdapter adapter;
	private boolean isdelete = true;// 定义一个标志来区别 删除状态 和平常状态 默认true 为平常状态
	List<String> map = new ArrayList<String>();
	private boolean iss; // 删除一个和删除全部的 区别
	private int pos;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_me_layout_3);
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		init();
	}

	private void init() {
		inf = LayoutInflater.from(mContext);
		screenWidth = PreferenceUtil.getInt(mContext, "screenWidth", 100) - 22;
		initButton();
		if (NetworkUtil.isWIFIConnected(mContext)) {
			new GetData().execute();
		}
		adapter = new MyAdapter();
		String headString = getPre("head_img");
		ImageView head = (ImageView) findViewById(R.id.me_yonghu_head);
		Logger.i("头像地址：" + headString);
		if (StringUtil.isEmpty(headString)) {
			head.setImageResource(R.drawable.user_edit);
		} else {
			LoadBitmap.getIntence().loadImage(headString, head);

		}
		textname = (TextView) findViewById(R.id.me_layout_textname);
		if (!StringUtil.isEmpty(getPre("nick_name"))) {
			textname.setText(getPre("nick_name"));
		}
		textphone = (TextView) findViewById(R.id.me_layout_user_phone);// 账号信息
		if (!StringUtil.isEmpty(getPre("user_name"))) {
			textphone.setText(getPre("user_name"));
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
		/*
		 * sign_qianmian = (TextView) findViewById(R.id.me_layout_ziliao); if
		 * (!StringUtil.isEmpty(getPre("sign"))) {
		 * sign_qianmian.setText(getPre("sign")); } else {
		 * sign_qianmian.setText("你没设计个人签名！"); }
		 */
		// setTitle("我的");
		setBack(null);
		ImageView right = (ImageView) findViewById(R.id.right_img1);
		right.setVisibility(View.VISIBLE);
		// findViewById(R.id.comm_title_line).setVisibility(View.INVISIBLE);
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
						gv.setVisibility(View.GONE);
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
									gv.setVisibility(View.VISIBLE);
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

	@SuppressLint("NewApi")
	private void initButton() {
		gv = (GridView) findViewById(R.id.me_layout_grid_view);
		me_button = (RelativeLayout) findViewById(R.id.me_button);
		me_textview = (LinearLayout) findViewById(R.id.me_textview_null);
		// te = (TextView) findViewById(R.id.me_Text_all);
		// be = (Button) findViewById(R.id.me_button_all);
		final ImageButton delete = (ImageButton) findViewById(R.id.me_button_complete);
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isdelete) {
					// te.setVisibility(View.GONE);
					// be.setVisibility(View.VISIBLE);
					// 在这里设计背景图
					delete.setBackground(getResources().getDrawable(
							R.drawable.schy_1));
				} else {
					// te.setVisibility(View.VISIBLE);
					// be.setVisibility(View.GONE);
					delete.setBackground(getResources().getDrawable(
							R.drawable.wo_delete));
				}
				isdelete = !isdelete;
				adapter.notifyDataSetChanged();
				// delete.setVisibility(View.GONE);
				// be.setVisibility(View.INVISIBLE);
			}
		});
		/*
		 * be.setOnClickListener(new OnClickListener() { // 全部删除
		 * 
		 * public void onClick(View v) { if (adapterData.size() > 0) {
		 * StringBuilder sb = new StringBuilder(); for (int i = 0; i <
		 * adapterData.size(); i++) { String id = adapterData.get(i).get("id");
		 * sb.append(id); sb.append(",");
		 * 
		 * } String idss = sb.deleteCharAt(sb.length() - 1).toString();
		 * map.add(idss); iss = false; new deleteHistory().execute();
		 * 
		 * }
		 * 
		 * } });
		 */
	}

	private void initAdapter() {

		gv.setVisibility(View.VISIBLE);
		gv.setAdapter(adapter);

		// 在这里需要区分两种状态
		gv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, String> map_item = adapterData.get(position);
				if (isdelete) { // 跳转到详情界面
					/*
					 * if ("2".equals(map_item.get("is_sys"))) { Intent intent =
					 * new Intent(mContext, AdWebActivity.class);
					 * intent.putExtra("url", map_item.get("video_url"));
					 * intent.putExtra("title", map_item.get("name"));
					 * startActivity(intent); }
					 */
					// else {
					Intent intent = new Intent(MeYeActivity.this,
							VideoDetailActivity_new.class);
					intent.putExtra("video_id", map_item.get("video_id"));
					startActivity(intent);
					// }

				} else {
					String str_id = adapterData.get(position).get("id");

					map.add(str_id);
					pos = position;
					iss = true;
					new deleteHistory().execute();
				}

			}
		});
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return adapterData.size();
		}

		@Override
		public Object getItem(int position) {

			return adapterData.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			convertView = inf.inflate(R.layout.activity_me_new_gr_item, parent,
					false);
			ImageView img = (ImageView) convertView
					.findViewById(R.id.me_item_new_image);
			ImageView delete = (ImageView) convertView
					.findViewById(R.id.me_item_new_delete11);
			HashMap<String, String> hashMap = adapterData.get(position);
			String headString = hashMap.get("video_img");
			if (!StringUtil.isEmpty(headString)) {
				LoadBitmap.getIntence().loadImage(headString, img);
			} else {
				Bitmap bitmap = android.graphics.BitmapFactory.decodeResource(
						getResources(), R.drawable.a190_190);
				if (bitmap != null) {
					Bitmap bitmap2 = BitmapUtil.getBitmap(bitmap,
							screenWidth / 2, screenWidth / 2);
					if (bitmap2 != null) {
						BitmapUtil.setImageBitmap(img, bitmap2);
					}
				}
				// img.setBackground(getResources().getDrawable(R.drawable.a190_190));
			}
			android.view.ViewGroup.LayoutParams params = img.getLayoutParams();

			if (!isdelete) { // 在删除模式显示
				delete.setVisibility(View.VISIBLE);
			} else {
				delete.setVisibility(View.GONE);
			}
			params.height = screenWidth / 2;
			return convertView;

		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		gv.setVisibility(View.VISIBLE);
		if (requestCode == 911) {
			if (!StringUtil.isEmpty(getPre("nick_name"))) {
				textname.setText(getPre("nick_name"));
			}
			if (!StringUtil.isEmpty(getPre("user_name"))) {
				textphone.setText(getPre("user_name"));
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
	// private TextView te;
	// private Button be;
	private RelativeLayout me_button;
	private LinearLayout me_textview;
	private TextView textname;
	private TextView textphone;
	private ImageView textsex;
	private GridView gv;
	private LayoutInflater inf;
	private int screenWidth;

	// private TextView sign_qianmian;

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
				ImageView head = (ImageView) findViewById(R.id.me_yonghu_head);
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

	// 获取浏览记录
	private class GetData extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {

			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_FR_USER_INFO_NIGHT_GET_HEAD_HISTORY),
					"10", "1");

		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			mLoadBar.dismiss();
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					Logger.i("xyz--->表明已登陆" + result);
					ArrayList<HashMap<String, String>> list = DataRequest
							.getArrayListFromJSONArrayString(result.get("data"));
					if (!StringUtil.isEmpty(list)) {
						me_button.setVisibility(View.VISIBLE);
						me_textview.setVisibility(View.GONE);
						findViewById(R.id.me_button_complete).setVisibility(
								View.VISIBLE);
						adapterData.addAll(list);
						initAdapter();

					}

				} else if ("-1".equals(result.get("code"))) {
					makeText("没登陆，请重新退出登录");
				}
			} else {
				makeText("暂无浏览记录");
			}
		}
	}

	private class deleteHistory extends
			AsyncTask<String, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			String string = map.get(0);

			return DataRequest
					.getHashMapFromUrl_Base64(
							getUrl(AppConfig.NEW_FR_USER_INFO_NIGHT_DELETE_HEAD_HISTORY),
							string);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {

			Logger.i(result.get("data"));
			Logger.i("删除的ID号：" + map.get(0));
			super.onPostExecute(result);
			if ("1".equals(result.get("code"))) {
				// isDelete=true;
				if (iss) {
					if (!StringUtil.isEmpty(pos + "")) {
						adapterData.remove(pos);
						if (map.size() > 0) {
							map.clear();
						}
						if (adapterData.size() == 0) {
							// me_button.setVisibility(View.INVISIBLE);
							findViewById(R.id.me_Text_all).setVisibility(
									View.VISIBLE);
							me_textview.setVisibility(View.VISIBLE);
							findViewById(R.id.me_button_complete)
									.setVisibility(View.GONE);
							// be.setVisibility(View.INVISIBLE);
						}
					}

				} else {
					adapterData.clear();
					// me_button.setVisibility(View.INVISIBLE);
					findViewById(R.id.me_Text_all).setVisibility(View.VISIBLE);
					me_textview.setVisibility(View.VISIBLE);
					findViewById(R.id.me_button_complete).setVisibility(
							View.GONE);
					// be.setVisibility(View.INVISIBLE);

				}
				makeText("删除成功");
				adapter.notifyDataSetChanged();

			} else {
				makeText(result.get("msg"));

			}
		}

	}

}
