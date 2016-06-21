package com.android.iwo.media.preview.picture;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;

public class GalleryAdapter extends BaseAdapter {

	private Context context;

	private ArrayList<MyImageView> imageViews = new ArrayList<MyImageView>();

	private List<String> mItems;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Bitmap bitmap = (Bitmap) msg.obj;
			Bundle bundle = msg.getData();
			String url = bundle.getString("url");
			for (int i = 0; i < imageViews.size(); i++) {
				if (imageViews.get(i).getTag().equals(url)) {
					imageViews.get(i).setImageBitmap(bitmap);
				}
			}
		}
	};

	public void setData(List<String> data) {
		this.mItems = data;
		notifyDataSetChanged();
	}

	public GalleryAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return mItems != null ? mItems.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private HashMap<String, SoftReference<Bitmap>> mImageCache = new HashMap<String, SoftReference<Bitmap>>();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyImageView view = new MyImageView(context);
		view.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		String item = mItems.get(position);
		if (item != null) {
			Bitmap bmp;
			if (mImageCache.get("" + position) == null) {
				SoftReference<Bitmap> softBitMap = null;
				try {
					if (!StringUtil.isEmpty(item)) {
						softBitMap = new SoftReference<Bitmap>(BitmapUtil.compressImageFromFile(item));
					}
				} catch (Error e) {
					Logger.info(e.toString());
				}
				if (softBitMap != null) {
					mImageCache.put("" + position, softBitMap);
					view.setTag(item);
					if (softBitMap.get() != null) {
						view.setImageBitmap(softBitMap.get());
					}
				}

			} else {
				view.setTag(item);
				if (mImageCache.get("" + position).get() != null) {
					view.setImageBitmap(mImageCache.get("" + position).get());
				}
			}

			if (!this.imageViews.contains(view)) {
				imageViews.add(view);
			}
		}
		return view;
	}
}
