package srclib.huyanwei.phonelistener;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.text.format.Time;
import android.util.Log;

public class AudioRecordThread extends Thread {

	private final String TAG ="srclib.huyanwei.phonelistener.AudioRecordThread";
	
	private MediaRecorder mMediaRecorder;
	private File          mFile;
	private Vibrator 	  mVibrator;
	
	private Context       mContext;
	private String        mIncomingCallNumber ;
	
	private boolean       mIsRecording   = false;
	
	public final static int  MSG_RECORD_AUDIO_START = 100 ;
	public final static int  MSG_RECORD_AUDIO_STOP  = 101 ;
	
	private  Handler       mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			String ObjectName = "";
			switch(msg.what)
			{
				case MSG_RECORD_AUDIO_START:
					start_record_audio(mIncomingCallNumber);
					break;
				case MSG_RECORD_AUDIO_STOP:
					stop_record_audio();
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	public Handler getHandler()
	{
		return mHandler;
	}
	
	public boolean IsRecording()
	{
		return mIsRecording ;
	}
	
	AudioRecordThread(Context c, String incomingcallnum)
	{
		mIncomingCallNumber = incomingcallnum;
		mContext = c;
		
		if(mContext != null)
		{
			mVibrator = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
		}
	}
	
	public void start_record_audio(String incomingNumber)
	{
		 mIsRecording = true;
		 
		 File path = Environment.getExternalStorageDirectory();
		 File audio_record_dir = new File(path.getAbsolutePath()+"/srclib/call_record/");
		 Log.d(TAG,"audio_record_dir="+audio_record_dir.toString());
		 if(!audio_record_dir.exists())
		 {
			 audio_record_dir.mkdirs();
		 }
		 
		 Time time =new Time(Time.getCurrentTimezone());
		 time .setToNow();
		 String time_str = String.format(Locale.ENGLISH, "@%04d%02d%02d%02d%02d%02d",time.year,(time.month+1),time.monthDay,
					time.hour,time.minute,time.second); 
		 //String time_str = "@"+System.currentTimeMillis() ;	 
		 
		 mFile = new File(audio_record_dir, incomingNumber + time_str + ".amr");
		 mMediaRecorder = new MediaRecorder();
		 mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		 mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		 mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		 mMediaRecorder.setOutputFile(mFile.getAbsolutePath());
		 try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 mMediaRecorder.start();
		 
		//震动一下
		 mVibrator.vibrate(100);
	}
	
	public void stop_record_audio()
	{
        if (mMediaRecorder != null)
        {
        	mMediaRecorder.stop();
        	mMediaRecorder.release();
        	mMediaRecorder = null;
        }
        
		mIsRecording = false;
		
		//震动一下
		 mVibrator.vibrate(100);
		 
		 try {
			this.stop();
			this.finalize(); // 推出线程			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	       // MessageQueueLooper
		   Looper.prepare();
           Looper.loop();		 
           //super.run();
	}
}
