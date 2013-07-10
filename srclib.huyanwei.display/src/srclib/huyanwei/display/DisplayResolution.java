
package srclib.huyanwei.display;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.ScrollingMovementMethod;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Build;
import android.os.SystemProperties;

import android.util.DisplayMetrics;

//huyanwei {
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.http.util.EncodingUtils;
//huyanwei }
import android.os.Build;
import android.os.SystemProperties;

import android.util.DisplayMetrics;

public class DisplayResolution extends Activity {	

    private String TAG = "srclib.huyanwei.display";	
	
    private TextView m_width_value; 
    private TextView m_height_value;
    private TextView m_density_value;

    private int res = -1 ;
    private int width  = 0 ; 
    private int height = 0 ;

    private int android_width  = 0 ; 
    private int android_height = 0 ;

    private float android_density  = 0 ;
    private int android_density_dpi  = 0 ;

    private float android_scaledDensity = 0;
    private float android_noncompatDensity = 0;
    private float android_noncompatScaledDensity = 0;


    private int android_width2  = 0 ; 
    private int android_height2 = 0 ;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);        
        requestWindowFeature(Window.FEATURE_NO_TITLE);        
        setContentView(R.layout.main);                     
        m_width_value = (TextView)findViewById(R.id.lcm_width_value);
        m_height_value = (TextView)findViewById(R.id.lcm_height_value);
        m_density_value = (TextView)findViewById(R.id.lcm_density_value);

	DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
	android_width = dm.widthPixels;
	android_height = dm.heightPixels;

	android_density = dm.density;
        android_density_dpi  = dm.densityDpi ;

	android_scaledDensity = dm.scaledDensity;
	//android_noncompatDensity=dm.noncompatDensity;
	//android_noncompatScaledDensity=dm.noncompatScaledDensity;
		
	android_width2 = getWindowManager().getDefaultDisplay().getWidth();
	android_height2 = getWindowManager().getDefaultDisplay().getHeight();
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

//huyanwei {
    private String readLine(String filename) throws IOException 
    {
		         BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
		         try {
		             return reader.readLine();
		         } finally {
		             reader.close();
		         }
    }
//huyanwei }

//huyanwei {
    public String getCurrentPackageName()
        {
                String pacakge_name = "" ;
		/*
                try {
                        String fileName = "/proc/self/cmdline";
                        FileInputStream fin = new FileInputStream(fileName);
                        int length = fin.available();
                        byte[] buffer = new byte[length];
                        fin.read(buffer);
                        //pacakge_name = EncodingUtils.getString(buffer, "UTF-8");
                        pacakge_name = new String(buffer);
                        fin.close();
                        } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                }
		*/

               	try {
			//int pid = android.os.Process.myPid();
			//pacakge_name=readLine("/proc/"+pid+"/cmdline");
			pacakge_name=readLine("/proc/self/cmdline");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                return pacakge_name;
        }
//huyanwei }

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {

	String pacakge_name = getCurrentPackageName();

        res = DisplayNative.get_framebuffer_info_init();

	if(res == 0)
	{
		//res = DisplayNative.get_framebuffer_info(width, height);
		width = DisplayNative.get_framebuffer_info_width();
		height = DisplayNative.get_framebuffer_info_height();
	}
	res = DisplayNative.get_framebuffer_info_deinit();

        //m_width_value.setText(width.toString());
        //m_height_value.setText(height.toString());

	if(width > 0)
	        m_width_value.setText("K["+Integer.toString(width)+"]"+" M["+Integer.toString(android_width)+"]"+" D["+Integer.toString(android_width2)+"]");
	else
		m_width_value.setText("K["+this.getResources().getString(R.string.unknown)+"]"+" M["+Integer.toString(android_width)+"]"+" D["+Integer.toString(android_width2)+"]");

	if(height > 0)
		m_height_value.setText("K["+Integer.toString(height)+"]"+" M["+Integer.toString(android_height)+"]"+" D["+Integer.toString(android_height2)+"]");
	else
		m_height_value.setText("K["+this.getResources().getString(R.string.unknown)+"]"+" M["+Integer.toString(android_height)+"]"+" D["+Integer.toString(android_height2)+"]");
       
	m_density_value.setText("[PackageName="+pacakge_name+"]"+"{"+Float.toString(android_density)+"/"+Integer.toString(android_density_dpi)+"/"+Float.toString(android_scaledDensity)+"/"+Float.toString(android_noncompatDensity)+"/"+Float.toString(android_noncompatScaledDensity)+"}");

		super.onResume();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
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
		super.onDestroy();		
	}
}
