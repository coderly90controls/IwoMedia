package com.test.iwomag.android.pubblico.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

public class StringUtil {


	/**
	 * 将unicode转为中文
	 * 
	 * @param s
	 * @return
	 */
	public static String unicodeToGBK(String s) {
		String[] k = s.split(";");
		String rs = "";
		for (int i = 0; i < k.length; i++) {
			int strIndex = k[i].indexOf("&#");
			String newstr = k[i];
			if (strIndex > -1) {
				String kstr = "";
				if (strIndex > 0) {
					kstr = newstr.substring(0, strIndex);
					rs = rs + kstr;
					newstr = newstr.substring(strIndex);
				}

				int m = Integer.parseInt(newstr.replace("&#", ""));
				char c = (char) m;
				rs = rs + c;
			} else {
				rs = rs + k[i];
			}
		}

		return rs;
	}

	/**
	 * 过滤文本中的特殊表情标签，重新替换为文本表情以便发送到服务器
	 * 
	 * @param content
	 * @return
	 */
	public static String filterHtml(String content) {

		String cont = unicodeToGBK(content);

		String pattern = "<img src=\"(\\[f\\d+\\])\"/?>";
		StringBuilder sb = new StringBuilder();
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(cont);
		int end = 0;
		while (m.find()) {
			String face = m.group(1); // 表情文本
			int start = m.start();
			sb.append(cont.substring(end, start)).append(face);
			end = m.end();
		}
		sb.append(cont.substring(end));
		return sb.toString().trim();
	}

