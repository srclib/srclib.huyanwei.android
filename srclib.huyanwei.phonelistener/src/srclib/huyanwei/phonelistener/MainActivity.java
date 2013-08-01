package srclib.huyanwei.phonelistener;

import java.util.Locale;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

	private String TAG = "srclib.huyanwei.phonelistener.MainActivity";
	
	private Context 	mContext;
	
	private SlideButton mSlideButton;
	
	private ImageButton mImageButton;
	
	private LinearLayout mlinearLayout;
	
	private	SQLiteDatabase mDatabase;
	
	private int config_proximity_sensor_enable 	= 0;
	private int config_action 					= 0;
	private int config_speaker 					= 0;

	private int query_config_value(String name)
	{
		ContentResolver mContentResolver = mContext.getContentResolver();
		
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
	
	private int update_config_value(String name,int value)
	{
		ContentResolver mContentResolver = mContext.getContentResolver();

		final String TABLE_FILED_ID 	= ConfigContentProvider.TABLE_FIELD_ID;
		final String TABLE_FILED_NAME 	= ConfigContentProvider.TABLE_FIELD_NAME;
		final String TABLE_FILED_VALUE 	= ConfigContentProvider.TABLE_FIELD_VALUE;
        final Uri uri = ConfigContentProvider.CONTENT_URI;

        final ContentValues values = new ContentValues();
        //values.put(TABLE_FILED_NAME,name);
        values.put(TABLE_FILED_VALUE,value); // only update value.
        
        int c = mContentResolver.update(uri,values,TABLE_FILED_NAME+"=?",new String[]{name});
        
        return c;
	}
	
	private SlideButton.OnSwitchChangedListener mOnSwitchChangedListener = new SlideButton.OnSwitchChangedListener()
	{
		@Override
		public void onSwitchChanged(SlideButton obj, boolean status) 
		{		
			// TODO Auto-generated method stub
			switch(obj.getId())
			{
				case R.id.SlideButton1:
					if(status)
					{
						Log.d(TAG,"SlideButton.onSwitchChanged() true");
						
						//mImageButton.setVisibility(View.VISIBLE);
						
						update_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ENABLE,1);
					}
					else
					{
						Log.d(TAG,"SlideButton.onSwitchChanged() false");
						
						//mImageButton.setVisibility(View.INVISIBLE);
						
						update_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ENABLE,0);
						
						/*
						Intent svc = new Intent(mContext, PhoneListenerService.class);
						mContext.stopService(svc);
						*/
					}				
					break;
				//case R.id.SlideButton2:
				//	break;
				default:
					break;
			}
		}
	};
	
	private OnClickListener mOnClickListener =  new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.imageButton1:
					 Log.d(TAG,"huyanwei start service by manual.");
					 Intent svc = new Intent(mContext, PhoneListenerService.class);
					 mContext.startService(svc);
					break;
			}
		}
	};
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        mContext= this;
        
        mlinearLayout = (LinearLayout)findViewById(R.id.linearLayout3);
        
        mSlideButton = (SlideButton) mlinearLayout.findViewById(R.id.SlideButton1);     
        mSlideButton.setOnSwitchChangedListener(mOnSwitchChangedListener);
        
        mImageButton = (ImageButton) mlinearLayout.findViewById(R.id.imageButton1);    
        
        mImageButton.setOnClickListener(mOnClickListener);
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	public void update_controls_state()
	{
		mSlideButton.setValue((config_proximity_sensor_enable >=1)?true:false);
		//mSlideButton.setValue(false);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		config_proximity_sensor_enable 	= query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ENABLE);
		config_action 					= query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ACTION);
		config_speaker 					= query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_SPEAKER);
		
		update_controls_state();
				
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
}
