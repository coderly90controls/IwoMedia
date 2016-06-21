package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.*;

import com.android.iwo.media.view.AbstractSpinerAdapter.IOnItemSelectListener;
import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.view.IwoToast;
import com.android.iwo.media.view.SpinerPopWindow;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 上传视频
 * 
 * @author abc
 * 
 */
public class UploadVideoActivity extends BaseActivity implements
		OnClickListener {
	private String event_ID = "0";
	private SpinerPopWindow window4;
	private TextView text4_4;
	private EditText upload_video_title, upload_video_introduce;
	private String path;
	private String type = "1", eventType = "0";
	private ImageView pengyouquan_img, event_img;
	private CommonDialog loadBar;
	private ArrayList<HashMap<String, String>> list1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_video);
		path = getIntent().getStringExtra("path");
		setBack(this);
		setTitle("上传视频");
		loadBar = new CommonDialog(this);
		list1 = new ArrayList<HashMap<String, String>>();
		new Set_Dict_Echo().execute();
		init();
	}

	private void init() {
		// zhuye_img = (ImageView) findViewById(R.id.zhuye_img);
		pengyouquan_img = (ImageView) findViewById(R.id.pengyouquan_img);
		event_img = (ImageView) findViewById(R.id.event_img);
		upload_video_title = (EditText) findViewById(R.id.upload_video_title);
		upload_video_introduce = (EditText) findViewById(R.id.upload_video_introduce);
		findViewById(R.id.ok).setOnClickListener(this);
		findViewById(R.id.weixuanzhong_layout).setOnClickListener(this);
		event_img.setOnClickListener(this);
	}

	private CommonDialog commonDialog = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_img:
			setCommonDialog();
			break;
		case R.id.ok:
			if (orClick()) {
				loadBar = new CommonDialog(this);
				loadBar.setMessage("上传视频中···");
				loadBar.show();
				new setUserVideo().execute();
				break;
			}
			break;
		case R.id.weixuanzhong_layout:
			if ("1".equals(type)) {
				type = "0";
				pengyouquan_img.setImageResource(R.drawable.weixuanzhong_fang);
			} else {
				type = "1";
				pengyouquan_img.setImageResource(R.drawable.xuanzhong_fang);
			}

			break;

		case R.id.event_img:
			if ("1".equals(eventType)) {
				eventType = "0";
				event_img.setImageResource(R.drawable.weixuanzhong_fang);
			} else {
				eventType = "1";
				event_img.setImageResource(R.drawable.xuanzhong_fang);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 上传视频
	 */
	private class setUserVideo extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			if ("1".equals(eventType)) {
				eventType = event_ID;
			}
			return DataRequest.SendFile(DataRequest.getUrl(
					getUrl(AppConfig.CAR_UPLOAD_VIDEO),
					getEditView(upload_video_title).replace("\r", "").replace(
							"\n", ""), getEditView(upload_video_introduce)
							.replace("\r", "").replace("\n", ""), getUserTel(),
					getUid(), type, eventType), "video_sid", path);
		}

		private String getEditView(EditText v) {
			String str = v.getText().toString();
			if (StringUtil.isEmpty(str)) {
				return "";
			} else {
				return str;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			Logger.i("返回结果：" + result);
			if (result != null) {
				if ("1".equals(result)) {
					IwoToast.makeText(mContext, "上传成功，请等待系统审核！审核通过后，才可以显示。",
							IwoToast.LENGTH_LONG);
					setResult(RESULT_OK);
					finish();
				} else if ("0".equals(result)) {
					makeText("操作失败");
				}
			} else {
				makeText("操作失败");
			}
			loadBar.dismiss();
		}

	}

	private boolean orClick() {
		String title = upload_video_title.getText().toString();
		if (StringUtil.isEmpty(title) || StringUtil.isEmpty(title.trim())) {
			makeText("标题不可为空");
			upload_video_title.setText("");
			return false;
		}

		String introduce = upload_video_introduce.getText().toString();
		if (StringUtil.isEmpty(introduce)
				|| StringUtil.isEmpty(introduce.trim())) {
			makeText("内容不可为空");
			upload_video_introduce.setText("");
			return false;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		setCommonDialog();
	}

	private void setCommonDialog() {
		int[] clikViews = null;
		OnClickListener clickListener_black = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.determine:
					finish();
					break;
				case R.id.cancel:
					commonDialog.dismiss();
					break;
				default:
					break;
				}
			}
		};
		clikViews = new int[] { R.id.determine, R.id.cancel };
		commonDialog = new CommonDialog(UploadVideoActivity.this, "是否取消视频上传？",
				clickListener_black, R.layout.loading_dialog_text, clikViews,
				R.id.tipText_view);
		commonDialog.show();
	}

	private class Set_Dict_Echo extends
			AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest
					.getHashMapFromUrl_Base64(getUrl(AppConfig.VIDEO_GET_CHILDREN_CHANNEL));
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null && "1".equals(result.get("code"))) {
				Logger.i("  活动数据   " + result.toString());
				ArrayList<HashMap<String, String>> list = DataRequest
						.getArrayListFromJSONArrayString(result.get("data"));
				if (list != null) {
					Logger.i("  活动数据   list " + list.toString());
					list1.addAll(list);
					setDictPopu();
				}

			}
		}

	}

	private void setDictPopu() {
		try {
			text4_4 = (TextView) findViewById(R.id.address);
			window4 = new SpinerPopWindow(this);
			window4.setKey("ch_name");
			window4.refreshData(list1, 0);
			window4.setItemListener(new IOnItemSelectListener() {
				@Override
				public void onItemClick(int pos) {
					event_ID = list1.get(pos).get("id");
					// event = list1.get(pos).get("ch_name");
					setItem(pos, text4_4, list1);
				}
			});
			final RelativeLayout layout1 = (RelativeLayout) findViewById(R.id.address_4_layout);
			OnClickListener listener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (v.getId() == R.id.address_4_layout && window4 != null) {
						window4.setWidth(layout1.getWidth());
						window4.showAsDropDown(layout1);
					}
				}
			};
			layout1.setOnClickListener(listener);
		} catch (Exception e) {
			makeText("没有该数据");
		}
	}

	private void setItem(int pos, TextView tex,
			List<HashMap<String, String>> list12) {
		if (pos >= 0 && pos <= list12.size()) {
			String value = list12.get(pos).get("ch_name");
			Logger.i("value " + value);
			tex.setText(value);
		}
	}
}
