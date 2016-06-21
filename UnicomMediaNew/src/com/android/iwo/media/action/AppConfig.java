package com.android.iwo.media.action;

import com.test.iwomag.android.pubblico.util.PreferenceUtil;

import android.content.Context;

public class AppConfig {

	public static String ENCODING = "UTF-8";
	public static String SHARE_TYPE = "1";
	public static String TITLE = "";
	public static boolean ISLOIN=false;
	// public static String HTTP_TITLE = TITLE + "v/";
	public static String HTTP_TITLE = TITLE + "share/";

	/**
	 * 新分享的接口
	 */
	public static final String NEW_UN_GET_CHANNEL_EDITIMG = TITLE + "check_iwo?type=get_ch_info&ch_id={0}&video_id={1}&is_ad={2}";// 小编头像
	public static final String NEW_UN_GET_CHANNEL_ISLOIN = HTTP_TITLE + "check_validate?type=get_user_info";// 判断登录状态
	public static final String NEW_UN_GET_CHANNEL_VIDEO = TITLE + "check_iwo?type=get_channel_video&ch_id={0}&p={1}&n={2}";// 搜索频道列表
	public static final String NEW_UN_GET_CHANNEL_VIDEO_JPTJ = HTTP_TITLE + "check_validate?type=search_jptj&p={0}&n={1}";// 搜索频道列表_精品推荐

	public static final String NEW_UN_SEARCH_VIDEO_LIST = TITLE + "check_iwo?type=search_video_list&key={0}&p={1}&n={2}";// 搜索某字段视频列表。

	public static final String NEW_UN_GET_SHARE_LIST = HTTP_TITLE + "check_validate?type=get_share_list&p={0}&n={1}";// 获取我追的视频列表。
	public static final String NEW_UN_GET_SHARE_LIST_NEW = TITLE + "check_iwo?type=get_children_channel&pid={0}";//推荐收藏
	public static final String NEW_UN_GET_SHARE_LIST_NEW_STRING  = HTTP_TITLE + "check_validate?type=get_share_list&p={0}&n={1}";// 获取我追的视频列表全部

	public static final String NEW_UN_VIDEO_GET_SHARE = HTTP_TITLE + "check_validate?type=get_share&video_id={0}&status={1}&ch_id={2}";// 设置时候追这个视频。

	public static final String NEW_UN_GET_VIDEO_COLLECT = HTTP_TITLE + "check_validate?type=get_video_collect&video_id={0}";// 设
	public static final String NEW_UN_GET_AGENCY_CAR = TITLE + "check_iwo?type=get_agency_car&agency_id={0}";

	public static String NEW_V_GET_VIDEO_CHANNEL = TITLE + "check_iwo?type=get_children_channel&pid={0}";// 获取首页多少个频道
	public static String NEW_V_GET_VIDEO_CHANNEL_BRAND = HTTP_TITLE + "check_validate?type=get_brand_channel_video&ch_id={0}";// 获取品牌全部数据
	public static String NEW_V_GET_VIDEO_CHANNEL_BRAND_MORE = HTTP_TITLE + "check_validate?type=get_index_channel_video&ch_id={0}";// more全部数据
	public static String NEW_V_GET_TEST_INFO = TITLE + "check_iwo?type=get_test_info&video_id={0}";// 获取视频预约试驾接口

	public static String NEW_V_TEST_DRIVER_SAVE = TITLE
			+ "check_iwo?type=test_driver_save&video_id={0}&mobile={1}&agency_name={2}&car_name={3}&city_name={4}&area_name={5}&buy_name={6}&username={7}&mobile_system=1&iwo_sys=6";// 试驾保存接口

	public static String NEW_V_GET_AD = TITLE + "check_iwo?type=get_ad&ad_code={0}&ad_sys=6&count=10";

	public static String NEW_V_GET_DEALER_AREA = TITLE + "get_dealer_area?ss";// 获取城市，地区经销商。

	public static String NEW_V_GET_DICT_LIST = TITLE + "check_iwo?type=get_dict_list&dict_type=106";

	public static final String VIDEO_SHARE_COUNT = TITLE + "v/check_iwo?type=get_share_count&video_id={0}";
	public static final String VIDEO_SEND_SMS = TITLE + "check_iwo?type=send_sms&coutext={0}&mobiles={1}";

	// http://wap.iwo.mokacn.com:8080//http://iwotv.cn/check_iwo?type=get_app&sys=1
	// http://114.247.0.78:8080/ "http://wap.iwo.mokacn.com/"
	public static String VIDEO_GET_APP_URL = "http://wap.iwo.mokacn.com:8080/check_iwo?type=get_app&sys=6";// 视频分享
	// public static String VIDEO_GET_APP_URL =
	// "http://wap.iwo.mokacn.com:8080/check_validate?type=get_app&sys=6";//
	// 视频分享
	
