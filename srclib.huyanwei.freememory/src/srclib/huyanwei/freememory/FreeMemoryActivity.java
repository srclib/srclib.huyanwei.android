package srclib.huyanwei.freememory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import android.R.string;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FreeMemoryActivity extends Activity {
    /** Called when the activity is first created. */
	public String TAG="FreeMemory";
	
	public long total_memory = 0 ;
	public long avail_memory = 0 ;
	public long used_memory = 0 ;
	
	public long last_used_memory = 0 ;
	private static int free_zise =0;
	
	private TextView total_view ;
	private TextView avail_view ;
	private TextView used_view ;
	
	private Button	 free_btn ;
	
	private Handler	 handler ; 
	
	private static final int MSG_UPDATE_VIEW = 10000;
	
	
		
    @Override    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        init();
        
        //Button.
        View.OnClickListener OnClickListener = new View.OnClickListener()
        {
			public void onClick(View v)
			{
				Log.i(TAG, ">>> onClick start");		
				
				Thread work_thread = new Thread()
				{
					public void run()
					{
						last_used_memory = used_memory ;
						
						killProcess();
						
						Message msg = handler.obtainMessage();
						msg.what = MSG_UPDATE_VIEW;
						msg.arg1 = 1;
						msg.sendToTarget();						
					}
				};
				work_thread.start();				
				Log.i(TAG, "<<< onClick end");
			}
		};

        free_btn.setOnClickListener(OnClickListener);
        
    }

    public void tip()
    {
    	free_zise =(int)(last_used_memory - used_memory );    	
    	if( free_zise > 0)
		{
    		Toast.makeText(getApplicationContext(), new String().format("已经释放%d MB 内存", free_zise),Toast.LENGTH_SHORT).show();    	
		}
    }
    
    public void init()
	{
    		
    		RelativeLayout RelativeLayout1 = (RelativeLayout)findViewById(R.id.relativeLayout1);
	        total_view = (TextView)RelativeLayout1.findViewById(R.id.textView2);
	        avail_view = (TextView)RelativeLayout1.findViewById(R.id.textView4);
	        used_view = (TextView)RelativeLayout1.findViewById(R.id.textView6);

	        RelativeLayout RelativeLayout2 = (RelativeLayout)findViewById(R.id.relativeLayout2);	        
	        free_btn   = (Button)RelativeLayout2.findViewById(R.id.button1);
	        
	        update(this);
	        
	        handler = new Handler()
	        {
	        	@Override
	    		public void handleMessage(Message msg)
	        	{
	        		switch (msg.what) 
	        		{
	        			case MSG_UPDATE_VIEW:        					        				
	        				update(null);
	        				tip();
	        				break;
	        		}
	        	}
	        };
	}
    
    public void update(Context context)
    {
        total_memory =  getTotalMemory(this);       
        total_view.setText(String.format("%d M",total_memory));
        
        avail_memory = getAvailMemory(this);
        avail_view.setText(String.format("%d M",avail_memory));    	
        
        used_memory = total_memory - avail_memory ;
        used_view.setText(String.format("%d M",used_memory));
    }
    
    public static long getTotalMemory(Context context) 
    {
        String str1 = "/proc/meminfo";// 系统内存信息文件 
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try 
        {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
            localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小 

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte 
            localBufferedReader.close();

        } catch (IOException e) {
        }
        //return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化 
        return initial_memory/(1024*1024);
    }
    
    public static long getAvailMemory(Context context) 
    {
        // 获取android当前可用内存大小 
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存 

        //return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化 
        return mi.availMem/(1024*1024);
    }
    
    public void killProcess() 
    {
    	ActivityManager activityManger=(ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list=activityManger.getRunningAppProcesses();
        if(list!=null)
        for(int i=0;i<list.size();i++)
        {
            ActivityManager.RunningAppProcessInfo apinfo=list.get(i);
            
            //System.out.println("pid            "+apinfo.pid);
            //System.out.println("processName    "+apinfo.processName);
            //System.out.println("importance     "+apinfo.importance);
            Log.v(TAG,"pid="+apinfo.pid);
            Log.v(TAG,"processName="+apinfo.processName);
            Log.v(TAG,"importance="+apinfo.importance);
            String[] pkgList=apinfo.pkgList;
            
            if(apinfo.importance>ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE)
            {
               // Process.killProcess(apinfo.pid);
                for(int j=0;j<pkgList.length;j++)
                {
                	Log.v(TAG,"pid="+apinfo.pid);
                    //2.2以上是过时的,请用killBackgroundProcesses代替                	
                    //activityManger.restartPackage(pkgList[j]);
                    activityManger.killBackgroundProcesses(pkgList[j]);
                    
                } 
            }
        }    
    }
}


