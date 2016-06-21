package com.android.iwo.media.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.BadTokenException;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * @Description: 自定义对话
 */
public class CommonDialog {

	private static Dialog dialog;
	// private ImageView loadding;
	private String mCon = "";
	private View v;

	public CommonDialog(Context context) {
		dialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
		createLoadingDialog(context);
	}

	public CommonDialog(Context context, String msg) {
		mCon = msg;
		dialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
		createLoadingDialog(context);
	}

	public CommonDialog(Context context, OnClickListener clickListener, int layout, int[] clikViews, int editViewID) {
		dialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
		createEditTextDialog(context, clickListener, layout, clikViews, editViewID);
	}

	public CommonDialog(Context context, OnClickListener clickListener, int layout, int[] clikViews, int editViewID, String str) {
		dialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
		createEditTextDialog(context, clickListener, layout, clikViews, editViewID, str);
	}

	public CommonDialog(Context context, OnClickListener clickListener, int layout, int[] clikViews, int editViewID, String str, String type) {
		dialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
		createEditTextDialog(context, clickListener, layout, clikViews, editViewID, str, type);
	}

	/**
	 * 创建有EditText的弹框
	 * 
	 * @param context
	 * @param clickListener
	 * @param layout
	 * @param clikViews
	 * @param editViewID
	 */
	public void createEditTextDialog(Context context, OnClickListener clickListener, int layout, int[] clikViews, int editViewID) {
		LayoutInflater inflater = LayoutInflater.from(context);
		v = inflater.inflate(layout, null);// 得到加载view
		// main.xml中的ImageView
		EditText tipTextView = (EditText) v.findViewById(editViewID);// 提示文字
		for (int i : clikViews) {
			View itemView = v.findViewById(i);
			itemView.setTag(tipTextView);
			itemView.setOnClickListener(clickListener);
		}
		dialog.setCancelable(true);// 不可以用“返回键”取消
		dialog.setContentView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
	}

	public void createEditTextDialog(Context context, OnClickListener clickListener, int layout, int[] clikViews, int editViewID, String str) {
		createEditTextDialog(context, clickListener, layout, clikViews, editViewID, str, null);
	}

	public void createEditTextDialog(Context context, OnClickListener clickListener, int layout, int[] clikViews, int editViewID, String str, String type) {
		LayoutInflater inflater = LayoutInflater.from(context);
		v = inflater.inflate(layout, null);// 得到加载view
		// main.xml中的ImageView
		EditText tipTextView = (EditText) v.findViewById(editViewID);// 提示文字
		if (type == null) {
			if (!StringUtil.isEmpty(str)) {
				tipTextView.setText(str);
			}
		} else {
			if (!StringUtil.isEmpty(str)) {
				tipTextView.setHint(str);
			}

		}

		for (int i : clikViews) {
			View itemView = v.findViewById(i);
			itemView.setTag(tipTextView);
			itemView.setOnClickListener(clickListener);
		}
		dialog.setCancelable(true);// 不可以用“返回键”取消
		dialog.setContentView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
	}

	/**
	 * 
	 * 创建一个有点击按钮的弹框
	 * 
	 * @param context
	 *            上下文
	 * @param msg
	 *            提示信息
	 * @param clickListener
	 *            点击监听
	 * @param layout
	 *            要加载的布局
	 * @param clikView
	 *            需要点击的 ViewID数组
	 * @param textView
	 *            需要显示提示信息的 textviewID
	 * 
	 */
	public CommonDialog(Context context, String msg, OnClickListener clickListener, int layout, int[] clikViews, int textViewID) {
		mCon = msg;
		dialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
		createLoadingDialog(context, clickListener, layout, clikViews, msg, textViewID);
	}

	public void createLoadingDialog(Context context, OnClickListener clickListener, int layout, int[] clikViews, String msg, int textViewID) {
		LayoutInflater inflater = LayoutInflater.from(context);
		v = inflater.inflate(layout, null);// 得到加载view
		TextView tipTextView = (TextView) v.findViewById(textViewID);// 提示文字
		tipTextView.setText(msg);// 设置加载信息
		boolean mode = true;
		if ("day".equals(getMode(context))) {
			mode = true;
		} else if ("night".equals(getMode(context))) {
			mode = false;
		}
		for (int i : clikViews) {
			if (!mode) {
				v.findViewById(i).setBackgroundColor(context.getResources().getColor(R.color.comm_pink_color));
			}
			v.findViewById(i).setOnClickListener(clickListener);
		}
		dialog.setCancelable(true);// 不可以用“返回键”取消
		dialog.setContentView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
	}

	public void createLoadingDialog(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
		// main.xml中的ImageView
		ImageView loadding = (ImageView) v.findViewById(R.id.img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字

		tipTextView.setText(mCon);// 设置加载信息
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
		// 使用ImageView显示动画
		loadding.startAnimation(hyperspaceJumpAnimation);
		dialog.setCancelable(true);// 不可以用“返回键”取消
		dialog.setContentView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
	}

	public void setMessage(String msg) {
		mCon = msg;
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
		tipTextView.setText(mCon);// 设置加载信息
	}

	public void show() {
		try {
			if (dialog != null) {
				dialog.show();
			}
		} catch (BadTokenException e) {
			this.dismiss();
		} catch (IllegalStateException e1) {
			this.dismiss();
		} catch (Exception e) {
			this.dismiss();
		}
	}

	public void dismiss() {
		try {
			if (dialog != null)
				dialog.dismiss();
		} catch (BadTokenException e) {
			this.dismiss();
		} catch (IllegalStateException e1) {
			this.dismiss();
		}
	}

	protected String getMode(Context mContext) {
		return PreferenceUtil.getString(mContext, "video_model", "");
	}

}
