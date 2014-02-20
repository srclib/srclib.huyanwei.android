/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 *
 * MediaTek Inc. (C) 2010. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER ON
 * AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek Software")
 * have been modified by MediaTek Inc. All revisions are subject to any receiver's
 * applicable license agreements with MediaTek Inc.
 */

/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AnimationUtils;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mediatek.featureoption.FeatureOption;
import com.android.launcher3.AppAdapter;
import com.android.launcher3.R;

import android.app.Activity;

import android.content.pm.PackageInfo;
import java.text.Collator;
import java.util.Comparator;
import java.util.Collections;

/*****************************
** huyanwei add start 
**********************************************/
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.Matrix;
/*****************************
** huyanwei add end
**********************************************/

/*
 RelativeLayout
 */

public class AllApps2D
        extends ViewGroup
        implements AllAppsView,
                   AdapterView.OnItemClickListener,
                   AdapterView.OnItemLongClickListener,
                   View.OnKeyListener,
                   DragSource {

    private static final String TAG = "srclib.huyanwei.Launcher.AllApps2D";
    private static final boolean DEBUG = false;

    private Launcher mLauncher;
    private DragController mDragController;
 
    private static final float APP_PAGE_SIZE = 16.0f;
    private int PageCount= 0 ;
    private Scroller mScroller;
	private VelocityTracker mVelocityTracker;	
	private int mCurScreen;
	private int mDefaultScreen = 0;
	private int page = 0;	
	private PageListener pageListener;
	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
	
	private static final int page_gap = 0;	
	private static final int SNAP_VELOCITY = 600;
	
	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop;
	private float mLastMotionX;
	private float mLastMotionY;
	
	private Drawable mScreenIndicator ;
	private LinearLayout mIndicator;
	
	//private IconCache iconCache = new IconCache((LauncherApplication)getContext().getApplication()); //getContent()
	private IconCache iconCache ;	
    //private GridView mGrid;
    
    private ArrayList<ApplicationInfo> mAllAppsList = new ArrayList<ApplicationInfo>();

    // preserve compatibility with 3D all apps:
    //    0.0 -> hidden
    //    1.0 -> shown and opaque
    //    intermediate values -> partially shown & partially opaque
    private float mZoom;

    private AppsAdapter mAppsAdapter;

    //huyanwei add {
    private static int homescreen_indication_width = 31;    
    private static int homescreen_indication_height = 31;
    //huyanwei add }

    private static final int INVALID_POINTER = -1;
    private int mActivePointerId = INVALID_POINTER;

    // huyanwei modify it {
    //private static final int INVALID_SCREEN = -1;
    private static final int INVALID_SCREEN = -999;

    private boolean transparent_bg = false;
    // huyanwei modify it }

    private float mSmoothingTime;
    private float mTouchX;	

    // ------------------------------------------------------------
    /*
    public static class HomeButton extends ImageButton {
        public HomeButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
        @Override
        public View focusSearch(int direction) {
            if (direction == FOCUS_UP) return super.focusSearch(direction);
            return null;
        }
    }
    */

    public class AppsAdapter extends ArrayAdapter<ApplicationInfo> {
        private final LayoutInflater mInflater;
        private List<ApplicationInfo> mList;
        
        public AppsAdapter(Context context, ArrayList<ApplicationInfo> apps) {
            super(context, 0, apps);
            mInflater = LayoutInflater.from(context);
        }       

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ApplicationInfo info = getItem(position);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.application_boxed, parent, false);
            }

