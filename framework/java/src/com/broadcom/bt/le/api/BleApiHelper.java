
package com.broadcom.bt.le.api;

import com.broadcom.bt.service.gatt.BluetoothGattID;

public class BleApiHelper
{
    public static BleGattID gatt2BleID(BluetoothGattID gattID)
    {
        if (gattID.getUuidType() == 16) {
            return new BleGattID(gattID.getInstanceID(), gattID.getUuid(),
                    gattID.getServiceType());
        }
        if (gattID.getUuidType() == 2) {
            return new BleGattID(gattID.getInstanceID(), gattID.getUuid16(),
                    gattID.getServiceType());
        }
        return null;
    }

}
