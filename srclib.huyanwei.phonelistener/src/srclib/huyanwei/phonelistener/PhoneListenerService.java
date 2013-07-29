package srclib.huyanwei.phonelistener;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

public class PhoneListenerService extends Service  {

	private String TAG = "srclib.huyanwei.phonelistener.PhoneListenerService";
	
	private IntentFilter mIntentFilter ;
	private PhoneStateReceiver mPhoneStateReceiver;
	
	private TelephonyManager 	mTelephonyManager;
	private AudioManager 		mAudioManager ;
	private SensorManager 		mSensorManager;
	
	private Sensor 				mProximitySensor;
	private Sensor 				mLightSensor;	
	
	private Context 			mContext;

	private boolean 			mProximitySensorListening   =  false ;
	private float 				mProximitySensorValue   	=  1.0f ;  // left state.
	private float 				mLastProximitySensorValue   =  1.0f ;  // left state.
	
	private boolean 			mEventhappen = false;
	
	private boolean             mProcessMothedAnswer = true;  // ȱʡ�� �ҵ绰.
	
	private final int 			MSG_HANDLE_INCOMING_CALL	= 1;
	
	private KeyEvent mKeyEvent;

	//ref frameworks/base/services/java/com/android/server/power/DisplayPowerController.java
    // Trigger proximity if distance is less than 5 cm.
    private static final float TYPICAL_PROXIMITY_THRESHOLD = 5.0f;
	private float  mProximityThreshold = TYPICAL_PROXIMITY_THRESHOLD;
	
