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

import java.util.ArrayList;
import java.util.HashSet;


import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.IMTKWidget;

import com.android.launcher3.R;

/**
 * The workspace is a wide area with a wallpaper and a finite number of screens. Each
 * screen contains a number of icons, folders or widgets the user can interact with.
 * A workspace is meant to be used with a fixed width only.
 */
public class Workspace extends ViewGroup implements DropTarget, DragSource, DragScroller {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final String TAG = "srclib.huyanwei.launcher.Workspace";
    private static final boolean DEBUG = false;
    private static int COUNT_UPDATE_WALLPAPER = 0;
    private static final boolean ENABLE_GOOGLE_SMOOTH = true;
    
    //private static final int INVALID_SCREEN = -1;
    private static final int INVALID_SCREEN = -999;
    
    /**
     * The velocity at which a fling gesture will cause us to snap to the next screen
     */
    private static final int SNAP_VELOCITY = 600;

    private final WallpaperManager mWallpaperManager;
    
    private Paint mPaint;
    private Bitmap mWallpaper;

    private int mWallpaperWidth;
    private int mWallpaperHeight;
    private float mWallpaperOffset;
    private boolean mWallpaperLoaded;
    private boolean mStaticWallpaper;
    
    private int mDefaultScreen;

    private boolean mFirstLayout = true;

    private int mCurrentScreen;
    private int mNextScreen = INVALID_SCREEN;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    /**
     * CellInfo for the cell that is currently being dragged
     */
    private CellLayout.CellInfo mDragInfo;
    
    /**
     * Target drop area calculated during last acceptDrop call.
     */
    private int[] mTargetCell = null;

    private float mLastMotionX;
    private float mLastMotionY;
    
    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;

    private int mTouchState = TOUCH_STATE_REST;

    private OnLongClickListener mLongClickListener;

    private Launcher mLauncher;
    private IconCache mIconCache;
    private DragController mDragController;
    
    /**
     * Cache of vacant cells, used during drag events and invalidated as needed.
     */
    private CellLayout.CellInfo mVacantCache = null;
    
    private int[] mTempCell = new int[2];
    private int[] mTempEstimate = new int[2];

    private boolean mAllowLongPress = true;

    private int mTouchSlop;
    private int mMaximumVelocity;
    
    private static final int INVALID_POINTER = -1;

    private int mActivePointerId = INVALID_POINTER;
    
    private LinearLayout mHomeScreenIndicator;
    
    //huyanwei {
    private static int last_page = 0;
    private static final Object mMutex = new Object(); 
    private static int homescreen_indication_width = 31;
    private static int homescreen_indication_height = 31;
    //huyanwei }

    /* for google design to draw smoothly ,but effect is not good ,skip it */
    private static final float NANOTIME_DIV = 1000000000.0f;
    private static final float SMOOTHING_SPEED = 0.75f;
    private static final float SMOOTHING_CONSTANT = (float) (0.016 / Math.log(SMOOTHING_SPEED));
    private float mSmoothingTime;
    private float mTouchX;

    private WorkspaceOvershootInterpolator mScrollInterpolator;

    private static final float BASELINE_FLING_VELOCITY = 2500.f;
    private static final float FLING_VELOCITY_INFLUENCE = 0.4f;
    private static boolean canSendMessage = true;
    
    private static class WorkspaceOvershootInterpolator implements Interpolator {
        private static final float DEFAULT_TENSION = 1.3f;
        private float mTension;

        public WorkspaceOvershootInterpolator() {
            mTension = DEFAULT_TENSION;
        }
        
        public void setDistance(int distance) {
            mTension = distance > 0 ? DEFAULT_TENSION / distance : DEFAULT_TENSION;
        }

        public void disableSettle() {
            mTension = 0.f;
        }

        public float getInterpolation(float t) {
            // _o(t) = t * t * ((tension + 1) * t + tension)
            // o(t) = _o(t - 1) + 1
            t -= 1.0f;
            return t * t * ((mTension + 1) * t + mTension) + 1.0f;
        }
    }
    
    /**
     * Used to inflate the Workspace from XML.
     *
     * @param context The application's context.
     * @param attrs The attribtues set containing the Workspace's customization values.
     */
    public Workspace(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Used to inflate the Workspace from XML.
     *
     * @param context The application's context.
     * @param attrs The attribtues set containing the Workspace's customization values.
     * @param defStyle Unused.
     */
    public Workspace(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mWallpaperManager = WallpaperManager.getInstance(context);
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Workspace, defStyle, 0);
        mDefaultScreen = a.getInt(R.styleable.Workspace_defaultScreen, 1);
        a.recycle();

        setHapticFeedbackEnabled(false);
        initWorkspace();
    }

    /**
     * Initializes various states for this workspace.
     */
    private void initWorkspace() {
        Context context = getContext();
        mScrollInterpolator = new WorkspaceOvershootInterpolator();
        mScroller = new Scroller(context, mScrollInterpolator);
        mCurrentScreen = mDefaultScreen;
        Launcher.setScreen(mCurrentScreen);
        LauncherApplication app = (LauncherApplication)context.getApplicationContext();
        mIconCache = app.getIconCache();

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        //huyanwei {
        final Resources resources = getResources();
        homescreen_indication_width = resources.getDimensionPixelSize(R.dimen.homescreen_indication_width);
        homescreen_indication_height = resources.getDimensionPixelSize(R.dimen.homescreen_indication_height);
        
        //homescreen_indication_width = Math.min(homescreen_indication_width, 31);
        //homescreen_indication_height = Math.min(homescreen_indication_height, 31);	
        //huyanwei }
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        if (!(child instanceof CellLayout)) {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        }
        super.addView(child, index, params);
    }
    
	void loadWallpaper(boolean staticWallpaper) {
		mStaticWallpaper = staticWallpaper;
		if (!mStaticWallpaper) {
			if (mWallpaper != null)
				mWallpaper.recycle();
			mWallpaper = null;
			mWallpaperLoaded = false;
		} else {
			Drawable localDrawable = mWallpaperManager.getDrawable();
			if ((localDrawable instanceof BitmapDrawable)) {
				if (mWallpaper != null)
					mWallpaper.recycle();
				Bitmap localBitmap = ((BitmapDrawable) localDrawable)
						.getBitmap();
				mWallpaper = localBitmap;
				mWallpaperLoaded = true;
			}
			requestLayout();
			invalidate();
		}
	}

    @Override
    public void addView(View child) {
        if (!(child instanceof CellLayout)) {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        }
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (!(child instanceof CellLayout)) {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        }
        super.addView(child, index);
    }

    @Override
    public void addView(View child, int width, int height) {
        if (!(child instanceof CellLayout)) {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        }
        super.addView(child, width, height);
    }

    @Override
    public void addView(View child, LayoutParams params) {
        if (!(child instanceof CellLayout)) {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        }
        super.addView(child, params);
    }

