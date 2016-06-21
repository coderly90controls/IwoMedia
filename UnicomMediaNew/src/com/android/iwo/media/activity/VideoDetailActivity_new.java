package com.android.iwo.media.activity;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iwo.media.action.ActivityUtil;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.MyGridView;
import com.android.iwo.media.action.MyTextView;
import com.android.iwo.media.apk.activity.R;
import com.android.iwo.media.dao.DataBaseDao;
import com.android.iwo.media.dao.Download_Service;
import com.android.iwo.media.dao.PublicUtils;
import com.android.iwo.media.view.CircleImageView;
import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.view.LinearLayoutForListView;
import com.android.iwo.media.view.textview.AlignTextView;
import com.android.iwo.users.UserLogin;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.DateUtil;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.NetworkUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * 使用视频详情页 Intent需要传递String类型参数 , 键值为"video_id" ，“head_img” 显示用户评论头像，"ch_name"标题
 * 传入 “edit_id” //获取小编头像
 * 
 * @author
 */
public class VideoDetailActivity_new extends BaseActivity implements OnClickListener {
	private String video_Id;
	private Map<String, String> mapVideo = new HashMap<String, String>();
//	private Share mShare;
	private String head_img;
	private LinearLayoutForListView detail_listView;

	protected IwoAdapter mAdapter;
	private ArrayList<HashMap<String, String>> commentnData = new ArrayList<HashMap<String, String>>();
	private boolean loading_more = true;// 评论 判断是否是加载1条
	private boolean loading_one = true; // 评论 判断是否是第一次加载
	private boolean loading_not_more = false; // 判断；评论是否可收起
	private boolean iss_s = true; // 定义一个用来控制加载更多 和收起评论的标志
	private String ping_count, video_desc;
	private boolean introduction_all = true;
	private EditText comments_edit;
	private ScrollView video_detail_scroll;
	private TextView synopsis;
	private InputMethodManager imm;
	private String audit_status;
	private boolean onClikCommentn = true, onClikright_textview = true;
	private String iwoShareType;
	// private TextView right_textview;
	private ImageView store_img;// 收藏图片

	private boolean isFree = true; // 判断是不是免费 默认设为免费
	private int isState;
	private int pp = 1;
	// 加入试驾有礼
	private Button testDriveForpresent;
	private List<String> text_id = new ArrayList<String>();// 聚集颜色改变
	private ArrayList<HashMap<String, String>> Series = new ArrayList<HashMap<String, String>>();
	private int number_m;
	private String path_Series = "http://114.247.0.113/v/10/12/201501041649.ts";
	private boolean idMore = true;// 如果有剧集 显示更多或收起的标志
	private int adnumber;
	private long lastTimemGoToPlayListListener;// 频繁点之
	private static long CLICK_INTERVAL = 800;
	private boolean isOnClick = true;

	String appID = "wx7369dbf242da5ae3";
	String appSecret = "e7fe1c897e07e68118b264ddab35ebf4";
	final UMSocialService mController_share = UMServiceFactory.getUMSocialService("com.umeng.share");

	private boolean isDowload = false;// 测试下载 fcz

	/**
	 * @see 过滤掉特殊字符
	 * @param str
	 *            传入的字符串
	 * @return
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_new);

		video_Id = this.getIntent().getStringExtra("video_id");
		edit_id = this.getIntent().getStringExtra("edit_id");
		is_ad = this.getIntent().getStringExtra("is_ad");
		boolean getLogInState = GetLogInState();
		Logger.i("登陆方式" + getLogInState);
		Logger.i("视频详情ID" + video_Id + "小编ID：" + edit_id+"广告进来的："+is_ad);
		testDriveForpresent = (Button) findViewById(R.id.id_testDriveForpresent);
		testDriveForpresent.setOnClickListener(this);
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		init();
		new GetEditImg().execute();//获取小编
		new GetUserData().execute();//
		new GetJJ().execute();//获取剧集
//		new GetMsg().execute();
		new GetCommentData().execute();
		new CheckVideoChaes().execute();

	}

	/**
	 * 
	 * 点赞啊
	 * 
	 * @author Administrator
	 * 
	 */
	private class GetDate_zan extends AsyncTask<String, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(String... params) {

			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.REQUEST_DETAIL_COMMENT_ZAN), params[0]);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			super.onPostExecute(result);
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					makeText("赞成功");
				} else if ("-1".equals(result.get("code"))) {
					makeText("已赞过");
				}
			}
		}

	}

	/*
	 * 分享
	 */
	void myClickShare() {
		findViewById(R.id.new_share_layout).setVisibility(View.VISIBLE);
		isOnClick = false;
		// 设置分享图片, 参数2为图片的url地址
		mController_share.setShareMedia(new UMImage(mContext, mapVideo.get("img_url_2")));
		// 分享内容后的跳转的链接：http://www.pcmglobe.com/iwovideo/index.html
		// 分享内容后的跳转的链接：http://iwotv.cn/share/video_info?id=103464&ch_id=146
		// ====》 wap地址
		mController_share.setShareContent(mapVideo.get("name") + "：" + mapVideo.get("note") + "http://iwotv.cn/share/video_info?id="
				+ mapVideo.get("id") + "&ch_id=" + mapVideo.get("ch_id"));// mapVideo.get("video_url")
		findViewById(R.id.share_wechat).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 添加微信平台
				UMWXHandler wxHandler = new UMWXHandler(mContext, appID, appSecret);
				wxHandler.addToSocialSDK();
				// 设置微信好友分享内容
				WeiXinShareContent weixinContent = new WeiXinShareContent();
				// 设置分享文字
				weixinContent.setShareContent(mapVideo.get("name") + "：" + mapVideo.get("note") + "http://iwotv.cn/share/video_info?id="
						+ mapVideo.get("id") + "&ch_id=" + mapVideo.get("ch_id"));
				// 设置title
				weixinContent.setTitle("爱握视频");
				// 设置分享内容跳转URL
				weixinContent.setTargetUrl("http://iwotv.cn/share/video_info?id=" + mapVideo.get("id") + "&ch_id=" + mapVideo.get("ch_id"));//
				// 设置分享图片
				weixinContent.setShareImage(new UMImage(mContext, mapVideo.get("img_url_2")));
				mController_share.setShareMedia(weixinContent);
				shareWithPlatform(mContext, SHARE_MEDIA.WEIXIN);

			}

		});
		findViewById(R.id.share_wefriends).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 添加微信朋友圈
				UMWXHandler wxCircleHandler = new UMWXHandler(mContext, appID, appSecret);
				wxCircleHandler.setToCircle(true);
				wxCircleHandler.addToSocialSDK();
				// 设置微信朋友圈分享内容
				CircleShareContent circleMedia = new CircleShareContent();
				circleMedia.setShareContent(mapVideo.get("name") + "：" + mapVideo.get("note") + "http://iwotv.cn/share/video_info?id="
						+ mapVideo.get("id") + "&ch_id=" + mapVideo.get("ch_id"));
				// 设置朋友圈title
