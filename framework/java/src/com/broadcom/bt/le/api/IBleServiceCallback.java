/*     */

package com.broadcom.bt.le.api;

/*     */
/*     */import android.os.Binder;
/*     */
import android.os.IBinder;
/*     */
import android.os.IInterface;
/*     */
import android.os.Parcel;
/*     */
import android.os.Parcelable.Creator;
/*     */
import android.os.RemoteException;
/*     */
import com.broadcom.bt.service.gatt.BluetoothGattID;

/*     */
/*     */public abstract interface IBleServiceCallback extends IInterface
/*     */{
    /*     */public abstract void onServiceCreated(byte paramByte, int paramInt)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onServiceRegistered(byte paramByte,
            BluetoothGattID paramBluetoothGattID)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onIncludedServiceAdded(byte paramByte, int paramInt)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onCharacteristicAdded(byte paramByte,
            BluetoothGattID paramBluetoothGattID, int paramInt)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onCharacteristicDescrAdded(byte paramByte,
            BluetoothGattID paramBluetoothGattID, int paramInt)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onServiceDeleted(byte paramByte)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onServiceStarted(byte paramByte)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onServiceStopped(byte paramByte)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onHandleValueIndicationCompleted(byte paramByte, int paramInt)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onHandleValueNotificationCompleted(byte paramByte, int paramInt)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onResponseSendCompleted(byte paramByte, int paramInt)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onAttributeRequestRead(String paramString, int paramInt1,
            int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onAttributeRequestWrite(String paramString, int paramInt1,
            int paramInt2, int paramInt3, boolean paramBoolean1, int paramInt4,
            boolean paramBoolean2, int paramInt5, byte[] paramArrayOfByte)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onAttributeExecWrite(String paramString, int paramInt1,
            int paramInt2, int paramInt3)
            /*     */throws RemoteException;

    /*     */
    /*     */public static abstract class Stub extends Binder
            /*     */implements IBleServiceCallback
    /*     */{
        /*     */private static final String DESCRIPTOR = "com.broadcom.bt.le.api.IBleServiceCallback";
        /*     */static final int TRANSACTION_onServiceCreated = 1;
        /*     */static final int TRANSACTION_onServiceRegistered = 2;
        /*     */static final int TRANSACTION_onIncludedServiceAdded = 3;
        /*     */static final int TRANSACTION_onCharacteristicAdded = 4;
        /*     */static final int TRANSACTION_onCharacteristicDescrAdded = 5;
        /*     */static final int TRANSACTION_onServiceDeleted = 6;
        /*     */static final int TRANSACTION_onServiceStarted = 7;
        /*     */static final int TRANSACTION_onServiceStopped = 8;
        /*     */static final int TRANSACTION_onHandleValueIndicationCompleted = 9;
        /*     */static final int TRANSACTION_onHandleValueNotificationCompleted = 10;
        /*     */static final int TRANSACTION_onResponseSendCompleted = 11;
        /*     */static final int TRANSACTION_onAttributeRequestRead = 12;
        /*     */static final int TRANSACTION_onAttributeRequestWrite = 13;
        /*     */static final int TRANSACTION_onAttributeExecWrite = 14;

        /*     */
        /*     */public Stub()
        /*     */{
            /* 18 */attachInterface(this, "com.broadcom.bt.le.api.IBleServiceCallback");
            /*     */}

        /*     */
        /*     */public static IBleServiceCallback asInterface(IBinder obj)
        /*     */{
            /* 26 */if (obj == null) {
                /* 27 */return null;
                /*     */}
            /* 29 */IInterface iin = obj
                    .queryLocalInterface("com.broadcom.bt.le.api.IBleServiceCallback");
            /* 30 */if ((iin != null) && ((iin instanceof IBleServiceCallback))) {
                /* 31 */return (IBleServiceCallback) iin;
                /*     */}
            /* 33 */return new Proxy(obj);
            /*     */}

        /*     */
        /*     */public IBinder asBinder() {
            /* 37 */return this;
            /*     */}

        /*     */
        /*     */public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            /* 41 */switch (code)
            /*     */{
            /*     */case 1598968902:
                    /* 45 */reply.writeString("com.broadcom.bt.le.api.IBleServiceCallback");
                    /* 46 */return true;
                    /*     */case 1:
                    /* 50 */data.enforceInterface("com.broadcom.bt.le.api.IBleServiceCallback");
                    /*     */
                    /* 52 */byte _arg0 = data.readByte();
                    /*     */
                    /* 54 */int _arg1 = data.readInt();
                    /* 55 */onServiceCreated(_arg0, _arg1);
                    /* 56 */reply.writeNoException();
                    /* 57 */return true;
                    /*     */case 2:
                    /* 61 */data.enforceInterface("com.broadcom.bt.le.api.IBleServiceCallback");
                    /*     */
                    /* 63 */byte _arg0 = data.readByte();
                    /*     */BluetoothGattID _arg1;
                    /*     */BluetoothGattID _arg1;
                    /* 65 */if (0 != data.readInt()) {
                        /* 66 */_arg1 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 69 */_arg1 = null;
                        /*     */}
                    /* 71 */onServiceRegistered(_arg0, _arg1);
                    /* 72 */reply.writeNoException();
                    /* 73 */return true;
                    /*     */case 3:
                    /* 77 */data.enforceInterface("com.broadcom.bt.le.api.IBleServiceCallback");
                    /*     */
                    /* 79 */byte _arg0 = data.readByte();
                    /*     */
                    /* 81 */int _arg1 = data.readInt();
                    /* 82 */onIncludedServiceAdded(_arg0, _arg1);
                    /* 83 */reply.writeNoException();
                    /* 84 */return true;
                    /*     */case 4:
                    /* 88 */data.enforceInterface("com.broadcom.bt.le.api.IBleServiceCallback");
                    /*     */
                    /* 90 */byte _arg0 = data.readByte();
                    /*     */BluetoothGattID _arg1;
                    /*     */BluetoothGattID _arg1;
                    /* 92 */if (0 != data.readInt()) {
                        /* 93 */_arg1 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 96 */_arg1 = null;
                        /*     */}
                    /*     */
                    /* 99 */int _arg2 = data.readInt();
                    /* 100 */onCharacteristicAdded(_arg0, _arg1, _arg2);
                    /* 101 */reply.writeNoException();
                    /* 102 */return true;
                    /*     */case 5:
                    /* 106 */data.enforceInterface("com.broadcom.bt.le.api.IBleServiceCallback");
                    /*     */
                    /* 108 */byte _arg0 = data.readByte();
                    /*     */BluetoothGattID _arg1;
                    /*     */BluetoothGattID _arg1;
                    /* 110 */if (0 != data.readInt()) {
                        /* 111 */_arg1 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 114 */_arg1 = null;
                        /*     */}
                    /*     */
                    /* 117 */int _arg2 = data.readInt();
                    /* 118 */onCharacteristicDescrAdded(_arg0, _arg1, _arg2);
                    /* 119 */reply.writeNoException();
                    /* 120 */return true;
                    /*     */case 6:
                    /* 124 */data.enforceInterface("com.broadcom.bt.le.api.IBleServiceCallback");
                    /*     */
                    /* 126 */byte _arg0 = data.readByte();
                    /* 127 */onServiceDeleted(_arg0);
                    /* 128 */reply.writeNoException();
                    /* 129 */return true;
                    /*     */case 7:
                    /* 133 */data.enforceInterface("com.broadcom.bt.le.api.IBleServiceCallback");
                    /*     */
                    /* 135 */byte _arg0 = data.readByte();
                    /* 136 */onServiceStarted(_arg0);
                    /* 137 */reply.writeNoException();
                    /* 138 */return true;
                    /*     */case 8:
                    /* 142 */data.enforceInterface("com.broadcom.bt.le.api.IBleServiceCallback");
                    /*     */
                    /* 144 */byte _arg0 = data.readByte();
                    /* 145 */onServiceStopped(_arg0);
                    /* 146 */reply.writeNoException();
                    /* 147 */return true;
                    /*     */case 9:
                    /* 151 */data.enforceInterface("com.broadcom.bt.le.api.IBleServiceCallback");
                    /*     */
                    /* 153 */byte _arg0 = data.readByte();
                    /*     */
                    /* 155 */int _arg1 = data.readInt();
                    /* 156 */onHandleValueIndicationCompleted(_arg0, _arg1);
                    /* 157 */reply.writeNoException();
                    /* 158 */return true;
                    /*     */case 10:
                    /* 162 */data.enforceInterface("com.broadcom.bt.le.api.IBleServiceCallback");
                    /*     */
                    /* 164 */byte _arg0 = data.readByte();
                    /*     */
                    /* 166 */int _arg1 = data.readInt();
                    /* 167 */onHandleValueNotificationCompleted(_arg0, _arg1);
                    /* 168 */reply.writeNoException();
                    /* 169 */return true;
                    /*     */case 11:
                    /* 173 */data.enforceInterface("com.broadcom.bt.le.api.IBleServiceCallback");
                    /*     */
                    /* 175 */byte _arg0 = data.readByte();
                    /*     */
                    /* 177 */int _arg1 = data.readInt();
                    /* 178 */onResponseSendCompleted(_arg0, _arg1);
                    /* 179 */reply.writeNoException();
                    /* 180 */return true;
                    /*     */case 12:
                    /* 184 */data.enforceInterface("com.broadcom.bt.le.api.IBleServiceCallback");
                    /*     */
                    /* 186 */String _arg0 = data.readString();
                    /*     */
                    /* 188 */int _arg1 = data.readInt();
                    /*     */
                    /* 190 */int _arg2 = data.readInt();
                    /*     */
                    /* 192 */int _arg3 = data.readInt();
                    /*     */
                    /* 194 */int _arg4 = data.readInt();
                    /*     */
                    /* 196 */boolean _arg5 = 0 != data.readInt();
                    /* 197 */onAttributeRequestRead(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                    /* 198 */reply.writeNoException();
                    /* 199 */return true;
                    /*     */case 13:
                    /* 203 */data.enforceInterface("com.broadcom.bt.le.api.IBleServiceCallback");
                    /*     */
                    /* 205 */String _arg0 = data.readString();
                    /*     */
                    /* 207 */int _arg1 = data.readInt();
                    /*     */
                    /* 209 */int _arg2 = data.readInt();
                    /*     */
                    /* 211 */int _arg3 = data.readInt();
                    /*     */
                    /* 213 */boolean _arg4 = 0 != data.readInt();
                    /*     */
                    /* 215 */int _arg5 = data.readInt();
                    /*     */
                    /* 217 */boolean _arg6 = 0 != data.readInt();
                    /*     */
                    /* 219 */int _arg7 = data.readInt();
                    /*     */
                    /* 221 */byte[] _arg8 = data.createByteArray();
                    /* 222 */onAttributeRequestWrite(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5,
                            _arg6, _arg7, _arg8);
                    /* 223 */reply.writeNoException();
                    /* 224 */return true;
                    /*     */case 14:
                    /* 228 */data.enforceInterface("com.broadcom.bt.le.api.IBleServiceCallback");
                    /*     */
                    /* 230 */String _arg0 = data.readString();
                    /*     */
                    /* 232 */int _arg1 = data.readInt();
                    /*     */
                    /* 234 */int _arg2 = data.readInt();
                    /*     */
                    /* 236 */int _arg3 = data.readInt();
                    /* 237 */onAttributeExecWrite(_arg0, _arg1, _arg2, _arg3);
                    /* 238 */reply.writeNoException();
                    /* 239 */return true;
                    /*     */}
            /*     */
            /* 242 */return super.onTransact(code, data, reply, flags);
            /*     */}

        /*     */private static class Proxy implements IBleServiceCallback {
            /*     */private IBinder mRemote;

            /*     */
            /*     */Proxy(IBinder remote) {
                /* 249 */this.mRemote = remote;
                /*     */}

            /*     */
            /*     */public IBinder asBinder() {
                /* 253 */return this.mRemote;
                /*     */}

            /*     */
            /*     */public String getInterfaceDescriptor() {
                /* 257 */return "com.broadcom.bt.le.api.IBleServiceCallback";
                /*     */}

            /*     */
            /*     */public void onServiceCreated(byte status, int svcHandle) throws RemoteException {
                /* 261 */Parcel _data = Parcel.obtain();
                /* 262 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 264 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleServiceCallback");
                    /* 265 */_data.writeByte(status);
                    /* 266 */_data.writeInt(svcHandle);
                    /* 267 */this.mRemote.transact(1, _data, _reply, 0);
                    /* 268 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 271 */_reply.recycle();
                    /* 272 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onServiceRegistered(byte status, BluetoothGattID svcId)
                    throws RemoteException {
                /* 277 */Parcel _data = Parcel.obtain();
                /* 278 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 280 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleServiceCallback");
                    /* 281 */_data.writeByte(status);
                    /* 282 */if (svcId != null) {
                        /* 283 */_data.writeInt(1);
                        /* 284 */svcId.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 287 */_data.writeInt(0);
                        /*     */}
                    /* 289 */this.mRemote.transact(2, _data, _reply, 0);
                    /* 290 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 293 */_reply.recycle();
                    /* 294 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onIncludedServiceAdded(byte status, int incSvcHandle)
                    throws RemoteException {
                /* 299 */Parcel _data = Parcel.obtain();
                /* 300 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 302 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleServiceCallback");
                    /* 303 */_data.writeByte(status);
                    /* 304 */_data.writeInt(incSvcHandle);
                    /* 305 */this.mRemote.transact(3, _data, _reply, 0);
                    /* 306 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 309 */_reply.recycle();
                    /* 310 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onCharacteristicAdded(byte status, BluetoothGattID charId, int charHdl)
                    throws RemoteException {
                /* 315 */Parcel _data = Parcel.obtain();
                /* 316 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 318 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleServiceCallback");
                    /* 319 */_data.writeByte(status);
                    /* 320 */if (charId != null) {
                        /* 321 */_data.writeInt(1);
                        /* 322 */charId.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 325 */_data.writeInt(0);
                        /*     */}
                    /* 327 */_data.writeInt(charHdl);
                    /* 328 */this.mRemote.transact(4, _data, _reply, 0);
                    /* 329 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 332 */_reply.recycle();
                    /* 333 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onCharacteristicDescrAdded(byte status, BluetoothGattID charId,
                    int chardescHdl) throws RemoteException {
                /* 338 */Parcel _data = Parcel.obtain();
                /* 339 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 341 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleServiceCallback");
                    /* 342 */_data.writeByte(status);
                    /* 343 */if (charId != null) {
                        /* 344 */_data.writeInt(1);
                        /* 345 */charId.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 348 */_data.writeInt(0);
                        /*     */}
                    /* 350 */_data.writeInt(chardescHdl);
                    /* 351 */this.mRemote.transact(5, _data, _reply, 0);
                    /* 352 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 355 */_reply.recycle();
                    /* 356 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onServiceDeleted(byte status) throws RemoteException {
                /* 361 */Parcel _data = Parcel.obtain();
                /* 362 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 364 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleServiceCallback");
                    /* 365 */_data.writeByte(status);
                    /* 366 */this.mRemote.transact(6, _data, _reply, 0);
                    /* 367 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 370 */_reply.recycle();
                    /* 371 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onServiceStarted(byte status) throws RemoteException {
                /* 376 */Parcel _data = Parcel.obtain();
                /* 377 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 379 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleServiceCallback");
                    /* 380 */_data.writeByte(status);
                    /* 381 */this.mRemote.transact(7, _data, _reply, 0);
                    /* 382 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 385 */_reply.recycle();
                    /* 386 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onServiceStopped(byte status) throws RemoteException {
                /* 391 */Parcel _data = Parcel.obtain();
                /* 392 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 394 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleServiceCallback");
                    /* 395 */_data.writeByte(status);
                    /* 396 */this.mRemote.transact(8, _data, _reply, 0);
                    /* 397 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 400 */_reply.recycle();
                    /* 401 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onHandleValueIndicationCompleted(byte status, int attrHandle)
                    throws RemoteException {
                /* 406 */Parcel _data = Parcel.obtain();
                /* 407 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 409 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleServiceCallback");
                    /* 410 */_data.writeByte(status);
                    /* 411 */_data.writeInt(attrHandle);
                    /* 412 */this.mRemote.transact(9, _data, _reply, 0);
                    /* 413 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 416 */_reply.recycle();
                    /* 417 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onHandleValueNotificationCompleted(byte status, int attrHandle)
                    throws RemoteException {
                /* 422 */Parcel _data = Parcel.obtain();
                /* 423 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 425 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleServiceCallback");
                    /* 426 */_data.writeByte(status);
                    /* 427 */_data.writeInt(attrHandle);
                    /* 428 */this.mRemote.transact(10, _data, _reply, 0);
                    /* 429 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 432 */_reply.recycle();
                    /* 433 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onResponseSendCompleted(byte status, int attrHandle)
                    throws RemoteException {
                /* 438 */Parcel _data = Parcel.obtain();
                /* 439 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 441 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleServiceCallback");
                    /* 442 */_data.writeByte(status);
                    /* 443 */_data.writeInt(attrHandle);
                    /* 444 */this.mRemote.transact(11, _data, _reply, 0);
                    /* 445 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 448 */_reply.recycle();
                    /* 449 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onAttributeRequestRead(String address, int connId, int transId,
                    int attrHandle, int offset, boolean isLong) throws RemoteException {
                /* 454 */Parcel _data = Parcel.obtain();
                /* 455 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 457 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleServiceCallback");
                    /* 458 */_data.writeString(address);
                    /* 459 */_data.writeInt(connId);
                    /* 460 */_data.writeInt(transId);
                    /* 461 */_data.writeInt(attrHandle);
                    /* 462 */_data.writeInt(offset);
                    /* 463 */_data.writeInt(isLong ? 1 : 0);
                    /* 464 */this.mRemote.transact(12, _data, _reply, 0);
                    /* 465 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 468 */_reply.recycle();
                    /* 469 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onAttributeRequestWrite(String address, int connId, int transId,
                    int attrHandle, boolean isPrep, int len, boolean needRsp, int offset,
                    byte[] data) throws RemoteException {
                /* 474 */Parcel _data = Parcel.obtain();
                /* 475 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 477 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleServiceCallback");
                    /* 478 */_data.writeString(address);
                    /* 479 */_data.writeInt(connId);
                    /* 480 */_data.writeInt(transId);
                    /* 481 */_data.writeInt(attrHandle);
                    /* 482 */_data.writeInt(isPrep ? 1 : 0);
                    /* 483 */_data.writeInt(len);
                    /* 484 */_data.writeInt(needRsp ? 1 : 0);
                    /* 485 */_data.writeInt(offset);
                    /* 486 */_data.writeByteArray(data);
                    /* 487 */this.mRemote.transact(13, _data, _reply, 0);
                    /* 488 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 491 */_reply.recycle();
                    /* 492 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onAttributeExecWrite(String address, int connId, int transId,
                    int execWrite) throws RemoteException {
                /* 497 */Parcel _data = Parcel.obtain();
                /* 498 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 500 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleServiceCallback");
                    /* 501 */_data.writeString(address);
                    /* 502 */_data.writeInt(connId);
                    /* 503 */_data.writeInt(transId);
                    /* 504 */_data.writeInt(execWrite);
                    /* 505 */this.mRemote.transact(14, _data, _reply, 0);
                    /* 506 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 509 */_reply.recycle();
                    /* 510 */_data.recycle();
                    /*     */}
                /*     */}
            /*     */
        }
        /*     */
    }
    /*     */
}

/*
 * Location:
 * /opt/android/sdk/add-ons/addon-open_bluetooth_low-energy_api-broadcom
 * -10/libs/com.broadcom.bt.le.jar Qualified Name:
 * com.broadcom.bt.le.api.IBleServiceCallback JD-Core Version: 0.6.0
 */
