package srclib.huyanwei.phonelistener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import srclib.huyanwei.phonelistener.SlideButton.OnSwitchChangedListener;
import srclib.huyanwei.phonelistener.SlideButton;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

	private String TAG = "srclib.huyanwei.phonelistener.MainActivity";
	
	private boolean DBG = false;
	
	private Context 	mContext;
	private Resources 	mResources;
	
	private ActivityManager mActivityManager;
	private boolean  		mserver_is_running = false ;
	
	private LayoutInflater mLayoutInflater;	
	private LinearLayout   mHeaderView;
	private LinearLayout   mFooterView;
			
	private SlideButton  mSlideButton;
	private ImageButton  mImageButton;	
	private LinearLayout mlinearLayout;
	private ListView     mListView;
	private SeekBar      mSeekBar;	
	private TextView 	 mTextView;
	
	private int          mSeekBarValue ;

	private final int    mSlideButtonIdBase    					= 1000;
	private final int    mSlideButtonIdOffsetProximityEnable    = 0;
	private final int    mSlideButtonIdOffsetAction  	  		= 1;
	private final int    mSlideButtonIdOffsetAudioRecord		= 2;
	private final int    mSlideButtonIdOffsetSpeaker	  		= 3;
	private final int    mSlideButtonIdOffsetLightEnable  		= 4;
	private final int    mSlideButtonIdOffsetLightThreshold		= 5;
	
	private final boolean mSlideButtonRecognitionByTag  		= true;  // 否则的话，就用Id 来识别.
	
	private ListItemAdapter mListItemAdapter ;
	
	private	SQLiteDatabase mDatabase;
	
	private final int MSG_UPDATE_ANGLE  = 100;
	
	private Handler  mHandler  = new  Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch (msg.what)
			{
				case MSG_UPDATE_ANGLE:
					//Log.d(TAG,"msg.arg1="+msg.arg1);
					rotate_image_button_view(msg.arg1);
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	public void rotate_image_button_view(int angle)
	{
		final Bitmap msrc=BitmapFactory.decodeResource(mContext.getResources(),(R.drawable.setting));
		final int wp=msrc.getWidth();
		final int hp=msrc.getHeight();		
	
		//Log.d(TAG,"wp = "+wp + "hp="+hp);
		
		//创建操作图片是用的matrix对象
		Matrix matrix=new Matrix();
		//缩放图片动作
		matrix.postScale(1.0f, 1.0f);
				
		//旋转图片动作
		matrix.postRotate(angle);
		//创建新图片
		Bitmap resizedBitmap=Bitmap.createBitmap(msrc,0,0,wp,hp,matrix,true);
		//将上面创建的bitmap转换成drawable对象，使其可以使用在ImageView,ImageButton中
		BitmapDrawable bmd=new BitmapDrawable(resizedBitmap);
		//mImageButton.setAdjustViewBounds(true);
		mImageButton.setImageDrawable(bmd);
		
	}

	private int config_proximity_sensor_enable 	= 0;
	private int config_action 					= 0;
	private int config_speaker 					= 0;
	private int config_light_sensor_enable		= 0;
	private int config_light_sensor_threshold	= 0;
	private int config_audio_record				= 0;

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
			
			int mRecognition = 0;
			
			if(mSlideButtonRecognitionByTag)
			{
				mRecognition = (Integer) obj.getTag();
			}
			else
			{	
				mRecognition = (Integer) obj.getId();
			}			

			switch(mRecognition)
			{
				case (mSlideButtonIdBase+mSlideButtonIdOffsetProximityEnable): //proximity sensor
					if(DBG)
					{
						Log.d(TAG,"SlideButton.onSwitchChanged(CONFIG_ENABLE) "+ status);
					}
					config_proximity_sensor_enable = (status?1:0);
					update_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ENABLE,config_proximity_sensor_enable);
					update_list_view_ui();
					break;
				case (mSlideButtonIdBase+mSlideButtonIdOffsetAction): // Action
					if(DBG)
					{
						Log.d(TAG,"SlideButton.onSwitchChanged(CONFIG_ACTION) "+status);
					}
					config_action = (status?1:0);
					update_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ACTION,config_action);
					update_list_view_ui();
					break;
				case (mSlideButtonIdBase+mSlideButtonIdOffsetAudioRecord): // Action
					if(DBG)
					{
						Log.d(TAG,"SlideButton.onSwitchChanged(CONFIG_AUDIO_RECORD) "+status);
					}
					config_audio_record = (status?1:0);
					update_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_AUDIO_RECORD,config_audio_record);
					//update_list_view_ui();
					break;
				case (mSlideButtonIdBase+mSlideButtonIdOffsetSpeaker): // speaker
					if(DBG)
					{
						Log.d(TAG,"SlideButton.onSwitchChanged(CONFIG_SPEAKER) "+status);
					}
					config_speaker = (status?1:0);
					update_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_SPEAKER,config_speaker);
					//update_list_view_ui();
					break;
				case (mSlideButtonIdBase+mSlideButtonIdOffsetLightEnable):   // light sensor 
					if(DBG)
					{
						Log.d(TAG,"SlideButton.onSwitchChanged(CONFIG_LIGHT_SENSOR_ENABLE) "+status);
					}
					config_light_sensor_enable = (status?1:0);
					update_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_LIGHT_SENSOR_ENABLE,config_light_sensor_enable);
					update_list_view_ui();
					break;
				default:
					break;
			}
		}
	};
	
	private void update_list_view_ui()
	{
		updateListItemAdapterData();
		
		// 下面的可以更新 Visual,但是不能更新 Value. 所以采用重新添加的这种方式 -> updateListItemAdapterData()	
		/*
		if(config_proximity_sensor_enable == 0)
		{
			
			mListItemAdapter.setItemVisual((mSlideButtonIdBase+mSlideButtonIdOffsetAction),  false);
			mListItemAdapter.setItemVisual((mSlideButtonIdBase+mSlideButtonIdOffsetSpeaker), false);
			mListItemAdapter.setItemVisual((mSlideButtonIdBase+mSlideButtonIdOffsetLightEnable), false);
			
			
			mFooterView.setVisibility(View.INVISIBLE);
			
		}
		else
		{
			
			mListItemAdapter.setItemVisual((mSlideButtonIdBase+mSlideButtonIdOffsetAction), true);
			
			if(config_action == 0)
			{
				mListItemAdapter.setItemVisual((mSlideButtonIdBase+mSlideButtonIdOffsetSpeaker), false);
			}
			else
			{
				mListItemAdapter.setItemVisual((mSlideButtonIdBase+mSlideButtonIdOffsetSpeaker),  true);
			}
			
			mListItemAdapter.setItemVisual((mSlideButtonIdBase+mSlideButtonIdOffsetLightEnable),  true);
			
			if(config_light_sensor_enable == 0)
			{
				mFooterView.setVisibility(View.INVISIBLE);
			}
			else
			{
				mFooterView.setVisibility(View.VISIBLE);
			}
		}
		*/
		
		mListItemAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private OnClickListener mOnClickListener =  new OnClickListener()
	{
		//@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.btn_start_service:
					 if(mserver_is_running == false)
					 {
						 // 服务还没有运行起来，则启动服务。
						 if(DBG)
						 {
							 Log.d(TAG,"huyanwei start service by manual.");
						 }
						 Intent svc = new Intent(mContext, PhoneListenerService.class);
						 mContext.startService(svc);
						 
						 
						 Toast local_toast = Toast.makeText(mContext, R.string.service_start_notification, Toast.LENGTH_SHORT);
						 //local_toast.setGravity(local_toast.getGravity(), 0, 100);
						 local_toast.setGravity(Gravity.TOP, 0, 400);
						 local_toast.show();
						 
						 mserver_is_running = true;
						 
						 update_running_view();
						 
					 }
					 else
					 {
						 // 服务还没有运行起来，则启动服务。
						 if(DBG)
						 {
							 Log.d(TAG,"huyanwei start service by manual.");
						 }
						 Intent svc = new Intent(mContext, PhoneListenerService.class);
						 mContext.stopService(svc);
						 
						 Toast local_toast = Toast.makeText(mContext, R.string.service_stop_notification, Toast.LENGTH_SHORT);
						 local_toast.setGravity(Gravity.TOP, 0, 400);
						 local_toast.show();
						 
						 mserver_is_running = false;
						 
						 update_running_view();
						 
					 }
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
		public Object   switch_tag      ;
		public boolean  visual     		;
	}
	
	private class ListItemAdapter extends BaseAdapter
	{
		
		private ArrayList<ListItemFuction> mListItemFuction = new ArrayList<ListItemFuction>();
		
		private Context mContext; 
		
		private int mVisualCount ;
		private int mCount ;
		
		public ListItemAdapter(Context c)
		{
			mContext = c;
			mCount = 0 ;
			mVisualCount = 0 ;
		}
		
		public void resetItemList()
		{
			mCount = 0 ;
			mVisualCount = 0 ;			
			mListItemFuction.clear();
		}
		
		public void updateItemList()
		{
			mVisualCount = 0 ;
			for(int i = 0 ; i < mCount ; i++ )
			{
				ListItemFuction local_item = mListItemFuction.get(i);
				if(local_item.visual)
				{
					mVisualCount ++;
				}
			}
		}
		
		public void setItemVisual(int tag , boolean visual)
		{
			int index = 0 ;
			
			ListItemFuction local_item = null ;  // init 1st item.
			
			for( index = 0 ; index < mListItemFuction.size(); index++)
			{
				local_item = mListItemFuction.get(index);
				if(local_item.switch_tag.equals(tag))
				{
					Log.d(TAG,"setItemVisual found it at "+ index);
					break;
				}
			}
			
			if((index >= mListItemFuction.size()))
			{
				// not found.
				Log.d(TAG,"setItemVisual not found.");
				return ;
			}
			
			if(local_item.visual != visual)
			{
				local_item.visual = visual;
				
				updateItemList();
				
				//this.notifyDataSetChanged();
			}
		}
		
		public void addOneItem(ListItemFuction obj)
		{
			mListItemFuction.add(mCount,obj);
			mCount++;
			
			updateItemList();
			
			//this.notifyDataSetChanged();
		}
		
		//@Override
		public int getCount() {
			// TODO Auto-generated method stub
			//if(DBG)
			//{
			//	Log.d(TAG,"getCount()");
			//}
			//return mCount;
			return mVisualCount;
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
            
            int first = mListView.getFirstVisiblePosition(); 	// 在总View的index
            int last  = mListView.getLastVisiblePosition(); 	// 在总View的index
            int child_count = mListView.getChildCount(); 		// 可见的view数目
            int count = mListView.getCount();     		 		// 总view数目
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
            
            ListItemFuction local_item = null;            
            // Map start
            int visual_index = -1 ;
            for(int i = 0 ; i < mListItemFuction.size();i++)
            {
            	local_item = mListItemFuction.get(i);
            	if(local_item.visual)
            	{
            		visual_index ++;
            	}
            	
            	if(visual_index == position)
            	{	
            		break;  // local_item is need one.
            	}
            }
            // Map end
            
            // switch name
            TextView mTextView = (TextView) ll.findViewById(R.id.switch_name);  
           	mTextView.setText(local_item.switch_name);
           	
           	// switch button
           	SlideButton mSlideButton = (SlideButton) ll.findViewById(R.id.SlideButton);
           	mSlideButton.setSwitchOffText(local_item.switch_off_str);
           	mSlideButton.setSwitchOnText(local_item.switch_on_str);
           	mSlideButton.setValue(local_item.switch_value);           	
           	
           	if(mSlideButtonRecognitionByTag)
           	{
           		mSlideButton.setTag(local_item.switch_tag);          // 用 Tag 标记区分 SlideButton
           	}
           	else
           	{
           		mSlideButton.setId(mSlideButtonIdBase+position);     // 注意这里已经改变了他的ID
           	}

           	mSlideButton.setOnSwitchChangedListener(local_item.OnSwitchChangedListener);
           	
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
                	
                	SlideButton local_sb = null ;
                	
                	if(mSlideButtonRecognitionByTag)
                	{
                		local_sb = (SlideButton)((LinearLayout)arg1).findViewById(R.id.SlideButton);
                	}
                	else
                	{
                		// id alread have been modified to (mSlideButtonIdBase+position).
                		local_sb = (SlideButton)((LinearLayout)arg1).findViewById(mSlideButtonIdBase+(int)arg3);
                	}
                	
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
	
	private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener()
	{
		//@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			
			mSeekBarValue = progress ;
			
			// 更新 TextView
			mTextView.setText(mResources.getString(R.string.light_sensor_threshold_str)+":"+ mSeekBarValue);
			
		}

		//@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		//@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

			// 更新 TextView
			mTextView.setText(mResources.getString(R.string.light_sensor_threshold_str)+":"+ mSeekBarValue);
			
			//更新数据库
			update_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_LIGHT_SENSOR_THRESHOLD,mSeekBarValue);
			
		}
	};
	
	public void updateListItemAdapterData()
	{
		mListItemAdapter.resetItemList();
		
        // add 1st Item : Proximity
		ListItemFuction mListItemFuction = new ListItemFuction();
		mListItemFuction.switch_name 	 = mResources.getString(R.string.proximity_sensor_enable_str);
		mListItemFuction.switch_off_str  = mResources.getString(R.string.proximity_sensor_off_str);
		mListItemFuction.switch_on_str   = mResources.getString(R.string.proximity_sensor_on_str);
		mListItemFuction.switch_value  	 = (config_proximity_sensor_enable>=1)?true:false;
		mListItemFuction.switch_tag 	 = mSlideButtonIdBase+mSlideButtonIdOffsetProximityEnable;
		mListItemFuction.OnSwitchChangedListener = mOnSwitchChangedListener;
		mListItemFuction.visual          = true ; // always visual .
		mListItemAdapter.addOneItem(mListItemFuction);
        
        // add 2nd Item : Action
		mListItemFuction = new ListItemFuction();
		mListItemFuction.switch_name 	 = mResources.getString(R.string.default_action_str);
		mListItemFuction.switch_off_str  = mResources.getString(R.string.default_action_off_str);
		mListItemFuction.switch_on_str   = mResources.getString(R.string.default_action_on_str);
		mListItemFuction.switch_value  	 = (config_action>=1)?true:false ;
		mListItemFuction.switch_tag 	= mSlideButtonIdBase+mSlideButtonIdOffsetAction;
		mListItemFuction.OnSwitchChangedListener = mOnSwitchChangedListener;
		mListItemFuction.visual          = (config_proximity_sensor_enable>=1)?true:false;
		mListItemAdapter.addOneItem(mListItemFuction);

        // add 3rd Item : audio record
		mListItemFuction = new ListItemFuction();
		mListItemFuction.switch_name 	 = mResources.getString(R.string.audio_record_str);
		mListItemFuction.switch_off_str  = mResources.getString(R.string.audio_record_off_str);
		mListItemFuction.switch_on_str   = mResources.getString(R.string.audio_record_on_str);
		mListItemFuction.switch_value  	 = (config_audio_record>=1)?true:false ;
		mListItemFuction.switch_tag 	= mSlideButtonIdBase+mSlideButtonIdOffsetAudioRecord;
		mListItemFuction.OnSwitchChangedListener = mOnSwitchChangedListener;
		mListItemFuction.visual          = ((config_proximity_sensor_enable>=1)?true:false) && ((config_action>=1)?true:false) ;
		mListItemAdapter.addOneItem(mListItemFuction);
		
        // add 4th Item : Speaker
		mListItemFuction = new ListItemFuction();
		mListItemFuction.switch_name 	 = mResources.getString(R.string.speaker_state_str);
		mListItemFuction.switch_off_str  = mResources.getString(R.string.speaker_state_off_str);
		mListItemFuction.switch_on_str   = mResources.getString(R.string.speaker_state_on_str);
		mListItemFuction.switch_value  	 = (config_speaker>=1)?true:false ;
		mListItemFuction.switch_tag 	= mSlideButtonIdBase+mSlideButtonIdOffsetSpeaker;
		mListItemFuction.OnSwitchChangedListener = mOnSwitchChangedListener;
		mListItemFuction.visual          = ((config_proximity_sensor_enable>=1)?true:false) && ((config_action>=1)?true:false) ;
		mListItemAdapter.addOneItem(mListItemFuction);
		
        // add 5th Item : light sensor 
		mListItemFuction = new ListItemFuction();
		mListItemFuction.switch_name 	 = mResources.getString(R.string.light_sensor_enable_str);
		mListItemFuction.switch_off_str  = mResources.getString(R.string.light_sensor_off_str);
		mListItemFuction.switch_on_str   = mResources.getString(R.string.light_sensor_on_str);
		mListItemFuction.switch_value  	 = (config_light_sensor_enable>=1)?true:false ;
		mListItemFuction.switch_tag 	= mSlideButtonIdBase+mSlideButtonIdOffsetLightEnable;
		mListItemFuction.OnSwitchChangedListener = mOnSwitchChangedListener;
		mListItemFuction.visual          = (config_proximity_sensor_enable>=1)?true:false;
		mListItemAdapter.addOneItem(mListItemFuction);

		mListItemAdapter.notifyDataSetChanged(); // notify data changed.
		
		// update light sensor threshold value:
		mSeekBar.setProgress(Math.max(0,Math.min(255,config_light_sensor_threshold)));
		mTextView.setText(mResources.getString(R.string.light_sensor_threshold_str)+":"+ config_light_sensor_threshold);
		
		if((config_light_sensor_enable >= 1) && ((config_proximity_sensor_enable>=1)?true:false))
		{
			mFooterView.setVisibility(View.VISIBLE);
		}
		else
		{
			mFooterView.setVisibility(View.INVISIBLE);
		}
		
		mHeaderView.setBackgroundResource(R.drawable.v5_preference_item_single_bg);
		mHeaderView.setPadding(0, 5, 0, 5);
		
		mFooterView.setBackgroundResource(R.drawable.v5_preference_item_single_bg);
		mHeaderView.setPadding(0, 5, 0, 5);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_main);

        mContext= this;
        
		mResources = mContext.getResources();
        
        mLayoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHeaderView = 	(LinearLayout)mLayoutInflater.inflate(R.layout.header, null);
        
        mImageButton = (ImageButton) mHeaderView.findViewById(R.id.btn_start_service);
        mImageButton.setOnClickListener(mOnClickListener);
        
        rotate_image_button_view(0); // default 0 degree.        
        
        mFooterView = (LinearLayout)mLayoutInflater.inflate(R.layout.footer, null);
        mSeekBar 	= (SeekBar) mFooterView.findViewById(R.id.seekBar);
		mSeekBar.setMax(255);
		mSeekBar.setProgress(120);
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        
        mTextView 	= (TextView) mFooterView.findViewById(R.id.textView);                
        
        mlinearLayout = (LinearLayout)findViewById(R.id.LinearLayout);        
        mListView = (ListView) mlinearLayout.findViewById(R.id.listView);        
        mListView.setOnItemClickListener(mOnItemClickListener);
        mListItemAdapter = new ListItemAdapter(this);
        
        mListView.addHeaderView(mHeaderView);
        mListView.addFooterView(mFooterView);
        
        mListView.setAdapter(mListItemAdapter);
        
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

	public void update_running_view()
	{
		
		Thread mThread  = new Thread(new Runnable()
		{
			//@Override
			public void run() {
				
				int angle = 0 ;
				
				while(mserver_is_running)
				{
					angle = (angle+10) % 360 ; //再次旋转5°
					
					Message msg = new Message();
					msg.what = MSG_UPDATE_ANGLE;
					msg.arg1 = angle;
					msg.setTarget(mHandler);
					msg.sendToTarget();
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}			
		});
		
		// 在运行就 转动.
		if(mserver_is_running)
		{
			mThread.start();	
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
        // view database
		config_proximity_sensor_enable 	= query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ENABLE);
		config_action 					= query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_ACTION);
		config_speaker 					= query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_SPEAKER);
		config_light_sensor_enable      = query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_LIGHT_SENSOR_ENABLE);
		config_light_sensor_threshold   = query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_LIGHT_SENSOR_THRESHOLD);
		config_audio_record             = query_config_value(ConfigContentProvider.TABLE_CONTENT_CONFIG_AUDIO_RECORD);
		
		// update List data
		updateListItemAdapterData();
		
		// update service state
		String className = "srclib.huyanwei.phonelistener.PhoneListenerService";		
		mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(50);
		//if(DBG) Log.d(TAG,"mServiceList.size()="+mServiceList.size());
		for(int i = 0; i < mServiceList.size(); i++)
		{
			 	String local_service_name = mServiceList.get(i).service.getClassName();
			 	// if(DBG) Log.d(TAG, local_service_name);			 	
	            if(className.equals(local_service_name))
	            {
	        	   mserver_is_running = true;
	        	   break;
	            }
	    }
		update_running_view();
		 
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
