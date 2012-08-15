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
		java/src/com/broadcom/bt/le/api/IBleServiceEventHandler.aidl \
		java/src/com/broadcom/bt/le/api/IBleServiceCallback.aidl \
		java/src/com/broadcom/bt/le/api/IBleProfileEventCallback.aidl \
		java/src/com/broadcom/bt/le/api/IBleClientCallback.aidl \
		java/src/com/broadcom/bt/le/api/IBleCharacteristicDataCallback.aidl \
		java/src/com/broadcom/bt/service/gatt/IBluetoothGatt.aidl

LOCAL_SRC_FILES += $(call all-subdir-java-files, java)

LOCAL_AIDL_INCLUDES += btle/framework/java/src/

LOCAL_NO_STANDARD_LIBRARIES := true
LOCAL_JAVA_LIBRARIES := bouncycastle core ext framework

LOCAL_MODULE := btle-framework
LOCAL_MODULE_CLASS := JAVA_LIBRARIES

LOCAL_NO_EMMA_INSTRUMENT := true
LOCAL_NO_EMMA_COMPILE := true

LOCAL_DX_FLAGS := --core-library
LOCAL_MODULE_TAGS := optional

include $(BUILD_JAVA_LIBRARY)
