package com.android.iwo.media.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iwo.media.action.ActivityUtil;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.View4_new;
import com.android.iwo.media.action.ViewMe;
import com.android.iwo.media.action.ViewMyChase;
import com.android.iwo.media.apk.activity.R;
import com.android.iwo.media.chat.XmppClient;
import com.test.iwomag.android.pubblico.util.AppUtil;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.NetworkUtil;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 白天模式
 */
public class ModelActivity extends BaseActivity {
	String path;
	private LinearLayout mPager;
	private LinearLayout tab1, tab2, tab3, tab5;
	private RelativeLayout tab4;
	private ImageView img1, img2, img4, img5;
	private TextView tex1, tex2, tex4, tex5;
	private static ViewMyChase view2;
	private View4_new view1_new;
	// private ViewSearch viewSearch;

	private int mCurrent = 0;
	private LinearLayout camel;
	private final int REQUEST_PHOTOGRAPH = 20140422;
	private final int REQUEST_LOCAL = 201404221;
	private final int REQUEST_UP = 201404222;

	private final int REQUEST_FRIENDDETAIL = 5191819;
	private final int REQUEST_UPLOADVIDEO = 5201455;

	private String syte;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceUtil.setString(mContext, "video_model", "day");
		setContentView(R.layout.activity_model);
		// Logger.setDebugMode(false);//关掉日记
		setData();
		syte = getIntent().getStringExtra("syte");
		Logger.i("密码：" + getPre("user_pass"));
		Logger.i("账号：" + getPre("user_name"));
		IsLogin();
		ChaneLoginState(new LoginInterface() {
			@Override
			public void Login() {
				if (AppConfig.ISLOIN) {
					Logger.i("登录");
				} else {
					if (GetLogInState() == false) {
						new Login_umeng().execute();
					}
					Logger.i("没登录");
				}
			}
		});

	}

	private void setData() {
		setPubParame();
		// AppUtil.setPubParame(this, "iwo_video", getUid(), getUserTel(), "");
		// new GetDownload().execute();
		init();
		if (!StringUtil.isEmpty(getIntent().getStringExtra("type"))) {
			if (view2.getmData() == null) {
				tabSelect(1);
			} else {
				tabSelect(1);
			}

		} else {
			tabSelect(0);
		}
		ActivityUtil.getInstance().deleteAll(this.getClass().getSimpleName());

		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				return false;
			}
		});
		if (!StringUtil.isEmpty(getUid()))
			new Thread(new Runnable() {
				public void run() {
					XmppClient.getInstance(mContext).login(getUserTel());
					handler.sendMessage(new Message());
				}
			}).start();
		if (NetworkUtil.isWIFIConnected(mContext)) {
			updataAddress();
		}
		AppUtil appUtil = new AppUtil();
		appUtil.checkVersion(this, getUrl(AppConfig.VIDEO_GET_VERSION), "1");
	}

	@Override
	protected void onStart() {
		super.onStart();
		PreferenceUtil.setString(mContext, "video_model", "day");
	}

	private void init() {
		mPager = (LinearLayout) findViewById(R.id.view);
		view2 = new ViewMyChase(this);
		// view1 = new View4(this);
		// 新添加
		view1_new = new View4_new(this);
		// viewme = new ViewMe(mContext);
		// viewSearch = new ViewSearch(this);
		// mPager.addView(view1.getView());
		mPager.addView(view1_new.getView());
		tab1 = (LinearLayout) findViewById(R.id.tab1);
		tab2 = (LinearLayout) findViewById(R.id.tab2);
		tab3 = (LinearLayout) findViewById(R.id.tab3);
		tab4 = (RelativeLayout) findViewById(R.id.tab4);
		tab5 = (LinearLayout) findViewById(R.id.tab5);

		img1 = (ImageView) findViewById(R.id.img1);
		img2 = (ImageView) findViewById(R.id.img2);
		img4 = (ImageView) findViewById(R.id.img4);
		img5 = (ImageView) findViewById(R.id.img5);

		tex1 = (TextView) findViewById(R.id.tex1);
		tex2 = (TextView) findViewById(R.id.tex2);
		tex4 = (TextView) findViewById(R.id.tex4);
		tex5 = (TextView) findViewById(R.id.tex5);

		camel = (LinearLayout) findViewById(R.id.camer);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {

				switch (v.getId()) {
				case R.id.tab1:

					tabSelect(0);
					break;
				case R.id.tab2:
					// 追的
					if (GetLogInState()) {
						if (!setUserLogin()) {
							return;
						}
						ArrayList<HashMap<String, String>> data = view2
								.getmData();
						Logger.i("经典收藏：" + data);
						if (data == null || data.size() == 0) {
							makeText("暂无收藏");
						}
						tabSelect(1);
					} else {
						IsLogin();
						ChaneLoginState(new LoginInterface() {

							@Override
							public void Login() {
								if (!AppConfig.ISLOIN) {
									playActivityLog();
									return;
								}
								ArrayList<HashMap<String, String>> data = view2
										.getmData();
								Logger.i("经典收藏：" + data);
								if (data == null || data.size() == 0) {
									makeText("暂无收藏");
								}
								tabSelect(1);
							}
						});
					}
					break;
				case R.id.tab3:
					camel.setVisibility(View.VISIBLE);
					break;
				// case R.id.tab4:
				// // 消息
				// setUserLogin();
				// tabSelect(2);
				// break;
				case R.id.tab5:
					// 我的
					if (GetLogInState()) {
						if (!setUserLogin()) {
							return;
						}
						startActivity(new Intent(ModelActivity.this,
								MeYeActivity_new.class));

					} else {
						IsLogin();
						ChaneLoginState(new LoginInterface() {

							@Override
							public void Login() {
								if (!AppConfig.ISLOIN) {
									playActivityLog();
									return;
								}
								startActivity(new Intent(ModelActivity.this,
										MeYeActivity_new.class));
							}
						});
					}
					break;
				case R.id.right_left_img:
					// 我的搜索
					// if (!setUserLogin()) {
					// return;
					// }

					startActivity(new Intent(mContext, SearchActivity.class));

					break;
				case R.id.right_left_img1:
					// 我的搜索
					if (GetLogInState()) {
						if (!setUserLogin()) {
							return;
						}
						startActivity(new Intent(mContext, SearchActivity.class));
					} else {
						IsLogin();
						ChaneLoginState(new LoginInterface() {

							@Override
							public void Login() {
								if (!AppConfig.ISLOIN) {
									playActivityLog();
									return;
								}
								startActivity(new Intent(mContext,
										SearchActivity.class));
							}
						});
					}

					break;
				}
			}
		};
		findViewById(R.id.right_left_img).setOnClickListener(listener);
		findViewById(R.id.right_left_img1).setOnClickListener(listener);
		tab1.setOnClickListener(listener);
		tab2.setOnClickListener(listener);
		tab3.setOnClickListener(listener);
		tab4.setOnClickListener(listener);
		tab5.setOnClickListener(listener);
		setTab3();
		/*
		 * view3.setUpDateMsg(new UpDateMsg() {
		 * 
		 * @Override public void upData(boolean up) { TextView msg_tip =
		 * (TextView) findViewById(R.id.meg_tex); if (up)
		 * msg_tip.setVisibility(View.VISIBLE); else
		 * msg_tip.setVisibility(View.GONE); } });
		 */
	}

	protected void setTab3() {
		LinearLayout btn1 = (LinearLayout) findViewById(R.id.nor);
		LinearLayout btn2 = (LinearLayout) findViewById(R.id.model);
		LinearLayout btn3 = (LinearLayout) findViewById(R.id.cancle);

		OnClickListener listener = new OnClickListener() {
			Intent intent = null;

			@Override
			public void onClick(View v) {

				// if (v.getId() == R.id.nor) {
				// intent =
				// VideoXiaoYingIntent.initCaptureVideoIntent(ModelActivity.this);
				// startActivityForResult(intent, REQUEST_PHOTOGRAPH);
				// camel.setVisibility(View.GONE);
				// } else if (v.getId() == R.id.model) {
				// intent = new Intent(Intent.ACTION_GET_CONTENT);
				// intent.setType("video/*");
				// Intent wrapperIntent = Intent.createChooser(intent, null);
				// startActivityForResult(wrapperIntent, REQUEST_LOCAL);
				// camel.setVisibility(View.GONE);
				// } else if (v.getId() == R.id.cancle) {
				// camel.setVisibility(View.GONE);
				// }
			}
		};
		btn1.setOnClickListener(listener);
		btn2.setOnClickListener(listener);
		btn3.setOnClickListener(listener);
	}

	protected void tabSelect(int n) {
		findViewById(R.id.add_layout).setVisibility(View.GONE);
		ImageView left = (ImageView) findViewById(R.id.left_img);
		ImageView right = (ImageView) findViewById(R.id.right_img);
		ImageView center = (ImageView) findViewById(R.id.title_img);
		ImageView right_left_img = (ImageView) findViewById(R.id.right_left_img);
		ImageView right_left_img1 = (ImageView) findViewById(R.id.right_left_img1);
		ImageView right_seting = (ImageView) findViewById(R.id.right_img1);
		left.setVisibility(View.GONE);
		right.setVisibility(View.GONE);
		center.setVisibility(View.GONE);
		right_left_img1.setVisibility(View.GONE);
		right_left_img.setVisibility(View.GONE);
		right_seting.setVisibility(View.GONE);
		if (n == 0) {// 首页
			center.setVisibility(View.VISIBLE);
			BitmapUtil.setImageResource(center, R.drawable.logo);
			setTab(tab1, tex1, img1, R.drawable.white01_z, true);
			setTitle("");
			right.setVisibility(View.VISIBLE);
			BitmapUtil.setImageResource(right, R.drawable.two_white_history);
			right_left_img.setVisibility(View.VISIBLE);
			BitmapUtil.setImageResource(right_left_img,
					R.drawable.two_white_search);
			findViewById(R.id.title_tex).setVisibility(View.GONE);
			findViewById(R.id.title_img1).setVisibility(View.GONE);
			// BitmapUtil.setImageResource(right_left_img, R.drawable.sousuo);
			// BitmapUtil.setImageResource(right_left_img,
			// R.drawable.ic_action_search);

			right.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// startActivityForResult(new Intent(mContext,
					// MyHomePageActivity.class), 20140211);
					if (GetLogInState()) {
						if (!setUserLogin()) {
							return;
						}
						startActivity(new Intent(mContext,
								MeRecordActivity.class));
					} else {
						IsLogin();
						ChaneLoginState(new LoginInterface() {

							@Override
							public void Login() {
								if (!AppConfig.ISLOIN) {
									playActivityLog();
									return;
								}
								startActivity(new Intent(mContext,
										MeRecordActivity.class));
							}
						});
					}

				}
			});
		} else if (n == 1) {// 收藏
			setTab(tab2, tex2, img2, R.drawable.normal02_z, true);

			findViewById(R.id.title_img1).setVisibility(View.VISIBLE);

			OnClickListener listener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (v.getId() == R.id.left_img) {
						finish();
						new Thread(new Runnable() {
							public void run() {
								XmppClient.getInstance(mContext).colseConn();
							}
						}).start();
					} else if (v.getId() == R.id.title_img) {

					} else if (v.getId() == R.id.right_img) {
						startActivity(new Intent(ModelActivity.this,
								TelAddActivity.class));
						// XmppClient.getInstance(mContext).joinXml();
					}
				}
			};
			right.setOnClickListener(listener);
			// left.setOnClickListener(listener);
		} else if (n == 2) {// 消息
			setTab(tab5, tex5, img5, R.drawable.normal05_z, true);
			right_seting.setVisibility(View.VISIBLE);
			BitmapUtil.setImageResource(right_seting, R.drawable.setting1);
			right_seting.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(ModelActivity.this,
							UserSetListActivity.class);
					startActivityForResult(intent, 911);
				}
			});
		} else if (n == 3) { // 朋友圈
			setTitle("朋友圈");
			// setTab(tab5, tex5, img5, R.drawable.white05, true);
		} else if (n == 4) {
			setTitle("搜索");
		}

		if (mCurrent == n)
			return;

		if (mCurrent == 0)
			setTab(tab1, tex1, img1, R.drawable.white01, false);
		else if (mCurrent == 1)
			setTab(tab2, tex2, img2, R.drawable.normal02, false);
		else if (mCurrent == 2)
			setTab(tab5, tex5, img5, R.drawable.normal05, false);

		mCurrent = n;
		setView(n);
	}

	private void setTab(View tab, TextView tex, ImageView img, int imgid,
			boolean sel) {

		if (sel) {
			tab.setBackgroundColor(getResources().getColor(
					R.color.comm_bg_color));
			// tex.setTextColor(getResources().getColor(R.color.comm_color));
		} else {
			tab.setBackgroundColor(getResources().getColor(
					R.color.comm_bg_color));
			// tex.setTextColor(0xffffffff);
		}

		img.setImageResource(imgid);
	}

	private void setView(int n) {
		mPager.removeAllViews();

		if (n == 0) {
			mPager.addView(view1_new.getView());// z主页
		} else if (n == 1) {
			mPager.addView(view2.getView());// 我追

		} else if (n == 2) {
			mPager.addView(viewme.getView());
		} else if (n == 3) {
			// mPager.addView(view4.getView());
		} else if (n == 4) {// 搜索界面
			// mPager.addView(viewSearch.getView());
		}
	}

	@Override
	public void onBackPressed() {
		exitBy2Click();
	}

	private boolean doubdle_exit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (doubdle_exit == false) {
			doubdle_exit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					doubdle_exit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
		} else {
			finish();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 911) {
			// viewme.setText();
			Logger.i("14515");
			return;
		}

		if (resultCode == AppConfig.RESULT_FRIENDDETAIL_SETBLACKLIST) {
			// view2.refresh();
			return;
		}
		if (requestCode == REQUEST_PHOTOGRAPH && resultCode == RESULT_OK) {// 拍摄上传
			// Intent intent = new Intent(ModelActivity.this,
			// UploadVideoActivity.class);
			// intent.putExtra("path",
			// VideoXiaoYingIntent.getOutputUri().getPath());
			// startActivityForResult(intent, REQUEST_UPLOADVIDEO);
			return;
		}

		if (requestCode == REQUEST_LOCAL && resultCode == RESULT_OK) {
			// Uri uri = data.getData();
			// Intent intent = VideoXiaoYingIntent.initVideoEditIntent(uri,
			// ModelActivity.this);
			// startActivityForResult(intent, REQUEST_UP);
			return;
		}
		if (requestCode == REQUEST_UP && resultCode == RESULT_OK) {
			// Intent intent = new Intent(ModelActivity.this,
			// UploadVideoActivity.class);
			// intent.putExtra("path",
			// VideoXiaoYingIntent.getOutputUri2().getPath());
			// startActivityForResult(intent, REQUEST_UPLOADVIDEO);
			return;
		}

		if (requestCode == 20140211 && resultCode == RESULT_OK) {
			// startActivity(new Intent(this, UserLogin.class));
			// finish();
			return;
		}
		if (requestCode == REQUEST_UPLOADVIDEO && resultCode == RESULT_OK) {
			// startActivityForResult(new Intent(mContext,
			// MyHomePageActivity.class), 20140211);
		}

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

		if (requestCode == 20140211 || requestCode == REQUEST_FRIENDDETAIL
				|| requestCode == 5231627) {
			// view4.refreshUser();
			return;
		}
	}

	public static Handler handler = new Handler(new Handler.Callback() {
		public boolean handleMessage(Message msg) {
			if (msg.what == 1) {
				// if (view3 != null)
				// view3.refresh();
			} else if (msg.what == 2) {
				// if (view2 != null)
				// view2.refresh();
				// if (view3 != null)
				// view3.refresh();
				// if (view4 != null)
				// view4.refreshUser();
			}

			return false;
		}
	});
	private ViewMe viewme;

	public static class UpDataService extends BroadcastReceiver {

		@Override
		public void onReceive(Context c, Intent intent) {
			Logger.i("数据刷新" + intent.getAction());

			Message message = new Message();
			if (intent
					.getAction()
					.equals("com.android.broadcast.receiver.media.refreshmsg.CHAT_REFRESH_SHARE")) {
				message.what = 1;
			} else if (intent
					.getAction()
					.equals("com.android.broadcast.receiver.media.refresh.CHAT_REFRESH_SHARE")) {
				message.what = 2;
			}
			handler.sendMessage(message);
		}
	}

	protected void onResume() {
		Logger.w(this.getClass().getSimpleName()
				+ this.getClass().getCanonicalName()
				+ "ModelActivity---onResume");
		super.onResume();
		// view2.refresh();

		// Intent intent = getIntent();
		// if (intent == null)
		// return;
		// Bundle bundle = intent.getExtras();
		// if (bundle != null) {
		// if ("3".equals(bundle.getString("name")))// 好友申请
		// tabSelect(2);
		// else if ("4".equals(bundle.getString("name"))) {// 删除好友
		// tabSelect(1);
		// }
		// Logger.w("onResume -- name --->" + bundle.getString("name"));
		// setIntent(null);
		// }
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);// must store the new intent unless getIntent() will
		// return the old one

		Bundle bundle = intent.getExtras();
		if (bundle != null)
			Logger.w("name --->" + bundle.getString("name"));
		Logger.w(this.getClass().getSimpleName()
				+ this.getClass().getCanonicalName()
				+ "ModelActivity---onNewIntent");
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

	// 发送第三方登陆请求
	private class Login_umeng extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected void onPreExecute() {
			mLoadBar.show();
		}

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_USER_LOGIN_UMENG), getPre("umeng_id"),
					getPre("umeng_nick"), getPre("umeng_sex"),
					getPre("umeng_head"));
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			mLoadBar.dismiss();
			if (result != null && "1".equals(result.get("code"))) {
				HashMap<String, String> day = DataRequest
						.getHashMapFromJSONObjectString(result.get("data"));
				Logger.i("三方登陆后台返回-==========>" + day.toString());
				// makeText("已登录");
				PreferenceUtil.setString(mContext, "nick_name",
						day.get("nick_name"));
				PreferenceUtil.setString(mContext, "id", day.get("id"));
				PreferenceUtil.setString(mContext, "sex", day.get("sex"));
				PreferenceUtil.setString(mContext, "head_img",
						day.get("head_img"));
				PreferenceUtil.setString(mContext, "create_time",
						day.get("create_time"));
				PreferenceUtil.setString(mContext, "user_status",
						day.get("user_status"));
				PreferenceUtil.setString(mContext, "tp_id", day.get("tp_id"));
				PreferenceUtil.setString(mContext, "bg_img", day.get("bg_img"));
				PreferenceUtil.setBoolean(mContext, "islogin", false);
				PreferenceUtil.setString(mContext, "video_model", "day");
				if ("1".equals(syte)) {
					finish();
				}

			}
			// else {
			// if (result == null)
			// makeText("登录失败");
			// }

		}
	}

}
