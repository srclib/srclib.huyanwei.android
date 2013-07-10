package srclib.huyanwei.animationselect;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends Activity {
	private String TAG = "srclib.huyanwei.animationselect";
	
	private String logo_file = "/sys/logo/logo_style";
	
	private RadioGroup mRadioGroup;
	
	private ImageView mImageView ;
	
	private FrameLayout mFrameLayout;
	
	public static final String MSG_TYPE = "MSG_TYPE";	
	
	public static final String MSG_TYPE_ANI = "ANI";
	public static final String MSG_TYPE_ANI_FINISH = "FINISH";
	
	public static final String MSG_TYPE_ANI_DATA = "ANI_DATA";	

	private Handler mHandler = null;

    private String readFile(File fn) {
        FileReader f;
        int len;
        f = null;
        try {
            f = new FileReader(fn);
            String s = "";
            char[] cbuf = new char[200];
            while ((len = f.read(cbuf, 0, cbuf.length)) >= 0) {
                s += String.valueOf(cbuf, 0, len);
            }
            return s;
        } catch (IOException ex) {
            return "0";
        } finally {
            if (f != null) {
                try {
                    f.close();
                } catch (IOException ex) {
                    return "0";
                }
            }
        }
    }
	
	public void  onSelectAnimation(int index)
	{
		String data_string = "";
		File file = new File(logo_file);
		if (file.exists())
		{
			FileOutputStream outStream = null;
			try {
				outStream = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				data_string += String.valueOf(index);
				Log.d(TAG,"data_string="+data_string);
				outStream.write(data_string.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				outStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
    RadioGroup.OnCheckedChangeListener mCheckedChangeListener = new RadioGroup.OnCheckedChangeListener()
    {
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
	    	//switch(checkedId)
			switch(group.getCheckedRadioButtonId())
	    	{
	    		case R.id.radio0:
	    			select_animation_resource(0);
	    			 break;    			 
	    		case R.id.radio1:
	    			select_animation_resource(1);
	   			 	break;
	   			default:
	   				break;
	   		}
		}    	
    };
    
    class SelectAnimationThread extends Thread{   	
    	int index_animation ;
    	public SelectAnimationThread(int index)
    	{
    		index_animation = index ;
		}
    	
		public void run(){
			
					onSelectAnimation(index_animation); // op
					
					Message srt_msg = new Message();
					srt_msg.setTarget(mHandler);
					Bundle bundle = new Bundle();
					bundle.putInt(MSG_TYPE_ANI_DATA,index_animation);
					bundle.putString(MSG_TYPE,MSG_TYPE_ANI_FINISH);
					srt_msg.setData(bundle);
					srt_msg.sendToTarget();
					}
	}       
    
    public void select_animation_resource(int index)
    {
		Message msg = new Message();
		msg.setTarget(mHandler);
		Bundle bundle = new Bundle();
		bundle.putString(MSG_TYPE,MSG_TYPE_ANI);
		bundle.putInt(MSG_TYPE_ANI_DATA,index);
		msg.setData(bundle);
		msg.sendToTarget();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   
        
        mFrameLayout = (FrameLayout)findViewById(R.id.FrameLayout1);
        mRadioGroup = (RadioGroup)mFrameLayout.findViewById(R.id.radioGroup);                
        mImageView  = (ImageView)mFrameLayout.findViewById(R.id.animation);
        
    	mHandler = new Handler(){
    		public void handleMessage(Message msg)
    		{    			
    			if(msg.getData().getString(MSG_TYPE).equals(MSG_TYPE_ANI))
    			{
    				int index = msg.getData().getInt(MSG_TYPE_ANI_DATA);
    		        SelectAnimationThread mThread = null;    		        
    				switch(index)
    				{
    					case 0:
    		    			 Log.d(TAG,"onSelectAnimation(0)");
    		    			 mThread = new SelectAnimationThread(0);
    		    			 mThread.start();
    						break;
    					case 1:
    		    			Log.d(TAG,"onSelectAnimation(1)");
    		    			mThread = new SelectAnimationThread(1);
   		    			 	mThread.start();
    						break;
    					default:
    						break;
    				}    			        			
    			}
    			else if(msg.getData().getString(MSG_TYPE).equals(MSG_TYPE_ANI_FINISH))
    			{
    				int index = msg.getData().getInt(MSG_TYPE_ANI_DATA);
    				switch(index)
    				{
    					case 0:
    		    			 mImageView.setImageResource(R.drawable.uboot);
    		    			 Log.d(TAG,"onSelectAnimation(0) ok");
    						break;
    					case 1:
    		    			mImageView.setImageResource(R.drawable.uboot2);
    		    			Log.d(TAG,"onSelectAnimation(1) ok");    		    			
    						break;
    					default:
    						break;
    				}    
    			}
    	    }
    	};    	
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		String logo_index = readFile(new File(logo_file));		
		logo_index.replace('\n', ' ');
		//logo_index.replace('\r', ' ');
		Log.d(TAG,"logo_index="+logo_index);		
		if(logo_index.trim().equals("0"))
		{
			mRadioGroup.check(R.id.radio0);
			mImageView.setImageResource(R.drawable.uboot);
		}
		else
		{
			mRadioGroup.check(R.id.radio1);
			mImageView.setImageResource(R.drawable.uboot2);
		}
		mRadioGroup.setOnCheckedChangeListener(mCheckedChangeListener);
		
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
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
