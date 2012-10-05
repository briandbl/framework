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

import android.bluetooth.BluetoothDevice;
import android.os.RemoteException;
import android.util.Log;

import com.broadcom.bt.service.gatt.BluetoothGattCharDescrID;
import com.broadcom.bt.service.gatt.BluetoothGattCharID;
import com.broadcom.bt.service.gatt.BluetoothGattID;
import com.broadcom.bt.service.gatt.BluetoothGattInclSrvcID;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a low energy service in the client role. <br>
 * <br>
 * This is a base class implementing a BLE service, that an application should
 * override to implement a new service. A client service is identified by a
 * unique UUID and provides access to characteristics and descriptors provided
 * by a remote device. <br>
 * <br>
 * A BleClientService derived service is usually assigned to a
 * {@link BleClientProfile} as a required or optional service.
 * 
 * @author manuel
 */
public abstract class BleClientService
{
    private static String TAG = "BleClientService";

    private BleClientProfile mProfile = null;
    private BleGattID mServiceId = null;
    private HashMap<BluetoothDevice, ArrayList<ServiceData>> mdeviceToDataMap =
            new HashMap<BluetoothDevice, ArrayList<ServiceData>>();
    private BleCharacteristicDataCallback mCallback =
            new BleCharacteristicDataCallback();
    private boolean mReadDescriptors = true;

    /**
     * Creates a new Bluetooth Low Energy service identified by the given UUID.
     * 
     * @param serviceId
     */
    public BleClientService(BleGattID serviceId)
    {
        mServiceId = serviceId;
        if (mServiceId.getServiceType() == BleConstants.GATT_UNDEFINED)
            mServiceId.setServiceType(BleConstants.GATT_SERVICE_PRIMARY);
    }

    /**
     * Returns the UUID of this service.
     */
    public BleGattID getServiceId()
    {
        return mServiceId;
    }

    /**
     * Writes to a characteristic on a remote device. <br>
     * <br>
     * This function determines if the characteristic have been modified and
     * issues the necessary write commands. <br>
     * <br>
     * Once the characteristic write completes, the
     * onWriteCharacteristicComplete callback is invoked.
     * 
     * @param remoteDevice Identifies the remote device to write to.
     * @param instanceId - Instance id of this service.
     * @param characteristic - Characteristic to be written
     * @return BleConstants.GATT_SUCCESS if the write operation was initiated
     *         successfully
     * @see {@link #onWriteCharacteristicComplete(int, BluetoothDevice, BleCharacteristic)}
     */
    public int writeCharacteristic(BluetoothDevice remoteDevice, int instanceId,
            BleCharacteristic characteristic)
    {
        Log.d(TAG, "writeCharacteristic");

        int ret = BleConstants.GATT_SUCCESS;
        int connID = BleConstants.GATT_INVALID_CONN_ID;

        if ((connID = mProfile.getConnIdForDevice(remoteDevice)) == BleConstants.GATT_INVALID_CONN_ID) {
            return BleConstants.GATT_INVALID_CONN_ID;
        }
        ServiceData s = getServiceData(remoteDevice, instanceId);

        if (s == null) {
            return ret;
        }
        s.writeIndex = s.characteristics.indexOf(characteristic);

        if ((s.characteristics != null) && (s.writeIndex >= BleConstants.GATT_SERVICE_PRIMARY)) {
            Log.d(TAG, "writeCharacteristic found characteristic in array:");
            Log.d(TAG,
                    "Service = [instanceID = " + instanceId + " svcid = "
                            + mServiceId.toString() + " serviceType = "
                            + mServiceId.getServiceType());
            Log.d(TAG, "CharID = [instanceID = " + characteristic.getInstanceID()
                    + " svcid = " + characteristic.getID().toString());
            BleGattID svcId = new BleGattID(instanceId, mServiceId.getUuid(),
                    mServiceId.getServiceType());
            BleGattID cID = characteristic.getID();
            BluetoothGattCharID charID = new BluetoothGattCharID(svcId, cID);
            try
            {
                if (characteristic.isDirty()) {
                    if (characteristic.getWriteType() == BleConstants.GATT_SUCCESS)
                        characteristic.setWriteType(2);
                    characteristic.setDirty(false);
                    mProfile.getGattService().writeCharValue(connID, charID,
                            characteristic.getWriteType(), characteristic.getAuthReq(),
                            characteristic.getValue());
                }
                else if (!characteristic.getDirtyDescQueue().isEmpty()) {
                    ArrayList<BleDescriptor> descList =
                            characteristic.getDirtyDescQueue();
                    BleDescriptor descObj = descList.get(0);

                    Log.d(TAG, "writeCharacteristic - descriptor = "
                            + descObj.getID().toString());
                    if (descObj.isDirty()) {
                        BluetoothGattCharDescrID descID = new BluetoothGattCharDescrID(
                                svcId, cID, descObj.getID());
                        descObj.setDirty(false);
                        mProfile.getGattService().writeCharDescrValue(connID,
                                descID, descObj.getWriteType(), descObj.getAuthReq(),
                                descObj.getValue());
                    }

                }
                else
                {
                    onWriteCharacteristicComplete(0, remoteDevice, characteristic);
                }
            } catch (RemoteException e) {
                ret = BleConstants.GATT_ERROR;
            }
        } else {
            onWriteCharacteristicComplete(0, remoteDevice, characteristic);
        }
        return ret;
    }

    /**
     * Retrieves an array of all characteristics included in this service. The
     * characteristics and descriptors are read when this service is refreshed
     * using the refresh function.
     */
    public ArrayList<BleCharacteristic> getAllCharacteristics(BluetoothDevice remoteDevice)
    {
        Log.d(TAG, "getAllCharacteristics");

        ServiceData s = getServiceData(remoteDevice, mServiceId.getInstanceID());
        if (null != s) {
            return s.characteristics;
        }
        return null;
    }

