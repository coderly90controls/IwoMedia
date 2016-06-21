package com.android.iwo.media.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.iwo.media.activity.AttentionListActivity;
import com.android.iwo.media.activity.FriendDetail;
import com.android.iwo.media.activity.GetBgImg;
import com.android.iwo.media.activity.MyHomePageActivity;
import com.android.iwo.media.activity.UserInfoEdit;
import com.android.iwo.media.activity.VideoDetailActivity;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.view.IwoToast;
import com.android.iwo.media.view.XListView;
import com.android.iwo.media.view.XListView.IXListViewListener;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.ACache;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.DateUtil;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.LoadBitmap.LoadBitmapCallBack;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.SendFile;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 朋友圈
 */
public class View3 extends ViewTab implements OnClickListener {
	String path;
	private View listViewHead;
	public Context mContext;
	protected XListView listView;
	protected ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private HashMap<String, String> userData = new HashMap<String, String>();
	private boolean scrollOn = true;
	private LinearLayout camel;
	private String uid, up, mark = "2";// mark用来判定上传头像的时候是白天模式上传还是黑夜模式上传。1为白天，2为晚上。
	private final int REQUEST_FRIENDDETAIL = 5191819;
	private boolean dataResult = true;
	ACache mCache;

	public View3(Context context) {
		mContext = context;
		mCache = ACache.get(mContext);
		view = View.inflate(mContext, R.layout.view3_layout, null);
		camel = (LinearLayout) ((Activity) mContext).findViewById(R.id.camer);
		camel = (LinearLayout) ((Activity) mContext).findViewById(R.id.camer);
		init();
	}

