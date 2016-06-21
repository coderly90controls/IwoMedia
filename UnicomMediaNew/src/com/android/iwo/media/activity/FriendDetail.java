package com.android.iwo.media.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.iwo.media.action.ActivityUtil;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.DBhelper;
import com.android.iwo.media.action.IwoSQLiteHelper;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.chat.XmppClient;
import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.view.IwoToast;
import com.android.iwo.media.view.XListView;
import com.android.iwo.media.view.XListView.IXListViewListener;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.DateUtil;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.LoadBitmap.LoadBitmapCallBack;
import com.test.iwomag.android.pubblico.util.LocationManage;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 需传递好友ID。
 * 
 * @author hanpengyuan
 * 
 */
public class FriendDetail extends BaseActivity implements OnClickListener {
	private XListView listView;
	private IwoAdapter mAdapter;
	private String id = "";
	private String name = "";
	private View listViewHead;
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private HashMap<String, String> userData = new HashMap<String, String>();
	private HashMap<String, String> userNote = new HashMap<String, String>();
	private HashMap<String, String> userRoster = new HashMap<String, String>();
	private String stringUserNote;
	protected boolean isrefresh = false;
	private String star, note_name;
	String attention;// 我的关系
	String friendAttention;// 我与 --- 好友的好友的关系。
	private boolean whetherBlackList = false;
	private boolean scrollOn = true;
	private DBhelper per;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_detail);
		id = getIntent().getStringExtra("id");
		name = getIntent().getStringExtra("name");
		if (!StringUtil.isEmpty(name)) {
			if (name.startsWith("video_")) {
				name = name.replace("video_", "");
				Logger.i(" 电话号码  " + name);
			}
		}
		per = new DBhelper(this, IwoSQLiteHelper.FRIEND_TAB);
		init();
		if ("day".equals(getMode())) {
			setTitleRightImg(R.drawable.more, this);
			attention = getIntent().getStringExtra("attention");
			if (!StringUtil.isEmpty(attention)) {
				if ("3".equals(attention)) {
					whetherBlackList = true;
					setImage(R.id.set_fiend_7_img, R.drawable.d_qx_heimingdan);
					setItem(R.id.set_fiend_7_text, "取消黑名单");
				}
			}
		} else {
			findViewById(R.id.video_comments_bottom).setVisibility(View.VISIBLE);
			findViewById(R.id.user_focus).setOnClickListener(this);
			findViewById(R.id.user_pull_black).setOnClickListener(this);
			attention = getIntent().getStringExtra("attention");
			// 0,我的好友 1，我的关注 （取消关注），2，我的粉丝（加关注）3,黑名单
			if ("1".equals(attention) || "0".equals(attention)) {
				// user_focus_tet
				setImage(R.id.user_focus_img, R.drawable.qx_guanzhu);
				setItem(R.id.user_focus_tet, "取消关注");
			}
			if ("3".equals(attention)) {

				whetherBlackList = true;
				setImage(R.id.user_pull_black_img, R.drawable.qx_lahei);
				setItem(R.id.user_pull_black_text, "取消拉黑");
			}

		}
		setBack(this);
		setOnClick();

	}

	private void init() {
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		listViewHead = View.inflate(this, R.layout.view3_layout_head, null);
		listView = (XListView) findViewById(R.id.list);
		TextView edit_data = (TextView) listViewHead.findViewById(R.id.edit_data);
		edit_data.setText("详细资料");
		listViewHead.setVisibility(View.GONE);
		listView.addHeaderView(listViewHead);
		new Infor().execute();
		listView.setDividerHeight(0);
		mAdapter = new IwoAdapter(this, mData) {
			int[] viewID = { R.id.comments, R.id.play, R.id.share };
			int[] d_img = { R.drawable.d_pinglun, R.drawable.d_bofang, R.drawable.d_fenxiang };
			int[] n_img = { R.drawable.n_pinglun, R.drawable.n_bofang, R.drawable.n_fenxiang };

			@Override
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null) {
					v = mInflater.inflate(R.layout.view3_layout_item, null);
				}
				setMode_Image(v, R.id.alarm_img, R.drawable.d_alarm, R.drawable.n_alarm);
				setItemViewImg(v, viewID, d_img, n_img);
				Map<String, String> map = mData.get(position);
				final ImageView userImg = (ImageView) v.findViewById(R.id.head);
				LoadBitmap bitmap = new LoadBitmap();
				if (!StringUtil.isEmpty(map.get("head_img"))) {
					bitmap.loadImage(map.get("head_img"), new LoadBitmapCallBack() {

						@Override
						public void callBack(Bitmap bit) {
							if (bit != null) {
								userImg.setImageBitmap(BitmapUtil.toRoundCorner(bit, 91));
							}
						}
					});
					userImg.setBackgroundColor(mContext.getResources().getColor(R.color.comm_bg_color));
				} else {
					userImg.setBackgroundResource(R.drawable.ico_default);
				}

				if (!StringUtil.isEmpty(stringUserNote)) {
					setItem(v, R.id.name, stringUserNote);
				} else {
					setItem(v, R.id.name, !StringUtil.isEmpty(userRoster.get("note_name")) ? userRoster.get("note_name") : map.get("nickname"));
				}

				TextView create_time = (TextView) v.findViewById(R.id.time);
				if ("day".equals(getMode())) {
					create_time.setTextColor(mContext.getResources().getColor(R.color.comm_green_color));
				} else {
					create_time.setTextColor(mContext.getResources().getColor(R.color.comm_pink_color));
				}
				if (!StringUtil.isEmpty(map.get("create_time"))) {
					setItem(v, R.id.time, DateUtil.format("MM-dd HH:mm", "" + DateUtil.getTime("yyyy-MM-dd HH:mm:ss", map.get("create_time"))));
				}
				setItem(v, R.id.synopsis, map.get("name"));
				ImageView big_img = (ImageView) v.findViewById(R.id.item_img);
				setImgSize(big_img, 50, 169 / 300.0f, 1);
				bitmap.loadImage(map.get("img_url_2"), big_img);
				big_img.setTag(position);
				big_img.setOnClickListener(FriendDetail.this);
				setItem(v, R.id.comments_text, StringUtil.isEmpty(map.get("ping_count")) ? "0" : map.get("ping_count"));
				setItem(v, R.id.share_text, StringUtil.isEmpty(map.get("share_count")) ? "0" : map.get("share_count"));
				setItem(v, R.id.play_text, StringUtil.isEmpty(map.get("play_count")) ? "0" : map.get("play_count"));
				return v;
			}

		};

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if (scrollOn) {
							new GetData().execute();
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int first, int visible, int total) {

			}
		});
		listView.setAdapter(mAdapter);

		listView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				isrefresh = true;
				new GetData().execute();
			}
		});
		listView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) { // 设置弹框隐藏
				if (findViewById(R.id.set_fiends).getVisibility() == View.VISIBLE) {
					findViewById(R.id.set_fiends).setVisibility(View.GONE);
				}
				return false;
			}
		});
	}

	private void setUser() {
		if (!StringUtil.isEmpty(userRoster.get("note_name"))) {
			note_name = userRoster.get("note_name");
			setTitle(userRoster.get("note_name"));
		} else {
			setTitle(userData.get("nick_name"));
		}
		ImageView user_imgImageView = (ImageView) listViewHead.findViewById(R.id.view3_layout_user_img);
		LoadBitmap bitmap = new LoadBitmap();
		bitmap.loadImage(userData.get("head_img"), user_imgImageView);
		setViewGone(listViewHead, R.id.my_pages_layout);
		if (!"0".equals(attention)) {
			setViewGone(listViewHead, R.id.set_friend_angel);
		}
		ImageView layout_head_img = (ImageView) listViewHead.findViewById(R.id.view3_layout_head_img);
		String i = userData.get("bg_img");
		try {
			setImgSize(layout_head_img, 0, 7 / 12.0f, 1);
			Logger.i("背景图：" + "wallpaper" + i + ".jpg");
			layout_head_img.setImageBitmap(BitmapFactory.decodeStream(mContext.getAssets().open("wallpaper" + i + ".jpg")));
		} catch (Exception e) {
			Logger.e("设置背景图出错" + e.getMessage());
		}
		TextView user_nameTextView = (TextView) listViewHead.findViewById(R.id.view3_layout_user_name);
		user_nameTextView.setText(userData.get("nick_name"));
		// ----------------------------公共部分个性签名。
		setViewShow(listViewHead, R.id.view3_layout_signature);
		TextView content = (TextView) setViewShow(listViewHead, R.id.view3_layout_signature_content);
		content.setText(userData.get("sign"));
		// ----------------------------
		setViewShow(listViewHead, R.id.user_dialogue);
		setViewonClick(listViewHead, R.id.user_dialogue);

		if ("day".equals(getMode())) {
			if (!StringUtil.isEmpty(userNote)) {

				if ("1".equals(userNote.get("is_star"))) {// 是星标好友
					star = "3";
					setViewShow(listViewHead, R.id.d_user_sex_img_2_1);
				} else if ("0".equals(userNote.get("is_star"))) {// 非星标好友
					star = "2";
				}
			} else {
				star = "2";
			}

			String city = StringUtil.isEmpty(userData.get("city_name")) ? "暂无城市" : userData.get("city_name");
			String area = StringUtil.isEmpty(userData.get("area_name")) ? "暂无区域" : userData.get("area_name");

			setItem(listViewHead, R.id.d_friend_distance, city + " " + area);
			setViewGone(listViewHead, R.id.line);
			setViewGone(listViewHead, R.id.user_sex_img);
			setViewGone(listViewHead, R.id.user_age_text);
			setViewGone(listViewHead, R.id.n_head_bottom_layout);
			setItem(listViewHead, R.id.d_user_age_text_2, userData.get("nick_name"));

			setViewShow(listViewHead, R.id.d_friends_pages_layout);
			setViewShow(listViewHead, R.id.white_line_2);
			setViewonClick(listViewHead, R.id.user_renmai_layout);
			// layout_head_img.setOnClickListener(this);
			findViewById(R.id.user_renmai_layout).setBackgroundColor(getResources().getColor(R.color.comm_green_color));
			TextView d_user_renmai_text = (TextView) listViewHead.findViewById(R.id.d_user_renmai_text);
			if (userData.get("friends") != null) {
				d_user_renmai_text.setText("(" + userData.get("friends") + ")");
			} else {
				d_user_renmai_text.setText("(0)");
			}
		} else {
			setViewShow(listViewHead, R.id.n_friends_pages_layout);
			ImageView sex = (ImageView) listViewHead.findViewById(R.id.user_sex_img_2);
			if ("1".equals(userData.get("sex"))) {
				sex.setImageResource(R.drawable.boy);
			} else if ("2".equals(userData.get("sex"))) {
				sex.setImageResource(R.drawable.girl);
			} else {
				sex.setImageResource(R.drawable.boy);
			}
			TextView user_age_text = (TextView) listViewHead.findViewById(R.id.user_age_text_2);
			user_age_text.setText(userData.get("age"));

			TextView friend_distance = (TextView) listViewHead.findViewById(R.id.friend_distance);
			//
			String latlng_str = userData.get("latlng");
			String juli;

			if (StringUtil.isEmpty(latlng_str)) {
				juli = "暂无距离";
			} else {
				String[] Mylatlng = getPre("address").split(",");
				String[] latlng = userData.get("latlng").split(",");
				double dis = LocationManage.getDistatce(Double.valueOf(Mylatlng[0]), Double.valueOf(latlng[0]), Double.valueOf(Mylatlng[1]), Double.valueOf(latlng[1]));
				if (dis < 1) {
					juli = (int) (dis * 1000) + "m";
				} else {
					DecimalFormat df = new DecimalFormat("###,##0");
					juli = df.format(dis) + "km";
				}
			}
			String last_time = userData.get("last_time");
			String shijian;

			if (StringUtil.isEmpty(last_time)) {
				shijian = "暂无时间";
			} else {
				shijian = DateUtil.subDate(Long.valueOf(last_time));
			}

			friend_distance.setText(juli + "|" + shijian);

			setViewShow(listViewHead, R.id.n_head_bottom_layout);
			setViewonClick(listViewHead, R.id.set_friend_angel);
			setViewonClick(listViewHead, R.id.edit_data);
			setViewonClick(listViewHead, R.id.friends);
			setViewonClick(listViewHead, R.id.attention);
			setViewonClick(listViewHead, R.id.fans);
		}
		listViewHead.setVisibility(View.VISIBLE);
	}

	private void setOnClick() {
		setViewonClick(R.id.set_fiend_1);
		setViewonClick(R.id.set_fiend_2);
		setViewonClick(R.id.set_fiend_3);
		setViewonClick(R.id.set_fiend_4);
		setViewonClick(R.id.set_fiend_5);
		setViewonClick(R.id.set_fiend_6);
		setViewonClick(R.id.set_fiend_7);
		setViewonClick(R.id.set_fiend_8);
		setViewonClick(R.id.set_fiend_cancel);
	}

	CommonDialog commonDialog = null;

	@Override
	public void onClick(View v) {
		int position;
		Map<String, String> map = null;
		Intent intent = null;
		int[] clikViews = null;
		switch (v.getId()) {

		case R.id.left_img: // 头部返回键
			setResult(AppConfig.RESULT_FRIENDDETAIL_SETBLACKLIST);
			finish();
			break;

		case R.id.user_focus: // 夜间模式好友加关注
			// 0。 1，是取消关注 2，是添加关注， 0，是以为好友。3，是黑名单

			if ("3".equals(attention)) {
				makeText("请先取消黑名单");
				return;
			} else {
				new SetFriendFocus().execute(id);
			}

			break;
		case R.id.user_pull_black: // 夜间模式好友拉黑
			OnClickListener clickListener_black = new OnClickListener() {

				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.determine:
						new setBlackList().execute();// 拉黑好友，
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
			if (whetherBlackList) {
				commonDialog = new CommonDialog(FriendDetail.this, "你是否取消好友黑名单？", clickListener_black, R.layout.loading_dialog_text, clikViews, R.id.tipText_view);
			} else {
				commonDialog = new CommonDialog(FriendDetail.this, "你是否要将好友拉入黑名单？", clickListener_black, R.layout.loading_dialog_text, clikViews, R.id.tipText_view);
			}
			commonDialog.show();
			break;

		case R.id.set_friend_angel:// 黑夜模式设置好友为天使
			if ("0".equals(attention)) {
				new SetFriendAngel().execute();
			} else {
				makeText("该用户还不是你的好友不可设置为天使哦，亲！！！");
			}

			break;

		// FriendInformation
		case R.id.set_fiend_1:// 设置为星标好友

			new SetFriendsStateData().execute(star, "");

			break;

		case R.id.set_fiend_3:// 查看历史记录
			intent = new Intent(mContext, ChatHistory.class);
			intent.putExtra("userID", name);
			intent.putExtra("head_img", userData.get("head_img"));
			intent.putExtra("name", StringUtil.isEmpty(note_name) ? userData.get("nick_name") : note_name);
			intent.putExtra("from", "yes");
			startActivity(intent);
			break;

		case R.id.set_fiend_2:// 设置为星标好友

			intent = new Intent(mContext, FriendInformation.class); // 跳到会话界面跳到好友人脉
			intent.putExtra("id", id);
			intent.putExtra("name", name);
			mContext.startActivity(intent);
			break;

		case R.id.set_fiend_6:// 修改备注
			OnClickListener clickListener_6 = new OnClickListener() {

				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.determine:
						EditText edit = (EditText) v.getTag();
						// EditText v
						String textnike = edit.getText().toString();
						if (StringUtil.isEmpty(textnike)) {
							makeText("好友备注不可为空");
							commonDialog.dismiss();
							return;
						}
						// TODO
						new SetFriendsStateData().execute("1", textnike);

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
			commonDialog = new CommonDialog(FriendDetail.this, clickListener_6, R.layout.loading_dialog_edittext, clikViews, R.id.edit_view,
					StringUtil.isEmpty(note_name) ? "" : note_name);
			commonDialog.show();
			break;
		case R.id.set_fiend_7:// 设置加入黑名单
			if (StringUtil.isEmpty(attention)) {
				OnClickListener clickListener_7 = new OnClickListener() {

					@Override
					public void onClick(View v) {
						switch (v.getId()) {
						case R.id.determine:
							new setBlackList().execute();
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
				commonDialog = new CommonDialog(FriendDetail.this, "你是否要将好友拉入黑名单？", clickListener_7, R.layout.loading_dialog_text, clikViews, R.id.tipText_view);
				commonDialog.show();
			}

			break;
		case R.id.set_fiend_8:// 设置删除好友
			OnClickListener clickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.determine:
						new SetDeleteFriendsData().execute(name);
						commonDialog.dismiss();
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
			commonDialog = new CommonDialog(FriendDetail.this, "你是否要将好友删除？", clickListener, R.layout.loading_dialog_text, clikViews, R.id.tipText_view);
			commonDialog.show();
			break;

		case R.id.set_fiend_cancel:// 设置好友弹框取消
			findViewById(R.id.set_fiends).setVisibility(View.GONE);
			break;
		case R.id.right_img:// 设置好友状态
			findViewById(R.id.set_fiends).setVisibility(View.VISIBLE);
			break;
		case R.id.user_renmai_layout:// 黑夜模式，好友人脉
			intent = new Intent(mContext, FriendsNetworkActivity.class); // 跳到会话界面跳到好友人脉
			intent.putExtra("id", id);
			mContext.startActivity(intent);
			break;
		case R.id.user_dialogue:
			intent = new Intent(mContext, ChatActivity.class); // 跳到会话界面
			intent.putExtra("userID", userData.get("user_name"));
			intent.putExtra("head_img", userData.get("head_img"));
			intent.putExtra("name", userData.get("nick_name"));
			mContext.startActivity(intent);
			break;
		case R.id.edit_data:// 黑夜模式，详细资料
			intent = new Intent(mContext, FriendInformation.class);
			intent.putExtra("id", id);
			intent.putExtra("name", name);
			mContext.startActivity(intent);
			break;
		case R.id.friends:// 好友
			/*
			 * intent = new Intent(mContext, FriendsListActivity.class);
			 * intent.putExtra("friendAttentionID", id);
			 * mContext.startActivity(intent);
			 */
			break;
		case R.id.attention:// 关注
			intent = new Intent(mContext, AttentionListActivity.class);
			intent.putExtra("attention", "1");
			intent.putExtra("friendAttentionID", id);
			mContext.startActivity(intent);
			break;
		case R.id.fans:// 粉丝
			intent = new Intent(mContext, AttentionListActivity.class);
			intent.putExtra("attention", "2");
			intent.putExtra("friendAttentionID", id);
			mContext.startActivity(intent);
			break;
		case R.id.item_img: // 视频图片
			position = (Integer) v.getTag();
			map = mData.get(position);
			intent = new Intent(mContext, VideoDetailActivity.class);
			intent.putExtra("video_id", map.get("video_id"));
			intent.putExtra("nickname", map.get("nickname"));
			intent.putExtra("create_time", map.get("create_time"));
			intent.putExtra("head_img", map.get("head_img"));
			mContext.startActivity(intent);
			break;

		default:
			break;
		}
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

	private View setViewShow(View v, int viewID) {
		View childV = v.findViewById(viewID);
		childV.setVisibility(View.VISIBLE);
		return childV;
	}

	private void setViewGone(View v, int viewID) {
		v.findViewById(viewID).setVisibility(View.GONE);
	}

	private void setViewonClick(int viewID) {
		findViewById(viewID).setOnClickListener(this);
	}

	private void setViewonClick(View v, int viewID) {
		v.findViewById(viewID).setOnClickListener(this);
	}

	/**
	 * 设置好友黑名单
	 * 
	 * @author hanpengyuan
	 * 
	 */
	protected class setBlackList extends AsyncTask<String, Void, HashMap<String, String>> {
		String originalAttention = "4";

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			if (whetherBlackList) {
				return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_SET_BLACKLIST), id, "2");
			} else {
				return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_SET_BLACKLIST), id, "1");
			}
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (null != result) {
				if ("1".equals(result.get("code"))) {
					if ("3".equals(attention)) {
						attention = originalAttention;
					} else {
						originalAttention = attention;
					}
					FriendDetail.this.makeText(result.get("msg"));
					finishActivity(AppConfig.RESULT_FRIENDDETAIL_SETBLACKLIST);
					if ("day".equals(getMode())) {
						if (whetherBlackList) {
							whetherBlackList = false;
							setItem(R.id.set_fiend_7_text, "加入黑名单");
							setImage(R.id.set_fiend_7_img, R.drawable.d_qx_heimingdan);
							makeText("取消好友黑名单成功！");
						} else {
							whetherBlackList = true;
							setItem(R.id.set_fiend_7_text, "取消黑名单");
							setImage(R.id.set_fiend_7_img, R.drawable.jhmd);
							makeText("拉黑好友操作成功！");
							attention = "3";
						}

					} else {
						if (whetherBlackList) {
							whetherBlackList = false;
							setItem(R.id.user_pull_black_text, "拉黑");
							setImage(R.id.user_pull_black_img, R.drawable.bshmd);
							makeText("取消好友黑名单成功！");
						} else {
							whetherBlackList = true;
							setItem(R.id.user_pull_black_text, "取消拉黑");
							setImage(R.id.user_pull_black_img, R.drawable.qx_lahei);
							makeText("拉黑好友操作成功！");
							attention = "3";
						}
					}
				}

			} else {
				FriendDetail.this.makeText("设置好友黑名单失败");
			}
			commonDialog.dismiss();
		}
	}

	/**
	 * 获取好友信息
	 * 
	 * @author hanpengyuan
	 * 
	 */
	private class Infor extends AsyncTask<Void, Void, HashMap<String, String>> {
		protected HashMap<String, String> doInBackground(Void... params) {
			// ArrayList<HashMap<String, String>> list=
			// DataRequest.getArrayListFromUrl_Base64(AppConfig.NEW_FR_GET_FRIEND_INFO
			// + AppConfig.END, id);
			String userInfo = null;
			Map<String, String> map;
			if ("day".equals(getMode())) {
				map = DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_FR_GET_FRIEND_INFO), id, name);
			} else {
				map = DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_FR_GET_FRIEND_INFO), id, name);
			}
			if (map != null) {
				if ("1".equals(map.get("code"))) {
					return DataRequest.getHashMapFromJSONObjectString(map.get("data"));
				}
			}
			return null;
		}

		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				// userRoster
				Map<String, String> friend = DataRequest.getHashMapFromJSONObjectString(result.get("friend"));
				if (friend != null) {
					userData.putAll(friend);
					id = friend.get("id");
				}
				Map<String, String> ofRoster = DataRequest.getHashMapFromJSONObjectString(result.get("ofRoster"));
				if (ofRoster != null) {
					userRoster.putAll(ofRoster);
				}
				Logger.i("好友信息：" + result.toString());
				setUser();
				if (!StringUtil.isEmpty(id)) {
					new GetData().execute();
				} else {
				}
				listView.setVisibility(View.VISIBLE);

			} else {
			}
			mLoadBar.dismiss();
		}
	}

	/**
	 * 获取好友视频列表
	 * 
	 * @author hanpengyuan
	 * 
	 */
	private class GetData extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
		protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
			String str = "";
			HashMap<String, String> map;
			if ("day".equals(getMode())) {
				// 白天模式访问的接口
				if (isrefresh) {
					str = DataRequest.getStringFromURL_Base64(getUrl(AppConfig.NEW_V_GET_FRIEND_VIDEO_LIST), id, "1", "10");
				} else {
					str = DataRequest.getStringFromURL_Base64(getUrl(AppConfig.NEW_V_GET_FRIEND_VIDEO_LIST), id, getStart(mData.size()), "10");
				}
				map = DataRequest.getHashMapFromJSONObjectString(str);
				if (map != null) {
					return DataRequest.getArrayListFromJSONArrayString(map.get("data"));
				} else {
					return null;
				}

			} else {
				// 夜晚模式 访问的接口
				if (isrefresh) {
					str = DataRequest.getStringFromURL_Base64(getUrl(AppConfig.NEW_V_GET_FRIEND_VIDEO_LIST), id, "1", "10");
				} else {
					str = DataRequest.getStringFromURL_Base64(getUrl(AppConfig.NEW_V_GET_FRIEND_VIDEO_LIST), id, getStart(mData.size()), "10");
				}
				map = DataRequest.getHashMapFromJSONObjectString(str);
				if (map != null) {
					return DataRequest.getArrayListFromJSONArrayString(map.get("data"));
				} else {
					return null;
				}
			}
		}

		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (result != null) {
				if (result.size() < 10) {
					scrollOn = false;
				} else {
					scrollOn = true;
				}
				if (isrefresh) {
					mData.clear();
					if (result.size() < 10) {
						scrollOn = false;
					} else {
						scrollOn = true;
					}
				}
				mData.addAll(result);
				mAdapter.notifyDataSetChanged();
			}
			listView.setVisibility(View.VISIBLE);
			mLoadBar.dismiss();

			if (isrefresh) {
				listView.stopRefresh();
				isrefresh = false;
			}
		}
	}

	/**
	 * 删除好友
	 */

	private class SetDeleteFriendsData extends AsyncTask<String, Void, Boolean> {
		String name = "";

		protected Boolean doInBackground(String... params) {
			name = params[0];
			return XmppClient.getInstance(mContext).removeUser(params[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				per.delete("tab_" + PreferenceUtil.getString(mContext, "user_name", ""), "name", name);
				DBhelper per = new DBhelper(mContext, IwoSQLiteHelper.MESSAGE_TAB);
				per.delete("tab_msg" + PreferenceUtil.getString(mContext, "user_name", ""), "user_name", name);
				per.close();
				DBhelper tab = new DBhelper(mContext, IwoSQLiteHelper.MESSAGE_TAB);
				tab.delete("tab_" + getUid() + name);
				tab.close();
				DBhelper invite_from = new DBhelper(mContext, IwoSQLiteHelper.INVITE_FROM);
				invite_from.delete("invite_from", "tel", name);
				invite_from.close();
				mContext.sendBroadcast(new Intent("com.android.broadcast.receiver.media.refresh.CHAT_REFRESH_SHARE"));
				IwoToast.makeText(mContext, "删除成功");
				ActivityUtil.getInstance().delete(ChatActivity.class.getSimpleName());
				finish();
			} else {
				IwoToast.makeText(mContext, "删除失败");
			}
		}

	}

	/**
	 * 标为星标好友
	 */
	private class SetFriendsStateData extends AsyncTask<String, Void, HashMap<String, String>> {
		String str;

		protected HashMap<String, String> doInBackground(String... params) {
			str = params[0];
			if ("1".equals(params[0])) {
				stringUserNote = params[1];
				return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_SET_NICKNAME_STAR), params[0], name, params[1]); // 备注好友名称
			} else if ("2".equals(params[0])) {
				return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_SET_NICKNAME_STAR), params[0], id, ""); // 设置星标
			} else if ("3".equals(params[0])) {
				return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_SET_NICKNAME_STAR), params[0], id, ""); // 取消星标
			}
			return null;
		}

		protected void onPostExecute(HashMap<String, String> result) {
			Logger.i("result" + result);
			if (result != null) {
				makeText(result.get("msg"));
				if ("3".equals(str)) {
					setViewGone(listViewHead, R.id.d_user_sex_img_2_1);
					star = "2";
				} else if ("2".equals(str)) {
					setViewShow(listViewHead, R.id.d_user_sex_img_2_1);
					star = "3";
				} else if ("1".equals(str)) {
					setTitle(stringUserNote);
					mAdapter.notifyDataSetChanged();
					note_name = stringUserNote;
					commonDialog.dismiss();
					DBhelper db = new DBhelper(mContext, IwoSQLiteHelper.MESSAGE_TAB);
					db.update("tab_msg" + getUserTel(), "nick_name", note_name, "user_id", userData.get("user_name"));
					db.close();
					XmppClient.getInstance(mContext).updateFriend(userData.get("user_name"));
				}
			} else {
				makeText("操作失败");
			}

			if ("1".equals(str)) {
				commonDialog.dismiss();
			}
		}
	}

	/**
	 * 设置取消好友关注
	 */
	protected class SetFriendFocus extends AsyncTask<String, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			if ("1".equals(attention) || "0".equals(attention)) {
				return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_EDIT_GUAN), params[0], "2");

			} else if ("2".equals(attention)) {
				return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_EDIT_GUAN), params[0], "1");
			} else {
				attention = "2";
				return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_EDIT_GUAN), params[0], "1");
			}
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (null != result) {
				if ("1".equals(result.get("code"))) {
					if ("1".equals(attention) || "0".equals(attention)) {
						setItem(R.id.user_focus_tet, "加关注");
						setImage(R.id.user_focus_img, R.drawable.yaoqing);
						attention = "2";

					} else if ("2".equals(attention)) {
						setItem(R.id.user_focus_tet, "取消关注");
						setImage(R.id.user_focus_img, R.drawable.qx_guanzhu);
						attention = "1";
					}
					makeText(result.get("msg"));

				} else {
					FriendDetail.this.makeText(result.get("msg"));
				}
			} else {
				FriendDetail.this.makeText("设置好友关注操作失败");
			}

		}
	}

	protected class SetFriendAngel extends AsyncTask<String, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_ADD_ANGEL), getUid(), id);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (null != result) {
				mAdapter.setmAdapterData(mData);
				mAdapter.notifyDataSetChanged();
				FriendDetail.this.makeText(result.get("msg"));
			} else {
				FriendDetail.this.makeText("请检查网络");
			}
		}

	}

}
