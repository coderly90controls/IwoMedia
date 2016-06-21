package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.iwo.media.action.AudioAction;
import com.android.iwo.media.action.ChatUtils;
import com.android.iwo.media.action.DBhelper;
import com.android.iwo.media.action.FaceAction;
import com.android.iwo.media.action.IwoSQLiteHelper;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.preview.picture.PictureViewActivity;
import com.android.iwo.media.view.XListView;
import com.android.iwo.media.view.XListView.IXListViewListener;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.AndroidUtils;
import com.test.iwomag.android.pubblico.util.DateUtil;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.LoadBitmap.LoadBitmapCallBack;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class ChatHistory extends BaseActivity {
	private String head = "", name = "";
	private XListView listView;
	private IwoAdapter adapter;
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> mHistoryData = new ArrayList<HashMap<String, String>>();
	private AudioAction audioAction;
	private DBhelper helper;
	public static boolean isIN = false;
	public static String userID = "";
	public static String FROM = "";
	private ChatUtils mChatUtils;
	private FaceAction faceAction;
	private LinearLayout chat_tel_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		isIN = true;
		userID = "";
		FROM = "";
		init();
		initListView();
	}

	/**
	 * 初始化
	 */
	private void init() {
		chat_tel_layout = (LinearLayout) findViewById(R.id.chat_tel_layout);
		mChatUtils = new ChatUtils(this, scale, dm);
		faceAction = new FaceAction(this);
		new FaceAction(this);
		findViewById(R.id.box_layout).setVisibility(View.GONE);
		setBack(null);
		userID = getIntent().getStringExtra("userID");

		helper = new DBhelper(this, IwoSQLiteHelper.MESSAGE_TAB);
		audioAction = new AudioAction();
		userID = getIntent().getStringExtra("userID");
		Logger.i("userID----" + userID);
		head = getIntent().getStringExtra("head_img");
		name = getIntent().getStringExtra("name");
		setTitle(name);
		Logger.i("我的头像地址：" + head);
	}

	private void initListView() {
		listView = (XListView) findViewById(R.id.chat_listview);
		listView.setVisibility(View.VISIBLE);
		adapter = new IwoAdapter(this, mData) {
			@Override
			public View getView(final int position, View v, ViewGroup parent) {
				if (v == null)
					v = mInflater.inflate(R.layout.list_chat_item, parent, false);
				final HashMap<String, String> map = mData.get(position);
				RelativeLayout send_layout = (RelativeLayout) v.findViewById(R.id.send_layout);
				RelativeLayout get_layout = (RelativeLayout) v.findViewById(R.id.get_layout);
				send_layout.setVisibility(View.GONE);
				get_layout.setVisibility(View.GONE);
				TextView time = (TextView) v.findViewById(R.id.time);
				if (!StringUtil.isEmpty(map.get("time"))) {
					time.setText(DateUtil.format("yyyy-MM-dd HH:mm:ss", map.get("time") + "000"));
					time.setVisibility(View.VISIBLE);
				} else {
					time.setVisibility(View.GONE);
				}
				final String type = map.get("type");
				TextView send_tex = (TextView) v.findViewById(R.id.send_tex);
				TextView send_dur = (TextView) v.findViewById(R.id.send_dur);
				TextView send_add_tex = (TextView) v.findViewById(R.id.send_add_tex);
				final ImageView send_img = (ImageView) v.findViewById(R.id.send_img);
				final ImageView send_img_ = (ImageView) v.findViewById(R.id.send_img_);

				TextView get_tex = (TextView) v.findViewById(R.id.get_tex);
				TextView get_dur = (TextView) v.findViewById(R.id.get_dur);
				TextView get_add_tex = (TextView) v.findViewById(R.id.get_add_tex);
				final ImageView get_img = (ImageView) v.findViewById(R.id.get_img);
				final ImageView get_img_ = (ImageView) v.findViewById(R.id.get_img_);

				final LinearLayout get_linelayout = (LinearLayout) v.findViewById(R.id.get_linelayout);
				final LinearLayout send_linelayout = (LinearLayout) v.findViewById(R.id.send_linelayout);

				// ImageView ico_chat_video_btn = (ImageView)
				// v.findViewById(R.id.video_ico);
				// ico_chat_video_btn.setVisibility(View.GONE);
				LinearLayout send_img_layout = (LinearLayout) v.findViewById(R.id.send_img_layout);

				send_img_layout.setVisibility(View.GONE);
				send_dur.setVisibility(View.GONE);
				send_add_tex.setVisibility(View.GONE);
				send_tex.setVisibility(View.GONE);
				send_img.setVisibility(View.GONE);
				send_img_.setVisibility(View.GONE);
				get_img.setVisibility(View.GONE);
				get_img_.setVisibility(View.GONE);
				get_tex.setVisibility(View.GONE);
				get_dur.setVisibility(View.GONE);
				get_add_tex.setVisibility(View.GONE);
				if ("1".equals(map.get("send"))) {// 我发的消息显示
					send_tex.setTextColor(0xff000000);
					send_layout.setVisibility(View.VISIBLE);
					ImageView head = (ImageView) v.findViewById(R.id.send_head);
					LoadBitmap.getIntence().loadImage(map.get("head_img"), head);
					Logger.i("头像：" + map.get("head_img"));
					if ("1".equals(type)) {// 文字表情
						send_tex.setVisibility(View.VISIBLE);
						send_tex.setText(faceAction.setTextView(mactivity, chat_tel_layout, map.get("msg_tex")));
						send_tex.setMovementMethod(LinkMovementMethod.getInstance());

					} else if ("2".equals(type)) {// 图片
						Logger.i("图片地址：" + map.get("richbody"));
						Bitmap bitmap = LoadBitmap.toCompressionBitmap(map.get("richbody"), 100, 100);
						send_img_.setVisibility(View.VISIBLE);

						if (bitmap == null) {
							setImgSize(send_img_, 0, 9 / 16.0f, 3);
							send_img_.setImageResource(R.drawable.video_zhanwei);
						} else {
							setImgSize(send_img_, bitmap);
							send_img_.setBackground(new BitmapDrawable(bitmap));
						}
					} else if ("3".equals(type)) {// 语音
						send_tex.setTextColor(0xff57a7fc);
						send_img.setVisibility(View.VISIBLE);
						send_img_layout.setVisibility(View.VISIBLE);
						if ("1".equals(map.get("isread"))) {
							send_img.setImageResource(R.drawable.ico_chat_voice_send_read);
						} else
							send_img.setImageResource(R.drawable.ico_chat_voice_send);

						String string = "";
						try {
							int dua = Integer.valueOf(map.get("duartion"));
							Logger.i("dua" + dua);
							for (int i = 0; i < dua && i < 18; i++) {
								string = string + "1";
							}
						} catch (Exception e) {
						}
						send_tex.setText("" + string);
						send_tex.setVisibility(View.VISIBLE);
						send_dur.setText(map.get("duartion") + "''");
						send_dur.setVisibility(View.VISIBLE);
					}
					if ("4".equals(type)) {// 视频
						send_img_.setVisibility(View.VISIBLE);
						send_img_.setBackgroundResource(R.drawable.video_zhanwei);
						Logger.i("视频：" + map.get("msg_tex"));
						setImgSize(send_img_, 0, 9 / 16.0f, 3);
						LoadBitmap.getIntence().loadImage(map.get("msg_tex"), new LoadBitmap.LoadBitmapCallBack() {
							public void callBack(Bitmap bit) {
								if (bit != null) {
									setImgSize(send_img_, 0, 61 / 70.0f, 3);
									send_img_.setBackground(new BitmapDrawable(bit));
								}
							}
						});
					} else if ("5".equals(type)) {// 位置
						String tex = map.get("msg_tex");
						if (!StringUtil.isEmpty(tex)) {
							String[] add = tex.split(",");
							if (add.length > 0) {
								send_add_tex.setText(add[0]);
							}
						}
						setImgSize(send_img_, 0, 62 / 72.0f, 3);
						setImgSize(send_add_tex, 0, 3);
						send_add_tex.setVisibility(View.VISIBLE);
						send_add_tex.setBackgroundColor(0x88555555);
						send_img_.setVisibility(View.VISIBLE);
						send_img_.setBackgroundResource(R.drawable.location_msg);
					}
					OnClickListener listener = new OnClickListener() {
						public void onClick(View arg0) {
							if ("3".equals(type)) {
								helper.update("tab_" + getUid() + userID, "isread", "1", "richbody", map.get("richbody"));
								mData.get(position).put("isread", "1");
								send_img.setImageDrawable(new ColorDrawable(0x00000000));
								send_img.setBackgroundResource(R.drawable.chat_send_read_animation);
								AnimationDrawable ani = (AnimationDrawable) send_img.getBackground();
								audioAction.readVoice(map.get("richbody"), ani, send_img, true);
							} else if ("4".equals(type)) {
								AndroidUtils.loadMp4(map.get("richbody"), ChatHistory.this);
							} else if ("5".equals(type)) {
								Intent intent = new Intent(mContext, ScreenShotActivity.class);
								String tex = map.get("msg_tex");
								if (!StringUtil.isEmpty(tex)) {
									String[] add = tex.split(",");
									if (add.length == 3) {
										intent.putExtra("lat", "" + add[1]);
										intent.putExtra("lon", "" + add[2]);
									}
								}
								intent.putExtra("path", map.get("richbody"));
								startActivity(intent);
							} else if ("2".equals(type)) {
								setBigImg(map, map.get("richbody"));
							}
						}
					};
					send_linelayout.setOnClickListener(listener);
					mChatUtils.setOnLongClick(send_linelayout, type, map);
				} else {// 对方发的消息显示
					get_layout.setVisibility(View.VISIBLE);
					ImageView head = (ImageView) v.findViewById(R.id.get_head);
					get_tex.setTextColor(0xff000000);
					LoadBitmap.getIntence().loadImage(map.get("head_img"), head);

					if ("1".equals(type)) {
						get_tex.setVisibility(View.VISIBLE);
						get_tex.setText(faceAction.setTextView(mactivity, chat_tel_layout, map.get("msg_tex")));
						get_tex.setMovementMethod(LinkMovementMethod.getInstance());
					} else if ("2".equals(type)) {
						Bitmap bitmap = LoadBitmap.toCompressionBitmap(map.get("richbody"), 100, 100);
						get_img_.setVisibility(View.VISIBLE);

						if (bitmap == null) {
							setImgSize(get_img_, 0, 9 / 16.0f, 3);
							get_img_.setImageResource(R.drawable.video_zhanwei);
						} else {
							setImgSize(get_img_, bitmap);
							get_img_.setBackground(new BitmapDrawable(bitmap));
						}

					} else if ("3".equals(type)) {
						get_tex.setTextColor(0xffffffff);
						get_img.setVisibility(View.VISIBLE);
						get_dur.setText(map.get("duartion") + "''");
						get_dur.setVisibility(View.VISIBLE);
						if ("1".equals(map.get("isread"))) {
							get_img.setImageResource(R.drawable.ico_chat_voice_get_read);
						} else
							get_img.setImageResource(R.drawable.ico_chat_voice_get);

						String string = "";
						try {
							int dua = Integer.valueOf(map.get("duartion"));
							for (int i = 0; i < dua && i < 18; i++) {
								string = string + "1";
							}
						} catch (Exception e) {
						}
						get_tex.setText(string);
						get_tex.setVisibility(View.VISIBLE);
					} else if ("4".equals(type)) {
						get_img_.setVisibility(View.VISIBLE);
						get_img_.setBackgroundResource(R.drawable.video_zhanwei);
						Logger.i("视频：" + map.get("richbody"));
						setImgSize(get_img_, 0, 9 / 16.0f, 3);
						if (!StringUtil.isEmpty(map.get("richbody"))) {
							String paString[] = map.get("richbody").split(",");
							if (paString.length == 2) {
								Logger.i("视频111：" + paString[0]);
								LoadBitmap.getIntence().loadImage(paString[0], new LoadBitmapCallBack() {
									public void callBack(Bitmap bit) {
										if (bit != null) {
											setImgSize(get_img_, 0, 61 / 70.0f, 3);
											get_img_.setBackground(new BitmapDrawable(bit));
										}
									}
								});
							}
						}
					} else if ("5".equals(type)) {
						String tex = map.get("msg_tex");
						if (!StringUtil.isEmpty(tex)) {
							String[] add = tex.split(",");
							if (add.length > 0) {
								get_add_tex.setText(add[0]);
							}
						}
						setImgSize(get_img_, 0, 61 / 70.0f, 3);
						setImgSize(get_add_tex, 0, 3);
						get_add_tex.setVisibility(View.VISIBLE);
						get_add_tex.setBackgroundColor(0x88555555);
						get_img_.setVisibility(View.VISIBLE);
						get_img_.setBackgroundResource(R.drawable.location_msg);
					}
					mChatUtils.setOnLongClick(get_linelayout, type, map);
					get_linelayout.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							if ("3".equals(type)) {
								helper.update("tab_" + getUid() + userID, "isread", "1", "richbody", map.get("richbody"));
								mData.get(position).put("isread", "1");
								get_img.setImageDrawable(new ColorDrawable(0x00000000));
								get_img.setBackgroundResource(R.drawable.chat_get_read_animation);
								AnimationDrawable ani = (AnimationDrawable) get_img.getBackground();
								audioAction.readVoice(map.get("richbody"), ani, get_img, false);
							} else if ("5".equals(type)) {
								Intent intent = new Intent(mContext, ScreenShotActivity.class);
								String tex = map.get("msg_tex");
								if (!StringUtil.isEmpty(tex)) {
									String[] add = tex.split(",");
									if (add.length == 3) {
										intent.putExtra("lat", "" + add[1]);
										intent.putExtra("lon", "" + add[2]);
									}
								}
								startActivity(intent);
							} else if ("2".equals(type)) {
								setBigImg(map, map.get("richbody"));

							} else if ("4".equals(type)) {
								if (!StringUtil.isEmpty(map.get("richbody"))) {
									String paString[] = map.get("richbody").split(",");
									if (paString.length == 2) {
										AndroidUtils.loadMp4(paString[1], ChatHistory.this);
									} else
										makeText("播放失败");
								} else
									makeText("播放失败");
							}
						}
					});
				}
				send_tex.setOnLongClickListener(new OnLongClickListener() {
					public boolean onLongClick(View v1) {
						Logger.i("长按");
						mChatUtils.setLongClick(send_linelayout, type, map);
						faceAction.isLongClick = true;
						return true;
					}
				});
				get_tex.setOnLongClickListener(new OnLongClickListener() {
					public boolean onLongClick(View v1) {
						Logger.i("长按");
						mChatUtils.setLongClick(get_linelayout, type, map);
						faceAction.isLongClick = true;
						return true;
					}
				});
				return v;
			}
		};

		listView.setXListViewListener(new IXListViewListener() {
			public void onRefresh() {

				if (mHistoryData != null) {
					int n = mData.size();
					for (int i = 0; i < 10 && n < mHistoryData.size() - 1; i++) {
						n = n + 1;
						Logger.i("n=" + n);
						mData.add(0, mHistoryData.get(n));
					}
					adapter.notifyDataSetChanged();
				}
				listView.stopRefresh();
			}
		});
		listView.setCacheColorHint(0);
		listView.setDividerHeight(0);
		listView.setSelector(new ColorDrawable(0x00000000));
		listView.setAdapter(adapter);

		Logger.i("get uid " + getUid());
		mHistoryData = helper.select("tab_" + getUid() + userID, "timestamp", true);
		ArrayList<HashMap<String, String>> list = helper.select("tab_" + getUid() + userID, mData.size());
		if (list != null) {
			mData.addAll(0, list);
			adapter.notifyDataSetChanged();
		}
		listView.setSelection(adapter.getCount() - 1);
	}

	private void setBigImg(HashMap<String, String> m, String msg) {
		ArrayList<HashMap<String, String>> list = helper.select("tab_" + getUid() + userID);
		ArrayList<String> list1 = new ArrayList<String>();
		for (HashMap<String, String> map : list) {
			if ("2".equals(map.get("type"))) {
				list1.add(map.get("richbody"));
			}
		}

		int n = 0;
		for (int i = 0; i < list1.size(); i++) {
			if (msg.equals(list1.get(i))) {
				n = i;
				break;
			}
		}

		Intent intent = new Intent(mContext, PictureViewActivity.class);
		intent.putExtra("data", list1);
		intent.putExtra("n", n);
		intent.putExtra("map", m);

		startActivity(intent);
	}

	protected void setImgSize(ImageView item, Bitmap bitmap) {
		android.view.ViewGroup.LayoutParams params = item.getLayoutParams();
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (width >= height) {
			height = (int) (150 * (1.0f * height / width));
			width = 150;
		} else {
			width = (int) (150 * (1.0f * width / height));
			height = 150;
		}
		params.height = (int) (height * scale);
		params.width = (int) (width * scale);
	}
}
