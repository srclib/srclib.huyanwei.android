package srclib.huyanwei.effect;

import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {

	private ListView mListView;
    private ListItemAdapter mListItemAdapter;
    private EffectAnimation mEffectAnimation;
	
    private class ListItemAdapter extends BaseAdapter {
    	
    	private int icons[] =
    		{
    			R.drawable.icon_1,
    			R.drawable.icon_2,
    			R.drawable.icon_3,
    			R.drawable.icon_4,
    			R.drawable.icon_5,
    			R.drawable.icon_6,
    			R.drawable.icon_7,
    			R.drawable.icon_8,
    			R.drawable.icon_9,
    			R.drawable.icon_10,
    		};
    	
    	
        public ListItemAdapter(Context c) {
            mContext = c;            
            mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return 10;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	View temp_view = mLayoutInflater.inflate(R.layout.list_item, null);
        	if(temp_view != null)
        	{	
	        	ImageView mImageView = (ImageView) temp_view.findViewById(R.id.img);
	        	TextView  mTextView  = (TextView)  temp_view.findViewById(R.id.txt);
	        	
	        	mImageView.setImageResource(icons[position%icons.length]);
	        	mTextView.setText("List Item"+position);
	        	convertView =  temp_view;
        	}
			return convertView;
        }
        private Context mContext;
        private LayoutInflater mLayoutInflater;
    };
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mListView = (ListView) findViewById(R.id.listView_root);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
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

		mListItemAdapter = new ListItemAdapter(this);
		mListView.setAdapter(mListItemAdapter);

		startEffection();
		
		super.onResume();
	}

	Runnable effect_runnable = new Runnable()
	{
		private float dalta_ratio = 0.0f;
		private Matrix mMatrix;
		
		@SuppressLint("NewApi")
		@Override
		public void run() {
			//TODO Auto-generated method stub			
			try {
				Thread.sleep(10);				
				dalta_ratio += 0.01f;				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("srclib.huyanwei.effect","ratio="+dalta_ratio);
/*			
			if(dalta_ratio < 1.0f)
			{
				mListView.postDelayed(this,1000);
			}
*/
			mListView.startAnimation(mEffectAnimation);
		}
	};
	
	@SuppressLint("NewApi")
	private void startEffection() {
		// TODO Auto-generated method stub		
/*
  		// M1
		mEffectAnimation = new EffectAnimation();
		mEffectAnimation.setFillAfter(true);
		mEffectAnimation.setFillBefore(false);
		mEffectAnimation.setDuration(3000);
		mListView.startAnimation(mEffectAnimation);
*/
		
		// M2
		mListView.setAlpha(0);
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator objectAnimator0_px = ObjectAnimator.ofFloat(mListView, "pivotX",0f);
		ObjectAnimator objectAnimator0_py = ObjectAnimator.ofFloat(mListView, "pivotY",0f);
		ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(mListView,"rotationY", 0f, 30f);
		//ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(mListView,"rotation", -360f, 0f);
		ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(mListView, "alpha",0f, 1f);
		set.setDuration(3000);
		
		set.play(objectAnimator0_px).before(objectAnimator1);
		set.play(objectAnimator0_py).before(objectAnimator1);		
		set.play(objectAnimator0_px).with(objectAnimator0_py);
		set.play(objectAnimator2).with(objectAnimator1);
		//set.playTogether(objectAnimator1, objectAnimator2);
		set.setStartDelay(10);
		set.start();
		
		
		//M3
		WindowManager.LayoutParams mLayoutParams_start = new WindowManager.LayoutParams();
		mLayoutParams_start.y = -700;
		
		WindowManager.LayoutParams mLayoutParams_stop = new WindowManager.LayoutParams();
		mLayoutParams_stop.y = 0;
		
		AnimatorSet mAnimatorSet = new AnimatorSet();
		ObjectAnimator objectAnimator_xx = ObjectAnimator.ofObject(mListView,"LayoutParams",new LayoutParamsEvaluator(),mLayoutParams_start, mLayoutParams_stop);
		mAnimatorSet.setDuration(3000);
		mAnimatorSet.play(objectAnimator_xx);
		mAnimatorSet.setStartDelay(10);
		mAnimatorSet.start();
		
		// M4
		//mListView.postDelayed(effect_runnable, 10);
	}

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
		super.onDestroy();
	}
}