	//类型
	public static String NEW_V_GET_VIDEO_CHANNEL_SYTE = TITLE + "check_iwo?type=get_children_channel&pid={0}";
	public static String NEW_V_GET_VIDEO_CHANNEL_SYTE_NEW = TITLE + "check_iwo?type=get_channel_info&ch_id={0}";
	public static String VIDEO_GET_NIGHT_INFO = HTTP_TITLE + "check_iwo?type=get_night_info&user_name={0}";
	public static String VIDEO_GET_VERSION = TITLE + "check_iwo?type=get_client_version";// 获取版本
	public static String VIDEO_CHAT_VIDEO = TITLE + "chat_video?name=aaa";
	public static String VIDEO_GET_TOPIC_LIST = TITLE + "check_iwo?type=get_topic_list&p={0}&n={1}&sys=1&status=1";

	public static String VIDEO_GET_AD = TITLE + "check_iwo?type=get_ad&ad_code=video_share_start&count=5&ad_sys=6&device={0}";
	public static String VIDEO_LON_LAT = TITLE + "check_iwo?type=lon_lat";
	public static String VIDEO_GET_CHILDREN_CHANNEL = TITLE + "check_iwo?type=get_children_channel&pid=100";

	public static String VIDEO_IMPORT_CONTACTS = HTTP_TITLE + "check_iwo?type=import_contacts&contacts={0}&sys=1";
	public static String VIDEO_USER_NEARBY = HTTP_TITLE + "check_iwo?type=user_nearby&age_min={0}&age_max={1}&time={2}&sex={3}&p={4}&n=10";
	// public static String NEW_USER_LOGIN_STEP1 = HTTP_TITLE +
	// "check_iwo?type=new_login&mobile={0}";// 用户新登录
	public static String NEW_USER_LOGIN_STEP1 = HTTP_TITLE + "check_validate?type=login&mobile={0}&pass={1}";
	// public static String NEW_USER_LOGIN = HTTP_TITLE +
	// "check_iwo?type=login&mobile={0}&pass={1}";//用户旧登录
	public static String NEW_USER_LOGIN = HTTP_TITLE + "check_validate?type=login&mobile={0}&pass={1}";
	public static String NEW_USER_LOGIN_UMENG = HTTP_TITLE + "check_validate?type=login&tp_id={0}&nick_name={1}&sex={2}&head_img={3}";
	public static String NEW_USER_LOGIN_CODE = TITLE + "check_iwo?type=send_code&mobile={0}";// 第一次登录发送验证码
	// public static String NEW_USER_CHECK_CODE = HTTP_TITLE +
	// "check_iwo?type=reg_activate&mobile={0}&active_code={1}"; //用户注册成功
	public static String NEW_USER_CHECK_CODE = HTTP_TITLE + "check_validate?type=reg_pass&user_name={0}&nick_name={1}&user_pass={2}&active_code={3}";
	public static String NEW_USER_REGIN_OK = HTTP_TITLE + "check_validate?type=reg_pass&user_name={0}&user_pass={1}&nick_name={2}";//按最新要求修改的注册
	// public static String NEW_USER_INFO = HTTP_TITLE +
	// "check_iwo?type=register_end&head_img={0}&nickname={1}&password={2}&mobile={3}";//用户注册完成
	// 最后一步
	public static String NEW_USER_INFO = HTTP_TITLE + "check_iwo?type=reg_pass&user_name={0}&nick_name={1}&user_pass={2}&active_code={3}";
	public static String NEW_USER_SEND_CODE = HTTP_TITLE + "check_iwo?type=reg_activate&mobile={0}&checkType={1}";// 重新发送验证码
	// public static String NEW_USER_REG_GO = TITLE +
	// "check_iwo?type=send_code&mobile={0}&sys=1"; // 重发验证码
	// public static String NEW_USER_REG_GO = TITLE +
	// "check_validate?type=reg_go&mobile={0}&sys={1}";
	public static String NEW_USER_REG_GO = TITLE + "check_iwo?type=send_code&mobile={0}&sys={1}";
	//新添加的发送验证码
	public static String NEW_USER_REG_GO_ZHUC = TITLE + "check_iwo?type=reg_go&mobile={0}&sys={1}";
	// public static String NEW_USER_REG = HTTP_TITLE +
	// "check_iwo?type=reg&reg_mobile={0}"; //用户注册下一步 发短信
	public static String NEW_USER_REG = HTTP_TITLE + "check_validate?type=reg&reg_mobile={0}&nick_name={1}&user_pass={2}";

