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

#define LOG_TAG "srclib.huyanwei.jni.gsensor"

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

#include "jni.h"
#include "JNIHelp.h"


#ifdef __cplusplus 
extern "C" { 
#endif 

#include "libhwm.h"
#include <linux/hwmsensor.h>
#include "libfile_op.h"

#if 0
extern int gsensor_calibration(int fd, int period, int count, int tolerance, int trace, HwmData *cali);
extern int gsensor_write_nvram(HwmData *dat);
extern int gsensor_read_nvram(HwmData *dat);
extern int gsensor_rst_cali(int fd);
extern int gsensor_set_cali(int fd, HwmData *dat);
extern int gsensor_get_cali(int fd, HwmData *dat);
extern int gsensor_read(int fd, HwmData *dat);
extern int gsensor_close(int fd);
extern int gsensor_open(int *fd);
#endif



//using namespace android;

//#define GSENSOR_NAME "/dev/gsensor"
//#define GSENSOR_ATTR_SELFTEST "/sys/bus/platform/drivers/gsensor/selftest"


static HwmData cali;
static HwmData cali_drv;
static HwmData cali_nvram;
static HwmData dat;
static jint fd = -1;


int gsensor_support_selftest = 0 ;

JNIEXPORT
jboolean android_gsensor_opendev(JNIEnv *env, jobject thiz)
{
	 int self_fd = -1;
	 fd = open(GSENSOR_NAME, O_RDONLY);	 
	if(fd < 0)
	{
			LOGD("g sensor device open fail.\n");
			return false;
	}

	self_fd = open(GSENSOR_ATTR_SELFTEST, O_RDWR);
	
	if(self_fd < 0)
		gsensor_support_selftest = 0 ;
	else
		gsensor_support_selftest = 1 ;
		
	 
	LOGD("g sensor device open success.\n");
        return true;
}

JNIEXPORT
jboolean android_gsensor_closedev(JNIEnv *env, jobject thiz)
{
	if(fd>0)
	{
		close(fd);
	}
	
	gsensor_support_selftest = 0 ;

	LOGD("g sensor device close success.\n");
	
       return true;
}

JNIEXPORT
jboolean android_gsensor_calibrator(JNIEnv *env, jobject thiz,jint delay ,jint num,jint tolerance)
{
#if 1
	jint err = -1;
	if(fd >0)
	{
		/**************
		**  check parameter
		**************/
		if(!delay || !num || !tolerance)
		{
			LOGD("parameter error  delay=%d,num=%d,tolerance=%d \n",delay,num,tolerance);
			//return false;
			delay = 50;
			num = 20 ;
			tolerance = 40;
		}		

		/***************
		** Clear data 
		****************/
		err = gsensor_rst_cali(fd);		
		if(err)
		{
			LOGD("rst calibration failed,result=%d",err);
			//return false;
		}
		else
		{
			LOGD("rst calibration ok ,result=%d",err);			
		}

		memset(&cali, 0x00, sizeof(cali));
		memset(&cali_nvram, 0x00, sizeof(cali_nvram));
		memset(&cali_drv, 0x00, sizeof(cali_drv));
		memset(&dat, 0x00, sizeof(dat));		
		
		err = gsensor_write_nvram(&cali_nvram);
		if(err)
		{
			LOGD("rst nvram calibration failed,result %d",err);
			//return false;
		}
		else
		{
			LOGD("rst nvram  calibration result %d",err);			
		}


		/***************
		** calibrator
		***************/
		err = gsensor_calibration(fd, delay, num,tolerance, 0, &cali);
		if(err)
		{
			LOGD("calibration failed,result %d",err);
			//return false;
		}
		else
		{
			LOGD("calibration result %d",err);			
		}		

		err = gsensor_set_cali(fd,&cali);		// sensor  x offset , y offset , z offset .
		if(err)
		{
			LOGD("sensor chip set calibration data failed,result %d",err);
			//return false;
		}
		else
		{
			LOGD("sensor chip set calibration data result %d",err);			
		}		

		err = gsensor_get_cali(fd, &cali);	// sensor x offset , y offset , z offset .
		if(err)
		{
			LOGD("sensor chip get calibration data failed,result %d",err);
			//return false;
		}
		else
		{
			LOGD("sensor chip get calibration data result %d",err);			
		}

		err = gsensor_write_nvram(&cali);	// nvram x offset , y offset , z offset .
		if(err)
		{
			LOGD("write nvram calibration data failed,result %d",err);
			//return false;
		}
		else
		{
			LOGD("write nvram calibration data  result %d",err);			
		}
		

		/***************
		** check
		***************/
	/*
		err = gsensor_read_nvram(&cali_nvram); // nvram x offset , y offset , z offset .

		err = gsensor_read(fd, &dat); // g sensor  x,y z value 
	*/				
		
	}

#endif


#if 0
	//
       bool backup_res = false ;
       backup_res = FileOp_BackupToBinRegion_All();
       if(backup_res)
       {   
               FileOp_SetCleanBootFlag(1);
       }   
#endif

        return true;
}


// Dalvik VM type signatures
static JNINativeMethod gMethods[] = {
    {   "opendev",
        "()Z",
        (void*)android_gsensor_opendev
    },
    {   "closedev",
        "()Z",
        (void*)android_gsensor_closedev
    },

    {   "calibrator",
        "(III)Z",
        (void*)android_gsensor_calibrator
    },    
};

static const char* const kClassPathName = "srclib/huyanwei/bubble/GSensorNative";

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

