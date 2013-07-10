package srclib.huyanwei.backlight;

import android.os.Bundle;
import android.os.IPowerManager;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.view.Menu;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {

	private SeekBar mSeekBar;
	
	private PowerManager mPowerManager;
	
	private int brightness;
	
	private ContentResolver resolver ;
	
	private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener()
	{
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			
				brightness = progress ;
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

				setBrightness(brightness);

		}		
	};
	
    /*
    *Set the brightness of devices
    */
   private void setBrightness(int brightness) {
	   
	   /*
       try {
           IPowerManager power = IPowerManager.Stub.asInterface(
                   ServiceManager.getService("power"));
           //Only set backlight value when screen is on
           if (power != null) {
               if (power.isScreenOn()) {
                   power.setTemporaryScreenBrightnessSettingOverride(brightness);
               } else {
                   power.setTemporaryScreenBrightnessSettingOverride(-1);
               }
           }
       } catch (RemoteException doe) {

       }
       */	    
	   mPowerManager.setBacklightBrightness(brightness);

	   Settings.System.putInt(resolver,Settings.System.SCREEN_BRIGHTNESS, brightness);
	   
   }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		resolver= this.getBaseContext().getContentResolver();

		mSeekBar = (SeekBar)findViewById(R.id.seekBar1);
		
		//mSeekBar.setMin(0); only 0
		mSeekBar.setMax(255);
		mSeekBar.setProgress(125);

		mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		
		mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
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
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		brightness = Settings.System.getInt(resolver,Settings.System.SCREEN_BRIGHTNESS, 20);

		mSeekBar.setProgress(brightness);
		
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
