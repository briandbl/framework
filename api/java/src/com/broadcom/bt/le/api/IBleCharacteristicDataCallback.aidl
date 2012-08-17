package com.broadcom.bt.le.api;

import com.broadcom.bt.service.gatt.BluetoothGattID;

interface IBleCharacteristicDataCallback {
    void onGetFirstCharacteristic(int connID, 
                                  int status,
                                  in BluetoothGattID svcId,
                                  in BluetoothGattID charId);

    void onGetFirstCharacteristicDescriptor(int connId, 
                                   int status,
                                   in BluetoothGattID svcId,
                                   in BluetoothGattID charId,
                                   in BluetoothGattID descId);

    void onGetNextCharacteristic(int connId, 
                                 int status,
                                 in BluetoothGattID svcId, 
                                 in BluetoothGattID charId);

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