//            if (!info.filtered) {
//                info.icon = Utilities.createIconThumbnail(info.icon, getContext());
//                info.filtered = true;
//            }

            final TextView textView = (TextView) convertView;
            if (DEBUG) {
                Log.d(TAG, "icon bitmap = " + info.iconBitmap 
                    + " density = " + info.iconBitmap.getDensity());
            }
            info.iconBitmap.setDensity(Bitmap.DENSITY_NONE);
            textView.setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(info.iconBitmap), null, null);
            //textView.setCompoundDrawablesWithIntrinsicBounds(null, Launcher.zoomDrawable(new BitmapDrawable(info.iconBitmap),50,50), null, null);
            textView.setText(info.title);

            return convertView;
        }
    }

    public AllApps2D(Context context, AttributeSet attrs) {
        super(context, attrs);
        setVisibility(View.GONE);
        setSoundEffectsEnabled(false);

        mScroller = new Scroller(context); // huyanwei add it
        
        mCurScreen = mDefaultScreen;
        
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        
        mAppsAdapter = new AppsAdapter(getContext(), mAllAppsList);
        mAppsAdapter.setNotifyOnChange(false);

	//huyanwei add {
        final Resources resources = getResources();
        homescreen_indication_width = resources.getDimensionPixelSize(R.dimen.homescreen_indication_width);
        homescreen_indication_height = resources.getDimensionPixelSize(R.dimen.homescreen_indication_height);
        
        //homescreen_indication_width = Math.min(homescreen_indication_width, 31);
        //homescreen_indication_height = Math.min(homescreen_indication_height, 31);

	transparent_bg = resources.getBoolean(R.bool.grid_background_transparent);
	//huyanwei add }

    }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onLayout");
		//if (changed)  // huyanwei del it .
		{
			int childLeft = 0;
			final int childCount = getChildCount();
			//Log.e(TAG, "childCount="+childCount);	   			
			for (int i=0; i<childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					final int childWidth = childView.getMeasuredWidth();
					//Log.e(TAG, "childWidth="+childWidth);
					childView.layout(childLeft, 0, childLeft+childWidth, childView.getMeasuredHeight());
					childLeft += childWidth+ page_gap;
				}
			}
		}
	}

    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
    	Log.e(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);   
  
        final int width = MeasureSpec.getSize(widthMeasureSpec);   
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);   
        if (widthMode != MeasureSpec.EXACTLY) {   
            throw new IllegalStateException("ScrollLayout only canmCurScreen run at EXACTLY mode!"); 
        }   
  
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);   
        if (heightMode != MeasureSpec.EXACTLY) {   
            throw new IllegalStateException("ScrollLayout only can run at EXACTLY mode!");
        }   
  
        // The children are given the same width and height as the scrollLayout   
        final int count = getChildCount();   
		Log.e(TAG, "count="+count);        
        for (int i = 0; i < count; i++) {   
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);   
        }   
        // Log.e(TAG, "moving to screen "+mCurScreen);   
        scrollTo(mCurScreen * width, 0);         
	setCurPage(mCurScreen); //sync state .
    }  
    
    public void updateView()
    {	
    	Log.e(TAG, "huyanwei update");
    	int n_total_page;    	
    	//this.removeAllViews();
    	initChildView();
    	//n_total_page = this.getPage();
    	Log.e(TAG, "huyanwei update PageCount="+ PageCount);
    	this.snapToScreen(Math.max(PageCount-1, 0));
    	invalidate();		// Redraw the layout    	
    }    
    
	class ApplicationComp implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			int index = 0 ;
			final PackageManager pm = getContext().getPackageManager();
			ResolveInfo c1 = (ResolveInfo) o1;
			ResolveInfo c2 = (ResolveInfo) o2;
			
			/*
			* sort by first install time.
			*/
			List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
			
			PackageInfo pkg1 = null;
			PackageInfo pkg2 = null;
			
			//Log.e(TAG, "c1 name="+c1.loadLabel(pm).toString());
			//Log.e(TAG, "c2 name="+c2.loadLabel(pm).toString());
			//Log.e(TAG, "package size="+packageInfoList.size());
			
			index = 0 ;
			while(index < packageInfoList.size())
			{
				//Log.e(TAG, "pkg1 name="+pm.getApplicationLabel(packageInfoList.get(index).applicationInfo));
				
				if(c1.loadLabel(pm).toString().equals(pm.getApplicationLabel(packageInfoList.get(index).applicationInfo)))			
				{
					pkg1 = packageInfoList.get(index);					
					break;
				}
				index ++;
			}
			
			index = 0 ;
			while(index < packageInfoList.size())
			{
				//Log.e(TAG, "pkg2 name="+pm.getApplicationLabel(packageInfoList.get(index).applicationInfo));
				if(c2.loadLabel(pm).toString().equals(pm.getApplicationLabel(packageInfoList.get(index).applicationInfo)))						{					
					pkg2 = packageInfoList.get(index);				
					break;
				}								
				index ++;
			}

			if((pkg1 == null) || (pkg2 == null))
				return 0;			
			
			if ((pkg1.firstInstallTime - pkg2.firstInstallTime) < 0)
				return -1;
			else if ((pkg1.firstInstallTime - pkg2.firstInstallTime) < 0)
				return 1;
			else
				return 0;		
			
			
			/* 
			 * chinese name sort
			*/
			/*
			Collator myCollator = Collator.getInstance(java.util.Locale.CHINA);
			if (myCollator.compare(c1.loadLabel(pm), c2.loadLabel(pm)) < 0)
				return -1;
			else if (myCollator.compare(c1.loadLabel(pm), c2.loadLabel(pm)) > 0)
				return 1;
			else
				return 0;
			*/

		}
	}    
    
    public void initChildView()
    {		
    	Log.e(TAG, ">>>initChildView()");
    	AllApps2D all_apps;		
		AppAdapter Adp ;
		final PackageManager packageManager = getContext().getPackageManager();
	    final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	    // get all apps 
	    final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
	    

	    /*	    
	    Comparator cmp = new ApplicationComp();
	    Collections.sort(apps, cmp);    
	    */
	
	    // old.
	    Collections.sort(apps, new ResolveInfo.DisplayNameComparator(packageManager));
	    
	    //all_apps = (AllApps2D)findViewById(R.id.all_apps_view_2d);
	    all_apps = this ;
	    
	    all_apps.removeAllViews();        
	    // the total pages
	    PageCount = (int)Math.ceil(apps.size()/APP_PAGE_SIZE);
	    Log.e(TAG, "size:"+apps.size()+" page:"+PageCount);
	    for (int i=0; i<PageCount; i++) {
	    	GridView appPage = new GridView(getContext());
	    	Adp = new AppAdapter(getContext(), apps, i) ;
	    	// get the "i" page data
	    	appPage.setAdapter(Adp);
	    	//appPage.setAdapter(new AppAdapter(getContext(), mAllAppsList, i));        	
	    	appPage.setNumColumns(4);
	    	appPage.setOnItemClickListener(this); 
	    	appPage.setOnItemLongClickListener(this);
	    	
	    	// huyanwei add it 
	    	//appPage.setOnItemClickListener(listener);
	    	//appPage.setOnItemLongClickListener(long_listener);        
	    	//Log.e(TAG, "addView(i)="+i);
	    	all_apps.addView(appPage);        	
	    	//Adp.notifyDataSetChanged();
	    	//Log.e(TAG, "appPage.getChildCount()="+appPage.getChildCount());	        	
	    }
	
	    all_apps.setPageListener(new AllApps2D.PageListener() {
			public void page(int page) {
				setCurPage(page);				
			}
		});    	
    }
    
    // huyanwei add it 
	public void initViews() {

		initChildView();
        if(mIndicator != null)
        {
        	setCurPage(0);// all_apps.getPage()
        }
        //snapToScreen(0);
	}
	
    @Override
    protected void onFinishInflate() {
    	if(transparent_bg)
    	{
		//setBackgroundColor(Color.TRANSPARENT);
		setBackgroundColor(R.color.grid_dark_background);
	}
	else
	{
		setBackgroundColor(Color.BLACK);
	}

        try {
        	/*
            mGrid = (GridView)findViewWithTag("all_apps_2d_grid");
            if (mGrid == null) throw new Resources.NotFoundException();
            mGrid.setOnItemClickListener(this);
            mGrid.setOnItemLongClickListener(this);
            //mGrid.setBackgroundColor(Color.BLACK);
            //mGrid.setCacheColorHint(Color.BLACK);            
            mGrid.setCacheColorHint(Color.TRANSPARENT);
            mGrid.setBackgroundColor(Color.TRANSPARENT);       
            */
        	
            /*
            ImageButton homeButton = (ImageButton) findViewWithTag("all_apps_2d_home");                        
            if (homeButton == null) throw new Resources.NotFoundException();
            homeButton.setBackgroundColor(Color.TRANSPARENT);            
            homeButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        mLauncher.closeAllApps(true);
                    }
                });
            */    
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find necessary layout elements for AllApps2D");
        }

        	setOnKeyListener(this);       
     
        	initViews(); // huyanwei add it
    }

    public AllApps2D(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public void setLauncher(Launcher launcher) {
        	mLauncher = launcher;
    }

    public void setIndicators(Drawable screen_index) {
    		Log.e(TAG,"setIndicators(Drawable screen_index)");
    		mScreenIndicator=screen_index;	 		
    		//screen_index.setLevel(mCurScreen);     
    }
    
    public void setIndicatorControl(LinearLayout layout) {
    		Log.e(TAG,"setIndicatorControl(LinearLayout layout)");
    		mIndicator = layout ;
    		setCurPage(0);    		// init page num
    }    
    
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (!isVisible()) return false;

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                mLauncher.closeAllApps(true);
                break;
            default:
                return false;
        }

        return true;
    }
	public OnItemClickListener listener = new OnItemClickListener() {
		
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			ResolveInfo appInfo = (ResolveInfo)parent.getItemAtPosition(position);
			Intent mainIntent = mContext.getPackageManager()
				.getLaunchIntentForPackage(appInfo.activityInfo.packageName);
			mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			try {
				// launcher the package
				mContext.startActivity(mainIntent);
			} catch (ActivityNotFoundException noFound) {
				Toast.makeText(mContext, "Package not found!", Toast.LENGTH_SHORT).show();
			}
		}		
	};	
	
	public OnItemLongClickListener long_listener = new OnItemLongClickListener() {		
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position,long id) {
			// TODO Auto-generated method stub			
			ResolveInfo appInfo = (ResolveInfo)parent.getItemAtPosition(position);
			
			//ApplicationInfo app = (ApplicationInfo) new ApplicationInfo(appInfo,iconCache);
			ApplicationInfo app = (ApplicationInfo) new ApplicationInfo(appInfo,mLauncher.getIconCache());
			
	        app = new ApplicationInfo(app);
	        //mDragController.startDrag(view, this, app, DragController.DRAG_ACTION_COPY);
	        mLauncher.closeAllApps(true);
	        return true;
		}		
	};
	
    public void onItemClick(AdapterView parent, View v, int position, long id) {
    	Log.e(TAG,"all_apps grid onItemClick");
    	
    	
    	ResolveInfo info = (ResolveInfo)parent.getItemAtPosition(position);
    	ApplicationInfo app = new ApplicationInfo(info,mLauncher.getIconCache());    	
    	//ApplicationInfo app = (ApplicationInfo)parent.getItemAtPosition(position);
    	
    	mLauncher.startActivitySafely(app.intent, app);
    	/*
        ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);
        mLauncher.startActivitySafely(app.intent, app);
        */
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
    	Log.e(TAG,"all_apps grid onItemLongClick");
        if (!view.isInTouchMode()) {
            return false;
        }

        ResolveInfo info = (ResolveInfo)parent.getItemAtPosition(position);
        ApplicationInfo app = (ApplicationInfo) new ApplicationInfo(info,mLauncher.getIconCache());
        //ApplicationInfo app = (ApplicationInfo)parent.getItemAtPosition(position);
        
        app = new ApplicationInfo(app);

        mDragController.startDrag(view, this, app, DragController.DRAG_ACTION_COPY);
        mLauncher.closeAllApps(true);

		/*
        ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);
        app = new ApplicationInfo(app);

        mDragController.startDrag(view, this, app, DragController.DRAG_ACTION_COPY);
        mLauncher.closeAllApps(true);
    		 */
        return true;
    }

    protected void onFocusChanged(boolean gainFocus, int direction, android.graphics.Rect prev) {
        if (gainFocus) {
           // mGrid.requestFocus();
        }
    }

    public void setDragController(DragController dragger) {
        mDragController = dragger;
    }

    public void onDropCompleted(View target, boolean success) {
    }

    /**
     * Zoom to the specifed level.
     *
     * @param zoom [0..1] 0 is hidden, 1 is open
     */
    public void zoom(float zoom, boolean animate) {
//        Log.d(TAG, "zooming " + ((zoom == 1.0) ? "open" : "closed"));
        cancelLongPress();

        mZoom = zoom;
	
// huyanwei modify it .
        if (isVisible()) 
	{
            setVisibility(View.VISIBLE);
	}
	else
	{
            setVisibility(View.GONE);
	}
/*
        if (isVisible()) {
            //getParent().bringChildToFront(this);
            setVisibility(View.VISIBLE);
           // mGrid.setAdapter(mAppsAdapter);       
            if (animate) {
                startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.all_apps_2d_fade_in));
            } else {
                onAnimationEnd();
            }
        } else {
            if (animate) {
                startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.all_apps_2d_fade_out));
            } else {
                onAnimationEnd();
            }
        }
*/
    }

    protected void onAnimationEnd() {
        if (!isVisible()) {
            setVisibility(View.GONE);
           // mGrid.setAdapter(null);
            mZoom = 0.0f;
        } else {
            mZoom = 1.0f;
        }
        mLauncher.zoomed(mZoom);
    }

    public boolean isVisible() {
        return mZoom > 0.001f;
    }

    @Override
    public boolean isOpaque() {
        return mZoom > 0.999f;
    }

    public void setApps(ArrayList<ApplicationInfo> list) {
        mAllAppsList.clear();
        addApps(list);
    }
    
    public void reorderApps(){
		if (AllAppsList.mTopPackages == null || mAllAppsList == null
				|| AllAppsList.mTopPackages.isEmpty() || mAllAppsList.isEmpty()) {
			return;
		}

		ArrayList<ApplicationInfo> dataReorder = new ArrayList<ApplicationInfo>(
				AllAppsList.DEFAULT_APPLICATIONS_NUMBER);

		for (AllAppsList.TopPackage tp : AllAppsList.mTopPackages) {
			int loop = 0;
			for (ApplicationInfo ai : mAllAppsList) {
				if (ai.componentName.getPackageName().equals(tp.mPackageName)
						&& ai.componentName.getClassName().equals(tp.mClassName)) {
					mAllAppsList.remove(ai);
					dataReorder.add(ai);
					break;
				}
				loop++;
			}
		}

		for (AllAppsList.TopPackage tp : AllAppsList.mTopPackages) {
			int newIndex = 0;
			for (ApplicationInfo ai : dataReorder) {
				if (ai.componentName.getPackageName().equals(tp.mPackageName)
						&& ai.componentName.getClassName().equals(tp.mClassName)) {
					newIndex = Math.min(Math.max(tp.mOrder, 0), mAllAppsList.size());
					mAllAppsList.add(newIndex, ai);
					break;
				}
			}
		}
    }

    public void addApps(ArrayList<ApplicationInfo> list) {
//        Log.d(TAG, "addApps: " + list.size() + " apps: " + list.toString());

        final int N = list.size();

        for (int i=0; i<N; i++) {
            final ApplicationInfo item = list.get(i);
            int index = Collections.binarySearch(mAllAppsList, item,
                    LauncherModel.APP_NAME_COMPARATOR);
            if (index < 0) {
                index = -(index+1);
            }
            mAllAppsList.add(index, item);
        }

	/*
        if (FeatureOption.MTK_YMCAPROP_SUPPORT) {
        	reorderApps();
        }
	*/
        mAppsAdapter.notifyDataSetChanged();
    }

    public void removeApps(ArrayList<ApplicationInfo> list) {
        final int N = list.size();
        for (int i=0; i<N; i++) {
            final ApplicationInfo item = list.get(i);
            int index = findAppByComponent(mAllAppsList, item);
            if (index >= 0) {
                mAllAppsList.remove(index);
            } else {
                Log.w(TAG, "couldn't find a match for item \"" + item + "\"");
                // Try to recover.  This should keep us from crashing for now.
            }
        }

/*
        if (FeatureOption.MTK_YMCAPROP_SUPPORT) {
        	reorderApps();
        }
*/
        mAppsAdapter.notifyDataSetChanged();
    }

    public void updateApps(ArrayList<ApplicationInfo> list) {
        // Just remove and add, because they may need to be re-sorted.
        removeApps(list);
        addApps(list);
    }

    private static int findAppByComponent(ArrayList<ApplicationInfo> list, ApplicationInfo item) {
        ComponentName component = item.intent.getComponent();
        final int N = list.size();
        for (int i=0; i<N; i++) {
            ApplicationInfo x = list.get(i);
            if (x.intent.getComponent().equals(component)) {
                return i;
            }
        }
        return -1;
    }

    public void dumpState() {
        ApplicationInfo.dumpApplicationInfoList(TAG, "mAllAppsList", mAllAppsList);
    }
    
    public void surrender() {
    }
    
    // huyanwei add it 
    /**
     * According to the position of current layout
     * scroll to the destination page.
     */
    public void snapToDestination() {
   
    	//Log.e(TAG, "snapToDestination()");		    

    	final int screenWidth = getWidth();
    	int destScreen = (getScrollX()+ (screenWidth*2/3))/screenWidth;
    	destScreen= destScreen % getChildCount();
    	snapToScreen(destScreen);
    }
    
    public void snapToScreen(int whichScreen) {
    	// get the valid layout page
    	// 	Log.e(TAG, "before whichScreen="+whichScreen);		
    	whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));
	    //	Log.e(TAG, "after whichScreen="+whichScreen);		
    	int dest_x=whichScreen*getWidth();
    	if(whichScreen>0)
    		dest_x+=page_gap*whichScreen;

    	Log.e(TAG, "dest_x ="+dest_x+" , getWidth()="+getWidth());

		
		page=whichScreen;
		Log.e(TAG, "page :"+page);	
		pageListener.page(page); 

	    //Log.d(TAG, "getScrollX() ="+getScrollX());	
