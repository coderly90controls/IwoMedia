package com.test.iwomag.android.pubblico.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetworkUtil {
	
	public static boolean getNetStatus(Context context) {
		NetworkInfo network = ((ConnectivityManager) context .getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		return network != null && network.isConnected();
	}

	/**
	 * 判断是否连接上WIFI
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWIFIConnected(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		boolean flag = false;
		if (wifiManager.isWifiEnabled()) {
			WifiInfo info = wifiManager.getConnectionInfo();
			if (info != null) {
				flag = info.getNetworkId() != -1;
			}
		}
		return flag;
	}

	/**
	 * 获取本地ip
	 * 
	 * @param context
	 * @return
	 */
	public static String getLocalIpAddress(Context context) {
		String ip = "";
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled()) {
			WifiInfo info = wifiManager.getConnectionInfo();
			if (info != null) {
				int intIp = info.getIpAddress();
				ip = int2ip(intIp);
			}
		}
		if (ip == null) {
			try {
				outer: for (Enumeration<NetworkInterface> en = NetworkInterface
						.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf
							.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							ip = inetAddress.getHostAddress().toString();
							break outer;
						}
					}
				}
			} catch (SocketException ex) {
				Log.e("NetworkUtils", "获取ip异常", ex);
			}
		}

		return ip;
	}

	private static String int2ip(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}
}
