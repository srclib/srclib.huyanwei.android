package srclib.huyanwei.flow;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.util.Log;

public class FlowActivity extends Activity {
	String TAG = "Activity1" ;
	ActivityManager am = null ;
	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		Log.e(TAG,"onAttachedToWindow");
		super.onAttachedToWindow();
	}
	
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
    	Log.e(TAG,"onCreateContextMenu");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		Log.e(TAG,"onCreateView");
		return super.onCreateView(name, context, attrs);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.e(TAG,"onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		Log.e(TAG,"onDetachedFromWindow");
		super.onDetachedFromWindow();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.e(TAG,"onKeyDown");
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.e(TAG,"onKeyLongPress");
		return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.e(TAG,"onKeyUp");
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.e(TAG,"onPause");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.e(TAG,"onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.e(TAG,"onResume");
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.e(TAG,"onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.e(TAG,"onStop");
		super.onStop();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.e(TAG,"onTouchEvent");
		return super.onTouchEvent(event);
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.e(TAG,"onCreate");
        am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        //ComponentName cn = am.getRunningTasks(2).get(0).topActivity;
        //cn.getClassName().equals("")
        //cn.getPackageName().equals("com.")
    }
}