//				circleMedia.setTitle("爱握视频");
				circleMedia.setTitle(mapVideo.get("name"));
				circleMedia.setShareImage(new UMImage(mContext, mapVideo.get("img_url_2")));
				circleMedia.setTargetUrl("http://iwotv.cn/share/video_info?id=" + mapVideo.get("id") + "&ch_id=" + mapVideo.get("ch_id"));// mapVideo.get("video_url")
				mController_share.setShareMedia(circleMedia);
				shareWithPlatform(mContext, SHARE_MEDIA.WEIXIN_CIRCLE);

			}

		});

		findViewById(R.id.share_sina).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				mController_share.getConfig().setSsoHandler(new SinaSsoHandler());// 新浪sso免登陆代码
				shareWithPlatform(mContext, SHARE_MEDIA.SINA);

			}
		});
		findViewById(R.id.share_iwo_new).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String smsto = "smsto:";
				Uri smsToUri = Uri.parse(smsto);
				Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
				intent.putExtra("sms_body", mapVideo.get("name") + "：" + mapVideo.get("note") + "http://iwotv.cn/share/video_info?id="
						+ mapVideo.get("id") + "&ch_id=" + mapVideo.get("ch_id"));
				startActivity(intent);
			}
		});
		findViewById(R.id.share_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				findViewById(R.id.new_share_layout).setVisibility(View.GONE);
				isOnClick = true;
			}
		});

	}

	/**
	 * 这是一个根据不同平台去分享的方法
	 * 
	 * @author zly
	 * @param context
	 * @param media
	 */
	protected void shareWithPlatform(Context context, SHARE_MEDIA media) {
		// TODO Auto-generated method stub
		mController_share.postShare(context, media, new SnsPostListener() {
			@Override
			public void onStart() {
				Toast.makeText(VideoDetailActivity_new.this, "开始分享", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
				if (eCode == 200) {
					Toast.makeText(VideoDetailActivity_new.this, "分享成功", Toast.LENGTH_SHORT).show();
				} else {
					String eMsg = "";
					if (eCode == -101) {
						eMsg = "没有授权";
					}
					Toast.makeText(VideoDetailActivity_new.this, "分享失败", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void init() {
		vi_syyl = (ImageButton) findViewById(R.id.vi_syyl);// 试驾有礼
		vi_syyl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(VideoDetailActivity_new.this, DriveTest.class);
				intent.putExtra("web_url", mapVideo.get("active_url"));
				intent.putExtra("title_name", mapVideo.get("name"));
				startActivity(intent);

			}
		});
		gr = (MyGridView) findViewById(R.id.de_gridview);
		adapter = new MyAdapter();
		gr.setAdapter(adapter);
		gr.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int pos, long arg3) {
				Logger.i("第" + (pos + 1) + "集");
				String str_id = Series.get(pos).get("id");
				if (!text_id.contains(str_id)) {
					text_id.clear();
					text_id.add(str_id);
					adapter.notifyDataSetChanged();
				}
				if (GetLogInState()) {
					if (!setUserLogin()) {
						return;
					}
					Logger.i("传进来的url：" + Series.get(pos).get("series_url"));

					if (isFree) {
						mapVideo.get("play_count");
						int play = Integer.valueOf(mapVideo.get("play_count")) + 1;
						mapVideo.put("play_count", "" + play);
						setItem(R.id.play_text, "" + play);
						isState = 0;
						number_m = pos;
						new SetComments4Share().execute("2");
						new SetCommentsIsPlay().execute(video_Id, "1");
						// 后面带参数来区别剧集和普通的播放
					} else {
						isState = 1;
						new SetCommentsIsPlay().execute(video_Id, "1");
					}
					;
				} else {
					IsLogin();
					ChaneLoginState(new LoginInterface() {

						@Override
						public void Login() {
							if (!AppConfig.ISLOIN) {
								playActivityLog();
								return;
							}
							if (isFree) {
								mapVideo.get("play_count");
								int play = Integer.valueOf(mapVideo.get("play_count")) + 1;
								mapVideo.put("play_count", "" + play);
								setItem(R.id.play_text, "" + play);
								isState = 0;
								number_m = pos;
								new SetComments4Share().execute("2");
								new SetCommentsIsPlay().execute(video_Id, "1");
							} else {
								isState = 1;
								new SetCommentsIsPlay().execute(video_Id, "1");
							}
							;
						}
					});
				}
			}

		});

		// setBack_text(new OnClickListener() { //文字和图片返回的区别
		// public void onClick(View v) {
		// onBackPressed();
		// }
		// });
		setBack(null);
		// right_textview = (TextView) findViewById(R.id.right_textview);
		store_img = (ImageView) findViewById(R.id.right_left_img1);
		// setMode(getMode());
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
					imm.hideSoftInputFromWindow(comments_edit.getWindowToken(), 0);
					findViewById(R.id.comments_popup).setVisibility(View.GONE);

				}
				return false;
			}
		});

	}

	@SuppressLint("NewApi")
	private void setVideoDetail(Map<String, String> map) {

		if (getUid().equals(map.get("userid"))) {

			if ("0".equals(map.get("share_mark"))) {

				iwoShareType = "1";
			} else {
				iwoShareType = "0";
			}
			// mShare.setIwoShareShow(true);
			// mShare.setIwoShareType(iwoShareType);
			// mShare.setShareVideoID(video_Id);
		}
		// else {
		// mShare.setIwoShareShow(false);
		// }

		// if (mShare != null) {
		// if (!StringUtil.isEmpty(countShare)) {
		// mShare.setCon(map.get("name") + ":" + map.get("note")
		// // + "http://wap.iwo.mokacn.com:8080/share/video_info?id="+
		// // video_Id);
		// + map.get("video_url"));
		// }
		// Log.i("*****zly*****", String.valueOf(map));
		// mShare.setShareVideoID(video_Id);
		// }
		// 设计标题

		setTitle(map.get("name"));

		// (!StringUtil.isEmpty(getIntent().getStringExtra("ch_name"))) {
		// setTitle(getIntent().getStringExtra("ch_name"));
		// }
		// findViewById(R.id.title_img).setVisibility(View.VISIBLE);
		setItem(R.id.synopsis, map.get("video_desc"));
		video_desc = map.get("video_desc");
		ping_count = map.get("ping_count");

		// TODO
		if (!StringUtil.isEmpty(map.get("name"))) {
			setItem(R.id.user_name, map.get("name"));
		}
		setItem(R.id.comments_text, StringUtil.isEmpty(ping_count) ? "0" : map.get("ping_count"));
		setItem(R.id.share_text, StringUtil.isEmpty(map.get("share_count")) ? "0" : map.get("share_count"));
		LoadBitmap bitmap = new LoadBitmap();
		ImageView big_img = (ImageView) findViewById(R.id.item_img);
		// --------------播放点击--------------------
		findViewById(R.id.detail_paly_layout).setOnClickListener(this);

		setImgSize(big_img, 14, 27 / 48.0f, 1);
		bitmap.loadImage(map.get("img_url_2"), big_img);
		big_img.setOnClickListener(this);

		big_img.setTag(map.get("video_url"));

		setItem(R.id.play_text, StringUtil.isEmpty(map.get("play_count")) ? "0" : map.get("play_count"));
		setItem(R.id.video_introduction_text1, "  " + map.get("video_desc"));
		setItem(R.id.video_introduction_text2, "  " + map.get("video_desc"));
		// "推荐理由"-------
		setItemText1(R.id.recommended_reason_layout, R.id.recommended_reason_text, map, "note");

		// 设置头部
		ImageView userImg = (ImageView) findViewById(R.id.friends_img);
		if (!StringUtil.isEmpty(mapVideo.get("img_url"))) {
			bitmap.loadImage(map.get("img_url"), userImg);
		}

		// TextView title_name = (TextView) findViewById(R.id.video_title_name);
		// title_name.setText(map.get("name"));
		// 简介
		setItemText(R.id.vi_de_type_layout, R.id.vi_de_type_text, map, "video_type");
		setItemText(R.id.vi_de_year_layout, R.id.vi_de_year_text, map, "year");
		setItemText(R.id.vi_de_man_layout, R.id.vi_de_man_text, map, "suit_people");
		setItemText(R.id.vi_de_director_layout, R.id.vi_de_director_text, map, "director");
		setItemText(R.id.vi_de_actor_layout, R.id.vi_de_actor_text, map, "actor");
		LinearLayout luse_null = (LinearLayout) findViewById(R.id.de_luse_null);
		if (StringUtil.isEmpty(map.get("video_type")) && StringUtil.isEmpty(map.get("year")) && StringUtil.isEmpty(map.get("suit_people"))
				&& StringUtil.isEmpty(map.get("director")) && StringUtil.isEmpty(map.get("actor"))) {
			luse_null.setVisibility(View.GONE);
		}

	}

	private void setItemText(int Layout_id, int Text_id, Map<String, String> map_new, String type) {
		if (!StringUtil.isEmpty(map_new.get(type))) {
			TextView te = (TextView) findViewById(Text_id);
			te.setText(map_new.get(type));
		} else {
			findViewById(Layout_id).setVisibility(View.GONE);
		}

	}

	@SuppressWarnings("unused")
	private void setItemText1(int Layout_id, int Text_id, Map<String, String> map_new, String type) {
		if (!StringUtil.isEmpty(map_new.get(type))) {
			AlignTextView te = (AlignTextView) findViewById(Text_id);
			te.setText("      " + map_new.get(type).trim());
			findViewById(Layout_id).setVisibility(View.VISIBLE);
		} else {
			findViewById(Layout_id).setVisibility(View.GONE);
		}

	}

	/**
	 * 设置界面中可点击的组件
	 */
	private void setOnClickView() {
		findViewById(R.id.video_introduction_loading).setOnClickListener(this);
		findViewById(R.id.video_comments_loading).setOnClickListener(this);
		findViewById(R.id.user_comments).setOnClickListener(this);
		// ------------评论点击----------------------
		findViewById(R.id.detail_comment_layout).setOnClickListener(this);
		// ------------分享点击---------------------------
		findViewById(R.id.detail_share_layout).setOnClickListener(this);
		findViewById(R.id.user_share).setOnClickListener(this);
		findViewById(R.id.comments_popup_text).setOnClickListener(this);
		findViewById(R.id.comments_popup).setOnClickListener(this);
		// 注意：取消了视频简介显示dialog的点击事件
		// findViewById(R.id.synopsis).setOnClickListener(this);

		findViewById(R.id.isplay_ms_look).setOnClickListener(this);
		findViewById(R.id.isplay_ms_share).setOnClickListener(this);
		findViewById(R.id.isplay_ms_money).setOnClickListener(this);
		findViewById(R.id.isplay_ms_back).setOnClickListener(this);
		series_img = (ImageView) findViewById(R.id.de_gengduo);
		series_img.setOnClickListener(this);
		// right_textview.setOnClickListener(this);
		store_img.setOnClickListener(this);
	}

	/**
	 * 设置黑夜白天模式的,的区别。
	 * 
	 * @param mode
	 */
	// private void setMode(String mode) {
	// // setMode_BG(R.id.video_comments_bottom);
	// setMode_BG(R.id.video_introduction_line);
	// setMode_BG(R.id.video_comments_line);
	// if ("day".equals(mode)) {
	// setImage(R.id.comments, R.drawable.d_pinglun);// 评论图片
	// setImage(R.id.play, R.drawable.d_bofang);// 评论图片
	// setImage(R.id.share, R.drawable.d_fenxiang);// 评论图片
	// setImage(R.id.video_introduction_img, R.drawable.d_jianjie);// 评论图片
	// setImage(R.id.video_comments_img, R.drawable.d_pinglunbiao);// 评论图片
	// //
	// findViewById(R.id.comments_popup_text).setBackgroundColor(getResources().getColor(R.color.comm_green_color));
	//
	// } else if ("night".equals(mode)) {
	// setImage(R.id.comments, R.drawable.n_pinglun);// 评论图片
	// setImage(R.id.play, R.drawable.n_bofang);// 评论图片
	// setImage(R.id.share, R.drawable.n_fenxiang);// 评论图片
	//
	// setImage(R.id.video_introduction_img, R.drawable.n_jianjie);// 评论图片
	// setImage(R.id.video_comments_img, R.drawable.n_pinglunbiao);// 评论图片
	// //
	// findViewById(R.id.comments_popup_text).setBackgroundColor(getResources().getColor(R.color.comm_pink_color));
	// }
	// }

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

				final Map<String, String> map = commentnData.get(position);
				v = mInflater.inflate(R.layout.view_comments_item_new, null);
				TextView nickName = (TextView) v.findViewById(R.id.comm_user_name);
				TextView content = (TextView) v.findViewById(R.id.user_comment);
				TextView createTime = (TextView) v.findViewById(R.id.comment_time);
				final TextView zanCount = (TextView) v.findViewById(R.id.count_zan);
				CircleImageView img = (CircleImageView) v.findViewById(R.id.user_img);

				final String zan_id = map.get("id");// 加赞后加的id
				String user_nick = map.get("user_nick");
				if (!StringUtil.isEmpty(user_nick)) {
					if (user_nick.length() > 6) {
						nickName.setText(user_nick.substring(0, 5));
					} else {
						nickName.setText(user_nick);
					}
				}
				content.setText(map.get("comm_content"));
				createTime.setText(DateUtil.getTime(map.get("create_time")));

				Logger.i("我是头像：" + map.get("head_img"));
				if (!StringUtil.isEmpty(map.get("head_img"))) {
					LoadBitmap.getIntence().loadImage(map.get("head_img"), img);
				}
				if (StringUtil.isEmpty(map.get("praise_count"))) {
					zanCount.setText("0");
				} else {
					zanCount.setText(map.get("praise_count"));
				}
				// 背景颜色
				String praise_ok = map.get("praise_ok");
				if ("0".equals(praise_ok)) { // 没赞
					zanCount.setBackgroundResource(R.drawable.zan_default);
				} else {
					zanCount.setBackgroundResource(R.drawable.zan_success);
				}

				zanCount.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						String str_ok = map.get("praise_ok");
						if ("0".equals(str_ok) || "".equals(str_ok)) {
							String str_number = map.get("praise_count");
							if (!StringUtil.isEmpty(str_number)) {
								int integer_prasise = Integer.valueOf(str_number).intValue() + 1;
								zanCount.setText(integer_prasise + "");
							} else {
								zanCount.setText("1");
							}
							zanCount.setBackgroundResource(R.drawable.zan_success);
							new GetDate_zan().execute(zan_id);
						} else {
							makeText("已赞过");
						}

					}
				});

				return v;
			}
		};
		if (loading_not_more) {
			loading_not_more = false;
		}
		listView.setAdapter(mAdapter);

	}

	CommonDialog commonDialog = null;

	// 判断登陆-不跳转
	protected boolean setUserLogin_san() {
		if (!StringUtil.isEmpty(getPre("user_name"))) {
			if (!StringUtil.isEmpty(getPre("user_pass"))) {

				return true;
			}
		}
		if (!StringUtil.isEmpty(getPre("Umeng"))) {
			return true;
		}

		return false;

	}

	@Override
	public void onClick(View v) {
		int[] clikViews = null;
		if (!isOnClick) {
			return;
		}
		switch (v.getId()) {
		case R.id.item_img:

			// 测试下载 fcz
			if (isDowload) {
				// 首先判断数据库有没有这个视频的ID
				if (NetworkUtil.isWIFIConnected(mContext)) {
					DownVideo(video_Id, mapVideo.get("ch_id"), mapVideo.get("name"), mapVideo.get("video_url"), mapVideo.get("img_url_2"),
							mapVideo.get("ping_fen"));
				} else {
					makeText("只在wifi下支持下载");
				}
				return;
			}

			long time = System.currentTimeMillis();
			if (time - lastTimemGoToPlayListListener < CLICK_INTERVAL) {
				return;
			}
			lastTimemGoToPlayListListener = time;

			if (GetLogInState()) {
				if (!setUserLogin()) {
					return;
				}
				if (isFree) {
					mapVideo.get("play_count");
					int play = Integer.valueOf(mapVideo.get("play_count")) + 1;
					mapVideo.put("play_count", "" + play);
					setItem(R.id.play_text, "" + play);
					isState = 0;
					new SetComments4Share().execute("2");
					new SetCommentsIsPlay().execute(video_Id, "2");
				} else {
					isState = 1;
					new SetCommentsIsPlay().execute(video_Id, "2");
				}
			} else {
				IsLogin();
				ChaneLoginState(new LoginInterface() {

					@Override
					public void Login() {
						if (!AppConfig.ISLOIN) {
							playActivityLog();
							return;
						}
						if (isFree) {
							mapVideo.get("play_count");
							int play = Integer.valueOf(mapVideo.get("play_count")) + 1;
							mapVideo.put("play_count", "" + play);
							setItem(R.id.play_text, "" + play);
							isState = 0;
							new SetComments4Share().execute("2");
							new SetCommentsIsPlay().execute(video_Id, "2");
						} else {
							isState = 1;
							new SetCommentsIsPlay().execute(video_Id, "2");
						}
					}
				});
			}
			break;
		case R.id.detail_paly_layout:
			if (GetLogInState()) { // 手机登陆
				if (!setUserLogin()) {
					return;
				}
				if (isFree) {
					mapVideo.get("play_count");
					int play = Integer.valueOf(mapVideo.get("play_count")) + 1;
					mapVideo.put("play_count", "" + play);
					setItem(R.id.play_text, "" + play);
					isState = 0;
					new SetComments4Share().execute("2");
					new SetCommentsIsPlay().execute(video_Id, "2");
				} else {
					isState = 1;
					new SetCommentsIsPlay().execute(video_Id, "2");
				}
			} else {
				IsLogin();
				ChaneLoginState(new LoginInterface() {

					public void Login() {
						if (!AppConfig.ISLOIN) {
							playActivityLog();
							return;
						}
						if (isFree) {
							mapVideo.get("play_count");
							int play = Integer.valueOf(mapVideo.get("play_count")) + 1;
							mapVideo.put("play_count", "" + play);
							setItem(R.id.play_text, "" + play);
							isState = 0;
							new SetComments4Share().execute("2");
							new SetCommentsIsPlay().execute(video_Id, "2");
						} else {
							isState = 1;
							new SetCommentsIsPlay().execute(video_Id, "2");
						}
					}
				});
			}

			break;
		case R.id.user_comments:// 底部评论
			if (GetLogInState()) {
				if (!setUserLogin()) {
					return;
				}
				comments_edit.setFocusable(true);
				comments_edit.setFocusableInTouchMode(true);
				comments_edit.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(comments_edit, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
				findViewById(R.id.comments_popup).setVisibility(View.VISIBLE);
			} else {
				IsLogin();
				ChaneLoginState(new LoginInterface() {

					@Override
					public void Login() {
						if (!AppConfig.ISLOIN) {
							playActivityLog();
							return;
						}
						comments_edit.setFocusable(true);
						comments_edit.setFocusableInTouchMode(true);
						comments_edit.requestFocus();
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.showSoftInput(comments_edit, InputMethodManager.RESULT_SHOWN);
						imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
						findViewById(R.id.comments_popup).setVisibility(View.VISIBLE);

					}
				});
			}
			break;
		// ------------视频底部评论点击----------------
		case R.id.detail_comment_layout:// 底部评论
			if (GetLogInState()) {
				if (!setUserLogin()) {
					return;
				}
				comments_edit.setFocusable(true);
				comments_edit.setFocusableInTouchMode(true);
				comments_edit.requestFocus();
				InputMethodManager immn = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				immn.showSoftInput(comments_edit, InputMethodManager.RESULT_SHOWN);
				immn.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
				findViewById(R.id.comments_popup).setVisibility(View.VISIBLE);
			} else {
				IsLogin();
				ChaneLoginState(new LoginInterface() {

					@Override
					public void Login() {
						if (!AppConfig.ISLOIN) {
							playActivityLog();
							return;
						}
						comments_edit.setFocusable(true);
						comments_edit.setFocusableInTouchMode(true);
						comments_edit.requestFocus();
						InputMethodManager immn = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						immn.showSoftInput(comments_edit, InputMethodManager.RESULT_SHOWN);
						immn.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
						findViewById(R.id.comments_popup).setVisibility(View.VISIBLE);

					}
				});
			}
			break;
		case R.id.comments_popup_text:// 弹框中的评论按钮
			if ("1".equals(audit_status)) {
				if (StringUtil.isEmpty(comments_edit.getText().toString().trim())) {
					makeText("评论内容不能为空");
				} else {
					this.imm.hideSoftInputFromWindow(comments_edit.getWindowToken(), 0);
					findViewById(R.id.comments_popup).setVisibility(View.GONE);
					if (onClikCommentn) {
						onClikCommentn = false;
						new GetCommentPublish().execute(comments_edit.getText().toString());
					}

				}
			} else {
				makeText("该视频暂未审核通过，无法评论");
			}
			break;

		case R.id.user_share:// 分享
			if (GetLogInState()) {
				if (!setUserLogin()) {
					return;
				}
				if ("1".equals(audit_status)) {
					myClickShare();

				} else {
					makeText("该视频暂未审核通过，无法分享");
				}
			} else {
				IsLogin();
				ChaneLoginState(new LoginInterface() {

					@Override
					public void Login() {
						if (!AppConfig.ISLOIN) {
							playActivityLog();
							return;
						}
						if ("1".equals(audit_status)) {
							myClickShare();

						} else {
							makeText("该视频暂未审核通过，无法分享");
						}
					}
				});
			}

			break;
		case R.id.detail_share_layout:// 视频下的分享
			if (GetLogInState()) {
				if (!setUserLogin()) {
					return;
				}
				if ("1".equals(audit_status)) {
					myClickShare();

				} else {
					makeText("该视频暂未审核通过，无法分享");
				}
			} else {
				IsLogin();
				ChaneLoginState(new LoginInterface() {

					@Override
					public void Login() {
						if (!AppConfig.ISLOIN) {
							playActivityLog();
							return;
						}
						if ("1".equals(audit_status)) {
							myClickShare();

						} else {
							makeText("该视频暂未审核通过，无法分享");
						}
					}
				});
			}

			break;

		case R.id.video_introduction_loading:// 显示全部简介
			if (introduction_all) {
				findViewById(R.id.video_introduction_text1).setVisibility(View.GONE);
				findViewById(R.id.video_introduction_text2).setVisibility(View.VISIBLE);
				TextView introduction_text = (TextView) findViewById(R.id.video_introduction_loading_text);
				introduction_text.setText("收起全部");
				setImage(R.id.video_introduction_loading_img, R.drawable.jiazai);
				introduction_all = false;
			} else {
				findViewById(R.id.video_introduction_text1).setVisibility(View.VISIBLE);
				findViewById(R.id.video_introduction_text2).setVisibility(View.GONE);
				TextView introduction_text = (TextView) findViewById(R.id.video_introduction_loading_text);
				introduction_text.setText("显示全部");
				setImage(R.id.video_introduction_loading_img, R.drawable.showall);
				introduction_all = true;
			}
			break;
		case R.id.video_comments_loading:// 加载更多。
			// 新添加 感觉在点击更多的时候 不是加载一条 不是第一次加载
			/*
			 * loading_more = false; loading_one = false;
			 */
			if (iss_s) { // 显示的是加载更多
				mLoadBar.setMessage("数据加载中...");
				mLoadBar.show();
				loading_more = false;
				loading_one = false;
				// pp = pp + 1;
			} else { // 显示收起评论
				// pp = 1;
				loading_more = true;

			}
			new GetCommentData().execute();
			break;

		case R.id.user_img:// 评论头像

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
				commonDialog = new CommonDialog(VideoDetailActivity_new.this, video_desc, clickListener,
						R.layout.loading_dialog_text_single, clikViews, R.id.tipText_view);
				commonDialog.show();
			}

			break;

		case R.id.right_textview:
			if (GetLogInState()) {
				if (!setUserLogin()) {
					return;
				}
				if (onClikright_textview) {
					onClikright_textview = false;
					new setVideoChaesOr().execute();
				}
			} else {
				IsLogin();
				ChaneLoginState(new LoginInterface() {

					@Override
					public void Login() {
						if (!AppConfig.ISLOIN) {
							playActivityLog();
							return;
						}
						if (onClikright_textview) {
							onClikright_textview = false;
							new setVideoChaesOr().execute();
						}
					}
				});
			}

			break;
		case R.id.right_left_img1:
			if (GetLogInState()) {
				if (!setUserLogin()) {
					return;
				}
				if (onClikright_textview) {
					onClikright_textview = false;
					new setVideoChaesOr().execute();
				}
			} else {
				IsLogin();
				ChaneLoginState(new LoginInterface() {

					@Override
					public void Login() {
						if (!AppConfig.ISLOIN) {
							playActivityLog();
							return;
						}
						if (onClikright_textview) {
							onClikright_textview = false;
							new setVideoChaesOr().execute();
						}
					}
				});
			}
			break;
		case R.id.isplay_ms_look:
			findViewById(R.id.isplay_ms_layout).setVisibility(View.GONE);
			Logger.i("马上观看");
			Intent intent2 = null;
			String string11 = mapVideo.get("beforeAdContent");
			if (StringUtil.isEmpty(string11)) {
				Logger.i("没广告视频！等全部内容");
				return;
			}

			HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(string11);
			String uri_before = map.get("ad_url_s");

			if (StringUtil.isEmpty(uri_before)) {
				Logger.i("没广告视频！");
				return;
			}
			if (!uri_before.contains("http")) {
				Logger.i("有广告视频数据！但后台格式不对");
				return;
			}
			// intent2 = new Intent(VideoDetailActivity_new.this,
			// MyVideoView_fcz.class);
			intent2 = new Intent(VideoDetailActivity_new.this, VideoViewPlayingActivity.class);
			intent2.putExtra("data", (Serializable) mapVideo);// fcz
			Uri uri = Uri.parse(uri_before);
			intent2.setData(uri);
			intent2.putExtra("syte", "1");
			intent2.putExtra("video_url", mapVideo.get("video_url"));// 播放完广告播的视频
			intent2.putExtra("video_id", video_Id);// 播放器请求剧集用到 （最好有剧集的时候才传此参数）
			intent2.putExtra("title", mapVideo.get("name"));
			intent2.putExtra("ad_text", map.get("ad_text"));// 点广告跳的外连接
			intent2.putExtra("ad_url", map.get("ad_url"));
			intent2.putExtra("orshare", orShare);// 收藏状态
			startActivityForResult(intent2, 333);
			break;
		case R.id.isplay_ms_share:
			findViewById(R.id.isplay_ms_layout).setVisibility(View.GONE);
			Intent intent3 = new Intent(getApplicationContext(), msShareActivity.class);
			intent3.putExtra("tell", getPre("user_name"));
			intent3.putExtra("ni_name", getPre("nick_name"));
			intent3.putExtra("video_id", video_Id);
			intent3.putExtra("video_name", mapVideo.get("name"));
			startActivity(intent3);
			Logger.i("马上分享");

			break;
		case R.id.isplay_ms_money:
			Logger.i("马上购买");
			break;
		case R.id.isplay_ms_back:
			findViewById(R.id.isplay_ms_layout).setVisibility(View.GONE);
			Logger.i("马上关闭");
			break;
		case R.id.id_testDriveForpresent:
			// 跳转到一个webView
			Intent intent1 = new Intent(VideoDetailActivity_new.this, DriveTest.class);
			intent1.putExtra("web_url", mapVideo.get("active_url"));
			startActivity(intent1);

		case R.id.de_gengduo:
			if (idMore) {
				adnumber = Series.size();
				series_img.setBackgroundResource(R.drawable.jj_shouqi);

			} else {
				adnumber = 7;
				series_img.setBackgroundResource(R.drawable.jj_gengduo);
			}
			idMore = !idMore;
			adapter.notifyDataSetChanged();
			break;
		}
	}

	/*
	 * video_Id 视频ID fcz ch_id 父ID video_name 视频名 video_url 播放地址 video_img_url
	 * 视频图片 ping_fen 视频评分
	 */
	private void DownVideo(String video_Id, String ch_id, String video_name, String video_url, String video_img_url, String ping_fen) {
		if (!PublicUtils.hasSdcard()) {
			makeText("内容卡不存在");
			return;
		}

		// 获得数据库中所以的视频id
		ArrayList<String> videoIds = DataBaseDao.getInstance(VideoDetailActivity_new.this).getVideoIds();
		Logger.i("数据库的ID：" + videoIds);
		if (!videoIds.contains(video_Id)) {
			// 查询数据库中所以下载状态
			ArrayList<String> query_All_DownStatus = DataBaseDao.getInstance(VideoDetailActivity_new.this).query_All_DownStatus();

			if (query_All_DownStatus.contains("1")) { // “1”
				Logger.i("不把视频数据传给服务");
			} else {
					Intent intent = new Intent(VideoDetailActivity_new.this, Download_Service.class);
					intent.putExtra("_id", video_Id);// 视频id
					intent.putExtra("urlTotal", ch_id);// 父ID
					intent.putExtra("filename", video_name);// 视频名
					intent.putExtra("url", video_url);// 视频播放地址
					intent.putExtra("picture", video_img_url);// 视频的图片地址
					intent.putExtra("foldername", ping_fen);// 视频评分
					intent.putExtra("mapKey", "-1");// 下载到第几个url
					intent.putExtra("downloadedSize", "0");// 下载完成度
					intent.putExtra("downloadSpeed", "0");// 文件写入了多少
					mContext.startService(intent);
			}
			// 将数据添加到数据库
			DataBaseDao.getInstance(VideoDetailActivity_new.this).initDownloadData(video_Id, ch_id, video_name, video_url, video_img_url,
					ping_fen, 0, 0, 0);
			makeText("成功添加至下载列表..");

		} else {
			makeText("已经在下载列表了");
		}

	}

	/**
	 *  获得视频信息
	 * @author Administrator
	 *
	 */
	private class GetUserData extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			ArrayList<HashMap<String, String>> list = DataRequest
					.getArrayListFromUrl_Base64(getUrl(AppConfig.NEW_V_VIDEO_DETAIL), video_Id);
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
				Logger.i("mapVideo===>" + mapVideo);
				if ("1".equals(result.get("is_show"))) {
					// // 有试驾有礼信息则显示按钮
					// testDriveForpresent.setVisibility(View.VISIBLE);
					// if (!StringUtil.isEmpty(result.get("active_name"))) {
					// testDriveForpresent.setText(result.get("active_name"));
					// }
					vi_syyl.setVisibility(View.VISIBLE);
				} else {
					vi_syyl.setVisibility(View.GONE);
				}
				/*
				 * audit_status 审核状态：0未审核、1审核通过、2未通过'
				 */
				audit_status = result.get("audit_status");
				findViewById(R.id.video_detail_scroll).setVisibility(View.VISIBLE);

				if ("1".equals(result.get("is_free"))) {
					isFree = false;
				} else if ("0".equals(result.get("is_free"))) {
					isFree = true;
				}
				// 有些视频是没有“is_free” 这个字段 在这里先默认都为免费 后面根据情况改
				else if (StringUtil.isEmpty(result.get("is_free"))) {
					isFree = true;
				}
			} else {
				if (NetworkUtil.isWIFIConnected(mContext)) {
					makeText("暂无数据");
				}

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
	private class GetCommentData extends AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {

			String ping_count = mapVideo.get("ping_count");
			if (!StringUtil.isEmpty(ping_count)) {
				Logger.i("评论的个数11：" + ping_count);
			}

			if (loading_more) {
				if (loading_one) {
					commentnData.clear();
				}
				return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_VIDEO_COMMENTS_LIST), video_Id, "1", "10");
			}
			if (loading_one) {
				commentnData.clear();
			}

			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_VIDEO_COMMENTS_LIST), video_Id,
					getStart(commentnData.size()), "10");

		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			Logger.i("请求页数：" + getStart(commentnData.size()));
			Logger.i("请求页数11：" + pp);
			mLoadBar.dismiss();
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					if (null == detail_listView) {
						detail_listView = (LinearLayoutForListView) findViewById(R.id.detail_listView);
					}
					detail_listView.setVisibility(View.VISIBLE);
					findViewById(R.id.detail_text).setVisibility(View.GONE);
					findViewById(R.id.de_pinlun_xian).setVisibility(View.VISIBLE);
					detail_listView.removeAllViews();
					ArrayList<HashMap<String, String>> list = DataRequest.getArrayListFromJSONArrayString(result.get("data"));
					commentnData.addAll(list);
					setDetailListView(detail_listView);
					if (!loading_more) {
						loading_one = false;
					}

					if (commentnData.size() >= 10) {
						iss_s = true;
						setItem(R.id.video_comments_loading_text, "加载更多");
						setImage(R.id.video_comments_loading_img, R.drawable.showall);
						findViewById(R.id.video_comments_loading).setVisibility(View.VISIBLE);
					}
					if (!loading_more) {
						if (list.size() < 10) {
							iss_s = false;
							setItem(R.id.video_comments_loading_text, "收起评论");
							setImage(R.id.video_comments_loading_img, R.drawable.jiazai);
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
						iss_s = false;
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

			} else {
				if (commentnData.size() > 1) {
					iss_s = false;
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
	private class GetCommentPublish extends AsyncTask<String, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_VIDEO_PUBLISH_COMMENTS), video_Id, params[0]);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			onClikCommentn = true;
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					comments_edit.setText("");
					HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(result.get("data"));
					if ("2".equals(map.get("comm_status"))) {
						if (findViewById(R.id.detail_listView).getVisibility() == View.GONE) {
							findViewById(R.id.detail_listView).setVisibility(View.VISIBLE);
						}
						if (findViewById(R.id.detail_text).getVisibility() == View.VISIBLE) {
							findViewById(R.id.detail_text).setVisibility(View.GONE);
						}
						makeText("评论成功，等待审核");
						commentnData.add(0, map);
						detail_listView.removeAllViews();
						setDetailListView(detail_listView);
						TextView comments_text = (TextView) findViewById(R.id.comments_text);

						int comments = Integer.valueOf(mapVideo.get("ping_count")) + 1;
						mapVideo.put("ping_count", "" + comments);
						comments_text.setText("" + comments);
					} else {
						makeText("评论成功，等待审核");
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
		else if (size / 10 > 0) { // 大于10
			return ((size % 10 > 0) ? (size / 10 + 2) : (size / 10 + 1)) + "";
		}
		return result;
	}

	/**
	 * 
	 * 评论或分享视频后，统计加1.
	 */
	private class SetComments4Share extends AsyncTask<String, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_SET_SHARE_PLAY), video_Id, params[0]);
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
		isOnClick = true;
