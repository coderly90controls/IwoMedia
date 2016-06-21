package com.android.iwo.media.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iwo.media.activity.ChatSendOther;
import com.android.iwo.media.chat.XmppClient;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.view.PopWindowDialog;
import com.android.iwo.media.view.PopWindowDialog.OnViewClickListener;
import com.test.iwomag.android.pubblico.util.FileCache;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;

public class ChatUtils {

	private Context mContext;
	private float scale = 0.0f;
	private DisplayMetrics dm;
	private onDeleteListener mlDeleteListener;
	public boolean isLongClick = false;

	public ChatUtils(Context context, float s, DisplayMetrics d) {
		mContext = context;
		scale = s;
		dm = d;
	}

	/**
	 * 添加好友
	 */
	public static void addFriend(Context context, String user_name, String head_img, String nick_name) {
		DBhelper db = new DBhelper(context, IwoSQLiteHelper.MESSAGE_TAB);
		String name = PreferenceUtil.getString(context, "user_name", "");
		db.update("tab_msg" + name, "type", "1", "user_name", user_name);
		db.update("tab_msg" + name, "msg_tex", "已经是你的好友，点击聊天", "user_name", user_name);
		DBhelper per = new DBhelper(context, IwoSQLiteHelper.FRIEND_TAB);
		per.delete("tab_" + name, "name", user_name);
		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("name", user_name);
		map2.put("avatar", head_img);
		map2.put("nick", nick_name);
		Logger.i("map2" + map2);
		per.insert("tab_" + name, map2);
		Logger.i("查询好友表：：：：：" + per.select("tab_" + name));
		per.close();
		db.close();
		XmppClient.getInstance(context).friendAddDel(true, name, user_name);
		// XmppClient.getInstance(context).updateFriend(user_name);
		context.sendBroadcast(new Intent("com.android.broadcast.receiver.media.refresh.CHAT_REFRESH_SHARE"));
		NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
		notificationManager.cancel(10010);
	}

