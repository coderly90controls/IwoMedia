package com.android.iwo.media.activity;

import java.util.HashMap;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class msShareActivity extends BaseActivity implements OnClickListener {

	private EditText tell_list;
	private String video_id;
	private TextView content;
	private boolean isFrequent = true;// 防止用户频繁点击

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ms_share);
		init();
	}

	private void init() {
		ImageView view = (ImageView) findViewById(R.id.title_img);
		view.setVisibility(View.VISIBLE);
		view.setBackgroundResource(R.drawable.logo);
		setBack(null);
		String tell = getIntent().getStringExtra("tell");
		String ni_name = getIntent().getStringExtra("ni_name");
		video_id = getIntent().getStringExtra("video_id");
		String video_name = getIntent().getStringExtra("video_name");
		tell_list = (EditText) findViewById(R.id.share_tell_list);
		content = (TextView) findViewById(R.id.share_content_list);
		findViewById(R.id.share_ok_share).setOnClickListener(this);
		findViewById(R.id.ms_share_xuanzhe).setOnClickListener(this);
		content.setText(ni_name + "(" + tell + ")"
				+ "分享：我发现一个非常有趣的视频应用，邀请你一起来玩啊！"
				+ "http://wap.iwo.mokacn.com:8080/share/video_info?id="
				+ video_id);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share_ok_share:
			Logger.i("马上分享");
			if (isShare()) {
				if (isFrequent) {
					isFrequent = false;
					new GetDataTell().execute();
				}
			}
			break;

		case R.id.ms_share_xuanzhe:
			Logger.i("选择通讯录");
			Intent intent = new Intent(msShareActivity.this,
					MytellAddActivity.class);
			startActivityForResult(intent, 911);
			break;
		}

	}

	private boolean isShare() {
		String tell = tell_list.getText().toString();
		String trim = tell.trim();
		String[] tell_ss = trim.split(",");
		if (StringUtil.isEmpty(tell) || "".equals(tell.trim())) {
			makeText("电话号码不能为空！");
			return false;
		}
		if (tell_ss.length < 10) {
			makeText("请输入10个以上号码");
			return false;
		}

		for (int i = 0; i < tell_ss.length; i++) {
			if (!StringUtil.isPhone(tell_ss[i])) {
				makeText("输入电话号码第" + (i + 1) + "个格式不正确！！！！");
				return false;
			}
		}

		return true;

	}

	private class GetDataTell extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoadBar.setMessage("正在提交数据，请稍候！");
			mLoadBar.show();

		}

		protected HashMap<String, String> doInBackground(Void... params) {
			String str = DataRequest.getStringFromURL_Base64(
					getUrl(AppConfig.SHARE_SEND_TEN_FRIEND), video_id,
					tell_list.getText().toString(), content.getText()
							.toString());
			return DataRequest.getHashMapFromJSONObjectString(str);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			super.onPostExecute(result);
			mLoadBar.dismiss();
			isFrequent = true;
			if (result != null) {
				Logger.i(result.toString());
				if ("1".equals(result.get("code"))) {
					makeText("分享成功");
					// Intent intent = new Intent(msShareActivity.this,
					// VideoDetailActivity_new.class);
					// intent.putExtra("video_id", video_id);
					// startActivity(intent);
					finish();
				}
			} else {
				makeText("好友分享失败");
			}

		}

	}

	// 在这里从通信录拿到的电话号码
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 911 && resultCode == 119) {
			String tell = data.getStringExtra("tell");
			Logger.i("拿到的电话是：" + tell);
			tell_list.setText(tell);
		}
	}

}
