package srclib.huyanwei.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LockScreenReceiver extends BroadcastReceiver {

	private String TAG = "srclib.huyanwei.lockscreen.LockScreenReceiver" ;
	
	@Override
	public void onReceive(Context arg0, Intent arg1) 
	{
		// TODO Auto-generated method stub
		
	    String str1 = arg1.getAction();
	    if (str1.equals("android.intent.action.USER_PRESENT"))
	    {
	      Intent localIntent1 = new Intent(arg0, LockScreenService.class);
	      localIntent1.setAction("com.coco.lock.action.KILL_SYSLOCK");
	      arg0.startService(localIntent1);	      
	    }
	    else if (str1.equals("android.intent.action.BOOT_COMPLETED"))
        {
	      arg0.startService(new Intent(arg0, LockScreenService.class));          
        }	    
	    else if (str1.equals("android.intent.action.PACKAGE_ADDED"))
	    {
	    	String str3 = arg1.getData().getSchemeSpecificPart();
	        Log.v(TAG, "onReceive  ACTION_PACKAGE_ADDED:" + str3);
	        
	        Intent localIntent2 = new Intent(arg0, LockScreenService.class);
	        localIntent2.setAction("enable_system_keyguard");
	        arg0.startService(localIntent2);
	    }
	    else if (str1.equals("android.intent.action.PACKAGE_REMOVED"))
	    {
	    	String str3 = arg1.getData().getSchemeSpecificPart();
	        Log.v(TAG, "onReceive  ACTION_PACKAGE_REMOVED:" + str3);
	        
	        Intent localIntent3 = new Intent(arg0, LockScreenService.class);
	        localIntent3.setAction("disable_system_keyguard");
	        arg0.startService(localIntent3);
	    }
	}
}
