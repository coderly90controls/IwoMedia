package com.android.iwo.media.night.chat;

import java.util.Collection;
import java.util.Iterator;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.packet.VCard;

import android.content.Context;

import com.android.iwo.media.action.Constants;
import com.android.iwo.media.chat.IwoFriendsInfoIQ;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class XmppNightClient {

	public XMPPConnection connection;

	private OfflineMessageManager offlineManager;
	private static XmppNightClient client = null;
	public Chat c = null;
	private ChatManager manager;
	public XmppChatManagerListener xmppChatManagerListener;

	/**
	 * 构造方法 连接服务器
	 * 
	 */
	public XmppNightClient() {
	}

	public void connect(Context context) {
		ConnectionConfiguration config = new ConnectionConfiguration("", 1);

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
			// pm.addIQProvider(IwoGroupsInfo.ELEMENT_NAME,IwoGroupsInfo.NAME_SPACE,new
			// IwoGroupsInfo.Provider());
			pm.addIQProvider(IwoFriendsInfoIQ.ELEMENT_NAME, IwoFriendsInfoIQ.NAME_SPACE, new IwoFriendsInfoIQ.Provider());
			connection = new XMPPConnection(config);
			/** 建立连接 */
			connection.connect();
			manager = connection.getChatManager();
			xmppChatManagerListener = new XmppChatManagerListener(context);
			manager.addChatListener(xmppChatManagerListener);
			
			// 收到好友邀请后manual表示需要经过同意,accept_all表示不经同意自动为好友
			connection.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual);

		} catch (Exception e) {
			Logger.e("连接" + e.toString());
		}
		if (connection != null && !connection.isConnected())
			connection = null;
		offlineManager = new OfflineMessageManager(connection);
	}

	public static XmppNightClient getInstance(Context context) {
		if (client == null)
			client = new XmppNightClient();
		if (client.connection == null || !client.connection.isConnected())
			client.connect(context);
		return client;
	}

	/**
	 * 登录
	 * 
	 * @param username
	 *            登录帐号
	 * @param password
	 *            登录密码
	 * @return
	 */
	public boolean login(Context context, final String username) {
		final String password = PreferenceUtil.getString(context, "of_pass", "");
		if(StringUtil.isEmpty(password)) return false;
		new Thread(new Runnable() {
			public void run() {
				try {
					/** 登录 */
					if (connection != null) {
						Logger.e("黑夜：name:" + username + "--pass" + password);
						connection.login(Constants.CHAT_TYPE + username, password);
						setPresence(0);
						// friendAction();
					}
				} catch (Exception e) {
					Logger.e("登录错误：" + e.toString() + username);
				}
			}
		}).start();

		return false;
	}

	/**
	 * 修改密码
	 * 
	 * @param connection
	 * @return
	 */
	public static boolean changePassword(XMPPConnection connection, String pwd) {
		try {
			connection.getAccountManager().changePassword(pwd);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 更改用户状态
	 */
	public void setPresence(int code) {
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
	 * 删除当前用户
	 * 
	 * @param connection
	 * @return
	 */
	public boolean deleteAccount() {
		if (connection == null || !connection.isConnected())
			return false;
		try {
			connection.getAccountManager().deleteAccount();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断是否登录
	 * 
	 * @return
	 */
	public boolean isLogin() {
		if (connection == null || !connection.isConnected()) {
			return false;
		}
		Logger.i("-----------getUser=" + connection.getUser());
		if (StringUtil.isEmpty(connection.getUser())) {
			return false;
		}
		return true;
	}

	/**
	 * 消息发送
	 * 
	 * @return
	 */
	public boolean sendMessage(String to, String body, XmppPacketExtension xmp) {
		Logger.i(to + "getUser=" + connection.getUser());
		if (StringUtil.isEmpty(to) || StringUtil.isEmpty(connection.getUser()))
			return false;
		if (connection == null || !connection.isConnected()) {
			return false;
		}

		if (c == null)
			c = connection.getChatManager().createChat(Constants.CHAT_TYPE + to + "@" + "", null);
		// connection.getChatManager().getThreadChat(c.getThreadID());c.getParticipant();
		try {
			Message message2 = new Message();
			message2.setBody(body);
			if (xmp != null)
				message2.addExtension(xmp);
			c.sendMessage(message2);
			recOffMessage();
			Logger.i("发送消息：" + message2.toXML() + "end----");

			return true;
		} catch (Exception e) {
			Logger.e("消息发送错误" + e.toString());
		}
		return false;
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

	/**
	 * 返回所有组信息 <RosterGroup>
	 * 
	 * @return List(RosterGroup)
	 */
	public static Collection<RosterGroup> getGroups(Roster roster) {
		return roster.getGroups();
	}

	/**
	 * 返回相应(groupName)组里的所有用户<RosterEntry>
	 * 
	 * @return List(RosterEntry)
	 */
	public static Collection<RosterEntry> getEntriesByGroup(Roster roster, String groupName) {
		RosterGroup rosterGroup = roster.getGroup(groupName);
		return rosterGroup.getEntries();
	}

	/**
	 * 返回所有用户信息 <RosterEntry>
	 * 
	 * @return List(RosterEntry)
	 */
	public static Collection<RosterEntry> getAllEntries(Roster roster) {
		return roster.getEntries();
	}

	/**
	 * 获取用户的vcard信息
	 * 
	 * @param connection
	 * @param user
	 * @return
	 * @throws XMPPException
	 */
	public VCard getUserVCard(String user) throws XMPPException {
		VCard vcard = new VCard();
		if (connection != null && connection.isConnected()) {
			vcard.load(connection, user);
		}
		return vcard;
	}

	public void colseConn() {
		if (connection != null) {
			setPresence(5);
			connection.disconnect();
		}
	}
}
