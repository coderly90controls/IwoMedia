package com.android.iwo.media.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.android.iwo.media.action.ActivityUtil;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.Constants;
import com.android.iwo.media.action.DBhelper;
import com.android.iwo.media.action.IwoSQLiteHelper;
import com.android.iwo.media.activity.ChatActivity;
import com.android.iwo.media.activity.ModelActivity;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.chat.GroupInfo.UserInfo;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class XmppPacketFilter implements PacketFilter {

	private Context context;

	public XmppPacketFilter(Context c) {
		context = c;
	}

	@Override
	public boolean accept(Packet packet) {
		
		Logger.i("accept packet ::: " + packet.toXML());
		
		
		if (packet instanceof IwoFriendsInfoIQ) {
			Logger.i("IQ:" + packet.toXML());
			DBhelper helper = new DBhelper(context, IwoSQLiteHelper.FRIEND_TAB);
			IwoFriendsInfoIQ iq = (IwoFriendsInfoIQ) packet;
			List<IwoFriendsInfo> list = iq.getIfis();
			String tab = "tab_" + PreferenceUtil.getString(context, "user_name", "");
			if(list != null)
				helper.delete(tab);
			for (IwoFriendsInfo info : list) {
				Logger.i(info.getName() + "" + info.getNick());
				HashMap<String, String> map = info.toMap();
				map.put("type", "0");
				Logger.i("刷新好友：");
				helper.delete(tab, "name", map.get("name"));
				helper.insert(tab, map);
			}
			Logger.i("iq" + iq.getIfis());
			helper.close();
			context.sendBroadcast(new Intent("com.android.broadcast.receiver.media.refresh.CHAT_REFRESH_SHARE"));
		} else if (packet instanceof Presence) {

			/**
			 * available: 表示处于在线状态 unavailable: 表示处于离线状态 subscribe: 表示发出添加好友的申请
			 * unsubscribe: 表示发出删除好友的申请 unsubscribed: 表示拒绝添加对方为好友 error:
			 * 表示presence信息报中包含了一个错误消息。
			 */

			Presence presence = (Presence) packet;
			final String from = presence.getFrom().split("@")[0].replace(Constants.CHAT_TYPE, "");
			final String to = presence.getTo().split("@")[0].replace(Constants.CHAT_TYPE, "");
			Logger.i("presence----" + presence.getType() + "----" + presence.getFrom() + "---" + from + "---to:" + to);
			Logger.i("presence----" + presence.toXML());
			DBhelper invite_from = new DBhelper(context, IwoSQLiteHelper.INVITE_FROM);
			if (presence.getType().equals(Presence.Type.subscribe)) {// 好友申请
				ArrayList<HashMap<String, String>> list = invite_from.select("invite_from", "tel", from, "type", "1");
				Logger.i("邀请：" + list);
				if (list == null){
					new GetData().execute(from, "3");
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("tel", from);
					map.put("type", "2");
					invite_from.insert("invite_from", map);
				}
			} else if (presence.getType().equals(Presence.Type.subscribed)) {// 同意添加好友
				new Thread(new Runnable() {
					public void run() {
						XmppClient.getInstance(context).friendAddDel(true, to, from);
					}
				}).start();
				ArrayList<HashMap<String, String>> list = invite_from.select("invite_from", "tel", from, "type", "2");
				Logger.i("同意：" + list);
				if (list == null)
					new GetData().execute(from, "4");
			} else if (presence.getType().equals(Presence.Type.unsubscribe)) {// 拒绝添加好友
				invite_from.delete("invite_from", "tel", from);
				new GetData().execute(from, "5");
			}
			invite_from.close();
		} else if(packet instanceof IwoGroupInfoIQ){
			Logger.i("other" + packet.toXML());
			DBhelper group = new DBhelper(context, IwoSQLiteHelper.GROUP);
			DBhelper user = new DBhelper(context, IwoSQLiteHelper.GROUP_MEMBER);
			
			String group_table = "group_" + PreferenceUtil.getString(context, "user_name", "");
			String groupmember_table = "group_member" + PreferenceUtil.getString(context, "user_name", "");
			group.delete(group_table);
			user.delete(groupmember_table);
			IwoGroupInfoIQ infoIQ = (IwoGroupInfoIQ) packet;
			List<GroupInfo> list = infoIQ.getIfis();
			for(int i=0; i< list.size(); i++){
				group.insert(group_table, list.get(i).toMap());
				list.get(i).getUserInfo();
				String id = list.get(i).getId();
				for(UserInfo info : list.get(i).getUserInfo()){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("jid", info.getJid());
					map.put("username", info.getUsername());
					map.put("id", id);
					user.insert(groupmember_table, map);
					//map.put("", "");
				}
			}
			group.close();
			user.close();
			context.sendBroadcast(new Intent("com.android.broadcast.receiver.media.refresh.CHAT_REFRESH_SHARE"));
		}

		return false;
	}

	public void setNotify(String name, String con, String type) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// 2.实例化一个通知，指定图标、概要、时间
		Notification n = new Notification(R.drawable.ic_launcher, name + ":" + con, System.currentTimeMillis());
		// 3.指定通知的标题、内容和intent
		
		Intent in = new Intent(context, ModelActivity.class);
		in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		in.putExtra("name", type);
		PendingIntent pi = PendingIntent.getActivity(context, 10010, in, PendingIntent.FLAG_UPDATE_CURRENT);
		n.setLatestEventInfo(context, name, con, pi);
		// 指定声音
		n.defaults = Notification.DEFAULT_ALL;
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		// 4.发送通知
		nm.notify(10010, n);
	}

	private class GetData extends AsyncTask<String, Void, HashMap<String, String>> {
		private String type = "";

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			type = params[1];
			return DataRequest.getMap_64(AppConfig.GetUrl(context, AppConfig.NEW_USER_FIND), params[0]);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("user_id", result.get("user_name"));
				map.put("user_name", result.get("user_name"));
				map.put("nick_name", result.get("nick_name"));
				map.put("head_img", result.get("head_img"));
				String msg = "";
				map.put("msg_tex", msg);
				map.put("type", "1");
				map.put("timestamp", "" + System.currentTimeMillis() / 1000);
				map.put("sex", result.get("sex"));

				DBhelper friend = new DBhelper(context, IwoSQLiteHelper.FRIEND_TAB);
				String friden_table = "tab_" + PreferenceUtil.getString(context, "user_name", "");

				DBhelper per = new DBhelper(context, IwoSQLiteHelper.MESSAGE_TAB);
				String table = "tab_msg" + PreferenceUtil.getString(context, "user_name", "");
				boolean del = false, isdel = false;
				if ("3".equals(type)) {
					msg = "申请添加你为好友";
					map.put("type", "-1");
				} else if ("4".equals(type)) {
					msg = "同意添加你为好友";
					map.put("name", map.get("user_name"));
					map.put("nick", map.get("nick_name"));
					map.put("avatar", map.get("head_img"));
					friend.delete(friden_table, "name", map.get("user_name"));
					friend.insert(friden_table, map);
				} else if ("5".equals(type)) {
					msg = "拒绝添加你为好友";
					if(friend.select(friden_table, "name", result.get("user_name")) != null)
						isdel = true;
					friend.delete(friden_table, "name", result.get("user_name"));
					if(!StringUtil.isEmpty(ChatActivity.userID) && ChatActivity.userID.equals(result.get("user_name"))){
						ActivityUtil.getInstance().delete("ChatActivity");
					}
					per.delete(table, "user_id", result.get("user_name"));
					per.delete("tab_" + PreferenceUtil.getString(context, "user_id", "") + result.get("user_name"));
					del = true;
				}

				if (!del) {
					map.put("msg_tex", msg);
					per.delete(table, "user_id", result.get("user_name"));
					per.insert(table, map);
				}
				per.close();
				friend.close();
				if(!isdel)
				setNotify(result.get("nick_name"), msg, type);
				context.sendBroadcast(new Intent("com.android.broadcast.receiver.media.refresh.CHAT_REFRESH_SHARE"));
			}
		}
	}
}
