package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.apk.activity.R;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.PreferenceUtil;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class Brand_pageActivity extends BaseActivity {
	private ArrayList<HashMap<String, String>> mData=new ArrayList<HashMap<String,String>>();
	private MyAdater adapter;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brand_page);
		new GetData().execute();
		init();
	}

	private void init() {
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
		setBack(null);
//		ImageView left = (ImageView) findViewById(R.id.right_left_img);
//		ImageView right = (ImageView) findViewById(R.id.right_img);
//		left.setVisibility(View.VISIBLE);
//		right.setVisibility(View.VISIBLE);
		findViewById(R.id.title_img).setVisibility(View.VISIBLE);
		adapter = new MyAdater();
		GridView gv=(GridView)findViewById(R.id.grid_view);
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				HashMap<String, String> map = mData.get(pos);
				Intent intent = new Intent(mContext, BrandActivity.class);
				intent.putExtra("video_name", map.get("ch_name"));
				intent.putExtra("id", map.get("id"));
				startActivity(intent);
			}
		});
//		left.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				if (!setUserLogin()) {
//					return;
//				}
//
//				startActivity(new Intent(mContext, SearchActivity.class));
//				
//			}
//		});
//		right.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if (!setUserLogin()) {
//					return;
//				}
//				startActivity(new Intent(mContext, MeRecordActivity.class));
//				
//			}
//		});
		
	}
	private class GetData extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>>{

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
			// TODO Auto-generated method stub
			 String str = DataRequest.getStringFromURL_Base64(getUrl(AppConfig.NEW_V_GET_VIDEO_LIST_BRAND));
			 return DataRequest.getArrayListFromJSONArrayString(str);
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			mLoadBar.dismiss();
			super.onPostExecute(result);
			if(result!=null&&result.size()>0){
				mData.clear();
				mData.addAll(result);
				adapter.notifyDataSetChanged();
			}
		}
		
	}
	private class MyAdater extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_gridview_new, null);
			ImageView img=(ImageView)convertView.findViewById(R.id.discover_item_item_img);
			HashMap<String, String> map = mData.get(position);
			LoadBitmap.getIntence().loadImage(map.get("ch_logo"), img);
			//item_text.setText(map.get("ch_name"));
			int screenWidth = PreferenceUtil.getInt(mContext, "screenWidth", 100);
			android.view.ViewGroup.LayoutParams params = img.getLayoutParams();
			int width = (screenWidth - 40) / 2;
		//	int width = img.getWidth();
			params.height = width*170/280;
			return convertView;
		}
		
	}
}
