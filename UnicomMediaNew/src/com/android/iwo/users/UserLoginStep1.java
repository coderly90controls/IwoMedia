package com.android.iwo.users;

import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.activity.BaseActivity;
import com.android.iwo.media.activity.ModelActivity;

import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 登录界面
 * 
 * @author ZhangZhanZhong
 * 
 */
public class UserLoginStep1 extends BaseActivity implements OnClickListener {

	private EditText mTel;
	private String user_login;
	private EditText mPas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_step1);
		user_login = getIntent().getStringExtra("user_login");
		init();
	}

	private void init() {
		setBack(null);
		TextView title = (TextView) setViewGone(R.id.title_tex, false);
		title.setText("登录");
		findViewById(R.id.title).setBackgroundColor(getResources().getColor(R.color.comm_bg_color));
		findViewById(R.id.ok).setOnClickListener(this);
		findViewById(R.id.cancle).setOnClickListener(this);
		findViewById(R.id.next).setOnClickListener(this);
		mTel = (EditText) findViewById(R.id.tel);
		mPas = (EditText) findViewById(R.id.pas);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok:
			new GetCode().execute();
			findViewById(R.id.dialog_layout).setVisibility(View.GONE);
			break;
		case R.id.cancle:
			findViewById(R.id.dialog_layout).setVisibility(View.GONE);
			break;
		case R.id.next:
			if (tel()) {
				new GetData().execute();
			}
			break;
		}
	}

	/**
	 * 手机号尚未注册 弹出的窗口
	 */
	private void setDialog() {
		findViewById(R.id.dialog_layout).setVisibility(View.VISIBLE);
		TextView textView = (TextView) findViewById(R.id.tex_tel);
		textView.setText(mTel.getText().toString());
	}

	private boolean tel() {
		if (StringUtil.isEmpty(mTel.getText().toString())) {
			makeText("手机号不能为空");
			return false;
		} else if (!StringUtil.isPhone(mTel.getText().toString())) {
			makeText("手机号不正确，请重新输入");
			return false;
		} else if (StringUtil.isEmpty(mPas.getText().toString())) {
			makeText("密码不能为空，请重新输入");
			return false;
		}
		return true;
	}

	private class GetData extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected void onPreExecute() {
			mLoadBar.setMessage("验证信息");
			mLoadBar.show();
		}

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_USER_LOGIN_STEP1), mTel.getText().toString(), mPas.getText().toString());
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {

			mLoadBar.dismiss();
			if (result != null) {
				String code = result.get("code");
				Intent intent = null;
				Logger.i("登录的返回：" + result);
				if ("1".equals(code)) {
					// if (StringUtil.isEmpty(user_login)) {
					// intent = new Intent(UserLoginStep1.this,
					// UserLogin.class);
					HashMap<String, String> day = DataRequest.getHashMapFromJSONObjectString(result.get("data"));
					intent = new Intent(UserLoginStep1.this, ModelActivity.class);
					
					
					
				/*	PreferenceUtil.setString(mContext, "user_name", mTel.getText().toString());
					PreferenceUtil.setString(mContext, "user_pass", mPas.getText().toString());
					PreferenceUtil.setString(mContext, "user_head", result.get("data"));

					PreferenceUtil.setString(mContext, "user_name", day.get("user_name"));
					PreferenceUtil.setString(mContext, "user_id", day.get("id"));
					PreferenceUtil.setString(mContext, "real_name", day.get("real_name"));
					PreferenceUtil.setString(mContext, "user_pass", day.get("user_pass"));
					PreferenceUtil.setString(mContext, "of_pass", day.get("of_pass"));
					PreferenceUtil.setString(mContext, "user_head", day.get("head_img"));
					PreferenceUtil.setString(mContext, "user_city_id", day.get("city_id"));
					PreferenceUtil.setString(mContext, "user_nickname", day.get("nick_name"));
					PreferenceUtil.setString(mContext, "user_bg_img_id", day.get("bg_img"));
					PreferenceUtil.setString(mContext, "user_area_id", day.get("area_id"));
					PreferenceUtil.setString(mContext, "area_name", day.get("area_name"));

					PreferenceUtil.setString(mContext, "user_industry_id", day.get("area_id"));
					PreferenceUtil.setString(mContext, "user_position_id", day.get("area_name"));
					PreferenceUtil.setString(mContext, "user_area", day.get("user_position"));

					PreferenceUtil.setString(mContext, "user_area_id", day.get("area_id"));

					PreferenceUtil.setString(mContext, "user_position", day.get("user_job"));
					PreferenceUtil.setString(mContext, "user_industry", day.get("user_trade"));*/
					makeText("登录成功");
					PreferenceUtil.setString(mContext, "user_id", day.get("id"));
					PreferenceUtil.setString(mContext, "of_pass", day.get("user_pass"));
					PreferenceUtil.setString(mContext, "id", day.get("id"));
					PreferenceUtil.setString(mContext, "user_name", day.get("user_name"));
					PreferenceUtil.setString(mContext, "user_pass", day.get("user_pass"));
					PreferenceUtil.setString(mContext, "nick_name", day.get("nick_name"));
					PreferenceUtil.setString(mContext, "user_status", day.get("user_status"));
					PreferenceUtil.setString(mContext, "create_time", day.get("create_time"));
					PreferenceUtil.setString(mContext, "sex", result.get("sex"));
					PreferenceUtil.setString(mContext, "age", result.get("age"));
					PreferenceUtil.setString(mContext, "head_img", day.get("head_img"));

					intent.putExtra("user_pass", mPas.getText().toString());
					intent.putExtra("login", "yes");
					startActivity(intent);
					finish();
					// }
					// else {
					// PreferenceUtil.setString(mContext, "user_name",
					// mTel.getText().toString());
					// PreferenceUtil.setString(mContext, "user_head",
					// result.get("data"));
					// setResult(RESULT_OK);
					// finish();
					// }

				} else if ("0".equals(code)) {
					setDialog();
				} else if ("-1".equals(code)) {
					makeText("密码不正确，请重新输入！");
				} else if ("-2".equals(code)) {
					makeText("你的号码还没注册！");
				}
			} else {
				makeText("请检测网络连接···");
			}
			mLoadBar.dismiss();
		}
	}

	/**
	 * 发送短信验证码
	 * 
	 * @author Weiguixiang
	 * 
	 */
	private class GetCode extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected void onPreExecute() {
			mLoadBar.setMessage("发送验证码...");
			mLoadBar.show();
		}

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_USER_LOGIN_CODE), mTel.getText().toString());
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					Intent intent = new Intent(UserLoginStep1.this, ActivationCodeActivity.class);
					intent.putExtra("tel", mTel.getText().toString());
					startActivityForResult(intent, 10001);
				} else {
					makeText(result.get("msg"));
				}

			} else {
				makeText("验证失败，请重新输入");
			}
			mLoadBar.dismiss();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 10001 && resultCode == RESULT_OK) {
			if (!StringUtil.isEmpty(getIntent().getStringExtra("from"))) {
				setResult(RESULT_OK);
				finish();
			} else {
					PreferenceUtil.setString(this, "video_model", "day");
					Intent intent = new Intent(this, ModelActivity.class);
					startActivity(intent);
					finish();
				}
			}

		
	}
}
