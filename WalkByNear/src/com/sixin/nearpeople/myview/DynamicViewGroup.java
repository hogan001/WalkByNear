package com.sixin.nearpeople.myview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hogan.walkbynear.R;
import com.sixin.nearpeople.utils.MyScroller;
import com.sixin.nearpeople.utils.Rotate3dAnimation;

/**
 * 
 * @ClassName: DynamicViewGroup
 * @Description: 本类是一个自定义的ViewGroup 特点：根据角度实时动态显示子view,子View不重叠显示
 * @author 刘红亮
 * @date 2015-1-17 下午3:17:45
 * 
 */
public class DynamicViewGroup extends ViewGroup {
	// 要显示的数据
	private List<PeopleView> peopleViews;
	//private List<People> peoples;
	// 要显示的数据在屏幕上的起点坐标
	private List<StartPoint> startPoints;
	private Context context;
	// 默认在屏幕中的放置高度
	private int defaultHeight;
	// 本布局的宽度
	private int defaultWidth;
	// 每度的单位宽度
	private double unitWidth;
	// 当前的方向度数（0~360）
	private double currentAngle = 0;
	// 默认的两个view的间距值
	private int margin = 2;
	// 当前的手机顶部翻转角度（0~-180）
	private float shangxiaAngle = 0;
	//标签平移控制器
	private MyScroller myScroller;
	//指示器
	private ImageView arrow;
	//当前选中的下标
	private int selectPosition=-1;
	//指示器跟随角度控制
	private Float fromDegreeX=null;
	private Float fromDegreeZ=null;
	
	public DynamicViewGroup(Context context) {
		this(context, null);
	}

	public DynamicViewGroup(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * 创建DynamicViewGroup，并初始化子View,拿到要显示的信息
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public DynamicViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// 从Application中获取要显示的数据
		//peoples = MyApplication.getInitial().getPeopleList();
		this.context = context;
		// initView();
		myScroller=new MyScroller(context);

	}

	/**
	 * 
	 * @param peoples
	 */
	public void setChildView(List<PeopleView> peopleViews) {
		this.peopleViews=peopleViews;
		//先清空数据
		this.removeAllViews();
		for (PeopleView peopleView : peopleViews) {
			this.addView(peopleView);
		}
		//Log.e("hongliang", "setChildView");
		//加入指示器
		
		arrow=new ImageView(context);
		arrow.setBackgroundResource(R.drawable.arrow);
		this.addView(arrow);
		
		requestLayout();
	}

	/**
	 * 确定每个子View建议大小
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int childCount = getChildCount()-1;//因为多加入了指示器，所以减一

		int specSize_Width = MeasureSpec.getSize(widthMeasureSpec);// 当前组件大小宽度
		// Log.e("hongliang", "onMeasure"+specSize_Width);
		int specSize_Height = MeasureSpec.getSize(heightMeasureSpec);
		defaultHeight = specSize_Height / 2;// 得到默认的高度,即屏幕的一半
		defaultWidth = specSize_Width;// 得到组件的宽度

		setMeasuredDimension(specSize_Width, specSize_Height);
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.measure(400, 400);
			this.measureChild(child, widthMeasureSpec, heightMeasureSpec);
		}
		//为arrow分配大小
		getChildAt(childCount).measure(400, 400);
		this.measureChild(getChildAt(childCount), widthMeasureSpec, widthMeasureSpec);
	}

	/**
	 * 确定子view在布局中的具体位置
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		// 算出每一个可绘制的起点坐标：
		initStartPoint();

		int childCount = getChildCount()-1;//因为多加入了指示器，所以减一
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			// 重新绘制：
			child.layout(startPoints.get(i).x, startPoints.get(i).y,
					startPoints.get(i).x + child.getMeasuredWidth(),
					startPoints.get(i).y + child.getMeasuredHeight());
		}
		if(selectPosition!=-1){//已选择标签，显示指示器
			arrow.setVisibility(View.VISIBLE);
			arrow.layout(getMeasuredWidth()/2-arrow.getMeasuredWidth()/2, getMeasuredHeight()*3/4, getMeasuredWidth()/2+arrow.getMeasuredWidth()/2, getMeasuredHeight()*3/4+arrow.getMeasuredHeight());
			float toDegreeZ=(float) (currentAngle-peopleViews.get(selectPosition).getPeople().getAngle());
			if(fromDegreeX==null){
				fromDegreeX=shangxiaAngle;
				fromDegreeZ=0f;
				
			}
			//防止跑到-90度，出现，指示器不见的情况
			if(shangxiaAngle<-70){
				shangxiaAngle=-70;
			}
			//3d旋转动画控制指示器
			Rotate3dAnimation animation=new Rotate3dAnimation(fromDegreeX, shangxiaAngle, 0, 0, fromDegreeZ, toDegreeZ);
			animation.setDuration(5);
			animation.setFillAfter(true);
			arrow.startAnimation(animation);
			this.fromDegreeX=shangxiaAngle;
			this.fromDegreeZ=toDegreeZ;
		}else{//隐藏指示器
			arrow.layout(0, 0, 0, 0);
		}
	}

	/**
	 * 思路： 将屏幕划分成60份，即每度应该占多少像素，实时获取当前手机的角度，根据目标位置的角度算出中心点的x坐标
	 * 中心点y坐标默认为屏幕中间，根据手机顶部抬起角度，动态更新 重叠问题解决： 步骤一 ：每放入一个view时，默认中心y坐标为屏幕中间， 步骤二：
	 * 与已经放好的view一一做矩形相交判断 如果都不相交则说明，这个位置可以，直接返回即可
	 * 如果存在一个与之相交，说明此位置出现重叠，则y坐标往上一个view的高度重复执行步骤二。
	 */
	private void initStartPoint() {

		startPoints = new ArrayList<StartPoint>();
		// 计算每一角度所占的宽度,默认扫描范围为60度：
		unitWidth = defaultWidth / 60.;
		int childCount = getChildCount()-1;//因为多加入了指示器，所以减一
		int shangxiaCha = (int) ((-90 - shangxiaAngle) * (defaultHeight / 70));
		for (int i = 0; i < childCount; i++) {
			// 算出相差角度。
			PeopleView peopleView = (PeopleView) getChildAt(i);
			double left = (currentAngle - 30 + 360) % 360;// 算出左边缘
			double chaAngle = (peopleViews.get(i).getPeople().getAngle() - left ) % 360;
			StartPoint startPoint = new StartPoint();
			// 确定中心点坐标
			startPoint.x = (int) (chaAngle * unitWidth);
			// 起点x坐标
			startPoint.x -= peopleView.getMeasuredWidth() / 2;
			// 去搜索可用的起点y坐标。
			// Log.e("hongliang", "defaultHeight:"+defaultHeight);
			startPoint.y = seachY(i, startPoint.x, defaultHeight + shangxiaCha);
			startPoints.add(startPoint);
			// Log.e("hongliang", "子view的大小："+peopleView.getMeasuredHeight());
		}
	}

