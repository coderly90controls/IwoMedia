package com.android.iwo.media.view;

import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.Logger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

public class IwoDialog extends Dialog {

	private TextView del, title;
	private OnClickListener listener;
	private String del_tex, title_tex;
	private Context mContext;
	
	public IwoDialog(Context context) {
		super(context, R.style.loading_dialog);
		mContext = context;
		Logger.i("IwoDialog");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Logger.i("onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_layout);
		del = (TextView)findViewById(R.id.del);
		title = (TextView)findViewById(R.id.name);
		del.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(listener != null)
					listener.click(IwoDialog.this);
			}
		});
		del.setText(del_tex);
		this.title.setText(title_tex);
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		float scale = mContext.getResources().getDisplayMetrics().density;
		int width = (dm.widthPixels - (int) (100 * scale + 0.5f));
		android.view.ViewGroup.LayoutParams params = title.getLayoutParams();
		if (params != null) {
			params.width = width;
		}
	}
	
	public void setText(String title, String deltex){
		Logger.i("setText");
		del_tex = deltex;
		title_tex = title;
	}
	
	public void setOnClickListener(OnClickListener listener){
		if(listener != null)
			this.listener = listener;
	}
	public interface OnClickListener{
		public void click(Dialog dialog);
	}
}
