package com.android.iwo.media.dao;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.iwo.media.activity.newPasswordActivity;
import com.android.iwo.media.apk.activity.R;
import com.android.iwo.media.dao.DownloadIng_Fragment.MyAdapter_Ing.ViewHolder;
import com.android.iwo.media.view.CommonDialog;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.NetworkUtil;
import com.test.iwomag.android.pubblico.util.StringUtil;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.AdapterView.OnItemClickListener;

public class DownloadIng_Fragment extends android.support.v4.app.Fragment {
	private View v;
	private TextView nothing_text;
	private ArrayList<HashMap<String, String>> arrayList;
	private ListView loading_list;
	private MyAdapter_Ing adapter;
	private MyReceiver receiver;
	private PublicUtils pu;
	private ArrayList<HashMap<String, String>> arrayList_all;

	private ViewGroup mSelectionMenuView;
	private Button selection_all;
	private Button selection_delete;
	private Boolean iskey = false;
	private Boolean ism3u8 = false;
	private Boolean isurls = false;
	private Boolean Inverse = false;
	private int checkNum = 0; // 记录选中的条目数量
	private SharedPreferences sp;
	private CommonDialog mLoadBar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pu = new PublicUtils(getActivity());
		arrayList = new ArrayList<HashMap<String, String>>();
		arrayList_all = new ArrayList<HashMap<String, String>>();
		sp = getActivity().getSharedPreferences(ConstantsDownload.share_pre_name, Context.MODE_PRIVATE);
		mLoadBar = new CommonDialog(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.downloading_fragment, container, false);

		nothing_text = (TextView) v.findViewById(R.id.nothing_text);
		adapter = new MyAdapter_Ing();

		mSelectionMenuView = (ViewGroup) v.findViewById(R.id.selection_menu);
		selection_all = (Button) v.findViewById(R.id.selection_all);
		selection_delete = (Button) v.findViewById(R.id.selection_delete);
		loading_list = (ListView) v.findViewById(R.id.loading_list);
		arrayList = DataBaseDao.getInstance(getActivity()).query_DownLoad_Ing("1", "下载中");
		arrayList_all.addAll(arrayList);
		loading_list.setAdapter(adapter);

		if (arrayList.size() == 0) {
			nothing_text.setVisibility(View.VISIBLE);
		} else {
			nothing_text.setVisibility(View.GONE);
		}

		for (int i = 0; i < arrayList_all.size(); i++) {
			adapter.getIsSelected().put(i, false);
		}

