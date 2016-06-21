package com.android.iwo.users;

import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.activity.AdWebActivity;
import com.android.iwo.media.activity.BaseActivity;
import com.android.iwo.media.activity.ModelActivity;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.AppUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;
import com.test.iwomag.android.pubblico.util.TextUtil;

public class RegistActivity extends BaseActivity {
	private EditText mEditTel;
	// private boolean isRead = false;
	private TextView ok;
	private final int REQUESTCODE_ADWEBACTIVITY = 5151649;
	private EditText mEdit_name;
	private EditText mEdit_nick;
	private EditText mEdit_pass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_activity_register);
		init();
	}

	private void init() {
		initView();
		setBack(null);
		// setTitle("注册");
		setDialog();
		mLoadBar.setMessage("正在提交数据...");
	}

	private void initView() {
		mEditTel = (EditText) findViewById(R.id.tel_reg);// 用户名
		mEdit_nick = (EditText) findViewById(R.id.reg_nick);// 昵称
		ok = (TextView) findViewById(R.id.ok);
		mEdit_name = (EditText) findViewById(R.id.tel_name_regg);// 密码
		mEdit_pass = (EditText) findViewById(R.id.tel_pass_regg);// 确认密码
		ok.setOnClickListener(new OnClickListener() {// 注册按钮

			@Override
			public void onClick(View v) {

				if (isRightPutIn()) {
					// if (isRead) {
					new Regist().execute();
					// } else {
					// makeText("请同意《用户协议及隐私政策》");
					// }

				}
			}
		});
	}

	private boolean isRightPutIn() {
		String tel = mEditTel.getText().toString();
		String na = mEdit_name.getText().toString();
		String pass = mEdit_pass.getText().toString();
		boolean ok = true;
		String msg = "";
		if (StringUtil.isEmpty(tel)) {
			msg = "手机号码/邮箱地址不能为空";
			ok = false;
		} else if (!StringUtil.isPhone(tel) & !StringUtil.isEmail(tel)) {
			msg = "请输入正确的手机号码或邮箱地址";
			ok = false;
		} else if (StringUtil.isEmpty(na)) {
			msg = "密码不能为空";

			ok = false;
		} else if (StringUtil.isEmpty(pass)) {
			msg = "确认密码不能为空";
			ok = false;
		} else if (!StringUtil.isPassWord(pass)) {
			msg = "密码为6-16位英文或数字";
			ok = false;
		} else if (!na.equals(pass)) {
			msg = "两次密码输入不一致";
			ok = false;
		}
		if (!StringUtil.isEmpty(msg))
			makeText(msg);

		return ok;
	}

	private void setDialog() {
		final LinearLayout layout = (LinearLayout) findViewById(R.id.tip);
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.login:
					finish();
					break;
				case R.id.find:
					Intent intent = new Intent(RegistActivity.this,
							FindPassWord.class);
					startActivity(intent);
					break;
				case R.id.cancle:
					layout.setVisibility(View.GONE);
					break;
				// case R.id.select_img_layout:
				// ImageView select_img = (ImageView)
				// findViewById(R.id.select_img);
				// if (isRead) {
				// isRead = false;
				// //
				// ok.setBackgroundColor(getResources().getColor(R.color.comm_bg_color));
				// select_img.setBackgroundResource(R.drawable.share_ico_select);
				// } else {
				// isRead = true;
				// //
				// ok.setBackgroundResource(R.drawable.btn_blue_rect_selector);
				// select_img.setBackgroundResource(R.drawable.share_ico_selecton1);
				// }
				// break;
				// case R.id.text_read:
				// Intent intent2 = new Intent(mContext, AdWebActivity.class);
				// intent2.putExtra("url",
				// "http://wap.iwo.mokacn.com/v/treaty");
				// intent2.putExtra("title", "《用户协议及隐私政策》");
				// // mContext.startActivity(intent2);
				// startActivityForResult(intent2, REQUESTCODE_ADWEBACTIVITY);
				// break;
				default:
					break;
				}
			}
		};

		findViewById(R.id.login).setOnClickListener(listener);
		findViewById(R.id.cancle).setOnClickListener(listener);
		findViewById(R.id.find).setOnClickListener(listener);
		// findViewById(R.id.select_img_layout).setOnClickListener(listener);
		// TextView text_read = (TextView) findViewById(R.id.text_read);
		// text_read.setOnClickListener(listener);
	}

	private class Regist extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected void onPreExecute() {
			mLoadBar.show();
		}

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_USER_REGIN_OK), mEditTel.getText()
							.toString(), mEdit_name.getText().toString(),
					mEdit_nick.getText().toString());
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			Logger.i("注册返回的结果" + result.get("code"));
			if (result != null) {
				Logger.i("reg----result====>" + result);
				if ("1".equals(result.get("code"))) {
					Logger.i("注册成功");
					makeText("注册成功");
					HashMap<String, String> reg_data = DataRequest
							.getHashMapFromJSONObjectString(result.get("data"));

					PreferenceUtil.setString(mContext, "create_time",
							reg_data.get("create_time"));
					PreferenceUtil.setString(mContext, "user_pass",
							reg_data.get("user_pass"));
					PreferenceUtil.setString(mContext, "nick_name",
							reg_data.get("nick_name"));
					PreferenceUtil
							.setString(mContext, "id", reg_data.get("id"));
					PreferenceUtil.setString(mContext, "user_name",
							reg_data.get("user_name"));
					PreferenceUtil.setString(mContext, "user_name_ti",
							reg_data.get("user_name"));
					PreferenceUtil.setString(mContext, "user_status",
							reg_data.get("user_status"));

					Intent intent = new Intent(RegistActivity.this,
							ModelActivity.class);
					intent.putExtra("tel", mEditTel.getText().toString());
					intent.putExtra("name", mEdit_name.getText().toString());
					intent.putExtra("pass", mEdit_pass.getText().toString());
					startActivityForResult(intent, 10001);
				} else if ("0".equals(result.get("code"))) {
					LinearLayout dialog = (LinearLayout) findViewById(R.id.tip);
					dialog.setVisibility(View.VISIBLE);
				} else {
					makeText(result.get("msg"));
				}
			}
			mLoadBar.dismiss();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 10001 && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		} else if (requestCode == 100 && resultCode == RESULT_OK) {
			// ImageView select_img = (ImageView) findViewById(R.id.select_img);
			// isRead = true;
			// select_img.setBackgroundResource(R.drawable.share_ico_selecton1);
			ok.setBackgroundResource(R.drawable.btn_blue_rect_selector);
		} else if (requestCode == REQUESTCODE_ADWEBACTIVITY) {
			// ImageView select_img = (ImageView) findViewById(R.id.select_img);
			// isRead = true;
			ok.setBackgroundResource(R.drawable.btn_blue_rect_selector);
			// select_img.setBackgroundResource(R.drawable.share_ico_selecton1);
		}
	}

}
