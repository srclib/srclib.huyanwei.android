
package srclib.huyanwei.bubble;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.ScrollingMovementMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.os.Build;
import android.os.SystemProperties;

public class BubbleActivity extends Activity {	
	private String TAG = "srclib.huyanwei.bubble";	
	private SensorManager mSm = null ;
	private Sensor mGravitySensor =null;
	private Handler mHandler = null;	
	
	public static final String MSG_TYPE = "MSG_TYPE";
		
	public static final String MSG_TYPE_COORD = "COORD";
	public static final String MSG_TYPE_COORD_RST = "COORD_RST";
	public static final String MSG_TYPE_CALIB = "CALIB";
	public static final String MSG_TYPE_CALIB_RST = "CALIB_RST";
	public static final String MSG_TYPE_CALIB_RST_STR = "CALIB_RST_STR";
	
	public static final String MSG_ID_DELAY = "DELAY";	
	public static final String MSG_ID_NUM = "NUM";
	public static final String MSG_ID_TOLERANCE = "TOLERANCE";
	
	public static final String MSG_ID_X = "X";
	public static final String MSG_ID_Y = "Y";
	public static final String MSG_ID_Z = "Z";
		
	private TextView G_X ;
	private TextView G_Y ;
	private TextView G_Z ;	
	private TextView G_DETAIL ;	

	private LevelBalance hor_level ;
	private LevelBalance ver_level ;
	private Button	calib_button ;
	
	private final boolean DBG =  false ;
	
	private OnClickListener mOnClickListener;
	
	private int mCurrentTargetVersion = 10 ;
	
	private String sdk_version = "";

	Process process = null;
	
	public static final int user_interface= 1; // 1-> activity , 2-> Toast.
	
	SensorEventListener lsn = new SensorEventListener(){
        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy)
        {
        }
        public void onSensorChanged(SensorEvent e)
        {
            if (e.sensor == mGravitySensor)
            {
                int x = (int) (e.values[SensorManager.DATA_X] * 1000);
                int y = (int) (e.values[SensorManager.DATA_Y] * 1000);
                int z = (int) (e.values[SensorManager.DATA_Z] * 1000);
                
                Message msg = new Message();
				msg.setTarget(mHandler);
				Bundle bundle = new Bundle();
				bundle.putString(MSG_TYPE,MSG_TYPE_COORD);
				bundle.putInt(MSG_ID_X, x);
				bundle.putInt(MSG_ID_Y, y);
				bundle.putInt(MSG_ID_Z, z);
				msg.setData(bundle);
				msg.sendToTarget();
				if(DBG) Log.d(TAG,"send:x="+x+",y="+y+",z="+z+"\n");                
            }
        }
    };

    public void onAttachedToWindow() {
    	if(mCurrentTargetVersion>=15)
    	{
    		//this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
    		//this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NEEDS_MENU_KEY);
    		this.getWindow().addFlags(0x08000000);
    		//this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_HOMEKEY_DISPATCHED);
    	}
        super.onAttachedToWindow();
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main);      
        
        sdk_version = SystemProperties.get("ro.build.version.sdk");
        
    	mCurrentTargetVersion = Integer.parseInt(sdk_version);
        
        G_X = (TextView)findViewById(R.id.N_X);
        G_Y = (TextView)findViewById(R.id.N_Y);
        G_Z = (TextView)findViewById(R.id.N_Z);
        G_DETAIL = (TextView)findViewById(R.id.N_DETAIL);

