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

//import com.android.common.Search;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.StatusBarManager;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Intent.ShortcutIconResource;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.LiveFolders;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.WindowManagerImpl;
import android.view.View.MeasureSpec;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.LinearLayout;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.DataInputStream;
import java.lang.ref.WeakReference;

import android.widget.IMTKWidget;
import com.mediatek.featureoption.FeatureOption;

import com.android.launcher3.R;

import com.android.launcher3.HomeItem;
import com.android.launcher3.ScrollLayout;

import android.widget.Toast;

import android.widget.RelativeLayout;

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

/**
 * Default launcher application.
 */
public final class Launcher extends Activity
        implements View.OnClickListener, OnLongClickListener, LauncherModel.Callbacks, AllAppsView.Watcher {
    static final String TAG = "Launcher2.Launcher";
    static final String TAG_SURFACEWIDGET = "mtkWidgetView";
    static final boolean LOGD = true;
    public static boolean mAppLaunchTimeLog = false;

    static final boolean DEBUG_SURFACEWIDGET = true;
    static final boolean PROFILE_STARTUP = false;
    static final boolean DEBUG_WIDGETS = false;
    static final boolean DEBUG_USER_INTERFACE = false;

    private static final int WALLPAPER_SCREENS_SPAN = 2;

    private static final int MENU_GROUP_ADD = 1;
    private static final int MENU_GROUP_WALLPAPER = MENU_GROUP_ADD + 1;

    private static final int MENU_ADD = Menu.FIRST + 1;
    private static final int MENU_MANAGE_APPS = MENU_ADD + 1;
    private static final int MENU_WALLPAPER_SETTINGS = MENU_MANAGE_APPS + 1;
    private static final int MENU_SEARCH = MENU_WALLPAPER_SETTINGS + 1;
    private static final int MENU_NOTIFICATIONS = MENU_SEARCH + 1;
    private static final int MENU_SETTINGS = MENU_NOTIFICATIONS + 1;

    private static final int REQUEST_CREATE_SHORTCUT = 1;
    private static final int REQUEST_CREATE_LIVE_FOLDER = 4;
    private static final int REQUEST_CREATE_APPWIDGET = 5;
    private static final int REQUEST_PICK_APPLICATION = 6;
    private static final int REQUEST_PICK_SHORTCUT = 7;
    private static final int REQUEST_PICK_LIVE_FOLDER = 8;
    private static final int REQUEST_PICK_APPWIDGET = 9;
    private static final int REQUEST_PICK_WALLPAPER = 10;
	private static final int REQUEST_SET_THEME = 11;

    static final String EXTRA_SHORTCUT_DUPLICATE = "duplicate";

    static final int SCREEN_COUNT = 5;
    static final int DEFAULT_SCREEN = 2;
    static final int NUMBER_CELLS_X = 4;
    static final int NUMBER_CELLS_Y = 4;

    static final int DIALOG_CREATE_SHORTCUT = 1;
    static final int DIALOG_RENAME_FOLDER = 2;

    private static final String PREFERENCES = "launcher.preferences";

    // Type: int
    private static final String RUNTIME_STATE_CURRENT_SCREEN = "launcher.current_screen";
    // Type: boolean
    private static final String RUNTIME_STATE_ALL_APPS_FOLDER = "launcher.all_apps_folder";
    // Type: long
    private static final String RUNTIME_STATE_USER_FOLDERS = "launcher.user_folder";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_SCREEN = "launcher.add_screen";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_X = "launcher.add_cellX";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_Y = "launcher.add_cellY";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_SPAN_X = "launcher.add_spanX";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_SPAN_Y = "launcher.add_spanY";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_COUNT_X = "launcher.add_countX";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_COUNT_Y = "launcher.add_countY";
    // Type: int[]
    private static final String RUNTIME_STATE_PENDING_ADD_OCCUPIED_CELLS = "launcher.add_occupied_cells";
    // Type: boolean
    private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME = "launcher.rename_folder";
    // Type: long
    private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME_ID = "launcher.rename_folder_id";
    
    private static final String RUNTIME_STATE_CURRENT_INDICATOR = "launcher.current_indicator";
    
    private static final String VIDEO_WIDGET = "VideoWidgetProvider";

	private static final String STK_PACKAGE = "com.android.stk";
    
    private static final String STK2_PACKAGE = "com.android.stk2";

    
    static final String CURRENT_SKIN = "Current_skin";
    
    // video live wallpaper
    private static final String VIDEO_LIVE_WALLPAPER_PKG = "com.mediatek.vlw";
    private static final String VIDEO_LIVE_WALLPAPER_CLS = "com.mediatek.vlw.VideoEditor";
    private Indicator mIndicator;
    private ImageView mPreview;
    private boolean mIsPortrait = true;
    private Bitmap mThumbnail = null;
    static final float PREVIEW_SCALE = 100.0f / 320;
    static final int PREVIEW_PADDING = 4;
    private DragLayer mDragLayer;

    static final int APPWIDGET_HOST_ID = 1024;

    private static final Object sLock = new Object();
    private static int sScreen = DEFAULT_SCREEN;

    private final BroadcastReceiver mCloseSystemDialogsReceiver
            = new CloseSystemDialogsIntentReceiver();
    private final ContentObserver mWidgetObserver = new AppWidgetResetObserver();

    private LayoutInflater mInflater;

    private DragController mDragController;
    private Workspace mWorkspace;

    private AppWidgetManager mAppWidgetManager;
    private LauncherAppWidgetHost mAppWidgetHost;

    private CellLayout.CellInfo mAddItemCellInfo;
    private CellLayout.CellInfo mMenuAddInfo;
    private final int[] mCellCoordinates = new int[2];
    private FolderInfo mFolderInfo;

    private DeleteZone mDeleteZone;
    private HandleView mHandleView;
    private AllAppsView mAllAppsGrid;
    private ScrollLayout mScrollLayout;    
    
    private LinearLayout m_app_screen_indicator;
    private LinearLayout m_home_screen_indicator;
    
    private AllApps2D all_apps;    
    
    private RelativeLayout shortcut;
    
    /*
    private ImageView mHotseatLeft2;
    private ImageView mHotseatLeft;
    private ImageView mHotseatRight;
    private ImageView mHotseatRight2;
    */
    private HomeItem mHotseat_phone;
    private HomeItem mHotseat_contact;
    private HomeItem mHotseat_message;
    private HomeItem mHotseat_apps;
    
    private Bundle mSavedState;

    private SpannableStringBuilder mDefaultKeySsb = null;

    private boolean mWorkspaceLoading = true;

    private boolean mPaused = true;
    private boolean mRestoring;
    private boolean mWaitingForResult;
    private boolean mOnResumeNeedsLoad;

    private Bundle mSavedInstanceState;

    private LauncherModel mModel;
    private IconCache mIconCache;

    private static LocaleConfiguration sLocaleConfiguration = null;

    private ArrayList<ItemInfo> mDesktopItems = new ArrayList<ItemInfo>();
    private static HashMap<Long, FolderInfo> sFolders = new HashMap<Long, FolderInfo>();

    // Hotseats (quick-launch icons next to AllApps)
    private static final int NUM_HOTSEATS = 2;
    private String[] mHotseatConfig = null;
    private Intent[] mHotseats = null;
    private Drawable[] mHotseatIcons = null;
    private CharSequence[] mHotseatLabels = null;

	static Bitmap mIconBackgroundBitmap = null;
    
	private static WallpaperIntentReceiver sWallpaperReceiver = null;
	private boolean mStaticWallpaper = false;

	private static final int HIDE_WALLPAPER = 1;
	private boolean mShowAllApps;
	

/************************************************
**
** huyanwei add start
**
************************************************/
    public static Bitmap convertDrawableToBitmap(Drawable drawable) 
    {
              int width = drawable.getIntrinsicWidth();   
              int height = drawable.getIntrinsicHeight();
              Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;
              Bitmap bitmap = Bitmap.createBitmap(width, height, config);  
              Canvas canvas = new Canvas(bitmap);      
              drawable.setBounds(0, 0, width, height);
              drawable.draw(canvas);    
              return bitmap;
    }

     public static Drawable zoomDrawable(Drawable drawable, int w, int h)
     {
               int width = drawable.getIntrinsicWidth();
               int height= drawable.getIntrinsicHeight();
               Bitmap oldbmp = convertDrawableToBitmap(drawable); 
               Matrix matrix = new Matrix();              
               float scaleWidth = ((float)w / width);    
               float scaleHeight = ((float)h / height);
               matrix.postScale(scaleWidth, scaleHeight); 
               Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);   
               return new BitmapDrawable(newbmp);  
     }

    public static Drawable zoomDrawableWithBackground(Drawable drawable,Drawable bg_drawable,int w, int h)
    {
              //Resources resource= getBaseContext().getResources();
              //Resources resource= getResources();
              //Drawable bg_drawable = resource.getDrawable(R.drawable.bg);

              int bg_width = bg_drawable.getIntrinsicWidth();
              int bg_height = bg_drawable.getIntrinsicHeight();
              Bitmap.Config config = bg_drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;  
              Bitmap new_bg_bmp = Bitmap.createBitmap(bg_width, bg_height, config);  
              Canvas canvas = new Canvas(new_bg_bmp);      

              Bitmap bg_bmp = convertDrawableToBitmap(bg_drawable);
              canvas.drawBitmap(bg_bmp,0,0,null);

              int width = drawable.getIntrinsicWidth();
              int height= drawable.getIntrinsicHeight();
              Bitmap oldbmp = convertDrawableToBitmap(drawable); 
              Matrix matrix = new Matrix();              
              float scaleWidth = ((float)w / width);    
              float scaleHeight = ((float)h / height);
              matrix.postScale(scaleWidth, scaleHeight); 
              Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);   
              canvas.drawBitmap(newbmp,(bg_width-width)>>1,(bg_height-height)>>1,null);
              return new BitmapDrawable(new_bg_bmp);  
    }