    /**
     * Returns a characteristic of this service based on it's ID.
     */
    public BleCharacteristic getCharacteristic(BluetoothDevice remoteDevice,
            BleGattID characteristicID)
    {
        Log.d(TAG, "getCharacteristic charID = [" + characteristicID.toString()
                + "] instance ID = [" + characteristicID.getInstanceID() + "]");
        ServiceData s = getServiceData(remoteDevice, mServiceId.getInstanceID());
        if (s == null) {
            Log.d(TAG, "getCharacterisic - Service data not found");
            return null;
        }
        for (int i = 0; i < s.characteristics.size(); i++) {
            BleCharacteristic c = s.characteristics.get(i);
            if (c != null) {
                if (c.getID() != null) {
                    if ((c.getID().toString().equals(characteristicID.toString()))
                            && (c.getInstanceID() == characteristicID.getInstanceID()))
                    {
                        return c;
                    }
                }
                else
                    Log.d(TAG, "Error: Characteristic ID is null");
            }
            else {
                Log.d(TAG, "Error: Cannot retrieve characteristic");
            }
        }
        return null;
    }

    /**
     * Returns a list of all instance IDs for this service.
     */
    public int[] getAllServiceInstanceIds(BluetoothDevice remoteDevice)
    {
        Log.d(TAG, "getAllServiceInstanceIds");
        ArrayList<ServiceData> s = mdeviceToDataMap.get(remoteDevice);
        if (s != null) {
            int[] instanceIds = new int[s.size()];

            for (int i = 0; i < s.size(); i++) {
                instanceIds[i] = s.get(0).instanceID;
            }

            return instanceIds;
        }

        return null;
    }

    /**
     * Refreshes all remote characteristics for all instances of this service. <br>
     * <br>
     * This method will determine all characteristics provided by a given
     * service on the remote device and update all accessible values in the
     * service. <br>
     * <br>
     * The {@link onRefreshComplete(BluetoothDevice)} callback is triggered
     * after all characteristics have been refreshed.
     * 
     * @see {@link onRefreshComplete(BluetoothDevice)}
     */
    public void refresh(BluetoothDevice remoteDevice)
    {
        Log.d(TAG, "Refresh (" + mServiceId.toString() + ")");

        ArrayList<ServiceData> s = mdeviceToDataMap.get(remoteDevice);
        if (s != null) {
            ServiceData sd = s.get(0);
            Log.e(TAG,
                    "refresh() - Service data found, reading first characteristic... (serviceType = "
                            + sd.serviceType + ")");
            readFirstCharacteristic(remoteDevice, new BleGattID(sd.instanceID,
                    getServiceId().getUuid(), sd.serviceType));
        } else {
            Log.e(TAG, "refresh() - Service data not found");
        }
    }

    /**
     * Refreshes a given characteristic from the remote device. This method
     * updates the given characteristic value and descriptors from the remote
     * device.
     * 
     * @see {@link #onReadCharacteristicComplete(BluetoothDevice,BleCharacteristic)}
     */
    public int readCharacteristic(BluetoothDevice remoteDevice,
            BleCharacteristic characteristic)
    {
        int ret = BleConstants.GATT_SUCCESS;
        int connID = BleConstants.GATT_INVALID_CONN_ID;
        Log.d(TAG,
                "readCharacteristic - svc UUID = " + getServiceId().getUuid().toString()
                        + ", characteristic = " + characteristic.getID());

        BluetoothGattCharID charID = new BluetoothGattCharID(new BleGattID(
                characteristic.getInstanceID(), getServiceId().getUuid(), getServiceId()
                        .getServiceType()), characteristic.getID());

        if ((connID = mProfile.getConnIdForDevice(remoteDevice)) != BleConstants.GATT_INVALID_CONN_ID)
            readCharacteristicValue(connID, charID, characteristic.getAuthReq());
        else {
            ret = BleConstants.GATT_INVALID_CONN_ID;
        }
        return ret;
    }

    /**
     * Called to indicate a write operation to a characteristic on a remote
     * device has competed.
     */
    public void onWriteCharacteristicComplete(int status, BluetoothDevice remoteDevice,
            BleCharacteristic characteristic)
    {
        Log.d(TAG, "onWriteCharacteristicComplete 1 status=" + status);
        if (status == BleConstants.GATT_INSUF_AUTHENTICATION) {
            Log.d(TAG,
                    "onWriteCharacteristicComplete rcv GATT_INSUF_AUTHENTICATION issue createBond");
            if (remoteDevice.createBond())
                Log.d(TAG, "onWriteCharacteristicComplete createBond request Accepted");
            else {
                Log.e(TAG, "onWriteCharacteristicComplete createBond request FAILED");
            }
        }
        else if (status == BleConstants.GATT_INSUF_ENCRYPTION) {
            Log.d(TAG,
                    "onWriteCharacteristicComplete rcv GATT_INSUF_ENCRYPTION check link can be encrypt or not");
            if (remoteDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                Log.d(TAG,
                        "device bonded start to encrypt the link.  !!!! This case should not happen !!!!");
            } else {
                Log.d(TAG, "device is Not bonded start to pair");
                remoteDevice.createBond();
            }
        }
    }

    /**
     * Callback indicating a remote characteristic has changed. This callback is
     * invoked if the local device has registered for notifications from the
     * remote server.
     * 
     * @see {@link #registerForNotification(BluetoothDevice, int, BleGattID)}
     */
    public void onCharacteristicChanged(BluetoothDevice remoteDevice,
            BleCharacteristic characteristic)
    {
        Log.d(TAG, "onCharacteristicChanged");
    }

    /**
     * Callback indicating a refresh has been completed for this service.
     */
    public void onRefreshComplete(BluetoothDevice remoteDevice)
    {
        Log.d(TAG, "onRefreshComplete");
    }