	public static String NEW_USER_TREATY = HTTP_TITLE + "check_iwo?type=treaty";
	// public static String NEW_USER_FIND_CODE = HTTP_TITLE +
	// "check_iwo?type=find_pwd&mobile={0}";
	public static String NEW_USER_FIND_CODE = HTTP_TITLE + "check_validate?type=find_pwd&mobile={0}&sign={1}";
	// public static String NEW_USER_FIND_CHECK_CODE = HTTP_TITLE +
	// "check_iwo?type=find_yzm&mobile={0}&check_code={1}";//找回密码验证码验证
	public static String NEW_USER_FIND_CHECK_CODE = HTTP_TITLE + "check_validate?type=find_yzm&mobile={0}&check_code={1}";
	public static String NEW_USER_REG_OK = HTTP_TITLE + "check_iwo?type=reg_ok&mobile={0}";
	public static String NEW_USER_FRIENDS_LIST = HTTP_TITLE + "check_iwo?type=get_user_guan&scope={0}&start=1&count=10";
	public static String NEW_GET_DICT_LIST = TITLE + "check_iwo?type=get_dict_list&dict_type={0}";
	public static String NEW_GET_CITY_LIST = TITLE + "check_iwo?type=get_city_list&dict_type={0}";
	public static String NEW_DAYGROUP_CREATEUSER = HTTP_TITLE + "check_iwo?type=daygroup_createuser&gid={0}&user_ids={1}";
	public static String NEW_DAYGROUP_CREATE = HTTP_TITLE + "check_iwo?type=daygroup_create&group_name={0}&cate_id={1}&city_id={2}&area_id={3}&user_ids={4}";
	public static String NEW_GROUP_LIST = HTTP_TITLE + "check_iwo?type=group_list&mark={0}";
	public static String NEW_DAYGROUP_VIEW = HTTP_TITLE + "check_iwo?type=daygroup_view&gid={0}";
	public static String NEW_GROUP_MESSAGE = HTTP_TITLE + "check_iwo?type=group_message&gid={0}";
	public static String NEW_GROUP_DELETE = HTTP_TITLE + "check_iwo?type=group_delete&gid={0}";
	public static String NEW_USER_FIND = HTTP_TITLE + "check_iwo?type=user_find&mobile={0}";
	public static String NEW_ADD_ANGEL = HTTP_TITLE + "check_iwo?type=get_user_relation&user_b={0}";
	public static String NEW_REG_PERFECT = HTTP_TITLE + "check_iwo?type=reg_perfect&birthday={0}&sex={1}&city_id={2}&user_trade={3}&user_job={4}&sign={5}&mark={6}";
	public static String NEW_DAYGROUP_UPDATE = HTTP_TITLE + "check_iwo?type=daygroup_update&img={0}&gid={1}";
	public static String NEW_RELATION_LIST = HTTP_TITLE + "check_iwo?type=get_user_relation_list";
	public static String NEW_CREATE_GROUP = HTTP_TITLE + "check_iwo?type=create_group&up_count={0}&is_hide={1}&join_conditions={2}";
	public static String NEW_NEARBY_USER = HTTP_TITLE + "nearby_user?age_range={0}&time={1}&sex={2}&p={3}&n={4}";
	public static String NEW_LON_LAT = HTTP_TITLE + "check_iwo?type=lon_lat&lon={0}&lat={1}";
	public static String NEW_SET_ISBLACK = HTTP_TITLE + "check_iwo?type=set_isblack&user_id={0}&is_add={1}";
	public static String NEW_DELE_FRIEND = HTTP_TITLE + "check_iwo?type=dele_friend&user_id_b={0}";
	public static String NEW_GROUP_INFO = HTTP_TITLE + "check_iwo?type=group_info&id={0}";
	public static String NEW_GET_FRIEND_GUAN = HTTP_TITLE + "check_iwo?type=get_friend_guan&user_id={0}&scope={1}&start=1&count=1000000";
	public static String NEW_GROUP_INVITEUSER = HTTP_TITLE + "check_iwo?type=group_invite_user&group_id={0}&user_id={1}";
	public static String NEW_GROUP_ACCEPT_NEWS = HTTP_TITLE + "check_iwo?type=group_accept_news&group_id={0}&status={1}";
	public static String NEW_GROUP_HIDE = HTTP_TITLE + "check_iwo?type=group_hide&master_id={0}&group_id={1}&is_hide={2}";
	public static String NEW_UPDATE_GROUP = HTTP_TITLE
			+ "check_iwo?type=update_group&id={0}&master_id={1}&group_name={2}&group_img={3}&group_desc={4}&group_tag={5}&addr_tag={6}&up_count={7}&is_hide={8}&join_conditions={9}";
	public static String NEW_NEARBY_GROUP = HTTP_TITLE + "nearby_group?p={0}&n={1}&key={2}";
	public static String NEW_GROUP_USER_MANAGE = HTTP_TITLE + "check_iwo?type=group_user_manage&group_id={0}&user_id={1}&tag=3";
	public static String NEW_GROUP_USER_REQUEST = HTTP_TITLE + "check_iwo?type=group_user_request&group_id={0}&user_id={1}";
	public static String NEW_GROUP_USER_CONFIRM = HTTP_TITLE + "check_iwo?type=group_user_confirm&group_id={0}&user_confirm={1}";
	public static String NEW_GROUP_MANAGER_CONFIRM = HTTP_TITLE + "check_iwo?type=group_manager_confirm&group_confirm={0}&user_id={1}&group_id={2}";
	public static String NEW_DEL_FRIEND_GROUP = HTTP_TITLE + "check_iwo?type=del_friend_group&id={0}";
	public static String NEW_DEL_GROUP_FRIEND = HTTP_TITLE + "check_iwo?type=del_group_friend&id={0}";

