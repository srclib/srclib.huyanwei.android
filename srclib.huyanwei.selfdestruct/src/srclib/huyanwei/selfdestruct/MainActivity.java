package srclib.huyanwei.selfdestruct;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.os.Power;
import android.os.PowerManager;
import android.content.Context;

import android.os.SystemProperties;

public class MainActivity extends Activity {
	
	private final String TAG = "srclib.huyanwei.selfdestruct";
	
	private String config_file = "/sys/self_destruct/config";
	
	private int config = 0 ;
	
	private Button btn1 ;
	private Button btn2 ;
	
	private int MSG_WRITE = 100;
	private int MSG_READ  = 101;
	
	private int MSG_DIR_IN  = 0;
	private int MSG_DIR_OUT = 1;
	
	private final boolean background_music = false;
	
    private MediaPlayer mPlayer;

	private PowerManager pm ;

	private final boolean shutdown_device = true;

	private final boolean low_level_shutdown = true;
		
	private View.OnClickListener mOnClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{
				case R.id.button1:
					play_warning_audio(); // play warning audio.
					notify_self_destructor(0x55);
					break;
				case R.id.button2:
					stop_warning_audio(); // stop warning audio.
					notify_self_destructor(0x00);
					break;
				default:
					break;
			}
		}
	};
	
    private String readFile(File fn) {
        FileReader f;
        int len;
        f = null;
        try {
            f = new FileReader(fn);
            String s = "";
            char[] cbuf = new char[200];
            while ((len = f.read(cbuf, 0, cbuf.length)) >= 0) {
                s += String.valueOf(cbuf, 0, len);
            }
            return s;
        } catch (IOException ex) {
            return "0";
        } finally {
            if (f != null) {
                try {
                    f.close();
                } catch (IOException ex) {
                    return "0";
                }
            }
        }
    }
    
	public void  writeFile(int index)
	{
		String data_string = "";
		File file = new File(config_file);
		if (file.exists())
		{
			FileOutputStream outStream = null;
			try {
				outStream = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				data_string += String.valueOf(index);
				Log.d(TAG,"data_string="+data_string);
				outStream.write(data_string.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				outStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

    Handler mHandler = new Handler(){
		public void handleMessage(Message msg)
		{    			
			int data = 0 ;
			if((msg.what == MSG_WRITE) && (msg.arg1 == MSG_DIR_OUT))
			{
				data = (int) msg.arg2 ;
				HandlerSelfDestructThread mHandlerSelfDestructThread = new HandlerSelfDestructThread(data);
				mHandlerSelfDestructThread.start();
			}
			else if((msg.what == MSG_READ) && (msg.arg1 == MSG_DIR_IN))
			{
				data = (int) msg.arg2 ;
				comlete_self_destruct();
			}
	    }
	};
	
	public void  init_audio_system()
	{
		if(background_music)
		{
	        AudioManager audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
	        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
	                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
	                AudioManager.FLAG_PLAY_SOUND);
	        
	        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.warning);
	        mPlayer.setLooping(true);		
		}
	}
	
	public void play_warning_audio()
	{
		if(background_music)
		{
			mPlayer.start();
		}
	}
	
	public void stop_warning_audio()
	{
		if(background_music)
        {
			mPlayer.stop();
        }
	}


		
    class HandlerSelfDestructThread extends Thread{   	
    	int data ;
    	public HandlerSelfDestructThread(int index)
    	{
    		data = index ;
		}
    	
		public void run()
		{			
			writeFile(data);
			notify_caller(0);
		}
	}
    
    public void notify_self_destructor(int index)
    {
		Message msg = mHandler.obtainMessage();
		msg.what = MSG_WRITE;
		msg.arg1 = MSG_DIR_OUT;
		msg.arg2 = index;
		msg.sendToTarget();
    }    
	
    public void notify_caller(int index)
    {
		Message msg = mHandler.obtainMessage();
		msg.what = MSG_READ;
		msg.arg1 = MSG_DIR_IN;
		msg.arg2 = index;
		msg.sendToTarget();
    }
    
    private void update_view_state()
    {
		String config_str = readFile(new File(config_file));		
		config_str.replace('\n', ' ');
		//config.replace('\r', ' ');
		Log.d(TAG,"config="+config_str);		
        config = Integer.parseInt(config_str.trim());
        
        if(config == 0x55)
        {
            btn1.setEnabled(false);
            btn1.setVisibility(View.GONE);
            
            btn2.setEnabled(true);            
            btn2.setVisibility(View.VISIBLE);
        }
        else
        {
            btn1.setEnabled(true);
            btn1.setVisibility(View.VISIBLE);
            
            btn2.setEnabled(false);            
            btn2.setVisibility(View.GONE);        	
        }
    }
    
    public void comlete_self_destruct()
    {
    	update_view_state();    	
    	Toast.makeText(this, R.string.handle_message_finished, Toast.LENGTH_LONG).show(); 
		if(shutdown_device)
		{
			reboot_device();
		}
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btn1 = (Button) findViewById(R.id.button1);
		btn1.setOnClickListener(mOnClickListener);		
		
		btn2= (Button) findViewById(R.id.button2);
		btn2.setOnClickListener(mOnClickListener);		
		
		init_audio_system();

		pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
	}

	public void reboot_device()
	{
		if(low_level_shutdown)
		{
			pm.setBacklightBrightness(0);

			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Power.shutdown(); // low-level
			SystemProperties.set("ctl.start", "shutdown"); // notify init service
		}
		else
		{
			// clean shutdown
			if(pm != null)
			{
				pm.setBacklightBrightness(0);
				pm.reboot("self-destruct");
				//pm.reboot("shutdown");
				//pm.reboot("recovery");
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
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
		
		update_view_state();
		
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
		stop_warning_audio();		
		super.onStop();
	}

}
