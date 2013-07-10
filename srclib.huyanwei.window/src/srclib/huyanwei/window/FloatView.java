package srclib.huyanwei.window;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;


public class FloatView extends ImageView {
	     private float mTouchStartX;
	     private float mTouchStartY;
	     private float x;
	     private float y;

	     GestureDetector mGestureDetector;
	     
	     private WindowManager wm=(WindowManager)getContext().getApplicationContext().getSystemService("window");
	     
	     //��wmParamsΪ��ȡ��ȫ�ֱ��������Ա����������ڵ�����
	     private WindowManager.LayoutParams wmParams = ((FloatWindowApp)getContext().getApplicationContext()).getParams();
	 
	     public FloatView(Context context) {
	         super(context);

	         //mGestureDetector = new GestureDetector(context, gestureListener);
	         
	         // TODO Auto-generated constructor stub
	     }
	     
	      @Override
	      public boolean onTouchEvent(MotionEvent event) {
	    	  
				if(mGestureDetector != null)
				{
						return mGestureDetector.onTouchEvent(event);
				}
				else
				{
					//getRawX()��ȡ�����Ļ�����꣬������Ļ���Ͻ�Ϊԭ��         
					x = event.getRawX();   
					y = event.getRawY()-25;   //25��ϵͳ״̬���ĸ߶�
					Log.i("currP", "currX"+x+"====currY"+y);
					switch (event.getAction())
					{
			             case MotionEvent.ACTION_DOWN:
			                 //getX()��ȡ���View�����꣬���Դ�View���Ͻ�Ϊԭ��
			                 mTouchStartX =  event.getX(); 
			                 mTouchStartY =  event.getY();
			                 
			                 Log.i("startP", "startX"+mTouchStartX+"====startY"+mTouchStartY);
			                 
			                 break;
			             case MotionEvent.ACTION_MOVE:                
			                 updateViewPosition();
			                 break;
			 
			             case MotionEvent.ACTION_UP:
			                 updateViewPosition();
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
	         wm.updateViewLayout(this, wmParams);
	         
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