	public static String NEW_SEND = TITLE + "message/new_send?base64=1&to={0}&type={1}&content={2}&video_id={3}&is_mark={4}&is_mode={5}";
	public static String NEW_GROUP_SEND = TITLE + "message/group_send?group_id={0}&type={1}&content={2}&video_id={3}&is_mark={4}&is_mode={5}";

	public static String NEW_DOWNLOAD = HTTP_TITLE + "check_iwo?type=download";

	/**
	 * 轮播图
	 */
	public static String MED_TOP_IMAGE = HTTP_TITLE + "check_iwo?type=get_ad&ad_code=102&count=5&token=no";
	/**
	 * 频道
	 */
	public static String TV_CH_LIST = HTTP_TITLE + "check_iwo?type=get_recommend&ch_id={0}&count=3&token={1}";
	public static String TV_CH_GALLERY = HTTP_TITLE + "check_iwo?type=get_ad&ad_code={0}&ad_sys=1&count=10&token={1}";
	public static String TV_BRAND_DETAIL = HTTP_TITLE + "check_iwo?type=get_brand_detail&brand_id={0}&token={1}";
	public static String TV_BRAND_LEST = HTTP_TITLE + "check_iwo?type=get_brand&ch_id={0}&p={1}&n={2}&k={3}&token={4}";
	public static String TV_SEARCH_TVLIST = HTTP_TITLE + "check_iwo?type=s_video&ch_id={0}&order=1&p={1}&n=10&brand_id={2}&token={3}";
	public static String TV_BUSS_LIST = HTTP_TITLE + "check_iwo?type=s_agency&n=10&p={0}&brand_id={1}&token={2}";
	public static String TV_MEDIA_DETAIL = HTTP_TITLE + "check_iwo?type=get_video&id={0}&token={1}";
	public static String TV_MEDIA_SEARCH = HTTP_TITLE + "check_iwo?type=s_video_a&order=1&ch_id={0}&p={1}&n=10&k={2}&token={3}";
	public static String TV_SEARCH_CAR_PAR = HTTP_TITLE + "check_iwo?type=get_car_detail&car_id={0}&token={1}";
	public static String TV_CAR_IMG = HTTP_TITLE + "check_iwo?type=get_carimg&series_id={0}&n={1}&p=0&token={2}";
	public static String TV_PINGLUN_LIST = HTTP_TITLE + "check_iwo?type=get_pinglun&video_id={0}&ch_id={1}&p={2}&n={3}&token={4}";
	public static String TV_BUSS_LIST_2 = HTTP_TITLE + "check_iwo?type=s_agency&n=10&p={0}&car_id={1}&token={2}";
	public static String TV_BRAND_BY_ID = HTTP_TITLE + "check_iwo?type=get_agency&id={0}&token={1}";
	public static String TV_BRAND_IMG_BY_ID = HTTP_TITLE + "check_iwo?type=get_agencyimg&agency_id={0}&p=0&n=0&token={1}";
	public static String TV_BRAND_CAR_ID = HTTP_TITLE + "check_iwo?type=get_agencycar&agency_id={0}&p={1}&n=10&token={2}";
	public static String TV_BRAND_SEND_INF = HTTP_TITLE
			+ "check_iwo?type=car_yuyue&user_name={0}&user_email={1}&user_tel={2}&address={3}&buy_time={4}&agency_id={5}&agency_name={6}&token={7}";
	public static String TV_REAL_IMG = HTTP_TITLE + "check_iwo?type=get_estateimg&estate_id={0}&n={1}&p=0&img_type={2}&token={3}";
	public static String TV_REAL_PEOPLE = HTTP_TITLE + "check_iwo?type=get_estateseller&estate_id={0}&token={1}";
	public static String TV_REAL_BY_ID = HTTP_TITLE + "check_iwo?type=get_estate&id={0}&token={1}";
	public static String TV_GET_LP_MEDIA = HTTP_TITLE + "check_iwo?type=s_video&ch_id={0}&order=1&p=0&n=10&estate_id={1}&token={2}";
	public static String TV_YOUHUI_DETAIL = HTTP_TITLE + "check_iwo?type=get_estatecoup&estate_id={0}&token={1}";
	public static String TV_HUIYUAN_LIST = HTTP_TITLE + "check_iwo?type=get_videomember&ch_id={0}&video_id={1}&p=0&n={2}&token={3}";
	public static String TV_IS_HUIYUAN = HTTP_TITLE + "check_iwo?type=isvideomember&video_id={0}&uid={1}&up={2}&token={3}&ch_id={4}";
	public static String TV_ADD_HUIYUAN = HTTP_TITLE + "check_iwo?type=getvideomemberadd&ch_id={0}&video_id={1}&uid={2}&up={3}&token={4}";
	public static String TV_NEAR_PEOPLE = HTTP_TITLE + "check_iwo?type=s_around&latlng={0}&ch_id={1}&token={2}";

