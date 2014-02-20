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

package com.android.launcher3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.GestureDetector.SimpleOnGestureListener;

public class Indicator extends View implements Animation.AnimationListener {

    // padding between indicators
    private static final float INIDICATOR_PADDING = 7.0f;

    private static final long FUTURE_DURATION = 700;

    private static final long INTERVAL = 100;

    private Launcher mLauncher;

    private Workspace mWorkspace;

    private float[] mPosX = new float[Launcher.SCREEN_COUNT];

    private float[] mPosY = new float[Launcher.SCREEN_COUNT];

    private Bitmap mNormalBitmap;

    private Bitmap mPressedBitmap;

    private Bitmap mSelectedBitmap;

    /*
     * padding from between the view's left/right side to the first/last
     * indicator
     */
    private float mLeftAndRightPadding;

    private int mCurrentScreen = -1;

    /* GestureDetector to handle long press on the indicator */
    private GestureDetector mGestureDetector = new GestureDetector(new MyGestureDetector());

    /* If mIsLongPressed is true, all the indicator will be highlighted */
    private boolean mIsLongPressed = false;

    private CountDownTimer mLongPressCountDown = new CountDownTimer(FUTURE_DURATION, INTERVAL) {

        @Override
        public void onFinish() {
            mIsLongPressed = false;
            invalidate();
        }

        @Override
        public void onTick(long arg0) {
            // Do nothing
        }
    };

    /* If mIsPreview is true, should show the thumbnail. */
    private boolean mIsPreview = false;

    private int mCurrentSelected;

    private OnLongPressedListener mLongPressedListener;

    private OnPreviewListener mPreviewListener;

    private OnSelectListener mSelectListener;

    public Indicator(Context context) {
        super(context);
    }

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    void setLauncher(Launcher launcher) {
        mLauncher = launcher;
        mWorkspace = launcher.getWorkspace();
        if ( !mLauncher.isScreenPortrait() ) {
        	initialIndicatorDrawables();
        }

        mCurrentScreen = mWorkspace.getCurrentScreen();
        this.setLongClickable(true);
    }

    void setOnLongPressedListener(OnLongPressedListener longPressedListener) {
        mLongPressedListener = longPressedListener;
    }

    void setOnPreviewListener(OnPreviewListener previewListener) {
        mPreviewListener = previewListener;
    }

    void setOnSelectListener(OnSelectListener selectListener) {
        mSelectListener = selectListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ( !mLauncher.isScreenPortrait() ) {
        	drawIndicators(canvas);	
        }        
    }

    private void drawIndicators(Canvas canvas) {
        for (int i = 0; i < Launcher.SCREEN_COUNT; i++) {
            if (mIsLongPressed) {
                if ( mPressedBitmap != null ) canvas.drawBitmap(mPressedBitmap, mPosX[i], mPosY[i], null);
            } else if (mIsPreview && (i == mCurrentSelected)) {
                if ( mPressedBitmap != null ) canvas.drawBitmap(mPressedBitmap, mPosX[i], mPosY[i], null);
            } else if (mCurrentScreen == i) {
                if ( mSelectedBitmap != null ) canvas.drawBitmap(mSelectedBitmap, mPosX[i], mPosY[i], null);
            } else {
                if ( mNormalBitmap != null )canvas.drawBitmap(mNormalBitmap, mPosX[i], mPosY[i], null);
            }
        }

        return;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ( !mLauncher.isScreenPortrait() ) {
            return true;
        }

        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        final int action = event.getAction();
        final float x = event.getX();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (mIsPreview) {
                    calculateSelected(x);
                    if (mPreviewListener != null) {
                        mPreviewListener.onPreview(mCurrentSelected);
                    }
                    setIndex(x);
                }

                break;

            case MotionEvent.ACTION_UP:
                if (mIsPreview) {
                    if (mSelectListener != null) {
                        mSelectListener.onSelect(mCurrentSelected);
                    }
                }
                mLongPressCountDown.cancel();
                mIsLongPressed = false;
                mIsPreview = false;

                break;
        }

