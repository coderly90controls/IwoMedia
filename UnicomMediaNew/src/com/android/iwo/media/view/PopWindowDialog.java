package com.android.iwo.media.view;

import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.Logger;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PopWindowDialog extends PopupWindow implements OnClickListener{
	
	private Context mContext ;
	private View view;
	private OnViewClickListener mListener;
	private float width = 0;
	
	public PopWindowDialog(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	private void init() {
		view = LayoutInflater.from(mContext).inflate(R.layout.chat_view_dialog, null);
		setContentView(view);
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);

		setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0xff0000);
		setBackgroundDrawable(dw);
		
	}
	
	public float getViewWidth(){
		Logger.i("宽：" + width);
		return width;
	}
	
	public void setView(String... value){
		TextView tab1 = (TextView)view.findViewById(R.id.tab1);
		TextView tab2 = (TextView)view.findViewById(R.id.tab2);
		TextView tab3 = (TextView)view.findViewById(R.id.tab3);
		
		View line1 = view.findViewById(R.id.line1);
		View line2 = view.findViewById(R.id.line2);
		tab1.setVisibility(View.GONE);
		tab2.setVisibility(View.GONE);
		tab3.setVisibility(View.GONE);
		
		line1.setVisibility(View.GONE);
		line2.setVisibility(View.GONE);
		
		float size = 15.3f;
		if(value.length == 1){
			tab1.setVisibility(View.VISIBLE);
			tab1.setText(value[0]);
			tab1.setOnClickListener(this);
			width = 20*2*1 + value[0].length()*size;
		}else if(value.length == 2){
			tab1.setVisibility(View.VISIBLE);
			tab2.setVisibility(View.VISIBLE);
			tab1.setText(value[0]);
			tab2.setText(value[1]);
			line1.setVisibility(View.VISIBLE);
			tab1.setOnClickListener(this);
			tab2.setOnClickListener(this);
			width = 20*2*2 + value[0].length()*size + value[1].length()*size;
		}else if(value.length == 3){
			tab1.setVisibility(View.VISIBLE);
			tab2.setVisibility(View.VISIBLE);
			tab3.setVisibility(View.VISIBLE);
			tab1.setText(value[0]);
			tab2.setText(value[1]);
			tab3.setText(value[2]);
			line1.setVisibility(View.VISIBLE);
			line2.setVisibility(View.VISIBLE);
			tab1.setOnClickListener(this);
			tab2.setOnClickListener(this);
			tab3.setOnClickListener(this);
			width = 20*2*3 + value[0].length()*size + value[1].length()*size + value[2].length()*size;
		}
	}

	public void setArrow(int w){
		ImageView dia = (ImageView)view.findViewById(R.id.dialog_);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);  // , 1是可选写的
		lp.setMargins(w, -1, 0, 0); 
		dia.setLayoutParams(lp);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab1:
			if(mListener != null) mListener.onClickLeft();
			break;
		case R.id.tab2:
			if(mListener != null) mListener.onClickCenter();
			break;
		case R.id.tab3:
			if(mListener != null) mListener.onClickRight();
			break;
		default:
			break;
		}
		dismiss();
	}
	
	public void setOnViewClickListener(OnViewClickListener listener){
		mListener = listener;
	}
	
	/**
	 * 视图点击事件
	 * @author abc
	 *
	 */
	public interface OnViewClickListener{
		/**
		 * 点击左边的按钮
		 */
		public void onClickLeft();
		/**
		 * 点击中间的按钮
		 */
		public void onClickCenter();
		/**
		 * 点击邮编的按钮
		 */
		public void onClickRight();
	}
}
