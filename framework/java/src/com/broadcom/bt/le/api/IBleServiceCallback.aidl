package com.broadcom.bt.le.api;

import com.broadcom.bt.service.gatt.BluetoothGattID;                                                                                   

interface IBleServiceCallback {
    
    void onServiceCreated(byte paramByte, int paramInt);
 
    void onServiceRegistered(byte paramByte,
                             in BluetoothGattID id);

    void onIncludedServiceAdded(byte paramByte, int paramInt);

    
    void onCharacteristicAdded(byte paramByte,
                               in BluetoothGattID id, 
                               int paramInt);

    void onCharacteristicDescrAdded(byte paramByte,
                                    in BluetoothGattID id, 
                                    int paramInt);

    void onServiceDeleted(byte paramByte);

    void onServiceStarted(byte paramByte);
    
    void onServiceStopped(byte paramByte);
    
    void onHandleValueIndicationCompleted(byte paramByte, 
                                          int paramInt);

    void onHandleValueNotificationCompleted(byte paramByte, 
                                            int paramInt);

    void onResponseSendCompleted(byte paramByte, 
                                 int paramInt);

    void onAttributeRequestRead(in String paramString, 
                                int paramInt1,
                                int paramInt2, 
                                int paramInt3, 
                                int paramInt4, 
                                boolean paramBoolean);
    
    void onAttributeRequestWrite(in String paramString, 
                                 int paramInt1,
                                 int paramInt2, 
                                 int paramInt3, 
                                 boolean paramBoolean1, 
                                 int paramInt4,
                                 boolean paramBoolean2, 
                                 int paramInt5, 
                                 in byte[] paramArrayOfByte);
    
    void onAttributeExecWrite(in String paramString, 
                              int paramInt1,
                              int paramInt2, 
                              int paramInt3);

}
