package srclib.huyanwei.shake;

import java.io.FileDescriptor;
import java.io.IOException;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Service;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	//定义sensor管理器  
	private SensorManager mSensorManager;  
  	//震动  
	private Vibrator mVibrator= null;
	
	Sensor mGravitySensor = null;
	
	private MediaPlayer mMediaPlayer = null ;	
	private AudioManager mAudioManager = null ;
	
	private Uri mUri;
	
	private AssetFileDescriptor mAssetFileDescriptor;
	private FileDescriptor mFileDescriptor;
	
	private static final int MSG_PLAY_AUDIO = 1;
	private static final int MSG_STOP_AUDIO = 2;	
	
	@SuppressWarnings("unused")
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
				case  MSG_PLAY_AUDIO:
					play_audio_effect();
					mMediaPlayer.start();
					break;
				case  MSG_STOP_AUDIO:					
					mMediaPlayer.pause();
					break;
				default:
					break;					
			}			
			super.handleMessage(msg);
		}
	};
	
    SensorEventListener mSensorEventListener = new SensorEventListener() {
        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) 
        {
        	
        }
        
        public void onSensorChanged(SensorEvent e) {
        //Log.i(TAG,"e.sensor ="+e.sensor+",mGravitySensor ="+mGravitySensor);
        	int sensorType = e.sensor.getType();
        	//if(sensorType == Sensor.TYPE_ACCELEROMETER)
            if (e.sensor == mGravitySensor) {
                int x = (int) e.values[SensorManager.DATA_X];
                int y = (int) e.values[SensorManager.DATA_Y];
                int z = (int) e.values[SensorManager.DATA_Z];
                
                if(    Math.abs(e.values[SensorManager.DATA_X]) > 14 
                	|| Math.abs(e.values[SensorManager.DATA_Y]) > 14 
                	|| Math.abs(e.values[SensorManager.DATA_Z]) > 14  )
                {
                	//摇动手机后，再伴随震动提示~~  
                	mVibrator.vibrate(500);                	
                	
                	mHandler.removeMessages(MSG_PLAY_AUDIO);                	
                	Message msg = mHandler.obtainMessage();                	
                	msg.what = MSG_PLAY_AUDIO;
                	mHandler.sendMessage(msg);
                }
            }
        }
    };
	
    void play_audio_effect()
    {
		if(mMediaPlayer == null)
		{
			mMediaPlayer = new MediaPlayer();
		}
		
		mMediaPlayer.reset();		
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		/* 下面设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到用户面前 */
		mMediaPlayer.setOnBufferingUpdateListener(null);  // Buffering		
		mMediaPlayer.setLooping(false);				 // loop play.		
		mMediaPlayer.setOnPreparedListener(null); // 准备好了的回调函数。		
		mMediaPlayer.setOnCompletionListener(null); // 视屏播放完成。
		mMediaPlayer.setOnInfoListener(null);
		
		mAssetFileDescriptor = this.getResources().openRawResourceFd(R.raw.phonering);
	    if(mAssetFileDescriptor == null)
	    {
		    mUri= Uri.parse("/storage/sdcard0/notify.mp3");
			try {
				mMediaPlayer.setDataSource(this, mUri);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//mMediaPlayer.setDataSource(mUri.toString());
	    }
	    else
	    {
			try {
				mMediaPlayer.setDataSource(mAssetFileDescriptor.getFileDescriptor(),mAssetFileDescriptor.getStartOffset(),mAssetFileDescriptor.getLength());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mAssetFileDescriptor.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }	    
		try {
			mMediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//获取传感器管理服务  
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);  
        //震动  
		mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);  
		
		//mGravitySensor = mSm.getDefaultSensor(android.hardware.Sensor.TYPE_GRAVITY);
		mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub		
		mSensorManager.unregisterListener(mSensorEventListener);		
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
		//加速度传感器  
		
		  mSensorManager.registerListener(mSensorEventListener,mGravitySensor,SensorManager.SENSOR_DELAY_NORMAL);
		  //还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，  
		  //根据不同应用，需要的反应速率不同，具体根据实际情况设定		
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
		if( (mMediaPlayer != null ) && (mMediaPlayer.isPlaying()))
		{
			mMediaPlayer.stop();		
			mMediaPlayer.release();
			mMediaPlayer = null;
		}		
		super.onStop();
	}

}
