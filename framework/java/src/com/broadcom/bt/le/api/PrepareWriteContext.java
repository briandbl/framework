
package com.broadcom.bt.le.api;

public class PrepareWriteContext
{
    public int mOffset = -1;
    public byte[] mData = null;
    public int mHandle = -1;

    public PrepareWriteContext(int offset, byte[] data, int handle) {
        this.mOffset = offset;
        this.mData = data;
        this.mHandle = handle;
    }

}
