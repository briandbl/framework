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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a Bluetooth LE service characteristic.<br>
 * <br>
 * A characteristic contains descriptors including the actual value, as well as
 * meta data such as the presentation format or a human readable description of
 * the value. <br>
 * <br>
 * A BleCharacteristic serves as a data container used by LE services to read
 * and write characteristics. The properties of this class include: <br>
 * <br>
 * <ul>
 * <li>The raw value of the characteristic</li>
 * <li>Collection of descriptors</li>
 * </ul>
 * A BleCharacteristic is usually assigned to, or retrieved by an instance of a
 * {@link BleClientService} or {@link BleServerService} class.
 */
public class BleCharacteristic extends BleAttribute
        implements Parcelable
{
    private static final String TAG = "BleCharacteristic";
    private HashMap<BleGattID, BleDescriptor> mDescriptorMap = new HashMap<BleGattID, BleDescriptor>();
    private ArrayList<BleDescriptor> mDirtyDescQueue = new ArrayList<BleDescriptor>();
    private int mProp;
    private int mWriteType;
    private byte mAuthReq;
    private int mPermission = 0;

    /** @hide */
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public static final Parcelable.Creator<BleCharacteristic> CREATOR = new Parcelable.Creator()
    {
        @Override
        public BleCharacteristic createFromParcel(Parcel source) {
            return new BleCharacteristic(source);
        }

        @Override
        public BleCharacteristic[] newArray(int size)
        {
            return new BleCharacteristic[size];
        }

    };

    /** @hide */
    public BleCharacteristic(Parcel source)
    {
        super(source);
    }

    public BleCharacteristic(BleGattID charID)
    {
        super(charID);
    }

    private BleGattID getBleGattId(int handle)
    {
        for (Map.Entry<BleGattID, Integer> entry : mHandleMap.entrySet()) {
            if (handle == entry.getValue().intValue()) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Returns the instance ID of this characteristic. The instance ID is used
     * by BLE profiles and services to identify which characteristics belong to
     * a given instance of the LE service or profile.
     * 
     * @see {@link #setInstanceID(int)}
     */
    public int getInstanceID()
    {
        return mID.getInstanceID();
    }

    /**
     * Assigns an instance ID to this characteristic.
     * 
     * @see {@link #getInstanceID()}
     */
    public void setInstanceID(int instanceID)
    {
        mID.setInstanceId(instanceID);
    }

    /**
     * Set the raw value bytes for this characteristic starting at a given
     * offset
     * 
     * @param value byte array with values to write
     * @param offset offset into the value array
     * @param len amount of elements from the value array
     * @param handle GATT id
     * @param totalsize attribute length
     * @param address remote device Bluetooth address
     * @return {@link BleConstants#GATT_SUCCESS} if written
     */
    public byte setValue(byte[] value, int offset, int len, int handle, int totalsize,
            String address)
    {
        int uuid = -1;
        int uuidType = -1;
        Log.e("BleCharacteristic", "#### handle is " + handle + " total size is "
                + totalsize);

        BleGattID gattUuid = getBleGattId(handle);
        if (gattUuid == null) {
            Log.e("BleCharacteristic", "setValue: Invalid handle");
            return BleConstants.GATT_INVALID_HANDLE;
        }

        if (gattUuid.equals(mID)) {
            Log.i("BleCharacteristic", "##Writing a characteristic value..");
            Log.i("BleCharacteristic", "##offset=" + offset + " mMaxLength="
                    + mMaxLength + " totalsize=" + totalsize);
            return setValue(value, offset, len, gattUuid, totalsize, address);
        }
        BleDescriptor descObj = mDescriptorMap.get(gattUuid);
        if (descObj != null) {
            Log.i("BleCharacteristic", "##Writing descriptor value..");
            Log.i("BleCharacteristic",
                    "##offset=" + offset + " mMaxSize=" + descObj.getMaxLength() + " totalsize="
                            + totalsize + "desc uuid =" + descObj.getID());
            if (offset > descObj.getMaxLength())
                return BleConstants.GATT_INVALID_OFFSET;
            if (offset + totalsize > descObj.getMaxLength())
                return BleConstants.GATT_INVALID_ATTR_LEN;
            Log.i("BleCharacteristic", "find the user defined descriptor ");
            return descObj.setValue(value, offset, len, gattUuid, totalsize, address);
        }
        Log.e("BleCharacteristic", "Failed to write the value correctly!!!");
        return -127;
    }

    /**
     * Set the raw value bytes for this characteristic starting at a given
     * offset
     */
    @Override
    public byte setValue(byte[] value, int offset, int len, BleGattID gattUuid, int totalsize,
            String address)
    {
        return super.setValue(value, offset, len, gattUuid, totalsize, address);
    }

    /**
     * Gets the characteristic properties value (bit field).
     */
    public int getProperty()
    {
        return mProp;
    }

    /**
     * Sets the charactereristic properties (Broadcast, read, write etc.).
     * 
     * @see {@link BleConstants#GATT_CHAR_PROP_BIT_BROADCAST},
     *      {@link BleConstants#GATT_CHAR_PROP_BIT_READ},
     *      {@link BleConstants#GATT_CHAR_PROP_BIT_WRITE_NR},
     *      {@link BleConstants#GATT_CHAR_PROP_BIT_WRITE},
     *      {@link BleConstants#GATT_CHAR_PROP_BIT_NOTIFY},
     *      {@link BleConstants#GATT_CHAR_PROP_BIT_INDICATE},
     *      {@link BleConstants#GATT_CHAR_PROP_BIT_AUTH},
     *      {@link BleConstants#GATT_CHAR_PROP_BIT_EXT_PROP}
     */
    public void setProperty(int Prop)
    {
        mProp = Prop;
    }

    /**
     * Gets a descriptor based on UUID
     * 
     * @return descriptor object
     */
    public BleDescriptor getDescriptor(BleGattID descriptor)
    {
        BleDescriptor descObj = mDescriptorMap.get(descriptor);
        if (descObj != null) {
            return descObj;
        }

        return null;
    }

    /**
     * Adds a descriptor object
     */
    public void addDescriptor(BleGattID descId, BleDescriptor descriptor)
    {
        Log.d("BleCharacteristic", "Inside add descriptor");
        mDescriptorMap.put(descId, descriptor);
        mDirtyDescQueue.add(descriptor);
        descriptor.setCharRef(this);
    }

    /**
     * Adds a descriptor object
     */
    public void addDescriptor(BleDescriptor descriptor)
    {
        mDescriptorMap.put(descriptor.mID, descriptor);
        mDirtyDescQueue.add(descriptor);
        descriptor.setCharRef(this);
    }

    /**
     * Returns an array of all user defined descriptors that are contained
     * within this characteristic.
     * 
     * @see {@link BleDescriptor}
     */
    public ArrayList<BleDescriptor> getAllDescriptors()
    {
        ArrayList<BleDescriptor> descList = new ArrayList<BleDescriptor>();
        for (Map.Entry<BleGattID, BleDescriptor> entrySet : mDescriptorMap.entrySet()) {
            descList.add(entrySet.getValue());
        }
        return descList;
    }

    /**
     * Returns a list of all descriptors inside this characteristic that have
     * been modified.
     */
    public ArrayList<BleDescriptor> getDirtyDescQueue()
    {
        return mDirtyDescQueue;
    }

    void updateDirtyDescQueue()
    {
        if (!mDirtyDescQueue.isEmpty())
            mDirtyDescQueue.remove(0);
    }

    /**
     * Maps an attribute of this attribute to a handle value.
     * 
     * @see {@link #getValueByHandle(int)}
     */
    public void addHandle(BleGattID uuid, int handle)
    {
        mHandleMap.put(uuid, Integer.valueOf(handle));
    }

    /**
     * Returns a handle for a given attribute ID
     */
    public int getHandle(BleGattID uuid)
    {
        Integer tmp;
        if ((tmp = mHandleMap.get(uuid)) != null) {
            return tmp.intValue();
        }
        return -1;
    }

    /**
     * Sets the level of authentication required to read/write this attribute.
     */
    @Override
    public void setAuthReq(byte AuthReq)
    {
        mAuthReq = AuthReq;
    }

    /**
     * Returns an array of all user defined descriptors that are contained
     * within this characteristic.
     * 
     * @see {@link BleDescriptor}
     */
    @Override
    public byte getAuthReq()
    {
        return mAuthReq;
    }

    /**
     * @return whether this characteristic broadcasts it's value
     */
    public boolean isBroadcast() {
        return (mProp & BleConstants.GATT_CHAR_PROP_BIT_BROADCAST) == BleConstants.GATT_CHAR_PROP_BIT_BROADCAST;
    }

    /**
     * @return whether this characteristic is readable
     */
    public boolean isReadable() {
        return (mProp & BleConstants.GATT_CHAR_PROP_BIT_READ) == BleConstants.GATT_CHAR_PROP_BIT_READ;
    }

    /**
     * @return whether this characteristic is writable
     */
    public boolean isWritable() {
        return (mProp & BleConstants.GATT_CHAR_PROP_BIT_WRITE) == BleConstants.GATT_CHAR_PROP_BIT_WRITE;
    }

    /**
     * @return whether this characteristic is writable async
     */
    public boolean isWritablewithNoAck() {
        return (mProp & BleConstants.GATT_CHAR_PROP_BIT_WRITE_NR) == BleConstants.GATT_CHAR_PROP_BIT_WRITE_NR;
    }

    /**
     * @return whether this characteristic can be notified
     */
    public boolean isNotifyable() {
        return (mProp & BleConstants.GATT_CHAR_PROP_BIT_NOTIFY) == BleConstants.GATT_CHAR_PROP_BIT_NOTIFY;
    }

    /**
     * @return whether this characteristic can be indicated
     */
    public boolean isIndicateable() {
        return (mProp & BleConstants.GATT_CHAR_PROP_BIT_INDICATE) == BleConstants.GATT_CHAR_PROP_BIT_INDICATE;
    }

    /**
     * Sets the write type for this characteristic
     * 
     * @see {@link BleConstants#GATTC_TYPE_WRITE},
     *      {@link BleConstants#GATTC_TYPE_WRITE_NO_RSP}
     */
    @Override
    public void setWriteType(int writeType)
    {
        this.mWriteType = writeType;
    }

    /**
     * Returns whether this characteristic requires waiting for write operations
     * to be acknowledged or not.
     * 
     * @see {@link #setWriteType(int)}
     */
    @Override
    public int getWriteType()
    {
        return mWriteType;
    }

    /**
     * Returns whether this characteristic permits signed writes.
     */
    public boolean isAuthenticated()
    {
        return (mProp & BleConstants.GATT_CHAR_PROP_BIT_AUTH) == BleConstants.GATT_CHAR_PROP_BIT_AUTH;
    }

    /**
     * @return whether this characteristics has extended properties
     */
    public boolean hasExtendedProperties() {
        return (mProp & BleConstants.GATT_CHAR_PROP_BIT_EXT_PROP) == BleConstants.GATT_CHAR_PROP_BIT_EXT_PROP;
    }

    /**
     * Returns an attribute of this characteristic based on a previously
     * assigned handle value.
     */
    @Override
    public byte[] getValueByHandle(int handle)
    {
        BleGattID gattUuid = getBleGattId(handle);
        if (gattUuid == null) {
            Log.w("BleCharacteristic", "Attribute UUID not found with handle " + handle);
            return null;
        }
        int uuidType = gattUuid.getUuidType();
        if (uuidType == BleConstants.GATT_UUID_TYPE_16)
            return getValueByUUID16(gattUuid);
        if (uuidType == BleConstants.GATT_UUID_TYPE_128) {
            return getValueByUUID128(gattUuid);
        }
        Log.w("BleCharacteristic", "Invalid UUID type.");
        return null;
    }

    /**
     * Retrieves an attribute of this characteristic based on the 16bit UUID
     * 
     * @return null if no attribute was found matching the provided UUID.
     *         <b>Note:</b> If the attribute value is empty, a 0 byte array is
     *         returned.
     */
    public byte[] getValueByUUID16(BleGattID uuid)
    {
        int uuid16 = uuid.getUuid16();
        if (uuid16 == -1) {
            Log.w("BleCharacteristic", "Invalid UUID16.");
            return null;
        }

        int thisAttrUuid16 = mID == null ? -1 : mID.getUuid16();
        if (uuid16 == thisAttrUuid16)
        {
            return getValue();
        }

        BleDescriptor descObj = mDescriptorMap.get(uuid);

        if (descObj != null) {
            Log.d("BleCharacteristic", "Descriptor UUID = " + descObj.getID().getUuid16());
            return descObj.getValue();
        }

        Log.w("BleCharacteristic", "Attribute query not supported for uuid16 value "
                + uuid16);
        return null;
    }

    /**
     * Returns an attribute of this characteristic based on the 128bit UUID
     * 
     * @return null if no attribute was found with the given UUID. <b>Note</b>:
     *         If the attribute value is empty, a 0 byte array is returned.
     */
    public byte[] getValueByUUID128(BleGattID uuid)
    {
        UUID uuid128 = uuid.getUuid();
        if (uuid128 == null) {
            return null;
        }

        if ((mID != null) && (uuid128.equals(mID.getUuid()))) {
            return getValue();
        }

        BleDescriptor descObj = mDescriptorMap.get(uuid);
        if (descObj != null) {
            return descObj.getValue();
        }
        Log.w("BleCharacteristic", "Attribute query not supported for uuid128 value "
                + uuid128);
        return null;
    }

}
