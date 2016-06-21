package com.android.iwo.media.qun.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.jivesoftware.smack.Chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.activity.BaseActivity;
import com.android.iwo.media.activity.ChatHistory;
import com.android.iwo.media.activity.FriendDetail;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.view.ScrollGridView;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.SendFile;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class QunInfoActivity extends BaseActivity {

	private String id = "", imgUrl, title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quninfo);
		init();
	}

	private void init() {
		setTitle("群资料");
		setBack(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
		id = getIntent().getStringExtra("id");
		ImageView right = (ImageView) findViewById(R.id.right_img);
		right.setBackgroundResource(R.drawable.tianjia);
		right.setVisibility(View.VISIBLE);
		right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(QunInfoActivity.this, CreateQun.class);
				startActivityForResult(intent, 10010);
			}
		});

		TextView add_new = (TextView) findViewById(R.id.add_new);
		add_new.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(QunInfoActivity.this, QunAddMember.class);
				intent.putExtra("id", id);
				startActivityForResult(intent, 20140212);
			}
		});

		// 聊天
		findViewById(R.id.chat).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(mContext, Chat.class);
				intent.putExtra("id", id);
				intent.putExtra("name", title);
				intent.putExtra("qun", "true");
				startActivity(intent);
			}
		});

		// 聊天记录
		findViewById(R.id.tab1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ChatHistory.class);
				intent.putExtra("id", id);
				intent.putExtra("name", title);
				startActivity(intent);
			}
		});
		// 聊天消息设置
		findViewById(R.id.tab2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new GetMessage().execute();
			}
		});

		findViewById(R.id.delete).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(mContext).setTitle("解散群").setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						new Delete().execute();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
			}
		});

		ImageView status = (ImageView) findViewById(R.id.off);
		String statustr = PreferenceUtil.getString(this, "status" + getUid(), "0");
		if ("1".equals(statustr)) {
			status.setBackgroundResource(R.drawable.switchoff);
		} else {
			status.setBackgroundResource(R.drawable.switchon);
		}

		new GetInfo().execute();
		setImage();
	}

	private void setImage() {
		ImageView head = (ImageView) findViewById(R.id.head);
		head.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(mContext).setTitle("设置头像").setItems(new String[] { "相机拍摄", "相册选取" }, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							imgUrl = SendFile.takePhoto((Activity) mContext, 1);
						} else if (which == 1) {
							Intent intent = new Intent();
							intent.setType("image/*");
							intent.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intent, 2);
						}
						dialog.dismiss();
					}

				}).show();
			}
		});
	}

	private void setInfo(final HashMap<String, String> data) {
		if (data == null)
			return;
		findViewById(R.id.layout).setVisibility(View.VISIBLE);
		LoadBitmap.getIntence().loadImage(data.get("group_img"), (ImageView) findViewById(R.id.head));
		setTitle(data.get("group_name"));
		title = data.get("group_name");
		setItem(R.id.name, "ID " + data.get("master_id"));
		setItem(R.id.type, data.get("cate_name"));
		setItem(R.id.address, data.get("dict_name") + data.get("area_name"));
		setItem(R.id.num, "群成员" + data.get("user_count") + "人");
		findViewById(R.id.num).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, Qunlist.class);
				intent.putExtra("data", "" + data.get("user_list"));
				intent.putExtra("name", "" + data.get("group_name"));
				startActivity(intent);
			}
		});
		ScrollGridView gridView = (ScrollGridView) findViewById(R.id.scrollGridView1);
		ArrayList<HashMap<String, String>> list1 = DataRequest.getArrayListFromJSONArrayString(data.get("user_list"));
		if (list1 != null) {
			final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			if (list1.size() > 4) {
				list.add(list1.get(0));
				list.add(list1.get(1));
				list.add(list1.get(2));
				list.add(list1.get(3));
			} else {
				list.addAll(list1);
			}
			gridView.setSelector(new ColorDrawable(0x00000000));
			IwoAdapter adapter = new IwoAdapter(this, list) {
				@Override
				public View getView(int position, View v, ViewGroup parent) {
					if (v == null)
						v = mInflater.inflate(R.layout.grid_list_item, parent, false);
					HashMap<String, String> map = list.get(position);
					setItem(v, R.id.tex, map.get("nickname"));
					setImageView(v, R.id.img, map.get("head_img"));
					return v;
				}
			};
			gridView.setAdapter(adapter);
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Intent intent = new Intent(mContext, FriendDetail.class);
					intent.putExtra("attention", "4");
					intent.putExtra("id", list.get(arg2).get("id"));
					startActivity(intent);
				}
			});
		} else {
			gridView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 10010 && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		} else if ((requestCode == 2) && resultCode == RESULT_OK) {
			imgUrl = SendFile.doPhoto(this, requestCode, data);
			Logger.i("path" + imgUrl);
			if (!StringUtil.isEmpty(imgUrl)) {
				new SendMsg().execute(imgUrl);
			}
		} else if ((requestCode == 3) && resultCode == RESULT_OK) {
			Logger.i("path" + imgUrl);
			if (!StringUtil.isEmpty(imgUrl)) {
				new SendMsg().execute(imgUrl);
			}
		} else if (requestCode == 20140212 && resultCode == RESULT_OK) {
			new GetInfo().execute();
		}
	}

	private class GetInfo extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					setInfo(DataRequest.getHashMapFromJSONObjectString(result.get("info")));
				} else {
					makeText(result.get("msg"));
				}
			} else {
				makeText("操作失败");
			}
		}
	}

	private class GetMessage extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				ImageView status = (ImageView) findViewById(R.id.off);
				if ("1".equals(result.get("code"))) {
					PreferenceUtil.setString(mContext, "status" + getUid(), result.get("status"));
					if ("1".equals(result.get("status"))) {
						status.setBackgroundResource(R.drawable.switchoff);
					} else {
						status.setBackgroundResource(R.drawable.switchon);
					}
				} else {
					makeText(result.get("msg"));
				}
			}
		}
	}

	private class Delete extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					setResult(RESULT_FIRST_USER);
					finish();
				} else {
					makeText(result.get("msg"));
				}
			}
		}
	}

	private class SendMsg extends AsyncTask<String, Void, String> {

		private String heString = "";

		@Override
		protected void onPreExecute() {
			mLoadBar.setMessage("上传头像...");
			mLoadBar.show();
		}

		@Override
		protected String doInBackground(String... params) {
			Logger.i("sfsd" + params[0]);
			String string = DataRequest.SendFile(AppConfig.SEND_HEAD_IMG + "cut=2", "ufile", params[0]);

			if (!StringUtil.isEmpty(string)) {
				HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(string);
				if (map != null) {
					heString = map.get("uploadFileUrl_b");
					return null;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!StringUtil.isEmpty(result)) {
				result = result.replace("(", "").replace(")", "");
				HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(result);
				if ("1".equals(map.get("code"))) {
					ImageView head = (ImageView) findViewById(R.id.head);
					LoadBitmap.getIntence().loadImage(heString, head);
					makeText("上传头像成功");
				}

				Logger.i("---" + result.toString());
			} else {
				makeText("上传头像失败");
			}

			if (mLoadBar != null) {
				mLoadBar.dismiss();
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (!StringUtil.isEmpty(imgUrl)) {
			setResult(RESULT_OK);
			finish();
		} else {
			super.onBackPressed();
		}

	}
}
