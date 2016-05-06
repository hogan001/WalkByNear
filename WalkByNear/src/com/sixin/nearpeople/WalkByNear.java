package com.sixin.nearpeople;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.hogan.walkbynear.R;
import com.sixin.nearpeople.entity.People;
import com.sixin.nearpeople.gaode.GaoDeMapManager;
import com.sixin.nearpeople.gaode.GaoDeMapManager.SuccessPoiSearchListener;
import com.sixin.nearpeople.myview.DynamicViewGroup;
import com.sixin.nearpeople.myview.PeopleView;
import com.sixin.nearpeople.myview.RoundImageView;
import com.sixin.nearpeople.utils.CameraUtil;
import com.sixin.nearpeople.utils.LatlngUtil;
import com.sixin.nearpeople.utils.LatlngUtil.Point;
import com.sixin.nearpeople.utils.OrientationSensorUtil;

public class WalkByNear extends Activity implements OnClickListener {

	private OrientationSensorUtil orientationSenosrUtil;
	private LatlngUtil latlngUtil;
	private SurfaceView sView;
	private RelativeLayout dongtaiLayout;
	private RoundImageView roundView;
	private CameraUtil cameraUtil;
	private DynamicViewGroup dynamicViewGroup;
	private LatLonPoint mLatLng;
	private List<People> myPeoples;
	private List<Point> points;

	private ImageView open_ig, image_bt;
	private List<PeopleView> peopleViews;// 子view,即界面上漂浮的东西

	private TextView name_bt, address_bt, distance_bt, time_bt, tv_title,
			line_tv, line_tv1, phone_bt;
	private View bottomView;
	private String thing;

	private LinearLayout layout_open, firstlayout, secondlayout, threelayout,
			back_tv;

	private int falg = 0;
	private GaoDeMapManager gaodeManager;
	private int selectIndex = -1;
	//第一次引导页
	private View firstGuide;
	private ImageView first_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题

		setContentView(R.layout.activity_main);

		Intent intent = getIntent();
		thing = intent.getStringExtra("thing");

		initView();
		
