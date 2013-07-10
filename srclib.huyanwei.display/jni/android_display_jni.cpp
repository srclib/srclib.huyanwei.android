/*
 * Copyright (C) 2009 Google Inc.
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

#define LOG_NDEBUG 0
#include <stdio.h>
#include <unistd.h>

#define LOG_TAG "srclib.huyanwei.jni.display"

#include <utils/Log.h>
//#include <nativehelper/jni.h>
//#include <nativehelper/JNIHelp.h>
#include <android_runtime/AndroidRuntime.h>
#include <math.h>
#include <dlfcn.h>

#include <ctype.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <fcntl.h>
#include <pthread.h>
#include <sys/mount.h>
#include <sys/statfs.h>
#include <dirent.h>
#include <linux/input.h>
#include <math.h>

#include <linux/fb.h>
#include <sys/ioctl.h>
#include <sys/mman.h>

#include "jni.h"
#include "JNIHelp.h"



#ifdef __cplusplus 
extern "C" { 
#endif 

char const * const fb_dev_node_paths[] = {
        "/dev/graphics/fb%u",
        "/dev/fb%u",
        0
};

static int fb_fd = -1;
static struct fb_var_screeninfo vinfo;
static struct fb_fix_screeninfo finfo;
static int x_virtual = 0 ;
static int y_virtual = 0 ;


JNIEXPORT
jint Native_get_framebuffer_info_init(JNIEnv *env, jobject thiz)
{
        unsigned int i = 0;
        char name[64] = { 0 };
        while ((fb_fd < 0) && fb_dev_node_paths[i])
        {
                snprintf(name, 64, fb_dev_node_paths[i], 0);
                fb_fd = open(name, O_RDWR, 0);
                i++;
        }

        if(fb_fd < 0)
        {
                //LOGE("open dev file fail\n");
                return -1;
        }

	return 0;
}

JNIEXPORT
jint Native_get_framebuffer_info_deinit(JNIEnv *env, jobject thiz)
{
        if(fb_fd < 0)
        {
                //LOGE("open dev file fail\n");
                return -1;
        }

        close(fb_fd);

	fb_fd = -1;

	return 0;
}

JNIEXPORT
jint Native_get_framebuffer_info_width(JNIEnv *env, jobject thiz)
{
        if(fb_fd < 0)
        {
                //LOGE("open dev file fail\n");
                return -1;
        }

        if (ioctl(fb_fd, FBIOGET_VSCREENINFO, &vinfo) < 0) {
                //fprintf(stderr, "ioctl FBIOGET_VSCREENINFO failed\n");
                return -2;
        }

        if (ioctl(fb_fd, FBIOGET_FSCREENINFO, &finfo) < 0) {
                //fprintf(stderr, "ioctl FBIOGET_FSCREENINFO failed\n");
                return -3;
        }

        //x_virtual = finfo.line_length/(vinfo.bits_per_pixel / 8);
        x_virtual = vinfo.xres;

        return x_virtual;
}

JNIEXPORT
jint Native_get_framebuffer_info_height(JNIEnv *env, jobject thiz)
{
        if(fb_fd < 0)
        {
                //LOGE("open dev file fail\n");
                return -1;
        }

        if (ioctl(fb_fd, FBIOGET_VSCREENINFO, &vinfo) < 0) {
                //fprintf(stderr, "ioctl FBIOGET_VSCREENINFO failed\n");
                return -2;
        }

        if (ioctl(fb_fd, FBIOGET_FSCREENINFO, &finfo) < 0) {
                //fprintf(stderr, "ioctl FBIOGET_FSCREENINFO failed\n");
                return -3;
        }

        //x_virtual = finfo.line_length/(vinfo.bits_per_pixel / 8);
        y_virtual = vinfo.yres;

        return y_virtual;
}

JNIEXPORT
jint Native_get_framebuffer_info(JNIEnv *env, jobject thiz,jintArray width,jintArray height)
{
        if(fb_fd < 0)
        {
                //LOGE("open dev file fail\n");
                return -1;
        }

        if (ioctl(fb_fd, FBIOGET_VSCREENINFO, &vinfo) < 0) {
                //fprintf(stderr, "ioctl FBIOGET_VSCREENINFO failed\n");
                return -2;
        }

        if (ioctl(fb_fd, FBIOGET_FSCREENINFO, &finfo) < 0) {
                //fprintf(stderr, "ioctl FBIOGET_FSCREENINFO failed\n");
                return -3;
        }

        //x_virtual = finfo.line_length/(vinfo.bits_per_pixel / 8);
        x_virtual = vinfo.xres;
        y_virtual = vinfo.yres;

	env->SetIntArrayRegion(width,0,1,&x_virtual);
	env->SetIntArrayRegion(height,0,1,&y_virtual);

	//width[0]  = x_virtual;
        //height[0] = y_virtual;

        return 0;
}

// Dalvik VM type signatures
static JNINativeMethod gMethods[] = {
    {   "get_framebuffer_info_init",
        "()I",
        (void*)Native_get_framebuffer_info_init
    },
    {   "get_framebuffer_info_deinit",
        "()I",
        (void*)Native_get_framebuffer_info_deinit
    },
    {   "get_framebuffer_info_width",
        "()I",
        (void*)Native_get_framebuffer_info_width
    },
    {   "get_framebuffer_info_height",
        "()I",
        (void*)Native_get_framebuffer_info_height
    },
    {   "get_framebuffer_info",
        "([I[I)I",
        (void*)Native_get_framebuffer_info
    }
};

static const char* const kClassPathName = "srclib/huyanwei/display/DisplayNative";

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jint result = -1;
    jclass clazz;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("ERROR: GetEnv failed\n");
        goto bail;
    }
    assert(env != NULL);

/*
    clazz = env->FindClass(kClassPathName);
    if (clazz == NULL) {
        LOGE("Can't find %s", kClassPathName);
        goto bail;
    }
*/

    if (jniRegisterNativeMethods(
            env, kClassPathName, gMethods, NELEM(gMethods)) < 0)
        goto bail;

    /* success -- return valid version number */
    result = JNI_VERSION_1_4;

 bail:
    return result;
}

#ifdef __cplusplus 
}
#endif 

