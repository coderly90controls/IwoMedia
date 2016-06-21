package com.android.iwo.media.chat;

import org.jivesoftware.smack.packet.Presence;

public class IwoPresence extends Presence{

	private String time = "";
	public IwoPresence(Type type) {
		super(type);
		// TODO Auto-generated constructor stub
	}
	
	public IwoPresence(Type type, String status, int priority, Mode mode) {
		super(type, status, priority, mode);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return super.toXML();
	}

	@Override
	protected synchronized String getExtensionsXML() {
		String xml = "<presence to=\"video_18612497869@124.207.193.54\" " +
				"from=\"video_18612497868@124.207.193.54\" " +
				"type=\"subscribe\" " +
				"time=\"121212\"></presence>";
		return xml;
	}
}
