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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.Toast;

import com.android.launcher3.R;

public class InstallShortcutReceiver extends BroadcastReceiver {
    private static final String ACTION_INSTALL_SHORTCUT =
            "com.android.launcher.action.INSTALL_SHORTCUT";

    private final int[] mCoordinates = new int[2];

    public void onReceive(Context context, Intent data) {
        if (!ACTION_INSTALL_SHORTCUT.equals(data.getAction())) {
            return;
        }

        int screen = Launcher.getScreen();

        if (!installShortcut(context, data, screen)) {
            // The target screen is full, let's try the other screens
            for (int i = 0; i < Launcher.SCREEN_COUNT; i++) {
                if (i != screen && installShortcut(context, data, i)) break;
            }
        }
    }

    private boolean installShortcut(Context context, Intent data, int screen) {
        String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);

        if (findEmptyCell(context, mCoordinates, screen)) {
            CellLayout.CellInfo cell = new CellLayout.CellInfo();
            cell.cellX = mCoordinates[0];
            cell.cellY = mCoordinates[1];
            cell.screen = screen;

            Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);

            if (intent.getAction() == null) {
                intent.setAction(Intent.ACTION_VIEW);
            }

            // By default, we allow for duplicate entries (located in
            // different places)
            boolean duplicate = data.getBooleanExtra(Launcher.EXTRA_SHORTCUT_DUPLICATE, true);
            if (duplicate || !LauncherModel.shortcutExists(context, name, intent)) {
                ((LauncherApplication)context.getApplicationContext()).getModel()
                        .addShortcut(context, data, cell, true);
                Toast.makeText(context, context.getString(R.string.shortcut_installed, name),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getString(R.string.shortcut_duplicate, name),
                        Toast.LENGTH_SHORT).show();
            }

            return true;
        } else {
            Toast.makeText(context, context.getString(R.string.out_of_space),
                    Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private static boolean findEmptyCell(Context context, int[] xy, int screen) {
        final int xCount = Launcher.NUMBER_CELLS_X;
        final int yCount = Launcher.NUMBER_CELLS_Y;

        boolean[][] occupied = new boolean[xCount][yCount];

        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI,
            new String[] { LauncherSettings.Favorites.CELLX, LauncherSettings.Favorites.CELLY,
                    LauncherSettings.Favorites.SPANX, LauncherSettings.Favorites.SPANY },
            LauncherSettings.Favorites.SCREEN + "=?",
            new String[] { String.valueOf(screen) }, null);

        final int cellXIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLX);
        final int cellYIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLY);
        final int spanXIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SPANX);
        final int spanYIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SPANY);

        try {
            while (c.moveToNext()) {
                int cellX = c.getInt(cellXIndex);
                int cellY = c.getInt(cellYIndex);
                int spanX = c.getInt(spanXIndex);
                int spanY = c.getInt(spanYIndex);

                for (int x = cellX; x < cellX + spanX && x < xCount; x++) {
                    for (int y = cellY; y < cellY + spanY && y < yCount; y++) {
                        occupied[x][y] = true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        } finally {
            c.close();
        }

        return CellLayout.findVacantCell(xy, 1, 1, xCount, yCount, occupied);
    }
}