	private Handler            mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
				case MSG_HANDLE_INCOMING_CALL:		
					Log.d(TAG,"handleMessage(MSG_HANDLE_INCOMING_CALL)");
					handle_incoming_call();
					break;
				default:
					break;
			}			
			super.handleMessage(msg);
		}		
	};
	
	private SensorEventListener mProximitySensorEventListener = new SensorEventListener()
	{

		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onSensorChanged(SensorEvent arg0) {
			// TODO Auto-generated method stub
            
            mLastProximitySensorValue = mProximitySensorValue;
            mProximitySensorValue = arg0.values[0];
            
            //Log.d(TAG, "mProximitySensorEventListener.onSensorChanged: proximity Sensor value: " + arg0.values[0]);
            Log.d(TAG, "mProximitySensorEventListener.onSensorChanged: proximity Sensor mLastProximitySensorValue=" + mLastProximitySensorValue + ",mProximitySensorValue=" + mProximitySensorValue);
            
			//boolean positive = ((mProximitySensorValue >= 0.0f) && (mProximitySensorValue < mProximityThreshold));

            // ֻ�����䣨�½��أ������á�-\_
            //if(((mLastProximitySensorValue - 1.0f) >= 0.0f) && ((mProximitySensorValue-1.0f) < 0.0f))
			
			// ֻҪ�б仯���ͷ�����Ϣ��[������ �� �½���]
			if(((int)mLastProximitySensorValue) != ((int)mProximitySensorValue))
            {
   				mEventhappen = true ;
		        Log.d(TAG,"mEventhappen="+mEventhappen);
            	post_handle_incoming_call_message();
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
			//Log.d(TAG, "onSensorChanged: Light Sensor value: " + event.values[0]);
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
			
			Log.d(TAG,"mPhoneStateListener.onCallStateChanged()");
			
			switch(state)
			{
				case  TelephonyManager.CALL_STATE_IDLE:    // ����״̬.
					Log.d(TAG,"mPhoneStateListener.onCallStateChanged(CALL_STATE_IDLE) ");
					if(mProximitySensorListening)
					{	
						unregisterSensorListener();
						if(mEventhappen)
						{
							//CloseSpeaker();  // �����֮ǰ���� Speaker,�ص�Speaker.
						}
					}
					break; 
				case  TelephonyManager.CALL_STATE_RINGING: // ��������״̬.
					Log.d(TAG,"mPhoneStateListener.onCallStateChanged(CALL_STATE_RINGING) ");
					mEventhappen = false ;      			// ��ʼ�� �¼���û�з�����
					registerSensorListener(); 				// �þ����Ӧ�� ����
					break;
				case  TelephonyManager.CALL_STATE_OFFHOOK: // �һ�״̬(�����˻�Ͳ/����绰)
					Log.d(TAG,"mPhoneStateListener.onCallStateChanged(CALL_STATE_OFFHOOK) ");
					if(mProximitySensorListening)
					{	
						unregisterSensorListener(); 		// ����ҲҪ��֮ǰ��ע�� ж�ص���
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
			
			//Log.d(TAG,"huyanwei debug onSignalStrengthChanged("+asu+")");
			
			super.onSignalStrengthChanged(asu);
		}

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			// TODO Auto-generated method stub
			
			//Log.d(TAG,"huyanwei debug onSignalStrengthsChanged("+signalStrength.getGsmSignalStrength()+")");
			
			super.onSignalStrengthsChanged(signalStrength);
		}		
	};

	public void registerSensorListener()
	{
		Log.d(TAG,"registerSensorListener()");

		// start lister sensor
        mSensorManager.registerListener(mProximitySensorEventListener, mProximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mLightSensorEventListener,     mLightSensor,    SensorManager.SENSOR_DELAY_NORMAL);
        
        mProximitySensorListening = true;  		// ֻ������ʱ����Ч��
	}
	
	public void unregisterSensorListener()
	{
		mProximitySensorListening = false;  	// ֻ������ʱ����Ч��
		
		Log.d(TAG,"registerSensorListener()");
		mSensorManager.unregisterListener(mLightSensorEventListener, mLightSensor);		
		mSensorManager.unregisterListener(mProximitySensorEventListener, mProximitySensor);	
	}
	
	public void OpenSpeaker()
	{
		Log.d(TAG,"OpenSpeaker()");
		// set speaker on
		mAudioManager.setMode(AudioManager.MODE_IN_CALL);
		mAudioManager.setSpeakerphoneOn(true);
	}
	
	public void CloseSpeaker()
	{
		Log.d(TAG,"CloseSpeaker()");
		mAudioManager.setMode(AudioManager.MODE_NORMAL);
		mAudioManager.setSpeakerphoneOn(false);
	}
	
	
	public void connectPhoneItelephony()
	{
		Log.d(TAG,"�Զ�����");
		try {
			Log.d(TAG,"Telephony Control");
			PhoneUtils.getITelephony(mTelephonyManager).silenceRinger();	// ����
			PhoneUtils.getITelephony(mTelephonyManager).answerRingingCall();// �Զ�����
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try 
		{
			Log.d(TAG,"line control answer key 1");
	    	Intent KeyIntent = new Intent("android.intent.action.MEDIA_BUTTON");
	    	mKeyEvent = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_HEADSETHOOK);
	    	KeyIntent.putExtra("android.intent.extra.KEY_EVENT", mKeyEvent);
	    	mContext.sendOrderedBroadcast(KeyIntent, "android.permission.CALL_PRIVILEGED");
	    	
	    	KeyIntent = new Intent("android.intent.action.MEDIA_BUTTON");
	    	mKeyEvent = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_HEADSETHOOK);
	    	KeyIntent.putExtra("android.intent.extra.KEY_EVENT", mKeyEvent);
	    	mContext.sendOrderedBroadcast(KeyIntent, "android.permission.CALL_PRIVILEGED");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		try 
		{
			Log.d(TAG,"line control answer key 2");		
			Intent KeyIntent = new Intent("android.intent.action.MEDIA_BUTTON");
			mKeyEvent = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_HEADSETHOOK);
			KeyIntent.putExtra("android.intent.extra.KEY_EVENT", mKeyEvent);
			mContext.sendOrderedBroadcast(KeyIntent, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

    	if(mEventhappen)
    	{
			//OpenSpeaker(); // �л�����ģʽ��
    	}
	}
	
	public void disconnectPhoneItelephony()
	{
		Log.d(TAG,"�Զ��Ҷ�");
		try {
			PhoneUtils.getITelephony(mTelephonyManager).endCall(); 						 // �Ҷ�
			PhoneUtils.getITelephony(mTelephonyManager).cancelMissedCallsNotification(); //ȡ��δ����ʾ
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void handle_incoming_call()
	{
		// ����״̬��
		if(mTelephonyManager.getCallState() == TelephonyManager.CALL_STATE_RINGING)
		{
			if((mProcessMothedAnswer))
			{
				connectPhoneItelephony();
			}
			else
			{
				disconnectPhoneItelephony();
			}
		}
	}
	
	public void post_handle_incoming_call_message()
	{
		Log.d(TAG,"post_handle_incoming_call_message()");
		Message msg = mHandler.obtainMessage();
		msg.what = MSG_HANDLE_INCOMING_CALL;
		msg.arg1 = 1;
		msg.arg2 = 1;
		msg.sendToTarget();
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
		// TODO Auto-generated method stub
		super.dump(fd, writer, args);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		Log.d(TAG,"huyanwei debug onCreate() registerReceiver() {");
		
		mContext = this;

		// TelephonyManager
		mTelephonyManager= (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		
		// SensorManager
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mLightSensor     = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

		// ref frameworks/base/services/java/com/android/server/power/DisplayPowerController.java
        if (mProximitySensor != null) 
		{
               mProximityThreshold = Math.min(mProximitySensor.getMaximumRange(),TYPICAL_PROXIMITY_THRESHOLD);
        }
        
        // AudioManager
		mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);       
		
		// ��̬ע��  �㲥������
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("android.intent.action.PHONE_STATE");		 // use-pemission
		
		mIntentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);		 	 // use-pemission
		
		mIntentFilter.setPriority(Integer.MAX_VALUE);
		
		mPhoneStateReceiver = new PhoneStateReceiver();
		registerReceiver(mPhoneStateReceiver, mIntentFilter);
		
		Log.d(TAG,"huyanwei debug onCreate() registerReceiver() }");

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
		if(mTelephonyManager!= null)
		{
			mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		
		unregisterReceiver(mPhoneStateReceiver);
		
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		if(mTelephonyManager!= null)
		{
			mTelephonyManager.listen(mPhoneStateListener, 
				PhoneStateListener.LISTEN_CALL_STATE | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		}		
		super.onStart(intent, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
}
