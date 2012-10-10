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
package com.broadcom.bt.service.gatt;

import com.broadcom.bt.service.gatt.BluetoothGattID;
import com.broadcom.bt.service.gatt.BluetoothGattCharID;
import com.broadcom.bt.service.gatt.BluetoothGattCharDescrID;
import com.broadcom.bt.service.gatt.BluetoothGattInclSrvcID;

import com.broadcom.bt.le.api.IBleServiceCallback;
import com.broadcom.bt.le.api.IBleClientCallback;
import com.broadcom.bt.le.api.IBleCharacteristicDataCallback;
import com.broadcom.bt.le.api.IBleProfileEventCallback;

interface IBluetoothGatt {

    int getApiLevel();
    
    String getFrameworkVersion();

    byte getDeviceType(in String address);

    void getUUIDs(in String address);
    
    void registerApp(in BluetoothGattID appUuid,
                     in IBleClientCallback callback);

    void unregisterApp(byte interfaceID);

    boolean setEncryption(in String address, byte action);

    void setScanParameters(int scanInterval, int scanWindow);

    void filterEnable(boolean p);

    void filterEnableBDA(boolean enable, int addr_type, in String address);

    void clearManufacturerData();

    void filterManufacturerData(int company, 
                                in byte[] data1,
                                in byte[] data2, 
                                in byte[] data3, 
                                in byte[] data4);

    void filterManufacturerDataBDA(int company, 
                                   in byte[] data1,
                                   in byte[] data2, 
                                   in byte[] data3, 
                                   in byte[] data4,
                                   boolean has_bda, 
                                   int addr_type,
                                   in String address);

    void observe(boolean start, 
                 int duration);

    void open(byte interfaceID, 
              in String remote, 
              boolean foreground);

    void close(byte interfaceID, 
               in String remote, 
               int clientID,
               boolean foreground);

    void registerServiceDataCallback(int connID,
                                     in BluetoothGattID serviceID, 
                                     in String address,
                                     in IBleCharacteristicDataCallback callback);

    void searchService(int connID, 
                       in BluetoothGattID serviceID);

    void getFirstChar(int connID, 
                      in BluetoothGattID serviceID,
                      in BluetoothGattID id);
    
    void getNextChar(int connID,
                     in BluetoothGattCharID charID, 
                     in BluetoothGattID id);

    void getFirstCharDescr(int connID,
                           in BluetoothGattCharID charID, 
                           in BluetoothGattID id);

    void getNextCharDescr(int connID,
                          in BluetoothGattCharDescrID charDescrID,
                          in BluetoothGattID id);

    void getFirstIncludedService(int connID,
                                 in BluetoothGattID serviceID, 
                                 in BluetoothGattID id2);

    void getNextIncludedService(int connID,
                                in BluetoothGattInclSrvcID includedServiceID,
                                in BluetoothGattID id);

    void readChar(int connID, 
                  in BluetoothGattCharID charID,
                  byte authReq);

    void readCharDescr(int connID,
                       in BluetoothGattCharDescrID charDescID, 
                       byte authReq);

    void writeCharValue(int connID,
                        in BluetoothGattCharID CharID, 
                        int writeType, 
                        byte authReq,
                        in byte[] value);

    void writeCharDescrValue(int connID,
                             in BluetoothGattCharDescrID descID, 
                             int writeType, 
                             byte authReq,
                             in byte[] value);
                             
    void sendIndConfirm(int connID,
                        in BluetoothGattCharID charID);

    void prepareWrite(int paramInt1,
                      in BluetoothGattCharID charID, 
                      int paramInt2, 
                      int paramInt3,
                      in byte[] paramArrayOfByte);

    void executeWrite(int paramInt, 
                      boolean paramBoolean);

    boolean registerForNotifications(byte interfaceID, 
                                  in String address,
                                  in BluetoothGattCharID charID);

    boolean deregisterForNotifications(byte interfaceID, 
                                    in String address,
                                    in BluetoothGattCharID charID);

    void registerServerServiceCallback(in BluetoothGattID id1,
                                       in BluetoothGattID id2, 
                                       in IBleServiceCallback callback);

    void registerServerProfileCallback(in BluetoothGattID id,
                                       in IBleProfileEventCallback callback);

    void unregisterServerServiceCallback(int paramInt);

    void unregisterServerProfileCallback(int paramInt);

    void GATTServer_CreateService(byte paramByte,
                                  in BluetoothGattID id, 
                                  int paramInt);

    void GATTServer_AddIncludedService(int paramInt1, 
                                       int paramInt2);

    void GATTServer_AddCharacteristic(int paramInt1,
                                      in BluetoothGattID id, 
                                      int paramInt2, 
                                      int paramInt3,
                                      boolean paramBoolean, 
                                      int paramInt4);

    void GATTServer_AddCharDescriptor(int paramInt1, 
                                      int paramInt2,
                                      in BluetoothGattID id);

    void GATTServer_DeleteService(int paramInt);

    void GATTServer_StartService(int paramInt, 
                                 byte paramByte);

    void GATTServer_StopService(int paramInt);

    void GATTServer_HandleValueIndication(int paramInt1, 
                                          int paramInt2,
                                          in byte[] paramArrayOfByte);

    void GATTServer_HandleValueNotification(int paramInt1, 
                                            int paramInt2,
                                            in byte[] paramArrayOfByte);

    void GATTServer_SendRsp(int paramInt1, 
                            int paramInt2, 
                            byte paramByte1,
                            int paramInt3, 
                            int paramInt4, 
                            in byte[] paramArrayOfByte, 
                            byte paramByte2,
                            boolean paramBoolean);

    void GATTServer_Open(byte paramByte, 
                         in String paramString,
                         boolean paramBoolean);

    void GATTServer_CancelOpen(byte paramByte, 
                               in String paramString,
                               boolean paramBoolean);

    void GATTServer_Close(int paramInt);
}
