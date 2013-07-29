package srclib.huyanwei.phonelistener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

	private String TAG = "srclib.huyanwei.phonelistener.MainActivity";
	
	private Context 	mContext;
	
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
				
				Intent svc = new Intent(mContext, PhoneListenerService.class);
				mContext.stopService(svc);
			}
		}
	};
	
	private OnClickListener mOnClickListener =  new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.imageButton1:
					 Log.d(TAG,"huyanwei start service by manual.");
					 Intent svc = new Intent(mContext, PhoneListenerService.class);
					 mContext.startService(svc);
					break;
			}
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.activity_main);
        
        mContext= this;
        
        mlinearLayout = (LinearLayout)findViewById(R.id.linearLayout3);  
        
        mSlideButton = (SlideButton) mlinearLayout.findViewById(R.id.SlideButton1);     
        mSlideButton.setCallback(mCallback);
        
        mImageButton = (ImageButton) mlinearLayout.findViewById(R.id.imageButton1);    
        
        mImageButton.setOnClickListener(mOnClickListener);
        
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
