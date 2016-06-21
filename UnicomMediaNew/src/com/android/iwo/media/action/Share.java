package com.android.iwo.media.action;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.iwo.media.lenovo.R;
import com.android.iwo.share.ShareActivity;
import com.android.iwo.share.Sina;
import com.android.iwo.share.Tencent;
import com.android.iwo.share.WeiXin;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class Share implements OnClickListener {

	private Activity mContent;
	private String mUrl = "", mCon = "", iwoShareType = "", VideoID;
	private boolean iwoShareShow = false, iwoShareOnClik = true;
	private boolean iscancel = false;
	public static boolean onAnimation = true;// 取消朋友圈分享
	public static boolean onAnimationDisappear = true;// 取消朋友圈分享

	Bitmap bitmap;

	public Share(Activity content) {
		mContent = content;
		init();
	}

	public boolean getIsCancle() {
		return iscancel;
	}

	public Share(Activity content, Bitmap bitmap) {
		mContent = content;
		this.bitmap = bitmap;
		init();
	}

	public void setCon(String con) {
		if (StringUtil.isEmpty(con) && con.length() > 108)
			mCon = con.substring(0, 108);
		else
			mCon = con;
	}

	public void setUrl(String url) {
		mUrl = url;
	}

	public void setIwoShareType(String iwoShareType) {
		this.iwoShareType = iwoShareType;
	}

	public void setIwoShareShow(Boolean iwoShareShow) {
		this.iwoShareShow = iwoShareShow;
	}

	public void setShareVideoID(String VideoID) {
		this.VideoID = VideoID;
	}

	private void init() {
		LinearLayout ten = (LinearLayout) mContent.findViewById(R.id.ten);
		ten.setOnClickListener(this);
		LinearLayout sina = (LinearLayout) mContent.findViewById(R.id.sina);
		sina.setOnClickListener(this);
		LinearLayout weixin_friend = (LinearLayout) mContent.findViewById(R.id.weixin_friend);
		weixin_friend.setOnClickListener(this);
		LinearLayout weixin = (LinearLayout) mContent.findViewById(R.id.weixin);
		weixin.setOnClickListener(this);
		Button cancle = (Button) mContent.findViewById(R.id.back);
		cancle.setOnClickListener(this);
		LinearLayout iwo_share = (LinearLayout) mContent.findViewById(R.id.iwo_share);
		iwo_share.setOnClickListener(this);
		LinearLayout sms_share = (LinearLayout) mContent.findViewById(R.id.sms_share);
		sms_share.setOnClickListener(this);

	}

	public void setShare() {
		showLogin(false);
	}

	public void closeShare() {
		if (isAutho()) {
			WebView autho = (WebView) mContent.findViewById(R.id.author_web);
			autho.setVisibility(View.GONE);
			return;
		}
		showLogin(true);
	}

	public boolean isAutho() {
		WebView autho = (WebView) mContent.findViewById(R.id.author_web);
		return autho.getVisibility() == View.VISIBLE;
	}

	public boolean isHsow() {
		RelativeLayout btn_layout = (RelativeLayout) mContent.findViewById(R.id.share_layout);
		return btn_layout.getVisibility() == View.VISIBLE;
	}

	public void closeHsow() {
		WebView web_layout = (WebView) mContent.findViewById(R.id.author_web);
		web_layout.setVisibility(View.GONE);
		LinearLayout btn_layout = (LinearLayout) mContent.findViewById(R.id.share_btn_layout);
		btn_layout.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		LinearLayout layout = (LinearLayout) mContent.findViewById(R.id.share_btn_layout);
		switch (v.getId()) {
		case R.id.ten:
			new Tencent().share(mContent, "", mCon);
			setShareAdd();
			break;
		case R.id.sina:
			new Sina().share(mContent, "", mCon);
			setShareAdd();
			break;
		case R.id.weixin_friend:
			if (bitmap == null) {
				new WeiXin().initView(mContent, true, "", mCon);
				setShareAdd();
			} else {
				new WeiXin().initView(mContent, true, "", mCon, bitmap);
				setShareAdd();
			}
			break;
		case R.id.weixin:
			if (bitmap == null) {
				new WeiXin().initView(mContent, false, "", mCon);
				setShareAdd();
			} else {
				new WeiXin().initView(mContent, false, "", mCon, bitmap);
				setShareAdd();
			}
			break;
		case R.id.back:
			showLogin(true);
			break;
		case R.id.iwo_share:
			setShareAdd();
			if (iwoShareShow) {
				if (iwoShareOnClik) {
					iwoShareOnClik = false;

					new GetUserData().execute(iwoShareType, VideoID);
					return;
				}
			} else {
				Intent intent = new Intent(mContent, ShareActivity.class);
				intent.putExtra("id", VideoID);
				mContent.startActivity(intent);
			}

			break;
		case R.id.sms_share:
			Intent intent = new Intent(mContent, ShareActivity.class);
			intent.putExtra("id", VideoID);
			mContent.startActivity(intent);
			break;

		default:
			break;
		}
		layout.setVisibility(View.GONE);

		/*
		 * if(!isAutho()) showLogin(true);
		 */
	}

	public void showLogin(boolean in) {
		final RelativeLayout btn_layout = (RelativeLayout) mContent.findViewById(R.id.share_layout);
		LinearLayout layout = (LinearLayout) mContent.findViewById(R.id.share_btn_layout);
		LinearLayout iwo_share = (LinearLayout) mContent.findViewById(R.id.iwo_share);
		LinearLayout sms_share = (LinearLayout) mContent.findViewById(R.id.sms_share);
		if (iwoShareShow) {
			iwo_share.setVisibility(View.VISIBLE);
			sms_share.setVisibility(View.VISIBLE);
			setIwoShareLaout();
		} else {
			iwo_share.setVisibility(View.VISIBLE);
			setMSMShareLaout();
			sms_share.setVisibility(View.INVISIBLE);
		}

		DisplayMetrics dm;
		dm = new DisplayMetrics();
		mContent.getWindowManager().getDefaultDisplay().getMetrics(dm);
		float scale = mContent.getResources().getDisplayMetrics().density;
		int height = (int) (dm.heightPixels * scale);

		TranslateAnimation animation = null;
		if (in) {
			animation = new TranslateAnimation(0, 0, 0, height);
			animation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					onAnimation = true;
					onAnimationDisappear = false;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					onAnimation = false;
					onAnimationDisappear = false;
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					onAnimation = true;
					onAnimationDisappear = true;
					btn_layout.setVisibility(View.GONE);
				}
			});
		} else {
			layout.setVisibility(View.VISIBLE);
			btn_layout.setVisibility(View.VISIBLE);
			animation = new TranslateAnimation(0, 0, height, 0);
		}

		animation.setDuration(800);
		btn_layout.startAnimation(animation);
	}

	private void setShareAdd() {
		if (shareAnimationListener != null) {
			shareAnimationListener.animationCompleted();
		}

	}

	// 获得视频信息
	private class GetUserData extends AsyncTask<String, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(String... params) {

			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.VIDEO_SET_SHARE), params[0], params[1]);

		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				if ("1".equals(result.get("code"))) {
					if ("0".equals(iwoShareType)) {
						iwoShareType = "1";
					} else if ("1".equals(iwoShareType)) {
						iwoShareType = "0";
					}
					setIwoShareLaout();
				}
			} else {

			}
			iwoShareOnClik = true;
			iscancel = true;
		}
	}

	private void setIwoShareLaout() {
		// ImageView iwo_share_img = (ImageView)
		// mContent.findViewById(R.id.iwo_share_img);
		TextView iwo_share_text = (TextView) mContent.findViewById(R.id.iwo_share_text);
		if (!StringUtil.isEmpty(iwoShareType) && "0".equals(iwoShareType)) {// 0取消分享，1分享
			iwo_share_text.setText("取消分享朋友圈");
		} else {
			iwo_share_text.setText("爱握朋友圈");
		}
	}

	private void setMSMShareLaout() {
		ImageView iwo_share_img = (ImageView) mContent.findViewById(R.id.iwo_share_img);
		iwo_share_img.setBackgroundResource(R.drawable.ico_fxaw);
		TextView iwo_share_text = (TextView) mContent.findViewById(R.id.iwo_share_text);
		iwo_share_text.setText("爱握分享");
	}

	protected String getUrl(String url) {
		return AppConfig.GetUrl(mContent, url);
	}

	private ShareAnimationListener shareAnimationListener;

	public interface ShareAnimationListener {
		void animationCompleted();

	}

	public void setShareAnimationListener(ShareAnimationListener shareAnimationListener) {
		this.shareAnimationListener = shareAnimationListener;
	}
}
