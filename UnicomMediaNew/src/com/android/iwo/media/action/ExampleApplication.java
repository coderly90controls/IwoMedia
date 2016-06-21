package com.android.iwo.media.action;

import com.test.iwomag.android.pubblico.util.Logger;

import android.app.Application;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

/**
 * For developer startup JPush SDK
 * 
 * 一般建议在自定义 Application 类里初始化。也可以在主 Activity 里。
 */
public class ExampleApplication extends Application {
    private static final String TAG = "JPush";

    @Override
    public void onCreate() {    	     
    	 Log.d(TAG, "[ExampleApplication] onCreate..视频分享0025");
    	 Logger.i("[ExampleApplication] onCreate..视频分享");
         super.onCreate();
         JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
         JPushInterface.init(this);      		// 初始化 JPush
    }
}
