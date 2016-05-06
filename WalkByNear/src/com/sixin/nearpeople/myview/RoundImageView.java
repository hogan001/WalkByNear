package com.sixin.nearpeople.myview;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sixin.nearpeople.gaode.GaoDeMapManager;
import com.sixin.nearpeople.utils.LatlngUtil;
import com.sixin.nearpeople.utils.LatlngUtil.Point;
/**
 * 
* @ClassName: RoundImageView 
* @Description: 自定义圆形转盘，利用旋转动画根据方向传感器转动
* @author 刘红亮
* @date 2015-1-19 下午3:56:45 
*
 */
public class RoundImageView extends View{
	//背景图片
	private Bitmap backgroundImage;
	private  int selectPosition=-1;
	//
	Paint paint;
	List<Point> points;
	//圆盘属性相关
	private int roundColor;
	private int lineColor;
	private int pointColor;
	//扇形属性相关
	private int startAngle;
	private int sweepAngle;
	private int sectorColor;
	
	private Context context;
	public RoundImageView(Context context) {
		this(context,null);
		
	}
	public RoundImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context=context;
		//backgroundImage=BitmapFactory.decodeResource(getResources(), R.drawable.area_icon);
//		TypedArray typeArray=context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomRoundView, defStyleAttr, 0);
//		int n = typeArray.getIndexCount();
//		for(int i=0;i<n;i++){
//			int attr=typeArray.getIndex(i);
//			
//			switch (attr) {
//				case R.styleable.CustomRoundView_roundColor:
//					roundColor=typeArray.getColor(attr, Color.CYAN);
//					break;	
//				case R.styleable.CustomRoundView_lineColor:
//					lineColor=typeArray.getColor(attr, Color.BLUE);
//					break;	
//				case R.styleable.CustomRoundView_pointColor:
//					pointColor=typeArray.getColor(attr, Color.GREEN);
//					break;
//					
//				default:
//					break;
//			}
//			paint=new Paint();
//			typeArray.recycle();
//		}
		//设置圆盘默认颜色
		roundColor=Color.parseColor("#88000000");
		lineColor=Color.parseColor("#FFFFFF");
		pointColor=Color.parseColor("#FF0000");
		
		//设置扇形默认参数
		startAngle=240;
		sweepAngle=60;
		sectorColor=Color.parseColor("#668DEEEE");
		paint=new Paint();
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		paint =new Paint();
//		canvas.drawBitmap(backgroundImage, 0, 0, paint);
		paint.setAntiAlias(true);//设置消除锯齿
		
		paint.setColor(lineColor);//设置线的颜色
		paint.setStyle(Paint.Style.FILL);//设置实心
		//画出最大的实心圆
		paint.setColor(roundColor);//设置填充的颜色
		paint.setStyle(Paint.Style.FILL);//设置实心
		canvas.drawCircle(getWidth()/2, getHeight()/2, getWidth()/2-2, paint);
		//设置线的宽度
		paint.setStrokeWidth(2);
		//画出最大的空心圆
		paint.setStyle(Paint.Style.STROKE);//设置空心
		paint.setColor(lineColor);//设置填充的颜色
		//-2是为了留出点间距，防止出现看起来有边缘被切的效果，和SectorImageView.java中的-2同理
		canvas.drawCircle(getWidth()/2, getHeight()/2, getWidth()/2-2, paint);
		
		//画出次大的圆
		canvas.drawCircle(getWidth()/2, getHeight()/2, getWidth()/3-2, paint);
		//画出最小的圆
		canvas.drawCircle(getWidth()/2, getHeight()/2, getWidth()/6-2, paint);
		
		
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		if(points!=null&&points.size()>0){
			for (int i=0;i<points.size();i++) {
				//画出未选中点
				if(i!=this.selectPosition){
					canvas.drawCircle((float)points.get(i).x, (float)points.get(i).y, 3, paint);
				}
			}
			//最后画出选中点，防止点太多，选中点被覆盖，看不见红点
			if(this.selectPosition>=0){
				paint.setColor(Color.RED);
				canvas.drawCircle((float)points.get(this.selectPosition).x, (float)points.get(this.selectPosition).y, 3, paint);
				paint.setColor(Color.WHITE);
			}
		}
		//绘制扇形区域
		//画出扇形指示器
		paint.setColor(sectorColor);
		paint.setStyle(Paint.Style.FILL);
		//RectF oval=new RectF(getWidth(),getWidth(),getWidth(),getWidth());
		//构建扇形矩形，-2是为了留出点间距，防止出现看起来有边缘被切的效果，和RoundImageView.java中的-2同理
		RectF oval=new RectF(0,0,getWidth()-2,getWidth()-2); 
		canvas.drawArc(oval, startAngle, sweepAngle, true, paint);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width;
		int height ;
		//精确指定
		if (widthMode == MeasureSpec.EXACTLY)
		{
			width = widthSize;
		} else  //环绕内容时
		{	//默认大小为屏幕的三分之一
			width=Math.min(widthSize,heightSize)/3;
		}

		if (heightMode == MeasureSpec.EXACTLY)
		{
			height = heightSize;
		} else
		{
			//默认大小为屏幕的三分之一
			height=Math.min(widthSize,heightSize)/3;

		}
		//将宽度和高度设置成一样大
		width=Math.max(width, height);
		height=Math.max(width, height);
		setMeasuredDimension(width, height);
	}
	
	
	public int getRoundColor() {
		return roundColor;
	}
	/**
	 * 
	 * @param roundColorString  请参照Color.parseColor(colorString)方法使用。
	 */
	public void setRoundColor(String roundColorString) {
		this.roundColor = Color.parseColor(roundColorString);
	}
	public int getLineColor() {
		return lineColor;
	}
	/**
	 * 
	 * @param lineColorString  请参照Color.parseColor(colorString)方法使用。
	 */
	public void setLineColor(String lineColorString) {
		this.lineColor = Color.parseColor(lineColorString);
	}
	public int getPointColor() {
		return pointColor;
	}
	/**
	 * 
	 * @param pointColorString  请参照Color.parseColor(colorString)方法使用。
	 */
	public void setPointColor(String pointColorString) {
		this.pointColor =Color.parseColor(pointColorString);
	}
	public int getSectorColor() {
		return sectorColor;
		
	}
	/**
	 * 
	 * @param sectorColorString  请参照Color.parseColor(colorString)方法使用。
	 */
	public void setSectorColor(String sectorColorString) {
		this.sectorColor = Color.parseColor(sectorColorString);
	}
//	public void showNearthings(List<Point> points){
//		
//		this.points = points;
//		invalidate();
//	}
	//根据角度动态更新盘上的点
	public void update(float currentAngle){
		// 将之转化为屏幕坐标在1500m范围内，因圆盘有边距，故适当加大一点转化范围，防止点跑出圆外
		LatlngUtil latlngUtil = new LatlngUtil(1550, GaoDeMapManager.getInstance((Activity) context).getMyLatLng());
		this.points=latlngUtil.getXYS(getMeasuredWidth()/2, GaoDeMapManager.getInstance((Activity) context).getPeopleList(),currentAngle);
		invalidate();
	}
	/**
	 * 让当前选中点变为红点
	 * @param position
	 */
	public void setSelectPoint(int position){
		this.selectPosition=position;
		invalidate();
	}
}
