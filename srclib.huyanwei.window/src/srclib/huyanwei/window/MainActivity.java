package srclib.huyanwei.window;

import android.os.Bundle;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;


@SuppressLint("NewApi")
public class MainActivity extends Activity {

	private Context mContext;
	private LayoutInflater mLayoutInflater ;
	
	private Button mButton0, mButton1, mButton2, mButton3,mButton4;

	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mLayoutParams;
	private View mView ;	
	private FloatView mFloatView = null ;	
	private List<FloatView> mFloatViewList = new ArrayList <FloatView>();
	
	private int mScreenWidth,mScreenHeight;
	
	private boolean mExpand = true;
	ImageButton mImageButton;
	
	@SuppressLint("NewApi")
	public void onCreateFloatWindows()
	{

		int NumOfFloatView = 0 ;
		ViewGroup mViewGroup = (ViewGroup) mLayoutInflater.inflate(R.layout.window, null); 
		
		mImageButton = (ImageButton) mViewGroup.findViewById(R.id.Pointer);
		mImageButton.setOnClickListener(mOnClickListener);
		
        mView =  (View)mViewGroup;
                
        int num = mViewGroup.getChildCount();
        for(int i =0 ; i< num ; i++)
        {        	
        	View tmp = mViewGroup.getChildAt(i);
        	if(tmp instanceof FloatView)
        	{
        		mFloatViewList.add(NumOfFloatView,(FloatView) tmp);
        		NumOfFloatView ++ ;
        		
        		AnimatorSet mAnimatorSet = new AnimatorSet();
        		
        		ObjectAnimator mObjectAnimator1 = ObjectAnimator.ofFloat(tmp, "ScaleX", 0.1f,1.0f);
        		mObjectAnimator1.setDuration(3000);
        		mObjectAnimator1.setRepeatCount(0);
        		mObjectAnimator1.setRepeatMode(ObjectAnimator.REVERSE);
        		
        		ObjectAnimator mObjectAnimator2 = ObjectAnimator.ofFloat(tmp, "ScaleY", 0.1f,1.0f);
        		mObjectAnimator2.setDuration(3000);
        		mObjectAnimator2.setRepeatCount(0);
        		mObjectAnimator2.setRepeatMode(ObjectAnimator.REVERSE);


        		ObjectAnimator mObjectAnimator3 = ObjectAnimator.ofFloat(tmp, "Rotation", 0.0f,360.0f);
        		mObjectAnimator3.setDuration(3000);
        		mObjectAnimator3.setRepeatCount(0);
        		//mObjectAnimator3.setRepeatCount(ObjectAnimator.INFINITE);
        		mObjectAnimator3.setRepeatMode(ObjectAnimator.REVERSE);
        		
        		ObjectAnimator mObjectAnimator4 = ObjectAnimator.ofFloat(tmp, "TranslationX", 500.0f,0.0f);
        		mObjectAnimator4.setDuration(3000);
        		mObjectAnimator4.setRepeatCount(0);
        		mObjectAnimator4.setRepeatMode(ObjectAnimator.REVERSE);
        		
        		mAnimatorSet.play(mObjectAnimator1).with(mObjectAnimator2).with(mObjectAnimator3).with(mObjectAnimator4);
        		mAnimatorSet.setStartDelay(100);
        		mAnimatorSet.start();
        	}
        }        

        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        

        
        //mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY; // 此窗口类别不能获得输入焦点
        mLayoutParams.type=WindowManager.LayoutParams.TYPE_PHONE;   //设置window type
        //mLayoutParams.type = WindowManager.LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY;

        
        //设置Window flag
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
       
        /*
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                			| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                			| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                			| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        */
        
        /*
         * 下面的flags属性的效果形同“锁定”。
         * 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
         wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL 
                               | LayoutParams.FLAG_NOT_FOCUSABLE
                               | LayoutParams.FLAG_NOT_TOUCHABLE;
        */

        //mLayoutParams.inputFeatures |= WindowManager.LayoutParams.INPUT_FEATURE_NO_INPUT_CHANNEL;

        //调整悬浮窗口至左下角
        //mLayoutParams.gravity=Gravity.LEFT|Gravity.BOTTOM;
        //以屏幕左上角为原点，设置x、y初始值
        //mLayoutParams.x=0;
        //mLayoutParams.y=50;// 距离底部是 50，向上

        //调整悬浮窗口至左上角
        mLayoutParams.gravity=Gravity.LEFT|Gravity.TOP;
        //以屏幕左上角为原点，设置x、y初始值
        mLayoutParams.x=0;
        mLayoutParams.y=0;
        
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        //mLayoutParams.format=PixelFormat.RGBA_8888;   //设置图片格式，效果为背景透明
        
        mLayoutParams.setTitle("FloatWindows");        
        
        
        //设置悬浮窗口长宽数据
        mLayoutParams.width=WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height=WindowManager.LayoutParams.WRAP_CONTENT;
	}
	
