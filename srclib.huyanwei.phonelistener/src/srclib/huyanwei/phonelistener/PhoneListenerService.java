package srclib.huyanwei.phonelistener;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

public class PhoneListenerService extends Service  {

	private String TAG = "srclib.huyanwei.phonelistener.PhoneListenerService";
	
	private boolean DBG = false;
	
	private IntentFilter mIntentFilter ;
	private PhoneStateReceiver mPhoneStateReceiver;
	
	private TelephonyManager 	mTelephonyManager;
	private AudioManager 		mAudioManager ;
	private SensorManager 		mSensorManager;
	private KeyguardManager     mKeyguardManager;
	
	private Sensor 				mProximitySensor;
	private Sensor 				mLightSensor;	
	
	private Context 			mContext;

	private boolean 			mProximitySensorListening   =  false ;
	private float 				mProximitySensorValue   	=  1.0f ;  // left state.
	private float 				mLastProximitySensorValue   =  1.0f ;  // left state.
	
	private float 				mLightSensorValue   		=  0.0f ;  // left state.
	private float 				mLastLightSensorValue   	=  0.0f ;  // left state.
	private float 				mMaxLightSensorValue   		=  0.0f ;  // left state.
	
	private boolean 			mEventhappen = false;
	
	private final int 			MSG_HANDLE_INCOMING_CALL		= 1;
	private final int 			MSG_PROXIMITY_SENSOR_DEBOUNCED	= 2;

	//ref frameworks/base/services/java/com/android/server/power/DisplayPowerController.java
    // Trigger proximity if distance is less than 5 cm.
    private static final float TYPICAL_PROXIMITY_THRESHOLD = 5.0f;
	private float  mProximityThreshold = TYPICAL_PROXIMITY_THRESHOLD;	
	
    // Proximity sensor debounce delay in milliseconds for positive or negative transitions.
    private static final int PROXIMITY_SENSOR_POSITIVE_DEBOUNCE_DELAY = 0;
    private static final int PROXIMITY_SENSOR_NEGATIVE_DEBOUNCE_DELAY = 500;
	
    private static final int PROXIMITY_UNKNOWN = -1;
    private static final int PROXIMITY_NEGATIVE = 0;
    private static final int PROXIMITY_POSITIVE = 1;
    
    private static int  	mProximityState = PROXIMITY_UNKNOWN;
    private static long 	mProximityDebounceTime;
    
	private KeyEvent mKeyEvent;
	
	private final  CallStateHandler   mHandler = new CallStateHandler();
	
	private AudioRecordThread mAudioRecordThread;

	private boolean             mConfigProximitySensorEnable 	= true;  // 缺省是启动功能
	private boolean             mConfigProcessMothedAnswer 		= true;  // 缺省是 接电话.
	private boolean             mConfigOpenSpeaker 		   		= true;  // 缺省是 开外放.
	private boolean             mConfigLightSensorEnable 		= true;  // 缺省是 Light Sensor 辅助.
	private int             	mConfigLightSensorThreshold  	= 30;    // 缺省是 开外放.
	private boolean            	mConfigAudioRecord			  	= false; // 缺省是自动录音
	
	private ContentResolver mContentResolver;
	
