package com.sixin.nearpeople.utils;

import java.util.List;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.hogan.walkbynear.R;
import com.sixin.nearpeople.myview.DynamicViewGroup;
import com.sixin.nearpeople.myview.RoundImageView;
import com.sixin.nearpeople.utils.LatlngUtil.Point;

/**
 * @author: 刘红亮
 * @类 说 明: 本类是一个水平传感器的工具类
 * @version 1.0
 * @创建时间：2015-1-6 上午9:12:01
 * 
 */
public class OrientationSensorUtil {
	private SensorManager sensorManager = null;
	private Context context;
	private Sensor accelerometerSensor;
	private Sensor magnetic_fieldSensor;
	private Sensor orientationSensor;
	private RoundImageView roundImageView;
	private RelativeLayout layout;
	private DynamicViewGroup dynamicView;//显示动态事物的布局
	// 加速度传感器数据
	private float accValues[] = new float[3];
	// 地磁传感器数据
	private float magValues[] = new float[3];
	// 旋转矩阵，用来保存磁场和加速度的数据
	private float r[] = new float[9];
	// 模拟方向传感器的数据（原始数据为弧度）
	private float values[] = new float[3];
	private MySensorEventListener listener;

	public OrientationSensorUtil(Context context,
			RoundImageView roundImageView, RelativeLayout layout) {
		this.context = context;
		this.roundImageView = roundImageView;
		this.layout = layout;
		dynamicView=(DynamicViewGroup) layout.findViewById(R.id.dynamicView);
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		accelerometerSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetic_fieldSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		orientationSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		listener = new MySensorEventListener();

	}

	public float[] getValues() {
		return values;
	}

	public void onResume() {
		// sensorManager.registerListener(listener, accelerometerSensor,
		// SensorManager.SENSOR_DELAY_GAME);
		// sensorManager.registerListener(listener, magnetic_fieldSensor,
		// SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(listener, orientationSensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	public void onPause() {
		sensorManager.unregisterListener(listener);
	}

	/**
	 * 传感器监听事件
	 * 
	 * @ClassName: MySensorEventListener
	 * @说明:
	 * @author 刘红亮
	 * @date 2015-1-6 上午9:44:53
	 */
	private class MySensorEventListener implements SensorEventListener {
		private float m_predegree = 0;
		private float y_predegree = 0;
		private float currentDegree = 0;
		private float preValues0=0;
		private float preValues1=0;
		private long preTime0=0;
		private long preTime1=0;
		@Override
		public void onSensorChanged(SensorEvent event) {
			values = event.values;
			/** 动画效果 */
			// Log.e("hongliang",
			// "m_predegree:"+m_predegree+"   values[0]:"+values[0]+"    y_predegree:"+y_predegree);
			/**
			 * 转动转盘
			 */
			if (Math.abs(values[0] - m_predegree) >= 1) {// 转动5度及以上才更新

//				RotateAnimation animation = new RotateAnimation(y_predegree,
//						-values[0], Animation.RELATIVE_TO_SELF, 0.5f,
//						Animation.RELATIVE_TO_SELF, 0.5f);
//				animation.setDuration(1);
//				animation.setFillAfter(true);
//				roundImageView.startAnimation(animation);
//				y_predegree = -values[0];// 更新圆盘角度
				//实时更新圆盘
				roundImageView.update(values[0]);
				m_predegree = values[0];// 更新上一次
			}
		

			/**
			 * 手机方向变化，动态刷新界面，实现左右浮动效果
			 */
			if (Math.abs(values[0] - preValues0) >= 1) {// 转动2度及以上才更新
				if(preTime0==0){
					preTime0=SystemClock.uptimeMillis();
				}
				preValues0=values[0];
				dynamicView.refresh(values[0],values[1],SystemClock.uptimeMillis()-preTime0);
				preTime0=SystemClock.uptimeMillis();
			}
			/**
			 * 手机翻转变化，动态刷新界面，实现左右浮动效果
			 */
			if(Math.abs(values[1] - preValues1)>=1){
				if(preTime1==0){
					preTime1=SystemClock.uptimeMillis();
				}
				preValues1=values[1];
				dynamicView.refresh(values[0],values[1],SystemClock.uptimeMillis()-preTime1);
				preTime1=SystemClock.uptimeMillis();
			}


		}

		/**
		 * 监听加速度传感器和地磁传感器变化，构造出方向传感器
		 */
		// @Override
		// public void onSensorChanged(SensorEvent event) {
		// if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
		// accValues=event.values;
		// }
		// else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
		// magValues=event.values;
		// }
		// /**public static boolean getRotationMatrix (float[] R, float[] I,
		// float[] gravity, float[] geomagnetic)
		// * 填充旋转数组r
		// * r：要填充的旋转数组
		// * I:将磁场数据转换进实际的重力坐标中 一般默认情况下可以设置为null
		// * gravity:加速度传感器数据
		// * geomagnetic：地磁传感器数据
		// */
		// SensorManager.getRotationMatrix(r, null, accValues, magValues);
		// /**
		// * public static float[] getOrientation (float[] R, float[] values)
		// * R：旋转数组
		// * values ：模拟方向传感器的数据
		// */
		// SensorManager.getOrientation(r, values);
		// //float secondDegress=(float) Math.toDegrees(values[0]);
		//
		// //将弧度转化为角度
		// for(int i=0;i<3;i++){
		// values[i]=(float) Math.toDegrees(values[i]);
		// }
		//
		//
		// /**动画效果*/
		// if(Math.abs(values[0]-m_predegree)>=5){//转动5度及以上才更新
		//
		// RotateAnimation animation = new RotateAnimation(y_predegree,
		// -values[0],
		// Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		// animation.setDuration(200);
		// animation.setFillAfter(true);
		// roundImageView.startAnimation(animation);
		// y_predegree=-values[0];//更新圆盘角度
		// m_predegree=values[0];//更新上一次
		// }
		// Log.e("hongliang", "***********");
		//
		// TextView text=(TextView) layout.findViewById(R.id.text);
		// text.setText("");
		// for(Point point:points){
		//
		// if(point.angle>=(-30+values[0])&&point.angle<=(30+values[0])){
		// Log.e("hongliang", point.name);
		// text.setText(text.getText().toString()+" "+point.name);
		// }
		// }
		// }

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

	}
	public static boolean isSupportSensor(Context context){
		SensorManager manager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor = manager
				.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		if(sensor==null) return false;
		return true;
	}
}