	public void setCamel(final Activity activity) {
		LinearLayout btn1 = (LinearLayout) activity.findViewById(R.id.nor);
		LinearLayout btn2 = (LinearLayout) activity.findViewById(R.id.model);
		LinearLayout btn3 = (LinearLayout) activity.findViewById(R.id.cancle);
		TextView btn_text1 = (TextView) activity.findViewById(R.id.nor_text);
		TextView btn_text2 = (TextView) activity.findViewById(R.id.model_text);
		btn_text1.setText("照片拍摄");
		btn_text2.setText("图库选择");
		uid = PreferenceUtil.getString(mContext, "user_id", "");
		up = PreferenceUtil.getString(mContext, "user_name", "");
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.nor:
					path = SendFile.takePhoto((Activity) mContext, 1);
					camel.setVisibility(View.GONE);
					break;
				case R.id.model:
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					activity.startActivityForResult(intent, 11);// 2
					camel.setVisibility(View.GONE);
					break;
				case R.id.cancle:
					camel.setVisibility(View.GONE);
					break;
				default:
					break;
				}
			}
		};
		btn1.setOnClickListener(listener);
		btn2.setOnClickListener(listener);
		btn3.setOnClickListener(listener);
	}

	protected void init() {
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		listViewHead = View.inflate(mContext, R.layout.view3_layout_head, null);
		listView = (XListView) view.findViewById(R.id.view3_layout_list);
		listView.addHeaderView(listViewHead);
		new GetUserData().execute();
		new GetData().execute();

		mAdapter = new IwoAdapter((Activity) mContext, mData) {

			@Override
			public View getView(int position, View v, ViewGroup parent) {
				View view = v;
				final ViewHolder holder;
				if (view == null) {
					view = mInflater.inflate(R.layout.view3_layout_item, null);
					holder = new ViewHolder();
					// TextView name, time, synopsis, comments_text, share_text,
					// play_text;
					// ImageView head, item_img, comments, play, share;
					holder.name = (TextView) view.findViewById(R.id.name);
					holder.time = (TextView) view.findViewById(R.id.time);
					holder.synopsis = (TextView) view.findViewById(R.id.synopsis);
					holder.comments_text = (TextView) view.findViewById(R.id.comments_text);
					holder.share_text = (TextView) view.findViewById(R.id.share_text);
					holder.play_text = (TextView) view.findViewById(R.id.play_text);

					holder.head = (ImageView) view.findViewById(R.id.head);
					holder.item_img = (ImageView) view.findViewById(R.id.item_img);
					holder.comments = (ImageView) view.findViewById(R.id.comments);
					holder.play = (ImageView) view.findViewById(R.id.play);
					holder.share = (ImageView) view.findViewById(R.id.share);
					view.setTag(holder);
				} else {
					holder = (ViewHolder) view.getTag();
				}
				if (mData.size() > 0) {
					Map<String, String> map = mData.get(position);
					holder.head.setTag(map.get("head_img"));
					LoadBitmap bitmap = new LoadBitmap();
					if (!StringUtil.isEmpty(holder.head.getTag().toString())) {
						bitmap.loadImage((String) holder.head.getTag(), new LoadBitmapCallBack() {

							@Override
							public void callBack(Bitmap bit) {
								if (bit != null) {
									holder.head.setImageBitmap(BitmapUtil.toRoundCorner(bit, 91));
								}
							}
						});
						holder.head.setBackgroundColor(mContext.getResources().getColor(R.color.comm_bg_color));
					} else {
						holder.head.setBackgroundResource(R.drawable.ico_default);
					}

					setItem(holder.name, StringUtil.isEmpty(map.get("note_name")) ? map.get("nick_name") : map.get("note_name"));

					if (!StringUtil.isEmpty(map.get("create_time"))) {
						setItem(holder.time, DateUtil.format("MM-dd HH:mm", "" + DateUtil.getTime("yyyy-MM-dd HH:mm:ss", map.get("create_time"))));
					}

					holder.time.setTextColor(mContext.getResources().getColor(R.color.comm_green_color));
					holder.head.setTag(position);
					holder.head.setOnClickListener(View3.this);
					setItem(holder.synopsis, map.get("video_name"));

					bitmap.loadImage(map.get("img_url_2"), holder.item_img);
					// setImgSize(holder.item_img, 64, 169 / 300.0f, 1);
					holder.item_img.setTag(position);
					holder.item_img.setOnClickListener(View3.this);
					setItem(holder.comments_text, StringUtil.isEmpty(map.get("ping_count")) ? "0" : map.get("ping_count"));
					setItem(holder.share_text, StringUtil.isEmpty(map.get("share_count")) ? "0" : map.get("share_count"));
					setItem(holder.play_text, StringUtil.isEmpty(map.get("play_count")) ? "0" : map.get("play_count"));

					int[] d_img = { R.drawable.d_pinglun, R.drawable.d_bofang, R.drawable.d_fenxiang };
					int[] n_img = { R.drawable.n_pinglun, R.drawable.n_bofang, R.drawable.n_fenxiang };

					ArrayList<ImageView> list = new ArrayList<ImageView>();
					list.add(holder.comments);
					list.add(holder.play);
					list.add(holder.share);
					setItemViewImg(list, d_img, n_img);

				}

				return view;
			}

		};
		listView.setCacheColorHint(0);
		listView.setDividerHeight(0);
		listView.setAdapter(mAdapter);

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					View lastItemView = (View) listView.getChildAt(listView.getChildCount() - 1);
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if (listView.getBottom() == lastItemView.getBottom()) {
							if (scrollOn) {
								scrollOn = false;
								new GetData().execute();
							} else {
								if (mData.size() < 10) {
									IwoToast.makeText(mContext, "没有更多内容").show();
								} else {
									if (dataResult) {
										IwoToast.makeText(mContext, "加载更多中").show();
									} else {
										IwoToast.makeText(mContext, "没有更多内容").show();
									}

								}

							}
						}

					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int first, int visible, int total) {
			}
		});
		listView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				isrefresh = true;
				new GetData().execute();
			}
		});
	}

	static class ViewHolder {
		TextView name, time, synopsis, comments_text, share_text, play_text;
		ImageView head, item_img, comments, play, share;
	}

	private void setUser() {
		final ImageView user_imgImageView = (ImageView) listViewHead.findViewById(R.id.view3_layout_user_img);
		LoadBitmap bitmap = new LoadBitmap();
		if (!StringUtil.isEmpty(userData.get("head_img"))) {

			bitmap.loadImage(userData.get("head_img"), new LoadBitmapCallBack() {

				@Override
				public void callBack(Bitmap bit) {
					if (bit != null) {
						user_imgImageView.setImageBitmap(BitmapUtil.toRoundCorner(bit, 1));
					}
				}

			});

		}
		setViewonClick(listViewHead, R.id.view3_layout_user_img);
		ImageView layout_head_img = (ImageView) listViewHead.findViewById(R.id.view3_layout_head_img);
		String i = PreferenceUtil.getString(mContext, "user_bg_img_id", "1");
		try {
			setImgSize(layout_head_img, 0, 7 / 12.0f, 1);
			layout_head_img.setImageBitmap(BitmapFactory.decodeStream(mContext.getAssets().open("wallpaper" + i + ".jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		TextView user_nameTextView = (TextView) listViewHead.findViewById(R.id.view3_layout_user_name);
		user_nameTextView.setText(userData.get("nick_name"));
		if ("day".equals(getMode())) {
			setViewGone(listViewHead, R.id.view3_layout_signature);
			setViewGone(listViewHead, R.id.view3_layout_signature_content);
			setViewGone(listViewHead, R.id.user_sex_img);
			setViewGone(listViewHead, R.id.user_age_text);
			setViewGone(listViewHead, R.id.n_head_bottom_layout);
			layout_head_img.setOnClickListener(this);
		} else {
			layout_head_img.setOnClickListener(this);
			setViewShow(listViewHead, R.id.view3_layout_signature);
			TextView content = (TextView) setViewShow(listViewHead, R.id.view3_layout_signature_content);
			ImageView sex = (ImageView) setViewShow(listViewHead, R.id.user_sex_img);
			content.setText(userData.get("sign"));
			if ("1".equals(userData.get("sex"))) {
				sex.setImageResource(R.drawable.boy);
			} else if ("2".equals(userData.get("sex"))) {
				sex.setImageResource(R.drawable.girl);
			} else {
				sex.setImageResource(R.drawable.boy);
			}
			TextView user_age_text = (TextView) setViewShow(listViewHead, R.id.user_age_text);
			if (StringUtil.isEmpty(userData.get("age"))) {
				user_age_text.setText("0");
			} else {
				user_age_text.setText(userData.get("age"));
			}

			setViewShow(listViewHead, R.id.n_head_bottom_layout);
			setViewonClick(listViewHead, R.id.edit_data);
			setViewonClick(listViewHead, R.id.friends);
			setViewonClick(listViewHead, R.id.attention);
			setViewonClick(listViewHead, R.id.fans);
		}
	}

	@Override
	public void onClick(View v) {
		int position;
		Map<String, String> map = null;
		Intent intent = null;
		switch (v.getId()) {
		case R.id.head: // 用户头像
			position = (Integer) v.getTag();
			map = mData.get(position);
			if (!StringUtil.isEmpty(map.get("user_id")) && !map.get("user_id").equals(PreferenceUtil.getString(mContext, "user_id", ""))) {
				intent = new Intent(mContext, FriendDetail.class);
				intent.putExtra("id", map.get("user_id"));
				intent.putExtra("name", map.get("user_name"));
				((Activity) mContext).startActivityForResult(intent, REQUEST_FRIENDDETAIL);
			}

			break;
		case R.id.item_img: // 视频图片
			position = (Integer) v.getTag();
			map = mData.get(position);
			intent = new Intent(mContext, VideoDetailActivity.class);
			intent.putExtra("video_id", map.get("video_id"));
			intent.putExtra("nickname", map.get("nickname"));
			intent.putExtra("create_time", map.get("create_time"));
			intent.putExtra("head_img", map.get("head_img"));
			((Activity) mContext).startActivityForResult(intent, 5231627);
			break;
		case R.id.view3_layout_head_img:
			intent = new Intent(mContext, GetBgImg.class);
			((Activity) mContext).startActivityForResult(intent, AppConfig.REQUEST_GETBGIMG);
			break;
		case R.id.view3_layout_user_img:
			if ("day".equals(getMode())) {
				intent = new Intent(mContext, MyHomePageActivity.class);
				((Activity) mContext).startActivityForResult(intent, 20140211);
			} else {
				camel.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.edit_data:// 黑夜模式，编辑资料
			intent = new Intent(mContext, UserInfoEdit.class);
			mContext.startActivity(intent);
			break;
		case R.id.friends:// 好友
			/*
			 * intent = new Intent(mContext, FriendsListActivity.class);
			 * intent.putExtra("attention", "0");
			 * mContext.startActivity(intent);
			 */
			break;
		case R.id.attention:// 关注
			intent = new Intent(mContext, AttentionListActivity.class);
			intent.putExtra("attention", "1");
			mContext.startActivity(intent);
			break;
		case R.id.fans:// 粉丝
			intent = new Intent(mContext, AttentionListActivity.class);
			intent.putExtra("attention", "2");
			mContext.startActivity(intent);
			break;
		default:
			break;
		}

	}

	@Override
	protected ArrayList<HashMap<String, String>> doInBack(String... params) {
		String str = "";
		// if ("day".equals(getMode())) {
		// 白天模式访问的接口
		if (isrefresh) {
			str = DataRequest.getStringFromURL_Base64(getUrl(AppConfig.NEW_V_GET_FRIENDS_VIDEO), "1", "10");

		} else {
			str = DataRequest.getStringFromURL_Base64(getUrl(AppConfig.NEW_V_GET_FRIENDS_VIDEO), getStart(mData.size()), "10");
		}
		if (!StringUtil.isEmpty(str)) {
			HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(str);
			if (isrefresh) {
				if (map != null) {
					mCache.put(Constants.FRIENDS_CIRCLE_VIDEO, map.get("data"));
				}
			}
			Logger.i("朋友圈数据：" + map);
			if ("1".equals(map.get("code")) && map != null) {
				return DataRequest.getArrayListFromJSONArrayString(map.get("data"));
			}
		}

		return null;
		// } else {
		// // 夜晚模式 访问的接口
		// if (isrefresh) {
		// str =
		// DataRequest.getStringFromURL_Base64(AppConfig.NEW_V_VIDEO_MY_LIST +
		// AppConfig.END_NIGHT, "1", "10");
		// } else {
		// str = DataRequest
		// .getStringFromURL_Base64(AppConfig.NEW_V_VIDEO_MY_LIST +
		// AppConfig.END_NIGHT, getStart(mData.size()), "10");
		// }
		// return DataRequest.getArrayListFromJSONArrayString(str);
		// }
	}

	@Override
	protected void onPostExe(ArrayList<HashMap<String, String>> result) {
		// Logger.i(result.toString());
		if (null != result) {
			scrollOn = true;
			dataResult = true;
			if (result.size() < 10) {
				scrollOn = false;
				dataResult = false;
			}
			if (isrefresh) {
				mData.clear();
				scrollOn = true;
			}
			mData.addAll(result);
			mAdapter.notifyDataSetChanged();

		} else {
			if (isrefresh) {
				mData.clear();

			}
		}
		if (isrefresh) {
			String friendsData = mCache.getAsString(Constants.FRIENDS_CIRCLE_VIDEO);
			if (!StringUtil.isEmpty(friendsData)) {
				if (result == null) {
					mData.addAll(DataRequest.getArrayListFromJSONArrayString(friendsData));
					mAdapter.notifyDataSetChanged();
					listView.stopRefresh();
				}

			} else {
				listView.stopRefresh();
			}

			isrefresh = false;
		}

	}

	/**
	 * 获取用户信息
	 * 
	 * @author hanpengyuan
	 * 
	 */
	private class GetUserData extends AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			HashMap<String, String> result = DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_MY_INFO));
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					mCache.put(Constants.USER_INIT_DATA, result.get("data"));
					return DataRequest.getHashMapFromJSONObjectString(result.get("data"));
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				Logger.i("用户数据：" + result.toString());
				userData.putAll(result);
				view.setVisibility(View.VISIBLE);
				setUser();
			} else {
				String user = mCache.getAsString(Constants.USER_INIT_DATA);
				if (!StringUtil.isEmpty(user)) {
					userData.putAll(DataRequest.getHashMapFromJSONObjectString(user));
					view.setVisibility(View.VISIBLE);
					setUser();
				}
			}
		}
	}

	protected String getMode() {
		return PreferenceUtil.getString(mContext, "video_model", "");
	}

	protected void setMode_Image(View fatherView, int imageID, int d_pictureID, int n_pictureID) {
		String mode = getMode();
		ImageView img = (ImageView) fatherView.findViewById(imageID);
		if ("day".equals(mode)) {
			img.setBackgroundResource(d_pictureID);
		} else if ("night".equals(mode)) {
			img.setBackgroundResource(n_pictureID);

		}
	}

	public void setHeadImgBG() {
		ImageView layout_head_img = (ImageView) listViewHead.findViewById(R.id.view3_layout_head_img);
		String i = PreferenceUtil.getString(mContext, "user_bg_img_id", "1");
		try {
			setImgSize(layout_head_img, 0, 7 / 12.0f, 1);
			layout_head_img.setImageBitmap(BitmapFactory.decodeStream(mContext.getAssets().open("wallpaper" + i + ".jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private View setViewShow(View v, int viewID) {
		View childV = v.findViewById(viewID);
		childV.setVisibility(View.VISIBLE);
		return childV;
	}

	private void setViewGone(View v, int viewID) {
		v.findViewById(viewID).setVisibility(View.GONE);
	}

	private void setViewonClick(View v, int viewID) {
		v.findViewById(viewID).setOnClickListener(this);
	}

	private void setItemViewImg(View v, int[] viewID, int[] d_img, int[] n_img) {
		int[] img;
		if ("day".equals(getMode())) {
			img = d_img;
		} else {
			img = n_img;
		}
		for (int i = 0; i < viewID.length; i++) {
			ImageView view = (ImageView) v.findViewById(viewID[i]);
			view.setImageResource(img[i]);
		}
	}

	private void setItemViewImg(List<ImageView> list, int[] d_img, int[] n_img) {
		int[] img;
		if ("day".equals(getMode())) {
			img = d_img;
		} else {
			img = n_img;
		}
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setImageResource(img[i]);
		}
	}

	/**
	 * 
	 * @param requestCode
	 *            请求参数
	 * @param resultCode
	 *            返回参数
	 * @param data
	 *            intent返回的
	 */
	public void setUserLoginHeadImg(int requestCode, int resultCode, Intent data) {
		if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
			String str = SendFile.doPhoto((Activity) mContext, 2, data);
			Logger.i("path  :" + str);
			if (!StringUtil.isEmpty(str)) {
				new SendMsg().execute(str);
			}
		} else if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
			Logger.i("path  :" + path);
			if (!StringUtil.isEmpty(path)) {
				new SendMsg().execute(path);
			}
		}
	}

	/**
	 * 
	 * @author hanpengyuan
	 * 
	 */
	private class SendMsg extends AsyncTask<String, Void, String> {
		private String heString = "";

		@Override
		protected void onPreExecute() {
			IwoToast.makeText(mContext, "上传头像...,请耐心等待").show();
		}

		@Override
		protected String doInBackground(String... params) {
			Logger.i("sfsd" + params[0]);
			String string = DataRequest.SendFile(getUrl(AppConfig.SEND_HEAD_IMG) + "cut=2", "ufile", params[0]);

			if (!StringUtil.isEmpty(string)) {
				HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(string);
				if (map != null) {
					Logger.i("---" + map.toString());
					heString = map.get("uploadFileUrl_b");
					return DataRequest.getStringFromURL_Base64(getUrl(AppConfig.SEND_IMG_CUT_TMP) + "&uid=" + uid + "&up=" + up, heString, mark);
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
					ImageView head = (ImageView) listViewHead.findViewById(R.id.view3_layout_user_img);
					LoadBitmap.getIntence().loadImage(heString, head);
					IwoToast.makeText(mContext, "上传头像成功").show();
				}
				Logger.i("---" + result.toString());
			} else {
				IwoToast.makeText(mContext, "上传头像失败").show();
			}

		}
	}

	public void refreshUser() {
		isrefresh = true;
		new GetUserData().execute();
		new GetData().execute();
	}

	protected String getUrl(String url) {
		return AppConfig.GetUrl(mContext, url);
	}
}
