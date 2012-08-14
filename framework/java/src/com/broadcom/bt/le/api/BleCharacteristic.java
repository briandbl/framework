

package com.broadcom.bt.le.api;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class BleCharacteristic extends BleAttribute
        implements Parcelable
{
    private static final String TAG = "BleCharacteristic";
    private HashMap<BleGattID, BleDescriptor> mDescriptorMap = new HashMap();
    private ArrayList<BleDescriptor> mDirtyDescQueue = new ArrayList();
    private int mProp;
    private int mWriteType;
    private byte mAuthReq;
    private int mPermission = 0;
    
    public static final Parcelable.Creator<BleCharacteristic> CREATOR = new Parcelable.Creator()
    {
        public BleCharacteristic createFromParcel(Parcel source) {
            return new BleCharacteristic(source);
            }

        
        public BleCharacteristic[] newArray(int size)
        {
            return new BleCharacteristic[size];
            }
        
    };

    
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
        for (Map.Entry entry : this.mHandleMap.entrySet()) {
            if (handle == ((Integer) entry.getValue()).intValue()) {
                return (BleGattID) entry.getKey();
                }
            }
        return null;
        }

    
    public int getInstanceID()
    {
        return this.mID.getInstanceID();
        }

    
    public void setInstanceID(int instanceID)
    {
        this.mID.setInstanceId(instanceID);
        }

    
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
            return 1;
            }
        
        if (gattUuid.equals(this.mID)) {
            Log.i("BleCharacteristic", "##Writing a characteristic value..");
            Log.i("BleCharacteristic", "##offset=" + offset + " mMaxLength="
                    + this.mMaxLength + " totalsize=" + totalsize);
            return setValue(value, offset, len, gattUuid, totalsize, address);
            }
        BleDescriptor descObj = (BleDescriptor) this.mDescriptorMap.get(gattUuid);
        if (descObj != null) {
            Log.i("BleCharacteristic", "##Writing descriptor value..");
            Log.i("BleCharacteristic",
                    "##offset=" + offset + " mMaxSize=" + descObj.getMaxLength() + " totalsize="
                            + totalsize + "desc uuid =" + descObj.getID());
            if (offset > descObj.getMaxLength())
                return 7;
            if (offset + totalsize > descObj.getMaxLength())
                return 13;
            Log.i("BleCharacteristic", "find the user defined descriptor ");
            return descObj.setValue(value, offset, len, gattUuid, totalsize, address);
            }
        Log.e("BleCharacteristic", "Failed to write the value correctly!!!");
        return -127;
        }

    
    public byte setValue(byte[] value, int offset, int len, BleGattID gattUuid, int totalsize,
            String address)
    {
        return super.setValue(value, offset, len, gattUuid, totalsize, address);
        }

    
    public int getProperty()
    {
        return this.mProp;
        }

    
    public void setProperty(int Prop)
    {
        this.mProp = Prop;
        }

    
    public BleDescriptor getDescriptor(BleGattID descId)
    {
        BleDescriptor descObj = (BleDescriptor) this.mDescriptorMap.get(descId);
        if (descObj != null) {
            return descObj;
            }
        
        return null;
        }

    
    public void addDescriptor(BleGattID descId, BleDescriptor descriptor)
    {
        Log.d("BleCharacteristic", "Inside add descriptor");
        this.mDescriptorMap.put(descId, descriptor);
        this.mDirtyDescQueue.add(descriptor);
        descriptor.setCharRef(this);
        }

    
    public void addDescriptor(BleDescriptor descriptor)
    {
        this.mDescriptorMap.put(descriptor.mID, descriptor);
        this.mDirtyDescQueue.add(descriptor);
        descriptor.setCharRef(this);
        }

    
    public ArrayList<BleDescriptor> getAllDescriptors()
    {
        ArrayList descList = new ArrayList();
        for (Map.Entry entrySet : this.mDescriptorMap.entrySet()) {
            descList.add(entrySet.getValue());
            }
        return descList;
        }

    
    public ArrayList<BleDescriptor> getDirtyDescQueue()
    {
        return this.mDirtyDescQueue;
        }

    
    void updateDirtyDescQueue()
    {
        if (!this.mDirtyDescQueue.isEmpty())
            this.mDirtyDescQueue.remove(0);
        }

    
    public void addHandle(BleGattID uuid, int handle)
    {
        this.mHandleMap.put(uuid, Integer.valueOf(handle));
        }

    
    public int getHandle(BleGattID uuid)
    {
        Integer tmp;
        if ((tmp = (Integer) this.mHandleMap.get(uuid)) != null) {
            return tmp.intValue();
            }
        return -1;
        }

    
    public void setAuthReq(byte AuthReq)
    {
        this.mAuthReq = AuthReq;
        }

    
    public byte getAuthReq()
    {
        return this.mAuthReq;
        }

    
    public boolean isBroadcast() {
        return (this.mProp & 0x1) == 1;
        }

    
    public boolean isReadable() {
        return (this.mProp & 0x2) == 2;
        }

    
    public boolean isWritable() {
        return (this.mProp & 0x8) == 8;
        }

    
    public boolean isWritablewithNoAck() {
        return (this.mProp & 0x4) == 4;
        }

    
    public boolean isNotifyable() {
        return (this.mProp & 0x10) == 16;
        }

    
    public boolean isIndicateable() {
        return (this.mProp & 0x20) == 32;
        }

    
    public void setWriteType(int writeType)
    {
        this.mWriteType = writeType;
        }

    
    public int getWriteType()
    {
        return this.mWriteType;
        }

    
    public boolean isAuthenticated()
    {
        return (this.mProp & 0x40) == 64;
        }

    
    public boolean hasExtendedProperties() {
        return (this.mProp & 0xFFFFFF80) == -128;
        }

    
    public byte[] getValueByHandle(int handle)
    {
        BleGattID gattUuid = getBleGattId(handle);
        if (gattUuid == null) {
            Log.w("BleCharacteristic", "Attribute UUID not found with handle " + handle);
            return null;
            }
        int uuidType = gattUuid.getUuidType();
        if (uuidType == 2)
            return getValueByUUID16(gattUuid);
        if (uuidType == 16) {
            return getValueByUUID128(gattUuid);
            }
        Log.w("BleCharacteristic", "Invalid UUID type.");
        return null;
        }

    
    public byte[] getValueByUUID16(BleGattID uuid)
    {
        int uuid16 = uuid.getUuid16();
        if (uuid16 == -1) {
            Log.w("BleCharacteristic", "Invalid UUID16.");
            return null;
            }
        
        int thisAttrUuid16 = this.mID == null ? -1 : this.mID.getUuid16();
        if (uuid16 == thisAttrUuid16)
        {
            return getValue();
            }
        
        BleDescriptor descObj = (BleDescriptor) this.mDescriptorMap.get(uuid);
        
        if (descObj != null) {
            Log.d("BleCharacteristic", "Descriptor UUID = " + descObj.getID().getUuid16());
            return descObj.getValue();
            }
        
        Log.w("BleCharacteristic", "Attribute query not supported for uuid16 value "
                + uuid16);
        return null;
        }

    
    public byte[] getValueByUUID128(BleGattID uuid)
    {
        UUID uuid128 = uuid.getUuid();
        if (uuid128 == null) {
            return null;
            }
        
        if ((this.mID != null) && (uuid128.equals(this.mID.getUuid()))) {
            return getValue();
            }
        
        BleDescriptor descObj = (BleDescriptor) this.mDescriptorMap.get(uuid);
        if (descObj != null) {
            return descObj.getValue();
            }
        Log.w("BleCharacteristic", "Attribute query not supported for uuid128 value "
                + uuid128);
        return null;
        }
    
}
