package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.Constants;
import com.android.iwo.media.apk.activity.R;
import com.android.iwo.media.view.SpinerPopWindow;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class UserInfoEdit extends BaseActivity {
	private SpinerPopWindow window1, window2, window3, window4;
	private ArrayList<HashMap<String, String>> list1, list2, list3, list4;
	private TextView text1_1, text2_2, text3_3, text4_4;
	private String city_ID = "0", region_ID = "0", industry_ID = "0",
			position_ID = "0";
	private String city = "城市", region = "区域", industry = "行业",
			position = "职位";
	private HashMap<String, String> mData = new HashMap<String, String>();
	private String user_id = "";
	private String sex = "1";
	private boolean isClick = true;
	private TextView edit_1, edit_2;
	private ImageView sex_boy_img, sex_grid_img;
	private String mode;
	private Button save;

	// private EditText sign;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_user_info);
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		list1 = new ArrayList<HashMap<String, String>>();
		list2 = new ArrayList<HashMap<String, String>>();
		list3 = new ArrayList<HashMap<String, String>>();
		list4 = new ArrayList<HashMap<String, String>>();
		mode = getMode();
		Logger.i("看有没有模式传过来：" + mode);
		init();
	}

	private void init() {
		setView();
		setBack(null);
		new GetUserData().execute();
		user_id = getUid();
		edit_1 = (TextView) findViewById(R.id.edit_user);
		edit_2 = (TextView) findViewById(R.id.edit_2);
		String user_name = getPre("user_name");
		String nick_name = getPre("nick_name");
		Logger.i("用户名：" + user_name);
		if ("day".equals(getMode())) {
			edit_1.setText(user_name);
			edit_2.setText(nick_name);
		}
		if (!StringUtil.isEmpty(getPre("Umeng"))) {
			edit_1.setText(getPre("Umeng"));
		}
		if (!StringUtil.isEmpty(getPre("nick_name"))) {
			edit_2.setText(getPre("nick_name"));
		}

		sex_boy_img = (ImageView) findViewById(R.id.sex_boy_img);
		sex_grid_img = (ImageView) findViewById(R.id.sex_grid_img);

	}

	private void setMode(String mode) {
		if ("day".equals(mode)) {
			findViewById(R.id.edit_user_ok).setBackgroundResource(
					R.drawable.btn_blue_radiu_selector);
			setSex(R.drawable.do_yes_pick, R.drawable.do_no_pick);
		} else if ("night".equals(mode)) {
			findViewById(R.id.edit_user_ok).setBackgroundResource(
					R.drawable.btn_pink_radiu_selector);
			setSex(R.drawable.do_yes_pick, R.drawable.do_no_pick);
		}
	}

	/**
	 * @param hover
	 *            被选中图片 资源 id
	 * @param loop
	 *            未选中图片 资源 id
	 */
	private void setSex(int hover, int loop) {
		sex = mData.get("sex") + "";
		if ("1".equals(sex)) {
			sex_boy_img.setImageResource(hover);
			sex_grid_img.setImageResource(loop);
		} else {
			sex_boy_img.setImageResource(loop);
			sex_grid_img.setImageResource(hover);
		}
	}

	/**
	 * 获取模式下的选中图片
	 * 
	 * @param or
	 *            true 选中的图片 false 未选中
	 * @return 图片 ID
	 */
	private int getModeSexYes(boolean or) {
		if ("day".equals(mode)) {
			if (or) {
				return R.drawable.do_yes_pick;
			} else {
				return R.drawable.do_no_pick;
			}

		} else if ("night".equals(mode)) {
			if (or) {
				return R.drawable.do_yes_pick;
			} else {
				return R.drawable.do_no_pick;
			}
		}
		return 0;
	}

	private void setView() {

		if (mData != null && "1".equals(mData.get("sex"))) {
			sex_boy_img.setBackgroundResource(R.drawable.do_yes_pick);
			sex_grid_img.setBackgroundResource(R.drawable.do_no_pick);
		} else if (mData != null && "2".equals(mData.get("sex"))) {
			sex_boy_img.setBackgroundResource(R.drawable.do_no_pick);
			sex_grid_img.setBackgroundResource(R.drawable.do_yes_pick);
		}

		if (mData != null) {
			Logger.i("数据===>" + mData.toString());
			if (mData.get("userName") != null) {
				edit_1.setText(mData.get("userName").substring(1));
			}
			LinearLayout sex_boy = (LinearLayout) findViewById(R.id.sex_boy);
			LinearLayout sex_grid = (LinearLayout) findViewById(R.id.sex_grid);
			OnClickListener listener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.sex_boy:
						sex_boy_img.setImageResource(getModeSexYes(true));
						sex_grid_img.setImageResource(getModeSexYes(false));
						sex = "1";
						break;
					case R.id.sex_grid:
						sex_boy_img.setImageResource(getModeSexYes(false));
						sex_grid_img.setImageResource(getModeSexYes(true));
						sex = "2";
						break;
					}
				}
			};
			sex_boy.setOnClickListener(listener);
			sex_grid.setOnClickListener(listener);
		}

		Button edit_user_ok = (Button) findViewById(R.id.edit_user_ok);
		edit_user_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (isname()) {
					new GetData().execute();
				}
			}

			private boolean isname() {
				String name = edit_2.getText().toString();
				// String sign_str = sign.getText().toString();
				if (StringUtil.isEmpty(name) || "".equals(name.trim())) {
					makeText("姓名不能为空");
					return false;
				}

				return true;
			}
		});
	}

	// 保存成功的
	private class GetData extends
			AsyncTask<String, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(String... params) {

			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_FR_USER_INFO_EDIT_SIGN), edit_2
							.getText().toString(), sex);

		}

		private String getNoNullText(EditText view) {
			if (view != null) {
				if (null == view.getText()) {
					return "nbsp_null";
				} else if ("".equals(view.getText().toString().trim())) {
					return "nbsp_null";
				}
				return view.getText().toString();
			}
			return "nbsp_null";
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					Logger.i("注册成功率么？====》" + result);
					// makeText(result.get("msg"));
					makeText("恭喜您，资料保存成功");
					PreferenceUtil.setString(mContext, "user_city_id", city_ID);
					PreferenceUtil.setString(mContext, "user_area_id",
							region_ID);
					PreferenceUtil.setString(mContext, "area_name", region);
					PreferenceUtil.setString(mContext, "user_position",
							position);
					PreferenceUtil.setString(mContext, "nick_name", edit_2
							.getText().toString());
					PreferenceUtil.setString(mContext, "sex", sex);

					finish();
				}
			} else {
				makeText("操作失败");
			}
			// isClick = true;
		}
	}

	private class GetUserData extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			if ("day".equals(getMode())) {

				HashMap<String, String> result = DataRequest
						.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_MY_INFO));
				if (result != null) {
					if ("1".equals(result.get("code"))) {
						mCache.put(Constants.USER_INIT_DATA, result.get("data"));
						return DataRequest
								.getHashMapFromJSONObjectString(result
										.get("data"));
					}
				}
				return result;
			} else {
				return DataRequest.getHashMapFromUrl_Base64(
						getUrl(AppConfig.NEW_FR_N_GET_USER_INF), getUid());
			}
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				Logger.i("mdata===>" + mData);
				mData.putAll(result);
				setView();
				// setMode(mode);
				// findViewById(R.id.user_edit_scroll).setVisibility(View.VISIBLE);
			} else {
				String user = mCache.getAsString(Constants.USER_INIT_DATA);
				if (!StringUtil.isEmpty(user)) {
					mData.putAll(DataRequest
							.getHashMapFromJSONObjectString(user));
					setView();
					// setMode(mode);
					findViewById(R.id.user_edit_scroll).setVisibility(
							View.VISIBLE);
				}
			}
			mLoadBar.dismiss();
		}
	}

	private void setItem(int pos, TextView tex,
			List<HashMap<String, String>> list12) {
		if (pos >= 0 && pos <= list12.size()) {
			String value = list12.get(pos).get("dict_name");
			Logger.i("value " + value);
			tex.setText(value);
		}
	}

}
