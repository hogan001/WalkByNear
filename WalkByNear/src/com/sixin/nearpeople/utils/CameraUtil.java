package com.sixin.nearpeople.utils;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;
/**
 * 
* @ClassName: CameraUtil 
* @Description: 摄像头工具类，用于打开摄像头，和关闭摄像头
* @author 刘红亮
* @date 2015-1-19 下午4:03:55 
*
 */
public class CameraUtil {
	private SurfaceView sView;
	private SurfaceHolder holder;
	Camera camera;
	boolean isPreview=false;
	public CameraUtil(SurfaceView sView){
		this.sView=sView;
	}
	/**
	 * 在界面显示时启动摄像头
	 */
	public void onResume(){
		holder=sView.getHolder();
		holder.addCallback(new Callback(){

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				initCamera();//打开摄像头
			}
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if(camera!=null){
					if (isPreview) camera.stopPreview();
					camera.release();
					camera = null;
				}
			}
		});
	}
	
	/**
	 * 在界面不可见时暂停摄像头去像
	 */
	public void onPause(){
		isPreview=false;
	}
	@SuppressWarnings("deprecation")
	private void initCamera() {
		if(!isPreview){
			try {
				camera=Camera.open();//默认打开后置摄像头
			} catch (Exception e) {
				
				Toast.makeText(sView.getContext(), "无法打开摄像头！", 0).show();
			}
			
		}
		if (camera != null && !isPreview){
			try{
				//设置在哪预览
				camera.setDisplayOrientation(90);
//				Camera.Parameters parameters = camera.getParameters();
//				parameters.setPreviewFrameRate(5); // 每秒5帧
//				parameters.setPictureFormat(PixelFormat.JPEG);// 设置照片的输出格式
//				parameters.set("jpeg-quality", 85);// 照片质量
//				camera.setParameters(parameters);
				camera.setPreviewDisplay(holder);  
				// 开始预览
				camera.startPreview();  
			}
			catch (Exception e){
				e.printStackTrace();
			}
			isPreview = true;
		}
	}
}
