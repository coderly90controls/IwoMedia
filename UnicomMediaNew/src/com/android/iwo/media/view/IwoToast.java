package com.android.iwo.media.view;

import com.android.iwo.media.apk.activity.*;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class IwoToast extends Toast {
	private static Toast toast;

	public IwoToast(Context context) {
		super(context);
	}

	public static Toast makeText(Context context, CharSequence text) {
		if (toast == null) {
			toast = new Toast(context);
		}

		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflate.inflate(R.layout.toast_layout, null);
		TextView tv = (TextView) v.findViewById(R.id.text);
		tv.setText(text);
		toast.setView(v);
		toast.setDuration(LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		return toast;
	}

	public static Toast makeText(Context context, CharSequence text, int duration) {
		if (toast == null) {
			toast = new Toast(context);
		}

		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflate.inflate(R.layout.toast_layout, null);
		TextView tv = (TextView) v.findViewById(R.id.text);
		tv.setText(text);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(v);
		toast.setDuration(duration);
		return toast;
	}

	public static Toast makeText(Context context, int resId, int duration) throws Resources.NotFoundException {
		return makeText(context, context.getResources().getText(resId), duration);
	}

}
