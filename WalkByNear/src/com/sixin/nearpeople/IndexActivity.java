package com.sixin.nearpeople;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.LocationManagerProxy;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.poisearch.PoiSearch;
import com.hogan.walkbynear.R;
import com.lidroid.xutils.exception.DbException;
import com.sixin.nearpeople.dialog.ChooseDialog;
import com.sixin.nearpeople.dialog.ChooseDialog.OnNagtiveClickListenner;
import com.sixin.nearpeople.dialog.ChooseDialog.OnPositiveClickListenner;
import com.sixin.nearpeople.dialog.LoadingDialog;
import com.sixin.nearpeople.entity.LifeMark;
import com.sixin.nearpeople.entity.LifeMarkDao;
import com.sixin.nearpeople.entity.People;
import com.sixin.nearpeople.gaode.GaoDeMapManager;
import com.sixin.nearpeople.gaode.GaoDeMapManager.SuccessLocationListener;
import com.sixin.nearpeople.gaode.GaoDeMapManager.SuccessPoiSearchListener;
import com.sixin.nearpeople.utils.NetworkConnected;
import com.sixin.nearpeople.utils.OrientationSensorUtil;

/**
 * @描述 此页面是初始化定位与搜索相关事物
 * @作者 ch
 * @时间 2015-1-19 下午3:13:05
 * */