    /**
     * @return The open folder on the current screen, or null if there is none
     */
    Folder getOpenFolder() {
        CellLayout currentScreen = (CellLayout) getChildAt(mCurrentScreen);
        int count = currentScreen.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = currentScreen.getChildAt(i);
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
            if (lp.cellHSpan == 4 && lp.cellVSpan == 4 && child instanceof Folder) {
                return (Folder) child;
            }
        }
        return null;
    }

    ArrayList<Folder> getOpenFolders() {
        final int screens = getChildCount();
        ArrayList<Folder> folders = new ArrayList<Folder>(screens);

        for (int screen = 0; screen < screens; screen++) {
            CellLayout currentScreen = (CellLayout) getChildAt(screen);
            int count = currentScreen.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = currentScreen.getChildAt(i);
                CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
                if (lp.cellHSpan == 4 && lp.cellVSpan == 4 && child instanceof Folder) {
                    folders.add((Folder) child);
                    break;
                }
            }
        }

        return folders;
    }

    boolean isDefaultScreenShowing() {
        return mCurrentScreen == mDefaultScreen;
    }

    /**
     * Returns the index of the currently displayed screen.
     *
     * @return The index of the currently displayed screen.
     */
    int getCurrentScreen() {
        return mCurrentScreen;
    }

    /**
     * Sets the current screen.
     *
     * @param currentScreen
     */
    void setCurrentScreen(int currentScreen) {
        if (!mScroller.isFinished()) mScroller.abortAnimation();
        clearVacantCache();
        mCurrentScreen = Math.max(0, Math.min(currentScreen, getChildCount() - 1));
        if ( mLauncher.isScreenPortrait() ) {        
        	setHomeScreenPage(mCurrentScreen);
        } else {
        	setHomeScreenPage(Launcher.SCREEN_COUNT+1);
        }
        
        //Log.d(TAG,"currentScreen="+currentScreen);
        
        scrollTo(mCurrentScreen * getWidth(), 0);
        updateWallpaperOffset();
        invalidate();
    }

    /**
     * Adds the specified child in the current screen. The position and dimension of
     * the child are defined by x, y, spanX and spanY.
     *
     * @param child The child to add in one of the workspace's screens.
     * @param x The X position of the child in the screen's grid.
     * @param y The Y position of the child in the screen's grid.
     * @param spanX The number of cells spanned horizontally by the child.
     * @param spanY The number of cells spanned vertically by the child.
     */
    void addInCurrentScreen(View child, int x, int y, int spanX, int spanY) {
        addInScreen(child, mCurrentScreen, x, y, spanX, spanY, false);
    }

    /**
     * Adds the specified child in the current screen. The position and dimension of
     * the child are defined by x, y, spanX and spanY.
     *
     * @param child The child to add in one of the workspace's screens.
     * @param x The X position of the child in the screen's grid.
     * @param y The Y position of the child in the screen's grid.
     * @param spanX The number of cells spanned horizontally by the child.
     * @param spanY The number of cells spanned vertically by the child.
     * @param insert When true, the child is inserted at the beginning of the children list.
     */
    void addInCurrentScreen(View child, int x, int y, int spanX, int spanY, boolean insert) {
        addInScreen(child, mCurrentScreen, x, y, spanX, spanY, insert);
    }

    /**
     * Adds the specified child in the specified screen. The position and dimension of
     * the child are defined by x, y, spanX and spanY.
     *
     * @param child The child to add in one of the workspace's screens.
     * @param screen The screen in which to add the child.
     * @param x The X position of the child in the screen's grid.
     * @param y The Y position of the child in the screen's grid.
     * @param spanX The number of cells spanned horizontally by the child.
     * @param spanY The number of cells spanned vertically by the child.
     */
    void addInScreen(View child, int screen, int x, int y, int spanX, int spanY) {
        addInScreen(child, screen, x, y, spanX, spanY, false);
    }

    /**
     * Adds the specified child in the specified screen. The position and dimension of
     * the child are defined by x, y, spanX and spanY.
     *
     * @param child The child to add in one of the workspace's screens.
     * @param screen The screen in which to add the child.
     * @param x The X position of the child in the screen's grid.
     * @param y The Y position of the child in the screen's grid.
     * @param spanX The number of cells spanned horizontally by the child.
     * @param spanY The number of cells spanned vertically by the child.
     * @param insert When true, the child is inserted at the beginning of the children list.
     */
    void addInScreen(View child, int screen, int x, int y, int spanX, int spanY, boolean insert) {
        if (screen < 0 || screen >= getChildCount()) {
            Log.e(TAG, "The screen must be >= 0 and < " + getChildCount()
                + " (was " + screen + "); skipping child");
            return;
        }

        clearVacantCache();

        final CellLayout group = (CellLayout) getChildAt(screen);
        CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
        if (lp == null) {
            lp = new CellLayout.LayoutParams(x, y, spanX, spanY);
        } else {
            lp.cellX = x;
            lp.cellY = y;
            lp.cellHSpan = spanX;
            lp.cellVSpan = spanY;
        }
        group.addView(child, insert ? 0 : -1, lp);
        if (!(child instanceof Folder)) {
            child.setHapticFeedbackEnabled(false);
            child.setOnLongClickListener(mLongClickListener);
        }
        if (child instanceof DropTarget) {
            mDragController.addDropTarget((DropTarget)child);
        }
        
        View widgetView = searchIMTKWidget(child);
        if ( widgetView != null) {
        	((IMTKWidget)widgetView).setScreen(screen);
        }
    }

    CellLayout.CellInfo findAllVacantCells(boolean[] occupied) {
        CellLayout group = (CellLayout) getChildAt(mCurrentScreen);
        if (group != null) {
            return group.findAllVacantCells(occupied, null);
        }
        return null;
    }

    private void clearVacantCache() {
        if (mVacantCache != null) {
            mVacantCache.clearVacantCells();
            mVacantCache = null;
        }
    }

    /**
     * Registers the specified listener on each screen contained in this workspace.
     *
     * @param l The listener used to respond to long clicks.
     */
    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        mLongClickListener = l;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).setOnLongClickListener(l);
        }
    }

    private void updateWallpaperOffset() {
    	if (DEBUG) {
    		COUNT_UPDATE_WALLPAPER++;
    	}
    	
		if (!mStaticWallpaper) {
			updateWallpaperOffset(getChildAt(getChildCount() - 1).getRight() - (mRight - mLeft));
		}
    }

    private void updateWallpaperOffset(int scrollRange) {
		if (!mStaticWallpaper) {
			IBinder token = getWindowToken();
			if (token != null) {
				mWallpaperManager.setWallpaperOffsetSteps(
						1.0f / (getChildCount() - 1), 0);
				mWallpaperManager.setWallpaperOffsets(getWindowToken(),
						Math.max(0.f, Math.min(mScrollX / (float) scrollRange, 1.f)), 0);
			}
		}
    }
    
    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        if(ENABLE_GOOGLE_SMOOTH) {
            mTouchX = x;
            mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
        }
    }
    
    @Override
    public void computeScroll() {

		//Log.d(TAG,"computeScroll() mNextScreen="+mNextScreen);
	
        if (mScroller.computeScrollOffset()) {
            if(ENABLE_GOOGLE_SMOOTH) {
        		mTouchX = mScrollX = mScroller.getCurrX();
        		mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
            } else {
				mScrollX = mScroller.getCurrX();
            }
            mScrollY = mScroller.getCurrY();
            super.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            updateWallpaperOffset();
            postInvalidate();
	   } else if (mNextScreen != INVALID_SCREEN) {
            		View whichHostView = getChildAt(mNextScreen);
			View whichMtkWidgetView = searchIMTKWidget(whichHostView);
			if (whichMtkWidgetView != null) {
				((IMTKWidget) whichMtkWidgetView).moveIn(mNextScreen);
				canSendMessage = true;
				if (Launcher.DEBUG_SURFACEWIDGET) Log.e(Launcher.TAG_SURFACEWIDGET, "moveIn");
			}
			
			//Log.d(TAG,"CCCCCCCCCCCCCCCCCCCCC");
			//Log.d(TAG,"computeScroll() mNextScreen="+mNextScreen);			
			
        	if (mNextScreen == -1 ) {
        		mCurrentScreen = getChildCount() - 1;
        		scrollTo(mCurrentScreen * getWidth(), getScrollY());
        		updateWallpaperOffset();
        	} else if (mNextScreen == getChildCount()) {
        		mCurrentScreen = 0;
        		scrollTo(0, getScrollY());
        		updateWallpaperOffset();
        	} else			
            	mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
            
            //Log.d(TAG,"computeScroll()  mCurrentScreen=" + mCurrentScreen);
            
            if ( mLauncher.isScreenPortrait() ) {
            	setHomeScreenPage(mCurrentScreen);            	
            } else {           	
            	setHomeScreenPage(Launcher.SCREEN_COUNT+1);
            }
            
            Launcher.setScreen(mCurrentScreen);
            mNextScreen = INVALID_SCREEN;
            clearChildrenCache();
        } else if (ENABLE_GOOGLE_SMOOTH && mTouchState == TOUCH_STATE_SCROLLING) {
            final float now = System.nanoTime() / NANOTIME_DIV;
            final float e = (float) Math.exp((now - mSmoothingTime) / SMOOTHING_CONSTANT);
            final float dx = mTouchX - mScrollX;
            mScrollX += dx * e;
            mSmoothingTime = now;

            // Keep generating points as long as we're more than 1px away from the target
            if (dx > 1.f || dx < -1.f) {
                updateWallpaperOffset();
                postInvalidate();
            }
        }
    }