		selection_all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 全选
				if (!Inverse) {
					selection_all.setText("反选");
					// 遍历list的长度，将MyAdapter中的map值全部设为true
					for (int i = 0; i < arrayList_all.size(); i++) {
						adapter.getIsSelected().put(i, true);
					}
					setCheckNum(arrayList_all.size());
					selection_delete.setText("删除（" + getCheckNum() + "）");

					// 数量设为list的长度
					// 刷新listview和TextView的显示
					adapter.notifyDataSetChanged();
					Inverse = true;
				} else {

					// 反选
					selection_all.setText("全选");
					for (int i = 0; i < arrayList_all.size(); i++) {
						if (adapter.getIsSelected().get(i)) {
							adapter.getIsSelected().put(i, false);
							setCheckNum(getCheckNum() - 1);
						} else {
							adapter.getIsSelected().put(i, true);
							setCheckNum(getCheckNum() + 1);
						}
					}

					selection_delete.setText("删除（" + getCheckNum() + "）");
					boolean isVisible = mSelectionMenuView.getVisibility() == View.VISIBLE;

					// 显示页底浮层
					if (getCheckNum() > 0) {
						if (!isVisible) {
							mSelectionMenuView.setVisibility(View.VISIBLE);
							// mSelectionMenuView.startAnimation(AnimationUtils.loadAnimation(getActivity(),
							// R.anim.footer_appear));fcz
						}

					} else {
						if (isVisible) {
							mSelectionMenuView.setVisibility(View.GONE);
							// mSelectionMenuView.startAnimation(AnimationUtils.loadAnimation(getActivity(),
							// R.anim.footer_disappear));fcz
						}

					}

					// 刷新listview和TextView的显示
					adapter.notifyDataSetChanged();
					Inverse = false;
				}

			}

		});

		selection_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new Delete_AsyncTask().execute("");

			}
		});

		loading_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
				ViewHolder holder = (ViewHolder) arg1.getTag();
				// 改变CheckBox的状态
				holder.downloading_check.toggle();
				// 将CheckBox的选中状况记录下来
				adapter.getIsSelected().put(arg2, holder.downloading_check.isChecked());
				// 调整选定条目
				if (holder.downloading_check.isChecked() == true) {
					setCheckNum(getCheckNum() + 1);
				} else {
					setCheckNum(getCheckNum() - 1);
				}

				selection_delete.setText("删除（" + getCheckNum() + "）");
				boolean isVisible = mSelectionMenuView.getVisibility() == View.VISIBLE;

				// 显示页底浮层
				if (getCheckNum() > 0) {
					if (!isVisible) {
						mSelectionMenuView.setVisibility(View.VISIBLE);
						// mSelectionMenuView.startAnimation(AnimationUtils.loadAnimation(getActivity(),
						// R.anim.footer_appear));fcz
					}

				} else {
					if (isVisible) {
						mSelectionMenuView.setVisibility(View.GONE);
						// mSelectionMenuView.startAnimation(AnimationUtils.loadAnimation(getActivity(),
						// R.anim.footer_disappear));fcz
					}

				}

			}
		});

		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(pu.DOWNING);
		filter.addAction(pu.FINSH);
		filter.addAction(pu.FAIL);
		// 注册广播
		getActivity().registerReceiver(receiver, filter);

		return v;

	}

	private class Delete_AsyncTask extends AsyncTask<String, Integer, Boolean> {

		public Delete_AsyncTask() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean flag = false;
			publishProgress(1);

			for (int i = 0; i < arrayList_all.size(); i++) {
				HashMap<Integer, Boolean> isSelected = adapter.getIsSelected();
				Boolean boolean1 = isSelected.get(i);
				if (boolean1) {
					HashMap<String, String> hashMap = arrayList_all.get(i);
					if (hashMap != null) {
						String id = hashMap.get("id");
						String isdownloadStatus = hashMap.get("downloadStatus");
						String url_content = hashMap.get("url_content");
						// 如果删除的视频正在下载中，发送暂停广播,使下载暂停。
						if (!isdownloadStatus.equals("0")) {
							Logger.i( "删除时正在下载");
							Intent intent_pause = new Intent();
							intent_pause.setAction(pu.PAUSE);
							intent_pause.putExtra("next_id", "");// 视频id
							intent_pause.putExtra("next_urlTotal", "");// 视频章节id
							intent_pause.putExtra("next_filename", "");// 视频名字
							intent_pause.putExtra("next_url", "");// 视频播放地址
							intent_pause.putExtra("next_picture", "");// 视频的图片地址
							intent_pause.putExtra("next_foldername", "");// 视频章节名称
							intent_pause.putExtra("next_mapKey", "0");// 下载到第几个url
							intent_pause.putExtra("next_downloadedSize", "0");// 下载完成度
							intent_pause.putExtra("next_downloadSpeed", "0");// 下载文件大小
							intent_pause.putExtra("url", url_content);
							getActivity().sendBroadcast(intent_pause);
						}

						// 删除数据库
						DataBaseDao.getInstance(getActivity()).delete_item(id);

						// 删除sd卡
						// iskey =
						// FileUtil.deleteFile(ConstantsDownload.path_key + "//"
						// + hashMap.get("tid") + id + "key");
					
						if (url_content.contains(".m3u8")) {

							ism3u8 = FileUtil.deleteFile(ConstantsDownload.path_m3u8 + "//" + hashMap.get("tid") + id + ".m3u8");

							isurls = FileUtil.deleteFolder(ConstantsDownload.path_urls + hashMap.get("tid") + id);

							// 把视频总大小重新设为0
							sp.edit().putLong("allurlsize" + id, 0).commit();
						} else {
							FileService fileService = new FileService(getActivity());
							int lastIndexOf = url_content.lastIndexOf("/");
							String substring = url_content.substring(lastIndexOf);
							String content_name = ConstantsDownload.path_m3u8 + substring;
							Logger.i("删除mp4名字：" + content_name);
							fileService.delete(url_content);  //删除对应的下载记录
							ism3u8 = FileUtil.deleteFile(content_name);
						}

					}
				}
			}

			flag = true;
			return flag;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			if (values[0] == 1) {

				if (!getActivity().isFinishing()) {
					mLoadBar.setMessage("正在删除下载信息，请等待...");
					mLoadBar.show();
				}

			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (mLoadBar != null) {
				mLoadBar.dismiss();
			}

			if (result) {

				arrayList = DataBaseDao.getInstance(getActivity()).query_DownLoad_Ing("1", "下载中");
				arrayList_all.clear();
				arrayList_all.addAll(arrayList);
				// 删除后全部设为不选中状态
				setCheckNum(0);
				for (int i = 0; i < arrayList_all.size(); i++) {
					adapter.getIsSelected().put(i, false);
				}

				adapter.notifyDataSetChanged();

				boolean isVisible = mSelectionMenuView.getVisibility() == View.VISIBLE;
				if (isVisible) {
					// 隐藏页底浮层
					mSelectionMenuView.setVisibility(View.GONE);
					// mSelectionMenuView.startAnimation(AnimationUtils.loadAnimation(getActivity(),
					// R.anim.footer_disappear));fcz

				}

				if (arrayList.size() == 0) {
					nothing_text.setVisibility(View.VISIBLE);
				} else {
					nothing_text.setVisibility(View.GONE);
				}

				Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();

			} else {

			}
		}

	}

	/**
	 * 正在下载适配器
	 */
	@SuppressLint("UseSparseArrays")
	class MyAdapter_Ing extends BaseAdapter {

		private HashMap<Integer, Boolean> isSelected;

		public MyAdapter_Ing() {
			isSelected = new HashMap<Integer, Boolean>();
		}

		@Override
		public int getCount() {
			return arrayList_all.size();
		}

		@Override
		public Object getItem(int position) {
			return arrayList_all.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public HashMap<Integer, Boolean> getIsSelected() {
			return isSelected;
		}

		public void setIsSelected(HashMap<Integer, Boolean> isSelected) {
			adapter.isSelected = isSelected;
		}

		public class ViewHolder {
			TextView downloading_name;
			TextView downloading_benfenbi, downloading_benfenbi_new;
			CheckBox downloading_check;
			ImageView downloading_img;
			ProgressBar downloading_progressbar;
			Button go_on_button;
			Button pause_button;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.downloading_item, null);
				holder.downloading_check = (CheckBox) convertView.findViewById(R.id.downloading_checks);
				holder.downloading_img = (ImageView) convertView.findViewById(R.id.downloading_img);
				holder.downloading_name = (TextView) convertView.findViewById(R.id.downloading_name);
				holder.downloading_benfenbi = (TextView) convertView.findViewById(R.id.downloading_benfenbi);
				holder.downloading_benfenbi_new = (TextView) convertView.findViewById(R.id.downloading_benfenbi_new);
				holder.go_on_button = (Button) convertView.findViewById(R.id.go_on_button);
				holder.pause_button = (Button) convertView.findViewById(R.id.pause_button);
				holder.downloading_progressbar = (ProgressBar) convertView.findViewById(R.id.downloading_progressbar);
				convertView.setTag(holder);
			} else {
				// 取出holder
				holder = (ViewHolder) convertView.getTag();
			}

			final HashMap<String, String> hashMap = arrayList_all.get(position);
			final String id = hashMap.get("id");
			final String urlTotal = hashMap.get("tid");
			final String filename = hashMap.get("name");
			final String url = hashMap.get("url_content");
			final String picture = hashMap.get("picture");
			final String foldername = hashMap.get("taskname");
			String downloadStatus = hashMap.get("downloadStatus");
			final String downloadedSize = hashMap.get("downloadedSize");
			String isfinish = hashMap.get("isfinish");
			final String mapKey = hashMap.get("mapKey");

			String downloadpos = hashMap.get("downloadpos");// 文件大小
			String downloadPercent = hashMap.get("downloadPercent");
			final String downloadSpeed = hashMap.get("downloadSpeed");
			final String treeid = hashMap.get("treeid");
			// 根据isSelected来设置checkbox的选中状况
			holder.downloading_check.setChecked(getIsSelected().get(position));

			if (filename.length() > 12) {
				holder.downloading_name.setText(filename.substring(0, 12) + "...");
			} else {
				holder.downloading_name.setText(filename);
			}
			LoadBitmap.getIntence().loadImage(holder.downloading_img, picture);// fcz
			holder.downloading_progressbar.setProgress((int) Float.parseFloat(downloadpos));
			String maxsize = hashMap.get("maxsize");
			if (!StringUtil.isEmpty(maxsize)) {
				holder.downloading_progressbar.setMax(Integer.parseInt(maxsize));
			}
				if ("0".equals(downloadStatus)) { // 是暂停
					holder.downloading_benfenbi.setText("0" + "K/s");
					holder.pause_button.setVisibility(View.GONE);
					holder.go_on_button.setVisibility(View.VISIBLE);
				} else {//1 是下载状态
					holder.downloading_benfenbi.setText(downloadPercent + "K/s");
					holder.pause_button.setVisibility(View.VISIBLE);
					holder.go_on_button.setVisibility(View.GONE);
				}
			if (!StringUtil.isEmpty(hashMap.get("baifenbi"))) {
				holder.downloading_benfenbi_new.setVisibility(View.VISIBLE);
				holder.downloading_benfenbi_new.setText( hashMap.get("baifenbi")+"%" );// fcz
			} else {
				holder.downloading_benfenbi_new.setVisibility(View.GONE);
			}

			holder.go_on_button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Logger.i("重新下载");
					if (NetworkUtil.isWIFIConnected(getActivity())) {

						if (url.contains(".m3u8")) {
							downloadVideo(holder.pause_button, holder.go_on_button, id, urlTotal, filename, url, picture, foldername);
						} else {
							ArrayList<String> query_All_DownStatus = DataBaseDao.getInstance(getActivity()).query_All_DownStatus();
							if (query_All_DownStatus.contains("1")) {
								Toast.makeText(getActivity(), "最多支持下载一个视频", Toast.LENGTH_LONG).show();

							} else {
								holder.pause_button.setVisibility(View.VISIBLE);
								holder.go_on_button.setVisibility(View.GONE);
								Intent intent = new Intent(getActivity(), Download_Service.class);
								intent.putExtra("_id", id);// 视频id
								intent.putExtra("urlTotal", urlTotal);// 父ID
								intent.putExtra("filename", filename);// 视频名
								intent.putExtra("url", url);// 视频播放地址
								intent.putExtra("picture", picture);// 视频的图片地址
								intent.putExtra("foldername", foldername);// 视频评分
								getActivity().startService(intent);
							}
						}
					} else {
						Toast.makeText(getActivity(), "网络连接不可用", Toast.LENGTH_SHORT).show();
					}

				}
			});

			holder.pause_button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) { // fcz
					Logger.i("暂停");
					holder.pause_button.setVisibility(View.GONE);
					holder.go_on_button.setVisibility(View.VISIBLE);
						DataBaseDao.getInstance(getActivity()).updata_DownStatus(id, 0);
						arrayList = DataBaseDao.getInstance(getActivity()).query_DownLoad_Ing("1", "下载中");
						arrayList_all.clear();
						arrayList_all.addAll(arrayList);
						adapter.notifyDataSetChanged();
					// 根据视频id更新视频下载状态
					Intent intent_pause = new Intent();
					intent_pause.setAction(pu.PAUSE);
					intent_pause.putExtra("url", url);
					getActivity().sendBroadcast(intent_pause);
				}
			});

			return convertView;
		}

	}

	/**
	 * 自动下载下一个
	 */
	public void downLoadNext(HashMap<String, String> hashMap3, Intent intent_pause) {

		String id = hashMap3.get("id");
		String urlTotal = hashMap3.get("tid");
		String filename = hashMap3.get("name");
		String url = hashMap3.get("url_content");
		String picture = hashMap3.get("picture");
		String foldername = hashMap3.get("taskname");
		String downloadStatus = hashMap3.get("downloadStatus");
		String downloadedSize = hashMap3.get("downloadedSize");
		String isfinish = hashMap3.get("isfinish");
		String mapKey = hashMap3.get("mapKey");
		String downloadpos = hashMap3.get("downloadpos");
		String downloadPercent = hashMap3.get("downloadPercent");
		String downloadSpeed = hashMap3.get("downloadSpeed");
		String treeid = hashMap3.get("treeid");

		// 更新下载按钮状态
		DataBaseDao.getInstance(getActivity()).updata_DownStatus(id, 1);

		ArrayList<String> query_contion_size = DataBaseDao.getInstance(getActivity()).query_contion_size(id);

		intent_pause.putExtra("next_id", id);// 视频id
		intent_pause.putExtra("next_urlTotal", urlTotal);// 视频章节id
		intent_pause.putExtra("next_filename", filename);// 视频名字
		intent_pause.putExtra("next_url", url);// 视频播放地址
		intent_pause.putExtra("next_picture", picture);// 视频的图片地址
		intent_pause.putExtra("next_foldername", foldername);// 视频章节名称
		intent_pause.putExtra("next_mapKey", query_contion_size.get(0));// 下载到第几个url
		intent_pause.putExtra("next_downloadedSize", query_contion_size.get(1));// 下载完成度
		intent_pause.putExtra("next_downloadSpeed", query_contion_size.get(2));// 下载文件大小

	}

	public void downloadVideo(Button pause_button, Button go_on_button, String id, String urlTotal, String filename, String url,
			String picture, String foldername) {

		ArrayList<String> query_All_DownStatus = DataBaseDao.getInstance(getActivity()).query_All_DownStatus();
		if (query_All_DownStatus.contains("1")) {

			Toast.makeText(getActivity(), "最多支持下载一个视频", Toast.LENGTH_LONG).show();

		} else {

			pause_button.setVisibility(View.VISIBLE);
			go_on_button.setVisibility(View.GONE);
			// 根据视频id更新视频下载状态
			DataBaseDao.getInstance(getActivity()).updata_DownStatus(id, 1);

			arrayList = DataBaseDao.getInstance(getActivity()).query_DownLoad_Ing("1", "下载中");
			arrayList_all.clear();
			arrayList_all.addAll(arrayList);
			adapter.notifyDataSetChanged();

			ArrayList<String> query_contion_size = DataBaseDao.getInstance(getActivity()).query_contion_size(id);

			Intent intent = new Intent(getActivity(), Download_Service.class);
			intent.putExtra("_id", id);// 视频id
			intent.putExtra("urlTotal", urlTotal);// 视频章节id
			intent.putExtra("filename", filename);// 视频名字
			intent.putExtra("url", url);// 视频播放地址
			intent.putExtra("picture", picture);// 视频的图片地址
			intent.putExtra("foldername", foldername);// 视频章节名称
			intent.putExtra("mapKey", query_contion_size.get(0));// 下载到第几个url
			intent.putExtra("downloadedSize", query_contion_size.get(1));// 下载完成度
			intent.putExtra("downloadSpeed", query_contion_size.get(2));// 下载文件大小
			getActivity().startService(intent);

			Toast.makeText(getActivity(), "即将启动下载", Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 广播接收器
	 */
	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(pu.DOWNING)) {

				Bundle bundle = intent.getExtras();
				Float progress = bundle.getFloat("progress");
				String baifenbi = bundle.getString("baifenbi");
				String updata_id = bundle.getString("id");
				String downloadStatus = bundle.getString("downloadStatus");
				int maxsize = bundle.getInt("maxsize");
				int downloadNetworkSpeed = bundle.getInt("downloadNetworkSpeed");
				for (int i = 0; i < arrayList.size(); i++) {
					HashMap<String, String> hashMap = arrayList.get(i);
					String list_id = hashMap.get("id");
					if (list_id.equals(updata_id)) {
						hashMap.put("downloadpos", String.valueOf(progress));
						hashMap.put("downloadPercent", String.valueOf(downloadNetworkSpeed));
						if(!StringUtil.isEmpty(downloadStatus)){
							hashMap.put("downloadStatus", downloadStatus);
						}
						else{
							hashMap.put("downloadStatus", "1");
						}
						hashMap.put("baifenbi", baifenbi);// fcz
						hashMap.put("maxsize", maxsize + "");
					}

				}
				arrayList_all.clear();
				arrayList_all.addAll(arrayList);
				adapter.notifyDataSetChanged();

			} else if (intent.getAction().equals(pu.FAIL)) {
				Log.v("tangcy", "接收广播下载失败");

				Bundle bundle = intent.getExtras();
				String updata_id = bundle.getString("id");
				for (int i = 0; i < arrayList.size(); i++) {
					HashMap<String, String> hashMap = arrayList.get(i);
					String list_id = hashMap.get("id");
					if (list_id.equals(updata_id)) {
						hashMap.put("downloadStatus", "0");
					}

				}

				arrayList_all.clear();
				arrayList_all.addAll(arrayList);
				adapter.notifyDataSetChanged();

			} else if (intent.getAction().equals(pu.FINSH)) {

				Logger.i("接受广播下载完成");
				arrayList_all.clear();
				arrayList = DataBaseDao.getInstance(getActivity()).query_DownLoad_Ing("1", "下载中");
				arrayList_all.addAll(arrayList);
				adapter.notifyDataSetChanged();

				Bundle bundle = intent.getExtras();
				String updata_id = bundle.getString("id");
				int query_Key_Id = DataBaseDao.getInstance(getActivity()).query_Key_Id(updata_id);
				ArrayList<HashMap<String, String>> query_Next_DownLoad_IngList = DataBaseDao.getInstance(getActivity())
						.query_Next_DownLoad_Ing("1", String.valueOf(query_Key_Id));

				// 如果query_Next_DownLoad_IngList的长度大于1那么自动下载他下面一个，否则下载第一个
				if (query_Next_DownLoad_IngList.size() >= 1) {

					HashMap<String, String> hashMap = query_Next_DownLoad_IngList.get(0);
					// 刷新自动下载下一个视频的按钮状态
					for (int i = 0; i < arrayList.size(); i++) {
						HashMap<String, String> hashMap_list = arrayList.get(i);
						if (hashMap_list.get("id").equals(hashMap.get("id"))) {
							hashMap_list.put("downloadStatus", "1");
						}
					}
					arrayList_all.clear();
					arrayList_all.addAll(arrayList);
					adapter.notifyDataSetChanged();

				}

				if (arrayList.size() == 0) {
					nothing_text.setVisibility(View.VISIBLE);
				} else {
					nothing_text.setVisibility(View.GONE);
				}

			}

		}

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDestroy() {

		getActivity().unregisterReceiver(receiver);
		super.onDestroy();
	}

	public int getCheckNum() {
		return checkNum;
	}

	public void setCheckNum(int checkNum) {
		this.checkNum = checkNum;
	}

}
