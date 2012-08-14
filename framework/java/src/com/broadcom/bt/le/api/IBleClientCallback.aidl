package com.broadcom.bt.le.api;

import com.broadcom.bt.service.gatt.BluetoothGattID;

interface IBleClientCallback {
    void onAppRegistered(byte paramByte1, byte paramByte2);

    void onAppDeregistered(byte paramByte);

    void onConnected(in String paramString, int paramInt);
    
    void onDisconnected(int paramInt, in String paramString);

    void onSearchResult(int paramInt, in BluetoothGattID id);

    void onSearchCompleted(int paramInt1, int paramInt2);

}
