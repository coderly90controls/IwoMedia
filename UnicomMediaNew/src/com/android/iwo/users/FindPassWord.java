package com.android.iwo.users;

import java.util.HashMap;

import android.R.string;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.activity.BaseActivity;
import com.android.iwo.media.apk.activity.*;
import com.android.iwo.media.apk.activity.R.color;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class FindPassWord extends BaseActivity {
	private EditText mEditTel, mEditCode;
	private LinearLayout content1, content2, content3;
	private boolean isCanSend = false;
	private Button send_again;
	private TextView tv_tishi;
	private String str_ts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_activity_find_password);
		setTitle("找回密码");
		init();
	}

	private void init() {
		setBack_text(null);
		tv_tishi = (TextView) findViewById(R.id.tv_tishi);
		if (!StringUtil.isEmpty(getPre("user_name_ti"))) {
			str_ts = getPre("user_name_ti");
			tv_tishi.setText(StringUtil.setTelHint(str_ts));
		} else {
			findViewById(R.id.layout_tishi).setVisibility(View.GONE);
		}
		content1 = (LinearLayout) findViewById(R.id.content1);
		content2 = (LinearLayout) findViewById(R.id.content2);
		content3 = (LinearLayout) findViewById(R.id.content3);
		send_again = (Button) findViewById(R.id.send_again);
		initTelView();
	}

	private void initTelView() {
		mEditTel = (EditText) findViewById(R.id.tel_num);
		TextView findPassWord = (TextView) findViewById(R.id.ok);
		findPassWord.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (isRightPutIn()) {
					new GetCode().execute();
				}
			}
		});
	}

	private boolean isRightPutIn() {
		String tel = mEditTel.getText().toString();
		if (StringUtil.isEmpty(tel)) {
			makeText("手机号码或邮箱不能为空");
			return false;
		} else if (!StringUtil.isPhone(tel) & !StringUtil.isEmail(tel)) {
			makeText("手机号码或邮箱输入错误，请重新输入");
			return false;
		} else if (!tel.equals(str_ts)) {
			makeText("手机号码或邮箱与提示信息不符，请重新输入");
			return false;
		}
		return true;
	}

	private void initCodeView() {
		mEditCode = (EditText) findViewById(R.id.code_num);

		TextView findPassWord = (TextView) findViewById(R.id.code_ok);
		findPassWord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isRightCodePutIn()) {
					new SearchCode().execute();
				}
			}
		});
		send_again.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isRightPutIn() && isCanSend) {
					isCanSend = false;
					setTimeToast();
					send_again.setBackgroundColor(color.ban_tou_ming);// comm_text_gray_color
					new AgainSend().execute();
				} else {
					// right_tip.setBackgroundColor(color.comm_text_gray_color);
					// makeText("时间未到，无法重新发送验证码");
				}
			}
		});
		TextView telphone_tip = (TextView) findViewById(R.id.telphone_tip);
		telphone_tip.setText("验证短信已经发送至 "
				+ StringUtil.setTelHint(mEditTel.getText().toString()));
		setTimeToast();
	}

	private void setTimeToast() {
		final Handler handler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				String data = (String) msg.getData().get("key");
				if (!StringUtil.isEmpty(data)) {
					send_again.setText(data + "秒");
				} else {
					send_again.setText("重新发送验证码");
					send_again.setBackgroundResource(R.drawable.button);
					isCanSend = true;
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
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("key", "");
				message.what = 0;
				message.setData(bundle);
				handler.sendMessage(message);
			}
		}).start();
	}

	private boolean isRightCodePutIn() {
		if (StringUtil.isEmpty(mEditCode.getText().toString())) {
			makeText("验证码不能为空");
			return false;
		}
		return true;
	}

	// 找回密码 发送验证码
	private class GetCode extends
			AsyncTask<Void, Void, HashMap<String, String>> {
		protected void onPreExecute() {
			mLoadBar.setMessage("数据加载...");
			mLoadBar.show();
		}

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_USER_FIND_CODE), mEditTel.getText()
							.toString(), "爱握视频分享");
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			mLoadBar.dismiss();
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					makeText("验证码已发送，请注意查收");
					content1.setVisibility(View.GONE);
					content2.setVisibility(View.VISIBLE);
					content3.setVisibility(View.VISIBLE);
					setTitle("填写验证码");
					send_again.setBackgroundColor(color.comm_text_gray_color);
					initCodeView();
				} else if ("-1".equals(result.get("code"))) {
					makeText("手机号码或邮箱尚未注册");
				} else {
					makeText(result.get("msg"));
				}
			}
		}

	}

	// 找回密码验证码验证
	private class SearchCode extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_USER_FIND_CHECK_CODE), mEditTel
							.getText().toString(), mEditCode.getText()
							.toString());
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					makeText("验证成功");
					Intent intent = new Intent(FindPassWord.this,
							SetNewPassWordActivity.class);
					intent.putExtra("tel", mEditTel.getText().toString());
					startActivity(intent);
					finish();
				} else {
					makeText("验证码错误，请重新输入");
				}
			}
		}
	}

	// 重新发送短信请求
	private class AgainSend extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			// return
			// DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_USER_REG_GO),
			// mEditTel.getText().toString(),"1");
			return DataRequest.getMap_64(getUrl(AppConfig.NEW_USER_FIND_CODE),
					mEditTel.getText().toString(), "爱握视频分享");

		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				if ("1".equals(result.get("code"))) {

					makeText("重新发送短信成功");
				} else {
					makeText("重新发送短信失败");
				}
			}
		}
	}
}
