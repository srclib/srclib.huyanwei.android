package srclib.huyanwei.downloader;

import java.io.File;
import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {

	private final String TAG = "srclib.huyanwei.downloader";
	
	private Button btn_start,btn_pause_resume,btn_stop;
	
	private ProgressBar mProgressBar;
	
	private int started = 0 ;
	private int paused   = 0 ;
	
	private final String URL = "http://www.go-gddq.com/down/2011-06/11061423314934.pdf";
	//"http://www.baidu.com/img/bdlogo.gif";  
	
	private final String local_patch = "/storage/sdcard0";
	
	private void doDownloadFile(String url ,String filename)
	{
		String filepath = local_patch + "/" + filename;
		File file = new File(filepath);
		long size = 0;
		if (file.exists()) {
			size = file.length();
		}
		
		Log.d(TAG,"huyanwei debug:"+url+"->"+local_patch+"/"+filename);
		
		int res  = 0 ;		
		res = Downloader.doDownloadTheFile(url,local_patch,filename, size, mHandler);	
		if(res == 1)
		{
			Message msg = mHandler.obtainMessage(Action.ACTION_IN, Action.ACTION_FINISH, 0);
			msg.sendToTarget();
		}
		else
		{
			Message msg = mHandler.obtainMessage(Action.ACTION_IN, Action.ACTION_EXCEPT, 0);
			msg.sendToTarget();			
		}
	}
	
	public void thread_download_worder()
	{
		Thread mThread = new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				doDownloadFile(URL,"huyanwei-"+UUID.randomUUID().toString()+".raw");
			}			
		});
		mThread.start();		
	}
	
	private OnClickListener  button_click_lister = new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.button_start:
					started = 1;
					NotifyToDo(Action.ACTION_START);
					updateViewState();
					break;
				case R.id.button_pause_resume:
					if(started == 1)
					{
						if(paused == 0)							
						{
							paused = 1 ;
							btn_pause_resume.setText(R.string.resume);
							NotifyToDo(Action.ACTION_PAUSE);									
						}
						else
						{	
							paused = 0 ;
							btn_pause_resume.setText(R.string.pause);
							NotifyToDo(Action.ACTION_RESUME);							
						}
					}
					updateViewState();
					break;
				case R.id.button_stop:
					started = 0 ;
					paused = 0 ;
					NotifyToDo(Action.ACTION_STOP);
					updateViewState();
					break;
				default :
					break;					
			}
		}		
	};
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(Action.ACTION_OUT == msg.what)
			{	
				switch(msg.arg1)
				{
					case Action.ACTION_START:			
						Log.d(TAG,"huyanwei debug ACTION_START");						
						thread_download_worder();
						break;
					case Action.ACTION_PAUSE:
						Log.d(TAG,"huyanwei debug ACTION_PAUSE");
						Downloader.doCancelDownloadTheFile(1,0);
						break;
					case Action.ACTION_RESUME:						
						Log.d(TAG,"huyanwei debug ACTION_RESUME");
						Downloader.doCancelDownloadTheFile(0,0);
						break;
					case Action.ACTION_STOP:
						Log.d(TAG,"huyanwei debug ACTION_STOP");
						Downloader.doCancelDownloadTheFile(0,1);
						break;
					default:
						break;
				}
			}
			else if(Action.ACTION_IN == msg.what)
			{
				switch(msg.arg1)
				{
					case Action.ACTION_FINISH:
						Log.d(TAG,"huyanwei debug ACTION_FINISH");
						started = 0 ;
						paused = 0 ;
						updateViewState();	
						Toast.makeText(getApplicationContext(), "download finish!", Toast.LENGTH_LONG).show();
						break;
					case Action.ACTION_UPDATE:
						//Log.d(TAG,"huyanwei debug ACTION_UPDATE");
						String percent = (String)msg.obj;
						int percent_100= (int)(Double.parseDouble(percent)* 100);
						updateProgressBar(percent_100);
						Toast.makeText(getApplicationContext(), percent, Toast.LENGTH_SHORT);
						break;
					case Action.ACTION_EXCEPT:
						Log.d(TAG,"huyanwei debug ACTION_EXCEPT");
						started = 0 ;
						paused = 0 ;
						updateViewState();
						Toast.makeText(getApplicationContext(), "Error!!!", Toast.LENGTH_SHORT);
						
					case Action.ACTION_ABORT:
						Log.d(TAG,"huyanwei debug ACTION_EXCEPT");
						started = 0 ;
						paused = 0 ;
						updateViewState();
						Toast.makeText(getApplicationContext(), "User Cancel", Toast.LENGTH_SHORT);
					default:
						break;
				}
			}
			super.handleMessage(msg);
		}		
	};
	
	private void NotifyToDo(int var)
	{
		Log.d(TAG,"huyanwei debug NotifyToDo("+var+")");
		Message msg = mHandler.obtainMessage();
		msg.what = Action.ACTION_OUT;
		msg.arg1 = var;
		msg.sendToTarget();
	}
	
	private void updateViewState()
	{
		if(started == 0)
		{
			btn_start.setEnabled(true);
			btn_pause_resume.setEnabled(false);
			btn_stop.setEnabled(false);
		}
		else
		{
			btn_start.setEnabled(false);
			btn_pause_resume.setEnabled(true);
			btn_stop.setEnabled(true);
		}
	}
	
	private void updateProgressBar(int percent)
	{
		if((percent > 100) || (percent < 0))
			return ;		
		mProgressBar.setProgress(percent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btn_start = (Button) findViewById(R.id.button_start);
		btn_pause_resume = (Button) findViewById(R.id.button_pause_resume);
		btn_stop = (Button) findViewById(R.id.button_stop);
		
		btn_start.setOnClickListener(button_click_lister);
		btn_pause_resume.setOnClickListener(button_click_lister);
		btn_stop.setOnClickListener(button_click_lister);		
		btn_pause_resume.setText(R.string.pause);
		
		updateViewState();
		
		mProgressBar = (ProgressBar)findViewById(R.id.download_progressbar);		
		mProgressBar.setMax(100);
		mProgressBar.setProgress(0);
		
		
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
