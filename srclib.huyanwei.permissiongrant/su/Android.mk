LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= su.c send_intent.cpp

LOCAL_MODULE:= su

ifeq ($(strip $(PLATFORM_SDK_VERSION)),10)
LOCAL_CFLAGS += -DANDROID_GB
endif
ifeq ($(strip $(PLATFORM_SDK_VERSION)),15)
LOCAL_CFLAGS += -DANDROID_ICS
endif
ifeq ($(strip $(PLATFORM_SDK_VERSION)),16)
LOCAL_CFLAGS += -DANDROID_JB
endif
ifeq ($(strip $(PLATFORM_SDK_VERSION)),17)
LOCAL_CFLAGS += -DANDROID_JB
endif

#LOCAL_FORCE_STATIC_EXECUTABLE := true

LOCAL_STATIC_LIBRARIES := libc

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../../external/sqlite/dist/ $(LOCAL_PATH)/../../../external/sqlite/android/ \
	frameworks/av/media/libmediaplayerservice

LOCAL_SHARED_LIBRARIES := \
    liblog \
    libsqlite \
    libcutils \
    libbinder \
    libutils \

LOCAL_MODULE_PATH := $(TARGET_OUT_OPTIONAL_EXECUTABLES)
LOCAL_MODULE_TAGS := optional
#LOCAL_PRELINK_MODULE := false

include $(BUILD_EXECUTABLE)