	AnimatorListener mAnimatorListener = new AnimatorListener()
	{

		@Override
		public void onAnimationCancel(Animator animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animator animation) {
			// TODO Auto-generated method stub
		}
		
	};
	
	OnClickListener mOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ObjectAnimator mObjectAnimator;
			TranslateAnimation mTranslateAnimation;
			AlphaAnimation mAlphaAnimation;
			AnimationSet mAnimationSet = new AnimationSet(true);
			AnimationSet mAnimationSet1 = new AnimationSet(true);
			AnimationSet mAnimationSet2 = new AnimationSet(true);
			AnimationSet mAnimationSet3 = new AnimationSet(true);
			AnimationSet mAnimationSet4 = new AnimationSet(true);
			int[]location= new int[2];			
			switch (v.getId())
			{
				case R.id.Button0:
	        		mObjectAnimator = ObjectAnimator.ofFloat(mFloatViewList.get(0), "TranslationX", 0.0f,400.0f);
	        		mObjectAnimator.setDuration(1000);
	        		mObjectAnimator.setRepeatCount(0);
	        		mObjectAnimator.setRepeatMode(ObjectAnimator.REVERSE);
	        		mObjectAnimator.start();
					break;
				case R.id.Button1:
					mFloatViewList.get(1).getLocationOnScreen(location);
					Log.d("huyanwei","1:location[0]="+location[0]+",location[1]="+location[1]);
	        		mObjectAnimator = ObjectAnimator.ofFloat(mFloatViewList.get(1), "TranslationX",mScreenWidth-location[0]-mFloatViewList.get(1).getWidth(),0.0f);
	        		mObjectAnimator.setDuration(1000);
	        		mObjectAnimator.setRepeatCount(0);
	        		mObjectAnimator.setRepeatMode(ObjectAnimator.REVERSE);
	        		mObjectAnimator.start();
					break;
				case R.id.Button2:
					mTranslateAnimation = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,1.0f
							,Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f);
					mTranslateAnimation.setDuration(1000);
					mTranslateAnimation.setFillAfter(true);
					mTranslateAnimation.setFillBefore(false);
					mFloatViewList.get(2).startAnimation(mTranslateAnimation);
					break;
				case R.id.Button3:
					mFloatViewList.get(3).getLocationOnScreen(location);
					Log.d("huyanwei","3:location[0]="+location[0]+",location[1]="+location[1]);
					mTranslateAnimation = new TranslateAnimation(
							Animation.ABSOLUTE,0.0f,Animation.ABSOLUTE,mScreenWidth-location[0]-mFloatViewList.get(3).getWidth()
							,Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f);
					mTranslateAnimation.setDuration(1000);
					mTranslateAnimation.setFillAfter(true);
					mTranslateAnimation.setFillBefore(false);
					mFloatViewList.get(3).startAnimation(mTranslateAnimation);
					break;	
				case R.id.Button4:
				case R.id.Pointer:					
					int[]location1= new int[2];
					int[]location2= new int[2];
					int[]location3= new int[2];
					int[]location4= new int[2];
					mImageButton.getLocationOnScreen(location);
					mFloatViewList.get(0).getLocationOnScreen(location1);
					mFloatViewList.get(1).getLocationOnScreen(location2);
					mFloatViewList.get(2).getLocationOnScreen(location3);
					mFloatViewList.get(3).getLocationOnScreen(location4);		
					
