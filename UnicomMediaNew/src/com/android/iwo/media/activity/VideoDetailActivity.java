package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.iwo.media.action.ActivityUtil;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.Constants;
import com.android.iwo.media.action.Share;
import com.android.iwo.media.action.Share.ShareAnimationListener;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.view.AbstractSpinerAdapter.IOnItemSelectListener;
import com.android.iwo.media.view.ChildViewPager;
import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.view.LinearLayoutForListView;
import com.android.iwo.media.view.SpinerPopWindow;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.adapter.ViewPageAdapter;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 使用视频详情页 Intent需要传递String类型参数 , 键值为"video_id"
 * 
 * @author
 */
public class VideoDetailActivity extends BaseActivity implements
		OnClickListener {
	private String video_Id;
	private Map<String, String> mapVideo = new HashMap<String, String>();
	private Share mShare;
	private String head_img;// nickname, create_time,
	private LinearLayoutForListView detail_listView;

	private ChildViewPager test_drive_img_layout_viewpager;
	private ViewPageAdapter test_drive_img_layout_viewpager_adapter;
	protected IwoAdapter mAdapter;
	private ArrayList<HashMap<String, String>> commentnData = new ArrayList<HashMap<String, String>>();
	private boolean loading_more = true;// 评论 判断是否是加载1条
	private boolean loading_one = true; // 评论 判断是否是第一次加载
	private boolean loading_not_more = false; // 判断；评论是否可收起
	private boolean isCheck = true;// checkBox的判断
	private String ping_count, video_desc;
	private boolean introduction_all = true;
	private EditText comments_edit;
	private ScrollView video_detail_scroll;
	// private String pinglunUserID;
	private TextView synopsis;
	private InputMethodManager imm;
	private String audit_status;
	private boolean onClikCommentn = true, onClikright_textview = true;
	private String iwoShareType;

	private TextView right_textview;

	private String city = "", region = "";
	private SpinerPopWindow window1, window2;
	private TextView text1_1, text2_2;
	private SpinerPopWindow test_drive_dealers_window, test_drive_car_window,
			test_drive_time_window;
	private TextView test_drive_dealers, test_drive_car,
			test_drive_buycar_time;// 经销商，车系

	private ArrayList<HashMap<String, String>> list1, list2;
	private ArrayList<HashMap<String, String>> dealers, car_list, advance_time,
			car_pictures;// car_pictures
							// 车型图片
	private String agency = "", car = "", time = "", username, mobile;

	/**
	 * @see 过滤掉特殊字符
	 * @param str
	 *            传入的字符串
	 * @return
	 */
	public String EditTextFilter(String str) {
		// 过滤特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		video_Id = this.getIntent().getStringExtra("video_id");
		// nickname = this.getIntent().getStringExtra("nickname");
		// create_time = this.getIntent().getStringExtra("create_time");
		head_img = this.getIntent().getStringExtra("head_img");
		mShare = new Share(VideoDetailActivity.this);
		mShare.setShareAnimationListener(new ShareAnimationListener() {

			@Override
			public void animationCompleted() {
				TextView share_text = (TextView) findViewById(R.id.share_text);
				int share = Integer.valueOf(mapVideo.get("share_count")) + 1;
				mapVideo.put("share_count", "" + share);
				share_text.setText("" + share);
				new SetComments4Share().execute("1");

			}

		});
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		init();
		new GetMsg().execute();
		new GetUserData().execute();
		new GetCommentData().execute();
		new CheckVideoChaes().execute();
		// checkBox监听事件
		findViewById(R.id.all_test_drive_checkBox).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						TextView text_note = (TextView) findViewById(R.id.test_drive_note);
						if (isCheck) {
							text_note.setText("个人资料有可能提交至经授权的经销商"
									+ test_drive_dealers.getText()
									+ "，请等待与你的进一步沟通试驾购车等事宜。");
						} else {
							text_note.setText(text_note.getText());
							isCheck = true;
						}
					}
				});

		findViewById(R.id.test_drive_submit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						EditText test_drive_name_edit = (EditText) findViewById(R.id.test_drive_name_edit);
						username = test_drive_name_edit.getText().toString();

						EditText test_drive_phone_edit = (EditText) findViewById(R.id.test_drive_phone_edit);
						mobile = test_drive_phone_edit.getText().toString();

						TextView city = (TextView) findViewById(R.id.address_1);
						String city11 = city.getText().toString();
						if (StringUtil.isEmpty(username)) {
							makeText("姓名不可为空");
							return;
						}
						if ("".equals(username.trim())) {
							makeText("姓名不可为空");
							return;
						}
						if (StringUtil.isEmpty(mobile)) {
							makeText("手机号不可为空");
							return;
						}
						if (!StringUtil.isPhone(mobile)) {
							makeText("手机号格式不对");
							return;
						}
						if (StringUtil.isEmpty(car)) {
							makeText("请选择车系");
							return;
						}
						if (StringUtil.isEmpty(city11)) {
							makeText("请选择城市");
							return;
						}
						if (StringUtil.isEmpty(region)) {
							makeText("请选择地区");
							return;
						}

						if (StringUtil.isEmpty(agency)) {
							makeText("请选择经销商");
							return;
						}

						if (StringUtil.isEmpty(time)) {
							makeText("请选择购车时间");
							return;
						}

						// video_id={0}&mobile={1}&agency_id={2}&car_id={3}&city_id={4}&area_id={5}&buy_time={6}&username={7}
						new SaveTestDrive().execute(video_Id, mobile, agency,
								car, city11, region, time, username);
						// 重置试驾有礼的数据
						test_drive_name_edit.setText("");
						test_drive_phone_edit.setText("");
						test_drive_car.setText("");
						text1_1.setText("");
						text2_2.setText("");
						test_drive_dealers.setText("");
						test_drive_buycar_time.setText("");
						isCheck = false;
					}
				});

	}

	private void init() {
		setBack(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
		right_textview = (TextView) findViewById(R.id.right_textview);
		setMode(getMode());
		setOnClickView();
		detail_listView = (LinearLayoutForListView) findViewById(R.id.detail_listView);
		comments_edit = (EditText) findViewById(R.id.comments_popup_edit);
		video_detail_scroll = (ScrollView) findViewById(R.id.video_detail_scroll);
		synopsis = (TextView) findViewById(R.id.synopsis);
		// 设置点击其他地方之后评论弹框消失
		video_detail_scroll.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (findViewById(R.id.comments_popup).getVisibility() == View.VISIBLE) {
					imm.hideSoftInputFromWindow(comments_edit.getWindowToken(),
							0);
					findViewById(R.id.comments_popup).setVisibility(View.GONE);

				}
				return false;
			}
		});

		// comments_edit.addTextChangedListener(new TextWatcher() {
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		//
		// }
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before, int
		// count) {
		// String text = comments_edit.getText().toString().trim();
		// Logger.i("jiage" + text);
		// try {
		// if (!StringUtil.isEmpty(text)) {
		// findViewById(R.id.comments_popup_text).setVisibility(View.VISIBLE);
		// findViewById(R.id.share_popup_text).setVisibility(View.GONE);
		// } else {
		// findViewById(R.id.comments_popup_text).setVisibility(View.GONE);
		// findViewById(R.id.share_popup_text).setVisibility(View.VISIBLE);
		// }
		// } catch (Exception e) {
		// }
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		// }
		// });

	}

	private void setVideoDetail(Map<String, String> map) {

		if (getUid().equals(map.get("userid"))) {

			if ("0".equals(map.get("share_mark"))) {

				iwoShareType = "1";
			} else {
				iwoShareType = "0";
			}
			mShare.setIwoShareShow(true);
			mShare.setIwoShareType(iwoShareType);
			mShare.setShareVideoID(video_Id);
		} else {
			mShare.setIwoShareShow(false);
		}

		if (mShare != null) {

			// mShare.setUrl(map.get("video_url"));
			if (!StringUtil.isEmpty(countShare)) {
				//
				mShare.setCon(countShare.replace("(" + getUserTel() + ")", ":"));
			}

			mShare.setShareVideoID(video_Id);
		}
		setTitle(map.get("name"));
		setItem(R.id.synopsis, map.get("video_desc"));
		video_desc = map.get("video_desc");
		ping_count = map.get("ping_count");

		// TODO
		if (!StringUtil.isEmpty(map.get("nickname"))) {
			setItem(R.id.user_name, "  " + map.get("nickname"));
		}
		setItem(R.id.comments_text,
				StringUtil.isEmpty(ping_count) ? "0" : map.get("ping_count"));
		setItem(R.id.share_text,
				StringUtil.isEmpty(map.get("share_count")) ? "0" : map
						.get("share_count"));
		LoadBitmap bitmap = new LoadBitmap();
		ImageView big_img = (ImageView) findViewById(R.id.item_img);
		setImgSize(big_img, 14, 27 / 48.0f, 1);
		bitmap.loadImage(map.get("img_url_2"), big_img);
		big_img.setOnClickListener(this);

		big_img.setTag(map.get("video_url"));

		setItem(R.id.play_text, StringUtil.isEmpty(map.get("play_count")) ? "0"
				: map.get("play_count"));
		setItem(R.id.video_introduction_text1, "  " + map.get("video_desc"));
		setItem(R.id.video_introduction_text2, "  " + map.get("video_desc"));
		// ------没有数据则隐藏"推荐理由"-------
		if (StringUtil.isEmpty(map.get("video_type"))) {
			findViewById(R.id.recommended_reason_text).setVisibility(View.GONE);
			findViewById(R.id.video_comments_head).setVisibility(View.GONE);// 隐藏推荐理由
																			// 头
		} else {
			setItem(R.id.recommended_reason_text, "  " + map.get("video_type"));
		}

		// 设置头部
		ImageView userImg = (ImageView) findViewById(R.id.friends_img);
		if (StringUtil.isEmpty(head_img)) {

			if (!StringUtil.isEmpty(map.get("head_img"))) {
				bitmap.loadImage(map.get("head_img"), userImg);
			} else {
				userImg.setBackgroundColor(getResources().getColor(
						R.color.comm_green_color));
			}

		} else {
			bitmap.loadImage(head_img, userImg);
		}
	}

	/**
	 * 设置界面中可点击的组件
	 */
	private void setOnClickView() {
		findViewById(R.id.video_introduction_loading).setOnClickListener(this);
		findViewById(R.id.video_comments_loading).setOnClickListener(this);
		findViewById(R.id.user_comments).setOnClickListener(this);
		findViewById(R.id.user_share).setOnClickListener(this);
		findViewById(R.id.comments_popup_text).setOnClickListener(this);
		findViewById(R.id.comments_popup).setOnClickListener(this);
		findViewById(R.id.synopsis).setOnClickListener(this);
		right_textview.setOnClickListener(this);
		// findViewById(R.id.share_popup_text).setOnClickListener(this);
	}

	/**
	 * 设置黑夜白天模式的,的区别。
	 * 
	 * @param mode
	 */
	private void setMode(String mode) {
		// setMode_BG(R.id.video_comments_bottom);

		setMode_BG(R.id.video_introduction_line);
		setMode_BG(R.id.video_comments_line);
		if ("day".equals(mode)) {
			setImage(R.id.comments, R.drawable.d_pinglun);// 评论图片
			setImage(R.id.play, R.drawable.d_bofang);// 评论图片
			setImage(R.id.share, R.drawable.d_fenxiang);// 评论图片
			setImage(R.id.video_introduction_img, R.drawable.d_jianjie);// 评论图片
			setImage(R.id.video_comments_img, R.drawable.d_pinglunbiao);// 评论图片
			// findViewById(R.id.comments_popup_text).setBackgroundColor(getResources().getColor(R.color.comm_green_color));

		} else if ("night".equals(mode)) {
			setImage(R.id.comments, R.drawable.n_pinglun);// 评论图片
			setImage(R.id.play, R.drawable.n_bofang);// 评论图片
			setImage(R.id.share, R.drawable.n_fenxiang);// 评论图片

			setImage(R.id.video_introduction_img, R.drawable.n_jianjie);// 评论图片
			setImage(R.id.video_comments_img, R.drawable.n_pinglunbiao);// 评论图片
			// findViewById(R.id.comments_popup_text).setBackgroundColor(getResources().getColor(R.color.comm_pink_color));
		}
	}

	/**
	 * 设置评论列表
	 * 
	 * @param listView
	 */
	private void setDetailListView(LinearLayoutForListView listView) {
		// listView.addHeaderView(listViewHead);
		mAdapter = null;
		mAdapter = new IwoAdapter(this, commentnData) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				// TODO Auto-generated method stub
				Map<String, String> map = commentnData.get(position);
				if (null == v) {
					v = mInflater.inflate(R.layout.view_comments_item, null);
				}
				Logger.i(map.toString());
				setItem(v, R.id.user_name, map.get("user_nick"));
				TextView textView = (TextView) v.findViewById(R.id.user_name);
				if ("day".equals(getMode())) {
					textView.setTextColor(getResources().getColor(
							R.color.comm_green_color));
				} else {
					textView.setTextColor(getResources().getColor(
							R.color.comm_night_color));
				}
				LoadBitmap bitmap = new LoadBitmap();
				ImageView img = (ImageView) v.findViewById(R.id.user_img);
				bitmap.loadImage(map.get("head_img"), img);
				setItem(v, R.id.user_comment, map.get("comm_content"));
				img.setOnClickListener(VideoDetailActivity.this);

				img.setTag(position);

				Logger.i("  -------   " + position);
				return v;
			}
		};
		if (loading_not_more) {
			loading_not_more = false;
		}
		// new GetCommentPublish().execute("测试数据，zzz添加");
		listView.setAdapter(mAdapter);
	}

	CommonDialog commonDialog = null;

	@Override
	public void onClick(View v) {
		Intent intent = null;
		int[] clikViews = null;
		switch (v.getId()) {
		case R.id.item_img:
			mapVideo.get("play_count");
			int play = Integer.valueOf(mapVideo.get("play_count")) + 1;
			mapVideo.put("play_count", "" + play);
			setItem(R.id.play_text, "" + play);
			new SetComments4Share().execute("2");
			new SetCommentsIsPlay().execute();
			/*
			 * Uri uri = Uri.parse((String) v.getTag()); if
			 * ("2".equals(mapVideo.get("is_sys"))) { intent = new
			 * Intent(mContext, AdWebActivity.class); intent.putExtra("url",
			 * mapVideo.get("video_url")); intent.putExtra("title",
			 * mapVideo.get("name")); mContext.startActivity(intent); } else {
			 * intent = new Intent(Intent.ACTION_VIEW); String type = "video/*";
			 * intent.setDataAndType(uri, type); startActivity(intent); }
			 */
			break;
		case R.id.user_comments:// 底部评论
			comments_edit.setFocusable(true);
			comments_edit.setFocusableInTouchMode(true);
			comments_edit.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(comments_edit, InputMethodManager.RESULT_SHOWN);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
					InputMethodManager.HIDE_IMPLICIT_ONLY);
			findViewById(R.id.comments_popup).setVisibility(View.VISIBLE);
			break;
		case R.id.comments_popup_text:// 弹框中的评论按钮
			if ("1".equals(audit_status)) {
				if (StringUtil.isEmpty(comments_edit.getText().toString()
						.trim())) {
					makeText("评论内容不可为空");
				} else {
					this.imm.hideSoftInputFromWindow(
							comments_edit.getWindowToken(), 0);
					findViewById(R.id.comments_popup).setVisibility(View.GONE);
					if (onClikCommentn) {
						onClikCommentn = false;
						new GetCommentPublish().execute(comments_edit.getText()
								.toString());
					}

				}
			} else {
				makeText("该视频暂未审核通过，无法评论");
			}
			break;

		case R.id.user_share:// 分享
			if ("1".equals(audit_status)) {
				if (Share.onAnimation) {
					mShare.setShare();
				}

			} else {
				makeText("该视频暂未审核通过，无法分享");
			}

			break;

		case R.id.video_introduction_loading:// 显示全部简介
			if (introduction_all) {
				findViewById(R.id.video_introduction_text1).setVisibility(
						View.GONE);
				findViewById(R.id.video_introduction_text2).setVisibility(
						View.VISIBLE);
				TextView introduction_text = (TextView) findViewById(R.id.video_introduction_loading_text);
				introduction_text.setText("收起全部");
				setImage(R.id.video_introduction_loading_img, R.drawable.jiazai);
				introduction_all = false;
			} else {
				findViewById(R.id.video_introduction_text1).setVisibility(
						View.VISIBLE);
				findViewById(R.id.video_introduction_text2).setVisibility(
						View.GONE);
				TextView introduction_text = (TextView) findViewById(R.id.video_introduction_loading_text);
				introduction_text.setText("显示全部");
				setImage(R.id.video_introduction_loading_img,
						R.drawable.showall);
				introduction_all = true;
			}
			break;
		case R.id.video_comments_loading:// 加载更多。
			new GetCommentData().execute();
			break;

		case R.id.user_img:// 评论头像
			// int position;
			// Map<String, String> map = null;
			// position = (Integer) v.getTag();
			// map = commentnData.get(position);
			// if (!StringUtil.isEmpty(map.get("user_id"))) {
			// if (!map.get("user_id").equals(getUid())) {
			// intent = new Intent(VideoDetailActivity.this,
			// FriendDetail.class);
			// intent.putExtra("id", map.get("user_id"));
			// startActivity(intent);
			// }
			// }
			break;
		case R.id.synopsis:// 加载更多。
			if (StringUtil.isEmpty(synopsis.getText().toString())) {
				makeText("该视频暂无简介");
			} else {
				OnClickListener clickListener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						switch (v.getId()) {
						case R.id.determine:
							commonDialog.dismiss();
							break;
						default:
							break;
						}
					}
				};
				clikViews = new int[] { R.id.determine };
				commonDialog = new CommonDialog(VideoDetailActivity.this,
						video_desc, clickListener,
						R.layout.loading_dialog_text_single, clikViews,
						R.id.tipText_view);
				commonDialog.show();
			}

			break;

		case R.id.right_textview:
			if (onClikright_textview) {
				onClikright_textview = false;
				new setVideoChaesOr().execute();
			}

			break;
		default:
			break;
		}
	}

	// 获得视频信息
	private class GetUserData extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			ArrayList<HashMap<String, String>> list = DataRequest
					.getArrayListFromUrl_Base64(
							getUrl(AppConfig.NEW_V_VIDEO_DETAIL), video_Id);
			if (list != null) {
				return list.get(0);
			}
			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {

			if (result != null) {
				mapVideo.putAll(result);
				setVideoDetail(result);

				if ("1".equals(result.get("is_show"))) {
					findViewById(R.id.test_drive_layout).setVisibility(
							View.VISIBLE);
					list1 = new ArrayList<HashMap<String, String>>();
					list2 = new ArrayList<HashMap<String, String>>();
					new Get_City_List().execute();
					new GetTestDriveData().execute();
					new GetShoppingTimeData().execute();
				}
				audit_status = result.get("audit_status");
				findViewById(R.id.video_detail_scroll).setVisibility(
						View.VISIBLE);
			} else {
				makeText("暂无数据");
			}
			mLoadBar.dismiss();
		}

	}

	/**
	 * 获得评论列表
	 * 
	 * @author 张占忠
	 * 
	 */
	private class GetCommentData extends
			AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			if (loading_more) {
				if (loading_one) {
					commentnData.clear();
				}
				return DataRequest.getHashMapFromUrl_Base64(
						getUrl(AppConfig.NEW_V_VIDEO_COMMENTS_LIST), video_Id,
						"1", "10");
			}
			if (loading_one) {
				commentnData.clear();
			}

			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_V_VIDEO_COMMENTS_LIST), video_Id,
					getStart(commentnData.size()), "10");
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					if (null == detail_listView) {
						detail_listView = (LinearLayoutForListView) findViewById(R.id.detail_listView);
					}
					detail_listView.setVisibility(View.VISIBLE);
					findViewById(R.id.detail_text).setVisibility(View.GONE);
					detail_listView.removeAllViews();
					ArrayList<HashMap<String, String>> list = DataRequest
							.getArrayListFromJSONArrayString(result.get("data"));
					commentnData.addAll(list);
					setDetailListView(detail_listView);
					if (!loading_more) {
						loading_one = false;
					}

					if (commentnData.size() >= 10) {
						setItem(R.id.video_comments_loading_text, "加载更多");
						setImage(R.id.video_comments_loading_img,
								R.drawable.showall);
						findViewById(R.id.video_comments_loading)
								.setVisibility(View.VISIBLE);
					}
					if (!loading_more) {
						if (list.size() < 10) {
							setItem(R.id.video_comments_loading_text, "收起评论");
							setImage(R.id.video_comments_loading_img,
									R.drawable.jiazai);
							if (null == detail_listView) {
								detail_listView = (LinearLayoutForListView) findViewById(R.id.detail_listView);
							}
							loading_not_more = true;
							loading_one = true;
							loading_more = true;
							return;
						}

					}
					loading_more = false;

				} else {
					if (commentnData.size() > 1) {
						setItem(R.id.video_comments_loading_text, "收起评论");
						setImage(R.id.video_comments_loading_img,
								R.drawable.jiazai);
						if (null == detail_listView) {
							detail_listView = (LinearLayoutForListView) findViewById(R.id.detail_listView);
						}
						loading_not_more = true;
						loading_one = true;
						loading_more = true;
					} else {

					}
				}

			} else {
				if (commentnData.size() > 1) {
					setItem(R.id.video_comments_loading_text, "收起评论");
					setImage(R.id.video_comments_loading_img, R.drawable.jiazai);
					if (null == detail_listView) {
						detail_listView = (LinearLayoutForListView) findViewById(R.id.detail_listView);
					}
					loading_not_more = true;
					loading_one = true;
					loading_more = true;
				} else {

				}
			}
		}
	}

	/**
	 * 发表评论
	 * 
	 * @author 张占忠
	 * 
	 */
	private class GetCommentPublish extends
			AsyncTask<String, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_V_VIDEO_PUBLISH_COMMENTS), video_Id,
					params[0]);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			onClikCommentn = true;
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					comments_edit.setText("");
					HashMap<String, String> map = DataRequest
							.getHashMapFromJSONObjectString(result.get("data"));
					if ("2".equals(map.get("comm_status"))) {
						if (findViewById(R.id.detail_listView).getVisibility() == View.GONE) {
							findViewById(R.id.detail_listView).setVisibility(
									View.VISIBLE);
						}
						if (findViewById(R.id.detail_text).getVisibility() == View.VISIBLE) {
							findViewById(R.id.detail_text).setVisibility(
									View.GONE);
						}
						makeText("评论成功");
						commentnData.add(0, map);
						detail_listView.removeAllViews();
						setDetailListView(detail_listView);
						TextView comments_text = (TextView) findViewById(R.id.comments_text);

						int comments = Integer.valueOf(mapVideo
								.get("ping_count")) + 1;
						mapVideo.put("ping_count", "" + comments);
						comments_text.setText("" + comments);
					} else {
						makeText("评论待审核");
					}
				}
			} else {
				makeText("评论内容发送失败");
			}
		}
	}

	protected String getStart(int size) {
		String result = "";
		if (size == 0) {
			return "1";
		} else if (size < 10 && size > 0)
			return "2";
		else if (size / 10 > 0) {
			return ((size % 10 > 0) ? (size / 10 + 2) : (size / 10 + 1)) + "";
		}
		return result;
	}

	/**
	 * 
	 * 评论或分享视频后，统计加1.
	 */
	private class SetComments4Share extends
			AsyncTask<String, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			return DataRequest
					.getHashMapFromUrl_Base64(
							getUrl(AppConfig.NEW_V_SET_SHARE_PLAY), video_Id,
							params[0]);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				// makeText(result.get("msg"));
			} else {
			}
		}
	}

	@Override
	public void onBackPressed() {

		if (mShare != null) {
			if (mShare.getIsCancle())
				setResult(RESULT_OK);
			if (mShare.isHsow()) {
				if (findViewById(R.id.author_web).getVisibility() == View.VISIBLE) {
					mShare.closeHsow();
				} else if (findViewById(R.id.share_btn_layout).getVisibility() == View.VISIBLE) {
					if (mShare.onAnimationDisappear) {
						mShare.showLogin(true);
					}

				} else {
					finish();
				}
			} else {
				finish();
			}
		} else {
			finish();
		}
		if (!StringUtil.isEmpty(getIntent().getStringExtra("push"))) {
			if (ActivityUtil.getInstance().isclose("ModelActivity")) {
				startActivity(new Intent(mContext, ModelActivity.class));
			}
		}
	}

	private String countShare;

	/**
	 * 获取分享内容的接口
	 * 
	 * @author hanpengyuan
	 * 
	 */
	private class GetMsg extends AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			HashMap<String, String> way = DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.VIDEO_SHARE_COUNT), video_Id);
			if (way != null) {
				if ("1".equals(way.get("code"))) {
					HashMap<String, String> map = DataRequest
							.getHashMapFromJSONObjectString(way.get("data"));
					if (map != null) {
						countShare = map.get("count");
					}
				}
			}
			return null;
		}

	}

	private String orShare;

	/**
	 * 取消追或者未追的状态。
	 * 
	 * @author hanpengyuan
	 * 
	 */
	private class setVideoChaesOr extends
			AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			if ("1".equals(orShare)) {
				orShare = "0";
			} else if ("0".equals(orShare)) {
				orShare = "1";
			}
			return DataRequest
					.getHashMapFromUrl_Base64(
							getUrl(AppConfig.NEW_UN_VIDEO_GET_SHARE), video_Id,
							orShare);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {

				if ("1".equals(result.get("code"))) {
					right_textview.setVisibility(View.VISIBLE);
					if ("1".equals(orShare)) {
						right_textview.setText("不追了");
					} else {
						right_textview.setText("追");
					}
				}

			} else {
				if ("1".equals(orShare)) {
					orShare = "0";
				} else if ("0".equals(orShare)) {
					orShare = "1";
				}
			}

			onClikright_textview = true;
		}

	}

	/**
	 * 获取视频追或未追的状态。
	 * 
	 * @author hanpengyuan
	 * 
	 */
	private class CheckVideoChaes extends
			AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_UN_GET_VIDEO_COLLECT), video_Id);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null && "1".equals(result.get("code"))) {
				Logger.i("视频状态数据：" + result.toString());
				orShare = result.get("data");
				right_textview.setVisibility(View.VISIBLE);
				if ("1".equals(orShare)) {
					right_textview.setText("不追了");
				} else {
					right_textview.setText("追");
				}
			}
		}
	}

	// ----------------------设置 城市地区经销商信息。--------------------------------

	/**
	 * 获取所在城市，地区，经销商。
	 * 
	 * @author ZhangZhanZhong
	 * 
	 */
	private class Get_City_List extends
			AsyncTask<String, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			dealers = new ArrayList<HashMap<String, String>>();
			String cityString = DataRequest
					.getStringFromURL_Base64(getUrl(AppConfig.NEW_V_GET_DEALER_AREA));
			if (!StringUtil.isEmpty(cityString)) {
				mCache.put(Constants.DEALER_AREA, cityString);
				return DataRequest.getHashMapFromJSONObjectString(cityString);
			}

			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null && "1".equals(result.get("code"))) {
				new Set_City_Echo().execute(DataRequest
						.getArrayListFromJSONArrayString(result.get("data")));
			} else {
				String data = mCache.getAsString(Constants.DEALER_AREA);
				if (!StringUtil.isEmpty(data))
					new Set_City_Echo().execute(DataRequest
							.getArrayListFromJSONArrayString(DataRequest
									.getHashMapFromJSONObjectString(data).get(
											"data")));

			}

		}
	}

	private class Set_City_Echo
			extends
			AsyncTask<ArrayList<HashMap<String, String>>, Void, ArrayList<HashMap<String, String>>> {
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				ArrayList<HashMap<String, String>>... params) {
			/**
			 * 需要做地区回显的时候在这个方法里面处理， 给 区域 list2 赋值，现在没有这个需求那就先空出来了，这个异步请求不是没有用的。
			 */
			return params[0];

		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (result != null && result.size() != 0) {
				list1.addAll(result);
				setPopu();
			}
		}

	}

	private void setItem(int pos, TextView tex,
			List<HashMap<String, String>> list12) {
		if (pos >= 0 && pos <= list12.size()) {
			String value = list12.get(pos).get("text");
			Logger.i("value " + value);
			tex.setText(value);
		}
	}

	private void setDealersItem(int pos, TextView tex,
			List<HashMap<String, String>> list12) {
		if (pos >= 0 && pos <= list12.size()) {
			String value = list12.get(pos).get("dealer");
			Logger.i("value " + value);
			tex.setText(value);
		}
	}

	private void setPopu() {
		text1_1 = (TextView) findViewById(R.id.address_1);
		text2_2 = (TextView) findViewById(R.id.address_2);
		test_drive_dealers = (TextView) findViewById(R.id.test_drive_dealers);
		window1 = new SpinerPopWindow(this);
		window1.setKey("text");
		window1.refreshData(list1, 0);
		window1.setItemListener(new IOnItemSelectListener() {
			@Override
			public void onItemClick(int pos) {
				list2.clear();
				list2.addAll(DataRequest.getArrayListFromJSONArrayString(list1
						.get(pos).get("children")));
				text2_2.setText(list2.get(0).get("text"));
				region = list2.get(0).get("text");
				setItem(pos, text1_1, list1);
			}
		});
		window2 = new SpinerPopWindow(this);
		window2.setKey("text");
		window2.refreshData(list2, 0);
		window2.setItemListener(new IOnItemSelectListener() {
			@Override
			public void onItemClick(int pos) {
				dealers.clear();
				dealers.addAll(DataRequest
						.getArrayListFromJSONArrayString(list2.get(pos).get(
								"children")));
				region = list2.get(pos).get("text");
				setItem(pos, text2_2, list2);
			}
		});

		test_drive_dealers_window = new SpinerPopWindow(this);
		test_drive_dealers_window.setKey("dealer");
		test_drive_dealers_window.refreshData(dealers, 0);
		test_drive_dealers_window.setItemListener(new IOnItemSelectListener() {
			@Override
			public void onItemClick(int pos) {
				agency = dealers.get(pos).get("dealer");
				setDealersItem(pos, test_drive_dealers, dealers);
			}
		});

		final RelativeLayout layout1 = (RelativeLayout) findViewById(R.id.address_1_layout);
		final RelativeLayout layout2 = (RelativeLayout) findViewById(R.id.address_2_layout);

		final RelativeLayout layout3 = (RelativeLayout) findViewById(R.id.test_drive_dealers_layout);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.address_1_layout && window1 != null) {
					window1.setWidth(layout1.getWidth());
					window1.showAsDropDown(layout1);
				} else if (v.getId() == R.id.address_2_layout
						&& window2 != null) {
					if (!StringUtil.isEmpty(text1_1.getText().toString())) {
						window2.setWidth(layout2.getWidth());
						window2.showAsDropDown(layout2);
					} else {
						makeText("请先选择省市");
					}
					Logger.i(" 地区点击相应事件： |" + text1_1.getText() + "|");
				} else if (v.getId() == R.id.test_drive_dealers_layout
						&& test_drive_dealers_window != null) {
					if (!StringUtil.isEmpty(text2_2.getText().toString())) {
						test_drive_dealers_window.setWidth(layout3.getWidth());
						test_drive_dealers_window.showAsDropDown(layout3);
					} else {
						makeText("请先选择地区");
					}
					Logger.i(" 经销商事件： |" + text2_2.getText() + "| dealers:"
							+ dealers.toString());
				}
			}
		};
		layout1.setOnClickListener(listener);
		layout2.setOnClickListener(listener);
		layout3.setOnClickListener(listener);
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * 获取试驾信息
	 * 
	 * @author hanpengyuan
	 * 
	 */
	private class GetTestDriveData extends
			AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_V_GET_TEST_INFO), video_Id);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null && "1".equals(result.get("code"))) {
				Logger.i("视频试驾数据：" + result.toString());
				car_pictures = new ArrayList<HashMap<String, String>>();
				car_list = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> map = DataRequest
						.getHashMapFromJSONObjectString(result.get("data"));

				car_list.addAll(DataRequest.getArrayListFromJSONArrayString(map
						.get("vc_lsit")));
				car_pictures.addAll(DataRequest
						.getArrayListFromJSONArrayString(map.get("img_list")));
				if (car_pictures.size() > 1) {
					findViewById(R.id.test_drive_img_layout).setVisibility(
							View.VISIBLE);
					test_drive_img_layout_viewpager = (ChildViewPager) findViewById(R.id.test_drive_img_layout_viewpager);

					setAdImgSize(test_drive_img_layout_viewpager, 0, 0.5f, 1);
					ArrayList<View> views = new ArrayList<View>();
					View view1 = null;
					for (final HashMap<String, String> map1 : car_pictures) {
						view1 = View.inflate(mContext,
								R.layout.gallery_list_item, null);
						ImageView imageView = (ImageView) view1
								.findViewById(R.id.img);
						TextView textView = (TextView) view1
								.findViewById(R.id.textView);
						textView.setVisibility(View.VISIBLE);
						setImgSize(imageView, 0, 0.5f, 1);
						setImageView_(imageView, map1.get("car_img"));
						setImageViewTitle(textView, map1.get("img_title"));
						views.add(view1);
					}
					test_drive_img_layout_viewpager_adapter = new ViewPageAdapter(
							views);
					test_drive_img_layout_viewpager
							.setAdapter(test_drive_img_layout_viewpager_adapter);
					findViewById(R.id.test_drive_img_layout_left_img)
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									test_drive_img_layout_viewpager
											.setCurrentItem(test_drive_img_layout_viewpager
													.getCurrentItem() - 1);
								}
							});

					findViewById(R.id.test_drive_img_layout_right_img)
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									test_drive_img_layout_viewpager
											.setCurrentItem(test_drive_img_layout_viewpager
													.getCurrentItem() + 1);
								}
							});
				}

				setCarPopu();
				// setTimePopu();
			}
		}
	}

	// /**
	// * 获取车系信息
	// *
	// * @author hanpengyuan
	// *
	// */
	// private class GetAgencyCarData extends AsyncTask<String, Void,
	// HashMap<String, String>> {
	// @Override
	// protected HashMap<String, String> doInBackground(String... params) {
	// return
	// DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_UN_GET_AGENCY_CAR),
	// params[0]);
	// }
	//
	// @Override
	// protected void onPostExecute(HashMap<String, String> result) {
	// Logger.i("" + result.toString());
	// if (result != null && "1".equals(result.get("code"))) {
	// car_list.addAll(DataRequest.getArrayListFromJSONArrayString(result.get("data")));
	// }
	// }
	// }

	private void setCarPopu() {
		test_drive_car = (TextView) findViewById(R.id.test_drive_car);

		// test_drive_dealers_window, test_drive_car_window;

		// dealers ,car_list,
		// agency_id = "0", car_id = "0",

		test_drive_car_window = new SpinerPopWindow(this);
		test_drive_car_window.setKey("car_name");
		test_drive_car_window.refreshData(car_list, 0);
		test_drive_car_window.setItemListener(new IOnItemSelectListener() {
			@Override
			public void onItemClick(int pos) {
				car = car_list.get(pos).get("car_name");
				setCarItem(pos, test_drive_car, car_list);
			}
		});

		final RelativeLayout layout2 = (RelativeLayout) findViewById(R.id.test_drive_car_layout);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.test_drive_car_layout
						&& test_drive_car_window != null) {
					test_drive_car_window.setWidth(layout2.getWidth());
					test_drive_car_window.showAsDropDown(layout2);
				}
			}
		};
		layout2.setOnClickListener(listener);
	}

	private void setCarItem(int pos, TextView tex,
			List<HashMap<String, String>> list12) {
		if (pos >= 0 && pos <= list12.size()) {
			String value = list12.get(pos).get("car_name");
			Logger.i("value " + value);
			tex.setText(value);
		}
	}

	private void setTimePopu() {
		test_drive_buycar_time = (TextView) findViewById(R.id.test_drive_buycar_time);
		test_drive_time_window = new SpinerPopWindow(this);
		test_drive_time_window.setKey("dict_name");
		test_drive_time_window.refreshData(advance_time, 0);
		test_drive_time_window.setItemListener(new IOnItemSelectListener() {
			@Override
			public void onItemClick(int pos) {
				time = advance_time.get(pos).get("dict_name");
				setTimeItem(pos, test_drive_buycar_time, advance_time);
			}
		});
		final RelativeLayout layout1 = (RelativeLayout) findViewById(R.id.test_drive_buycar_time_layout);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.test_drive_buycar_time_layout
						&& test_drive_time_window != null) {
					test_drive_time_window.setWidth(layout1.getWidth());
					test_drive_time_window.showAsDropDown(layout1);
				}
			}
		};
		layout1.setOnClickListener(listener);

	}

	private void setTimeItem(int pos, TextView tex,
			List<HashMap<String, String>> list12) {
		if (pos >= 0 && pos <= list12.size()) {
			String value = list12.get(pos).get("dict_name");
			Logger.i("value " + value);
			tex.setText(value);
		}
	}

	/**
	 * 获取购车时间
	 * 
	 * @author hanpengyuan
	 * 
	 */
	private class GetShoppingTimeData extends
			AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			advance_time = new ArrayList<HashMap<String, String>>();
			return DataRequest
					.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_GET_DICT_LIST));
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null && "1".equals(result.get("code"))) {
				if (!StringUtil.isEmpty(result.get("data"))) {
					advance_time.addAll(DataRequest
							.getArrayListFromJSONArrayString(DataRequest
									.getArrayListFromJSONArrayString(
											result.get("data")).get(0)
									.get("children")));
				}

				setTimePopu();
			}
		}
	}

	/**
	 * 提交试驾资料。
	 * 
	 * @author hanpengyuan
	 * 
	 */
	private class SaveTestDrive extends
			AsyncTask<String, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_V_TEST_DRIVER_SAVE), params[0],
					params[1], params[2], params[3], params[4], params[5],
					params[6], params[7]);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			Logger.i("" + result.toString());
			if (result != null && "1".equals(result.get("code"))) {
				makeText("保存成功");

			} else if ("-1".equals(result.get("code"))) {
				makeText("您的手机号已参加活动，敬请期待！");
			}
		}
	}

	protected void setAdImgSize(View item, int del, float size, int n) {
		int width = (dm.widthPixels - (int) (del * scale + 0.5f)) / n;
		LayoutParams params = item.getLayoutParams();
		if (params != null) {
			params.width = width;
			params.height = (int) (width * size);
		}
	}

	protected void setImageView_(ImageView imageView, String url) {
		if (!StringUtil.isEmpty(url)) {
			imageView.setVisibility(View.VISIBLE);
			LoadBitmap.getIntence().loadImage(url.replace("\\", ""), imageView);
		} else {
			BitmapUtil.setImageResource(imageView, R.drawable.head_img_bg);
		}
	}

	private void setImageViewTitle(TextView textView, String text) {
		textView.setText(text);

	}

	// 判断视频可不可以播放
	private class SetCommentsIsPlay extends
			AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {

			return DataRequest.getHashMapFromUrl_Base64(
					getUrl(AppConfig.NEW_V_VIDEO_MY_SEARCH_PLAY), video_Id);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			super.onPostExecute(result);
			if ("1".equals(result.get("code"))) {
				Intent intent = null;
				Uri uri = Uri.parse(mapVideo.get("video_url"));
				if ("2".equals(mapVideo.get("is_sys"))) {
					intent = new Intent(mContext, AdWebActivity.class);
					intent.putExtra("url", mapVideo.get("video_url"));
					intent.putExtra("title", mapVideo.get("name"));
					mContext.startActivity(intent);
				} else {
					intent = new Intent(Intent.ACTION_VIEW);
					String type = "video/*";
					intent.setDataAndType(uri, type);
					startActivity(intent);
				}
			} else {
				makeText(result.get("msg"));
			}

		}

	}

}
