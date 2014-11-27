package srclib.huyanwei.screeneffection;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends Activity {

	private ScreenEffectionView mScreenEffectionView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mScreenEffectionView = new ScreenEffectionView(this);
		
		mScreenEffectionView.setBackgroundColor(Color.rgb(0, 0, 0));
		
		setContentView(mScreenEffectionView);		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		float x = event.getX();
		float y = event.getY();
		
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				//mScreenEffectionView.AddWaterWave(x, y, 1.0f);		
				break;
			case MotionEvent.ACTION_MOVE:
				//mScreenEffectionView.AddWaterWave(x, y, 1.0f);
				break;				
			case MotionEvent.ACTION_UP:
				break;
		}		
		return true;		
		//return super.onTouchEvent(event);
	}
}
