package com.sixin.nearpeople.entity;

import java.io.Serializable;

import android.util.Log;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.sixin.nearpeople.gaode.GaoDeMapManager;
import com.sixin.nearpeople.utils.LatlngUtil;

public class People implements Serializable {

	private static final long serialVersionUID = 1L;
	public LatLonPoint location;
	public String name;
	public int distance;
	public String distanceStr;
	public double angle;
	public String phoneNum;
	public String city;
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String address;
	/**
	 * 将获取动态角度（根据每次地位的经纬度）
	 * @return
	 */
	public double getAngle() {
		//获取当前的位置
		LatLonPoint myLatLng = GaoDeMapManager.getMyLatLng();
		if(myLatLng!=null){
			//实时计算角度
			angle=LatlngUtil.getAngle(myLatLng.getLatitude(), myLatLng.getLongitude(),
					location.getLatitude(), location.getLongitude());
		}
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}


	public People(LatLonPoint location, String name, int distance) {
		this.location = location;
		this.name = name;
		this.distance = distance;
	}

	public People() {

	}

	public LatLonPoint getLocation() {
		return location;
	}

	public void setLocation(LatLonPoint location) {
		this.location = location;
	
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 实时更新距离
	 * @return
	 */
	public int getDistance() {
		LatLonPoint myLatLng = GaoDeMapManager.getMyLatLng();
		if(myLatLng!=null){
			//实时计算距离
			distance=(int) AMapUtils.calculateLineDistance(new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude()),
					new LatLng(location.getLatitude(), location.getLongitude()) );
		}
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getDistanceStr() {
		if(getDistance()>1000){
			distanceStr=String.valueOf(distance/1000.)+"km";
		}else{
			distanceStr=String.valueOf(distance)+"米";
		}
		return distanceStr;
	}
}
