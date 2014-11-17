package srclib.huyanwei.waterwave;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class WaterWaveView extends View {

	public final String TAG = "WaterWave";
	
	public ArrayList<Waver> WaterWavers = new ArrayList<Waver>();
	
	public Bitmap background = null;	
	private Context mContext;
	private float screenWidth 	= 540;
	private float screenHeight 	= 960;		
	private float mPrePointX, mPrePointY;
	private float mNowPointX, mNowPointY;
	private float mWaverPointX, mWaverPointY;
	
	private float mLastDownPointX, mLastDownPointY;
	
	private View mHelpViewer;
	private AnimationDrawable mAnimationDrawable;	
	private Paint mPaint;
	
	private AnimatorSet mAnimatorSet;
	
	//private OnTriggerListener mOnTriggerListener;
	
	public WaterWaveView(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}
	
	public WaterWaveView(Context context,AttributeSet attrs) {
		this(context,attrs,0);
		// TODO Auto-generated constructor stub
	}
	
	public WaterWaveView(Context context,AttributeSet attrs,int defStyle) {		
		super(context, attrs, defStyle);
		
		mContext = context;
		
		mHelpViewer = new View(mContext);
		mHelpViewer.setBackgroundResource(R.drawable.finger_effection);
		mAnimationDrawable = (AnimationDrawable) mHelpViewer.getBackground();
		mPaint = new Paint();
		mPaint.setColor(Color.CYAN);
		
		// mPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		
		mLastDownPointX = -100 ;
		mLastDownPointY = -100 ;
		
		setLockBack();
		
		//mSoundManager.initSoundPool(context);
		
		// TODO Auto-generated constructor stub
	}

	/*
	 * 
    public void cleanUp() {
        mSoundManager.cleanup();        
    }
 
    public void setOnTriggerListener(OnTriggerListener param) {
        this.mOnTriggerListener = param;
    }
    */
	
	
	private void setLockBack() {
		Bitmap bp = BitmapFactory.decodeFile("/data/lockwaller/lockwall.jpg");
		if (bp == null) {
			Drawable dw = mContext.getResources().getDrawable(R.drawable.defalut_background);
			this.background = getWallpaperDrawable((BitmapDrawable) dw);
			
			this.setBackgroundResource(R.drawable.defalut_background);
			
		} else {
			BitmapDrawable bd = new BitmapDrawable(bp);
			this.background = getWallpaperDrawable(bd);
		}
	}
	
	private Bitmap getWallpaperDrawable(BitmapDrawable wallPaper) {
		int height = 960;
		int width = 960;
		Resources mResources = mContext.getResources();
		int screenH = mResources.getDisplayMetrics().heightPixels;
		int screenW = mResources.getDisplayMetrics().widthPixels;
		Bitmap bitmap = Bitmap.createBitmap(screenW, screenH, wallPaper
				.getOpacity() == PixelFormat.OPAQUE ? Config.RGB_565
				: Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		wallPaper.setBounds(-300, 0, width - 300, height);
		wallPaper.draw(c);
		return bitmap;
	}
	
	private void onShowWaterWaver(Canvas canvas)
	{
	  //while(!WaterWavers.isEmpty())
	  {	
		int object_index  = 0 ;
		while(object_index < WaterWavers.size())
		{
			Waver mWaver = WaterWavers.get(object_index);
			int mCount = mAnimationDrawable.getNumberOfFrames();
			int index = mWaver.getCurrentFrameNum() ;
			
			if(index == (mCount-1))
			{
				Log.d(TAG,"huyanwei debug WaterWave.remove()");
				WaterWavers.remove(mWaver); // 运行完就删除这水滴.	
				continue ;
			}
			
			Log.d(TAG,"huyanwei debug WaterWave WaterWavers.size()="+WaterWavers.size());
			Log.d(TAG,"huyanwei debug WaterWave index ="+index+",mCount="+mCount);

			
			Drawable img = mAnimationDrawable.getFrame(index);
			Bitmap bmp = DrawableToBitmap(img);					


			canvas.save();
			Matrix mMatrix = new Matrix();						
			{	
				mMatrix.preScale(mWaver.GetScale(),mWaver.GetScale());
				mMatrix.preTranslate(-mWaver.getX(),-mWaver.getY());
				mMatrix.postTranslate(mWaver.getX(),mWaver.getY());
				canvas.setMatrix(mMatrix);				
			}
			canvas.drawBitmap(bmp,mWaver.getX()-(bmp.getWidth()/2),mWaver.getY()-(bmp.getHeight()/2), mPaint);
			canvas.restore();
		
			object_index ++;
		}
		
		if(!WaterWavers.isEmpty())
		{
			this.invalidate();
		}
	}
}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		onShowWaterWaver(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int e = event.getAction();
		switch (e) {
		case MotionEvent.ACTION_DOWN:
			mWaverPointX = mNowPointX = mPrePointX = event.getX();
			mWaverPointY = mNowPointY = mPrePointY = event.getY();
			Log.d(TAG,"huyanwei debug AddWaterWave("+mNowPointX+","+mNowPointY+");");

			if ((event.getPointerCount() == 1) && ((mNowPointX - mLastDownPointX) * (mNowPointX - mLastDownPointX)
					+ (mNowPointY - mLastDownPointY)* ( mNowPointY - mLastDownPointY) > 400.00F))
			{
				mLastDownPointX = mWaverPointX ;
				mLastDownPointY = mWaverPointY ;
			}
			AddWaterWave(mLastDownPointX,mLastDownPointY,1.25f);			
			
			/*
            if (mSoundManager != null) 
            {	            
                 mSoundManager.playSound(Action.LENS_FLARE_DRAG);
            }
            */			
			break;

		case MotionEvent.ACTION_MOVE:
			mNowPointX = event.getX();
			mNowPointY = event.getY();
			if ((event.getPointerCount() == 1) && ((mNowPointX - mWaverPointX) * (mNowPointX - mWaverPointX)
						+ (mNowPointY - mWaverPointY)* ( mNowPointY - mWaverPointY) > 6400.00F)) 
			{
				mWaverPointX = mNowPointX ;
				mWaverPointY = mNowPointY ;
				
				AddWaterWave(mWaverPointX,mWaverPointY,1.25f);
				/*
	            if (mSoundManager != null) 
	            {	            
	                 mSoundManager.playSound(Action.LENS_FLARE_DRAG);
	            }
	            */
			}
			break;
		case MotionEvent.ACTION_UP:
			mNowPointX = event.getX();
			mNowPointY = event.getY();
			if ((event.getPointerCount() == 1) && ((mNowPointX - mPrePointX) * (mNowPointX - mPrePointX)
					+ (mNowPointY - mPrePointY)* ( mNowPointY - mPrePointY) > 100.00F))
			{
				//AddWaterWave(screenWidth/2,screenHeight/2,2.0f);
				AddWaterWave(mNowPointX,mNowPointY,0.25f);
			}			
			if ((event.getPointerCount() == 1) && ((mNowPointX - mPrePointX) * (mNowPointX - mPrePointX)
					+ (mNowPointY - mPrePointY)* ( mNowPointY - mPrePointY) > 40000.00F))
			{
				/*
                if (mSoundManager != null) {
                    mSoundManager.playSound(Action.LENS_FLARE_UNLOCK);
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mOnTriggerListener.onTrigger(WaterWaveView.this, 0);
                        }
                    }, 300);
                }
                */
			}
			break;			
		default:
			break;
		}
		return true;
	}
	
	@SuppressLint("NewApi")
	private void AddWaterWave(float x , float y,float scale)
	{
		Waver _a_drop_of_waver_ = new Waver(x,y);
		
		_a_drop_of_waver_.setScale(scale);
		
		// Animation {		
		mAnimatorSet = new AnimatorSet();
		
		ObjectAnimator mObjectAnimator = ObjectAnimator.ofInt(_a_drop_of_waver_, "CurrentFrameNum", 0,mAnimationDrawable.getNumberOfFrames());
		mObjectAnimator.addListener(new AnimatorListener()
		{
			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				Log.d(TAG,"one Water Waver Animation Finish");
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub				
			}			
		});
		mObjectAnimator.setDuration(500);
		mObjectAnimator.setInterpolator(new TimeInterpolator(){
			@Override
			public float getInterpolation(float input) {
				// TODO Auto-generated method stub
				return input;
				}			
			}
		);
		
		mAnimatorSet.play(mObjectAnimator);
		mAnimatorSet.setDuration(600);
		mAnimatorSet.start();
		
		// Animation }	
		WaterWavers.add(_a_drop_of_waver_);
		
		
		this.invalidate();
	}
	
	Bitmap DrawableToBitmap(Drawable drawable)
	{
		/*
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();		
		Bitmap mBitmap = Bitmap.createBitmap(w, h, Config.RGB_565);		
		Canvas c = new Canvas(mBitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(c);		
		return mBitmap;
		*/
		
		int width = drawable.getIntrinsicWidth();
		width = width>0?width:1 ;
		int height = drawable.getIntrinsicHeight();
		height= height>0?height:1;
		//Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE? Bitmap.Config.ARGB_8888: Bitmap.Config.RGB_565);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);		
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
		drawable.draw(canvas);		
		return bitmap;
	}
	
	class Waver 
	{
		private float mCenterX,mCenterY;
		private int   mCurrentFrame;
		private float mScale;

		Waver(float x , float y)
		{
			mCurrentFrame = 0 ; 
			mScale      = 1.0f;
			mCenterX 	= x ;
			mCenterY 	= y ;
		}

		public float getX()
		{
			return mCenterX;
		}
		
		public float getY()
		{
			return mCenterY;
		}
		
		public void setScale(float scale)
		{
			mScale = scale;
		}
		
		public float GetScale()
		{
			return mScale ;
		}
		
		public int getCurrentFrameNum()
		{
			return mCurrentFrame;
		}
		
		public void setCurrentFrameNum(int index)
		{
			Log.d(TAG,"huyanei debug setCurrentFrameNum(+"+index+")");
			if(index > (mAnimationDrawable.getNumberOfFrames()-1))
			{
				index = mAnimationDrawable.getNumberOfFrames()-1;
			}
			if(index < 0)
			{
				index = 0 ;
			}			
			mCurrentFrame = index;
		}
	};	
}
