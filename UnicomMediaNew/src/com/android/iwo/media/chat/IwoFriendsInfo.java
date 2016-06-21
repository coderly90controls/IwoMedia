package com.android.iwo.media.chat;

import java.util.HashMap;

import com.android.iwo.media.action.Constants;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class IwoFriendsInfo {

	/**
	 * 账号
	 */
	private String jid;
	
	/**
	 * 手机号
	 */
	private String name;
	
	private String subscription;
	
	private String nick;
	
	private String avatar;
	
	private String age;
	
	private String sex;
	
	private String signature;
	
	private String videoImg;
	
	private String notename;

	public String getNotename() {
		return notename;
	}

	public void setNotename(String notename) {
		this.notename = notename;
	}

	public String getVideoImg() {
		return videoImg;
	}

	public void setVideoImg(String videoImg) {
		this.videoImg = videoImg;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public HashMap<String, String> toMap(){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("jid", this.jid);
		map.put("name", StringUtil.isEmpty(this.name)?"":this.name.replace(Constants.CHAT_TYPE, ""));
		map.put("subscription", this.subscription);
		map.put("nick", StringUtil.isEmpty(this.notename)?this.nick:this.notename);
		map.put("user_name", this.nick);
		map.put("avatar", this.avatar);
		map.put("age", this.age);
		map.put("sex", this.sex);
		map.put("signature", this.signature);
		map.put("videoImg", this.videoImg);
		map.put("notename", this.notename);
		return map;
	}
}