    /**
     * Callback invoked when a service requires the authorization requirement
     * for a given characteristic to be set. An application should call
     * {@link BleCharacteristic#setAuthReq(byte)} for the provided
     * characteristic to set the required level of authorization.
     */
    public void onSetCharacteristicAuthRequirement(BluetoothDevice remoteDevice,
            BleCharacteristic characteristic, int instanceID)
    {
        Log.d(TAG, "onSetCharacteristicAuthRequirement");
    }

    /**
     * Called when a given characteristic has been updated and it's value and
     * descriptors have been read.
     */
    public void onReadCharacteristicComplete(BluetoothDevice remoteDevice,
            BleCharacteristic characteristic)
    {
        Log.d(TAG, "onReadCharacteristicComplete");
    }

    public void onReadCharacteristicComplete(int status, BluetoothDevice remoteDevice,
            BleCharacteristic characteristic)
    {
        Log.d(TAG, "onReadCharacteristicComplete status=" + status);
        if (status == BleConstants.GATT_INSUF_AUTHENTICATION) {
            Log.d(TAG,
                    "onReadCharacteristicComplete rcv GATT_INSUF_AUTHENTICATION issue createBond");
            remoteDevice.createBond();
            return;
        }
        if (status != BleConstants.GATT_INSUF_ENCRYPTION) {
            return;
        }

        Log.d(TAG,
                "onReadCharacteristicComplete rcv GATT_INSUF_ENCRYPTION check link can be encrypt or not");
        if (remoteDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            Log.d(TAG,
                    "device bonded start to encrypt the link.  !!!! This case should not happen !!!!");
        } else {
            Log.d(TAG, "device is Not bonded start to pair");
            remoteDevice.createBond();
        }

    }

    /**
     * Registers for notification from the server for this service.
     */
    public int registerForNotification(BluetoothDevice remoteDevice, int instanceID,
            BleGattID characteristicID)
    {
        int ret = BleConstants.GATT_SUCCESS;
        Log.d(TAG, "registerForNotification address: " + remoteDevice.getAddress());
        try {
            BleGattID svcId = new BleGattID(instanceID, getServiceId().getUuid(),
                    getServiceId().getServiceType());
            BluetoothGattCharID charId = new BluetoothGattCharID(svcId, characteristicID);

            mProfile.getGattService().registerForNotifications(
                    mProfile.getClientIf(), remoteDevice.getAddress(), charId);
        } catch (RemoteException e) {
            ret = BleConstants.GATT_ERROR;
        }
        return ret;
    }

    /**
     * Unregister for notification from the server for this service.
     */
    public int unregisterNotification(BluetoothDevice remoteDevice, int instanceID,
            BleGattID characteristicID)
    {
        int ret = BleConstants.GATT_SUCCESS;
        Log.d(TAG, "unregisterNotification address: " + remoteDevice.getAddress());
        try {
            BleGattID svcId = new BleGattID(instanceID, getServiceId().getUuid(),
                    getServiceId().getServiceType());
            BluetoothGattCharID charId = new BluetoothGattCharID(svcId, characteristicID);

            mProfile.getGattService().deregisterForNotifications(
                    mProfile.getClientIf(), remoteDevice.getAddress(), charId);
        } catch (RemoteException e) {
            ret = BleConstants.GATT_ERROR;
        }

        return ret;
    }

    void setInstanceID(BluetoothDevice remoteDevice, int instanceId)
    {
        Log.d(TAG, "setInstanceID address = " + remoteDevice.getAddress());

        ServiceData sd = getServiceData(remoteDevice, instanceId);
        mServiceId.setInstanceId(instanceId);
        if (null == sd) {
            Log.d(TAG, "setInstanceID setting instance id (" + instanceId + ")");

            sd = new ServiceData();
            sd.instanceID = mServiceId.getInstanceID();
            sd.serviceType = mServiceId.getServiceType();
            ArrayList<ServiceData> s = mdeviceToDataMap.get(remoteDevice);
            if (null == s) {
                s = new ArrayList<ServiceData>();
            }
            s.add(sd);
            mdeviceToDataMap.put(remoteDevice, s);
        }

        sd.instanceID = mServiceId.getInstanceID();
        sd.serviceType = mServiceId.getServiceType();
        try
        {
            int connID = BleConstants.GATT_INVALID_CONN_ID;

            if ((connID = mProfile.getConnIdForDevice(remoteDevice)) != BleConstants.GATT_INVALID_CONN_ID)
                mProfile.getGattService().registerServiceDataCallback(connID,
                        mServiceId, remoteDevice.getAddress(), mCallback);
        } catch (RemoteException e)
        {
            Log.d(TAG, e.toString());
        }
    }

    /** @hide */
    void removeInstanceID(BluetoothDevice remoteDevice, int instanceID)
    {
    }

    /** @hide */
    void setProfile(BleClientProfile profile)
    {
        mProfile = profile;
    }

    protected ServiceData getServiceData(BluetoothDevice remoteDevice, int instanceID)
    {
        Log.d(TAG, "getServiceData address = " + remoteDevice.getAddress()
                + " instanceID = " + instanceID);

        ServiceData sData = null;
        ArrayList<ServiceData> s = mdeviceToDataMap.get(remoteDevice);
        if (s != null) {
            for (int i = 0; i < s.size(); i++) {
                if (s.get(i).instanceID == instanceID) {
                    sData = s.get(i);
                    break;
                }
            }
        }
        return sData;
    }

