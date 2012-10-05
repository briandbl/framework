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

oneway interface IBleServiceCallback {
    
    void onServiceCreated(byte status, int svcHandle);
 
    void onServiceRegistered(byte status,
                             in BluetoothGattID svcId);

    void onIncludedServiceAdded(byte status, int incSvc);

    
    void onCharacteristicAdded(byte status,
                               in BluetoothGattID charDescId, 
                               int charHandle);

    void onCharacteristicDescrAdded(byte status,
                                    in BluetoothGattID charDescId, 
                                    int charDescHandle);

    void onServiceDeleted(byte status);

    void onServiceStarted(byte status);
    
    void onServiceStopped(byte status);
    
    void onHandleValueIndicationCompleted(byte status, 
                                          int attrHandle);

    void onHandleValueNotificationCompleted(byte status, 
                                            int attrHandle);

    void onResponseSendCompleted(byte status, 
                                 int attrHandle);

    void onAttributeRequestRead(in String address, 
                                int connId,
                                int transId, 
                                int attrHandle, 
                                int offset, 
                                boolean isLong);
    
    void onAttributeRequestWrite(in String address, 
                                 int connId,
                                 int transId, 
                                 int attrHandle, 
                                 boolean isPrep, 
                                 int len,
                                 boolean needRsp, 
                                 int offset, 
                                 in byte[] data);
    
    void onAttributeExecWrite(in String address, 
                              int connId,
                              int transId, 
                              int execWrite);

}
