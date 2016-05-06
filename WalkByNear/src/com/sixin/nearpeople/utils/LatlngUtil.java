package com.sixin.nearpeople.utils;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.services.core.LatLonPoint;
import com.sixin.nearpeople.entity.People;

public class LatlngUtil {
	private double r;// 默认搜索范围
	private static LatLonPoint yuanxin;

	public class Point {
		public double x;
		public double y;
		public String name;
		public double angle;

		public Point(double x, double y, String name, double angle) {
			this.x = x;
			this.y = y;
			this.name = name;
			this.angle = angle;

		}

	}

	public LatlngUtil(double r, LatLonPoint yuanxin) {
		this.r = r;
		if (yuanxin != null) {

			this.yuanxin = yuanxin;
		}
	}

	@Deprecated
	public Point getXY(double width, People people) {
		if (yuanxin != null) {
			double proportion = width / r;// 现实1500m到屏幕上映射的比例
			double distance = getDistance(yuanxin.getLatitude(), yuanxin.getLongitude(),
					people.location.getLatitude(), people.location.getLongitude())
					* proportion;// 两地屏幕上距离
			double angle = getAngle(yuanxin.getLatitude(), yuanxin.getLongitude(),
					people.location.getLatitude(), people.location.getLongitude());
			angle = Math.toRadians(angle);// 角度转弧度

			//Log.e("getXY", "distance=" + distance + "  angle=" + angle);
			Point point = new Point(width, width, people.name, angle);
			if (angle >= 0) {
				point.x = width + distance * Math.sin(angle);
			} else {
				angle = -angle;
				point.x = width - distance * Math.sin(angle);
			}
			if (angle < 90) {// 右上 ,y-
				point.y = width - distance * Math.cos(angle);
			} else if (angle <= 180) {// 右下 y+
				point.y = width + distance * Math.cos(180 - angle);
			}
			return point;
		}
		return null;
	}

	/*
	 * 将经纬度坐标换算成屏幕坐标
	 */
	public Point getXY2(double width, People people,float currentAngle) {

		if (yuanxin != null) {
			double proportion = width / r;// 现实1500m到屏幕上映射的比例
			double distance = getDistance(yuanxin.getLatitude(), yuanxin.getLongitude(),
					people.location.getLatitude(), people.location.getLongitude())
					* proportion;// 两地屏幕上距离
			//目标点与圆心连线与正北方向的夹角
			double angle = getAngle(yuanxin.getLatitude(), yuanxin.getLongitude(),
					people.location.getLatitude(), people.location.getLongitude());
			//与手机的夹角
			angle=(angle-currentAngle+360)%360;
			// double raidan=Math.toRadians(angle);//角度转弧度

			//Log.e("getXY", "distance=" + distance + "  angle=" + angle);
			Point point = new Point(width, width, people.name, angle);
			if (angle >= 0 && angle <= 90) { // 一： x+ y-
				point.x = width + distance * Math.sin(Math.toRadians(angle));
				point.y = width - distance * Math.cos(Math.toRadians(angle));
			} else if (angle > 90 && angle <= 180) { // 二：x+ y+
				point.x = width + distance
						* Math.sin(Math.toRadians(180 - angle));
				point.y = width + distance
						* Math.cos(Math.toRadians(180 - angle));
			} else if (angle > 180 && angle <= 270) { // 三：x- y+
				point.x = width - distance
						* Math.sin(Math.toRadians(angle - 180));
				point.y = width + distance
						* Math.cos(Math.toRadians(angle - 180));
			} else if (angle > 270 && angle <= 360) { // 四：x- y-
				point.x = width - distance
						* Math.sin(Math.toRadians(360 - angle));
				point.y = width - distance
						* Math.cos(Math.toRadians(360 - angle));
			}
			return point;
		}
		return null;
	}

	/**
	 * 将所有位置转换成平面坐标
	 * 
	 * @param width
	 * @param peoples
	 * @return
	 */
	public List<Point> getXYS(double width, List<People> peoples,float currentAngle) {
		//Log.e("getxys", "width=" + width);

		List<Point> points = new ArrayList<Point>();
		for (People people : peoples) {
			if (people != null) {
				Point point = getXY2(width, people,currentAngle);
				points.add(point);
			}
		}
		return points;
	}

	/**
	 * 获取两地连线与正北方向的夹角
	 * 
	 * @param lat_a
	 * @param lng_a
	 * @param lat_b
	 * @param lng_b
	 * @return 与正北的角度，0~360
	 */
	public static double getAngle(double lat_a, double lng_a, double lat_b,
			double lng_b) {
		MyLatLonPoint A = new MyLatLonPoint(lng_a, lat_a);
		MyLatLonPoint B = new MyLatLonPoint(lng_b, lat_b);
		double dx = (B.m_RadLo - A.m_RadLo) * A.Ed;
		double dy = (B.m_RadLa - A.m_RadLa) * A.Ec;
		double angle = 0.0;
		angle = Math.atan(Math.abs(dx / dy)) * 180. / Math.PI;
		double dLo = B.m_Longitude - A.m_Longitude;
		double dLa = B.m_Latitude - A.m_Latitude;
		if (dLo > 0 && dLa <= 0) {
			angle = (90. - angle) + 90;
		} else if (dLo <= 0 && dLa < 0) {
			angle = angle + 180.;
		} else if (dLo < 0 && dLa >= 0) {
			angle = (90. - angle) + 270;
		}
		return angle;
	}

	static class MyLatLonPoint {
		final static double Rc = 6378137;
		final static double Rj = 6356725;
		double m_Longitude, m_Latitude;
		double m_RadLo, m_RadLa;
		double Ec;
		double Ed;

		public MyLatLonPoint(double longitude, double latitude) {

			m_Longitude = longitude;
			m_Latitude = latitude;
			m_RadLo = longitude * Math.PI / 180.;
			m_RadLa = latitude * Math.PI / 180.;
			Ec = Rj + (Rc - Rj) * (90. - m_Latitude) / 90.;
			Ed = Ec * Math.cos(m_RadLa);
		}
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 由经纬度计算两地距离
	 * 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return 两地的距离 单位m
	 */
	public static double getDistance(double lat1, double lng1, double lat2,
			double lng2) {
		double r1 = rad(lat1);
		double r2 = rad(lng1);

		double a = rad(lat2);
		double b = rad(lng2);
		int R = 6378137;
		double s = Math.acos(Math.cos(r1) * Math.cos(a) * Math.cos(r2 - b)
				+ Math.sin(r1) * Math.sin(a))
				* R;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

}
