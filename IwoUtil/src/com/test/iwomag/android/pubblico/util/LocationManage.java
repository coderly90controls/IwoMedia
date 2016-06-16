package com.test.iwomag.android.pubblico.util;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.test.iwomag.android.pubblico.util.Logger;

/**
 * 定位功能
 */
public class LocationManage implements AMapLocationListener, Runnable {
	private LocationManagerProxy mAMapLocManager = null;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private Handler handler = new Handler();
	private LocationSuccess success;
	public LocationManage(Context context) {
		mAMapLocManager = LocationManagerProxy.getInstance(context);
		handler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (mAMapLocManager != null) {
			mAMapLocManager.removeUpdates(this);
			mAMapLocManager.destory();
		}
		mAMapLocManager = null;
	}
	public void onLocationChanged(Location location) {
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 混合定位回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			this.aMapLocation = location;// 判断超时机制
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			String cityCode = "";
			String desc = "";
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}
			String str = ("定位成功:(" + geoLng + "," + geoLat + ")" + "\n精    度    :" + location.getAccuracy() + "米" + "\n定位方式:" + location.getProvider() + "\n定位时间:"
					+ location.getTime() + "\n城市编码:" + cityCode + "\n位置描述:" + desc + "\n省:" + location.getProvince() + "\n市:" + location.getCity()
					+ "\n区(县):" + location.getDistrict() + "\n区域编码:" + location.getAdCode());
			Logger.v("---" + str);
			if(success != null){
				success.onSuccess(location);
			}
		}
		
		stopLocation();
	}

	@Override
	public void run() {
		if (aMapLocation == null) {
			Logger.e("12秒内还没有定位成功，停止定位");
			stopLocation();// 销毁掉定位
		}
	}
	
	public void setLocationSuccess(LocationSuccess su){
		success = su;
	}
	public interface LocationSuccess{
		public void onSuccess(AMapLocation map);
	}
	
	/**
	 * 根据两点的经纬度算距离
	 * @param lat1
	 * @param lat2
	 * @param lon1
	 * @param lon2
	 * @return
	 */
	public static double getDistatce(double lat1, double lat2, double lon1, double lon2) {
        double R = 6371;
        double distance = 0.0;
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1 * Math.PI / 180)
                * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        distance = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * R;
        return distance;
    }
	
	/**
	 * 定位
	 * @param context
	 */
	public void getLocation(final Context context){
		mAMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 5000, 10, this);
		setLocationSuccess(new LocationSuccess() {
			public void onSuccess(AMapLocation map) {
				if (map != null) {
					PreferenceUtil.setString(context, "address_lng", map.getLongitude() + "");
					PreferenceUtil.setString(context, "address_lat", map.getLatitude() + "");
					Bundle locBundle = map.getExtras();
					if (locBundle != null) {
						PreferenceUtil.setString(context, "address", locBundle.getString("desc") + "," + map.getLatitude() + "," + map.getLongitude());
					}
				}
			}
		});
	}
}
