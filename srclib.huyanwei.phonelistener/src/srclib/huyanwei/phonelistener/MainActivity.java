package srclib.huyanwei.phonelistener;

import java.util.ArrayList;
import java.util.Locale;

import srclib.huyanwei.phonelistener.SlideButton.OnSwitchChangedListener;
import srclib.huyanwei.phonelistener.SlideButton;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

	private String TAG = "srclib.huyanwei.phonelistener.MainActivity";
	
	private boolean DBG = false;
	
	private Context 	mContext;

	private LayoutInflater mLayoutInflater;
	private LinearLayout   mFooterView;
			
	private SlideButton  mSlideButton;
	private ImageButton  mImageButton;	
	private LinearLayout mlinearLayout;
	private ListView     mListView;

	private final int    mSlideButtonIdBase    = 1000;
	private ListItemAdapter mListItemAdapter ;
	
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
		//@Override
		public void onSwitchChanged(SlideButton obj, boolean status) 
		{		
			// TODO Auto-generated method stub

			switch(obj.getId())
			{
				case (mSlideButtonIdBase+0):
					if(status)
					{
						if(DBG)
						{
							Log.d(TAG,"SlideButton.onSwitchChanged(CONFIG_ENABLE) true");
						}
						update_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ENABLE,1);
					}
					else
					{
						if(DBG)
						{
							Log.d(TAG,"SlideButton.onSwitchChanged(CONFIG_ENABLE) false");
						}
						update_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ENABLE,0);			
					}				
					break;
				case (mSlideButtonIdBase+1):
					if(status)
					{
						if(DBG)
						{
							Log.d(TAG,"SlideButton.onSwitchChanged(CONFIG_ACTION) true");
						}
						update_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ACTION,1);
					}
					else
					{
						if(DBG)
						{
							Log.d(TAG,"SlideButton.onSwitchChanged(CONFIG_ACTION) false");
						}
						update_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ACTION,0);			
					}
					break;
				case (mSlideButtonIdBase+2):
					if(status)
					{
						if(DBG)
						{
							Log.d(TAG,"SlideButton.onSwitchChanged(CONFIG_SPEAKER) true");
						}
						update_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_SPEAKER,1);
					}
					else
					{
						if(DBG)
						{
							Log.d(TAG,"SlideButton.onSwitchChanged(CONFIG_SPEAKER) false");
						}
						update_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_SPEAKER,0);			
					}	
					break;
				default:
					break;
			}
		}
	};
	
	@SuppressWarnings("unused")
	private OnClickListener mOnClickListener =  new OnClickListener()
	{
		//@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.btn_start_service:
					 if(DBG)
					 {
						Log.d(TAG,"huyanwei start service by manual.");
					 }
					 Intent svc = new Intent(mContext, PhoneListenerService.class);
					 mContext.startService(svc);
					break;
			}
		}
	};
	
	public final class ListItemFuction
	{
		public String   switch_name		;
		public String   switch_off_str  ;
		public String   switch_on_str   ;
		public boolean  switch_value   	;
		public SlideButton.OnSwitchChangedListener OnSwitchChangedListener;		
	}
	
	private class ListItemAdapter extends BaseAdapter
	{
		
		private ArrayList<ListItemFuction> mListItemFuction = new ArrayList<ListItemFuction>();
		
		private Context mContext; 
		
		private int mCount ;
		
		public ListItemAdapter(Context c)
		{
			mContext = c;
			mCount = 0 ;
		}
		
		public void addOneItem(ListItemFuction obj)
		{
			mListItemFuction.add(mCount++,obj);
			//mCount++;			
			this.notifyDataSetChanged();
		}
		
		//@Override
		public int getCount() {
			// TODO Auto-generated method stub
			//if(DBG)
			//{
			//	Log.d(TAG,"getCount()");
			//}
			return mCount;
		}

		//@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if(DBG)
			{
				Log.d(TAG,"getItem("+position+")");
			}
			return position;
		}

		//@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			if(DBG)
			{
				Log.d(TAG,"getItemId("+position+")");
			}
			return position;
		}

		//@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(DBG)
			{
				Log.d(TAG,"getView("+position+")");
			}
			
			LinearLayout ll;
			// 每次都加载，不让重复使用View
            //if (convertView == null) {
            ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
            //} else {
            //	ll = (LinearLayout) convertView;
            //}
            
            int first = mListView.getFirstVisiblePosition(); // 在总View的index
            int last  = mListView.getLastVisiblePosition(); // 在总View的index
            int child_count = mListView.getChildCount(); // 可见的view数目
            int count = mListView.getCount();     		 // 总view数目
            int header_count = ((ListView) mListView).getHeaderViewsCount();
            int footer_count = ((ListView) mListView).getFooterViewsCount();

            if(DBG)
            {
            	Log.d(TAG,"["+first+"-"+last+"]/["+child_count+"-"+count+"]["+header_count+"/"+footer_count+"]");
            }
            
            // header ,footer 不用背景
            if((count - header_count - footer_count) ==1)
            {
            	// Only One Item
            	ll.setBackgroundResource(R.drawable.v5_preference_item_single_bg);
            }
            else if(position == 0)
            {
            	// first
            	ll.setBackgroundResource(R.drawable.v5_preference_item_first_bg);
            }
            else if(position == (count-1-header_count-footer_count))
            {
            	// last
            	ll.setBackgroundResource(R.drawable.v5_preference_item_last_bg);
            }
            else
            {
            	// moddile
            	ll.setBackgroundResource(R.drawable.v5_preference_item_middle_bg);
            }
            
            ListItemFuction local_item = mListItemFuction.get(position);            

            // switch name
            TextView mTextView = (TextView) ll.findViewById(R.id.switch_name);  
           	mTextView.setText(local_item.switch_name);
           	
           	// switch button
           	SlideButton mSlideButton = (SlideButton) ll.findViewById(R.id.SlideButton);
           	mSlideButton.setSwitchOffText(local_item.switch_off_str);
           	mSlideButton.setSwitchOnText(local_item.switch_on_str);
           	mSlideButton.setValue(local_item.switch_value);           	
           	mSlideButton.setOnSwitchChangedListener(local_item.OnSwitchChangedListener);
           	mSlideButton.setId(mSlideButtonIdBase+position); // 注意这里已经改变了他的ID
           	
            return ll;
		}

		@Override
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			if(DBG)
			{
				Log.d(TAG,"isEnabled("+position+")");
			}
			return super.isEnabled(position);
		}
	};
	
	private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener()
	{
		//@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
				// TODO Auto-generated method stub
				if(DBG)
				{
					// arg0 -> AdapterView , arg1 -> item view , arg2 -> count index , arg3 -> origin index
					Log.d(TAG,"onItemClick("+arg0+","+arg1+","+arg2+","+arg3+")");
				}
                int first = arg0.getFirstVisiblePosition();
                int last  = arg0.getLastVisiblePosition();
                int child_count = arg0.getChildCount();
                int count = arg0.getCount();
                int header_count = ((ListView) arg0).getHeaderViewsCount();
                int footer_count = ((ListView) arg0).getFooterViewsCount();
                
                if(DBG)
                {
                	Log.d(TAG,"["+first+"-"+last+"/"+count+"]["+header_count+"/"+footer_count+"]");
                }
                if((arg2 > (header_count-1)) && (arg2 < (count-footer_count))) // index calc
				{
                	
                	//TextView    local_nm = (TextView) ((LinearLayout)arg1).findViewById(R.id.switch_name);
                	//local_nm.setText(local_nm.getText()+" is clicked.");
                	
                	// id alread have been modified.
                	//SlideButton local_sb = (SlideButton)((LinearLayout)arg1).findViewById(R.id.SlideButton);
                	SlideButton local_sb = (SlideButton)((LinearLayout)arg1).findViewById(mSlideButtonIdBase+(int)arg3);
                	
                	if(local_sb != null)
                	{
                		local_sb.switchValue();
                	}
                	else
                	{
                		Log.d(TAG,"SlideButton is null ");
                	}
				}
                else
				{
					Toast.makeText(mContext, "mFloatView("+arg2+") is clicked!", Toast.LENGTH_SHORT).show();
				}
		}
	};
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        mContext= this;
        
        mLayoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFooterView = 	(LinearLayout)mLayoutInflater.inflate(R.layout.footer, null);
        
        mImageButton = (ImageButton) mFooterView.findViewById(R.id.btn_start_service);
        mImageButton.setOnClickListener(mOnClickListener);
        
        mlinearLayout = (LinearLayout)findViewById(R.id.LinearLayout);        
        mListView = (ListView) mlinearLayout.findViewById(R.id.listView);        
        mListView.setOnItemClickListener(mOnItemClickListener);
        mListItemAdapter = new ListItemAdapter(this);
        
        mListView.addHeaderView(mFooterView);
        //mListView.addFooterView(mFooterView);
        
        mListView.setAdapter(mListItemAdapter);
        
        // view database
		config_proximity_sensor_enable 	= query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ENABLE);
		config_action 					= query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ACTION);
		config_speaker 					= query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_SPEAKER);
        
		Resources mResources = mContext.getResources();
		
        // add 1st Item
		ListItemFuction mListItemFuction = new ListItemFuction();
		mListItemFuction.switch_name 	 = mResources.getString(R.string.proximity_sensor_enable_str);
		mListItemFuction.switch_off_str  = mResources.getString(R.string.proximity_sensor_off_str);
		mListItemFuction.switch_on_str   = mResources.getString(R.string.proximity_sensor_on_str);
		mListItemFuction.switch_value  	 = (config_proximity_sensor_enable>=1)?true:false;
		mListItemFuction.OnSwitchChangedListener = mOnSwitchChangedListener;
		mListItemAdapter.addOneItem(mListItemFuction);
        
        // add 2nd Item
		mListItemFuction = new ListItemFuction();
		mListItemFuction.switch_name 	 = mResources.getString(R.string.default_action_str);
		mListItemFuction.switch_off_str  = mResources.getString(R.string.default_action_off_str);
		mListItemFuction.switch_on_str   = mResources.getString(R.string.default_action_on_str);
		mListItemFuction.switch_value  	 = (config_action>=1)?true:false ;
		mListItemFuction.OnSwitchChangedListener = mOnSwitchChangedListener;
		mListItemAdapter.addOneItem(mListItemFuction);
        
        // add 3rd Item
		mListItemFuction = new ListItemFuction();
		mListItemFuction.switch_name 	 = mResources.getString(R.string.speaker_state_str);
		mListItemFuction.switch_off_str  = mResources.getString(R.string.speaker_state_off_str);
		mListItemFuction.switch_on_str   = mResources.getString(R.string.speaker_state_on_str);
		mListItemFuction.switch_value  	 = (config_speaker>=1)?true:false ;
		mListItemFuction.OnSwitchChangedListener = mOnSwitchChangedListener;
		mListItemAdapter.addOneItem(mListItemFuction);
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
		//mSlideButton.setValue((config_proximity_sensor_enable >=1)?true:false);
		//mSlideButton.setValue(false);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
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
