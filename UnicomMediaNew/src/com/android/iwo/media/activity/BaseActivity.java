package com.android.iwo.media.activity;

import java.util.HashMap;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.iwo.media.action.ActivityUtil;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.PushAction;
import com.android.iwo.media.apk.activity.R;
import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.view.IwoToast;
import com.android.iwo.users.UserLogin;
import com.test.iwomag.android.pubblico.util.ACache;
import com.test.iwomag.android.pubblico.util.AppUtil;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.NetworkUtil;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {

	protected DisplayMetrics dm;// 屏幕分辨率
	protected float scale = 0;
	protected CommonDialog mLoadBar;
	protected Context mContext;
	protected ACache mCache;// 缓存
	private LoginInterface loginInterface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCache = ACache.get(this);
		setPubParame();
		mContext = this;
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		scale = getResources().getDisplayMetrics().density;
		mLoadBar = new CommonDialog(this);
		ActivityUtil.getInstance().add(this.getClass().getSimpleName(), this);
		if (!NetworkUtil.isWIFIConnected(mContext)) {
			makeText("请检查网络");
		}
		/** 设置是否对日志信息进行加密, 默认false(不加密). */
		AnalyticsConfig.enableEncrypt(true);
		/**
		 * 发送策略定义了用户由统计分析SDK产生的数据发送回友盟服务器的频率
		 */
		MobclickAgent.updateOnlineConfig( mContext );
	}

	protected View setViewGone(int id, boolean isgone) {
		View view = findViewById(id);
		if (isgone) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
		}
		return view;
	}

	protected void setTitle(String title) {
		if (StringUtil.isEmpty(title))
			return;
		((TextView) setViewGone(R.id.title_tex, false)).setText(title);
		setTitleBGDay();
	}

	private void setTitleBGDay() {
		/*
		 * findViewById(R.id.title).setBackgroundColor(
		 * getResources().getColor(R.color.comm_green_color));
		 */
		findViewById(R.id.title).setBackgroundColor(
				getResources().getColor(R.color.comm_bg_color));
	}

	protected void setNightTitle(String title) {
		if (StringUtil.isEmpty(title))
			return;
		((TextView) setViewGone(R.id.title_tex, false)).setText(title);
		setTitleBG(getMode());
	}

	protected void setTitleBG(String mode) {
		findViewById(R.id.title).setBackgroundColor(
				getResources().getColor(R.color.comm_pink_color));
	}

	protected void setBack(OnClickListener listener) {
		if (listener != null)
			setViewGone(R.id.left_img, false).setOnClickListener(listener);
		else {
			setViewGone(R.id.left_img, false).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							finish();
						}
					});
		}
	}

	// 新添加
	protected void setBack_text(OnClickListener listener) {
		if (listener != null)
			setViewGone(R.id.left_img_1, false).setOnClickListener(listener);
		else {
			setViewGone(R.id.left_img_1, false).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							finish();
						}
					});
		}
	}

	protected void setTitleRightImg(int imgID, OnClickListener listener) {
		View v = setViewGone(R.id.right_img, false);
		v.setBackgroundResource(imgID);
		v.setOnClickListener(listener);
	}

	protected String getUid() {
		// if ("day".equals(getMode())) {
		return PreferenceUtil.getString(this, "user_id", "");
		/*
		 * } else { return PreferenceUtil.getString(this, "n_user_id", ""); }
		 */
	}

	protected String getUserTel() {
		// if ("day".equals(getMode())) {
		return PreferenceUtil.getString(this, "user_name", "");
		/*
		 * } else { return "b" + PreferenceUtil.getString(this, "user_name",
		 * ""); }
		 */
	}

	protected String getMode() {
		return PreferenceUtil.getString(this, "video_model", "day");
	}

	protected String getPre(String key) {
		return PreferenceUtil.getString(this, key, "");
	}

	protected void setImage(View v, int id, int resid) {
		if (!StringUtil.isEmpty(resid))
			BitmapUtil.setBackgroundResource((ImageView) v.findViewById(id),
					resid);
	}

	protected void setImage(int id, int resid) {
		ImageView img = (ImageView) findViewById(id);
		img.setImageResource(resid);
	}

	protected void setItem(View v, int id, String value) {
		if (!StringUtil.isEmpty(value)) {
			((TextView) v.findViewById(id)).setText(value);
		} else {
			((TextView) v.findViewById(id)).setText("0");
		}
	}

	protected void setItem(int id, String value) {
		if (!StringUtil.isEmpty(value)) {
			((TextView) findViewById(id)).setText(value);
		} else {
			((TextView) findViewById(id)).setText("");
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		setPubParame();
		Logger.v(this.getClass().getSimpleName(), this.getClass()
				.getCanonicalName() + "---onStart");
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		Logger.v(this.getClass().getSimpleName(), this.getClass()
				.getCanonicalName() + "---paused.");
	}

	@Override
	protected void onStop() {
		Logger.e(this.getClass().getSimpleName()
				+ this.getClass().getCanonicalName() + "---onStop");
		super.onStop();
	}

	protected String getStart(int size) {
		String result = "";
		if (size == 0) {
			return "1";
		} else if (size < 10 && size > 0)
			return "2";
		else if (size / 10 > 0) {
			return ((size % 10 > 0) ? (size / 10 + 2) : (size / 10 + 1)) + "";
		}
		return result;
	}

	/**
	 * 
	 * @param item
	 *            View
	 * @param del
	 *            间距
	 * @param size
	 *            高宽比
	 * @param n
	 *            一个屏幕宽度中，占有几张图片
	 * @return
	 */
	protected int setImgSize(ImageView item, int del, float size, int n) {
		int width = (dm.widthPixels - (int) (del * scale + 0.5f)) / n;
		android.view.ViewGroup.LayoutParams params = item.getLayoutParams();
		params.height = (int) (width * size);
		params.width = width;
		return params.height;
	}

	protected int setImgSize(ImageView item, int del, float size, float n) {
		int width = (int) ((dm.widthPixels - (int) (del * scale + 0.5f)) / n);
		android.view.ViewGroup.LayoutParams params = item.getLayoutParams();
		params.height = (int) (width * size);
		params.width = width;
		return params.height;

	}

	protected int setImgSize(ImageView item, int del, float size, float n,
			boolean b) {
		int width = (int) ((dm.widthPixels - del) / n);
		android.view.ViewGroup.LayoutParams params = item.getLayoutParams();
		params.height = (int) (width * size);
		params.width = width;
		return params.height;

	}

	protected void setImgSize(View item, int del, int n) {
		int width = (dm.widthPixels - (int) (del * scale + 0.5f)) / n;
		android.view.ViewGroup.LayoutParams params = item.getLayoutParams();
		params.width = (int) (width - 5 * scale);
	}

	protected void makeText(String value) {
		if (!StringUtil.isEmpty(value)) {
			IwoToast.makeText(this, value).show();
		}
	}

	protected void makeText(String value, int dru) {
		IwoToast.makeText(this, value, dru).show();
	}

	/**
	 * 根据选定模式，改变布局的背景。
	 * 
	 * @param viewID
	 *            View组件的ID
	 * 
	 */
	protected void setMode_BG(int viewID) {
		String mode = getMode();
		if ("day".equals(mode)) {
			findViewById(viewID).setBackgroundColor(
					getResources().getColor(R.color.comm_bg_color));
		} else if ("night".equals(mode)) {
			findViewById(viewID).setBackgroundColor(
					getResources().getColor(R.color.comm_bg_color));

		}
	}

	protected View setView_BG(int viewID, int imgID) {
		View v = findViewById(viewID);
		v.setBackgroundResource(imgID);
		return v;
	}

	/**
	 * 根据选定模式，改变布局的背景。
	 * 
	 * @param fatherView
	 *            父 View
	 * @param viewID
	 *            子View的 ID
	 */
	protected void setMode_BG(View fatherView, int viewID) {
		String mode = getMode();
		if ("day".equals(mode)) {
			fatherView.findViewById(viewID).setBackgroundColor(
					getResources().getColor(R.color.comm_green_color));
		} else if ("night".equals(mode)) {
			fatherView.findViewById(viewID).setBackgroundColor(
					getResources().getColor(R.color.comm_pink_color));

		}
	}

	/**
	 * 
	 * @param imageID
	 *            图片的ID
	 * @param d_pictureID
	 *            白天图片 资源的ID
	 * @param n_pictureID
	 *            夜晚图片 资源的ID
	 */
	protected void setMode_Image(int imageID, int d_pictureID, int n_pictureID) {
		String mode = getMode();
		ImageView img = (ImageView) findViewById(imageID);
		if ("day".equals(mode)) {
			img.setImageResource(d_pictureID);
		} else if ("night".equals(mode)) {
			img.setImageResource(n_pictureID);
		}
	}

	/**
	 * 
	 * @param img
	 * @param d_pictureID
	 *            白天图片 资源的ID
	 * @param n_pictureID
	 *            夜晚图片 资源的ID
	 */
	protected void setMode_Image(ImageView img, int d_pictureID, int n_pictureID) {
		String mode = getMode();
		if ("day".equals(mode)) {
			img.setImageResource(d_pictureID);
		} else if ("night".equals(mode)) {
			img.setImageResource(n_pictureID);
		}
	}

	/**
	 * @param imageID
	 *            图片的ID
	 * @param d_pictureID
	 *            白天图片 资源的ID
	 * @param n_pictureID
	 *            夜晚图片 资源的ID
	 */
	protected void setMode_Image(View fatherView, int imageID, int d_pictureID,
			int n_pictureID) {
		String mode = getMode();
		ImageView img = (ImageView) fatherView.findViewById(imageID);
		if ("day".equals(mode)) {
			img.setBackgroundResource(d_pictureID);
		} else if ("night".equals(mode)) {
			img.setBackgroundResource(n_pictureID);
		}
	}

	/**
	 * 添加公共参数
	 */
	protected void setPubParame() {
		if (StringUtil.isEmpty(AppUtil.END))
			AppUtil.setPubParame(this, "video_share", getUid(), getUserTel(),
					"");
	}

	protected String getStringNotNull(String str) {
		if (StringUtil.isEmpty(str)) {
			return "";
		} else {
			return str;
		}
	}

	/**
	 * 更新
	 */
	public void updataAddress() {
		try {
			PushAction.getInstance(this).init();
			new Thread(new Runnable() {
				public void run() {
					DataRequest
							.getStringFromURL_Base64(getUrl(AppConfig.VIDEO_LON_LAT));
				}
			}).start();
		} catch (Error e) {
			Logger.i(e.toString());
		}

	}

	/**
	 * @Title addShortcut
	 * @Description: 添加桌面快捷方式
	 * @param
	 * @return void
	 * @throws
	 */
	protected void addShortcut() {
		Logger.i("---add short cut");
		if (!StringUtil.isEmpty(getPre("shortcut")))
			return;
		PreferenceUtil.setString(this, "shortcut", "yes");
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));
		shortcut.putExtra("duplicate", false); // 不允许重复创建

		// 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
		// 注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序
		ComponentName comp = new ComponentName(this, Welcome.class);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
				Intent.ACTION_MAIN).setComponent(comp));

		// 快捷方式的图标
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
				this, R.drawable.ic_launcher);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

		sendBroadcast(shortcut);
	}

	// 判断用户登录
	protected boolean setUserLogin() {
		Logger.i("用户名" + getPre("user_name") + "密码" + getPre("user_pass"));
		if (!StringUtil.isEmpty(getPre("user_name"))) {
			if (!StringUtil.isEmpty(getPre("user_pass"))) {

				return true;
			}
		}
		if (!StringUtil.isEmpty(getPre("Umeng"))) {
			return true;
		}
		playActivityLog();
		return false;

	}

	protected void playActivityLog() {
		Intent intent = new Intent(BaseActivity.this, UserLogin.class);
		intent.putExtra("syte", "1");
		startActivity(intent);
	}

	protected String getUrl(String url) {
		return AppConfig.GetUrl(this, url);
	}

	// 区别第3方登陆和手机登陆 ture是手机登陆
	protected boolean GetLogInState() {
		return PreferenceUtil.getBoolean(mContext, "islogin", true);
	}

	// 发请求获取登录状态
	protected void IsLogin() {
		if (NetworkUtil.isWIFIConnected(mContext)) {
			new GetLoinState().execute();
		} else {
			if (loginInterface != null) {
				loginInterface.Login();
			}
		}

	}

	private class GetLoinState extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest
					.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_UN_GET_CHANNEL_ISLOIN));
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			super.onPostExecute(result);
			if (result != null && "1".equals(result.get("code"))) {
				AppConfig.ISLOIN = true;

			} else {
				AppConfig.ISLOIN = false;
			}
			if (loginInterface != null) {
				loginInterface.Login();
			}
		}

	};

	public void ChaneLoginState(LoginInterface i) {
		loginInterface = i;
	}

	public interface LoginInterface {
		public void Login();
	};

}
