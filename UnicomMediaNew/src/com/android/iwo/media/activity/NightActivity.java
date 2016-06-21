package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iwo.media.action.ActivityUtil;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.chat.XmppClient;
import com.android.iwo.media.night.chat.XmppNightClient;
import com.android.iwo.media.view.XListView;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.DateUtil;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class NightActivity extends BaseActivity implements OnClickListener {
	private RelativeLayout select;
	private String sex = "0", age = "3", age_min = "0", age_max = "100", time_btn = "10", time = "" + 60 * 24 * 365;
	private TextView btn[];
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private HashMap<String, String> UserData = new HashMap<String, String>();
	private IwoAdapter adapter;
	protected boolean hasMore = true;
	protected boolean isloading = true;
	private boolean re = false;
	private final int NIGHT_REQUEST_NIGHTDIALOG = 292342;
	private final int NIGHT_REQUEST_NIGHTHOMEPAGE = 05071053;
	XListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nightmodel);
		PreferenceUtil.setString(mContext, "video_model", "night");
		setPubParame();

		init();
		ActivityUtil.getInstance().deleteAll(this.getClass().getSimpleName());
		if (!StringUtil.isEmpty(getUid()))
			new Thread(new Runnable() {
				public void run() {
					if (!XmppNightClient.getInstance(mContext).isLogin())
						XmppNightClient.getInstance(mContext).login(mContext, getUserTel());
				}
			}).start();
		updataAddress();
		Intent intent = new Intent(NightActivity.this, NightDialogActivity.class);
		startActivityForResult(intent, NIGHT_REQUEST_NIGHTDIALOG);
		// selectDialog();
	}

	@Override
	protected void onStart() {
		super.onStart();
		PreferenceUtil.setString(mContext, "video_model", "night");
	}

	private void init() {
		setNightTitle("陌生人");
		// initAddress();
		select = (RelativeLayout) findViewById(R.id.select);
		ImageView left = (ImageView) findViewById(R.id.left_img);
		ImageView right = (ImageView) findViewById(R.id.right_img);
		left.setVisibility(View.VISIBLE);
		right.setVisibility(View.VISIBLE);
		BitmapUtil.setImageResource(left, R.drawable.bianshen);
		BitmapUtil.setImageResource(right, R.drawable.shaixuan);
		left.setOnClickListener(this);
		right.setOnClickListener(this);
		int[] ids = { R.id.sex1, R.id.sex2, R.id.sex3, R.id.age1, R.id.age2, R.id.age3, R.id.age4, R.id.time1, R.id.time2, R.id.time3,
				R.id.time4, R.id.ok, R.id.cancle_ };
		btn = new TextView[ids.length];
		for (int i = 0; i < ids.length; i++) {
			btn[i] = (TextView) findViewById(ids[i]);
			btn[i].setOnClickListener(this);
		}

		listView = (XListView) findViewById(R.id.list);
		listView.setDividerHeight(0);
		listView.setSelector(new ColorDrawable(0x00000000));
		adapter = new IwoAdapter(this, mData) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null)
					v = mInflater.inflate(R.layout.list_n_near_item, parent, false);
				HashMap<String, String> map = mData.get(position);
				if (StringUtil.isEmpty(map.get("age")) || "0".equals(map.get("age"))) {
					setItem(v, R.id.age, "保密");
				} else
					setItem(v, R.id.age, map.get("age"));

				setItem(v, R.id.sign, map.get("sign"));
				ImageView sex = (ImageView) v.findViewById(R.id.sex);
				if ("2".equals(map.get("sex"))) {
					sex.setBackgroundResource(R.drawable.girl);
				} else {
					sex.setBackgroundResource(R.drawable.boy);
				}
				LinearLayout time_layout = (LinearLayout) v.findViewById(R.id.time_layout);
				if (position == 0) {
					time_layout.setVisibility(View.GONE);
				} else {
					time_layout.setVisibility(View.VISIBLE);
				}
				if (!StringUtil.isEmpty(map.get("last_time"))) {
					long time = Long.valueOf(map.get("last_time"));
					setItem(v, R.id.address, map.get("dis") + " | ");
					setItem(v, R.id.time, DateUtil.subDate(time));
				} else {
					setItem(v, R.id.address, map.get("dis"));
				}
				setItem(v, R.id.name, map.get("nick_name"));
				setImageView(v, R.id.head, map.get("head_img"));
				return v;
			}
		};
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int f, int v, int t) {
				Logger.i("hasmore " + hasMore + " is :" + isloading + "-" + f + "-" + v + "-" + t);
				if (hasMore && !isloading && (f + v == t)) {
					new GetData().execute();
				}
			}
		});

		listView.setXListViewListener(new XListView.IXListViewListener() {

			@Override
			public void onRefresh() {
				re = true;
				hasMore = true;
				new GetData().execute();
			}
		});
		listView.setOnItemClickListener(new AbsListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position = position - listView.getHeaderViewsCount();
				HashMap<String, String> map = mData.get(position);
				if (position == 0) {
					Intent intent = new Intent(mContext, MyHomePageNightActivity.class);
					startActivityForResult(intent, NIGHT_REQUEST_NIGHTHOMEPAGE);
				} else {
					Logger.i("map" + map);
					Intent intent = new Intent(mContext, NightChat.class);
					intent.putExtra("userID", map.get("user_name"));
					intent.putExtra("head_img", map.get("head_img"));
					intent.putExtra("name", map.get("nick_name"));
					intent.putExtra("send_name", UserData.get("nick_name"));
					intent.putExtra("send_head", UserData.get("head_img"));
					startActivity(intent);
				}
			}
		});

		Logger.i("----" + sex + "---" + age + "---" + time_btn);
		setBtn(Integer.valueOf(sex));
		// setBtn(Integer.valueOf(age));
		setBtn(Integer.valueOf(time_btn));

		new GetData().execute();
	}

	@Override
	public void onBackPressed() {
		if (select.getVisibility() == View.VISIBLE)
			select.setVisibility(View.GONE);
		else
			exitBy2Click();
	}

	private boolean doubdle_exit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (doubdle_exit == false) {
			doubdle_exit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					doubdle_exit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
		} else {

			new Thread(new Runnable() {
				public void run() {
					if (!StringUtil.isEmpty(getUid()))
						if (!XmppClient.getInstance(mContext).isLogin())
							XmppClient.getInstance(mContext).login(getPre("user_name"));
					XmppNightClient.getInstance(mContext).colseConn();
				}
			}).start();

			PreferenceUtil.setString(this, "video_model", "day");
			System.exit(0);
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == NIGHT_REQUEST_NIGHTDIALOG && resultCode == RESULT_OK) {
			UserData.clear();
			UserData.put("sex", data.getStringExtra("sex"));
			UserData.put("head_img", data.getStringExtra("user_head_img"));
			UserData.put("nick_name", data.getStringExtra("user_night"));
			if (mData.size() > 0) {
				mData.add(0, UserData);
			} else {
				mData.add(UserData);
			}
			adapter.notifyDataSetChanged();
		} else if (requestCode == NIGHT_REQUEST_NIGHTHOMEPAGE && resultCode == RESULT_OK) {
			Logger.i("data = " + data.getStringExtra("sex"));
			UserData.put("sex", data.getStringExtra("sex"));
			UserData.put("head_img", data.getStringExtra("user_head_img"));
			UserData.put("nick_name", data.getStringExtra("user_night"));
			UserData.put("sign", data.getStringExtra("sign"));
			UserData.put("age", data.getStringExtra("age"));
			if (mData.size() > 0) {
				mData.add(0, UserData);
			} else {
				mData.add(UserData);
			}
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_img:
			if (StringUtil.isEmpty(getPre("dayloginset"))) {
				// PreferenceUtil.setString(mContext, "video_model", "night");
				Intent intent = new Intent(mContext, InfoEdit.class);
				intent.putExtra("model", "day");
				startActivity(intent);
			} else {
				PreferenceUtil.setString(mContext, "video_model", "day");
				Intent intent = new Intent(mContext, ModelActivity.class);
				intent.putExtra("type", "yes");
				overridePendingTransition(R.anim.outanim, R.anim.outanim);
				finish();
				startActivity(intent);
			}
			break;
		case R.id.right_img:
			if (select.getVisibility() == View.VISIBLE) {
				select.setVisibility(View.GONE);
			} else {
				select.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.ok:
			re = true;
			hasMore = true;
			new GetData().execute();
			select.setVisibility(View.GONE);
			break;
		case R.id.cancle_:
			select.setVisibility(View.GONE);
			break;
		case R.id.age1:
			setBtn(3);
			break;
		case R.id.age2:
			setBtn(4);
			break;
		case R.id.age3:
			setBtn(5);
			break;
		case R.id.age4:
			setBtn(6);
			break;
		case R.id.sex1:
			setBtn(0);
			break;
		case R.id.sex2:
			setBtn(1);
			break;
		case R.id.sex3:
			setBtn(2);
			break;
		case R.id.time1:
			setBtn(7);
			break;
		case R.id.time2:
			setBtn(8);
			break;
		case R.id.time3:
			setBtn(9);
			break;
		case R.id.time4:
			setBtn(10);
			break;
		default:
			break;
		}
	}

	/**
	 * 选择年龄
	 */
	private void setBtn(int n) {
		Logger.i("---n = " + n);
		switch (n) {
		case 0:
			sex = "0";
			break;
		case 1:
			sex = "1";
			break;
		case 2:
			sex = "2";
			break;
		case 3:
			age = "" + n;
			age_min = "0";
			age_max = "22";
			break;
		case 4:
			age = "" + n;
			age_min = "23";
			age_max = "30";
			break;
		case 5:
			age_min = "31";
			age_max = "45";
			break;
		case 6:
			age = "" + n;
			age_min = "45";
			age_max = "100";
			break;
		case 7:
			time_btn = "" + n;
			time = 15 + "";
			break;
		case 8:
			time_btn = "" + n;
			time = 3 * 60 + "";
			break;
		case 9:
			time_btn = "" + n;
			time = 24 * 60 + "";
			break;
		case 10:
			time_btn = "" + n;
			time = 10 * 365 * 24 * 60 + "";
			break;
		}

		setBtnStyle(n);
	}

	private void setBtnStyle(int n) {

		int i = 0, j = 0;
		if (n < 3) {
			j = 2;
		} else if (n >= 3 && n <= 6) {
			i = 3;
			j = 6;
		} else if (n > 6) {
			i = 7;
			j = 10;
		}

		for (; i <= j; i++) {
			btn[i].setTextColor(0xff000000);
			btn[i].setBackgroundResource(R.drawable.fangxing_);
		}
		btn[n].setTextColor(0xffff2fa0);
		btn[n].setBackgroundResource(R.drawable.fangxing);
	}

	private class GetData extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected void onPreExecute() {
			isloading = true;
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
			if (re) {
				return DataRequest.getArrayListFromUrl_Base64(getUrl(AppConfig.VIDEO_USER_NEARBY), age_min, age_max, time, sex, "0");
			} else {
				return DataRequest.getArrayListFromUrl_Base64(getUrl(AppConfig.VIDEO_USER_NEARBY), age_min, age_max, time, sex,
						getStart(mData.size() - 1));
			}
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (re)
				mData.clear();
			if (mData.size() == 0 && UserData != null && UserData.size()>0)
				mData.add(UserData);
			if (result != null) {
				ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				String id = getPre("n_user_id");
				for (HashMap<String, String> map : result) {
					if (!map.get("id").equals(id)) {
						list.add(map);
					}
				}
				mData.addAll(list);
				if (result.size() < 10)
					hasMore = false;
			} else {
				hasMore = false;
			}
			adapter.notifyDataSetChanged();
			re = false;
			listView.stopRefresh();
			isloading = false;
		}
	}
}
