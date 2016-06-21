package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.StringUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

//重新写 搜索结果显示
public class SearchResultActivity extends BaseActivity {
	private String search_string;
	private ListView listView;
	private String video_id;
	private ArrayList<HashMap<String, String>> mdata = new ArrayList<HashMap<String, String>>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singleton_listview);
		search_string = getIntent().getStringExtra("search_string");
		video_id = getIntent().getStringExtra("video_id");
		findViewById(R.id.title_img).setVisibility(View.VISIBLE);
		listView = (ListView) findViewById(R.id.singleton_list);
		listView.setDivider(null);// 将ListView分线去掉
		setBack(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
		findViewById(R.id.syte_jiazai_gengduo).setVisibility(View.GONE);
		new GetData().execute(video_id);
	}

	protected class GetData extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		protected void onPreExecute() {
			mLoadBar.setMessage("数据加载中...");
			mLoadBar.show();
		}

		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			String data = null;
			data = DataRequest.getStringFromURL_Base64(
					getUrl(AppConfig.NEW_UN_SEARCH_VIDEO_LIST), search_string,
					"1", "10");
			if (!StringUtil.isEmpty(data)) {
				return DataRequest.getArrayListFromJSONArrayString(data);
			}
			return null;
		}

		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			mLoadBar.dismiss();
			if (result != null) {
				mdata.addAll(result);
				initView(result);
			} else {
				makeText("没有符合条件的视频");
			}
		}

	}

	// 界面显示
	public void initView(final ArrayList<HashMap<String, String>> result) {
		if (result == null || result.size() == 0) {
			makeText("没有符合条件的视频");
			return;
		}
		IwoAdapter adapter = new IwoAdapter(SearchResultActivity.this, result) {

			private viewhold hold;

			@SuppressLint("NewApi")
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null) {
					hold = new viewhold();
					v = View.inflate(SearchResultActivity.this,
							R.layout.layout_recmmended_video_list_item_se, null);
					hold.imageView = (ImageView) v.findViewById(R.id.item_img);
					hold.title = (TextView) v
							.findViewById(R.id.video_title_name);
					v.setTag(hold);
				} else {
					hold = (viewhold) v.getTag();
				}
				HashMap<String, String> map = result.get(position);
				if (!StringUtil.isEmpty(map.get("img_url_2"))) {
					setImgSize(hold.imageView, 14, 27 / 48.0f, 1);
					LoadBitmap.getIntence().loadImage(map.get("img_url_2"),
							hold.imageView);
				} else {
					hold.imageView.setBackground(getResources().getDrawable(
							R.drawable.souye_weizhi));
				}
				setItem(hold.title, map.get("name"));

				return v;

			}

		};
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, String> hashMap = result.get(position);
				Intent intent = new Intent(mContext,
						VideoDetailActivity_new.class);
				intent.putExtra("video_id", hashMap.get("id"));
				startActivity(intent);

			}
		});

	}

	private class viewhold {
		ImageView imageView;
		TextView title;
	}
}
