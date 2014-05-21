LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# include res directory from timepicker
LOCAL_CERTIFICATE := platform

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := OneKeyInstall

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))