public class IndexActivity extends Activity implements OnClickListener {
	// 定位相关
	private LocationManagerProxy mLocationManagerProxy;
	private LatLonPoint myLatLon;
	// 搜索结果初始化
	private PoiSearch poiSearch;
	private People people;
	private List<LifeMark> myLifeMarks;
	// 相关控伯初始化
	private LoadingDialog myDialog;
	// 附近的事物
	private List<People> myPeople; // 附近的事物
	private LinearLayout back_tv;
	private TextView back_name;
	private ImageView open_ig;
	private TextView tv_title;
	private long exitTime = 0;
	private String thing = "美食";
	private GridView gridView;
	private LifeMarkDao markDao;
	private MyGridViewAdapter gridAdapter;
	private GaoDeMapManager gaoDeMapManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_index);
		init();
		gaoDeMapManager = GaoDeMapManager.getInstance(this);
		if (!isOPen(IndexActivity.this)) {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle("GPS定位未打开，请允许打开？");
			dialog.setMessage("请允许打开GPS定位，否则可能影响定位精准度");
			dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							openGPS(IndexActivity.this);
						}
					});
			dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});

			dialog.show();

		}
		initLocation();
	}

	private void getMyDialog(Context context, String str) {

		myDialog = new LoadingDialog(this, str);
		myDialog.setCancelable(true);
		myDialog.show();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	/**
	 * @描述 相关控件初始化
	 * @时间 2015-1-17 上午11:06:06
	 */

	private void init() {
		gridView = (GridView) findViewById(R.id.index_gridView);
		markDao = new LifeMarkDao(this);
		markDao.initData();
		try {
			myLifeMarks = markDao.getMyLifeMark();
			if (myLifeMarks == null) {
				myLifeMarks = new ArrayList<LifeMark>();
			}
			myLifeMarks.add(new LifeMark(LifeMark.TYPE_MYTYPE, "更多", 1));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gridAdapter = new MyGridViewAdapter(this);
		gridView.setAdapter(gridAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position != myLifeMarks.size() - 1) {
					thing = myLifeMarks.get(position).getName();
					findNearThings(myLifeMarks.get(position).getName(),
							myLatLon);
				} else {
					IndexActivity.this.startActivityForResult(new Intent(
							IndexActivity.this, AddLifeMarkActivity.class),
							0x101);
				}
			}
		});
		back_name = (TextView) findViewById(R.id.back_name);
		back_name.setText("发现");
		back_tv = (LinearLayout) findViewById(R.id.back_tv);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("周边");
		open_ig = (ImageView) findViewById(R.id.open_ig);
		open_ig.setVisibility(View.GONE);
		// 监听事件
		back_tv.setOnClickListener(this);
		open_ig.setOnClickListener(this);

	}

	/**
	 * @描述 定位相关数据初始化
	 * @时间 2015-1-16 下午3:27:19
	 */

	private void initLocation() {
		// 成功的回调
		gaoDeMapManager
				.setSuccessLocationListener(new SuccessLocationListener() {
					@Override
					public void successLocation(LatLonPoint myLatLon) {
						IndexActivity.this.myLatLon = myLatLon;
						gaoDeMapManager.setMyLatLng(myLatLon);
					}
				});
		//5s定位一次，1m变动，定位一次
		gaoDeMapManager.startLocation(5000, 1);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.open_ig:
			startActivityForResult(new Intent(this, AddLifeMarkActivity.class),
					0x101);
			break;
		case R.id.back_tv:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * @描述 用于查找以自己为中心半径为1500米范围内的相关事物
	 * @时间 2015-1-10 下午2:26:59
	 * 
	 */

	private void findNearThings(String things, LatLonPoint myLatLng) {
		// 判断是否支持传感器
		if (!OrientationSensorUtil.isSupportSensor(this)) {
			Toast.makeText(this, "很遗憾，您的手机不支持陀螺仪传感器，无法使用！", Toast.LENGTH_LONG)
					.show();
			return;
		}
		if (NetworkConnected.isNetworkConnected(IndexActivity.this)) {
			getMyDialog(IndexActivity.this, "加载数据中，请稍等...");

			gaoDeMapManager.setSearchListener(new SuccessPoiSearchListener() {
				@Override
				public void successPoiSearch(List<People> myPeoples) {
					myPeople = myPeoples;
					myDialog.dismiss();
					if (myPeople == null || myPeople.size() == 0) {
						myDialog.dismiss();
						Toast.makeText(IndexActivity.this,
								"附近1500内未找到" + thing, Toast.LENGTH_LONG).show();
						return;
					}

					Intent intent = new Intent(IndexActivity.this,
							WalkByNear.class);
					intent.putExtra("thing", thing);
					//取消监听
					gaoDeMapManager.setSearchListener(null);
					startActivity(intent);
				}
			});
			gaoDeMapManager.startPoiSearchByBound(myLatLng, 1500, things, "",
					"");

		} else {
			ChooseDialog dialog = new ChooseDialog(this, "网络有问题，去设置？",
					"没有网络，您不能很好的享受本软件", "点击确定，设置网络");

			dialog.setOnPositiveClickListenner(new OnPositiveClickListenner() {
				@Override
				public void onPositiveClick(ChooseDialog dialog) {
					dialog.dismiss();

					startActivity(new Intent(
							Settings.ACTION_ACCESSIBILITY_SETTINGS));

				}
			});
			dialog.setOnNagtiveClickListenner(new OnNagtiveClickListenner() {
				@Override
				public void onNagtiveClick(ChooseDialog dialog) {

					dialog.dismiss();
				}
			});
			dialog.show();
		}
	}

	@Override
	protected void onDestroy() {
		gaoDeMapManager.stopLocation();
		super.onDestroy();

	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		//gaoDeMapManager.stopLocation();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
	 * 
	 * @param context
	 * @return true 表示开启
	 */
	public static final boolean isOPen(final Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		boolean network = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps || network) {
			return true;
		}

		return false;
	}

	/**
	 * 提示用户打开GPS
	 * 
	 * @param context
	 */
	public static final void openGPS(Context context) {

		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(intent);

		} catch (ActivityNotFoundException ex) {

			// The Android SDK doc says that the location settings activity
			// may not be found. In that case show the general settings.

			// General settings activity
			intent.setAction(Settings.ACTION_SETTINGS);
			try {
				context.startActivity(intent);
			} catch (Exception e) {
			}
		}
	}

	// 二次退出
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class MyGridViewAdapter extends BaseAdapter {
		private Context context;

		public MyGridViewAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			if (myLifeMarks == null)
				return 0;
			return myLifeMarks.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return myLifeMarks.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.item_lifemark,
						null);
				holder = new Holder();
				holder.markImage = (ImageView) convertView
						.findViewById(R.id.lifeMark_item_image);

				holder.name = (TextView) convertView
						.findViewById(R.id.lifeMark_item_name);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.name.setText(myLifeMarks.get(position).getName());
			holder.markImage.setBackgroundResource(myLifeMarks.get(position)
					.getImageId());
			return convertView;
		}

	}

	static class Holder {
		ImageView markImage;
		TextView name;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0x102) {
			try {
				myLifeMarks = markDao.getMyLifeMark();
				if (myLifeMarks == null) {
					myLifeMarks = new ArrayList<LifeMark>();
				}
				myLifeMarks.add(new LifeMark(LifeMark.TYPE_MYTYPE, "更多", 1));
			} catch (DbException e) {
				e.printStackTrace();
			}
			gridAdapter.notifyDataSetChanged();
		}
	}

	public List<People> randomList(List<People> peoples, int size) {
		if (peoples == null || peoples.size() <= size) {
			return peoples;
		} else {
			List<People> temps = new ArrayList<People>();
			int bz[] = new int[peoples.size()];// 标记数组，0为未使用过，1为已使用
			// 将数组填充为0
			Arrays.fill(bz, 0);
			// 循环至指定个数
			while (temps.size() < size) {
				Random random = new Random();
				int id = random.nextInt(peoples.size());
				if (bz[id] == 0) {// 为使用，则加入，并标记已使用
					temps.add(peoples.get(id));
					bz[id] = 1;
				}
			}
			return temps;
		}
	}
}