	public static String TV_VIDEO_IS_SAVE = HTTP_TITLE + "check_iwo?type=iscang&vid={0}&uid={1}&up={2}&token={3}";
	public static String TV_VIDEO_SAVE = HTTP_TITLE + "check_iwo?type=cang&id={0}&play_url={1}&uid={2}&up={3}&token={4}";
	public static String TV_VIDEO_PL = HTTP_TITLE + "check_iwo?type=pinglun&video_id={0}&ch_id={1}&content={2}&uid={3}&up={4}&token={5}";
	public static String TV_VIDEO_IS_PL = HTTP_TITLE + "check_iwo?type=isvideomember&video_id={0}&uid={1}&up={2}&token={3}";
	public static String TV_VIDEO_CANCLE_PL = HTTP_TITLE + "check_iwo?type=getvideomemberadd&video_id={0}&ch_id={1}&uid={2}&up={3}&token={4}";
	public static String TV_VIDEO_SHARE_ADD_1 = HTTP_TITLE + "check_iwo?type=share&id={0}&token={1}";
	public static String TV_VIDEO_PLAY_ADD_1 = HTTP_TITLE + "check_iwo?type=play&id={0}&token={1}";
	public static String ES_SHARE_ADD = HTTP_TITLE + "check_iwo?type=share&id={0}&token={1}";
	public static String TV_VIDEO_DOWNLOAD = HTTP_TITLE + "check_iwo?type=download"; // v/check_iwo?type=download
	
	/**
	 * 好友相关
	 */
	public static String TV_PEO_NEAR = HTTP_TITLE + "check_iwo?type=s_around&latlng={0}&ch_id={1}&token={2}";
	public static String FR_OWNER_INF = HTTP_TITLE + "user/info?type=owner&uid={0}&up={1}&token={2}";
	public static String FR_NUREAD_MSG = HTTP_TITLE + "message/mtotalcount?base64=1&uid={0}&up={1}&token={2}";
	public static String FR_GET_FRIENT = HTTP_TITLE + "user/info?type=myfriends&scope={0}&start={1}&count={2}";
	public static String FR_USER_GUANZHU = HTTP_TITLE + "user/info?type=userguan&user_id={0}&is_add={1}&uid={2}&up={3}&token={4}";
	public static String FR_GET_MESSAGE_LIST = HTTP_TITLE + "message/mcount?base64=1";
	public static String FR_GET_HIS_MSG_LIST = HTTP_TITLE + "message/read_his?base64=1&start=0&count={0}&to={1}";
	public static String FR_GET_NEAR_MSG_LIST = HTTP_TITLE + "message/read?base64=1&start=0&count=100&to={0}&uid={1}&up={2}&token={3}";
	public static String FR_SEND_MSG = HTTP_TITLE + "message/send?base64=1&to={0}&type={1}&content={2}&uid={3}&up={4}&token={5}";
	public static String FR_GET_OTHER_MSG = HTTP_TITLE + "message/receive?base64=1&from={0}";
	public static String FR_GETPHOTO_LIST = HTTP_TITLE + "user/info?type=myalbum&start=0&count=100&uid={0}&up={1}&token={2}";
	public static String FR_GETPHOTO_IMG_LIST = HTTP_TITLE + "user/info?type=myphoto&album_id=1&start={0}&count={1}&uid={2}&up={3}&token={4}";
	public static String FR_CREATE_PHOTO = HTTP_TITLE + "user/info?type=albumadd&album_name={0}&album_desc={1}&open_type={2}&uid={3}&up={4}&token={5}";
	public static String FR_GET_NEAR_LIST = HTTP_TITLE + "user/getaround?&latlng={0}&count=10&p={1}&sex={2}&uid={3}&up={4}&token={5}";
	public static String FR_SEARCH_LIST = HTTP_TITLE + "user/getsquare_user?count={0}&p={1}&key={2}&uid={3}&up={4}&token={5}";
	public static String FR_MY_VIDEO_LIST = HTTP_TITLE + "user/info?type=myvideo&start={0}&count={1}&user_id={2}";
	public static String FR_USER_VIDEO_LIST = HTTP_TITLE + "user/info?type=uservideo&start={0}&count={1}&user_id={2}&uid={3}&up={4}&token={5}";
	public static String FR_GET_USER_FRIENT = HTTP_TITLE + "user/info?type=userfriends&scope={0}&start={1}&count={2}&user_id={3}&uid={4}&up={5}&token={6}";
	public static String FR_USER_DATA_INF = HTTP_TITLE + "user/info?type=other&user_id={0}&uid={1}&up={2}&token={3}";
	public static String FR_FRIEND_R_LIST = HTTP_TITLE + "user/info?type=myvideo&start={0}&count={1}&user_id={2}";
	public static String FR_MY_USER_INF = HTTP_TITLE + "user/info?type=userfriendship&user_id={0}&is_add=1&uid={1}&up={2}&token";
	public static String FR_GET_SAVE_LIST = HTTP_TITLE + "user/get_shoucang?&page={0}&count={1}&uid={2}&up={3}&token={4}";
	public static String FR_EDIT_PASSWORD = HTTP_TITLE + "user/my_pwd?";

