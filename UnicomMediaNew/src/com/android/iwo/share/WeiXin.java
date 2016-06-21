package com.android.iwo.share;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class WeiXin {
	// private String APP_ID = "wxde4dbc891949e762";wx81129e700d5da7e2
	private String APP_ID = "wxa9abd20d3e61cb9d";
	private IWXAPI api;
	private Context mContext;
	private String mContent;
	private String mUrl;

	public void initView(Context context, boolean friend, String u, String data) {
		mContent = data;
		mUrl = u;
		if (StringUtil.isEmpty(mContent)) {
			Toast.makeText(context, "分享的内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		mContext = context;
		api = WXAPIFactory.createWXAPI(mContext, APP_ID);
		api.registerApp(APP_ID);

		Logger.i("api -->" + api.getWXAppSupportAPI());
		setShareWeiXin(friend);
	}

	private void setShareWeiXin(boolean friend) {
		WXTextObject object = new WXTextObject();
		object.text = mContent + mUrl;

		WXMediaMessage mag = new WXMediaMessage();
		mag.mediaObject = object;
		mag.description = "微信分享";

		SendMessageToWX.Req send = new SendMessageToWX.Req();
		send.transaction = System.currentTimeMillis() + "";
		send.message = mag;
		if (friend) {// 好友
			send.scene = SendMessageToWX.Req.WXSceneSession;
		} else {
			send.scene = SendMessageToWX.Req.WXSceneTimeline;
		}
		api.sendReq(send);
	}

	public void initView(Context context, boolean friend, String u, String data, Bitmap bitmap) {
		mContent = data;

		mUrl = u + "【视频分享】";
		if (StringUtil.isEmpty(mContent)) {
			Toast.makeText(context, "分享的内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		mContext = context;

		api = WXAPIFactory.createWXAPI(mContext, APP_ID);
		api.registerApp(APP_ID);
		Logger.i("api -->" + api.getWXAppSupportAPI());
		setSharaWeiXin2Img(friend, bitmap);
	}

	/**
	 * 
	 * @param friend
	 * @param bitmap
	 */

	private void setSharaWeiXin2Img(boolean friend, Bitmap bitmap) {
		Logger.i("微信分享" + friend);
		String url = "http://www.ABC.net";// 收到分享的好友点击信息会跳转到这个地址去 ，这个要改
		WXWebpageObject localWXWebpageObject = new WXWebpageObject();
		localWXWebpageObject.webpageUrl = url;
		WXMediaMessage localWXMediaMessage = new WXMediaMessage(localWXWebpageObject);
		localWXMediaMessage.title = "爱握视频我的二维码";// 不能太长，否则微信会提示出错。不过博主没验证过具体能输入多长。
		localWXMediaMessage.description = mContent + mUrl;

		localWXMediaMessage.thumbData = getBitmapBytes(bitmap, false);

		SendMessageToWX.Req localReq = new SendMessageToWX.Req();
		if (friend) {// 好友
			localReq.scene = SendMessageToWX.Req.WXSceneSession;
		} else {
			localReq.scene = SendMessageToWX.Req.WXSceneTimeline;
		}
		localReq.transaction = System.currentTimeMillis() + "";

		localReq.message = localWXMediaMessage;
		IWXAPI api = WXAPIFactory.createWXAPI(mContext, APP_ID, true);

		api.sendReq(localReq);
	}

	/**
	 * 处理图片
	 * 
	 * @param bitmap
	 * @param paramBoolean
	 * @return
	 */
	private static byte[] getBitmapBytes(Bitmap bitmap, boolean paramBoolean) {
		Bitmap localBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
		Canvas localCanvas = new Canvas(localBitmap);
		int i;
		int j;
		if (bitmap.getHeight() > bitmap.getWidth()) {
			i = bitmap.getWidth();
			j = bitmap.getWidth();
		} else {
			i = bitmap.getHeight();
			j = bitmap.getHeight();
		}
		while (true) {
			localCanvas.drawBitmap(bitmap, new Rect(0, 0, i, j), new Rect(0, 0, 100, 100), null);
			if (paramBoolean)
				bitmap.recycle();
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localByteArrayOutputStream);
			localBitmap.recycle();
			byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
			try {
				localByteArrayOutputStream.close();
				return arrayOfByte;
			} catch (Exception e) {
			}
			i = bitmap.getHeight();
			j = bitmap.getHeight();
		}
	}

}
