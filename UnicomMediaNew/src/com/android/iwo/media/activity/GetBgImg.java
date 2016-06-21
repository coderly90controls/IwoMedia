package com.android.iwo.media.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.*;
import com.android.iwo.media.view.CommonDialog;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;

public class GetBgImg extends BaseActivity {
	protected DisplayMetrics dm;
	GridView content;
	protected float scale = 0;
	private ArrayList<Bitmap> mData = new ArrayList<Bitmap>();
	private int isSelect = 0;
	private BaseAdapter mAdapter;
	int n;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gridview);
		mLoadBar = new CommonDialog(this, "加载数据");
		mLoadBar.show();
		PreferenceUtil.getString(mContext, "user_bg_img_id", "0");
		new GetData().execute();
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		scale = getResources().getDisplayMetrics().density;
		setBack(null);
		init();

	}

	private void init() {
		String user_bg_id = PreferenceUtil.getString(GetBgImg.this,
				"user_bg_img_id", "-1");
		if (!user_bg_id.equals("-1")) {
			n = Integer.valueOf(user_bg_id);
			n -= 1;
			isSelect = n;
		}

		setTitle("更换主页封面");
		ImageView msg = (ImageView) findViewById(R.id.right_img);
		msg.setVisibility(View.VISIBLE);
		msg.setImageResource(R.drawable.yes);
		msg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mData.size() == 0 || isSelect == -1) {
					makeText("请选择需要的背景");
				} else {

					new ChangeImg().execute();
				}

			}
		});
	}

	private void initView() {
		content = (GridView) findViewById(R.id.content);
		mAdapter = new BaseAdapter() {
			@Override
			public View getView(final int position, View v, ViewGroup parent) {
				// TODO Auto-generated method stub
				if (v == null) {
					v = View.inflate(GetBgImg.this, R.layout.getbg_list_item,
							null);
				}
				if (position == n) {
					v.findViewById(R.id.img_1).setVisibility(View.VISIBLE);
				} else {
					v.findViewById(R.id.img_1).setVisibility(View.GONE);
				}

				ImageView imageView = (ImageView) v.findViewById(R.id.img);
				// setImgSize(imageView, 0, 0.5f, 3);
				imageView.setImageBitmap(mData.get(position));
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						n = position;
						setSelect(n);
						mAdapter.notifyDataSetChanged();
					}
				});

				return v;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public Bitmap getItem(int position) {
				// TODO Auto-generated method stub
				return mData.get(position);
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mData.size();
			}
		};

		content.setAdapter(mAdapter);
	}

	private void setSelect(int n) {
		isSelect = n;
	}

	@Override
	protected void setBack(OnClickListener listener) {
		if (listener != null)
			setViewGone(R.id.left_img, false).setOnClickListener(listener);
		else {
			setViewGone(R.id.left_img, false).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (isSelect != -1) {
								if (mData.size() > 1) {
									Bitmap img_bg_src = mData.get(isSelect);
									if (img_bg_src != null) {
										setResult(Activity.RESULT_OK);
										finish();
									} else {
										finish();
									}

								} else {
									finish();
								}
							} else {
								finish();
							}

						}
					});
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (!(isSelect == -1)) {
				Bitmap img_bg_src = mData.get(isSelect);
				if (img_bg_src != null) {
					setResult(Activity.RESULT_OK);
					finish();
					return true;
				} else {
					finish();
				}
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private class GetData extends AsyncTask<Void, Void, ArrayList<Bitmap>> {

		@Override
		protected ArrayList<Bitmap> doInBackground(Void... params) {
			ArrayList<Bitmap> imgData = new ArrayList<Bitmap>();
			for (int i = 1; i <= 9; i++) {
				try {
					imgData.add(BitmapFactory.decodeStream(getAssets().open(
							"wallpaper" + i + ".jpg")));
					Logger.i("  -------  " + "i");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return imgData;

		}

		@Override
		protected void onPostExecute(ArrayList<Bitmap> result) {
			if (result != null) {
				Logger.i(" asdasd " + result.toString());
				mData.addAll(result);
				initView();
			}
			mLoadBar.dismiss();
		}
	}

	private class ChangeImg extends
			AsyncTask<String, Void, HashMap<String, String>> {
		@Override
		protected void onPreExecute() {
			mLoadBar.setMessage("上传背景图片...");
			mLoadBar.show();
		}

		@Override
		protected HashMap<String, String> doInBackground(String... params) {

			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_V_BACKGROUND_SAVE), ""
							+ (isSelect + 1));
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			mLoadBar.dismiss();
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					makeText("添加背景图成功");
					PreferenceUtil.setString(GetBgImg.this, "user_bg_img_id",
							"" + (isSelect + 1));
					setResult(Activity.RESULT_OK);
					finish();
				} else {
					makeText("操作失败");
				}
			} else {
				makeText("操作失败");
			}

		}
	}

}
