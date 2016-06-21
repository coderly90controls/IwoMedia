package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.AudioAction;
import com.android.iwo.media.action.ChatUtils;
import com.android.iwo.media.action.ChatUtils.onDeleteListener;
import com.android.iwo.media.action.DBhelper;
import com.android.iwo.media.action.FaceAction;
import com.android.iwo.media.action.IwoSQLiteHelper;

import com.android.iwo.media.chat.XmppClient;
import com.android.iwo.media.chat.XmppMessageListener.XmppGetMessage;
import com.android.iwo.media.chat.XmppPacketExtension;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.preview.picture.PictureViewActivity;
import com.android.iwo.media.view.CommonDialog;
import com.android.iwo.media.view.XListView;
import com.android.iwo.media.view.XListView.IXListViewListener;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.adapter.ViewPageAdapter;
import com.test.iwomag.android.pubblico.util.AndroidUtils;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.DateUtil;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.LoadBitmap.LoadBitmapCallBack;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.SendFile;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 聊天类，使用时传入userID（对方的id）, user_head(对方的头像), head(自己的头像)
 * 
 * @author DEV
 * 
 */
public class ChatActivity extends BaseActivity implements OnClickListener, OnTouchListener {
	private EditText chat_edit;
	private String user_head = "", head = "", name = "", sex = "";
	private ImageView btn_video, btn_face, btn_add;
	private TextView send;
	private Button video_btn, btn_voice, voice_btn;
	private RelativeLayout face_layout;
	private LinearLayout chat_tel_layout;
	private ViewPager face;
	private LinearLayout add_layout;
	private boolean isvideo = true, isimg = true, isvoice = true;
	private InputMethodManager imm = null;
	private XListView listView;
	private IwoAdapter adapter;
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> mHistoryData = new ArrayList<HashMap<String, String>>();
	private String imgUrl;