	public static String FR_SELECT = HTTP_TITLE + "check_iwo?type=get_dict&dict_id=101&ch_id={0}&token={1}";
	public static String FR_GET_PICTURE = HTTP_TITLE + "estate/check_iwo?type=get_photo_ios&n=10&p={0}&uid={1}&up={2}&token={3}";
	public static String FR_SEND_USER_INF_VIDEO = HTTP_TITLE + "user/upload_video_my?&token=";
	//url:/share/check_validate?type=share_friends&token
	public static String SHARE_SEND_TEN_FRIEND = HTTP_TITLE +"check_validate?type=share_friends&videoId={0}&mobiles={1}&shareCon={2}";
	//public static String SHARE_SEND_TEN_FRIEND ="http://192.168.0.165:8080/share/check_validate?type=share_friends&videoId={0}&mobiles={1}&shareCon={2}";
	/**
	 * 上传文件接口
	 */
	public static String SEND_HEAD_IMG = TITLE + "upload_album?";
	public static String SEND_IMG_CUT_TMP = TITLE + "img_cut_tmp?url={0}";
	public static String SEND_UP_VIDEO = HTTP_TITLE + "user/upload_video?&open_type={0}&name={1}&video_desc={2}&uid={3}&up={4}&token={5}";
	public static String SEND_GET_IMG_LIST = HTTP_TITLE + "user/info?type=myphoto&album_id={0}&start={1}&count={2}&uid={3}&up={4}&token={5}";
	public static String SEND_SAVE_IMG = HTTP_TITLE
			+ "user/info?type=photosadd&album_id={0}&photo_name={1}&photo_desc={2}&photo_url_b={3}&photo_url={4}&photo_url_s={5}&uid={6}&up={7}&token={8}";
	public static String SEND_SAVE_AGAIN_IMG = HTTP_TITLE + "user/photo_save?&photo_ids={0}&photo_name={1}&photo_desc={2}&album_id={3}&uid={4}&up={5}&token={6}";
	public static String MAG_LOGIN = HTTP_TITLE + "check_iwo?type=login&mobile={0}&pass={1}";
	public static String MAG_REGIST = HTTP_TITLE + "check_iwo?type=reg&reg_mobile={0}&reg_nick={1}&reg_pass={2}";
	public static String MAG_GET_CODE = HTTP_TITLE + "check_iwo?type=find_pwd&mobile={0}";
	public static String MAG_GET_ACTIVE_CODE = HTTP_TITLE + "check_iwo?type=reg_ok&reg_mobile={0}&reg_nick={1}&reg_pass={2}&active_code={3}";
	public static String MAG_GET_SEARCH_CODE = HTTP_TITLE + "check_iwo?type=find_yzm&mobile={0}&check_code={1}";
	// public static String MAG_GET_NEW_PASSWORD = HTTP_TITLE +
	// "check_iwo?type=find_reset&mobile={0}&new_pwd={1}&new_pwd2={2}";//重新设计密码
	public static String MAG_GET_NEW_PASSWORD = HTTP_TITLE + "check_validate?type=find_reset&mobile={0}&new_pwd={1}";
	public static String MAG_SHARE = "http://iwoshare.com/share?uuid=123456&wid=000000&backurl={0}&id={1}&un={2}&content={3}";
	public static String MAG_AGAIN_CODE = HTTP_TITLE + "check_iwo?type=send_code&mobile={0}";
	public static String MAG_WELCOME_IMAGE = HTTP_TITLE + "check_iwo?type=channelad&chid=103&num=1";
	public static String MAG_SHARE_NUM_ADD = HTTP_TITLE + "check_iwo?type=art_share&art_id={0}";
	// public static String MAG_MAG_SEND = HTTP_TITLE +
	// "check_iwo?type=message_save&deviceToken={0}&version={1}&ios=android&device=2&soft=1";
	public static String MAG_SEND_TEL = HTTP_TITLE + "check_iwo?type=userphone&uname={0}&uphone={1}&uid={2}&token={3}&soft=iwomag";

