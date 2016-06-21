package com.android.iwo.media.chat;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupInfo {

	public final static String NAME = "name";
	public final static String DESCRIPTION = "description";
	public final static String MAXNUMBER = "maxNumber";
	public final static String AVATAR = "avatar";
	public final static String AREA = "area";
	public final static String CITY = "city";
	public final static String CATEGORY = "category";
	
	private String id;
	private String name;
	private String description ;
	private String maxNumber ;
	private String avatar;
	private String area;
	private String city;
	private String category;
	private String isShield;
	
	private ArrayList<UserInfo> userInfo;
	
	public ArrayList<UserInfo> getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(ArrayList<UserInfo> userInfo) {
		this.userInfo = userInfo;
	}
	public String getIsShield() {
		return isShield;
	}
	public void setIsShield(String isShield) {
		this.isShield = isShield;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMaxNumber() {
		return maxNumber;
	}
	public void setMaxNumber(String maxNumber) {
		this.maxNumber = maxNumber;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	public static class UserInfo{
		private String jid;
		private String username;
		public UserInfo(){
			
		}
		public String getJid() {
			return jid;
		}
		public void setJid(String jid) {
			this.jid = jid;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
	}
	
	public HashMap<String, String> toMap(){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", this.id);
		map.put("name", this.name);
		map.put("description", this.description);
		map.put("maxNumber", this.maxNumber);
		map.put("avatar", this.avatar);
		map.put("area", this.area);
		map.put("city", this.city);
		map.put("category", this.category);
		map.put("isShield", this.isShield);
		/*for(int i=0; i<userInfo.size(); i++){
			map.put("jid", userInfo.get(i).getJid());
			map.put("username", userInfo.get(i).getUsername());
		}*/
		
		return map;
	}
}