    protected ServiceData getNextServiceData(BluetoothDevice remoteDevice,
            int currentInstanceID)
    {
        Log.d(TAG, "getServiceData address = " + remoteDevice.getAddress()
                + " currentinstanceID = " + currentInstanceID);

        ServiceData sData = null;
        ArrayList<ServiceData> s = mdeviceToDataMap.get(remoteDevice);
        if (s != null) {
            for (int i = 0; i < s.size(); i++) {
                if (s.get(i).instanceID != currentInstanceID)
                    continue;
                if (s.size() > i + 1) {
                    sData = s.get(i + 1);
                    break;
                }
            }

        }

        return sData;
    }

    /** @hide */
    protected int getFirstIncludedService(BluetoothDevice remoteDevice, int instanceID)
    {
        int ret = BleConstants.GATT_SUCCESS;
        int connID = BleConstants.GATT_INVALID_CONN_ID;

        Log.d(TAG, "getFirstIncludedService address = " + remoteDevice.getAddress());
        ServiceData sd = getServiceData(remoteDevice, instanceID);
        try {
            if ((connID = mProfile.getConnIdForDevice(remoteDevice)) != BleConstants.GATT_INVALID_CONN_ID) {
                BleGattID svcId = new BleGattID(sd.instanceID, mServiceId.getUuid(),
                        mServiceId.getServiceType());
                mProfile.getGattService().getFirstIncludedService(connID, svcId,
                        null);
            }
            else {
                ret = BleConstants.GATT_INVALID_CONN_ID;
            }
        } catch (RemoteException e) {
            Log.d(TAG, "getFirstIncludedService " + e.toString());
            ret = BleConstants.GATT_ERROR;
        }

        return ret;
    }

    protected int getNextIncludedService(BluetoothDevice remoteDevice,
            BluetoothGattInclSrvcID inclsvcId)
    {
        int ret = BleConstants.GATT_SUCCESS;
        int connID = BleConstants.GATT_INVALID_CONN_ID;

        Log.d(TAG, "getNextIncludedService address = " + remoteDevice.getAddress());
        try
        {
            if ((connID = mProfile.getConnIdForDevice(remoteDevice)) != BleConstants.GATT_INVALID_CONN_ID)
                mProfile.getGattService().getNextIncludedService(connID, inclsvcId,
                        null);
            else
                ret = BleConstants.GATT_INVALID_CONN_ID;
        } catch (RemoteException e) {
            Log.d(TAG, "getNextIncludedService " + e.toString());
            ret = BleConstants.GATT_ERROR;
        }

        return ret;
    }

    /** @hide */
    protected int readFirstCharacteristic(BluetoothDevice remoteDevice, BleGattID svcId)
    {
        int ret = BleConstants.GATT_SUCCESS;
        int connID = BleConstants.GATT_INVALID_CONN_ID;

        Log.d(TAG, "readFirstCharacteristic address = " + remoteDevice.getAddress());
        try
        {
            if ((connID = mProfile.getConnIdForDevice(remoteDevice)) != BleConstants.GATT_INVALID_CONN_ID)
                mProfile.getGattService().getFirstChar(connID, svcId, null);
            else
                ret = BleConstants.GATT_INVALID_CONN_ID;
        } catch (RemoteException e) {
            Log.d(TAG, "getFirstCharacteristic " + e.toString());
            ret = BleConstants.GATT_ERROR;
        }

        return ret;
    }

    /** @hide */
    protected int readNextCharacteristic(BluetoothDevice remoteDevice, BleGattID svcId,
            BleGattID characteristicID)
    {
        Log.d(TAG,
                "readNextCharacteristic characteristicID = " + characteristicID.toString()
                        + " char inst id = " + characteristicID.getInstanceID());
        Log.d(TAG, "serviceID = " + svcId.toString());

        int ret = BleConstants.GATT_SUCCESS;
        int connID = BleConstants.GATT_INVALID_CONN_ID;
        ServiceData sd = getServiceData(remoteDevice, svcId.getInstanceID());
        BluetoothGattCharID charId = new BluetoothGattCharID(svcId, characteristicID);
        try
        {
            if ((connID = mProfile.getConnIdForDevice(remoteDevice)) != BleConstants.GATT_INVALID_CONN_ID)
                mProfile.getGattService().getNextChar(connID, charId, null);
            else
                ret = BleConstants.GATT_INVALID_CONN_ID;
        } catch (RemoteException e) {
            Log.d(TAG, "getFirstCharacteristic " + e.toString());
            ret = BleConstants.GATT_ERROR;
        }

        return ret;
    }

    /** @hide */
    protected int readFirstCharDescriptor(BluetoothDevice remoteDevice, BleGattID svcId,
            BleGattID characteristicId)
    {
        int ret = BleConstants.GATT_SUCCESS;
        int connID = BleConstants.GATT_INVALID_CONN_ID;

        Log.d(TAG, "readFirstCharDescriptor address = " + remoteDevice.getAddress());
        BluetoothGattCharID charId = new BluetoothGattCharID(svcId, characteristicId);
        try
        {
            if ((connID = mProfile.getConnIdForDevice(remoteDevice)) != BleConstants.GATT_INVALID_CONN_ID)
                mProfile.getGattService().getFirstCharDescr(connID, charId, null);
            else
                ret = BleConstants.GATT_INVALID_CONN_ID;
        } catch (RemoteException e) {
            Log.d(TAG, "getFirstCharDescriptor " + e.toString());
            ret = BleConstants.GATT_ERROR;
        }

        return ret;
    }

