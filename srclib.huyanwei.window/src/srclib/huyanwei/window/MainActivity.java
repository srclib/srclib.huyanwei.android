package srclib.huyanwei.window;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;


public class MainActivity extends Activity {

	private Context mContext;
	private LayoutInflater mLayoutInflater ;
	private View mView ;
	
	private WindowManager mWindowManager;
	
	private WindowManager.LayoutParams mLayoutParams;

	private FloatView mFloatView = null ;	
	
	public void onCreateFloatWindows()
	{
        /*
        mView =  (View)mLayoutInflater.inflate(R.layout.window, null);
        */
        mFloatView = new FloatView(getApplicationContext());
        //mFloatView.setImageResource(R.drawable.ic_launcher);
        mFloatView.setBackgroundColor(Color.BLACK);

        /*
        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.FILL_PARENT);
        */
        mLayoutParams = ((FloatWindowApp)getApplication()).getParams();
        
        /*
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                			| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                			| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                			| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        
        mLayoutParams.setTitle("PointerLocationFloatWindows");        
        
        mLayoutParams.inputFeatures |= WindowManager.LayoutParams.INPUT_FEATURE_NO_INPUT_CHANNEL;
        */
        
        /**
         *以下都是WindowManager.LayoutParams的相关属性
         * 具体用途可参考SDK文档
         */
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        //mLayoutParams.type=WindowManager.LayoutParams.TYPE_PHONE;   //设置window type
        mLayoutParams.format=PixelFormat.RGBA_8888;   //设置图片格式，效果为背景透明

        //设置Window flag
        mLayoutParams.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                              | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        /*
         * 下面的flags属性的效果形同“锁定”。
         * 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
         wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL 
                               | LayoutParams.FLAG_NOT_FOCUSABLE
                               | LayoutParams.FLAG_NOT_TOUCHABLE;
        */
        
        
        mLayoutParams.gravity=Gravity.LEFT|Gravity.TOP;   //调整悬浮窗口至左上角
        //以屏幕左上角为原点，设置x、y初始值
        mLayoutParams.x=0;
        mLayoutParams.y=0;
        
        //设置悬浮窗口长宽数据
        mLayoutParams.width=54;
        mLayoutParams.height=96;
        
        //显示FloatView图像
        mWindowManager.addView(mFloatView, mLayoutParams);

	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        mContext = this ;

        mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);        

        mLayoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        onCreateFloatWindows(); // 创建悬浮窗口 
        
        this.finish(); // 显示悬浮窗口后，Activity自动退出 
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
		mWindowManager.removeView(mFloatView);		
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub	
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
