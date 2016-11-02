LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := andjni

LOCAL_SRC_FILES := com_vince_andwidget_jni_AndJNI.c

include $(BUILD_SHARED_LIBRARY)