					Log.d("huyanwei","location[1]="+location[1]);
					Log.d("huyanwei","location1[1]="+location1[1]);
					Log.d("huyanwei","location2[1]="+location2[1]);
					Log.d("huyanwei","location3[1]="+location3[1]);
					Log.d("huyanwei","location4[1]="+location4[1]);
					
					
					if(true == mExpand)					
					{
						ObjectAnimator mObjectAnimator_header = ObjectAnimator.ofFloat(mImageButton, "Rotation", 0.0f,90.0f);
						mObjectAnimator_header.setDuration(1000+500);
						mObjectAnimator_header.setRepeatCount(0);
		        		//mObjectAnimator_header.setRepeatCount(ObjectAnimator.INFINITE);
						mObjectAnimator_header.setRepeatMode(ObjectAnimator.REVERSE);
						mObjectAnimator_header.start();
						
						mObjectAnimator_header.addListener(mAnimatorListener);
					}
					else
					{
						ObjectAnimator mObjectAnimator_header = ObjectAnimator.ofFloat(mImageButton, "Rotation", 90.0f,0.0f);
						mObjectAnimator_header.setDuration(1000+500);
						mObjectAnimator_header.setRepeatCount(0);
		        		//mObjectAnimator_header.setRepeatCount(ObjectAnimator.INFINITE);
						mObjectAnimator_header.setRepeatMode(ObjectAnimator.REVERSE);
						mObjectAnimator_header.start();
						
						mObjectAnimator_header.addListener(mAnimatorListener);
					}
					
					
					
