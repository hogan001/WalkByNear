package com.sixin.nearpeople.gaode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.Query;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.hogan.walkbynear.R;
import com.sixin.nearpeople.entity.People;
import com.sixin.nearpeople.utils.LatlngUtil;

/**
 * 高德地图定位，poi检索管理
 * 
 * @author liuhongliang
 * 
 *         2015-2-5 下午1:38:25
 */
public class GaoDeMapManager implements AMapLocationListener,
		OnPoiSearchListener, AMapNaviListener {
	//初始化图片
	private static Map<String, Integer> images;
	//定位得到的经纬度信息
	private static LatLonPoint myLatLng;
	
	private Activity activity;
	private static GaoDeMapManager gaoDeMapManager;
	// 定位相关
	private LocationManagerProxy mLocationManagerProxy;
	
	// 回调相关
	private SuccessLocationListener successLocationListener;
	private SuccessPoiSearchListener searchListener;
	// POI搜索
	private PoiSearch poiSearch;
	private List<PoiItem> results;
	// 导航
	private AMapNavi mAMapNavi;
	// 路径规划
	private Class destClass;
	private ProgressDialog mRouteCalculatorProgressDialog;// 路径规划过程显示状态
	// 附近的事物
	private List<People> peopleList;
	//为定位成功，等待定位成功执行的任务
	//private boolean isWaitLocation=true;
	private LatLonPoint latLonPoint;
	private int distance;
	private String keyText=null, type, area;
	//初始化图片信息
	static{
		images=new HashMap<String, Integer>();
		images.put("美食", R.drawable.selector_life_mark_icon_food);
		images.put("KTV", R.drawable.selector_life_mark_icon_ktv);
		images.put("景点", R.drawable.selector_life_mark_icon_landscape);
		images.put("酒店", R.drawable.selector_life_mark_icon_rummery);
		images.put("超市", R.drawable.selector_life_mark_icon_supermarket);
		images.put("公交车", R.drawable.selector_life_mark_icon_bus);
		images.put("ATM", R.drawable.selector_life_mark_icon_atm);
		images.put("电影院", R.drawable.selector_life_mark_icon_movie);
		images.put("药店", R.drawable.selector_life_mark_icon_pharmacy);
		images.put("地铁", R.drawable.selector_life_mark_icon_subway);
		images.put("快餐", R.drawable.selector_life_mark_icon_snack);
		images.put("更多", R.drawable.selector_life_mark_icon_add);
		images.put("自定义", R.drawable.icon_default);
	}
	
	/**
	 * 
	 * @param context
	 *            上下文对象
	 */
	private GaoDeMapManager(Activity acivity) {
		this.activity = acivity;
	}

	/**
	 * 单例模式
	 * 
	 * @param context
	 * @return
	 */
	public static synchronized GaoDeMapManager getInstance(Activity activity) {
		if (gaoDeMapManager == null) {
			gaoDeMapManager=new GaoDeMapManager(activity);
		}
		return gaoDeMapManager;
	}

	/**
	 * 开启定位,定位结果可在SuccessLocationListener的回调方法中获得
	 * 
	 * @param updateTime
	 *            更新时间,单位ms,-1代表只定位一次，不更新
	 * @param minDistance
	 *            更新的最小距离，即移动的距离
	 */
	public void startLocation(long updateTime, float minDistance) {
		if(mLocationManagerProxy==null){
			mLocationManagerProxy = LocationManagerProxy.getInstance(activity);
		}
		// provider - 注册监听的 provider 名称。
		// 用户需根据定位需求来设定provider。有三种定位Provider供用户选择，分别是:LocationManagerProxy.GPS_PROVIDER，代表使用手机GPS定位；LocationManagerProxy.NETWORK_PROVIDER，代表使用手机网络定位；LocationProviderProxy.AMapNetwork，代表高德网络定位服务。
		// minTime - 位置变化的通知时间，单位为毫秒，实际时间有可能长于或短于设定值
		// 。参数设置为-1，为单次定位；反之为每隔设定的时间，都会触发定位请求。
		// minDistance - 位置变化通知距离，单位为米。
		// listener - 监听listener。
		// LocationProviderProxy.AMapNetwork:高德网络定位服务。该定位服务是高德的混合定位，包括GPS和网络定位。如不需要GPS，可以在LocationManagerProxy中设置setGpsEnable方法。
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, updateTime, minDistance,
				this);
		mLocationManagerProxy.setGpsEnable(true);
	}

	/**
	 * 停止定位
	 */
	public void stopLocation() {
		if (mLocationManagerProxy != null) {
			mLocationManagerProxy.removeUpdates(this);
			mLocationManagerProxy.destroy();
		}
		mLocationManagerProxy = null;
	}

	/**
	 * 开始Poi周边检索，结果可在SuccessPoiSearchListener的回调方法中获得
	 * 
	 * @param latLonPoint
	 *            自己的经纬度信息
	 * @param distance
	 *            查询的范围，单位m
	 * @param keyText
	 *            查询的关键字
	 * @param type
	 *            查询的类型（餐饮）
	 * @param area
	 *            POI搜索区域（空字符串代表全国）
	 */
	public void startPoiSearchByBound(LatLonPoint latLonPoint, int distance,
			String keyText, String type, String area) {
		this.latLonPoint=latLonPoint;
		this.distance=distance;
		this.keyText=keyText;
		this.type=type;
		this.area=area;
		if(latLonPoint==null){//还未定位成功，等待定位成功后处理
			return ;
		}else{//若需实时更新数据，则此处不需要，否则设置text为空，保证下次定位时不在搜索！
			this.keyText=null;
		}
		Query query = new PoiSearch.Query(keyText, type, area);
		query.setPageSize(30);// 设置每页最多返回多少条poiitem，默认值是20 条,取值范围在1-30 条
		query.setPageNum(0);// 设置查第一页
		poiSearch = new PoiSearch(activity, query);
		poiSearch.setBound(new SearchBound(latLonPoint, distance));
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}

	/**
	 * 取消定位回调
	 */
	public void unLocationListener() {
		setSuccessLocationListener(null);
	}

	/**
	 * 取消POI回调
	 */
	public void unPoiSearchByBoundListener() {
		setSearchListener(null);
	}

	public void startRouteNavi(LatLonPoint startLatLng, LatLonPoint endLatLng,
			Class destClass) {
		this.destClass = destClass;
		mRouteCalculatorProgressDialog = new ProgressDialog(activity);
		mRouteCalculatorProgressDialog.setCancelable(true);
		mRouteCalculatorProgressDialog.show();
		NaviLatLng start = new NaviLatLng(startLatLng.getLatitude(),
				startLatLng.getLongitude());
		NaviLatLng end = new NaviLatLng(endLatLng.getLatitude(),
				endLatLng.getLongitude());
		mAMapNavi = AMapNavi.getInstance(activity);
		mAMapNavi.setAMapNaviListener(this);
		calculateFootRoute(start, end);
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	/**
	 * 定位的监听
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			// 获取位置信息
			myLatLng = new LatLonPoint(amapLocation.getLatitude(),
					amapLocation.getLongitude());
			Log.e("hongliang", "定位成功   la:"+myLatLng.getLatitude()+"   lo:"+myLatLng.getLongitude());
			if(this.keyText!=null){
				//执行待处理任务
				startPoiSearchByBound(myLatLng, distance, keyText, type, area);
			}
			// 回调
			if (successLocationListener != null) {
				successLocationListener.successLocation(myLatLng);
			}
		}
	}

	public SuccessLocationListener getSuccessLocationListener() {
		return successLocationListener;
	}

	public void setSuccessLocationListener(
			SuccessLocationListener successLocationListener) {
		this.successLocationListener = successLocationListener;
	}

	public SuccessPoiSearchListener getSearchListener() {
		return searchListener;
	}

	public void setSearchListener(SuccessPoiSearchListener searchListener) {
		this.searchListener = searchListener;
	}

	/**
	 * 详情检索
	 */
	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {

	}

	/**
	 * POI异步检索监听
	 */
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {

		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {
				if (result.getQuery().getPageNum() == 0) {// 第一页初始化
					results = new ArrayList<PoiItem>();
				}
				results.addAll(result.getPois());
			}
			if (result.getQuery().getPageNum()>=1||result.getQuery().getPageNum() == result.getPageCount()) {// 全部加载完成
				saveDataToApplication();
				if (searchListener != null) {
					searchListener.successPoiSearch(peopleList);
				}
			} else {// 加载下一页数据
				poiSearch.getQuery().setPageNum(
						result.getQuery().getPageNum() + 1);
				poiSearch.searchPOIAsyn();
			}
		}
	}

	private void saveDataToApplication(){
		peopleList=new ArrayList<People>();
		for (PoiItem item : results) {
			LatLonPoint reLatLng = item.getLatLonPoint();
			// 二点角度
			double angle = LatlngUtil.getAngle(myLatLng.getLatitude(),
					myLatLng.getLongitude(), reLatLng.getLatitude(),
					reLatLng.getLongitude());
			People people;
			if (item.getDistance() <= 1500) {
				people = new People();
				people.setLocation(item.getLatLonPoint());
				people.setName(item.getTitle());
				people.setDistance(item.getDistance());
				people.setAngle(angle);
				people.setAddress(item.getSnippet());
				people.setCity(item.getCityName());
				people.setPhoneNum(item.getTel());
				peopleList.add(people);
			}
		}
		//随机抽取前50个数据，不足50个时，全部返回
		peopleList=randomList(peopleList,60);
		// 把得到所有的点放入MyApplication中
		//MyApplication.getInitial().setPeopleList(myPeoples);
		Log.e("hongliang", "数据获取成功！ "+peopleList.size());
	}
	public List<People> randomList(List<People> peoples,int size){
		if(peoples==null||peoples.size()<=size){
			return peoples;
		}else{
			List<People> temps=new ArrayList<People>();
			boolean bz[]=new boolean[peoples.size()];
			//将数组填充为未使用
			Arrays.fill(bz, false);
			Random random;
			int id;
			//循环至指定个数
			while(temps.size()<size){
				random=new Random();
				id=random.nextInt(peoples.size());
				while(bz[id]){
					id=random.nextInt(peoples.size());
				}
				temps.add(peoples.get(id));
				bz[id]=true;
			}
			return temps;
		}
	}

	// 回调接口
	public interface SuccessLocationListener {
		public void successLocation(LatLonPoint myLatLon);
	}

	public interface SuccessPoiSearchListener {
		/**
		 * POI结果
		 * 
		 * @param results
		 *            null表示查询出错，size()==0为未查到结果
		 */
		public void successPoiSearch(List<People> myPeoples);
	}

	// 计算步行路线
	private void calculateFootRoute(NaviLatLng mNaviStart, NaviLatLng mNaviEnd) {
		boolean isSuccess = mAMapNavi.calculateWalkRoute(mNaviStart, mNaviEnd);
		if (!isSuccess) {
			showToast("路线计算失败,检查参数情况");
		}
	}

	private void startEmulatorNavi(Class destClass) {
		Intent emulatorIntent = new Intent(activity, destClass);
		Bundle bundle = new Bundle();
		bundle.putBoolean(Utils.ISEMULATOR, true);
		bundle.putInt(Utils.ACTIVITYINDEX, Utils.SIMPLEROUTENAVI);
		emulatorIntent.putExtras(bundle);
		emulatorIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		activity.startActivity(emulatorIntent);
	}

	private void startGPSNavi(Class destClass) {

		Intent gpsIntent = new Intent(activity, destClass);
		Bundle bundle = new Bundle();
		bundle.putBoolean(Utils.ISEMULATOR, false);
		bundle.putInt(Utils.ACTIVITYINDEX, Utils.SIMPLEROUTENAVI);
		gpsIntent.putExtras(bundle);
		gpsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		activity.startActivity(gpsIntent);

	}

	private void showToast(String message) {
		Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
	}

	// --------------------导航监听回调事件-----------------------------
	@Override
	public void onArriveDestination() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onArrivedWayPoint(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCalculateRouteFailure(int arg0) {
		showToast("路径规划出错" + arg0);
		mRouteCalculatorProgressDialog.dismiss();
	}

	@Override
	public void onCalculateRouteSuccess() {
		// showToast("路径规划成功");
		mRouteCalculatorProgressDialog.dismiss();
		AMapNaviPath naviPath = mAMapNavi.getNaviPath();
		if (naviPath == null) {
			return;
		}
		// 获取路径规划线路，显示到地图上
		// mRouteOverLay.setRouteInfo(naviPath);
		// mRouteOverLay.addToMap();
		startGPSNavi(destClass);

	}
	/**
	 * 停止poi搜索
	 * @param keyText
	 */
	public void stopPoiSearch() {
		this.keyText = null;
	}

	public static Map<String, Integer> getImages() {
		return images;
	}

	
	public List<People> getPeopleList() {
		return peopleList;
	}

	public void setPeopleList(List<People> peopleList) {
		this.peopleList = peopleList;
	}

	public static LatLonPoint getMyLatLng() {
		return myLatLng;
	}

	public static void setMyLatLng(LatLonPoint myLatLng) {
		GaoDeMapManager.myLatLng = myLatLng;
	}

	@Override
	public void onEndEmulatorNavi() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGpsOpenStatus(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNaviFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNaviSuccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChange(AMapNaviLocation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviInfoUpdated(AMapNaviInfo arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReCalculateRouteForTrafficJam() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReCalculateRouteForYaw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartNavi(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTrafficStatusUpdate() {
		// TODO Auto-generated method stub

	}
}
