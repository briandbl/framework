/*      */ package com.broadcom.bt.service.gatt;
/*      */ 
/*      */ import android.os.Binder;
/*      */ import android.os.IBinder;
/*      */ import android.os.IInterface;
/*      */ import android.os.Parcel;
/*      */ import android.os.Parcelable.Creator;
/*      */ import android.os.RemoteException;
/*      */ import com.broadcom.bt.le.api.IBleCharacteristicDataCallback;
/*      */ import com.broadcom.bt.le.api.IBleCharacteristicDataCallback.Stub;
/*      */ import com.broadcom.bt.le.api.IBleClientCallback;
/*      */ import com.broadcom.bt.le.api.IBleClientCallback.Stub;
/*      */ import com.broadcom.bt.le.api.IBleProfileEventCallback;
/*      */ import com.broadcom.bt.le.api.IBleProfileEventCallback.Stub;
/*      */ import com.broadcom.bt.le.api.IBleServiceCallback;
/*      */ import com.broadcom.bt.le.api.IBleServiceCallback.Stub;
/*      */ 
/*      */ public abstract interface IBluetoothGatt extends IInterface
/*      */ {
/*      */   public abstract void registerApp(BluetoothGattID paramBluetoothGattID, IBleClientCallback paramIBleClientCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void unregisterApp(byte paramByte)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void setEncryption(String paramString, byte paramByte)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void setScanParameters(int paramInt1, int paramInt2)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void filterEnable(boolean paramBoolean)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void filterEnableBDA(boolean paramBoolean, int paramInt, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void clearManufacturerData()
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void filterManufacturerData(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void filterManufacturerDataBDA(int paramInt1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, boolean paramBoolean, int paramInt2, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void observe(boolean paramBoolean, int paramInt)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void open(byte paramByte, String paramString, boolean paramBoolean)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void close(byte paramByte, String paramString, int paramInt, boolean paramBoolean)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void registerServiceDataCallback(int paramInt, BluetoothGattID paramBluetoothGattID, String paramString, IBleCharacteristicDataCallback paramIBleCharacteristicDataCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void searchService(int paramInt, BluetoothGattID paramBluetoothGattID)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getFirstChar(int paramInt, BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getNextChar(int paramInt, BluetoothGattCharID paramBluetoothGattCharID, BluetoothGattID paramBluetoothGattID)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getFirstCharDescr(int paramInt, BluetoothGattCharID paramBluetoothGattCharID, BluetoothGattID paramBluetoothGattID)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getNextCharDescr(int paramInt, BluetoothGattCharDescrID paramBluetoothGattCharDescrID, BluetoothGattID paramBluetoothGattID)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getFirstIncludedService(int paramInt, BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getNextIncludedService(int paramInt, BluetoothGattInclSrvcID paramBluetoothGattInclSrvcID, BluetoothGattID paramBluetoothGattID)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void readChar(int paramInt, BluetoothGattCharID paramBluetoothGattCharID, byte paramByte)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void readCharDescr(int paramInt, BluetoothGattCharDescrID paramBluetoothGattCharDescrID, byte paramByte)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void writeCharValue(int paramInt1, BluetoothGattCharID paramBluetoothGattCharID, int paramInt2, byte paramByte, byte[] paramArrayOfByte)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void writeCharDescrValue(int paramInt1, BluetoothGattCharDescrID paramBluetoothGattCharDescrID, int paramInt2, byte paramByte, byte[] paramArrayOfByte)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void sendIndConfirm(int paramInt, BluetoothGattCharID paramBluetoothGattCharID)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void prepareWrite(int paramInt1, BluetoothGattCharID paramBluetoothGattCharID, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void executeWrite(int paramInt, boolean paramBoolean)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void registerForNotifications(byte paramByte, String paramString, BluetoothGattCharID paramBluetoothGattCharID)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void deregisterForNotifications(byte paramByte, String paramString, BluetoothGattCharID paramBluetoothGattCharID)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void registerServerServiceCallback(BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2, IBleServiceCallback paramIBleServiceCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void registerServerProfileCallback(BluetoothGattID paramBluetoothGattID, IBleProfileEventCallback paramIBleProfileEventCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void unregisterServerServiceCallback(int paramInt)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void unregisterServerProfileCallback(int paramInt)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void GATTServer_CreateService(byte paramByte, BluetoothGattID paramBluetoothGattID, int paramInt)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void GATTServer_AddIncludedService(int paramInt1, int paramInt2)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void GATTServer_AddCharacteristic(int paramInt1, BluetoothGattID paramBluetoothGattID, int paramInt2, int paramInt3, boolean paramBoolean, int paramInt4)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void GATTServer_AddCharDescriptor(int paramInt1, int paramInt2, BluetoothGattID paramBluetoothGattID)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void GATTServer_DeleteService(int paramInt)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void GATTServer_StartService(int paramInt, byte paramByte)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void GATTServer_StopService(int paramInt)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void GATTServer_HandleValueIndication(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void GATTServer_HandleValueNotification(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void GATTServer_SendRsp(int paramInt1, int paramInt2, byte paramByte1, int paramInt3, int paramInt4, byte[] paramArrayOfByte, byte paramByte2, boolean paramBoolean)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void GATTServer_Open(byte paramByte, String paramString, boolean paramBoolean)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void GATTServer_CancelOpen(byte paramByte, String paramString, boolean paramBoolean)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void GATTServer_Close(int paramInt)
/*      */     throws RemoteException;
/*      */ 
/*      */   public static abstract class Stub extends Binder
/*      */     implements IBluetoothGatt
/*      */   {
/*      */     private static final String DESCRIPTOR = "com.broadcom.bt.service.gatt.IBluetoothGatt";
/*      */     static final int TRANSACTION_registerApp = 1;
/*      */     static final int TRANSACTION_unregisterApp = 2;
/*      */     static final int TRANSACTION_setEncryption = 3;
/*      */     static final int TRANSACTION_setScanParameters = 4;
/*      */     static final int TRANSACTION_filterEnable = 5;
/*      */     static final int TRANSACTION_filterEnableBDA = 6;
/*      */     static final int TRANSACTION_clearManufacturerData = 7;
/*      */     static final int TRANSACTION_filterManufacturerData = 8;
/*      */     static final int TRANSACTION_filterManufacturerDataBDA = 9;
/*      */     static final int TRANSACTION_observe = 10;
/*      */     static final int TRANSACTION_open = 11;
/*      */     static final int TRANSACTION_close = 12;
/*      */     static final int TRANSACTION_registerServiceDataCallback = 13;
/*      */     static final int TRANSACTION_searchService = 14;
/*      */     static final int TRANSACTION_getFirstChar = 15;
/*      */     static final int TRANSACTION_getNextChar = 16;
/*      */     static final int TRANSACTION_getFirstCharDescr = 17;
/*      */     static final int TRANSACTION_getNextCharDescr = 18;
/*      */     static final int TRANSACTION_getFirstIncludedService = 19;
/*      */     static final int TRANSACTION_getNextIncludedService = 20;
/*      */     static final int TRANSACTION_readChar = 21;
/*      */     static final int TRANSACTION_readCharDescr = 22;
/*      */     static final int TRANSACTION_writeCharValue = 23;
/*      */     static final int TRANSACTION_writeCharDescrValue = 24;
/*      */     static final int TRANSACTION_sendIndConfirm = 25;
/*      */     static final int TRANSACTION_prepareWrite = 26;
/*      */     static final int TRANSACTION_executeWrite = 27;
/*      */     static final int TRANSACTION_registerForNotifications = 28;
/*      */     static final int TRANSACTION_deregisterForNotifications = 29;
/*      */     static final int TRANSACTION_registerServerServiceCallback = 30;
/*      */     static final int TRANSACTION_registerServerProfileCallback = 31;
/*      */     static final int TRANSACTION_unregisterServerServiceCallback = 32;
/*      */     static final int TRANSACTION_unregisterServerProfileCallback = 33;
/*      */     static final int TRANSACTION_GATTServer_CreateService = 34;
/*      */     static final int TRANSACTION_GATTServer_AddIncludedService = 35;
/*      */     static final int TRANSACTION_GATTServer_AddCharacteristic = 36;
/*      */     static final int TRANSACTION_GATTServer_AddCharDescriptor = 37;
/*      */     static final int TRANSACTION_GATTServer_DeleteService = 38;
/*      */     static final int TRANSACTION_GATTServer_StartService = 39;
/*      */     static final int TRANSACTION_GATTServer_StopService = 40;
/*      */     static final int TRANSACTION_GATTServer_HandleValueIndication = 41;
/*      */     static final int TRANSACTION_GATTServer_HandleValueNotification = 42;
/*      */     static final int TRANSACTION_GATTServer_SendRsp = 43;
/*      */     static final int TRANSACTION_GATTServer_Open = 44;
/*      */     static final int TRANSACTION_GATTServer_CancelOpen = 45;
/*      */     static final int TRANSACTION_GATTServer_Close = 46;
/*      */ 
/*      */     public Stub()
/*      */     {
/*   19 */       attachInterface(this, "com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */     }
/*      */ 
/*      */     public static IBluetoothGatt asInterface(IBinder obj)
/*      */     {
/*   27 */       if (obj == null) {
/*   28 */         return null;
/*      */       }
/*   30 */       IInterface iin = obj.queryLocalInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*   31 */       if ((iin != null) && ((iin instanceof IBluetoothGatt))) {
/*   32 */         return (IBluetoothGatt)iin;
/*      */       }
/*   34 */       return new Proxy(obj);
/*      */     }
/*      */ 
/*      */     public IBinder asBinder() {
/*   38 */       return this;
/*      */     }
/*      */ 
/*      */     public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
/*   42 */       switch (code)
/*      */       {
/*      */       case 1598968902:
/*   46 */         reply.writeString("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*   47 */         return true;
/*      */       case 1:
/*   51 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */         BluetoothGattID _arg0;
/*      */         BluetoothGattID _arg0;
/*   53 */         if (0 != data.readInt()) {
/*   54 */           _arg0 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*   57 */           _arg0 = null;
/*      */         }
/*      */ 
/*   60 */         IBleClientCallback _arg1 = IBleClientCallback.Stub.asInterface(data.readStrongBinder());
/*   61 */         registerApp(_arg0, _arg1);
/*   62 */         reply.writeNoException();
/*   63 */         return true;
/*      */       case 2:
/*   67 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*   69 */         byte _arg0 = data.readByte();
/*   70 */         unregisterApp(_arg0);
/*   71 */         reply.writeNoException();
/*   72 */         return true;
/*      */       case 3:
/*   76 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*   78 */         String _arg0 = data.readString();
/*      */ 
/*   80 */         byte _arg1 = data.readByte();
/*   81 */         setEncryption(_arg0, _arg1);
/*   82 */         reply.writeNoException();
/*   83 */         return true;
/*      */       case 4:
/*   87 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*   89 */         int _arg0 = data.readInt();
/*      */ 
/*   91 */         int _arg1 = data.readInt();
/*   92 */         setScanParameters(_arg0, _arg1);
/*   93 */         reply.writeNoException();
/*   94 */         return true;
/*      */       case 5:
/*   98 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  100 */         boolean _arg0 = 0 != data.readInt();
/*  101 */         filterEnable(_arg0);
/*  102 */         reply.writeNoException();
/*  103 */         return true;
/*      */       case 6:
/*  107 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  109 */         boolean _arg0 = 0 != data.readInt();
/*      */ 
/*  111 */         int _arg1 = data.readInt();
/*      */ 
/*  113 */         String _arg2 = data.readString();
/*  114 */         filterEnableBDA(_arg0, _arg1, _arg2);
/*  115 */         reply.writeNoException();
/*  116 */         return true;
/*      */       case 7:
/*  120 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*  121 */         clearManufacturerData();
/*  122 */         reply.writeNoException();
/*  123 */         return true;
/*      */       case 8:
/*  127 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  129 */         int _arg0 = data.readInt();
/*      */ 
/*  131 */         byte[] _arg1 = data.createByteArray();
/*      */ 
/*  133 */         byte[] _arg2 = data.createByteArray();
/*      */ 
/*  135 */         byte[] _arg3 = data.createByteArray();
/*      */ 
/*  137 */         byte[] _arg4 = data.createByteArray();
/*  138 */         filterManufacturerData(_arg0, _arg1, _arg2, _arg3, _arg4);
/*  139 */         reply.writeNoException();
/*  140 */         return true;
/*      */       case 9:
/*  144 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  146 */         int _arg0 = data.readInt();
/*      */ 
/*  148 */         byte[] _arg1 = data.createByteArray();
/*      */ 
/*  150 */         byte[] _arg2 = data.createByteArray();
/*      */ 
/*  152 */         byte[] _arg3 = data.createByteArray();
/*      */ 
/*  154 */         byte[] _arg4 = data.createByteArray();
/*      */ 
/*  156 */         boolean _arg5 = 0 != data.readInt();
/*      */ 
/*  158 */         int _arg6 = data.readInt();
/*      */ 
/*  160 */         String _arg7 = data.readString();
/*  161 */         filterManufacturerDataBDA(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
/*  162 */         reply.writeNoException();
/*  163 */         return true;
/*      */       case 10:
/*  167 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  169 */         boolean _arg0 = 0 != data.readInt();
/*      */ 
/*  171 */         int _arg1 = data.readInt();
/*  172 */         observe(_arg0, _arg1);
/*  173 */         reply.writeNoException();
/*  174 */         return true;
/*      */       case 11:
/*  178 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  180 */         byte _arg0 = data.readByte();
/*      */ 
/*  182 */         String _arg1 = data.readString();
/*      */ 
/*  184 */         boolean _arg2 = 0 != data.readInt();
/*  185 */         open(_arg0, _arg1, _arg2);
/*  186 */         reply.writeNoException();
/*  187 */         return true;
/*      */       case 12:
/*  191 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  193 */         byte _arg0 = data.readByte();
/*      */ 
/*  195 */         String _arg1 = data.readString();
/*      */ 
/*  197 */         int _arg2 = data.readInt();
/*      */ 
/*  199 */         boolean _arg3 = 0 != data.readInt();
/*  200 */         close(_arg0, _arg1, _arg2, _arg3);
/*  201 */         reply.writeNoException();
/*  202 */         return true;
/*      */       case 13:
/*  206 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  208 */         int _arg0 = data.readInt();
/*      */         BluetoothGattID _arg1;
/*      */         BluetoothGattID _arg1;
/*  210 */         if (0 != data.readInt()) {
/*  211 */           _arg1 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  214 */           _arg1 = null;
/*      */         }
/*      */ 
/*  217 */         String _arg2 = data.readString();
/*      */ 
/*  219 */         IBleCharacteristicDataCallback _arg3 = IBleCharacteristicDataCallback.Stub.asInterface(data.readStrongBinder());
/*  220 */         registerServiceDataCallback(_arg0, _arg1, _arg2, _arg3);
/*  221 */         reply.writeNoException();
/*  222 */         return true;
/*      */       case 14:
/*  226 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  228 */         int _arg0 = data.readInt();
/*      */         BluetoothGattID _arg1;
/*      */         BluetoothGattID _arg1;
/*  230 */         if (0 != data.readInt()) {
/*  231 */           _arg1 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  234 */           _arg1 = null;
/*      */         }
/*  236 */         searchService(_arg0, _arg1);
/*  237 */         reply.writeNoException();
/*  238 */         return true;
/*      */       case 15:
/*  242 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  244 */         int _arg0 = data.readInt();
/*      */         BluetoothGattID _arg1;
/*      */         BluetoothGattID _arg1;
/*  246 */         if (0 != data.readInt()) {
/*  247 */           _arg1 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else
/*  250 */           _arg1 = null;
/*      */         BluetoothGattID _arg2;
/*      */         BluetoothGattID _arg2;
/*  253 */         if (0 != data.readInt()) {
/*  254 */           _arg2 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  257 */           _arg2 = null;
/*      */         }
/*  259 */         getFirstChar(_arg0, _arg1, _arg2);
/*  260 */         reply.writeNoException();
/*  261 */         return true;
/*      */       case 16:
/*  265 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  267 */         int _arg0 = data.readInt();
/*      */         BluetoothGattCharID _arg1;
/*      */         BluetoothGattCharID _arg1;
/*  269 */         if (0 != data.readInt()) {
/*  270 */           _arg1 = (BluetoothGattCharID)BluetoothGattCharID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else
/*  273 */           _arg1 = null;
/*      */         BluetoothGattID _arg2;
/*      */         BluetoothGattID _arg2;
/*  276 */         if (0 != data.readInt()) {
/*  277 */           _arg2 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  280 */           _arg2 = null;
/*      */         }
/*  282 */         getNextChar(_arg0, _arg1, _arg2);
/*  283 */         reply.writeNoException();
/*  284 */         return true;
/*      */       case 17:
/*  288 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  290 */         int _arg0 = data.readInt();
/*      */         BluetoothGattCharID _arg1;
/*      */         BluetoothGattCharID _arg1;
/*  292 */         if (0 != data.readInt()) {
/*  293 */           _arg1 = (BluetoothGattCharID)BluetoothGattCharID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else
/*  296 */           _arg1 = null;
/*      */         BluetoothGattID _arg2;
/*      */         BluetoothGattID _arg2;
/*  299 */         if (0 != data.readInt()) {
/*  300 */           _arg2 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  303 */           _arg2 = null;
/*      */         }
/*  305 */         getFirstCharDescr(_arg0, _arg1, _arg2);
/*  306 */         reply.writeNoException();
/*  307 */         return true;
/*      */       case 18:
/*  311 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  313 */         int _arg0 = data.readInt();
/*      */         BluetoothGattCharDescrID _arg1;
/*      */         BluetoothGattCharDescrID _arg1;
/*  315 */         if (0 != data.readInt()) {
/*  316 */           _arg1 = (BluetoothGattCharDescrID)BluetoothGattCharDescrID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else
/*  319 */           _arg1 = null;
/*      */         BluetoothGattID _arg2;
/*      */         BluetoothGattID _arg2;
/*  322 */         if (0 != data.readInt()) {
/*  323 */           _arg2 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  326 */           _arg2 = null;
/*      */         }
/*  328 */         getNextCharDescr(_arg0, _arg1, _arg2);
/*  329 */         reply.writeNoException();
/*  330 */         return true;
/*      */       case 19:
/*  334 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  336 */         int _arg0 = data.readInt();
/*      */         BluetoothGattID _arg1;
/*      */         BluetoothGattID _arg1;
/*  338 */         if (0 != data.readInt()) {
/*  339 */           _arg1 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else
/*  342 */           _arg1 = null;
/*      */         BluetoothGattID _arg2;
/*      */         BluetoothGattID _arg2;
/*  345 */         if (0 != data.readInt()) {
/*  346 */           _arg2 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  349 */           _arg2 = null;
/*      */         }
/*  351 */         getFirstIncludedService(_arg0, _arg1, _arg2);
/*  352 */         reply.writeNoException();
/*  353 */         return true;
/*      */       case 20:
/*  357 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  359 */         int _arg0 = data.readInt();
/*      */         BluetoothGattInclSrvcID _arg1;
/*      */         BluetoothGattInclSrvcID _arg1;
/*  361 */         if (0 != data.readInt()) {
/*  362 */           _arg1 = (BluetoothGattInclSrvcID)BluetoothGattInclSrvcID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else
/*  365 */           _arg1 = null;
/*      */         BluetoothGattID _arg2;
/*      */         BluetoothGattID _arg2;
/*  368 */         if (0 != data.readInt()) {
/*  369 */           _arg2 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  372 */           _arg2 = null;
/*      */         }
/*  374 */         getNextIncludedService(_arg0, _arg1, _arg2);
/*  375 */         reply.writeNoException();
/*  376 */         return true;
/*      */       case 21:
/*  380 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  382 */         int _arg0 = data.readInt();
/*      */         BluetoothGattCharID _arg1;
/*      */         BluetoothGattCharID _arg1;
/*  384 */         if (0 != data.readInt()) {
/*  385 */           _arg1 = (BluetoothGattCharID)BluetoothGattCharID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  388 */           _arg1 = null;
/*      */         }
/*      */ 
/*  391 */         byte _arg2 = data.readByte();
/*  392 */         readChar(_arg0, _arg1, _arg2);
/*  393 */         reply.writeNoException();
/*  394 */         return true;
/*      */       case 22:
/*  398 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  400 */         int _arg0 = data.readInt();
/*      */         BluetoothGattCharDescrID _arg1;
/*      */         BluetoothGattCharDescrID _arg1;
/*  402 */         if (0 != data.readInt()) {
/*  403 */           _arg1 = (BluetoothGattCharDescrID)BluetoothGattCharDescrID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  406 */           _arg1 = null;
/*      */         }
/*      */ 
/*  409 */         byte _arg2 = data.readByte();
/*  410 */         readCharDescr(_arg0, _arg1, _arg2);
/*  411 */         reply.writeNoException();
/*  412 */         return true;
/*      */       case 23:
/*  416 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  418 */         int _arg0 = data.readInt();
/*      */         BluetoothGattCharID _arg1;
/*      */         BluetoothGattCharID _arg1;
/*  420 */         if (0 != data.readInt()) {
/*  421 */           _arg1 = (BluetoothGattCharID)BluetoothGattCharID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  424 */           _arg1 = null;
/*      */         }
/*      */ 
/*  427 */         int _arg2 = data.readInt();
/*      */ 
/*  429 */         byte _arg3 = data.readByte();
/*      */ 
/*  431 */         byte[] _arg4 = data.createByteArray();
/*  432 */         writeCharValue(_arg0, _arg1, _arg2, _arg3, _arg4);
/*  433 */         reply.writeNoException();
/*  434 */         return true;
/*      */       case 24:
/*  438 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  440 */         int _arg0 = data.readInt();
/*      */         BluetoothGattCharDescrID _arg1;
/*      */         BluetoothGattCharDescrID _arg1;
/*  442 */         if (0 != data.readInt()) {
/*  443 */           _arg1 = (BluetoothGattCharDescrID)BluetoothGattCharDescrID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  446 */           _arg1 = null;
/*      */         }
/*      */ 
/*  449 */         int _arg2 = data.readInt();
/*      */ 
/*  451 */         byte _arg3 = data.readByte();
/*      */ 
/*  453 */         byte[] _arg4 = data.createByteArray();
/*  454 */         writeCharDescrValue(_arg0, _arg1, _arg2, _arg3, _arg4);
/*  455 */         reply.writeNoException();
/*  456 */         return true;
/*      */       case 25:
/*  460 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  462 */         int _arg0 = data.readInt();
/*      */         BluetoothGattCharID _arg1;
/*      */         BluetoothGattCharID _arg1;
/*  464 */         if (0 != data.readInt()) {
/*  465 */           _arg1 = (BluetoothGattCharID)BluetoothGattCharID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  468 */           _arg1 = null;
/*      */         }
/*  470 */         sendIndConfirm(_arg0, _arg1);
/*  471 */         reply.writeNoException();
/*  472 */         return true;
/*      */       case 26:
/*  476 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  478 */         int _arg0 = data.readInt();
/*      */         BluetoothGattCharID _arg1;
/*      */         BluetoothGattCharID _arg1;
/*  480 */         if (0 != data.readInt()) {
/*  481 */           _arg1 = (BluetoothGattCharID)BluetoothGattCharID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  484 */           _arg1 = null;
/*      */         }
/*      */ 
/*  487 */         int _arg2 = data.readInt();
/*      */ 
/*  489 */         int _arg3 = data.readInt();
/*      */ 
/*  491 */         byte[] _arg4 = data.createByteArray();
/*  492 */         prepareWrite(_arg0, _arg1, _arg2, _arg3, _arg4);
/*  493 */         reply.writeNoException();
/*  494 */         return true;
/*      */       case 27:
/*  498 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  500 */         int _arg0 = data.readInt();
/*      */ 
/*  502 */         boolean _arg1 = 0 != data.readInt();
/*  503 */         executeWrite(_arg0, _arg1);
/*  504 */         reply.writeNoException();
/*  505 */         return true;
/*      */       case 28:
/*  509 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  511 */         byte _arg0 = data.readByte();
/*      */ 
/*  513 */         String _arg1 = data.readString();
/*      */         BluetoothGattCharID _arg2;
/*      */         BluetoothGattCharID _arg2;
/*  515 */         if (0 != data.readInt()) {
/*  516 */           _arg2 = (BluetoothGattCharID)BluetoothGattCharID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  519 */           _arg2 = null;
/*      */         }
/*  521 */         registerForNotifications(_arg0, _arg1, _arg2);
/*  522 */         reply.writeNoException();
/*  523 */         return true;
/*      */       case 29:
/*  527 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  529 */         byte _arg0 = data.readByte();
/*      */ 
/*  531 */         String _arg1 = data.readString();
/*      */         BluetoothGattCharID _arg2;
/*      */         BluetoothGattCharID _arg2;
/*  533 */         if (0 != data.readInt()) {
/*  534 */           _arg2 = (BluetoothGattCharID)BluetoothGattCharID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  537 */           _arg2 = null;
/*      */         }
/*  539 */         deregisterForNotifications(_arg0, _arg1, _arg2);
/*  540 */         reply.writeNoException();
/*  541 */         return true;
/*      */       case 30:
/*  545 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */         BluetoothGattID _arg0;
/*      */         BluetoothGattID _arg0;
/*  547 */         if (0 != data.readInt()) {
/*  548 */           _arg0 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else
/*  551 */           _arg0 = null;
/*      */         BluetoothGattID _arg1;
/*      */         BluetoothGattID _arg1;
/*  554 */         if (0 != data.readInt()) {
/*  555 */           _arg1 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  558 */           _arg1 = null;
/*      */         }
/*      */ 
/*  561 */         IBleServiceCallback _arg2 = IBleServiceCallback.Stub.asInterface(data.readStrongBinder());
/*  562 */         registerServerServiceCallback(_arg0, _arg1, _arg2);
/*  563 */         reply.writeNoException();
/*  564 */         return true;
/*      */       case 31:
/*  568 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */         BluetoothGattID _arg0;
/*      */         BluetoothGattID _arg0;
/*  570 */         if (0 != data.readInt()) {
/*  571 */           _arg0 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  574 */           _arg0 = null;
/*      */         }
/*      */ 
/*  577 */         IBleProfileEventCallback _arg1 = IBleProfileEventCallback.Stub.asInterface(data.readStrongBinder());
/*  578 */         registerServerProfileCallback(_arg0, _arg1);
/*  579 */         reply.writeNoException();
/*  580 */         return true;
/*      */       case 32:
/*  584 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  586 */         int _arg0 = data.readInt();
/*  587 */         unregisterServerServiceCallback(_arg0);
/*  588 */         reply.writeNoException();
/*  589 */         return true;
/*      */       case 33:
/*  593 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  595 */         int _arg0 = data.readInt();
/*  596 */         unregisterServerProfileCallback(_arg0);
/*  597 */         reply.writeNoException();
/*  598 */         return true;
/*      */       case 34:
/*  602 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  604 */         byte _arg0 = data.readByte();
/*      */         BluetoothGattID _arg1;
/*      */         BluetoothGattID _arg1;
/*  606 */         if (0 != data.readInt()) {
/*  607 */           _arg1 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  610 */           _arg1 = null;
/*      */         }
/*      */ 
/*  613 */         int _arg2 = data.readInt();
/*  614 */         GATTServer_CreateService(_arg0, _arg1, _arg2);
/*  615 */         reply.writeNoException();
/*  616 */         return true;
/*      */       case 35:
/*  620 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  622 */         int _arg0 = data.readInt();
/*      */ 
/*  624 */         int _arg1 = data.readInt();
/*  625 */         GATTServer_AddIncludedService(_arg0, _arg1);
/*  626 */         reply.writeNoException();
/*  627 */         return true;
/*      */       case 36:
/*  631 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  633 */         int _arg0 = data.readInt();
/*      */         BluetoothGattID _arg1;
/*      */         BluetoothGattID _arg1;
/*  635 */         if (0 != data.readInt()) {
/*  636 */           _arg1 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  639 */           _arg1 = null;
/*      */         }
/*      */ 
/*  642 */         int _arg2 = data.readInt();
/*      */ 
/*  644 */         int _arg3 = data.readInt();
/*      */ 
/*  646 */         boolean _arg4 = 0 != data.readInt();
/*      */ 
/*  648 */         int _arg5 = data.readInt();
/*  649 */         GATTServer_AddCharacteristic(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
/*  650 */         reply.writeNoException();
/*  651 */         return true;
/*      */       case 37:
/*  655 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  657 */         int _arg0 = data.readInt();
/*      */ 
/*  659 */         int _arg1 = data.readInt();
/*      */         BluetoothGattID _arg2;
/*      */         BluetoothGattID _arg2;
/*  661 */         if (0 != data.readInt()) {
/*  662 */           _arg2 = (BluetoothGattID)BluetoothGattID.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  665 */           _arg2 = null;
/*      */         }
/*  667 */         GATTServer_AddCharDescriptor(_arg0, _arg1, _arg2);
/*  668 */         reply.writeNoException();
/*  669 */         return true;
/*      */       case 38:
/*  673 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  675 */         int _arg0 = data.readInt();
/*  676 */         GATTServer_DeleteService(_arg0);
/*  677 */         reply.writeNoException();
/*  678 */         return true;
/*      */       case 39:
/*  682 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  684 */         int _arg0 = data.readInt();
/*      */ 
/*  686 */         byte _arg1 = data.readByte();
/*  687 */         GATTServer_StartService(_arg0, _arg1);
/*  688 */         reply.writeNoException();
/*  689 */         return true;
/*      */       case 40:
/*  693 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  695 */         int _arg0 = data.readInt();
/*  696 */         GATTServer_StopService(_arg0);
/*  697 */         reply.writeNoException();
/*  698 */         return true;
/*      */       case 41:
/*  702 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  704 */         int _arg0 = data.readInt();
/*      */ 
/*  706 */         int _arg1 = data.readInt();
/*      */ 
/*  708 */         byte[] _arg2 = data.createByteArray();
/*  709 */         GATTServer_HandleValueIndication(_arg0, _arg1, _arg2);
/*  710 */         reply.writeNoException();
/*  711 */         return true;
/*      */       case 42:
/*  715 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  717 */         int _arg0 = data.readInt();
/*      */ 
/*  719 */         int _arg1 = data.readInt();
/*      */ 
/*  721 */         byte[] _arg2 = data.createByteArray();
/*  722 */         GATTServer_HandleValueNotification(_arg0, _arg1, _arg2);
/*  723 */         reply.writeNoException();
/*  724 */         return true;
/*      */       case 43:
/*  728 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  730 */         int _arg0 = data.readInt();
/*      */ 
/*  732 */         int _arg1 = data.readInt();
/*      */ 
/*  734 */         byte _arg2 = data.readByte();
/*      */ 
/*  736 */         int _arg3 = data.readInt();
/*      */ 
/*  738 */         int _arg4 = data.readInt();
/*      */ 
/*  740 */         byte[] _arg5 = data.createByteArray();
/*      */ 
/*  742 */         byte _arg6 = data.readByte();
/*      */ 
/*  744 */         boolean _arg7 = 0 != data.readInt();
/*  745 */         GATTServer_SendRsp(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
/*  746 */         reply.writeNoException();
/*  747 */         return true;
/*      */       case 44:
/*  751 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  753 */         byte _arg0 = data.readByte();
/*      */ 
/*  755 */         String _arg1 = data.readString();
/*      */ 
/*  757 */         boolean _arg2 = 0 != data.readInt();
/*  758 */         GATTServer_Open(_arg0, _arg1, _arg2);
/*  759 */         reply.writeNoException();
/*  760 */         return true;
/*      */       case 45:
/*  764 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  766 */         byte _arg0 = data.readByte();
/*      */ 
/*  768 */         String _arg1 = data.readString();
/*      */ 
/*  770 */         boolean _arg2 = 0 != data.readInt();
/*  771 */         GATTServer_CancelOpen(_arg0, _arg1, _arg2);
/*  772 */         reply.writeNoException();
/*  773 */         return true;
/*      */       case 46:
/*  777 */         data.enforceInterface("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*      */ 
/*  779 */         int _arg0 = data.readInt();
/*  780 */         GATTServer_Close(_arg0);
/*  781 */         reply.writeNoException();
/*  782 */         return true;
/*      */       }
/*      */ 
/*  785 */       return super.onTransact(code, data, reply, flags);
/*      */     }
/*      */     private static class Proxy implements IBluetoothGatt {
/*      */       private IBinder mRemote;
/*      */ 
/*      */       Proxy(IBinder remote) {
/*  792 */         this.mRemote = remote;
/*      */       }
/*      */ 
/*      */       public IBinder asBinder() {
/*  796 */         return this.mRemote;
/*      */       }
/*      */ 
/*      */       public String getInterfaceDescriptor() {
/*  800 */         return "com.broadcom.bt.service.gatt.IBluetoothGatt";
/*      */       }
/*      */ 
/*      */       public void registerApp(BluetoothGattID appID, IBleClientCallback callback) throws RemoteException {
/*  804 */         Parcel _data = Parcel.obtain();
/*  805 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/*  807 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*  808 */           if (appID != null) {
/*  809 */             _data.writeInt(1);
/*  810 */             appID.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/*  813 */             _data.writeInt(0);
/*      */           }
/*  815 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/*  816 */           this.mRemote.transact(1, _data, _reply, 0);
/*  817 */           _reply.readException();
/*      */         }
/*      */         finally {
/*  820 */           _reply.recycle();
/*  821 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void unregisterApp(byte clientIf) throws RemoteException {
/*  826 */         Parcel _data = Parcel.obtain();
/*  827 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/*  829 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*  830 */           _data.writeByte(clientIf);
/*  831 */           this.mRemote.transact(2, _data, _reply, 0);
/*  832 */           _reply.readException();
/*      */         }
/*      */         finally {
/*  835 */           _reply.recycle();
/*  836 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void setEncryption(String address, byte action) throws RemoteException {
/*  841 */         Parcel _data = Parcel.obtain();
/*  842 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/*  844 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*  845 */           _data.writeString(address);
/*  846 */           _data.writeByte(action);
/*  847 */           this.mRemote.transact(3, _data, _reply, 0);
/*  848 */           _reply.readException();
/*      */         }
/*      */         finally {
/*  851 */           _reply.recycle();
/*  852 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void setScanParameters(int scanInterval, int scanWindow) throws RemoteException {
/*  857 */         Parcel _data = Parcel.obtain();
/*  858 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/*  860 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*  861 */           _data.writeInt(scanInterval);
/*  862 */           _data.writeInt(scanWindow);
/*  863 */           this.mRemote.transact(4, _data, _reply, 0);
/*  864 */           _reply.readException();
/*      */         }
/*      */         finally {
/*  867 */           _reply.recycle();
/*  868 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void filterEnable(boolean enable) throws RemoteException {
/*  873 */         Parcel _data = Parcel.obtain();
/*  874 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/*  876 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*  877 */           _data.writeInt(enable ? 1 : 0);
/*  878 */           this.mRemote.transact(5, _data, _reply, 0);
/*  879 */           _reply.readException();
/*      */         }
/*      */         finally {
/*  882 */           _reply.recycle();
/*  883 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void filterEnableBDA(boolean enable, int addr_type, String address) throws RemoteException {
/*  888 */         Parcel _data = Parcel.obtain();
/*  889 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/*  891 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*  892 */           _data.writeInt(enable ? 1 : 0);
/*  893 */           _data.writeInt(addr_type);
/*  894 */           _data.writeString(address);
/*  895 */           this.mRemote.transact(6, _data, _reply, 0);
/*  896 */           _reply.readException();
/*      */         }
/*      */         finally {
/*  899 */           _reply.recycle();
/*  900 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void clearManufacturerData() throws RemoteException {
/*  905 */         Parcel _data = Parcel.obtain();
/*  906 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/*  908 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*  909 */           this.mRemote.transact(7, _data, _reply, 0);
/*  910 */           _reply.readException();
/*      */         }
/*      */         finally {
/*  913 */           _reply.recycle();
/*  914 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void filterManufacturerData(int company, byte[] data1, byte[] data2, byte[] data3, byte[] data4) throws RemoteException {
/*  919 */         Parcel _data = Parcel.obtain();
/*  920 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/*  922 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*  923 */           _data.writeInt(company);
/*  924 */           _data.writeByteArray(data1);
/*  925 */           _data.writeByteArray(data2);
/*  926 */           _data.writeByteArray(data3);
/*  927 */           _data.writeByteArray(data4);
/*  928 */           this.mRemote.transact(8, _data, _reply, 0);
/*  929 */           _reply.readException();
/*      */         }
/*      */         finally {
/*  932 */           _reply.recycle();
/*  933 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void filterManufacturerDataBDA(int company, byte[] data1, byte[] data2, byte[] data3, byte[] data4, boolean has_bda, int addr_type, String address) throws RemoteException {
/*  938 */         Parcel _data = Parcel.obtain();
/*  939 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/*  941 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*  942 */           _data.writeInt(company);
/*  943 */           _data.writeByteArray(data1);
/*  944 */           _data.writeByteArray(data2);
/*  945 */           _data.writeByteArray(data3);
/*  946 */           _data.writeByteArray(data4);
/*  947 */           _data.writeInt(has_bda ? 1 : 0);
/*  948 */           _data.writeInt(addr_type);
/*  949 */           _data.writeString(address);
/*  950 */           this.mRemote.transact(9, _data, _reply, 0);
/*  951 */           _reply.readException();
/*      */         }
/*      */         finally {
/*  954 */           _reply.recycle();
/*  955 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void observe(boolean start, int duration) throws RemoteException {
/*  960 */         Parcel _data = Parcel.obtain();
/*  961 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/*  963 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*  964 */           _data.writeInt(start ? 1 : 0);
/*  965 */           _data.writeInt(duration);
/*  966 */           this.mRemote.transact(10, _data, _reply, 0);
/*  967 */           _reply.readException();
/*      */         }
/*      */         finally {
/*  970 */           _reply.recycle();
/*  971 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void open(byte clientIf, String address, boolean isDirect) throws RemoteException {
/*  976 */         Parcel _data = Parcel.obtain();
/*  977 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/*  979 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*  980 */           _data.writeByte(clientIf);
/*  981 */           _data.writeString(address);
/*  982 */           _data.writeInt(isDirect ? 1 : 0);
/*  983 */           this.mRemote.transact(11, _data, _reply, 0);
/*  984 */           _reply.readException();
/*      */         }
/*      */         finally {
/*  987 */           _reply.recycle();
/*  988 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void close(byte clientIf, String address, int connId, boolean isDirect) throws RemoteException {
/*  993 */         Parcel _data = Parcel.obtain();
/*  994 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/*  996 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/*  997 */           _data.writeByte(clientIf);
/*  998 */           _data.writeString(address);
/*  999 */           _data.writeInt(connId);
/* 1000 */           _data.writeInt(isDirect ? 1 : 0);
/* 1001 */           this.mRemote.transact(12, _data, _reply, 0);
/* 1002 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1005 */           _reply.recycle();
/* 1006 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void registerServiceDataCallback(int connId, BluetoothGattID svcUuid, String bdaddr, IBleCharacteristicDataCallback callback) throws RemoteException {
/* 1011 */         Parcel _data = Parcel.obtain();
/* 1012 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1014 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1015 */           _data.writeInt(connId);
/* 1016 */           if (svcUuid != null) {
/* 1017 */             _data.writeInt(1);
/* 1018 */             svcUuid.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1021 */             _data.writeInt(0);
/*      */           }
/* 1023 */           _data.writeString(bdaddr);
/* 1024 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 1025 */           this.mRemote.transact(13, _data, _reply, 0);
/* 1026 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1029 */           _reply.recycle();
/* 1030 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void searchService(int connId, BluetoothGattID uuid) throws RemoteException {
/* 1035 */         Parcel _data = Parcel.obtain();
/* 1036 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1038 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1039 */           _data.writeInt(connId);
/* 1040 */           if (uuid != null) {
/* 1041 */             _data.writeInt(1);
/* 1042 */             uuid.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1045 */             _data.writeInt(0);
/*      */           }
/* 1047 */           this.mRemote.transact(14, _data, _reply, 0);
/* 1048 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1051 */           _reply.recycle();
/* 1052 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void getFirstChar(int connId, BluetoothGattID serviceId, BluetoothGattID condCharUuid) throws RemoteException {
/* 1057 */         Parcel _data = Parcel.obtain();
/* 1058 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1060 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1061 */           _data.writeInt(connId);
/* 1062 */           if (serviceId != null) {
/* 1063 */             _data.writeInt(1);
/* 1064 */             serviceId.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1067 */             _data.writeInt(0);
/*      */           }
/* 1069 */           if (condCharUuid != null) {
/* 1070 */             _data.writeInt(1);
/* 1071 */             condCharUuid.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1074 */             _data.writeInt(0);
/*      */           }
/* 1076 */           this.mRemote.transact(15, _data, _reply, 0);
/* 1077 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1080 */           _reply.recycle();
/* 1081 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void getNextChar(int connId, BluetoothGattCharID startCharId, BluetoothGattID condCharUuid) throws RemoteException {
/* 1086 */         Parcel _data = Parcel.obtain();
/* 1087 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1089 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1090 */           _data.writeInt(connId);
/* 1091 */           if (startCharId != null) {
/* 1092 */             _data.writeInt(1);
/* 1093 */             startCharId.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1096 */             _data.writeInt(0);
/*      */           }
/* 1098 */           if (condCharUuid != null) {
/* 1099 */             _data.writeInt(1);
/* 1100 */             condCharUuid.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1103 */             _data.writeInt(0);
/*      */           }
/* 1105 */           this.mRemote.transact(16, _data, _reply, 0);
/* 1106 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1109 */           _reply.recycle();
/* 1110 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void getFirstCharDescr(int connId, BluetoothGattCharID charId, BluetoothGattID condDescrUuid) throws RemoteException {
/* 1115 */         Parcel _data = Parcel.obtain();
/* 1116 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1118 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1119 */           _data.writeInt(connId);
/* 1120 */           if (charId != null) {
/* 1121 */             _data.writeInt(1);
/* 1122 */             charId.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1125 */             _data.writeInt(0);
/*      */           }
/* 1127 */           if (condDescrUuid != null) {
/* 1128 */             _data.writeInt(1);
/* 1129 */             condDescrUuid.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1132 */             _data.writeInt(0);
/*      */           }
/* 1134 */           this.mRemote.transact(17, _data, _reply, 0);
/* 1135 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1138 */           _reply.recycle();
/* 1139 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void getNextCharDescr(int connId, BluetoothGattCharDescrID descrId, BluetoothGattID condDescrUuid) throws RemoteException {
/* 1144 */         Parcel _data = Parcel.obtain();
/* 1145 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1147 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1148 */           _data.writeInt(connId);
/* 1149 */           if (descrId != null) {
/* 1150 */             _data.writeInt(1);
/* 1151 */             descrId.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1154 */             _data.writeInt(0);
/*      */           }
/* 1156 */           if (condDescrUuid != null) {
/* 1157 */             _data.writeInt(1);
/* 1158 */             condDescrUuid.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1161 */             _data.writeInt(0);
/*      */           }
/* 1163 */           this.mRemote.transact(18, _data, _reply, 0);
/* 1164 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1167 */           _reply.recycle();
/* 1168 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void getFirstIncludedService(int connId, BluetoothGattID srvcId, BluetoothGattID condSrvcUuid) throws RemoteException {
/* 1173 */         Parcel _data = Parcel.obtain();
/* 1174 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1176 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1177 */           _data.writeInt(connId);
/* 1178 */           if (srvcId != null) {
/* 1179 */             _data.writeInt(1);
/* 1180 */             srvcId.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1183 */             _data.writeInt(0);
/*      */           }
/* 1185 */           if (condSrvcUuid != null) {
/* 1186 */             _data.writeInt(1);
/* 1187 */             condSrvcUuid.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1190 */             _data.writeInt(0);
/*      */           }
/* 1192 */           this.mRemote.transact(19, _data, _reply, 0);
/* 1193 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1196 */           _reply.recycle();
/* 1197 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void getNextIncludedService(int connId, BluetoothGattInclSrvcID startSrvcId, BluetoothGattID condSrvcUuid) throws RemoteException {
/* 1202 */         Parcel _data = Parcel.obtain();
/* 1203 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1205 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1206 */           _data.writeInt(connId);
/* 1207 */           if (startSrvcId != null) {
/* 1208 */             _data.writeInt(1);
/* 1209 */             startSrvcId.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1212 */             _data.writeInt(0);
/*      */           }
/* 1214 */           if (condSrvcUuid != null) {
/* 1215 */             _data.writeInt(1);
/* 1216 */             condSrvcUuid.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1219 */             _data.writeInt(0);
/*      */           }
/* 1221 */           this.mRemote.transact(20, _data, _reply, 0);
/* 1222 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1225 */           _reply.recycle();
/* 1226 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void readChar(int connId, BluetoothGattCharID charId, byte authReq) throws RemoteException {
/* 1231 */         Parcel _data = Parcel.obtain();
/* 1232 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1234 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1235 */           _data.writeInt(connId);
/* 1236 */           if (charId != null) {
/* 1237 */             _data.writeInt(1);
/* 1238 */             charId.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1241 */             _data.writeInt(0);
/*      */           }
/* 1243 */           _data.writeByte(authReq);
/* 1244 */           this.mRemote.transact(21, _data, _reply, 0);
/* 1245 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1248 */           _reply.recycle();
/* 1249 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void readCharDescr(int connId, BluetoothGattCharDescrID charDescr, byte authReq) throws RemoteException {
/* 1254 */         Parcel _data = Parcel.obtain();
/* 1255 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1257 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1258 */           _data.writeInt(connId);
/* 1259 */           if (charDescr != null) {
/* 1260 */             _data.writeInt(1);
/* 1261 */             charDescr.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1264 */             _data.writeInt(0);
/*      */           }
/* 1266 */           _data.writeByte(authReq);
/* 1267 */           this.mRemote.transact(22, _data, _reply, 0);
/* 1268 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1271 */           _reply.recycle();
/* 1272 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void writeCharValue(int connId, BluetoothGattCharID charId, int writeType, byte authReq, byte[] value) throws RemoteException {
/* 1277 */         Parcel _data = Parcel.obtain();
/* 1278 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1280 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1281 */           _data.writeInt(connId);
/* 1282 */           if (charId != null) {
/* 1283 */             _data.writeInt(1);
/* 1284 */             charId.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1287 */             _data.writeInt(0);
/*      */           }
/* 1289 */           _data.writeInt(writeType);
/* 1290 */           _data.writeByte(authReq);
/* 1291 */           _data.writeByteArray(value);
/* 1292 */           this.mRemote.transact(23, _data, _reply, 0);
/* 1293 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1296 */           _reply.recycle();
/* 1297 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void writeCharDescrValue(int connId, BluetoothGattCharDescrID charDescr, int writeType, byte authReq, byte[] charDescrVal) throws RemoteException {
/* 1302 */         Parcel _data = Parcel.obtain();
/* 1303 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1305 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1306 */           _data.writeInt(connId);
/* 1307 */           if (charDescr != null) {
/* 1308 */             _data.writeInt(1);
/* 1309 */             charDescr.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1312 */             _data.writeInt(0);
/*      */           }
/* 1314 */           _data.writeInt(writeType);
/* 1315 */           _data.writeByte(authReq);
/* 1316 */           _data.writeByteArray(charDescrVal);
/* 1317 */           this.mRemote.transact(24, _data, _reply, 0);
/* 1318 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1321 */           _reply.recycle();
/* 1322 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void sendIndConfirm(int connId, BluetoothGattCharID charId) throws RemoteException {
/* 1327 */         Parcel _data = Parcel.obtain();
/* 1328 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1330 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1331 */           _data.writeInt(connId);
/* 1332 */           if (charId != null) {
/* 1333 */             _data.writeInt(1);
/* 1334 */             charId.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1337 */             _data.writeInt(0);
/*      */           }
/* 1339 */           this.mRemote.transact(25, _data, _reply, 0);
/* 1340 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1343 */           _reply.recycle();
/* 1344 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void prepareWrite(int connId, BluetoothGattCharID charId, int offset, int len, byte[] value) throws RemoteException {
/* 1349 */         Parcel _data = Parcel.obtain();
/* 1350 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1352 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1353 */           _data.writeInt(connId);
/* 1354 */           if (charId != null) {
/* 1355 */             _data.writeInt(1);
/* 1356 */             charId.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1359 */             _data.writeInt(0);
/*      */           }
/* 1361 */           _data.writeInt(offset);
/* 1362 */           _data.writeInt(len);
/* 1363 */           _data.writeByteArray(value);
/* 1364 */           this.mRemote.transact(26, _data, _reply, 0);
/* 1365 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1368 */           _reply.recycle();
/* 1369 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void executeWrite(int connId, boolean isExecute) throws RemoteException {
/* 1374 */         Parcel _data = Parcel.obtain();
/* 1375 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1377 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1378 */           _data.writeInt(connId);
/* 1379 */           _data.writeInt(isExecute ? 1 : 0);
/* 1380 */           this.mRemote.transact(27, _data, _reply, 0);
/* 1381 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1384 */           _reply.recycle();
/* 1385 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void registerForNotifications(byte clientIf, String address, BluetoothGattCharID charID) throws RemoteException {
/* 1390 */         Parcel _data = Parcel.obtain();
/* 1391 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1393 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1394 */           _data.writeByte(clientIf);
/* 1395 */           _data.writeString(address);
/* 1396 */           if (charID != null) {
/* 1397 */             _data.writeInt(1);
/* 1398 */             charID.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1401 */             _data.writeInt(0);
/*      */           }
/* 1403 */           this.mRemote.transact(28, _data, _reply, 0);
/* 1404 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1407 */           _reply.recycle();
/* 1408 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void deregisterForNotifications(byte clientIf, String address, BluetoothGattCharID charId) throws RemoteException {
/* 1413 */         Parcel _data = Parcel.obtain();
/* 1414 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1416 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1417 */           _data.writeByte(clientIf);
/* 1418 */           _data.writeString(address);
/* 1419 */           if (charId != null) {
/* 1420 */             _data.writeInt(1);
/* 1421 */             charId.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1424 */             _data.writeInt(0);
/*      */           }
/* 1426 */           this.mRemote.transact(29, _data, _reply, 0);
/* 1427 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1430 */           _reply.recycle();
/* 1431 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void registerServerServiceCallback(BluetoothGattID serviceUuid, BluetoothGattID appUuid, IBleServiceCallback callback) throws RemoteException {
/* 1436 */         Parcel _data = Parcel.obtain();
/* 1437 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1439 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1440 */           if (serviceUuid != null) {
/* 1441 */             _data.writeInt(1);
/* 1442 */             serviceUuid.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1445 */             _data.writeInt(0);
/*      */           }
/* 1447 */           if (appUuid != null) {
/* 1448 */             _data.writeInt(1);
/* 1449 */             appUuid.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1452 */             _data.writeInt(0);
/*      */           }
/* 1454 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 1455 */           this.mRemote.transact(30, _data, _reply, 0);
/* 1456 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1459 */           _reply.recycle();
/* 1460 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void registerServerProfileCallback(BluetoothGattID appUuid, IBleProfileEventCallback callback) throws RemoteException {
/* 1465 */         Parcel _data = Parcel.obtain();
/* 1466 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1468 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1469 */           if (appUuid != null) {
/* 1470 */             _data.writeInt(1);
/* 1471 */             appUuid.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1474 */             _data.writeInt(0);
/*      */           }
/* 1476 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 1477 */           this.mRemote.transact(31, _data, _reply, 0);
/* 1478 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1481 */           _reply.recycle();
/* 1482 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void unregisterServerServiceCallback(int svcHandle) throws RemoteException {
/* 1487 */         Parcel _data = Parcel.obtain();
/* 1488 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1490 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1491 */           _data.writeInt(svcHandle);
/* 1492 */           this.mRemote.transact(32, _data, _reply, 0);
/* 1493 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1496 */           _reply.recycle();
/* 1497 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void unregisterServerProfileCallback(int serIf) throws RemoteException {
/* 1502 */         Parcel _data = Parcel.obtain();
/* 1503 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1505 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1506 */           _data.writeInt(serIf);
/* 1507 */           this.mRemote.transact(33, _data, _reply, 0);
/* 1508 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1511 */           _reply.recycle();
/* 1512 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void GATTServer_CreateService(byte serIf, BluetoothGattID serviceId, int numHandles) throws RemoteException {
/* 1517 */         Parcel _data = Parcel.obtain();
/* 1518 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1520 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1521 */           _data.writeByte(serIf);
/* 1522 */           if (serviceId != null) {
/* 1523 */             _data.writeInt(1);
/* 1524 */             serviceId.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1527 */             _data.writeInt(0);
/*      */           }
/* 1529 */           _data.writeInt(numHandles);
/* 1530 */           this.mRemote.transact(34, _data, _reply, 0);
/* 1531 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1534 */           _reply.recycle();
/* 1535 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void GATTServer_AddIncludedService(int svcHandle, int includedSvcHandle) throws RemoteException {
/* 1540 */         Parcel _data = Parcel.obtain();
/* 1541 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1543 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1544 */           _data.writeInt(svcHandle);
/* 1545 */           _data.writeInt(includedSvcHandle);
/* 1546 */           this.mRemote.transact(35, _data, _reply, 0);
/* 1547 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1550 */           _reply.recycle();
/* 1551 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void GATTServer_AddCharacteristic(int svcHandle, BluetoothGattID charId, int permissions, int charProperty, boolean dirtyFlag, int dirtDescNum) throws RemoteException {
/* 1556 */         Parcel _data = Parcel.obtain();
/* 1557 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1559 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1560 */           _data.writeInt(svcHandle);
/* 1561 */           if (charId != null) {
/* 1562 */             _data.writeInt(1);
/* 1563 */             charId.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1566 */             _data.writeInt(0);
/*      */           }
/* 1568 */           _data.writeInt(permissions);
/* 1569 */           _data.writeInt(charProperty);
/* 1570 */           _data.writeInt(dirtyFlag ? 1 : 0);
/* 1571 */           _data.writeInt(dirtDescNum);
/* 1572 */           this.mRemote.transact(36, _data, _reply, 0);
/* 1573 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1576 */           _reply.recycle();
/* 1577 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void GATTServer_AddCharDescriptor(int svcHandle, int permissions, BluetoothGattID descrUuid) throws RemoteException {
/* 1582 */         Parcel _data = Parcel.obtain();
/* 1583 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1585 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1586 */           _data.writeInt(svcHandle);
/* 1587 */           _data.writeInt(permissions);
/* 1588 */           if (descrUuid != null) {
/* 1589 */             _data.writeInt(1);
/* 1590 */             descrUuid.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1593 */             _data.writeInt(0);
/*      */           }
/* 1595 */           this.mRemote.transact(37, _data, _reply, 0);
/* 1596 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1599 */           _reply.recycle();
/* 1600 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void GATTServer_DeleteService(int svcHandle) throws RemoteException {
/* 1605 */         Parcel _data = Parcel.obtain();
/* 1606 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1608 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1609 */           _data.writeInt(svcHandle);
/* 1610 */           this.mRemote.transact(38, _data, _reply, 0);
/* 1611 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1614 */           _reply.recycle();
/* 1615 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void GATTServer_StartService(int svcHandle, byte supTransport) throws RemoteException {
/* 1620 */         Parcel _data = Parcel.obtain();
/* 1621 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1623 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1624 */           _data.writeInt(svcHandle);
/* 1625 */           _data.writeByte(supTransport);
/* 1626 */           this.mRemote.transact(39, _data, _reply, 0);
/* 1627 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1630 */           _reply.recycle();
/* 1631 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void GATTServer_StopService(int svcHandle) throws RemoteException {
/* 1636 */         Parcel _data = Parcel.obtain();
/* 1637 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1639 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1640 */           _data.writeInt(svcHandle);
/* 1641 */           this.mRemote.transact(40, _data, _reply, 0);
/* 1642 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1645 */           _reply.recycle();
/* 1646 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void GATTServer_HandleValueIndication(int connId, int attrHandle, byte[] val) throws RemoteException {
/* 1651 */         Parcel _data = Parcel.obtain();
/* 1652 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1654 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1655 */           _data.writeInt(connId);
/* 1656 */           _data.writeInt(attrHandle);
/* 1657 */           _data.writeByteArray(val);
/* 1658 */           this.mRemote.transact(41, _data, _reply, 0);
/* 1659 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1662 */           _reply.recycle();
/* 1663 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void GATTServer_HandleValueNotification(int connId, int attrHandle, byte[] val) throws RemoteException {
/* 1668 */         Parcel _data = Parcel.obtain();
/* 1669 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1671 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1672 */           _data.writeInt(connId);
/* 1673 */           _data.writeInt(attrHandle);
/* 1674 */           _data.writeByteArray(val);
/* 1675 */           this.mRemote.transact(42, _data, _reply, 0);
/* 1676 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1679 */           _reply.recycle();
/* 1680 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void GATTServer_SendRsp(int conn_id, int transId, byte responseStatus, int handle, int offset, byte[] val, byte authReq, boolean write_req) throws RemoteException {
/* 1685 */         Parcel _data = Parcel.obtain();
/* 1686 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1688 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1689 */           _data.writeInt(conn_id);
/* 1690 */           _data.writeInt(transId);
/* 1691 */           _data.writeByte(responseStatus);
/* 1692 */           _data.writeInt(handle);
/* 1693 */           _data.writeInt(offset);
/* 1694 */           _data.writeByteArray(val);
/* 1695 */           _data.writeByte(authReq);
/* 1696 */           _data.writeInt(write_req ? 1 : 0);
/* 1697 */           this.mRemote.transact(43, _data, _reply, 0);
/* 1698 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1701 */           _reply.recycle();
/* 1702 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void GATTServer_Open(byte serIf, String bdaddr, boolean isDirect) throws RemoteException {
/* 1707 */         Parcel _data = Parcel.obtain();
/* 1708 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1710 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1711 */           _data.writeByte(serIf);
/* 1712 */           _data.writeString(bdaddr);
/* 1713 */           _data.writeInt(isDirect ? 1 : 0);
/* 1714 */           this.mRemote.transact(44, _data, _reply, 0);
/* 1715 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1718 */           _reply.recycle();
/* 1719 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void GATTServer_CancelOpen(byte serIf, String bdaddr, boolean isDirect) throws RemoteException {
/* 1724 */         Parcel _data = Parcel.obtain();
/* 1725 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1727 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1728 */           _data.writeByte(serIf);
/* 1729 */           _data.writeString(bdaddr);
/* 1730 */           _data.writeInt(isDirect ? 1 : 0);
/* 1731 */           this.mRemote.transact(45, _data, _reply, 0);
/* 1732 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1735 */           _reply.recycle();
/* 1736 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void GATTServer_Close(int connId) throws RemoteException {
/* 1741 */         Parcel _data = Parcel.obtain();
/* 1742 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1744 */           _data.writeInterfaceToken("com.broadcom.bt.service.gatt.IBluetoothGatt");
/* 1745 */           _data.writeInt(connId);
/* 1746 */           this.mRemote.transact(46, _data, _reply, 0);
/* 1747 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1750 */           _reply.recycle();
/* 1751 */           _data.recycle();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /opt/android/sdk/add-ons/addon-open_bluetooth_low-energy_api-broadcom-10/libs/com.broadcom.bt.le.jar
 * Qualified Name:     com.broadcom.bt.service.gatt.IBluetoothGatt
 * JD-Core Version:    0.6.0
 */