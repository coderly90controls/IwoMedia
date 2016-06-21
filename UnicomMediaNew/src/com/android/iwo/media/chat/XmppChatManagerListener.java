package com.android.iwo.media.chat;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;

import android.content.Context;

public class XmppChatManagerListener implements ChatManagerListener {

	private Context mContext;
	public XmppChatManagerListener(Context context){
		mContext = context;
		
	}
	@Override
	public void chatCreated(Chat c, boolean b) {
		System.out.println("asdqweqweasdasdqweqwe");
	}

}
