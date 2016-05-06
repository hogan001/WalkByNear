package com.sixin.nearpeople.utils;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 
 * @ClassName: Rotate3dAnimation
 * @Description: 利用Camera加Matrix实现的3d绕自身中心点的旋转动画
 * @author 刘红亮
 * @date 2015-1-23 上午9:45:03
 * 
 */
public class Rotate3dAnimation extends Animation {
	// 绕x,y,z轴的起始度数和终止度数
	private float mFromDegreeX;
	private float mToDegreeX;
	private float mFromDegreeY;
	private float mToDegreeY;
	private float mFromDegreeZ;
	private float mToDegreeZ;
	// 转动的中心点
	private float mCenterX;
	private float mCenterY;
	// camera加Matrix实现3d旋转
	private Camera mCamera;
	/**
	 * 根据x,y,z轴的旋转度数初始化,若不需要绕某轴旋转只需将该轴起始度数和终止度数都设置为0即可
	 * @param fromDegreeX    绕X轴的起始度数
	 * @param toDegreeX      绕X轴的终止度数
	 * @param fromDegreeY
	 * @param toDegreeY
	 * @param fromDegreeZ
	 * @param toDegreeZ
	 */
	public Rotate3dAnimation(float fromDegreeX, float toDegreeX,
			float fromDegreeY, float toDegreeY, float fromDegreeZ,
			float toDegreeZ) {
		this.mFromDegreeX=fromDegreeX;
		this.mToDegreeX=toDegreeX;
		this.mFromDegreeY=fromDegreeY;
		this.mToDegreeY=toDegreeY;
		this.mFromDegreeZ=fromDegreeZ;
		this.mToDegreeZ=toDegreeZ;
	}
	/**
	 * 初始化转动的中心点，和初始化Camera
	 */
	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCenterX = width / 2;
		mCenterY = height / 2;
		mCamera = new Camera();
	}
	/**
	 * 控制旋转
	 */
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		//算出旋转量
		final float fromDegreeX = mFromDegreeX;
		float degreeX = fromDegreeX + (mToDegreeX - mFromDegreeX)
				* interpolatedTime;
		final float fromDegreeY = mFromDegreeY;
		float degreeY = fromDegreeY + (mToDegreeY - mFromDegreeY)
				* interpolatedTime;
		final float fromDegreeZ = mFromDegreeZ;
		float degreeZ = fromDegreeZ + (mToDegreeZ - mFromDegreeZ)
				* interpolatedTime;

		final Matrix matrix = t.getMatrix();
	//	Log.e("hongliang", "X="+degreeX+"   y="+degreeY+"   z="+degreeZ);
		mCamera.save();
		mCamera.rotateX(degreeX);
		mCamera.rotateY(degreeY);
		mCamera.rotateZ(degreeZ);
		
		mCamera.getMatrix(matrix);
		mCamera.restore();
		// preTranslate函数是在旋转前移动而postTranslate是在旋转完成后移动，主要作用是让对象围绕自己的中心二旋转。
		// 即设置中心点
		matrix.preTranslate(-mCenterX, -mCenterY);
		matrix.postTranslate(mCenterX, mCenterY);
	}
}
