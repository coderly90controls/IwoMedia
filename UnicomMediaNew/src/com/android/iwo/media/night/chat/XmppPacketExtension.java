package com.android.iwo.media.night.chat;

import org.jivesoftware.smack.packet.PacketExtension;

import com.test.iwomag.android.pubblico.util.StringUtil;

public class XmppPacketExtension implements PacketExtension {
	/**
	 * 消息时间戳
	 */
	private String timestamp;

	/**
	 * 消息类型
	 */
	private String category;
	
	/**
	 * 图片和语音加密流
	 */
	private String richbody;
	
	/**
	 * 发消息人的头像
	 */
	private String head;
	/**
	 * 发消息人的昵称
	 */
	private String nick;
	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	/**
	 * 音频时长
	 */
	private String duartion;
	
	public String getDuartion() {
		return duartion;
	}

	public void setDuartion(String duartion) {
		this.duartion = duartion;
	}

	public static final String NAME = "custom";
	public static final String NAME_SPACE = "com:roger:other";

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getRichbody() {
		return richbody;
	}

	public void setRichbody(String richbody) {
		if(StringUtil.isEmpty(richbody))
			this.richbody = "";
		else 
			this.richbody = richbody;
	}

	@Override
	public String getElementName() {
		return NAME;
	}

	@Override
	public String getNamespace() {
		return NAME_SPACE;
	}

	@Override
	public String toXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<").append(NAME).append(" xmlns=\"").append(NAME_SPACE)
				.append("\">");
		sb.append("<timestamp>").append(timestamp).append("</timestamp>");
		sb.append("<category>").append(category).append("</category>");
		sb.append("<richbody>").append(richbody).append("</richbody>");
		sb.append("<duartion>").append(duartion).append("</duartion>");
		sb.append("<head>").append(head).append("</head>");
		sb.append("<nick>").append(nick).append("</nick>");
		sb.append("<clienttype>").append("android").append("</clienttype>");
		sb.append("</").append(NAME).append(">");
		return sb.toString();
	}

	public XmppPacketExtension() {
		super();
	}
}
