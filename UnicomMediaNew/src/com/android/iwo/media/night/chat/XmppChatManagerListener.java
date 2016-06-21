package com.android.iwo.media.night.chat;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;

import android.content.Context;

public class XmppChatManagerListener implements ChatManagerListener {

	private Context mContext;
	public XmppMessageListener mXmppMessageListener;
	public XmppChatManagerListener(Context context){
		mContext = context;
		mXmppMessageListener = new XmppMessageListener(mContext);
	}
	@Override
	public void chatCreated(Chat c, boolean b) {
		c.addMessageListener(mXmppMessageListener);
		System.out.println("asdqweqweasdasdqweqwe");
	}

}
