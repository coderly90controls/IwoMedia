package com.test.iwomag.android.pubblico.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class AppUtil {

	public static String END = "";
	public static final String HTTP_END = "&lat={0}&lon={1}&uid={2}&up={3}&token={4}&version={5}&device=android&soft={6}";

	private CallBack mBack;
	private Activation activation;
	private String appSoft;

	/**
	 * 添加参数后面的公共参数
	 * 
	 * @param param
	 */
	public static void setPubParame(Context c, String soft, String exrt) {
		END = DataRequest.getUrl(HTTP_END, getPre(c, "address_lat"), getPre(c, "address_lng"), getPre(c, "user_id"), getPre(c, "user_name"), getToken(c), getVersion(c),
				soft) + exrt;
		Logger.i("end=" + END);
	}

	public static void setPubParame(Context c, String soft, String uid, String up, String exrt) {
		END = DataRequest.getUrl(HTTP_END, getPre(c, "address_lat"), getPre(c, "address_lng"), uid, up, getToken(c), getVersion(c), soft) + exrt;
		Logger.i("end=" + END);
	}

	public static void getParame(Context c, String soft, String exrt) {
		if (StringUtil.isEmpty(END))
			setPubParame(c, soft, exrt);
	}

	private static String getPre(Context context, String key) {
		return PreferenceUtil.getString(context, key, "");
	}

	/**
	 * 获取版本号
	 */
	public static String getVersion(Context context) {
		String versionname;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionname = pi.versionName;// 获取在AndroidManifest.xml中配置的版本号
		} catch (PackageManager.NameNotFoundException e) {
			versionname = "";
		}
		Logger.i("--" + versionname);
		return versionname;
	}

	/**
	 * 获取TelephonyManager
	 * 
	 * @param context
	 * @return TelephonyManager
	 */
	public static TelephonyManager getTelephonyManager(Context context) {
		return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);//
	}

	public static String getUrl(String url, String value) {

		try {
			if (StringUtil.isEmpty(value))
				value = "";
			else
				value = URLEncoder.encode((String) value, Config.ENCODING);
		} catch (UnsupportedEncodingException e) {
			Logger.e("getArrayFormUrl --- the unsupported encoding exception");
			return null;
		}
		return "&" + url + "=" + value;
	}

	/**
	 * 获取手机的所有信息。
	 * 
	 * @param context
	 * @return
	 */
	public static String getTelInf(Context context) {
		String inf = "";
		TelephonyManager tm = getTelephonyManager(context);
		/*
		 * 唯一的设备ID： GSM手机的 IMEI 和 CDMA手机的 MEID. Return null if device ID is not
		 * available.
		 */
		inf += getUrl("imei", tm.getDeviceId());
		/*
		 * 手机号： GSM手机的 MSISDN. Return null if it is unavailable.
		 */
		inf += getUrl("tel", tm.getLine1Number());
		/*
		 * 电话状态： 1.tm.CALL_STATE_IDLE=0 无活动 2.tm.CALL_STATE_RINGING=1 响铃
		 * 3.tm.CALL_STATE_OFFHOOK=2 摘机
		 */
		inf += getUrl("callstate", "" + tm.getCallState());
		/*
		 * 电话方位：
		 */
		inf += getUrl("celllocation", "" + tm.getCellLocation());
		/*
		 * 设备的软件版本号： 例如：the IMEI/SV(software version) for GSM phones. Return
		 * null if the software version is not available.
		 */
		inf += getUrl("devicesoftwareversion", tm.getDeviceSoftwareVersion());

		/*
		 * 附近的电话的信息: 类型：List<NeighboringCellInfo>
		 * 需要权限：android.Manifest.permission#ACCESS_COARSE_UPDATES
		 */
		inf += getUrl("neighboringcellInfo", "" + tm.getNeighboringCellInfo());

		/*
		 * 获取ISO标准的国家码，即国际长途区号。 注意：仅当用户已在网络注册后有效。 在CDMA网络中结果也许不可靠。
		 */
		inf += getUrl("networkcountryIso", tm.getNetworkCountryIso());

		/*
		 * MCC+MNC(mobile country code + mobile network code) 注意：仅当用户已在网络注册时有效。
		 * 在CDMA网络中结果也许不可靠。
		 */
		inf += getUrl("networkoperator", tm.getNetworkOperator());

		/*
		 * 按照字母次序的current registered operator(当前已注册的用户)的名字 注意：仅当用户已在网络注册时有效。
		 * 在CDMA网络中结果也许不可靠。
		 */
		inf += getUrl("networkoperatorname", tm.getNetworkOperatorName());

		/*
		 * 当前使用的网络类型： 例如： NETWORK_TYPE_UNKNOWN 网络类型未知 0 NETWORK_TYPE_GPRS GPRS网络
		 * 1 NETWORK_TYPE_EDGE EDGE网络 2 NETWORK_TYPE_UMTS UMTS网络 3
		 * NETWORK_TYPE_HSDPA HSDPA网络 8 NETWORK_TYPE_HSUPA HSUPA网络 9
		 * NETWORK_TYPE_HSPA HSPA网络 10 NETWORK_TYPE_CDMA CDMA网络,IS95A 或 IS95B. 4
		 * NETWORK_TYPE_EVDO_0 EVDO网络, revision 0. 5 NETWORK_TYPE_EVDO_A EVDO网络,
		 * revision A. 6 NETWORK_TYPE_1xRTT 1xRTT网络 7
		 */
		inf += getUrl("networktype", "" + tm.getNetworkType());

		/*
		 * 手机类型： 例如： PHONE_TYPE_NONE 无信号 PHONE_TYPE_GSM GSM信号 PHONE_TYPE_CDMA
		 * CDMA信号
		 */
		inf += getUrl("phonetype", "" + tm.getPhoneType());

		/*
		 * Returns the ISO country code equivalent for the SIM provider's
		 * country code. 获取ISO国家码，相当于提供SIM卡的国家码。
		 */
		inf += getUrl("simcountryiso", tm.getSimCountryIso());

		/*
		 * Returns the MCC+MNC (mobile country code + mobile network code) of
		 * the provider of the SIM. 5 or 6 decimal digits.
		 * 获取SIM卡提供的移动国家码和移动网络码.5或6位的十进制数字. SIM卡的状态必须是
		 * SIM_STATE_READY(使用getSimState()判断).
		 */
		inf += getUrl("simoperator", tm.getSimOperator());

		/*
		 * 服务商名称： 例如：中国移动、联通 SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).
		 */
		inf += getUrl("simoperatorname", tm.getSimOperatorName());

		/*
		 * SIM卡的序列号： 需要权限：READ_PHONE_STATE
		 */
		inf += getUrl("simserialnumber", tm.getSimSerialNumber());

		/*
		 * SIM的状态信息： SIM_STATE_UNKNOWN 未知状态 0 SIM_STATE_ABSENT 没插卡 1
		 * SIM_STATE_PIN_REQUIRED 锁定状态，需要用户的PIN码解锁 2 SIM_STATE_PUK_REQUIRED
		 * 锁定状态，需要用户的PUK码解锁 3 SIM_STATE_NETWORK_LOCKED 锁定状态，需要网络的PIN码解锁 4
		 * SIM_STATE_READY 就绪状态 5
		 */
		inf += getUrl("simstate", "" + tm.getSimState());

		/*
		 * 唯一的用户ID： 例如：IMSI(国际移动用户识别码) for a GSM phone. 需要权限：READ_PHONE_STATE
		 */
		inf += getUrl("subscriberid", tm.getSubscriberId());

		/*
		 * 取得和语音邮件相关的标签，即为识别符 需要权限：READ_PHONE_STATE
		 */
		inf += getUrl("voicemailalphatag", tm.getVoiceMailAlphaTag());

		/*
		 * 获取语音邮件号码： 需要权限：READ_PHONE_STATE
		 */
		inf += getUrl("voicemailnumber", tm.getVoiceMailNumber());

		/*
		 * ICC卡是否存在
		 */
		inf += getUrl("hasicccard", "" + tm.hasIccCard());

		/*
		 * 是否漫游: (在GSM用途下)
		 */
		inf += getUrl("isnetworkroaming", "" + tm.isNetworkRoaming());
		// 主板
		inf += getUrl("boand", Build.BOARD);
		// android系统定制商
		inf += getUrl("brand", Build.BRAND);
		// cpu指令集
		inf += getUrl("cpuabi", Build.CPU_ABI);
		// 设备参数
		inf += getUrl("device", Build.DEVICE);
		// 显示屏参数
		inf += getUrl("display", Build.DISPLAY);
		// 硬件名称
		inf += getUrl("fingerprint", Build.FINGERPRINT);
		inf += getUrl("host", Build.HOST);
		// 修订版本列表
		inf += getUrl("id", Build.ID);
		// 硬件制造商
		inf += getUrl("manufacturer", Build.MANUFACTURER);
		// 版本
		inf += getUrl("model", Build.MODEL);
		// 手机制造商
		inf += getUrl("product", Build.PRODUCT);
		// 描述build类型
		inf += getUrl("tags", Build.TAGS);
		inf += getUrl("time", "" + Build.TIME);
		// builder类型
		inf += getUrl("type", Build.TYPE);
		inf += getUrl("user", Build.USER);
		Logger.i("tel infmation " + inf + "");
		return inf;
	}

	/**
	 * 通过服务器检测版本号
	 * 
	 * @param context
	 * @param url
	 */
	public void checkVersion(Context context, String url) {
		new GetLineVersion(context, url).execute();
	}

	public void checkVersion(Context context, String url, String appSoft) {
		this.appSoft = appSoft;
		new GetLineVersion(context, url).execute();
	}

	public void checkVersion(Context context, String url, CallBack back) {
		mBack = back;
		new GetLineVersion(context, url).execute();
	}

	public void checkVersion(Context context, String url, CallBack back, Activation activation) {
		mBack = back;
		this.activation = activation;
		new GetLineVersion(context, url).execute();
	}

	public void checkVersion(Context context, String url, CallBack back, Activation activation, String appSoft) {
		this.appSoft = appSoft;
		mBack = back;
		this.activation = activation;
		new GetLineVersion(context, url).execute();
	}

	public static String getToken(Context context) {
		String de = getTelephonyManager(context).getDeviceId();
		if (StringUtil.isEmpty(de)) {
			return "no";
		} else if (de.length() < 10) {
			return "no";
		} else {
			return de;
		}
	}

	public class GetLineVersion extends AsyncTask<Void, Void, HashMap<String, String>> {

		private Context mContext;
		private String url = "";

		public GetLineVersion(Context context, String u) {
			mContext = context;
			url = u;
		}

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {

			return DataRequest.getHashMapFromUrl_Base64(url);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null && result.size() != 0) {
				Logger.i("--" + result);
				if ("1".equals(appSoft)) {
					if ("1".equals(result.get("code"))) { // 1 为 爱握视频APP
						HashMap<String, String> map = DataRequest.getHashMapFromJSONObjectString(result.get("data"));
						if (!StringUtil.isEmpty(map.get("version_no")) && map.get("version_no").compareTo(getVersion(mContext)) > 0) {
							downLoad(mContext, map.get("version_url"));
							Logger.i("----down");
						} else {
							if (mBack != null) {
								mBack.back();
							}
						}

					}

				} else {
					if (!StringUtil.isEmpty(result.get("version_no")) && result.get("version_no").compareTo(getVersion(mContext)) > 0) {
						downLoad(mContext, result.get("version_url"));
						Logger.i("----down");

					} else {
						if (mBack != null) {
							mBack.back();
						}
					}
				}

			}
			if (activation != null)
				activation.activation();// 防止在网速慢的情况下，多次弹出对话框。
		}

	}

	public interface CallBack {
		public void back();
	}

	public interface Activation {
		public boolean activation();
	}

	private void downLoad(final Context context, final String url) {
		Builder builder = new AlertDialog.Builder(context);
		Dialog dialog = builder.create();
		dialog.setTitle("提示");
		builder.setMessage("现在有新版本需要更新，是否更新？");
		builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Uri uri = Uri.parse(url);
				try {
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					context.startActivity(intent);
				} catch (ActivityNotFoundException ex) {
					Toast.makeText(context, "下载出错，请从官网或相关市场更新", Toast.LENGTH_SHORT).show();
				}
			}
		});
		builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.show();
	}

	/**
	 * 工程配置文件元数据读取工具
	 */
	private static Object readKey(Context context, String keyName) {

		try {

			ApplicationInfo appi = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

			Bundle bundle = appi.metaData;

			Object value = bundle.get(keyName);

			return value;

		} catch (NameNotFoundException e) {

			return null;

		}

	}

	public static int getInt(Context context, String keyName) {

		return (Integer) readKey(context, keyName);

	}

	public static String getString(Context context, String keyName) {

		return (String) readKey(context, keyName);

	}

	public static Boolean getBoolean(Context context, String keyName) {

		return (Boolean) readKey(context, keyName);

	}

	public static Object get(Context context, String keyName) {

		return readKey(context, keyName);

	}
}