//huyanwei modify it  {
/*    	if (getScrollX() != dest_x) {
    		
    		final int delta = dest_x-getScrollX();
    		//mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta)*2);
    		mScroller.startScroll(getScrollX(), 0, delta, 0, 50);
    		mCurScreen = whichScreen;
    		invalidate();		// Redraw the layout
    	}
*/
	setToScreen(page); 
//huyanwei modify it  }

    }
    
    public void setToScreen(int whichScreen) {
    	whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));
    	mCurScreen = whichScreen;
    	scrollTo(whichScreen*getWidth(), 0);
    }
    
    public int getCurScreen() {
    	return mCurScreen;
    }
	public int getPage() {
		return page;
	}   
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub

		//Log.d(TAG,"computeScroll() ========================= begin");
		
		if (mScroller.computeScrollOffset()) {
			//Log.d(TAG,"computeScroll() AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
		else
		{
			//Log.d(TAG,"computeScroll() BBBBBBBBBBBBBBBBBBBBBBB");
		}		
		
		//Log.d(TAG,"computeScroll() ========================= end");		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.e(TAG, "event down!");
            		mActivePointerId = event.getPointerId(0);
			if (!mScroller.isFinished()){
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			break;
			
		case MotionEvent.ACTION_MOVE:
            		final int pointerIndex = event.findPointerIndex(mActivePointerId);
			int deltaX = (int)(mLastMotionX - x);
			mLastMotionX = x;
			
            scrollBy(deltaX, 0);
			break;
			
		case MotionEvent.ACTION_UP:
			Log.e(TAG, "event : up");   
            // if (mTouchState == TOUCH_STATE_SCROLLING) {   
            final VelocityTracker velocityTracker = mVelocityTracker;   
            velocityTracker.computeCurrentVelocity(6000);   
            int velocityX = (int) velocityTracker.getXVelocity();   

            //Log.e(TAG, "velocityX="+velocityX); 
            if (velocityX > SNAP_VELOCITY )
	        {   		 				
			// Fling enough to move left   
			Log.e(TAG, "snap left");
			if(mCurScreen > 0)				
				snapToScreen(mCurScreen - 1);   
			else if(mCurScreen == 0)
				snapToScreen(getChildCount() - 1);   
			else
			{
				Log.e(TAG, "do nothing");			
			}
		
            }
	     else if (velocityX < -SNAP_VELOCITY )
	    { 
                // Fling enough to move right                   
	            	Log.e(TAG, "snap right");
			if(mCurScreen < (getChildCount() - 1))
				snapToScreen(mCurScreen + 1); 
			else if(mCurScreen == (getChildCount() - 1))
				snapToScreen(0); 
			else
			{
		            	Log.e(TAG, "do nothing");			
			}			
            } 
	     else
	     {   
                snapToDestination();   
            }               

            if (mVelocityTracker != null) {   
                mVelocityTracker.recycle();   
                mVelocityTracker = null;   
            }   
            // }   
            mTouchState = TOUCH_STATE_REST;              
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onInterceptTouchEvent-slop:"+mTouchSlop);
		
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}
		
		final float x = ev.getX();
		final float y = ev.getY();
		
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int)Math.abs(mLastMotionX-x);
			if (xDiff>mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;
				
			}
			break;
			
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			mTouchState = mScroller.isFinished()? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
			break;
			
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		
		return mTouchState != TOUCH_STATE_REST;
	}
	public void setPageListener(PageListener pageListener) {
		this.pageListener = pageListener;
	}	
    
	public interface PageListener {
		void page(int page);
	}
	
	public void setCurPage(int page) {
		Log.e(TAG, "setCurPage:PageCount="+PageCount);
		final Integer index_img[] = 
			{
				R.drawable.page1,
				R.drawable.page2,
				R.drawable.page3,
				R.drawable.page4,
				R.drawable.page5,
				R.drawable.page6,
				R.drawable.page7,
				R.drawable.page8,
				R.drawable.page9,
				R.drawable.page10,
				R.drawable.page11,
				R.drawable.page12,
				R.drawable.page13,
				R.drawable.page14,
				R.drawable.page15,
				R.drawable.page16,
				R.drawable.page17,			
				R.drawable.page18,
				R.drawable.page19,
				R.drawable.page20				
			};

		final Integer index_img_bg[] = 
			{
				R.drawable.page_point1,
				R.drawable.page_point2,
				R.drawable.page_point3,
				R.drawable.page_point4,
				R.drawable.page_point5,
				R.drawable.page_point6,
				R.drawable.page_point7,
				R.drawable.page_point8,
				R.drawable.page_point9,
				R.drawable.page_point10,
				R.drawable.page_point11,
				R.drawable.page_point12,
				R.drawable.page_point13,
				R.drawable.page_point14,
				R.drawable.page_point15,
				R.drawable.page_point16,
				R.drawable.page_point17,			
				R.drawable.page_point18,
				R.drawable.page_point19,
				R.drawable.page_point20				
			};


		//mScreenIndicator.setLevel(page);		
		Resources resource= getResources();
		
		mIndicator.removeAllViews();
		for (int i = 0; i < PageCount; i++) {
			ImageView imgCur = new ImageView(getContext());
			imgCur.setId(i);	
			if (imgCur.getId() == page)
			{
				//imgCur.setImageResource(index_img[i]);
				imgCur.setImageDrawable(Launcher.zoomDrawable(resource.getDrawable(index_img[i]),homescreen_indication_width,homescreen_indication_height));
			}
			else
			{
				
				//imgCur.setImageResource(R.drawable.page_point);
				imgCur.setImageDrawable(Launcher.zoomDrawable(resource.getDrawable(R.drawable.page_point),homescreen_indication_width,homescreen_indication_height));
				
				//imgCur.setImageResource(index_img_bg[i]);
			}
			mIndicator.addView(imgCur);
		}	
		}

	/* (non-Javadoc)
	 * @see android.view.ViewGroup#dispatchDraw(android.graphics.Canvas)
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		//super.dispatchDraw(canvas);
	        boolean restore = false;
	        int restoreCount = 0;

	    // ViewGroup.dispatchDraw() supports many features we don't need:
	    // clip to padding, layout animation, animation listener, disappearing
	    // children, etc. The following implementation attempts to fast-track
	    // the drawing dispatch by drawing only what we know needs to be drawn.
		
	    boolean fastDraw = false;
        //boolean fastDraw = (mTouchState != TOUCH_STATE_SCROLLING) && (mNextScreen == INVALID_SCREEN);
        // If we are not scrolling or flinging, draw only the current screen

        //Log.d(TAG,"dispatchDraw() ========================begin");

        if (fastDraw) {
            drawChild(canvas, getChildAt(mCurScreen), getDrawingTime());
        } else {
            long drawingTime = getDrawingTime();
            int width = getWidth();
            float scrollPos = (float) getScrollX() / width;
            boolean endlessScrolling = true;

            int leftScreen;
            int rightScreen;
            boolean isScrollToRight = false;
            int childCount = getChildCount();
            if (scrollPos < 0 && endlessScrolling) {
                leftScreen = childCount - 1;
                rightScreen = 0;
            } else {
                leftScreen = Math.min( (int) scrollPos, childCount - 1 );
                rightScreen = leftScreen + 1;
                if (endlessScrolling) {
                    rightScreen = rightScreen % childCount;
                    isScrollToRight = true;
                }
            }

            if (isScreenNoValid(leftScreen)) {
                if (rightScreen == 0 && !isScrollToRight) {
                    int offset = childCount * width;
	             	//Log.d(TAG,"dispatchDraw()  111111111111111111111111");
                    canvas.translate(-offset, 0);
                    drawChild(canvas, getChildAt(leftScreen), drawingTime);
                    canvas.translate(+offset, 0);
                } else {
     	             //Log.d(TAG,"dispatchDraw()  222222222222222222222222");
                    drawChild(canvas, getChildAt(leftScreen), drawingTime);
                }
            }
            if (scrollPos != leftScreen && isScreenNoValid(rightScreen)) {
                if (endlessScrolling && rightScreen == 0  && isScrollToRight) {
	             	 //Log.d(TAG,"dispatchDraw()  333333333333333333333333");					
                     int offset = childCount * width;
                     canvas.translate(+offset, 0);
                     drawChild(canvas, getChildAt(rightScreen), drawingTime);
                     canvas.translate(-offset, 0);
                } else {
	             	//Log.d(TAG,"dispatchDraw()  444444444444444444444444");					                
                    drawChild(canvas, getChildAt(rightScreen), drawingTime);
                }
            }
      	    }
           //Log.d(TAG,"dispatchDraw() ========================end");
	}


private boolean isScreenNoValid(int screen) 
	{
	   return screen >= 0 && screen < getChildCount(); 
	}	
}


