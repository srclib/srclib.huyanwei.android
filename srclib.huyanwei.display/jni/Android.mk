LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

#LOCAL_ARM_MODE := arm

LOCAL_MODULE:= libdisplay_jni

#LOCAL_C_INCLUDES += $(TOP)/external/zlib/

LOCAL_SRC_FILES:= \
	android_display_jni.cpp

LOCAL_MODULE_TAGS=optional

LOCAL_STATIC_LIBRARIES := libc libcutils
LOCAL_SHARED_LIBRARIES := libcutils libc libstdc++ libz libdl liblog
LOCAL_SHARED_LIBRARIES += libandroid_runtime libnativehelper 
#libmhal

LOCAL_PRELINK_MODULE := false

#LOCAL_ALLOW_UNDEFINED_SYMBOLS := true

#LOCAL_MODULE_PATH := $(TARGET_OUT_BIN)

include $(BUILD_SHARED_LIBRARY)

