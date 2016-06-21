package com.android.iwo.media.activity;

import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.iwo.media.action.AppConfig;
import com.android.iwo.media.action.DBhelper;
import com.android.iwo.media.action.IwoSQLiteHelper;
import com.android.iwo.media.lenovo.R;
import com.android.iwo.media.chat.XmppClient;
import com.test.iwomag.android.pubblico.util.DataRequest;
import com.test.iwomag.android.pubblico.util.LoadBitmap;
import com.test.iwomag.android.pubblico.util.StringUtil;

/**
 * 查询好友结果
 * @author abc
 *
 */
public class SearchFriResActivtity extends BaseActivity {

	private String tel = "";
	private String id = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_res);
		init();
	}
	
	private void init(){
		tel = getIntent().getStringExtra("tel");
		setBack(null);
		setTitle("添加好友");
		new GetData().execute();
	}
	
	private void setView(HashMap<String, String> map){
		if(map == null) return;
		findViewById(R.id.layout).setVisibility(View.VISIBLE);
		setItem(R.id.name, map.get("nick_name") + " " + (StringUtil.isEmpty(map.get("age"))?"":map.get("age")));
		setItem(R.id.address, map.get("city_name"));
		setItem(R.id.say, map.get("sign"));
		ImageView head = (ImageView)findViewById(R.id.head);
		LoadBitmap.getIntence().loadImage(map.get("head_img"), head);
		
		id = map.get("id");
		TextView btn = (TextView)findViewById(R.id.add);
		DBhelper per = new DBhelper(this, IwoSQLiteHelper.FRIEND_TAB);
		if(id.equals(getUid()) && per.selectTable("tab" + getPre("user_name"), "name", map.get("user_name"))!=null)
			btn.setVisibility(View.GONE);
		per.close();
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AddF().execute();
			}
		});
	}
	
	private class GetData extends AsyncTask<Void, Void, HashMap<String, String>>{

		@Override
		protected void onPreExecute() {
			mLoadBar.show();
		}
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			return DataRequest.getHashMapFromUrl_Base64(getUrl(AppConfig.NEW_USER_FIND), tel);
		}
		
		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			if(result != null){
				if("1".equals(result.get("code"))){
					setView(DataRequest.getHashMapFromJSONObjectString(result.get("data")));
				}else {
					makeText(result.get("msg"));
				}
				if("1".equals(result.get("is_friend"))){
					findViewById(R.id.add).setVisibility(View.GONE);
				}
			}
			mLoadBar.dismiss();
		}
	}
	
	private class AddF extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			return XmppClient.getInstance(mContext).addFriend(tel, null);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				makeText("已发送好友邀请");
				findViewById(R.id.add).setVisibility(View.GONE);
			}
		}
	}
}