//		if (mShare != null) {
//			if (mShare.getIsCancle())
//				setResult(RESULT_OK);
//			if (mShare.isHsow()) {
//				if (findViewById(R.id.new_author_web).getVisibility() == View.VISIBLE) {
//					mShare.closeHsow();
//				} else if (findViewById(R.id.new_share_btn_layout).getVisibility() == View.VISIBLE) {
//					if (mShare.onAnimationDisappear) {
//						mShare.showLogin(true);
//					}
//
//				} else {
//					finish();
//				}
//			} else {
//				finish();
//			}
//		} else {
//			finish();
//		}
		finish();
		if (!StringUtil.isEmpty(getIntent().getStringExtra("push"))) {
			if (ActivityUtil.getInstance().isclose("ModelActivity")) {
				startActivity(new Intent(mContext, ModelActivity.class));
			}
		}
	}

//	private String countShare;

	/**
	 * 获取分享内容的接口
	 * 
	 * @author hanpengyuan
	 * 
	 */
//	private class GetMsg extends AsyncTask<Void, Void, HashMap<String, String>> {
//		@Override
//		protected HashMap<String, String> doInBackground(Void... params) {
//			HashMap<String, String> way = DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.VIDEO_SHARE_COUNT), video_Id);
//			if (way != null) {
//				if ("1".equals(way.get("code"))) {
//					HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(way.get("data"));
//					if (map != null) {
//						countShare = map.get("count");
//					}
//				}
//				Log.i("----way----", String.valueOf(way));
//			}
//			return null;
//		}
//
//	}

	private String orShare;
	private MyGridView gr;
	private MyAdapter adapter;
	private ImageView series_img;
	private ImageButton vi_syyl;
	private String edit_id;
	private String is_ad;

	/**
	 * 取消收藏与不收藏的状态。
	 * 
	 * @author hanpengyuan
	 * 
	 */
	private class setVideoChaesOr extends AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			if ("1".equals(orShare)) {
				orShare = "0";
			} else if ("0".equals(orShare)) {
				orShare = "1";
			}
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_UN_VIDEO_GET_SHARE), video_Id, orShare, edit_id);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {

				if ("1".equals(result.get("code"))) {

					// right_textview.setVisibility(View.VISIBLE);
					store_img.setVisibility(View.VISIBLE);
					if ("1".equals(orShare)) {
						// right_textview.setText("不收藏");
						BitmapUtil.setImageResource(store_img, R.drawable.store_2_select);
						makeText("收藏成功");
						// store_img.setImageDrawable(getResources().getDrawable(R.drawable.store_2_select));
					} else {
						// right_textview.setText("收藏");
						store_img.setImageDrawable(getResources().getDrawable(R.drawable.store_2_defaut));
						makeText("取消收藏");
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
	 * 获取视频收藏与否的状态。
	 * 
	 * @author hanpengyuan
	 * 
	 */
	private class CheckVideoChaes extends AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {

			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_UN_GET_VIDEO_COLLECT), video_Id);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null && "1".equals(result.get("code"))) {
				if (!setUserLogin_san()) {
					return;
				}
				Logger.i("视频状态数据：" + result.toString());
				orShare = result.get("data");
				// right_textview.setVisibility(View.VISIBLE);
				store_img.setVisibility(View.VISIBLE);
				if ("1".equals(orShare)) {
					// right_textview.setText("不收藏");
					// store_img.setImageDrawable(getResources().getDrawable(R.drawable.store_2_select));
					BitmapUtil.setImageResource(store_img, R.drawable.store_2_select);
				} else {
					// right_textview.setText("收藏");
					store_img.setImageDrawable(getResources().getDrawable(R.drawable.store_2_defaut));
				}
			}
		}
	}

	// 判断视频可不可以播放
	private class SetCommentsIsPlay extends AsyncTask<String, Void, HashMap<String, String>> {

		private String syte;
		private String syte_newString;

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			syte = params[0];
			syte_newString = params[1];
			if (!StringUtil.isEmpty(edit_id)) {

				return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_VIDEO_MY_SEARCH_PLAY), syte, edit_id);
			}
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_VIDEO_MY_SEARCH_PLAY_NEW), syte);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			super.onPostExecute(result);
			if (result == null) {
				makeText("连接服务器失败");
				return;
			}
			Logger.i("播放视频结果：" + result.get("code"));
			if ("1".equals(result.get("code"))) {
				Intent intent = null;
				if ("1".equals(syte_newString)) {
					Uri uri = Uri.parse(Series.get(number_m).get("series_url"));
					Logger.i("剧集URI：" + uri);
					intent = new Intent(mContext, VideoViewPlayingActivity.class);
					intent.setData(uri);
					intent.putExtra("syte", "3");
					intent.putExtra("title", mapVideo.get("name"));
					intent.putExtra("video_id", video_Id);
					intent.putExtra("orshare", orShare);
					intent.putExtra("data", (Serializable) mapVideo);// fcz
					startActivityForResult(intent, 333);
				} else {
					if (isState == 0) {
						Uri uri = Uri.parse(mapVideo.get("video_url"));
						if ("2".equals(mapVideo.get("is_sys"))) {
							intent = new Intent(mContext, AdWebActivity.class);
							intent.putExtra("url", mapVideo.get("video_url"));
							intent.putExtra("title", mapVideo.get("name"));
							startActivityForResult(intent, 333);
						} else {
							intent = new Intent(mContext, VideoViewPlayingActivity.class);
							intent.putExtra("data", (Serializable) mapVideo);// fcz
							intent.setData(uri);
							intent.putExtra("orshare", orShare);
							intent.putExtra("title", mapVideo.get("name"));
							intent.putExtra("syte", "3");
							intent.putExtra("video_id", video_Id);
							startActivityForResult(intent, 333);
						}
					}
					if (isState == 1) { // 不免费 但能播放
						Logger.i("不免费。。但能播放");
						Uri uri = Uri.parse(mapVideo.get("video_url"));
						if ("2".equals(mapVideo.get("is_sys"))) {
							intent = new Intent(mContext, AdWebActivity.class);
							intent.putExtra("url", mapVideo.get("video_url"));
							intent.putExtra("title", mapVideo.get("name"));
							startActivityForResult(intent, 333);
						} else {
							// intent = new Intent(Intent.ACTION_VIEW);
							// String type = "video/*";
							// intent.setDataAndType(uri, type);
							// 调新的播放器
							intent = new Intent(mContext, VideoViewPlayingActivity.class);
							intent.putExtra("data", (Serializable) mapVideo);// fcz
							intent.setData(uri);
							intent.putExtra("orshare", orShare);
							intent.putExtra("syte", "3");
							intent.putExtra("title", mapVideo.get("name"));
							intent.putExtra("video_id", video_Id);
							startActivityForResult(intent, 333);
						}
					}
				}

			} else if ("-1".equals(result.get("code"))) {// 第三方登录容易自动掉线
				Intent intent = new Intent(VideoDetailActivity_new.this, UserLogin.class);
				intent.putExtra("syte", "1");
				startActivity(intent);
			} else {
				// findViewById(R.id.isplay_ms_layout).setVisibility(View.VISIBLE);
			}

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		// UMSsoHandler ssoHandler =
		// mController_share.getConfig().getSsoHandler(
		// requestCode);
		// if (ssoHandler != null) {
		// ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		// }
		if (requestCode == 333 && resultCode == 23) {
			makeText("播放出错");
			return;
		}
		if (requestCode == 333) {
			findViewById(R.id.activity_detail).setVisibility(View.VISIBLE);
		}
		if (requestCode == 333 && resultCode == 333) {
			findViewById(R.id.activity_detail).setVisibility(View.VISIBLE);
			if (data != null) {
				String str = data.getStringExtra("orshare");
				if (!StringUtil.isEmpty(str)) {
					if ("1".equals(str)) {
						orShare = "1";
						BitmapUtil.setImageResource(store_img, R.drawable.store_2_select);
					} else {
						orShare = "0";
						BitmapUtil.setImageResource(store_img, R.drawable.store_2_defaut);
					}
				}
			}

		}

	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (Series.size() > 7) {
				return adnumber;
			}

			return Series.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position + 1;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyTextView textView = new MyTextView(mContext);
			HashMap<String, String> hashMap = Series.get(position);
			if (text_id.contains(hashMap.get("id"))) {
				textView.setTextColor(Color.BLACK);
				textView.setBackgroundResource(R.color.de_new_huang);
			} else {
				textView.setTextColor(Color.WHITE);
				textView.setBackgroundResource(R.color.comm_bg_color_new);
			}
			DecimalFormat Df = new DecimalFormat("00");// 用这个工具 可以补0
			textView.setText(Df.format(getItem(position)));
			textView.setTextSize(16);
			textView.setGravity(Gravity.CENTER);
			return textView;

		}
	}

	private class GetJJ extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {

			return DataRequest.getArrayListFromUrl_Base64(getUrl(AppConfig.NEW_V_GET_VIDEO_LIST_NEW_SERIES), video_Id);
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			super.onPostExecute(result);
			TextView text_number = (TextView) findViewById(R.id.video_comments_head_text2);
			if (result != null && result.size() > 0) {
				Logger.i(result.toString() + "...剧集信息");
				findViewById(R.id.recommended_tv_layout).setVisibility(View.VISIBLE);
				text_number.setVisibility(View.VISIBLE);
				// 更新到08集/全80集
				text_number.setText("更新至" + result.size() + "集/" + "全" + mapVideo.get("series_num") + "集");
				Series.clear();
				Series.addAll(result);
				if (Series.size() > 7) {
					adnumber = 7;
					findViewById(R.id.de_gengduo).setVisibility(View.VISIBLE);
				}
				adapter.notifyDataSetChanged();
			} else {
				Logger.i("后台没添加剧集...");
				text_number.setVisibility(View.GONE);
				findViewById(R.id.recommended_tv_layout).setVisibility(View.GONE);
				findViewById(R.id.de_gengduo).setVisibility(View.GONE);
				findViewById(R.id.recommended_reason_layout_xian).setVisibility(View.GONE);
			}
		}

	}

	private class GetEditImg extends AsyncTask<String, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			// TODO Auto-generated method stub
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_UN_GET_CHANNEL_EDITIMG), edit_id, video_Id,is_ad);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			super.onPostExecute(result);
			CircleImageView fanbb = (CircleImageView) findViewById(R.id.ico_fanbb);
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(result.get("data"));
					if (map != null) {
						// 小编头像
						if (!StringUtil.isEmpty(map.get("head_img"))) {
							LoadBitmap.getIntence().loadImage(map.get("head_img"), fanbb);
						} else {
							fanbb.setBackgroundResource(R.drawable.user_edit);
						}
						TextView ed_name = (TextView) findViewById(R.id.ed_name);
						ed_name.setText(map.get("nick_name"));
					}
				}
			} else {
				fanbb.setBackgroundResource(R.drawable.user_edit);
			}
		}

	}

}
