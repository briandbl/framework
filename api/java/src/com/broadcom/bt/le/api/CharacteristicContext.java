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

public class CharacteristicContext
{
    public int mSvcHandle;
    public BluetoothGattID mCharId;
    public int mPermissions;
    public int mCharProperty;
    public boolean mDirtyMask;
    public int mDirtyDescNum;

    public CharacteristicContext(int svc_handle, BluetoothGattID charId, int permissions,
            int char_property, boolean dirtyMask, int dirtyDescNum)
    {
        this.mSvcHandle = svc_handle;
        this.mCharId = charId;
        this.mPermissions = permissions;
        this.mCharProperty = char_property;
        this.mDirtyMask = dirtyMask;
        this.mDirtyDescNum = dirtyDescNum;
    }

    public String toString()
    {
        StringBuilder b = new StringBuilder();
        b.append("svcHandle:");
        b.append(this.mSvcHandle);
        b.append(", CharacteristicId:");
        b.append(this.mCharId);
        b.append(", Permissions:");
        b.append(this.mPermissions);
        b.append(", Property:");
        b.append(this.mCharProperty);
        b.append(", DirtyMask:");
        b.append(this.mDirtyMask);
        return b.toString();
    }

}
