#
# Copyright (C) 2012 Naranjo Manuel Francisco
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
	java/src/android/bluetooth/le/BluetoothAdapterLE.java \
	java/src/android/server/le/BluetoothLEService.java \
	java/src/android/bluetooth/le/IBluetoothLE.aidl

aidl_files := java/scr/android/bluetooth/le/IBluetoothLE.aidl
#$(gen): PRIVATE_SRC_FILES := $(aidl_files)
#$(gen): $(aidl_files) | $(AIDL)
#		@echo Aidl Preprocess: $@
#		$(hide) $(AIDL) --preprocess $@ $(PRIVATE_SRC_FILES)

# FRAMEWORKS_BASE_JAVA_SRC_DIRS comes from build/core/pathmap.mk
#LOCAL_AIDL_INCLUDES := $(FRAMEWORKS_BASE_JAVA_SRC_DIRS)

LOCAL_NO_STANDARD_LIBRARIES := true
LOCAL_JAVA_LIBRARIES := bouncycastle core core-junit ext framework

LOCAL_MODULE := btle-framework
LOCAL_MODULE_CLASS := JAVA_LIBRARIES

LOCAL_NO_EMMA_INSTRUMENT := true
LOCAL_NO_EMMA_COMPILE := true

#LOCAL_JARJAR_RULES := $(LOCAL_PATH)/jarjar-rules.txt

LOCAL_DX_FLAGS := --core-library
LOCAL_MODULE_TAGS := optional

include $(BUILD_JAVA_LIBRARY)

framework_built := $(call java-lib-deps,btle-framework)

# Include subdirectory makefiles
# ============================================================

# If we're building with ONE_SHOT_MAKEFILE (mm, mmm), then what the framework
# team really wants is to build the stuff defined by this makefile.
ifeq (,$(ONE_SHOT_MAKEFILE))
include $(call first-makefiles-under,$(LOCAL_PATH))
endif

