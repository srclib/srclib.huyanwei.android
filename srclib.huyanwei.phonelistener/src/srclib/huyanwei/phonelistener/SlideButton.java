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
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
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

	private boolean DBG = false;
	
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
	Bitmap img_bg;
	Bitmap img_off;
	Bitmap img_on;
	
	// slide �����Ϣ
	private int slide_block_x = 0;
	private int slide_block_y = 0;
	private int slide_block_w = 0;
	private int slide_block_h = 0;

	// ���浱ǰ����Ϣ.
	private String         current_text			= null;
	private ColorStateList current_text_color 	= null;
	private Bitmap         current_block_img    = null ;

	private boolean mShowFontMetricsLine = false ; 
	
	private float first_down_x;
	private float first_down_y;
	
	private float last_down_x;
	private float last_down_y;	
	
	private boolean mHasSelected = false ;
	private boolean mHasScrolled = false ;	
	private boolean scrolling 	 = false ;
	
	public static final int MSG_UPDATE_STATE = 100;
	
	private Object mLock = new Object();
	
	Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
				case  MSG_UPDATE_STATE:
					scrolling = ((msg.arg1 > 1)?true:false);
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	public void switchValue()
	{
		synchronized(mLock)
		{
			doAnimation(SlideButton_value);
			// slideButton_value = !SlideButton_value;
			// trigger notify when redraw.
		}
	}
	
	public boolean getValue()
	{
		return SlideButton_value;
	}
	
	public void setValue(boolean arg)
	{
		synchronized(mLock)
		{
			SlideButton_value = arg;
		
			this.invalidate();
		}		
		// trigger notify when redraw.
	}
	
	public void setSwitchOffText(String off_str)
	{
		SlideButton_text_off = off_str;
	}
	
	public void setSwitchOnText(String on_str)
	{
		SlideButton_text_on = on_str;
	}
	
	public void setBlockWidth(int  block_width)
	{
		SlideButton_block_size = block_width ;
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
		img_bg_h = 	img_bg.getHeight();		
		img_block_w = img_off.getWidth();
		img_block_h = img_off.getHeight();
		
		if(DBG)
		{
			Log.d(TAG,"["+img_bg_w+","+img_bg_h+","+img_block_w+","+img_block_h+"]");
		}
		
		// init block position
		slide_block_x = 0 ;
		slide_block_y = 0 ;
		slide_block_w = (SlideButton_block_size==0)?(img_block_w):(SlideButton_block_size); // ����ָ����block �Ŀ�ȡ�
		slide_block_h = img_block_h ;
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
		
		if(DBG)
		{	
			Log.d(TAG,"SlideButton_text_off="+SlideButton_text_off);
			Log.d(TAG,"SlideButton_text_on="+SlideButton_text_on);
			Log.d(TAG,"SlideButton_text_off_color="+SlideButton_text_off_color);
			Log.d(TAG,"SlideButton_text_on_color="+SlideButton_text_on_color);
			Log.d(TAG,"SlideButton_text_size="+SlideButton_text_size);
			Log.d(TAG,"SlideButton_block_size="+SlideButton_block_size);
			Log.d(TAG,"SlideButton_value="+SlideButton_value);
		}
		
		a.recycle();
				
		init(context); // init control .
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

	/**
	 * AnimationTransRunnable ������������ʹ�õ��߳�
	 */
	private class AnimationTransRunnable implements Runnable
	{
		private int step_len = 5;
		
		private int srcX, dstX;
		
		private Handler handler;

		/**
		 * ��������
		 * @param srcX ������ʼ��
		 * @param dstX ������ֹ��
		 * @param duration �Ƿ���ö�����1���ã�0������
		 */
		public AnimationTransRunnable(float srcX, float dstX, final Handler h)
		{
			this.srcX = 	(int)srcX;
			this.dstX = 	(int)dstX;
			this.handler = h;
		}

		//@Override
		public void run() 
		{
			int dir = 1;
			int count = 0;
			
			if(dstX < srcX)
				dir = -1;
			else
				dir = 1;

			while( Math.abs((dstX- srcX )) > step_len)
			{
				if(DBG)
				{
					Log.d(TAG,"AnimationTransRunnable() dstX="+dstX+",srcX="+srcX);
				}
				
				srcX += dir * step_len;
				slide_block_x += dir * step_len; 
				
				if(DBG)
				{
					Log.d(TAG,"run() slide_block_x="+slide_block_x);
				}
				
				SlideButton.this.postInvalidate();
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(DBG)
			{
				Log.d(TAG,"AnimationTransRunnable() rest dstX="+dstX+",srcX="+srcX);
			}
			
			// res
			{
				slide_block_x += (dstX - srcX);	
				SlideButton.this.postInvalidate();
			}
		}
		
		{
			/*
			Message msg = handler.obtainMessage();
			msg.what = SlideButton.MSG_UPDATE_STATE ;
			msg.arg1 = 0 ;
			msg.setTarget(handler);
			*/
			
			Message msg = new Message();
			msg.what = SlideButton.MSG_UPDATE_STATE ;
			msg.arg1 = 0 ;
			msg.setTarget(handler);
		}
	}
	
	public void sendMessage(int state)
	{
		/*
		Message msg = mHandler.obtainMessage();
		msg.what = SlideButton.MSG_UPDATE_STATE; 
		msg.arg1 = state;
		msg.sendToTarget();
		*/
		
		Message msg = new Message();
		msg.what = SlideButton.MSG_UPDATE_STATE ;
		msg.arg1 = state ;
		msg.setTarget(mHandler);
	}
	
	@SuppressWarnings("unused")
	public void doAnimation(boolean cur_value )
	{
		int src_x =  slide_block_x;
		int src_y =  slide_block_y;
		
		int dst_x =  slide_block_x;
		int dst_y =  slide_block_y;

		if(DBG)
		{
			Log.d(TAG,"doAnimation() slide_block_x="+slide_block_x+",slide_block_w="+slide_block_w);
		}
		
		if(cur_value)
		{
			// to left
			dst_x = 0;
			
			scrolling = true ; // ��ʼ ����
		}
		else
		{
			// to right
			dst_x = (width_size-slide_block_w);
			
			scrolling = true ; // ��ʼ ����
		}
		
		if(DBG)
		{
			Log.d(TAG,"dst_x="+dst_x);
		}

		AnimationTransRunnable runnable = new AnimationTransRunnable(src_x, dst_x, mHandler);
		Thread mThread = new Thread(runnable);
		mThread.start();
	}
	
	public void handle_up_event(int x , int y)
	{
		int src_x =  slide_block_x;
		int src_y =  slide_block_y;
		
		int dst_x =  slide_block_x;
		int dst_y =  slide_block_y;

		if(DBG)
		{
			Log.d(TAG,"handle_up_event() mHasScrolled="+mHasScrolled+",SlideButton_value="+SlideButton_value);
		}
		
		if(!mHasScrolled)
		{
			// single click event .
			
			if(SlideButton_value)
			{
				Rect rect = new Rect(0,slide_block_y,slide_block_w,slide_block_y+slide_block_h);
				if(rect.contains(x, y))
				{
					//go to left
					dst_x = 0;
					
					scrolling = true ; // ��ʼ ����
					
				}
			}
			else
			{
				Rect rect = new Rect((width_size - slide_block_w),slide_block_y,width_size,slide_block_y+slide_block_h);
				if(rect.contains(x, y))
				{
					//go to right
					dst_x = (width_size-slide_block_w);
					
					scrolling = true ; // ��ʼ ����
				}				
			}			
			AnimationTransRunnable runnable = new AnimationTransRunnable(src_x, dst_x, mHandler);
			Thread mThread = new Thread(runnable);
			mThread.start();
		}
		else
		{
			// srolled , do animation, .
			if( (slide_block_x+(slide_block_w/2)) < (width_size/2)) // �ؼ���ǰ��λ������,�ֽ�� Ϊ �ؼ��� �е�
			{
				// to left
				dst_x = 0;
				
				scrolling = true ; // ��ʼ ����
			}
			else
			{
				// to right
				dst_x = (width_size-slide_block_w);
				
				scrolling = true ; // ��ʼ ����
			}
			
			if(DBG)
			{
				Log.d(TAG,"dst_x="+dst_x);
			}

			AnimationTransRunnable runnable = new AnimationTransRunnable(src_x, dst_x, mHandler);
			Thread mThread = new Thread(runnable);
			mThread.start();
			
			//doAnimation();
		}
	}

	public void update_slide_block_position(int x , int y)
	{
		slide_block_x += x;
		slide_block_y += y ;
		
		if(slide_block_x<0)
		{
			slide_block_x = 0 ;
		}
		
		if((slide_block_x + slide_block_w) > width_size)
		{
			slide_block_x = (width_size -  slide_block_w);
		}
		
		if(DBG)
		{
			Log.d(TAG,"update_slide_block_position() slide_block_x="+slide_block_x);
		}
		
		this.invalidate();
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
				first_down_x = (float) event.getX();
				first_down_y = (float) event.getY();

				last_down_x =  first_down_x;
				last_down_y =  first_down_y;

				if(DBG)
				{
					Log.d(TAG,"ACTION_DOWN first_down_x="+first_down_x+",first_down_y="+first_down_y);
				}

				mHasSelected = false ; 
				scrolling 	 = false ; // init state
				mHasScrolled = false ; // init state
				
				Rect block_rect = new Rect(slide_block_x,slide_block_y,slide_block_x+slide_block_w , slide_block_y+slide_block_h);
				if(block_rect.contains((int)first_down_x, (int)first_down_y))
				{
					mHasSelected = true;
				}
				break;
			case  MotionEvent.ACTION_MOVE:
				x = (float) event.getX();
				y = (float) event.getY();
				
				if(DBG)
				{
					Log.d(TAG,"ACTION_MOVE x="+x+",y="+y);
				}
				
				if(mHasSelected)
				{	
					if(DBG)
					{
						Log.d(TAG,"ACTION_MOVE scrolling="+scrolling);
					}
					//if( Math.abs((x-first_down_x )) > 10)
					{
						scrolling    = true ;
						mHasScrolled = true;
					}
					
					delta_x = x - last_down_x ; //
					delta_y = 0 ;          		// hor
					
					last_down_x =  x;
					last_down_y =  y;
					
					update_slide_block_position((int)delta_x,(int)delta_y);
				}				
				break;
			case  MotionEvent.ACTION_UP:
			case  MotionEvent.ACTION_CANCEL:
				x = (float) event.getX();
				y = (float) event.getY();
				
				if(DBG)
				{
					Log.d(TAG,"ACTION_UP x="+x+",y="+y);
				}
				handle_up_event((int)x,(int)y); 
				mHasScrolled = false ;
				break;
			default:
				break;
		}

		return true ;
		//return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		
		if(DBG)
		{
			Log.d(TAG,"draw() {");
		}
		
		Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		mPaint.setTextSize(SlideButton_text_size);
		
		mPaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		if(DBG)
		{
			Log.d(TAG,"onDraw() :width_size="+width_size+",height_size="+height_size);		
			Log.d(TAG,"SlideButton_block_size="+SlideButton_block_size);
		}
		
		if(	scrolling )
		{
			
			if(slide_block_x < 20)
			{
				current_text 		= SlideButton_text_off;
				current_text_color	= SlideButton_text_off_color;
				current_block_img   = img_off;
				
				if(SlideButton_value == true)
				{
					SlideButton_value = false;
					
					// notify 
					for(int i = 0 ; i < mOnSwitchChangedListener.size();i++ )
					{
						mOnSwitchChangedListener.get(i).onSwitchChanged(this, SlideButton_value);
					}
				}				
			}
			
			if((slide_block_x+slide_block_w) > width_size - 20)
			{
				current_text 		= SlideButton_text_on;
				current_text_color	= SlideButton_text_on_color;
				current_block_img   = img_on;
				
				if(SlideButton_value == false)
				{
					SlideButton_value = true;
					
					// notify 
					for(int i = 0 ; i < mOnSwitchChangedListener.size();i++ )
					{
						mOnSwitchChangedListener.get(i).onSwitchChanged(this, SlideButton_value);
					}
				}				
			}
		}
		else if(SlideButton_value == false)
		{
			slide_block_w = SlideButton_block_size;
			slide_block_h = height_size ;

			slide_block_x = 0;
			slide_block_y = 0 ;
			
			current_text 		= SlideButton_text_off;
			current_text_color	= SlideButton_text_off_color;
			current_block_img   = img_off;
		}
		else
		{
			slide_block_w = SlideButton_block_size;
			slide_block_h = height_size ;
			
			slide_block_x = width_size-slide_block_w;
			slide_block_y = 0 ;
			
			current_text 		= SlideButton_text_on;
			current_text_color	= SlideButton_text_on_color;
			current_block_img   = img_on;
		}
		
		// draw bg
		canvas.drawBitmap(img_bg, null, new Rect(0, 0, width_size, height_size),  mPaint);
		
		// draw block
		canvas.drawBitmap(current_block_img, null, new Rect(slide_block_x, slide_block_y, slide_block_x+slide_block_w, slide_block_y+slide_block_h),mPaint);
		
		mPaint.setColor(current_text_color.getDefaultColor());

		// �������ֿ��
		float text_w = mPaint.measureText(current_text);
		
		// �������ָ߶�
		Paint.FontMetrics fm = mPaint.getFontMetrics();

		if(DBG)
		{
			Log.d(TAG,"top="+fm.top);
			Log.d(TAG,"ascent="+fm.ascent);
			Log.d(TAG,"descent="+fm.descent);
			Log.d(TAG,"bottom="+fm.bottom);
			Log.d(TAG,"leading="+fm.leading);
		}
		
		float text_h = fm.bottom - fm.top;
		
		//mPaint.setTextAlign(Align.CENTER); // ˮƽ���С�
		
		// calc baseline
		float textBaseY = height_size - ((height_size - text_h) / 2) - fm.bottom;
		
		//canvas.translate((SlideButton_block_size-text_w)/2, 0);
		
		// ����Ҫ���drawText���������ֵ����Ͻǿ�ʼ���ƣ�������baseLineΪ���߻���
		canvas.drawText(current_text, slide_block_x+(slide_block_w-text_w)/2, textBaseY, mPaint);
		
		if(mShowFontMetricsLine)
		{
			// Draw Font Metrics {
			//���ߣ�baeseline�����¶���ascenter��,�µף�descenter��
			//���¶ȣ�ascent�������¶ȣ�descent��
			//�м�ࣨleading�����µ׵���һ���¶��ľ���, �������� ���߼䴹ֱ����.
			//����ĸ߶ȣ����¶ȣ����¶ȣ��м��
			//ascent��ָ��һ���ֵĻ���(baseline)������ľ��룬descent��ָһ���ֵĻ��ߵ���ײ��ľ���
			//ע��, ascent��top���Ǹ���
			
			float baseX     = slide_block_x;
			float baseY     = textBaseY;
			float topY 		= baseY + fm.top; 
		    float ascentY 	= baseY + fm.ascent; 
			float descentY 	= baseY + fm.descent; 
			float bottomY 	= baseY + fm.bottom;

			// Base Line�軭 
			Paint baseLinePaint = new Paint( Paint.ANTI_ALIAS_FLAG); 
			baseLinePaint.setColor( Color.RED);
			canvas.drawLine(0, baseY, getWidth(), baseY, baseLinePaint);
			canvas.drawCircle( baseX, baseY, 5, baseLinePaint); // Base Point ������� 

			
			// Top Line�軭 
			Paint topLinePaint = new Paint( Paint.ANTI_ALIAS_FLAG); 
			topLinePaint.setColor( Color.LTGRAY); 
			canvas.drawLine(0, topY, getWidth(), topY, topLinePaint); 

			// Ascent Line�軭 
			Paint ascentLinePaint = new Paint( Paint.ANTI_ALIAS_FLAG); 
			ascentLinePaint.setColor( Color.GREEN); 
			canvas.drawLine(0, ascentY,getWidth(), ascentY, ascentLinePaint); 

			// 	Descent Line�軭 
			Paint descentLinePaint = new Paint( Paint.ANTI_ALIAS_FLAG); 
			descentLinePaint.setColor( Color.YELLOW); 
			canvas.drawLine(0, descentY, getWidth(), descentY, descentLinePaint); 

			// Buttom Line�軭 
			Paint bottomLinePaint = new Paint( Paint.ANTI_ALIAS_FLAG); 
			bottomLinePaint.setColor( Color.MAGENTA); 
			canvas.drawLine(0, bottomY, getWidth(), bottomY, bottomLinePaint); 		   
			// 	Draw Font Metrics } 
		}
		
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
		
		if(DBG)
		{
			Log.d(TAG,"onMeasure() : width="+specWidthSize+",height="+specHeightSize);
		}
		
		// width 
		if( specWidthMode == MeasureSpec.AT_MOST) 
		{ 
			//������Ĵ�С�ļ��㣬��������ֵ����.   // fill
			if(DBG)
			{
				Log.d(TAG,"specWidthMode=MeasureSpec.AT_MOST");
			}
			width_size = specWidthSize; 
		} 
		else if(specWidthMode == MeasureSpec.EXACTLY) 
		{ 
			//	�����Ŀ����ܷ�����Щ���޷����Ǹ���ֵ. // wrap
			if(DBG)
			{
				Log.d(TAG,"specWidthMode=MeasureSpec.EXACTLY");
			}
			width_size = specWidthSize; 
		}
		else if(specWidthMode == MeasureSpec.UNSPECIFIED)
		{
			// δָ��������Ҫ�Լ��������С
			if(DBG)
			{
				Log.d(TAG,"specWidthMode=MeasureSpec.UNSPECIFIED");
			}
			width_size = (int)(slide_block_w * 2 );  // �ؼ���С�ǿؼ������2������
		}
		
		// height
		if( specHeightMode == MeasureSpec.AT_MOST) 
		{ 
			//������Ĵ�С�ļ��㣬��������ֵ����.
			if(DBG)
			{
				Log.d(TAG,"specHeightMode=MeasureSpec.AT_MOST");
			}
			height_size = specHeightSize; 
		} 
		else if(specHeightMode == MeasureSpec.EXACTLY) 
		{ 
			//	�����Ŀ����ܷ�����Щ���޷����Ǹ���ֵ. 
			if(DBG)
			{
				Log.d(TAG,"specHeightMode=MeasureSpec.EXACTLY");			
			}
			height_size = specHeightSize; 
		}
		else if(specHeightMode == MeasureSpec.UNSPECIFIED)
		{
			// δָ��������Ҫ�Լ��������С
			if(DBG)
			{
				Log.d(TAG,"specHeightMode=MeasureSpec.UNSPECIFIED");
			}
			height_size   = img_bg_h ;     // �ͱ���һ����С��
			slide_block_h = height_size ;  // block �߶�Ҳһ����
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
