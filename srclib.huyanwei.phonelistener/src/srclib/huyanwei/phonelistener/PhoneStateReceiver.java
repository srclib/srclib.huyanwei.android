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
	private AudioManager 		mAudioManager ;
	private SensorManager 		mSensorManager;
	
	private Sensor 				mProximitySensor;
	private Sensor 				mLightSensor;	
	
	private Context 			mContext;

	private boolean 			mProximitySensorListening   =  false ;
	
	private boolean 			mEventhappen = false;
	
	private SensorEventListener mProximitySensorEventListener = new SensorEventListener()
	{

		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onSensorChanged(SensorEvent arg0) {
			// TODO Auto-generated method stub
            float distance = arg0.values[0];
            Log.d(TAG, "onSensorChanged: proximity Sensor value: " + arg0.values[0]);
            
            if(distance > 1.0f)
            {
            	mEventhappen = true ;     
            	
            	try {
					PhoneUtils.getITelephony(mTelephonyManager).silenceRinger();
					PhoneUtils.getITelephony(mTelephonyManager).answerRingingCall();// 自动接听
					//PhoneUtils.getITelephony(mTelephonyManager).endCall();// 挂断
					//PhoneUtils.getITelephony(mTelephonyManager).cancelMissedCallsNotification(); //取消未接显示
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// 静铃
            }
		}
	};
	
	private SensorEventListener mLightSensorEventListener = new SensorEventListener()
	{

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}

		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onSensorChanged: Light Sensor value: " + event.values[0]);
		}
	};
	
	private PhoneStateListener mPhoneStateListener = new PhoneStateListener()
	{

		@Override
		public void onCallForwardingIndicatorChanged(boolean cfi) {
			// TODO Auto-generated method stub
			super.onCallForwardingIndicatorChanged(cfi);
		}

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			
			Log.d(TAG,"onCallStateChanged ");
			
			switch(state)
			{
				case  TelephonyManager.CALL_STATE_IDLE:    // 空闲状态.
					Log.d(TAG,"onCallStateChanged(CALL_STATE_IDLE) ");
					if(mProximitySensorListening)
					{	
						uninstall_sensor_listener();
						mProximitySensorListening = false;
					}
					break; 
				case  TelephonyManager.CALL_STATE_RINGING: // 来电铃声状态.
					Log.d(TAG,"onCallStateChanged(CALL_STATE_RINGING) ");
					install_sensor_listener(); // 让距离感应器 监听
					mEventhappen = false ;      // 初始化 事件还没有发生。
					mProximitySensorListening = true;  // 只在来电时候生效。
					break;
				case  TelephonyManager.CALL_STATE_OFFHOOK: // 挂机状态(拿起了话筒/接起电话)
					Log.d(TAG,"onCallStateChanged(CALL_STATE_OFFHOOK) ");
					if(mProximitySensorListening)
					{	
						uninstall_sensor_listener();
						mProximitySensorListening = false;
					}
					break;
				default:
					break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}

		@Override
		public void onCellLocationChanged(CellLocation location) {
			// TODO Auto-generated method stub
			super.onCellLocationChanged(location);
		}

		@Override
		public void onDataActivity(int direction) {
			// TODO Auto-generated method stub
			super.onDataActivity(direction);
		}

		@Override
		public void onDataConnectionStateChanged(int state, int networkType) {
			// TODO Auto-generated method stub
			super.onDataConnectionStateChanged(state, networkType);
		}

		@Override
		public void onDataConnectionStateChanged(int state) {
			// TODO Auto-generated method stub
			super.onDataConnectionStateChanged(state);
		}

		@Override
		public void onMessageWaitingIndicatorChanged(boolean mwi) {
			// TODO Auto-generated method stub
			super.onMessageWaitingIndicatorChanged(mwi);
		}

		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
			// TODO Auto-generated method stub
			super.onServiceStateChanged(serviceState);
		}

		@Override
		public void onSignalStrengthChanged(int asu) {
			// TODO Auto-generated method stub
			super.onSignalStrengthChanged(asu);
		}

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			// TODO Auto-generated method stub
			super.onSignalStrengthsChanged(signalStrength);
		}		
	};
	
	public void install_sensor_listener()
	{
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mLightSensor     = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
	
        // start lister sensor
        mSensorManager.registerListener(mLightSensorEventListener, mLightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mProximitySensorEventListener, mProximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void uninstall_sensor_listener()
	{
		mSensorManager.unregisterListener(mLightSensorEventListener, mLightSensor);		
		mSensorManager.unregisterListener(mProximitySensorEventListener, mProximitySensor);	
	}
	
	public void install_telephony_listener()
	{
		mTelephonyManager= (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyManager.listen(mPhoneStateListener, 
				PhoneStateListener.LISTEN_CALL_STATE | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}
	
	public void uninstall_telephony_listener()
	{
		mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
	}
	
	public void install_audio_listener()
	{
		mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
		// set speaker on
		mAudioManager.setMode(AudioManager.MODE_IN_CALL);
		mAudioManager.setSpeakerphoneOn(true);
	}
	
	public void uninstall_audio_listener()
	{
		mAudioManager.setMode(AudioManager.MODE_NORMAL);
		mAudioManager.setSpeakerphoneOn(false);
	}
		
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		mContext = arg0;		
		if(arg1.getAction().equals("android.intent.action.PHONE_STATE"))
		{
			install_telephony_listener();
			//uninstall_telephony_listener();
		}
	}
}