    /** @hide */
    protected int readNextCharDescriptor(BluetoothDevice remoteDevice, BleGattID svcId,
            BleGattID charId, BleGattID descriptorId)
    {
        int ret = BleConstants.GATT_SUCCESS;
        int connID = BleConstants.GATT_INVALID_CONN_ID;
        Log.d(TAG, "readNextCharDescriptor address = " + remoteDevice.getAddress());
        Log.d(TAG, "svcId " + svcId);
        Log.d(TAG, "charId " + charId);
        Log.d(TAG, "descId " + descriptorId);
        if (descriptorId == null)
            descriptorId = charId;

        BluetoothGattCharDescrID descId = new BluetoothGattCharDescrID(svcId, charId,
                descriptorId);
        try
        {
            if ((connID = mProfile.getConnIdForDevice(remoteDevice)) != BleConstants.GATT_INVALID_CONN_ID) {
                try {
                    mProfile.getGattService().getNextCharDescr(connID, descId, descriptorId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
                ret = BleConstants.GATT_INVALID_CONN_ID;
        } catch (Exception e) {
            Log.d(TAG, "getNextCharDescriptor " + e.toString());
            ret = BleConstants.GATT_ERROR;
        }

        return ret;
    }

    /** @hide */
    protected int readCharacteristicValue(int connID, BluetoothGattCharID charID, byte authReq)
    {
        Log.d(TAG, "readCharacteristicValue ");
        int ret = BleConstants.GATT_SUCCESS;
        try {
            mProfile.getGattService().readChar(connID, charID, authReq);
        } catch (RemoteException e) {
            Log.d(TAG, "readCharacteristicValue" + e.toString());
            ret = BleConstants.GATT_ERROR;
        }
        return ret;
    }

    /** @hide */
    protected int readCharDescriptorValue(int connID, BluetoothGattCharDescrID charDescrID,
            byte authReq)
    {
        Log.d(TAG, "readCharDescriptor");
        int ret = BleConstants.GATT_SUCCESS;
        try
        {
            mProfile.getGattService().readCharDescr(connID, charDescrID, authReq);
        } catch (RemoteException e) {
            Log.d(TAG, "readCharacteristicExtProp" + e.toString());
            ret = BleConstants.GATT_ERROR;
        }

        return ret;
    }

    protected int sendIndicationConfirmation(int connId, BluetoothGattCharID charId)
    {
        int ret = BleConstants.GATT_SUCCESS;
        Log.d(TAG, "sendIndicationConfirmation");
        try
        {
            mProfile.getGattService().sendIndConfirm(connId, charId);
        } catch (RemoteException e) {
            Log.d(TAG, "sendIndicationConfirmation" + e.toString());
            ret = BleConstants.GATT_ERROR;
        }
        return ret;
    }

    /** @hide */
    protected void onServiceRefreshed(int connID)
    {
        Log.d(TAG, "onServiceRefreshed");
        onRefreshComplete(mProfile.getDeviceforConnId(connID));
        Log.d(TAG, "onRefreshComplete done");
        mProfile.onServiceRefreshed(this, mProfile.getDeviceforConnId(connID));
        Log.d(TAG, "onServiceRefreshed complete");
    }

    class BleCharacteristicDataCallback extends IBleCharacteristicDataCallback.Stub
    {
        BleCharacteristicDataCallback()
        {
        }

        @Override
        public void onGetFirstCharacteristic(int connID, int status, BluetoothGattID svcId,
                BluetoothGattID characteristicID, int prop)
        {
            Log.d(BleClientService.TAG,
                    "onGetFirstCharacteristic " + characteristicID.toString() + " status = "
                            + status);

            BluetoothDevice device = BleClientService.this.mProfile
                    .getDeviceforConnId(connID);
            if ((device == null)
                    || (BleClientService.this.mProfile.isDeviceDisconnecting(device))) {
                Log.e(BleClientService.TAG,
                        "onGetFirstCharacteristic() - Device is disconnecting...");
                return;
            }

            if (status == BleConstants.GATT_SUCCESS) {
                ServiceData s = BleClientService.this.getServiceData(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        svcId.getInstanceID());
                s.characteristics.clear();

                Log.d(BleClientService.TAG,
                        "characteristic ID = " + characteristicID.toString() + "instance ID = "
                                + characteristicID.getInstanceID());

                BleCharacteristic characteristic = null;
                if (characteristicID.getUuidType() == 16)
                    characteristic = new BleCharacteristic(new BleGattID(
                            characteristicID.getUuid()), prop);
                else
                    characteristic = new BleCharacteristic(new BleGattID(
                            characteristicID.getUuid16()), prop);
                characteristic.setInstanceID(characteristicID.getInstanceID());

                BleClientService.this.onSetCharacteristicAuthRequirement(
                        BleClientService.this.mProfile.getDeviceforConnId(connID), characteristic,
                        svcId.getInstanceID());

                s.characteristics.add(characteristic);
                BleClientService.this.readFirstCharDescriptor(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        BleApiHelper.gatt2BleID(svcId),
                        BleApiHelper.gatt2BleID(characteristicID));
            }
            else
            {
                ServiceData s = BleClientService.this.getNextServiceData(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        svcId.getInstanceID());
                if (null != s)
                {
                    BleClientService.this.readFirstCharacteristic(
                            BleClientService.this.mProfile.getDeviceforConnId(connID),
                            new BleGattID(s.instanceID,
                                    svcId.getUuid(),
                                    svcId.getServiceType()));
                }
                else
                {
                    BleClientService.this.onServiceRefreshed(connID);
                }
            }
        }

        @Override
        public void onGetFirstCharacteristicDescriptor(int connID, int status,
                BluetoothGattID svcId, BluetoothGattID characteristicID,
                BluetoothGattID descriptorID)
        {
            Log.d(BleClientService.TAG, "onGetFirstCharacteristicDescriptor "
                    + "svc: " + svcId + ", char: " + characteristicID + " status = " + status);

            BluetoothDevice device = BleClientService.this.mProfile.getDeviceforConnId(connID);
            if ((device == null)
                    || (BleClientService.this.mProfile.isDeviceDisconnecting(device))) {
                Log.e(BleClientService.TAG,
                        "onGetFirstCharacteristicDescriptor() - Device is disconnecting...");
                return;
            }

            if (status != BleConstants.GATT_SUCCESS) {
                Log.v(TAG, "asking for readNextCharacteristic with " + svcId + ", "
                        + characteristicID);
                readNextCharacteristic(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        BleApiHelper.gatt2BleID(svcId),
                        BleApiHelper.gatt2BleID(characteristicID));
                return;
            }

            Log.d(BleClientService.TAG,
                    "characteristic ID = " + characteristicID.toString() + "instance ID = "
                            + characteristicID.getInstanceID());

            BleCharacteristic characteristic = findCharacteristic(connID,
                    BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID));

            if (descriptorID.getUuidType() == BleConstants.GATT_UUID_TYPE_128) {
                String uuid128 = descriptorID.getUuid().toString();
                if (uuid128.equals(BleConstants.GATT_UUID_CHAR_EXT_PROP))
                    characteristic.addDescriptor(new BleExtProperty());
                else if (uuid128.equals(BleConstants.GATT_UUID_CHAR_CLIENT_CONFIG))
                    characteristic.addDescriptor(new BleClientConfig());
                else if (uuid128.equals(BleConstants.GATT_UUID_CHAR_SRVR_CONFIG))
                    characteristic.addDescriptor(new BleServerConfig());
                else if (uuid128.equals(BleConstants.GATT_UUID_CHAR_PRESENT_FORMAT))
                    characteristic.addDescriptor(new BlePresentationFormat());
                else if (uuid128.equals(BleConstants.GATT_UUID_CHAR_DESCRIPTION))
                    characteristic.addDescriptor(new BleUserDescription());
                else
                    characteristic.addDescriptor(new BleDescriptor(new BleGattID(
                            descriptorID.getUuid())));
            }
            else {
                switch (descriptorID.getUuid16()) {
                    case BleConstants.GATT_UUID_CHAR_EXT_PROP16:
                        characteristic.addDescriptor(new BleExtProperty());
                        break;
                    case BleConstants.GATT_UUID_CHAR_CLIENT_CONFIG16:
                        characteristic.addDescriptor(new BleClientConfig());
                        break;
                    case BleConstants.GATT_UUID_CHAR_SRVR_CONFIG16:
                        characteristic.addDescriptor(new BleServerConfig());
                        break;
                    case BleConstants.GATT_UUID_CHAR_PRESENT_FORMAT16:
                        characteristic.addDescriptor(new BlePresentationFormat());
                        break;
                    case BleConstants.GATT_UUID_CHAR_DESCRIPTION16:
                        characteristic.addDescriptor(new BleUserDescription());
                        break;
                    default:
                        characteristic.addDescriptor(new BleDescriptor(new BleGattID(
                                descriptorID.getUuid16())));
                }

            }

            BleClientService.this.readNextCharDescriptor(
                    BleClientService.this.mProfile.getDeviceforConnId(connID),
                    BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID),
                    BleApiHelper.gatt2BleID(descriptorID));

        }

        @Override
        public void onGetNextCharacteristicDescriptor(int connID, int status,
                BluetoothGattID svcId, BluetoothGattID characteristicID,
                BluetoothGattID descriptorID)
        {
            Log.d(BleClientService.TAG, "onGetNextCharacteristicDescriptor");
            Log.d(BleClientService.TAG, "onGetNextCharacteristicDescriptor "
                    + characteristicID.toString() + " status = " + status);

            BluetoothDevice device = BleClientService.this.mProfile
                    .getDeviceforConnId(connID);
            if ((device == null)
                    || (BleClientService.this.mProfile.isDeviceDisconnecting(device))) {
                Log.e(BleClientService.TAG,
                        "onGetNextCharacteristicDescriptor() - Device is disconnecting...");
                return;
            }

            if (status != BleConstants.GATT_SUCCESS) {
                BleClientService.this.readNextCharacteristic(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        BleApiHelper.gatt2BleID(svcId),
                        BleApiHelper.gatt2BleID(characteristicID));
                return;
            }

            Log.d(BleClientService.TAG,
                    "characteristic ID = " + characteristicID.toString() + "instance ID = "
                            + characteristicID.getInstanceID());

            BleCharacteristic characteristic = findCharacteristic(connID,
                    BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID));

            if (descriptorID.getUuidType() == BleConstants.GATT_UUID_TYPE_128) {
                characteristic.addDescriptor(new BleDescriptor(new BleGattID(
                        descriptorID.getUuid())));
            }
            else {
                characteristic.addDescriptor(new BleDescriptor(new BleGattID(
                        descriptorID.getUuid16())));
            }

            BleClientService.this.readNextCharDescriptor(
                    BleClientService.this.mProfile.getDeviceforConnId(connID),
                    BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID),
                    BleApiHelper.gatt2BleID(descriptorID));

        }

