package com.android.iwo.media.chat;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import com.android.iwo.media.chat.GroupInfo.UserInfo;

/**
 * IQ packet used for discovering the user's shared groups and for getting the
 * answer back from the server.
 * <p>
 * 
 * Important note: This functionality is not part of the XMPP spec and it will
 * only work with Wildfire.
 * 
 * @author Gaston Dombiak
 */
public class IwoGroupInfoIQ extends IQ {

	public static final String ELEMENT_NAME = "muc";

	public static final String CHILD_ELEMENT_NAME = "group";

	public static final String NAME_SPACE = "jabber:iq:iwo-group";

	private List<GroupInfo> ifis;

	public String getChildElementXML() {

		StringBuffer sb = new StringBuffer();

		sb.append("<").append(ELEMENT_NAME).append(" xmlns=\"")
				.append(NAME_SPACE).append("\" type=\"7\" msgtype=\"1\">");

		if (ifis != null) {
			sb.append("<date xmlns=\"\">");
			for (GroupInfo ifi : ifis) {
				sb.append("<").append(CHILD_ELEMENT_NAME);
				sb.append(" id=\"").append(ifi.getId()).append("\"");
				sb.append(" name=\"").append(ifi.getName()).append("\"");
				sb.append(" description=\"").append(ifi.getDescription())
						.append("\"");
				sb.append(" maxNumber=\"").append(ifi.getMaxNumber())
						.append("\"");
				sb.append(" avatar=\"").append(ifi.getAvatar()).append("\"");
				sb.append(" area=\"").append(ifi.getArea()).append("\"");
				sb.append(" category=\"").append(ifi.getCategory())
						.append("\"");
				sb.append(" isShield=\"").append(ifi.getIsShield())
						.append("\">");
				for (UserInfo userInfo : ifi.getUserInfo()) {
					sb.append("<user ").append("jid=\"")
							.append(userInfo.getJid() + "\"");
					sb.append(" username=\"").append(
							userInfo.getUsername() + "\"/>");
				}
				sb.append("</group>");
			}
			sb.append("<\\date>");
			System.out.println("群 里面 : " + sb.toString());
		}
		sb.append("</").append(ELEMENT_NAME).append(">");
		System.out.println("群 外面: " + sb.toString());
		return sb.toString();
	}

	/**
	 * Internal Search service Provider.
	 */
	public static class Provider implements IQProvider {

		/**
		 * Provider Constructor.
		 */
		public Provider() {
			super();
		}

		public IQ parseIQ(XmlPullParser parser) throws Exception {
			IwoGroupInfoIQ iq = new IwoGroupInfoIQ();

			boolean done = false;
			GroupInfo ifi = null;
			ArrayList<UserInfo> userInfos = null;
			while (!done) {

				int eventType = parser.next();
				if (eventType == XmlPullParser.START_TAG) {
					System.out.println("群信息：" + parser.getName());
					if (parser.getName().equals(CHILD_ELEMENT_NAME)) {

						ifi = new GroupInfo();
						userInfos = new ArrayList<GroupInfo.UserInfo>();
						ifi.setId(parser.getAttributeValue("", "id"));
						ifi.setName(parser.getAttributeValue("", "name"));
						ifi.setDescription(parser.getAttributeValue("",
								"description"));
						ifi.setArea(parser.getAttributeValue("", "area"));
						ifi.setAvatar(parser.getAttributeValue("", "avatar"));
						ifi.setCategory(parser
								.getAttributeValue("", "category"));
						ifi.setMaxNumber(parser.getAttributeValue("",
								"maxNumber"));
						ifi.setIsShield(parser
								.getAttributeValue("", "isShield"));
					} else if (parser.getName().equals("user")) {
						UserInfo userInfo = new UserInfo();
						userInfo.setJid(parser.getAttributeValue("", "jid"));
						userInfo.setUsername(parser.getAttributeValue("",
								"username"));
						userInfos.add(userInfo);
						System.out.println("user："
								+ parser.getAttributeValue("", "jid"));
					}

				} else if (eventType == XmlPullParser.END_TAG) {

					if (parser.getName().equals(CHILD_ELEMENT_NAME)) {
						if (iq.ifis == null)
							iq.ifis = new ArrayList<GroupInfo>();
						ifi.setUserInfo(userInfos);
						iq.ifis.add(ifi);
					}
					/*
					 * if (parser.getName().equals("user")) { UserInfo userInfo
					 * = new UserInfo();
					 * userInfo.setJid(parser.getAttributeValue("", "jid"));
					 * userInfo.setUsername(parser.getAttributeValue("",
					 * "username")); ifi.setUserInfo(userInfo);
					 * System.out.println("END_TAG：" +
					 * parser.getAttributeValue("", "jid")); }
					 */

					// System.out.println("END_TAG：" + parser.getName());
					if (parser.getName().equals(ELEMENT_NAME)) {
						done = true;
					}
				}
			}

			System.out.println("IWO : " + parser.toString());
			return iq;
		}
	}

	public List<GroupInfo> getIfis() {
		return ifis;
	}

	public void setIfis(List<GroupInfo> ifis) {
		this.ifis = ifis;
	}

}
