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
        
        animationSet = new AnimationSet(true);//����һ��AnimationSet����
        
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
            //animationSet.addAnimation(scaleAnimation);//��ScaleAnimation������ӵ�AnimationSet����
        	
            
            // TODO Auto-generated method stub 
            //��һ������fromDegreesΪ������ʼʱ����ת�Ƕ�     
            //�ڶ�������toDegreesΪ������ת���ĽǶ�    
            //����������pivotXTypeΪ������X����������λ������   
            //���ĸ�����pivotXValueΪ��������������X����Ŀ�ʼλ��  
            //���������pivotXTypeΪ������Y����������λ������    
            //����������pivotYValueΪ��������������Y����Ŀ�ʼλ��  
            rotateAnimation = new RotateAnimation(0, 180, 
                    Animation.RELATIVE_TO_SELF, 0.5f, 
                    Animation.RELATIVE_TO_SELF, 2.0f); 
            rotateAnimation.setDuration(2000); 
            //animationSet.addAnimation(rotateAnimation);//��RotateAnimation������ӵ�AnimationSet����

			// TODO Auto-generated method stub 
            //fromXDeltaΪ������ʼʱ X�����ϵ��ƶ�λ��     
            //toXDeltaΪ��������ʱ X�����ϵ��ƶ�λ��       
            //fromYDeltaΪ������ʼʱY�����ϵ��ƶ�λ��      
            //toYDeltaΪ��������ʱY�����ϵ��ƶ�λ��  
            //Animation.RELATIVE_TO_SELF ��Ա��ؼ� 
            translateAnimation = new TranslateAnimation( 
                    Animation.RELATIVE_TO_SELF, 1.0f, 
                    Animation.RELATIVE_TO_SELF, 100.0f, 
                    Animation.RELATIVE_TO_SELF, 1.0f, 
                    Animation.RELATIVE_TO_SELF, 100.0f); 
            //���ö�������ʱ��  
            translateAnimation.setDuration(2000); 
            //animationSet.addAnimation(translateAnimation);//��TranslateAnimation������ӵ�AnimationSet����
          
			// ��һ������fromAlphaΪ ������ʼʱ��͸���� 
            // �ڶ�������toAlphaΪ ��������ʱ��͸���� 
            // 0.0��ʾ��ȫ͸�� 
            // 1.0��ʾ��ȫ��͸�� 
        	
            alphaAnimation=new AlphaAnimation(1.0f, 0.0f); 
            //����ʱ��
        	alphaAnimation.setInterpolator(new AccelerateInterpolator());            
            //alphaAnimation.setFillAfter(false);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setDuration(1000); 
            animationSet.addAnimation(alphaAnimation);//��AlphaAnimation������ӵ�AnimationSet����
            
            mImageView1.startAnimation(animationSet);
        }	     
        //return true ;
		return super.onTouchEvent(event);
	}
}