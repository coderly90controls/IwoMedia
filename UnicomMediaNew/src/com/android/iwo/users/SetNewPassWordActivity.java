package com.android.iwo.users;

import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.activity.BaseActivity;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class SetNewPassWordActivity extends BaseActivity {
	private EditText mPassWord, mAgainPassWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_activity_setnew_password);
		init();
	}

	private void init() {
		setTitle("填写新密码");
		setBack_text(null);
		initView();
	}

	private void initView() {
		mPassWord = (EditText) findViewById(R.id.new_password);
		mAgainPassWord = (EditText) findViewById(R.id.again_password);

		findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isRightPutIn()) {
					new NewPassWord().execute();
				}
			}
		});
	}

	private boolean isRightPutIn() {
		String mpass = mPassWord.getText().toString();
		String mpass_again = mAgainPassWord.getText().toString();
		if (StringUtil.isEmpty(mpass)) {
			makeText("新密码不能为空");
			return false;
		} else if (!StringUtil.isPassWord(mpass)) {
			makeText("新密码为6－16位英文或数字");
			return false;
		} else if (StringUtil.isEmpty(mpass_again)) {
			makeText("确认密码不能为空");
			return false;
		} else if (!StringUtil.isPassWord(mpass_again)) {
			makeText("确认密码为6－16位英文或数字");
			return false;
		} else if (!mpass_again.equals(mpass)) {
			makeText("新密码与确认密码不一致，请重新输入");
			return false;
		}
		return true;
	}

	private class NewPassWord extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			String tel = SetNewPassWordActivity.this.getIntent()
					.getStringExtra("tel");
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.MAG_GET_NEW_PASSWORD), tel, mPassWord
							.getText().toString());
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					makeText("恭喜您，密码修改成功");
					startActivity(new Intent(getApplicationContext(),
							UserLogin.class));
					finish();
				} else {
					makeText("当前密码输入错误，请重新输入");
				}
			}
		}
	}
}