		updateData();
		
	}

	private void initView() {
		//第一次引导页
		firstGuide=findViewById(R.id.first_guide);
		//防止點擊
		firstGuide.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		first_btn=(ImageView) findViewById(R.id.first_btn);
		first_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				firstGuide.setVisibility(View.GONE);
			}
		});
		SharedPreferences setting=this.getSharedPreferences("first_guide", 0);
		Boolean user_first = setting.getBoolean("FIRST",true); 
		if(user_first){//第一次       
			setting.edit().putBoolean("FIRST", false).commit();
			firstGuide.setVisibility(View.VISIBLE);
		}else{
			firstGuide.setVisibility(View.GONE);
		}

		name_bt = (TextView) findViewById(R.id.name_bt);
		address_bt = (TextView) findViewById(R.id.address_bt);
		distance_bt = (TextView) findViewById(R.id.distance_bt);
		time_bt = (TextView) findViewById(R.id.time_bt);
		bottomView = findViewById(R.id.allbottom);
		threelayout = (LinearLayout) findViewById(R.id.threelayout);
		layout_open = (LinearLayout) findViewById(R.id.layout_open);
		firstlayout = (LinearLayout) findViewById(R.id.firstlayout);
		secondlayout = (LinearLayout) findViewById(R.id.secondlayout);
		image_bt = (ImageView) findViewById(R.id.image_bt);
		back_tv = (LinearLayout) findViewById(R.id.back_tv);
		line_tv = (TextView) findViewById(R.id.line_tv);
		line_tv1 = (TextView) findViewById(R.id.line_tv1);
		phone_bt = (TextView) findViewById(R.id.phone_bt);
		phone_bt.setOnClickListener(this);
		firstlayout.setOnClickListener(this);
		secondlayout.setOnClickListener(this);
		threelayout.setOnClickListener(this);
		layout_open.setOnClickListener(this);
		back_tv.setOnClickListener(this);
		image_bt.setBackgroundResource(R.drawable.display_default);

		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(thing);
		dynamicViewGroup = (DynamicViewGroup) findViewById(R.id.dynamicView);
		dongtaiLayout = (RelativeLayout) findViewById(R.id.dongtaiLayout);
		sView = (SurfaceView) findViewById(R.id.CameraView);
		roundView = (RoundImageView) findViewById(R.id.roundView);
		open_ig = (ImageView) findViewById(R.id.open_ig);
		open_ig.setOnClickListener(this);
		// 处理按标签以外位置，标签选中取消
		dongtaiLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bottomView.setVisibility(View.GONE);
				dynamicViewGroup.setSelectPoint(-1);
				roundView.setSelectPoint(-1);
				WalkByNear.this.selectIndex=-1;
			}
		});
		
		// 初始化摄像头工具类
		cameraUtil = new CameraUtil(sView);
		gaodeManager = GaoDeMapManager.getInstance(this);
		
	}

	/**
	 * @描述 用于更新界面
	 * @时间 2015-1-16 下午3:28:03
	 */

	@SuppressLint("NewApi")
	private void updateData() {
		myPeoples = GaoDeMapManager.getInstance(this).getPeopleList();

		// 初始化子View
		peopleViews = new ArrayList<PeopleView>();
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		
		for (int i = 0; i < myPeoples.size(); i++) {
			PeopleView peopleView = new PeopleView(getApplicationContext(),R.drawable.bg_markview);
			peopleView.setNameTextColor(0xff4d4d4d);
			peopleView.setDistanceTextColor(0xffffffff);
			final float fontScale = this.getResources().getDisplayMetrics().scaledDensity; 
			peopleView.setNameTextSize((int) (14 * fontScale + 0.5f));
			peopleView.setDistanceTextSize((int) (15 * fontScale + 0.5f));
			peopleView.setPadding(5.5f);
			peopleView.setMaxTextNum(12);
			peopleView.setPeople(myPeoples.get(i), i);
			peopleView.setBackground(getResources().getDrawable(
					R.drawable.bg_markview));

			peopleView.setOnClickListener(myOnClickListener);

			peopleViews.add(peopleView);

		}

		dynamicViewGroup.setChildView(peopleViews);
		//恢复上次选中
		dynamicViewGroup.setSelectPoint(this.selectIndex);
		// mLatLng = MyLatLng.getMyLatLng().getLatLng();
		mLatLng = GaoDeMapManager.getInstance(this).getMyLatLng();

		// 将之转化为屏幕坐标在1500m范围内，因圆盘有边距，故适当加大一点转化范围，防止点跑出圆外
//		latlngUtil = new LatlngUtil(1550, mLatLng);
//		// 得到背景圆盘的大小
//		float round_diameter = getResources().getDimension(
//				R.dimen.round_diameter);
//		points = latlngUtil.getXYS(round_diameter / 2, myPeoples,0);

		// 初始化方向传感器工具类
		orientationSenosrUtil = new OrientationSensorUtil(
				getApplicationContext(), roundView, dongtaiLayout);
//		// 构建圆盘附近的事物
//		roundViewGroup.getRoundView().showNearthings(points);
	}

	@Override
	protected void onResume() {
		// 开启摄像头

		cameraUtil.onResume();
		// 定位成功后，开启方向传感器，并给圆盘注册监听
		orientationSenosrUtil.onResume();
		//更新监听，监听数据变化，更新界面数据
		gaodeManager.setSearchListener(new SuccessPoiSearchListener() {
			@Override
			public void successPoiSearch(List<People> myPeoples) {
				//Toast.makeText(WalkByNear.this, "数据更新了！", 0).show();
				//Log.e("hongliang", "数据更新啦");
				//updateData();
			}
		});
		
		super.onResume();

	}

	@Override
	protected void onPause() {
		// 关闭摄像头
		cameraUtil.onPause();
		// 关闭传感器
		orientationSenosrUtil.onPause();
		gaodeManager.setSearchListener(null);
		gaodeManager.stopPoiSearch();
		super.onPause();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.open_ig:

			startActivityForResult(new Intent(WalkByNear.this,
					ListViewActivity.class), 0x101);
			break;

		case R.id.back_tv:

			finish();
			break;

		case R.id.layout_open:
			judgeView();
			break;
		case R.id.phone_bt: // 调用打电话
			String phoneNum = ((TextView) v).getText().toString();
			callPhone(phoneNum);
			break;
		case R.id.secondlayout:
			if (selectIndex != -1) {
				LatLonPoint endLatLon = myPeoples.get(selectIndex)
						.getLocation();
				gaodeManager.startRouteNavi(mLatLng, endLatLon,
						SimpleNaviActivity.class);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 根据号码打电话
	 * 
	 * @param phoneNum
	 */
	public void callPhone(String phoneNum) {

		if (phoneNum == null || phoneNum.trim().equals("")) {
			return;
		}
		phoneNum = phoneNum.trim();
		// 跳转到拨号界面
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
				+ phoneNum));

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		int selectPosition = -1;
		int disance = -1;

		if (resultCode == 0x102) {
			selectPosition = data.getIntExtra("selectPosition", -1);
			disance = data.getIntExtra("disance", -1);
		}
		if (selectPosition != -1) {
			this.selectIndex = selectPosition;
			bottomView.setVisibility(View.VISIBLE);
			dynamicViewGroup.setSelectPoint(selectPosition);
			roundView.setSelectPoint(selectPosition);
			name_bt.setText(data.getStringExtra("name"));
			address_bt.setText(data.getStringExtra("address"));
			if (disance != -1) {
				// distance_bt.setText(String.valueOf(disance) + "米");
				distance_bt.setText(myPeoples.get(selectPosition)
						.getDistanceStr());
				int time = (int) (disance / 48);// 正常人每分钟走48米
				time_bt.setText("约" + String.valueOf(time) + "分钟");

				phone_bt.setText(data.getStringExtra("phone"));

			}

		}
	}

	/**
	 * @描述 点击图标，决断使用哪个View
	 * @时间 2015-1-28 上午11:12:49
	 */

	private void judgeView() {
		falg = falg % 2;
		if (falg == 0) {
			falg++;
			image_bt.setBackgroundResource(R.drawable.display_press);
			line_tv.setVisibility(View.VISIBLE);
			line_tv1.setVisibility(View.VISIBLE);
			secondlayout.setVisibility(View.VISIBLE);
			threelayout.setVisibility(View.VISIBLE);
		} else {
			falg++;
			image_bt.setBackgroundResource(R.drawable.display_default);
			secondlayout.setVisibility(View.GONE);
			threelayout.setVisibility(View.GONE);
			line_tv.setVisibility(View.GONE);
			line_tv1.setVisibility(View.GONE);
		}
	}

	// 子view的点击事件
	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			PeopleView pv = (PeopleView) v;
			int selectPosition = pv.getIndex();
			WalkByNear.this.selectIndex = selectPosition;
			bottomView.setVisibility(View.VISIBLE);
			dynamicViewGroup.setSelectPoint(selectPosition);
			roundView.setSelectPoint(selectPosition);
			name_bt.setText(pv.getPeople().getName());
			address_bt.setText(pv.getPeople().getAddress());
			if (pv.getPeople().getDistance() != -1) {
				distance_bt.setText(pv.getPeople().getDistanceStr());
				int time = (int) (pv.getPeople().getDistance() / 48);// 正常人每分钟走48米
				time_bt.setText("约" + String.valueOf(time) + "分钟");

				if (pv.getPeople().getPhoneNum() == null
						|| pv.getPeople().getPhoneNum().equals("")) {
					phone_bt.setText("暂无电话");
				} else {
					phone_bt.setText(pv.getPeople().getPhoneNum());
				}
			}

		}

	}

}
