package com.android.iwo.media.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.lenovo.R;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 我的关注 || 我的粉丝
 * 
 */
public class AttentionListActivity extends BaseActivity implements OnClickListener {
	protected ListView listView;
	protected IwoAdapter mAdapter;
	protected ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	String attention;
	String friendAttentionID;
	boolean my2heFrirnd;
	boolean onItemClick = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singleton_listview);
		attention = getIntent().getStringExtra("attention");
		new GetData().execute();
		friendAttentionID = getIntent().getStringExtra("friendAttentionID");
		my2heFrirnd = StringUtil.isEmpty(friendAttentionID);
		if ("1".equals(attention)) {
			if (my2heFrirnd) {
				setTitle("我的关注");
			} else {
				setTitle("他（她）的关注");
			}
		} else if ("2".equals(attention)) {
			if (my2heFrirnd) {
				setTitle("我的粉丝");
			} else {
				setTitle("他（她）的粉丝");
			}
		}
		mLoadBar.setMessage("数据加载中...");
		mLoadBar.show();
	}
	private void init() {
		setBack(null);
		listView = (ListView) findViewById(R.id.singleton_list);
		mAdapter = new IwoAdapter(this, mData) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				HashMap<String, String> map = mData.get(position);
				LoadBitmap bitmap = null;
				if (null == v) {
					v = mInflater.inflate(R.layout.view_attention_item, null);
				}
				if (bitmap == null) {
					bitmap = new LoadBitmap();
				}
				ImageView head_img = (ImageView) v.findViewById(R.id.head);
				bitmap.loadImage(map.get("head_img"), head_img);
				setItem(v, R.id.name, mData.get(position).get("nickname_b"));
				setItem(v, R.id.sign, mData.get(position).get("sign"));
				ImageView sex = (ImageView) v.findViewById(R.id.user_sex);
				if ("1".equals(map.get("sex"))) {
					sex.setImageResource(R.drawable.boy);
				} else if ("2".equals(map.get("sex"))) {
					sex.setImageResource(R.drawable.girl);
				} else {
					sex.setImageResource(R.drawable.boy);
				}
				ImageView cancel = (ImageView) v.findViewById(R.id.cancel);

				if (!my2heFrirnd) {
					cancel.setImageResource(R.drawable.n_jia_guanzhu);
				} else {
					if ("2".equals(attention)) {
						cancel.setImageResource(R.drawable.n_jia_guanzhu);
					}
				}
				cancel.setOnClickListener(AttentionListActivity.this);
				cancel.setTag(position);
				return v;
			}
		};
		listView.setCacheColorHint(0);
		listView.setDividerHeight(0);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (my2heFrirnd) {
					Intent intent = new Intent(mContext, FriendDetail.class);
					intent.putExtra("id", mData.get(position).get("user_id_b"));
					intent.putExtra("attention", attention);
					startActivityForResult(intent, AppConfig.REQUEST_FRIENDS_LIST_ACTIVITY);
				} else {
					// 0,我的好友 1，我的关注 （取消关注），2，我的粉丝（加关注）
					onItemClick = true;
					new GetRelation().execute(mData.get(position).get("user_id_b"));
				}
			}
		});
	}

	/**
	 * 获取我的关注列表，
	 * 
	 * @param mode
	 */
	protected class GetData extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
			if ("1".equals(attention)) {
				
				if (my2heFrirnd) {
					return DataRequest.getArrayListFromUrl_Base64(getUrl(AppConfig.NEW_USER_FRIENDS_LIST), "guans");
				} else {
					return DataRequest.getArrayListFromUrl_Base64(getUrl(AppConfig.NEW_GET_FRIEND_GUAN), friendAttentionID, "guans");
				}
				
			} else if ("2".equals(attention)) {
				if (my2heFrirnd) {
					return DataRequest.getArrayListFromUrl_Base64(getUrl(AppConfig.NEW_USER_FRIENDS_LIST), "fans");
				} else {
					return DataRequest.getArrayListFromUrl_Base64(getUrl(AppConfig.NEW_GET_FRIEND_GUAN), friendAttentionID, "fans");
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (null != result) {
				mData.clear();
				
				Map<String,String> myMap = null;
				for(Map<String,String> map:result){
					if(getUid().equals(map.get("user_id_b"))){
						
						myMap=map;
					}
				}
				result.remove(myMap);
				
				mData.addAll(result);
				init();
				findViewById(R.id.singleton_list).setVisibility(View.VISIBLE);
				
			} else {
				AttentionListActivity.this.makeText("暂无数据");
			}
			mLoadBar.dismiss();
		}
	}

	/**
	 * 设置取消好友关注
	 * 
	 */
	protected class setBlackList extends AsyncTask<String, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			if (!my2heFrirnd) {
				return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_EDIT_GUAN), params[0], "1");
			} else {
				if ("1".equals(attention)) {
					return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_EDIT_GUAN), params[0], "2");
				} else if ("2".equals(attention)) {
					return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_V_EDIT_GUAN), params[0], "1");
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (null != result) {
				mAdapter.setmAdapterData(mData);
				mAdapter.notifyDataSetChanged();
				AttentionListActivity.this.makeText(result.get("msg"));
			} else {
				AttentionListActivity.this.makeText("本次操作失败，请重新操作！");
			}
		}
	}

	/**
	 * 判断当前用户与该好友关系
	 * 
	 * @author hanpengyuan
	 * 
	 */
	protected class GetRelation extends AsyncTask<String, Void, HashMap<String, String>> {
		String userID;

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			userID = params[0];
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_GET_RELATION), params[0]);
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if (result != null) {
				Intent intent = new Intent(mContext, FriendDetail.class);
				intent.putExtra("id", userID);
				// relation
				switch (Integer.valueOf(result.get("relation"))) {
				case 0:// 陌生人
					intent.putExtra("attention", "4");
					break;
				case 1:// 关注
					intent.putExtra("attention", "1");
					break;
				case 2:// 粉丝
					intent.putExtra("attention", "2");
					break;
				case 3:// 好友
					intent.putExtra("attention", "0");
					break;
				case 4:// 黑名单
					intent.putExtra("attention", "3");
					break;
				default:
					break;
				}
				mContext.startActivity(intent);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel:
			int position = (Integer) v.getTag();
			Map<String, String> map = mData.remove(position);
			new setBlackList().execute(map.get("user_id_b"));
			break;

		default:
			break;
		}

	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if( requestCode ==AppConfig.REQUEST_FRIENDS_LIST_ACTIVITY&&AppConfig.RESULT_FRIENDDETAIL_SETBLACKLIST==resultCode){
			new GetData().execute();
		}
		
		
	}
}
