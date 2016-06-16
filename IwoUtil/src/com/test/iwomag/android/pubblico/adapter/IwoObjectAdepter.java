package com.test.iwomag.android.pubblico.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.iwomag.android.pubblico.util.BitmapUtil;
import com.test.iwomag.android.pubblico.util.LoadBmp;
import com.test.iwomag.android.pubblico.util.StringUtil;

public abstract class IwoObjectAdepter extends BaseAdapter{
	public List<?> mAdapterData;
	public LayoutInflater mInflater;
	
	
	
	/**
	 * 此构造方法需要重写getView方法
	 * @param activity
	 * @param data
	 */
	public IwoObjectAdepter(Activity activity,List<?> data){
		this.mAdapterData = data;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mAdapterData.size();
	}

	@Override
	public Object getItem(int position) {
		return mAdapterData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View v, ViewGroup parent);

	/**
	 *  eg "name: jake"
	 * @param color 设置name的颜色
	 * @param title 设置name的值
	 * @param value 设置jake的值
	 * @return
	 */
	protected CharSequence setValidString(String color, CharSequence title, String value){
		title = Html.fromHtml("<font color='"+color+"'>"+title+"</font>" +value);
		return StringUtil.isEmpty(value)?"":(title);
	}
	/**
	 * 设置整个字段的颜色值
	 * @param color
	 * @param title
	 * @param value
	 * @return
	 */
	protected CharSequence setColorToString(String color, CharSequence title, String value){
		title = Html.fromHtml("<font color='"+color+"'>"+title+value+"</font>" );
		return StringUtil.isEmpty(value)?"":(title);
	}
	/**
	 * 设置value的颜色
	 * @param color
	 * @param title
	 * @param value
	 * @return
	 */
	protected CharSequence setColorToData(String color, CharSequence title, String value){
		title = Html.fromHtml(title+"<font color='"+color+"'>"+value+"</font>" );
		return StringUtil.isEmpty(value)?"":(title);
	}
	/**
	 * @param v 当前视图 的View
	 * @param ID 添加item的View id
	 * @param value 设置item的值
	 */
	protected void setItem(View v, int ID, CharSequence value){
		TextView view = (TextView)v.findViewById(ID);
		if(!StringUtil.isEmpty(value)){
			view.setText(value);
			view.setVisibility(View.VISIBLE);
		}else {
			view.setText("");
		}
	}
	
	/**
	 * @param v 当前视图 的View
	 * @param ID 添加item的View id
	 * @param value 设置item的值
	 */
	protected void setItem(View v, int ID, String value, String color){
		TextView view = (TextView)v.findViewById(ID);
		if(!StringUtil.isEmpty(value)){
			CharSequence value1 = Html.fromHtml("<font color='"+color+"'>"+value+"</font>");
			view.setText(value1);
			view.setVisibility(View.VISIBLE);
		}else {
			view.setText("");
		}
	}
	
	protected void setItem(TextView textView, CharSequence value){
		if(!StringUtil.isEmpty(value)){
			textView.setText(value);
			textView.setVisibility(View.VISIBLE);
		}else {
			textView.setVisibility(View.GONE);
		}
	}
	/**
	 * 
	 * @param v
	 * @param ID
	 * @param titleColor
	 * @param title
	 * @param valueColor
	 * @param value
	 */
	
	protected void setItem(View v, int ID, String titleColor, String title, String valueColor, CharSequence value){
		TextView view = (TextView)v.findViewById(ID);
		if(!StringUtil.isEmpty(value)){
			CharSequence str = Html.fromHtml("<font color='"+titleColor+"'>"+title+"</font>" + "<font color='"+valueColor+"'>"+value+"</font>" );
			view.setText(str);
			view.setVisibility(View.VISIBLE);
		}else {
			view.setVisibility(View.GONE);
		}
	}
	/**
	 * @param v 当前视图 的View
	 * @param ID 添加item的View id
	 * @param value 设置item的值
	 */
	protected void setItem(View v, int ID, String title,CharSequence value){
		TextView view = (TextView)v.findViewById(ID);
		if(!StringUtil.isEmpty(value)){
			view.setText(title+value);
			view.setVisibility(View.VISIBLE);
		}else {
			view.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 控制图片的大小
	 * @param v 当前视图的View
	 * @param id 显示Imageview的id
	 * @param url 
	 */
	protected void setImageView(int size, View v, int id, String url){
		final ImageView imageView = (ImageView)v.findViewById(id);
		if(!StringUtil.isEmpty(url)){
			imageView.setVisibility(View.VISIBLE);
			LoadBmp.getIntence().loadImage(url, imageView);
		}else {
			//imageView.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 显示列表的图片
	 * @param v 当前视图的View
	 * @param id 显示Imageview的id
	 * @param url 
	 */
	protected void setImageView(View v, int id, String url){
		final ImageView imageView = (ImageView)v.findViewById(id);
		if(!StringUtil.isEmpty(url)){
			imageView.setVisibility(View.VISIBLE);
			LoadBmp.getIntence().loadImage(url, imageView);
		}else {
			//imageView.setVisibility(View.GONE);
		}
	}
	
	protected void setImageView(View v, int id, Bitmap bitmap){
		final ImageView imageView = (ImageView)v.findViewById(id);
		if(bitmap != null){
			imageView.setVisibility(View.VISIBLE);
			imageView.setImageBitmap(bitmap);
		}else {
			//imageView.setVisibility(View.GONE);
		}
	}
	
	
	protected void setItemGone(View v, int id){
		View view = v.findViewById(id);
		view.setVisibility(View.GONE);
	}
	protected void setItemGone(View v){
		v.setVisibility(View.GONE);
	}
	/**
	 * @Title: setImageView 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param @param context
	 * @param @param v
	 * @param @param id
	 * @param @param resId
	 * @return void
	 * @author xingwu
	 */
	 
	protected void setImageView(Context context, View v, int id, int resId){
		ImageView imageView = (ImageView)v.findViewById(id);
		Bitmap bitmap = BitmapUtil.decodeResource(context, resId);
		if(bitmap != null)
			BitmapUtil.setImageBitmap(imageView,bitmap);
	}
	/**
	 * 滚动的时候不加载图片
	 * @param isScorlling
	 * @param v
	 * @param id
	 * @param url
	 */
	protected void setImageView(Boolean isScorlling, View v, int id, String url){
		final ImageView imageView = (ImageView)v.findViewById(id);
		if(!StringUtil.isEmpty(url)){
			imageView.setVisibility(View.VISIBLE);
			LoadBmp.getIntence().loadImage(url, imageView);
		}else {
			imageView.setVisibility(View.GONE);
		}
	}
	protected void setImageView(final ImageView imageView, String url){
		if(!StringUtil.isEmpty(url)){
			imageView.setVisibility(View.VISIBLE);
			LoadBmp.getIntence().loadImage(url, imageView);
		}else {
			imageView.setVisibility(View.GONE);
		}
	}
	
	protected String setTime(String time){
		if(StringUtil.isEmpty(time)) return "";
		long callTime = Long.parseLong(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(callTime));
	}
}
