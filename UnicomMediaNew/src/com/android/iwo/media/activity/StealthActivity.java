package com.android.iwo.media.activity;

import java.util.HashMap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
/**
 * user_ hide_status preference 保存模式状态的字段。
 * @author hanpengyuan
 *
 */
public class StealthActivity extends BaseActivity implements OnClickListener {
	String status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stealth_mode);
		init();
		setTitle("隐身模式");
		setBack(null);
		
	}
	private void init() {
		status = PreferenceUtil.getString(this, "user_ hide_status", "1");
		setOnClick(R.id.layout_1,R.id.layout_2,R.id.layout_3);
		switch (Integer.valueOf(status)) {
		case 1:
			findViewById(R.id.select_1).setVisibility(View.VISIBLE);
			break;
		case 2:
			findViewById(R.id.select_3).setVisibility(View.VISIBLE);
			break;
		case 3:
			findViewById(R.id.select_2).setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}
	
	private void setOnClick(Integer... params) {
		for (int i = 0; i < params.length; i++) {
			findViewById(params[i]).setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		View v1 = findViewById(R.id.select_1);
		View v2 = findViewById(R.id.select_2);
		View v3 = findViewById(R.id.select_3);
		v1.setVisibility(View.GONE);
		v2.setVisibility(View.GONE);
		v3.setVisibility(View.GONE);
		switch (v.getId()) {
		case R.id.layout_1:// 在线 1 接口请求参数
			v1.setVisibility(View.VISIBLE);
			status = "1";
			break;
		case R.id.layout_2:// 仅好友可见 3
			v2.setVisibility(View.VISIBLE);
			status = "3";
			break;
		case R.id.layout_3:// 隐身 2
			v3.setVisibility(View.VISIBLE);
			status = "2";
			break;
		default:
			break;
		}
		new SetStatus().execute(status);
	}

	/**
	 * 设置用户在线状态
	 * 
	 */
	protected class SetStatus extends AsyncTask<String, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_EDIT_USER_STATUS), params[0]);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (null != result) {
				makeText(result.get("msg"));
				PreferenceUtil.setString(StealthActivity.this,"user_hide_status", status);
			} else {
				makeText("操作失败");
				init();
			}
		}
	}
}
