
package com.broadcom.bt.le.api;

import com.broadcom.bt.service.gatt.BluetoothGattID;

public class GattServerContext
{
    private int mSvcHandle;
    public BluetoothGattID mSvcUuid;
    public BluetoothGattID mAppUuid;
    public IBleServiceCallback mCallback;

    public GattServerContext(BluetoothGattID serviceUuid, BluetoothGattID appUuid,
            IBleServiceCallback callback)
    {
        this.mSvcUuid = serviceUuid;
        this.mAppUuid = appUuid;
        this.mCallback = callback;
    }

    public void setServiceHandle(int svcHandle) {
        this.mSvcHandle = svcHandle;
    }

    public int getServiceHandle() {
        return this.mSvcHandle;
    }

    public void setServiceInstid(int instId) {
        this.mSvcUuid.setInstId(instId);
    }

    public int getServiceInstId() {
        return this.mSvcUuid.getInstanceID();
    }

}
