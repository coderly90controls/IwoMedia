package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.AudioAction;
import com.android.iwo.media.action.DBhelper;
import com.android.iwo.media.action.FaceAction;
import com.android.iwo.media.action.IwoSQLiteHelper;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.night.chat.XmppMessageListener.XmppGetMessage;
import com.android.iwo.media.night.chat.XmppNightClient;
import com.android.iwo.media.night.chat.XmppPacketExtension;
import com.android.iwo.util.bitmapcache.ImageFileCache;
import com.android.iwo.util.bitmapcache.ImageGetFromHttp;
import com.android.iwo.util.bitmapcache.SyncBitmap;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.adapter.ViewPageAdapter;
import com.test.iwomag.android.pubblico.util.AndroidUtils;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.DateUtil;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.SendFile;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class NightChat extends BaseActivity implements OnClickListener, OnTouchListener {
	private EditText chat_edit;
	private String user_head = "", head = "", sex = "";
	private ImageView btn_video, btn_face, btn_add;
	private TextView send;
	private Button video_btn, btn_voice, voice_btn;
	private RelativeLayout face_layout;
	private ViewPager face;
	private LinearLayout add_layout;
	private boolean isvideo = true, isimg = true, isvoice = true;
	private InputMethodManager imm = null;
	private ListView listView;
	private IwoAdapter adapter;
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private String imgUrl;
	
	private AudioAction audioAction;
	private DBhelper helper;
	private long time_str = 0;
	public static boolean isIN = false;
	public static String userID = "";
	public static String FROM = "";
	private Bitmap user_head_bit, my_big;
	private String video_path = "";//发送视频的地址
	private static final int VIDEO = 20140520;
	private String send_head, send_name;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		isIN = true;
		userID = "";
		FROM = "";
		init();
		setViewClick();
		getBitmapUrl(user_head);
		initListView();
	}

	@Override
	public void onBackPressed() {
		close();
		setResult(RESULT_OK);
		finish();
	}

	private void close() {
		if (helper != null) {
			helper.delete("tab_n" + getUid() + userID);
			helper.close();
			helper = null;
		}
		XmppNightClient.getInstance(mContext).c = null;
		isIN = false;
		userID = "";
		FROM = "";
		mData.clear();
		audioAction.stopPlay();
	}
	
	private void getBitmapUrl(final String url) {
		new Thread(new Runnable() {
			public void run() {
				if (!StringUtil.isEmpty(url))
					user_head_bit = ImageGetFromHttp.downloadBitmap(url);
				if (!StringUtil.isEmpty(send_head))
					my_big = ImageGetFromHttp.downloadBitmap(send_head);
			}
		}).start();
	}

	/**
	 * 初始化
	 */
	private void init() {
		NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
		setBack(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
		send_head = getIntent().getStringExtra("send_head");
		send_name = getIntent().getStringExtra("send_name");
		userID = getIntent().getStringExtra("userID");
		ImageView right_img = (ImageView)findViewById(R.id.right_img);
		right_img.setVisibility(View.VISIBLE);
		right_img.setImageResource(R.drawable.home);
		right_img.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(mContext, NightFriendDatailActivity.class);
				intent.putExtra("friend_tp", userID);
				startActivity(intent);
			}
		});
		helper = new DBhelper(this, IwoSQLiteHelper.MESSAGE_TAB);
		audioAction = new AudioAction();
		userID = getIntent().getStringExtra("userID");
		Logger.i("userID----" + userID);
		user_head = getIntent().getStringExtra("head_img");
		setNightTitle(getIntent().getStringExtra("name"));
		head = getPre("n_user_head");
		Logger.i("我的头像地址：" + head);
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
		voice_btn.setOnTouchListener(this);//TODO
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
				if (arg3 > 0 || arg1 >0) {
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
		listView = (ListView) findViewById(R.id.chat_night_listview);
		listView.setVisibility(View.VISIBLE);
		adapter = new IwoAdapter(this, mData) {
			@Override
			public View getView(final int position, View v, ViewGroup parent) {
				if (v == null)
					v = mInflater.inflate(R.layout.list_chat_item, parent, false);
				// "user_send, user_get, type, msg_tex msg_img, send"
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
				ImageView send_img_ = (ImageView) v.findViewById(R.id.send_img_);
				
				TextView get_tex = (TextView) v.findViewById(R.id.get_tex);
				TextView get_dur = (TextView) v.findViewById(R.id.get_dur);
				TextView get_add_tex = (TextView) v.findViewById(R.id.get_add_tex);
				final ImageView get_img = (ImageView) v.findViewById(R.id.get_img);
				ImageView get_img_ = (ImageView) v.findViewById(R.id.get_img_);
				
				//ImageView ico_chat_video_btn = (ImageView)v.findViewById(R.id.video_ico);
				//ico_chat_video_btn.setVisibility(View.GONE);
				send_dur.setVisibility(View.GONE);
				send_add_tex.setVisibility(View.GONE);
				send_tex.setVisibility(View.GONE);
				send_img.setVisibility(View.GONE);
				send_img_.setVisibility(View.GONE);
				get_img_.setVisibility(View.GONE);
				get_img.setVisibility(View.GONE);
				get_tex.setVisibility(View.GONE);
				get_dur.setVisibility(View.GONE);
				get_add_tex.setVisibility(View.GONE);
				if ("1".equals(map.get("send"))) {// 我发的消息显示
					send_tex.setTextColor(0xff000000);
					send_layout.setVisibility(View.VISIBLE);
					ImageView head = (ImageView) v.findViewById(R.id.send_head);
					if (my_big != null) {
						head.setImageBitmap(my_big);
					} else {
						head.setImageResource(R.drawable.ico_default);
					}

					if ("1".equals(type)) {//文字表情
						send_tex.setVisibility(View.VISIBLE);
						//send_tex.setText(FaceAction.setTextView(mactivity, map.get("msg_tex")));
					} else if ("2".equals(type)) {//图片
						Bitmap bitmap = BitmapFactory.decodeFile(map.get("richbody"));
						send_img_.setVisibility(View.VISIBLE);
						//setImgSize(send_img, 0, 1, 3);
						if (bitmap == null) {
							send_img_.setImageResource(R.drawable.video_zhanwei);
						} else {
							setImgSize(send_img_, 0, bitmap.getHeight()/(1.0f*bitmap.getWidth()), 3);
							send_img_.setImageBitmap(bitmap);
						}
					} else if ("3".equals(type)) {//语音
						send_tex.setTextColor(0xffffffff);
						send_img.setVisibility(View.VISIBLE);
						if ("1".equals(map.get("isread"))) {
							send_img.setImageResource(R.drawable.ico_chat_voice_send_read);
						} else
							send_img.setImageResource(R.drawable.ico_chat_voice_send);
						
						String string = "";
						try {
							int dua = Integer.valueOf(map.get("duartion"));
							Logger.i("dua" + dua);
							for(int i=0; i<dua && i<20; i++){
								string = string+ "1";
							}
						} catch (Exception e) {
						}
						
						send_tex.setText("" + string);
						send_tex.setVisibility(View.VISIBLE);
						send_dur.setText(map.get("duartion") + "''");
						send_dur.setVisibility(View.VISIBLE);
					} if("4".equals(type)){//视频
						Bitmap bitmap = AndroidUtils.getBitmapsFromVideo(map.get("richbody"));
						send_img.setVisibility(View.VISIBLE);
						//ico_chat_video_btn.setVisibility(View.VISIBLE);
						//setImgSize(send_img, 0, 1, 3);
						if (bitmap == null) {
							send_img.setImageResource(R.drawable.video_zhanwei);
						} else {
							send_img.setImageBitmap(bitmap);
						}
					} else if ("5".equals(type)) {// 位置
						String tex = map.get("msg_tex");
						if (!StringUtil.isEmpty(tex)) {
							String[] add = tex.split(",");
							if (add.length > 0) {
								send_add_tex.setText(add[0]);
							}
						}
						send_add_tex.setVisibility(View.VISIBLE);
						send_img.setVisibility(View.VISIBLE);
						send_img.setImageResource(R.drawable.location_msg);
					}
					send_layout.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							if ("3".equals(type)) {
								mData.get(position).put("isread", "1");
								send_img.setImageDrawable(new ColorDrawable(0x00000000));
								send_img.setBackgroundResource(R.drawable.chat_send_read_animation);
								AnimationDrawable ani = (AnimationDrawable) send_img.getBackground();
								audioAction.readVoice(map.get("richbody"), ani, send_img, true);
							} if ("4".equals(type)) {
								AndroidUtils.loadMp4(map.get("richbody"), NightChat.this);
							} else if ("5".equals(type)) {
								Intent intent = new Intent(mContext, ScreenShotActivity.class);
								intent.putExtra("type", "day");
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
								Intent intent = new Intent(mContext, ChatBitmapActivity.class);
								intent.putExtra("path", map.get("richbody"));
								intent.putExtra("type", "night");
								startActivity(intent);
							}
						}
					});

				} else {// 对方发的消息显示
					get_layout.setVisibility(View.VISIBLE);
					ImageView head = (ImageView) v.findViewById(R.id.get_head);
					get_tex.setTextColor(0xff000000);
					if (user_head_bit != null) {
						head.setImageBitmap(user_head_bit);
					} else {
						head.setImageResource(R.drawable.ico_default);
					}
					if ("1".equals(type)) {
						get_tex.setVisibility(View.VISIBLE);
						//get_tex.setText(FaceAction.setTextView(mactivity, map.get("msg_tex")));
					} else if ("2".equals(type)) {
						Bitmap bitmap = BitmapFactory.decodeFile(map.get("richbody"));
						get_img_.setVisibility(View.VISIBLE);
						//setImgSize(get_img, 0, 1, 3);
						if (bitmap == null) {
							get_img_.setImageResource(R.drawable.video_zhanwei);
						} else {
							setImgSize(get_img_, 0, bitmap.getHeight()/(1.0f*bitmap.getWidth()), 3);
							get_img_.setImageBitmap(bitmap);
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
							for(int i=0; i<dua && i<20; i++){
								string = string + "1";
							}
						} catch (Exception e) {
						}
						get_tex.setText(string);
						get_tex.setVisibility(View.VISIBLE);
					} else if ("4".equals(type)) {
						get_img.setVisibility(View.VISIBLE);
						get_img.setImageResource(R.drawable.video_zhanwei);
						//setImgSize(get_img, 0, 1, 3);
						Logger.i("视频：" + map.get("richbody"));
						if(!StringUtil.isEmpty(map.get("richbody"))){
							String paString[] = map.get("richbody").split(",");
							if(paString.length == 2){
								Logger.i("视频111：" + paString[0]);
								SyncBitmap.getIntence(mContext).loadImage(paString[0], get_img, new SyncBitmap.LoadCallBack() {
									public void callBack(ImageView imageView, Bitmap bit) {
										if(bit != null) get_img.setImageBitmap(bit);
									}
								});
							}
						}
					}else if ("5".equals(type)) {
						String tex = map.get("msg_tex");
						if (!StringUtil.isEmpty(tex)) {
							String[] add = tex.split(",");
							if (add.length > 0) {
								get_add_tex.setText(add[0]);
							}
						}
						get_add_tex.setVisibility(View.VISIBLE);
						get_img.setVisibility(View.VISIBLE);
						get_img.setImageResource(R.drawable.location_msg);
					}
					get_layout.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							if ("3".equals(type)) {
								mData.get(position).put("isread", "1");
								get_img.setImageDrawable(new ColorDrawable(0x00000000));
								get_img.setBackgroundResource(R.drawable.chat_get_read_animation);
								AnimationDrawable ani = (AnimationDrawable) get_img.getBackground();
								audioAction.readVoice(map.get("richbody"), ani, get_img, false);
							} else if ("5".equals(type)) {
								Intent intent = new Intent(mContext, ScreenShotActivity.class);
								intent.putExtra("type", "night");
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
								Intent intent = new Intent(mContext, ChatBitmapActivity.class);
								Logger.i("richbody;" + map.get("richbody"));
								intent.putExtra("path", map.get("richbody"));

								startActivity(intent);
							}else if ("4".equals(type)){
								if(!StringUtil.isEmpty(map.get("richbody"))){
									String paString[] = map.get("richbody").split(",");
									if(paString.length == 2){
										AndroidUtils.loadMp4(paString[1], NightChat.this);
									}else
										makeText("播放失败");
								}else 
									makeText("播放失败");
							}
						}
					});
				}
				return v;
			}
		};

		listView.setCacheColorHint(0);
		listView.setDividerHeight(0);
		listView.setSelector(new ColorDrawable(0x00000000));
		listView.setAdapter(adapter);
		ArrayList<HashMap<String, String>> list = null;
		if(helper != null)
			list = helper.select("tab_n" + getUid() + userID, mData.size());
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
		// new GetDataList().execute();
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
			intent.putExtra("type", "day");
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
			break;
		case R.id.btn_video:
			face_layout.setVisibility(View.GONE);
			add_layout.setVisibility(View.GONE);
			voice_btn.setVisibility(View.GONE);
			btn_face.setImageResource(R.drawable.ico_chat_face);
			if (isvideo) {
				isvideo = false;
				btn_video.setImageResource(R.drawable.ico_chat_jianpan);
				video_btn.setVisibility(View.VISIBLE);
				chat_edit.setVisibility(View.GONE);
				imm.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0); // 关闭软键盘
			} else {
				isvideo = true;
				btn_video.setImageResource(R.drawable.ico_chat_voice);
				video_btn.setVisibility(View.GONE);
				chat_edit.setVisibility(View.VISIBLE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);// 打开软键盘
			}
			break;
		case R.id.send:
			send("1", chat_edit.getText().toString(), "");
			break;
		case R.id.video_btn:
			Intent intent2 = new Intent(mContext, VideoActivity.class);
			startActivityForResult(intent2, VIDEO);
			//video_path = AndroidUtils.video(NightChat.this);
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
			map.put("msg_tex", chat_con);
			map.put("type", type);

			map.put("duartion", time + "");
			map.put("richbody", "");
			if (!"1".equals(type))
				map.put("richbody", chat_con);
			if ("5".equals(type)) {
				map.put("msg_tex", getPre("address"));
			}
			map.put("timestamp", System.currentTimeMillis() / 1000 + "");
			map.put("head_img", send_head);
			map.put("user_name", userID);
			map.put("isread", "0");

			HashMap<String, String> list = getTime(map);
			mData.add(list);
			adapter.notifyDataSetChanged();
			listView.setSelection(adapter.getCount() - 1);
			if(StringUtil.isEmpty(video_path))
				new GetData().execute(type, chat_con, time);
			video_path = "";
		}
		chat_edit.setText("");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Logger.i("path--" + requestCode + "" + resultCode);
		ImageFileCache cache = new ImageFileCache();
		Bitmap bitmap = null;
		String path = "";
		if (requestCode == 2 && resultCode == RESULT_OK) {
			String str = SendFile.doPhoto(this, requestCode, data);
			Logger.i("path" + str);
			if (!StringUtil.isEmpty(str)) {
				bitmap = FaceAction.reduce(BitmapFactory.decodeFile(str), (int) (500 * scale), (int) (500 * scale));
				str = cache.saveBitmap(bitmap, "a/" + System.currentTimeMillis() + ".jpg");
				send("2", str, "");
			}
		} else if (requestCode == 3 && resultCode == RESULT_OK) {
			Logger.i("path" + imgUrl);
			if (!StringUtil.isEmpty(imgUrl)) {
				bitmap = FaceAction.reduce(BitmapFactory.decodeFile(imgUrl), (int) (500 * scale), (int) (500 * scale));
				path = cache.saveBitmap(bitmap, "a/" + System.currentTimeMillis() + ".jpg");
				send("2", path, "");
				imgUrl = "";
			}
		} else if ((requestCode == 20140403) && resultCode == RESULT_OK) {
			send("5", "[位置]", "");
		} else if(requestCode == AndroidUtils.VIDEO && resultCode == RESULT_OK){
			Logger.i("video_path = " + video_path);
			new SendVideo().execute(video_path);
			send("4", video_path, "");
		} else if(requestCode == VIDEO && resultCode == RESULT_OK){
			if(data != null)
				video_path = data.getStringExtra("video_url");
			new SendVideo().execute(video_path);
			send("4", video_path, "");
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
			xmp.setHead(send_head);
			xmp.setNick(send_name);
			if ("0".equals(type)) {
				if (!XmppNightClient.getInstance(mContext).isLogin())
					XmppNightClient.getInstance(mContext).login(mContext, getUserTel());
				// DataRequest.getHashMapFromUrl_Base64(AppConfig.CAR_LON_LAT);
				t = true;
			} else if ("1".equals(type)) {
				t = XmppNightClient.getInstance(mContext).sendMessage(userID, chat_con, xmp);
			} else if ("2".equals(type)) {
				xmp.setRichbody(FaceAction.GetImageStr(mContext, chat_con));
				t = XmppNightClient.getInstance(mContext).sendMessage(userID, "[图片]", xmp);// FaceAction.GetImageStr(chat_con)
			} else if ("3".equals(type)) {
				xmp.setDuartion(arg0[2] + "");
				audioAction.getVoicePath();
				xmp.setRichbody(FaceAction.GetImageStr(mContext, audioAction.mFimeMp3));
				t = XmppNightClient.getInstance(mContext).sendMessage(userID, "[语音]", xmp);
			} else if("4".equals(type)){
				xmp.setRichbody(chat_con);
				t = XmppNightClient.getInstance(mContext).sendMessage(userID, "[视频]", xmp);
			}else if ("5".equals(type)) {
				xmp.setRichbody("");
				t = XmppNightClient.getInstance(mContext).sendMessage(userID, getPre("address"), xmp);
			}
			chat_con = "";
			return t;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if ("0".equals(type)) {
				if (result) {
					XmppNightClient.getInstance(mContext).xmppChatManagerListener.mXmppMessageListener.setXmppGetMessage(new XmppGetMessage() {

						@Override
						public void getMessage(Bundle bundle) {
							Logger.i("聊天界面" + bundle);
							Message m = new Message();
							m.setData(bundle);
							mHandler.sendMessage(m);
						}
					});
				}
			} else {
				if (!result)
					makeText("发送失败");
			}
			adapter.notifyDataSetChanged();
		}
	}

	private class SendVideo extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			return DataRequest.getStringFrom_base64(DataRequest.SendFile(getUrl(AppConfig.VIDEO_CHAT_VIDEO), "video_sid", params[0]));
		}
		
		@Override
		protected void onPostExecute(String result) {
			HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(result);
			if(map != null && "1".equals(map.get("code"))){
				HashMap<String, String> data = DataRequest.getHashMapFromJSONObjectString(map.get("data"));
				if(data != null)
				new GetData().execute("4", data.get("img_url") + "," + data.get("video_url"), "");
			}
			Logger.i("视频录制：" + map);
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
			map.put("sex", sex);
			map.put("isread", "0");

			HashMap<String, String> list = getTime(map);
			Logger.i("接收消息插入数据");
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
			voice_btn.setText("松开  发送");
			voice_btn.setBackgroundColor(0xffF5567C);
			audioAction.startVoice();
			time_send = System.currentTimeMillis();
			Logger.i("time_send" + time_send);
			break;
		case MotionEvent.ACTION_UP:
			voice_btn.setText("按住  说话");
			voice_btn.setBackgroundColor(0xff919191);
			audioAction.stopVoice();
			Logger.i("time_send_stop_" + time_send);
			long time = (System.currentTimeMillis() - time_send) / 1000;
			time_send = 0;
			Logger.i("time_send_stop" + time);
			if (time < 1) {
				makeText("时间太短");
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
}