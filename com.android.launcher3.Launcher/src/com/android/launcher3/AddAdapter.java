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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.service.wallpaper.WallpaperService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import com.mediatek.featureoption.FeatureOption;

import com.android.launcher3.AddAdapter.ListItem;
import com.android.launcher3.R;

/**
 * Adapter showing the types of items that can be added to a {@link Workspace}.
 */
public class AddAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    
    private final ArrayList<ListItem> mItems = new ArrayList<ListItem>();
    
    public static final int ITEM_SHORTCUT = 0;
    public static final int ITEM_APPWIDGET = 1;
    public static final int ITEM_LIVE_FOLDER = 2;
    public static final int ITEM_WALLPAPER = 3;
    public static final int ITEM_VIDEO_WALLPAPER = 4;
    private static final String VIDEO_WALLPAPER_PACKAGE = "com.mediatek.vlw";
    private PackageManager mPackageManager;
    /**
     * Specific item in our list.
     */
    public class ListItem {
        public final CharSequence text;
        public final Drawable image;
        public final int actionTag;
        
        public ListItem(Resources res, int textResourceId, int imageResourceId, int actionTag) {
            text = res.getString(textResourceId);
            if (imageResourceId != -1) {
                image = res.getDrawable(imageResourceId);
            } else {
                image = null;
            }
            this.actionTag = actionTag;
        }
    }
    
    public AddAdapter(Launcher launcher) {
        super();

        mInflater = (LayoutInflater) launcher.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPackageManager = launcher.getPackageManager();
        // Create default actions
        Resources res = launcher.getResources();
        
        mItems.add(new ListItem(res, R.string.group_shortcuts,
                R.drawable.ic_launcher_shortcut, ITEM_SHORTCUT));

        mItems.add(new ListItem(res, R.string.group_widgets,
                R.drawable.ic_launcher_appwidget, ITEM_APPWIDGET));
        
        mItems.add(new ListItem(res, R.string.group_live_folders,
                R.drawable.ic_launcher_folder, ITEM_LIVE_FOLDER));
        
        mItems.add(new ListItem(res, R.string.group_wallpapers,
                R.drawable.ic_launcher_wallpaper, ITEM_WALLPAPER));
        if (FeatureOption.MTK_VLW_APP) {
            if (findVideoWallpaper()) {
        	    mItems.add(new ListItem(res, R.string.group_video_wallpaper, 
            		R.drawable.ic_launcher_video_wallpaper, ITEM_VIDEO_WALLPAPER));
            }
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ListItem item = (ListItem) getItem(position);
        
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.add_list_item, parent, false);
        }
        
        TextView textView = (TextView) convertView;
        textView.setTag(item);
        textView.setText(item.text);
        textView.setCompoundDrawablesWithIntrinsicBounds(item.image, null, null, null);
        
        return convertView;
    }

    public int getCount() {
        return mItems.size();
    }

    public Object getItem(int position) {
        return mItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    private boolean findVideoWallpaper() {
    	List<ResolveInfo> list = mPackageManager.queryIntentServices(
                new Intent(WallpaperService.SERVICE_INTERFACE),
                PackageManager.GET_META_DATA);
    	int listSize = list.size();
    	boolean res = false;
    	for (int i = 0; i < listSize; i++) {
            ResolveInfo resolveInfo = list.get(i);
            String packageName = resolveInfo.serviceInfo.packageName;
            if (VIDEO_WALLPAPER_PACKAGE.equals(packageName)) {
            	res = true;
            	break;
            }
    	}
    	return res;
    }
}
