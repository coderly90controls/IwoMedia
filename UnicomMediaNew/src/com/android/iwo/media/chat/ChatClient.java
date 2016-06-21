package com.android.iwo.media.chat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.OfflineMessageManager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.android.iwo.media.action.ConnectionSevice;
import com.android.iwo.media.action.Constants;
import com.android.iwo.media.action.DBhelper;
import com.android.iwo.media.action.IwoSQLiteHelper;
import com.android.iwo.media.activity.ChatActivity;
import com.android.iwo.media.lenovo.R;
import com.test.iwomag.android.pubblico.util.FileCache;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class ChatClient {

	public XMPPConnection connection;

	private OfflineMessageManager offlineManager;
	private static ChatClient client = null;
	public Chat c = null;
	private ChatManager manager;
	public XmppChatManagerListener xmppChatManagerListener;
	private static Context mContext;
	private static String ip = "0";
	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(android.os.Message msg) {
			if (successListener != null)
				successListener.success(true);
			successListener = null;// 执行完连接成功后置空
			return false;
		}
	});

	public void connect() {
		new Thread(new Runnable() {
			public void run() {
				String post = FileCache.getInstance().readStringFormSD(Constants.GET_APP_CHAT_PORT);
				ConnectionConfiguration config = new ConnectionConfiguration(ip, Integer.valueOf(post));

				/** 是否启用安全验证 */
				config.setCompressionEnabled(false);
				config.setSelfSignedCertificateEnabled(false);
				config.setSASLAuthenticationEnabled(false);
				config.setVerifyChainEnabled(false);

				// 允许登陆成功后更新在线状态
				config.setSendPresence(true);

				// 收到好友邀请后manual表示需要经过同意,accept_all表示不经同意自动为好友
				// Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);

				/** 创建connection链接 */
				try {
					ProviderManager pm = ProviderManager.getInstance();

					pm.addIQProvider(IwoFriendsInfoIQ.ELEMENT_NAME, IwoFriendsInfoIQ.NAME_SPACE, new IwoFriendsInfoIQ.Provider());
					connection = new XMPPConnection(config);
					/** 建立连接 */
					connection.connect();
					manager = connection.getChatManager();
					xmppChatManagerListener = new XmppChatManagerListener(mContext);
					manager.addChatListener(xmppChatManagerListener);
					connection.addConnectionListener(new XmppConnectionListener(mContext));
					connection.addPacketListener(new XmppPacketListener(), new XmppPacketFilter(mContext));
					// 收到好友邀请后manual表示需要经过同意,accept_all表示不经同意自动为好友
					connection.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual);

					mHandler.sendMessage(new android.os.Message());
					stopService();
					offlineManager = new OfflineMessageManager(connection);
				} catch (Exception e) {
					Logger.e("连接" + e.toString());
				}
			}
		}).start();
	}

	public static ChatClient getInstance(Context context) {
		ip = FileCache.getInstance().readStringFormSD(Constants.GET_APP_CHAT_IP);
		if (client == null)
			client = new ChatClient();
		mContext = context;
		return client;
	}

	private boolean isConnect() {
		if (connection == null || !connection.isConnected())
			return false;
		return true;
	}

	/**
	 * 登录
	 * 
	 * @param username
	 *            登录帐号
	 * @param password
	 *            登录密码
	 */
	public void login(final String username, final String password) {
		if (!isConnect()) {
			setConnectSuccess(new ConnectSuccessListener() {
				public void success(boolean success) {
					login_(username, password);
				}
			});
			connect();
		} else {
			login_(username, password);
		}
	}

	private void login_(final String username, final String password) {
		new Thread(new Runnable() {
			public void run() {
				try {
					Logger.i("name:" + username + "--pass" + password);
					connection.login(Constants.CHAT_TYPE + username, password);
					setPresence(0);
					updateFriend(username);
				} catch (Exception e) {
					Logger.e("登录错误：" + e.toString());
				}
			}
		}).start();
	}

	/**
	 * 停止重连服务
	 */
	private void stopService() {
		Intent intent = new Intent(mContext, ConnectionSevice.class);
		PendingIntent p_intent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
		AlarmManager am = (AlarmManager) mContext.getSystemService(Service.ALARM_SERVICE);
		am.cancel(p_intent);
	}

	/**
	 * 更改用户状态
	 */
	public void setPresence(final int code) {
		if (!isConnect()) {
			setConnectSuccess(new ConnectSuccessListener() {
				public void success(boolean success) {
					setPresence_(code);
				}
			});
			connect();
		} else {
			setPresence_(code);
		}
	}

	private void setPresence_(int code) {
		if (connection == null)
			return;
		Presence presence;
		switch (code) {
		case 0:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.available);
			connection.sendPacket(presence);
			Logger.i("state设置在线");
			break;
		case 1:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.chat);
			connection.sendPacket(presence);
			Logger.i("state设置Q我吧");
			Logger.i(presence.toXML());
			break;
		case 2:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.dnd);
			connection.sendPacket(presence);
			Logger.i("state设置忙碌");
			Logger.i(presence.toXML());
			break;
		case 3:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.away);
			connection.sendPacket(presence);
			Logger.i("state设置离开");
			Logger.i(presence.toXML());
			break;
		case 4:
			Roster roster = connection.getRoster();
			Collection<RosterEntry> entries = roster.getEntries();
			for (RosterEntry entry : entries) {
				presence = new Presence(Presence.Type.unavailable);
				presence.setPacketID(Packet.ID_NOT_AVAILABLE);
				presence.setFrom(connection.getUser());
				presence.setTo(entry.getUser());
				connection.sendPacket(presence);
				Logger.i(presence.toXML());
			}
			// 向同一用户的其他客户端发送隐身状态
			presence = new Presence(Presence.Type.unavailable);
			presence.setPacketID(Packet.ID_NOT_AVAILABLE);
			presence.setFrom(connection.getUser());
			presence.setTo(StringUtils.parseBareAddress(connection.getUser()));
			connection.sendPacket(presence);
			Logger.i("state设置隐身");
			break;
		case 5:
			presence = new Presence(Presence.Type.unavailable);
			connection.sendPacket(presence);
			Logger.i("state设置离线");
			break;
		default:
			break;
		}
	}

	/**
	 * 判断是否登录
	 */
	public boolean isLogin() {
		if (!isConnect()) {
			return false;
		}
		if (StringUtil.isEmpty(connection.getUser())) {
			return false;
		}
		return true;
	}

	/**
	 * 消息发送
	 */

	public void sendMessage(final String to, final String body, final XmppPacketExtension xmp) {

		if (!isConnect()) {
			setConnectSuccess(new ConnectSuccessListener() {
				public void success(boolean success) {
					sendMessage_(to, body, xmp);
				}
			});
			connect();
		} else {
			if (StringUtil.isEmpty(to) || StringUtil.isEmpty(connection.getUser()))
				return;
			sendMessage_(to, body, xmp);
		}
	}

	private void sendMessage_(String to, String body, XmppPacketExtension xmp) {
		Logger.i(to + "getUser=" + connection.getUser());
		if (c == null)
			c = connection.getChatManager().createChat(Constants.CHAT_TYPE + to + "@" + ip, null);
		try {
			Message message2 = new Message();
			message2.setBody(body);
			if (xmp != null)
				message2.addExtension(xmp);
			c.sendMessage(message2);
			recOffMessage();
			Logger.i("发送消息：" + message2.toXML() + "end----");
		} catch (Exception e) {
			Logger.e("消息发送错误" + e.toString());
		}
	}

	/**
	 * 离线消息接收
	 * 
	 * @return
	 */
	public void recOffMessage() {
		try {
			Iterator<Message> it = offlineManager.getMessages();
			Logger.i("离线消息接收：" + it);
			while (it.hasNext()) {
				Message msg = it.next();
				Logger.i("离线消息接收：" + msg.getBody());
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	public void colseConn() {
		if (connection != null) {
			setPresence(5);
			connection.disconnect();
		}
	}

	/**
	 * 添加好友
	 * 
	 * @param name
	 * @return
	 */
	public void addFriend(final String name, final String[] group) {
		if (!isConnect()) {
			setConnectSuccess(new ConnectSuccessListener() {
				public void success(boolean success) {
					addFriend_(name, group);
				}
			});
			connect();
		} else {			
			addFriend_(name, group);
		}
	}
	
	private void addFriend_(String name, String[] group) {

		Roster roster = null;
		if (connection != null && connection.isConnected())
			roster = connection.getRoster();
		DBhelper per = new DBhelper(mContext, IwoSQLiteHelper.INVITE_FROM);
		try {
			roster.createEntry(Constants.CHAT_TYPE + name + "@" + ip, Constants.CHAT_TYPE + name, group);

			if (per.select("invite_from", "tel", name) == null) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("tel", name);
				map.put("type", "1");
				per.insert("invite_from", map);
			}

			per.close();
		} catch (Exception e) {
			Logger.e("添加好友失败" + e.toString());
		}
	}

	/**
	 * 删除好友
	 * 
	 * @param userName
	 * @return
	 */
	
	public void removeUser(final String userName) {
		if (!isConnect()) {
			setConnectSuccess(new ConnectSuccessListener() {
				public void success(boolean success) {
					removeUser_(userName);
				}
			});
			connect();
		} else {			
			removeUser_(userName);
		}
	}
	private void removeUser_(String userName) {
		Roster roster = null;
		if (connection != null && connection.isConnected())
			roster = connection.getRoster();
		try {
			if (userName.contains("@")) {
				userName = userName.split("@")[0];
			}
			userName = Constants.CHAT_TYPE + userName.replace(Constants.CHAT_TYPE, "") + "@" + ip;
			RosterEntry entry = roster.getEntry(userName);
			Logger.i("删除好友：" + userName);
			Logger.i("User." + (roster.getEntry(userName) == null));
			roster.removeEntry(entry);
		} catch (Exception e) {
			Logger.e("删除好友失败" + e.toString());
		}
	}

	/**
	 * 同意或者拒绝好友申请
	 * 
	 * @param ok
	 *            true 同意, false 拒绝
	 * @param from
	 * @param to
	 */
	
	public void friendAddDel(final boolean ok, final String from, final String to){
		if (!isConnect()) {
			setConnectSuccess(new ConnectSuccessListener() {
				public void success(boolean success) {
					friendAddDel_(ok, from, to);
				}
			});
			connect();
		} else {			
			friendAddDel_(ok, from, to);
		}
	}
	private void friendAddDel_(boolean ok, String from, String to) {
		Presence presence = null;
		if (ok) {
			Logger.i("同意好友邀请" + from + "-- to -- " + to);
			presence = new Presence(Presence.Type.subscribed);// 同意
		} else {
			Logger.i("拒绝好友邀请" + from + "-- to -- " + to);
			presence = new Presence(Presence.Type.unsubscribe);// 拒绝
		}
		// subscribed 拒绝是unsubscribe
		presence.setTo(Constants.CHAT_TYPE + to + "@" + ip);// 接收方jid
		presence.setFrom(Constants.CHAT_TYPE + from + "@" + ip);// 发送方jid
		if (connection != null && connection.isConnected()) {
			Logger.i("同意活拒绝的协议" + presence.toXML());
			connection.sendPacket(presence);// connection是你自己的XMPPConnection链接
		}
	}

	/**
	 * 刷新好友列表
	 */
	public void updateFriend(final String name) {
		if (!isConnect()) {
			setConnectSuccess(new ConnectSuccessListener() {
				public void success(boolean success) {
					updateFriend_(name);
				}
			});
			connect();
		} else {			
			updateFriend_(name);
		}
	}
	private void updateFriend_(String name) {
		Logger.i("-----" + Constants.CHAT_TYPE + name + "@" + ip);
		IwoFriendsInfoIQ igi = new IwoFriendsInfoIQ();
		igi.setType(Type.GET);
		Logger.i("刷新好友列表" + igi.toXML());
		connection.sendPacket(igi);
	}

	public void setNotify(String name, String con) {
		NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		// 2.实例化一个通知，指定图标、概要、时间
		Notification n = new Notification(R.drawable.ic_launcher, name + ":" + con, System.currentTimeMillis());
		// 3.指定通知的标题、内容和intent
		Intent in = new Intent(mContext, ChatActivity.class);
		PendingIntent pi = PendingIntent.getActivity(mContext, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
		n.setLatestEventInfo(mContext, name, con, pi);
		// 指定声音
		n.defaults = Notification.DEFAULT_ALL;
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		// 4.发送通知
		nm.notify(0, n);
	}

	/**
	 * 连接成功后回调的方法
	 */
	public ConnectSuccessListener successListener;

	public void setConnectSuccess(ConnectSuccessListener s) {
		if (s != null)
			successListener = s;
	}

	public interface ConnectSuccessListener {
		public void success(boolean success);
	}
}