	public static String SEND_UPLOAD_ALBUM = HTTP_TITLE + "check_iwo?type=get_version"; // upload_album
	/**
	 * 用户相关
	 */
	// public static String NEW_FR_GET_USER_UPDATE_PASSWORD = TITLE +
	// "check_iwo?type=my_pwd&old_pwd={0}&new_pwd={1}&confirm_pwd={2}";//修改密码
	public static String NEW_FR_GET_USER_UPDATE_PASSWORD = HTTP_TITLE + "check_validate?type=my_pwd&old_pwd={0}&new_pwd={1}&confirm_pwd={2}";
	// public static String NEW_MY_INFO = HTTP_TITLE +
	// "check_iwo?type=my_info";//获取登录用户信息
	public static String NEW_MY_INFO = HTTP_TITLE + "check_validate?type=get_user_info";
	public static String NEW_FR_N_GET_USER_INF = HTTP_TITLE + "check_iwo?type=my_info&user_id={0}";// 夜晚用户的信息
	public static String NEW_EDIT_USER_STATUS = HTTP_TITLE + "check_iwo?type=edit_user_status&user_status={0}";// 设置用户在线状态
	public static String NEW_FR_GET_FRIEND_INFO = HTTP_TITLE + "check_iwo?type=friend_info&user_b={0}&user_name={1}&mark=1";// 获取好友详细信息
	public static String NEW_FR_GET_CONTACTS = HTTP_TITLE + "check_iwo?type=get_contacts&user_id={0}";// 获取好友人脉
	public static String NEW_V_BACKGROUND_SAVE = HTTP_TITLE + "check_iwo?type=background_save&url={0}";// 修改背景图片background_save
	// public static String NEW_FR_USER_INFO_EDIT = HTTP_TITLE
	// +
	// "check_iwo?type=info_edit&sex={0}&age={1}&city_id={2}&sign={3}&user_profession={4}&user_company={5}&user_school={6}&user_fond={7}&area_id={8}&user_trade={9}&user_job={10}";
	// 保存资料
	public static String NEW_FR_USER_INFO_EDIT = HTTP_TITLE + "check_validate?type=user_info_edit&nick_name={0}&sex={1}";
	public static String NEW_FR_USER_INFO_EDIT_SIGN = HTTP_TITLE + "check_validate?type=user_info_edit&nick_name={0}&sex={1}";
	public static String NEW_FR_USER_INFO_NIGHT = HTTP_TITLE + "check_iwo?type=info_edit&sex={0}&age={1}&sign={2}";

	public static String NEW_FR_USER_INFO_NIGHT_EDIT = HTTP_TITLE + "check_iwo?type=info_edit&sex={0}&nick_name={1}&head_img={2}&edit_mark=1";

	public static String NEW_FR_USER_INFO_NIGHT_GET_HEAD = HTTP_TITLE + "check_iwo?type=get_head"; // check_iwo?type=get_head
	// 我的主页 浏览记录
	public static String NEW_FR_USER_INFO_NIGHT_GET_HEAD_HISTORY = HTTP_TITLE + "check_validate?type=play_list&n={0}&p={1}";
	// http://wap.iwo.mokacn.com/share/check_validate?type=play_list&n=10&p=1
	public static String NEW_FR_USER_INFO_NIGHT_DELETE_HEAD_HISTORY =HTTP_TITLE + "check_validate?type=play_delete&ids={0}";
	/**
	 * 视频相关 id
	 */
	public static String NEW_V_VIDEO_DETAIL = TITLE + "check_iwo?type=get_video&sys=6&video_id={0}";// 视频详情页接口
	public static String NEW_V_VIDEO_PUBLISH_COMMENTS = TITLE + "check_iwo?type=pinglun_add&video_id={0}&ch_id=6&content={1}";// 视频发表评论
	public static String NEW_V_VIDEO_COMMENTS_LIST = TITLE + "check_iwo?type=get_pinglun&video_id={0}&p={1}&n={2}&ch_id=6"; // 视频评论列表
	public static String NEW_V_VIDEO_COMMENTS_LIST_DAY = TITLE + "check_iwo?type=getLatelyComment&video_id={0}"; // 视频今日评论																											// &token
	public static String NEW_V_VIDEO_MY_LIST = HTTP_TITLE + "check_iwo?type=get_user_video&p={0}&n={1}";// 获取我的视频信息
	public static String NEW_V_GET_FRIENDS_VIDEO = HTTP_TITLE + "check_iwo?type=get_friends_video&p={0}&n={1}&sys=1";// 白天模式获取朋友圈视频列表

	public static String NEW_V_VIDEO_MY_SEARCH = HTTP_TITLE + "share_iwo?type=get_share_list&p={0}&n={1}";// 获取我追过的视频信息
	public static String NEW_V_VIDEO_MY_SEARCH_PLAY = HTTP_TITLE + "check_validate?type=is_play_video&videoId={0}&ch_id={1}";//判断视频播放
	public static String NEW_V_VIDEO_MY_SEARCH_PLAY_NEW = HTTP_TITLE + "check_validate?type=is_play_video&videoId={0}";//判断视频播放ch_id 为空

