package com.test.iwomag.android.pubblico.util;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class BitmapCache {

	private ArrayList<Bitmap> VALUE = null;

	public BitmapCache() {
		VALUE = new ArrayList<Bitmap>();
	}

	public void add(Bitmap value) {
		if (value != null) {
			VALUE.add(value);
		}
	}

	public void remove() {
		for (Bitmap bit : VALUE) {
			if (bit != null && !bit.isRecycled()) {
				bit.recycle();
				bit = null;
			}
		}
		VALUE = null;
	}
}
