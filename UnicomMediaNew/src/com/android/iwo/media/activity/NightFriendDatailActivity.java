package com.android.iwo.media.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.DateUtil;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.LocationManage;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class NightFriendDatailActivity extends BaseActivity {
	String friend_tp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_detail_night);
		friend_tp = getIntent().getStringExtra("friend_tp");
		setNightTitle("详细资料");
		setBack(null);
		new GetData().execute();
	}

	private class GetData extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		protected HashMap<String, String> doInBackground(Void... params) {
			ArrayList<HashMap<String, String>> map;
			map = DataRequest.getArrayListFromUrl_Base64(
					getUrl(AppConfig.VIDEO_GET_NIGHT_INFO), friend_tp);

			Logger.i("map = " + map);
			if (map != null && map.size() > 0) {
				return map.get(0);
			}
			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				Logger.i("资料信息：" + result);
				ImageView user_imgImageView = (ImageView) findViewById(R.id.friend_head_img);
				LoadBitmap bitmap = new LoadBitmap();
				if (!StringUtil.isEmpty(result.get("head_img"))) {
					bitmap.loadImage(result.get("head_img"), user_imgImageView);
				}
				setItem(R.id.friend_name, result.get("nick_name"));
				setItem(R.id.friend_sex, "2".equals(result.get("sex")) ? "女"
						: "男");
				String user_age = result.get("age");
				if (StringUtil.isEmpty(user_age) || "0".equals(user_age)) {
					setItem(R.id.friend_age, "保密");
				} else {
					setItem(R.id.friend_age, user_age);
				}
				setItem(R.id.friend_signature, result.get("sign"));
				// 距离。
				setTitle("");
				// friend_time 最后操作时间
				if (!StringUtil.isEmpty(result.get("last_time"))) {
					String t = result.get("last_time");
					long ti = 0;
					try {
						ti = Long.valueOf(t);
					} catch (Exception e) {
					}
					String time = DateUtil.subDate(ti);
					setItem(R.id.friend_time, !StringUtil.isEmpty(time) ? time
							: "暂无时间");// 时间
				}

				String lat1 = PreferenceUtil.getString(
						NightFriendDatailActivity.this, "address_lat", "");
				String lon1 = PreferenceUtil.getString(
						NightFriendDatailActivity.this, "address_lng", "");
				if (!StringUtil.isEmpty(lat1) && !StringUtil.isEmpty(lon1)) {
					double juli = 0;
					try {
						juli = LocationManage.getDistatce(Double.valueOf(lon1),
								Double.valueOf(result.get("latlng_0_d")),
								Double.valueOf(lon1),
								Double.valueOf(result.get("latlng_1_d")));

						if (juli > 100) {
							setItem(R.id.friend_juli,
									juli > 0 ? setDouble(juli / 1000) + "km"
											: "暂无距离");// 距离
						} else {
							setItem(R.id.friend_juli, juli > 0 ? juli + "m"
									: "暂无距离");// 距离
						}
					} catch (Exception e) {
					}
				} else {

				}

				findViewById(R.id.fiend_data_night_layout).setVisibility(
						View.VISIBLE);

			} else {
				makeText("暂无好友信息！");
			}
		}
	}

	private String setDouble(double db) {
		DecimalFormat df = new DecimalFormat("#.00");
		return df.format(db);
	}
}
