package com.sixin.nearpeople.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.hogan.walkbynear.R;
import com.sixin.nearpeople.entity.People;
import com.sixin.nearpeople.utils.CommonUtil;

/**
 * 用于显示目标事物的自定义view
 * 
 * @ClassName: PeopleView
 * @Description: TODO
 * @author 刘红亮
 * @date 2015-1-17 下午3:54:11
 * 
 */
public class PeopleView extends View {
	private static final String TAG = "PeopleView";
	// 目标事物的信息
	private People people;
	private int index;

	private Paint paint;
	private float padding;
	//绘制文字属性相关
	private float nameTextSize;
	private int nameTextColor;
	private float distanceTextSize;
	private int distanceTextColor;
	private int maxTextNum;
	//两个显示字符串的矩形
	Rect nameBounds;
	Rect distanceBounds;
	// 背景图片
	private Bitmap bitmap;
	private int backGroundImageId;

	public People getPeople() {
		return people;
	}

	private Context context;
	
	public PeopleView(Context context,int backGroundImageId) {
		super(context, null, 0); 
		this.backGroundImageId=backGroundImageId;
		this.context = context;
		paint = new Paint();
		int id=R.drawable.bg_markview;

		bitmap = BitmapFactory.decodeResource(getResources(),
				backGroundImageId);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		paint.setTextSize(this.nameTextSize);
		paint.setColor(this.nameTextColor);//设置字体颜色为#4d4d4d
		String name=null;
		if(people.name.length()>this.maxTextNum){
			name=people.name.substring(0, this.maxTextNum-3)+"...";
		}else{
			name=people.name;
		}
		
		
//		Log.e("hongliang", "Mheight:"+rect.height()+"   padding:"+padding);
		canvas.drawText(name, getMeasuredWidth()/2-nameBounds.width()/2,  bitmap.getHeight() *1/ 4+nameBounds.height()/2, paint);
//		
		paint.setColor(this.distanceTextColor);
		paint.setTextSize(this.distanceTextSize);
		
		canvas.drawText(people.getDistanceStr(), getMeasuredWidth()/2-distanceBounds.width()/2, bitmap.getHeight() *3/ 4+distanceBounds.height()/2,paint);
//		
		super.onDraw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		paint.setTextSize(this.nameTextSize);
		//最小宽度
		Rect minWidth=new Rect();
		String minStr="最少占用宽度";
		paint.getTextBounds(minStr, 0, minStr.length(), minWidth);
		
		nameBounds = new Rect();
		String name=null;
		if(people.name.length()>this.maxTextNum){
			name=people.name.substring(0, this.maxTextNum-3)+"...";
		}else{
			name=people.name;
		}
		
		paint.getTextBounds(name, 0, name.length(), nameBounds);
		// Log.e("hongliang", "bounds:" + bounds + "   bitmap:" + bitmap);
		distanceBounds = new Rect();
		paint.setTextSize(this.distanceTextSize);
		paint.getTextBounds(people.getDistanceStr(), 0, (people.getDistanceStr()).length(), distanceBounds);

		int width = Math.max(nameBounds.width(), distanceBounds.width());
		//与最小的宽度比较，取最大
		width=Math.max(width, minWidth.width());
		setMeasuredDimension((int) (width + padding* 2), bitmap.getHeight());
	}

	/**
	 * 设置目标事物，在使用之前必须设置
	 * 
	 * @param people
	 */
	public void setPeople(People people, int index) {
		this.people = people;
		this.index = index;

	}

	public int getIndex() {
		return index;
	}

	public float getPadding() {
		return padding;
	}
	/**
	 * 设置文字在左右方向上的内边距，单位dp
	 */
	public void setPadding(float padding) {
		
		this.padding = CommonUtil.dip2px(context, padding);//设置边距
	}

	public float getNameTextSize() {
		return nameTextSize;
	}
	/**
	 * 设置显示名字的文字的大小，单位px
	 */
	public void setNameTextSize(float nameTextSize) {
		this.nameTextSize = nameTextSize;
	}


	
	public float getDistanceTextSize() {
		return distanceTextSize;
	}
	/**
	 * 设置显示距离的文字的大小，单位px
	 * @param distanceTextSize
	 */
	public void setDistanceTextSize(float distanceTextSize) {
		this.distanceTextSize = distanceTextSize;
	}
	public int getNameTextColor() {
		return nameTextColor;
	}
	/**
	 * 设置显示名字的文字的颜色，16进制整数，如白色不透明：0xFFFFFFFF
	 * @param nameTextColor
	 */
	public void setNameTextColor(int nameTextColor) {
		this.nameTextColor = nameTextColor;
	}

	public int getDistanceTextColor() {
		return distanceTextColor;
	}
	/**
	 * 设置显示距离的文字的颜色，16进制整数，如白色不透明：0xFFFFFFFF
	 */
	public void setDistanceTextColor(int distanceTextColor) {
		this.distanceTextColor = distanceTextColor;
	}
	
	public int getMaxTextNum() {
		return maxTextNum;
	}
	/**
	 * 设置显示文字的最大数量，若超过则以...代替
	 * @param maxTextNum
	 */
	public void setMaxTextNum(int maxTextNum) {
		this.maxTextNum = maxTextNum;
	}


	

}
