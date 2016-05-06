package com.sixin.nearpeople.utils;

import android.content.Context;
import android.os.SystemClock;
/**
 * 通过速度计算位移的工具类
* @ClassName: MyScroller 
* @Description: TODO
* @author 刘红亮
* @date 2015-1-21 下午5:50:02 
*
 */
public class MyScroller {

	
	private double startX;
	private double startY;
	private double distanceX;
	private double distanceY;
	
	/**
	 * 开始执行绘制的时间
	 */
	private long startTime;
	/**
	 * 判断是否正在执行绘制
	 * true 是还在运行
	 * false  已经停止
	 */
	private boolean isFinish;

	public MyScroller(Context ctx){
		
	}

	/**
	 * 开移移动
	 * @param startX	开始时的方向角度
	 * @param startY	开始时的手机顶部翻转角度
	 * @param disX		X方向 要移动的度数
	 * @param disY		Y方向 要移动的度数
	 */
	public void startScroll(double startX, double startY, double disX, double disY,long duration) {
		this.startX = startX;
		this.startY = startY;
		this.distanceX = disX;
		this.distanceY = disY;
		this.startTime = SystemClock.uptimeMillis();
		
		this.isFinish = false;
		this.duration=(int) duration+200;
	}

	/**
	 * 默认运行的时间
	 * 毫秒值
	 */
	private int duration = 100;
	/**
	 * 当前的X值
	 */
	private long currX;
	
	/**
	 * 当前的Y值
	 */
	private long currY;
	
	/**
	 * 计算一下当前的运行状况
	 * 返回值：
	 * true  还在运行
	 * false 运行结束
	 */
	public boolean computeScrollOffset() {

		if (isFinish) {
			return false;
		}

		// 获得所用的时间
		long passTime = SystemClock.uptimeMillis() - startTime;

		// 如果时间还在允许的范围内
		if (passTime < duration) {
			
			// 当前的位置  =  开始的位置  +  移动的距离（距离 = 速度*时间）
			currX = (long) (startX + distanceX * passTime / duration);
			
			currY = (long) (startY + distanceY * passTime / duration);

		} else {
			currX = (long) (startX + distanceX);
			currY = (long) (startY + distanceY);
			isFinish = true;
		}
		return true;
	}

	public long getCurrX() {
		return currX;
	}

	public void setCurrX(long currX) {
		this.currX = currX;
	}

	public long getCurrY() {
		return currY;
	}

	public void setCurrY(long currY) {
		this.currY = currY;
	}
	
	
}
