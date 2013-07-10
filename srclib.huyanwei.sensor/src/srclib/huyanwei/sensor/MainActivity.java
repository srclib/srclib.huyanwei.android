package srclib.huyanwei.sensor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SystemSensorManager;

// ref frameworks/base/services/java/com/android/server/PowerManagerService.java

public class MainActivity extends Activity {

	private String TAG =  "srclib.huyanwei.sensor";
	
    // light sensor events rate in microseconds
    private static final int LIGHT_SENSOR_RATE = 1000000;
	
	private TextView m_light_value;  
	private TextView m_promitity_value;
	
	private Sensor mProximitySensor ;
	private Sensor mLightSensor ;
	
	private SensorManager mSensorManager ;

	private String MSG_SENSOR 	= "sensor";
	private String MSG_SENSOR_L = "light";
	private String MSG_SENSOR_P = "proximity";
	private String MSG_SENSOR_VALUE = "value";
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{    			
			if(msg.getData().getString(MSG_SENSOR).equals(MSG_SENSOR_L))
			{	
				float value = 0 ;
				value = msg.getData().getFloat(MSG_SENSOR_VALUE) ;    		
				m_light_value.setText(""+value);
			}
			else if(msg.getData().getString(MSG_SENSOR).equals(MSG_SENSOR_P))
    		{
				float value = 0 ;
				value = msg.getData().getFloat(MSG_SENSOR_VALUE) ;    		
				m_promitity_value.setText(""+value);
    		}
		}
	};
	
	 SensorEventListener mLightListener = new SensorEventListener() {
         //@Override
         public void onSensorChanged(SensorEvent event) {
             Log.d(TAG, "onSensorChanged: light value: " + event.values[0]);
             
             Message msg = new Message();
			 msg.setTarget(mHandler);
			 Bundle bundle = new Bundle();
			 bundle.putString(MSG_SENSOR,MSG_SENSOR_L);
			 bundle.putFloat(MSG_SENSOR_VALUE, event.values[0]);
			 msg.setData(bundle);
			 msg.sendToTarget();
         }

         // @Override
         public void onAccuracyChanged(Sensor sensor, int accuracy) {
             // ignore
         }
     };

     SensorEventListener mProximityListener = new SensorEventListener()
     {
         public void onSensorChanged(SensorEvent event) {                          
             float distance = event.values[0];
             Log.d(TAG, "onSensorChanged: proximity value: " + event.values[0]);

             Message msg = new Message();
			 msg.setTarget(mHandler);
			 Bundle bundle = new Bundle();
			 bundle.putString(MSG_SENSOR,MSG_SENSOR_P);
			 bundle.putFloat(MSG_SENSOR_VALUE, event.values[0]);
			 msg.setData(bundle);
			 msg.sendToTarget();
             
         }
         
         public void onAccuracyChanged(Sensor sensor, int accuracy)
         {
         // ignore
         }
     };
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        m_light_value = (TextView) findViewById(R.id.light_value);
        m_promitity_value = (TextView)findViewById(R.id.proximity_value);
        
        m_light_value.setText("");
        m_promitity_value.setText("");
        
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mLightSensor     = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		mSensorManager.unregisterListener(mLightListener, mLightSensor);
		
		mSensorManager.unregisterListener(mProximityListener, mProximitySensor);	
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

        mSensorManager.registerListener(mLightListener, mLightSensor,LIGHT_SENSOR_RATE);
        
        
        mSensorManager.registerListener(mProximityListener, mProximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL);

		
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
