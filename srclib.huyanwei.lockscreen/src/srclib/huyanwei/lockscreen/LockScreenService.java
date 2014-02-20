package srclib.huyanwei.lockscreen;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class LockScreenService extends Service 
{
	private KeyguardManager.KeyguardLock mKeyguardLock;
	private KeyguardManager mKeyguardManager;
	
	private TelephonyManager mTelephonyManager;
	
	private String TAG = "srclib.huyanwei.lockscreen.LockScreenService";
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub		
		super.onCreate();
		
		this.mKeyguardManager = ((KeyguardManager)getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE));
		mKeyguardLock = mKeyguardManager.newKeyguardLock("lock");
		
	    mTelephonyManager = (TelephonyManager) this.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
	    
	    IntentFilter localIntentFilter1 = new IntentFilter();
	    localIntentFilter1.addAction("android.intent.action.SCREEN_OFF");
	    registerReceiver(this.mServiceReceiver, localIntentFilter1);
	    
	    
	    IntentFilter localIntentFilter2 = new IntentFilter();	    
	    localIntentFilter2.addAction("android.intent.action.SCREEN_ON");
	    registerReceiver(this.mServiceReceiver, localIntentFilter2);
	    	    
	    //this.startForeground(true, null);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub		
		//stopForeground(true);		
		
		unregisterReceiver(this.mServiceReceiver);
		
		Intent localIntent = new Intent();
	    localIntent.setClass(this, LockScreenService.class);
	    startService(localIntent);		
	    
		super.onDestroy();		
	}

	@Override
	public void onStart(Intent arg0, int arg1) {
		// TODO Auto-generated method stub
		super.onStart(arg0, arg1);
	}

	@Override
	public int onStartCommand(Intent arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onStartCommand");
		String ActionStr = arg0.getAction() ;
		if ("enable_system_keyguard".equals(ActionStr))		
		{
			if (isSimReady())
	        {
	          this.mKeyguardLock.reenableKeyguard();
	          Log.v(TAG, "onStartCommand() enable_system_keyguard");
	        }
		}
		else if ("disable_system_keyguard".equals(ActionStr))
		{
			if (isSimReady())
		    {
		        this.mKeyguardLock.disableKeyguard();
		        Log.v(TAG, "onStartCommand() disable_system_keyguard");
		    }			
		}
		return super.onStartCommand(arg0, arg1, arg2);
	}

	@Override
	public boolean onUnbind(Intent arg0) {
		// TODO Auto-generated method stub
		return super.onUnbind(arg0);
	}	  
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private boolean isSimReady()
	{
		return true;
/*		
	    switch (mTelephonyManager.getSimState())
	    {
	       	case TelephonyManager.SIM_STATE_UNKNOWN:
	    	case TelephonyManager.SIM_STATE_ABSENT:
	    		 return false; 		
	    	case TelephonyManager.SIM_STATE_PIN_REQUIRED:
	    	case TelephonyManager.SIM_STATE_PUK_REQUIRED:
	    	case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
	    	case TelephonyManager.SIM_STATE_READY:    		
	    		return true;
	    	default :
	    		return true;
	    }
*/	    
	  }	

	private void startLockActivity()
	{
		Log.v(TAG, "startLockActivity()");
		
	    Intent localIntent = new Intent();
	    localIntent.setClassName("srclib.huyanwei.lockscreen", "srclib.huyanwei.lockscreen.MainActivity");
	    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(localIntent);
	}
	
	private BroadcastReceiver mServiceReceiver = new BroadcastReceiver()
	{
	    public void onReceive(Context paramContext, Intent paramIntent)
	    {
	      String str = paramIntent.getAction();
	      if (str.equals("android.intent.action.SCREEN_OFF"))
	      {
	    	  	Log.v(TAG, "BroadcastReceiver SCREEN_OFF");
	    	  	
	    	    mKeyguardLock.disableKeyguard();
	    	    
	    	  	startLockActivity();
	        	return;
	      }
	      else if (str.equals("android.intent.action.SCREEN_ON"))
	      {
	    	  
	    	  mKeyguardLock.disableKeyguard();
	    	  
	    	  //startLockActivity();
	      }	      
	      else if (str.equals("com.coco.action.DISABLE_SYSLOCK"))
	      {
	    	  Log.v(TAG, "BroadcastReceiver DISABLE_SYSLOCK");
	    	  return ;
	      }
	    }
	};
}
