package com.android.iwo.media.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.util.StringUtils;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.lenovo.R;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.DateUtil;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

import android.R.integer;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MeActivity extends BaseActivity {
	private boolean isbutton = true;
	ArrayList<HashMap<String, String>> adapterData = new ArrayList<HashMap<String, String>>();
	private Button buttom_all;
	private Button button_delete;
	private ListView me_listview;
	private LinearLayout camel;
	private String path;
	private String uid, up, mark = "2";
	private IwoAdapter adapter;
	private boolean iss;  //删除一个和删除全部的 区别
	private int pos;
	List<String> map = new ArrayList<String>();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_me_layout_2);
		init();
	}

	private void init() {

		 String headString = getPre("head_img");
		ImageView head = (ImageView) findViewById(R.id.me_yonghu_head);

		Logger.i("头像地址：" + headString);
		if (StringUtil.isEmpty(headString)) {
			head.setImageResource(R.drawable.ico_default);
		} else {
			LoadBitmap.getIntence().loadImage(headString, head);
		}
		re = (RelativeLayout)findViewById(R.id.me_button);
		te = (LinearLayout)findViewById(R.id.me_textview_null);
		
		buttom_all = (Button) findViewById(R.id.me_button_all);
		button_delete = (Button) findViewById(R.id.me_button_complete);
		me_listview = (ListView) findViewById(R.id.me_listview);

		button_delete.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isbutton) {
					Logger.i("完成");
					buttom_all.setVisibility(View.VISIBLE);
					button_delete.setText("完成");
				} else {
					button_delete.setText("删除");
					buttom_all.setVisibility(View.GONE);
				}

				isbutton = !isbutton;
				adapter.notifyDataSetChanged();
			}
		});
		buttom_all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(adapterData.size()>0){
				StringBuilder sb =new StringBuilder();
				for(int i=0;i<adapterData.size();i++){
					String id = adapterData.get(i).get("id");
					sb.append(id);
					sb.append(",");
					
				}
			     String idss = sb.deleteCharAt(sb.length()-1).toString();
			     map.add(idss);
			     iss=false;
				new deleteHistory().execute();
				
			}
			}

		});

		setTitle("我的主页");
		setBack(null);
		ImageView right = (ImageView) findViewById(R.id.right_img);
		right.setVisibility(View.VISIBLE);
		BitmapUtil.setImageResource(right, R.drawable.setting);
		right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), UserSetListActivity.class);
				startActivity(intent);
			}
		});
		new GetData1().execute();
		camel = (LinearLayout) findViewById(R.id.camer);
		// 图片的点击
		findViewById(R.id.me_yonghu_head).setOnClickListener(new OnClickListener() {

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
				up = PreferenceUtil.getString(mContext, "user_name", "");
				OnClickListener listener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						switch (v.getId()) {
						case R.id.nor: // 照片拍摄
							Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "xiaoma.jpg"));
							path = uri.getPath();
							intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							startActivityForResult(intent2, 2);
							camel.setVisibility(View.GONE);
							break;
						case R.id.model:// 图库选择
							Intent intent = new Intent(Intent.ACTION_PICK, null);
							intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
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
	//获取浏览记录
	private class GetData1 extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_FR_USER_INFO_NIGHT_GET_HEAD_HISTORY), "10", "1");
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if ("1".equals(result.get("code"))) {
				ArrayList<HashMap<String, String>> list = DataRequest.getArrayListFromJSONArrayString(result.get("data"));
				Logger.i("浏览记录数据：" + DataRequest.getArrayListFromJSONArrayString(result.get("data")));
				if(!StringUtil.isEmpty(list)){
					te.setVisibility(View.GONE);
					re.setVisibility(View.VISIBLE);
					adapterData.addAll(list);
				}
				
				
			} else if ("-1".equals(result.get("code"))) {
				makeText("没登陆，请重新退出登录");
			}
			initAdapter();
		}

		// 适配数据
		private void initAdapter() {
			
			adapter = new IwoAdapter(MeActivity.this, adapterData) {

				private ViewHolder holder;

				@Override
				public View getView(final int position, View v, ViewGroup parent) {

					if (v == null) {
						holder = new ViewHolder();
						v = View.inflate(MeActivity.this, R.layout.me_layout_item, null);
						holder.content = (TextView) v.findViewById(R.id.me_layout_content);
						holder.button_delateButton = (Button) v.findViewById(R.id.me_layout_button_delete);
						holder.time = (TextView) v.findViewById(R.id.me_layout_time);
						holder.user_ing=(ImageView)v.findViewById(R.id.me_layout_imageview);
						v.setTag(holder);

					} else {
						holder = (ViewHolder) v.getTag();
					}
					if (adapterData.size() > 0) {
						HashMap<String, String> hashMap = adapterData.get(position);
						holder.content.setText(hashMap.get("video_name"));
						String str_time = hashMap.get("create_time");
						String time = DateUtil.getTime(str_time);
						holder.time.setText(time);
						String headString = getPre("head_img");
						if (!StringUtil.isEmpty(headString)) {
							LoadBitmap.getIntence().loadImage(headString, holder.user_ing);
						} 

						// 点删除按钮出现
						if (isbutton) { // 显示删除
							holder.button_delateButton.setVisibility(View.GONE);
							holder.time.setVisibility(View.VISIBLE);
						} else { // 显示完成
							holder.button_delateButton.setVisibility(View.VISIBLE);
							holder.time.setVisibility(View.GONE);
							holder.button_delateButton.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									Logger.i("点击删除");
									if (adapterData.size() > 0) {
										showdelete(position);
									}

								}

								private void showdelete(int position) {
									String str_id = adapterData.get(position).get("id");
									
									map.add(str_id);
									pos=position;
									iss=true;
									new deleteHistory().execute();
																	

								}
							});
						}
					}
					return v;
				}

			};
			me_listview.setAdapter(adapter);

		}

	}

	private class ViewHolder {
		TextView content;
		Button button_delateButton;
		TextView time;
		ImageView user_ing;
	}

	private class deleteHistory extends AsyncTask<String, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			 String string = map.get(0);
			
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_FR_USER_INFO_NIGHT_DELETE_HEAD_HISTORY), string);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {

			Logger.i(result.get("data"));
			Logger.i("删除的ID号：" + map.get(0));
			super.onPostExecute(result);
			if ("1".equals(result.get("code"))) {
				//isDelete=true;
				if(iss){
					if(!StringUtil.isEmpty(pos+"")){
					adapterData.remove(pos);
					 if(adapterData.size()==0){
						re.setVisibility(View.GONE);
						te.setVisibility(View.VISIBLE);
					}
				 }
					
				}
				else{
					adapterData.clear();
					//--新增---
					re.setVisibility(View.GONE);
					te.setVisibility(View.VISIBLE);
				}
				makeText("删除成功!!!");
				adapter.notifyDataSetChanged();

			}
			else{
				makeText(result.get("msg"));
				
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

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

	private String heString = "";
	private RelativeLayout re;
	private LinearLayout te;
	

	private class SendMsg extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {
			Logger.i("sfsd" + params[0]);
			String string = DataRequest.getStringFrom_base64(DataRequest.SendFile(getUrl(AppConfig.SEND_HEAD_IMG) + "cut=2", "ufile",
					params[0]));
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
				ImageView head = (ImageView) findViewById(R.id.me_yonghu_head);
				LoadBitmap.getIntence().loadImage(heString, head);
				Logger.i(" 首页图片地址： " + heString);
				PreferenceUtil.setString(mContext, "head_img", heString);
				//上传图片成功保存在到服务器
				new SendMsg2().execute(result);
				makeText("上传头像成功");
				
				adapter.notifyDataSetChanged();
	
				
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
			return DataRequest.getStringFromURL_Base64(getUrl(AppConfig.SEND_IMG_CUT_TMP) + "&uid=" + getPre("user_id") + "&up=" + getPre("user_name"), params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
		}
	}

	@Override
	protected void onResume() {
		if(!StringUtil.isEmpty(adapter)){
			adapter.notifyDataSetChanged();
		}
		
		super.onResume();
	}


}
