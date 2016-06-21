package com.android.iwo.media.dao;

import android.R.integer;

public interface DownloadProgressListener {
	public void onDownloadSize(int size,int downloadNetworkSpeed);
}
