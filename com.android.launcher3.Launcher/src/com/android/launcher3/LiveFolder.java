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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.net.Uri;
import android.provider.LiveFolders;
import android.os.AsyncTask;
import android.database.Cursor;

import java.lang.ref.WeakReference;

import com.android.launcher3.R;

public class LiveFolder extends Folder {
    private AsyncTask<LiveFolderInfo,Void,Cursor> mLoadingTask;

    public LiveFolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    static LiveFolder fromXml(Context context, FolderInfo folderInfo) {
        final int layout = isDisplayModeList(folderInfo) ?
                R.layout.live_folder_list : R.layout.live_folder_grid;
        return (LiveFolder) LayoutInflater.from(context).inflate(layout, null);
    }

    private static boolean isDisplayModeList(FolderInfo folderInfo) {
        return ((LiveFolderInfo) folderInfo).displayMode ==
                LiveFolders.DISPLAY_MODE_LIST;
    }

    @Override
    public void onItemClick(AdapterView parent, View v, int position, long id) {
        LiveFolderAdapter.ViewHolder holder = (LiveFolderAdapter.ViewHolder) v.getTag();

        if (holder.useBaseIntent) {
            final Intent baseIntent = ((LiveFolderInfo) mInfo).baseIntent;
            if (baseIntent != null) {
                final Intent intent = new Intent(baseIntent);
                Uri uri = baseIntent.getData();
                uri = uri.buildUpon().appendPath(Long.toString(holder.id)).build();
                intent.setData(uri);
                mLauncher.startActivitySafely(intent, "(position=" + position + ", id=" + id + ")");
            }
        } else if (holder.intent != null) {
            mLauncher.startActivitySafely(holder.intent,
                    "(position=" + position + ", id=" + id + ")");
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    void bind(FolderInfo info) {
        super.bind(info);
        if (mLoadingTask != null && mLoadingTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadingTask.cancel(true);
        }
        mLoadingTask = new FolderLoadingTask(this).execute((LiveFolderInfo) info);
    }

    @Override
    void onOpen() {
        super.onOpen();
        requestFocus();
    }

    @Override
    void onClose() {
        super.onClose();
        if (mLoadingTask != null && mLoadingTask.getStatus() == AsyncTask.Status.RUNNING) {
            mLoadingTask.cancel(true);
        }

        // The adapter can be null if onClose() is called before FolderLoadingTask
        // is done querying the provider
        final LiveFolderAdapter adapter = (LiveFolderAdapter) mContent.getAdapter();
        if (adapter != null) {
            adapter.cleanup();
        }
    }

    static class FolderLoadingTask extends AsyncTask<LiveFolderInfo, Void, Cursor> {
        private final WeakReference<LiveFolder> mFolder;
        private LiveFolderInfo mInfo;

        FolderLoadingTask(LiveFolder folder) {
            mFolder = new WeakReference<LiveFolder>(folder);
        }

        protected Cursor doInBackground(LiveFolderInfo... params) {
            final LiveFolder folder = mFolder.get();
            if (folder != null) {
                mInfo = params[0];
                return LiveFolderAdapter.query(folder.mLauncher, mInfo);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (!isCancelled()) {
                if (cursor != null) {
                    final LiveFolder folder = mFolder.get();
                    if (folder != null) {
                        final Launcher launcher = folder.mLauncher;
                        folder.setContentAdapter(new LiveFolderAdapter(launcher, mInfo, cursor));
                    }
                }
            } else if (cursor != null) {
                cursor.close();
            }
        }
    }
}