        @Override
        public void onGetNextCharacteristic(int connID, int status, BluetoothGattID svcId,
                BluetoothGattID characteristicID, int prop)
        {
            Log.d(BleClientService.TAG,
                    "onGetNextCharacteristic status = " + status);
            if (characteristicID != null)
                Log.d(BleClientService.TAG, "char: " + characteristicID.toString()
                        + "instance id = " + characteristicID.getInstanceID());
            else
                Log.d(BleClientService.TAG, "no char");

            BluetoothDevice device = mProfile.getDeviceforConnId(connID);
            if ((device == null)
                    || (mProfile.isDeviceDisconnecting(device))) {
                Log.e(BleClientService.TAG,
                        "onGetNextCharacteristic() - Device is disconnecting...");
                return;
            }

            if (status == BleConstants.GATT_SUCCESS) {
                ServiceData s = BleClientService.this.getServiceData(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        svcId.getInstanceID());

                Log.d(BleClientService.TAG,
                        "characteristic ID = " + characteristicID.toString() + " instance ID = "
                                + characteristicID.getInstanceID());

                BleCharacteristic characteristic = null;
                if (characteristicID.getUuidType() == BleConstants.GATT_UUID_TYPE_128)
                    characteristic = new BleCharacteristic(new BleGattID(
                            characteristicID.getUuid()), prop);
                else
                    characteristic = new BleCharacteristic(new BleGattID(
                            characteristicID.getUuid16()), prop);
                characteristic.setInstanceID(characteristicID.getInstanceID());

                BleClientService.this.onSetCharacteristicAuthRequirement(
                        BleClientService.this.mProfile.getDeviceforConnId(connID), characteristic,
                        svcId.getInstanceID());

                s.characteristics.add(characteristic);

                BleClientService.this.readFirstCharDescriptor(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        BleApiHelper.gatt2BleID(svcId),
                        BleApiHelper.gatt2BleID(characteristicID));
            }
            else
            {

                Log.v(TAG, "status != SUCCESS, service data");
                ServiceData s = BleClientService.this.getNextServiceData(
                        mProfile.getDeviceforConnId(connID),
                        svcId.getInstanceID());
                Log.v(TAG, "value " + s);
                if (null != s)
                {
                    BleClientService.this.readFirstCharacteristic(
                            BleClientService.this.mProfile.getDeviceforConnId(connID),
                            new BleGattID(s.instanceID, BleClientService.this.getServiceId()
                                    .getUuid(), BleClientService.this.getServiceId()
                                    .getServiceType()));
                }
                else
                {
                    onServiceRefreshed(connID);
                }
            }
        }

