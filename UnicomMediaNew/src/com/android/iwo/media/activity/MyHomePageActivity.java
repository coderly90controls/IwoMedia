package com.android.iwo.media.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.Constants;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.view.IwoToast;
import com.android.iwo.media.view.XListView;
import com.android.iwo.media.view.XListView.IXListViewListener;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.DateUtil;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;
import com.test.iwomag.android.pubblico.util.LoadBitmap.LoadBitmapCallBack;

public class MyHomePageActivity extends BaseActivity implements OnClickListener {
	String path;
	private LinearLayout camel;
	private View listViewHead;
	protected XListView listView;
	protected ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private HashMap<String, String> userData = new HashMap<String, String>();
	protected IwoAdapter mAdapter;
	protected static boolean isrefresh = true;

	private boolean setUserImg = false;
	private String uid, up, mark = "1";
	private boolean scrollOn = true;
	private final int REQUEST_PHOTOGRAPH = 201404221;
	private final int REQUEST_LOCAL = 201404;
	private final int REQUEST_UP = 2014042222;
	private final int REQUEST_UPLOAD_VIDEO = 201404251;
	private boolean dataResult = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_home_page);
		setBack(null);
		setTitle("我的主页");
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		setViewGone(R.id.right_img, false);
		setImage(R.id.right_img, R.drawable.setting);
		camel = (LinearLayout) findViewById(R.id.camer);
		uid = getUid();
		up = getUserTel();
		setViewClick();
		listViewHead = View.inflate(this, R.layout.view3_layout_head, null);
		listViewHead.setVisibility(View.GONE);
		listView = (XListView) findViewById(R.id.view3_layout_list);
		listView.addHeaderView(listViewHead);
	}

	@Override
	protected void onStart() {
		super.onStart();
		isrefresh = true;
		new GetUserData().execute();
		init();
	}

	private void init() {
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mAdapter = new IwoAdapter((Activity) this, mData) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				// TODO Auto-generated method stub
				if (null == v) {
					v = mInflater.inflate(R.layout.view3_layout_item, null);
				}
				Map<String, String> map = mData.get(position);
				final ImageView userImg = (ImageView) v.findViewById(R.id.head);
				LoadBitmap bitmap = new LoadBitmap();

				if (!StringUtil.isEmpty(map.get("head_img"))) {
					bitmap.loadImage(map.get("head_img"), new LoadBitmapCallBack() {

						@Override
						public void callBack(Bitmap bit) {
							if (bit != null) {
								userImg.setImageBitmap(BitmapUtil.toRoundCorner(bit, 91));
							}
						}
					});
					userImg.setBackgroundColor(mContext.getResources().getColor(R.color.comm_bg_color));
				} else {
					userImg.setBackgroundResource(R.drawable.ico_default);
				}
				setItem(v, R.id.name, map.get("nickname"));
				if (!StringUtil.isEmpty(map.get("create_time"))) {
					setItem(v, R.id.time, DateUtil.format("MM-dd HH:mm", "" + DateUtil.getTime("yyyy-MM-dd HH:mm:ss", map.get("create_time"))));
				}

				TextView time = (TextView) v.findViewById(R.id.time);
				time.setTextColor(getResources().getColor(R.color.comm_green_color));
				userImg.setTag(position);

				setTextViewValue((TextView) v.findViewById(R.id.synopsis), map.get("name"), getAuditText(map.get("audit_status")), "");
				ImageView big_img = (ImageView) v.findViewById(R.id.item_img);
				setImgSize(big_img, 64, 169 / 300.0f, 1);
				bitmap.loadImage(map.get("img_url_2"), big_img);
				big_img.setTag(position);
				big_img.setOnClickListener(MyHomePageActivity.this);
				setItem(v, R.id.comments_text, StringUtil.isEmpty(map.get("ping_count")) ? "0" : map.get("ping_count"));
				setItem(v, R.id.share_text, StringUtil.isEmpty(map.get("share_count")) ? "0" : map.get("share_count"));
				setItem(v, R.id.play_text, StringUtil.isEmpty(map.get("play_count")) ? "0" : map.get("play_count"));
				int[] viewID = { R.id.comments, R.id.play, R.id.share };
				int[] d_img = { R.drawable.d_pinglun, R.drawable.d_bofang, R.drawable.d_fenxiang };
				int[] n_img = { R.drawable.n_pinglun, R.drawable.n_bofang, R.drawable.n_fenxiang };
				setItemViewImg(v, viewID, d_img, n_img);
				return v;
			}

		};
		listView.setCacheColorHint(0);
		listView.setDividerHeight(0);
		listView.setAdapter(mAdapter);
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				View lastItemView = (View) listView.getChildAt(listView.getChildCount() - 1);
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if (listView.getBottom() == lastItemView.getBottom() + (int) (dm.density * 44)) {
							if (scrollOn) {
								new GetData().execute();
							} else {
								if (mData.size() < 10) {
									IwoToast.makeText(mContext, "没有更多内容").show();
								} else {
									if (dataResult) {
										IwoToast.makeText(mContext, "加载更多中").show();
									} else {
										IwoToast.makeText(mContext, "没有更多内容").show();
									}

								}
							}
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int first, int visible, int total) {
				if ((first + visible == total)) {
					// new GetData().execute();
				}
			}
		});
		listView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				isrefresh = true;
				new GetData().execute();
			}
		});
	}

	protected String getAuditText(String string) {
		if ("0".equals(string)) {
			return " (审核中)";
		} else if ("1".equals(string)) {
			return " (审核通过)";
		} else if ("2".equals(string)) {
			return " (审核失败)";
		}
		return null;
	}

	private void setTextViewValue(TextView tv, String prefix, String body, String suffix) {
		tv.setText(Html.fromHtml(prefix + "<font color=red>" + body + "</font>" + suffix));
	}

	private void setUser() {
		listViewHead.setVisibility(View.VISIBLE);
		ImageView user_imgImageView = (ImageView) listViewHead.findViewById(R.id.view3_layout_user_img);
		LoadBitmap bitmap = new LoadBitmap();
		if (!StringUtil.isEmpty(userData.get("head_img"))) {
			bitmap.loadImage(userData.get("head_img"), user_imgImageView);
		}
		user_imgImageView.setOnClickListener(this);
		TextView user_nameTextView = (TextView) listViewHead.findViewById(R.id.view3_layout_user_name);
		user_nameTextView.setText(userData.get("nick_name"));
		ImageView layout_head_img = (ImageView) findViewById(R.id.view3_layout_head_img);

		String i = PreferenceUtil.getString(this, "user_bg_img_id", "1");
		try {
			setImgSize(layout_head_img, 0, 7 / 12.0f, 1);
			layout_head_img.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("wallpaper" + i + ".jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		layout_head_img.setOnClickListener(this);

		setViewShow(listViewHead, R.id.view3_layout_signature);
		setViewShow(listViewHead, R.id.view3_layout_signature_content);
		if (StringUtil.isEmpty(userData.get("sign"))) {
			TextView signature = (TextView) findViewById(R.id.view3_layout_signature_content);
			signature.setTextColor(getResources().getColor(R.color.color_f3f3f3));
			setItem(listViewHead, R.id.view3_layout_signature_content, "这个家伙很懒什么也没有留下~");
		} else {
			setItem(listViewHead, R.id.view3_layout_signature_content, userData.get("sign"));
		}
		listViewHead.findViewById(R.id.user_shooting).setVisibility(View.VISIBLE);
		listViewHead.findViewById(R.id.user_shooting).setOnClickListener(this);
		View lineView = listViewHead.findViewById(R.id.line);
		lineView.setVisibility(View.GONE);
	}

	private void setViewClick() {
		findViewById(R.id.right_img).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int position;
		Map<String, String> map = null;
		Intent intent = null;
		switch (v.getId()) {
		case R.id.item_img: // 视频图片
			position = (Integer) v.getTag();
			map = mData.get(position);
			intent = new Intent(mContext, VideoDetailActivity.class);
			intent.putExtra("video_id", map.get("video_id"));
			intent.putExtra("nickname", map.get("nickname"));
			intent.putExtra("create_time", map.get("create_time"));
			intent.putExtra("head_img", map.get("head_img"));
			mContext.startActivity(intent);
			break;
		case R.id.right_img: // 设置用户信息
			//设置用户是否登录
			intent = new Intent(mContext, UserSetListActivity.class);
			startActivityForResult(intent, 20140419);// 判断是否是用户设置界面退出。
			break;

		case R.id.left_img: // 返回键

			break;
		case R.id.view3_layout_user_img: // 设置用户信息
			setCamel(this, true);
			findViewById(R.id.camer).setVisibility(View.VISIBLE);
			break;

		case R.id.user_shooting: // 设置用户信息
			setCamel(this, false);
			findViewById(R.id.camer).setVisibility(View.VISIBLE);
			break;

		case R.id.view3_layout_head_img: // 设置用户信息
			intent = new Intent(mContext, GetBgImg.class);
			((Activity) mContext).startActivityForResult(intent, AppConfig.REQUEST_GETBGIMG);
			break;

		default:
			break;
		}
	}

	/**
	 * 获取我的视频列表信息
	 * 
	 * @author hanpengyuan
	 * 
	 */
	protected class GetData extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			String str = "";
			if (isrefresh) {
				Logger.i("    :有没有调用到！" + isrefresh);
				return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_VIDEO_MY_LIST), "1", "10");
			} else {
				Logger.i("    :有没有调用到！" + isrefresh);
				return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_VIDEO_MY_LIST), getStart(mData.size()), "10");
			}
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			mLoadBar.dismiss();
			if (null != result) {
				if ("1".equals(result.get("code"))) {
					if (isrefresh) {
						mCache.put(Constants.PERSONAL_HOME_VIDEO, result.get("data"));
					}
					ArrayList<HashMap<String, String>> list = DataRequest.getArrayListFromJSONArrayString(result.get("data"));
					if (list != null && list.size() > 0) {

						if (list.size() < 10) {
							dataResult = false;
							scrollOn = false;
						} else {
							scrollOn = true;
							dataResult = true;
						}
						if (isrefresh) {
							mData.clear();
							if (list.size() < 10) {

								scrollOn = false;
							} else {
								scrollOn = true;

							}
						}
						mData.addAll(list);
						if (mAdapter != null) {
							mAdapter.notifyDataSetChanged();
						}
					}
				}

			} else {

				if (isrefresh) {
					String personal_home_video = mCache.getAsString(Constants.PERSONAL_HOME_VIDEO);
					if (!StringUtil.isEmpty(personal_home_video)) {
						mData.clear();
						mData.addAll(DataRequest.getArrayListFromJSONArrayString(personal_home_video));
						mAdapter.notifyDataSetChanged();
					}

				}
			}

			if (isrefresh) {

				listView.stopRefresh();
				isrefresh = false;
			}

		}
	}

	/**
	 * 获取用户信息
	 * 
	 * @author hanpengyuan
	 * 
	 */
	private class GetUserData extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			String u_id = getUid();
			if ("day".equals(getMode())) {
				HashMap<String, String> result = DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_MY_INFO));
				if (result != null) {
					if ("1".equals(result.get("code"))) {
						mCache.put(Constants.USER_INIT_DATA, result.get("data"));
						return DataRequest.getHashMapFromJSONObjectString(result.get("data"));
					}
				}
			} else if ("night".equals(getMode())) {

				return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_FR_N_GET_USER_INF), u_id);
			}
			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				userData.clear();
				userData.putAll(result);
				Logger.i(userData.toString());
				setUser();
				Logger.i("用户数据： 执行到了没有？");
				new GetData().execute();

			} else {
				String user = mCache.getAsString(Constants.USER_INIT_DATA);
				if (!StringUtil.isEmpty(user)) {
					userData.clear();
					userData.putAll(DataRequest.getHashMapFromJSONObjectString(user));
					Logger.i(userData.toString());
					setUser();
					new GetData().execute();
				}
			}
			mLoadBar.dismiss();
		}
	}

	private void setViewShow(View v, int viewID) {
		v.findViewById(viewID).setVisibility(View.VISIBLE);
	}

	private void setItemViewImg(View v, int[] viewID, int[] d_img, int[] n_img) {
		int[] img;
		if ("day".equals(getMode())) {
			img = d_img;
		} else {
			img = n_img;
		}
		for (int i = 0; i < viewID.length; i++) {
			ImageView view = (ImageView) v.findViewById(viewID[i]);
			view.setImageResource(img[i]);
		}
	}

	/**
	 * 
	 * @param activity
	 * @param or
	 *            true 拍摄图片 false 视频拍摄
	 */
	public void setCamel(final Activity activity, final boolean or) {
		LinearLayout btn1 = (LinearLayout) activity.findViewById(R.id.nor);
		LinearLayout btn2 = (LinearLayout) activity.findViewById(R.id.model);
		LinearLayout btn3 = (LinearLayout) activity.findViewById(R.id.cancle);

		TextView btn_text1 = (TextView) activity.findViewById(R.id.nor_text);
		TextView btn_text2 = (TextView) activity.findViewById(R.id.model_text);
		if (or) {
			btn_text1.setText("照片拍摄");
			btn_text2.setText("图库选择");
		} else {
			btn_text1.setText("拍摄");
			btn_text2.setText("剪辑");
		}

		// uid = PreferenceUtil.getString(mContext, "user_id", "");
		// up =PreferenceUtil.getString(mContext, "user_name", "");
		OnClickListener listener = new OnClickListener() {
			Intent intent = null;

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.nor:
					if (or) {
						Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "xiaoma.jpg"));
						path = uri.getPath();
						intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
						startActivityForResult(intent2, 2);
					} else {
						// intent =
						// VideoXiaoYingIntent.initCaptureVideoIntent(MyHomePageActivity.this);//
						// startActivityForResult(intent, REQUEST_PHOTOGRAPH);
					}
					camel.setVisibility(View.GONE);
					break;
				case R.id.model:
					if (or) {
						Intent intent = new Intent(Intent.ACTION_PICK, null);
						intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
						startActivityForResult(intent, 111);
					} else {
						intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("video/*");
						Intent wrapperIntent = Intent.createChooser(intent, null);
						startActivityForResult(wrapperIntent, REQUEST_LOCAL);
					}

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
		if (requestCode == REQUEST_LOCAL && resultCode == RESULT_OK) {
			// Uri uri = data.getData();
			// Intent intent = VideoXiaoYingIntent.initVideoEditIntent(uri,
			// MyHomePageActivity.this);
			// startActivityForResult(intent, REQUEST_UP);
			return;
		}
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

		if (requestCode == REQUEST_PHOTOGRAPH && resultCode == RESULT_OK) {// 拍摄上传
		// Intent intent = new Intent(MyHomePageActivity.this,
		// UploadVideoActivity.class);
		// intent.putExtra("path",
		// VideoXiaoYingIntent.getOutputUri().getPath());
		// startActivityForResult(intent, REQUEST_UPLOAD_VIDEO);
			return;
		}

		if (requestCode == REQUEST_UP && resultCode == RESULT_OK) {
			// Intent intent = new Intent(MyHomePageActivity.this,
			// UploadVideoActivity.class);
			// intent.putExtra("path",
			// VideoXiaoYingIntent.getOutputUri2().getPath());
			// startActivityForResult(intent, REQUEST_UPLOAD_VIDEO);

			return;
		}

		if (requestCode == REQUEST_UPLOAD_VIDEO && resultCode == RESULT_OK) {
			isrefresh = true;
			Logger.i("    :有没有调用到！");
			new GetUserData().execute();
			return;
		}
		if (requestCode == 20140419 && resultCode == Activity.RESULT_OK) {// 跳转到用户设置界面返回。
			setResult(RESULT_OK);
			finish();
			return;
		}
		if (requestCode == AppConfig.REQUEST_GETBGIMG && resultCode == RESULT_OK) {// 修改用户背景图片的刷新。
			setHeadImgBG();
			return;
		}
		if (requestCode == 20140419) {
			isrefresh = true;
			return;
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
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {
			String string = DataRequest.SendFile(getUrl(AppConfig.SEND_HEAD_IMG) + "cut=2", "ufile", params[0]);
			if (!StringUtil.isEmpty(string)) {
				HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(DataRequest.getStringFrom_base64(string));
				if (map != null) {
					Logger.i("-ddd--" + map.toString());
					heString = map.get("uploadFileUrl_b");
					return DataRequest.getStringFromURL_Base64(getUrl(AppConfig.SEND_IMG_CUT_TMP) + "&uid=" + uid + "&up=" + up, heString, mark);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!StringUtil.isEmpty(result)) {
				HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(result);
				if ("1".equals(map.get("code"))) {
					ImageView head = (ImageView) listViewHead.findViewById(R.id.view3_layout_user_img);
					Logger.i(" 首页图片地址： " + heString);
					LoadBitmap.getIntence().loadImage(heString, head);
					IwoToast.makeText(mContext, "上传头像成功").show();
					PreferenceUtil.setString(mContext, "user_head", heString);
					setUserImg = true;
				}
				Logger.i("---" + result.toString());
			} else {
				IwoToast.makeText(mContext, "上传头像失败").show();
			}

			isrefresh = true;
			new GetData().execute();
			mLoadBar.dismiss();
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

	public void setHeadImgBG() {
		ImageView layout_head_img = (ImageView) listViewHead.findViewById(R.id.view3_layout_head_img);
		String i = PreferenceUtil.getString(mContext, "user_bg_img_id", "1");
		try {
			setImgSize(layout_head_img, 0, 7 / 12.0f, 1);
			layout_head_img.setImageBitmap(BitmapFactory.decodeStream(mContext.getAssets().open("wallpaper" + i + ".jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
