
package com.broadcom.bt.le.api;

import com.broadcom.bt.service.gatt.BluetoothGattID;

/**
 * @hide
 */
public class BleHelper
{
    public static int updateDirtyMask(BluetoothGattID uuid, int dirtyMask)
    {
        if (uuid.getUuid16() == BleConstants.GATT_UUID_CHAR_AGG_FORMAT16)
            dirtyMask &= -65;
        if (uuid.getUuid16() == BleConstants.GATT_UUID_CHAR_EXT_PROP16)
            dirtyMask &= -5;
        if (uuid.getUuid16() == BleConstants.GATT_UUID_CHAR_PRESENT_FORMAT16)
            dirtyMask &= -9;
        if (uuid.getUuid16() == BleConstants.GATT_UUID_CHAR_CLIENT_CONFIG16)
            dirtyMask &= -17;
        if (uuid.getUuid16() == BleConstants.GATT_UUID_CHAR_SRVR_CONFIG16)
            dirtyMask &= -33;
        if (uuid.getUuid16() == BleConstants.GATT_UUID_CHAR_DESCRIPTION16)
            dirtyMask &= -3;
        return dirtyMask;
    }

}
