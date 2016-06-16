package com.test.iwomag.android.pubblico.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * 格式化时间
 * 
 * @author hanpengyuan
 * 
 */
public class DateUtil {
	static final int SECONDS_OF_HOUR = 3600;
	static final int SECONDS_OF_DAY = SECONDS_OF_HOUR * 24;
	static final int SECONDS_OF_MONTH = SECONDS_OF_DAY * 30;
	static final int SECONDS_OF_YEAR = SECONDS_OF_MONTH * 12;

	public static String subDate(long l) {
		long now = new Date().getTime();
		int sub = (int) ((now - l) / 1000);
		int year = sub / SECONDS_OF_YEAR;
		int month = (sub - year * SECONDS_OF_YEAR) / SECONDS_OF_MONTH;
		int day = (sub - year * SECONDS_OF_YEAR - month * month) / SECONDS_OF_DAY;
		int hour = (sub - year * SECONDS_OF_YEAR - month * month - day * SECONDS_OF_DAY) / SECONDS_OF_HOUR;
		int minute = (sub - year * SECONDS_OF_YEAR - month * month - day * SECONDS_OF_DAY - hour * SECONDS_OF_HOUR) / 60;
		int sec = sub - year * SECONDS_OF_YEAR - month * month - day * SECONDS_OF_DAY - hour * SECONDS_OF_HOUR - minute * 60;
		if (year > 0) {
			return format(Config.TIMEFORMAT_FULL, l);
		}
		if (month > 3) {
			return format(Config.TIMEFORMAT_SHORT, l);
		} else if (month > 0) {
			return month + "个月前";
		}
		if (day > 0) {
			return day + "天前";
		}
		if (hour > 0) {
			return hour + "小时前";
		}
		if (minute > 0) {
			return minute + "分钟前";
		}
		if (sec > 0) {
			return sec + "秒前";
		}
		return "刚刚";
	}

	/**
	 * 把"yyyy-MM-dd HH:mm:ss"转换成 多少时间前
	 * 
	 * @param time
	 * @return
	 */
	public static String formatTime(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			return "";
		}