	private AudioAction audioAction;
	private DBhelper helper;
	private long time_str = 0;
	public static boolean isIN = false;
	public static String userID = "";
	public static String FROM = "";
	// private Bitmap user_head_bit, my_big;
	private String video_path = "";// 发送视频的地址
	private ChatUtils mChatUtils;
	private FaceAction faceAction;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(10010);
		isIN = true;
		userID = "";
		FROM = "";
		init();
		setViewClick();
		new GetInfo().execute();
	}

	@Override
	public void onBackPressed() {
		if(chat_tel_layout.getVisibility() == View.VISIBLE){
			chat_tel_layout.setVisibility(View.GONE);
			return;
		}
		close();
		setResult(RESULT_OK);
		finish();
	}

	private void close() {
		if (mData.size() > 0 && helper != null) {
			Logger.i("mData" + mData);
			mData.get(mData.size() - 1).put("head_img", user_head);
			mData.get(mData.size() - 1).put("user_name", name);
			helper.delete("tab_msg" + getUserTel(), "user_id", userID);

			DBhelper friend = new DBhelper(mContext, IwoSQLiteHelper.FRIEND_TAB);
			String friden_table = "tab_" + getUserTel();
			if (friend.select(friden_table, "name", userID) != null)
				helper.insert("tab_msg" + getUserTel(), mData.get(mData.size() - 1));
			sendBroadcast(new Intent("com.android.broadcast.receiver.media.refreshmsg.CHAT_REFRESH_SHARE"));
			friend.close();
		}
		if (helper != null) {
			helper.close();
			helper = null;
		}
		XmppClient.getInstance(mContext).c = null;
		isIN = false;
		userID = "";
		FROM = "";
		mData.clear();
		audioAction.stopPlay();
	}

	@Override
	protected void onPause() {
		if (mData.size() > 0 && helper != null) {
			Logger.i("mData" + mData);
			HashMap<String, String> map = new HashMap<String, String>();
			map.putAll(mData.get(mData.size() - 1));
			map.put("head_img", user_head);
			map.put("user_name", name);
			helper.delete("tab_msg" + getUserTel(), "user_id", userID);

			DBhelper friend = new DBhelper(mContext, IwoSQLiteHelper.FRIEND_TAB);
			String friden_table = "tab_" + getUserTel();
			if (friend.select(friden_table, "name", userID) != null)
				helper.insert("tab_msg" + getUserTel(), map);
			sendBroadcast(new Intent("com.android.broadcast.receiver.media.refreshmsg.CHAT_REFRESH_SHARE"));
			friend.close();
		}
		super.onPause();
	}
	/**
	 * 初始化
	 */
	private void init() {
		mChatUtils = new ChatUtils(this, scale, dm);
		faceAction = new FaceAction(this);
		NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
		setBack(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
		chat_tel_layout = (LinearLayout)findViewById(R.id.chat_tel_layout);
		userID = getIntent().getStringExtra("userID");
		ImageView right_img = (ImageView) findViewById(R.id.right_img);
		right_img.setVisibility(View.VISIBLE);
		right_img.setImageResource(R.drawable.home);
		right_img.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(mContext, FriendDetail.class);
				intent.putExtra("name", userID);
				startActivity(intent);
			}
		});
		helper = new DBhelper(this, IwoSQLiteHelper.MESSAGE_TAB);
		audioAction = new AudioAction();
		userID = getIntent().getStringExtra("userID");
		Logger.i("userID----" + userID);
		user_head = getIntent().getStringExtra("head_img");
		name = getIntent().getStringExtra("name");
		setTitle(name);
		head = getPre("user_head");
		Logger.i("我的头像地址：" + head + "--" + user_head);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		chat_edit = (EditText) findViewById(R.id.chat_edit);
		btn_video = (ImageView) findViewById(R.id.btn_video);
		btn_face = (ImageView) findViewById(R.id.btn_face);
		btn_add = (ImageView) findViewById(R.id.btn_add);
		video_btn = (Button) findViewById(R.id.video_btn);
		voice_btn = (Button) findViewById(R.id.voice_btn);
		btn_voice = (Button) findViewById(R.id.btn_voice);
		face_layout = (RelativeLayout) findViewById(R.id.face_layout);
		face = (ViewPager) findViewById(R.id.face);
		add_layout = (LinearLayout) findViewById(R.id.add_layout);
		send = (TextView) findViewById(R.id.send);
		ArrayList<View> views = new ArrayList<View>();
		for (int i = 0; i < 5; i++) {
			View view = View.inflate(this, R.layout.chat_face_layout, null);
			GridView gridView = (GridView) view.findViewById(R.id.grid);
			new FaceAction(this, i * 20, gridView, chat_edit);
			views.add(view);
		}
		setAdImgSize(face, 0, 0.5f, 1);
		setAdImgSize(face_layout, 0, 0.5f, 1);
		ViewPageAdapter a = new ViewPageAdapter(views);
		face.setAdapter(a);
		int ids[] = { R.id.point1, R.id.point2, R.id.point3, R.id.point4, R.id.point5 };
		final ImageView point[] = new ImageView[5];
		for (int i = 0; i < point.length; i++) {
			point[i] = (ImageView) findViewById(ids[i]);
		}
		face.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				for (int i = 0; i < point.length; i++) {
					point[i].setImageResource(R.drawable.enter_point_normal);
				}
				point[arg0].setImageResource(R.drawable.enter_point_selected);
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		mChatUtils.setOnDeleteListener(new onDeleteListener() {
			@Override
			public void delete(String time) {
				helper.delete("tab_" + getUid() + userID, "timestamp", time);
				for(int i=0; i<mData.size(); i++){
					if(!StringUtil.isEmpty(time) && time.equals(mData.get(i).get("timestamp"))){
						mData.remove(i);
						adapter.notifyDataSetChanged();
						break;
					}
				}
			}
		});
	}

	private void setAdImgSize(View item, int del, float size, int n) {
		int width = (dm.widthPixels - (int) (del * scale + 0.5f)) / n;
		android.view.ViewGroup.LayoutParams params = item.getLayoutParams();
		if (params != null) {
			params.width = width;
			params.height = (int) (width * size);
		}
	}

	/**
	 * 需要设置点击的View
	 */
	private void setViewClick() {
		findViewById(R.id.btn_img).setOnClickListener(this);
		findViewById(R.id.btn_pho).setOnClickListener(this);// 照片
		findViewById(R.id.btn_address).setOnClickListener(this);// 视频
		btn_add.setOnClickListener(this);
		btn_face.setOnClickListener(this);
		btn_video.setOnClickListener(this);
		video_btn.setOnClickListener(this);
		btn_voice.setOnClickListener(this);
		voice_btn.setOnTouchListener(this);// TODO
		send.setOnClickListener(this);
		chat_edit.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				face_layout.setVisibility(View.GONE);
				isimg = true;
				btn_face.setImageResource(R.drawable.ico_chat_face);
				add_layout.setVisibility(View.GONE);
				return false;
			}
		});

		chat_edit.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				if (arg3 > 0 || arg1 > 0) {
					send.setVisibility(View.VISIBLE);
					btn_add.setVisibility(View.INVISIBLE);
				} else {
					send.setVisibility(View.GONE);
					btn_add.setVisibility(View.VISIBLE);
				}
			}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			public void afterTextChanged(Editable arg0) {
			}
		});
	}

	private void initListView() {
		listView = (XListView) findViewById(R.id.chat_listview);
		listView.setVisibility(View.VISIBLE);
		adapter = new IwoAdapter(this, mData) {
			@SuppressLint("NewApi")
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

				final LinearLayout get_linelayout = (LinearLayout)v.findViewById(R.id.get_linelayout);
				final LinearLayout send_linelayout = (LinearLayout)v.findViewById(R.id.send_linelayout);
				
				//ImageView ico_chat_video_btn = (ImageView) v.findViewById(R.id.video_ico);
				//ico_chat_video_btn.setVisibility(View.GONE);
				LinearLayout send_img_layout = (LinearLayout)v.findViewById(R.id.send_img_layout);
				
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
					send_layout.setVisibility(View.VISIBLE);
					ImageView h = (ImageView) v.findViewById(R.id.send_head);
					//LoadBitmap.getIntence().loadImage(map.get("head_img"), head);
					LoadBitmap.getIntence().loadImage(head, h);
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
						send_tex.setVisibility(View.INVISIBLE);
						send_dur.setText(map.get("duartion") + "''");
						send_dur.setVisibility(View.VISIBLE);
					}else if ("4".equals(type)) {// 视频
						send_img_.setVisibility(View.VISIBLE);
						send_img_.setBackgroundResource(R.drawable.video_zhanwei);
						Logger.i("视频：" + map.get("msg_tex"));
						setImgSize(send_img_, 0, 9 / 16.0f, 3);
						LoadBitmap.getIntence().loadImage(map.get("msg_tex"), new LoadBitmap.LoadBitmapCallBack() {
							public void callBack(Bitmap bit) {
								if (bit != null) {
									setImgSize(send_img_, 0, 61 / 70.0f, 3);
									send_img_.setBackground(new BitmapDrawable(bit));
								}else {
									new Handler().postDelayed(new Runnable() {
										public void run() {
											LoadBitmap.getIntence().loadImage(map.get("msg_tex"), new LoadBitmap.LoadBitmapCallBack() {
												public void callBack(Bitmap bit) {
													if (bit != null) {
														setImgSize(send_img_, 0, 61 / 70.0f, 3);
														send_img_.setBackground(new BitmapDrawable(bit));
													}
												}
											});
										}
									}, 3*1000);
									
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
							}else if ("4".equals(type)) {
								AndroidUtils.loadMp4(map.get("richbody"), ChatActivity.this);
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
								setBigImg(map, map.get("timestamp"));
							}
						}
					};
					send_linelayout.setOnClickListener(listener);
					mChatUtils.setOnLongClick(send_linelayout, type, map);
				} else {// 对方发的消息显示
					get_layout.setVisibility(View.VISIBLE);
					ImageView head = (ImageView) v.findViewById(R.id.get_head);
					LoadBitmap.getIntence().loadImage(user_head, head);

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
						get_tex.setVisibility(View.INVISIBLE);
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
								setBigImg(map, map.get("timestamp"));
								
							} else if ("4".equals(type)) {
								if (!StringUtil.isEmpty(map.get("richbody"))) {
									String paString[] = map.get("richbody").split(",");
									if (paString.length == 2) {
										AndroidUtils.loadMp4(paString[1], ChatActivity.this);
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
		
		listView.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				chat_tel_layout.setVisibility(View.GONE);
				add_layout.setVisibility(View.GONE);
				imm.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0); // 关闭软键盘
				return false;
			}
		});

		listView.setXListViewListener(new IXListViewListener() {
			public void onRefresh() {
				System.out.println("n000 = " + mHistoryData);
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

		ArrayList<HashMap<String, String>> list = helper.select("tab_" + getUid() + userID, mData.size());
		if (list != null) {
			mData.addAll(0, list);
			adapter.notifyDataSetChanged();
			for (int i = 0; i < list.size(); i++) {
				if (!StringUtil.isEmpty(list.get(i).get("time"))) {
					if (!StringUtil.isEmpty(list.get(i).get("timestamp")))
						try {
							time_str = Long.valueOf(list.get(i).get("timestamp"));
						} catch (Exception e) {
						}
					break;
				}
			}
		}
		listView.setSelection(adapter.getCount() - 1);
		new GetData().execute("0", "", "");
		imm.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0); // 关闭软键盘
	}

	private void setBigImg(HashMap<String, String> m, String msg){
		ArrayList<HashMap<String, String>> list = helper.select("tab_" + getUid() + userID);
		ArrayList<String> list1 = new ArrayList<String>(); 
		
		int n=0;
		for(HashMap<String, String> map : list){
			if("2".equals(map.get("type"))){
				list1.add(map.get("richbody"));
				if(msg.equals(map.get("timestamp"))){
					n = list1.size() -1;
				}
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
		Logger.i("view视图大小：" + params.width + "----" + params.height);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_img:
			Intent intent1 = new Intent();
			intent1.setType("image/*");
			intent1.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent1, 2);
			break;
		case R.id.btn_pho:
			imgUrl = SendFile.takePhoto((Activity) mContext, 1);
			break;
		case R.id.btn_address:
			Intent intent = new Intent(mContext, ScreenShotActivity.class);
			intent.putExtra("lat", getPre("address_lat"));
			intent.putExtra("lon", getPre("address_lng"));
			intent.putExtra("screen", "yes");
			startActivityForResult(intent, 20140403);
			break;
		case R.id.btn_add:
			isimg = true;
			btn_face.setImageResource(R.drawable.ico_chat_face);
			isvideo = true;
			isvoice = true;
			btn_video.setImageResource(R.drawable.ico_chat_voice);
			face_layout.setVisibility(View.GONE);
			video_btn.setVisibility(View.GONE);
			voice_btn.setVisibility(View.GONE);
			chat_edit.setVisibility(View.VISIBLE);
			if (add_layout.getVisibility() == View.VISIBLE) {
				add_layout.setVisibility(View.GONE);
			} else {
				add_layout.setVisibility(View.VISIBLE);
				imm.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0); // 关闭软键盘
			}
			Logger.i("btn_add" + adapter.getCount());
			listView.setSelection(adapter.getCount() - 1);
			break;
		case R.id.btn_face:
			add_layout.setVisibility(View.GONE);
			btn_video.setImageResource(R.drawable.ico_chat_voice);
			video_btn.setVisibility(View.GONE);
			voice_btn.setVisibility(View.GONE);
			chat_edit.setVisibility(View.VISIBLE);
			if (isimg) {
				isimg = false;
				btn_face.setImageResource(R.drawable.ico_chat_jianpan);
				face_layout.setVisibility(View.VISIBLE);
				imm.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0); // 关闭软键盘
			} else {
				isimg = true;
				btn_face.setImageResource(R.drawable.ico_chat_face);
				face_layout.setVisibility(View.GONE);
			}
			Logger.i("btn_face" + adapter.getCount());
			listView.setSelection(adapter.getCount() - 1);
			break;
		case R.id.btn_video:
			face_layout.setVisibility(View.GONE);
			add_layout.setVisibility(View.GONE);
			voice_btn.setVisibility(View.GONE);
			btn_face.setImageResource(R.drawable.ico_chat_face);
			if (isvideo && isvoice) {
				isvideo = false;
				isvoice = false;
				btn_video.setImageResource(R.drawable.ico_chat_jianpan);
				video_btn.setVisibility(View.VISIBLE);
				chat_edit.setVisibility(View.GONE);
				imm.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0); // 关闭软键盘
			} else {
				isvideo = true;
				isvoice = true;
				btn_video.setImageResource(R.drawable.ico_chat_voice);
				video_btn.setVisibility(View.GONE);
				chat_edit.setVisibility(View.VISIBLE);
				//imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);// 打开软键盘
			}
			break;
		case R.id.send:
			send("1", chat_edit.getText().toString(), "");
			break;
		case R.id.video_btn:
			// Intent intent2 = new Intent(mContext, VideoActivity.class);
			// startActivityForResult(intent2, VIDEO);
			
			
//			Uri uri = VideoXiaoYingIntent.getOutputUri("chat");
//			video_path = uri.getPath();
//			Intent intent2 = VideoXiaoYingIntent.initCaptureVideoIntent(getPackageName(), uri);
//			startActivityForResult(intent2, AndroidUtils.VIDEO);
			
			
			
			// video_path = AndroidUtils.video(ChatActivity.this);
			break;
		case R.id.btn_voice:
			face_layout.setVisibility(View.GONE);
			add_layout.setVisibility(View.GONE);
			video_btn.setVisibility(View.GONE);
			btn_face.setImageResource(R.drawable.ico_chat_face);
			if (isvoice) {
				isvoice = false;
				btn_video.setImageResource(R.drawable.ico_chat_jianpan);
				voice_btn.setVisibility(View.VISIBLE);
				chat_edit.setVisibility(View.GONE);
				imm.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0); // 关闭软键盘
			} else {
				isvoice = true;
				btn_video.setImageResource(R.drawable.ico_chat_voice);
				voice_btn.setVisibility(View.GONE);
				chat_edit.setVisibility(View.VISIBLE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);// 打开软键盘
			}
			break;
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param type
	 *            消息类型
	 * @param chat_con
	 *            消息内容
	 * @param time
	 *            消息时间
	 */
	private void send(String type, String chat_con, String time) {
		Logger.i("chat_con=" + chat_con);
		if (!StringUtil.isEmpty(chat_con)) {
			// "user_send, user_get, type, msg_tex msg_img, send"
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("user_id", userID);
			map.put("send", "1");
			if ("4".equals(type)) {
				map.put("msg_tex", chat_con.split(",")[0]);
			} else
				map.put("msg_tex", chat_con);

			map.put("type", type);

			map.put("duartion", time + "");
			map.put("richbody", "");
			if (!"1".equals(type))
				map.put("richbody", chat_con);
			if ("4".equals(type))
				map.put("richbody", chat_con.split(",")[1]);
			if ("5".equals(type)) {
				map.put("msg_tex", getPre("address"));
			}
			map.put("timestamp", System.currentTimeMillis() / 1000 + "");
			map.put("head_img", head);
			map.put("user_name", userID);
			map.put("nick_name", name);
			map.put("sex", "0");
			map.put("isread", "0");

			HashMap<String, String> list = getTime(map);
			helper.insert("tab_" + getUid() + userID, list);
			mData.add(list);
			if (adapter != null){
				adapter.notifyDataSetChanged();
				listView.setSelection(adapter.getCount() - 1);
			}
			// if (StringUtil.isEmpty(video_path))
			new GetData().execute(type, chat_con, time);
		}
		chat_edit.setText("");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Logger.i(video_path + "path--" + requestCode + "" + resultCode);
		String path = "";
		if (requestCode == 2 && resultCode == RESULT_OK) {
			String str = SendFile.doPhoto(this, requestCode, data);
			Logger.i("path" + str);
			if (!StringUtil.isEmpty(str)) {
				str = BitmapUtil.rotaingImageView(str);
				send("2", str, "");
			}
		} else if (requestCode == 3 && resultCode == RESULT_OK) {
			Logger.i("path" + imgUrl);
			if (!StringUtil.isEmpty(imgUrl)) {
				path = BitmapUtil.rotaingImageView(imgUrl);
				imgUrl = path;
				send("2", path, "");
				imgUrl = "";
			}
		} else if ((requestCode == 20140403) && resultCode == RESULT_OK) {
			send("5", "[位置]", "");
		} else if (requestCode == AndroidUtils.VIDEO && resultCode == RESULT_OK) {
			Logger.i("video_path = " + video_path);
			new SendVideo().execute(video_path);
			video_path = "";
		}
	}

	/**
	 * 0.登录,1.文字，表情。2.图片。3.语音。4.地理位置
	 */
	private class GetData extends AsyncTask<String, Void, Boolean> {
		String type = "", chat_con = "";

		@Override
		protected Boolean doInBackground(String... arg0) {
			// 消息发送
			type = arg0[0];
			chat_con = arg0[1];
			boolean t = false;
			XmppPacketExtension xmp = new XmppPacketExtension();
			xmp.setCategory(type);
			xmp.setTimestamp(System.currentTimeMillis() / 1000 + "");
			if ("0".equals(type)) {
				if (!XmppClient.getInstance(mContext).isLogin())
					XmppClient.getInstance(mContext).login(getUserTel());
				// DataRequest.getHashMapFromUrl_Base64(AppConfig.CAR_LON_LAT);
				t = true;
			} else if ("1".equals(type)) {
				t = XmppClient.getInstance(mContext).sendMessage(userID, chat_con, xmp);
			} else if ("2".equals(type)) {
				xmp.setRichbody(FaceAction.GetImageStr(mContext, chat_con));
				t = XmppClient.getInstance(mContext).sendMessage(userID, "[图片]", xmp);// FaceAction.GetImageStr(chat_con)
			} else if ("3".equals(type)) {
				xmp.setDuartion(arg0[2] + "");
				audioAction.getVoicePath();
				xmp.setRichbody(FaceAction.GetImageStr(mContext, audioAction.mFimeMp3));
				t = XmppClient.getInstance(mContext).sendMessage(userID, "[语音]", xmp);
			} else if ("4".equals(type)) {
				xmp.setRichbody(chat_con);
				t = XmppClient.getInstance(mContext).sendMessage(userID, "[视频]", xmp);
			} else if ("5".equals(type)) {
				xmp.setRichbody("");
				t = XmppClient.getInstance(mContext).sendMessage(userID, getPre("address"), xmp);
			}
			chat_con = "";
			return t;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if ("0".equals(type)) {
				if (result) {
					try {
						XmppClient.getInstance(mContext).xmppChatManagerListener.mXmppMessageListener
								.setXmppGetMessage(new XmppGetMessage() {

									@Override
									public void getMessage(Bundle bundle) {
										Logger.i("聊天界面" + bundle);
										Message m = new Message();
										m.setData(bundle);
										mHandler.sendMessage(m);
									}
								});
					} catch (Exception e) {
					}
				}
			} else {
				if (!result)
					makeText("发送失败");
			}
			adapter.notifyDataSetChanged();
		}
	}

	private class SendVideo extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			mLoadBar.setMessage("发送视频中...");
			mLoadBar.show();
		}

		@Override
		protected String doInBackground(String... params) {
			return DataRequest.getStringFrom_base64(DataRequest.SendFile(getUrl(AppConfig.VIDEO_CHAT_VIDEO), "video_sid", params[0]));
		}

		@Override
		protected void onPostExecute(String result) {
			HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(result);
			if (map != null && "1".equals(map.get("code"))) {
				HashMap<String, String> data = DataRequest.getHashMapFromJSONObjectString(map.get("data"));
				if (data != null) {
					// new GetData().execute("4", data.get("img_url") + "," +
					// data.get("video_url"), "");
					send("4", data.get("img_url") + "," + data.get("video_url"), "");
				}
			} else {
				Toast.makeText(mContext, "发送视频失败", Toast.LENGTH_SHORT).show();
			}
			Logger.i("视频录制：" + map);
			mLoadBar.dismiss();
		}
	}

	/**
	 * 消息接收
	 */
	private Handler mHandler = new Handler(new Handler.Callback() {
		public boolean handleMessage(Message m) {
			HashMap<String, String> map = new HashMap<String, String>();
			Bundle bundle = m.getData();
			map.put("msg_tex", bundle.getString("body"));
			map.put("type", bundle.getString("category"));
			map.put("duartion", bundle.getString("duartion"));
			Logger.i("接收消息插入数据" + bundle.getString("richbody"));
			map.put("richbody", bundle.getString("richbody"));
			map.put("timestamp", bundle.getString("timestamp"));
			map.put("send", "2");
			map.put("head_img", user_head);
			map.put("user_id", userID);
			map.put("user_name", userID);
			map.put("nick_name", name);
			map.put("sex", sex);
			map.put("isread", "0");

			HashMap<String, String> list = getTime(map);
			Logger.i("接收消息插入数据");
			helper.insert("tab_" + getUid() + userID, list);
			mData.add(list);
			adapter.notifyDataSetChanged();
			listView.setSelection(adapter.getCount() - 1);
			return false;
		}
	});

	long time_send = 0;

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		switch (arg1.getAction()) {
		case MotionEvent.ACTION_DOWN:
			voice_btn.setText("松开 发送");
			voice_btn.setBackgroundColor(0xff57a7fc);
			audioAction.startVoice();
			time_send = System.currentTimeMillis();
			break;
		case MotionEvent.ACTION_UP:
			voice_btn.setText("按住 说话");
			voice_btn.setBackgroundColor(0xff919191);
			boolean isok = audioAction.stopVoice();
			long time = (System.currentTimeMillis() - time_send) / 1000;
			time_send = 0;
			if (time < 1) {
				makeText("时间太短");
				return false;
			}
			if (!isok) {
				makeText("录音失败");
				return false;
			}
			send("3", audioAction.mFimeMp3, time + "");
			break;
		default:
			break;
		}
		return false;
	}

	private HashMap<String, String> getTime(HashMap<String, String> data) {
		long t = 0;
		if (!StringUtil.isEmpty(data.get("timestamp")))
			try {
				t = Long.valueOf(data.get("timestamp"));
			} catch (Exception e) {
			}
		Logger.i(time_str + "==" + t + "==" + (-time_str + t));
		if ((-time_str + t) > 2 * 60) {
			time_str = t;
			data.put("time", "" + t);
		} else {
			data.put("time", "");
		}

		return data;
	}

	@Override
	protected void onDestroy() {
		close();
		super.onDestroy();
	}

	CommonDialog commonDialog;

	private class GetInfo extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			DBhelper db = new DBhelper(mContext, IwoSQLiteHelper.FRIEND_TAB);
			mHistoryData = helper.select("tab_" + getUid() + userID, "timestamp", true);
			ArrayList<HashMap<String, String>> list = db.select("tab_" + getUserTel(), "name", userID);
			db.close();
			Logger.i("消息：" + list);
			if (list != null && list.size() > 0)
				return list.get(0);
			else
				return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			Logger.i("result===" + result + getIntent().getStringExtra("from"));
			LinearLayout box_layout = (LinearLayout) findViewById(R.id.box_layout);

			if (result != null) {
				user_head = result.get("avatar");
				sex = result.get("sex");
				name = result.get("nick");
			} else {

				box_layout.setVisibility(View.GONE);
				int[] clikViews = new int[] { R.id.determine, R.id.cancel };

				OnClickListener clickListener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						switch (v.getId()) {
						case R.id.determine:
							new AddF().execute();
							commonDialog.dismiss();
							break;
						case R.id.cancel:
							commonDialog.dismiss();
							finish();
							break;
						default:
							break;
						}
					}
				};
				commonDialog = new CommonDialog(mContext, "需要添加好友才能聊天", clickListener, R.layout.loading_dialog_text, clikViews,
						R.id.tipText_view);
				commonDialog.show();
				return;
			}
			setTitle(name);
			// getBitmapUrl(user_head, head);
			initListView();
		}
	}

	private class AddF extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			return XmppClient.getInstance(mContext).addFriend(userID, null);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				makeText("已发送好友邀请");
			}
			finish();
		}
	}
}
