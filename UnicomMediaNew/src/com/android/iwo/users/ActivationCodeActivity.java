package com.android.iwo.users;

import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.activity.BaseActivity;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.AppUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 激活账号
 */
public class ActivationCodeActivity extends BaseActivity {

	private EditText mCode;
	private String mTel;
	private TextView send_again;
	private boolean isClick = true;
	private String mName;
	private String mPass;
	private String pass;  //注册传过来的密码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_activity_activation_code);
		pass = getIntent().getStringExtra("pass");
		init();
	}

	private void init() {
		setBack_text(null);
		setTitle("激活账号");
		initView();
	}

	private void initView() {
		Intent intent = this.getIntent();
		mTel = intent.getStringExtra("tel");
		mName = intent.getStringExtra("name");
		mPass = intent.getStringExtra("pass");
		mCode = (EditText) findViewById(R.id.code_num);
		send_again = (TextView) findViewById(R.id.send_again);
		findViewById(R.id.code_ok).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isRightPutIn()) {
					new GetData().execute();
				}
			}
		});
		//重新发送号码
		send_again.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isClick) {
					isClick = true;
					setTimeToast();
					new AgainSend().execute();
				}

			}
		});
		TextView telphone_tip = (TextView) findViewById(R.id.telphone_tip);
		telphone_tip.setText("验证短信已经发送至" + StringUtil.setTelHint(mTel));
		setTimeToast();
		initDialog();
	}

	private void initDialog() {
		final LinearLayout dialog = (LinearLayout) findViewById(R.id.dialog_layout);
		findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.setVisibility(View.GONE);
				new GetData().execute();
				
				
				// new RegOk().execute();
				//makeText("提交数据请耐心等待！");
			}
		});

		findViewById(R.id.cancle).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.setVisibility(View.GONE);
			}
		});
	}

	private void setTimeToast() {
		final Handler handler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				String data = (String) msg.getData().get("key");

				if ("1".equals(data)) {
					send_again.setText("重新发送验证码");
					isClick = false;
				} else {
					send_again.setText(data + "秒");
				}
				return false;
			}
		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				int i = 60;

				while (i > 0) {
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putString("key", "" + i);
					message.setData(bundle);
					message.what = i;
					handler.sendMessage(message);
					i--;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private boolean isRightPutIn() {
		if (StringUtil.isEmpty(mCode.getText().toString())) {
			makeText("验证码不能为空");
			return false;
		}
		return true;
	}



	private class ActiveCode extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_USER_CHECK_CODE),
					mTel,mName,mPass,mCode.getText().toString());
		}
		
		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			Logger.i("第2步注册结果：" + result.get("code"));
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					// Toast.makeText(ActivationCodeActivity.this, "激活码验证成功",
					// Toast.LENGTH_SHORT).show();
					LinearLayout dialog = (LinearLayout) findViewById(R.id.dialog_layout);
					if (dialog.getVisibility() == View.GONE) {
						dialog.setVisibility(View.VISIBLE);
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(mCode.getWindowToken(), 0);
					}
				} else {
					makeText(result.get("msg"));
				}
			}
		}
	}
   /**
    *   
    * @author ZhangZhanZhong
    *
    */

	/**
	 * 重新发送验证码
	 * 
	 * @author ZhangZhanZhong
	 * 
	 */
	private class AgainSend extends AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) { 
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_USER_REG_GO_ZHUC), mTel,"6");
		}

		// TODO 这个接口有问题，记得回头修改。
		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			Logger.i("注册重新发送验证码"+result.get("code"));
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					makeText("验证码已发送，请注意查收");
				}
				if("-1".equals(result.get("code"))){
					makeText("手机号码错误");
				}
				else {
					makeText(result.get("msg"));
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 10001 && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}
	//注册成功
	private class GetData extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected void onPreExecute() {
			mLoadBar.setMessage("数据加载...");
			mLoadBar.show();
		}
		/*
		Intent intent = new Intent(mContext, RegistInfoActivity.class);
		intent.putExtra("tel", mTel);
		intent.putExtra("name", mName);
		intent.putExtra("pass", mPass);
		intent.putExtra("code", mCode.getText().toString());
		*/
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			//return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_USER_CHECK_CODE), mTel, mName, mPass
					//,mCode.getText().toString());
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_USER_CHECK_CODE),
					mTel,mName,mPass,mCode.getText().toString());
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					makeText("注册成功");
					Intent intent = new Intent(mContext, UserLogin.class);
					intent.putExtra("pass", pass);
					startActivityForResult(intent, 10001);
					HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(result.get("data"));
					PreferenceUtil.setString(mContext, "mTel", "");
					Logger.i("  注册数据：" + result.toString());
					if (map != null) {

						PreferenceUtil.setString(mContext, "user_name", map.get("user_name"));
						PreferenceUtil.setString(mContext, "id", map.get("id"));
						PreferenceUtil.setString(mContext, "real_name", map.get("real_name"));
						PreferenceUtil.setString(mContext, "user_pass", map.get("user_pass"));
						PreferenceUtil.setString(mContext, "nick_name", map.get("nick_name"));
						//intent.putExtra("pass", mEdit_pass.getText().toString());
						//PreferenceUtil.setString(mContext, "user_pass11", map.get("user_pass"));
						//自定义存密码
						PreferenceUtil.setString(mContext, "user_pass11", pass);
						
						
						AppUtil.END = "";
						setPubParame();
					}
					
					PreferenceUtil.setString(mContext, "login_tel", mTel);
					
					startActivity(new Intent(mContext, UserLogin.class));
				} else {
					makeText(result.get("msg"));
				}
			}

			mLoadBar.dismiss();
		}
	}
	
}