//	G_DETAIL.setMovementMethod(ScrollingMovementMethod.getInstance());
        
        hor_level = (LevelBalance)findViewById(R.id.hor_level_balance);
        ver_level = (LevelBalance)findViewById(R.id.ver_level_balance);   
        
        hor_level.setStyle(LevelBalance.HOR);        
        ver_level.setStyle(LevelBalance.VER);
        
        mOnClickListener = new OnClickListener()
        {
    		public void onClick(View arg0) {
    			// TODO Auto-generated method stub			
				switch(arg0.getId())
				{
				case R.id.button_calib:
					String startstring = String.format(" ");
					G_DETAIL.setText(startstring);

					int delay = 50 , num = 20 , tolerance = 40 ;
					Message msg = new Message();
					msg.setTarget(mHandler);
					Bundle bundle = new Bundle();
					bundle.putString(MSG_TYPE,MSG_TYPE_CALIB);
					bundle.putInt(MSG_ID_DELAY, delay);
					bundle.putInt(MSG_ID_NUM, num);
					bundle.putInt(MSG_ID_TOLERANCE, tolerance);
					msg.setData(bundle);
					msg.sendToTarget();				
					if(DBG) Log.d(TAG,"send:dealy="+delay+",num="+num+",tolerance="+tolerance+"\n");
					
					break;
				default:
					if(DBG) Log.d(TAG,"onClick default\n");
					break;
				}
    		}        	
        };
        calib_button = (Button)findViewById(R.id.button_calib);
        calib_button.setOnClickListener(mOnClickListener);   
                
        mSm = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        if(mCurrentTargetVersion >= 15)
        {
            //mGravitySensor = mSm.getDefaultSensor(android.hardware.Sensor.TYPE_GRAVITY);
            mGravitySensor = mSm.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);
        }
        else
        {
        	mGravitySensor = mSm.getDefaultSensor(android.hardware.Sensor.TYPE_GRAVITY);
        }        

        if(mGravitySensor == null)
        {
                Log.i(TAG,"getDefaultSensor() is null");
        }
        
        class CalibratorThread extends Thread{    	
        	int mdelay ;
        	int mnum ;
        	int mtolerance;
        	public CalibratorThread(int delay, int num , int tolerance){
        		mdelay = delay ;
        		mnum  = num ;
        		mtolerance = tolerance ;
    		}
        	
    		public void run(){    			
				boolean res = false ;
				res = GSensorNative.opendev();
				if(res)
				{
					if(DBG)
						Log.d(TAG,"CalibratorThread run():dealy="+mdelay+",num="+mnum+",tolerance="+mtolerance+"\n");

					res =GSensorNative.calibrator(mdelay,mnum,mtolerance);
					if(res )
					{
						if(DBG) Log.d(TAG,"calibrator ok .");
					}
					GSensorNative.closedev();
				}
    		}
    	}

        class CalibratorCommandThread extends Thread{    	
        	int mdelay ;
        	int mnum ;
        	int mtolerance;
        	public CalibratorCommandThread(int delay, int num , int tolerance){
        		mdelay = delay ;
        		mnum  = num ;
        		mtolerance = tolerance ;
        		
        		if(DBG) 
        			Log.d(TAG,"receive:dealy="+delay+",num="+num+",tolerance="+tolerance+"\n");
    		}
        	
    		public void run(){
    			File su = new File("/system/xbin/su");
				if (su.exists())
				{
					// JNI mothed.
					String command ;
					command = new String("/system/xbin/su -c /system/bin/gsensor_calibrator -d "+mdelay+" -n "+mnum+" -t "+mtolerance);
					{
						try {
							process = Runtime.getRuntime().exec(command);
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						if(process != null)
						{
							//process.waitFor();
						}
// huyanwei {
						/*
						DataOutputStream os = new DataOutputStream(process.getOutputStream());
						InputStream is = process.getInputStream();
						try {
							Thread.sleep(200);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						byte[] buff = new byte[10000];
						try {
							int read = is.read(buff, 0, buff.length);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String str = new String(buff);
						*/
						
						BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
						String str = null , line = null ;						
						try {
							while ((line = in.readLine()) != null) {   
								 str += line + "\n";                  
							   }
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}   
//huyanwei }						
						
						
						Message srt_msg = new Message();
						srt_msg.setTarget(mHandler);
						Bundle bundle = new Bundle();
						bundle.putString(MSG_TYPE_CALIB_RST_STR,str);
						bundle.putString(MSG_TYPE,MSG_TYPE_CALIB_RST);
						srt_msg.setData(bundle);
						srt_msg.sendToTarget();	
						
						//os.writeBytes("chmod 4755 /system/bin/su\n");
						//os.writeBytes("exit\n");
						//os.flush();
					}
				}
    		}
    	}       

    	mHandler = new Handler(){
    		public void handleMessage(Message msg)
    		{    			
    			if(msg.getData().getString(MSG_TYPE).equals(MSG_TYPE_COORD))
    			{	
    				int x =0 , y =0 , z =0 ;
        			x = msg.getData().getInt(MSG_ID_X) ;
        			y = msg.getData().getInt(MSG_ID_Y) ;
        			z = msg.getData().getInt(MSG_ID_Z) ;    	
        		
        			if(DBG) Log.d(TAG,"receive:x="+x+",y="+y+",z="+z+"\n");
        			
        			G_X.setText("x="+(x *1.0) /1000);
        			G_Y.setText("y="+(y *1.0) /1000);
        			G_Z.setText("z="+(z *1.0) /1000);
        			
        			hor_level.setValue(x, y, z);
        			
        			ver_level.setValue(x, y, z);        			
    			}
    			else if(msg.getData().getString(MSG_TYPE).equals(MSG_TYPE_CALIB))
        		{	
					calib_button.setEnabled(false); // disable button .
					
    				int delay = 0 , num = 0 , tolerance = 0 ;
    				delay = 	msg.getData().getInt(MSG_ID_DELAY) ;
        			num =		msg.getData().getInt(MSG_ID_NUM) ;
        			tolerance = msg.getData().getInt(MSG_ID_TOLERANCE) ;   
        			
        			if(DBG) Log.d(TAG,"receive:dealy="+delay+",num="+num+",tolerance="+tolerance+"\n");
				

        			CalibratorCommandThread mCalibratorCommandThread= new CalibratorCommandThread(delay,num,tolerance);
        			mCalibratorCommandThread.start();
        			
        			/*
        			File su = new File("/system/xbin/su");
        			if (su.exists())
        			{
					// JNI mothed.
					String command ;
					command = new String("/system/xbin/su -c /system/bin/gsensor_calibrator -d "+delay+" -n "+num+" -t "+tolerance);
					try {
						process = Runtime.getRuntime().exec(command);
						if(process != null)
						{
							//process.waitFor();
						}
						DataOutputStream os = new DataOutputStream(process.getOutputStream());
						InputStream is = process.getInputStream();
						Thread.sleep(200);
						byte[] buff = new byte[10000];
						int read = is.read(buff, 0, buff.length);
						String str = new String(buff);				
						G_DETAIL.setText(str);
					
							Message srt_msg = new Message();
							srt_msg.setTarget(mHandler);
							Bundle bundle = new Bundle();
							bundle.putString(MSG_TYPE,MSG_TYPE_CALIB_RST);
							srt_msg.setData(bundle);
							srt_msg.sendToTarget();				

							//os.writeBytes("chmod 4755 /system/bin/su\n");
							//os.writeBytes("exit\n");
							//os.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// JNI mothed. but only can calibrator chip , can't save date to nvram ,need root .
	        			//CalibratorThread mCalibratorThread = new CalibratorThread(delay,num,tolerance);
	        			//mCalibratorThread.start();
						}
						else
						{
	        				Log.d(TAG,"file /system/xbin/su is not exists !!!\n");
						}
						*/
        		}
				else if(msg.getData().getString(MSG_TYPE).equals(MSG_TYPE_CALIB_RST))
				{
					String str = msg.getData().getString(MSG_TYPE_CALIB_RST_STR) ;
					Log.d(TAG,"handler recerve result string="+str);
					G_DETAIL.setText(str);
					calib_button.setEnabled(true); // enable button
				}
    	    }
    	};
    }
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
        mSm.registerListener(lsn, mGravitySensor, SensorManager.SENSOR_DELAY_GAME);
        
		super.onResume();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mSm.unregisterListener(lsn);
		super.onPause();
	}
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(process != null)
		{
			process.destroy();
		}				
		super.onDestroy();		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem item;
		item = menu.add(Menu.NONE, 0,0, R.string.help);
		//item.setIcon(R.drawable.clear_grant_list);
		//return true;
		return super.onCreateOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
        Toast toast;
		switch (item.getItemId())
        {
		case 0:
			if(user_interface == 1)
			{
			    Intent viewlist_intent = new Intent();
			    viewlist_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    viewlist_intent.setComponent(new ComponentName("srclib.huyanwei.bubble", "srclib.huyanwei.bubble.HelpActivity"));
			    startActivity(viewlist_intent);
			}
			else
			{
				toast = Toast.makeText(this, getString(R.string.help_content), Toast.LENGTH_LONG);
				toast.show();
			}
			break;
		default:
		    break;
        }        		
		return super.onOptionsItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsMenuClosed(android.view.Menu)
	 */
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		// TODO Auto-generated method stub
		super.onOptionsMenuClosed(menu);
	}    
}
