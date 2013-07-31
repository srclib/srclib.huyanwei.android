package srclib.huyanwei.phonelistener;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
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

public class SlideButton extends View {
	
	private String TAG = "srclib.huyanwei.phonelistener.SlideButton";

	private boolean DBG = true;
	
	private Context mContext ;
	
	// ȷ�� �ؼ��Ŀռ��С
	private int width_mode 	= 0 ;
	private int height_mode = 0 ;		
	
	private int width_size  = 0 ;  // �ؼ��Ŀ��
	private int height_size = 0 ;  // �ؼ��ĸ߶�

	// img state size 
	int img_bg_w 		= 0 ;     // ��ʵ��ͼƬ�Ĵ�С
	int img_bg_h 		= 0 ;	  // ��ʵ��ͼƬ�Ĵ�С
	int img_block_w 	= 0 ;     // ��ʵ��ͼƬ�Ĵ�С
	int img_block_h		= 0 ;     // ��ʵ��ͼƬ�Ĵ�С
	
	// attrs
	private String         SlideButton_text_off			= null;
	private String         SlideButton_text_on			= null;
	private ColorStateList SlideButton_text_off_color 	= null;
	private ColorStateList SlideButton_text_on_color 	= null;
	private int            SlideButton_text_size  		= 15;
	private int            SlideButton_block_size  		= 60;
	private boolean        SlideButton_value  			= false;
	
	//����״̬ͼ
	Bitmap img_bg,img_off, img_on;
	
	public boolean getValue()
	{
		return SlideButton_value;
	}
	
	public void setValue(boolean arg)
	{
		SlideButton_value = arg;
		
		this.invalidate();
	}
	
	// listener
	public static interface OnSwitchChangedListener
	{
		/**
		 * ״̬�ı� �ص�����
		 * @param status  SWITCH_ON��ʾ�� SWITCH_OFF��ʾ�ر�
		 */
		public abstract void onSwitchChanged(SlideButton obj, boolean status);
	}
	
	private ArrayList<OnSwitchChangedListener> mOnSwitchChangedListener = new ArrayList<OnSwitchChangedListener>();
	
	public void setOnSwitchChangedListener(OnSwitchChangedListener object)
	{
		mOnSwitchChangedListener.add(object);
	}
	
	public void init(Context context)
	{
		mContext = context ;
		
		Resources res = mContext.getResources();
		img_bg = BitmapFactory.decodeResource(res, R.drawable.slide_bg);
		img_off = BitmapFactory.decodeResource(res, R.drawable.slide_off);
		img_on = BitmapFactory.decodeResource(res, R.drawable.slide_on);

		img_bg_w  = img_bg.getWidth();
		img_bg_h = img_bg.getHeight();		
		img_block_w = img_off.getWidth();
		img_block_h = img_off.getHeight();
		
		Log.d(TAG,"["+img_bg_w+","+img_bg_h+","+img_block_w+","+img_block_h+"]");
		
	}
	
	public SlideButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		// TODO Auto-generated constructor stub
		if(DBG)
		{
			Log.d(TAG,"SlideButton(Context context, AttributeSet attrs, int defStyle)");
		}
		
