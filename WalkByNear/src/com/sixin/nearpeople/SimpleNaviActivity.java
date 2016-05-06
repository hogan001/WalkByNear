package com.sixin.nearpeople;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.hogan.walkbynear.R;
import com.sixin.nearpeople.gaode.TTSController;
import com.sixin.nearpeople.gaode.Utils;
/**
 * 
 *导航界面
 * 
 * */
public class SimpleNaviActivity extends Activity implements
		AMapNaviViewListener {
	//导航View
	private AMapNaviView mAmapAMapNaviView;
   //是否为模拟导航
	private boolean mIsEmulatorNavi = true;
	//记录有哪个页面跳转而来，处理返回键
	private int mCode=-1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simplenavi);
		Bundle bundle = getIntent().getExtras();
		processBundle(bundle);
		init(savedInstanceState);
	
	}

	private void processBundle(Bundle bundle) {
		if (bundle != null) {
			mIsEmulatorNavi = bundle.getBoolean(Utils.ISEMULATOR, true);
			mCode=bundle.getInt(Utils.ACTIVITYINDEX);
		}
	}

	/**
	 * 初始化
	 * 
	 * @param savedInstanceState
	 */
	private void init(Bundle savedInstanceState) {
		mAmapAMapNaviView = (AMapNaviView) findViewById(R.id.simplenavimap);
		mAmapAMapNaviView.onCreate(savedInstanceState);
		mAmapAMapNaviView.setAMapNaviViewListener(this);
		TTSController ttsManager = TTSController.getInstance(this);// 初始化语音模块
		ttsManager.init();
		AMapNavi.getInstance(this).setAMapNaviListener(ttsManager);// 设置语音模块播报
		TTSController.getInstance(this).startSpeaking();
		if (mIsEmulatorNavi) {
			// 设置模拟速度
			AMapNavi.getInstance(this).setEmulatorNaviSpeed(100);
			// 开启模拟导航
			AMapNavi.getInstance(this).startNavi(AMapNavi.EmulatorNaviMode);

		} else {
			// 开启实时导航
			AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);
		}
	}

//-----------------------------导航界面回调事件------------------------
	/**
	 * 导航界面返回按钮监听
	 * */
	@Override
	public void onNaviCancel() {
//		Intent intent = new Intent(SimpleNaviActivity.this,
//				NaviRouteActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		startActivity(intent);
//		finish();
	}
   
	@Override
	public void onNaviSetting() {

	}

	@Override
	public void onNaviMapMode(int arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onNaviTurnClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNextRoadClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScanViewButtonClick() {
		// TODO Auto-generated method stub

	}
	/**
	 * 
	 * 返回键监听事件
	 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if(mCode==Utils.SIMPLEROUTENAVI){
//				Intent intent = new Intent(SimpleNaviActivity.this,
//						NaviRouteActivity.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				startActivity(intent);
//				finish();
//				
//			}
//			else if(mCode==Utils.SIMPLEGPSNAVI){
//				Intent intent = new Intent(SimpleNaviActivity.this,
//						SimpleGPSNaviActivity.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				startActivity(intent);
//				finish();
//			}
//			else{
//				finish();
//			}
//		}
		return super.onKeyDown(keyCode, event);
	}

	// ------------------------------生命周期方法---------------------------
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mAmapAMapNaviView.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAmapAMapNaviView.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
		mAmapAMapNaviView.onPause();
		AMapNavi.getInstance(this).stopNavi();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mAmapAMapNaviView.onDestroy();
		
		TTSController.getInstance(this).stopSpeaking();
		
	}

	

}