					if(true == mExpand)
					{
						mAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);
					}
					else
					{
						mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
					}
					mAlphaAnimation.setFillAfter(true);
					mAlphaAnimation.setFillBefore(false);
					mAlphaAnimation.setDuration(1000);
					
					if(true == mExpand)					
						mTranslateAnimation = new TranslateAnimation(
							Animation.ABSOLUTE,0.0f,Animation.ABSOLUTE,0.0f
							,Animation.ABSOLUTE,0.0f,Animation.ABSOLUTE,location[1]-location1[1]);
					else
						mTranslateAnimation = new TranslateAnimation(
								Animation.ABSOLUTE,0.0f,Animation.ABSOLUTE,0.0f
								,Animation.ABSOLUTE,location[1]-location1[1],Animation.ABSOLUTE,0.0f);						
					mTranslateAnimation.setDuration(1000);
					mTranslateAnimation.setFillAfter(true);
					mTranslateAnimation.setFillBefore(false);
					mAnimationSet1.addAnimation(mTranslateAnimation);
					mAnimationSet1.addAnimation(mAlphaAnimation);
					mAnimationSet1.setFillAfter(true);
					mAnimationSet1.setFillBefore(false);
					mFloatViewList.get(0).startAnimation(mAnimationSet1);
					
					if(true == mExpand)					
						mTranslateAnimation = new TranslateAnimation(
							Animation.ABSOLUTE,0.0f,Animation.ABSOLUTE,0.0f
							,Animation.ABSOLUTE,0.0f,Animation.ABSOLUTE,location[1]-location2[1]);
					else
						mTranslateAnimation = new TranslateAnimation(
								Animation.ABSOLUTE,0.0f,Animation.ABSOLUTE,0.0f
								,Animation.ABSOLUTE,location[1]-location2[1],Animation.ABSOLUTE,0.0f);						
					mTranslateAnimation.setDuration(1000);
					mTranslateAnimation.setFillAfter(true);
					mTranslateAnimation.setFillBefore(false);
					mAnimationSet2.addAnimation(mTranslateAnimation);
					mAnimationSet2.addAnimation(mAlphaAnimation);
					mAnimationSet2.setFillAfter(true);
					mAnimationSet2.setFillBefore(false);
					mFloatViewList.get(1).startAnimation(mAnimationSet2);

					if(true == mExpand)
						mTranslateAnimation = new TranslateAnimation(
							Animation.ABSOLUTE,0.0f,Animation.ABSOLUTE,0.0f
							,Animation.ABSOLUTE,0.0f,Animation.ABSOLUTE,location[1]-location3[1]);
					else
						mTranslateAnimation = new TranslateAnimation(
								Animation.ABSOLUTE,0.0f,Animation.ABSOLUTE,0.0f
								,Animation.ABSOLUTE,location[1]-location3[1],Animation.ABSOLUTE,0.0f);
						
					mTranslateAnimation.setDuration(1000);
					mTranslateAnimation.setFillAfter(true);
					mTranslateAnimation.setFillBefore(false);
					mAnimationSet3.addAnimation(mTranslateAnimation);
					mAnimationSet3.addAnimation(mAlphaAnimation);		
					mAnimationSet3.setFillAfter(true);
					mAnimationSet3.setFillBefore(false);
					mFloatViewList.get(2).startAnimation(mAnimationSet3);
					
					if(true == mExpand)
						mTranslateAnimation = new TranslateAnimation(
							Animation.ABSOLUTE,0.0f,Animation.ABSOLUTE,0.0f
							,Animation.ABSOLUTE,0.0f,Animation.ABSOLUTE,location[1]-location4[1]);
					else
						mTranslateAnimation = new TranslateAnimation(
								Animation.ABSOLUTE,0.0f,Animation.ABSOLUTE,0.0f
								,Animation.ABSOLUTE,location[1]-location4[1],Animation.ABSOLUTE,0.0f);						
					mTranslateAnimation.setDuration(1000);
					mTranslateAnimation.setFillAfter(true);
					mTranslateAnimation.setFillBefore(false);
					mAnimationSet4.addAnimation(mTranslateAnimation);
					mAnimationSet4.addAnimation(mAlphaAnimation);					
					mAnimationSet4.setFillAfter(true);
					mAnimationSet4.setFillBefore(false);
					mFloatViewList.get(3).startAnimation(mAnimationSet4);
					
					mExpand = !mExpand;
					
					break;
			}
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);        
        
        mButton0 = (Button) findViewById(R.id.Button0);        
        mButton1 = (Button) findViewById(R.id.Button1);
        mButton2 = (Button) findViewById(R.id.Button2);
        mButton3 = (Button) findViewById(R.id.Button3);    
        mButton4 = (Button) findViewById(R.id.Button4);
        
        mButton0.setOnClickListener(mOnClickListener);
        mButton1.setOnClickListener(mOnClickListener);
        mButton2.setOnClickListener(mOnClickListener);
        mButton3.setOnClickListener(mOnClickListener);
        mButton4.setOnClickListener(mOnClickListener);        
        
        mContext = this ;

        mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);        

        /*
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);        
        mScreenWidth  = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        */
        mScreenWidth  = mWindowManager.getDefaultDisplay().getWidth();
        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();
        
        mLayoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        onCreateFloatWindows(); // 创建悬浮窗口 
        
        //this.finish(); // 显示悬浮窗口后，Activity自动退出 
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
        //隐藏FloatView图像
		mWindowManager.removeView(mView);
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub	

		//显示FloatView图像
        mWindowManager.addView(mView, mLayoutParams);
		
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
}
