package srclib.huyanwei.scale;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {

	TextView mTextView ;
	AnimationSet mAnimationSet;
	
	int root_width  = 0 ;
	int root_height = 0 ;
	
	int view_width = 0 ;
	int view_height = 0 ;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTextView = (TextView)findViewById(R.id.hello);
		
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
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	Runnable animation_runnable = new Runnable()
	{
		@Override
		public void run() 
		{
			// TODO Auto-generated method stub
			Rect mRect = new Rect();
			
			View mRootView = MainActivity.this.getWindow().getDecorView();
			mRootView.getWindowVisibleDisplayFrame(mRect);			
			root_width  = mRect.right - mRect.left;
			root_height = mRect.bottom - mRect.top ;
			
			Log.d("huyanwei","Rect Of Root="+mRect.toString());
			//mTextView.getDrawingRect(mRect);
			//mTextView.getWindowVisibleDisplayFrame(mRect);
			//view_width  = mRect.right - mRect.left;
			//view_height = mRect.bottom - mRect.top ;					
			//Log.d("huyanwei","Rect Of View ="+mRect.toString());

			view_width  = mTextView.getRight()  - mTextView.getLeft();
			view_height = mTextView.getBottom() - mTextView.getTop();
			
			Log.d("huyanwei",mTextView.getLeft()+","+mTextView.getTop()+"-"+mTextView.getRight()+","+mTextView.getBottom());
			
			Log.d("huyanwei","view_width ="+view_width+",view_height="+view_height);
			
			float delta_x = (float)(root_width  - view_width);
			float delta_y = (float)(root_height - 4*view_height);
			
			mAnimationSet = new AnimationSet(false);		
			TranslateAnimation mTranslateAnimation = new TranslateAnimation(Animation.ABSOLUTE,delta_x/0.5f,Animation.ABSOLUTE,delta_y);
			ScaleAnimation     mScaleAnimation     =  new ScaleAnimation(Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1.0f);
			mAnimationSet.addAnimation(mTranslateAnimation);
			mAnimationSet.addAnimation(mScaleAnimation);
			mAnimationSet.setDuration(3000);
			
			mAnimationSet.setFillBefore(false);
			mAnimationSet.setFillAfter(true);
		
			mTextView.startAnimation(mAnimationSet);					
		}		
	};
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mTextView.postDelayed(animation_runnable, 1000);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}
