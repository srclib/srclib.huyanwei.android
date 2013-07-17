package srclib.huyanwei.surfacer;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private String TAG = "srclib.huyanwei.surfacer";
	
	private SurfaceView mSurfaceView = null ;
	
	private SurfaceHolder mSurfaceHolder = null ;
	
	private MediaPlayer mMediaPlayer = null ;
	
	private AudioManager mAudioManager = null ;
	
	private int MaxSound;
	private int CurSound;
	
	private Uri mUri;	
	private int paused = 0 ;
	
	private AssetFileDescriptor mAssetFileDescriptor;
	private FileDescriptor mFileDescriptor;
	
	private final int CMD_CREATE = 0;
	private final int CMD_PLAY   = 1;
	private final int CMD_PAUSE  = 2;
	private final int CMD_STOP   = 3;

	 private ScheduledExecutorService executorService;
	 
	 private Button btn_prev;
	 
	 private Button btn_next;
	 
	 private int resource[] =
	 {
			 R.raw.cloudy,
			 R.raw.cloudy_night,
			 //R.raw.heavy_rain, // 这个切换的时候，会卡死，
			 R.raw.light_rain,
			 R.raw.overcast,
			 R.raw.rain,
			 R.raw.rain_showers,
			 R.raw.rainstorm,
			 //R.raw.showers, // 这个切换的时候，会卡死，
			 R.raw.snow,
			 R.raw.sunny,
			 R.raw.t_storms
	 };
	
	 private int  resource_index = 0 ;	 
	 private int  resource_count = resource.length;
	 
	private OnPreparedListener mOnPreparedListener = new OnPreparedListener()
	{	
		@Override
		public void onPrepared(final MediaPlayer mp) {
			//mp.start();			
			mMediaPlayer.start();			 // 准备好了就可以播放了。
		}
	};
	
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub			
			switch(msg.what)
			{
/*			
				case CMD_CREATE:
					onCreateVideo();
					break;
*/					
				case CMD_PLAY:
					Log.d(TAG,"huyanwei debug handleMessage(CMD_PLAY)");
					onPlayVideo();
					break;
				case CMD_PAUSE:
					Log.d(TAG,"huyanwei debug handleMessage(CMD_PAUSE)");
					onPauseVideo();
					break;
/*					
				case CMD_STOP:
					onStopVideo();
					break;
*/					
				default:
					break;
			}
			super.handleMessage(msg);
		}		
	};	
	
	class DelayRunnable implements Runnable
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Log.d(TAG,"huyanwei debug DelayRunnable .....");
			
			SendMessage(CMD_PLAY);			
		}		
	};
	
	public void SendMessage(int cmd)
	{
		Message msg = mHandler.obtainMessage();
		msg.what = cmd;
		msg.sendToTarget();
	}
	
	public void onCreateVideo()
	{
		mAudioManager=(AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		MaxSound = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	    CurSound = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

	    Log.d(TAG,"Vol:"+CurSound+"/"+MaxSound);
	    //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MaxSound-1, 0);
	}	
	
	public void onPlayVideo()
	{
		Log.d(TAG,"huyanwei debug onPlayVideo()");

		if(mMediaPlayer == null)
		{
			mMediaPlayer = new MediaPlayer();
		}

		mMediaPlayer.reset();
		
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		mMediaPlayer.setLooping(true); // loop play.
		
		if(mSurfaceHolder != null)
		{
			mMediaPlayer.setDisplay(mSurfaceHolder);
		}
		
	    mUri = Uri.parse(getApplicationContext().getResources().getAssets().toString());	
	    Log.d(TAG,"assets:"+getApplicationContext().getResources().getAssets().toString());
	    
	    try {
	    	
	    	Log.d(TAG,"huyanwei debug raw resource :"+resource_index+"/"+resource_count);
	    	
		    //mAssetFileDescriptor = this.getResources().openRawResourceFd(R.raw.sunny);
		    mAssetFileDescriptor = this.getResources().openRawResourceFd(resource[resource_index]);
		    
		    if(mAssetFileDescriptor == null)
		    {
		    	Log.d(TAG,"huyanwei debug open raw resource failed!use sdcard resource");
		    	
			    mUri= Uri.parse("/storage/sdcard0/MIUI/weather/sunny.mp4");
				mMediaPlayer.setDataSource(this, mUri);
				//mMediaPlayer.setDataSource(mUri.toString());
		    }
		    else
		    {
				mMediaPlayer.setDataSource(mAssetFileDescriptor.getFileDescriptor(),mAssetFileDescriptor.getStartOffset(),mAssetFileDescriptor.getLength());
				mAssetFileDescriptor.close();
		    }
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
		mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
	}
	
	public void onPauseVideo()
	{
		Log.d(TAG,"huyanwei debug onPauseVideo()");
		
		if(mMediaPlayer.isPlaying())
		{	
			mMediaPlayer.pause();
			paused = 1 ;
		}	
		else
		{
			if(paused == 1)
			{
				mMediaPlayer.start();
				paused = 0 ;
			}
		}
	}

	public void onStopVideo()
	{
		Log.d(TAG,"huyanwei debug onStopVideo().");
		if(mMediaPlayer.isPlaying())
		{
			mMediaPlayer.stop();		
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	
	private OnClickListener mOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.prev:  //case  btn_prev.getId():
					Log.d(TAG,"huyanwei debug prev button pressed.");
					onStopVideo();
					resource_index = (resource_index+ (resource_count-1)) % resource_count;
					SendMessage(CMD_PLAY);
					break;
				case R.id.next:  //case  btn_next.getId():
					onStopVideo();
					Log.d(TAG,"huyanwei debug next button pressed.");
					resource_index = (resource_index+ 1) % resource_count;
					SendMessage(CMD_PLAY);
					break;
				default:					
					break;
			}
		}		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);
		
		btn_prev = (Button) findViewById(R.id.prev);
		btn_next = (Button) findViewById(R.id.next);
		btn_prev.setOnClickListener(mOnClickListener);
		btn_next.setOnClickListener(mOnClickListener);
		
		//btn_prev.setAlpha(00000000);
		//btn_next.setAlpha(00000000);
		
		mSurfaceView = (SurfaceView) findViewById(R.id.video_surface);		
		
		mSurfaceHolder = mSurfaceView.getHolder();
		
		//mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);		
		//mSurfaceHolder.setFixedSize(320,240);
		
		onCreateVideo();
		
		executorService = Executors.newScheduledThreadPool(1);
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
		
		onStopVideo();
		
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		onPauseVideo();
		
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
		
		executorService.schedule(new DelayRunnable(), 40000, TimeUnit.MICROSECONDS); // 计划执行
		 
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
