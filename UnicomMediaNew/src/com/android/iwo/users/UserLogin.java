package com.android.iwo.users;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.activity.BaseActivity;
import com.android.iwo.media.activity.ModelActivity;
import com.android.iwo.media.apk.activity.R;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/*
 * 传入 “syte”=“1” 登录成功回到原界面
 * */
public class UserLogin extends BaseActivity {

	private EditText mEditPass;
	private EditText mTel;
	private final int REQUEST_USER_LOGIN_STEP = 20140419;
	final UMSocialService mController_log = UMServiceFactory
			.getUMSocialService("com.umeng.login");
	String appID = "wx7369dbf242da5ae3";
	String appSecret = "e7fe1c897e07e68118b264ddab35ebf4";
	String tp_id, nick_name, sex, head_img;// 三方登陆字符串

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_activity_login);
		init();
	}

	private void init() {
		syte = getIntent().getStringExtra("syte");
		Logger.i("传入登录的标志" + syte);
		// setBack(null);
		findViewById(R.id.sina_log).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mController_log.getConfig().setSsoHandler(new SinaSsoHandler());// sso免登陆
				// TODO Auto-generated method stub
				mController_log.doOauthVerify(UserLogin.this, SHARE_MEDIA.SINA,
						new UMAuthListener() {
							@Override
							public void onError(SocializeException e,
									SHARE_MEDIA platform) {
							}

							@Override
							public void onComplete(Bundle value,
									SHARE_MEDIA platform) {
								if (value != null
										&& !TextUtils.isEmpty(value
												.getString("uid"))) {

									Logger.i("新浪value:  " + value);
									Toast.makeText(UserLogin.this, "授权成功",
											Toast.LENGTH_SHORT).show();
									// 获取用户资料
									mController_log.getPlatformInfo(
											UserLogin.this, SHARE_MEDIA.SINA,
											new UMDataListener() {
												@Override
												public void onStart() {
													Toast.makeText(
															UserLogin.this,
															"获取平台数据开始...",
															Toast.LENGTH_SHORT)
															.show();
												}

												@Override
												public void onComplete(
														int status,
														Map<String, Object> info) {
													if (status == 200
															&& info != null) {
														Log.i("info=====sina=>",
																info.toString());
														StringBuilder sb = new StringBuilder();
														Set<String> keys = info
																.keySet();
														for (String key : keys) {
															sb.append(key
																	+ "="
																	+ info.get(
																			key)
																			.toString()
																	+ "\r\n");
														}
														Log.d("TestData",
																sb.toString());
														tp_id = info.get("uid")
																.toString();

														nick_name = info.get(
																"screen_name")
																.toString();

														sex = info
																.get("gender")
																.toString();

														head_img = info
																.get("profile_image_url")
																.toString();
														PreferenceUtil
																.setString(
																		mContext,
																		"umeng_id",
																		tp_id);
														PreferenceUtil
																.setString(
																		mContext,
																		"umeng_nick",
																		nick_name);
														PreferenceUtil
																.setString(
																		mContext,
																		"umeng_sex",
																		sex);
														PreferenceUtil
																.setString(
																		mContext,
																		"umeng_head",
																		head_img);
														PreferenceUtil
																.setString(
																		mContext,
																		"Umeng",
																		"来自新浪微博");
														new Login_umeng()
																.execute();// 三方登陆请求
													} else {
														Log.d("TestData",
																"发生错误："
																		+ status);
													}

												}

											});
								} else {
									Toast.makeText(UserLogin.this, "授权失败",
											Toast.LENGTH_SHORT).show();
								}
							}

							@Override
							public void onCancel(SHARE_MEDIA platform) {
							}

							@Override
							public void onStart(SHARE_MEDIA platform) {
							}
						});

			}
		});
		findViewById(R.id.qq_log).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
				UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(
						UserLogin.this, "1101113173", "z7nhs45Ju3pdJRRD");
				qqSsoHandler.addToSocialSDK();
				mController_log.doOauthVerify(mContext, SHARE_MEDIA.QQ,
						new UMAuthListener() {
							@Override
							public void onStart(SHARE_MEDIA platform) {
								Toast.makeText(mContext, "授权开始",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onError(SocializeException e,
									SHARE_MEDIA platform) {
								Toast.makeText(mContext, "授权错误",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onComplete(Bundle value,
									SHARE_MEDIA platform) {
								if (value != null
										&& !TextUtils.isEmpty(value
												.getString("uid"))) {
									tp_id = value.getString("uid");
									Logger.i("扣扣value----" + value);
								}

								Toast.makeText(mContext, "授权完成",
										Toast.LENGTH_SHORT).show();

								// 获取相关授权信息
								mController_log.getPlatformInfo(mContext,
										SHARE_MEDIA.QQ, new UMDataListener() {
											@Override
											public void onStart() {
												Toast.makeText(mContext,
														"获取平台数据开始...",
														Toast.LENGTH_SHORT)
														.show();
											}

											@Override
											public void onComplete(int status,
													Map<String, Object> info) {
												if (status == 200
														&& info != null) {
													Log.i("info===qq==>",
															info.toString());
													StringBuilder sb = new StringBuilder();
													Set<String> keys = info
															.keySet();
													for (String key : keys) {
														sb.append(key
																+ "="
																+ info.get(key)
																		.toString()
																+ "\r\n");
													}
													Log.d("TestData",
															sb.toString());
													nick_name = info.get(
															"screen_name")
															.toString();
													sex = info.get("gender")
															.toString();
													head_img = info
															.get("profile_image_url")
															.toString();
													PreferenceUtil.setString(
															mContext,
															"umeng_id", tp_id);
													PreferenceUtil.setString(
															mContext,
															"umeng_nick",
															nick_name);
													PreferenceUtil.setString(
															mContext,
															"umeng_sex", sex);
													PreferenceUtil.setString(
															mContext,
															"umeng_head",
															head_img);
													PreferenceUtil.setString(
															mContext, "Umeng",
															"来自QQ");
													new Login_umeng().execute();
												} else {
													Log.d("TestData", "发生错误："
															+ status);
												}
											}
										});
							}

							@Override
							public void onCancel(SHARE_MEDIA platform) {
								Toast.makeText(mContext, "授权取消",
										Toast.LENGTH_SHORT).show();
							}
						});
			}
		});
		findViewById(R.id.wei_log).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 添加微信平台
				UMWXHandler wxHandler = new UMWXHandler(mContext, appID,
						appSecret);
				wxHandler.addToSocialSDK();
				mController_log.doOauthVerify(mContext, SHARE_MEDIA.WEIXIN,
						new UMAuthListener() {
							@Override
							public void onStart(SHARE_MEDIA platform) {
								Toast.makeText(mContext, "授权开始",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onError(SocializeException e,
									SHARE_MEDIA platform) {
								Toast.makeText(mContext, "授权错误",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onComplete(Bundle value,
									SHARE_MEDIA platform) {
								if (value != null
										&& !TextUtils.isEmpty(value
												.getString("uid"))) {
									tp_id = value.getString("uid");
									Logger.i("微信value:  " + value);
								}
								Toast.makeText(mContext, "授权完成",
										Toast.LENGTH_SHORT).show();
								// 获取相关授权信息
								mController_log.getPlatformInfo(mContext,
										SHARE_MEDIA.WEIXIN,
										new UMDataListener() {
											@Override
											public void onStart() {
												Toast.makeText(mContext,
														"获取平台数据开始...",
														Toast.LENGTH_SHORT)
														.show();
											}

											@Override
											public void onComplete(int status,
													Map<String, Object> info) {
												if (status == 200
														&& info != null) {
													Log.i("weixin===info=>",
															info + "");
													StringBuilder sb = new StringBuilder();
													Set<String> keys = info
															.keySet();
													for (String key : keys) {
														sb.append(key
																+ "="
																+ info.get(key)
																		.toString()
																+ "\r\n");
													}
													nick_name = info.get(
															"nickname")
															.toString();
													sex = info.get("sex")
															.toString();
													head_img = info.get(
															"headimgurl")
															.toString();
													PreferenceUtil.setString(
															mContext,
															"umeng_id", tp_id);
													PreferenceUtil.setString(
															mContext,
															"umeng_nick",
															nick_name);
													PreferenceUtil.setString(
															mContext,
															"umeng_sex", sex);
													PreferenceUtil.setString(
															mContext,
															"umeng_head",
															head_img);
													PreferenceUtil.setString(
															mContext, "Umeng",
															"来自微信");
													new Login_umeng().execute();
													Log.d("TestData",
															sb.toString());
												} else {
													Log.d("TestData", "发生错误："
															+ status);
												}
											}
										});
							}

							@Override
							public void onCancel(SHARE_MEDIA platform) {
								Toast.makeText(mContext, "授权取消",
										Toast.LENGTH_SHORT).show();
							}
						});

			}
		});
		findViewById(R.id.islog_news_fanhui).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();

					}
				});
		initView();

		mLoadBar.setMessage("正在验证信息...");

		// String headString = getPre("head_img");
		// ImageView headView = (ImageView) findViewById(R.id.head);
		// if (StringUtil.isEmpty(headString)) {
		// headView.setImageResource(R.drawable.logo_list);
		// } else {
		// LoadBitmap.getIntence().loadImage(headString, headView);
		// }

		// XmppClient.getInstance(this, false);
	}

	private void initView() {
		mTel = (EditText) findViewById(R.id.tel_num);
		mEditPass = (EditText) findViewById(R.id.password);
		if (!StringUtil.isEmpty(getPre("user_name"))) {
			mTel.setText(getPre("user_name"));
		}

		findViewById(R.id.login).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isRightPutIn()) {
					new Login().execute();
				}
			}
		});

		findViewById(R.id.find_pass).setOnClickListener(listener);
		findViewById(R.id.regist1).setOnClickListener(listener);
		findViewById(R.id.change_user).setOnClickListener(listener);
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = null;

			switch (v.getId()) {
			case R.id.regist1:
				intent = new Intent(mContext, RegistActivity.class);
				break;
			case R.id.find_pass:
				intent = new Intent(mContext, FindPassWord.class);
				break;
			case R.id.change_user:
				intent = new Intent(mContext, UserLoginStep1.class);
				intent.putExtra("user_login", "yes");
				startActivityForResult(intent, REQUEST_USER_LOGIN_STEP);
				return;
			default:
				break;
			}
			if (intent != null)
				startActivity(intent);
		}
	};
	private String syte;

	private boolean isRightPutIn() {
		if (StringUtil.isEmpty(mTel.getText().toString())) {
			makeText("手机号码/邮箱地址不能为空");
			return false;
		} else if (!StringUtil.isPhone(mTel.getText().toString())
				& !StringUtil.isEmail(mTel.getText().toString())) {
			makeText("请输入正确的手机号码或邮箱地址");
			return false;
		} else if (StringUtil.isEmpty(mEditPass.getText().toString())) {
			makeText("密码不能为空");
			return false;
		} else if (!StringUtil.isPassWord(mEditPass.getText().toString())) {
			makeText("密码为6－16位英文或数字");
			return false;
		}
		return true;
	}

	// 登录完成
	private class Login extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected void onPreExecute() {
			mLoadBar.show();
		}

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_USER_LOGIN),
					mTel.getText().toString(), mEditPass.getText().toString()
							+ "");
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			mLoadBar.dismiss();
			if (result != null && "1".equals(result.get("code"))) {
				HashMap<String, String> day = DataRequest
						.getHashMapFromJSONObjectString(result.get("data"));
				Logger.i("手机登陆返回结果===>" + result);
				makeText("登录成功");
				PreferenceUtil.setString(mContext, "user_id", day.get("id"));
				PreferenceUtil.setString(mContext, "user_name",
						day.get("user_name"));
				PreferenceUtil.setString(mContext, "user_name_ti",
						day.get("user_name"));
				PreferenceUtil.setString(mContext, "user_pass",
						day.get("user_pass"));
				PreferenceUtil.setString(mContext, "nick_name",
						day.get("nick_name"));
				PreferenceUtil.setString(mContext, "user_status",
						day.get("user_status"));
				PreferenceUtil.setString(mContext, "create_time",
						day.get("create_time"));
				PreferenceUtil.setString(mContext, "sex", day.get("sex"));
				PreferenceUtil.setString(mContext, "head_img",
						day.get("head_img"));
				PreferenceUtil.setBoolean(mContext, "islogin", true);
				Intent intent = null;
				if ("1".equals(syte)) {
					finish();
				} else {
					PreferenceUtil.setString(mContext, "video_model", "day");
					intent = new Intent(mContext, ModelActivity.class);
					intent.putExtra("user_pass", result.get("user_pass"));
				}
				if (intent != null) {
					startActivity(intent);
					finish();
				}

			} else {
				if (result == null)
					makeText("登录失败");
				else {
					makeText("密码错误，请重新输入");
				}
			}

		}
	}

	// 登录完成-第三方
	private class Login_umeng extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected void onPreExecute() {
			mLoadBar.show();
		}

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_USER_LOGIN_UMENG), tp_id, nick_name,
					sex, head_img);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			mLoadBar.dismiss();
			if (result != null && "1".equals(result.get("code"))) {
				HashMap<String, String> day = DataRequest
						.getHashMapFromJSONObjectString(result.get("data"));
				Logger.i("三方登陆后台返回-==========>" + day.toString());
				makeText("登录成功");
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

				Intent intent = null;
				PreferenceUtil.setString(mContext, "video_model", "day");
				if ("1".equals(syte)) {
					finish();
				} else {
					intent = new Intent(mContext, ModelActivity.class);
					if (intent != null) {
						finish();
						startActivity(intent);
					}
				}

			} else {
				if (result == null)
					makeText("登录失败");
			}

		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}

	private void setInfor(HashMap<String, String> day,
			HashMap<String, String> night) {
		boolean day_b = true, night_n = false;

		if (day == null)
			day_b = false;
		else {
			if (StringUtil.isEmpty(day.get("head_img")))
				day_b = false;
			if (StringUtil.isEmpty(day.get("nick_name")))
				day_b = false;
			if (StringUtil.isEmpty(day.get("birthday")))
				day_b = false;
			if (StringUtil.isEmpty(day.get("sex")))
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
		}

		if (night_n)
			PreferenceUtil.setString(mContext, "nightloginset", "yes");
		else {
			PreferenceUtil.setString(mContext, "nightoginset", "");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController_log.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

		if (requestCode == REQUEST_USER_LOGIN_STEP
				&& resultCode == Activity.RESULT_OK) {// 跳转到用户设置界面返回。

			mTel.setText(getPre("user_name"));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
