
package com.broadcom.bt.le.api;

import android.os.ParcelUuid;

public abstract interface IBleServiceEventHandler
{
    public abstract void onServiceRegistered(byte paramByte, int paramInt);

    public abstract void onServiceCreated(byte paramByte, int paramInt);

    public abstract void onIncludedServiceAdded(byte paramByte, int paramInt);

    public abstract void onCharacteristicAdded(byte paramByte, ParcelUuid paramParcelUuid,
            int paramInt);

    public abstract void onCharacteristicDescrAdded(byte paramByte, ParcelUuid paramParcelUuid,
            int paramInt);

    public abstract void onServiceDeleted(byte paramByte);

    public abstract void onServiceStarted(byte paramByte1, byte paramByte2);

    public abstract void onServiceStopped(byte paramByte);

    public abstract void onHandleValueIndicationCompleted(byte paramByte, int paramInt);

    public abstract void onHandleValueNotificationCompleted(byte paramByte, int paramInt);

    public abstract void onResponseSendCompleted(byte paramByte, int paramInt);

    public abstract void onAttributeRequestRead(String paramString, int paramInt1, int paramInt2,
            int paramInt3, int paramInt4, boolean paramBoolean);

    public abstract void onAttributeRequestWrite(String paramString, int paramInt1, int paramInt2,
            int paramInt3, boolean paramBoolean1, int paramInt4, boolean paramBoolean2,
            int paramInt5, byte[] paramArrayOfByte);
}

/*
 * Location:
 * /opt/android/sdk/add-ons/addon-open_bluetooth_low-energy_api-broadcom
 * -10/libs/com.broadcom.bt.le.jar Qualified Name:
 * com.broadcom.bt.le.api.IBleServiceEventHandler JD-Core Version: 0.6.0
 */
