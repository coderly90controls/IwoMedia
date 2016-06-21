package com.android.iwo.media.activity;

import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.R;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class newPasswordActivity extends BaseActivity {
	private EditText edit1, edit2, edit3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_password);
		// setTitle("修改密码");
		setBack(null);
		initView();
		Logger.i("我的密码" + PreferenceUtil.getString(mContext, "user_pass", ""));
	}

	private void initView() {
		Button present = (Button) findViewById(R.id.regist);
		edit1 = (EditText) findViewById(R.id.tel_num);
		edit2 = (EditText) findViewById(R.id.password);
		edit3 = (EditText) findViewById(R.id.new_password);
		setMode(getMode());
		present.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isRightPutIn()) {
					new Regist().execute();
				}
			}
		});
	}

	private void setMode(String mode) {
		if ("day".equals(mode)) {
			findViewById(R.id.regist).setBackgroundColor(
					getResources().getColor(R.color.comm_color_chense));
		} else if ("night".equals(mode)) {
			findViewById(R.id.regist).setBackgroundColor(
					getResources().getColor(R.color.comm_color_chense));
		}
	}

	private boolean isRightPutIn() {
		String pass_old = edit1.getText().toString();
		String pass_new = edit2.getText().toString();
		String pass_newAgain = edit3.getText().toString();
		if (StringUtil.isEmpty(pass_old)) {
			makeText("当前密码不能为空");
			return false;
		} else if (!StringUtil.isPassWord(pass_old)) {
			makeText("当前密码为6－16位英文或数字");
		} else if (!pass_old.equals(getPre("user_pass"))) {
			makeText("当前密码输入错误，请重新输入");
			return false;
		} else if (StringUtil.isEmpty(pass_new)) {
			makeText("新密码不能为空");
			return false;
		} else if (!StringUtil.isPassWord(pass_new)) {
			makeText("新密码为6-16位英文或数字");
			return false;
		} else if (pass_new.equals(pass_old)) {
			makeText("新密码和当前密码一致，请重新输入");
			return false;
		} else if (StringUtil.isEmpty(pass_newAgain)) {
			makeText("确认密码不能为空");
			return false;
		} else if (!StringUtil.isPassWord(pass_newAgain)) {
			makeText("确认密码为6－16位英文或数字");
			return false;
		} else if (!pass_new.equals(pass_newAgain)) {
			makeText("确认密码与新密码不一致，请重新输入");
			return false;
		}
		return true;
	}

	private class Regist extends AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			String tel = getUserTel();
			if (StringUtil.isEmpty(tel)) {
				makeText("请检测您是否登录成功");
			}
			if ("day".equals(getMode())) {
				return DataRequest.getHashMapFromUrl_Base64(
						getUrl(AppConfig.NEW_FR_GET_USER_UPDATE_PASSWORD),
						edit1.getText().toString(), edit2.getText().toString(),
						edit3.getText().toString());
			} else if ("night".equals(getMode())) {
				return DataRequest.getHashMapFromUrl_Base64(
						getUrl(AppConfig.NEW_FR_GET_USER_UPDATE_PASSWORD),
						edit1.getText().toString(), edit2.getText().toString(),
						edit3.getText().toString());
			}
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_FR_GET_USER_UPDATE_PASSWORD), edit1
							.getText().toString(), edit2.getText().toString(),
					edit3.getText().toString());

		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			Logger.i("修改密码成功的标志" + result.get("code"));
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					PreferenceUtil.setString(mContext, "user_pass", edit2
							.getText().toString());
					makeText("恭喜您，密码修改成功");
					finish();
				} else if ("-1".equals(result.get("code"))) {
					// makeText("密码修改失败，请从新登陆");
					// Intent intent = new Intent(getApplicationContext(),
					// UserLoginStep1.class);
					// startActivity(intent);
				} else {
					makeText(result.get("msg"));
				}
			}
		}

	}

}