	/**
	 * 递归搜索可用的y坐标
	 * 
	 * @param index
	 * @param x
	 * @param y
	 * @return
	 */
	private int seachY(int index, int x, int y) {
		// 构建矩形一，并且加上默认边距值
		Rect r1 = new Rect(x, y, x + getChildAt(index).getMeasuredWidth(), y
				+ getChildAt(index).getMeasuredHeight());
		for (int i = 0; i < index; i++) {
			View view = getChildAt(i);
			Rect r2 = new Rect(startPoints.get(i).x, startPoints.get(i).y,
					startPoints.get(i).x + view.getMeasuredWidth(),
					startPoints.get(i).y + view.getMeasuredHeight());
			if (isRectCross(r1, r2)) {// 相交则继续往上搜索
				return seachY(index, x, y
						- getChildAt(index).getMeasuredHeight() - this.margin);
			}
		}
		// 出来了说明找到不相交的位置
		return y;
	}
	public void refresh(double currentAngle, double shangxiaAngle,long duration ) {
		//此处是为了解决0度附近的反向多转一圈问题
		int chaX=(int) (currentAngle-this.currentAngle);
		if(Math.abs(chaX)>=250){//瞬间变化太大，说明发生了反向旋转，可以使之在肉眼不可察觉的时间上让之转过
			duration=-199;//由于后面设置时间默认多加了200,所以最终时间为-199+200=1ms
		}
		//开启控制器，让控制器去平缓更新
		myScroller.startScroll(this.currentAngle, this.shangxiaAngle, currentAngle-this.currentAngle, shangxiaAngle-this.shangxiaAngle,duration);
		requestLayout();

	}
	/**
	 * requestLayout方法会触发此方法执行
	 */
	@Override
	public void computeScroll() {
		if(myScroller.computeScrollOffset()){//控制器控制currentAngle步进至最终值
			currentAngle = (int) myScroller.getCurrX();
			shangxiaAngle = (int) myScroller.getCurrY();
			//Log.e("hongliang", "newX::"+currentAngle+"  newY::"+shangxiaAngle);
			//invalidate();
			requestLayout();//重新加载
		};
		
	}

	/**
	 * 用于存放子view的起始坐标
	* @ClassName: StartPoint 
	* @Description: TODO
	* @author 刘红亮
	* @date 2015-1-21 下午5:45:10 
	*
	 */
	private class StartPoint {
		int x;
		int y;
	}
	/**
	 * 矩形类
	* @ClassName: Rect 
	* @Description: TODO
	* @author 刘红亮
	* @date 2015-1-21 下午5:45:56 
	*
	 */
	private static class Rect {
		double x1;
		double y1;
		double x2;
		double y2;

		public Rect(double x1, double y1, double x2, double y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		public Rect() {
		}

	}

	/**
	 * 两个矩形相交的条件:两个矩形的重心距离在X和Y轴上都小于两个矩形长或宽的一半之和.这样,分两次判断一下就行了.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return true ：为相交 false ：不相交（包括贴着）
	 */
	public static boolean isRectCross(Rect r1, Rect r2) {
		return Math.abs((r1.x1 + r1.x2) / 2 - (r2.x1 + r2.x2) / 2) < ((r1.x2
				+ r2.x2 - r1.x1 - r2.x1) / 2)
				&& Math.abs((r1.y1 + r1.y2) / 2 - (r2.y1 + r2.y2) / 2) < ((r1.y2
						+ r2.y2 - r1.y1 - r2.y1) / 2);
	}

	/**
	 * 让当前选中的文字背景变为红色,让之前的变回原来背景
	 * 
	 * @param position
	 */
	public void setSelectPoint(int position) {
		PeopleView peopleView=null;
		//将原来的选择状态变为未选中
		if(selectPosition!=-1){
			peopleView = (PeopleView) getChildAt(selectPosition);
			peopleView.setBackgroundResource(R.drawable.bg_markview);
		}
		if(position!=-1){//设置当前选中
			peopleView = (PeopleView) getChildAt(position);
			peopleView.setBackgroundResource(R.drawable.bg_markview_selected);
		}
		this.selectPosition=position;
		requestLayout();
	}

}
