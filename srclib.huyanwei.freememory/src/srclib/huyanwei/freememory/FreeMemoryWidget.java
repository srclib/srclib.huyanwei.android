package srclib.huyanwei.freememory;

import java.util.List;

import android.content.Context;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.RemoteViews;
import android.content.ComponentName;
import android.widget.Toast;

public class FreeMemoryWidget extends AppWidgetProvider
{
	private  static final String ACTIVITY_SERVICE = "activity";
	private static final String TAG = "FreeMemory" ;
	private static final String CLICK_NAME_ACTION = "com.srclib.action.widget.click";
	private static RemoteViews rv;
	
	
	public long total_memory = 0 ;
	public long avail_memory = 0 ;
	public long used_memory = 0 ;
	
	public long last_used_memory = 0 ;
	private static int free_zise =0;
	
	private Handler	 handler ; 
	private static final int MSG_UPDATE_VIEW = 10000;
	
	private Context widget_context;

    public void tip(Context context)
    {
    	free_zise =(int)(last_used_memory - used_memory );    	
    	//if(free_zise > 0)
		{
			Toast toast = Toast.makeText(context, new String().format("�Ѿ��ͷ�%d MB �ڴ�", free_zise),Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			
			Log.v(TAG,"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		}
    }
    
    public void killProcess(Context context) 
    {
    	ActivityManager activityManger=(ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list=activityManger.getRunningAppProcesses();
        if(list!=null)
        {	
        	 Log.v(TAG,"list.size()=" + list.size());
        	 
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
	            
	            if(apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE)
	            {
	               // Process.killProcess(apinfo.pid);
	            	
	            	Log.v(TAG,"pkgList.length =" + pkgList.length);
	                for(int j=0;j<pkgList.length;j++)
	                {	
	                	Log.v(TAG,"kill pkgList[j]="+pkgList[j]);
	                	
	    	            if(pkgList[j] == "com.srclib.freememory")
	    	            {
	    	            	//skip myself .	    	            	
	    	            	continue;
	    	            }
	    	            
	                    //2.2�����ǹ�ʱ��,����killBackgroundProcesses����                	
	                    //activityManger.restartPackage(pkgList[j]);
	                    activityManger.killBackgroundProcesses(pkgList[j]);
	                    
	                } 
	            }
	        }
        }
    }
    
  //ÿ�θ��¶�����һ�θ÷�����ʹ��Ƶ��
	@Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds)
	{
		Log.v(TAG,"++++++++++++++ onUpdate()");

		widget_context = context ;

		final int N = appWidgetIds.length;
        Log.v(TAG,"appWidgetIds.length = " + appWidgetIds.length );
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }		
        Log.v(TAG,"------------- onUpdate()");
	}

	
	public static void updateAppWidget(Context context,
        AppWidgetManager appWidgeManger, int appWidgetId)
	{
		Log.v(TAG,"+++++++++++++ updateAppWidget()");		
	    rv = new RemoteViews(context.getPackageName(), R.layout.dsk);
	    Intent intentClick = new Intent(CLICK_NAME_ACTION);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,intentClick, 0);
	    rv.setOnClickPendingIntent(R.id.clear, pendingIntent);
	    appWidgeManger.updateAppWidget(appWidgetId, rv);	    
		Log.v(TAG,"------------- updateAppWidget()");
	}
	
	//ÿ����һ�ι㲥��Ϣ�͵���һ�Σ�ʹ��Ƶ��
	   @Override
	    public void onReceive(final Context context, Intent intent) {
	        // TODO Auto-generated method stub
		   Log.v(TAG,"intent.getAction()=" + intent.getAction());
		   Log.v(TAG,"++++++++++++++ onReceive()");
		   
	        
	        synchronized (intent)
			{
		        if (rv == null) {
		            rv = new RemoteViews(context.getPackageName(), R.layout.dsk);
		        }

	        if (intent.getAction().equals(CLICK_NAME_ACTION))
	        {
	        	Log.v(TAG,"============ onReceive(CLICK_NAME_ACTION)");
	        	
	        	total_memory = FreeMemoryActivity.getTotalMemory(context);
	        	avail_memory = FreeMemoryActivity.getAvailMemory(context);
	        	used_memory = total_memory - avail_memory ;
	        	
	        	Log.v(TAG,"============ onReceive(CLICK_NAME_ACTION)");
	        	
	    	    handler = new Handler()
	    	    {
	    	    	@Override
	    			public void handleMessage(Message msg)
	    	    	{
	    	    		switch (msg.what) 
	    	    		{
	    	    			case MSG_UPDATE_VIEW:      
	    	    				Log.v(TAG,"Handler.handleMessage(MSG_UPDATE_VIEW)");
	    	    				tip(context);
	    	    				break;
	    	    		}
	    	    	}
	    	    };
	        	
	        	Thread work_thread = new Thread()
				{
					public void run()
					{
						Log.v(TAG,">>>>>> Thread.run()");
						last_used_memory = used_memory ;					
						
						try
						{
							Log.v(TAG,"before killProcess");							
							killProcess(context);							
							Log.v(TAG,"after killProcess");
							
				        	total_memory = FreeMemoryActivity.getTotalMemory(context);
				        	avail_memory = FreeMemoryActivity.getAvailMemory(context);
				        	used_memory = total_memory - avail_memory ; 
				        	
							Message msg = handler.obtainMessage();
							msg.what = MSG_UPDATE_VIEW;
							msg.arg1 = 1;
							msg.sendToTarget();

						}catch ( Exception excep)
						{
							Log.v(TAG,"excep=" + excep);
						}finally {
							Log.v(TAG,"Thread.end");
						}
						Log.v(TAG,"<<<<<< Thread.run()");
					}
				};
				work_thread.start();	

				AppWidgetManager appWidgetManger = AppWidgetManager
		                .getInstance(context);
		        int[] appIds = appWidgetManger.getAppWidgetIds(new ComponentName(
		                context, FreeMemoryWidget.class));
		        appWidgetManger.updateAppWidget(appIds, rv);
	        }
	        else
	        {
	        	Log.v(TAG,">>>>>>>>>>>>>>> super.onReceive()");
	        	super.onReceive(context, intent);
	        	Log.v(TAG,"<<<<<<<<<<<<<<< super.onReceive()");
	        }
			}	        	           
	        Log.v(TAG,"------------------ onReceive()");
	    }


	//ÿɾ��һ���͵���һ��
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		Log.v(TAG,"++++++++++++ onDeleted()");
		super.onDeleted(context, appWidgetIds);
		Log.v(TAG,"------------ onDeleted()");
	}


	//�����һ����Widgetɾ���ǵ��ø÷�����ע�������һ�� 
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		Log.v(TAG,"+++++++++++ onDisabled()");
		super.onDisabled(context);
		Log.v(TAG,"----------- onDisabled()");
	}


	//����Widget��һ����ӵ������ǵ��ø÷���������Ӷ�ε�ֻ��һ�ε���
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		Log.v("TAG","+++++++++++++  onEnabled()");
		super.onEnabled(context);
		Log.v("TAG","------------ onEnabled()");
	}		   
	   
}


