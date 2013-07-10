package srclib.huyanwei.notifycation;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.support.v4.app.NavUtils;

import android.content.BroadcastReceiver;

public class Notifycation extends Activity {

	private String TAG = "srclib.huyanwei.notification"; 
	
	private Button btn_gen ; 
	private Button btn_clear ;	
	private Button btn_update ;	
	
	private final int NOTIFY_ID = 1;
	
	private Intent mClickIntent;
	private PendingIntent mPendingClickIntent;	

	private Intent mClearIntent;
	private PendingIntent mPendingClearIntent;		
	
	private Notification mNotification;
	private NotificationManager mNotificationManager;
	
	private final boolean m_allow_clear = false ;
	private final boolean m_allow_remoteview = true ;
	
	private RemoteViews mRemoteViews ;
	
	private int process_bar_value = 0 ;
	
	private boolean debug = false;
	
	public String ACTION_INC = "SRCLIB_INC";
	public String ACTION_DEC = "SRCLIB_DEC";
	public String ACTION_AUT = "SRCLIB_AUT";
		
    private final BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(ACTION_INC))
                {
                	update_remote_view_progress_bar(true,5);
                    Log.d(TAG,"huyanwei debug receive Broadcast ACTION_INC.");
                }
                else if (action.equals(ACTION_DEC))
                {
                	update_remote_view_progress_bar(false,5);
                	Log.d(TAG,"huyanwei debug receive Broadcast ACTION_DEC.");
                }
                else if (action.equals(ACTION_AUT))
                {
                	Log.d(TAG,"huyanwei debug receive Broadcast ACTION_AUT.");
                	update_remote_view();
                }	
        }
    };
	
		
	private View.OnClickListener btn_OnClickListener = new View.OnClickListener() 
	{
		public void onClick(View arg0) {
			switch(arg0.getId())
			{
				case R.id.notify_button_gen:
					generate_notification();					
					break;
				case R.id.notify_button_clear:
					clear_notification();
					break;
				case R.id.update_notify_remote_view:
					update_remote_view();
					break;
				default:
					break;			
			}
		}		
	};
		
	Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub        
        	if(debug) 
        		Log.d(TAG,"huyanwei receive msg.arg1="+msg.arg1);
        	
        	// min/max value check .
            if ((msg.arg1 < 0)) 
            {
            	process_bar_value = 0 ;
            }
            if((msg.arg1 > 100))
            {
            	process_bar_value = 100 ;
            }
            
        	mNotification.contentView.setProgressBar(R.id.progressBar1, 100, msg.arg1,
                    false);
        	mNotificationManager.notify(NOTIFY_ID, mNotification);
        	/*	
            if (msg.arg1 >= 100) {
            	mNotificationManager.cancel(NOTIFY_ID); // 完成后消失.
            }
            */
            super.handleMessage(msg);
        }
    };
	
	private void generate_notification()
	{
		mClickIntent = new Intent(Intent.ACTION_VIEW);
		mClickIntent.setData(Uri.parse("http://www.baidu.com"));
		mClickIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);		
        //主要是设置点击通知时显示内容的类
        mPendingClickIntent = PendingIntent.getActivity(this, 0, mClickIntent, 0);
	        
        mClearIntent = new Intent(Intent.ACTION_VIEW);
        mClearIntent.setData(Uri.parse("http://www.google.com"));
        mClearIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
        //主要是设置清除所有通知时显示内容的类		
        PendingIntent mPendingClearIntent = PendingIntent.getActivity(this, 0, mClearIntent, 0);
        
		mNotification = new Notification();
		
		//设置通知在状态栏显示的图标
        mNotification.when = System.currentTimeMillis(); 
        
		mNotification.icon = R.drawable.notify;
		
		mNotification.tickerText = this.getString(R.string.notification_brief);
		
		/*
		mNotification.defaults =  Notification.DEFAULT_SOUND 		    //通知时默认的声音     
								| Notification.DEFAULT_VIBRATE 			//通知时震动
								| Notification.DEFAULT_LIGHTS;          //通知时亮屏
		*/
		
		//mNotification.vibrate = new long[] { 0, 700, 500, 1000 };		
		
		//mNotification.flags		|= 	Notification.FLAG_ONLY_ALERT_ONCE;		
		//mNotification.flags 	|= 	Notification.FLAG_INSISTENT ;    	//通知的音乐效果一直播放
		
		if( !m_allow_clear )
		{
			mNotification.flags |= 	Notification.FLAG_NO_CLEAR; 		// Notification.FLAG_AUTO_CANCEL		
			mNotification.flags |= 	Notification.FLAG_ONGOING_EVENT;    // "正在进行时" 还是 "通知"
		}
		else
		{		
			//由于上面是 禁止 清除，所以，下面的delete可能无法调用
			mNotification.deleteIntent = mPendingClearIntent; // 通知被 “systemUI -> clear all” 清除时.
		}
		
        if(m_allow_remoteview)
        {
        	Intent mRemoteIntent;
        	PendingIntent mRemoteViewIntent;
        	
        	mRemoteViews = new RemoteViews(this.getApplication().getPackageName(), R.layout.notification_remote_views);
        	mRemoteViews.setProgressBar(R.id.progressBar1, 100, 0, false);

        	/*
        	mRemoteIntent = new Intent(Intent.ACTION_VIEW);
        	mRemoteIntent.setData(Uri.parse("http://www.baidu.com"));
        	mRemoteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	mRemoteViewIntent = PendingIntent.getActivity(this, 0, mRemoteIntent, 0);
        	*/
        	mRemoteIntent = new Intent(ACTION_DEC);
        	mRemoteViewIntent = PendingIntent.getBroadcast(this, 0, mRemoteIntent, 0);
        	mRemoteViews.setOnClickPendingIntent(R.id.button1, mRemoteViewIntent);

        	mRemoteIntent = new Intent(ACTION_AUT);
        	mRemoteViewIntent = PendingIntent.getBroadcast(this, 0, mRemoteIntent, 0);
        	mRemoteViews.setOnClickPendingIntent(R.id.button2, mRemoteViewIntent);
        	
        	/*
        	mRemoteIntent = new Intent(Intent.ACTION_VIEW);
        	mRemoteIntent.setData(Uri.parse("http://www.google.com"));
        	mRemoteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	mRemoteViewIntent = PendingIntent.getActivity(this, 0, mRemoteIntent, 0);
        	*/
        	mRemoteIntent = new Intent(ACTION_INC);
        	mRemoteViewIntent = PendingIntent.getBroadcast(this, 0, mRemoteIntent, 0);
        	mRemoteViews.setOnClickPendingIntent(R.id.button3, mRemoteViewIntent);
        	
        	mNotification.contentView = mRemoteViews;        	
        	mNotification.contentIntent = mPendingClickIntent;
        	
        	//mNotification.contentViewTouchHandle = 1 ;        	
        }
        else
        {
			//设置通知显示的参数.
			mNotification.setLatestEventInfo(this, 
				this.getString(R.string.notification_title), 
				this.getString(R.string.notification_context),
				mPendingClickIntent);
        }
		//可以理解为执行这个通知
		mNotificationManager.notify(NOTIFY_ID, mNotification);
	}
	
	private void clear_notification()
	{
		mNotificationManager.cancel(NOTIFY_ID);
	}
	
	
	class remote_view_runable implements Runnable
	{
		boolean inc ;
		int     delta ;
		
		remote_view_runable(boolean inc_value , int delta_value)
		{
			inc = inc_value;
			delta = delta_value ;
		}
		
		public void run()
		{
			// TODO Auto-generated method stub
			if(inc)
    		{
    			process_bar_value += delta;
    		}
    		else
    		{
    			process_bar_value -= delta;
    		}
            Message msg = mHandler.obtainMessage();
            msg.arg1 = process_bar_value;
            msg.sendToTarget();
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
	}
	
    private void update_remote_view_progress_bar(boolean inc , int delta)
    {
		// valid object check.
		if (mNotification == null )
			return ;		
		Thread mThread = new Thread(new remote_view_runable(inc,delta));		
		mThread.start();
    }
	
	private void update_remote_view()
	{
		// valid object check.
		if (mNotification == null )
			return ;
		
		Thread mThread = new Thread(new Runnable() {
            public void run() {   
            	process_bar_value = 0 ;
                while (true) {
                	process_bar_value += 5;
                    Message msg = mHandler.obtainMessage();
                    msg.arg1 = process_bar_value;
                    msg.sendToTarget();
                    
                    if(debug) 
                    	Log.d(TAG,"huyanwei debug process_bar_value="+process_bar_value);                    
                    
                    if(process_bar_value >= 100)
                    	break;
                    
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
		
		mThread.start();		
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifycation);        
        
        btn_gen = (Button)findViewById(R.id.notify_button_gen);
        btn_gen.setOnClickListener(btn_OnClickListener);        
        
        btn_clear = (Button)findViewById(R.id.notify_button_clear);
        btn_clear.setOnClickListener(btn_OnClickListener);
        
        btn_update = (Button)findViewById(R.id.update_notify_remote_view);
        btn_update.setOnClickListener(btn_OnClickListener);
        
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        
        // broadcast 
        IntentFilter mNotificationReceiverFilter = new IntentFilter();
        mNotificationReceiverFilter.addAction(ACTION_INC);
        mNotificationReceiverFilter.addAction(ACTION_DEC);
        mNotificationReceiverFilter.addAction(ACTION_AUT);
        this.registerReceiver(mNotificationReceiver,mNotificationReceiverFilter);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notifycation, menu);
        return true;
    }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		clear_notification(); // 清楚所有的通知.
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub		
		this.unregisterReceiver(mNotificationReceiver);
		super.onDestroy();
	}

}