private boolean isScreenNoValid(int screen) 
	{
	   return screen >= 0 && screen < getChildCount(); 
	}


    @Override
    protected void dispatchDraw(Canvas canvas) {
        boolean restore = false;
        int restoreCount = 0;

        // ViewGroup.dispatchDraw() supports many features we don't need:
        // clip to padding, layout animation, animation listener, disappearing
        // children, etc. The following implementation attempts to fast-track
        // the drawing dispatch by drawing only what we know needs to be drawn.
		
        if (mStaticWallpaper) {
			float x = mScrollX * mWallpaperOffset;
			if (x + mWallpaperWidth < mRight - mLeft) {
				x = mRight - mLeft - mWallpaperWidth;
			}
			canvas.drawBitmap(mWallpaper, x,
					(mBottom - mTop - mWallpaperHeight) / 2, mPaint);
		}

        // boolean fastDraw = false;
        boolean fastDraw = (mTouchState != TOUCH_STATE_SCROLLING) && (mNextScreen == INVALID_SCREEN);
        // If we are not scrolling or flinging, draw only the current screen
//huyanwei modify {
/*        
        if (fastDraw) {
            drawChild(canvas, getChildAt(mCurrentScreen), getDrawingTime());
        } else {
            final long drawingTime = getDrawingTime();
            final float scrollPos = (float) mScrollX / getWidth();
            final int leftScreen = (int) scrollPos;
            final int rightScreen = leftScreen + 1;			
            if (leftScreen >= 0) {
                drawChild(canvas, getChildAt(leftScreen), drawingTime);
            }
            if (scrollPos != leftScreen && rightScreen < getChildCount()) {
                drawChild(canvas, getChildAt(rightScreen), drawingTime);
            }
        }
*/
        //Log.d(TAG,"dispatchDraw() ========================begin");

        if (fastDraw) {
            drawChild(canvas, getChildAt(mCurrentScreen), getDrawingTime());
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
                    canvas.translate(-offset, 0);
                    drawChild(canvas, getChildAt(leftScreen), drawingTime);
                    canvas.translate(+offset, 0);
                } else {
                    drawChild(canvas, getChildAt(leftScreen), drawingTime);
                }
            }
            if (scrollPos != leftScreen && isScreenNoValid(rightScreen)) {
                if (endlessScrolling && rightScreen == 0  && isScrollToRight) {
                     int offset = childCount * width;
                     canvas.translate(+offset, 0);
                     drawChild(canvas, getChildAt(rightScreen), drawingTime);
                     canvas.translate(-offset, 0);
                } else {
                    drawChild(canvas, getChildAt(rightScreen), drawingTime);
                }
            }
      	    }
           //Log.d(TAG,"dispatchDraw() ========================end");
				 
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        computeScroll();
        mDragController.setWindowToken(getWindowToken());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Workspace can only be used in EXACTLY mode.");
        }

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Workspace can only be used in EXACTLY mode.");
        }

        // The children are given the same width and height as the workspace
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }

		if (mStaticWallpaper) {
			if (mWallpaperLoaded) {
				mWallpaperLoaded = false;
				mWallpaper = Utilities.centerToFit(mWallpaper, width,
						MeasureSpec.getSize(heightMeasureSpec), getContext());
				mWallpaperWidth = mWallpaper.getWidth();
				mWallpaperHeight = mWallpaper.getHeight();
			}
	        final int wallpaperWidth = mWallpaperWidth;
	        mWallpaperOffset = wallpaperWidth > width ? (count * width - wallpaperWidth) /
	                ((count - 1) * (float) width) : 1.0f;
		}

        if (mFirstLayout) {
            setHorizontalScrollBarEnabled(false);
            scrollTo(mCurrentScreen * width, 0);
            setHorizontalScrollBarEnabled(true);
            updateWallpaperOffset(width * (getChildCount() - 1));
            mFirstLayout = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childLeft = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        int screen = indexOfChild(child);
        if (screen != mCurrentScreen || !mScroller.isFinished()) {
            if (!mLauncher.isWorkspaceLocked()) {
                snapToScreen(screen);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        if (!mLauncher.isAllAppsVisible()) {
            final Folder openFolder = getOpenFolder();
            if (openFolder != null) {
                return openFolder.requestFocus(direction, previouslyFocusedRect);
            } else {
                int focusableScreen;
                if (mNextScreen != INVALID_SCREEN) {
                    focusableScreen = mNextScreen;
                } else {
                    focusableScreen = mCurrentScreen;
                }
		// huyanwei add it {
		if(getChildAt(focusableScreen) != null)
		// huyanwei add it }
                {
			getChildAt(focusableScreen).requestFocus(direction, previouslyFocusedRect);
		}
            }
        }
        return false;
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        if (direction == View.FOCUS_LEFT) {
            if (getCurrentScreen() > 0) {
                snapToScreen(getCurrentScreen() - 1);
                return true;
            }
        } else if (direction == View.FOCUS_RIGHT) {
            if (getCurrentScreen() < getChildCount() - 1) {
                snapToScreen(getCurrentScreen() + 1);
                return true;
            }
        }
        return super.dispatchUnhandledMove(focused, direction);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (!mLauncher.isAllAppsVisible()) {
            final Folder openFolder = getOpenFolder();
            if (openFolder == null) {
                getChildAt(mCurrentScreen).addFocusables(views, direction);
                if (direction == View.FOCUS_LEFT) {
                    if (mCurrentScreen > 0) {
                        getChildAt(mCurrentScreen - 1).addFocusables(views, direction);
                    }
                } else if (direction == View.FOCUS_RIGHT){
                    if (mCurrentScreen < getChildCount() - 1) {
                        getChildAt(mCurrentScreen + 1).addFocusables(views, direction);
                    }
                }
            } else {
                openFolder.addFocusables(views, direction);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (mLauncher.isWorkspaceLocked() || mLauncher.isAllAppsVisible()) {
                return false;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final boolean workspaceLocked = mLauncher.isWorkspaceLocked();
        final boolean allAppsVisible = mLauncher.isAllAppsVisible();
        if (workspaceLocked || allAppsVisible) {
            return false; // We don't want the events.  Let them fall through to the all apps view.
        }

        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */

        /*
         * Shortcut the most recurring case: the user is in the dragging
         * state and he is moving his finger.  We want to intercept this
         * motion.
         */
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }

        acquireVelocityTrackerAndAddMovement(ev);
        
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE: {
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */

                /*
                 * Locally do absolute value. mLastMotionX is set to the y value
                 * of the down event.
                 */
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                final int yDiff = (int) Math.abs(y - mLastMotionY);

                final int touchSlop = mTouchSlop;
                boolean xMoved = xDiff > touchSlop;
                boolean yMoved = yDiff > touchSlop;
                
                if (xMoved || yMoved) {
                    
                    if (xMoved) {
                        // Scroll if the user moved far enough along the X axis
                        mTouchState = TOUCH_STATE_SCROLLING;
                        if(ENABLE_GOOGLE_SMOOTH) {
                            mLastMotionX = x;
                            mTouchX = mScrollX;
                            mSmoothingTime = System.nanoTime() / NANOTIME_DIV;	
                        }                    
                        enableChildrenCache(mCurrentScreen - 1, mCurrentScreen + 1);
                    }
                    // Either way, cancel any pending longpress
                    if (mAllowLongPress) {
                        mAllowLongPress = false;
                        // Try canceling the long press. It could also have been scheduled
                        // by a distant descendant, so use the mAllowLongPress flag to block
                        // everything
                        final View currentScreen = getChildAt(mCurrentScreen);
                        currentScreen.cancelLongPress();
                    }
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                // Remember location of down touch
                mLastMotionX = x;
                mLastMotionY = y;
                mActivePointerId = ev.getPointerId(0);
                mAllowLongPress = true;

                /*
                 * If being flinged and user touches the screen, initiate drag;
                 * otherwise don't.  mScroller.isFinished should be false when
                 * being flinged.
                 */
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                
                if (mTouchState != TOUCH_STATE_SCROLLING) {
                    final CellLayout currentScreen = (CellLayout)getChildAt(mCurrentScreen);
                    if (!currentScreen.lastDownOnOccupiedCell()) {
                        getLocationOnScreen(mTempCell);
                        // Send a tap to the wallpaper if the last down was on empty space
    					if (!mStaticWallpaper) {
    						final int pointerIndex = ev.findPointerIndex(mActivePointerId);
    						if (pointerIndex >= 0) {
        						mWallpaperManager.sendWallpaperCommand(
        								getWindowToken(), "android.wallpaper.tap",
        								mTempCell[0] + (int) ev.getX(pointerIndex),
        								mTempCell[1] + (int) ev.getY(pointerIndex), 0,
        								null);
    						}
    					}
                    }
                }
                
                // Release the drag
                clearChildrenCache();
                mTouchState = TOUCH_STATE_REST;
                mActivePointerId = INVALID_POINTER;
                mAllowLongPress = false;
                releaseVelocityTracker();
                break;
                
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }

        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        return mTouchState != TOUCH_STATE_REST;
    }
    
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = ev.getX(newPointerIndex);
            mLastMotionY = ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    /**
     * If one of our descendant views decides that it could be focused now, only
     * pass that along if it's on the current screen.
     *
     * This happens when live folders requery, and if they're off screen, they
     * end up calling requestFocus, which pulls it on screen.
     */
    @Override
    public void focusableViewAvailable(View focused) {
        View current = getChildAt(mCurrentScreen);
        View v = focused;
        while (true) {
            if (v == current) {
                super.focusableViewAvailable(focused);
                return;
            }
            if (v == this) {
                return;
            }
            ViewParent parent = v.getParent();
            if (parent instanceof View) {
                v = (View)v.getParent();
            } else {
                return;
            }
        }
    }

	public void hideWallpaper(boolean hide) {
		if (!mStaticWallpaper) {
			IBinder windowToken = getWindowToken();
			if (windowToken != null) {
				if (hide) {
					mWallpaperManager.sendWallpaperCommand(windowToken, "hide",
							0, 0, 0, null);
				} else {
					mWallpaperManager.sendWallpaperCommand(windowToken, "show",
							0, 0, 0, null);
				}
			}
		}	
	}

    void enableChildrenCache(int fromScreen, int toScreen) {
        if (fromScreen > toScreen) {
            final int temp = fromScreen;
            fromScreen = toScreen;
            toScreen = temp;
        }
        
        final int count = getChildCount();

        fromScreen = Math.max(fromScreen, 0);
        toScreen = Math.min(toScreen, count - 1);

        for (int i = fromScreen; i <= toScreen; i++) {
            final CellLayout layout = (CellLayout) getChildAt(i);
            layout.setChildrenDrawnWithCacheEnabled(true);
            layout.setChildrenDrawingCacheEnabled(true);
        }
    }

    void clearChildrenCache() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final CellLayout layout = (CellLayout) getChildAt(i);
            layout.setChildrenDrawnWithCacheEnabled(false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        
        if (mLauncher.isWorkspaceLocked()) {
            return false; // We don't want the events.  Let them fall through to the all apps view.
        }
        if (mLauncher.isAllAppsVisible()) {
            // Cancel any scrolling that is in progress.
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            snapToScreen(mCurrentScreen);
            return false; // We don't want the events.  Let them fall through to the all apps view.
        }

        acquireVelocityTrackerAndAddMovement(ev);

        final int action = ev.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            /*
             * If being flinged and user touches, stop the fling. isFinished
             * will be false if being flinged.
             */
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }

            // Remember where the motion event started
            mLastMotionX = ev.getX();
            mActivePointerId = ev.getPointerId(0);
            if (mTouchState == TOUCH_STATE_SCROLLING) {
                enableChildrenCache(mCurrentScreen - 1, mCurrentScreen + 1);
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if (canSendMessage && ! mDragController.isDraging() ) {
                View currentHostView = getChildAt(mCurrentScreen);
                View currentMtkWidgetView = searchIMTKWidget(currentHostView);
                
                if (currentMtkWidgetView != null) {
                    boolean result = ((IMTKWidget) currentMtkWidgetView).moveOut(mCurrentScreen);
                    if (result == false) {
                        if (Launcher.DEBUG_SURFACEWIDGET) Log.e(Launcher.TAG_SURFACEWIDGET, "moveOut false");
                        return true;
                    }
                    canSendMessage = false;
                    if (Launcher.DEBUG_SURFACEWIDGET) Log.e(Launcher.TAG_SURFACEWIDGET, "moveOut true");
                }
            }
        	if ( !mLauncher.isScreenPortrait() ) {
                mLauncher.getIndicator().setVisibility(View.VISIBLE);
            }
            if (mTouchState == TOUCH_STATE_SCROLLING) {
                // Scroll to follow the motion event
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float deltaX = mLastMotionX - x;
                mLastMotionX = x;

                if (deltaX < 0) {
                    if (mTouchX > 0) {
                        mTouchX += Math.max(-mTouchX, deltaX);
                        mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
                        invalidate();
                    }
                    else if (mTouchX > -getWidth()) {
                    	mTouchX += deltaX;
                        mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
                        invalidate();
                    }
                } else if (deltaX > 0) {
                    final float availableToScroll = getChildAt(getChildCount() - 1).getRight() -
                            mTouchX ;
                    if (availableToScroll > 0) {
                        mTouchX += Math.min(availableToScroll, deltaX);
                        mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
                        invalidate();
                    }
                } else {
                    awakenScrollBars();
                }
            }	 	
            break;
        case MotionEvent.ACTION_UP:
            if (mTouchState == TOUCH_STATE_SCROLLING) {
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                final int velocityX = (int) velocityTracker.getXVelocity();

                //Log.d(TAG,"AAAAAAAAAAAAAAAAAAAAAAAAAA+BBBBBBBBBBBBBBBBBBBBBBBBBBBB");

		  		//Log.d(TAG,"mCurrentScreen = " + mCurrentScreen);
		 
                final int screenWidth = getWidth();
                final int whichScreen = (int)Math.floor((getScrollX() + (screenWidth *1/2.0)) / screenWidth);
                final float scrolledPos = (float) getScrollX() / screenWidth;
                if (velocityX > SNAP_VELOCITY && (mCurrentScreen > -1)) {
                    // Fling hard enough to move left.
                    // Don't fling across more than one screen at a time.
                    //Log.d(TAG,"AAAAAAAAAAAAAAAAAAAAAAAAAA");
                    final int bound = scrolledPos < whichScreen ?
                            mCurrentScreen - 1 : mCurrentScreen;
                    snapToScreen(Math.min(whichScreen, bound), velocityX, true);
                } else if (velocityX < -SNAP_VELOCITY && mCurrentScreen < getChildCount() -0) {
                    // Fling hard enough to move right
                    // Don't fling across more than one screen at a time.
                    //Log.d(TAG,"BBBBBBBBBBBBBBBBBBBBBBBBBB");                    
                    final int bound = scrolledPos > whichScreen ?
                            mCurrentScreen + 1 : mCurrentScreen;
                    snapToScreen(Math.max(whichScreen, bound), velocityX, true);
                } else {
                    snapToScreen(whichScreen, 0, true);
                }
            }
            mTouchState = TOUCH_STATE_REST;
            mActivePointerId = INVALID_POINTER;
            releaseVelocityTracker();
            break;
        case MotionEvent.ACTION_CANCEL:
            if (mTouchState == TOUCH_STATE_SCROLLING) {
                final int screenWidth = getWidth();
                final int whichScreen = (mScrollX + (screenWidth / 2)) / screenWidth;
                snapToScreen(whichScreen, 0, true);
            }
            mTouchState = TOUCH_STATE_REST;
            mActivePointerId = INVALID_POINTER;
            releaseVelocityTracker();
            break;
        case MotionEvent.ACTION_POINTER_UP:
            onSecondaryPointerUp(ev);
            break;
        }

        return true;
    }
    
    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    void snapToScreen(int whichScreen) {
        snapToScreen(whichScreen, 0, false);
    }

    private void snapToScreen(int whichScreen, int velocity, boolean settle) {
    	if (DEBUG) {
	    	Log.d(TAG, "snapToScreen COUNT_UPDATE_WALLPAPER == " + COUNT_UPDATE_WALLPAPER);
	    	COUNT_UPDATE_WALLPAPER = 0;
    	}
	 
	 	//Log.d(TAG,"before whichScreen="+whichScreen);	 
        //whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		whichScreen = Math.max(-1,Math.min(whichScreen, getChildCount() - 0));
	 	//Log.d(TAG,"after whichScreen="+whichScreen);	 
	 
        if ( !mLauncher.isScreenPortrait() ) {
            Indicator indicator = mLauncher.getIndicator();
            AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
            animation.setDuration(2000);
            animation.setAnimationListener(indicator);
            indicator.startAnimation(animation);
        
            mLauncher.getIndicator().setLastIndex(whichScreen);
        }
        
        clearVacantCache();
        enableChildrenCache(mCurrentScreen, whichScreen);

        mNextScreen = whichScreen;
	 	//Log.d(TAG,"DDDDDDDDDDDDDDDDDDDDDDDD");
        
        if ( mLauncher.isScreenPortrait() ) {
        	setHomeScreenPage(mNextScreen);
        } else {
        	setHomeScreenPage(Launcher.SCREEN_COUNT+1);
        }

        View focusedChild = getFocusedChild();
        if (focusedChild != null && whichScreen != mCurrentScreen &&
                focusedChild == getChildAt(mCurrentScreen)) {
            focusedChild.clearFocus();
        }
        
        final int screenDelta = Math.max(1, Math.abs(whichScreen - mCurrentScreen));
        final int newX = whichScreen * getWidth();
        final int delta = newX - mScrollX;
        int duration = (screenDelta + 1) * 100;

        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        
        if (settle) {
            mScrollInterpolator.setDistance(screenDelta);
        } else {
            mScrollInterpolator.disableSettle();
        }
        
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration += (duration / (velocity / BASELINE_FLING_VELOCITY))
                    * FLING_VELOCITY_INFLUENCE;
        } else {
            duration += 100;
        }

        controlMTKWidget(whichScreen);

        awakenScrollBars(duration);
        //mScroller.startScroll(mScrollX, 0, delta, 0, duration);
        mScroller.startScroll(mScrollX, 0, delta, 0, 50); // huyanwei modify it .
        invalidate();
    }

    void startDrag(CellLayout.CellInfo cellInfo) {
        View child = cellInfo.cell;
        
        // Make sure the drag was started by a long press as opposed to a long click.
        if (!child.isInTouchMode()) {
            return;
        }
        
        mDragInfo = cellInfo;
        mDragInfo.screen = mCurrentScreen;
        
        CellLayout current = ((CellLayout) getChildAt(mCurrentScreen));

        current.onDragChild(child);
        mDragController.startDrag(child, this, child.getTag(), DragController.DRAG_ACTION_MOVE);
        invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final SavedState state = new SavedState(super.onSaveInstanceState());
        state.currentScreen = mCurrentScreen;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.currentScreen != -1) {
            mCurrentScreen = savedState.currentScreen;
            Launcher.setScreen(mCurrentScreen);
        }
    }

    void addApplicationShortcut(ShortcutInfo info, CellLayout.CellInfo cellInfo) {
        addApplicationShortcut(info, cellInfo, false);
    }

    void addApplicationShortcut(ShortcutInfo info, CellLayout.CellInfo cellInfo,
            boolean insertAtFirst) {
        final CellLayout layout = (CellLayout) getChildAt(cellInfo.screen);
        final int[] result = new int[2];

        layout.cellToPoint(cellInfo.cellX, cellInfo.cellY, result);
        onDropExternal(result[0], result[1], info, layout, insertAtFirst);
    }

    public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo) {
        final CellLayout cellLayout = getCurrentDropLayout();
        if (source != this) {
            onDropExternal(x - xOffset, y - yOffset, dragInfo, cellLayout);
        } else {
            // Move internally
            if (mDragInfo != null) {
                final View cell = mDragInfo.cell;
                int index = mScroller.isFinished() ? mCurrentScreen : mNextScreen;                
                if (index != mDragInfo.screen) {
                    final CellLayout originalCellLayout = (CellLayout) getChildAt(mDragInfo.screen);
                    originalCellLayout.removeView(cell);
                    cellLayout.addView(cell);
                }
                mTargetCell = estimateDropCell(x - xOffset, y - yOffset,
                        mDragInfo.spanX, mDragInfo.spanY, cell, cellLayout, mTargetCell);
                cellLayout.onDropChild(cell, mTargetCell);
                
                View mtkWidgetView = searchIMTKWidget(cell);
                if (mtkWidgetView != null) {
                	((IMTKWidget)mtkWidgetView).stopDrag();
                    if (Launcher.DEBUG_SURFACEWIDGET) Log.e(Launcher.TAG_SURFACEWIDGET, "stopDrag");
                }

                final ItemInfo info = (ItemInfo) cell.getTag();
                CellLayout.LayoutParams lp = (CellLayout.LayoutParams) cell.getLayoutParams();
                LauncherModel.moveItemInDatabase(mLauncher, info,
                        LauncherSettings.Favorites.CONTAINER_DESKTOP, index, lp.cellX, lp.cellY);
            }
        }
    }

    public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo) {
        clearVacantCache();
	
		final Folder openFolder = getOpenFolder();
		if( openFolder != null ) {
			mLauncher.closeFolder( openFolder );
		}
    }

    public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo) {
    }

    public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo) {
        clearVacantCache();
    }

    private void onDropExternal(int x, int y, Object dragInfo, CellLayout cellLayout) {
        onDropExternal(x, y, dragInfo, cellLayout, false);
    }
    
    private void onDropExternal(int x, int y, Object dragInfo, CellLayout cellLayout,
            boolean insertAtFirst) {
        // Drag from somewhere else
        ItemInfo info = (ItemInfo) dragInfo;

        View view;

        switch (info.itemType) {
        case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
        case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
            if (info.container == NO_ID && info instanceof ApplicationInfo) {
                // Came from all apps -- make a copy
                info = new ShortcutInfo((ApplicationInfo)info);
            }
            view = mLauncher.createShortcut(R.layout.application, cellLayout, (ShortcutInfo)info);
            break;
        case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
            view = FolderIcon.fromXml(R.layout.folder_icon, mLauncher,
                    (ViewGroup) getChildAt(mCurrentScreen), ((UserFolderInfo) info));
            break;
        default:
            throw new IllegalStateException("Unknown item type: " + info.itemType);
        }
        if(view == null) {
            // do not add the view if view is null.
            return;
        }
        cellLayout.addView(view, insertAtFirst ? 0 : -1);
        view.setHapticFeedbackEnabled(false);
        view.setOnLongClickListener(mLongClickListener);
        if (view instanceof DropTarget) {
            mDragController.addDropTarget((DropTarget) view);
        }

        mTargetCell = estimateDropCell(x, y, 1, 1, view, cellLayout, mTargetCell);
        cellLayout.onDropChild(view, mTargetCell);
        CellLayout.LayoutParams lp = (CellLayout.LayoutParams) view.getLayoutParams();

        LauncherModel.addOrMoveItemInDatabase(mLauncher, info,
                LauncherSettings.Favorites.CONTAINER_DESKTOP, mCurrentScreen, lp.cellX, lp.cellY);
    }
    
    /**
     * Return the current {@link CellLayout}, correctly picking the destination
     * screen while a scroll is in progress.
     */
    private CellLayout getCurrentDropLayout() {
        int index = mScroller.isFinished() ? mCurrentScreen : mNextScreen;
        return (CellLayout) getChildAt(index);
    }

    /**
     * {@inheritDoc}
     */
    public boolean acceptDrop(DragSource source, int x, int y,
            int xOffset, int yOffset, DragView dragView, Object dragInfo) {
        final CellLayout layout = getCurrentDropLayout();
        final CellLayout.CellInfo cellInfo = mDragInfo;
        final int spanX = cellInfo == null ? 1 : cellInfo.spanX;
        final int spanY = cellInfo == null ? 1 : cellInfo.spanY;

        if (mVacantCache == null) {
            final View ignoreView = cellInfo == null ? null : cellInfo.cell;
            mVacantCache = layout.findAllVacantCells(null, ignoreView);
        }

        return mVacantCache.findCellForSpan(mTempEstimate, spanX, spanY, false);
    }
    
    /**
     * {@inheritDoc}
     */
    public Rect estimateDropLocation(DragSource source, int x, int y,
            int xOffset, int yOffset, DragView dragView, Object dragInfo, Rect recycle) {
        final CellLayout layout = getCurrentDropLayout();
        
        final CellLayout.CellInfo cellInfo = mDragInfo;
        final int spanX = cellInfo == null ? 1 : cellInfo.spanX;
        final int spanY = cellInfo == null ? 1 : cellInfo.spanY;
        final View ignoreView = cellInfo == null ? null : cellInfo.cell;
        
        final Rect location = recycle != null ? recycle : new Rect();
        
        // Find drop cell and convert into rectangle
        int[] dropCell = estimateDropCell(x - xOffset, y - yOffset,
                spanX, spanY, ignoreView, layout, mTempCell);
        
        if (dropCell == null) {
            return null;
        }
        
        layout.cellToPoint(dropCell[0], dropCell[1], mTempEstimate);
        location.left = mTempEstimate[0];
        location.top = mTempEstimate[1];
        
        layout.cellToPoint(dropCell[0] + spanX, dropCell[1] + spanY, mTempEstimate);
        location.right = mTempEstimate[0];
        location.bottom = mTempEstimate[1];
        
        return location;
    }

    /**
     * Calculate the nearest cell where the given object would be dropped.
     */
    private int[] estimateDropCell(int pixelX, int pixelY,
            int spanX, int spanY, View ignoreView, CellLayout layout, int[] recycle) {
        // Create vacant cell cache if none exists
        if (mVacantCache == null) {
            mVacantCache = layout.findAllVacantCells(null, ignoreView);
        }

        // Find the best target drop location
        return layout.findNearestVacantArea(pixelX, pixelY,
                spanX, spanY, mVacantCache, recycle);
    }
    
    void setLauncher(Launcher launcher) {
        mLauncher = launcher;
    }

    public void setDragController(DragController dragController) {
        mDragController = dragController;
    }

    public void onDropCompleted(View target, boolean success) {
        clearVacantCache();

        if (success){
            if (target != this && mDragInfo != null) {
                final CellLayout cellLayout = (CellLayout) getChildAt(mDragInfo.screen);
                cellLayout.removeView(mDragInfo.cell);
                if (mDragInfo.cell instanceof DropTarget) {
                    mDragController.removeDropTarget((DropTarget)mDragInfo.cell);
                }
                //final Object tag = mDragInfo.cell.getTag();
            }
        } else {
            if (mDragInfo != null) {
                final CellLayout cellLayout = (CellLayout) getChildAt(mDragInfo.screen);
                cellLayout.onDropAborted(mDragInfo.cell);
            }
        }

        mDragInfo = null;
    }

    protected void controlMTKWidget(int whichScreen) {
		if (canSendMessage && ! mDragController.isDraging() ) {
			View currentHostView = getChildAt(mCurrentScreen);
			View currentMtkWidgetView = searchIMTKWidget(currentHostView);
			if (currentMtkWidgetView != null) {
				((IMTKWidget) currentMtkWidgetView).moveOut(mCurrentScreen);
				canSendMessage = false;
				if (Launcher.DEBUG_SURFACEWIDGET) Log.e(Launcher.TAG_SURFACEWIDGET, "moveOut");
			}
		}
    }

    public void scrollLeft() {
        clearVacantCache();
        
        //Log.d(TAG,"scrollLeft()");
        
        if (mScroller.isFinished()) {
        	//Log.d(TAG,"mCurrentScreen="+mCurrentScreen);        	
            if (mCurrentScreen > 0) 
            	snapToScreen(mCurrentScreen - 1);
            else if(mCurrentScreen == 0)
            	snapToScreen(getChildCount() -1);
        } else {        	
        	//Log.d(TAG,"mNextScreen="+mNextScreen);        	
            if (mNextScreen > 0) 
            	snapToScreen(mNextScreen - 1);    
            else if (mNextScreen == 0)
            	snapToScreen(getChildCount() -1);
        }
    }

    public void scrollRight() {
        clearVacantCache();
        //Log.d(TAG,"scrollRight()");
        if (mScroller.isFinished()) {
        	//Log.d(TAG,"mCurrentScreen="+mCurrentScreen);  
            if (mCurrentScreen < getChildCount() -1)
            	snapToScreen(mCurrentScreen + 1);
            else if(mCurrentScreen == (getChildCount() -1))
            	snapToScreen(0);
        } else {
        	//Log.d(TAG,"mNextScreen="+mNextScreen);
            if (mNextScreen < (getChildCount() -1)) 
            	snapToScreen(mNextScreen + 1);
            else if (mNextScreen == (getChildCount() -1))
            	snapToScreen(0);
        }
    }

    public int getScreenForView(View v) {
        int result = -1;
        if (v != null) {
            ViewParent vp = v.getParent();
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                if (vp == getChildAt(i)) {
                    return i;
                }
            }
        }
        return result;
    }

    public Folder getFolderForTag(Object tag) {
        int screenCount = getChildCount();
        for (int screen = 0; screen < screenCount; screen++) {
            CellLayout currentScreen = ((CellLayout) getChildAt(screen));
            int count = currentScreen.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = currentScreen.getChildAt(i);
                CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
                if (lp.cellHSpan == 4 && lp.cellVSpan == 4 && child instanceof Folder) {
                    Folder f = (Folder) child;
                    if (f.getInfo() == tag) {
                        return f;
                    }
                }
            }
        }
        return null;
    }

    public View getViewForTag(Object tag) {
        int screenCount = getChildCount();
        for (int screen = 0; screen < screenCount; screen++) {
            CellLayout currentScreen = ((CellLayout) getChildAt(screen));
            int count = currentScreen.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = currentScreen.getChildAt(i);
                if (child.getTag() == tag) {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * @return True is long presses are still allowed for the current touch
     */
    public boolean allowLongPress() {
        return mAllowLongPress;
    }
    
    /**
     * Set true to allow long-press events to be triggered, usually checked by
     * {@link Launcher} to accept or block dpad-initiated long-presses.
     */
    public void setAllowLongPress(boolean allowLongPress) {
        mAllowLongPress = allowLongPress;
    }

    void removeItems(final ArrayList<ApplicationInfo> apps) {
        final int count = getChildCount();
        final PackageManager manager = getContext().getPackageManager();
        final AppWidgetManager widgets = AppWidgetManager.getInstance(getContext());

        final HashSet<String> packageNames = new HashSet<String>();
        final int appCount = apps.size();
        for (int i = 0; i < appCount; i++) {
            packageNames.add(apps.get(i).componentName.getPackageName());
        }

        for (int i = 0; i < count; i++) {
            final CellLayout layout = (CellLayout) getChildAt(i);

            // Avoid ANRs by treating each screen separately
            post(new Runnable() {
                public void run() {
                    final ArrayList<View> childrenToRemove = new ArrayList<View>();
                    childrenToRemove.clear();
        
                    int childCount = layout.getChildCount();
                    for (int j = 0; j < childCount; j++) {
                        final View view = layout.getChildAt(j);
                        Object tag = view.getTag();
        
                        if (tag instanceof ShortcutInfo) {
                            final ShortcutInfo info = (ShortcutInfo) tag;
                            final Intent intent = info.intent;
                            final ComponentName name = intent.getComponent();
        
                            if (Intent.ACTION_MAIN.equals(intent.getAction()) && name != null) {
                                for (String packageName: packageNames) {
                                    if (packageName.equals(name.getPackageName())) {
                                        LauncherModel.deleteItemFromDatabase(mLauncher, info);
                                        childrenToRemove.add(view);
                                    }
                                }
                            }
                        } else if (tag instanceof UserFolderInfo) {
                            final UserFolderInfo info = (UserFolderInfo) tag;
                            final ArrayList<ShortcutInfo> contents = info.contents;
                            final ArrayList<ShortcutInfo> toRemove = new ArrayList<ShortcutInfo>(1);
                            final int contentsCount = contents.size();
                            boolean removedFromFolder = false;
        
                            for (int k = 0; k < contentsCount; k++) {
                                final ShortcutInfo appInfo = contents.get(k);
                                final Intent intent = appInfo.intent;
                                final ComponentName name = intent.getComponent();
        
                                if (Intent.ACTION_MAIN.equals(intent.getAction()) && name != null) {
                                    for (String packageName: packageNames) {
                                        if (packageName.equals(name.getPackageName())) {
                                            toRemove.add(appInfo);
                                            LauncherModel.deleteItemFromDatabase(mLauncher, appInfo);
                                            removedFromFolder = true;
                                        }
                                    }
                                }
                            }
        
                            contents.removeAll(toRemove);
                            if (removedFromFolder) {
                                final Folder folder = getOpenFolder();
                                if (folder != null) folder.notifyDataSetChanged();
                            }
                        } else if (tag instanceof LiveFolderInfo) {
                            final LiveFolderInfo info = (LiveFolderInfo) tag;
                            final Uri uri = info.uri;
                            final ProviderInfo providerInfo = manager.resolveContentProvider(
                                    uri.getAuthority(), 0);

                            if (providerInfo != null) {
                                for (String packageName: packageNames) {
                                    if (packageName.equals(providerInfo.packageName)) {
                                        LauncherModel.deleteItemFromDatabase(mLauncher, info);
                                        childrenToRemove.add(view);                        
                                    }
                                }
                            }
                        } else if (tag instanceof LauncherAppWidgetInfo) {
                            final LauncherAppWidgetInfo info = (LauncherAppWidgetInfo) tag;
                            final AppWidgetProviderInfo provider =
                                    widgets.getAppWidgetInfo(info.appWidgetId);
                            if (provider != null) {
                                for (String packageName: packageNames) {
                                    if (packageName.equals(provider.provider.getPackageName())) {
                                        LauncherModel.deleteItemFromDatabase(mLauncher, info);
                                        childrenToRemove.add(view);                                
                                    }
                                }
                            }
                        }
                    }
        
                    childCount = childrenToRemove.size();
                    for (int j = 0; j < childCount; j++) {
                        View child = childrenToRemove.get(j);
                        layout.removeViewInLayout(child);
                        if (child instanceof DropTarget) {
                            mDragController.removeDropTarget((DropTarget)child);
                        }
                    }
        
                    if (childCount > 0) {
                        layout.requestLayout();
                        layout.invalidate();
                    }
                }
            });
        }
    }

    void updateShortcuts(ArrayList<ApplicationInfo> apps) {
        final PackageManager pm = mLauncher.getPackageManager();

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final CellLayout layout = (CellLayout) getChildAt(i);
            int childCount = layout.getChildCount();
            for (int j = 0; j < childCount; j++) {
                final View view = layout.getChildAt(j);
                Object tag = view.getTag();
                if (tag instanceof ShortcutInfo) {
                    ShortcutInfo info = (ShortcutInfo)tag;
                    // We need to check for ACTION_MAIN otherwise getComponent() might
                    // return null for some shortcuts (for instance, for shortcuts to
                    // web pages.)
                    final Intent intent = info.intent;
                    final ComponentName name = intent.getComponent();
                    if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION &&
                            Intent.ACTION_MAIN.equals(intent.getAction()) && name != null) {
                        final int appCount = apps.size();
                        for (int k=0; k<appCount; k++) {
                            ApplicationInfo app = apps.get(k);
                            if (app.componentName.equals(name)) {
                                info.setIcon(mIconCache.getIcon(info.intent));
                                ((TextView)view).setCompoundDrawablesWithIntrinsicBounds(null,
                                        new FastBitmapDrawable(info.getIcon(mIconCache)),
                                        null, null);
                                }
                        }
                    }
                }
            }
        }
    }

    void moveToDefaultScreen(boolean animate) {
        if (animate) {
            snapToScreen(mDefaultScreen);
        } else {
            setCurrentScreen(mDefaultScreen);
        }
        getChildAt(mDefaultScreen).requestFocus();
    }

    void setIndicators(Drawable previous, Drawable next) {
    	
    }

    void setIndicators(Drawable screen_index) {

    }    
    
    void setHomeScreenPage(int page)
    {	
	
    	// Log.e(TAG, "setCurPage:PageCount="+PageCount);1
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

	synchronized(mMutex)
	{
		if((mHomeScreenIndicator.getChildCount() == Launcher.SCREEN_COUNT ) && (last_page == page))
		{
			//huyanwei add it mHomeScreenIndicator.getChildCount() : for second time save_last_page == page.
			// case flash screen.
			return ;
		}

		Resources resource= getResources();
		mHomeScreenIndicator.removeAllViews();
		for (int i = 0; i < Launcher.SCREEN_COUNT /* 5 */; i++) 
		{
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
			}
			//mHomeScreenIndicator.addView(imgCur);
			
			LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);			
			linearLayoutParams.leftMargin  = 5;
			linearLayoutParams.rightMargin = 5;
			mHomeScreenIndicator.addView(imgCur, (LayoutParams)linearLayoutParams);
		}		
		last_page = page ;		
	}
    }
    
    void setIndicators(LinearLayout indicator_layout) {
    	mHomeScreenIndicator = indicator_layout ;
    	setHomeScreenPage(mCurrentScreen);
    }    
    
	public View searchIMTKWidget(View hostView ) {
    	if ( hostView instanceof IMTKWidget ) {
			return hostView;
    	} else if (hostView instanceof ViewGroup) {
    		int childCount = ((ViewGroup)hostView).getChildCount();
    		for(int i = 0; i < childCount; i++) {
    			View mtkWidgetView = searchIMTKWidget(((ViewGroup)hostView).getChildAt(i));
    			if( mtkWidgetView != null )
    				return mtkWidgetView;
    		}
    		return null;
    	} else {
    			return null;
    	}
    }

    public static class SavedState extends BaseSavedState {
        int currentScreen = -1;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentScreen = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentScreen);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
