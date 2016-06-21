package com.android.iwo.media.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.android.iwo.media.activity.VideoViewPlayingActivity;
import com.android.iwo.media.apk.activity.R;
import com.android.iwo.media.view.CommonDialog;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.Logger;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//下载完成

public class DownloadFinsh_Fragment extends Fragment {
	private View v;
	private TextView nothing_text;
	private ArrayList<HashMap<String, String>> arrayList;
	private MyAdapter_finsh adapter;
	private ListView download_finsh_list;
	private PublicUtils pu;
	private ArrayList<HashMap<String, String>> arrayList_all;
	private String islogin;// 是否登录标示

	private ViewGroup mSelectionMenuView;
	private Button selection_all;
	private Button selection_delete;
	private Boolean iskey = false;
	private Boolean ism3u8 = false;
	private Boolean isurls = false;
	private Boolean Inverse = false;
	private int checkNum = 0; // 记录选中的条目数量

	private TextView tipTextView;
	private CommonDialog mLoadBar;
	private final int UPDATE_TEXT = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pu = new PublicUtils(getActivity());
		arrayList = new ArrayList<HashMap<String, String>>();
		arrayList_all = new ArrayList<HashMap<String, String>>();
		mLoadBar = new CommonDialog(getActivity());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.downloadfinsh_fragment, container, false);
		nothing_text = (TextView) v.findViewById(R.id.nothing_text);

		adapter = new MyAdapter_finsh();

		mSelectionMenuView = (ViewGroup) v.findViewById(R.id.selection_menu);
		selection_all = (Button) v.findViewById(R.id.selection_all);
		selection_delete = (Button) v.findViewById(R.id.selection_delete);
		download_finsh_list = (ListView) v.findViewById(R.id.download_finsh_list);
		arrayList = DataBaseDao.getInstance(getActivity()).query_DownLoad_Ing("1", "下载完成");
		arrayList_all.addAll(arrayList);

		download_finsh_list.setAdapter(adapter);

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
							// R.anim.footer_appear)); fcz

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
				Logger.i("删除");
				new Delete_AsyncTask().execute("");

			}
		});

		return v;
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == UPDATE_TEXT) {
				String updatetext = (String) msg.obj;
				tipTextView.setText("正在删除:" + updatetext);
			}

			super.handleMessage(msg);
		}

	};

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
						String update_name = hashMap.get("name");
						String id = hashMap.get("id");
						// 删除数据库
						DataBaseDao.getInstance(getActivity()).delete_item(id);
						String url_content = hashMap.get("url_content");
						if (url_content.contains(".m3u8")) {
							// 删除sd卡
							// iskey =
							// FileUtil.deleteFile(ConstantsDownload.path_key+"//"+hashMap.get("tid")+id+"key");
							ism3u8 = FileUtil.deleteFile(ConstantsDownload.path_m3u8 + "//" + hashMap.get("tid") + id + ".m3u8");
							isurls = FileUtil.deleteFolder(ConstantsDownload.path_urls + hashMap.get("tid") + id);
						} else {
							FileService fileService=new FileService(getActivity());
							int lastIndexOf = url_content.lastIndexOf("/");
							String substring = url_content.substring(lastIndexOf);
							String content_name = ConstantsDownload.path_m3u8 + substring;
							Logger.i("完成删除mp4名字：" + content_name);
							fileService.delete(content_name);
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

				mLoadBar.setMessage("正在删除...");
				mLoadBar.show();
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mLoadBar != null) {
				mLoadBar.dismiss();
			}

			if (result) {
				arrayList = DataBaseDao.getInstance(getActivity()).query_DownLoad_Ing("1", "下载完成");
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
					// R.anim.footer_disappear)); fcz

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
	 * 下载完成适配器
	 */
	class MyAdapter_finsh extends BaseAdapter {

		private HashMap<Integer, Boolean> isSelected;

		public MyAdapter_finsh() {
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

		public class ViewHolder_finsh {
			TextView downloading_name;
			TextView file_size;
			CheckBox downloading_check;
			ImageView downloading_img;
			TextView play_button;
			RelativeLayout downlaod_finsh_zong_layout;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder_finsh holder;
			if (convertView == null) {
				holder = new ViewHolder_finsh();
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.downloadfinsh_item, null);
				holder.downloading_check = (CheckBox) convertView.findViewById(R.id.downloading_checks);
				holder.downloading_img = (ImageView) convertView.findViewById(R.id.downloading_img);
				holder.downloading_name = (TextView) convertView.findViewById(R.id.downloading_name);
				holder.file_size = (TextView) convertView.findViewById(R.id.file_size);
				holder.play_button = (TextView) convertView.findViewById(R.id.play_button);
				holder.downlaod_finsh_zong_layout = (RelativeLayout) convertView.findViewById(R.id.downlaod_finsh_zong_layout);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder_finsh) convertView.getTag();
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
			String downloadpos = hashMap.get("downloadpos");
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

			long fliesize = Long.parseLong(downloadedSize);

			String formetFileSize = FileUtil.FormetFileSize(fliesize);

			holder.file_size.setText(formetFileSize);

			holder.downlaod_finsh_zong_layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// 改变CheckBox的状态
					holder.downloading_check.toggle();
					// 将CheckBox的选中状况记录下来
					adapter.getIsSelected().put(position, holder.downloading_check.isChecked());
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

			holder.play_button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), VideoViewPlayingActivity.class);
					HashMap<String, String> hashMap2 = arrayList_all.get(position);
					String url_content = hashMap2.get("url_content");
					String uriString = "http://114.247.0.113/v/m3u8/ya/output000.ts";
					String xx = "/storage/sdcard0/iwo_downs/iwo_m3u8/123103065.m3u8";
					// String mmString="http://114.247.0.113/v/mp4/dmkj.mp4";
					String url_content_new = null;
					if (!url_content.contains(".m3u8")) {
						int lastIndexOf = url_content.lastIndexOf("/");
						String substring = url_content.substring(lastIndexOf);
						url_content_new = ConstantsDownload.path_m3u8 + substring;
					} else {
						url_content_new = url_content;
					}
					Uri uri = Uri.parse(url_content_new);
					intent.setData(uri);
					intent.putExtra("syte", "3");// 下载的视频
					intent.putExtra("title", hashMap2.get("name"));
					intent.putExtra("video_id", hashMap2.get("id"));
					intent.putExtra("data", hashMap2);// fcz
					intent.putExtra("isjj", false);// 在下载进去默认没剧集
					try {
						new PlayVideoServer(getActivity()).start();
						Logger.i("启动本地视频服务成功");
					} catch (Exception e) {
						e.printStackTrace();
						Logger.i("启动本地视频服务失败");
					}
					startActivityForResult(intent, 333);

				}
			});

			return convertView;
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
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public int getCheckNum() {
		return checkNum;
	}

	public void setCheckNum(int checkNum) {
		this.checkNum = checkNum;
	}

}
