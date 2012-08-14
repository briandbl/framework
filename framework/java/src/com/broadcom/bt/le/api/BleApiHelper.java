
package com.broadcom.bt.le.api;

import com.broadcom.bt.service.gatt.BluetoothGattID;

public class BleApiHelper
{
    public static BleGattID gatt2BleID(BluetoothGattID gattID)
    {
        if (gattID.getUuidType() == BleConstants.GATT_UUID_TYPE_128) {
            return new BleGattID(gattID.getInstanceID(), gattID.getUuid(),
                    gattID.getServiceType());
        }
        if (gattID.getUuidType() == BleConstants.GATT_UUID_TYPE_16) {
            return new BleGattID(gattID.getInstanceID(), gattID.getUuid16(),
                    gattID.getServiceType());
        }
        return null;
    }

}
