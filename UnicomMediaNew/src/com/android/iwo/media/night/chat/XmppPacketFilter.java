package com.android.iwo.media.night.chat;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;

public class XmppPacketFilter implements PacketFilter {

	@Override
	public boolean accept(Packet packet) {
		return true;
	}
}
