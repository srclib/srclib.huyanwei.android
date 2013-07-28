package srclib.huyanwei.phonelistener;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SlideButton extends FrameLayout {
	private String TAG = "srclib.huyanwei.phonelistener.SlideButton";
	private Context mContext ;
	
	private FrameLayout 	mFrameLayout;
	private LayoutInflater 	mLayoutInflater;
	private ImageView 		mBackgroundImage;
	private LinearLayout 	mLinearLayout;
	private ImageView 		mSlideImage;
	private TextView  		mTextView;
	
	private Drawable mDrawable_bg;
	private Drawable mDrawable_off;
	private Drawable mDrawable_on;
	
	private int width  = 0 ;
	private int height = 0 ;
	
	private boolean state  = false ;
	private int pre_state  = (state==true)?1:0;
	
	
	private float down_x =  0.0f ;
	private float down_y =  0.0f ;
	
	private float init_left 	=  0.0f ;
	private float init_top 		=  0.0f ;
	private float init_right 	=  0.0f ;
	private float init_bottom 	=  0.0f ;
	
	private LayoutParams mLayoutParams ;
	
	public boolean getState()
	{
		return state;
	}
	
	public interface Callback
	{
		public void onStateChange(boolean state);
	};
	
	private ArrayList<Callback> Callbacks = new ArrayList<Callback>();
	
	public void setCallback(Callback object)
	{
		Callbacks.add(object);
	}
		
	public SlideButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		Log.d(TAG,"SlideButton(Context context, AttributeSet attrs, int defStyle)");
	}

	public SlideButton(Context context, AttributeSet attrs) {
		super(context, attrs);		
		// TODO Auto-generated constructor stub
		Log.d(TAG,"SlideButton(Context context, AttributeSet attrs)");
		
		mContext = context ;
		
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mFrameLayout = (FrameLayout)mLayoutInflater.inflate(R.layout.slidebutton,null);
		mFrameLayout.setBackgroundColor(0x00FFFFFF);

		mDrawable_bg = mContext.getResources().getDrawable(R.drawable.slide_bg);
		mDrawable_off = mContext.getResources().getDrawable(R.drawable.slide_off);
		mDrawable_on = mContext.getResources().getDrawable(R.drawable.slide_on);
		
		Log.d(TAG,"mDrawable_off.getMinimumWidth()="+mDrawable_off.getMinimumWidth());
		
		mBackgroundImage = (ImageView) mFrameLayout.findViewById(R.id.slide_area_bg);		 
		mBackgroundImage.setBackgroundDrawable(mDrawable_bg);
		
		mLinearLayout = (LinearLayout) mFrameLayout.findViewById(R.id.slide_area_touch);
		mLinearLayout.setBackgroundDrawable(mDrawable_off);
		
		mTextView     =  (TextView) mLinearLayout.findViewById(R.id.slide_text);
		mTextView.setText(R.string.value_off);
		
		this.addView(mFrameLayout);
	}

	public SlideButton(Context context) {
		//super(context);		
		this(context,null);		
		Log.d(TAG,"SlideButton(Context context)");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		
		//mFrameLayout.draw(canvas);
		
		super.draw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onLayout("+left+","+top+","+right+","+bottom+")");
		
		int i = 0 ;
		//for(i = 0 ; i < this.getChildCount();i++)
		{
			//this.getChildAt(i).layout(left, top, right, bottom);
		}
		
		super.onLayout(changed, left, top, right, bottom);
	}


	/** 
     * 比onDraw先执行 
     *  
     * 一个MeasureSpec封装了父布局传递给子布局的布局要求，每个MeasureSpec代表了一组宽度和高度的要求。 
     * 一个MeasureSpec由大小和模式组成 
     * 它有三种模式：UNSPECIFIED(未指定),父元素部队自元素施加任何束缚，子元素可以得到任意想要的大小; 
     *              EXACTLY(完全)，父元素决定子元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小； 
     *              AT_MOST(至多)，子元素至多达到指定大小的值。 
     *  
              * 　　它常用的三个函数： 　　 
     * 1.static int getMode(int measureSpec):根据提供的测量值(格式)提取模式(上述三个模式之一) 
     * 2.static int getSize(int measureSpec):根据提供的测量值(格式)提取大小值(这个大小也就是我们通常所说的大小)  
     * 3.static int makeMeasureSpec(int size,int mode):根据提供的大小值和模式创建一个测量值(格式) 
     */  
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		
		Log.d(TAG,"onMeasure("+width+","+height+")");
		
		setMeasuredDimension(width,height);

		for(int i= 0;i<getChildCount();i++){
			View v = getChildAt(i);
			Log.v(TAG, "measureWidth is " +v.getMeasuredWidth() + ",measureHeight is " +v.getMeasuredHeight());
			int widthSpec = 0;
			int heightSpec = 0;
			LayoutParams params = (LayoutParams) v.getLayoutParams();
			if(params.width > 0){
				widthSpec = MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY);
			}else if (params.width == -1) {
				widthSpec = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY);
			} else if (params.width == -2) {
				widthSpec = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.AT_MOST);
			}
			
			if(params.height > 0){
				heightSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
			}else if (params.height == -1) {
				heightSpec = MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.EXACTLY);
			} else if (params.height == -2) {
				heightSpec = MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.AT_MOST);
			}
			v.measure(widthSpec, heightSpec);
			
		}		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onSizeChanged("+w+","+h+","+oldw+","+oldh+")");
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float x = 0 ;
		float y = 0 ;
		
		float delta_x = 0.0f;
		float delta_y = 0.0f;
		
		switch(event.getAction())
		{
			case  MotionEvent.ACTION_DOWN:
				down_x = (float) event.getX();
				down_y = (float) event.getY();
				Log.d(TAG,"down x="+down_x+",y="+down_y);
				
				init_left 	=  mLinearLayout.getLeft();
				init_top 	=  mLinearLayout.getTop();
				init_right 	=  init_left + mLinearLayout.getWidth();
				init_bottom =  init_top + mLinearLayout.getHeight();				
				Log.d(TAG,"down init_left="+init_left+",init_top="+init_top+",init_right="+init_right+",init_bottom="+init_bottom);
				
				mLayoutParams = (LayoutParams) mLinearLayout.getLayoutParams();
				
				break;
			case  MotionEvent.ACTION_MOVE:
				x = (float) event.getX();
				y = (float) event.getY();
				
				delta_x = x - down_x ; //
				delta_y = 0 ;          // hor
				
				Log.d(TAG,"move x="+x+",y="+y+",delta_x="+delta_x);
				if(((init_left+delta_x) >= 0) && (((init_left+delta_x) < (mDrawable_off.getMinimumWidth()/8))))
				{
					if(pre_state == 1)
					{
						mLinearLayout.setBackgroundDrawable(mDrawable_off);					
						mTextView.setText(R.string.value_off);
						
						pre_state = 1-pre_state ;
						
						state = false;
						int i = 0 ;
						for(i=0;i<Callbacks.size();i++)
						{
							((Callback)Callbacks.get(i)).onStateChange(state);
						}
					}
					else
					{
						//mLayoutParams.width =
						mLinearLayout.setLayoutParams(mLayoutParams);
						
						mLinearLayout.layout((int)(init_left+delta_x), (int)init_top, (int)(init_right+delta_x), (int)init_bottom);
						mLinearLayout.invalidate();
					}
					
				}
				else if(((init_left+delta_x) >= (mDrawable_off.getMinimumWidth()/8) ) && (init_right+delta_x <= (width -1- (mDrawable_off.getMinimumWidth()/8))))
				{
					mLinearLayout.layout((int)(init_left+delta_x), (int)init_top, (int)(init_right+delta_x), (int)init_bottom);
					mLinearLayout.invalidate();
					//mLinearLayout.requestLayout();
				}
				else if( ((init_right+delta_x) > (width -1- (mDrawable_off.getMinimumWidth()/8))) 
						&& ((init_right+delta_x) <= (width -1)))
				{
					if(pre_state == 0)
					{
						mLinearLayout.setBackgroundDrawable(mDrawable_on);
						mTextView.setText(R.string.value_on);
						pre_state = 1-pre_state ;
						state = true;
						int i = 0 ;
						for(i=0;i<Callbacks.size();i++)
						{
							((Callback)Callbacks.get(i)).onStateChange(state);
						}
					}
					else
					{
						mLinearLayout.layout((int)(init_left+delta_x), (int)init_top, (int)(init_right+delta_x), (int)init_bottom);
						mLinearLayout.invalidate();
					}
					
				}
				
				break;
			case  MotionEvent.ACTION_UP:
				x = (float) event.getX();
				y = (float) event.getY();
				Log.d(TAG,"up x="+x+",y="+y);
				break;
			default:
				break;
		}
		return true ;
		//return super.onTouchEvent(event);
	}
}