	// public static String FR_FEED_BACK = TITLE +
	// "check_iwo?type=opinion&feed_content={0}&sys=1";// 意见反馈
	public static String FR_FEED_BACK = HTTP_TITLE + "check_validate?type=suggest_feedback&content={0}";
//get_channel_video
	public static String NEW_V_GET_VIDEO_LIST = TITLE + "check_iwo?type=get_zt_video&ch_id={0}&p={1}&n={2}";// 获取视频列表
	public static String NEW_V_GET_VIDEO_LIST_NEW = HTTP_TITLE + "check_validate?type=get_channel_zt&ch_id={0}";// 新获取视频列表
	public static String NEW_V_GET_VIDEO_LIST_NEW_SERIES  = TITLE + "check_iwo?type=get_video_series&sys=6&video_id={0}";// 详情集剧
	public static String NEW_V_GET_VIDEO_LIST_NEW_BRAND_PAGE = HTTP_TITLE + "check_validate?type=get_channel_video&ch_id=115";//合作商界面
	public static String NEW_V_GET_VIDEO_LIST_NEW_BRAND = HTTP_TITLE + "check_validate?type=get_brand_channel_video&ch_id={0}";// 新获取视频品牌列表
	public static String NEW_V_GET_VIDEO_LIST_BRAND = HTTP_TITLE + "check_validate?type=get_brand_channels&ch_id=123";// 首页品牌单独数据
	public static String NEW_V_GET_VIDEO_LIST_BRAND_DE = TITLE + "check_iwo?type=get_channel_video&ch_id={0}&p={1}&n={2}";// 首页品牌里面的列表
	public static String NEW_V_GET_VIDEO_LIST_BRAND_DE_MORE = HTTP_TITLE + "check_validate?type=get_area_channel_video&ch_id={0}&p={1}&n={2}&area_id={3}";// 首页more里面的列表
	public static String NEW_V_SET_SHARE_PLAY = TITLE + "check_iwo?type=set_share_play&id={0}&op_type={1}";// 视频详情页统计，分享，或评论加一。
																											// 1是分享，2是播放。

	public static String CAR_UPLOAD_VIDEO = TITLE + "upload_video?name={0}&video_desc={1}&up={2}&uid={3}&sys=1&mark={4}&topic_id={5}&soft=video_share";
	public static String VIDEO_SET_SHARE = HTTP_TITLE + "check_iwo?type=set_share&share_mark={0}&video_id={1}";//
	/**
	 * 好友相关
	 */

	public static String NEW_V_SET_BLACKLIST = HTTP_TITLE + "check_iwo?type=set_isblack&user_id={0}&is_add={1}";// 设置好友黑名单
	public static String NEW_V_GET_FRIEND_VIDEO_LIST = HTTP_TITLE + "check_iwo?type=get_user_video&user_id={0}&p={1}&n={2}";// 获取好友的视频信息
	public static String NEW_V_DELETE_FRIEND = HTTP_TITLE + "check_iwo?type=dele_friend&user_id_b={0}";// 删除好友
	public static String NEW_V_SET_NICKNAME_STAR = HTTP_TITLE + "check_iwo?type=set_notename&sys={0}&mobile={1}&notename={2}";// 删除好友

	public static String NEW_V_EDIT_GUAN = HTTP_TITLE + "check_iwo?type=edit_guan&user_id={0}&is_add={1}";// 设置关注或取消关注。is_add
																											// 1添加关注，2，移除关注。
	public static String NEW_V_ADD_ANGEL = HTTP_TITLE + "check_iwo?type=add_angel&user_id_a={0}&user_id_b={1}";// 黑夜模式设置为天使
	public static String NEW_V_GET_CITY_LIST = HTTP_TITLE + "check_iwo?type=get_city_list&dict_type=101";// 获取城市列表
	// public static String FR_CHANGE_IMG =
	// "http://wap.kazhu365.com/user/background_save?&src={0}&uid={1}&up={2}&token={3}";
	public static String NEW_BACKGROUND_SAVE = HTTP_TITLE + "check_iwo?type=background_save&src={0}";// 设置背景图片列表
	public static String FR_GET_XT_BG_IMG = "http://wap.kazhu365.com/user/info?type=getback_imgs";
	public static String NEW_GET_RELATION = HTTP_TITLE + "check_iwo?type=get_relation&user_id={0}&is_add={1}";// 当前登录用户与该用户的关系
	//导航图
	public static String NEW_DAO_HANG_PIN_DAOSTRING=TITLE+"check_iwo?type=get_children_channel&pid={0}&p={1}&n={2}";
	public static String NEW_DAO_HANG_PIN_DAOSTRING_ZI=TITLE+"check_iwo?type=get_channel_video&ch_id={0}&p={1}&n={2}";
	/**
	 * 跳转页面设置的常量值 一级界面,0x000 二级 界面,0x000000 三级界面,0x000000000
	 */
	public static int REQUEST_GETBGIMG = 0x000001;// GetBgImg
	public static int RESULT_FRIENDDETAIL_SETBLACKLIST = 0x000002;// FriendDetail-添加好友为黑名单返回参数
	public static int REQUEST_FRIENDS_LIST_ACTIVITY = 0x000003;// 黑夜模式好友列表请求参数
	public static String REQUEST_MY_COMMENT="check_iwo?type=get_video_comments&p={0}&n={1}";//我的评论
	public static String REQUEST_DETAIL_COMMENT_ZAN=HTTP_TITLE+"check_validate?type=add_praise&id={0}";//详情页评论加赞

	public static String GetUrl(Context context, String str) {
	//	return PreferenceUtil.getString(context, Constants.GET_APP_URL, "http://wap.iwo.mokacn.com:8080/") + str;
		// return PreferenceUtil.getString(context, Constants.GET_APP_URL,
		// "http://wap.kazhu365.com/") + str;
		// return "http://192.168.0.127:8086/" + str;
		// return "http://192.168.0.66:8080/" + str;
		 return "http://www.iwotv.cn/"+str;


	}
}
