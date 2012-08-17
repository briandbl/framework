
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
