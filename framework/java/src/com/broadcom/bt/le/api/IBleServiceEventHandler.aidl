package com.broadcom.bt.le.api;

import com.broadcom.bt.le.api.BleGattID;

interface IBleServiceEventHandler
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

