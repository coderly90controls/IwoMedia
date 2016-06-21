package com.android.iwo.media.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.*;
import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.view.SpinerPopWindow;
import com.android.iwo.media.view.AbstractSpinerAdapter.IOnItemSelectListener;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.DateUtil;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class InfoEditNight extends BaseActivity {
	private SpinerPopWindow[] windows = new SpinerPopWindow[6];
	private ArrayList<HashMap<String, String>> list1, list2, list3, list4,
			list5, list6;
	private TextView[] text = new TextView[6];
	private String type1, type2, city_id, year, month, day, sign,
			industry_ID = "0", position_ID = "0";
	private String sex = "1", up = "", uid = "";
	private EditText nick_edit, sign_edit;
	private String model = "", mark = "1", imgUrl = "";
	private String path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info_edit);
		setPubParame();
		model = getIntent().getStringExtra("model");
		// PreferenceUtil.setString(this, "video_model", model);
		if (model.equals("night")) {
			up = "b" + PreferenceUtil.getString(this, "user_name", "");
			uid = PreferenceUtil.getString(this, "n_user_id", "");
		} else {
			up = PreferenceUtil.getString(this, "user_name", "");
			uid = PreferenceUtil.getString(this, "user_id", "");
		}
		init();
	}

	private void init() {
		setBack(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		// PreferenceUtil.setString(mContext, "video_model", model);
		mLoadBar = new CommonDialog(this, "加载数据");
		mLoadBar.show();
		nick_edit = (EditText) findViewById(R.id.nick_name);
		sign_edit = (EditText) findViewById(R.id.sign);
		list1 = new ArrayList<HashMap<String, String>>();
		list2 = new ArrayList<HashMap<String, String>>();
		list3 = new ArrayList<HashMap<String, String>>();
		list4 = new ArrayList<HashMap<String, String>>();
		list5 = new ArrayList<HashMap<String, String>>();
		list6 = new ArrayList<HashMap<String, String>>();
		// TODO
		setWindows(0, R.id.year); // 年
		setWindows(1, R.id.month);// 月
		setWindows(2, R.id.day);// 日
		setWindows(3, R.id.address);
		setWindows(4, R.id.type1);
		setWindows(5, R.id.type2);
		windows[4].setItemListener(new IOnItemSelectListener() {
			@Override
			public void onItemClick(int pos) {
				type1 = list5.get(pos).get("dict_name");
				list6.clear();
				ArrayList<HashMap<String, String>> list = DataRequest
						.getArrayListFromJSONArrayString(list5.get(pos).get(
								"children"));
				industry_ID = list5.get(pos).get("id");
				if (list != null) {
					list6.addAll(list);
					type2 = list6.get(0).get("dict_name");
				}
				windows[5].refreshData(list6, 0);
				setItem(4, pos, list5);
				setItem(5, 0, list6);
			}
		});

		windows[5].setItemListener(new IOnItemSelectListener() {

			@Override
			public void onItemClick(int pos) {
				type2 = list6.get(pos).get("dict_name");
				position_ID = list6.get(pos).get("id");
				setItem(5, pos, list6);
			}
		});

		windows[3].setItemListener(new IOnItemSelectListener() {
			public void onItemClick(int pos) {
				city_id = list4.get(pos).get("id");
				setItem(3, pos, list4);
			}
		});
		windows[0].setItemListener(new IOnItemSelectListener() {
			public void onItemClick(int pos) {
				year = list1.get(pos).get("dict_name");
				setItem(0, pos, list1);
				text[1].setText("");
				text[2].setText("");
				month = "";
				day = "";

			}
		});
		windows[1].setItemListener(new IOnItemSelectListener() {
			public void onItemClick(int pos) {
				month = list2.get(pos).get("dict_name");
				setItem(1, pos, list2);
				new GetDataTimeDay().execute();
				text[2].setText("");
				day = "";
			}
		});
		windows[2].setItemListener(new IOnItemSelectListener() {
			public void onItemClick(int pos) {
				day = list3.get(pos).get("dict_name");
				setItem(2, pos, list3);
			}
		});

		TextView ok = (TextView) findViewById(R.id.ok);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isRight()) {
					new SetOk().execute();
				}
			}
		});

		setView();
		setImage();
		new GetData().execute();
		new Get_City_List().execute();
		new Get_Dict_List().execute();
	}

	private void setView() {
		setTitle("完善资料");
		final Button boy = (Button) findViewById(R.id.boy);
		final Button grid = (Button) findViewById(R.id.gird);
		ImageView head = (ImageView) findViewById(R.id.head);

		String headstr, nick;
		if (model.equals("night")) {
			Drawable img_on, img_off;
			img_on = getResources().getDrawable(R.drawable.xuanzhong_n);
			img_off = getResources().getDrawable(R.drawable.weixuan_n);

			img_on.setBounds(0, 0, img_on.getMinimumWidth(),
					img_on.getMinimumHeight());
			img_off.setBounds(0, 0, img_off.getMinimumWidth(),
					img_off.getMinimumHeight());
			boy.setCompoundDrawables(img_on, null, null, null);
			grid.setCompoundDrawables(img_off, null, null, null);

			headstr = getPre("n_user_head");
			nick = getPre("n_user_nickname");

			mark = "2";
			setTitle("填写资料");
			findViewById(R.id.title).setBackgroundColor(
					getResources().getColor(R.color.comm_pink_color));
			if (model.equals("night")) {
				findViewById(R.id.line).setBackgroundColor(
						getResources().getColor(R.color.comm_pink_color));
			} else {
				findViewById(R.id.line).setBackgroundColor(
						getResources().getColor(R.color.comm_green_color));
			}

			findViewById(R.id.ok).setBackgroundResource(
					R.drawable.btn_pink_rect_selector);

			BitmapUtil.setImageResource((ImageView) findViewById(R.id.xiala1),
					R.drawable.xiala_n);
			BitmapUtil.setImageResource((ImageView) findViewById(R.id.xiala2),
					R.drawable.xiala_n);
			BitmapUtil.setImageResource((ImageView) findViewById(R.id.xiala3),
					R.drawable.xiala_n);
			BitmapUtil.setImageResource((ImageView) findViewById(R.id.xiala4),
					R.drawable.xiala_n);
			BitmapUtil.setImageResource((ImageView) findViewById(R.id.xiala5),
					R.drawable.xiala_n);
			BitmapUtil.setImageResource((ImageView) findViewById(R.id.xiala6),
					R.drawable.xiala_n);
		} else {
			headstr = getPre("user_head");
			nick = getPre("user_nickname");
		}

		if (!StringUtil.isEmpty(headstr)) {
			head.setVisibility(View.GONE);
			imgUrl = headstr;
		}
		if (!StringUtil.isEmpty(nick)) {
			nick_edit.setText(nick);
			nick_edit.setVisibility(View.GONE);
		}

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				int id1, id2;
				id1 = R.drawable.xuanzhong;
				id2 = R.drawable.weixuan;
				if (model.equals("night")) {
					id1 = R.drawable.xuanzhong_n;
					id2 = R.drawable.weixuan_n;
				}
				Drawable img_on, img_off;
				img_on = getResources().getDrawable(id1);
				img_off = getResources().getDrawable(id2);

				img_on.setBounds(0, 0, img_on.getMinimumWidth(),
						img_on.getMinimumHeight());
				img_off.setBounds(0, 0, img_off.getMinimumWidth(),
						img_off.getMinimumHeight());
				if (v.getId() == R.id.boy) {
					boy.setCompoundDrawables(img_on, null, null, null);
					grid.setCompoundDrawables(img_off, null, null, null);
					sex = "1";
				} else {
					boy.setCompoundDrawables(img_off, null, null, null);
					grid.setCompoundDrawables(img_on, null, null, null);
					sex = "2";
				}
			}
		};

		boy.setOnClickListener(listener);
		grid.setOnClickListener(listener);
	}

	private void setImage() {
		ImageView head = (ImageView) findViewById(R.id.head);
		head.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(mContext)
						.setTitle("设置头像")
						.setItems(new String[] { "相机拍摄", "相册选取" },
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (which == 0) {
											Intent intent2 = new Intent(
													MediaStore.ACTION_IMAGE_CAPTURE);
											Uri uri = Uri.fromFile(new File(
													Environment
															.getExternalStorageDirectory(),
													"xiaoma.jpg"));
											path = uri.getPath();
											intent2.putExtra(
													MediaStore.EXTRA_OUTPUT,
													uri);
											startActivityForResult(intent2, 2);

										} else if (which == 1) {
											Intent intent = new Intent(
													Intent.ACTION_PICK, null);
											intent.setDataAndType(
													MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
													"image/*");
											startActivityForResult(intent, 111);
										}
										dialog.dismiss();
									}

								}).show();
			}
		});
	}

	private boolean isRight() {
		sign = sign_edit.getText().toString();
		String nick = nick_edit.getText().toString();
		if (StringUtil.isEmpty(nick)) {
			makeText("请设置您的昵称");
			return false;
		} else if (StringUtil.isEmpty(imgUrl)) {
			makeText("请上传头像");
			return false;
		} else if (StringUtil.isEmpty(sign)) {
			makeText("写个签名表达一下");
			return false;
		} else if (StringUtil.isEmpty(year) || StringUtil.isEmpty(month)
				|| StringUtil.isEmpty(day)) {
			makeText("请选择出生年月");
			return false;
		}
		return true;
	}

	private void setWindows(int n, int id) {
		text[n] = (TextView) findViewById(id);
		windows[n] = new SpinerPopWindow(this);
		windows[n].setKey("dict_name");
		if (n == 0) {// 年
			Logger.i(list1.toString());
			windows[n].refreshData(list1, 0);

		} else if (n == 1) {// 月
			windows[n].refreshData(list2, 0);
		} else if (n == 2) {// 日
			windows[n].refreshData(list3, 0);
		} else if (n == 3) {
			windows[n].refreshData(list4, 0);
		} else if (n == 4) {
			windows[n].refreshData(list5, 0);
		} else if (n == 5) {
			windows[n].refreshData(list6, 0);
		}

		final RelativeLayout address_1_layout = (RelativeLayout) findViewById(R.id.address_1_layout);
		final RelativeLayout address_2_layout = (RelativeLayout) findViewById(R.id.address_2_layout);
		final RelativeLayout address_3_layout = (RelativeLayout) findViewById(R.id.address_3_layout);
		final RelativeLayout address_4_layout = (RelativeLayout) findViewById(R.id.address_4_layout);
		final RelativeLayout address_5_layout = (RelativeLayout) findViewById(R.id.address_5_layout);
		final RelativeLayout address_6_layout = (RelativeLayout) findViewById(R.id.address_6_layout);

		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.address_1_layout && windows[0] != null) {
					windows[0].setWidth(address_1_layout.getWidth());
					windows[0].showAsDropDown(address_1_layout);
				} else if (v.getId() == R.id.address_2_layout
						&& windows[1] != null) {
					if (StringUtil.isEmpty(text[0].getText().toString())) {
						makeText("请选择年份");
					} else {
						windows[1].setWidth(address_2_layout.getWidth());
						windows[1].showAsDropDown(address_2_layout);
					}
				} else if (v.getId() == R.id.address_3_layout
						&& windows[2] != null) {
					if (StringUtil.isEmpty(text[1].getText().toString())) {
						makeText("请选择月份");
					} else {
						windows[2].setWidth(address_3_layout.getWidth());
						windows[2].showAsDropDown(address_3_layout);
					}
				} else if (v.getId() == R.id.address_4_layout
						&& windows[2] != null) {
					windows[3].setWidth(address_4_layout.getWidth());
					windows[3].showAsDropDown(address_4_layout);
				} else if (v.getId() == R.id.address_5_layout
						&& windows[2] != null) {
					windows[4].setWidth(address_5_layout.getWidth());
					windows[4].showAsDropDown(address_5_layout);
				} else if (v.getId() == R.id.address_6_layout
						&& windows[2] != null) {
					windows[5].setWidth(address_6_layout.getWidth());
					windows[5].showAsDropDown(address_6_layout);
				}
			}
		};

		address_1_layout.setOnClickListener(listener);
		address_2_layout.setOnClickListener(listener);
		address_3_layout.setOnClickListener(listener);
		address_4_layout.setOnClickListener(listener);
		address_5_layout.setOnClickListener(listener);
		address_6_layout.setOnClickListener(listener);
	}

	private void setItem(int n, int pos, List<HashMap<String, String>> list12) {
		Logger.i("pos" + pos + "---" + list12.size());
		if (pos >= 0 && pos <= list12.size() && list12.size() != 0) {
			String value = list12.get(pos).get("dict_name");
			Logger.i("value " + value);
			text[n].setText(value);
		} else {
			text[n].setText("");
		}
	}

	private class SetOk extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected void onPreExecute() {
			mLoadBar.show();
		}

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			if ("night".equals(model)) {
				return DataRequest.getHashMapFromUrl_Base64(
						getUrl(AppConfig.NEW_REG_PERFECT) + "&uid=" + uid
								+ "&up=" + up + "&nick_name="
								+ nick_edit.getText().toString(), year + "-"
								+ month + "-" + day, sex, city_id, type1,
						type2, sign, mark);
			} else
				return DataRequest.getHashMapFromUrl_Base64(
						getUrl(AppConfig.NEW_REG_PERFECT) + "&uid=" + uid
								+ "&up=" + up + "&nick_name="
								+ nick_edit.getText().toString(), year + "-"
								+ month + "-" + day, sex, city_id, type1,
						type2, sign, mark);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				Logger.i("---" + result.get("code"));
				if ("1".equals(result.get("code"))) {
					HashMap<String, String> map = DataRequest
							.getHashMapFromJSONObjectString(result.get("data"));
					HashMap<String, String> day = null;
					HashMap<String, String> night = null;
					if (map != null) {
						day = DataRequest.getHashMapFromJSONObjectString(map
								.get("dinfo"));
						night = DataRequest.getHashMapFromJSONObjectString(map
								.get("ninfo"));
						Logger.i(" 信息：  " + day.toString());
					}
					if (day != null) {
						PreferenceUtil.setString(mContext, "user_name",
								day.get("user_name"));
						PreferenceUtil.setString(mContext, "user_id",
								day.get("id"));
						PreferenceUtil.setString(mContext, "real_name",
								day.get("real_name"));
						PreferenceUtil.setString(mContext, "user_pass",
								day.get("user_pass"));
						PreferenceUtil.setString(mContext, "user_head",
								day.get("head_img"));
						PreferenceUtil.setString(mContext, "user_city_id",
								day.get("city_id"));
						PreferenceUtil.setString(mContext, "user_nickname",
								day.get("nickname"));
						PreferenceUtil.setString(mContext, "user_industry_id",
								industry_ID);
						PreferenceUtil.setString(mContext, "user_position_id",
								position_ID);

					}
					if (night != null) {
						PreferenceUtil.setString(mContext, "n_user_id",
								night.get("id"));
						PreferenceUtil.setString(mContext, "n_real_name",
								night.get("real_name"));
						PreferenceUtil.setString(mContext, "n_user_head",
								night.get("head_img"));
						PreferenceUtil.setString(mContext, "n_user_city_id",
								night.get("city_id"));
						PreferenceUtil.setString(mContext, "n_user_nickname",
								night.get("nick_name"));
					}
					 
					
						PreferenceUtil
								.setString(mContext, "video_model", "day");
						Intent intent = new Intent(mContext, ModelActivity.class);
						PreferenceUtil
								.setString(mContext, "dayloginset", "yes");
					 

					if (intent != null) {
						startActivity(intent);
						finish();
					}
				} else {
					makeText(result.get("msg"));
				}
			}
			mLoadBar.dismiss();
		}
	}

	private class Get_Dict_List extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Void... params) {
			return DataRequest.getList_64(getUrl(AppConfig.NEW_GET_DICT_LIST),
					"103");
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (result != null && result.size() > 0) {
				if (!StringUtil.isEmpty(result.get(0).get("children"))) {

					ArrayList<HashMap<String, String>> list = DataRequest
							.getArrayListFromJSONArrayString(result.get(0).get(
									"children"));
					list5.addAll(list);
					windows[4].refreshData(list5, 0);
					setItem(4, 0, list5);
					if (list5.size() != 0)
						type1 = list5.get(0).get("dict_name");

					list6.clear();
					if (list5.size() > 0) {
						ArrayList<HashMap<String, String>> list1 = DataRequest
								.getArrayListFromJSONArrayString(list5.get(0)
										.get("children"));
						if (list1 != null)
							list6.addAll(list1);
						windows[5].refreshData(list6, 0);
						setItem(5, 0, list6);
						if (list6.size() != 0)
							type2 = list6.get(0).get("dict_name");
					}

				}
			}
		}
	}

	private class Get_City_List extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Void... params) {
			return DataRequest.getList_64(getUrl(AppConfig.NEW_GET_CITY_LIST),
					"101");
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (result != null && result.size() != 0) {
				if (!StringUtil.isEmpty(result.get(0).get("children"))) {
					ArrayList<HashMap<String, String>> list = DataRequest
							.getArrayListFromJSONArrayString(result.get(0).get(
									"children"));
					list4.addAll(list);
					windows[3].refreshData(list4, 0);
					setItem(3, 0, list4);
					if (list4.size() != 0)
						city_id = list4.get(0).get("id");
				}

				mLoadBar.dismiss();
			}
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
			return;
		}
	}

	private class GetData extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getMap_64(getUrl(AppConfig.NEW_MY_INFO)
					+ "&uid=" + uid + "&up=" + up);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				Logger.i("资料信息：" + result);
				if (!StringUtil.isEmpty(result.get("birthday"))) {
					String[] str = result.get("birthday").split("-");
					text[0].setText(str[0]);
					text[1].setText(str[1]);
					text[2].setText(str[2].replace(" 00:00:00", ""));
					year = str[0];
					month = str[1];
					day = str[2].replace(" 00:00:00", "");
				}
				text[3].setText(result.get("dict_name"));
				text[4].setText(result.get("user_trade"));
				text[5].setText(result.get("user_job"));

				type1 = result.get("user_trade");
				type2 = result.get("user_job");
				city_id = result.get("dict_name");
				sign_edit.setText(result.get("sign"));
				sign = result.get("sign");
			}

			new GetDataTime().execute();
		}
	}

	/**
	 * 获取出生日期的数据
	 * 
	 * @author Weiguixiang
	 * 
	 */
	private class GetDataTime extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {
			list1.addAll(DateUtil.getAllYear());
			list2.addAll(DateUtil.getAllMonth());
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			/*
			 * setWindows(0, R.id.year); //年 setWindows(1, R.id.month);//月
			 */new GetDataTimeDay().execute();
		}
	}

	private class GetDataTimeDay extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {
			if (!StringUtil.isEmpty(year) && !StringUtil.isEmpty(month)) {
				String itemMonth = month;
				if (month.startsWith("0")) {
					itemMonth.substring(1);
				}
				list3.clear();
				list3.addAll(DateUtil.getAllDay(Integer.valueOf(year),
						Integer.valueOf(itemMonth)));
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}

	private String heString = "";

	private class SendMsg extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			mLoadBar.setMessage("上传头像...");
			mLoadBar.show();
		}

		@Override
		protected String doInBackground(String... params) {
			Logger.i("sfsd" + params[0]);
			String string = DataRequest.getStringFrom_base64(DataRequest
					.SendFile(getUrl(AppConfig.SEND_HEAD_IMG) + "cut=2",
							"ufile", params[0]));
			Logger.i("上传头像：" + string);
			if (!StringUtil.isEmpty(string)) {
				HashMap<String, String> map = DataRequest
						.getHashMapFromJSONObjectString(string);
				if (map != null) {
					heString = map.get("uploadFileUrl_b");
					Logger.i("  uid:" + uid + "    up:" + up);
					String data = DataRequest.getStringFromURL_Base64(
							getUrl(AppConfig.SEND_IMG_CUT_TMP) + "&uid=" + uid
									+ "&up=" + up, heString, mark);
					Logger.i("------" + data);
					return data;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Logger.i("-------onPostExecute" + result);
			if (!StringUtil.isEmpty(result)) {
				HashMap<String, String> map = DataRequest
						.getHashMapFromJSONObjectString(result);
				if ("1".equals(map.get("code"))) {
					ImageView head = (ImageView) findViewById(R.id.head);
					LoadBitmap.getIntence().loadImage(heString, head);
					imgUrl = heString;
					if (model.equals("day")) {
						PreferenceUtil.setString(mContext, "user_head",
								heString);
					} else {
						PreferenceUtil.setString(mContext, "n_user_head",
								heString);
					}

					makeText("上传头像成功");
				}

				Logger.i("---" + result.toString());
			} else {
				makeText("上传头像失败");
			}

			if (mLoadBar != null) {
				mLoadBar.dismiss();
			}
		}
	}

	private void startPhotoZoom(Uri uri) {
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

	private void saveMyBitmap(String bitName, Bitmap mBitmap) {
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
}
