package com.android.iwo.media.dao;

import java.io.File;

import android.os.Environment;

public class ConstantsDownload {
	public static final String BASE_URL = "http://www.wyzc.com/index.php?a=game&m=Mobile2";
	public static final String IMG_URL = "http://www.wyzc.com/data/uploads/";
	public static final String DOMAIN_NAME = "http://www.wyzc.com/";
	public static final String NEW_VERSION ="http://www.wyzc.com/index.php?a=game&m=Mobile2&c=getVersionInfo&type=4";
	
	// 网络可用
	public static final int NETWORK = 1;
	// 没有网络
	public static final int NONETWORK = 2;
	
	/* 照片存储路径 */
	public static final String HEAD_PIC_URL = Environment.getExternalStorageDirectory() + "/woying_mobile/wyzc_headbg/";
	
	/* 头像名称 */
	public static final String IMAGE_FILE_NAME = "original_drawing.jpg";
	public static final String IMAGE_FINAL_FILE_NAME = "thumbnail";
	
	/* 请求码 */
	public static final int IMAGE_REQUEST_CODE = 0; // 相册
	public static final int CAMERA_REQUEST_CODE = 1; // 相机
	public static final int REQUEST_CODE_GETIMAGE_BYCROP = 12;
	public static final int RESULT_REQUEST_CODE = 2; // 结果码
	/* 照片缩小比例 */
	public static final int SCALE = 2;
	
	
	public static int downloadNum; // 下载数量
	// 4个页面的常量
	public static final int COURSE = 0; // 我的课程
	public static final int LEARN = 1;	// 学习中心
	public static final int PERSONAL = 2; // 个人中心
	public static final int ASKCLASSIFY = 3;// 有问必答
	
	// 用户信息
	public static final String ISLOGIN = "is_login"; // 标记是否登录：0代表没有登录，1代表已经登录
	public static final String UID = "uid";
	public static final String UNAME = "uname";
	public static final String UFACE = "uface";
	public static final String OAUTH_TOKEN = "oauth_token";
	public static final String OAUTH_TOKEN_SECRET = "oauth_token_secret";
	public static final String EMAIL = "email";
	public static final String REMIND = "is_remind";
	public static final String SEX = "sex";
	public static final String QQ = "qq";
	public static final String MOTTO = "motto";
	public static final String OPEN_MOVE = "open_move";
	
	// 下载数量
	public static final String DOWNLOAD_NUM = "download_num";
	
	// 设备串号
	public static final String IMEI_NUM = "imei_num";
	
	// 下载管理页面中handler需要的常量
	public static final int DELETE_START = 1001;
	public static final int DELETE_END = 2002;
	public static final int UPDATE_UI = 3003;
	
	public static final int DOWNLOADING = 1;
	public static final int PAUSE = 2;
	public static final int WAIT = 3;
	public static final int COMPLETE = 4;
	public static final int DELETE = 5;
	
	// 我的课程中的广播需要的常量
	public static final String ACTION_RECEIVER = "com.CourseReceiver";
	public static final String ACTION_RECEIVER2 = "com.UpdateCourseReceiver";
	// 视频列表页面中的广播需要的action
	public static final String ACTION_BROADCAST = "com.markvideobroadcast";
	public static final String ACTION_BROADCAST2 = "com.DownloadMsgBroadcast";
	// 修改下载数量广播的action
	public static final String ACTION_MODIFY_BADGE = "com.modifybadgebroadcast";
	// 更新下载界面广播的action
	public static final String ACTION_UPDATE_GRIDVIEW = "com.updategridviewbroadcast";
	// 更改导航栏里按钮状态的广播
	public static final String ACTION_MODIFY_RADIOBUTTON = "com.modifyradiobuttonbroadcast";
	// 更改你问我答问题的广播
	public static final String ACTION_UPDATE_QUESTION = "com.updatequestionbroadcast";
	
	// 线程池的容量
	public static final int POOL_CAPACITY = 1;
	// sd卡的根路径
	public static String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"mwoying";
	// 判断内容中是否有表情的正则表达式
	public static String REGULAR = "\\[(qq|msn|default)_0?([0-9]{1,2}|[a-zA-Z]{3,})\\]";
	// 标记是否可以下载
	public static boolean flag = false;
	
	// 下载管理的编辑状态
	// 无状态
	public static final int NO_STATUS = 0;
	// 下载中状态
	public static final int DOWNLOADING_STATUS = 1;
	// 已下载状态
	public static final int DOWNLOADED_STATUS = 2;
	
	public static String share_pre_name = "woying_config";
	
    //key在sd卡下的路径
	public static final String path_key = Environment.getExternalStorageDirectory().getPath()+"/iwo_downs"+"/iwo_key";
	//url在sd卡下的路径
	public static final String path_urls = Environment.getExternalStorageDirectory().getPath()+"/iwo_downs"+"/iwo_urls";
	//m3u8在sd卡下的路径
	public static final String path_m3u8 = Environment.getExternalStorageDirectory().getPath()+"/iwo_downs"+"/iwo_m3u8";
}
