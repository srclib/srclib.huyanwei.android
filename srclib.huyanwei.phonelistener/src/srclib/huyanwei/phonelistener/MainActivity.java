package srclib.huyanwei.phonelistener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

	private SlideButton mSlideButton;
	
	private ImageButton mImageButton;
	
	private LinearLayout mlinearLayout;
	
	private SlideButton.Callback mCallback = new SlideButton.Callback()
	{
		public void onStateChange(boolean state) {
			// TODO Auto-generated method stub
			if(state)
			{
				mImageButton.setVisibility(View.VISIBLE);
			}
			else
			{
				mImageButton.setVisibility(View.INVISIBLE);
			}
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.activity_main);
        
        mlinearLayout = (LinearLayout)findViewById(R.id.linearLayout3);  
        
        mSlideButton = (SlideButton) mlinearLayout.findViewById(R.id.SlideButton1);     
        mSlideButton.setCallback(mCallback);
        
        mImageButton = (ImageButton) mlinearLayout.findViewById(R.id.imageButton1);        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
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
