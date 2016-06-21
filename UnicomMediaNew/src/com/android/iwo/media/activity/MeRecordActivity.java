package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.R;
import com.android.iwo.media.view.XListView;
import com.android.iwo.media.view.XListView.IXListViewListener;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.NetworkUtil;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MeRecordActivity extends BaseActivity {
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private XListView listview;
	private IwoAdapter adapter;
	private TextView edit;
	private boolean isEdit = false;
	private boolean isOne = true;// 第一次加载
	private boolean isData = false;
	private boolean isDelete = true;// 控制删除完一次在到下一次
	private boolean isToast = true;
	private LinearLayout bootom;
	private HashSet<String> hash_tell = new HashSet<String>(); // 装id
	private LinearLayout att_bottom;
	private long lastTimemGoToPlayListListener;// 频繁点之
	private static long CLICK_INTERVAL = 600;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_x_listview);
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

	@SuppressLint("ResourceAsColor")
	private void init() {
		bootom = (LinearLayout) findViewById(R.id.bootom);
		att_bottom = (LinearLayout) findViewById(R.id.att_bottom);
		edit = (TextView) findViewById(R.id.title_edit);
		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isEdit) {
					hash_tell.clear();
					edit.setText("编辑");
					findViewById(R.id.me_bottom_all).setBackgroundResource(
							R.drawable.me_b_z);
					findViewById(R.id.me_bottom_delete).setBackgroundResource(
							R.drawable.me_b_w);
					att_bottom.setVisibility(View.GONE);
				} else {
					edit.setText("完成");
					att_bottom.setVisibility(View.VISIBLE);
				}
				isEdit = !isEdit;
				adapter.notifyDataSetChanged();
			}
		});
		// 全选
		findViewById(R.id.me_bottom_all).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						long time = System.currentTimeMillis();
						if (time - lastTimemGoToPlayListListener < CLICK_INTERVAL) {
							return;
						}
						lastTimemGoToPlayListListener = time;
						findViewById(R.id.me_bottom_all).setBackgroundResource(
								R.drawable.me_b_z);
						findViewById(R.id.me_bottom_delete)
								.setBackgroundResource(R.drawable.me_b_w);
						for (int i = 0; i < mData.size(); i++) {
							String str_id = mData.get(i).get("id");
							if (hash_tell.contains(str_id)) {
								continue;
							}
							hash_tell.add(str_id);
						}
						adapter.notifyDataSetChanged();

					}
				});
		findViewById(R.id.me_bottom_delete).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						findViewById(R.id.me_bottom_all).setBackgroundResource(
								R.drawable.me_b_w);
						findViewById(R.id.me_bottom_delete)
								.setBackgroundResource(R.drawable.me_b_z);
						if (hash_tell.size() > 0) {
							if (isDelete) {
								isDelete = false;
								mLoadBar.setMessage("正在删除...请稍候!");
								mLoadBar.show();
								ArrayList<String> list = new ArrayList<String>(
										hash_tell);
								StringBuilder sb = new StringBuilder();
								for (int i = 0; i < list.size(); i++) {
									String idString = list.get(i);
									sb.append(idString);
									sb.append(",");
								}
								String idS = sb.deleteCharAt(sb.length() - 1)
										.toString();
								new deleteHistory().execute(idS);
							}
						} else {
							makeText("请最少选择一个浏览记录");
						}
					}
				});
		setBack_text(null);
		setTitle("最近观看");
		listview = (XListView) findViewById(R.id.view_my_chase_layout_list);
		adapter = new IwoAdapter(this, mData) {

			private viewHold hold;

			@Override
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null) {
					hold = new viewHold();
					LayoutInflater inflater = LayoutInflater.from(mContext);
					v = inflater.inflate(R.layout.item_merecord, parent, false);
					hold.img_leat = (ImageView) v.findViewById(R.id.img_leat);
					hold.title = (TextView) v.findViewById(R.id.vi_title);
					hold.vi_details = (TextView) v
							.findViewById(R.id.vi_details);
					hold.img_yuan = (ImageView) v.findViewById(R.id.img_yuan);
					v.setTag(hold);
				} else {
					hold = (viewHold) v.getTag();
				}
				HashMap<String, String> map = mData.get(position);
				if (!StringUtil.isEmpty(map.get("video_img"))) {
					LoadBitmap.getIntence().loadImage(map.get("video_img"),
							hold.img_leat);
				} else {
					BitmapUtil.setImageResource(hold.img_leat,
							R.drawable.a190_190);
				}
				hold.title.setText(map.get("video_name"));
				hold.vi_details.setText(map.get("reason"));
				if (isEdit) {
					hold.img_yuan.setVisibility(View.VISIBLE);
				} else {
					hold.img_yuan.setVisibility(View.GONE);
				}
				if (hash_tell.contains(map.get("id"))) {
					hold.img_yuan
							.setBackgroundResource(R.drawable.me_item_yuan_z);
				} else {
					hold.img_yuan
							.setBackgroundResource(R.drawable.me_item_yuan_w);
				}
				return v;
			}

		};
		listview.setAdapter(adapter);
		listview.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				isOne = true;
				new GetData().execute();
				Logger.i("下拉涮新完成");
			}
		});
		listview.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if (mData.size() > 0) {
							if (mData.size() % 10 != 0) {
								makeText("没有更多记录");
								return;
							}
							if (!isData) {
								makeText("没有更多记录");
								return;
							}
							bootom.setVisibility(View.VISIBLE);
							isOne = false;
							new GetData().execute();
						}
					}
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				String id = null;
				if (isEdit) {
					id = mData.get(pos - listview.getHeaderViewsCount()).get(
							"id");
					if (hash_tell.contains(id)) {
						hash_tell.remove(id);
					} else {
						hash_tell.add(id);
					}
					adapter.notifyDataSetChanged();
				} else {
					id = mData.get(pos - listview.getHeaderViewsCount()).get(
							"video_id");
					Intent intent = new Intent(mContext,
							VideoDetailActivity_new.class);
					intent.putExtra("video_id", id);
					startActivity(intent);
				}

			}
		});
	}

	private class GetData extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		private int size_ll;

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			if (isOne) {

				return DataRequest
						.getHashMapFromUrl_Base64(
								getUrl(AppConfig.NEW_FR_USER_INFO_NIGHT_GET_HEAD_HISTORY),
								"10", "1");
			}
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_FR_USER_INFO_NIGHT_GET_HEAD_HISTORY),
					"10", getStart(mData.size()));

		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			mLoadBar.dismiss();
			listview.stopRefresh();
			if (bootom.getVisibility() == View.VISIBLE) {
				bootom.setVisibility(View.GONE);
			}
			if (result != null && "1".equals(result.get("code"))) {
				ArrayList<HashMap<String, String>> list = DataRequest
						.getArrayListFromJSONArrayString(result.get("data"));
				if (!StringUtil.isEmpty(list) && list.size() > 0) {
					isData = true;
					findViewById(R.id.moren_mychase_i).setVisibility(View.GONE);
					findViewById(R.id.view_my_chase_xian).setVisibility(
							View.GONE);
					listview.setVisibility(View.VISIBLE);
					edit.setVisibility(View.VISIBLE);
					if (isOne) {
						mData.clear();
					}
					mData.addAll(list);
					size_ll = mData.size();
					Logger.i("共几个浏览记录" + size_ll);

					adapter.notifyDataSetChanged();
				} else if ("-1".equals(result.get("code"))) {
					makeText("没登陆，请重新退出登录");
				}
			} else {
				isData = false;
				if (isOne) {
					ImageView moren_mychase_i = (ImageView) findViewById(R.id.moren_mychase_i);
					moren_mychase_i.setVisibility(View.VISIBLE);
					Bitmap bitmap = android.graphics.BitmapFactory
							.decodeResource(getResources(),
									R.drawable.moren_mychase);
					int screenWidth = PreferenceUtil.getInt(
							MeRecordActivity.this, "screenWidth", 720);
					if (bitmap != null) {
						Bitmap bitmap2 = BitmapUtil.getBitmap(bitmap,
								screenWidth - 36,
								(screenWidth - 36) * 190 / 318);
						if (bitmap2 != null) {
							BitmapUtil.setImageBitmap(moren_mychase_i, bitmap2);
						}
					}
					listview.setVisibility(View.GONE);
					edit.setVisibility(View.GONE);
					att_bottom.setVisibility(View.GONE);
					findViewById(R.id.view_my_chase_xian).setVisibility(
							View.VISIBLE);
					if (isToast) {
						makeText("没有浏览记录");
					}
				}
			}

		}
	}

	private class viewHold {
		ImageView img_leat, img_yuan;
		TextView title, vi_details;
	}

	private class deleteHistory extends
			AsyncTask<String, Void, HashMap<String, String>> {

		protected HashMap<String, String> doInBackground(String... params) {
			String id = params[0];
			Logger.i("需要删除的id" + id);
			return DataRequest
					.getHashMapFromUrl_Base64(
							getUrl(AppConfig.NEW_FR_USER_INFO_NIGHT_DELETE_HEAD_HISTORY),
							params[0]);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			Logger.i(result.toString());
			mLoadBar.dismiss();
			isDelete = true;
			if (result != null && "1".equals(result.get("code"))) {
				makeText("删除成功");
				isOne = true;
				isToast = false;
				hash_tell.clear();
				new GetData().execute();
			} else {
				makeText(result.get("msg"));
			}
		}
	}
}
