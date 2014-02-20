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

//#define LOG_NDEBUG 0
#define LOG_TAG "srclib.huyanwei.jni.gsensor"

//#define TAG	LOG_TAG,
#define TAG ""

#include <utils/Log.h>
//#include <nativehelper/jni.h>
//#include <nativehelper/JNIHelp.h>
//#include <android_runtime/AndroidRuntime.h>
#include <math.h>
#include <dlfcn.h>

#include <ctype.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <dirent.h>
#include <fcntl.h>
#include <pthread.h>
#include <sys/mount.h>
#include <sys/statfs.h>
#include <dirent.h>
#include <linux/input.h>
#include <math.h>



#if defined(LOGD)
#undef LOGD
#define LOGD printf
#endif


#ifdef __cplusplus 
extern "C" { 
#endif 

#include "libhwm.h"
#include <linux/hwmsensor.h>
#include "libfile_op.h"


//using namespace android;

//#define GSENSOR_NAME "/dev/gsensor"
//#define GSENSOR_ATTR_SELFTEST "/sys/bus/platform/drivers/gsensor/selftest"


static HwmData cali;
static HwmData cali_drv;
static HwmData cali_nvram;
static HwmData dat;
static int fd = -1;


int gsensor_support_selftest = 0 ;

bool gsensor_opendev(void)
{
	int self_fd = -1;
	fd = open(GSENSOR_NAME, O_RDONLY);	 
	if(fd < 0)
	{
			LOGD(TAG"\r\ng sensor device open fail.\n");
			return false;
	}

	self_fd = open(GSENSOR_ATTR_SELFTEST, O_RDWR);
	
	if(self_fd < 0)
		gsensor_support_selftest = 0 ;
	else
		gsensor_support_selftest = 1 ;
		
	 
	LOGD(TAG"\r\ng sensor device open success.\n");
        return true;
}

bool gsensor_closedev(void)
{
	if(fd>0)
	{
		close(fd);
	}
	
	gsensor_support_selftest = 0 ;

	LOGD(TAG"\r\ng sensor device close success.\n");
	
       return true;
}

bool gsensor_calibrator(int delay ,int num,int tolerance)
{
#if 1
	int err = -1;
	if(fd >0)
	{
		/**************
		**  check parameter
		**************/
		if(!delay || !num || !tolerance)
		{
			LOGD(TAG"\r\nparameter error  delay=%d,num=%d,tolerance=%d \r\n",delay,num,tolerance);
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
			LOGD(TAG"\r\nrst calibration failed,result=%d\r\n",err);
			//return false;
		}
		else
		{
			LOGD(TAG"\r\nrst calibration ok ,result=%d\r\n",err);			
		}

		memset(&cali, 0x00, sizeof(cali));
		memset(&cali_nvram, 0x00, sizeof(cali_nvram));
		memset(&cali_drv, 0x00, sizeof(cali_drv));
		memset(&dat, 0x00, sizeof(dat));		
		
		err = gsensor_write_nvram(&cali_nvram);
		if(err)
		{
			LOGD(TAG"\r\nrst nvram calibration failed,result %d\r\n",err);
			//return false;
		}
		else
		{
			LOGD(TAG"\r\nrst nvram  calibration result %d\r\n",err);			
		}


		/***************
		** calibrator
		***************/
		err = gsensor_calibration(fd, delay, num,tolerance, 0, &cali);
		if(err)
		{
			LOGD(TAG"\r\ncalibration failed,result %d\r\n",err);
			//return false;
		}
		else
		{
			LOGD(TAG"\r\ncalibration result %d\r\n",err);			
		}		

		err = gsensor_set_cali(fd,&cali);		// sensor  x offset , y offset , z offset .
		if(err)
		{
			LOGD(TAG"\r\nsensor chip set calibration data failed,result %d\r\n",err);
			//return false;
		}
		else
		{
			LOGD(TAG"\r\nsensor chip set calibration data result %d\r\n",err);			
		}		

		err = gsensor_get_cali(fd, &cali);	// sensor x offset , y offset , z offset .
		if(err)
		{
			LOGD(TAG"\r\nsensor chip get calibration data failed,result %d\r\n",err);
			//return false;
		}
		else
		{
			LOGD(TAG"\r\nsensor chip get calibration data result %d\r\n",err);			
		}

		err = gsensor_write_nvram(&cali);	// nvram x offset , y offset , z offset .
		if(err)
		{
			LOGD(TAG"\r\nwrite nvram calibration data failed,result %d\r\n",err);
			//return false;
		}
		else
		{
			LOGD(TAG"\r\nwrite nvram calibration data  result %d\r\n",err);			
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

#ifdef __cplusplus 
}
#endif 


void usage(const char * name)
{
	printf("\r\n%s usage :\r\n",name);
	printf("%s -d delay -n num -t tolerance\r\n",name);
	printf("ex:\r\n");
	printf("%s -d 50 -n 20 -t 40\r\n",name);
	printf("\r\nAuthor:huyanwei\r\n");
	printf("Email :srclib@hotmail.com\r\n");
	return ;
}

int main(int argc, char **argv)
{
	int delay = 0 ;
	int num = 0 ;
	int tolerance = 0 ;
	int c = '?';

	do {
		c = getopt(argc, argv, "d:n:t:h");

		#if 0
		printf("optarg=%s \n",optarg);
		printf("optopt=%c \n",optopt);
		printf("optind=%d \n",optind);
		printf("c=0x%x \n",c);
		#endif
		
		if (c == -1)
			break;

		switch (c) 
		{
			case 'd':
				delay = strtol(optarg, NULL, 0);
				break;
			case 'n':
				num = strtol(optarg, NULL, 0);
				break;
			case 't':
				tolerance = strtol(optarg, NULL, 0);
				break;
			case 'h':
				usage(argv[0]);
				return -1;
			case '?':
				fprintf(stderr, "%s: invalid option -%c\n",
				argv[0], optopt);
				usage(argv[0]);
				exit(1);
		}
	} while (1);

	if(optind > argc) {
		fprintf(stdout, "%s: too few arguments\n", argv[0]);
		exit(1);
	}
	
	printf("\r\ngsensor calibrat start ...... \r\n");
		
	gsensor_opendev();
	gsensor_calibrator(delay,num,tolerance);
	gsensor_closedev();

	printf("\r\ngsensor calibrate end...... \r\n");

	return 0;
}




