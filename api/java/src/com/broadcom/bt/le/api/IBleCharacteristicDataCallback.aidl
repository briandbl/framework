/************************************************************************************
 *
 *  Copyright (C) 2012      Naranjo Manuel Francisco <naranjo.manuel@gmail.com>
 *  Copyright (C) 2009-2011 Broadcom Corporation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ************************************************************************************/
package com.broadcom.bt.le.api;

import com.broadcom.bt.service.gatt.BluetoothGattID;

oneway interface IBleCharacteristicDataCallback {
    void onGetFirstCharacteristic(int connID, 
                                  int status,
                                  in BluetoothGattID svcId,
                                  in BluetoothGattID charId,
                                  int prop);

    void onGetFirstCharacteristicDescriptor(int connId, 
                                   int status,
                                   in BluetoothGattID svcId,
                                   in BluetoothGattID charId,
                                   in BluetoothGattID descId);

    void onGetNextCharacteristic(int connId, 
                                 int status,
                                 in BluetoothGattID svcId, 
                                 in BluetoothGattID charId,
                                 int prop);

    void onGetNextCharacteristicDescriptor(int connId, 
                                           int status,
                                           in BluetoothGattID svcId,
                                           in BluetoothGattID charId,
                                           in BluetoothGattID descId);

    void onReadCharacteristicValue(int connId, 
                                   int status,
                                   in BluetoothGattID svcId, 
                                   in BluetoothGattID charId,
                                   in byte[] data);

    void onReadCharDescriptorValue(int connId, 
                                   int status,
                                   in BluetoothGattID svcId, 
                                   in BluetoothGattID charId,
                                   in BluetoothGattID descId, 
                                   in byte[] data);

    void onWriteCharValue(int connId, 
                          int status,
                          in BluetoothGattID svcId, 
                          in BluetoothGattID charId);

    void onWriteCharDescrValue(int connId, 
                               int status,
                               in BluetoothGattID svcId, 
                               in BluetoothGattID charId,
                               in BluetoothGattID descId);

    void onRegForNotifications(int connId, 
                               int status,
                               in BluetoothGattID svcId, 
                               in BluetoothGattID charId);

    void onUnregisterNotifications(int connId, 
                                   int status,
                                   in BluetoothGattID svcId, 
                                   in BluetoothGattID charId);

    void onNotify(int connId, 
                  in String address,
                  in BluetoothGattID svcId, 
                  in BluetoothGattID charId,
                  boolean isNotify, 
                  in byte[] data);

    void onGetFirstIncludedService(int connId, 
                                   int status,
                                   in BluetoothGattID svcId, 
                                   in BluetoothGattID charId);

    void onGetNextIncludedService(int connId, 
                                  int status,
                                  in BluetoothGattID svcId, 
                                  in BluetoothGattID charId);
}
