package com.android.iwo.share;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.activity.BaseActivity;
import com.android.iwo.media.apk.activity.*;
import com.android.iwo.media.view.ScrollGridView;
import com.android.iwo.users.UserLogin;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class ShareActivity extends BaseActivity {
	private ScrollGridView gridView;
	private IwoAdapter adapter;
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private String con = "", type = "";
	private TextView conText;
	private String tel = "";
	private String id = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_main);

		if (StringUtil.isEmpty(getUid())) {
			Intent intent = new Intent(this, UserLogin.class);
			startActivityForResult(intent, 20130927);
		} else {
			init();
			new GetMsg().execute();
		}
	}

	private void init() {
		setBack(null);
		setTitle("分享");
		mLoadBar.setMessage("正在分享...");
		id = getIntent().getStringExtra("id");

		conText = (TextView) findViewById(R.id.share_con);
		gridView = (ScrollGridView) findViewById(R.id.grid_1);
		gridView.setSelector(new ColorDrawable(0xffffff));
		if (!StringUtil.isEmpty(con)) {
			conText.setText(con);
		}
		adapter = new IwoAdapter(this, mData) {
			public View getView(int position, View v, ViewGroup parent) {
				v = mInflater.inflate(R.layout.show_tel_item, parent, false);
				HashMap<String, String> map = (HashMap<String, String>) getItem(position);
				setItem(v, R.id.name, map.get("name") + "(" + map.get("tel")
						+ ")");
				ImageView close = (ImageView) v.findViewById(R.id.close);
				final int n = position;
				close.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mData.remove(n);
						adapter.notifyDataSetChanged();
					}
				});
				return v;
			}
		};
		gridView.setAdapter(adapter);

		TextView share = (TextView) findViewById(R.id.share);
		share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (StringUtil.isEmpty(tel))
					initTel();
				if (StringUtil.isEmpty(tel)) {
					Toast.makeText(ShareActivity.this, "请选择要分享的人",
							Toast.LENGTH_SHORT).show();
				} else {
					new Share().execute();
				}
			}
		});

		TextView select = (TextView) findViewById(R.id.select);
		select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ShareActivity.this,
						ContactActivity.class);
				if (mData != null && mData.size() != 0) {
					intent.putExtra("data", "" + mData);
				}
				intent.putExtra("from", "");
				startActivityForResult(intent, 20130923);
			}
		});
		initAddTel();
	}

	private void setMsg() {
		Uri smsToUri = Uri.parse("smsto:");
		Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
		sendIntent.putExtra("address", tel); // 电话号码，这行去掉的话，默认就没有电话
		sendIntent.putExtra("sms_body", con);
		sendIntent.setType("vnd.android-dir/mms-sms");
		startActivity(sendIntent);
	}

	/**
	 * 通过输入框输入手机号
	 */
	private void initAddTel() {
		final TextView put = (TextView) findViewById(R.id.put_in);
		TextView edit_tel_ok = (TextView) findViewById(R.id.edit_tel_ok);
		final EditText put_tel = (EditText) findViewById(R.id.edit_tel);
		final RelativeLayout put_tel_layout = (RelativeLayout) findViewById(R.id.put_tel_layout);
		put.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (put_tel_layout.getVisibility() == View.VISIBLE) {
					put.setText("输入");
					put_tel_layout.setVisibility(View.INVISIBLE);
				} else {
					put.setText("取消");
					put_tel_layout.setVisibility(View.VISIBLE);
				}
			}
		});

		edit_tel_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String tel = put_tel.getText().toString().trim();
				if (!StringUtil.isEmpty(tel)) {
					String[] tels = tel.split(",");
					if (tels != null && tels.length > 0) {
						HashMap<String, String> map = null;
						for (String t : tels) {
							if (StringUtil.isPhone(t)) {
								map = new HashMap<String, String>();
								map.put("name", "");
								map.put("tel", t);
								if (mData.size() < 10)
									mData.add(map);
								else {
									break;
								}
							} else {
								makeText("手机号" + t + "输入错误");
							}
						}
						checkTel();
						adapter.notifyDataSetChanged();
						put_tel.setText("");
					}
				}
			}
		});
	}

	private void checkTel() {
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		for (int j = 0; j < mData.size(); j++) {
			boolean has = true;
			for (int i = 0; i < data.size(); i++) {
				if (!mData.get(j).get("tel").equals(data.get(i).get("tel"))) {
					has = true;
					break;
				} else {
					has = true;
				}
			}
			if (has) {
				data.add(mData.get(j));
			}
		}

		mData.clear();
		mData.addAll(data);
	}

	private void initTel() {
		for (HashMap<String, String> map : mData) {
			tel += map.get("tel") + ",";
		}

		Logger.i("------" + tel);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 20130923 && resultCode == RESULT_OK) {
			mData.clear();
			String str = data.getStringExtra("data_back");
			String[] strs = str.split(";");

			HashMap<String, String> map = null;
			int i = 0;
			for (String s : strs) {
				map = new HashMap<String, String>();
				String ss[] = s.split("_");
				if (ss.length == 2) {
					map.put("name", ss[0]);
					map.put("tel", ss[1]);
					mData.add(map);
				}
				i++;
				if (i >= 10)
					break;
			}
			adapter.notifyDataSetChanged();
		} else if (requestCode == 20130923 && resultCode == RESULT_CANCELED) {
			mData.clear();
			adapter.notifyDataSetChanged();
		} else if (requestCode == 20130927 && resultCode == RESULT_OK) {
			init();
			// new GetData().execute();
		} else if (requestCode == 20130927 && resultCode == RESULT_CANCELED) {
			finish();
		}
	}

	private class GetMsg extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			HashMap<String, String> way = DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.VIDEO_SHARE_COUNT), id);
			HashMap<String, String> way1 = DataRequest
					.getHashMapFromUrl_Base64(
							getUrl(AppConfig.NEW_V_VIDEO_DETAIL), id);
			Log.i("ooooooooooo---->", String.valueOf(way1));
			if (way != null) {
				if ("1".equals(way.get("code"))) {
					HashMap<String, String> map = DataRequest
							.getHashMapFromJSONObjectString(way.get("data"));
					if (map != null) {
						type = map.get("share");
						con = map.get("count");
						Log.i("map---->", String.valueOf(map));// =count
						Log.i("mdats", String.valueOf(mData));// 空
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			conText.setText(con);
			Log.i("result---->", String.valueOf(result));
		}
	}

	private class Share extends AsyncTask<Void, Void, Boolean> {

		private HashMap<String, String> msg = null;

		@Override
		protected void onPreExecute() {
			if (mLoadBar != null)
				mLoadBar.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if ("2".equals(type)) {
				msg = DataRequest.getHashMapFromUrl_Base64(
						getUrl(AppConfig.VIDEO_SEND_SMS), con, tel, id);
				return true;
			} else {
				setMsg();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				if (msg != null && "1".equals(msg.get("code"))) {
					Toast.makeText(ShareActivity.this, "恭喜您，分享成功",
							Toast.LENGTH_SHORT).show();
					tel = "";
					mData.clear();
					msg = null;
					adapter.notifyDataSetChanged();
				} else {
					makeText("分享失败，请检查当前网络");
				}
			}
			if (mLoadBar != null)
				mLoadBar.dismiss();
			Log.i("result", "result" + result);
		}
	}
}
