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
	java/src/android/bluetooth/le/IBluetoothLE.aidl \
	java/src/org/bluez/Adapter.java \
	java/src/org/bluez/Characteristic.java \
	java/src/org/bluez/Device.java \
	java/src/org/bluez/Error.java \
	java/src/org/bluez/Manager.java \
	java/src/org/bluez/Service.java \
	java/src/org/bluez/Watcher.java \
	java/src/cx/ath/matthew/utils/Hexdump.java \
	java/src/org/freedesktop/dbus/Struct.java \
	java/src/org/freedesktop/dbus/Path.java \
	java/src/org/freedesktop/dbus/UInt32.java \
	java/src/org/freedesktop/dbus/Tuple.java \
	java/src/org/freedesktop/dbus/RemoteInvocationHandler.java \
	java/src/org/freedesktop/dbus/InternalSignal.java \
	java/src/org/freedesktop/dbus/DBusConnection.java \
	java/src/org/freedesktop/dbus/Position.java \
	java/src/org/freedesktop/dbus/Transport.java \
	java/src/org/freedesktop/dbus/AbstractConnection.java \
	java/src/org/freedesktop/dbus/Variant.java \
	java/src/org/freedesktop/dbus/MethodTuple.java \
	java/src/org/freedesktop/dbus/MethodCall.java \
	java/src/org/freedesktop/dbus/SignalTuple.java \
	java/src/org/freedesktop/dbus/ArrayFrob.java \
	java/src/org/freedesktop/dbus/RemoteObject.java \
	java/src/org/freedesktop/dbus/DBusAsyncReply.java \
	java/src/org/freedesktop/dbus/MessageWriter.java \
	java/src/org/freedesktop/dbus/CallbackHandler.java \
	java/src/org/freedesktop/dbus/ObjectTree.java \
	java/src/org/freedesktop/dbus/UInt64.java \
	java/src/org/freedesktop/dbus/ExportedObject.java \
	java/src/org/freedesktop/dbus/DBusSignal.java \
	java/src/org/freedesktop/dbus/DBusMap.java \
	java/src/org/freedesktop/dbus/Container.java \
	java/src/org/freedesktop/dbus/ObjectPath.java \
	java/src/org/freedesktop/dbus/DBusMemberName.java \
	java/src/org/freedesktop/dbus/DirectConnection.java \
	java/src/org/freedesktop/dbus/DBusInterface.java \
	java/src/org/freedesktop/dbus/DBusSerializable.java \
	java/src/org/freedesktop/dbus/TypeSignature.java \
	java/src/org/freedesktop/dbus/MessageReader.java \
	java/src/org/freedesktop/dbus/Message.java \
	java/src/org/freedesktop/dbus/DBusCallInfo.java \
	java/src/org/freedesktop/dbus/types/DBusMapType.java \
	java/src/org/freedesktop/dbus/types/DBusListType.java \
	java/src/org/freedesktop/dbus/types/DBusStructType.java \
	java/src/org/freedesktop/dbus/Error.java \
	java/src/org/freedesktop/dbus/StrongReference.java \
	java/src/org/freedesktop/dbus/exceptions/FatalDBusException.java \
	java/src/org/freedesktop/dbus/exceptions/NotConnected.java \
	java/src/org/freedesktop/dbus/exceptions/MessageProtocolVersionException.java \
	java/src/org/freedesktop/dbus/exceptions/DBusExecutionException.java \
	java/src/org/freedesktop/dbus/exceptions/UnknownTypeCodeException.java \
	java/src/org/freedesktop/dbus/exceptions/MarshallingException.java \
	java/src/org/freedesktop/dbus/exceptions/MessageFormatException.java \
	java/src/org/freedesktop/dbus/exceptions/MessageTypeException.java \
	java/src/org/freedesktop/dbus/exceptions/NonFatalException.java \
	java/src/org/freedesktop/dbus/exceptions/DBusException.java \
	java/src/org/freedesktop/dbus/exceptions/FatalException.java \
	java/src/org/freedesktop/dbus/exceptions/InternalMessageException.java \
	java/src/org/freedesktop/dbus/DBusMatchRule.java \
	java/src/org/freedesktop/dbus/DBusInterfaceName.java \
	java/src/org/freedesktop/dbus/MethodReturn.java \
	java/src/org/freedesktop/dbus/UInt16.java \
	java/src/org/freedesktop/dbus/Gettext.java \
	java/src/org/freedesktop/dbus/Marshalling.java \
	java/src/org/freedesktop/dbus/DBusSigHandler.java \
	java/src/org/freedesktop/dbus/EfficientMap.java \
	java/src/org/freedesktop/dbus/EfficientQueue.java \
	java/src/org/freedesktop/DBus.java


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

include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
	java/src/android/server/le/test/TestManager.java \
	java/src/android/server/le/test/TestDevice.java \
	java/src/android/server/le/test/TestCharacteristic.java \
	java/src/android/server/le/test/TestService.java
 
LOCAL_NO_STANDARD_LIBRARIES := true
LOCAL_JAVA_LIBRARIES := bouncycastle core core-junit ext framework btle-framework

LOCAL_MODULE := btle-tests
LOCAL_MODULE_CLASS := JAVA_LIBRARIES
LOCAL_MODULE_TAGS := optional

LOCAL_NO_EMMA_INSTRUMENT := true
LOCAL_NO_EMMA_COMPILE := true

include $(BUILD_JAVA_LIBRARY)

