package srclib.huyanwei.metaview;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ComponentName;
import android.database.DataSetObserver;

public class MainActivity extends Activity {
	private String TAG = "srclib.huyanwei.metaview";
	
	private ImageButton backBtn;
	private ImageButton setBtn;
	private WindowManager mWindowManager;
	private LayoutInflater mLayoutInflater;
	private WindowManager.LayoutParams mLayoutParams;
	private View mFloatView;
	
	private ListView mListView;
		
	private Context mContext; 
	
	private ListItemAdapter mListItemAdapter;
	private boolean mScrolling = false;
	
	public void attachView(View view)
	{
		  mLayoutParams = new WindowManager.LayoutParams(
				  WindowManager.LayoutParams.MATCH_PARENT,
				  180,
				  WindowManager.LayoutParams.TYPE_BASE_APPLICATION,
				  WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,//WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
				  PixelFormat.RGBA_8888
				  );
	        /*
	        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY;
	        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
	                			| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
	                			| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
	                			| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
	        mLayoutParams.format = PixelFormat.TRANSLUCENT;
	        
	        mLayoutParams.setTitle("PointerLocationFloatWindows");        
	        
	        mLayoutParams.inputFeatures |= WindowManager.LayoutParams.INPUT_FEATURE_NO_INPUT_CHANNEL;
	        */
	        
	        /**
	         *以下都是WindowManager.LayoutParams的相关属性
	         * 具体用途可参考SDK文档
	         */
	        //mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
	        //mLayoutParams.type=WindowManager.LayoutParams.TYPE_PHONE;   //设置window type
	        //mLayoutParams.format=PixelFormat.RGBA_8888;   //设置图片格式，效果为背景透明

	        //设置Window flag
//	        mLayoutParams.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//	                              | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
	        /*
	         * 下面的flags属性的效果形同“锁定”。
	         * 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
	         wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL 
	                               | LayoutParams.FLAG_NOT_FOCUSABLE
	                               | LayoutParams.FLAG_NOT_TOUCHABLE;
	        */
	        
	        //mLayoutParams.gravity=Gravity.CENTER_HORIZONTAL;   //调整悬浮窗口至底部居中
	        
			DisplayMetrics dm = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(dm);
	        int android_width = dm.widthPixels;
	        int android_height = dm.heightPixels;

	        //设置悬浮窗口长宽数据
	        mLayoutParams.width=android_width;
	        mLayoutParams.height=96;

	        //以屏幕左上角为原点，设置x、y初始值
	        mLayoutParams.x=(android_width  - mLayoutParams.width);
	        mLayoutParams.y=(android_height - mLayoutParams.height);
	        
	        Log.d(TAG,""+mLayoutParams.toString());
	        
	        //显示FloatView图像
	        mWindowManager.addView(view, mLayoutParams);
	}
	
	public void disattachView(View view)
	{
		mWindowManager.removeViewImmediate(view);
	}
	
	public void go_to_back()
	{
		this.finish();
	}
	
	private OnClickListener mOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.btn_back:			
					go_to_back();
					break;
				case R.id.btn_setting:
					//Toast.makeText(mContext, "Seting", Toast.LENGTH_SHORT).show();
					mListItemAdapter.fork();
					mListItemAdapter.notifyDataSetChanged();
					break;
				default:
					break;
			}
		}		
	};
	
	private class ListItemAdapter extends BaseAdapter
	{
		private Context mContext; 
		
		private int mCount ;
		
		public ListItemAdapter(Context c)
		{
			mContext = c;
			mCount = 20 ;
		}
		
		public void fork()
		{
			mCount *= 2 ;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			Log.d(TAG,"getCount()");
			return mCount;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			Log.d(TAG,"getItem("+position+")");
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			Log.d(TAG,"getItemId("+position+")");
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Log.d(TAG,"getView("+position+")");
			LinearLayout ll;
            if (convertView == null) {
            	ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
            } else {
            	ll = (LinearLayout) convertView;
            }
            
            if(position%2 == 0)
            {
            	ll.setBackgroundResource(R.drawable.bottom_button);
            	//ll.setAlpha(0.618f);            	
            }	
            else
            {
            	ll.setBackgroundResource(R.drawable.bottom_button);
            }
            
            TextView mTextView = (TextView) ll.findViewById(R.id.textView1);   
            if(mScrolling == true)            
            {
            	mTextView.setTextColor(0xffff00ff);
            	mTextView.setText("LOADING ITEM"+position+" ......");            	            	
            }
            else
            {
            	mTextView.setTextColor(0xff0000ff);
            	mTextView.setText("ITEM"+position);
            }
            return ll;
		}

		@Override
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			Log.d(TAG,"isEnabled("+position+")");
			if(position %2 == 0)
			{
				return true;
			}
			else
			{
				return true;
				//return false;
			}
			//return super.isEnabled(position);
		}
	};
	
	private OnScrollListener mOnScrollListener = new OnScrollListener()
	{

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
	        switch (scrollState) {
	        	case OnScrollListener.SCROLL_STATE_IDLE:
	        		// 当滚动停止时：
	        		mScrolling = false;
	                int first = view.getFirstVisiblePosition();
	                int count = view.getChildCount();
	                for (int i=0; i<count; i++) {
	                	LinearLayout ll = (LinearLayout)view.getChildAt(i);
	                    TextView mTextView = (TextView) ll.findViewById(R.id.textView1);                        
	                    
	                    mTextView.setTextColor(0xffff0000);
	                    mTextView.setText("UPDATA ITEM("+first+"+"+i+")");
	                    //Log.d(TAG,"ChildAt("+i+") at array["+first+i+"]");
	                }	
	        		Log.d(TAG,"SCROLL_STATE_IDLE");
	        		break;
	        	case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
	        		mScrolling = true;
	        		Log.d(TAG,"SCROLL_STATE_TOUCH_SCROLL");
	        		break;
	        	case OnScrollListener.SCROLL_STATE_FLING:
	        		mScrolling = true;
	        		Log.d(TAG,"SCROLL_STATE_FLING");
	        		break;
	        }
		}
	};
	
	private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
				// TODO Auto-generated method stub
				Log.d(TAG,"onItemClick("+arg0+","+arg1+","+arg2+","+arg3+")");
				TextView mTextView = (TextView) arg1.findViewById(R.id.textView1);    
                mTextView.setTextColor(0xff000000);
                mTextView.setText("clicked Item"+arg2);
				Toast.makeText(mContext, "Item"+arg2+" is clicked!", Toast.LENGTH_SHORT).show();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this ;
		
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
		setContentView(R.layout.activity_main);
		
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customer_title);
		
		mWindowManager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
		mLayoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
        backBtn = (ImageButton)findViewById(R.id.btn_back);

        backBtn.setOnClickListener(mOnClickListener);
        
        mListView = (ListView) this.findViewById(R.id.listView1);
        
        mListItemAdapter = new ListItemAdapter(this);
        
        mListView.setAdapter(mListItemAdapter);
        
        mListView.setOnScrollListener(mOnScrollListener);
        
        mListView.setOnItemClickListener(mOnItemClickListener);
		
        mFloatView = mLayoutInflater.inflate(R.layout.floater, null);
        
        setBtn = (ImageButton) mFloatView.findViewById(R.id.btn_setting);
        
        setBtn.setOnClickListener(mOnClickListener);
        
        attachView(mFloatView);
        //setListFooter(footView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		disattachView(mFloatView);
		super.onDestroy();
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub		
		super.onPause();
	}


	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub		
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
