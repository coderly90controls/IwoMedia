package com.android.iwo.media.chat;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

import com.test.iwomag.android.pubblico.util.Logger;

public class XmppPacketListener implements PacketListener {

	@Override
	public void processPacket(Packet packet) {
		Logger.i("-------XmppPacketListener..." + packet.toXML());
	}
}
