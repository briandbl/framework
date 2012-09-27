/************************************************************************************
 *
 *  Copyright (C) 2012      Naranjo Manuel Francisco <naranjo.manuel@gmail.com>
 *  Copyright (C) 2009-2011 Broadcom Corporation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ************************************************************************************/

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
