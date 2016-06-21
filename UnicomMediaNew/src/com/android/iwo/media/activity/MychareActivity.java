package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.R;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.NetworkUtil;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class MychareActivity extends BaseActivity {
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private MyAdater adater;
	private GridView gr;
	private TextView edit;
	private boolean isEdit = false, onClikright_textview = true;
	private boolean isOne = true;
	private int screenWidth;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mychare);
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		if (NetworkUtil.isWIFIConnected(mContext)) {
			new GetData().execute();
		} else {
			mLoadBar.dismiss();
			makeText("请检查网络");
		}
		init();
	}

	private void init() {
		screenWidth = PreferenceUtil.getInt(mContext, "screenWidth", 100);
		edit = (TextView) findViewById(R.id.title_edit);
		setBack_text(null);
		setTitle("我的收藏");
		gr = (GridView) findViewById(R.id.grid_view);
		adater = new MyAdater();
		gr.setAdapter(adater);
		gr.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				String id = mData.get(pos).get("video_id");
				if (isEdit) {
					Logger.i("删除的ID号：" + id);
					if (onClikright_textview) {
						onClikright_textview = false;
						new setVideoChaesOr().execute(id);
					}
				} else {
					Intent intent = new Intent(mContext,
							VideoDetailActivity_new.class);
					intent.putExtra("video_id", id);
					startActivityForResult(intent, 119);
				}
			}
		});
		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isEdit) {
					edit.setText("编辑");
				} else {
					edit.setText("完成");
				}
				isEdit = !isEdit;
				adater.notifyDataSetChanged();
			}
		});

	}

	private class GetData extends
			AsyncTask<String, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(String... params) {

			return DataRequest
					.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_UN_GET_SHARE_LIST_NEW_STRING),"1",Integer.MAX_VALUE+"");
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			super.onPostExecute(result);
			mLoadBar.dismiss();
			if (result != null && "1".equals(result.get("code"))) {
				ArrayList<HashMap<String, String>> list = DataRequest
						.getArrayListFromJSONArrayString(result.get("data"));
				if (list != null && list.size() > 0) {
					isOne = false;
					edit.setVisibility(View.VISIBLE);
					findViewById(R.id.moren_mychase_i).setVisibility(View.GONE);
					gr.setVisibility(View.VISIBLE);
					mData.clear();
					mData.addAll(list);
					adater.notifyDataSetChanged();
				}
			} else {
				edit.setVisibility(View.GONE);
				ImageView moren_mychase_i = (ImageView) findViewById(R.id.moren_mychase_i);
				moren_mychase_i.setVisibility(View.VISIBLE);
				Bitmap bitmap = android.graphics.BitmapFactory.decodeResource(
						getResources(), R.drawable.moren_mychase);
				int screenWidth = PreferenceUtil.getInt(MychareActivity.this,
						"screenWidth", 720);
				if (bitmap != null) {
					Bitmap bitmap2 = BitmapUtil.getBitmap(bitmap,
							screenWidth - 40, (screenWidth - 40));
					if (bitmap2 != null) {
						BitmapUtil.setImageBitmap(moren_mychase_i, bitmap2);
					}
				}
				gr.setVisibility(View.GONE);
				if (isOne) {
					makeText("暂无收藏");
				}
			}
		}

	}

	private class MyAdater extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_gridview, null);
			}
			ImageView img = (ImageView) convertView
					.findViewById(R.id.discover_item_item_img);
			TextView item_text = (TextView) convertView
					.findViewById(R.id.discover_item_item_text);
			ImageView delete = (ImageView) convertView
					.findViewById(R.id.me_item_new_delete11);
			TextView pinfen = (TextView) convertView
					.findViewById(R.id.discover_item_item_pinfen);
			HashMap<String, String> map = mData.get(position);
			if (!StringUtil.isEmpty(map.get("video_img_x"))) {
				LoadBitmap.getIntence().loadImage(map.get("video_img_x"), img);
			} else {
				BitmapUtil.setBackgroundResource(img, R.drawable.souye_weizhi);
			}
			item_text.setText(map.get("video_name"));

			// android.view.ViewGroup.LayoutParams params =
			// img.getLayoutParams();
			// int width = (screenWidth - 40) / 3;
			// params.height = params.width;
			if (isEdit) {
				delete.setVisibility(View.VISIBLE);
			} else {
				delete.setVisibility(View.GONE);
			}
			if (!StringUtil.isEmpty(map.get("ping_fen"))) {
				pinfen.setVisibility(View.VISIBLE);
				pinfen.getBackground().setAlpha(90);
				pinfen.setText(map.get("ping_fen"));
			}
			return convertView;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 119) {
			new GetData().execute();
		}
	}

	private class setVideoChaesOr extends
			AsyncTask<String, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_UN_VIDEO_GET_SHARE), params[0], "0",null);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {

			if (result != null) {
				if ("1".equals(result.get("code"))) {
					makeText("取消收藏");
					new GetData().execute();
				}

			} else {
				makeText("取消失败");
			}
			onClikright_textview = true;
		}

	}

}