        invalidate();

        return super.onTouchEvent(event);
    }

    private void startLongPressCountDownTimer() {
        mLongPressCountDown.start();
    }

    private void calculateSelected(float x) {
        int i;
        float tempX = x - mLeftAndRightPadding + INIDICATOR_PADDING;
        if (tempX <= 0) {
            i = 0;
        } else {
            i = (int) (tempX * Launcher.SCREEN_COUNT / (INIDICATOR_PADDING
                    * (Launcher.SCREEN_COUNT + 1) + mNormalBitmap.getWidth()
                    * Launcher.SCREEN_COUNT));
            if (i > Launcher.SCREEN_COUNT - 1) {
                i = Launcher.SCREEN_COUNT - 1;
            }
        }

        mCurrentSelected = i;
    }

    public Launcher getLauncher() {
        return mLauncher;
    }

    public void computeXY() {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.indicator_background);
        int imageWidth = bmp.getWidth();
        mLeftAndRightPadding = (imageWidth - INIDICATOR_PADDING * (Launcher.SCREEN_COUNT - 1) - mNormalBitmap
                .getWidth()
                * (Launcher.SCREEN_COUNT)) / 2;

        for (int i = 0; i < Launcher.SCREEN_COUNT; i++) {
            mPosX[i] = mLeftAndRightPadding + i * (INIDICATOR_PADDING + mNormalBitmap.getWidth());
            mPosY[i] = 0;
        }
    }

    public void initialIndicatorDrawables() {
        Bitmap bitmapNormal = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.btn_home_switch_homepage_normal);
        Bitmap bitmapPressed = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.btn_home_switch_homepage_pressed);
        Bitmap bitmapSelected = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.btn_home_switch_homepage_selected);

        mNormalBitmap = bitmapNormal;
        mPressedBitmap = bitmapPressed;
        mSelectedBitmap = bitmapSelected;

        computeXY();
    }

    public void setIndex(float x) {
        int i;
        float tempX = x - mLeftAndRightPadding + INIDICATOR_PADDING;
        if (tempX <= 0) {
            i = 0;
        } else {
            i = (int) (tempX * Launcher.SCREEN_COUNT / (INIDICATOR_PADDING
                    * (Launcher.SCREEN_COUNT + 1) + mNormalBitmap.getWidth()
                    * Launcher.SCREEN_COUNT));
            if (i > Launcher.SCREEN_COUNT - 1) {
                i = Launcher.SCREEN_COUNT - 1;
            }
        }
        mCurrentScreen = i;
    }

    public int getCurrentIndex() {
        return mCurrentScreen;
    }

    class MyGestureDetector extends SimpleOnGestureListener {
        public void onLongPress(MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                mIsLongPressed = true;
                mIsPreview = true;
                if (mLongPressedListener != null) {
                    mLongPressedListener.onLongPressed();
                }
                startLongPressCountDownTimer();
                invalidate();
            }
        }
    }

    public interface OnLongPressedListener {
        public abstract void onLongPressed();
    }

    public interface OnPreviewListener {
        public abstract void onPreview(int index);
    }

    public interface OnSelectListener {
        public abstract void onSelect(int index);
    }

    public float getXPos(int index) {
        return mPosX[index];
    }

    public float getYPos(int index) {
        return mPosY[index];
    }

    public int getIndicatorWidth() {
        return mNormalBitmap.getWidth();
    }

    public void reset() {
        this.mIsLongPressed = false;
        this.mIsPreview = false;
    }

    public void setLastIndex(int whichScreen) {
        mCurrentScreen = whichScreen;
    }

    public void onAnimationStart(Animation anim) {
        setVisibility(View.VISIBLE);
    }

    public void onAnimationEnd(Animation anim) {
        setVisibility(View.GONE);
    }

    public void onAnimationRepeat(Animation anim) {
    }
}
