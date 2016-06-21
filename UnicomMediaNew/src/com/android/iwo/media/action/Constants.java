package com.android.iwo.media.action;

/**
 * 常量
 */
public class Constants {
	public static final String CHAT_TYPE = "share_";

	// public static final String SOFT = "iwo_media";
	// public static final String CHAT_SERVER = "124.207.193.54";
	// public static final int CHAT_PORT = 5222;// 18522,5222
	// public static String CHAT_SERVER = "114.247.0.79";
	// public static int CHAT_PORT = 18522;// 18522,5222

	/**
	 * 好友数据库表
	 */
	public static final String TAB_FRIEND = "tab_friend";
	public static final String TAB_MESSAGE = "tab_message";
	/**
	 * 跳转页面设置的常量值 一级界面,0x000 二级 界面,0x000000 三级界面,0x000000000
	 */
	// 一级

	// 二级
	public static final int REQUEST_USER_EDITOR = 0x0000011;// 个人中心请求，编辑资料
	public static final int REQUEST_PERSONAL_FRIENDLIST = 0x000002;// 个人中心请求好友列表
	public static final int REQUEST_FRIENDDETAIL_FRIENDLIST = 0x000003;// 好友详情请求好友列表
	public static final int REQUEST_AFTERSALES_PERSONAL = 0x000004;// 售后服务请求个人中心
	public static final int REQUEST_GETBGIMG = 0x000005;// GetBgImg
	public static final int REQUEST_AFTERSALES1_INVITATION = 0x000006;// 我的维修请求邀请好友
	public static final int REQUEST_AFTERSALES2_INVITATION = 0x000007;// 我的保养请求邀请好友
	public static final int REQUEST_HOME_SERVICE = 0x0000101;//
	public static final int REQUEST_HOME_PERSONAL = 0x000102;//
	public static final int REQUEST_AFTERSALES2_ONLINEINSURANCE = 0x000103;//
	public static final int REQUEST_PERSONAL_TABMAIN = 0x000104;//
	public static final int REQUEST_HOME_TABMAIN = 0x0000105;//
	public static final int REQUEST_APP_HOME_PERSONAL = 0x0000106;// 主页请求
	public static final int REQUEST_APP_HOME_LOGIN = 0x0000107;// 主页请求
	public static final int REQUEST_APP_HOME_MAIN = 0x0000108;// 主页请求
	// 三级
	/**
	 * 两个界面传参，参数键值常量。
	 */

	public static String SHARE_TYPE = "1";

	/**
	 * 广告轮播图常量值。
	 */
	public static final int COMPANYADVERTISING = 1;
	public static final int COMPANYCAR = 2;

	/**
	 * 缓存参数
	 */
	public static final String VIDEO_CHANNEL = "video_channel";// 首页发现频道kay
	public static final String VIDEO_ADVERTISING = "video_advertising";// 首页发现广告
	public static final String VIDEO_ADVERTISING_NEW = "video_advertising_new";// 首页品牌广告
	
	public static final String VIDEO_CHANNEL_SYTE = "video_channel_syte"; //3天频道

	public static final String USER_INIT_DATA = "user_init_data";// 用户数据缓存

	public static final String FRIENDS_CIRCLE_VIDEO = "friends_circle_video";// 朋友圈第一次加载视频。
	public static final String PERSONAL_HOME_VIDEO = "personal_home_video";// 朋友圈第一次加载视频。

	public static final String ME_SEARCH_VIDEO = "me_search_video";// 我追的视频列表第一次加载。

	public static final String ME_SEARCH_LIST_VIDEO = "me_search_list_video";// 我追的视频列表第一次加载。
	public static final String ME_SEARCH_LIST_VIDEO_JPTJ = "me_search_list_video_jptj";//搜索里面的精品推荐
	public static final String RECOMMENDED_VIDEO_LIST = "recommended_video_list";// 推荐视频列表
	public static final String DEALER_AREA = "DEALER_AREA";// 城市地区经销商
	public static final String CITY_STRING = "city_string";// 城市字典
	public static final String INDUSTRY_STRING = "industry_string";// 获取行业字典
	public static final String EVENT_VIDEO_LIST = "event_video_list";// 推荐视频列表
	// public static final String RECOMMENDED_ABOUTUS = "about_us";
	public static final String RECOMMENDED_ABOUTUS = "share_about_us";
	public static final String GET_APP_URL = "APP_VIDEO_SHARE_URL";
	public static final String GET_APP_CHAT_IP = "APP_VIDEO_CHAT";
	public static final String GET_APP_CHAT_PORT = "APP_VIDEO_CHAT_PORT";
}
