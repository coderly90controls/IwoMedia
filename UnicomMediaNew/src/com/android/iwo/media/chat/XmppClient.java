package com.android.iwo.media.chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.packet.VCard;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.android.iwo.media.action.ConnectionSevice;
import com.android.iwo.media.action.Constants;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class XmppClient {

	public XMPPConnection connection;

	private OfflineMessageManager offlineManager;
	private static XmppClient client = null;
	public Chat c = null;
	private ChatManager manager;
	public XmppChatManagerListener xmppChatManagerListener;
	private static Context mContext;
	private static String ip = "0";

	/**
	 * 构造方法 连接服务器
	 * 
	 */
	public XmppClient() {
	}

	public void connect(Context context) {
		new Thread(new Runnable() {
			public void run() {

				String post = PreferenceUtil.getString(mContext,
						Constants.GET_APP_CHAT_PORT, "18522");
				Logger.i("ip : " + ip + "-- post -- " + post);
				ConnectionConfiguration config = new ConnectionConfiguration(
						ip, Integer.valueOf(post));

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
					pm.addIQProvider(IwoGroupInfoIQ.ELEMENT_NAME,
							IwoGroupInfoIQ.NAME_SPACE,
							new IwoGroupInfoIQ.Provider());
					pm.addIQProvider(IwoFriendsInfoIQ.ELEMENT_NAME,
							IwoFriendsInfoIQ.NAME_SPACE,
							new IwoFriendsInfoIQ.Provider());
					connection = new XMPPConnection(config);
					/** 建立连接 */
					connection.connect();
					manager = connection.getChatManager();
					xmppChatManagerListener = new XmppChatManagerListener(
							mContext);
					manager.addChatListener(xmppChatManagerListener);
					connection
							.addConnectionListener(new XmppConnectionListener(
									mContext));
					/*
					 * try {
					 * Class.forName("org.jivesoftware.smack.ReconnectionManager"
					 * ); } catch (Exception e1) { e1.printStackTrace(); }
					 */
				
					// 收到好友邀请后manual表示需要经过同意,accept_all表示不经同意自动为好友
					connection.getRoster().setSubscriptionMode(
							Roster.SubscriptionMode.manual);
					login(PreferenceUtil.getString(mContext, "user_name", ""));
				} catch (Exception e) {
					Logger.e("连接" + e.toString());
				}
				if (connection != null && !connection.isConnected())
					connection = null;
				offlineManager = new OfflineMessageManager(connection);
			}
		}).start();
	}

	public static XmppClient getInstance(Context context) {
		ip = PreferenceUtil.getString(context, Constants.GET_APP_CHAT_IP,
				"114.247.0.79");
		if (client == null)
			client = new XmppClient();
		mContext = context;
		if (client.connection == null || !client.connection.isConnected()) {
			try {
				client.connect(context);
			} catch (Exception e) {
				Logger.e("连接失败：" + e.toString());
			}
		}

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
	public boolean login(final String username) {
		final String password = PreferenceUtil.getString(mContext, "of_pass",
				"");
		if (StringUtil.isEmpty(password))
			return false;
		new Thread(new Runnable() {
			public void run() {
				try {
					/** 登录 */
					if (connection != null && connection.isConnected()) {
						Logger.i("name:" + username + "--pass" + password);
						connection.login(Constants.CHAT_TYPE + username,
								password);
						// recOffMessage();
						// setPresence(0);
						// / friendAction();
					}
				} catch (Exception e) {
					Logger.e("登录错误：" + e.toString());
				}
				updateFriend(username);
				// getQunMember(username);
				stopService();
			}
		}).start();

		return false;
	}

	private void stopService() {
		Intent intent = new Intent(mContext, ConnectionSevice.class);
		PendingIntent p_intent = PendingIntent.getBroadcast(mContext, 0,
				intent, 0);
		AlarmManager am = (AlarmManager) mContext
				.getSystemService(Service.ALARM_SERVICE);
		am.cancel(p_intent);
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
		if (connection == null || connection.isConnected())
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
		if (connection == null || !connection.isConnected()) {
			return false;
		}
		if (StringUtil.isEmpty(to) || StringUtil.isEmpty(connection.getUser()))
			return false;
		Logger.i(to + "getUser=" + connection.getUser());
		if (c == null)
			c = connection.getChatManager().createChat(
					Constants.CHAT_TYPE + to + "@" + ip, null);
		// connection.getChatManager().getThreadChat(c.getThreadID());c.getParticipant();
		try {
			Message message2 = new Message();
			message2.setBody(body);
			if (xmp != null)
				message2.addExtension(xmp);
			c.sendMessage(message2);
			// recOffMessage();
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
	public static Collection<RosterEntry> getEntriesByGroup(Roster roster,
			String groupName) {
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

	/**
	 * 获取用户头像信息
	 * 
	 * public static ImageIcon getUserImage(XMPPConnection connection, String
	 * user) { ImageIcon ic = null; try { Logger.i("获取用户头像信息: "+user); VCard
	 * vcard = new VCard(); vcard.load(connection, user);
	 * 
	 * if(vcard == null || vcard.getAvatar() == null) { return null; }
	 * ByteArrayInputStream bais = new ByteArrayInputStream( vcard.getAvatar());
	 * Image image = ImageIO.read(bais);
	 * 
	 * 
	 * ic = new ImageIcon(image);
	 * Logger.i("图片大小:"+ic.getIconHeight()+" "+ic.getIconWidth());
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } return ic; }
	 */

	public void colseConn() {
		if (connection != null) {
			// setPresence(5);
			connection.disconnect();
			connection = null;
		}
	}

	public boolean removeUser(String userName) {
		Roster roster = null;
		if (connection != null && connection.isConnected())
			roster = connection.getRoster();
		try {
			if (userName.contains("@")) {
				userName = userName.split("@")[0];
			}
			userName = Constants.CHAT_TYPE
					+ userName.replace(Constants.CHAT_TYPE, "") + "@" + ip;
			RosterEntry entry = roster.getEntry(userName);
			Logger.i("删除好友：" + userName);
			Logger.i("User." + (roster.getEntry(userName) == null));
			roster.removeEntry(entry);
			return true;
		} catch (Exception e) {
			Logger.e("删除好友失败" + e.toString());
			return false;
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
	public boolean friendAddDel(boolean ok, String from, String to) {
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
			return true;
		} else {
			return false;// wireshark
		}
	}

	/**
	 * 刷新好友列表
	 */
	public void updateFriend(String name) {
		Logger.i("-----" + Constants.CHAT_TYPE + name + "@" + ip);
		IwoFriendsInfoIQ igi = new IwoFriendsInfoIQ();
		igi.setType(Type.GET);
		Logger.i("刷新好友列表" + igi.toXML());
		if (connection != null && connection.isConnected()) {
			connection.sendPacket(igi);
		}
	}

	/**
	 * 建群
	 * 
	 * @return
	 */
	public boolean joinXml(final GroupInfo groupInfo,
			final ArrayList<UserInfo> users) {
		IQ iq = new IQ() {
			public String getChildElementXML() {
				StringBuilder buf = new StringBuilder();

				buf.append("<muc xmlns=\"jabber:iq:iwo-group\" type=\"1\">");
				buf.append("<name>" + groupInfo.getName() + "</name>");
				buf.append("<description>" + groupInfo.getDescription()
						+ "</description>");
				buf.append("<maxNumber>" + groupInfo.getMaxNumber()
						+ "</maxNumber>");
				buf.append("<avatar>" + groupInfo.getAvatar() + "</avatar>");
				buf.append("<city>" + groupInfo.getCity() + "</city>");
				buf.append("<area>" + groupInfo.getArea() + "</area>");
				buf.append("<category>" + groupInfo.getCategory()
						+ "</category>");

				for (UserInfo user : users) {
					buf.append("<item>");
					buf.append("<user jid=\"" + user.getJid() + "@" + ip);
					buf.append("\" name=\"" + user.getName());
					buf.append("\" subscription=\"" + user.getSubscription());
					buf.append("\" nick=\"" + user.getNick());
					buf.append("\" avatar=\"" + user.getAvatar());
					buf.append("\" age=\"" + user.getAge());
					buf.append("\" sex=\"" + user.getSex());
					buf.append("\" signature=\"" + user.getSignature() + "\"/>");
					buf.append("</item>");
				}
				buf.append("</muc>");

				return buf.toString();
			}
		};
		iq.setType(IQ.Type.SET);
		//
		// 方法如名，这里是设置这份报文来至那个JID,后边的/smack是这段信息来至哪个端，如spark端就是/spark，android就是/Smack
		// String name = PreferenceUtil.getString(mContext, "user_name", "");
		// iq.setFrom(Constants.CHAT_TYPE + name + "@" + ip);
		Logger.i("建群：" + iq.toXML());
		if (connection != null && connection.isConnected()) {
			connection.sendPacket(iq);
			return true;
		}

		return false;
	}

	/**
	 * 获取群成员
	 */
	public boolean getQunMember(String name) {
		IwoGroupInfoIQ iq = new IwoGroupInfoIQ();
		iq.setType(IQ.Type.SET);
		iq.setFrom(Constants.CHAT_TYPE + name + "@" + ip);

		Logger.i("群成员：" + iq.toXML());
		if (connection != null && connection.isConnected()) {
			connection.sendPacket(iq);
			return true;
		}
		return false;
	}
}
