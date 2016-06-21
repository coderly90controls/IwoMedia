package com.android.iwo.media.action;

import java.io.File;
import com.android.iwo.media.apk.activity.R;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class ViewMe extends ViewTab {
	private TextView textname;
	private TextView textphone;
	private ImageView textsex;
	private LinearLayout camel;
	private String uid, up, mark = "2";
	private String path;

	public ViewMe(Context context) {
		mContext = context;
		view = View.inflate(mContext, R.layout.view_me_new, null);
	}

	@Override
	protected void init() {
		String headString = getPre("head_img");
		ImageView head = (ImageView) view.findViewById(R.id.me_yonghu_head);
		Logger.i("头像地址：" + headString);
		if (StringUtil.isEmpty(headString)) {
			head.setImageResource(R.drawable.user_edit);
		} else {
			LoadBitmap.getIntence().loadImage(headString, head);

		}
		textname = (TextView) view.findViewById(R.id.me_layout_textname);
		if (!StringUtil.isEmpty(getPre("nick_name"))) {
			textname.setText(getPre("nick_name"));
		}
		textphone = (TextView) view.findViewById(R.id.me_layout_user_phone);
		if (!StringUtil.isEmpty(getPre("user_name"))) {
			textphone.setText(getPre("user_name"));
		}
		textsex = (ImageView) view.findViewById(R.id.me_layout_sex);
		if (!StringUtil.isEmpty(getPre("sex"))) {
			if ("1".equals(getPre("sex"))) {
				// textsex.setText("男");
				textsex.setBackground(mContext.getResources().getDrawable(
						R.drawable.man));
			} else {
				// textsex.setText("女");
				textsex.setBackground(mContext.getResources().getDrawable(
						R.drawable.woman));
			}
		}
		camel = (LinearLayout) view.findViewById(R.id.camer);
		view.findViewById(R.id.me_yonghu_head).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						camel.setVisibility(View.VISIBLE);
						setCamel();
					}
				});

	}

	protected void setCamel() {
		LinearLayout btn1 = (LinearLayout) view.findViewById(R.id.nor);
		LinearLayout btn2 = (LinearLayout) view.findViewById(R.id.model);
		LinearLayout btn3 = (LinearLayout) view.findViewById(R.id.cancle);
		TextView btn_text1 = (TextView) view.findViewById(R.id.nor_text);
		TextView btn_text2 = (TextView) view.findViewById(R.id.model_text);
		btn_text1.setText("拍摄");
		btn_text2.setText("选择本地");
		uid = PreferenceUtil.getString(mContext, "user_id", "");
		up = PreferenceUtil.getString(mContext, "user_name", "");
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.nor: // 照片拍摄
					Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					Uri uri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), "xiaoma.jpg"));
					path = uri.getPath();
					intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					((Activity) mContext).startActivityForResult(intent2, 2);

					camel.setVisibility(View.GONE);

					break;
				case R.id.model:// 图库选择
					Intent intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
					((Activity) mContext).startActivityForResult(intent, 111);
					camel.setVisibility(View.GONE);
					break;
				case R.id.cancle:
					camel.setVisibility(View.GONE);
					break;
				}
			}
		};
		btn1.setOnClickListener(listener);
		btn2.setOnClickListener(listener);
		btn3.setOnClickListener(listener);
	}

	@Override
	public View getView() {
		return view;
	}

	public void setText() {
		if (!StringUtil.isEmpty(getPre("nick_name"))) {
			textname.setText(getPre("nick_name"));
		}
		if (!StringUtil.isEmpty(getPre("user_name"))) {
			textphone.setText(getPre("user_name"));
		}
		if (!StringUtil.isEmpty(getPre("sex"))) {
			if ("1".equals(getPre("sex"))) {
				// textsex.setText("男");
				textsex.setBackground(mContext.getResources().getDrawable(
						R.drawable.man));
			} else {
				textsex.setBackground(mContext.getResources().getDrawable(
						R.drawable.woman));
				// textsex.setText("女");
			}
		}
	}

}