        @Override
        public void onReadCharacteristicValue(int connID, int status, BluetoothGattID svcId,
                BluetoothGattID characteristicID, byte[] data)
        {
            Log.d(BleClientService.TAG, "onReadCharacteristicValue status = " + status);
            Log.d(BleClientService.TAG, "charID = " + characteristicID.toString());

            BleCharacteristic c = findCharacteristic(connID,
                    BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID));

            if (c == null) {
                Log.e(BleClientService.TAG,
                        "onReadCharacteristicValue() - Characteristic not found");
            }

            if (status != BleConstants.GATT_SUCCESS) {
                BleClientService.this.onReadCharacteristicComplete(status,
                        BleClientService.this.mProfile.getDeviceforConnId(connID), c);
                return;
            }

            Log.v(TAG, "setting read char value");
            c.setValue(data);
            if (BleClientService.this.mReadDescriptors)
            {
                ArrayList<BleDescriptor> descList = c.getDirtyDescQueue();
                if (!descList.isEmpty()) {
                    BleDescriptor descObj = descList.get(0);
                    BleClientService.this.readCharDescriptorValue(
                            connID,
                            new BluetoothGattCharDescrID(svcId, characteristicID, descObj
                                    .getID()), descObj.getAuthReq());
                }
                else {
                    BleClientService.this.onReadCharacteristicComplete(
                            BleClientService.this.mProfile.getDeviceforConnId(connID), c);
                }
            } else {
                BleClientService.this.onReadCharacteristicComplete(
                        BleClientService.this.mProfile.getDeviceforConnId(connID), c);
            }
        }

        @Override
        public void onReadCharDescriptorValue(int connID, int status, BluetoothGattID svcId,
                BluetoothGattID characteristicID, BluetoothGattID descr, byte[] data)
        {
            Log.d(BleClientService.TAG, "onReadCharDescriptorValue svcID = "
                    + svcId + " CharID = " + characteristicID + " descID = "  + descr + " status = " + status);

            BleDescriptor d = findDescriptor(connID, BleApiHelper.gatt2BleID(svcId),
                    BleApiHelper.gatt2BleID(characteristicID), BleApiHelper.gatt2BleID(descr));

            BleCharacteristic c = findCharacteristic(connID,
                    BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID));

            
            if (d != null)
                if (status == 0) {
                    d.setValue(data);

                    if (BleClientService.this.mReadDescriptors)
                    {
                        if (c != null)
                        {
                            c.updateDirtyDescQueue();
                            ArrayList<BleDescriptor> descList = c.getDirtyDescQueue();
                            if (!descList.isEmpty()) {
                                BleDescriptor descObj = descList.get(0);
                                BleClientService.this.readCharDescriptorValue(connID,
                                        new BluetoothGattCharDescrID(svcId, characteristicID,
                                                descObj.getID()), descObj.getAuthReq());
                            }
                            else
                            {
                                BleClientService.this.mReadDescriptors = false;
                                BleClientService.this.onReadCharacteristicComplete(
                                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                                        c);
                            }
                        }
                    }
                }
                else if (c != null) {
                    BleClientService.this.onReadCharacteristicComplete(status,
                            BleClientService.this.mProfile.getDeviceforConnId(connID), c);
                }
        }

        @Override
        public void onWriteCharValue(int connID, int status, BluetoothGattID svcId,
                BluetoothGattID characteristicID)
        {
            Log.d(BleClientService.TAG, "onWriteCharValue connID " + connID + " status "
                    + status);
            BleCharacteristic c = findCharacteristic(connID,
                    BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID));

            if (status == 0) {
                BleClientService.this.writeCharacteristic(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        svcId.getInstanceID(), c);
            }
            else
                BleClientService.this.onWriteCharacteristicComplete(status,
                        BleClientService.this.mProfile.getDeviceforConnId(connID), c);
        }

        @Override
        public void onWriteCharDescrValue(int connID, int status, BluetoothGattID svcId,
                BluetoothGattID characteristicID, BluetoothGattID descr)
        {
            Log.d(BleClientService.TAG, "onWriteCharDescrValue status=" + status);

            BleCharacteristic c = findCharacteristic(connID,
                    BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID));

            if (status == 0) {
                BleClientService.this.writeCharacteristic(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        svcId.getInstanceID(), c);
            }
            else
                BleClientService.this.onWriteCharacteristicComplete(status,
                        BleClientService.this.mProfile.getDeviceforConnId(connID), c);
        }

        BleCharacteristic findNextCharacteristic(int connID, BleCharacteristic c,
                int instanceID)
        {
            Log.d(BleClientService.TAG,
                    "findNextCharacteristic " + connID + " current characteristic :"
                            + c.getID().toString() + " instance id = " + c.getInstanceID());

            ServiceData s = BleClientService.this.getServiceData(
                    BleClientService.this.mProfile.getDeviceforConnId(connID), instanceID);

            int i;
            for (i = 0; i < s.characteristics.size(); i++) {
                BleCharacteristic cTemp = s.characteristics.get(i);
                if (c.getID().equals(cTemp.getID()))
                {
                    break;
                }
            }
            if (i + 1 < s.characteristics.size()) {
                Log.d(BleClientService.TAG, "findNextCharacteristic position =  " + i
                        + "connID = " + connID + " next characteristic :"
                        + s.characteristics.get(i + 1).getID().toString());

                return s.characteristics.get(i + 1);
            }
            return null;
        }

        BleCharacteristic findCharacteristic(int connID, BleGattID svcId,
                BleGattID characteristicID)
        {
            Log.d(BleClientService.TAG,
                    "findCharacteristic charID = [" + characteristicID.toString()
                            + "] instance ID = [" + characteristicID.getInstanceID() + "]");

            ServiceData s = BleClientService.this.getServiceData(
                    BleClientService.this.mProfile.getDeviceforConnId(connID),
                    svcId.getInstanceID());

            for (int i = 0; i < s.characteristics.size(); i++) {
                BleCharacteristic c = s.characteristics.get(i);
                if ((!c.getID().toString().equals(characteristicID.toString()))
                        || (c.getInstanceID() != characteristicID.getInstanceID()))
                    continue;
                Log.d(BleClientService.TAG, "findCharacteristic - found");
                return c;
            }

            return null;
        }

        BleDescriptor findDescriptor(int connID, BleGattID svcId, BleGattID characteristicID,
                BleGattID descriptorID)
        {
            Log.d(BleClientService.TAG,
                    "findDescriptor charID = " + characteristicID.toString()
                            + ":" + characteristicID.getInstanceID() + 
                            ", descID = "  + descriptorID.toString() + 
                            ":" + descriptorID.getInstanceID());

            ServiceData s = BleClientService.this.getServiceData(
                    BleClientService.this.mProfile.getDeviceforConnId(connID),
                    svcId.getInstanceID());

            BleCharacteristic charObj = findCharacteristic(connID, svcId, characteristicID);
                    
            if (charObj != null) {
                for (int i = 0; i < charObj.getAllDescriptors().size(); i++) {
                    BleDescriptor d = charObj.getAllDescriptors().get(i);
                    if (d.getID().equals(descriptorID)) {
                        Log.d(BleClientService.TAG, "findDescriptor - found");
                        return d;
                    }
                }
            }

            Log.v(TAG, "no descriptor match");
            return null;
        }

        @Override
        public void onRegForNotifications(int connId, int status, BluetoothGattID svcId,
                BluetoothGattID charId)
        {
            Log.d(BleClientService.TAG, "onRegForNotifications " + status);
        }

        @Override
        public void onUnregisterNotifications(int connId, int status, BluetoothGattID svcId,
                BluetoothGattID charId)
        {
            Log.d(BleClientService.TAG, "onUnregisterNotifications" + status);
        }

        @Override
        public void onNotify(int connId, String address, BluetoothGattID svcId,
                BluetoothGattID characteristicID, boolean isNotify, byte[] data)
        {
            Log.d(BleClientService.TAG, "onNotify " + connId + " " + address);

            BleCharacteristic c = findCharacteristic(connId,
                    BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID));

            if (c != null)
            {
                c.setValue(data);
                BleClientService.this.onCharacteristicChanged(
                        BleClientService.this.mProfile.getDeviceforConnId(connId), c);
            } else {
                Log.d(BleClientService.TAG, "onNotify Characteristic not found" + connId
                        + " " + address);
            }

            if (!isNotify)
            {
                BluetoothGattCharID charId = new BluetoothGattCharID(svcId,
                        characteristicID);

                BleClientService.this.sendIndicationConfirmation(connId, charId);
            }
        }

        @Override
        public void onGetFirstIncludedService(int connID, int status, BluetoothGattID svcId,
                BluetoothGattID inclsvcId)
        {
            Log.d(BleClientService.TAG, "onGetFirstIncludedService");

            if (status == 0) {
                BluetoothGattInclSrvcID cursvcId = new BluetoothGattInclSrvcID(svcId,
                        inclsvcId);

                BleClientService.this.getNextIncludedService(
                        BleClientService.this.mProfile.getDeviceforConnId(connID), cursvcId);
            }
            else {
                BleClientService.this.onServiceRefreshed(connID);
            }
        }

        @Override
        public void onGetNextIncludedService(int connID, int status, BluetoothGattID svcId,
                BluetoothGattID inclsvcId)
        {
            Log.d(BleClientService.TAG, "onGetNextIncludedService");
            if (status == 0) {
                BluetoothGattInclSrvcID cursvcId = new BluetoothGattInclSrvcID(svcId,
                        inclsvcId);

                BleClientService.this.getNextIncludedService(
                        BleClientService.this.mProfile.getDeviceforConnId(connID), cursvcId);
            }
            else {
                BleClientService.this.onServiceRefreshed(connID);
            }
        }

    }

    class ServiceData
    {
        public int instanceID = -1;
        public int writeIndex = -1;
        public int serviceType = -1;

        public ArrayList<BleCharacteristic> characteristics = new ArrayList<BleCharacteristic>();

        ServiceData()
        {
        }

    }

}
