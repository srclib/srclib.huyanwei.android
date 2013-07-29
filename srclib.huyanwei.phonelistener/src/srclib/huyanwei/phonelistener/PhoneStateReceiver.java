package srclib.huyanwei.phonelistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.RemoteException;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStateReceiver extends BroadcastReceiver {

	private String TAG = "srclib.huyanwei.phonelistener.PhoneStateReceiver";
	
	private TelephonyManager 	mTelephonyManager;
	
	private Context 			mContext;
	
    private static String mIncomingNumber = null;
    
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		mContext = arg0;		
    /*
		if(arg1.getAction().equals("android.intent.action.PHONE_STATE"))
		{			
			Log.d(TAG,"PhoneStateReceiver.onReceive()");			

			// TelephonyManager
			mTelephonyManager= (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
					
			 switch (mTelephonyManager.getCallState()) 
			 {
	            case TelephonyManager.CALL_STATE_RINGING:
	                mIncomingNumber = arg1.getStringExtra("incoming_number");
	                Log.i(TAG, "RINGING :" + mIncomingNumber);
	                break;
	            case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(TAG, "incoming ACCEPT :" + mIncomingNumber);
	                break;
	            case TelephonyManager.CALL_STATE_IDLE:
                     Log.i(TAG, "incoming IDLE");
	                break;
	         }
		}
		else if (arg1.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) // 如果是拨打电话 
		{
	            String phoneNumber = arg1.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
	            Log.i(TAG, "call OUT:" + phoneNumber);
	    }
	    */
	}
}