	public void setOnLongClick(final View view, final String type, final HashMap<String, String> map) {
		view.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				setLongClick(view, type, map);
				return false;
			}
		});
	}

	public void setLongClick(View view, final String type, final HashMap<String, String> map) {
		PopWindowDialog dialog = new PopWindowDialog(mContext);
		if ("1".equals(type)) {// 文字
			dialog.setView("复制", "转发", "删除");
		} else if ("2".equals(type)) {// 图片
			dialog.setView("转发", "保存到本地", "删除");
		} else if ("3".equals(type)) {// 语音
			dialog.setView("转发", "保存到本地", "删除");
		} else if ("4".equals(type)) {// 视频
			dialog.setView("转发", "保存到本地", "删除");
		} else if ("5".equals(type)) {// 位置
			dialog.setView("转发", "删除");
		}

		dialog.setOnViewClickListener(new OnViewClickListener() {
			public void onClickRight() {
				if (!"5".equals(type)) {// 删除
					chatDelete(map.get("timestamp"));
					// Toast.makeText(mContext, "删除",
					// Toast.LENGTH_SHORT).show();
				}
			}

			public void onClickLeft() {
				if ("1".equals(type)) {// 复制
					chatCopy(map.get("msg_tex"));
				} else {// 转发
					// Toast.makeText(mContext, "转发",
					// Toast.LENGTH_SHORT).show();
					chatSend(map);
				}
			}

			public void onClickCenter() {
				if ("1".equals(type)) {// 转发
					// Toast.makeText(mContext, "转发",
					// Toast.LENGTH_SHORT).show();
					chatSend(map);
				} else if ("5".equals(type)) {// 删除
					// Toast.makeText(mContext, "删除",
					// Toast.LENGTH_SHORT).show();
					chatDelete(map.get("timestamp"));
				} else {// 保存到本地
					// Toast.makeText(mContext, "保存到本地",
					// Toast.LENGTH_SHORT).show();
					chatSave(map);
				}
			}
		});

		int[] location = new int[2];
		view.getLocationOnScreen(location);
		dialog.setWidth((int) (dialog.getViewWidth() * scale));
		dialog.setHeight((int) (55 * scale));
		Logger.i("item:" + location[0] + "---" + location[1] + "---" + view.getWidth());
		int w = dm.widthPixels;
		int dlw = view.getWidth();
		int dw = dialog.getWidth();
		int x = location[0];
		int arrw = 0;
		if (location[0] == scale * 52) {// 左边
			if (x + dlw >= dw) {
				arrw = (int) (dlw / 2.0f);
			} else {
				arrw = (int) (x + dlw / 2.0f);
				location[0] = 0;
			}
		} else {// 右边
			if (w - x > dw) {
				arrw = (int) (dlw / 2.0f);
			} else {
				arrw = (int) (dw - (w - x) + dlw / 2.0f);
			}
		}

		dialog.setArrow((int) (arrw - (13.5f * scale)));
		if ((location[1] - dialog.getHeight()) < 0) {
			dialog.showAtLocation(view, Gravity.NO_GRAVITY, location[0], 100);
		} else
			dialog.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] - dialog.getHeight());
		Logger.i("----" + (location[1] - dialog.getHeight()));
	}

	/**
	 * 保存到本地
	 */
	public void chatSave(HashMap<String, String> map) {
		if ("2".equals(map.get("type"))) {
			String newpath = FileCache.getInstance().CACHE_PATH + "/imgdownload/";
			File file = new File(newpath);
			// 创建下载目录
			if (!file.exists()) {
				file.mkdirs();
			}
			copyFile(map.get("richbody"), newpath + System.currentTimeMillis() + ".jpg");
		} else if ("3".equals(map.get("type"))) {
			String newpath = FileCache.getInstance().CACHE_PATH + "/voicedownload/";
			File file = new File(newpath);
			// 创建下载目录
			if (!file.exists()) {
				file.mkdirs();
			}
			copyFile(map.get("richbody"), newpath + System.currentTimeMillis() + ".mp3");
		} else {
			Toast.makeText(mContext, "开始下载...", Toast.LENGTH_SHORT).show();
			DownLoadAction action = new DownLoadAction();
			action.download(mContext, map.get("richbody"));
		}
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径
	 * @param newPath
	 *            String 复制后路径
	 * @return boolean
	 */
	public void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				Toast.makeText(mContext, "图片已保存在" + newPath, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, "图片保存失败", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 删除
	 */
	public void chatDelete(final String time) {
		final Dialog dialog = new Dialog(mContext, R.style.dialog_style);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view2 = inflater.inflate(R.layout.chat_send_dialog, null);

		dialog.setCancelable(true);// 不可以用“返回键”取消
		dialog.setContentView(view2, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));// 设置布局

		TextView name = (TextView) view2.findViewById(R.id.name);
		name.setVisibility(View.GONE);
		TextView ok = (TextView) view2.findViewById(R.id.ok);
		TextView cancle = (TextView) view2.findViewById(R.id.cancle);

		TextView title = (TextView) view2.findViewById(R.id.title);
		title.setText("是否删除？");

		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.ok:
					if (mlDeleteListener != null)
						mlDeleteListener.delete(time);
					break;
				case R.id.cancel:
					break;
				}
				dialog.dismiss();
			}
		};
		ok.setOnClickListener(listener);
		cancle.setOnClickListener(listener);
		dialog.show();
	}

	/**
	 * 转发
	 */
	public void chatSend(HashMap<String, String> map) {
		Intent intent = new Intent(mContext, ChatSendOther.class);
		intent.putExtra("map", map);
		mContext.startActivity(intent);
	}

	/**
	 * 复制
	 * 
	 * @param tex
	 */
	public void chatCopy(String content) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext.getSystemService("clipboard");
			ClipData clip = ClipData.newPlainText("label", content);
			clipboard.setPrimaryClip(clip);

		} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mContext.getSystemService("clipboard");
			clipboard.setText(content);
		}
		Toast.makeText(mContext, "已复制", Toast.LENGTH_LONG).show();
	}

	public SpannableString getClickableSpan1(final View view, String tel) {
		String s = "-0123456789";
		String str = "";
		int n = 0;
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < tel.length(); i++) {
			if (s.contains("" + tel.charAt(i))) {
				n++;
				str += tel.charAt(i);
				if (i == tel.length() - 1)
					if (n >= 7)
						list.add(str);
			} else {
				if (n >= 7)
					list.add(str);
				n = 0;
				str = "";
			}
		}

		SpannableString spanableInfo = new SpannableString(tel);

		for (int i = 0; i < list.size(); i++) {
			final String t = list.get(i);
			View.OnClickListener l = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Logger.i("电话号码");
					if (!isLongClick)
						setTel0(view, t);
					isLongClick = false;
				}
			};

			int start = tel.indexOf(t);
			int end = start + t.length();
			spanableInfo.setSpan(new Clickable0(l), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		return spanableInfo;
	}

	/**
	 * 对手机号的操作
	 * 
	 * @param view
	 */
	private void setTel0(final View view, final String tel) {

		OnClickListener listener = new OnClickListener() {

			public void onClick(View v) {
				Intent intent = null;
				switch (v.getId()) {
				case R.id.chat_tel_call:
					intent = new Intent();
					intent.setAction("android.intent.action.DIAL");
					intent.setData(Uri.parse("tel:" + tel));
					mContext.startActivity(intent);
					break;
				case R.id.chat_tel_copy:
					chatCopy(tel);
					break;
				case R.id.chat_tel_add:
					intent = new Intent(Intent.ACTION_INSERT);
					intent.setType("vnd.android.cursor.dir/person");
					intent.setType("vnd.android.cursor.dir/contact");
					intent.setType("vnd.android.cursor.dir/raw_contact");

					intent.putExtra(ContactsContract.Intents.Insert.PHONE, tel);
					mContext.startActivity(intent);
					break;
				case R.id.chat_tel_cancle:
					break;
				default:
					break;
				}
				view.setVisibility(View.GONE);
			}
		};
		((TextView) view.findViewById(R.id.chat_tel)).setText(tel + "可能是个手机号码，你可以");
		view.setVisibility(View.VISIBLE);
		view.findViewById(R.id.chat_tel_call).setOnClickListener(listener);
		view.findViewById(R.id.chat_tel_copy).setOnClickListener(listener);
		view.findViewById(R.id.chat_tel_cancle).setOnClickListener(listener);
		view.findViewById(R.id.chat_tel_add).setOnClickListener(listener);
	}

	public void setOnDeleteListener(onDeleteListener listener) {
		mlDeleteListener = listener;
	}

	public interface onDeleteListener {
		public void delete(String time);
	}

	class Clickable0 extends ClickableSpan implements OnClickListener {
		private final View.OnClickListener mListener;

		public Clickable0(View.OnClickListener l) {
			mListener = l;
		}

		@Override
		public void onClick(View v) {
			mListener.onClick(v);
		}
	}
}
