package com.android.iwo.media.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;

public class NightDialogActivity extends BaseActivity implements
		OnClickListener {
	private ImageView boy_img, girl_img;
	private TextView boy_tex1, girl_tex2;
	HashMap<String, String> imgHash = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_night_dialog_or);
		new getUserImg().execute();
	}

	private void init() {
		boy_img = (ImageView) findViewById(R.id.boy_img);
		girl_img = (ImageView) findViewById(R.id.girl_img);
		boy_tex1 = (TextView) findViewById(R.id.boy_tex1);
		girl_tex2 = (TextView) findViewById(R.id.girl_tex2);
		LoadBitmap bitmap = new LoadBitmap();
		bitmap.loadImage(imgHash.get("nan_head"), boy_img);
		bitmap.loadImage(imgHash.get("nv_head"), girl_img);
		boy_img.setOnClickListener(this);
		girl_img.setOnClickListener(this);

	}

	private String ID;
	private int SEX;

	private void setRandom() {
		ID = (char) ('A' + System.currentTimeMillis() % 26) + ""
				+ (int) (Math.random() * 1000000) + "0000000";
		SEX = 1;
		ID = ID.substring(0, 7);
		Logger.i("昵称：" + ID + "  性别： " + SEX);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.boy_img:
			SEX = 1;
			boy_img.setBackgroundResource(R.drawable.ico_select);
			girl_img.setBackgroundColor(getResources().getColor(
					R.color.transparent_aiwo));

			boy_tex1.setTextColor(getResources().getColor(
					R.color.comm_pink_color));
			girl_tex2.setTextColor(getResources().getColor(R.color.black));
			new SetInfo().execute();
			break;
		case R.id.girl_img:
			SEX = 2;
			boy_img.setBackgroundColor(getResources().getColor(
					R.color.transparent_aiwo));
			girl_img.setBackgroundResource(R.drawable.ico_select);
			boy_tex1.setTextColor(getResources().getColor(R.color.black));
			girl_tex2.setTextColor(getResources().getColor(
					R.color.comm_pink_color));
			new SetInfo().execute();
			break;
		default:
			break;
		}
	}

	private class SetInfo extends
			AsyncTask<Void, Void, HashMap<String, String>> {
		String user_head;

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			if (SEX == 2) {
				user_head = imgHash.get("nv_head");
			} else {
				user_head = imgHash.get("nan_head");
			}
			DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_FR_USER_INFO_NIGHT_EDIT), "" + SEX,
					ID, user_head);
			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			Intent intent = new Intent();
			intent.putExtra("sex", "" + SEX);
			intent.putExtra("user_night", ID);
			intent.putExtra("user_head_img", user_head);
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	private class getUserImg extends
			AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			setRandom();
			return DataRequest
					.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_FR_USER_INFO_NIGHT_GET_HEAD));
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				imgHash.clear();
				if ("1".equals(result.get("code"))) {
					imgHash.putAll(DataRequest
							.getHashMapFromJSONObjectString(result.get("data")));
					init();
				}
			}

		}

	}

	@Override
	public void onBackPressed() {
		new SetInfo().execute();
	}

}
