package srclib.huyanwei.torch;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	Parameters mParameters;
	Camera 	   mCamera;	
	
	Button mButton;
	
	private final int STATE_ON  = 1 ;
	private final int STATE_OFF = 0 ;
	
	private int state = STATE_OFF;
	
	
	View.OnClickListener mOnClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{
			case R.id.torch:
				if(state == STATE_OFF)
				{
					mButton.setText(R.string.torch_on);
					onOpenFlashLight();
					state = STATE_ON;
				}
				else
				{
					mButton.setText(R.string.torch_off);
					onCloseFlashLight();
					state = STATE_OFF;
				}
				break;
			}			
		}
	};
	
	
	private void onOpenFlashLight()
	{		
		if(	mCamera != null)
		{
			mParameters = mCamera.getParameters();		
			mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(mParameters);
		}
	}
	
	private void onCloseFlashLight()
	{
		if(	mCamera != null)
		{
			mParameters = mCamera.getParameters();
			mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);		
			mCamera.setParameters(mParameters);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mButton = (Button)findViewById(R.id.torch);
		mButton.setOnClickListener(mOnClickListener);

		mButton.setText(R.string.torch_on);		
		state = STATE_ON;
		
		mCamera = Camera.open();
		if(	mCamera != null)
		{
			mCamera.startPreview();
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		if(	mCamera != null)
		{
			mCamera.release();
		}
		
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
