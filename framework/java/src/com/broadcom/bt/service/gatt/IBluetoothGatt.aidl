package com.broadcom.bt.service.gatt;

import com.broadcom.bt.service.gatt.BluetoothGattID;
import com.broadcom.bt.service.gatt.BluetoothGattCharID;
import com.broadcom.bt.service.gatt.BluetoothGattCharDescrID;
import com.broadcom.bt.service.gatt.BluetoothGattInclSrvcID;

import com.broadcom.bt.le.api.IBleServiceCallback;
import com.broadcom.bt.le.api.IBleClientCallback;
import com.broadcom.bt.le.api.IBleCharacteristicDataCallback;
import com.broadcom.bt.le.api.IBleProfileEventCallback;

interface IBluetoothGatt {

	void registerApp(in BluetoothGattID paramBluetoothGattID,
                     in IBleClientCallback paramIBleClientCallback);

    void unregisterApp(byte paramByte);

    void setEncryption(in String paramString, byte paramByte);

    void setScanParameters(int p1, int p2);

    void filterEnable(boolean p);

    void filterEnableBDA(boolean p, int i, in String s);

    void clearManufacturerData();

    void filterManufacturerData(int paramInt, 
                                in byte[] paramArrayOfByte1,
                                in byte[] paramArrayOfByte2, 
                                in byte[] paramArrayOfByte3, 
                                in byte[] paramArrayOfByte4);

    void filterManufacturerDataBDA(int paramInt1, 
                                   in byte[] paramArrayOfByte1,
                                   in byte[] paramArrayOfByte2, 
                                   in byte[] paramArrayOfByte3, 
                                   in byte[] paramArrayOfByte4,
                                   boolean paramBoolean, 
                                   int paramInt2,
                                   in String paramString);

    void observe(boolean paramBoolean, 
                 int paramInt);

    void open(byte paramByte, 
              in String paramString, 
              boolean paramBoolean);

    void close(byte paramByte, 
               in String paramString, 
               int paramInt,
               boolean paramBoolean);

    void registerServiceDataCallback(
                                     int paramInt,
                                     in BluetoothGattID id, 
                                     in String paramString,
                                     in IBleCharacteristicDataCallback cb);

    void searchService(int paramInt, 
                       in BluetoothGattID id);

    void getFirstChar(int paramInt, 
                      in BluetoothGattID id1,
                      in BluetoothGattID id2);
    
    void getNextChar(int paramInt,
                     in BluetoothGattCharID charID, 
                     in BluetoothGattID ID);

    void getFirstCharDescr(int paramInt,
                           in BluetoothGattCharID CharID, 
                           in BluetoothGattID ID);

    void getNextCharDescr(int paramInt,
                          in BluetoothGattCharDescrID CharDescrID,
                          in BluetoothGattID ID);

    void getFirstIncludedService(int paramInt,
                                 in BluetoothGattID id1, 
                                 in BluetoothGattID id2);

    void getNextIncludedService(int paramInt,
                                in BluetoothGattInclSrvcID SrvcID,
                                in BluetoothGattID id);

    void readChar(int paramInt, 
                  in BluetoothGattCharID charID,
                  byte paramByte);

    void readCharDescr(int paramInt,
                       in BluetoothGattCharDescrID charDesc, 
                       byte paramByte);

    void writeCharValue(int paramInt1,
                        in BluetoothGattCharID CharID, 
                        int paramInt2, 
                        byte paramByte,
                        in byte[] paramArrayOfByte);

    void writeCharDescrValue(int paramInt1,
                             in BluetoothGattCharDescrID descID, 
                             int paramInt2, 
                             byte paramByte,
                             in byte[] paramArrayOfByte);

    void sendIndConfirm(int paramInt,
                        in BluetoothGattCharID charID);

    void prepareWrite(int paramInt1,
                      in BluetoothGattCharID charID, 
                      int paramInt2, 
                      int paramInt3,
                      in byte[] paramArrayOfByte);

    void executeWrite(int paramInt, 
                      boolean paramBoolean);

    void registerForNotifications(byte paramByte, 
                                  in String paramString,
                                  in BluetoothGattCharID charID);

    void deregisterForNotifications(byte paramByte, 
                                    in String paramString,
                                    in BluetoothGattCharID charID);

    void registerServerServiceCallback(in BluetoothGattID id1,
                                       in BluetoothGattID id2, 
                                       in IBleServiceCallback callback);

    void registerServerProfileCallback(in BluetoothGattID id,
                                       in IBleProfileEventCallback callback);

    void unregisterServerServiceCallback(int paramInt);

    void unregisterServerProfileCallback(int paramInt);

    void GATTServer_CreateService(byte paramByte,
                                  in BluetoothGattID id, 
                                  int paramInt);

    void GATTServer_AddIncludedService(int paramInt1, 
                                       int paramInt2);

    void GATTServer_AddCharacteristic(int paramInt1,
                                      in BluetoothGattID id, 
                                      int paramInt2, 
                                      int paramInt3,
                                      boolean paramBoolean, 
                                      int paramInt4);

    void GATTServer_AddCharDescriptor(int paramInt1, 
                                      int paramInt2,
                                      in BluetoothGattID id);

    void GATTServer_DeleteService(int paramInt);

    void GATTServer_StartService(int paramInt, 
                                 byte paramByte);

    void GATTServer_StopService(int paramInt);

    void GATTServer_HandleValueIndication(int paramInt1, 
                                          int paramInt2,
                                          in byte[] paramArrayOfByte);

    void GATTServer_HandleValueNotification(int paramInt1, 
                                            int paramInt2,
                                            in byte[] paramArrayOfByte);

    void GATTServer_SendRsp(int paramInt1, 
                            int paramInt2, 
                            byte paramByte1,
                            int paramInt3, 
                            int paramInt4, 
                            in byte[] paramArrayOfByte, 
                            byte paramByte2,
                            boolean paramBoolean);

    void GATTServer_Open(byte paramByte, 
                         in String paramString,
                         boolean paramBoolean);

    void GATTServer_CancelOpen(byte paramByte, 
                               in String paramString,
                               boolean paramBoolean);

    void GATTServer_Close(int paramInt);
}
