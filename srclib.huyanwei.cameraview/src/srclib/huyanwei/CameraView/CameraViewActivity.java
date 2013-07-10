package srclib.huyanwei.CameraView;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class CameraViewActivity extends Activity {
	
	ImageView mImageView1 ;
	ImageView mImageView2 ;
	
	Rotate3dAnimation leftAnimation ;
	Rotate3dAnimation rightAnimation ;
	
	ScaleAnimation scaleAnimation ;
	RotateAnimation rotateAnimation ;
	TranslateAnimation translateAnimation ;
	AlphaAnimation alphaAnimation ;
	
	AnimationSet animationSet;
	
	boolean touch_state = false ;
	
	float touch_x = 0 ;
	float touch_y = 0 ;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mImageView1 = (ImageView) findViewById(R.id.imageView1);        
        mImageView2 = (ImageView) findViewById(R.id.imageView2);

        mImageView2.setVisibility(View.GONE);      
        
        animationSet = new AnimationSet(true);//创建一个AnimationSet对象
        
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
        final float mCenterX = 160;
        final float mCenterY = 240;
        
        float x_pos = 0f;        
        
    	float delta_x ;
    	float delta_y ;
    	float delta_z ;

    	float last_angel = 0;
    	float angel ;
        
        if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
        	touch_state = true ;
        	
        	touch_x = event.getX();
        	touch_y = event.getY();
        	
        	x_pos = touch_x ;
        	
        	//last_angel = (event.getX() - x_pos ) * 90.0f /320.0f ;
        	
		}
        else if(event.getAction() == MotionEvent.ACTION_MOVE)
        {
        	
        	/*
        	delta_x = event.getX() - touch_x ;
        	delta_y = event.getY() - touch_y ;
        	delta_z = 0f ;
        	
        	angel = delta_x*90.0f/(320);
        	
        	leftAnimation =  new Rotate3dAnimation(last_angel, last_angel+angel, mCenterX, mCenterY,0,0,0,true);
        	rightAnimation = new Translate3dAnimation(delta_x,delta_y,delta_z);
        	
            leftAnimation.setInterpolator(new AccelerateInterpolator());
            leftAnimation.setFillAfter(true);
            leftAnimation.setDuration(1000);
            
            rightAnimation.setInterpolator(new AccelerateInterpolator());
            rightAnimation.setFillAfter(true);
            rightAnimation.setDuration(1000);

        	mImageView1.startAnimation(rightAnimation);
        	mImageView1.startAnimation(leftAnimation);
        	
        	last_angel += angel ;
        	
        	*/
        	
        }
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
        	touch_state = false ;

        	delta_x = event.getX() - touch_x ;
        	delta_y = 0f;
        	delta_z = 0f ;
        	
        	angel = delta_x*90.0f/(320);
        	
        	leftAnimation =  new Rotate3dAnimation(-0,  -90, mCenterX, mCenterY,0,0,0,false);            
        	rightAnimation = new Rotate3dAnimation(90,  -0, mCenterX, mCenterY,0,0,0,false);
        	
            leftAnimation.setInterpolator(new AccelerateInterpolator());            
            leftAnimation.setFillAfter(true);
            leftAnimation.setDuration(1000);
            
            rightAnimation.setInterpolator(new AccelerateInterpolator());
            rightAnimation.setFillAfter(true);
            rightAnimation.setDuration(1000);

        	mImageView1.startAnimation(leftAnimation);
        	mImageView1.startAnimation(rightAnimation);

        	
        	scaleAnimation = new ScaleAnimation(1f, 0.2f, 1f, 0.2f, 
                    Animation.RELATIVE_TO_SELF, 0.2f, 
                    Animation.RELATIVE_TO_SELF, 0.2f); 
            scaleAnimation.setDuration(2000); 
            //animationSet.addAnimation(scaleAnimation);//将ScaleAnimation对象添加到AnimationSet当中
        	
            
            // TODO Auto-generated method stub 
            //第一个参数fromDegrees为动画起始时的旋转角度     
            //第二个参数toDegrees为动画旋转到的角度    
            //第三个参数pivotXType为动画在X轴相对于物件位置类型   
            //第四个参数pivotXValue为动画相对于物件的X坐标的开始位置  
            //第五个参数pivotXType为动画在Y轴相对于物件位置类型    
            //第六个参数pivotYValue为动画相对于物件的Y坐标的开始位置  
            rotateAnimation = new RotateAnimation(0, 180, 
                    Animation.RELATIVE_TO_SELF, 0.5f, 
                    Animation.RELATIVE_TO_SELF, 2.0f); 
            rotateAnimation.setDuration(2000); 
            //animationSet.addAnimation(rotateAnimation);//将RotateAnimation对象添加到AnimationSet当中

			// TODO Auto-generated method stub 
            //fromXDelta为动画起始时 X坐标上的移动位置     
            //toXDelta为动画结束时 X坐标上的移动位置       
            //fromYDelta为动画起始时Y坐标上的移动位置      
            //toYDelta为动画结束时Y坐标上的移动位置  
            //Animation.RELATIVE_TO_SELF 相对本控件 
            translateAnimation = new TranslateAnimation( 
                    Animation.RELATIVE_TO_SELF, 1.0f, 
                    Animation.RELATIVE_TO_SELF, 100.0f, 
                    Animation.RELATIVE_TO_SELF, 1.0f, 
                    Animation.RELATIVE_TO_SELF, 100.0f); 
            //设置动画持续时间  
            translateAnimation.setDuration(2000); 
            //animationSet.addAnimation(translateAnimation);//将TranslateAnimation对象添加到AnimationSet当中
          
			// 第一个参数fromAlpha为 动画开始时候透明度 
            // 第二个参数toAlpha为 动画结束时候透明度 
            // 0.0表示完全透明 
            // 1.0表示完全不透明 
        	
            alphaAnimation=new AlphaAnimation(1.0f, 0.0f); 
            //设置时间
        	alphaAnimation.setInterpolator(new AccelerateInterpolator());            
            //alphaAnimation.setFillAfter(false);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setDuration(1000); 
            animationSet.addAnimation(alphaAnimation);//将AlphaAnimation对象添加到AnimationSet当中
            
            mImageView1.startAnimation(animationSet);
        }	     
        //return true ;
		return super.onTouchEvent(event);
	}
}