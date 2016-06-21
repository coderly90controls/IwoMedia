package com.android.iwo.media.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.android.iwo.media.apk.activity.*;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class ScreenShotActivity extends BaseActivity implements
		OnGeocodeSearchListener {
	private AMap aMap;
	private MapView mapView;
	private ProgressDialog progDialog = null;
	private GeocodeSearch geocoderSearch;
	private Marker regeoMarker;
	private LatLonPoint latLonPoint = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screenshor);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		setBack(null);
		if (StringUtil.isEmpty(getIntent().getStringExtra("type")))
			setTitle("位置");
		else {
			setNightTitle("位置");
		}
		try {
			float lat = Float.valueOf(getIntent().getStringExtra("lat"));
			float lon = Float.valueOf(getIntent().getStringExtra("lon"));
			latLonPoint = new LatLonPoint(lat, lon);
		} catch (Exception e) {
			Logger.i("获取经纬度失败" + e.toString());
			latLonPoint = new LatLonPoint(0, 0);
		}

		if ("yes".equals(getIntent().getStringExtra("screen"))) {
			ImageView right = (ImageView) findViewById(R.id.right_img);
			right.setVisibility(View.VISIBLE);
			right.setImageResource(R.drawable.yes);
			right.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					setResult(RESULT_OK);
					finish();
				}
			});
		}

		if (aMap == null) {
			aMap = mapView.getMap();
			regeoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		}
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		progDialog = new ProgressDialog(this);
		getAddress(latLonPoint);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在获取地址");
		progDialog.show();
	}

	/**
	 * 隐藏进度条对话框
	 */
	public void dismissDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
		showDialog();
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {

	}

	/**
	 * 逆地理编码查询回调
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		dismissDialog();
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				result.getRegeocodeAddress().getFormatAddress();
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						convertToLatLng(latLonPoint), 15));
				regeoMarker.setPosition(convertToLatLng(latLonPoint));
			} else {
			}
		} else if (rCode == 27) {
		} else if (rCode == 32) {
		} else {
		}
	}

	/**
	 * 把LatLonPoint对象转化为LatLon对象
	 */
	public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
		return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
	}
}