	/**
	 * 获取字符串中第一个匹配的部分
	 * 
	 * @param str
	 * @param regex
	 * @return
	 */
	public static String getFirstMatch(String str, String regex) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		String temp = null;
		if (m.find()) {
			temp = m.group();
		}
		return temp;
	}

	/**
	 * 对字符串进行url编码
	 * 
	 * @return
	 */
	public static String urlencode(String src, String encoding) {
		try {
			return URLEncoder.encode(src, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return src;
	}

	public static String urlencode(String src) {
		return urlencode(src, "UTF-8");
	}

	public static String bytesToString(byte[] bytes) {
		StringBuilder hs = new StringBuilder();
		for (int n = 0; n < bytes.length; n++) {
			String stmp = java.lang.Integer.toHexString(bytes[n] & 0xFF);
			if (stmp.length() == 1) {
				hs.append("0").append(stmp);
			} else {
				hs.append(stmp);
			}
		}
		return hs.toString().toLowerCase();
	}
	public static final String readUTF(DataInputStream dis) throws IOException {
		String retVal = dis.readUTF();
		if (retVal.length() == 0)
			return null;
		return retVal;
	}

	// 时间戳到当前时间转换
	public static String getTime(String format, String str_time) {
		String result = "";
		if (format != null && !format.equals("") && str_time != null
				&& str_time.length() > 0 && !str_time.equals("")) {
			Long time = Long.parseLong(str_time);
			result = new java.text.SimpleDateFormat(format)
					.format(new java.util.Date(time * 1000));
		}
		return result;
	}

	public static String getMD5(byte[] source) {
		String s = null;
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest();
			char str[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static boolean isValidAddress(String address) {
		// Note: Some email provider may violate the standard, so here we only
		// check that
		// address consists of two part that are separated by '@', and domain
		// part contains
		// at least one '.'.
		int len = address.length();
		int firstAt = address.indexOf('@');
		int lastAt = address.lastIndexOf('@');
		int firstDot = address.indexOf('.', lastAt + 1);
		int lastDot = address.lastIndexOf('.');
		return firstAt > 0 && firstAt == lastAt && lastAt + 1 < firstDot
				&& firstDot <= lastDot && lastDot < len - 1;
	}

	public static String replaceBlank(String str) {
		// Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Pattern p = Pattern.compile("\\s*|\t");
		Matcher m = p.matcher(str);
		String after = m.replaceAll("");
		return after;
	}
	
	public String formatDay(String end_time)
	{
		if("".equals(end_time) || end_time == null)
		{
			return "";
		}
		String split[] = end_time.split("\\.");
		end_time = split[0];
		Date date = new Date();
		Long now = date.getTime();
		now /= 1000;
		Long old = Long.parseLong(end_time);
		Long newTime = old - now;
		String day_str = "";
		String hour_str = "";
		if(newTime <= 0)
		{
			return "已结束";
		}
		else
		{
			int day = (int) (newTime / (3600 * 24));
			if(day != 0)
			{
				day_str = Integer.toString(day);
				day_str += "天";
			}
			int hour = (int)((newTime - day * 24 * 3600) / 3600);
			hour_str = Integer.toString(hour);
			hour_str += "小时";
		}
		return day_str + " " + hour_str;
	}
	
	
	/**
	 * @Description: emaill格式验证
	 * @param @param email
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean isEmail(String email){
		String check = "^([a-z0-9a-z]+[-|\\.]?)+[a-z0-9a-z]@([a-z0-9a-z]+(-[a-z0-9a-z]+)?\\.)+[a-za-z]{2,}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(email);
		boolean ismatched = matcher.matches();
		return ismatched;
	}
	
	/**
	 * @Description: 手机号码验证
	 * @param @param str
	 * @param @return
	 * @return boolean
	 * @throws
	 */
    public static boolean isPhone(String str) {
    	Pattern pattern = Pattern.compile("1[0-9]{10}");
    	Matcher matcher = pattern.matcher(str); 
    	if (matcher.matches()) {
    		return true;
    	}else {
    		return false;
    	}  
    }
    /**
	 * @Description: 年龄验证
	 * @param @param str
	 * @param @return
	 * @return boolean
	 * @throws
	 */
    public static boolean isAge(String str) {
    	Pattern pattern = Pattern.compile("^(?:[1-9][0-9]?|1[01][0-9]|120)$");
    	Matcher matcher = pattern.matcher(str); 
    	if (matcher.matches()) {
    		return true;
    	}else {
    		return false;
    	}  
    }

    /**
	 * @Description: 车牌号验证
	 * @param @param str
	 * @param @return
	 * @return boolean
	 * @throws
	 */
    public static boolean isLicensePlate(String str) {
    	Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{3,5}");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
    		return true;
    	}else {
    		return false;
    	}  
    }
    
    /**
   	 * @Description: 姓名验证
   	 * @param @param str
   	 * @param @return
   	 * @return boolean
   	 * @throws
   	 */
       public static boolean isName(String str) {
       	Pattern pattern = Pattern.compile("([\u4E00-\u9FA5]{2,7})|([a-zA-Z]{3,10})");
       	Matcher matcher = pattern.matcher(str); 
       	if (matcher.matches()) {
       		return true;
       	}else {
       		return false;
       	}  
       }
    
    

    /**
	 * @Title getImgSrc
	 * @Description: 从一个字符串中获取图片地址
	 * @param String
	 * @return String
	 * @throws
	 */

	public static String getImgSrc(String s) {
		String[] ss = null;
		String[] sss = null;
		if (s.contains("<img")) {
			ss = s.split("<img");
			sss = ss[1].split("/>");
		} else if (s.contains("src=\"")) {
			ss = s.split("src=\"");
			sss = ss[1].split("\"");
		} else {
			return "";
		}
		return sss[0];
	}
	
	
	/** 
	 * MD5加密类 
	 */  
	public static String MD5(String str){  
	    try {  
	        MessageDigest md = MessageDigest.getInstance("MD5");  
	        md.update(str.getBytes());  
	        byte[]byteDigest = md.digest();  
	        int i;  
	        StringBuffer buf = new StringBuffer("");  
	        for (int offset = 0; offset < byteDigest.length; offset++) {  
	            i = byteDigest[offset];  
	            if (i < 0)  
	                i += 256;  
	            if (i < 16)  
	                buf.append("0");  
	            buf.append(Integer.toHexString(i));  
	        }  
//	        //32位加密  
//	        return buf.toString();  
	        // 16位的加密  
	        return buf.toString().substring(8, 24);   
	    } catch (NoSuchAlgorithmException e) {  
	        e.printStackTrace();  
	        return null;  
	    }  
	}  
	public static String[][] getUseArray(String[][] array) {
		int n = 0;
		for (int i = 0; i < array.length; i++) {
			if (!StringUtil.isEmpty(array[i][0])) {
				n++;
			} else {
				break;
			}
		}

		String[][] it = new String[n][2];
		for (int i = 0; i < n; i++) {
			it[i][0] = array[i][0];
			it[i][1] = array[i][1];
		}

		return it;
	}
	
	public static boolean isEmpty(String str){
		if(TextUtils.isEmpty(str) || "null".equals(str) || "".equals(str)){
			return true;
		}else {
			return false;
		}
	}
	public static boolean isEmpty(CharSequence str){
		if(str == null || "".equals(str) || "null".equals(str)){
			return true;
		}else {
			return false;
		}
	}
	public static boolean isEmpty(Object str){
		if(str == null || "".equals(str) || "null".equals(str)){
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 设置中间几位为*
	 * @param tel
	 * @return
	 */
	public static String setTelHint(String tel){
		String res = "";
		for(int i=0; i<tel.length(); i++){
			if(i>2 && i<7){
				res += "*";
			}else {
				res += tel.charAt(i);
			}
		}
		return res;
	}
	
	public static boolean isPassWord(String password){
		int length = password.length();
		Logger.i("密码测试");
		if(length <6 || length > 16){
			Logger.i("密码测试长度问题");
			return false;
		}else{
			Pattern pattern = Pattern.compile("^[A-Za-z0-9]{6,16}");
	    	Matcher matcher = pattern.matcher(password); 
	    	if (matcher.matches()) {
	    		Logger.i("密码测试字符匹配成功");
	    		return true;
	    	}else {
	    		Logger.i("密码测试匹配失败");
	    		return false;
	    	}  
		}
	}
	
	public static HashMap<String, String> StringToHashMap(String value){
		if(isEmpty(value)) return null;
		HashMap<String, String> map = new HashMap<String, String>();
		
		String[] strings = value.replace("{", "").replace("}", "").split(",");
		
		for(String str:strings){
			String[] key = str.split("="); 
			if(key.length ==2){
				map.put(key[0].trim(), key[1].trim());
			}
		}
		return map;
	}
	
	public static float round(float value, int scale){
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, BigDecimal.ROUND_UP);
		float d = (float) bd.doubleValue();
		bd = null;
		return d;
	}
	
	/**
	 * 发送文件到服务器
	 */
	public static String[] url2absolute(String[] relativeurl, String site) {
		List<String> l = new ArrayList<String>();
		if (relativeurl != null) {
			for (int i = 0; i < relativeurl.length; i++) {
				if (relativeurl[i] != null && relativeurl[i].trim().equals("")) {
					continue;
				}
				if (relativeurl[i] != null && relativeurl[i].trim().equals("#")) {
					continue;
				}
				if (relativeurl[i] != null
						&& relativeurl[i].trim().startsWith("javascript")) {
					continue;
				}

				String s = url2absolute(relativeurl[i], site);
				if (s != null) {
					l.add(s);
				}
			}
		}
		return l.toArray(new String[0]);
	}

	/**
	 * 发送文件到服务器
	 */
	public static String url2absolute(String relativeurl, String site) {

		if (relativeurl == null)
			return relativeurl;
		if (relativeurl.toLowerCase().indexOf("javascript") != -1) {
			return null;
		}
		relativeurl = relativeurl.trim();
		relativeurl = relativeurl.replaceAll("\r\n", "");
		relativeurl = relativeurl.replaceAll("\n", "");
		relativeurl = relativeurl.replaceAll("\r", "");
		relativeurl = relativeurl.replaceAll("\\s", "%20");
		try {
			URL baseURL = new URL(site);
			if (!relativeurl.contains("%2F"))
				relativeurl = relativeurl.replaceAll("&amp;", "&");
			URL relativeURL = new URL(baseURL, relativeurl);
			return relativeURL.toExternalForm();
		} catch (MalformedURLException e1) {
			Logger.e(e1.getMessage());
			return null;
		}
	}
}