		//R.style ��  R.styleable ʲô���� ��styleable��ʾ���ԣ�style/attr ��������. ûָ���ľ�Ӧ����ȱʡ�ġ�		
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.SlideButtomStyle,defStyle,0);
		
		/*
		 * ��֪��Ϊʲô  ���ַ����������
		int color = a.getColor(R.styleable.SlideButtomStyle_textColor, 0xffffffff);
		int size = a.getInt(R.styleable.SlideButtomStyle_textSize, 15);
		boolean state = a.getBoolean(R.styleable.SlideButtomStyle_state, false);
		
		Log.d(TAG,"color="+color);
		Log.d(TAG,"size="+size);
		Log.d(TAG,"state="+state);
		*/
		
		
		int n = a.getIndexCount();
		for(int i = 0 ; i < n ; i++)
		{
			int attr = a.getIndex(i);
			switch(attr)
			{
			
				case R.styleable.SlideButtomStyle_text_off:
					SlideButton_text_off = a.getString(attr);
					break;
				case R.styleable.SlideButtomStyle_text_on:
					SlideButton_text_on = a.getString(attr);
					break;
				case R.styleable.SlideButtomStyle_text_off_color:
					SlideButton_text_off_color = a.getColorStateList(attr);
					break;
				case R.styleable.SlideButtomStyle_text_on_color:
					SlideButton_text_on_color = a.getColorStateList(attr);
					break;
				case R.styleable.SlideButtomStyle_text_size:
					SlideButton_text_size = a.getDimensionPixelSize(attr, SlideButton_text_size);
					break;
				case R.styleable.SlideButtomStyle_block_size:
					SlideButton_block_size = a.getDimensionPixelSize(attr, SlideButton_block_size);
					break;
				case R.styleable.SlideButtomStyle_value:
					SlideButton_value = a.getBoolean(attr, SlideButton_value);
					break;
				default:
					break;
			}
		}
		
		Log.d(TAG,"SlideButton_text_off="+SlideButton_text_off);
		Log.d(TAG,"SlideButton_text_on="+SlideButton_text_on);
		Log.d(TAG,"SlideButton_text_off_color="+SlideButton_text_off_color);
		Log.d(TAG,"SlideButton_text_on_color="+SlideButton_text_on_color);
		Log.d(TAG,"SlideButton_text_size="+SlideButton_text_size);
		Log.d(TAG,"SlideButton_block_size="+SlideButton_block_size);
		Log.d(TAG,"SlideButton_value="+SlideButton_value);
		
		a.recycle();
				
		init(context);
	}

	public SlideButton(Context context, AttributeSet attrs) {
		//super(context, attrs);
		this(context,attrs,R.attr.SlideButtomDefaultStyle); // �����Լ��Ĺ��캯����
		// TODO Auto-generated constructor stub
		
		if(DBG)
		{
			Log.d(TAG,"SlideButton(Context context, AttributeSet attrs)");
		}		
	}

	public SlideButton(Context context) {
		//super(context);		
		this(context,null); // �����Լ��Ĺ��캯����
		
		if(DBG)
		{
			Log.d(TAG,"SlideButton(Context context)");
		}
		// TODO Auto-generated constructor stub
	}

	public void drawBitmap(Canvas canvas, Rect src, Rect dst, Bitmap bitmap)
	{
		dst = (dst == null ? new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()) : dst);
		Paint paint = new Paint();
		canvas.drawBitmap(bitmap, src, dst, paint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float x = 0 ;
		float y = 0 ;
		
		float delta_x = 0.0f;
		float delta_y = 0.0f;
		/*
		switch(event.getAction())
		{
			case  MotionEvent.ACTION_DOWN:
				down_x = (float) event.getX();
				down_y = (float) event.getY();
				if(DBG)
				Log.d(TAG,"down x="+down_x+",y="+down_y);
				
				init_left 	=  mLinearLayout.getLeft();
				init_top 	=  mLinearLayout.getTop();
				init_right 	=  init_left + mLinearLayout.getWidth();
				init_bottom =  init_top + mLinearLayout.getHeight();
				
				if(DBG)
					Log.d(TAG,"down init_left="+init_left+",init_top="+init_top+",init_right="+init_right+",init_bottom="+init_bottom);
				

				
				break;
			case  MotionEvent.ACTION_MOVE:
				x = (float) event.getX();
				y = (float) event.getY();
				
				delta_x = x - down_x ; //
				delta_y = 0 ;          // hor
				
				if(DBG)
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
						for(i=0;i<mOnSwitchChangedListener.size();i++)
						{
							((OnSwitchChangedListener)mOnSwitchChangedListener.get(i)).onSwitchChanged(this, state);
						}
					}
					else
					{
						//mLayoutParams.width =
						
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
						for(i=0;i<mOnSwitchChangedListener.size();i++)
						{
							((OnSwitchChangedListener)mOnSwitchChangedListener.get(i)).onSwitchChanged(this, state);
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
				if(DBG)
					Log.d(TAG,"up x="+x+",y="+y);
				break;
			default:
				break;
		}
		*/
		//return true ;
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		
		Log.d(TAG,"draw() {");
		
		Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		mPaint.setTextSize(SlideButton_text_size);
		
		mPaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		Log.d(TAG,"width_size="+width_size+",height_size="+height_size);		
		
		Log.d(TAG,"SlideButton_block_size="+SlideButton_block_size);		
		
		if(SlideButton_value == false)
		{
			drawBitmap(canvas, null, new Rect(0, 0, width_size, height_size), img_bg);
			drawBitmap(canvas, null, new Rect(0, 0, SlideButton_block_size, height_size), img_off);
			mPaint.setColor(SlideButton_text_off_color.getDefaultColor());

			float text_w = mPaint.measureText(SlideButton_text_off);
			Paint.FontMetrics fm = mPaint.getFontMetrics();			
			float text_h = Math.abs(fm.ascent) + (Math.abs(fm.leading)*2) ;
			
			Log.d(TAG,"text_w="+text_w+",text_h="+text_h);
			
			//canvas.translate((SlideButton_block_size-text_w)/2, 0);
			// ����Ҫ���drawText���������ֵ����Ͻǿ�ʼ���ƣ�������baseLineΪ���߻���
			canvas.drawText(SlideButton_text_off, (SlideButton_block_size-text_w)/2, (height_size - text_h)/2 , mPaint);
			
		}
		else
		{
			Rect rt = new Rect();
			rt.left  	= 0 ;
			rt.right 	= 200;
			rt.top 		= 0 ;
			rt.bottom   = 32;
			drawBitmap(canvas, null, rt, img_bg);
			canvas.translate(img_bg.getWidth()-img_on.getWidth(), 0);
			drawBitmap(canvas, null, null, img_on);
			mPaint.setColor(Color.rgb(0, 105, 0));
			canvas.translate(img_off.getWidth()/2, 0);
			canvas.drawText(this.getResources().getString(R.string.value_on), 0, 20, mPaint);
		}
		
		Log.d(TAG,"draw() }");
		
		super.onDraw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
	}

	/** 
     * ��onDraw��ִ�� 
     *  
     * һ��MeasureSpec��װ�˸����ִ��ݸ��Ӳ��ֵĲ���Ҫ��ÿ��MeasureSpec������һ���Ⱥ͸߶ȵ�Ҫ�� 
     * һ��MeasureSpec�ɴ�С��ģʽ��� 
     * ��������ģʽ��UNSPECIFIED(δָ��),��Ԫ�ز�����Ԫ��ʩ���κ���������Ԫ�ؿ��Եõ�������Ҫ�Ĵ�С; 
     *              EXACTLY(��ȫ)����Ԫ�ؾ�����Ԫ�ص�ȷ�д�С����Ԫ�ؽ����޶��ڸ����ı߽���������������С�� 
     *              AT_MOST(����)����Ԫ������ﵽָ����С��ֵ�� 
     *  
              * ���������õ����������� ���� 
     * 1.static int getMode(int measureSpec):�����ṩ�Ĳ���ֵ(��ʽ)��ȡģʽ(��������ģʽ֮һ) 
     * 2.static int getSize(int measureSpec):�����ṩ�Ĳ���ֵ(��ʽ)��ȡ��Сֵ(�����СҲ��������ͨ����˵�Ĵ�С)  
     * 3.static int makeMeasureSpec(int size,int mode):�����ṩ�Ĵ�Сֵ��ģʽ����һ������ֵ(��ʽ) 
     */  
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		/*
		 * ������ �����ڵĲ��� ϣ����ǰ�Ӵ��ڵĲ��� ��Ϣ
		 * һ��MeasureSpec��װ�˸����ִ��ݸ��Ӳ��ֵĲ���Ҫ��ÿ��MeasureSpec������һ���Ⱥ͸߶ȵ�Ҫ��
		 * 
		 * �����õ�����������
		 * 1.static int getMode(int measureSpec):�����ṩ�Ĳ���ֵ(��ʽ)��ȡģʽ(��������ģʽ֮һ)
		 * 2.static int getSize(int measureSpec):�����ṩ�Ĳ���ֵ(��ʽ)��ȡ��Сֵ(�����СҲ��������ͨ����˵�Ĵ�С)
		 * 3.static int makeMeasureSpec(int size,int mode):�����ṩ�Ĵ�Сֵ��ģʽ����һ������ֵ(��ʽ)�����ֽ�+���ֽڣ�
		 * 
		 * MeasureSpec.EXACTLY�Ǿ�ȷ�ߴ磺���ؼ���layout_width��layout_heightָ��Ϊ������ֵʱ��andorid:layout_width="50dip��FILL_PARENT ������ȷ�ġ�
		 * MeasureSpec.AT_MOST �����ߴ磺���ؼ���layout_width��layout_heightָ��ΪWRAP_CONTENTʱ��Ҫ���ӿؼ��Ŀռ��С��ֻҪ���������ؼ���������ߴ缴��
		 * MeasureSpec.UNSPECIFIED��δָ���ߴ硣
		 */
		
		int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
		int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);
		
		int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
		int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		Log.d(TAG,"width="+specWidthSize+",height="+specHeightSize);
		
		// width 
		if( specWidthMode == MeasureSpec.AT_MOST) 
		{ 
			//������Ĵ�С�ļ��㣬��������ֵ����.   // fill
			width_size = specWidthSize; 
		} 
		else if(specWidthMode == MeasureSpec.EXACTLY) 
		{ 
			//	�����Ŀ����ܷ�����Щ���޷����Ǹ���ֵ. // wrap
			width_size = specWidthSize; 
		}
		else if(specWidthMode == MeasureSpec.UNSPECIFIED)
		{
			// δָ��������Ҫ�Լ��������С
			width_size = img_block_w * 3 ; 
		}
		
		// height
		if( specHeightMode == MeasureSpec.AT_MOST) 
		{ 
			//������Ĵ�С�ļ��㣬��������ֵ����. 
			height_size = specHeightSize; 
		} 
		else if(specHeightMode == MeasureSpec.EXACTLY) 
		{ 
			//	�����Ŀ����ܷ�����Щ���޷����Ǹ���ֵ. 
			height_size = specHeightSize; 
		}
		else if(specHeightMode == MeasureSpec.UNSPECIFIED)
		{
			// δָ��������Ҫ�Լ��������С
			height_size = img_bg_h ;  // �ͱ���һ����С��
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
