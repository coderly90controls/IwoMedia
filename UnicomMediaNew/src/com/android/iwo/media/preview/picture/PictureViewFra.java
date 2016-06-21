package com.android.iwo.media.preview.picture;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iwo.media.action.ChatUtils;
import com.android.iwo.media.activity.newPasswordActivity;
import com.android.iwo.media.lenovo.R;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class PictureViewFra extends Fragment implements OnClickListener {
	private PicGallery gallery;
	private Activity activity;
	private GalleryAdapter mAdapter;
	private RelativeLayout layout;
	private TextView img_browse_title_text;
	private ArrayList<String> imgData = new ArrayList<String>();// 图片本地地址保存。
	private int n = 0; // 当前选中了那个条目
	private ChatUtils chatUtils;
	HashMap<String, String> map;

	public GalleryAdapter getAdapter() {
		return mAdapter;
	}

	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.picture_view, null);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		float scale = getResources().getDisplayMetrics().density;
		chatUtils = new ChatUtils(activity, scale, dm);
		layout = (RelativeLayout) view.findViewById(R.id.img_browse_title_layout);
		gallery = (PicGallery) view.findViewById(R.id.pic_gallery);
		gallery.setVerticalFadingEdgeEnabled(false);// 取消竖直渐变边框
		gallery.setHorizontalFadingEdgeEnabled(false);// 取消水平渐变边框
		gallery.setDetector(new GestureDetector(getActivity(), new MySimpleGesture()));
		img_browse_title_text = (TextView) view.findViewById(R.id.img_browse_title_text);
		mAdapter = new GalleryAdapter(getActivity());
		gallery.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				try {
					PictureViewFra.this.view.findViewById(R.id.camer).setVisibility(View.VISIBLE);
				} catch (Error e) {
					Logger.info(e.toString());
				}
				return false;
			}
		});
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				img_browse_title_text.setText(position + 1 + "/" + gallery.getCount());
				map.put("richbody", imgData.get(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});

		view.findViewById(R.id.img_browse_title_image).setOnClickListener(this);
		view.findViewById(R.id.img_browse_title_share).setOnClickListener(this);
		view.findViewById(R.id.nor).setOnClickListener(this);
		view.findViewById(R.id.model).setOnClickListener(this);
		view.findViewById(R.id.cancle).setOnClickListener(this);

	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.setData(imgData);
		gallery.setAdapter(mAdapter);
		gallery.setSelection(n);

	}

	private class MySimpleGesture extends SimpleOnGestureListener {
		// 按两下的第二下Touch down时触发
		public boolean onDoubleTap(MotionEvent e) {

			View view = gallery.getSelectedView();
			if (view instanceof MyImageView) {
				MyImageView imageView = (MyImageView) view;
				if (imageView.getScale() > imageView.getMiniZoom()) {
					imageView.zoomTo(imageView.getMiniZoom());
				} else {
					imageView.zoomTo(imageView.getMaxZoom());
				}

			} else {

			}
			return true;
		}

		@SuppressLint("NewApi")
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// 单击时触发
			setSystemUiVisibility(getSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility()));
			View camer = view.findViewById(R.id.camer);
			if (camer.getVisibility() == View.VISIBLE) {
				camer.setVisibility(View.GONE);
			}
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			View camer1 = view.findViewById(R.id.camer);
			if (camer1.getVisibility() == View.VISIBLE) {
				camer1.setVisibility(View.GONE);
			}
			return super.onScroll(e1, e2, distanceX, distanceY);

		}
	}

	private boolean getSystemUiVisibility(int visibility) {
		if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
			return true;
		}
		return false;

	}

	@SuppressLint("NewApi")
	private void setSystemUiVisibility(boolean visibility) {

		if (visibility) {
			layout.setVisibility(View.GONE);
			activity.getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);

		} else {
			activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			layout.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		imgData.clear();
		Intent intent = activity.getIntent();

		imgData.addAll(intent.getStringArrayListExtra("data"));
		n = intent.getIntExtra("n", 0);
		map = (HashMap<String, String>) intent.getSerializableExtra("map");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_browse_title_image:
			activity.finish();
			break;
		case R.id.img_browse_title_share:
			view.findViewById(R.id.camer).setVisibility(View.VISIBLE);
			break;
		case R.id.nor:// 发送给好友
			chatUtils.chatSend(map);
			break;
		case R.id.model:// 保存到本地
			chatUtils.chatSave(map);
			break;
		case R.id.cancle:// 取消
			view.findViewById(R.id.camer).setVisibility(View.GONE);
			break;
		default:
			break;
		}

	}

}
