package srclib.huyanwei.metaview;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
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
import android.widget.ImageView;
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
	private ImageButton plusBtn;
	private ImageButton minusBtn;
	private WindowManager mWindowManager;
	private LayoutInflater mLayoutInflater;
	private WindowManager.LayoutParams mLayoutParams;
	private WindowManager.LayoutParams mCacheLayoutParams;
	private View mFloatView;
	
	private ListView mListView;
		
	private Context mContext; 
	
	private ListItemAdapter mListItemAdapter;
	
	private boolean mScrolling = false;	
	private boolean mIsFloatWindow = true;
	
	private View    mDragView  ;
	private boolean mDraging    = false ;
	
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
				case R.id.btn_plus:
					//Toast.makeText(mContext, "Seting", Toast.LENGTH_SHORT).show();
					mListItemAdapter.twice();
					mListItemAdapter.notifyDataSetChanged();
					break;
				case R.id.btn_minus:
					//Toast.makeText(mContext, "Seting", Toast.LENGTH_SHORT).show();
					mListItemAdapter.half();
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
			mCount = 1 ;
		}
		
		public void twice()
		{
			mCount *= 2 ;
		}
		
		public void half()
		{
			mCount /= 2 ;
			
			if(mCount < 1)
				mCount = 1 ;
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
			// 每次都加载，不让重复使用View
            //if (convertView == null) {
            	ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
            //} else {
            //	ll = (LinearLayout) convertView;
            //}
            
            /*
            if(position%2 == 0)
            {
            	ll.setBackgroundResource(R.drawable.bottom_button);
            	ll.setAlpha(0.618f);            	
            }
            else
            {
            	ll.setBackgroundResource(R.drawable.bottom_button);
            }
            */
            
            int first = mListView.getFirstVisiblePosition();
            int last  = mListView.getLastVisiblePosition();
            int child_count = mListView.getChildCount(); // 可见的view
            int count = mListView.getCount();     		// 总view
            int header_count = ((ListView) mListView).getHeaderViewsCount();
            int footer_count = ((ListView) mListView).getFooterViewsCount();

            if(count ==1)
            {
            	// Only One Item
            	ll.setBackgroundResource(R.drawable.v5_preference_item_single_bg);
            }
            else if(position == 0)
            {
            	// first
            	ll.setBackgroundResource(R.drawable.v5_preference_item_first_bg);
            }
            else if(position == count -1)
            {
            	// last
            	ll.setBackgroundResource(R.drawable.v5_preference_item_last_bg);
            }
            else
            {
            	// moddile
            	ll.setBackgroundResource(R.drawable.v5_preference_item_middle_bg);
            }            
            
            TextView mTextView = (TextView) ll.findViewById(R.id.textView1);   
            if(mScrolling == true)            
            {
            	mTextView.setTextColor(0xffff00ff);
            	mTextView.setText("LOADING ITEM"+(position+header_count)+" ......");            	            	
            }
            else
            {
            	mTextView.setTextColor(0xff0000ff);
            	mTextView.setText("ITEM"+(position+header_count));
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
	                int last  = view.getLastVisiblePosition();
	                int child_count = view.getChildCount(); // 可见的view
	                int count = view.getCount();     		// 总view
	                int header_count = ((ListView) view).getHeaderViewsCount();
	                int footer_count = ((ListView) view).getFooterViewsCount();
	                Log.d(TAG,"["+first+"-"+last+"]/["+child_count+"-"+count+"]["+header_count+"/"+footer_count+"]");

	                for (int i=0; i<child_count; i++)
	                {	
	                	if(((i+first) >= (0+header_count)) && ( (i+first) <= (count -1 - footer_count) ))
	                	{	
		                	LinearLayout ll = (LinearLayout)view.getChildAt(i);
		                    TextView mTextView = (TextView) ll.findViewById(R.id.textView1);                        
		                    
		                    mTextView.setTextColor(0xffff0000);
		                    mTextView.setText("UPDATA ITEM("+(first)+"+"+i+")");
	                	}
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
                int first = arg0.getFirstVisiblePosition();
                int last  = arg0.getLastVisiblePosition();
                int child_count = arg0.getChildCount();
                int count = arg0.getCount();
                int header_count = ((ListView) arg0).getHeaderViewsCount();
                int footer_count = ((ListView) arg0).getFooterViewsCount();
                Log.d(TAG,"["+first+"-"+last+"/"+count+"]["+header_count+"/"+footer_count+"]");
                if((arg2 > (header_count-1)) && (arg2 < (count-footer_count))) // index calc
				{
                	TextView mTextView = (TextView) arg1.findViewById(R.id.textView1);
                	mTextView.setTextColor(0xff000000);
                	mTextView.setText("clicked Item"+(arg2));
                	Toast.makeText(mContext, "Item"+(arg2)+" is clicked!", Toast.LENGTH_SHORT).show();
				}
                else
				{
					Toast.makeText(mContext, "mFloatView("+arg2+") is clicked!", Toast.LENGTH_SHORT).show();
				}
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
        
        mListView.setOnScrollListener(mOnScrollListener);
        
        mListView.setOnItemClickListener(mOnItemClickListener);
		
        mFloatView = mLayoutInflater.inflate(R.layout.floater, null);
        
        plusBtn = (ImageButton) mFloatView.findViewById(R.id.btn_plus);
        
        plusBtn.setOnClickListener(mOnClickListener);
        
        minusBtn = (ImageButton) mFloatView.findViewById(R.id.btn_minus);
        
        minusBtn.setOnClickListener(mOnClickListener);
        
        if(mIsFloatWindow)
        {
        	attachView(mFloatView);
        }
        else
        {
        	mListView.addHeaderView(mFloatView);  // must appear before setAdapter().
        	//mListView.setListFooter(mFloatView);
        	//mListView.addFooterView(mFloatView);  // must appear before setAdapter().
        }
        
        mListItemAdapter = new ListItemAdapter(this);
        
        mListView.setAdapter(mListItemAdapter);        

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
		if(mIsFloatWindow)
		{
			disattachView(mFloatView);
		}
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
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// getRawX()和getRawY()：获得的是相对屏幕的位置
		// getX()和getY()：获得的永远是相对view的触摸位置 坐标（这两个值不会超过view的长度和宽度)
		// getLeft , getTop, getBottom,getRight, 这个指的是该控件相对于父控件的距离.
		int x,y ;
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				x = (int) event.getX();// 获取相对与ListView的x坐标 
				y = (int) event.getY();// 获取相应与ListView的y坐标
				Log.d(TAG,"onTouchEvent down ("+x+","+y+");");
				startDrag(x,y);
				break;
			case MotionEvent.ACTION_MOVE: 
				x = (int) event.getX();// 获取相对与ListView的x坐标 
				y = (int) event.getY();// 获取相应与ListView的y坐标
				Log.d(TAG,"onTouchEvent move ("+x+","+y+");");
				onDrag(y);				
				break;
			case MotionEvent.ACTION_UP:
				x = (int) event.getX();// 获取相对与ListView的x坐标 
				y = (int) event.getY();// 获取相应与ListView的y坐标
				Log.d(TAG,"onTouchEvent up ("+x+","+y+");");
				stopDrag();
				break; 
			default: 
				break;
		}
		return false ;
		
		//return super.onTouchEvent(event);
	}
		

		private void startDrag(int x , int y)
		{
			/*** 
			* 初始化window. 
			*/ 
			mCacheLayoutParams = new WindowManager.LayoutParams(); 
			mCacheLayoutParams.gravity = Gravity.TOP; 
			mCacheLayoutParams.x = 0; 
			mCacheLayoutParams.y = y; 
			mCacheLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT; 
			mCacheLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; 

			mCacheLayoutParams.flags =  WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE		// 不需获取焦点 
								| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE		// 不需接受触摸事件 
								| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON	// 保持设备常开，并保持亮度不变。 
								| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;	// 窗口占满整个屏幕，忽略周围的装饰边框（例如状态栏）。此窗口需考虑到装饰边框的内容。 

			// windowParams.format = PixelFormat.TRANSLUCENT;// 默认为不透明，这里设成透明效果. 
			mCacheLayoutParams.windowAnimations = 0;// 窗口所使用的动画设置 

			//初始化影像
			
			Log.d(TAG,"mListView.getFirstVisiblePosition()="+mListView.getFirstVisiblePosition());
			
			View ListItemView = mListView.getChildAt(0);
			ListItemView.setDrawingCacheEnabled(true);// 开启cache. 
			Bitmap bm = Bitmap.createBitmap(ListItemView.getDrawingCache());// 根据cache创建一个新的bitmap对象.
			
			ImageView imageView = new ImageView(this); 
			imageView.setImageBitmap(bm);
			mWindowManager.addView(imageView, mCacheLayoutParams);
			
			mDragView = imageView;
			
			if(mDragView != null)
			{
				mDraging = true ;
			}
		}
		
		/** 
		* 拖动执行，在Move方法中执行 
		* 
		* @param y 
		*/
		public void onDrag(int y)
		{ 
			if (mDraging)
			{ 
				mCacheLayoutParams.alpha = 0.5f; 
				mCacheLayoutParams.y = y; 
				mWindowManager.updateViewLayout(mDragView, mCacheLayoutParams);// 时时移动.
				
				//onChange(y);// 时时交换
				//doScroller(y);// listview移动.
			} 
		} 
		
		/** 
		* 停止拖动，删除影像 
		*/ 
		public void stopDrag()
		{ 
			if (mDraging) 
			{ 
				mWindowManager.removeView(mDragView); 
				mDragView = null; 				
				mDraging = false ;
			}
		}
		
		/** 
		* 拖动放下的时候 
		* 
		* @param y 
		*/ 
		public void onDrop(int y) { 
			((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();// 刷新.
		} 
}
