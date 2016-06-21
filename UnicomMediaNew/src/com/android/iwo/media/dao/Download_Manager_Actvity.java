package com.android.iwo.media.dao;

import com.android.iwo.media.apk.activity.R;
import com.test.iwomag.android.pubblico.util.Logger;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("ResourceAsColor")
public class Download_Manager_Actvity extends FragmentActivity implements OnClickListener {
	private TextView loading_button;
	private TextView loadfinsh_button;
	private Button back_title_button;
	private DownloadFinsh_Fragment downloadfinsh_Fragment;
	private FragmentManager manager;
	private DownloadIng_Fragment downloadIng_Fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.acitivity_download_manage);

		// 获得Fragment管理类
		manager = getSupportFragmentManager();
		// FragmentTransaction对fragment进行添加,移除,替换,以及执行其他动作。
		FragmentTransaction transaction = manager.beginTransaction();

		back_title_button = (Button) findViewById(R.id.back_title_button);
		loading_button = (TextView) findViewById(R.id.loading_button);
		loadfinsh_button = (TextView) findViewById(R.id.loadfinsh_button);
		back_title_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		downloadIng_Fragment = new DownloadIng_Fragment();
		downloadfinsh_Fragment = new DownloadFinsh_Fragment();
		// 添加一个Fragment，这是一个事务
		transaction.add(R.id.fragment_container, downloadIng_Fragment);
		// 要给activity应用事务, 必须调用 commit().
		transaction.commit();
		loading_button.setOnClickListener(this);
		loadfinsh_button.setOnClickListener(this);

	}

	private FragmentTransaction transaction;

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loading_button: {
			Logger.i("事舞正在下载");
			// 开启事物管理
			loading_button.setBackgroundResource(R.drawable.me_b_z);
			loadfinsh_button.setBackgroundResource(R.drawable.me_b_w);
			transaction = manager.beginTransaction();
			if (downloadfinsh_Fragment == null) {
				downloadfinsh_Fragment = new DownloadFinsh_Fragment();
			}

			downloadfinsh_Fragment.setCheckNum(0);

			transaction.replace(R.id.fragment_container, downloadIng_Fragment);
			// 提交事务
			transaction.commit();
			break;
		}

		case R.id.loadfinsh_button: {
			Logger.i("事舞下载完成");
			loading_button.setBackgroundResource(R.drawable.me_b_w);
			loadfinsh_button.setBackgroundResource(R.drawable.me_b_z);
			transaction = manager.beginTransaction();
			if (downloadIng_Fragment == null) {
				downloadIng_Fragment = new DownloadIng_Fragment();
			}
			downloadfinsh_Fragment.setCheckNum(0);
			transaction.replace(R.id.fragment_container, downloadfinsh_Fragment);
			transaction.commit();
			break;
		}
		}

	}

}
