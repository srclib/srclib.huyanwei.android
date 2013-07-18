package srclib.huyanwei.surfacer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.TimedText;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity{
	
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
	private final int CMD_UPDATE = 4;

	 private ScheduledExecutorService executorService;
	 
	 private final int SCHEDULE = 0; // executorService to schedule execute
	 private final int EVENT 	= 1; // event mechanism,this method is better than SCHEDULE.
	 private int mPlayVideoMethod = EVENT;
	 
	 private int ThreadProcessMethod = 1;
	 	 
	 private Object mThreadLock = new Object();
	 private Object mHandlerLock = new Object();
	 	 
	 private Button btn_prev;	 
	 private Button btn_next;
	 private TextView tv_buffer;
	 private SeekBar mSeekBar ;
	 
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
	 
	 private boolean network_url = false ;
	 
	 private URLConnection cn;
	 
	 private int MaxDuration = 0 ;
	 private int CurDuration = 0 ;
	 
	 private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener()
	 {
		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			Log.d(TAG,"huyanwei debug Video play Completion.");
		}
	 };
	 
	 private MediaPlayer.OnTimedTextListener  mOnTimedTextListener = new MediaPlayer.OnTimedTextListener()
	 {
		@Override
		public void onTimedText(MediaPlayer mp, TimedText text) {
			// TODO Auto-generated method stub
		}
	 };
	 
	 private MediaPlayer.OnInfoListener	 mOnInfoListener = new MediaPlayer.OnInfoListener()
	 {
		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			return false;
		}
	 };
	 
	 @SuppressWarnings("unused")
	 private OnBufferingUpdateListener mOnBufferingUpdateListener = new OnBufferingUpdateListener()
	 {
			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				// TODO Auto-generated method stub		
				Log.d(TAG,"huyanwei debug onBufferingUpdate("+percent+")");		
				tv_buffer.setText(Integer.toString(percent)+"/100");
				tv_buffer.setTextColor(0x80ff0000);
				tv_buffer.setBackgroundColor(0x00000000);
				tv_buffer.setTextSize((float)24);
				
				mSeekBar.setSecondaryProgress(mSeekBar.getMax() * percent / 100); // second progress
				
			}
	 };
	 
	 @SuppressWarnings("unused")
	 private SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback()
	 {
		 @Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				// TODO Auto-generated method stub
				Log.d(TAG,"huyanwei debug surfaceChanged().");				
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				Log.d(TAG,"huyanwei debug surfaceCreated().");
				
				//SendMessage(CMD_PLAY); 
				onPlayVideo(); // after surfaceCreated, prepare player.
				
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				
				Log.d(TAG,"huyanwei debug surfaceDestroyed().");
				
			}
	 };
	 
	private OnPreparedListener mOnPreparedListener = new OnPreparedListener()
	{	
		@Override
		public void onPrepared(final MediaPlayer mp) {
			//mp.start();
			
			//mMediaPlayer.start();			 // 准备好了就可以播放了。
	        
			MaxDuration = mMediaPlayer.getDuration();
			CurDuration = mMediaPlayer.getCurrentPosition();
			
			mSeekBar.setProgress(mSeekBar.getMax() * CurDuration / MaxDuration ); // percent 
			
	        int videoWidth  = mMediaPlayer.getVideoWidth();  
	        int videoHeight = mMediaPlayer.getVideoHeight();
	        if((videoWidth != 0 ) && (videoHeight != 0))
			{
	        	Log.d(TAG,"huyanwei debug mOnPreparedListener.onPrepared ().");	        	
	        	mMediaPlayer.start();			 // 准备好了就可以播放了。
			}
	        else
	        {
	        	Log.d(TAG,"huyanwei debug mMediaPlayer Video Format unsupported!");
	        }
	        
	        SendMessage(CMD_UPDATE); // 更新相关数据
	        
		}
	};
	
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub		
			synchronized(mHandlerLock)
			{
				switch(msg.what)
				{
					case CMD_CREATE:
						if(ThreadProcessMethod == 1)
						{
							MediaPlayerExecuteThread mMediaPlayerExecuteThread=new MediaPlayerExecuteThread(CMD_CREATE);
							mMediaPlayerExecuteThread.start();
							try {
								mMediaPlayerExecuteThread.join();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{
							Log.d(TAG,"huyanwei debug handleMessage(CMD_CREATE)");
							onPlayVideo();
						}
						break;
					case CMD_PLAY:
						if(ThreadProcessMethod == 1)
						{
							MediaPlayerExecuteThread mMediaPlayerExecuteThread=new MediaPlayerExecuteThread(CMD_PLAY);
							mMediaPlayerExecuteThread.start();
							try {
								mMediaPlayerExecuteThread.join();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{			
							Log.d(TAG,"huyanwei debug handleMessage(CMD_PLAY)");
							onPlayVideo();
						}
						break;
					case CMD_PAUSE:
						if(ThreadProcessMethod == 1)
						{
							MediaPlayerExecuteThread mMediaPlayerExecuteThread=new MediaPlayerExecuteThread(CMD_PAUSE);
							mMediaPlayerExecuteThread.start();
							try {
								mMediaPlayerExecuteThread.join();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{			
							Log.d(TAG,"huyanwei debug handleMessage(CMD_PAUSE)");
							onPauseVideo();						
						}					
						break;
					case CMD_STOP:
						if(ThreadProcessMethod == 1)
						{	
							MediaPlayerExecuteThread mMediaPlayerExecuteThread=new MediaPlayerExecuteThread(CMD_STOP);
							mMediaPlayerExecuteThread.start();
							try {
								mMediaPlayerExecuteThread.join();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{			
							Log.d(TAG,"huyanwei debug handleMessage(CMD_STOP)");
							onStopVideo();						
						}	
						break;			
					case CMD_UPDATE:
						if(ThreadProcessMethod == 1)
						{
							MediaPlayerExecuteThread mMediaPlayerExecuteThread=new MediaPlayerExecuteThread(CMD_UPDATE);
							mMediaPlayerExecuteThread.start();
							try {
								mMediaPlayerExecuteThread.join();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}						
						}	
						else
						{
							Log.d(TAG,"huyanwei debug handleMessage(CMD_UPDATE)");
							onUpdateVideo();
						}
						SendMessage(CMD_UPDATE);
						break;
					default:
						break;
				}
			}
			super.handleMessage(msg);
		}
	};
	
	
	public void SendMessage(int cmd)
	{
		Message msg = mHandler.obtainMessage();
		msg.what = cmd;
		msg.sendToTarget();
	}
	
	class DelayRunnable implements Runnable
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Log.d(TAG,"huyanwei debug DelayRunnable .....");
			
			SendMessage(CMD_PLAY);			
		}		
	};
	
	class MediaPlayerExecuteThread extends Thread
	{
		int ReceiveCommand = 0;
		
		MediaPlayerExecuteThread(int cmd)
		{
			ReceiveCommand = cmd ;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized(mThreadLock)
			{
				switch(ReceiveCommand)
				{
					case CMD_CREATE:
						Log.d(TAG,"huyanwei debug MediaPlayerExecuteThread(CMD_CREATE)");
						onPlayVideo();
					break;
					case CMD_PLAY:
						Log.d(TAG,"huyanwei debug MediaPlayerExecuteThread(CMD_PLAY)");
						onPlayVideo();
						break;
					case CMD_PAUSE:
						Log.d(TAG,"huyanwei debug MediaPlayerExecuteThread(CMD_PAUSE)");
						onPauseVideo();
						break;
					case CMD_STOP:
						Log.d(TAG,"huyanwei debug MediaPlayerExecuteThread(CMD_STOP)");
						onStopVideo();
						break;
					case CMD_UPDATE:
						Log.d(TAG,"huyanwei debug MediaPlayerExecuteThread(CMD_UPDATE)");
						onUpdateVideo();
						break;
					default:
						break;
				}			
			}
			super.run();
		}				
	};
	
	public void onCreateVideo()
	{
		if(mMediaPlayer == null)
		{
			mMediaPlayer = new MediaPlayer();
		}		
	}
	
	public void onPlayVideo()
	{
		Log.d(TAG,"huyanwei debug onPlayVideo()");

		onCreateVideo();
		
		mMediaPlayer.reset();
		
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		/* 下面设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到用户面前 */
		mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);  // Buffering
		
		mMediaPlayer.setLooping(true); // loop play.
		
		mMediaPlayer.setOnPreparedListener(mOnPreparedListener); // 准备好了的回调函数。
		
		mMediaPlayer.setOnCompletionListener(mOnCompletionListener); // 视屏播放完成。

		mMediaPlayer.setOnInfoListener(mOnInfoListener);
		
		
		
		if(mSurfaceHolder != null)
		{
			mMediaPlayer.setDisplay(mSurfaceHolder);
		}
		
	    //mUri = Uri.parse(getApplicationContext().getResources().getAssets().toString());	
	    //Log.d(TAG,"huyanwei debug assets path:"+getApplicationContext().getResources().getAssets().toString());
	    
	    try {
	    	
	    	if(network_url)
	    	{
	    		Log.d(TAG,"huyanwei debug open network resource!");
				mUri= Uri.parse("http://daily3gp.com/vids/Baile%20Sexy.3gp");
				//mUri= Uri.parse("http://www.dubblogs.cc:8751/Android/Test/Media/3gp/test.3gp");
				mMediaPlayer.setDataSource(this, mUri);
	    	}
	    	else
	    	{
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
	}
	
	public void onUpdateVideo()
	{
		if((mMediaPlayer != null) && (mMediaPlayer.isLooping()))
		{
			MaxDuration = mMediaPlayer.getDuration();
			CurDuration = mMediaPlayer.getCurrentPosition();
			mSeekBar.setProgress(mSeekBar.getMax() * CurDuration / MaxDuration ); // percent
		}
	}
	
	public void onPauseVideo()
	{
		Log.d(TAG,"huyanwei debug onPauseVideo()");
		
		if( (mMediaPlayer != null ) && (mMediaPlayer.isPlaying()))
		{	
			mMediaPlayer.pause();
			paused = 1 ;
		}	
		else if( (mMediaPlayer != null ) && (!mMediaPlayer.isPlaying()))
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
		if( (mMediaPlayer != null ) && (mMediaPlayer.isPlaying()))
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
					SendMessage(CMD_STOP);//onStopVideo();
					resource_index = (resource_index+ (resource_count-1)) % resource_count;
					SendMessage(CMD_PLAY);
					break;
				case R.id.next:  //case  btn_next.getId():
					SendMessage(CMD_STOP);//onStopVideo();
					Log.d(TAG,"huyanwei debug next button pressed.");
					resource_index = (resource_index+ 1) % resource_count;
					SendMessage(CMD_PLAY);
					break;
				default:					
					break;
			}
		}	
	};
	
	private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener()
	{
		int current;
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			current = progress ;
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			if(mMediaPlayer!= null)
			{
				Log.d(TAG,"huyanweei debug onStopTrackingTouch("+current+")");
				int msec = (int)((1.0 * current * mMediaPlayer.getDuration()) / mSeekBar.getMax()) ;
				mMediaPlayer.seekTo(msec);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		onCreateVideo();
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);   //全屏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 应用运行时，保持屏幕高亮，不锁屏
		
		setContentView(R.layout.activity_main);
		
		tv_buffer = (TextView) findViewById(R.id.bufferpercent);		
		mSeekBar  = (SeekBar) findViewById(R.id.seekBar);
		
		mSeekBar.setMax(100);
		mSeekBar.setProgress(0);
		mSeekBar.setSecondaryProgress(0);
		mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);		
		
		btn_prev = (Button) findViewById(R.id.prev);
		btn_next = (Button) findViewById(R.id.next);
		btn_prev.setOnClickListener(mOnClickListener);
		btn_next.setOnClickListener(mOnClickListener);
		
		//btn_prev.setAlpha(00000000);
		//btn_next.setAlpha(00000000);
		
		mSurfaceView = (SurfaceView) findViewById(R.id.video_surface);
		
		mSurfaceView.setBackgroundColor(0x00000000);
		
		mSurfaceHolder = mSurfaceView.getHolder();
		
		if(mPlayVideoMethod == EVENT)
		{
			mSurfaceHolder.addCallback(mSurfaceHolderCallback);
		}
		
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);	 // 不缓冲	
		//mSurfaceHolder.setFixedSize(320,240);
		
		mAudioManager=(AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		MaxSound = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	    CurSound = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	    Log.d(TAG,"Vol:"+CurSound+"/"+MaxSound);
	    //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MaxSound-1, 0);
		
	    if(mPlayVideoMethod == SCHEDULE)
		{
	    	executorService = Executors.newScheduledThreadPool(1);
		}
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
		
		//onStopVideo();
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
		
		if(network_url)
		{
			tv_buffer.setVisibility(View.VISIBLE);
		}
		else
		{
			tv_buffer.setVisibility(View.INVISIBLE);
		}
		
		if(mPlayVideoMethod == SCHEDULE)
		{
			executorService.schedule(new DelayRunnable(), 40000, TimeUnit.MICROSECONDS); // 计划执行
		}
		 
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
		onStopVideo();
		super.onStop();
	}
}
