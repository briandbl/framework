
package com.broadcom.bt.le.api;

import com.broadcom.bt.service.gatt.BluetoothGattID;

public class BleHelper
{
    public static int updateDirtyMask(BluetoothGattID uuid, int dirtyMask)
    {
        if (uuid.getUuid16() == 10501)
            dirtyMask &= -65;
        if (uuid.getUuid16() == 10496)
            dirtyMask &= -5;
        if (uuid.getUuid16() == 10500)
            dirtyMask &= -9;
        if (uuid.getUuid16() == 10498)
            dirtyMask &= -17;
        if (uuid.getUuid16() == 10499)
            dirtyMask &= -33;
        if (uuid.getUuid16() == 10497)
            dirtyMask &= -3;
        return dirtyMask;
    }

}
