package com.broadcom.bt.le.api;

import com.broadcom.bt.service.gatt.BluetoothGattID;                                                                                   

interface IBleServiceCallback {
    
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
