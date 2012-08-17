package com.broadcom.bt.le.api;

import com.broadcom.bt.service.gatt.BluetoothGattID;

interface IBleClientCallback {
    void onAppRegistered(byte status, byte serIf);

    void onAppDeregistered(byte client_if);

    void onConnected(in String address, int connId);
    
    void onDisconnected(int connId, in String address);

    void onSearchResult(int connId, in BluetoothGattID srvcId);

    void onSearchCompleted(int connID, int status);

}