/************************************************
**
** huyanwei add end
**
*************************************************/ 
	
	
	
	
	
	
	
	// greater than 80ms is OK on mt6573
	// But we need take care of the situation of back to home screen from
	// any other window by pressing home key
	private static final long TIME_OUT = 80;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case HIDE_WALLPAPER:
				if(mShowAllApps){
					mWorkspace.hideWallpaper(true);
					setBackground(false);
					Log.i(TAG, "hide wallpaper since AllAppsGrid 3D SurfaceView is there");
				}
				removeMessages(HIDE_WALLPAPER);
				break;
			default:
				Log.w(TAG, "unknown message " + msg.what);
				break;
			}
		}
	};
    private static final int QVGA_WIDTH = 240;
    private static final int QVGA_HEIGHT = 320;
    private static boolean isQVGAMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		String wallpaper = SystemProperties.get("launcher3.wallpaper");
		if ("static".equals(wallpaper)) {
			setTheme(android.R.style.Theme_NoTitleBar);
			// The current implementation registers a wallpaper intent receiver
			// to let other applications change the wallpaper.
			if (sWallpaperReceiver == null) {
				final Application application = getApplication();
				sWallpaperReceiver = new WallpaperIntentReceiver(application,
						this);
				IntentFilter filter = new IntentFilter(
						Intent.ACTION_WALLPAPER_CHANGED);
				application.registerReceiver(sWallpaperReceiver, filter);
			} else {
				sWallpaperReceiver.setLauncher(this);
			}

			mStaticWallpaper = true;
		}
        super.onCreate(savedInstanceState);
        isQVGAMode = isSpecialWidthAndHeight(QVGA_WIDTH, QVGA_HEIGHT);

        LauncherApplication app = ((LauncherApplication)getApplication());
        mModel = app.setLauncher(this);
        mIconCache = app.getIconCache();
        mDragController = new DragController(this);
        mInflater = getLayoutInflater();

		mIsPortrait = isScreenPortrait();

        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new LauncherAppWidgetHost(this, APPWIDGET_HOST_ID);
        mAppWidgetHost.startListening();

        if (PROFILE_STARTUP) {
            android.os.Debug.startMethodTracing("/sdcard/launcher");
        }

        loadHotseats();
        checkForLocaleChange();
        setWallpaperDimension();

        setContentView(R.layout.launcher);
        setupViews();

        registerContentObservers();

        lockAllApps();
        
        initialIconBackgroundBitmap();

        mSavedState = savedInstanceState;
        restoreState(mSavedState);

        if (PROFILE_STARTUP) {
            android.os.Debug.stopMethodTracing();
        }

        if (!mRestoring) {
        	mModel.setAllAppsDirty();
            mModel.startLoader(this, true);
        }

        // For handling default keys
        mDefaultKeySsb = new SpannableStringBuilder();
        Selection.setSelection(mDefaultKeySsb, 0);

        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mCloseSystemDialogsReceiver, filter);
        mAppLaunchTimeLog = checkAppLaunchTimeProperty();
    }
    
    public boolean isSpecialWidthAndHeight(int width, int height) {
        Display mDisplay = getWindowManager().getDefaultDisplay();
        final int mWidth = mDisplay.getWidth();
        final int mHeight = mDisplay.getHeight();

        return ((width == mWidth) && (height == mHeight));
    }

    public static boolean isQVGAMode() {
        return isQVGAMode;
    }
    
    public boolean isScreenPortrait() {
        DisplayMetrics dm = new DisplayMetrics();   
    	getWindowManager().getDefaultDisplay().getMetrics(dm);    	
    	int screenWidth = dm.widthPixels;
    	int screenHeight = dm.heightPixels;
    	
    	return screenHeight > screenWidth;
    }

    public IconCache getIconCache() {
    	return mIconCache;
    }
    
    private void checkForLocaleChange() {
        if (sLocaleConfiguration == null) {
            new AsyncTask<Void, Void, LocaleConfiguration>() {
                @Override
                protected LocaleConfiguration doInBackground(Void... unused) {
                    LocaleConfiguration localeConfiguration = new LocaleConfiguration();
                    readConfiguration(Launcher.this, localeConfiguration);
                    return localeConfiguration;
                }

                @Override
                protected void onPostExecute(LocaleConfiguration result) {
                    sLocaleConfiguration = result;
                    checkForLocaleChange();  // recursive, but now with a locale configuration
                }
            }.execute();
            return;
        }

        final Configuration configuration = getResources().getConfiguration();

        final String previousLocale = sLocaleConfiguration.locale;
        final String locale = configuration.locale.toString();

        final int previousMcc = sLocaleConfiguration.mcc;
        final int mcc = configuration.mcc;

        final int previousMnc = sLocaleConfiguration.mnc;
        final int mnc = configuration.mnc;

        boolean localeChanged = !locale.equals(previousLocale) || mcc != previousMcc || mnc != previousMnc;

        if (localeChanged) {
            sLocaleConfiguration.locale = locale;
            sLocaleConfiguration.mcc = mcc;
            sLocaleConfiguration.mnc = mnc;

            mIconCache.flush();
            loadHotseats();

            final LocaleConfiguration localeConfiguration = sLocaleConfiguration;
            new Thread("WriteLocaleConfiguration") {
                public void run() {
                    writeConfiguration(Launcher.this, localeConfiguration);
                }
            }.start();
        }

		final String previousSkin = readSkinSetting();
        final String skin = configuration.skin;
        boolean skinChanged = !skin.equalsIgnoreCase(previousSkin);
        
        if (skinChanged) {
        	writeSkinSetting(this, skin);
        	mIconCache.flush();
        }
    }

	private String readSkinSetting() {
    	String skin = Configuration.SKIN_UNDEFINED;
    	skin = getSharedPreferences(CURRENT_SKIN, 0).getString(CURRENT_SKIN, null);
        return skin;
    }
    
    private void writeSkinSetting(Context context, String skin) {
        SharedPreferences settings = context.getSharedPreferences(CURRENT_SKIN, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(CURRENT_SKIN, skin);

        // Don't forget to commit your edits!!!
        editor.commit();
    }

    private static class LocaleConfiguration {
        public String locale;
        public int mcc = -1;
        public int mnc = -1;
    }

    private static void readConfiguration(Context context, LocaleConfiguration configuration) {
        DataInputStream in = null;
        try {
            in = new DataInputStream(context.openFileInput(PREFERENCES));
            configuration.locale = in.readUTF();
            configuration.mcc = in.readInt();
            configuration.mnc = in.readInt();
        } catch (FileNotFoundException e) {
            // Ignore
        } catch (IOException e) {
            // Ignore
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    private static void writeConfiguration(Context context, LocaleConfiguration configuration) {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(context.openFileOutput(PREFERENCES, MODE_PRIVATE));
            out.writeUTF(configuration.locale);
            out.writeInt(configuration.mcc);
            out.writeInt(configuration.mnc);
            out.flush();
        } catch (FileNotFoundException e) {
            // Ignore
        } catch (IOException e) {
            //noinspection ResultOfMethodCallIgnored
            context.getFileStreamPath(PREFERENCES).delete();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    static int getScreen() {
        synchronized (sLock) {
            return sScreen;
        }
    }

    static void setScreen(int screen) {
        synchronized (sLock) {
            sScreen = screen;
        }
    }

    private void setWallpaperDimension() {
        WallpaperManager wpm = (WallpaperManager)getSystemService(WALLPAPER_SERVICE);

        Display display = getWindowManager().getDefaultDisplay();
        //to support landscape, we mustn't do this
        //boolean isPortrait = display.getWidth() < display.getHeight();

        //final int width = isPortrait ? display.getWidth() : display.getHeight();
        //final int height = isPortrait ? display.getHeight() : display.getWidth();
        
        final int width = display.getWidth();
        final int height = display.getHeight();
        wpm.suggestDesiredDimensions(width * WALLPAPER_SCREENS_SPAN, height);
    }

    // Note: This doesn't do all the client-id magic that BrowserProvider does
    // in Browser. (http://b/2425179)
    private Uri getDefaultBrowserUri() {
        String url = getString(R.string.default_browser_url);
        if (url.indexOf("{CID}") != -1) {
            url = url.replace("{CID}", "android-google");
        }
        return Uri.parse(url);
    }

    // Load the Intent templates from arrays.xml to populate the hotseats. For
    // each Intent, if it resolves to a single app, use that as the launch
    // intent & use that app's label as the contentDescription. Otherwise,
    // retain the ResolveActivity so the user can pick an app.
    private void loadHotseats() {
        if (mHotseatConfig == null) {
            mHotseatConfig = getResources().getStringArray(R.array.hotseats);
            if (mHotseatConfig.length > 0) {
                mHotseats = new Intent[mHotseatConfig.length];
                mHotseatLabels = new CharSequence[mHotseatConfig.length];
                mHotseatIcons = new Drawable[mHotseatConfig.length];
            } else {
                mHotseats = null;
                mHotseatIcons = null;
                mHotseatLabels = null;
            }

            TypedArray hotseatIconDrawables = getResources().obtainTypedArray(R.array.hotseat_icons);
            for (int i=0; i<mHotseatConfig.length; i++) {
                // load icon for this slot; currently unrelated to the actual activity
                try {
                    mHotseatIcons[i] = hotseatIconDrawables.getDrawable(i);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    Log.w(TAG, "Missing hotseat_icons array item #" + i);
                    mHotseatIcons[i] = null;
                }
            }
            hotseatIconDrawables.recycle();
        }

        PackageManager pm = getPackageManager();
        for (int i=0; i<mHotseatConfig.length; i++) {
            Intent intent = null;
            if (mHotseatConfig[i].equals("*BROWSER*")) {
                // magic value meaning "launch user's default web browser"
                // replace it with a generic web request so we can see if there is indeed a default
                String defaultUri = getString(R.string.default_browser_url);
                intent = new Intent(
                        Intent.ACTION_VIEW,
                        ((defaultUri != null)
                            ? Uri.parse(defaultUri)
                            : getDefaultBrowserUri())
                    ).addCategory(Intent.CATEGORY_BROWSABLE).putExtra("BROWSER", "LAUNCHER");
                // note: if the user launches this without a default set, she
                // will always be taken to the default URL above; this is
                // unavoidable as we must specify a valid URL in order for the
                // chooser to appear, and once the user selects something, that 
                // URL is unavoidably sent to the chosen app.
            } else {
                try {
                    intent = Intent.parseUri(mHotseatConfig[i], 0);
                } catch (java.net.URISyntaxException ex) {
                    Log.w(TAG, "Invalid hotseat intent: " + mHotseatConfig[i]);
                    // bogus; leave intent=null
                }
            }
            
            if (intent == null) {
                mHotseats[i] = null;
                mHotseatLabels[i] = getText(R.string.activity_not_found);
                continue;
            }

            if (LOGD) {
                Log.d(TAG, "loadHotseats: hotseat " + i 
                    + " initial intent=[" 
                    + intent.toUri(Intent.URI_INTENT_SCHEME)
                    + "]");
            }

            ResolveInfo bestMatch = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            List<ResolveInfo> allMatches = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (LOGD) { 
                Log.d(TAG, "Best match for intent: " + bestMatch);
                Log.d(TAG, "All matches: ");
                for (ResolveInfo ri : allMatches) {
                    Log.d(TAG, "  --> " + ri);
                }
            }
            // did this resolve to a single app, or the resolver?
            if (allMatches.size() == 0 || bestMatch == null) {
                // can't find any activity to handle this. let's leave the 
                // intent as-is and let Launcher show a toast when it fails 
                // to launch.
                mHotseats[i] = intent;

                // set accessibility text to "Not installed"
                mHotseatLabels[i] = getText(R.string.activity_not_found);
            } else {
                boolean found = false;
                for (ResolveInfo ri : allMatches) {
                    if (bestMatch.activityInfo.name.equals(ri.activityInfo.name)
                        && bestMatch.activityInfo.applicationInfo.packageName
                            .equals(ri.activityInfo.applicationInfo.packageName)) {
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    if (LOGD) Log.d(TAG, "Multiple options, no default yet");
                    // the bestMatch is probably the ResolveActivity, meaning the
                    // user has not yet selected a default
                    // so: we'll keep the original intent for now
                    mHotseats[i] = intent;

                    // set the accessibility text to "Select shortcut"
                    mHotseatLabels[i] = getText(R.string.title_select_shortcut);
                } else {
                    // we have an app!
                    // now reconstruct the intent to launch it through the front
                    // door
                    ComponentName com = new ComponentName(
                        bestMatch.activityInfo.applicationInfo.packageName,
                        bestMatch.activityInfo.name);
                    mHotseats[i] = new Intent(Intent.ACTION_MAIN).setComponent(com);

                    // load the app label for accessibility
                    mHotseatLabels[i] = bestMatch.activityInfo.loadLabel(pm);
                }
            }

            if (LOGD) {
                Log.d(TAG, "loadHotseats: hotseat " + i 
                    + " final intent=[" 
                    + ((mHotseats[i] == null)
                        ? "null"
                        : mHotseats[i].toUri(Intent.URI_INTENT_SCHEME))
                    + "] label=[" + mHotseatLabels[i]
                    + "]"
                    );
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mWaitingForResult = false;

        // The pattern used here is that a user PICKs a specific application,
        // which, depending on the target, might need to CREATE the actual target.

        // For example, the user would PICK_SHORTCUT for "Music playlist", and we
        // launch over to the Music app to actually CREATE_SHORTCUT.

        if (resultCode == RESULT_OK && mAddItemCellInfo != null) {
            switch (requestCode) {
                case REQUEST_PICK_APPLICATION:
                    completeAddApplication(this, data, mAddItemCellInfo);
                    break;
                case REQUEST_PICK_SHORTCUT:
                    processShortcut(data);
                    break;
                case REQUEST_CREATE_SHORTCUT:
                    completeAddShortcut(data, mAddItemCellInfo);
                    break;
                case REQUEST_PICK_LIVE_FOLDER:
                    addLiveFolder(data);
                    break;
                case REQUEST_CREATE_LIVE_FOLDER:
                    completeAddLiveFolder(data, mAddItemCellInfo);
                    break;
                case REQUEST_PICK_APPWIDGET:
                    addAppWidget(data);
                    break;
                case REQUEST_CREATE_APPWIDGET:
                    completeAddAppWidget(data, mAddItemCellInfo);
                    break;
                case REQUEST_PICK_WALLPAPER:
                    // We just wanted the activity result here so we can clear mWaitingForResult
                    break;
				case REQUEST_SET_THEME:
                    break;
            }
        } else if ((requestCode == REQUEST_PICK_APPWIDGET ||
                requestCode == REQUEST_CREATE_APPWIDGET) && resultCode == RESULT_CANCELED &&
                data != null) {
            // Clean up the appWidgetId if we canceled
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
		if(!mAllAppsGrid.isVisible()){
			setBackground(true);
		}
		View hostView = mWorkspace.getChildAt(mWorkspace.getCurrentScreen());
		View mtkWidgetView = mWorkspace.searchIMTKWidget(hostView);
		if (mtkWidgetView != null) {
		    ((IMTKWidget)mtkWidgetView).onResumeWhenShown(mWorkspace.getCurrentScreen());
		    if (DEBUG_SURFACEWIDGET) Log.e(TAG_SURFACEWIDGET, "onResumeWhenShown");
		}

        mPaused = false;
        
        loadWallpaper();
        if (mRestoring || mOnResumeNeedsLoad) {
            mWorkspaceLoading = true;
            mModel.startLoader(this, true);
            mRestoring = false;
            mOnResumeNeedsLoad = false;
        }
        mDragController.cancelDrag();
        
		if (mIndicator != null) {
			mIndicator.reset();
			if (mPreview != null && mPreview.getParent() != null) {
				WindowManagerImpl.getDefault().removeView(mPreview);
			}
			mIndicator.postInvalidate();
		}
		// If AllAppsGrid 3D SurfaceView is there and we get back from some other
		// window, then live wallpaper would have been started by WMS, so stop it
		// Need to wait some time for launcher to complete its animation and can
		// be caught by WMS as wallpaper target window
		// TODO: if AllAppsGrid 3D SurfaceView has its own window WMS will take
		// care of this correctly, remove this code
		if(mAllAppsGrid.isVisible()){
			mHandler.sendEmptyMessageDelayed(HIDE_WALLPAPER, TIME_OUT);
		}
    }


   @Override
  protected void onStop() {
       setBackground(false);
	super.onStop();
       }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;

	//setBackground(false);

        View hostView = mWorkspace.getChildAt(mWorkspace.getCurrentScreen());
		    View mtkWidgetView = mWorkspace.searchIMTKWidget(hostView);
		    if (mtkWidgetView != null) {
			      ((IMTKWidget)mtkWidgetView).onPauseWhenShown(mWorkspace.getCurrentScreen());
			      if (DEBUG_SURFACEWIDGET) Log.e(TAG_SURFACEWIDGET, "onPauseWhenShown");
		    }

		if (mWorkspace != null) {
            mWorkspace.setCurrentScreen(getCurrentWorkspaceScreen());
        }
		/*
        dismissPreview(mPreviousView);
        dismissPreview(mNextView);
        */
        mDragController.cancelDrag();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        // Flag the loader to stop early before switching
        mModel.stopLoader();
        mAllAppsGrid.surrender();
        return Boolean.TRUE;
    }

    // We can't hide the IME if it was forced open.  So don't bother
    /*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            final InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            inputManager.hideSoftInputFromWindow(lp.token, 0, new android.os.ResultReceiver(new
                        android.os.Handler()) {
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            Log.d(TAG, "ResultReceiver got resultCode=" + resultCode);
                        }
                    });
            Log.d(TAG, "called hideSoftInputFromWindow from onWindowFocusChanged");
        }
    }
    */

    private boolean acceptFilter() {
        final InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        return !inputManager.isFullscreenMode();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = super.onKeyDown(keyCode, event);
        if (!handled && acceptFilter() && keyCode != KeyEvent.KEYCODE_ENTER) {
            boolean gotKey = TextKeyListener.getInstance().onKeyDown(mWorkspace, mDefaultKeySsb,
                    keyCode, event);
            if (gotKey && mDefaultKeySsb != null && mDefaultKeySsb.length() > 0) {
                // something usable has been typed - start a search
                // the typed text will be retrieved and cleared by
                // showSearchDialog()
                // If there are multiple keystrokes before the search dialog takes focus,
                // onSearchRequested() will be called for every keystroke,
                // but it is idempotent, so it's fine.
                return onSearchRequested();
            }
        }

        // Eat the long press event so the keyboard doesn't come up.
        if (keyCode == KeyEvent.KEYCODE_MENU && event.isLongPress()) {
            return true;
        }

        return handled;
    }

    private String getTypedText() {
        return mDefaultKeySsb.toString();
    }

    private void clearTypedText() {
        mDefaultKeySsb.clear();
        mDefaultKeySsb.clearSpans();
        Selection.setSelection(mDefaultKeySsb, 0);
    }

    /**
     * Restores the previous state, if it exists.
     *
     * @param savedState The previous state.
     */
    private void restoreState(Bundle savedState) {
        if (savedState == null) {
            return;
        }

        final boolean allApps = savedState.getBoolean(RUNTIME_STATE_ALL_APPS_FOLDER, false);
        if (allApps) {
            showAllApps(false);
        }

        final int currentScreen = savedState.getInt(RUNTIME_STATE_CURRENT_SCREEN, -1);
        if (currentScreen > -1) {
            mWorkspace.setCurrentScreen(currentScreen);
            mIndicator.setLastIndex(currentScreen);
        }

        final int addScreen = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SCREEN, -1);
        if (addScreen > -1) {
            mAddItemCellInfo = new CellLayout.CellInfo();
            final CellLayout.CellInfo addItemCellInfo = mAddItemCellInfo;
            addItemCellInfo.valid = true;
            addItemCellInfo.screen = addScreen;
            addItemCellInfo.cellX = savedState.getInt(RUNTIME_STATE_PENDING_ADD_CELL_X);
            addItemCellInfo.cellY = savedState.getInt(RUNTIME_STATE_PENDING_ADD_CELL_Y);
            addItemCellInfo.spanX = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SPAN_X);
            addItemCellInfo.spanY = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SPAN_Y);
            addItemCellInfo.findVacantCellsFromOccupied(
                    savedState.getBooleanArray(RUNTIME_STATE_PENDING_ADD_OCCUPIED_CELLS),
                    savedState.getInt(RUNTIME_STATE_PENDING_ADD_COUNT_X),
                    savedState.getInt(RUNTIME_STATE_PENDING_ADD_COUNT_Y));
            mRestoring = true;
        }

        boolean renameFolder = savedState.getBoolean(RUNTIME_STATE_PENDING_FOLDER_RENAME, false);
        if (renameFolder) {
            long id = savedState.getLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID);
            mFolderInfo = mModel.getFolderById(this, sFolders, id);
            mRestoring = true;
        }
    }
    
    private long readTotalMem() {
        try {
            long memTotal = 0;
            FileInputStream is = new FileInputStream("/proc/meminfo");
            byte[] mBuffer = new byte[1024]; 
            int len = is.read(mBuffer);
            is.close();
            final int BUFLEN = mBuffer.length;
            for (int i=0; i<len && (memTotal == 0); i++) {
                if (matchText(mBuffer, i, "MemTotal")) {
                    i += 7;
                    memTotal = extractMemValue(mBuffer, i);
                }
                while (i < BUFLEN && mBuffer[i] != '\n') {
                    i++;
                }
            }
            return memTotal;
        } catch (java.io.FileNotFoundException e) {
        } catch (java.io.IOException e) {
        }
        return 0;
    }

     private long extractMemValue(byte[] buffer, int index) {
        while (index < buffer.length && buffer[index] != '\n') {
            if (buffer[index] >= '0' && buffer[index] <= '9') {
                int start = index;
                index++;
                while (index < buffer.length && buffer[index] >= '0'
                    && buffer[index] <= '9') {
                    index++;
                }
                String str = new String(buffer, 0, start, index-start);
                return ((long)Integer.parseInt(str)) * 1024;
            }
            index++;
        }
        return 0;
    }

    private boolean matchText(byte[] buffer, int index, String text) {
        int N = text.length();
        if ((index+N) >= buffer.length) {
            return false;
        }
        for (int i=0; i<N; i++) {
            if (buffer[index+i] != text.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    final long TOTAL_MEM = 256 * 1024 * 1024;
    final int OPENGLES_VERSION = 131072;

	private AllAppsView getAllAppsGrid(DragLayer dragLayer) {
		
		String allAppsGrid = SystemProperties.get("launcher3.allappsgrid", "2d");
		/*
		long cur_mem = readTotalMem();
		if (cur_mem < TOTAL_MEM ) {
			allAppsGrid = "2d";
		} else {
			String opengles = SystemProperties.get("ro.opengles.version");
			Log.e(TAG, "getAllAppsGrid.opengles = " + opengles);
			if(opengles == null || opengles.equals("")) {
				allAppsGrid = "2d";
			} else {
				int opengles_version = Integer.valueOf(opengles).intValue(); 
				if (opengles_version < OPENGLES_VERSION ) {
					allAppsGrid = "3d_11";
				} else {
					allAppsGrid = "3d_20";
				}
			}
		}
		*/
		allAppsGrid = "2d"; // huyanwei add it for only 2D
		ViewStub stub;
		if (allAppsGrid.equals("2d")) {
			stub = (ViewStub) dragLayer.findViewById(R.id.stub_all_apps_2d);
		} else if (allAppsGrid.equals("3d_11")) {
			stub = (ViewStub) dragLayer.findViewById(R.id.stub_all_apps_3d_11);
		} else  if (allAppsGrid.equals("3d_20")) {
			stub = (ViewStub) dragLayer.findViewById(R.id.stub_all_apps_3d_20);
		} else {
			stub = (ViewStub) dragLayer.findViewById(R.id.stub_all_apps_2d);
		}
		
		return (AllAppsView) stub.inflate();

	}

	void setBackground(boolean is_translate)
	{
	
		Intent i = new Intent("com.android.lanucher.Launcher3.UPDATE_STATUS");
		i.putExtra("mallapps", is_translate);
		sendBroadcast(i);

	}


    /**
     * Finds all the views we need and configure them properly.
     */
    private void setupViews() {
        DragController dragController = mDragController;

        DragLayer dragLayer = (DragLayer) findViewById(R.id.drag_layer);
        dragLayer.setDragController(dragController);
      
        
        mAllAppsGrid = getAllAppsGrid(dragLayer);
      
        mAllAppsGrid.setLauncher(this);
        mAllAppsGrid.setDragController(dragController);
        ((View) mAllAppsGrid).setWillNotDraw(false); // We don't want a hole punched in our window.
        // Manage focusability manually since this thing is always visible
        ((View) mAllAppsGrid).setFocusable(false);            
        
        /*
        mScrollLayout = (ScrollLayout) dragLayer.findViewById(R.id.ScrollLayoutGrid);
        mScrollLayout.setFocusable(false);
        mScrollLayout.setVisibility(View.GONE);
        */
        
        mWorkspace = (Workspace) dragLayer.findViewById(R.id.workspace);
        final Workspace workspace = mWorkspace;
        workspace.setHapticFeedbackEnabled(false);

        DeleteZone deleteZone = (DeleteZone) dragLayer.findViewById(R.id.delete_zone);
        mDeleteZone = deleteZone;       

/*        
        mHandleView = (HandleView) findViewById(R.id.all_apps_button);
        mHandleView.setLauncher(this);
        mHandleView.setOnClickListener(this);
        mHandleView.setOnLongClickListener(this);

        mHotseatLeft2 = (ImageView) findViewById(R.id.hotseat_left);
        mHotseatLeft2.setContentDescription(mHotseatLabels[0]);
        mHotseatLeft2.setImageDrawable(mHotseatIcons[0]);

        
        mHotseatLeft = (ImageView) findViewById(R.id.hotseat_left);
        mHotseatLeft.setContentDescription(mHotseatLabels[1]);
        mHotseatLeft.setImageDrawable(mHotseatIcons[1]);
        
        mHotseatRight = (ImageView) findViewById(R.id.hotseat_right);
        mHotseatRight.setContentDescription(mHotseatLabels[2]);
        mHotseatRight.setImageDrawable(mHotseatIcons[2]);

        mHotseatRight2 = (ImageView) findViewById(R.id.hotseat_right2);
        mHotseatRight2.setContentDescription(mHotseatLabels[3]);
        mHotseatRight2.setImageDrawable(mHotseatIcons[3]);
*/
        
        // {
/*        
        mHotseat_phone = (HomeItem)findViewById(R.id.home_item_phone);
        //mHotseat_phone.setImageDrawable(mHotseatIcons[0]);
        //mHotseat_phone.setText(mHotseatLabels[0]);
        mHotseat_phone.setImageDrawable(getResources().getDrawable(R.drawable.hotseat_phone));
        mHotseat_phone.setText(getResources().getString(R.string.hotseat_phone));
        mHotseat_phone.setOnClickListener(this);
        mHotseat_phone.setOnLongClickListener(this);        
                
        mHotseat_contact = (HomeItem)findViewById(R.id.home_item_contact);
        //mHotseat_contact.setImageDrawable(mHotseatIcons[1]);
        //mHotseat_contact.setText(mHotseatLabels[1]);
        mHotseat_contact.setImageDrawable(getResources().getDrawable(R.drawable.hotseat_contact));
        mHotseat_contact.setText(getResources().getString(R.string.hotseat_contact));        
        mHotseat_contact.setOnClickListener(this);
        mHotseat_contact.setOnLongClickListener(this);        

        mHotseat_message = (HomeItem)findViewById(R.id.home_item_message);
        //mHotseat_message.setImageDrawable(mHotseatIcons[2]);
        //mHotseat_message.setText(mHotseatLabels[2]);
        mHotseat_message.setImageDrawable(getResources().getDrawable(R.drawable.hotseat_message));
        mHotseat_message.setText(getResources().getString(R.string.hotseat_message));
        mHotseat_message.setOnClickListener(this);
        mHotseat_message.setOnLongClickListener(this);        
       
        mHotseat_apps = (HomeItem)findViewById(R.id.home_item_apps);
        //mHotseat_apps.setImageDrawable(mHotseatIcons[3]);
        //mHotseat_apps.setText(mHotseatLabels[3]);
        mHotseat_apps.setImageDrawable(getResources().getDrawable(R.drawable.hotseat_apps));
        mHotseat_apps.setText(getResources().getString(R.string.hotseat_apps));
        mHotseat_apps.setOnClickListener(this);
        mHotseat_apps.setOnLongClickListener(this);        
*/
        mHotseat_phone = (HomeItem)findViewById(R.id.home_item_phone);
        //mHotseat_phone.setImageDrawable(mHotseatIcons[0]);
        //mHotseat_phone.setText(mHotseatLabels[0]);
        mHotseat_phone.setImageDrawable(getResources().getDrawable(R.drawable.home_item_phone));
        mHotseat_phone.setText(getResources().getString(R.string.hotseat_phone));
        mHotseat_phone.setOnClickListener(this);
        mHotseat_phone.setOnLongClickListener(this);
        mHotseat_phone.setFocusable(true);
                
        mHotseat_contact = (HomeItem)findViewById(R.id.home_item_contact);
        //mHotseat_contact.setImageDrawable(mHotseatIcons[1]);
        //mHotseat_contact.setText(mHotseatLabels[1]);
        mHotseat_contact.setImageDrawable(getResources().getDrawable(R.drawable.home_item_contact));
        mHotseat_contact.setText(getResources().getString(R.string.hotseat_contact));        
        mHotseat_contact.setOnClickListener(this);
        mHotseat_contact.setOnLongClickListener(this);       
        mHotseat_contact.setFocusable(true);

        mHotseat_message = (HomeItem)findViewById(R.id.home_item_message);
        //mHotseat_message.setImageDrawable(mHotseatIcons[2]);
        //mHotseat_message.setText(mHotseatLabels[2]);
        mHotseat_message.setImageDrawable(getResources().getDrawable(R.drawable.home_item_message));
        mHotseat_message.setText(getResources().getString(R.string.hotseat_message));
        mHotseat_message.setOnClickListener(this);
        mHotseat_message.setOnLongClickListener(this);    
        mHotseat_message.setFocusable(true);
       
        mHotseat_apps = (HomeItem)findViewById(R.id.home_item_apps);
        //mHotseat_apps.setImageDrawable(mHotseatIcons[3]);
        //mHotseat_apps.setText(mHotseatLabels[3]);
        mHotseat_apps.setImageDrawable(getResources().getDrawable(R.drawable.home_item_apps));
        mHotseat_apps.setText(getResources().getString(R.string.hotseat_apps));
        mHotseat_apps.setOnClickListener(this);
        mHotseat_apps.setOnLongClickListener(this);    
        mHotseat_apps.setFocusable(true);
        mHotseat_apps.requestFocus();
        mHotseat_apps.setFocusableInTouchMode(true);
        // }
        
        workspace.setOnLongClickListener(this);
        workspace.setDragController(dragController);
        workspace.setLauncher(this);
        loadWallpaper();
        
        m_home_screen_indicator = (LinearLayout)findViewById(R.id.home_screen_indicator);        
        mWorkspace.setIndicators(m_home_screen_indicator);
                
        m_app_screen_indicator = (LinearLayout)findViewById(R.id.all_app_indicator);
        mAllAppsGrid.setIndicatorControl(m_app_screen_indicator);
        m_app_screen_indicator.setVisibility(View.GONE);        
        
        all_apps = (AllApps2D)findViewById(R.id.all_apps_view_2d);
        
        /*
        mPreviousView.setHapticFeedbackEnabled(false);
        mPreviousView.setOnLongClickListener(this);
        mNextView.setHapticFeedbackEnabled(false);
        mNextView.setOnLongClickListener(this);
		*/

        shortcut = (RelativeLayout) findViewById(R.id.all_apps_button_cluster);
        //shortcut.setBackgroundResource(R.drawable.homescreen_menu_shortcut_bg);
        //shortcut.getBackground().setAlpha(0xC0);        
        shortcut.setBackgroundResource(R.drawable.homescreen_menu_shortcut_black_bg);
        //shortcut.getBackground().setAlpha(0x40);        
        shortcut.getBackground().setAlpha(0x40);
        
        deleteZone.setLauncher(this);
        deleteZone.setDragController(dragController);
        deleteZone.setHandle(shortcut);

        dragController.setDragScoller(workspace);
        dragController.setDragListener(deleteZone);
        dragController.setScrollView(dragLayer);
        dragController.setMoveTarget(workspace);

        // The order here is bottom to top.
        dragController.addDropTarget(workspace);
        dragController.addDropTarget(deleteZone);

 		/* for landscape mode indicator*/
		mIndicator = (Indicator) findViewById(R.id.indicator);
		mIndicator.setLauncher(this);
		Bitmap indicatorBmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.indicator_background);    
    	mIndicator.getLayoutParams().width = indicatorBmp.getWidth();    
    	mIndicator.setVisibility(View.GONE);    	
        mIndicator.setClickable(false);
        mIndicator.setFocusable(false); 
    }
    
    private void loadWallpaper(){
    	mWorkspace.loadWallpaper(mStaticWallpaper);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void previousScreen(View v) {
        if (!isAllAppsVisible()) {
            mWorkspace.scrollLeft();
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void nextScreen(View v) {
        if (!isAllAppsVisible()) {
            mWorkspace.scrollRight();
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void launchHotSeat(View v) {
        if (isAllAppsVisible()) return;

        int index = -1;
/*        
        if (v.getId() == R.id.hotseat_left) {
            index = 1;
        } else if (v.getId() == R.id.hotseat_right) {
            index = 2;
        } else if (v.getId() == R.id.hotseat_left2) {
            index = 0;
        } else if (v.getId() == R.id.hotseat_right2) {
            index = 3;                    
        }
*/     
        /* 
        if (v.getId() == R.id.home_item_phone) {
            index = 0;
        } else if (v.getId() == R.id.home_item_contact) {
            index = 1;
        } else if (v.getId() == R.id.home_item_message) {
            index = 2;
        } else if (v.getId() == R.id.home_item_apps) {
            index = 3;                    
        }       
        // reload these every tap; you never know when they might change
        loadHotseats();
        if (index >= 0 && index < mHotseats.length && mHotseats[index] != null) {
            Intent intent = mHotseats[index];
            startActivitySafely(
                mHotseats[index],
                "hotseat"
            );
        }
        */
    }
    
    /**
     * Creates a view representing a shortcut.
     *
     * @param info The data structure describing the shortcut.
     *
     * @return A View inflated from R.layout.application.
     */
    View createShortcut(ShortcutInfo info) {
        return createShortcut(R.layout.application,
                (ViewGroup) mWorkspace.getChildAt(mWorkspace.getCurrentScreen()), info);
    }

    /**
     * Creates a view representing a shortcut inflated from the specified resource.
     *
     * @param layoutResId The id of the XML layout used to create the shortcut.
     * @param parent The group the shortcut belongs to.
     * @param info The data structure describing the shortcut.
     *
     * @return A View inflated from layoutResId.
     */
    View createShortcut(int layoutResId, ViewGroup parent, ShortcutInfo info) {
        TextView favorite = (TextView) mInflater.inflate(layoutResId, parent, false);

		final ComponentName component = info.intent.getComponent();
		if (component != null) {
			final String pkgName = component.getPackageName();
			if (Launcher.STK2_PACKAGE.equalsIgnoreCase(pkgName)
					|| Launcher.STK_PACKAGE.equalsIgnoreCase(pkgName)) {
				info.setIcon(mIconCache.getIcon(info.intent));
			}
		}
        Bitmap icon = info.getIcon(mIconCache);

	// huyanwei modify it
        Drawable background= new FastBitmapDrawable(icon);
        int width = background.getIntrinsicWidth();   
        int height = background.getIntrinsicHeight();
        background.setBounds(0, 0, width, height-6);
        favorite.setCompoundDrawables(null, background, null, null);

	/*
        favorite.setCompoundDrawablesWithIntrinsicBounds(null, new FastBitmapDrawable(icon), null, null);
	*/
        favorite.setText(info.title);
        favorite.setTag(info);
        favorite.setOnClickListener(this);

        return favorite;
    }
    
    static public Bitmap createCompoundBitmap(Bitmap bgBitmap, Bitmap iconBitmap) {
    	if ( bgBitmap == null ) {
    		return iconBitmap;
    	}
    	float sx = 1.0f;
    	float sy = 1.0f;

		if (DisplayMetrics.DENSITY_DEVICE <= 120) {
			sx = 0.86f;
			sy = 0.86f;
		}
    	/*if(mIndicator.getOrientation() == Indicator.ORIENTATION_VERTICAL){
    		sx = 0.95f;
    		sy = 0.95f;
    	}*/
    	final Bitmap bg = scaleBitmap(bgBitmap, sx, sy);
		final Bitmap icon = scaleBitmap(iconBitmap, sx, sy);
    	final int bgWidth = bg.getWidth();
		final int bgHeight = bg.getHeight();
		final int iconWidth = icon.getWidth();
		final int iconHeight = icon.getHeight();

		Bitmap compoundBitmap = Bitmap.createBitmap(bgWidth, bgWidth, Config.ARGB_8888);
		Canvas canvas = new Canvas(compoundBitmap);
		canvas.drawBitmap(bg, 0, 0, null);
		canvas.drawBitmap(icon, (bgWidth - iconWidth) / 2, (bgHeight - iconHeight) / 2, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		
		return compoundBitmap;
    }
    
    /**
     * scale a bitmap with with*sx, height*sy
     * @param bm
     * @param sx
     * @param sy
     * @return The sacled bitmap
     */
	public static Bitmap scaleBitmap(Bitmap bm, float sx, float sy) {
		if (sx == 1.0f && sy == 1.0f) {
			return bm;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy);
		return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
	}

    /**
     * Add an application shortcut to the workspace.
     *
     * @param data The intent describing the application.
     * @param cellInfo The position on screen where to create the shortcut.
     */
    void completeAddApplication(Context context, Intent data, CellLayout.CellInfo cellInfo) {
        cellInfo.screen = mWorkspace.getCurrentScreen();
        if (!findSingleSlot(cellInfo)) return;

        final ShortcutInfo info = mModel.getShortcutInfo(context.getPackageManager(),
                data, context);

        if (info != null) {
            info.setActivity(data.getComponent(), Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            info.container = ItemInfo.NO_ID;
            mWorkspace.addApplicationShortcut(info, cellInfo, isWorkspaceLocked());
        } else {
            Log.e(TAG, "Couldn't find ActivityInfo for selected application: " + data);
        }
    }

    /**
     * Add a shortcut to the workspace.
     *
     * @param data The intent describing the shortcut.
     * @param cellInfo The position on screen where to create the shortcut.
     */
    private void completeAddShortcut(Intent data, CellLayout.CellInfo cellInfo) {
        cellInfo.screen = mWorkspace.getCurrentScreen();
        if (!findSingleSlot(cellInfo)) return;

        final ShortcutInfo info = mModel.addShortcut(this, data, cellInfo, false);

        if (!mRestoring) {
            final View view = createShortcut(info);
            mWorkspace.addInCurrentScreen(view, cellInfo.cellX, cellInfo.cellY, 1, 1,
                    isWorkspaceLocked());
        }
    }


    /**
     * Add a widget to the workspace.
     *
     * @param data The intent describing the appWidgetId.
     * @param cellInfo The position on screen where to create the widget.
     */
    private void completeAddAppWidget(Intent data, CellLayout.CellInfo cellInfo) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

        if (LOGD) Log.d(TAG, "dumping extras content=" + extras.toString());

        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        // Calculate the grid spans needed to fit this widget
        CellLayout layout = (CellLayout) mWorkspace.getChildAt(cellInfo.screen);
        int[] spans = layout.rectToCell(appWidgetInfo.minWidth, appWidgetInfo.minHeight);

        // Try finding open space on Launcher screen
        final int[] xy = mCellCoordinates;
        if (!findSlot(cellInfo, xy, spans[0], spans[1])) {
            if (appWidgetId != -1) mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            return;
        }

        // Build Launcher-specific widget info and save to database
        LauncherAppWidgetInfo launcherInfo = new LauncherAppWidgetInfo(appWidgetId);
        launcherInfo.spanX = spans[0];
        launcherInfo.spanY = spans[1];

        LauncherModel.addItemToDatabase(this, launcherInfo,
                LauncherSettings.Favorites.CONTAINER_DESKTOP,
                mWorkspace.getCurrentScreen(), xy[0], xy[1], false);

        if (!mRestoring) {
            mDesktopItems.add(launcherInfo);

            // Perform actual inflation because we're live
            launcherInfo.hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);

            launcherInfo.hostView.setAppWidget(appWidgetId, appWidgetInfo);
            launcherInfo.hostView.setTag(launcherInfo);

            mWorkspace.addInCurrentScreen(launcherInfo.hostView, xy[0], xy[1],
                    launcherInfo.spanX, launcherInfo.spanY, isWorkspaceLocked());
        }
    }

    public void removeAppWidget(LauncherAppWidgetInfo launcherInfo) {
        mDesktopItems.remove(launcherInfo);
        launcherInfo.hostView = null;
    }

    public LauncherAppWidgetHost getAppWidgetHost() {
        return mAppWidgetHost;
    }

    void closeSystemDialogs() {
        getWindow().closeAllPanels();

        try {
            dismissDialog(DIALOG_CREATE_SHORTCUT);
            // Unlock the workspace if the dialog was showing
        } catch (Exception e) {
            // An exception is thrown if the dialog is not visible, which is fine
        }

        try {
            dismissDialog(DIALOG_RENAME_FOLDER);
            // Unlock the workspace if the dialog was showing
        } catch (Exception e) {
            // An exception is thrown if the dialog is not visible, which is fine
        }

        // Whatever we were doing is hereby canceled.
        mWaitingForResult = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Close the menu
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            // also will cancel mWaitingForResult.
            closeSystemDialogs();

            boolean alreadyOnHome = ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                        != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            boolean allAppsVisible = isAllAppsVisible();
            if (!mWorkspace.isDefaultScreenShowing()) {
            	mWorkspace.controlMTKWidget( mWorkspace.getCurrentScreen() );
                mWorkspace.moveToDefaultScreen(alreadyOnHome && !allAppsVisible);
                mIndicator.setLastIndex(DEFAULT_SCREEN);
            }
            closeAllApps(alreadyOnHome && allAppsVisible);

            final View v = getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Do not call super here
        mSavedInstanceState = savedInstanceState;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(RUNTIME_STATE_CURRENT_SCREEN, mWorkspace.getCurrentScreen());
        outState.putInt(RUNTIME_STATE_CURRENT_INDICATOR, mIndicator.getCurrentIndex());

        final ArrayList<Folder> folders = mWorkspace.getOpenFolders();
        if (folders.size() > 0) {
            final int count = folders.size();
            long[] ids = new long[count];
            for (int i = 0; i < count; i++) {
                final FolderInfo info = folders.get(i).getInfo();
                ids[i] = info.id;
            }
            outState.putLongArray(RUNTIME_STATE_USER_FOLDERS, ids);
        } else {
            super.onSaveInstanceState(outState);
        }

        // TODO should not do this if the drawer is currently closing.
        if (isAllAppsVisible()) {
            outState.putBoolean(RUNTIME_STATE_ALL_APPS_FOLDER, true);
        }

        if (mAddItemCellInfo != null && mAddItemCellInfo.valid && mWaitingForResult) {
            final CellLayout.CellInfo addItemCellInfo = mAddItemCellInfo;
            final CellLayout layout = (CellLayout) mWorkspace.getChildAt(addItemCellInfo.screen);

            outState.putInt(RUNTIME_STATE_PENDING_ADD_SCREEN, addItemCellInfo.screen);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_X, addItemCellInfo.cellX);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_Y, addItemCellInfo.cellY);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SPAN_X, addItemCellInfo.spanX);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SPAN_Y, addItemCellInfo.spanY);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_COUNT_X, layout.getCountX());
            outState.putInt(RUNTIME_STATE_PENDING_ADD_COUNT_Y, layout.getCountY());
            outState.putBooleanArray(RUNTIME_STATE_PENDING_ADD_OCCUPIED_CELLS,
                   layout.getOccupiedCells());
        }

        if (mFolderInfo != null && mWaitingForResult) {
            outState.putBoolean(RUNTIME_STATE_PENDING_FOLDER_RENAME, true);
            outState.putLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID, mFolderInfo.id);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            mAppWidgetHost.stopListening();
        } catch (NullPointerException ex) {
            Log.w(TAG, "problem while stopping AppWidgetHost during Launcher destruction", ex);
        }

        TextKeyListener.getInstance().release();

        mModel.stopLoader();

        unbindDesktopItems();

        getContentResolver().unregisterContentObserver(mWidgetObserver);
        
        /*
        dismissPreview(mPreviousView);
        dismissPreview(mNextView);
        */

        unregisterReceiver(mCloseSystemDialogsReceiver);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (requestCode >= 0) mWaitingForResult = true;
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startSearch(String initialQuery, boolean selectInitialQuery,
            Bundle appSearchData, boolean globalSearch) {

        closeAllApps(true);

        if (initialQuery == null) {
            // Use any text typed in the launcher as the initial query
            initialQuery = getTypedText();
            clearTypedText();
        }
        if (appSearchData == null) {
            appSearchData = new Bundle();
            appSearchData.putString("source", "launcher-search");
        }

        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchManager.startSearch(initialQuery, selectInitialQuery, getComponentName(),
            appSearchData, globalSearch);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isWorkspaceLocked()) {
            return false;
        }

        super.onCreateOptionsMenu(menu);

        menu.add(MENU_GROUP_ADD, MENU_ADD, 0, R.string.menu_add)
                .setIcon(android.R.drawable.ic_menu_add)
                .setAlphabeticShortcut('A');
        menu.add(0, MENU_MANAGE_APPS, 0, R.string.menu_manage_apps)
                .setIcon(android.R.drawable.ic_menu_manage)
                .setAlphabeticShortcut('M');
        menu.add(MENU_GROUP_WALLPAPER, MENU_WALLPAPER_SETTINGS, 0, R.string.menu_wallpaper)
                 .setIcon(android.R.drawable.ic_menu_gallery)
                 .setAlphabeticShortcut('W');
        menu.add(0, MENU_SEARCH, 0, R.string.menu_search)
                .setIcon(android.R.drawable.ic_search_category_default)
                .setAlphabeticShortcut(SearchManager.MENU_KEY);
        menu.add(0, MENU_NOTIFICATIONS, 0, R.string.menu_notifications)
                .setIcon(com.android.internal.R.drawable.ic_menu_notifications)
                .setAlphabeticShortcut('N');

        final Intent settings = new Intent(android.provider.Settings.ACTION_SETTINGS);
        settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        menu.add(0, MENU_SETTINGS, 0, R.string.menu_settings)
                .setIcon(android.R.drawable.ic_menu_preferences).setAlphabeticShortcut('P')
                .setIntent(settings);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // If all apps is animating, don't show the menu, because we don't know
        // which one to show.
        if (mAllAppsGrid.isVisible() && !mAllAppsGrid.isOpaque()) {
            return false;
        }

        // Only show the add and wallpaper options when we're not in all apps.
        boolean visible = !mAllAppsGrid.isOpaque();
        menu.setGroupVisible(MENU_GROUP_ADD, visible);
        menu.setGroupVisible(MENU_GROUP_WALLPAPER, visible);

        // Disable add if the workspace is full.
        if (visible) {
            mMenuAddInfo = mWorkspace.findAllVacantCells(null);
            menu.setGroupEnabled(MENU_GROUP_ADD, mMenuAddInfo != null && mMenuAddInfo.valid);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ADD:
                addItems();
                return true;
            case MENU_MANAGE_APPS:
                manageApps();
                return true;
            case MENU_WALLPAPER_SETTINGS:
                startWallpaper();
                return true;
            case MENU_SEARCH:
                onSearchRequested();
                return true;
            case MENU_NOTIFICATIONS:
                showNotifications();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Indicates that we want global search for this activity by setting the globalSearch
     * argument for {@link #startSearch} to true.
     */

    @Override
    public boolean onSearchRequested() {
        startSearch(null, false, null, true);
        return true;
    }

    public boolean isWorkspaceLocked() {
        return mWorkspaceLoading || mWaitingForResult;
    }

    private void addItems() {
        closeAllApps(true);
        showAddDialog(mMenuAddInfo);
    }

    private void manageApps() {
        startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS));
    }

    void addAppWidget(Intent data) {
        // TODO: catch bad widget exception when sent
        int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidget = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        if (appWidget.provider.getClassName().contains(VIDEO_WIDGET) && mWorkspace.searchIMTKWidget(mWorkspace) != null) {
            if (mAppWidgetHost != null) {
            	mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            	Toast.makeText(this, R.string.one_video_widget, Toast.LENGTH_LONG).show();
            	return;
            }
        }
        
        if (appWidget.configure != null) {
            // Launch over to configure widget, if needed
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidget.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            startActivityForResultSafely(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            // Otherwise just add it
            onActivityResult(REQUEST_CREATE_APPWIDGET, Activity.RESULT_OK, data);
        }
    }

    void processShortcut(Intent intent) {
        // Handle case where user selected "Applications"
        String applicationName = getResources().getString(R.string.group_applications);
        String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);

        if (applicationName != null && applicationName.equals(shortcutName)) {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
            pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
            pickIntent.putExtra(Intent.EXTRA_TITLE, getText(R.string.title_select_application));
            startActivityForResultSafely(pickIntent, REQUEST_PICK_APPLICATION);
        } else {
            startActivityForResultSafely(intent, REQUEST_CREATE_SHORTCUT);
        }
    }

    void addLiveFolder(Intent intent) {
        // Handle case where user selected "Folder"
        String folderName = getResources().getString(R.string.group_folder);
        String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);

        if (folderName != null && folderName.equals(shortcutName)) {
            addFolder();
        } else {
            startActivityForResultSafely(intent, REQUEST_CREATE_LIVE_FOLDER);
        }
    }

    void addFolder() {
        UserFolderInfo folderInfo = new UserFolderInfo();
        folderInfo.title = getText(R.string.folder_name);

        CellLayout.CellInfo cellInfo = mAddItemCellInfo;
        cellInfo.screen = mWorkspace.getCurrentScreen();
        if (!findSingleSlot(cellInfo)) return;

        // Update the model
        LauncherModel.addItemToDatabase(this, folderInfo,
                LauncherSettings.Favorites.CONTAINER_DESKTOP,
                mWorkspace.getCurrentScreen(), cellInfo.cellX, cellInfo.cellY, false);
        sFolders.put(folderInfo.id, folderInfo);

        // Create the view
        FolderIcon newFolder = FolderIcon.fromXml(R.layout.folder_icon, this,
                (ViewGroup) mWorkspace.getChildAt(mWorkspace.getCurrentScreen()), folderInfo);
        mWorkspace.addInCurrentScreen(newFolder,
                cellInfo.cellX, cellInfo.cellY, 1, 1, isWorkspaceLocked());
    }

    void removeFolder(FolderInfo folder) {
        sFolders.remove(folder.id);
    }

    private void completeAddLiveFolder(Intent data, CellLayout.CellInfo cellInfo) {
        cellInfo.screen = mWorkspace.getCurrentScreen();
        if (!findSingleSlot(cellInfo)) return;

        final LiveFolderInfo info = addLiveFolder(this, data, cellInfo, false);

        if (!mRestoring) {
            final View view = LiveFolderIcon.fromXml(R.layout.live_folder_icon, this,
                (ViewGroup) mWorkspace.getChildAt(mWorkspace.getCurrentScreen()), info);
            mWorkspace.addInCurrentScreen(view, cellInfo.cellX, cellInfo.cellY, 1, 1,
                    isWorkspaceLocked());
        }
    }

    static LiveFolderInfo addLiveFolder(Context context, Intent data,
            CellLayout.CellInfo cellInfo, boolean notify) {

        Intent baseIntent = data.getParcelableExtra(LiveFolders.EXTRA_LIVE_FOLDER_BASE_INTENT);
        String name = data.getStringExtra(LiveFolders.EXTRA_LIVE_FOLDER_NAME);

        Drawable icon = null;
        Intent.ShortcutIconResource iconResource = null;

        Parcelable extra = data.getParcelableExtra(LiveFolders.EXTRA_LIVE_FOLDER_ICON);
        if (extra != null && extra instanceof Intent.ShortcutIconResource) {
            try {
                iconResource = (Intent.ShortcutIconResource) extra;
                final PackageManager packageManager = context.getPackageManager();
                Resources resources = packageManager.getResourcesForApplication(
                        iconResource.packageName);
                final int id = resources.getIdentifier(iconResource.resourceName, null, null);
                icon = resources.getDrawable(id);
            } catch (Exception e) {
                Log.w(TAG, "Could not load live folder icon: " + extra);
            }
        }

        if (icon == null) {
            icon = context.getResources().getDrawable(R.drawable.ic_launcher_folder);
        }

        final LiveFolderInfo info = new LiveFolderInfo();
        info.icon = Utilities.createIconBitmap(icon, context);
        info.title = name;
        info.iconResource = iconResource;
        info.uri = data.getData();
        info.baseIntent = baseIntent;
        info.displayMode = data.getIntExtra(LiveFolders.EXTRA_LIVE_FOLDER_DISPLAY_MODE,
                LiveFolders.DISPLAY_MODE_GRID);

        LauncherModel.addItemToDatabase(context, info, LauncherSettings.Favorites.CONTAINER_DESKTOP,
                cellInfo.screen, cellInfo.cellX, cellInfo.cellY, notify);
        sFolders.put(info.id, info);

        return info;
    }

    private boolean findSingleSlot(CellLayout.CellInfo cellInfo) {
        final int[] xy = new int[2];
        if (findSlot(cellInfo, xy, 1, 1)) {
            cellInfo.cellX = xy[0];
            cellInfo.cellY = xy[1];
            return true;
        }
        return false;
    }

    private boolean findSlot(CellLayout.CellInfo cellInfo, int[] xy, int spanX, int spanY) {
        if (!cellInfo.findCellForSpan(xy, spanX, spanY)) {
            boolean[] occupied = mSavedState != null ?
                    mSavedState.getBooleanArray(RUNTIME_STATE_PENDING_ADD_OCCUPIED_CELLS) : null;
            cellInfo = mWorkspace.findAllVacantCells(occupied);
            if (!cellInfo.findCellForSpan(xy, spanX, spanY)) {
                Toast.makeText(this, getString(R.string.out_of_space), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void showNotifications() {
        final StatusBarManager statusBar = (StatusBarManager) getSystemService(STATUS_BAR_SERVICE);
        if (statusBar != null) {
            statusBar.expand();
        }
    }

    private void startWallpaper() {
        closeAllApps(true);
        final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
        Intent chooser = Intent.createChooser(pickWallpaper,
                getText(R.string.chooser_wallpaper));
        // NOTE: Adds a configure option to the chooser if the wallpaper supports it
        //       Removed in Eclair MR1
//        WallpaperManager wm = (WallpaperManager)
//                getSystemService(Context.WALLPAPER_SERVICE);
//        WallpaperInfo wi = wm.getWallpaperInfo();
//        if (wi != null && wi.getSettingsActivity() != null) {
//            LabeledIntent li = new LabeledIntent(getPackageName(),
//                    R.string.configure_wallpaper, 0);
//            li.setClassName(wi.getPackageName(), wi.getSettingsActivity());
//            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { li });
//        }
        startActivityForResult(chooser, REQUEST_PICK_WALLPAPER);
    }
    private void startVideoWallpaper() {
        mWorkspace.hideWallpaper(true);
        Intent intent = new Intent();
        ComponentName cpn = new ComponentName(VIDEO_LIVE_WALLPAPER_PKG, VIDEO_LIVE_WALLPAPER_CLS);
        intent.setComponent(cpn);
        startActivityForResult(intent, REQUEST_PICK_WALLPAPER);
    }
    /**
     * Registers various content observers. The current implementation registers
     * only a favorites observer to keep track of the favorites applications.
     */
    private void registerContentObservers() {
        ContentResolver resolver = getContentResolver();
        resolver.registerContentObserver(LauncherProvider.CONTENT_APPWIDGET_RESET_URI,
                true, mWidgetObserver);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_HOME:
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (SystemProperties.getInt("debug.launcher3.dumpstate", 0) != 0) {
                        dumpState();
                        return true;
                    }
                    break;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_HOME:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (isAllAppsVisible()) {
            closeAllApps(true);
        } else {
            closeFolder();
        }
        /*
        dismissPreview(mPreviousView);
        dismissPreview(mNextView);
        */
    }

    private void closeFolder() {
        Folder folder = mWorkspace.getOpenFolder();
        if (folder != null) {
            closeFolder(folder);
        }
    }

    void closeFolder(Folder folder) {
        folder.getInfo().opened = false;
        ViewGroup parent = (ViewGroup) folder.getParent();
        if (parent != null) {
            parent.removeView(folder);
            if (folder instanceof DropTarget) {
                // Live folders aren't DropTargets.
                mDragController.removeDropTarget((DropTarget)folder);
            }
        }
        folder.onClose();
    }

    /**
     * Re-listen when widgets are reset.
     */
    private void onAppWidgetReset() {
        mAppWidgetHost.startListening();
    }

    /**
     * Go through the and disconnect any of the callbacks in the drawables and the views or we
     * leak the previous Home screen on orientation change.
     */
    private void unbindDesktopItems() {
        for (ItemInfo item: mDesktopItems) {
            item.unbind();
        }
    }

    public Intent fixedintentObject(Intent intent)
    {
    	Intent intent_result ;
        PackageManager pm = getPackageManager();
    	ResolveInfo bestMatch = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        List<ResolveInfo> allMatches = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (LOGD) { 
            Log.d(TAG, "Best match for intent: " + bestMatch);
            Log.d(TAG, "All matches: ");
            for (ResolveInfo ri : allMatches) {
                Log.d(TAG, "  --> " + ri);
            }
        }
        // did this resolve to a single app, or the resolver?
        if (allMatches.size() == 0 || bestMatch == null) {
            // can't find any activity to handle this. let's leave the 
            // intent as-is and let Launcher show a toast when it fails 
            // to launch.
        	intent_result = intent;
            // set accessibility text to "Not installed"
            //mHotseatLabels[i] = getText(R.string.activity_not_found);
        } else {
            boolean found = false;
            for (ResolveInfo ri : allMatches) {
                if (bestMatch.activityInfo.name.equals(ri.activityInfo.name)
                    && bestMatch.activityInfo.applicationInfo.packageName
                        .equals(ri.activityInfo.applicationInfo.packageName)) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                if (LOGD) 
                	Log.d(TAG, "Multiple options, no default yet");
                // the bestMatch is probably the ResolveActivity, meaning the
                // user has not yet selected a default
                // so: we'll keep the original intent for now
                
                intent_result = intent;

                // set the accessibility text to "Select shortcut"
                //mHotseatLabels[i] = getText(R.string.title_select_shortcut);
            } else {
                // we have an app!
                // now reconstruct the intent to launch it through the front
                // door
                ComponentName com = new ComponentName(
                    bestMatch.activityInfo.applicationInfo.packageName,
                    bestMatch.activityInfo.name);                
                intent_result = new Intent(Intent.ACTION_MAIN).setComponent(com);
                // load the app label for accessibility
                //mHotseatLabels[i] = bestMatch.activityInfo.loadLabel(pm);
            }
        }
        return intent_result;
    }
    
    /**
     * Launches the intent referred by the clicked shortcut.
     *
     * @param v The view representing the clicked shortcut.
     */
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof ShortcutInfo) {
            // Open shortcut
            final Intent intent = ((ShortcutInfo) tag).intent;
            int[] pos = new int[2];
            v.getLocationOnScreen(pos);
            intent.setSourceBounds(new Rect(pos[0], pos[1],
                    pos[0] + v.getWidth(), pos[1] + v.getHeight()));
            startActivitySafely(intent, tag);
        } else if (tag instanceof FolderInfo) {
            handleFolderClick((FolderInfo) tag);
        } else if (v == mHandleView) {
            if (isAllAppsVisible()) {
                closeAllApps(true);
            } else {
                showAllApps(true);
            }
        } else if (v == mHotseat_phone) {           
		/*
        	Intent intent= new Intent();
        	intent.setAction("android.intent.action.DIAL");        	
        	intent.addCategory("android.intent.category.DEFAULT");        	
        	startActivity(fixedintentObject(intent));         
		*/
        	/*ComponentName com;
        	Intent intent= new Intent();        	
        	intent.setAction("com.android.phone.action.TOUCH_DIALER");        	
        	intent.addCategory("android.intent.category.DEFAULT");     
        	//intent.addCategory("android.intent.category.TAB");       	
        	com = new ComponentName("com.android.contacts","com.android.contacts.DialtactsActivity");
	        intent.setComponent(com);        	
        	startActivity(fixedintentObject(intent));*/
        	//yanweinan fix bug for launcher to DIAL bug. 2010.03.10
        	Intent invokeFrameworkDialer = new Intent();
                invokeFrameworkDialer.setClassName("com.android.contacts","com.android.contacts.DialtactsActivity");
                invokeFrameworkDialer.setAction(Intent.ACTION_DIAL);	
                startActivity(invokeFrameworkDialer);
         } else if (v == mHotseat_contact) {
    		// huyanwei add it     
        	Intent intent= new Intent();
        	ComponentName com;
        	//intent.setAction("android.intent.action.MAIN");
        	//intent.setAction("android.intent.action.VIEW");        	        	
        	//intent.setAction("com.android.contacts.action.LIST_DEFAULT");        	
        	//intent.addCategory("android.intent.category.DEFAULT");
        	intent.addCategory("android.intent.category.LAUNCHER");
        	//intent.addCategory("android.intent.category.BROWSABLE");
        	//intent.addCategory("android.intent.category.TAB");
        	
		//if(FeatureOption.MTK_IPHONE_STYLE_DAIL_APP)
		//{
		//	com = new ComponentName("com.android.contacts2","com.android.contacts2.DialtactsContactsEntryActivity");        	
		//}
		//else
		//{
        		//ComponentName com = new ComponentName("com.android.contacts","com.android.contacts.ContactsListActivity");
	        	com = new ComponentName("com.android.contacts","com.android.contacts.DialtactsContactsEntryActivity");        	
		//}

                intent.setComponent(com);         
                                
        	//intent.setDataAndType("vnd.android.cursor.item/sim-contact", "android:mimeType");
        	//intent.setData(Uri.parse("android.cursor.item/sim-contact"));
        	//intent.setType("mimeType");
            //intent.setData(Uri.parse("content://contacts/people"));
        	//intent.setData(Contacts.People.CONTENT_URI);
        	//intent.setAction("com.android.contacts.action.LIST_STREQUENT");        	
        	startActivity(fixedintentObject(intent));
        } else if (v == mHotseat_message) {
    		// huyanwei add it        	
        	Intent intent= new Intent();
        	//intent.setAction("android.intent.action.MAIN");
        	//intent.setAction("android.intent.action.VIEW");        	        	
        	//intent.setAction("com.android.contacts.action.LIST_DEFAULT");        	
        	//intent.addCategory("android.intent.category.DEFAULT");
        	intent.addCategory("android.intent.category.LAUNCHER");
        	//intent.addCategory("android.intent.category.BROWSABLE");
        	//intent.addCategory("android.intent.category.TAB");

        	ComponentName com = new ComponentName("com.android.mms","com.android.mms.ui.ConversationList");        	
            intent.setComponent(com);         
            startActivity(fixedintentObject(intent));
            
            /******************************************************
             *  start Browser :
             * ***************************************************
        	String defaultUri = getString(R.string.default_browser_url);
        	Intent intent= new Intent();
        	intent.setAction(Intent.ACTION_VIEW);        	
        	intent.addCategory(Intent.CATEGORY_BROWSABLE);
        	intent.setData((defaultUri != null)?Uri.parse(defaultUri):getDefaultBrowserUri());
        	intent.putExtra("BROWSER", "LAUNCHER");
        	startActivity(fixedintentObject(intent));
        	*******************************************************
        	***
        	*******************************************************/
        } else if (v == mHotseat_apps) {
        	// huyanwei add it
            if (isAllAppsVisible()) {
                closeAllApps(true);
            } else {
                showAllApps(true);
            }            
        }
    }

    void startActivitySafely(Intent intent, Object tag) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
        	if (mAppLaunchTimeLog) {
        		Log.i(TAG, "[AppLaunch] " + "Launcher.startActivitySafely() called");
        	}
        	if (FeatureOption.MTK_VLW_APP) {
        	    String pkg = null;
        	    ComponentName cpn = intent.getComponent();
        	    if (cpn != null) {
        		    pkg = cpn.getPackageName();
        	    }
        	    if (VIDEO_LIVE_WALLPAPER_PKG.equals(pkg)) {
        		    startVideoWallpaper();
        	    } else {
        	        startActivity(intent);
        	    }
            } else {
        		startActivity(intent);
        	}
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Unable to launch. tag=" + tag + " intent=" + intent, e);
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Launcher does not have the permission to launch " + intent +
                    ". Make sure to create a MAIN intent-filter for the corresponding activity " +
                    "or use the exported attribute for this activity. "
                    + "tag="+ tag + " intent=" + intent, e);
        }
    }
    
    void startActivityForResultSafely(Intent intent, int requestCode) {
        try {
            startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Launcher does not have the permission to launch " + intent +
                    ". Make sure to create a MAIN intent-filter for the corresponding activity " +
                    "or use the exported attribute for this activity.", e);
        }
    }

    private void handleFolderClick(FolderInfo folderInfo) {
        if (!folderInfo.opened) {
            // Close any open folder
            closeFolder();
            // Open the requested folder
            openFolder(folderInfo);
        } else {
            // Find the open folder...
            Folder openFolder = mWorkspace.getFolderForTag(folderInfo);
            int folderScreen;
            if (openFolder != null) {
                folderScreen = mWorkspace.getScreenForView(openFolder);
                // .. and close it
                closeFolder(openFolder);
                if (folderScreen != mWorkspace.getCurrentScreen()) {
                    // Close any folder open on the current screen
                    closeFolder();
                    // Pull the folder onto this screen
                    openFolder(folderInfo);
                }
            }
        }
    }

    /**
     * Opens the user fodler described by the specified tag. The opening of the folder
     * is animated relative to the specified View. If the View is null, no animation
     * is played.
     *
     * @param folderInfo The FolderInfo describing the folder to open.
     */
    private void openFolder(FolderInfo folderInfo) {
        Folder openFolder;

        if (folderInfo instanceof UserFolderInfo) {
            openFolder = UserFolder.fromXml(this);
        } else if (folderInfo instanceof LiveFolderInfo) {
            openFolder = com.android.launcher3.LiveFolder.fromXml(this, folderInfo);
        } else {
            return;
        }

        openFolder.setDragController(mDragController);
        openFolder.setLauncher(this);

        openFolder.bind(folderInfo);
        folderInfo.opened = true;

        mWorkspace.addInScreen(openFolder, folderInfo.screen, 0, 0, 4, 4);
        openFolder.onOpen();
    }

    public boolean onLongClick(View v) {
        switch (v.getId()) {
/*        
            case R.id.previous_screen:
                if (!isAllAppsVisible()) {
                    mWorkspace.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
                            HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
                    showPreviews(v);
                }
                return true;
            case R.id.next_screen:
                if (!isAllAppsVisible()) {
                    mWorkspace.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
                            HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
                    showPreviews(v);
                }
                return true;                
            case R.id.all_apps_button:
                if (!isAllAppsVisible()) {
                    mWorkspace.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
                            HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
                    showPreviews(v);
                }
                return true;
*/
        case R.id.home_item_phone:        	
        	//Toast.makeText(this, R.string.hotseat_phone, Toast.LENGTH_SHORT).show();	
        	return true;
        case R.id.home_item_contact:
        	//Toast.makeText(this, R.string.hotseat_contact, Toast.LENGTH_SHORT).show();	
        	return true;        
        case R.id.home_item_message:
        	//Toast.makeText(this, R.string.hotseat_message, Toast.LENGTH_SHORT).show();	
        	return true;
        
        case R.id.home_item_apps:
            if (!isAllAppsVisible()) {
                mWorkspace.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
                        HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
                showPreviews(v);
            }
            return true;        
        }

        

        if (isWorkspaceLocked()) {
            return false;
        }

        if (!(v instanceof CellLayout)) {
            v = (View) v.getParent();
        }

        CellLayout.CellInfo cellInfo = (CellLayout.CellInfo) v.getTag();

        // This happens when long clicking an item with the dpad/trackball
        if (cellInfo == null) {
            return true;
        }

        if (mWorkspace.allowLongPress()) {
            if (cellInfo.cell == null) {
                if (cellInfo.valid) {
                    // User long pressed on empty space
                    mWorkspace.setAllowLongPress(false);
                    mWorkspace.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
                            HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
                    showAddDialog(cellInfo);
                }
            } else {
                if (!(cellInfo.cell instanceof Folder)) {
                    // User long pressed on an item
                    View mtkWidgetView =mWorkspace.searchIMTKWidget(cellInfo.cell);
                	if (mtkWidgetView != null) {
            			    ((IMTKWidget)mtkWidgetView).startDrag();
            			    if (DEBUG_SURFACEWIDGET) Log.e(TAG_SURFACEWIDGET, "startDrag");
            		  }
                    /*mWorkspace.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
                            HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);*/
                    mWorkspace.startDrag(cellInfo);
                }
            }
        }
        return true;
    }

    @SuppressWarnings({"unchecked"})
    private void dismissPreview(final View v) {
        final PopupWindow window = (PopupWindow) v.getTag();
        if (window != null) {
            window.setOnDismissListener(new PopupWindow.OnDismissListener() {
                public void onDismiss() {
                    ViewGroup group = (ViewGroup) v.getTag(R.id.workspace);
                    int count = group.getChildCount();
                    for (int i = 0; i < count; i++) {
                        ((ImageView) group.getChildAt(i)).setImageDrawable(null);
                    }
                    ArrayList<Bitmap> bitmaps = (ArrayList<Bitmap>) v.getTag(R.id.icon);
                    for (Bitmap bitmap : bitmaps) bitmap.recycle();

                    v.setTag(R.id.workspace, null);
                    v.setTag(R.id.icon, null);
                    window.setOnDismissListener(null);
                }
            });
            window.dismiss();
        }
        v.setTag(null);
    }

    private void showPreviews(View anchor) {
        showPreviews(anchor, 0, mWorkspace.getChildCount());
    }

    private void showPreviews(final View anchor, int start, int end) {
        final Resources resources = getResources();
        final Workspace workspace = mWorkspace;

        CellLayout cell = ((CellLayout) workspace.getChildAt(start));
        
        float max = workspace.getChildCount();
        
        final Rect r = new Rect();
        resources.getDrawable(R.drawable.preview_backgroundnoprogressive).getPadding(r);
        int extraW = (int) ((r.left + r.right) * max);
        int extraH = r.top + r.bottom;

        int aW = cell.getWidth() - extraW;
        float w = aW / max;

        int width = cell.getWidth();
        int height = cell.getHeight();
        int x = cell.getLeftPadding();
        int y = cell.getTopPadding();
        width -= (x + cell.getRightPadding());
        height -= (y + cell.getBottomPadding());

        float scale = w / width;

        int count = end - start;

        final float sWidth = width * scale;
        float sHeight = height * scale;

        LinearLayout preview = new LinearLayout(this);

        PreviewTouchHandler handler = new PreviewTouchHandler(anchor);
        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>(count);

        for (int i = start; i < end; i++) {
            ImageView image = new ImageView(this);
            cell = (CellLayout) workspace.getChildAt(i);

            final Bitmap bitmap = Bitmap.createBitmap((int) sWidth, (int) sHeight,
                    Bitmap.Config.ARGB_8888);

            final Canvas c = new Canvas(bitmap);
            c.scale(scale, scale);
            c.translate(-cell.getLeftPadding(), -cell.getTopPadding());
            cell.dispatchDraw(c);
          
            image.setBackgroundDrawable(resources.getDrawable(R.drawable.preview_backgroundnoprogressive));
            image.setImageBitmap(bitmap);
            image.setTag(i);
            image.setOnClickListener(handler);
            image.setOnFocusChangeListener(handler);
            image.setFocusable(true);
            if (i == mWorkspace.getCurrentScreen()) image.requestFocus();

            preview.addView(image,
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            bitmaps.add(bitmap);            
        }

        final PopupWindow p = new PopupWindow(this);
        p.setContentView(preview);
        p.setWidth((int) (sWidth * count + extraW));
        p.setHeight((int) (sHeight + extraH));
        p.setAnimationStyle(R.style.AnimationPreview);
        p.setOutsideTouchable(true);
        p.setFocusable(true);
        p.setBackgroundDrawable(new ColorDrawable(0));
        p.showAsDropDown(anchor, 0, 0);

        p.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                dismissPreview(anchor);
            }
        });

        anchor.setTag(p);
        anchor.setTag(R.id.workspace, preview);
        anchor.setTag(R.id.icon, bitmaps);        
    }

    class PreviewTouchHandler implements View.OnClickListener, Runnable, View.OnFocusChangeListener {
        private final View mAnchor;

        public PreviewTouchHandler(View anchor) {
            mAnchor = anchor;
        }

        public void onClick(View v) {
            mWorkspace.snapToScreen((Integer) v.getTag());
            v.post(this);
        }

        public void run() {
            dismissPreview(mAnchor);            
        }

        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mWorkspace.snapToScreen((Integer) v.getTag());
            }
        }
    }

    Workspace getWorkspace() {
        return mWorkspace;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_CREATE_SHORTCUT:
                return new CreateShortcut().createDialog();
            case DIALOG_RENAME_FOLDER:
                return new RenameFolder().createDialog();
        }

        return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DIALOG_CREATE_SHORTCUT:
                break;
            case DIALOG_RENAME_FOLDER:
                if (mFolderInfo != null) {
                    EditText input = (EditText) dialog.findViewById(R.id.folder_name);
                    final CharSequence text = mFolderInfo.title;
                    input.setText(text);
                    input.setSelection(0, text.length());
                    input.addTextChangedListener(new AddDialogTextWatcher(dialog));
                    ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(input.length() == 0 ? false:true);
                }
                break;
        }
    }
    
    private class AddDialogTextWatcher implements TextWatcher {
    	private Dialog dlg;
    	
    	AddDialogTextWatcher(Dialog dialog) {
    		dlg = dialog;
    	}
        public void afterTextChanged(Editable s) {
                ((AlertDialog)dlg).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(s.length() == 0 ? false:true);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }   

    void showRenameDialog(FolderInfo info) {
        mFolderInfo = info;
        mWaitingForResult = true;
        showDialog(DIALOG_RENAME_FOLDER);
    }

    private void showAddDialog(CellLayout.CellInfo cellInfo) {
        mAddItemCellInfo = cellInfo;
        mWaitingForResult = true;
        showDialog(DIALOG_CREATE_SHORTCUT);
    }

    private void pickShortcut() {
        Bundle bundle = new Bundle();

        ArrayList<String> shortcutNames = new ArrayList<String>();
        shortcutNames.add(getString(R.string.group_applications));
        bundle.putStringArrayList(Intent.EXTRA_SHORTCUT_NAME, shortcutNames);

        ArrayList<ShortcutIconResource> shortcutIcons = new ArrayList<ShortcutIconResource>();
        shortcutIcons.add(ShortcutIconResource.fromContext(Launcher.this,
                        R.drawable.ic_launcher_application));
        bundle.putParcelableArrayList(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIcons);

        Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
        pickIntent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
        pickIntent.putExtra(Intent.EXTRA_TITLE, getText(R.string.title_select_shortcut));
        pickIntent.putExtras(bundle);

        startActivityForResult(pickIntent, REQUEST_PICK_SHORTCUT);
    }

    private class RenameFolder {
        private EditText mInput;

        Dialog createDialog() {
            final View layout = View.inflate(Launcher.this, R.layout.rename_folder, null);
            mInput = (EditText) layout.findViewById(R.id.folder_name);

            AlertDialog.Builder builder = new AlertDialog.Builder(Launcher.this);
            builder.setIcon(0);
            builder.setTitle(getString(R.string.rename_folder_title));
            builder.setCancelable(true);
            builder.setOnCancelListener(new Dialog.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    cleanup();
                }
            });
            builder.setNegativeButton(getString(R.string.cancel_action),
                new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cleanup();
                    }
                }
            );
            builder.setPositiveButton(getString(R.string.rename_action),
                new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        changeFolderName();
                    }
                }
            );
            builder.setView(layout);

            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                public void onShow(DialogInterface dialog) {
                    mWaitingForResult = true;
                    mInput.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(mInput, 0);
                }
            });

            return dialog;
        }

        private void changeFolderName() {
            final String name = mInput.getText().toString();
            if (!TextUtils.isEmpty(name)) {
                // Make sure we have the right folder info
                mFolderInfo = sFolders.get(mFolderInfo.id);
                mFolderInfo.title = name;
                LauncherModel.updateItemInDatabase(Launcher.this, mFolderInfo);

                if (mWorkspaceLoading) {
                    lockAllApps();
                    mModel.startLoader(Launcher.this, false);
                } else {
                    final FolderIcon folderIcon = (FolderIcon)
                            mWorkspace.getViewForTag(mFolderInfo);
                    if (folderIcon != null) {
                        folderIcon.setText(name);
                        getWorkspace().requestLayout();
                    } else {
                        lockAllApps();
                        mWorkspaceLoading = true;
                        mModel.startLoader(Launcher.this, false);
                    }
                }
            }
            cleanup();
        }

        private void cleanup() {
            dismissDialog(DIALOG_RENAME_FOLDER);
            mWaitingForResult = false;
            mFolderInfo = null;
        }
    }

    // Now a part of LauncherModel.Callbacks. Used to reorder loading steps.
    public boolean isAllAppsVisible() {
        return (mAllAppsGrid != null) ? mAllAppsGrid.isVisible() : false;
    }

    // AllAppsView.Watcher
    public void zoomed(float zoom) {
        if (zoom == 1.0f) {
            //mWorkspace.setVisibility(View.GONE); // huyanwei move ahead.
        }
    }
    
    private void showDragLayerElement(boolean isShow) {
    	int visibilityParam = isShow ? View.VISIBLE : View.GONE;
    	/*
    	mHotseatLeft.setVisibility(visibilityParam);
    	mHotseatRight.setVisibility(visibilityParam); 
    	mHotseatLeft2.setVisibility(visibilityParam);
    	mHotseatRight2.setVisibility(visibilityParam);
    	*/
    	
    	/*
    	mHotseat_phone.setVisibility(visibilityParam);
    	mHotseat_contact.setVisibility(visibilityParam);
    	mHotseat_message.setVisibility(visibilityParam);
    	mHotseat_apps.setVisibility(visibilityParam);
    	*/
    	
    	if(isShow)
    	{
    		m_home_screen_indicator.setVisibility(View.VISIBLE);
    		m_app_screen_indicator.setVisibility(View.GONE); 
    	}
    	else
    	{
    		m_home_screen_indicator.setVisibility(View.GONE);
    		m_app_screen_indicator.setVisibility(View.VISIBLE); 
    	}
    }

    void showAllApps(boolean animated) {
    	mShowAllApps = true;
	setBackground(false);
        View hostView = mWorkspace.getChildAt(mWorkspace.getCurrentScreen());
    	View mtkWidgetView = mWorkspace.searchIMTKWidget(hostView);
    	if ( mtkWidgetView != null) {
    		((IMTKWidget)mtkWidgetView).startCovered(mWorkspace.getCurrentScreen());
    		if (DEBUG_SURFACEWIDGET) Log.e(TAG_SURFACEWIDGET, "startCovered");
    	}
        //mWorkspace.hideWallpaper(true);
    	mWorkspace.setVisibility(View.GONE); // huyanwei move here.
        mAllAppsGrid.zoom(1.0f, animated);
        //((View) mAllAppsGrid).setFocusable(true);
        //((View) mAllAppsGrid).requestFocus();
        
        // TODO: fade these two too
        mDeleteZone.setVisibility(View.GONE);
        showDragLayerElement(false);

	mHotseat_apps.setImageDrawable(getResources().getDrawable(R.drawable.hotseat_home));        
        mHotseat_apps.setText(getResources().getString(R.string.hotseat_home));
        
        shortcut.setBackgroundResource(R.drawable.homescreen_menu_shortcut_black_bg);
        //shortcut.getBackground().setAlpha(0x40);        
        shortcut.getBackground().setAlpha(0x40);
    }

    /**
     * Things to test when changing this code.
     *   - Home from workspace
     *          - from center screen
     *          - from other screens
     *   - Home from all apps
     *          - from center screen
     *          - from other screens
     *   - Back from all apps
     *          - from center screen
     *          - from other screens
     *   - Launch app from workspace and quit
     *          - with back
     *          - with home
     *   - Launch app from all apps and quit
     *          - with back
     *          - with home
     *   - Go to a screen that's not the default, then all
     *     apps, and launch and app, and go back
     *          - with back
     *          -with home
     *   - On workspace, long press power and go back
     *          - with back
     *          - with home
     *   - On all apps, long press power and go back
     *          - with back
     *          - with home
     *   - On workspace, power off
     *   - On all apps, power off
     *   - Launch an app and turn off the screen while in that app
     *          - Go back with home key
     *          - Go back with back key  TODO: make this not go to workspace
     *          - From all apps
     *          - From workspace
     *   - Enter and exit car mode (becuase it causes an extra configuration changed)
     *          - From all apps
     *          - From the center workspace
     *          - From another workspace
     */
    void closeAllApps(boolean animated) {
    	mShowAllApps = false;
        if (mAllAppsGrid.isVisible()) {
            View hostView = mWorkspace.getChildAt(mWorkspace.getCurrentScreen());
        	View mtkWidgetView = mWorkspace.searchIMTKWidget(hostView);
        	if ( mtkWidgetView != null ) {
        		((IMTKWidget)mtkWidgetView).stopCovered(mWorkspace.getCurrentScreen());
        		if (DEBUG_SURFACEWIDGET) Log.e(TAG_SURFACEWIDGET, "stopCovered");
        	}

        	showDragLayerElement(true);
        	
            mWorkspace.hideWallpaper(false); 
	    setBackground(true);           
            mAllAppsGrid.zoom(0.0f, animated);
            ((View)mAllAppsGrid).setFocusable(false);
            mWorkspace.getChildAt(mWorkspace.getCurrentScreen()).requestFocus();
            
            if ( !mIsPortrait ) {
                mIndicator.setVisibility(View.INVISIBLE);
            }
            
            mWorkspace.setVisibility(View.VISIBLE);
            
            mHotseat_apps.setImageDrawable(getResources().getDrawable(R.drawable.hotseat_apps));
            mHotseat_apps.setText(getResources().getString(R.string.hotseat_apps));                      
            
            //shortcut.setBackgroundResource(R.drawable.homescreen_menu_shortcut_bg);
            //shortcut.getBackground().setAlpha(0xC0);
            shortcut.setBackgroundResource(R.drawable.homescreen_menu_shortcut_black_bg);
            //shortcut.getBackground().setAlpha(0x40);
            shortcut.getBackground().setAlpha(0x40);            
        }
    }

    void lockAllApps() {
        // TODO
    }

    void unlockAllApps() {
        // TODO
    }

    /**
     * Displays the shortcut creation dialog and launches, if necessary, the
     * appropriate activity.
     */
    private class CreateShortcut implements DialogInterface.OnClickListener,
            DialogInterface.OnCancelListener, DialogInterface.OnDismissListener,
            DialogInterface.OnShowListener {

        private AddAdapter mAdapter;

        Dialog createDialog() {
            mAdapter = new AddAdapter(Launcher.this);

            final AlertDialog.Builder builder = new AlertDialog.Builder(Launcher.this);
            builder.setTitle(getString(R.string.menu_item_add_item));
            builder.setAdapter(mAdapter, this);

            builder.setInverseBackgroundForced(true);

            AlertDialog dialog = builder.create();
            dialog.setOnCancelListener(this);
            dialog.setOnDismissListener(this);
            dialog.setOnShowListener(this);

            return dialog;
        }

        public void onCancel(DialogInterface dialog) {
            mWaitingForResult = false;
            cleanup();
        }

        public void onDismiss(DialogInterface dialog) {
        }

        private void cleanup() {
            try {
                dismissDialog(DIALOG_CREATE_SHORTCUT);
            } catch (Exception e) {
                // An exception is thrown if the dialog is not visible, which is fine
            }
        }

        /**
         * Handle the action clicked in the "Add to home" dialog.
         */
        public void onClick(DialogInterface dialog, int which) {
            Resources res = getResources();
            cleanup();

            switch (which) {
                case AddAdapter.ITEM_SHORTCUT: {
                    // Insert extra item to handle picking application
                    pickShortcut();
                    break;
                }

                case AddAdapter.ITEM_APPWIDGET: {
                    int appWidgetId = Launcher.this.mAppWidgetHost.allocateAppWidgetId();

                    Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
                    pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    // start the pick activity
                    startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
                    break;
                }

                case AddAdapter.ITEM_LIVE_FOLDER: {
                    // Insert extra item to handle inserting folder
                    Bundle bundle = new Bundle();

                    ArrayList<String> shortcutNames = new ArrayList<String>();
                    shortcutNames.add(res.getString(R.string.group_folder));
                    bundle.putStringArrayList(Intent.EXTRA_SHORTCUT_NAME, shortcutNames);

                    ArrayList<ShortcutIconResource> shortcutIcons =
                            new ArrayList<ShortcutIconResource>();
                    shortcutIcons.add(ShortcutIconResource.fromContext(Launcher.this,
                            R.drawable.ic_launcher_folder));
                    bundle.putParcelableArrayList(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIcons);

                    Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
                    pickIntent.putExtra(Intent.EXTRA_INTENT,
                            new Intent(LiveFolders.ACTION_CREATE_LIVE_FOLDER));
                    pickIntent.putExtra(Intent.EXTRA_TITLE,
                            getText(R.string.title_select_live_folder));
                    pickIntent.putExtras(bundle);

                    startActivityForResult(pickIntent, REQUEST_PICK_LIVE_FOLDER);
                    break;
                }

                case AddAdapter.ITEM_WALLPAPER: {
                    startWallpaper();
                    break;
                }
                case AddAdapter.ITEM_VIDEO_WALLPAPER: {
                	startVideoWallpaper();
                	break;
                }
            }
        }

        public void onShow(DialogInterface dialog) {
            mWaitingForResult = true;            
        }
    }

    /**
     * Receives notifications when applications are added/removed.
     */
    private class CloseSystemDialogsIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeSystemDialogs();
            String reason = intent.getStringExtra("reason");
            if (!"homekey".equals(reason)) {
                boolean animate = true;
                if (mPaused || "lock".equals(reason)) {
                    animate = false;
                }
                closeAllApps(animate);
            }
        }
    }

    /**
     * Receives notifications whenever the appwidgets are reset.
     */
    private class AppWidgetResetObserver extends ContentObserver {
        public AppWidgetResetObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            onAppWidgetReset();
        }
    }

    /**
     * If the activity is currently paused, signal that we need to re-run the loader
     * in onResume.
     *
     * This needs to be called from incoming places where resources might have been loaded
     * while we are paused.  That is becaues the Configuration might be wrong
     * when we're not running, and if it comes back to what it was when we
     * were paused, we are not restarted.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     *
     * @return true if we are currently paused.  The caller might be able to
     * skip some work in that case since we will come back again.
     */
    public boolean setLoadOnResume() {
        if (mPaused) {
            Log.i(TAG, "setLoadOnResume");
            mOnResumeNeedsLoad = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public int getCurrentWorkspaceScreen() {
        if (mWorkspace != null) {
            return mWorkspace.getCurrentScreen();
        } else {
            return SCREEN_COUNT / 2;
        }
    }

    /**
     * Refreshes the shortcuts shown on the workspace.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void startBinding() {
        final Workspace workspace = mWorkspace;
        int count = workspace.getChildCount();
        for (int i = 0; i < count; i++) {
            // Use removeAllViewsInLayout() to avoid an extra requestLayout() and invalidate().
            ((ViewGroup) workspace.getChildAt(i)).removeAllViewsInLayout();
        }

        if (DEBUG_USER_INTERFACE) {
            android.widget.Button finishButton = new android.widget.Button(this);
            finishButton.setText("Finish");
            workspace.addInScreen(finishButton, 1, 0, 0, 1, 1);

            finishButton.setOnClickListener(new android.widget.Button.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    /**
     * Bind the items start-end from the list.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
	public void bindItems(ArrayList<ItemInfo> shortcuts, int start, int end) {

		setLoadOnResume();

		final Workspace workspace = mWorkspace;

		synchronized (LauncherModel.mObject) {
			if (shortcuts.size() < end) {
				Log.w(TAG, "Launcher.bindItems exit without bind. because siez is " + shortcuts.size() + ", and end is " + end);
				return;
			}

			for (int i = start; i < end; i++) {
				final ItemInfo item = shortcuts.get(i);
				mDesktopItems.add(item);
				switch (item.itemType) {
					case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
					case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
						final View shortcut = createShortcut((ShortcutInfo) item);
						workspace.addInScreen(shortcut, item.screen, item.cellX, item.cellY, 1, 1, false);
						break;
					case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
						final FolderIcon newFolder = FolderIcon.fromXml(R.layout.folder_icon, this, 
								(ViewGroup) workspace.getChildAt(workspace.getCurrentScreen()),	(UserFolderInfo) item);
						workspace.addInScreen(newFolder, item.screen, item.cellX, item.cellY, 1, 1, false);
						break;
					case LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER:
						final FolderIcon newLiveFolder = LiveFolderIcon.fromXml(R.layout.live_folder_icon, this,
								(ViewGroup) workspace.getChildAt(workspace.getCurrentScreen()),(LiveFolderInfo) item);
						workspace.addInScreen(newLiveFolder, item.screen, item.cellX, item.cellY, 1, 1, false);
						break;
				}
			}
		}

		workspace.requestLayout();
	}

    /**
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindFolders(HashMap<Long, FolderInfo> folders) {
        setLoadOnResume();
        sFolders.clear();
        sFolders.putAll(folders);
    }

    /**
     * Add the views for a widget to the workspace.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAppWidget(LauncherAppWidgetInfo item) {
        setLoadOnResume();

        final long start = DEBUG_WIDGETS ? SystemClock.uptimeMillis() : 0;
        if (DEBUG_WIDGETS) {
            Log.d(TAG, "bindAppWidget: " + item);
        }
        final Workspace workspace = mWorkspace;

        final int appWidgetId = item.appWidgetId;
        final AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (DEBUG_WIDGETS) {
            Log.d(TAG, "bindAppWidget: id=" + item.appWidgetId + " belongs to component " + appWidgetInfo.provider);
        }

        item.hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);

        item.hostView.setAppWidget(appWidgetId, appWidgetInfo);
        item.hostView.setTag(item);

        workspace.addInScreen(item.hostView, item.screen, item.cellX,
                item.cellY, item.spanX, item.spanY, false);

        workspace.requestLayout();

        mDesktopItems.add(item);

        if (DEBUG_WIDGETS) {
            Log.d(TAG, "bound widget id="+item.appWidgetId+" in "
                    + (SystemClock.uptimeMillis()-start) + "ms");
        }
    }

    /**
     * Callback saying that there aren't any more items to bind.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void finishBindingItems() {
        setLoadOnResume();

        if (mSavedState != null) {
            if (!mWorkspace.hasFocus()) {
                mWorkspace.getChildAt(mWorkspace.getCurrentScreen()).requestFocus();
            }

            final long[] userFolders = mSavedState.getLongArray(RUNTIME_STATE_USER_FOLDERS);
            if (userFolders != null) {
                for (long folderId : userFolders) {
                    final FolderInfo info = sFolders.get(folderId);
                    if (info != null) {
                        openFolder(info);
                    }
                }
                final Folder openFolder = mWorkspace.getOpenFolder();
                if (openFolder != null) {
                    openFolder.requestFocus();
                }
            }

            mSavedState = null;
        }

        if (mSavedInstanceState != null) {
            super.onRestoreInstanceState(mSavedInstanceState);
            mSavedInstanceState = null;
        }

        mWorkspaceLoading = false;
    }

    /**
     * Add the icons for all apps.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAllApplications(ArrayList<ApplicationInfo> apps) {
        mAllAppsGrid.setApps(apps);
    }

    /**
     * A package was installed.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAppsAdded(ArrayList<ApplicationInfo> apps) {
    	
    	mAllAppsGrid.updateView(); // huyanwei add it;
    	
        setLoadOnResume();
        removeDialog(DIALOG_CREATE_SHORTCUT);
        mAllAppsGrid.addApps(apps);       
        
    }

    /**
     * A package was updated.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAppsUpdated(ArrayList<ApplicationInfo> apps) {
    	
    	mAllAppsGrid.updateView(); // huyanwei add it;
    	
    	setLoadOnResume();
        removeDialog(DIALOG_CREATE_SHORTCUT);
        mWorkspace.updateShortcuts(apps);
        mAllAppsGrid.updateApps(apps);
        
    }

    /**
     * A package was uninstalled.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAppsRemoved(ArrayList<ApplicationInfo> apps, boolean permanent) {
    	mAllAppsGrid.updateView(); // huyanwei add it;
        removeDialog(DIALOG_CREATE_SHORTCUT);
        if (permanent) {
            mWorkspace.removeItems(apps);
        }
        mAllAppsGrid.removeApps(apps);                
    }

	private void initialIconBackgroundBitmap() {
    	Bitmap iconBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.theme_applist_bkg);
    	mIconBackgroundBitmap = iconBgBitmap;
    }

	static public Bitmap getIconBackground() {
    	return Launcher.mIconBackgroundBitmap;
    }

    /**
     * Prints out out state for debugging.
     */
    public void dumpState() {
        Log.d(TAG, "BEGIN launcher3 dump state for launcher " + this);
        Log.d(TAG, "mSavedState=" + mSavedState);
        Log.d(TAG, "mWorkspaceLoading=" + mWorkspaceLoading);
        Log.d(TAG, "mRestoring=" + mRestoring);
        Log.d(TAG, "mWaitingForResult=" + mWaitingForResult);
        Log.d(TAG, "mSavedInstanceState=" + mSavedInstanceState);
        Log.d(TAG, "mDesktopItems.size=" + mDesktopItems.size());
        Log.d(TAG, "sFolders.size=" + sFolders.size());
        mModel.dumpState();
        mAllAppsGrid.dumpState();
        Log.d(TAG, "END launcher3 dump state");
    }

	public Indicator getIndicator() {
        return mIndicator;
    }
    
	/**
	 * Receives intents from other applications to change the wallpaper.
	 */
	private static class WallpaperIntentReceiver extends BroadcastReceiver {
		private final Application mApplication;
		private WeakReference<Launcher> mLauncher;

		WallpaperIntentReceiver(Application application, Launcher launcher) {
			mApplication = application;
			setLauncher(launcher);
		}

		void setLauncher(Launcher launcher) {
			mLauncher = new WeakReference<Launcher>(launcher);
		}

		@Override
		public void onReceive(Context context, Intent intent) {

			// Notify we have a new wallpaper
			final Launcher launcher = mLauncher.get();
			if (launcher != null) {
				launcher.loadWallpaper();
			}
		}
	}
	
	/*
     *  Call this function in OnCreate to check whether we should enable applaunchtime log by
     *  checking system property "persist.applaunchtime.enable"
     *  1 : Enable
     *  0 : Disable
     */
    private static boolean checkAppLaunchTimeProperty() {        
        return (1 == SystemProperties.getInt("persist.applaunchtime.enable", 0)) ? true : false;
    }
}
