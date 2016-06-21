package com.android.iwo.media.activity;

import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class FeedBackActivity extends BaseActivity implements OnClickListener{

	private EditText text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		init();
	}
	private void init() {
		//setTitle("意见反馈");
		setBack(null);
		text = (EditText) findViewById(R.id.feedback);
		findViewById(R.id.feedback_determine).setOnClickListener(this);
		findViewById(R.id.feedback_cancel).setOnClickListener(this);
		
		setMode(getMode());
	}
	/**
	 * 设置页面模式，
	 * @param mode
	 */
	private void setMode(String mode){
		if("day".equals(mode)){
			findViewById(R.id.feedback_determine).setBackgroundColor(getResources().getColor(R.color.comm_color_chense));
		}else if("night".equals(mode)){
			findViewById(R.id.feedback_determine).setBackgroundColor(getResources().getColor(R.color.comm_color_chense));
		}
	}
	private void setOk() {
		
		if (StringUtil.isEmpty(text.getText().toString())) {
			makeText("反馈信息不能为空");
		}
		
		else if("".equals(text.getText().toString().trim())){
			makeText("反馈信息不能为空");
		}
		else{
			new Send().execute();	
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.feedback_determine:
			setOk();
			break;
		case R.id.feedback_cancel:
			text.setText("");
			break;
		default:
			break;
		}
	}
	private class Send extends AsyncTask<Void, Void, HashMap<String, String>>{

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
				return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.FR_FEED_BACK), text.getText().toString());
		}
		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if(result != null){
				if("1".equals(result.get("code"))){
					//makeText(result.get("msg"));
					makeText("恭喜您，提交成功");
					finish();
				}
//				else{
//					makeText(result.get("msg"));
//				}
			}else{
				makeText("反馈失败");
			}
		}
	}
	
}
