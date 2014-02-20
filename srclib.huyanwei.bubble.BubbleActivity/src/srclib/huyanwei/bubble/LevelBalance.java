package srclib.huyanwei.bubble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.graphics.Paint;

public class LevelBalance extends FrameLayout {

	private final String TAG = "srclib.huyanwei.LevelBalance";
	
	public static final int HOR = 0 ;
	public static final int VER = 1 ;
	private int Style = HOR ;  
	
	private final boolean DBG = false ;
	
	private static final int G = 9807;
	
	Paint mPaint = new Paint();
	
	int mleft = this.getLeft();
	int mtop = this.getTop();
	int mright = this.getRight();
	int mbottom = this.getBottom();
	
	private  View     m_root;
	private ImageView m_bg ;
	private ImageView m_ind ;
	
	private int G_X ;
	private int G_Y ;
	private int G_Z ;
	
	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		//super.onDraw(canvas);
		//this.draw(canvas);
	}
	
	/* (non-Javadoc)
	 * @see android.widget.FrameLayout#draw(android.graphics.Canvas)
	 */
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub				
		super.draw(canvas);

/*		
		if(Style == HOR)
		{
			//canvas.save();
			mPaint.setColor(Color.BLACK);  
			canvas.drawRect(new Rect((mright+mleft)/2-40, mtop, (mright+mleft)/2+40, mbottom), mPaint);
			//canvas.restore();					
		}	
		else
		{
			//canvas.save();
			mPaint.setColor(Color.BLACK);  
			canvas.drawRect(new Rect(mleft, (mtop+mbottom)/2-40, mright, (mtop+mbottom)/2 + 40), mPaint);
			//canvas.restore();
		}
*/
		/*
		canvas.save();  

		canvas.drawColor(Color.GREEN);  
		 
		mPaint.setAntiAlias(true);  
		 
		canvas.clipRect(mleft, mtop, mright, mbottom); 
		
		mPaint.setColor(Color.GREEN);  
		canvas.drawRect(new Rect(mleft, mtop, mright, mbottom), mPaint);
		
		canvas.restore();			
		*/
	}

	/* (non-Javadoc)
	 * @see android.widget.FrameLayout#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if(DBG) Log.d(TAG, "onLayout changed="+changed+",left="+left+",top="+top+",right="+right+",bottom="+bottom);
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		
		mleft = left;
		mtop =  top;
		mright = right;
		mbottom = bottom;
		
		int delta = 0 ;
		
		if(DBG) Log.d(TAG,"G_X ="+G_X + ",G_Y="+G_Y+",G_Z="+G_Z);
		
		if(Style == HOR)
		{
			delta =  (int) ((G_X*1.0) * (right - left)*2/7) /G;
			
			//Log.d(TAG,"H delta ="+delta);
			
			m_bg.layout(left, top, right, bottom);
			m_ind.layout(delta+(right+left)/2 - 25 , top+10, delta+(right+left)/2 +25 , bottom-10);		

			if(DBG) Log.d(TAG,"H w3="+m_bg.getWidth()+",h3="+m_bg.getHeight());
			if(DBG) Log.d(TAG,"H w4="+m_ind.getWidth()+",h4="+m_ind.getHeight());

		}
		else
		{
			delta =  0 - (int) ((G_Y* 1.0 ) *  (bottom - top)*2/7 )/G ;
			
			//Log.d(TAG,"V delta ="+delta);
			
			m_bg.layout(left, top, right, bottom);
			m_ind.layout(left+10 , delta+(bottom+top)/2-25, right-10 , delta+(bottom+top)/2 +25);
			
			if(DBG) Log.d(TAG,"V w3="+m_bg.getWidth()+",h3="+m_bg.getHeight());
			if(DBG) Log.d(TAG,"V w4="+m_ind.getWidth()+",h4="+m_ind.getHeight());
		}	
	}

	/* (non-Javadoc)
	 * @see android.widget.FrameLayout#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		//Log.e(TAG, "onMeasure");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		final int width = MeasureSpec.getSize(widthMeasureSpec);   
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		
		final int height = MeasureSpec.getSize(heightMeasureSpec);   
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        
        int result = 500;
        if (widthMode == MeasureSpec.AT_MOST)
        {
	        // Calculate the ideal size of your
	        // control within this maximum size.
	        // If your control fills the available
	        // space return the outer bound.
        	result = width;
        } 
        else if (widthMode == MeasureSpec.EXACTLY)
        {
        	// If your control can fit within these bounds return that value.
        	result = width;
        }                
        
        m_bg.measure(widthMeasureSpec, heightMeasureSpec);
        m_ind.measure(widthMeasureSpec, heightMeasureSpec);   
        
        if(DBG) Log.d(TAG,"w1="+m_bg.getWidth()+",h1="+m_bg.getHeight());
        if(DBG) Log.d(TAG,"w2="+m_ind.getWidth()+",h2="+m_ind.getHeight());        
	}
	
	
	public void setStyle(int style)
	{
		Style = style % 2 ;	
		
		if(Style == HOR)
		{
			m_bg.setImageResource(R.drawable.bar_horizontal2);
			//m_ind.setImageResource(R.drawable.disk_bubble);
			
			m_bg.setBackgroundResource(R.drawable.bar_horizontal);
			m_ind.setBackgroundResource(R.drawable.disk_bubble);
		}	
		else
		{
			m_bg.setImageResource(R.drawable.bar_vertical2);
			//m_ind.setImageResource(R.drawable.disk_bubble);
			m_bg.setBackgroundResource(R.drawable.bar_vertical);
			m_ind.setBackgroundResource(R.drawable.disk_bubble);
		}
	}
	
	public void setValue(int x , int y , int z)
	{
		G_X = x ;
		G_Y = y ;
		G_Z = z ;	
		
		this.requestLayout(); // fresh UI.
	}

	public LevelBalance(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub

		G_X = 0 ;
		G_Y = 0 ;
		G_Z = 0 ;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_root = inflater.inflate(R.layout.balance, this, true);
		
        m_bg = (ImageView) m_root.findViewById(R.id.imageView_bg);       
        m_ind = (ImageView) m_root.findViewById(R.id.imageView_ind);
        
	}

	public LevelBalance(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO Auto-generated constructor stub
	}
	
	public LevelBalance(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}
}
