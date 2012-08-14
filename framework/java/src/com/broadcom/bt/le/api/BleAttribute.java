
package com.broadcom.bt.le.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a Bluetooth LE attribute. {@link BleCharacteristic} and
 * {@link BleDescriptor} objects are derived from this class.
 */
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
    protected int mWriteType = BleConstants.GATTC_TYPE_WRITE;

    protected int mHandle = BleConstants.GATT_UNDEFINED;
    protected byte[] mValue = null;

    private int mPermission = 0;

    HashMap<BleGattID, Integer> mHandleMap = new HashMap<BleGattID, Integer>();

    HashMap<String, ArrayList<PrepareWriteContext>> writeQueue = new HashMap<String, ArrayList<PrepareWriteContext>>();

    HashMap<String, Integer> writeSizeQueue = new HashMap<String, Integer>();

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    /**
     * @hide
     */
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

    /**
     * Returns the UUID of this attribute.
     */
    public BleGattID getID()
    {
        return mID;
    }

    public BleAttribute(Parcel source)
    {
    }

    public BleAttribute(BleGattID attrID)
    {
        mID = attrID;

        mMaxLength = BleConstants.GATT_MAX_CHAR_VALUE_LENGTH;
        mValue = new byte[mMaxLength];
    }

    /**
     * Indicates if this object has been modified.
     */
    public boolean isDirty()
    {
        return mDirty;
    }

    /**
     * A BleAttribute automatically marks itself as dirty when a value is
     * modified using the appropriate setters. This function allows the internal
     * state to be overwritten.
     */
    public void setDirty(boolean dirty)
    {
        mDirty = dirty;
    }

    /**
     * Returns the format of this attribute.
     * 
     * @see {@link BleConstants#GATT_FORMAT_RES},
     *      {@link BleConstants#GATT_FORMAT_BOOL},
     *      {@link BleConstants#GATT_FORMAT_2BITS},
     *      {@link BleConstants#GATT_FORMAT_NIBBLE},
     *      {@link BleConstants#GATT_FORMAT_UINT8},
     *      {@link BleConstants#GATT_FORMAT_UINT12},
     *      {@link BleConstants#GATT_FORMAT_UINT16},
     *      {@link BleConstants#GATT_FORMAT_UINT24},
     *      {@link BleConstants#GATT_FORMAT_UINT32},
     *      {@link BleConstants#GATT_FORMAT_UINT48},
     *      {@link BleConstants#GATT_FORMAT_UINT64},
     *      {@link BleConstants#GATT_FORMAT_UINT128},
     *      {@link BleConstants#GATT_FORMAT_SINT8},
     *      {@link BleConstants#GATT_FORMAT_SINT12},
     *      {@link BleConstants#GATT_FORMAT_SINT16},
     *      {@link BleConstants#GATT_FORMAT_SINT24},
     *      {@link BleConstants#GATT_FORMAT_SINT32},
     *      {@link BleConstants#GATT_FORMAT_SINT48},
     *      {@link BleConstants#GATT_FORMAT_SINT64},
     *      {@link BleConstants#GATT_FORMAT_SINT128},
     *      {@link BleConstants#GATT_FORMAT_FLOAT32},
     *      {@link BleConstants#GATT_FORMAT_FLOAT64},
     *      {@link BleConstants#GATT_FORMAT_SFLOAT},
     *      {@link BleConstants#GATT_FORMAT_FLOAT},
     *      {@link BleConstants#GATT_FORMAT_DUINT16},
     *      {@link BleConstants#GATT_FORMAT_UTF8S},
     *      {@link BleConstants#GATT_FORMAT_UTF16S},
     *      {@link BleConstants#GATT_FORMAT_STRUCT},
     *      {@link BleConstants#GATT_FORMAT_MAX}
     */
    public int getValueFormat()
    {
        return mFormat;
    }

    /**
     * Sets the value format of the attribute based on a predefined constant
     * representing a attribute value format
     * 
     * @param format any of {@link BleConstants#GATT_FORMAT_RES},
     *            {@link BleConstants#GATT_FORMAT_BOOL},
     *            {@link BleConstants#GATT_FORMAT_2BITS},
     *            {@link BleConstants#GATT_FORMAT_NIBBLE},
     *            {@link BleConstants#GATT_FORMAT_UINT8},
     *            {@link BleConstants#GATT_FORMAT_UINT12},
     *            {@link BleConstants#GATT_FORMAT_UINT16},
     *            {@link BleConstants#GATT_FORMAT_UINT24},
     *            {@link BleConstants#GATT_FORMAT_UINT32},
     *            {@link BleConstants#GATT_FORMAT_UINT48},
     *            {@link BleConstants#GATT_FORMAT_UINT64},
     *            {@link BleConstants#GATT_FORMAT_UINT128},
     *            {@link BleConstants#GATT_FORMAT_SINT8},
     *            {@link BleConstants#GATT_FORMAT_SINT12},
     *            {@link BleConstants#GATT_FORMAT_SINT16},
     *            {@link BleConstants#GATT_FORMAT_SINT24},
     *            {@link BleConstants#GATT_FORMAT_SINT32},
     *            {@link BleConstants#GATT_FORMAT_SINT48},
     *            {@link BleConstants#GATT_FORMAT_SINT64},
     *            {@link BleConstants#GATT_FORMAT_SINT128},
     *            {@link BleConstants#GATT_FORMAT_FLOAT32},
     *            {@link BleConstants#GATT_FORMAT_FLOAT64},
     *            {@link BleConstants#GATT_FORMAT_SFLOAT},
     *            {@link BleConstants#GATT_FORMAT_FLOAT},
     *            {@link BleConstants#GATT_FORMAT_DUINT16},
     *            {@link BleConstants#GATT_FORMAT_UTF8S},
     *            {@link BleConstants#GATT_FORMAT_UTF16S},
     *            {@link BleConstants#GATT_FORMAT_STRUCT},
     *            {@link BleConstants#GATT_FORMAT_MAX}
     */
    public void setValueFormat(int format)
    {
        mFormat = format;
        mIsFixed = true;

        switch (format) {
            case BleConstants.GATT_FORMAT_BOOL:
            case BleConstants.GATT_FORMAT_2BITS:
            case BleConstants.GATT_FORMAT_NIBBLE:
            case BleConstants.GATT_FORMAT_UINT8:
            case BleConstants.GATT_FORMAT_SINT8:
                mMaxLength = 1;
                break;

            case BleConstants.GATT_FORMAT_UINT12:
            case BleConstants.GATT_FORMAT_UINT16:
            case BleConstants.GATT_FORMAT_SINT12:
            case BleConstants.GATT_FORMAT_SINT16:
            case BleConstants.GATT_FORMAT_SFLOAT:
                mMaxLength = 2;
                break;
                
            case BleConstants.GATT_FORMAT_UINT24:
            case BleConstants.GATT_FORMAT_SINT24:
                mMaxLength = 3;
                break;
                
            case BleConstants.GATT_FORMAT_UINT32:
            case BleConstants.GATT_FORMAT_SINT32:
            case BleConstants.GATT_FORMAT_FLOAT32:
            case BleConstants.GATT_FORMAT_FLOAT:
            case BleConstants.GATT_FORMAT_DUINT16:
                mMaxLength = 4;
                break;
                
            case BleConstants.GATT_FORMAT_UINT48:
            case BleConstants.GATT_FORMAT_SINT48:
                mMaxLength = 6;
                break;
                
            case BleConstants.GATT_FORMAT_UINT64:
            case BleConstants.GATT_FORMAT_SINT64:
            case BleConstants.GATT_FORMAT_FLOAT64:
                mMaxLength = 8;
                break;
                
            case BleConstants.GATT_FORMAT_UINT128:
            case BleConstants.GATT_FORMAT_SINT128:
                mMaxLength = 16;
                break;
                
            case BleConstants.GATT_FORMAT_UTF8S:
            case BleConstants.GATT_FORMAT_UTF16S:
            case BleConstants.GATT_FORMAT_STRUCT:
                mMaxLength = 100;
                mIsFixed = false;
                break;
                
            default:
                Log.e("BleAttribute", "Format not found");
                mFormat = 0;
                mMaxLength = 0;
                mIsFixed = true;
        }
    }

    /**
     * Indicates whether the attribute uses a fixed-length value format.
     */
    public boolean isFixedLength()
    {
        return mIsFixed;
    }

    /**
     * Returns the maximum length of the value represented in this attribute.
     */
    public int getMaxLength()
    {
        return mMaxLength;
    }

    /**
     * Sets the maximum allowable length for the value in this attribute. This
     * function should only be called for variable length attribute values.
     * 
     * @see {@link #isFixedLength()}
     */
    public void setMaxLength(String maxSizeValString)
    {
        if (!mIsFixed)
            mMaxLength = Integer.parseInt(maxSizeValString, 16);
        else
            Log.e("BleAttribute", "Format is fixed size. Ignore the MaxSize tag");
    }

    /**
     * Set the length of a variable length value. This value must not exceed the
     * maximum allowable length.
     * 
     * @see {@link #getMaxLength()}
     */
    public void setLength(int length)
    {
        mLength = length;
    }

    /**
     * Returns the length of the value for variable length value formats.
     */
    public int getLength()
    {
        return mLength;
    }

    /**
     * Gets the raw value bytes for this attribute.
     */
    public byte[] getValue()
    {
        if ((mLength > 0) && (mValue != null)) {
            byte[] rValue = new byte[mLength];
            System.arraycopy(mValue, 0, rValue, 0, mLength);
            return rValue;
        }
        Log.i("BleAttribute", "the value is not supported so null is returned");
        return null;
    }

    /**
     * Gets the raw value byte for this attribute.
     */
    public byte getValueByte()
    {
        if ((mLength > 0) && (mValue != null)) {
            return mValue[0];
        }

        Log.i("BleAttribute", "the value is not initialized -1 is returned");
        return BleConstants.GATT_UNDEFINED;
    }

    /**
     * Gets the raw int value for this attribute.
     */
    public int getValueInt()
    {
        if ((mLength > 0) && (mValue != null)) {
            int value = 0;
            for (int i = 0; i < mLength; i++) {
                int shift = (mLength - 1 - i) * 8;
                value += ((mValue[i] & 0xFF) << shift);
            }
            return value;
        }

        Log.i("BleAttribute", "the value is not initialized -1 is returned");
        return -1;
    }

    /**
     * Set the value for this attribute. These method signature can be used for
     * char value and user description, Setting the value for this attribute
     * marks this attribute as modified.
     */
    public byte setValue(byte[] value)
    {
        int length = value.length;
        if (length > mMaxLength) {
            length = mMaxLength;
        }
        System.arraycopy(value, 0, mValue, 0, length);
        mLength = length;
        mDirty = true;
        return 0;
    }

    /**
     * Set the value for this attribute. These method signature can be used for
     * client config, server config, unit, exponent, Setting the value for this
     * attribute marks this attribute as modified.
     */
    public byte setValue(int value)
    {
        int length = 4;
        byte[] valueByte = null;

        if (length > mMaxLength) {
            length = mMaxLength;
            return -123;
        }

        mLength = length;
        valueByte = new byte[length];

        for (int i = 0; i < mLength; i++) {
            int offset = (mLength - 1 - i) * 8;
            valueByte[i] = (byte) (value >>> offset & 0xFF);
        }

        System.arraycopy(valueByte, 0, mValue, 0, mLength);
        mDirty = true;

        return 0;
    }

    /**
     * Set the value for this attribute. These method signature can be used for
     * extended property Setting the value for this attribute marks this
     * attribute as modified.
     */
    public byte setValue(byte value)
    {
        int length = 1;
        mValue[0] = value;
        mLength = length;
        mDirty = true;
        return 0;
    }

    /**
     * Set the value of this attribute given a variable length value (ex.
     * string).
     */
    public byte setValue(byte[] value, int length)
    {
        if (length > mMaxLength)
            length = mMaxLength;
        System.arraycopy(value, 0, mValue, 0, length);
        mLength = length;
        mDirty = true;
        return 0;
    }

    /**
     * Set the raw value bytes for this attribute starting at a given offset
     * 
     * @return {@link BleConstants#GATT_SUCCESS} if successful
     */
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

        if (gattUuid.equals(mID)) {
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

    /**
     * Sets the encryption key size.
     */
    public void setKeySize(int keySize)
    {
        this.mKeySize = keySize;
    }

    /**
     * Returns the permission bit mask.
     */
    public int getPermMask()
    {
        return this.mPermissionMask;
    }

    /**
     * Set permission bit mask for this attribute.
     */
    public void setPermMask(int permMask)
    {
        this.mPermissionMask = permMask;
        setPermission(permMask, this.mKeySize);
    }

    /**
     * Sets permissions including key size for this attribute.
     */
    public void setPermission(int permMask, int keySize)
    {
        int tempKeySize = 0;
        if (keySize > 0) {
            tempKeySize = keySize - BleAttribute.DEFAULT_KEYSIZE;
        }

        tempKeySize <<= 12;
        this.mPermission = (tempKeySize | permMask);
    }

    /**
     * Returns the combined key size/attribute permission value for this
     * attribute.
     */
    public int getPermission()
    {
        return this.mPermission;
    }

    /**
     * Sets the level of authentication required to read/write this attribute.
     */
    public void setAuthReq(byte AuthReq)
    {
        this.mAuthReq = AuthReq;
    }

    /**
     * Returns the level of authentication required to read or write this
     * attribute.
     */
    public byte getAuthReq()
    {
        return this.mAuthReq;
    }

    /**
     * Sets the write type for this attribute.
     * 
     * @param writeType {@link BleConstants#GATTC_TYPE_WRITE} or
     *            {@link BleConstants#GATTC_TYPE_WRITE_NO_RSP}
     */
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
    public int getWriteType()
    {
        return this.mWriteType;
    }

    /**
     * Returns whether this attribute has been registered with the Bluetooth
     * stack.
     */
    public boolean isRegistered()
    {
        return this.mHandleMap.get(this.mID) != null;
    }

    /**
     * Maps an attribute of this attribute to a handle value.
     * 
     * @see {@link #getValueByHandle(int)}
     */
    public void setHandle(int handle)
    {
        this.mHandle = handle;
    }

    /**
     * Returns a handle for this attribute ID.
     */
    public int getHandle()
    {
        if (this.mHandle > BleConstants.GATT_UNDEFINED) {
            return this.mHandle;
        }
        return BleConstants.GATT_UNDEFINED;
    }

    /**
     * Returns an attribute based on a previously assigned handle value.
     */
    public byte[] getValueByHandle(int handle)
    {
        if (this.mHandle == handle) {
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
