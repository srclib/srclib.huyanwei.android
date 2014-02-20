package srclib.huyanwei.receiver;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button mButton;
	
	private AudioManager mAudioManager;
	
	private MediaPlayer mMediaPlayer;
	
	private PowerManager mPowerManager;
	
	private PowerManager.WakeLock mWakeLock;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.button1:
					mMediaPlayer.start();
					break;
			}			
		}		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mButton = (Button) findViewById(R.id.button1);		
		mButton.setOnClickListener(mOnClickListener);
		
		mAudioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
		
		mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		
		mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "screen_on");	
		
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
		
		if( mMediaPlayer != null)
		{
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null ;
		}
		
		mAudioManager.setMode(AudioManager.MODE_NORMAL);

		mWakeLock.release();
		
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
		
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        
        //mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),AudioManager.FLAG_PLAY_SOUND);
        
        mAudioManager.setSpeakerphoneOn(false);
        
        if( mMediaPlayer == null)
		{
        	mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.disagree);        	
		}	
        mMediaPlayer.setLooping(true);
        
        mWakeLock.acquire();
        
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
