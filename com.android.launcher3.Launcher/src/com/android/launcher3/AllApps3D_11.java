/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 */
/* MediaTek Inc. (C) 2010. All rights reserved.
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

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.renderscript.Allocation;
import android.renderscript.Dimension;
import android.renderscript.Element;
import android.renderscript.ProgramFragment;
import android.renderscript.ProgramStore;
import android.renderscript.ProgramVertex;
import android.renderscript.RSSurfaceView;
import android.renderscript.RenderScriptGL;
import android.renderscript.RenderScript;
import android.renderscript.Sampler;
import android.renderscript.Script;
import android.renderscript.ScriptC;
import android.renderscript.SimpleMesh;
import android.renderscript.Type;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.SurfaceHolder;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.mediatek.featureoption.FeatureOption;
import com.android.launcher3.R;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.AllAppsList.TopPackage;

public class AllApps3D_11 extends RSSurfaceView
        implements AllAppsView, View.OnClickListener, View.OnLongClickListener, DragSource {
    private static final String TAG = "Launcher2.AllApps3D_11";
    static final boolean DEBUG_LOADERS = false;
    static final boolean DEBUG_3D = false;
    static final boolean DEBUG_EVENT = false;

    /** Bit for mLocks for when there are icons being loaded. */
    private static final int LOCK_ICONS_PENDING = 1;

    private static final int TRACKING_NONE = 0;
    private static final int TRACKING_FLING = 1;
    private static final int TRACKING_HOME = 2;

    private static final int SELECTED_NONE = 0;
    private static final int SELECTED_FOCUSED = 1;
    private static final int SELECTED_PRESSED = 2;

    private static final int SELECTION_NONE = 0;
    private static final int SELECTION_ICONS = 1;
    private static final int SELECTION_HOME = 2;

    private Launcher mLauncher;
    private DragController mDragController;

    /** When this is 0, modifications are allowed, when it's not, they're not.
     * TODO: What about scrolling? */
    private int mLocks = LOCK_ICONS_PENDING;

    private int mSlop;
    private int mMaxFlingVelocity;

    private Defines mDefines = new Defines();
    private RenderScriptGL mRS;
    private RolloRS mRollo;
    private ArrayList<ApplicationInfo> mAllAppsList;

    /**
     * True when we are using arrow keys or trackball to drive navigation
     */
    private boolean mArrowNavigation = false;
    private boolean mStartedScrolling;

    /**
     * Used to keep track of the selection when AllAppsView loses window focus.
     * One of the SELECTION_ constants.
     */
    private int mLastSelection;

    /**
     * Used to keep track of the selection when AllAppsView loses window focus
     */
    private int mLastSelectedIcon;
    
    private boolean mHasHomeBtn = true;

    private VelocityTracker mVelocityTracker;
    private int mTouchTracking;
    private int mMotionDownRawX;
    private int mMotionDownRawY;
    private int mDownIconIndex = -1;
    private int mCurrentIconIndex = -1;

    private boolean mShouldGainFocus;

    private boolean mZoomDirty = false;
    private boolean mAnimateNextZoom;
    private float mZoom;
    private float mPosX;
    private float mVelocity;
    private AAMessage mMessageProc;

    private int mColumnsPerPage;
    private int mRowsPerPage;

    @SuppressWarnings({"UnusedDeclaration"})
    static class Defines {
        public static final int ALLOC_PARAMS = 0;
        public static final int ALLOC_STATE = 1;
        public static final int ALLOC_ICON_IDS = 3;

        public static final int COLUMNS_PER_PAGE_PORTRAIT = 4;
        public static final int ROWS_PER_PAGE_PORTRAIT = 4;
        public static final int ROWS_PER_PAGE_PORTRAIT_1 = 3;

        public static final int COLUMNS_PER_PAGE_LANDSCAPE = 4;
        public static final int ROWS_PER_PAGE_LANDSCAPE = 3;


        public static final int ICON_TEXTURE_WIDTH_PX = (DisplayMetrics.DENSITY_DEVICE < 160) ? 64 : 128;
        public static final int ICON_TEXTURE_HEIGHT_PX = (DisplayMetrics.DENSITY_DEVICE < 160) ? 64 : 128;

        public static int SCREEN_WIDTH_PX;
        public static int SCREEN_HEIGHT_PX;

        public static void recompute(int w, int h) {
			SCREEN_WIDTH_PX = w;
            SCREEN_HEIGHT_PX = h;
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public AllApps3D_11(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        final boolean isPortrait = metrics.widthPixels < metrics.heightPixels;
        final float ratio = (float)metrics.widthPixels / (float)metrics.heightPixels;
        mColumnsPerPage = isPortrait ? Defines.COLUMNS_PER_PAGE_PORTRAIT : Defines.COLUMNS_PER_PAGE_LANDSCAPE;
        mRowsPerPage = isPortrait ? ((ratio>= 0.75f) ? Defines.ROWS_PER_PAGE_PORTRAIT_1 : Defines.ROWS_PER_PAGE_PORTRAIT) : Defines.ROWS_PER_PAGE_LANDSCAPE;
        
        mHasHomeBtn = !(!isPortrait && DisplayMetrics.DENSITY_DEVICE <= 120);
        
        if (DEBUG_3D) {
            Log.d(TAG, "AllApps3D_11 isPortrait==" + isPortrait ); 
            Log.d(TAG, "AllApps3D_11 ratio==" + ratio ); 
            Log.d(TAG, "AllApps3D_11 metrics.widthPixels==" + metrics.widthPixels );        
            Log.d(TAG, "AllApps3D_11 metrics.heightPixels==" + metrics.heightPixels );       
            Log.d(TAG, "AllApps3D_11 mRowsPerPage==" + mRowsPerPage );
            Log.d(TAG, "AllApps3D_11 mColumnsPerPage==" + mColumnsPerPage );
        }
        
        setFocusable(true);
        setSoundEffectsEnabled(false);
        getHolder().setFormat(isPortrait ? PixelFormat.OPAQUE : PixelFormat.TRANSLUCENT);
        final ViewConfiguration config = ViewConfiguration.get(context);
        mSlop = config.getScaledTouchSlop();
        mMaxFlingVelocity = config.getScaledMaximumFlingVelocity();

        setOnClickListener(this);
        setOnLongClickListener(this);
        setZOrderOnTop(true);        
        getHolder().setFormat(isPortrait ? PixelFormat.OPAQUE : PixelFormat.TRANSLUCENT);

        if (mRS == null) {
            mRS = createRenderScript(true);
        } else {
            createRenderScript(mRS);
        }
     }

    public void setIndicators(Drawable index) {
    	
    }
    
    public void setIndicatorControl(LinearLayout layout) {
    	
    }
    
    public void updateView()
    {
    	
    }
    
    @Override
    protected void onDetachedFromWindow() {
        destroyRenderScript();        
        mRS = null;
        mRollo = null;
    }

    /**
     * If you have an attached click listener, View always plays the click sound!?!?
     * Deal with sound effects by hand.
     */
    public void reallyPlaySoundEffect(int sound) {
        boolean old = isSoundEffectsEnabled();
        setSoundEffectsEnabled(true);
        playSoundEffect(sound);
        setSoundEffectsEnabled(old);
    }

    public AllApps3D_11(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }
    
    public void surrender() {

    }    

    public void setLauncher(Launcher launcher) {
        mLauncher = launcher;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        if (mRollo != null)
        	mRollo.mHasSurface = false;
        // Without this, we leak mMessageCallback which leaks the context.
        if (mRS != null)
        	mRS.mMessageCallback = null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);
        if (DEBUG_3D) {
        	Log.d(TAG, "surfaceChanged w==" + w +" h==" + h);
        }
        
        
        if (mRollo == null) {
            mRollo = new RolloRS();
            mRollo.mHasSurface = true;
            mRollo.init(getResources(), w, h);
			mRollo.mState.drawDone = 0;
            if (mAllAppsList != null) {
                mRollo.setApps(mAllAppsList);
            }
            if (mShouldGainFocus) {
                gainFocus();
                mShouldGainFocus = false;
            }
        } else {
            mRollo.mHasSurface = true;
        }
        mRollo.dirtyCheck();
        mRollo.resize(w, h);
        
        mRollo.setTransparent(!isVisible());
		
        mRS.mMessageCallback = mMessageProc = new AAMessage();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (mArrowNavigation) {
            if (!hasWindowFocus) {
                // Clear selection when we lose window focus
                mLastSelectedIcon = mRollo.mState.selectedIconIndex;
                mRollo.setHomeSelected(SELECTED_NONE);
                mRollo.clearSelectedIcon();
                mRollo.mState.save();
            } else if (hasWindowFocus) {
            	if (mRollo == null || mRollo.mHasSurface == false) {
					Log.d(TAG, "focus change but no surface");
					return;
            	}
                if (mRollo.mState.iconCount > 0) {
                    if (mLastSelection == SELECTION_ICONS) {
                        int selection = mLastSelectedIcon;
                        final int firstIcon = Math.round(mPosX) *
                            mColumnsPerPage;
                        if (selection < 0 || // No selection
                                selection < firstIcon || // off the top of the screen
                                selection >= mRollo.mState.iconCount || // past last icon
                                selection >= firstIcon + // past last icon on screen
                                    (mColumnsPerPage * mRowsPerPage)) {
                            selection = firstIcon;
                        }

                        // Select the first icon when we gain window focus
                        mRollo.selectIcon(selection, SELECTED_FOCUSED);
                        mRollo.mState.save();
                    } else if (mLastSelection == SELECTION_HOME) {
                        mRollo.setHomeSelected(SELECTED_FOCUSED);
                        mRollo.mState.save();
                    }
                }
            }
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        if (!isVisible()) {
            return;
        }

        if (gainFocus) {
            if (mRollo != null && mRollo.mHasSurface) {
                gainFocus();
            } else {
                mShouldGainFocus = true;
            }
        } else {
            if (mRollo != null) {
                if (mArrowNavigation) {
                    // Clear selection when we lose focus
                    mRollo.clearSelectedIcon();
                    mRollo.setHomeSelected(SELECTED_NONE);
                    mRollo.mState.save();
                    mArrowNavigation = false;
                }
            } else {
                mShouldGainFocus = false;
            }
        }
    }

    private void gainFocus() {
        if (!mArrowNavigation && mRollo.mState.iconCount > 0) {
            // Select the first icon when we gain keyboard focus
            mArrowNavigation = true;
            mRollo.selectIcon(Math.round(mPosX) * mColumnsPerPage,
                    SELECTED_FOCUSED);
            mRollo.mState.save();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        boolean handled = false;

        if (!isVisible() || mRollo == null) {
            return false;
        }
        final int iconCount = mRollo.mState.iconCount;

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            if (mArrowNavigation) {
                if (mLastSelection == SELECTION_HOME) {
                    reallyPlaySoundEffect(SoundEffectConstants.CLICK);
                    mLauncher.closeAllApps(true);
                } else {
                    int whichApp = mRollo.mState.selectedIconIndex;
                    if (whichApp >= 0) {
                        ApplicationInfo app = mAllAppsList.get(whichApp);
                        mLauncher.startActivitySafely(app.intent,app);
                        handled = true;
                    }
                }
            }
        }

        if (iconCount > 0) {
            mArrowNavigation = true;

            int currentSelection = mRollo.mState.selectedIconIndex;
            int currentTopRow = Math.round(mPosX);

            // The column of the current selection, in the range 0..COLUMNS_PER_PAGE-1
            final int currentPageCol = currentSelection % mColumnsPerPage;

            // The row of the current selection, in the range 0..mRowsPerPage-1
            final int currentPageRow = (currentSelection - (currentTopRow*mColumnsPerPage))
                    / mColumnsPerPage;
            
            if (DEBUG_EVENT) {
            	Log.d(TAG, " onKeydown currentTopRow==" + currentTopRow);
            	Log.d(TAG, " onKeydown currentSelection==" + currentSelection);          
            	Log.d(TAG, " onKeydown currentPageRow==" + currentPageRow);
            	Log.d(TAG, " onKeydown currentPageCol==" + currentPageCol);          	
            }

            int newSelection = currentSelection;

            switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mLastSelection == SELECTION_HOME) {
                    mRollo.setHomeSelected(SELECTED_NONE);
                    int lastRowCount = iconCount % mColumnsPerPage;
                    if (lastRowCount == 0) {
                        lastRowCount = mColumnsPerPage;
                    }
                    newSelection = iconCount - lastRowCount + (mColumnsPerPage / 2);
                    if (newSelection >= iconCount) {
                        newSelection = iconCount-1;
                    }
                    int target = (newSelection / mColumnsPerPage)
                            - (mRowsPerPage - 1);
                    if (target < 0) {
                        target = 0;
                    }
                    if (currentTopRow != target) {
                        mRollo.moveTo(target);
                    }
                } else {
                    if (currentPageRow > 0) {
                        newSelection = currentSelection - mColumnsPerPage;
                    } else if (currentTopRow > 0) {
                        newSelection = currentSelection - mColumnsPerPage;
                        if (newSelection >= 0) {
                            mRollo.moveTo(newSelection / mColumnsPerPage);
                        } else {
                            newSelection = currentSelection;
                        }
                    } else if (currentPageRow != 0) {
                        newSelection = currentTopRow * mRowsPerPage;
                    }
                }
                handled = true;
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN: {
                final int rowCount = iconCount / mColumnsPerPage
                        + (iconCount % mColumnsPerPage == 0 ? 0 : 1);
                final int currentRow = currentSelection / mColumnsPerPage;
                if (mLastSelection != SELECTION_HOME) {
                    if (currentRow < rowCount-1) {
                        mRollo.setHomeSelected(SELECTED_NONE);
                        if (currentSelection < 0) {
                            newSelection = 0;
                        } else {
                            newSelection = currentSelection + mColumnsPerPage;
                        }
                        if (newSelection >= iconCount) {
                            // Go from D to G in this arrangement:
                            //     A B C D
                            //     E F G
                            newSelection = iconCount - 1;
                        }
                        if (currentPageRow >= mRowsPerPage - 1) {
                            mRollo.moveTo((newSelection / mColumnsPerPage) -
                                    mRowsPerPage + 1);
                        }
                    } else if (mHasHomeBtn) {
                        newSelection = -1;
                        mRollo.setHomeSelected(SELECTED_FOCUSED);
                    } else {
                    	newSelection = currentSelection;
                    }
                }
                handled = true;
                break;
            }
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (currentPageCol > 0) {
                    newSelection = currentSelection - 1;
                }
                handled = true;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if ((currentPageCol < mColumnsPerPage - 1) &&
                        (currentSelection < iconCount - 1) && (currentSelection != -1)) {
                    newSelection = currentSelection + 1;
                }
                handled = true;
                break;
            }

            if (newSelection != currentSelection) {
                mRollo.selectIcon(newSelection, SELECTED_FOCUSED);
                mRollo.mState.save();
            }
        }
        return handled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        mArrowNavigation = false;

        if (!isVisible()) {
            return false;
        }

        if (mLocks != 0) {
            return true;
        }

        super.onTouchEvent(ev);

        int x = (int)ev.getX();
        int y = (int)ev.getY();

        int action = ev.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            if (y > mRollo.mTouchYBorders[mRollo.mTouchYBorders.length-1]) {
                mTouchTracking = TRACKING_HOME;
                mRollo.setHomeSelected(SELECTED_PRESSED);
                mRollo.mState.save();
                mCurrentIconIndex = -1;
            } else {
                mTouchTracking = TRACKING_FLING;

                mMotionDownRawX = (int)ev.getRawX();
                mMotionDownRawY = (int)ev.getRawY();

                mRollo.mState.newPositionX = ev.getRawY() / getHeight();
                mRollo.mState.newTouchDown = 1;

                if (!mRollo.checkClickOK()) {
                    mRollo.clearSelectedIcon();
                } else {
                    mDownIconIndex = mCurrentIconIndex
                            = mRollo.selectIcon(x, y, mPosX, SELECTED_PRESSED);
                    if (mDownIconIndex < 0) {
                        // if nothing was selected, no long press.
                        cancelLongPress();
                    }
                }
                mRollo.mState.save();
                mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(ev);
                mStartedScrolling = false;
            }
            break;
        case MotionEvent.ACTION_MOVE:
        case MotionEvent.ACTION_OUTSIDE:
            if (mTouchTracking == TRACKING_HOME) {
                mRollo.setHomeSelected(y > mRollo.mTouchYBorders[mRollo.mTouchYBorders.length-1]
                        ? SELECTED_PRESSED : SELECTED_NONE);
                mRollo.mState.save();
            } else if (mTouchTracking == TRACKING_FLING) {
                int rawY = (int)ev.getRawY();
                int slop;
                slop = Math.abs(rawY - mMotionDownRawY);

                if (!mStartedScrolling && slop < mSlop) {
                    // don't update anything so when we do start scrolling
                    // below, we get the right delta.
                    mCurrentIconIndex = mRollo.chooseTappedIcon(x, y, mPosX);
                    if (mDownIconIndex != mCurrentIconIndex) {
                        // If a different icon is selected, don't allow it to be picked up.
                        // This handles off-axis dragging.
                        cancelLongPress();
                        mCurrentIconIndex = -1;
                    }
                } else {
                    if (!mStartedScrolling) {
                        cancelLongPress();
                        mCurrentIconIndex = -1;
                    }
                    mRollo.mState.newPositionX = ev.getRawY() / getHeight();
                    mRollo.mState.newTouchDown = 1;
                    mRollo.move();

                    mStartedScrolling = true;
                    mRollo.clearSelectedIcon();
                    mVelocityTracker.addMovement(ev);
                    mRollo.mState.save();
                }
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            if (mTouchTracking == TRACKING_HOME) {
                if (action == MotionEvent.ACTION_UP) {
                    if (y > mRollo.mTouchYBorders[mRollo.mTouchYBorders.length-1]) {
                        reallyPlaySoundEffect(SoundEffectConstants.CLICK);
                        mLauncher.closeAllApps(true);
                    }
                    mRollo.setHomeSelected(SELECTED_NONE);
                    mRollo.mState.save();
                }
                mCurrentIconIndex = -1;
            } else if (mTouchTracking == TRACKING_FLING) {
                mRollo.mState.newTouchDown = 0;
                mRollo.mState.newPositionX = ev.getRawY() / getHeight();

                mVelocityTracker.computeCurrentVelocity(1000 /* px/sec */, mMaxFlingVelocity);
                mRollo.mState.flingVelocity = mVelocityTracker.getYVelocity() / getHeight();
                mRollo.clearSelectedIcon();
                mRollo.mState.save();
                mRollo.fling();

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
            }
            mTouchTracking = TRACKING_NONE;
            break;
        }

        return true;
    }

    public void onClick(View v) {
        if (mLocks != 0 || !isVisible()) {
            return;
        }
        if (mRollo.checkClickOK() && mCurrentIconIndex == mDownIconIndex
                && mCurrentIconIndex >= 0 && mCurrentIconIndex < mAllAppsList.size()) {
            reallyPlaySoundEffect(SoundEffectConstants.CLICK);
            ApplicationInfo app = mAllAppsList.get(mCurrentIconIndex);
            mLauncher.startActivitySafely(app.intent,app);
        }
    }

    public boolean onLongClick(View v) {
        if (mLocks != 0 || !isVisible()) {
            return true;
        }
        if (mRollo.checkClickOK() && mCurrentIconIndex == mDownIconIndex
                && mCurrentIconIndex >= 0 && mCurrentIconIndex < mAllAppsList.size()) {
            ApplicationInfo app = mAllAppsList.get(mCurrentIconIndex);

            Bitmap bmp = app.iconBitmap;
            final int w = bmp.getWidth();
            final int h = bmp.getHeight();

            // We don't really have an accurate location to use. This will do.
            int screenX = mMotionDownRawX - (w / 2);
            int screenY = mMotionDownRawY - h;

            mDragController.startDrag(bmp, screenX, screenY,
                    0, 0, w, h, this, app, DragController.DRAG_ACTION_COPY);

            mLauncher.closeAllApps(true);
        }
        return true;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SELECTED) {
            if (!isVisible()) {
                return false;
            }
            String text = null;
            int index;
            int count = mAllAppsList.size() + 1; // +1 is home
            int pos = -1;
            switch (mLastSelection) {
            case SELECTION_ICONS:
                index = mRollo.mState.selectedIconIndex;
                if (index >= 0) {
                    ApplicationInfo info = mAllAppsList.get(index);
                    if (info.title != null) {
                        text = info.title.toString();
                        pos = index;
                    }
                }
                break;
            case SELECTION_HOME:
                text = getContext().getString(R.string.all_apps_home_button_label);
                pos = count;
                break;
            }
            if (text != null) {
                event.setEnabled(true);
                event.getText().add(text);
                event.setItemCount(count);
                event.setCurrentItemIndex(pos);
            }
        }
        return false;
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
        cancelLongPress();
        if (mRollo == null || !mRollo.mHasSurface) {
            mZoomDirty = true;
            mZoom = zoom;
            mAnimateNextZoom = animate;
            return;
        } else {
        	mZoomDirty = false;
        	mZoom = zoom;
            mRollo.setZoom(zoom, animate);
        }
    }

    public boolean isVisible() {
        return mZoom > 0.001f;
    }

    public boolean isOpaque() {
        return mZoom > 0.999f;
    }

    public void setApps(ArrayList<ApplicationInfo> list) {
        mAllAppsList = list;
        if (mRollo != null) {        	        
            mRollo.setApps(list);            
        }
        mLocks &= ~LOCK_ICONS_PENDING;
    }
    
    public void reorderApps() {
    	
        if (AllAppsList.mTopPackages == null || mAllAppsList == null 
        		|| AllAppsList.mTopPackages.isEmpty() || mAllAppsList.isEmpty()) {
        	return ;
        } 
        
        ArrayList<ApplicationInfo> dataReorder =
            new ArrayList<ApplicationInfo>(AllAppsList.DEFAULT_APPLICATIONS_NUMBER); 
        
        for (AllAppsList.TopPackage tp : AllAppsList.mTopPackages) { 
        	int loop = 0;
        	for (ApplicationInfo ai : mAllAppsList) {         		
        		if (ai.componentName.getPackageName().equals(tp.mPackageName) 
        				&& ai.componentName.getClassName().equals(tp.mClassName)) {
						mAllAppsList.remove(ai);							
										
						if (mRollo != null) {
							mRollo.removeApp(loop);
                         } 
						
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
					if (mRollo != null) {
					    mRollo.addApp(newIndex, ai);
                     }        			

        			break;
        		}
        	}                	                
        } 
         
    }

    public void addApps(ArrayList<ApplicationInfo> list) {
        if (mAllAppsList == null) {
            // Not done loading yet. We'll find out about it later.
            return;
        }

        final int N = list.size();
        if (mRollo != null) {
            mRollo.reallocAppsList(mRollo.mState.iconCount + N);
        }

        for (int i=0; i<N; i++) {
            final ApplicationInfo item = list.get(i);
            int index = Collections.binarySearch(mAllAppsList, item,LauncherModel.APP_NAME_COMPARATOR);
            if (index < 0) {
                index = -(index+1);
            }
            mAllAppsList.add(index, item);
            if (mRollo != null) {
                mRollo.addApp(index, item);
            }
        }
        
/*
        if (FeatureOption.MTK_YMCAPROP_SUPPORT) {
        	reorderApps();
        }
*/

        if (mRollo != null) {
            mRollo.saveAppsList();
        }
    }

    public void removeApps(ArrayList<ApplicationInfo> list) {
        removeApps(list, true);
    }

    public void removeApps(ArrayList<ApplicationInfo> list, boolean updateRS) {
        if (mAllAppsList == null) {
            // Not done loading yet. We'll find out about it later.
            return;
        }

        final int N = list.size();
        for (int i=0; i<N; i++) {
            final ApplicationInfo item = list.get(i);
            int index = findAppByComponent(mAllAppsList, item);
            if (index >= 0) {
                int ic = mRollo != null ? mRollo.mState.iconCount : 666;
                mAllAppsList.remove(index);
                if (mRollo != null) {
                    mRollo.removeApp(index);
                }
            } else {
                Log.w(TAG, "couldn't find a match for item \"" + item + "\"");
                // Try to recover. This should keep us from crashing for now.
            }
        }
        
/*
        if (FeatureOption.MTK_YMCAPROP_SUPPORT) {
        	reorderApps();
        }
*/

        if (mRollo != null && updateRS) {
            mRollo.saveAppsList();
        }
    }

    public void updateApps(ArrayList<ApplicationInfo> list) {
        // Just remove and add, because they may need to be re-sorted.
        removeApps(list, false);
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
 	/*
    private static int countPages(int iconCount) {
        int iconsPerPage = mColumnsPerPage * mRowsPerPage;
        int pages = iconCount / iconsPerPage;
        if (pages*iconsPerPage != iconCount) {
            pages++;
        }
        return pages;
    }
    */

    class AAMessage extends RenderScript.RSMessage {
        public void run() {
            mPosX = ((float)mData[0]) / (1 << 16);
            mVelocity = ((float)mData[1]) / (1 << 16);
            mZoom = ((float)mData[2]) / (1 << 16);
            mZoomDirty = false;
        }
    }

    public class RolloRS {

        // Allocations ======
        private int mWidth;
        private int mHeight;

        private Resources mRes;
        private Script mScript;
        private Script.Invokable mInvokeMove;
        private Script.Invokable mInvokeMoveTo;
        private Script.Invokable mInvokeFling;
        private Script.Invokable mInvokeResetWAR;
        private Script.Invokable mInvokeSetZoom;

        private ProgramStore mPSIcons;
        private ProgramStore mPSText;
        private ProgramFragment mPFColor;
        private ProgramFragment mPFTexMip;
        private ProgramFragment mPFTexNearest;
        private ProgramVertex mPV;
        private ProgramVertex mPVOrtho;
        private SimpleMesh mMesh;
        private SimpleMesh mMesh2;
        private ProgramVertex.MatrixAllocation mPVA;

        private Allocation mHomeButtonNormal;
        private Allocation mHomeButtonFocused;
        private Allocation mHomeButtonPressed;

        private Allocation[] mIcons;
        private int[] mIconIds;
        private Allocation mAllocIconIds;
        private Allocation mSelectedIcon;

        private int[] mTouchYBorders;
        private int[] mTouchXBorders;

        private Bitmap mSelectionBitmap;
        private Canvas mSelectionCanvas;

        boolean mHasSurface = false;
        private boolean mAppsDirty = true;

        Params mParams;
        State mState;
        
        private boolean mNeedResetHWWar = true;

        class BaseAlloc {
            Allocation mAlloc;
            Type mType;

            void save() {
                mAlloc.data(this);
            }

			void restore() {
				mAlloc.read(this);
			}
        }

        private boolean checkClickOK() {
            return (Math.abs(mVelocity) < 0.4f) &&
                   (Math.abs(mPosX - Math.round(mPosX)) < 0.4f);
        }

        class Params extends BaseAlloc {
            Params() {
                mType = Type.createFromClass(mRS, Params.class, 1, "ParamsClass");
                mAlloc = Allocation.createTyped(mRS, mType);
                save();
            }
            public int bubbleWidth;
            public int bubbleHeight;
            public int bubbleBitmapWidth;
            public int bubbleBitmapHeight;

            public int homeButtonWidth;
            public int homeButtonHeight;
            public int homeButtonTextureWidth;
            public int homeButtonTextureHeight;
        }

        class State extends BaseAlloc {
            public float newPositionX;
            public int newTouchDown;
            public float flingVelocity;
            public int iconCount;
            public int selectedIconIndex = -1;
            public int selectedIconTexture;
            public float zoomTarget;
            public int homeButtonId;
            public float targetPos;
			public int drawDone;

            State() {
                mType = Type.createFromClass(mRS, State.class, 1, "StateClass");
                mAlloc = Allocation.createTyped(mRS, mType);
                save();
            }
        }

        public RolloRS() {
        }

        public void init(Resources res, int width, int height) {
            mRes = res;
            mWidth = width;
            mHeight = height;
            Defines.recompute(width, height);
            initProgramVertex();
            initProgramFragment();
            initProgramStore();
            initMesh();
            initGl();
            initData();
            initTouchState();
            initRs();

            // make the rollo transparent initially for best performance
			setTransparent(true);
        }

        public void initMesh() {
            SimpleMesh.TriangleMeshBuilder tm = new SimpleMesh.TriangleMeshBuilder(mRS, 3,
                SimpleMesh.TriangleMeshBuilder.TEXTURE_0 | SimpleMesh.TriangleMeshBuilder.COLOR);

            float y = 0;
            float z = 0;
            for (int ct=0; ct < 200; ct++) {
                float angle = 0;
                float maxAngle = 3.14f * 0.16f;
                float l = 1.f;

                if (DisplayMetrics.DENSITY_DEVICE == 160) {
                    maxAngle = 3.14f * 0.21f;
                }

                l = 1 - ((ct-7) * 0.10f);
                if (ct > 7) {
                    angle = maxAngle * (ct - 7) * 0.2f;
                    angle = Math.min(angle, maxAngle);
                }
                l = Math.max(0.4f, l);
                l = Math.min(1.0f, l);

                y += 0.1f * Math.cos(angle);
                z += 0.1f * Math.sin(angle);

                float t = 0.1f * ct;
                float ds = 0.08f;
                tm.setColor(l, l, l, 0.99f);
                tm.setTexture(ds, t);
                tm.addVertex(-0.5f, y, z);
                tm.setTexture(1 - ds, t);
                tm.addVertex(0.5f, y, z);
            }
            for (int ct=0; ct < (200 * 2 - 2); ct+= 2) {
                tm.addTriangle(ct, ct+1, ct+2);
                tm.addTriangle(ct+1, ct+3, ct+2);
            }
            mMesh2 = tm.create();
            mMesh2.setName("SMMesh");
        }

        void resize(int w, int h) {
            mPVA.setupProjectionNormalized(w, h);
            mWidth = w;
            mHeight = h;
        }

        private void initProgramVertex() {
            mPVA = new ProgramVertex.MatrixAllocation(mRS);
            resize(mWidth, mHeight);

            ProgramVertex.Builder pvb = new ProgramVertex.Builder(mRS, null, null);
            pvb.setTextureMatrixEnable(true);
            mPV = pvb.create();
            mPV.setName("PV");
            mPV.bindAllocation(mPVA);

            mRS.contextBindProgramVertex(mPV);
        }

        private void initProgramFragment() {
            Sampler.Builder sb = new Sampler.Builder(mRS);
            sb.setMin(Sampler.Value.LINEAR_MIP_LINEAR);
            sb.setMag(Sampler.Value.LINEAR);
            sb.setWrapS(Sampler.Value.CLAMP);
            sb.setWrapT(Sampler.Value.CLAMP);
            Sampler linear = sb.create();

            sb.setMin(Sampler.Value.NEAREST);
            sb.setMag(Sampler.Value.NEAREST);
            Sampler nearest = sb.create();

            ProgramFragment.Builder bf = new ProgramFragment.Builder(mRS);
            mPFColor = bf.create();
            mPFColor.setName("PFColor");

            //bf.setTexEnable(true, 0);
            //bf.setTexEnvMode(ProgramFragment.EnvMode.MODULATE, 0);
            bf.setTexture(ProgramFragment.Builder.EnvMode.MODULATE,
                          ProgramFragment.Builder.Format.RGBA, 0);

            mPFTexMip = bf.create();
            mPFTexMip.setName("PFTexMip");
            mPFTexMip.bindSampler(linear, 0);

            mPFTexNearest = bf.create();
            mPFTexNearest.setName("PFTexNearest");
            mPFTexNearest.bindSampler(nearest, 0);
        }

        private void initProgramStore() {
            ProgramStore.Builder bs = new ProgramStore.Builder(mRS, null, null);
            bs.setDepthFunc(ProgramStore.DepthFunc.ALWAYS);
            bs.setColorMask(true,true,true,false);
            bs.setDitherEnable(true);
            bs.setBlendFunc(ProgramStore.BlendSrcFunc.ONE,
                            ProgramStore.BlendDstFunc.ONE_MINUS_SRC_ALPHA);
            mPSIcons = bs.create();
            mPSIcons.setName("PSIcons");
        }

        private void initGl() {
            mTouchXBorders = new int[mColumnsPerPage+1];
            mTouchYBorders = new int[mRowsPerPage+1];
        }

        private void initData() {
            mParams = new Params();
            mState = new State();

            final Utilities.BubbleText bubble = new Utilities.BubbleText(getContext());

            mParams.bubbleWidth = bubble.getBubbleWidth();
            mParams.bubbleHeight = bubble.getMaxBubbleHeight();
            mParams.bubbleBitmapWidth = bubble.getBitmapWidth();
            mParams.bubbleBitmapHeight = bubble.getBitmapHeight();

			if (DisplayMetrics.DENSITY_DEVICE <= 120) {
				BitmapFactory.Options op = new BitmapFactory.Options();
            	op.inPreferredConfig = Bitmap.Config.ARGB_8888;
				op.inScaled = false;
				Bitmap home_normal = BitmapFactory.decodeResource(mRes, R.drawable.home_button_normal_ldpi, op);
				mHomeButtonNormal = Allocation.createFromBitmap(mRS, home_normal, Element.RGBA_8888(mRS), true);
            	mHomeButtonNormal.uploadToTexture(0);
            	mHomeButtonFocused = Allocation.createFromBitmapResource(mRS, mRes,
                    R.drawable.home_button_focused_ldpi, Element.RGBA_8888(mRS), false);
            	mHomeButtonFocused.uploadToTexture(0);
            	mHomeButtonPressed = Allocation.createFromBitmapResource(mRS, mRes,
                    R.drawable.home_button_pressed_ldpi, Element.RGBA_8888(mRS), false);
            	mHomeButtonPressed.uploadToTexture(0);
            	mParams.homeButtonWidth = 38;
            	mParams.homeButtonHeight = 34;
            	mParams.homeButtonTextureWidth = 64;
            	mParams.homeButtonTextureHeight = 64;
			} else if (DisplayMetrics.DENSITY_DEVICE == 160) {
				BitmapFactory.Options op = new BitmapFactory.Options();
            	op.inPreferredConfig = Bitmap.Config.ARGB_8888;
				op.inScaled = false;
				Bitmap home_normal = BitmapFactory.decodeResource(mRes, R.drawable.home_button_normal_160, op);
				mHomeButtonNormal = Allocation.createFromBitmap(mRS, home_normal, Element.RGBA_8888(mRS), true);
            	mHomeButtonNormal.uploadToTexture(0);
            	mHomeButtonFocused = Allocation.createFromBitmapResource(mRS, mRes,
                    R.drawable.home_button_focused_160, Element.RGBA_8888(mRS), false);
            	mHomeButtonFocused.uploadToTexture(0);
            	mHomeButtonPressed = Allocation.createFromBitmapResource(mRS, mRes,
                    R.drawable.home_button_pressed_160, Element.RGBA_8888(mRS), false);
            	mHomeButtonPressed.uploadToTexture(0);
            	mParams.homeButtonWidth = 51;
            	mParams.homeButtonHeight = 45;
            	mParams.homeButtonTextureWidth = 128;
            	mParams.homeButtonTextureHeight = 128;
			} else {
            	mHomeButtonNormal = Allocation.createFromBitmapResource(mRS, mRes,
                    R.drawable.home_button_normal, Element.RGBA_8888(mRS), false);
            	mHomeButtonNormal.uploadToTexture(0);
            	mHomeButtonFocused = Allocation.createFromBitmapResource(mRS, mRes,
                    R.drawable.home_button_focused, Element.RGBA_8888(mRS), false);
            	mHomeButtonFocused.uploadToTexture(0);
            	mHomeButtonPressed = Allocation.createFromBitmapResource(mRS, mRes,
                    R.drawable.home_button_pressed, Element.RGBA_8888(mRS), false);
            	mHomeButtonPressed.uploadToTexture(0);
            	mParams.homeButtonWidth = 76;
            	mParams.homeButtonHeight = 68;
            	mParams.homeButtonTextureWidth = 128;
            	mParams.homeButtonTextureHeight = 128;
			}

            mState.homeButtonId = mHomeButtonNormal.getID();

            mParams.save();
            mState.save();

            mSelectionBitmap = Bitmap.createBitmap(Defines.ICON_TEXTURE_WIDTH_PX,
                    Defines.ICON_TEXTURE_HEIGHT_PX, Bitmap.Config.ARGB_8888);
            mSelectionCanvas = new Canvas(mSelectionBitmap);

            setApps(null);
        }

        private void initScript(int id) {
        }

        private void initRs() {
            ScriptC.Builder sb = new ScriptC.Builder(mRS);

            if (DisplayMetrics.DENSITY_DEVICE <= 120) {
				if (mRowsPerPage == 4) {
					sb.setScript(mRes, R.raw.allapps11_120);
				}					
				else if (mRowsPerPage == 3) {
					sb.setScript(mRes, R.raw.allapps11_120_1);
				}					
				else {
					sb.setScript(mRes, R.raw.allapps11_120);
				}
           	} else if (DisplayMetrics.DENSITY_DEVICE <= 160) {
				sb.setScript(mRes, R.raw.allapps11_160);
			} else {
				sb.setScript(mRes, R.raw.allapps11_3);
			}

            sb.setRoot(true);
            sb.addDefines(mDefines);
            sb.setType(mParams.mType, "params", Defines.ALLOC_PARAMS);
            sb.setType(mState.mType, "state", Defines.ALLOC_STATE);
            mInvokeMove = sb.addInvokable("move");
            mInvokeFling = sb.addInvokable("fling");
            mInvokeMoveTo = sb.addInvokable("moveTo");
            mInvokeResetWAR = sb.addInvokable("resetHWWar");
            mInvokeSetZoom = sb.addInvokable("setZoom");
            mScript = sb.create();
            mScript.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            mScript.bindAllocation(mParams.mAlloc, Defines.ALLOC_PARAMS);
            mScript.bindAllocation(mState.mAlloc, Defines.ALLOC_STATE);
            mScript.bindAllocation(mAllocIconIds, Defines.ALLOC_ICON_IDS);

            mRS.contextBindRootScript(mScript);
            
            if (DEBUG_LOADERS) {
                Log.d(TAG, "initRs is completed!");
            }
        }

        void dirtyCheck() {
            if (mHasSurface) {
                if (mAppsDirty && mAllAppsList != null) {
                    for (int i=0; i < mState.iconCount; i++) {
                        uploadAppIcon(i, mAllAppsList.get(i));
                    }
                    saveAppsList();
                    mAppsDirty = false;
                }
                if (mZoomDirty) {
                    setZoom(mZoom, mAnimateNextZoom);
                }
            }
        }

        private void setApps(ArrayList<ApplicationInfo> list) {        	       
            final int count = list != null ? list.size() : 0;
            int allocCount = count;
            if (allocCount < 1) {
                allocCount = 1;
            }

            mIcons = new Allocation[count];
            mIconIds = new int[allocCount];
            mAllocIconIds = Allocation.createSized(mRS, Element.USER_I32(mRS), allocCount);

            Element ie8888 = Element.RGBA_8888(mRS);

            mState.iconCount = count;
            
            //mRS.pauseRendering();

			for (int i = 0; i < mState.iconCount; i++) {
				createAppIconAllocations(i, list.get(i));
			}
			for (int i = 0; i < mState.iconCount; i++) {
				uploadAppIcon(i, list.get(i));
			}
            
            //mRS.resumeRendering();
            
            saveAppsList();
        }

		private void setTransparent(boolean transparent)
		{
			Region region = transparent ? new Region(0, 0, mWidth, mHeight) : new Region(0, 0, 0, 0);;

			int[] location = new int[2];
			getLocationInWindow(location);
			region.translate(location[0], location[1]);

			//setTransparentRegion(region);
        }

        private void setZoom(float zoom, boolean animate) {
            mRollo.clearSelectedIcon();
            mRollo.setHomeSelected(SELECTED_NONE);
            mRollo.mState.drawDone = 0;
            if (zoom > 0.001f) {
                mRollo.mState.zoomTarget = zoom;

				setTransparent(false);
            } else {
                mRollo.mState.zoomTarget = 0;
            }

            mRollo.mState.save();
            if (!animate) {
                mRollo.mInvokeSetZoom.execute();
            }

            if (mRollo.mState.zoomTarget < 1) {
                // wait maximum 3000 ms
                for(int count = 0 ; count < 60 ; count++)
                {
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        Log.d(TAG, "sleep exception");
                    }

                    mRollo.mState.restore();
                    if (mRollo.mState.drawDone == 1)
                    {
                        break;
                    }
                }

                if (mRollo.mState.zoomTarget == 0) {
                    setTransparent(true);
                }
            }
        }

        private void frameBitmapAllocMips(Allocation alloc, int w, int h) {          
            int black[] = new int[w > h ? w : h];
            Allocation.Adapter2D a = alloc.createAdapter2D();
            int mip = 0;

            int iloop = 0;
            while (w > 1 || h > 1) {
                a.subData(0, 0, 1, h, black);
                a.subData(w-1, 0, 1, h, black);
                a.subData(0, 0, w, 1, black);
                a.subData(0, h-1, w, 1, black);
                mip++;
                w = (w + 1) >> 1;
                h = (h + 1) >> 1;           	           	
                a.setConstraint(Dimension.LOD, mip);
            }
            a.subData(0, 0, 1, 1, black); 
        }

        private void createAppIconAllocations(int index, ApplicationInfo item) {
            Bitmap bitmap = Utilities.createAllAppBitmap_11(item.iconBitmap,item.titleBitmap);
            
            mIcons[index] = Allocation.createFromBitmap(mRS, bitmap, Element.RGBA_8888(mRS), true);
            
            frameBitmapAllocMips(mIcons[index], bitmap.getWidth(), bitmap.getHeight());                    

            mIconIds[index] = mIcons[index].getID();  
        }

        private void uploadAppIcon(int index, ApplicationInfo item) {
            if (mIconIds[index] != mIcons[index].getID()) {
                throw new IllegalStateException("uploadAppIcon index=" + index
                    + " mIcons[index].getID=" + mIcons[index].getID()
                    + " mIconsIds[index]=" + mIconIds[index]
                    + " item=" + item);
            }
            mIcons[index].uploadToTexture(0);
        }

        /**
         * Puts the empty spaces at the end. Updates mState.iconCount. You must
         * fill in the values and call saveAppsList().
         */
        private void reallocAppsList(int count) {
            Allocation[] icons = new Allocation[count];
            int[] iconIds = new int[count];
            mAllocIconIds = Allocation.createSized(mRS, Element.USER_I32(mRS), count);

            final int oldCount = mRollo.mState.iconCount;

            System.arraycopy(mIcons, 0, icons, 0, oldCount);
            System.arraycopy(mIconIds, 0, iconIds, 0, oldCount);

            mIcons = icons;
            mIconIds = iconIds;
        }

        /**
         * Handle the allocations for the new app.  Make sure you call saveAppsList when done.
         */
        private void addApp(int index, ApplicationInfo item) {
            final int count = mState.iconCount - index;
            final int dest = index + 1;

            System.arraycopy(mIcons, index, mIcons, dest, count);
            System.arraycopy(mIconIds, index, mIconIds, dest, count);

            createAppIconAllocations(index, item);

            if (mHasSurface) {
                uploadAppIcon(index, item);
            } else {
                mAppsDirty = true;
            }

            mRollo.mState.iconCount++;
        }

        /**
         * Handle the allocations for the removed app. Make sure you call saveAppsList when done.
         */
        private void removeApp(int index) {
            final int count = mState.iconCount - index - 1;
            final int src = index + 1;

            System.arraycopy(mIcons, src, mIcons, index, count);
            System.arraycopy(mIconIds, src, mIconIds, index, count);

            mRollo.mState.iconCount--;
            final int last = mState.iconCount;

            mIcons[last] = null;
            mIconIds[last] = 0;
        }

        /**
         * Send the apps list structures to RS.
         */
        private void saveAppsList() {
            mRS.contextBindRootScript(null);

            mAllocIconIds.data(mIconIds);

            if (mScript != null) { // this happens when we init it
                mScript.bindAllocation(mAllocIconIds, Defines.ALLOC_ICON_IDS);
            }

            mState.save();

            // Note: mScript may be null if we haven't initialized it yet.
            // In that case, this is a no-op.
            if (mInvokeResetWAR != null && mNeedResetHWWar) {
            	mNeedResetHWWar = false;
            	
                mInvokeResetWAR.execute();
            }
            
            mRS.contextBindRootScript(mScript);
        }

        void initTouchState() {
            int width = getWidth();
            int height = getHeight();
            
            int cellHeight = 0;
            int offsetY = 0;
            
             if (DisplayMetrics.DENSITY_DEVICE <= 120) {
                if (mRowsPerPage == 3) {
    				cellHeight = width > height ? (width / mColumnsPerPage - 8) : (width / mColumnsPerPage + 7);
    				offsetY = width > height ? 1 : 0;
                } else {
                	cellHeight = width / 4 + 12;
                }
            } if (DisplayMetrics.DENSITY_DEVICE <= 160) {
            	if (mRowsPerPage == 3) {
            		cellHeight = width / 6 + 14;
            	} else {
            		cellHeight = width / 4 + 7;
            	}            
            }else {
            	cellHeight = 145; 
            }                        
            
            int centerY = (height / 2);                        
			if (mRowsPerPage == 3) {
				if (width > height) {
					mTouchYBorders[0] = centerY - (cellHeight * 3 / 2);
	            	mTouchYBorders[1] = centerY - (cellHeight / 2);
	            	mTouchYBorders[2] = centerY + (cellHeight / 2);
	            	mTouchYBorders[3] = height;
				} else {
					mTouchYBorders[0] = centerY - (cellHeight * 3 / 2) - offsetY;
	            	mTouchYBorders[1] = centerY - (cellHeight / 2) - offsetY;
	            	mTouchYBorders[2] = centerY + (cellHeight / 2) - offsetY;
	            	mTouchYBorders[3] = centerY + (cellHeight * 3 / 2) - offsetY;					
				}

			} else {
            	mTouchYBorders[0] = centerY - (cellHeight * 2);
            	mTouchYBorders[1] = centerY - cellHeight;
            	mTouchYBorders[2] = centerY;
            	mTouchYBorders[3] = centerY + cellHeight;
            	mTouchYBorders[4] = centerY + (cellHeight * 2);
			}

            int centerX = (width / 2);
			if (mColumnsPerPage == 4) {
	            mTouchXBorders[0] = 0;
	            mTouchXBorders[1] = centerX - (width / mColumnsPerPage);
	            mTouchXBorders[2] = centerX;
	            mTouchXBorders[3] = centerX + (width / mColumnsPerPage);
	            mTouchXBorders[4] = width;
			} else {				
	            mTouchXBorders[0] = 0;
	            mTouchXBorders[1] = centerX - 2 * (width / mColumnsPerPage);
	            mTouchXBorders[2] = centerX - (width / mColumnsPerPage);
	            mTouchXBorders[3] = centerX;
	            mTouchXBorders[4] = centerX + (width / mColumnsPerPage);
	            mTouchXBorders[5] = centerX + 2 * (width / mColumnsPerPage);
	            mTouchXBorders[6] = width;
			}           
        }

        void fling() {
            mInvokeFling.execute();
        }

        void move() {
            mInvokeMove.execute();
        }

        void moveTo(float row) {
            mState.targetPos = row;
            mState.save();
            mInvokeMoveTo.execute();
        }

        int chooseTappedIcon(int x, int y, float pos) {
            // Adjust for scroll position if not zero.
            y += (pos - ((int)pos)) * (mTouchYBorders[1] - mTouchYBorders[0]);

            int col = -1;
            int row = -1;
            for (int i=0; i<mColumnsPerPage; i++) {
                if (x >= mTouchXBorders[i] && x < mTouchXBorders[i+1]) {
                    col = i;
                    break;
                }
            }
            for (int i=0; i<mRowsPerPage; i++) {
                if (y >= mTouchYBorders[i] && y < mTouchYBorders[i+1]) {
                    row = i;
                    break;
                }
            }

            if (row < 0 || col < 0) {
                return -1;
            }

			int index = (((int)pos) * mColumnsPerPage)
                    + (row * mColumnsPerPage) + col;

            if (index >= mState.iconCount) {
                return -1;
            } else {
                return index;
            }
        }

        /**
         * You need to call save() on mState on your own after calling this.
         *
         * @return the index of the icon that was selected.
         */
        int selectIcon(int x, int y, float pos, int pressed) {
            final int index = chooseTappedIcon(x, y, pos);
            selectIcon(index, pressed);
            return index;
        }

        /**
         * Select the icon at the given index.
         *
         * @param index The index.
         * @param pressed one of SELECTED_PRESSED or SELECTED_FOCUSED
         */
        void selectIcon(int index, int pressed) {
            if (mAllAppsList == null || index < 0 || index >= mAllAppsList.size()) {
                mState.selectedIconIndex = -1;
                if (mLastSelection == SELECTION_ICONS) {
                    mLastSelection = SELECTION_NONE;
                }
            } else {
                if (pressed == SELECTED_FOCUSED) {
                    mLastSelection = SELECTION_ICONS;
                }

                int prev = mState.selectedIconIndex;
                mState.selectedIconIndex = index;

                ApplicationInfo info = mAllAppsList.get(index);
                Bitmap selectionBitmap = mSelectionBitmap;
                
                Utilities.drawSelectedAllAppsBitmap_11(mSelectionCanvas,
                        selectionBitmap.getWidth(), selectionBitmap.getHeight(),
                        pressed == SELECTED_PRESSED, info.iconBitmap);

                Allocation oldSelectedIcon = mSelectedIcon;

                //mRS.pauseRendering();
                try
                {
                    mSelectedIcon = Allocation.createFromBitmap(mRS, selectionBitmap,
                            Element.RGBA_8888(mRS), true);
                    frameBitmapAllocMips(mSelectedIcon, selectionBitmap.getWidth(), selectionBitmap.getHeight());
                    mSelectedIcon.uploadToTexture(0);
                    mState.selectedIconTexture = mSelectedIcon.getID();

                    mState.save();

                    if (oldSelectedIcon != null) {
                        oldSelectedIcon.destroy();
                    }


                } finally {                   
                    //mRS.resumeRendering();
                }

                if (prev != index) {
                    if (info.title != null && info.title.length() > 0) {
                        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
                    }
                }
            }
        }

        /**
         * You need to call save() on mState on your own after calling this.
         */
        void clearSelectedIcon() {
            mState.selectedIconIndex = -1;
        }

        void setHomeSelected(int mode) {
            final int prev = mLastSelection;
            switch (mode) {
            case SELECTED_NONE:
                mState.homeButtonId = mHomeButtonNormal.getID();
                break;
            case SELECTED_FOCUSED:
                mLastSelection = SELECTION_HOME;
                mState.homeButtonId = mHomeButtonFocused.getID();
                if (prev != SELECTION_HOME) {
                    sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
                }
                break;
            case SELECTED_PRESSED:
                mState.homeButtonId = mHomeButtonPressed.getID();
                break;
            }
        }

        public void dumpState() {
            Log.d(TAG, "mRollo.mWidth=" + mWidth);
            Log.d(TAG, "mRollo.mHeight=" + mHeight);
            Log.d(TAG, "mRollo.mIcons=" + Arrays.toString(mIcons));
            if (mIcons != null) {
                Log.d(TAG, "mRollo.mIcons.length=" + mIcons.length);
            }
            if (mIconIds != null) {
                Log.d(TAG, "mRollo.mIconIds.length=" + mIconIds.length);
            }
            Log.d(TAG, "mRollo.mIconIds=" +  Arrays.toString(mIconIds));
            Log.d(TAG, "mRollo.mTouchXBorders=" +  Arrays.toString(mTouchXBorders));
            Log.d(TAG, "mRollo.mTouchYBorders=" +  Arrays.toString(mTouchYBorders));
            Log.d(TAG, "mRollo.mHasSurface=" + mHasSurface);
            Log.d(TAG, "mRollo.mAppsDirty=" + mAppsDirty);
            Log.d(TAG, "mRollo.mState.newPositionX=" + mState.newPositionX);
            Log.d(TAG, "mRollo.mState.newTouchDown=" + mState.newTouchDown);
            Log.d(TAG, "mRollo.mState.flingVelocity=" + mState.flingVelocity);
            Log.d(TAG, "mRollo.mState.iconCount=" + mState.iconCount);
            Log.d(TAG, "mRollo.mState.selectedIconIndex=" + mState.selectedIconIndex);
            Log.d(TAG, "mRollo.mState.selectedIconTexture=" + mState.selectedIconTexture);
            Log.d(TAG, "mRollo.mState.zoomTarget=" + mState.zoomTarget);
            Log.d(TAG, "mRollo.mState.homeButtonId=" + mState.homeButtonId);
            Log.d(TAG, "mRollo.mState.targetPos=" + mState.targetPos);
            Log.d(TAG, "mRollo.mParams.bubbleWidth=" + mParams.bubbleWidth);
            Log.d(TAG, "mRollo.mParams.bubbleHeight=" + mParams.bubbleHeight);
            Log.d(TAG, "mRollo.mParams.bubbleBitmapWidth=" + mParams.bubbleBitmapWidth);
            Log.d(TAG, "mRollo.mParams.bubbleBitmapHeight=" + mParams.bubbleBitmapHeight);
            Log.d(TAG, "mRollo.mParams.homeButtonWidth=" + mParams.homeButtonWidth);
            Log.d(TAG, "mRollo.mParams.homeButtonHeight=" + mParams.homeButtonHeight);
            Log.d(TAG, "mRollo.mParams.homeButtonTextureWidth=" + mParams.homeButtonTextureWidth);
            Log.d(TAG, "mRollo.mParams.homeButtonTextureHeight=" + mParams.homeButtonTextureHeight);
        }
    }

    public void dumpState() {
        Log.d(TAG, "mRS=" + mRS);
        Log.d(TAG, "mRollo=" + mRollo);
        ApplicationInfo.dumpApplicationInfoList(TAG, "mAllAppsList", mAllAppsList);
        Log.d(TAG, "mArrowNavigation=" + mArrowNavigation);
        Log.d(TAG, "mStartedScrolling=" + mStartedScrolling);
        Log.d(TAG, "mLastSelection=" + mLastSelection);
        Log.d(TAG, "mLastSelectedIcon=" + mLastSelectedIcon);
        Log.d(TAG, "mVelocityTracker=" + mVelocityTracker);
        Log.d(TAG, "mTouchTracking=" + mTouchTracking);
        Log.d(TAG, "mShouldGainFocus=" + mShouldGainFocus);
        Log.d(TAG, "mZoomDirty=" + mZoomDirty);
        Log.d(TAG, "mAnimateNextZoom=" + mAnimateNextZoom);
        Log.d(TAG, "mZoom=" + mZoom);
        Log.d(TAG, "mPosX=" + mPosX);
        Log.d(TAG, "mVelocity=" + mVelocity);
        Log.d(TAG, "mMessageProc=" + mMessageProc);
        if (mRollo != null) {
            mRollo.dumpState();
        }
        if (mRS != null) {
            mRS.contextDump(0);
        }
    }
}


