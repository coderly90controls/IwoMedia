package com.android.iwo.media.chat;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import com.test.iwomag.android.pubblico.util.Logger;

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
public class IwoFriendsInfoIQ extends IQ {

	public static final String ELEMENT_NAME = "muc";

	public static final String CHILD_ELEMENT_NAME = "item";

	public static final String NAME_SPACE = "jabber:iq:iwo-friends-info";

	private List<IwoFriendsInfo> ifis;

	public String getChildElementXML() {

		StringBuffer sb = new StringBuffer();

		sb.append("<").append(ELEMENT_NAME).append(" xmlns=\"").append(NAME_SPACE).append("\">");

		if (ifis != null){
			for (IwoFriendsInfo ifi : ifis) {
				sb.append("<").append(CHILD_ELEMENT_NAME);
				sb.append(" jid=\"").append(ifi.getJid()).append("\"");
				sb.append(" name=\"").append(ifi.getName()).append("\"");
				sb.append(" subscription=\"").append(ifi.getSubscription()).append("\"");
				sb.append(" avatar=\"").append(ifi.getAvatar()).append("\"");
				sb.append(" age=\"").append(ifi.getAge()).append("\"");
				sb.append(" sex=\"").append(ifi.getSex()).append("\"");
				sb.append(" signature=\"").append(ifi.getSignature()).append("\"");
				sb.append(" videoImg=\"").append(ifi.getVideoImg()).append("\"");
				sb.append(" notename=\"").append(ifi.getNotename()).append("\"");
				sb.append(" nick=\"").append(ifi.getNick()).append("\"/>");
			}
			//Logger.i("IwoFriendsInfoIQ 里面 : " + sb.toString());
		}
		sb.append("</").append(ELEMENT_NAME).append(">");
		//Logger.i("IwoFriendsInfoIQ 外面: " + sb.toString());
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
			IwoFriendsInfoIQ iq = new IwoFriendsInfoIQ();

			boolean done = false;
			IwoFriendsInfo ifi = null;
			while (!done) {

				int eventType = parser.next();
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("item")) {

						ifi = new IwoFriendsInfo();
						ifi.setJid(parser.getAttributeValue("", "jid"));
						ifi.setName(parser.getAttributeValue("", "name"));
						ifi.setSubscription(parser.getAttributeValue("", "subscription"));
						ifi.setNick(parser.getAttributeValue("", "nick"));
						ifi.setAvatar(parser.getAttributeValue("", "avatar"));
						ifi.setAge(parser.getAttributeValue("", "age"));
						ifi.setSex(parser.getAttributeValue("", "sex"));
						ifi.setSignature(parser.getAttributeValue("", "signature"));
						ifi.setVideoImg(parser.getAttributeValue("", "videoImg"));
						ifi.setNotename(parser.getAttributeValue("", "notename"));
					}

				} else if (eventType == XmlPullParser.END_TAG) {

					if (parser.getName().equals(CHILD_ELEMENT_NAME)) {
						if (iq.ifis == null)
							iq.ifis = new ArrayList<IwoFriendsInfo>();

						iq.ifis.add(ifi);
					}

					if (parser.getName().equals(ELEMENT_NAME)) {
						done = true;
					}
				}
			}

			//Logger.i("IWO : " + parser.toString());
			return iq;
		}
	}

	public List<IwoFriendsInfo> getIfis() {
		return ifis;
	}

	public void setIfis(List<IwoFriendsInfo> ifis) {
		this.ifis = ifis;
	}

}