	private int query_config_value(String name)
	{
		int value = 0 ;
		final String TABLE_FILED_ID 	= ConfigContentProvider.TABLE_FIELD_ID;
		final String TABLE_FILED_NAME 	= ConfigContentProvider.TABLE_FIELD_NAME;
		final String TABLE_FILED_VALUE 	= ConfigContentProvider.TABLE_FIELD_VALUE;
        final Uri uri = ConfigContentProvider.CONTENT_URI;

        //Log.d(TAG,"query_database("+name+")");
        
        // select TABLE_FILED_ID,TABLE_FILED_NAME,TABLE_FILED_VALUE where TABLE_FILED_NAME=name;
        Cursor c = mContentResolver.query(uri
        		,new String[]{TABLE_FILED_ID,TABLE_FILED_NAME,TABLE_FILED_VALUE} 
        		,TABLE_FILED_NAME+"=?"
        		,new String[]{name}
        		,null
        );
        
        final int IdIndex = c.getColumnIndexOrThrow(TABLE_FILED_ID);
        final int NameIndex = c.getColumnIndexOrThrow(TABLE_FILED_NAME);
        final int ValueIndex = c.getColumnIndexOrThrow(TABLE_FILED_VALUE);
        
        try {
            while (c.moveToNext()) 
            {
                value = c.getInt(ValueIndex);
                
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
        }
        return value;
	}
	
	private void update_data_from_database()
	{
		if(DBG)
		{
			Log.d(TAG,"update_data_from_database() {");
		}
		
		// update Data.
		mConfigProximitySensorEnable 	= (query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ENABLE) >=1) ? true : false;
		mConfigProcessMothedAnswer 		= (query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ACTION) >=1) ? true : false;
		mConfigOpenSpeaker		 		= (query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_SPEAKER)>=1) ? true : false;
		mConfigAudioRecord		 		= (query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_AUDIO_RECORD)>=1) ? true : false;
		mConfigLightSensorEnable 		= (query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_LIGHT_SENSOR_ENABLE) >=1) ? true : false;
		mConfigLightSensorThreshold  	= query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_LIGHT_SENSOR_THRESHOLD);
		
		if(DBG)
		{
			Log.d(TAG,"mConfigProximitySensorEnable="+mConfigProximitySensorEnable+",mConfigProcessMothedAnswer="+mConfigProcessMothedAnswer+",mConfigOpenSpeaker="+mConfigOpenSpeaker);
			Log.d(TAG,"mConfigLightSensorEnable="+mConfigLightSensorEnable+",mConfigLightSensorThreshold="+mConfigLightSensorThreshold);
		}
				
		if(DBG)
		{
			Log.d(TAG,"update_data_from_database() }");
		}
	}
	
	private ContentObserver mContentObserver = new ContentObserver(new Handler())
	{
		@Override
		public void onChange(boolean selfChange) 
		{
			// TODO Auto-generated method stub
			// 数据已经发生改变，selfChange 最好不要用。		
			
			update_data_from_database();
	        
			super.onChange(selfChange);
		}
	};
	
	private final class CallStateHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
				case MSG_HANDLE_INCOMING_CALL:		
					if(DBG)
					{
						Log.d(TAG,"handleMessage(MSG_HANDLE_INCOMING_CALL)");
					}
					handle_incoming_call();
					break;
				case MSG_PROXIMITY_SENSOR_DEBOUNCED:
					if(DBG)
					{
						Log.d(TAG,"handleMessage(MSG_PROXIMITY_SENSOR_DEBOUNCED)");
					}
					debounceProximitySensor();
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	private void handleProximitySensorEvent(long time, boolean positive)
	{
        if (mProximityState == PROXIMITY_NEGATIVE && !positive) {
            return; // no change
        }
        if (mProximityState == PROXIMITY_POSITIVE && positive) {
            return; // no change
        }
        
        // Only accept a proximity sensor reading if it remains
        // stable for the entire debounce delay.
        mHandler.removeMessages(MSG_PROXIMITY_SENSOR_DEBOUNCED);
        if (positive) {
        	mProximityState = PROXIMITY_POSITIVE;
            mProximityDebounceTime = time + PROXIMITY_SENSOR_POSITIVE_DEBOUNCE_DELAY;
        } else {
        	mProximityState = PROXIMITY_NEGATIVE;
            mProximityDebounceTime = time + PROXIMITY_SENSOR_NEGATIVE_DEBOUNCE_DELAY;
        }
        debounceProximitySensor();
	}
	
    private void debounceProximitySensor() 
    {
        if (mProximityState != PROXIMITY_UNKNOWN)
        {
            final long now = SystemClock.uptimeMillis();
            if (mProximityDebounceTime <= now) // 过了抖动时间
            {
            	if(mProximityState == PROXIMITY_POSITIVE) // 只接近时有效
   				{
            		mEventhappen = true ;
            		if(DBG)
            		{
            			Log.d(TAG,"mEventhappen="+mEventhappen);
            		}
                	post_handle_incoming_call_message();
   				}
            }
            else
            {
                Message msg = mHandler.obtainMessage(MSG_PROXIMITY_SENSOR_DEBOUNCED);
                //msg.setAsynchronous(true);
                mHandler.sendMessageAtTime(msg, mProximityDebounceTime);
            }
        }
    }
	
	private SensorEventListener mProximitySensorEventListener = new SensorEventListener()
	{

		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onSensorChanged(SensorEvent arg0) {
			// TODO Auto-generated method stub
            
			long SensorEventUpTimes = SystemClock.uptimeMillis();

            mLastProximitySensorValue = mProximitySensorValue;
            mProximitySensorValue = arg0.values[0];
			
            //if(DBG)
            {
            	Log.d(TAG, "mProximitySensorEventListener.onSensorChanged() proximity Sensor : mLastProximitySensorValue=" + mLastProximitySensorValue + ",mProximitySensorValue=" + mProximitySensorValue);
            }
            
            // 从 非0值 到 0值 认为是接近。 从0值到非0值认为是远离. 主要是有些系统的远离值 由驱动ic决定，有些是 1.0f ，有些是5.0f.   
			boolean positive   = ((((int)mLastProximitySensorValue > 0)) && (((int)mProximitySensorValue) == 0));
			
			// 只在跳变（下降沿）起作用。-\_
			//boolean positive = (((mLastProximitySensorValue - 1.0f) >= 0.0f) && ((mProximitySensorValue-1.0f) < 0.0f));
			
			// 只要有变化，就发送消息。[上升沿 和 下降沿]
			//boolean positive = (((int)mLastProximitySensorValue) != ((int)mProximitySensorValue));
			
			//Log.d(TAG,"inKeyguardRestrictedInputMode()"+ mKeyguardManager.inKeyguardRestrictedInputMode());
			
			//Log.d(TAG,"isKeyguardLocked()"+ mKeyguardManager.isKeyguardLocked()); // Level-16 api
			//Log.d(TAG,"mMaxLightSensorValue="+ mMaxLightSensorValue);
			//Log.d(TAG,"mConfigLightSensorThreshold="+ mConfigLightSensorThreshold);
			
			// 只有允许这个功能才能使用.
			if(mConfigProximitySensorEnable)
            {
				if(mConfigLightSensorEnable)
				{
					// 防止口袋模式，所以，需要光感来帮忙确定是不是在口袋中，或黑暗中，一般，来电时，屏都会被打亮.
					//Log.d(TAG,"mLightSensorValue="+mLightSensorValue);
					//if(mLightSensorValue >= mConfigLightSensorThreshold ) 
					if(mMaxLightSensorValue > mConfigLightSensorThreshold) // 防止手接近时，als的值也很小了，达不到上面的条件。同时，也能处理口袋模式
					{
						handleProximitySensorEvent(SensorEventUpTimes, positive);
					}
				}
				else
				{
					// 不依靠 光感
					handleProximitySensorEvent(SensorEventUpTimes, positive);
				}
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
			// we record the light sensor value to judge the  [pocket mode]			
			mLastLightSensorValue = mLightSensorValue;
			mLightSensorValue = event.values[0];
			
			if(mMaxLightSensorValue < mLightSensorValue )
			{
				mMaxLightSensorValue = mLightSensorValue;
			}
			
			//if(DBG) 
			{
				Log.d(TAG, "mLightSensorEventListener.onSensorChanged() Light Sensor : mMaxLightSensorValue=" + mMaxLightSensorValue + ",mLightSensorValue="+mLightSensorValue);			
			}
		}
	};
	
	private void notify_to_start_record_audio()
	{
		if(DBG)
		{
			Log.d(TAG,"notify_to_start_record_audio() {");
		}
		
    	// auto-audio-record
    	if(mConfigAudioRecord)
    	{
    		// need mAudioRecordThread
    		if(mAudioRecordThread != null)
    		{
    			Runnable showRunable=new Runnable() 
    			{
    				            //@Override
    				            public void run() 
    				            {
    				                //給线程发送一个Message
    				            	mAudioRecordThread.getHandler().sendEmptyMessage(AudioRecordThread.MSG_RECORD_AUDIO_START);
    				                //mHandler.postDelayed(this, 2*1000); // 每隔 2s 后继续调用这个
    				            }
		        };
		        mHandler.post(showRunable);
    		}
    	}
    	
    	if(DBG)
    	{
    		Log.d(TAG,"notify_to_start_record_audio() }");
    	}
	}
	
	private void notify_to_stop_record_audio()
	{
		if(DBG)
		{
			Log.d(TAG,"notify_to_stop_record_audio() {");
		}
		
    	// auto-audio-record
		if(mAudioRecordThread != null)		
    	{
			if(mAudioRecordThread.IsRecording()) // 不用if(mConfigAudioRecord)，防止用户回来修改这个开关。	
    		{
    			Runnable showRunable=new Runnable() 
    			{
    				            //@Override
    				            public void run() 
    				            {
    				                //給线程发送一个Message
    				            	mAudioRecordThread.getHandler().sendEmptyMessage(AudioRecordThread.MSG_RECORD_AUDIO_STOP);
    				                //mHandler.postDelayed(this, 1000); // 2s 后继续调用这个
    				            	mAudioRecordThread = null;
    				            }
		        };
		        mHandler.post(showRunable);
    		}
			else
			{
				//mAudioRecordThread.stop(); // 不在录音，就停止线程.
				//mAudioRecordThread = null;
				
    			Runnable ThreadExitRunable=new Runnable() 
    			{
    				            //@Override
    				            public void run() 
    				            {
    				                //給线程发送一个Message
    				            	mAudioRecordThread.getHandler().sendEmptyMessage(AudioRecordThread.MSG_THREAD_EXIT);
    				                //mHandler.postDelayed(this, 1000); // 2s 后继续调用这个
    				            	mAudioRecordThread = null;
    				            }
		        };
		        mHandler.post(ThreadExitRunable);
			}						
    	}
		
		if(DBG)
		{
			Log.d(TAG,"notify_to_stop_record_audio() }");
		}
	}
	
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
			
			if(DBG)
			{
				Log.d(TAG,"mPhoneStateListener.onCallStateChanged()");
			}
			
			switch(state)
			{
				case  TelephonyManager.CALL_STATE_IDLE:    // 空闲状态.
					if(DBG)
					{
						Log.d(TAG,"mPhoneStateListener.onCallStateChanged(CALL_STATE_IDLE) ");
					}
					if(mProximitySensorListening)
					{	
						unregisterSensorListener();
						if((mEventhappen) && (mConfigOpenSpeaker))
						{
							CloseSpeaker();  				// 如果之前开了 Speaker,关掉Speaker.
						}
					}

					// stop audio record
					notify_to_stop_record_audio();

					break; 
				case  TelephonyManager.CALL_STATE_RINGING: // 来电铃声状态.
					if(DBG)
					{
						Log.d(TAG,"mPhoneStateListener.onCallStateChanged(CALL_STATE_RINGING) ");
					}
					
					if(mConfigAudioRecord)
					{
						mAudioRecordThread = new AudioRecordThread(mContext,incomingNumber);
		    			if(mAudioRecordThread != null)
		    			{
		    				mAudioRecordThread.start(); // 让线程等待消息。
		    			}
					}
					
					//if(mConfigProximitySensorEnable)             // 只有起作用才能使用这个功能,在事件里面截获是不是更加安全？
					{
						mEventhappen = false ;      			// 初始化 事件还没有发生。
						registerSensorListener(); 				// 让距离感应器 监听
					}					
					break;
				case  TelephonyManager.CALL_STATE_OFFHOOK: // 挂机状态(拿起了话筒/接起电话)
					if(DBG)
					{
						Log.d(TAG,"mPhoneStateListener.onCallStateChanged(CALL_STATE_OFFHOOK) ");
					}
					if(mProximitySensorListening)
					{
						unregisterSensorListener(); 		// 接听也要把之前的注册 卸载掉。						
					}
					notify_to_start_record_audio();         // 接通后才发消息，注意，里面有条件
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
		if(DBG)
		{
			Log.d(TAG,"registerSensorListener()");
		}

		mProximityState = PROXIMITY_UNKNOWN ;   // 距离感应器处于无效状态.        
		mProximitySensorValue   = 0.0f;         // 来电时 设置 接近态，只有下降沿（由高到低）才有效。  默认是被挡住  
		mLightSensorValue 		= 0.0f;         // 来电时 als 初始化 .来电时，默认是被挡住.
		mMaxLightSensorValue    = 0.0f;         // 之前没有值
		
		// start lister sensor
        mSensorManager.registerListener(mProximitySensorEventListener, mProximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mLightSensorEventListener,     mLightSensor,    SensorManager.SENSOR_DELAY_NORMAL);
        mProximitySensorListening = true;  		// 只在来电时候生效。
        
	}
	
	public void unregisterSensorListener()
	{
		if(DBG)
		{
			Log.d(TAG,"unregisterSensorListener()");
		}
		
		mProximitySensorListening = false;  	// 只在来电时候生效。
		
		mProximityState = PROXIMITY_UNKNOWN ;   // 距离感应器处于无效状态. 
		
		mHandler.removeMessages(MSG_PROXIMITY_SENSOR_DEBOUNCED);  // 删除 debounce 消息
		
		mSensorManager.unregisterListener(mLightSensorEventListener, mLightSensor);		
		mSensorManager.unregisterListener(mProximitySensorEventListener, mProximitySensor);	
		
		
	}
	
	public void OpenSpeaker()
	{
		if(DBG)
		{
			Log.d(TAG,"OpenSpeaker()");
		}
		// set speaker on
		mAudioManager.setMode(AudioManager.MODE_IN_CALL);
		mAudioManager.setSpeakerphoneOn(true);
	}
	
	public void CloseSpeaker()
	{
		if(DBG)
		{
			Log.d(TAG,"CloseSpeaker()");
		}
		mAudioManager.setMode(AudioManager.MODE_NORMAL);
		mAudioManager.setSpeakerphoneOn(false);
	}
	
	
	public void connectPhoneItelephony()
	{
		//Log.d(TAG,"自动接听");
		try {
			if(DBG)
			{
				Log.d(TAG,"Telephony Control");
			}
			PhoneUtils.getITelephony(mTelephonyManager).silenceRinger();	// 静铃
			PhoneUtils.getITelephony(mTelephonyManager).answerRingingCall();// 自动接听
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try 
		{
			//Log.d(TAG,"line control answer key 1");
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
			//Log.d(TAG,"line control answer key 2");		
			Intent KeyIntent = new Intent("android.intent.action.MEDIA_BUTTON");
			mKeyEvent = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_HEADSETHOOK);
			KeyIntent.putExtra("android.intent.extra.KEY_EVENT", mKeyEvent);
			mContext.sendOrderedBroadcast(KeyIntent, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

    	if((mEventhappen) && (mConfigOpenSpeaker))
    	{
			OpenSpeaker(); // 切换外音模式。
    	}
    	
    	// auto-audio-record
    	//notify_to_start_record_audio(); // move other where ?
	}
	
	public void disconnectPhoneItelephony()
	{
		//Log.d(TAG,"自动挂断");
		try {
			PhoneUtils.getITelephony(mTelephonyManager).endCall(); 						 // 挂断
			PhoneUtils.getITelephony(mTelephonyManager).cancelMissedCallsNotification(); // 取消未接显示
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
		// 来电状态。
		if(mTelephonyManager.getCallState() == TelephonyManager.CALL_STATE_RINGING)
		{
			if((mConfigProcessMothedAnswer))
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
		//Log.d(TAG,"post_handle_incoming_call_message()");
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

		//Log.d(TAG,"huyanwei debug onCreate() registerReceiver() {");
		
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
		
		// KeyguardManager
		mKeyguardManager = (KeyguardManager)mContext.getSystemService(Context.KEYGUARD_SERVICE);
		
		
		// 动态注册  广播接收器
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("android.intent.action.PHONE_STATE");		 // use-pemission		
		mIntentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);		 	 // use-pemission		
		mIntentFilter.setPriority(Integer.MAX_VALUE);		
		mPhoneStateReceiver = new PhoneStateReceiver();
		registerReceiver(mPhoneStateReceiver, mIntentFilter);
		
		// install ContentObserver
		mContentResolver = mContext.getContentResolver();		
		mContentResolver.registerContentObserver(ConfigContentProvider.CONTENT_URI, false, mContentObserver);
		
		update_data_from_database(); // init data .
		
		//Log.d(TAG,"huyanwei debug onCreate() registerReceiver() }");

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
		if(mTelephonyManager!= null)
		{
			mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		
		unregisterReceiver(mPhoneStateReceiver);
		
		// uninstall ContentObserver
		mContentResolver.unregisterContentObserver(mContentObserver);
		
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		if(mTelephonyManager!= null)
		{
			//mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
			mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE); // only call state
		}		
		super.onStart(intent, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
}
