package com.android.iwo.media.night.chat;

import java.util.HashMap;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.iwo.media.action.Constants;
import com.android.iwo.media.action.DBhelper;
import com.android.iwo.media.action.FaceAction;
import com.android.iwo.media.action.IwoSQLiteHelper;
import com.android.iwo.media.action.MessageAction;
import com.android.iwo.media.activity.NightChat;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;

public class XmppMessageListener implements MessageListener {
	
	private Context mContext;
	private XmppGetMessage mXmppGetMessage;
	public XmppMessageListener(Context context){
		mContext = context;
	}
	@Override
	public void processMessage(Chat c, Message msg) {
		System.out.println("收到消息：" + msg.getBody());
		String x = msg.toXML();
		if(x.contains("type=\"error\">")) return;
		Bundle bundle = new Bundle();
		bundle.putString("body", msg.getBody());
		bundle.putString("from", msg.getFrom());
		bundle.putString("user_name", msg.getFrom().replace(Constants.CHAT_TYPE, "").split("@")[0]);
		bundle.putString("userID", msg.getFrom().replace(Constants.CHAT_TYPE, "").split("@")[0]);
		bundle.putString("head_img", "");
		MessageAction action = new MessageAction();
		action.getMessage(x);
		bundle.putString("timestamp", action.timestamp);
		bundle.putString("category", action.category);
		if("2".equals(action.category) || "3".equals(action.category)){
			bundle.putString("richbody", FaceAction.GenerateImage(action.richbody, action.category));
		}else {
			bundle.putString("richbody", action.richbody);
		}
		bundle.putString("duartion", action.duartion);
		bundle.putString("user_name", action.nick); 
		Logger.i("--"+NightChat.isIN + "--" + msg.getTo() + "---" + NightChat.userID + "--" +msg.getFrom());
		if(!NightChat.isIN || !msg.getFrom().contains(NightChat.userID)){
			HashMap<String, String> map = inster(bundle);
			String head_img = "" , nick_name = "";
			head_img = action.head;
			nick_name = action.nick;
			map.put("head_img", head_img);
			map.put("nick_name", nick_name);
			DBhelper help = new DBhelper(mContext, IwoSQLiteHelper.MESSAGE_TAB);
			
			bundle.putString("head_img", head_img);
			bundle.putString("nick_name", nick_name); 
			//存到消息聊天中
			help.insert("tab_n" + PreferenceUtil.getString(mContext, "n_user_id", "") + bundle.getString("userID"), map);
			Intent intnet = new Intent("com.neter.broadcast.receiver.media.night.CHAT_BROADCAST_SHARE");
			intnet.putExtra("data", bundle);
			mContext.sendBroadcast(intnet);
			System.out.println(msg.getBody() + "没在聊天界面");
		}else {
			System.out.println(msg.getBody() + "聊天界面");
			if(mXmppGetMessage != null){
				mXmppGetMessage.getMessage(bundle);
			}
		}
	}
	public void setXmppGetMessage(XmppGetMessage msg){
		mXmppGetMessage = msg;
	}
	public interface XmppGetMessage {
		public void getMessage(Bundle bundle);
	}
	
	private HashMap<String, String> inster(Bundle bundle){
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("msg_tex", bundle.getString("body"));
		map.put("type", bundle.getString("category"));
		map.put("duartion", bundle.getString("duartion"));
		//String type = bundle.getString("category");
		map.put("richbody", bundle.getString("richbody"));
		//if(!"1".equals(type))
		//	map.put("richbody", FaceAction.GenerateImage(bundle.getString("richbody"), type));
		map.put("timestamp", bundle.getString("timestamp"));
		map.put("send", "2");
		map.put("head_img", bundle.getString("head_img"));
		map.put("user_id", bundle.getString("userID"));
		map.put("user_name", bundle.getString("user_name"));
		return map;
	}
}