		return subDate(date.getTime());
	}

	public static String getTime(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			return "";
		}
		long cur = System.currentTimeMillis();
		long t = cur - date.getTime();

		if (t / (1000 * 60 * 10.0) < 1) {
			return "刚刚";
		} else if (t / (1000 * 60 * 60.0f) < 1) {
			return ((int) (t / (1000 * 60.0f))) + "分钟前";
		} else if (t / (1000 * 60 * 60 * 24.0f) < 1) {
			return (int) (t / (1000 * 60 * 60.0f)) + "小时前";
		} else if (t / (1000 * 60 * 60 * 24.0f) > 1) {
			return dateToStr(date);
		}
		return subDate(date.getTime());
	}

	public static String dateToStr(Date dateDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
		String dateString = formatter.format(dateDate);
		return dateString;
	}

	/**
	 * 格式化当前时间
	 * 
	 * @param format
	 * @return
	 */
	public static String format(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		return sdf.format(new Date());
	}

	/**
	 * 格式化指定 的时间
	 * 
	 * @param format
	 * @return
	 */
	public static String format(String format, long time) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = new Date();
		date.setTime(time);
		return sdf.format(date);
	}

	/**
	 * 格式化指定 的时间
	 * 
	 * @param format
	 * @return
	 */
	public static String format(String format, String time) {
		long t = 0;
		try {
			t = Long.valueOf(time);
		} catch (Exception e) {
			return "";
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = new Date();
		date.setTime(t);
		return sdf.format(date);
	}

	/**
	 * 格式化指定 的时间
	 * 
	 * @param format
	 * @return
	 */
	public static String format(String from, String to, String time) {
		if (StringUtil.isEmpty(time))
			return "";
		SimpleDateFormat format1 = new SimpleDateFormat(from);
		Date date1 = null;
		try {
			date1 = format1.parse(time);
		} catch (ParseException e) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(to);
		Date date = new Date();
		try {
			date.setTime(date1.getTime());
		} catch (Exception e) {
			return "";
		}

		return sdf.format(date);
	}

	/**
	 * 获取字段的时间
	 * 
	 * @return
	 */
	public static long getTime(String formatstr, String time) {
		SimpleDateFormat format = new SimpleDateFormat(formatstr);
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException e) {
			return 0;
		}

		return date.getTime();
	}

	/**
	 * @Description: 时间截字符串转时间格式
	 * @param @param timestampString
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String TimeStamp2Date(String timestampString) {
		Long timestamp = Long.parseLong(timestampString) * 1000;
		String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
		return date;
	}

	/**
	 * 获取当前时间的日历。
	 * 
	 * @return
	 */
	public static Calendar getNowCalendar() {
		return Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
	}

	public static int getNowDay() {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
		return c.get(Calendar.DAY_OF_MONTH);

		// return Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));

	}

	/**
	 * 获取下一个月的日历。
	 * 
	 * @return
	 */
	public static Calendar getNowNextMonthCalendar(Calendar c) {
		c.add(Calendar.MONTH, 1);
		return c;
	}

	/**
	 * 获取当前日历的月份
	 * 
	 * @param c
	 * @return
	 */
	public static int getNowMonth(Calendar c) {
		return c.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取当前日历的年份
	 * 
	 * @param c
	 * @return
	 */
	public static int getNowYear(Calendar c) {
		return c.get(Calendar.YEAR);
	}

	/**
	 * 获取当前月份的最大天数
	 * 
	 * @param c
	 * @return
	 */
	public static int getNowMaxmum(Calendar c) {

		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static ArrayList<HashMap<String, String>> getWhatDay() {
		ArrayList<HashMap<String, String>> map = new ArrayList<HashMap<String, String>>();
		// TODO 这里应该用递归，但是由于开发时间原因没有修改。等以后再修改。
		// now 月
		Calendar nowCalendar = getNowCalendar();
		int today = nowCalendar.get(Calendar.DAY_OF_MONTH);// 今天
		int thisMonth = getNowMonth(nowCalendar);// 本月
		int thisMonthMaxmum = getNowMaxmum(nowCalendar);// 本月最大天数
		map.addAll(appMap(thisMonth, today + 1, thisMonthMaxmum));
		// Next 月
		Calendar nextCalendar = getNowNextMonthCalendar(nowCalendar);
		int nextMonthMaxmum = getNowMaxmum(nextCalendar);
		int nexMonth = getNowMonth(nextCalendar);// 下月

		if (nextMonthMaxmum + (thisMonthMaxmum - today) <= 60) {
			map.addAll(appMap(nexMonth, 1, nextMonthMaxmum));
			Calendar nextThreeCalendar = getNowNextMonthCalendar(nextCalendar);
			int nextThreeMonth = getNowMonth(nextThreeCalendar);// 下下月
			map.addAll(appMap(nextThreeMonth, 1, 60 - (nextMonthMaxmum + (thisMonthMaxmum - today))));
		} else {
			map.addAll(appMap(nexMonth, 1, nextMonthMaxmum - (60 - (nextMonthMaxmum + (thisMonthMaxmum - today)))));
		}
		String temp = "";
		HashMap<String, String> map2 = null;
		for (int i = 0; i < map.size(); i++) {
			map2 = map.get(i);
			if (!temp.equals(map2.get("month"))) {
				temp = map2.get("month");
				map.get(i).put("show", temp);
			}
		}
		return map;
	}

	private static ArrayList<HashMap<String, String>> appMap(int Month, int today, int thisMonthMaxmum) {
		ArrayList<HashMap<String, String>> dayList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = null;
		for (int i = today; i <= thisMonthMaxmum; i++) {
			map = new HashMap<String, String>();
			map.put("month", "" + Month);
			map.put("day", "" + i);
			dayList.add(map);
		}

		return dayList;
	}

	public static ArrayList<HashMap<String, String>> getWhatDay(int day) {
		ArrayList<HashMap<String, String>> map = new ArrayList<HashMap<String, String>>();
		// TODO 这里应该用递归，但是由于开发时间原因没有修改。等以后再修改。
		// now 月
		Calendar nowCalendar = getNowCalendar();
		int today = nowCalendar.get(Calendar.DAY_OF_MONTH);// 今天
		int thisMonth = getNowMonth(nowCalendar);// 本月
		int thisMonthMaxmum = getNowMaxmum(nowCalendar);// 本月最大天数
		map.addAll(appMap(thisMonth, today + 1 + day, thisMonthMaxmum));
		// Next 月
		Calendar nextCalendar = getNowNextMonthCalendar(nowCalendar);
		int nextMonthMaxmum = getNowMaxmum(nextCalendar);
		int nexMonth = getNowMonth(nextCalendar);// 下月

		if (nextMonthMaxmum + (thisMonthMaxmum - today - day) <= 60) {
			map.addAll(appMap(nexMonth, 1, nextMonthMaxmum));
			Calendar nextThreeCalendar = getNowNextMonthCalendar(nextCalendar);
			int nextThreeMonth = getNowMonth(nextThreeCalendar);// 下下月
			map.addAll(appMap(nextThreeMonth, 1, 60 - (nextMonthMaxmum + (thisMonthMaxmum - today - day))));
		} else {
			map.addAll(appMap(nexMonth, 1, nextMonthMaxmum - (60 - (nextMonthMaxmum + (thisMonthMaxmum - today - day)))));
		}
		String temp = "";
		HashMap<String, String> map2 = null;
		for (int i = 0; i < map.size(); i++) {
			map2 = map.get(i);
			if (!temp.equals(map2.get("month"))) {
				temp = map2.get("month");
				map.get(i).put("show", temp);
			}
		}
		return map;

	}

	/**
	 * 获取出生日期所有的月份。
	 * 
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> getAllMonth() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = null;
		int year = getNowYear(getNowCalendar());
		for (int i = 1; i <= 12; i++) {
			map = new HashMap<String, String>();
			map.put("dict_name", (i < 10 ? "0" + i : i) + "");
			list.add(map);
		}
		return list;
	}
	/**
	 * 获取出生日期所有的年份。
	 * 
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> getAllYear() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = null;
		int year = getNowYear(getNowCalendar());
		for (int i = year; i > year - 119; i--) {
			map = new HashMap<String, String>();
			map.put("dict_name", i + "");
			list.add(map);
		}
		return list;
	}
	
	
	/**
	 * 判断当前年份是否是瑞年。
	 * 
	 * @param year
	 * @return
	 */
	public static boolean isGregorianLeapYear(int year) {
		boolean isLeap = false;
		if (year % 4 == 0)
			isLeap = true;
		if (year % 100 == 0)
			isLeap = false;
		if (year % 400 == 0)
			isLeap = true;
		return isLeap;
	}
	public static ArrayList<HashMap<String, String>> getAllDay(int year, int month) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = null;
		int max = 0;
		switch (month) {
		case 1:
			max = 31;
			break;
		case 2:
            if(isGregorianLeapYear(year)){
            	max = 29;
            }else{
            	max = 28;
            }
			break;
		case 3:
			max = 31;
			break;
		case 4:
			max = 30;
			break;
		case 5:
			max = 31;
			break;
		case 6:
			max = 30;
			break;
		case 7:
			max = 31;
			break;

		case 8:
			max = 31;
			break;
		case 9:
			max = 30;
			break;
		case 10:
			max = 31;
			break;
		case 11:
			max = 30;
			break;
		case 12:
			max = 31;
			break;

		default:
			max = 31;
			break;
		}

		for (int i = 1; i <= max; i++) {
			map = new HashMap<String, String>();
			map.put("dict_name", (i < 10 ? "0" + i : i) + "");
			list.add(map);
		}
		return list;
	}
}
