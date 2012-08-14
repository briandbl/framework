
package com.broadcom.bt.le.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class BleAttribute
        implements Parcelable
{
    private static final String TAG = "BleAttribute";
    private static final int DEFAULT_KEYSIZE = 16;
    protected BleGattID mID = null;
    protected int mMaxLength = 0;
    protected boolean mIsFixed = false;
    protected int mFormat = 0;
    protected int mLength = 0;

    protected boolean mDirty = false;
    protected int mPermissionMask = 0;
    protected int mKeySize = 0;
    protected byte mAuthReq = 0;
    protected int mWriteType = 2;

    protected int mHandle = -1;
    protected byte[] mValue = null;

    private int mPermission = 0;

    HashMap<BleGattID, Integer> mHandleMap = new HashMap();

    HashMap<String, ArrayList<PrepareWriteContext>> writeQueue = new HashMap();

    HashMap<String, Integer> writeSizeQueue = new HashMap();

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public static final Parcelable.Creator<BleAttribute> CREATOR = new Parcelable.Creator()
    {
        public BleAttribute createFromParcel(Parcel source) {
            return new BleAttribute(source);
        }

        public BleAttribute[] newArray(int size)
        {
            return new BleAttribute[size];
        }

    };

    public BleGattID getID()
    {
        return this.mID;
    }

    public BleAttribute(Parcel source)
    {
    }

    public BleAttribute(BleGattID attrID)
    {
        this.mID = attrID;

        this.mMaxLength = 100;
        this.mValue = new byte[this.mMaxLength];
    }

    public boolean isDirty()
    {
        return this.mDirty;
    }

    public void setDirty(boolean dirty)
    {
        this.mDirty = dirty;
    }

    public int getValueFormat()
    {
        return this.mFormat;
    }

    public void setValueFormat(int format)
    {
        this.mFormat = format;
        this.mIsFixed = true;

        switch (format) {
            case 1:

            case 2:

            case 3:

            case 4:

            case 12:
                this.mMaxLength = 1;
                break;
            case 5:

            case 6:

            case 13:

            case 14:

            case 22:
                this.mMaxLength = 2;
                break;
            case 7:

            case 15:
                this.mMaxLength = 3;
                break;
            case 8:

            case 16:

            case 20:

            case 23:

            case 24:
                this.mMaxLength = 4;
                break;
            case 9:

            case 17:
                this.mMaxLength = 6;
                break;
            case 10:

            case 18:

            case 21:
                this.mMaxLength = 8;
                break;
            case 11:

            case 19:
                this.mMaxLength = 16;
                break;
            case 25:

            case 26:

            case 27:
                this.mMaxLength = 100;
                this.mIsFixed = false;
                break;
            default:
                Log.e("BleAttribute", "Format not found");
                this.mFormat = 0;
                this.mMaxLength = 0;
                this.mIsFixed = true;
        }
    }

    public boolean isFixedLength()
    {
        return this.mIsFixed;
    }

    public int getMaxLength()
    {
        return this.mMaxLength;
    }

    public void setMaxLength(String maxSizeValString)
    {
        if (!this.mIsFixed)
            this.mMaxLength = Integer.parseInt(maxSizeValString, 16);
        else
            Log.e("BleAttribute", "Format is fixed size. Ignore the MaxSize tag");
    }

    public void setLength(int length)
    {
        this.mLength = length;
    }

    public int getLength()
    {
        return this.mLength;
    }

    public byte[] getValue()
    {
        if ((this.mLength > 0) && (this.mValue != null)) {
            byte[] rValue = new byte[this.mLength];
            System.arraycopy(this.mValue, 0, rValue, 0, this.mLength);
            return rValue;
        }
        Log.i("BleAttribute", "the value is not supported so null is returned");
        return null;
    }

    public byte getValueByte()
    {
        if ((this.mLength > 0) && (this.mValue != null)) {
            return this.mValue[0];
        }

        Log.i("BleAttribute", "the value is not initialized -1 is returned");
        return -1;
    }

    public int getValueInt()
    {
        if ((this.mLength > 0) && (this.mValue != null)) {
            int value = 0;
            for (int i = 0; i < this.mLength; i++) {
                int shift = (this.mLength - 1 - i) * 8;
                value += ((this.mValue[i] & 0xFF) << shift);
            }
            return value;
        }

        Log.i("BleAttribute", "the value is not initialized -1 is returned");
        return -1;
    }

    public byte setValue(byte[] value)
    {
        int length = value.length;
        if (length > this.mMaxLength) {
            length = this.mMaxLength;
        }
        System.arraycopy(value, 0, this.mValue, 0, length);
        this.mLength = length;
        this.mDirty = true;
        return 0;
    }

    public byte setValue(int value)
    {
        int length = 4;
        byte[] valueByte = null;

        if (length > this.mMaxLength) {
            length = this.mMaxLength;
            return -123;
        }

        this.mLength = length;
        valueByte = new byte[length];
        
        for (int i = 0; i < this.mLength; i++) {
            int offset = (this.mLength - 1 - i) * 8;
            valueByte[i] = (byte) (value >>> offset & 0xFF);
        }

        System.arraycopy(valueByte, 0, this.mValue, 0, this.mLength);
        this.mDirty = true;

        return 0;
    }

    public byte setValue(byte value)
    {
        int length = 1;
        this.mValue[0] = value;
        this.mLength = length;
        this.mDirty = true;
        return 0;
    }

    public byte setValue(byte[] value, int length)
    {
        if (length > this.mMaxLength)
            length = this.mMaxLength;
        System.arraycopy(value, 0, this.mValue, 0, length);
        this.mLength = length;
        this.mDirty = true;
        return 0;
    }

    public byte setValue(byte[] value, int offset, int len, BleGattID gattUuid, int totalsize,
            String address)
    {
        int uuid = -1;
        int uuidType = -1;

        if (gattUuid == null) {
            Log.e("BleAttribute", "setValue: Invalid handle");
            return 1;
        }

        uuidType = gattUuid.getUuidType();
        Log.e("BleAttribute", "#### UUID type=" + gattUuid.getUuidType());

        if ((uuidType == 2) && ((uuid = gattUuid.getUuid16()) == -1))
        {
            Log.e("BleAttribute", "setValue: Invalid handle (UUID16 not found)");
            return 1;
        }

        if ((uuidType == 16) && (gattUuid == null)) {
            Log.e("BleAttribute", "setValue: Invalid handle (UUID128 not found)");
            return 1;
        }

        if (gattUuid.equals(this.mID)) {
            Log.i("BleAttribute", "##Writing a attribute value..");
            Log.i("BleAttribute", "##offset=" + offset + " mMaxLength=" + this.mMaxLength
                    + " totalsize=" + totalsize);

            if (offset > this.mMaxLength) {
                Log.d("BleAttribute", "Offsle is invalid");
                return 7;
            }

            if (offset + totalsize > this.mMaxLength) {
                return 13;
            }
            System.arraycopy(value, 0, this.mValue, offset, len);
            if (!this.mIsFixed)
                this.mLength = (offset + len);
            System.out.println("BleAttribute mLength=(" + this.mLength + ")");
        } else {
            Log.e("BleAttribute", "setValue: Invalid Uuid");
            return 1;
        }

        this.mDirty = true;
        return 0;
    }

    public void setKeySize(int keySize)
    {
        this.mKeySize = keySize;
    }

    public int getPermMask()
    {
        return this.mPermissionMask;
    }

    public void setPermMask(int permMask)
    {
        this.mPermissionMask = permMask;
        setPermission(permMask, this.mKeySize);
    }

    public void setPermission(int permMask, int keySize)
    {
        int tempKeySize = 0;
        if (keySize > 0) {
            tempKeySize = keySize - 16;
        }

        tempKeySize <<= 12;
        this.mPermission = (tempKeySize | permMask);
    }

    public int getPermission()
    {
        return this.mPermission;
    }

    public void setAuthReq(byte AuthReq)
    {
        this.mAuthReq = AuthReq;
    }

    public byte getAuthReq()
    {
        return this.mAuthReq;
    }

    public void setWriteType(int writeType)
    {
        this.mWriteType = writeType;
    }

    public int getWriteType()
    {
        return this.mWriteType;
    }

    public boolean isRegistered()
    {
        return this.mHandleMap.get(this.mID) != null;
    }

    public void setHandle(int handle)
    {
        this.mHandle = handle;
    }

    public int getHandle()
    {
        if (this.mHandle >= 0) {
            return this.mHandle;
        }
        return -1;
    }

    public byte[] getValueByHandle(int handle)
    {
        BleGattID gattUuid = null;
        if (this.mHandle == handle) {
            gattUuid = this.mID;
            return this.mValue;
        }
        Log.w("BleAttribute", "Attribute UUID not found with handle " + handle);
        return null;
    }

    @SuppressWarnings("unused")
    private byte[] convertBigEndianArrayToLittlEndian(byte[] bigEndian, int size)
    {
        byte[] littleEndian = new byte[size];
        int i = size - 1;
        for (int j = 0; i >= 0; j++) {
            littleEndian[j] = bigEndian[i];

            i--;
        }
        for (i = 0; i < size; i++) {
            System.out.printf("Big array idx i=%d val=0x%x", new Object[] {
                    Integer.valueOf(i), Byte.valueOf(bigEndian[i])
            });
            System.out.printf("Little array idx i=%d val=0x%x", new Object[] {
                    Integer.valueOf(i), Byte.valueOf(littleEndian[i])
            });
        }
        return littleEndian;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(flags);
    }

}
