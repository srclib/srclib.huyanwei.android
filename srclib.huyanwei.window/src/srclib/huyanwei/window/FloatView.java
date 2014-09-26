package srclib.huyanwei.window;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import java.util.ArrayList;


public class FloatView extends ImageView {
	     private float mTouchStartX;
	     private float mTouchStartY;
	     private float x;
	     private float y;

	     private GestureDetector mGestureDetector;	     
	     private WindowManager wm ;
	     private View mParentView;
	     
	     //��wmParamsΪ��ȡ��ȫ�ֱ��������Ա����������ڵ�����
	     private WindowManager.LayoutParams wmParams ; 
	 
	     public FloatView(Context context) {
	         
	    	 this(context, null);
	         // TODO Auto-generated constructor stub
	     }
	     
	 	public FloatView(Context context, AttributeSet attrs) {
			this(context, attrs, 0);
			// TODO Auto-generated constructor stub
		}
	 	
		public FloatView(Context context, AttributeSet attrs, int defStyle) 
		{
			super(context, attrs, defStyle);
	        wm=(WindowManager)getContext().getApplicationContext().getSystemService("window");
	        //mGestureDetector = new GestureDetector(context, gestureListener);
	        wmParams = (LayoutParams) this.getLayoutParams();
		}
		
	      @Override
	      public boolean onTouchEvent(MotionEvent event) {
	    	  
	    	  	int [] location = new int[2];
	    	  	
	    	  	mParentView = (View) this.getParent();
	    	  	
				if(mGestureDetector != null)
				{
						return mGestureDetector.onTouchEvent(event);
				}
				else
				{
					//getRawX()��ȡ�����Ļ�����꣬������Ļ���Ͻ�Ϊԭ��         
					x = event.getRawX();   
					y = event.getRawY();
					Log.i("currP", "currX"+x+"====currY"+y);
					switch (event.getAction())
					{
			             case MotionEvent.ACTION_DOWN:
			                 //getX()��ȡ���View�����꣬���Դ�View���Ͻ�Ϊԭ��,
			            	 //getRawX()��ȡ�����Ļ�����꣬������Ļ���Ͻ�Ϊԭ��.         
			                 mTouchStartX =  event.getRawX(); 
			                 mTouchStartY =  event.getRawY();
			                 mParentView.getLocationInWindow(location);
			                 Log.i("startP", "startX"+mTouchStartX+"====startY"+mTouchStartY);
			                 break;
			             case MotionEvent.ACTION_MOVE:
			    	         wmParams.x=(int)( x-mTouchStartX);
			    	         //System.out.println(mTouchStartX);
			    	         wmParams.y=(int) (y-mTouchStartY);
			    	         wm.updateViewLayout(mParentView, wmParams);
			                 break;			 
			             case MotionEvent.ACTION_UP:
			                 mTouchStartX=mTouchStartY=0;
			                 break;
	             	}
	             	return true;
				}
	         }
	      
	      private void updateViewPosition(){
	         //���¸�������λ�ò���,x���������Ļ��λ�ã�mTouchStartX�������ͼƬ��λ��
	         wmParams.x=(int)( x-mTouchStartX);
	         //System.out.println(mTouchStartX);
	         wmParams.y=(int) (y-mTouchStartY);
	         wm.updateViewLayout(mParentView, wmParams);
	      }
	      

	   // ����ʶ��
	  	GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() 
	  	{
	  		//@Override
	  		public boolean onDown(MotionEvent e) {
	  			// TODO Auto-generated method stub
	  			Log.d("OnGestureListener", "onDown");
	  			
                //getX()��ȡ���View�����꣬���Դ�View���Ͻ�Ϊԭ��
                mTouchStartX =  e.getX(); 
                mTouchStartY =  e.getY();

	  			return false;
	  		}

	  		//@Override
	  		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
	  				float velocityY) {
	  			Log.d("OnGestureListener", "onFling");

				x = e2.getRawX();   
				y = e2.getRawY();

                updateViewPosition();                
                
	  			return true;
	  		}

	  		//@Override
	  		public void onLongPress(MotionEvent e) {
	  			// TODO Auto-generated method stub
	  			Log.d("OnGestureListener", "onLongPress");
	  		}

	  		//@Override
	  		public boolean onScroll(MotionEvent e1, MotionEvent e2,
	  				float distanceX, float distanceY) {
	  			Log.d("OnGestureListener", "onShowPress");
	  			//mTouchRawX = e2.getRawX();
	  			//mTouchRawY = e2.getRawY();
	  			//mHandler.sendEmptyMessage(MESSAGE_MOVE);

	  			return true;
	  		}

	  		//@Override
	  		public void onShowPress(MotionEvent e) {
	  			Log.d("OnGestureListener", "onShowPress");
	  		}

	  		//@Override
	  		public boolean onSingleTapUp(MotionEvent e) {
	  			Log.d("OnGestureListener", "onSingleTapUp");

				x = e.getRawX();   
				y = e.getRawY();

                updateViewPosition();
                mTouchStartX=mTouchStartY=0;
                
	  			return false;
	  		}
	  	};
	 
	 }