package com.android.iwo.media.activity;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import com.android.iwo.media.action.ActivityUtil;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.PushAction;
import com.android.iwo.media.apk.activity.R;

import com.android.iwo.media.chat.XmppClient;
import com.android.iwo.media.view.ArrayWheelAdapter;
import com.android.iwo.media.view.WheelView;
import com.test.iwomag.android.pubblico.util.AppUtil;
import com.test.iwomag.android.pubblico.util.AppUtil.Activation;
import com.test.iwomag.android.pubblico.util.AppUtil.CallBack;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;

public class UserSetListActivity extends BaseActivity implements
		OnClickListener {
	private boolean vesion_f = true;
	final UMSocialService mController_log = UMServiceFactory
			.getUMSocialService("com.umeng.login");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_set_list_activity);
		init();
	}

	private void init() {
		// setTitle("设置");
		// findViewById(R.id.comm_title_line).setVisibility(View.INVISIBLE);
		setBack(null);
		findViewById(R.id.db_set_password).setOnClickListener(this);
		findViewById(R.id.d_my_zxing).setOnClickListener(this);
		findViewById(R.id.layout_4).setOnClickListener(this);
		findViewById(R.id.layout_5).setOnClickListener(this);
		findViewById(R.id.layout_6).setOnClickListener(this);
		findViewById(R.id.user_set).setOnClickListener(this);
		findViewById(R.id.user_set_bottom).setOnClickListener(this);
		// findViewById(R.id.layout_10).setOnClickListener(this);
		findViewById(R.id.b_blacklist).setOnClickListener(this);
		findViewById(R.id.b_stealth_mode).setOnClickListener(this);
		findViewById(R.id.b_not_disturb_time).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.db_set_password:
			intent = new Intent(UserSetListActivity.this,
					newPasswordActivity.class);// 修改密码
			break;
		case R.id.d_my_zxing:
			intent = new Intent(UserSetListActivity.this, QrcodeActivity.class);// 二维码
			break;
		case R.id.layout_4: // 版本更新
			if (vesion_f) {
				vesion_f = false;
				AppUtil util = new AppUtil();
				util.checkVersion(UserSetListActivity.this,
						getUrl(AppConfig.VIDEO_GET_VERSION), new CallBack() {
							@Override
							public void back() {
								makeText("当前版本无需更新");
							}
						}, new Activation() {
							@Override
							public boolean activation() {
								vesion_f = true;
								return false;
							}
						}, "1");
			}
			break;
		case R.id.layout_10:
			intent = new Intent(UserSetListActivity.this,
					FunctionGuideActivity.class);// 功能指导
			break;
		case R.id.layout_5:
			intent = new Intent(UserSetListActivity.this,
					FeedBackActivity.class);// 意见反馈
			break;
		case R.id.layout_6:
			intent = new Intent(UserSetListActivity.this, AboutUsActivity.class);// 关于我们
			break;

		case R.id.user_set:
			Logger.i("点击了编辑资料");
			intent = new Intent(UserSetListActivity.this, UserInfoEdit.class);// 编辑资料
			break;
		case R.id.user_set_bottom:
			PreferenceUtil.setString(mContext, "user_id", "");
			PreferenceUtil.setString(mContext, "user_name", "");
			PreferenceUtil.setString(mContext, "real_name", "");
			PreferenceUtil.setString(mContext, "user_pass", "");
			PreferenceUtil.setString(mContext, "user_city_id", "");
			PreferenceUtil.setString(mContext, "user_nickname", "");
			PreferenceUtil.setString(mContext, "n_user_id", "");
			PreferenceUtil.setString(mContext, "n_real_name", "");
			PreferenceUtil.setString(mContext, "n_user_city_id", "");
			PreferenceUtil.setString(mContext, "n_user_nickname", "");
			PreferenceUtil.setString(mContext, "video_model", "");
			PreferenceUtil.setString(mContext, "user_city_id", "");
			PreferenceUtil.setString(mContext, "user_area_id", "");
			PreferenceUtil.setString(mContext, "user_area", "");
			PreferenceUtil.setString(mContext, "user_position", "");
			PreferenceUtil.setString(mContext, "user_industry_id", "");
			PreferenceUtil.setString(mContext, "user_position_id", "");
			PreferenceUtil.setString(mContext, "nightloginset", "");
			PreferenceUtil.setString(mContext, "dayloginset", "");
			PreferenceUtil.setString(mContext, "user_area_id", "");
			PreferenceUtil.setString(mContext, "area_name", "");
			PreferenceUtil.setString(mContext, "sign", "");
			PreferenceUtil.setString(mContext, "sex", "");
			PreferenceUtil.setString(mContext, "tp_id", "");
			PreferenceUtil.setString(mContext, "nick_name", "");
			PreferenceUtil.setString(mContext, "head_img", "");
			PreferenceUtil.setString(mContext, "id", "");
			PreferenceUtil.setString(mContext, "user_status", "");
			PreferenceUtil.setString(mContext, "Umeng", "");
			PreferenceUtil.setBoolean(mContext, "islogin", true);
			makeText("退出成功");
			//
			mController_log.deleteOauth(mContext, SHARE_MEDIA.SINA,
					new SocializeClientListener() {
						@Override
						public void onStart() {
						}

						@Override
						public void onComplete(int status,
								SocializeEntity entity) {
							if (status == 200) {
								PreferenceUtil.setString(mContext, "umeng_id",
										"");
								PreferenceUtil.setString(mContext,
										"umeng_nick", "");
								PreferenceUtil.setString(mContext, "umeng_sex",
										"");
								PreferenceUtil.setString(mContext,
										"umeng_head", "");
								PreferenceUtil.setString(mContext, "Umeng", "");
								Logger.i("新浪账号推出了么");
								Toast.makeText(mContext, "退出成功",
										Toast.LENGTH_SHORT).show();
							} else {
								// Toast.makeText(mContext, "注销失败",
								// Toast.LENGTH_SHORT).show();
							}
						}
					});
			mController_log.deleteOauth(mContext, SHARE_MEDIA.QQ,
					new SocializeClientListener() {
						@Override
						public void onStart() {
						}

						@Override
						public void onComplete(int status,
								SocializeEntity entity) {
							if (status == 200) {
								PreferenceUtil.setString(mContext, "umeng_id",
										"");
								PreferenceUtil.setString(mContext,
										"umeng_nick", "");
								PreferenceUtil.setString(mContext, "umeng_sex",
										"");
								PreferenceUtil.setString(mContext,
										"umeng_head", "");
								PreferenceUtil.setString(mContext, "Umeng", "");
								Logger.i("扣扣账号推出:....");
								Toast.makeText(mContext, "退出成功",
										Toast.LENGTH_SHORT).show();
							} else {
								// Toast.makeText(mContext, "注销失败",
								// Toast.LENGTH_SHORT).show();
							}
						}
					});
			mController_log.deleteOauth(mContext, SHARE_MEDIA.WEIXIN,
					new SocializeClientListener() {
						@Override
						public void onStart() {
						}

						@Override
						public void onComplete(int status,
								SocializeEntity entity) {
							if (status == 200) {
								PreferenceUtil.setString(mContext, "umeng_id",
										"");
								PreferenceUtil.setString(mContext,
										"umeng_nick", "");
								PreferenceUtil.setString(mContext, "umeng_sex",
										"");
								PreferenceUtil.setString(mContext,
										"umeng_head", "");
								PreferenceUtil.setString(mContext, "Umeng", "");
								Toast.makeText(mContext, "退出成功",
										Toast.LENGTH_SHORT).show();
							} else {
								// Toast.makeText(mContext, "注销失败",
								// Toast.LENGTH_SHORT).show();
							}
						}
					});
			mCache.clear();
			AppUtil.END = "";
			ActivityUtil.getInstance().deleteAll(
					this.getClass().getSimpleName());// 退出黑屏,此处替换了deleteAll
			// ActivityUtil.getInstance().deleteAll();
			startActivity(new Intent(mContext, ModelActivity.class));

			new Thread(new Runnable() {
				public void run() {
					XmppClient.getInstance(mContext).colseConn();
					// XmppNightClient.getInstance(mContext).colseConn();
				}
			}).start();
			NotificationManager notificationManager = (NotificationManager) this
					.getSystemService(NOTIFICATION_SERVICE);
			notificationManager.cancel(10010);
			PushAction.getInstance(mContext).stop();
			break;
		case R.id.b_blacklist:// 黑名单
			// intent = new Intent(UserSetListActivity.this,
			// BlacklistActivity.class);
			break;
		case R.id.b_stealth_mode:// 隐身模式
			intent = new Intent(UserSetListActivity.this, StealthActivity.class);
			break;
		case R.id.b_not_disturb_time:// 勿打扰时间
			// intent = new Intent(UserSetListActivity.this,
			// UserInfoEdit.class);
			showMyDialog().show();
			break;
		default:
			break;
		}
		if (intent != null) {
			startActivity(intent);
			if (v.getId() == R.id.user_set_bottom) {
				finish();
			}
		}

	}

	private Dialog showMyDialog() {
		final Dialog dialog = new Dialog(this, R.style.myDialogTheme);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.BOTTOM);
		View view = View
				.inflate(this, R.layout.view_user_set_time_dialog, null);
		final String[] hour = getResources().getStringArray(
				R.array.clockadd_hour_array);
		final WheelView clockadd_hour_l = (WheelView) view
				.findViewById(R.id.clockadd_hour_l);
		final WheelView clockadd_hour_r = (WheelView) view
				.findViewById(R.id.clockadd_hour_l);// 这个有问题
		clockadd_hour_l.setAdapter(new ArrayWheelAdapter<String>(hour));
		clockadd_hour_l.setVisibleItems(3);
		clockadd_hour_l.setCyclic(true);
		clockadd_hour_l.setCurrentItem(0);
		clockadd_hour_r.setAdapter(new ArrayWheelAdapter<String>(hour));
		clockadd_hour_r.setVisibleItems(3);
		clockadd_hour_r.setCyclic(true);
		clockadd_hour_r.setCurrentItem(0);
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

					String lString = hour[clockadd_hour_l.getCurrentItem()];
					String rString = hour[clockadd_hour_r.getCurrentItem()];
					Logger.i(lString + " ------   " + rString);
					// PreferenceUtil.setString(UserSetListActivity.this,
					// "onlytime",);
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
