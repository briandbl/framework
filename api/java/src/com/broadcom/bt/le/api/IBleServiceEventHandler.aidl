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

import com.broadcom.bt.le.api.BleGattID;

oneway interface IBleServiceEventHandler
{
    void onServiceRegistered(byte status, int svcId);
    
    void onServiceCreated(byte status, int paramInt);

    void onIncludedServiceAdded(byte status, int paramInt);
    
    void onCharacteristicAdded(byte status, 
                               in BleGattID charId,
                               int charHdl);

    void onCharacteristicDescrAdded(byte status, 
                                    in BleGattID chardescId,
                                    int chardescHdl);

    void onServiceDeleted(byte status);

    void onServiceStarted(byte status, byte paramByte2);

    void onServiceStopped(byte status);

    void onHandleValueIndicationCompleted(byte status, 
                                          int paramInt);

    void onHandleValueNotificationCompleted(byte status, 
                                            int paramInt);

    void onResponseSendCompleted(byte status, int paramInt);

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
}

