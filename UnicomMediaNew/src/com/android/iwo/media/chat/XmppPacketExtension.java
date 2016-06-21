package com.android.iwo.media.chat;

import org.jivesoftware.smack.packet.PacketExtension;

import com.test.iwomag.android.pubblico.util.StringUtil;

public class XmppPacketExtension implements PacketExtension {
	/**
	 * 消息时间戳
	 */
	private String timestamp = "0";

	/**
	 * 消息类型
	 */
	private String category = "0";

	/**
	 * 图片和语音加密流
	 */
	private String richbody = "0";

	/**
	 * 音频时长
	 */
	private String duartion = "0";

	public String getDuartion() {
		if (StringUtil.isEmpty(duartion))
			return "0";
		return duartion;
	}

	public void setDuartion(String duartion) {
		this.duartion = duartion;
	}

	public static final String NAME = "custom";
	public static final String NAME_SPACE = "com:roger:other";

	public String getTimestamp() {
		if (StringUtil.isEmpty(timestamp))
			return "0";
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getCategory() {
		if (StringUtil.isEmpty(category))
			return "0";
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getRichbody() {
		if (StringUtil.isEmpty(richbody))
			return "0";
		return richbody;
	}

	public void setRichbody(String richbody) {
		if (StringUtil.isEmpty(richbody))
			this.richbody = "0";
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
		sb.append("<").append(NAME).append(" xmlns=\"").append(NAME_SPACE).append("\">");
		sb.append("<timestamp>").append(getTimestamp()).append("</timestamp>");
		sb.append("<category>").append(getCategory()).append("</category>");
		sb.append("<richbody>").append(getRichbody()).append("</richbody>");
		sb.append("<duartion>").append(getDuartion()).append("</duartion>");
		sb.append("<clienttype>").append("android").append("</clienttype>");
		sb.append("</").append(NAME).append(">");
		return sb.toString();
	}

	public XmppPacketExtension() {
		super();
	}
}
