package com.broadcom.bt.le.api;

import com.broadcom.bt.service.gatt.BluetoothGattID;

interface IBleCharacteristicDataCallback {
    void onGetFirstCharacteristic(int paramInt1, 
                                  int paramInt2,
                                  in BluetoothGattID id1,
                                  in BluetoothGattID id2);

    void onGetFirstCharacteristicDescriptor(int paramInt1, 
                                   int paramInt2,
                                   in BluetoothGattID id1,
                                   in BluetoothGattID id2,
                                   in BluetoothGattID id3);

    void onGetNextCharacteristic(int paramInt1, 
                                 int paramInt2,
                                 in BluetoothGattID id1, 
                                 in BluetoothGattID id2);

    void onGetNextCharacteristicDescriptor(int paramInt1, 
                                           int paramInt2,
                                           in BluetoothGattID id1,
                                           in BluetoothGattID id2,
                                           in BluetoothGattID id3);

    void onReadCharacteristicValue(int paramInt1, 
                                   int paramInt2,
                                   in BluetoothGattID id1, 
                                   in BluetoothGattID id2,
                                   in byte[] paramArrayOfByte);

    void onReadCharDescriptorValue(int paramInt1, 
                                   int paramInt2,
                                   in BluetoothGattID id1, 
                                   in BluetoothGattID id2,
                                   in BluetoothGattID id3, 
                                   in byte[] paramArrayOfByte);

    void onWriteCharValue(int paramInt1, 
                          int paramInt2,
                          in BluetoothGattID id1, 
                          in BluetoothGattID id2);

    void onWriteCharDescrValue(int paramInt1, 
                               int paramInt2,
                               in BluetoothGattID id1, 
                               in BluetoothGattID id2,
                               in BluetoothGattID id3);

    void onRegForNotifications(int paramInt1, 
                               int paramInt2,
                               in BluetoothGattID id1, 
                               in BluetoothGattID id2);

    void onUnregisterNotifications(int paramInt1, 
                                   int paramInt2,
                                   in BluetoothGattID id1, 
                                   in BluetoothGattID id2);

    void onNotify(int paramInt, 
                  in String paramString,
                  in BluetoothGattID id1, 
                  in BluetoothGattID id2,
                  boolean paramBoolean, 
                  in byte[] paramArrayOfByte);

    void onGetFirstIncludedService(int paramInt1, 
                                   int paramInt2,
                                   in BluetoothGattID id1, 
                                   in BluetoothGattID id2);

    void onGetNextIncludedService(int paramInt1, 
                                  int paramInt2,
                                  in BluetoothGattID id1, 
                                  in BluetoothGattID id2);
}
