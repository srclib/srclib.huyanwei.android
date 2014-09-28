package srclib.huyanwei.observer;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {

	private final String TAG = "srclib.huyanwei.observer"; 
	TextView mTextView ;
	
	ViewTreeObserver mViewTreeObserver ;
	
	boolean bMeasure = false;
	
	int max_line = 1;
	
	OnClickListener  mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.more:
					onToggle();
					break;					
				default:
					break;
			}
			
		}		
	};

	@SuppressLint("NewApi")
	public class LayoutParamsEvaluator implements TypeEvaluator<WindowManager.LayoutParams>
	{
	    public WindowManager.LayoutParams evaluate(float fraction, WindowManager.LayoutParams startValue, WindowManager.LayoutParams endValue) {
	    	WindowManager.LayoutParams mLayoutParams = startValue;
	        int start = startValue.y;
	        int end   = endValue.y; 
	        mLayoutParams.y = (int) (start + fraction * (end - start));
	        return mLayoutParams;
	    }
	}	
	
	@SuppressLint("NewApi")
	public void onToggle()
	{
		Log.d(TAG,"onToggle() max_line="+max_line);
		ObjectAnimator mObjectAnimator =  	ObjectAnimator.ofInt(mTextView, "MaxLines", 1,max_line);
		mObjectAnimator.setDuration(3000);
		mObjectAnimator.start();
	}	
	
	
	//��ȡViewTreeObserver View�۲��ߣ���ע��һ�������¼������ʱ������View��δ���Ƶ�ʱ��ִ�еģ�Ҳ������onDraw֮ǰ  
    //textViewĬ����û��maxLine���Ƶģ������ҾͿ��Լ��㵽��ȫ��ʾ��maxLine
	OnPreDrawListener mOnPreDrawListener  = new OnPreDrawListener ()
	{
		@SuppressLint("NewApi")
		@Override
		public boolean onPreDraw() {
			// TODO Auto-generated method stub
			if(!bMeasure)
			{	
				//����maxLine��Ĭ��ֵ�������û�����View����������maxLine��TextView  
				max_line = mTextView.getLineCount();
				mTextView.setMaxLines(1);
				Log.d(TAG,"onPreDraw() max_line="+max_line);				
				bMeasure = true;
			}
			return true;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTextView = (TextView) this.findViewById(R.id.more);		
		mTextView.setOnClickListener(mOnClickListener);
		
		mViewTreeObserver=mTextView.getViewTreeObserver();
		mViewTreeObserver.addOnPreDrawListener(mOnPreDrawListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		mViewTreeObserver.removeOnPreDrawListener(mOnPreDrawListener);
		
		super.onDestroy();
	}
}
