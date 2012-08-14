
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

public abstract class BleClientService
{
    private static String TAG = "BleClientService";

    private BleClientProfile mProfile = null;
    private BleGattID mServiceId = null;
    private HashMap<BluetoothDevice, ArrayList<ServiceData>> mdeviceToDataMap = new HashMap();
    private BleCharacteristicDataCallback mCallback = new BleCharacteristicDataCallback();
    private boolean mReadDescriptors = true;

    public BleClientService(BleGattID serviceId)
    {
        this.mServiceId = serviceId;
        if (this.mServiceId.getServiceType() == -1)
            this.mServiceId.setServiceType(0);
    }

    public BleGattID getServiceId()
    {
        return this.mServiceId;
    }

    public int writeCharacteristic(BluetoothDevice remoteDevice, int instanceId,
            BleCharacteristic characteristic)
    {
        Log.d(TAG, "writeCharacteristic");

        int ret = 0;
        int connID = 65535;

        if ((connID = this.mProfile.getConnIdForDevice(remoteDevice)) == 65535) {
            return 1;
        }
        ServiceData s = getServiceData(remoteDevice, instanceId);
        if (s != null)
        {
            s.writeIndex = s.characteristics.indexOf(characteristic);

            if ((s.characteristics != null) && (s.writeIndex >= 0)) {
                Log.d(TAG, "writeCharacteristic found characteristic in array:");
                Log.d(
                        TAG,
                        "Service = [instanceID = " + instanceId + " svcid = "
                                + this.mServiceId.toString() + " serviceType = "
                                + this.mServiceId.getServiceType());
                Log.d(TAG, "CharID = [instanceID = " + characteristic.getInstanceID()
                        + " svcid = " + characteristic.getID().toString());
                BleGattID svcId = new BleGattID(instanceId, this.mServiceId.getUuid(),
                        this.mServiceId.getServiceType());
                BleGattID cID = characteristic.getID();
                BluetoothGattCharID charID = new BluetoothGattCharID(svcId, cID);
                try
                {
                    if (characteristic.isDirty()) {
                        if (characteristic.getWriteType() == 0)
                            characteristic.setWriteType(2);
                        characteristic.setDirty(false);
                        this.mProfile.getGattService().writeCharValue(connID, charID,
                                characteristic.getWriteType(), characteristic.getAuthReq(),
                                characteristic.getValue());
                    }
                    else if (!characteristic.getDirtyDescQueue().isEmpty()) {
                        ArrayList descList = characteristic.getDirtyDescQueue();
                        BleDescriptor descObj = (BleDescriptor) descList.get(0);

                        Log.d(TAG, "writeCharacteristic - descriptor = "
                                + descObj.getID().toString());
                        if (descObj.isDirty()) {
                            BluetoothGattCharDescrID descID = new BluetoothGattCharDescrID(
                                    svcId, cID, descObj.getID());
                            descObj.setDirty(false);
                            this.mProfile.getGattService().writeCharDescrValue(connID,
                                    descID, descObj.getWriteType(), descObj.getAuthReq(),
                                    descObj.getValue());
                        }

                    }
                    else
                    {
                        onWriteCharacteristicComplete(0, remoteDevice, characteristic);
                    }
                } catch (RemoteException e) {
                    ret = 1;
                }
            } else {
                onWriteCharacteristicComplete(0, remoteDevice, characteristic);
            }
        }
        return ret;
    }

    public ArrayList<BleCharacteristic> getAllCharacteristics(BluetoothDevice remoteDevice)
    {
        Log.d(TAG, "getAllCharacteristics");

        ServiceData s = getServiceData(remoteDevice, this.mServiceId.getInstanceID());
        if (null != s) {
            return s.characteristics;
        }
        return null;
    }

    public BleCharacteristic getCharacteristic(BluetoothDevice remoteDevice,
            BleGattID characteristicID)
    {
        Log.d(TAG, "getCharacteristic charID = [" + characteristicID.toString()
                + "] instance ID = [" + characteristicID.getInstanceID() + "]");
        ServiceData s = getServiceData(remoteDevice, this.mServiceId.getInstanceID());
        if (s == null) {
            Log.d(TAG, "getCharacterisic - Service data not found");
            return null;
        }
        for (int i = 0; i < s.characteristics.size(); i++) {
            BleCharacteristic c = (BleCharacteristic) s.characteristics.get(i);
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

    public int[] getAllServiceInstanceIds(BluetoothDevice remoteDevice)
    {
        Log.d(TAG, "getAllServiceInstanceIds");
        ArrayList s = (ArrayList) this.mdeviceToDataMap.get(remoteDevice);
        if (s != null) {
            int[] instanceIds = new int[s.size()];

            for (int i = 0; i < s.size(); i++) {
                instanceIds[i] = ((ServiceData) s.get(0)).instanceID;
            }

            return instanceIds;
        }

        return null;
    }

    public void refresh(BluetoothDevice remoteDevice)
    {
        Log.d(TAG, "Refresh (" + this.mServiceId.toString() + ")");

        ArrayList s = (ArrayList) this.mdeviceToDataMap.get(remoteDevice);
        if (s != null) {
            ServiceData sd = (ServiceData) s.get(0);
            Log.e(TAG,
                    "refresh() - Service data found, reading first characteristic... (serviceType = "
                            + sd.serviceType + ")");
            readFirstCharacteristic(remoteDevice, new BleGattID(sd.instanceID,
                    getServiceId().getUuid(), sd.serviceType));
        } else {
            Log.e(TAG, "refresh() - Service data not found");
        }
    }

    public int readCharacteristic(BluetoothDevice remoteDevice,
            BleCharacteristic characteristic)
    {
        int ret = 0;
        int connID = 65535;
        Log.d(TAG,
                "readCharacteristic - svc UUID = " + getServiceId().getUuid().toString()
                        + ", characteristic = " + characteristic.getID());

        BluetoothGattCharID charID = new BluetoothGattCharID(new BleGattID(
                characteristic.getInstanceID(), getServiceId().getUuid(), getServiceId()
                        .getServiceType()), characteristic.getID());

        if ((connID = this.mProfile.getConnIdForDevice(remoteDevice)) != 65535)
            readCharacteristicValue(connID, charID, characteristic.getAuthReq());
        else {
            ret = 1;
        }
        return ret;
    }

    public void onWriteCharacteristicComplete(int status, BluetoothDevice remoteDevice,
            BleCharacteristic characteristic)
    {
        Log.d(TAG, "onWriteCharacteristicComplete 1 status=" + status);
        if (status == 5) {
            Log.d(TAG,
                    "onWriteCharacteristicComplete rcv GATT_INSUF_AUTHENTICATION issue createBond");
            if (remoteDevice.createBond())
                Log.d(TAG, "onWriteCharacteristicComplete createBond request Accepted");
            else {
                Log.e(TAG, "onWriteCharacteristicComplete createBond request FAILED");
            }
        }
        else if (status == 15) {
            Log
                    .d(TAG,
                            "onWriteCharacteristicComplete rcv GATT_INSUF_ENCRYPTION check link can be encrypt or not");
            if (remoteDevice.getBondState() == 12) {
                Log
                        .d(TAG,
                                "device bonded start to encrypt the link.  !!!! This case should not happen !!!!");
            } else {
                Log.d(TAG, "device is Not bonded start to pair");
                remoteDevice.createBond();
            }
        }
    }

    public void onCharacteristicChanged(BluetoothDevice remoteDevice,
            BleCharacteristic characteristic)
    {
        Log.d(TAG, "onCharacteristicChanged");
    }

    public void onRefreshComplete(BluetoothDevice remoteDevice)
    {
        Log.d(TAG, "onRefreshComplete");
    }

    public void onSetCharacteristicAuthRequirement(BluetoothDevice remoteDevice,
            BleCharacteristic characteristic, int instanceID)
    {
        Log.d(TAG, "onSetCharacteristicAuthRequirement");
    }

    public void onReadCharacteristicComplete(BluetoothDevice remoteDevice,
            BleCharacteristic characteristic)
    {
        Log.d(TAG, "onReadCharacteristicComplete");
    }

    public void onReadCharacteristicComplete(int status, BluetoothDevice remoteDevice,
            BleCharacteristic characteristic)
    {
        Log.d(TAG, "onReadCharacteristicComplete status=" + status);
        if (status == 5) {
            Log.d(TAG,
                    "onReadCharacteristicComplete rcv GATT_INSUF_AUTHENTICATION issue createBond");
            remoteDevice.createBond();
        } else if (status == 15) {
            Log
                    .d(TAG,
                            "onReadCharacteristicComplete rcv GATT_INSUF_ENCRYPTION check link can be encrypt or not");
            if (remoteDevice.getBondState() == 12) {
                Log
                        .d(TAG,
                                "device bonded start to encrypt the link.  !!!! This case should not happen !!!!");
            } else {
                Log.d(TAG, "device is Not bonded start to pair");
                remoteDevice.createBond();
            }
        }
    }

    public int registerForNotification(BluetoothDevice remoteDevice, int instanceID,
            BleGattID characteristicID)
    {
        int ret = 0;
        Log.d(TAG, "registerForNotification address: " + remoteDevice.getAddress());
        try {
            BleGattID svcId = new BleGattID(instanceID, getServiceId().getUuid(),
                    getServiceId().getServiceType());
            BluetoothGattCharID charId = new BluetoothGattCharID(svcId, characteristicID);

            this.mProfile.getGattService().registerForNotifications(
                    this.mProfile.getClientIf(), remoteDevice.getAddress(), charId);
        } catch (RemoteException e) {
            ret = 1;
        }
        return ret;
    }

    public int unregisterNotification(BluetoothDevice remoteDevice, int instanceID,
            BleGattID characteristicID)
    {
        int ret = 0;
        Log.d(TAG, "unregisterNotification address: " + remoteDevice.getAddress());
        try {
            BleGattID svcId = new BleGattID(instanceID, getServiceId().getUuid(),
                    getServiceId().getServiceType());
            BluetoothGattCharID charId = new BluetoothGattCharID(svcId, characteristicID);

            this.mProfile.getGattService().deregisterForNotifications(
                    this.mProfile.getClientIf(), remoteDevice.getAddress(), charId);
        } catch (RemoteException e) {
            ret = 1;
        }

        return ret;
    }

    void setInstanceID(BluetoothDevice remoteDevice, int instanceId)
    {
        Log.d(TAG, "setInstanceID address = " + remoteDevice.getAddress());

        ServiceData sd = getServiceData(remoteDevice, instanceId);
        this.mServiceId.setInstanceId(instanceId);
        if (null == sd) {
            Log.d(TAG, "setInstanceID setting instance id (" + instanceId + ")");

            sd = new ServiceData();
            sd.instanceID = this.mServiceId.getInstanceID();
            sd.serviceType = this.mServiceId.getServiceType();
            ArrayList s = (ArrayList) this.mdeviceToDataMap.get(remoteDevice);
            if (null == s) {
                s = new ArrayList();
            }
            s.add(sd);
            this.mdeviceToDataMap.put(remoteDevice, s);
        }

        sd.instanceID = this.mServiceId.getInstanceID();
        sd.serviceType = this.mServiceId.getServiceType();
        try
        {
            int connID = 65535;

            if ((connID = this.mProfile.getConnIdForDevice(remoteDevice)) != 65535)
                this.mProfile.getGattService().registerServiceDataCallback(connID,
                        this.mServiceId, remoteDevice.getAddress(), this.mCallback);
        } catch (RemoteException e)
        {
            Log.d(TAG, e.toString());
        }
    }

    void removeInstanceID(BluetoothDevice remoteDevice, int instanceID)
    {
    }

    void setProfile(BleClientProfile profile)
    {
        this.mProfile = profile;
    }

    protected ServiceData getServiceData(BluetoothDevice remoteDevice, int instanceID)
    {
        Log.d(TAG, "getServiceData address = " + remoteDevice.getAddress()
                + " instanceID = " + instanceID);

        ServiceData sData = null;
        ArrayList s = (ArrayList) this.mdeviceToDataMap.get(remoteDevice);
        if (s != null) {
            for (int i = 0; i < s.size(); i++) {
                if (((ServiceData) s.get(i)).instanceID == instanceID) {
                    sData = (ServiceData) s.get(i);
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
        ArrayList s = (ArrayList) this.mdeviceToDataMap.get(remoteDevice);
        if (s != null) {
            for (int i = 0; i < s.size(); i++) {
                if (((ServiceData) s.get(i)).instanceID != currentInstanceID)
                    continue;
                if (s.size() > i + 1) {
                    sData = (ServiceData) s.get(i + 1);
                    break;
                }
            }

        }

        return sData;
    }

    protected int getFirstIncludedService(BluetoothDevice remoteDevice, int instanceID)
    {
        int ret = 0;
        int connID = 65535;

        Log.d(TAG, "getFirstIncludedService address = " + remoteDevice.getAddress());
        ServiceData sd = getServiceData(remoteDevice, instanceID);
        try {
            if ((connID = this.mProfile.getConnIdForDevice(remoteDevice)) != 65535) {
                BleGattID svcId = new BleGattID(sd.instanceID, this.mServiceId.getUuid(),
                        this.mServiceId.getServiceType());
                this.mProfile.getGattService().getFirstIncludedService(connID, svcId,
                        null);
            }
            else {
                ret = 1;
            }
        } catch (RemoteException e) {
            Log.d(TAG, "getFirstIncludedService " + e.toString());
            ret = 1;
        }

        return ret;
    }

    protected int getNextIncludedService(BluetoothDevice remoteDevice,
            BluetoothGattInclSrvcID inclsvcId)
    {
        int ret = 0;
        int connID = 65535;

        Log.d(TAG, "getNextIncludedService address = " + remoteDevice.getAddress());
        try
        {
            if ((connID = this.mProfile.getConnIdForDevice(remoteDevice)) != 65535)
                this.mProfile.getGattService().getNextIncludedService(connID, inclsvcId,
                        null);
            else
                ret = 1;
        } catch (RemoteException e) {
            Log.d(TAG, "getNextIncludedService " + e.toString());
            ret = 1;
        }

        return ret;
    }

    protected int readFirstCharacteristic(BluetoothDevice remoteDevice, BleGattID svcId)
    {
        int ret = 0;
        int connID = 65535;

        Log.d(TAG, "readFirstCharacteristic address = " + remoteDevice.getAddress());
        try
        {
            if ((connID = this.mProfile.getConnIdForDevice(remoteDevice)) != 65535)
                this.mProfile.getGattService().getFirstChar(connID, svcId, null);
            else
                ret = 1;
        } catch (RemoteException e) {
            Log.d(TAG, "getFirstCharacteristic " + e.toString());
            ret = 1;
        }

        return ret;
    }

    protected int readNextCharacteristic(BluetoothDevice remoteDevice, BleGattID svcId,
            BleGattID characteristicID)
    {
        Log.d(TAG,
                "readNextCharacteristic characteristicID = " + characteristicID.toString()
                        + " char inst id = " + characteristicID.getInstanceID());

        int ret = 0;
        int connID = 65535;
        ServiceData sd = getServiceData(remoteDevice, svcId.getInstanceID());
        BluetoothGattCharID charId = new BluetoothGattCharID(svcId, characteristicID);
        try
        {
            if ((connID = this.mProfile.getConnIdForDevice(remoteDevice)) != 65535)
                this.mProfile.getGattService().getNextChar(connID, charId, null);
            else
                ret = 1;
        } catch (RemoteException e) {
            Log.d(TAG, "getFirstCharacteristic " + e.toString());
            ret = 1;
        }

        return ret;
    }

    protected int readFirstCharDescriptor(BluetoothDevice remoteDevice, BleGattID svcId,
            BleGattID characteristicId)
    {
        int ret = 0;
        int connID = 65535;

        Log.d(TAG, "readFirstCharDescriptor address = " + remoteDevice.getAddress());
        BluetoothGattCharID charId = new BluetoothGattCharID(svcId, characteristicId);
        try
        {
            if ((connID = this.mProfile.getConnIdForDevice(remoteDevice)) != 65535)
                this.mProfile.getGattService().getFirstCharDescr(connID, charId, null);
            else
                ret = 1;
        } catch (RemoteException e) {
            Log.d(TAG, "getFirstCharDescriptor " + e.toString());
            ret = 1;
        }

        return ret;
    }

    protected int readNextCharDescriptor(BluetoothDevice remoteDevice, BleGattID svcId,
            BleGattID charId, BleGattID descriptorId)
    {
        int ret = 0;
        int connID = 65535;
        Log.d(TAG, "readNextCharDescriptor address = " + remoteDevice.getAddress());
        BluetoothGattCharDescrID descId = new BluetoothGattCharDescrID(svcId, charId,
                descriptorId);
        try
        {
            if ((connID = this.mProfile.getConnIdForDevice(remoteDevice)) != 65535)
                this.mProfile.getGattService().getNextCharDescr(connID, descId, null);
            else
                ret = 1;
        } catch (RemoteException e) {
            Log.d(TAG, "getNextCharDescriptor " + e.toString());
            ret = 1;
        }

        return ret;
    }

    protected int readCharacteristicValue(int connID, BluetoothGattCharID charID, byte authReq)
    {
        Log.d(TAG, "readCharacteristicValue ");
        int ret = 0;
        try {
            this.mProfile.getGattService().readChar(connID, charID, authReq);
        } catch (RemoteException e) {
            Log.d(TAG, "readCharacteristicValue" + e.toString());
            ret = 1;
        }
        return ret;
    }

    protected int readCharDescriptorValue(int connID, BluetoothGattCharDescrID charDescrID,
            byte authReq)
    {
        Log.d(TAG, "readCharDescriptor");
        int ret = 0;
        try
        {
            this.mProfile.getGattService().readCharDescr(connID, charDescrID, authReq);
        } catch (RemoteException e) {
            Log.d(TAG, "readCharacteristicExtProp" + e.toString());
            ret = 1;
        }

        return ret;
    }

    protected int sendIndicationConfirmation(int connId, BluetoothGattCharID charId)
    {
        int ret = 0;
        Log.d(TAG, "sendIndicationConfirmation");
        try
        {
            this.mProfile.getGattService().sendIndConfirm(connId, charId);
        } catch (RemoteException e) {
            Log.d(TAG, "sendIndicationConfirmation" + e.toString());
            ret = 1;
        }
        return ret;
    }

    protected void onServiceRefreshed(int connID)
    {
        Log.d(TAG, "onServiceRefreshed");
        onRefreshComplete(this.mProfile.getDeviceforConnId(connID));
        this.mProfile.onServiceRefreshed(this, this.mProfile.getDeviceforConnId(connID));
    }

    class BleCharacteristicDataCallback extends IBleCharacteristicDataCallback.Stub
    {
        BleCharacteristicDataCallback()
        {
        }

        public void onGetFirstCharacteristic(int connID, int status, BluetoothGattID svcId,
                BluetoothGattID characteristicID)
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

            if (status == 0) {
                BleClientService.ServiceData s = BleClientService.this.getServiceData(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        svcId.getInstanceID());
                s.characteristics.clear();

                Log.d(BleClientService.TAG,
                        "characteristic ID = " + characteristicID.toString() + "instance ID = "
                                + characteristicID.getInstanceID());

                BleCharacteristic characteristic = null;
                if (characteristicID.getUuidType() == 16)
                    characteristic = new BleCharacteristic(new BleGattID(
                            characteristicID.getUuid()));
                else
                    characteristic = new BleCharacteristic(new BleGattID(
                            characteristicID.getUuid16()));
                characteristic.setInstanceID(characteristicID.getInstanceID());

                BleClientService.this.onSetCharacteristicAuthRequirement(
                        BleClientService.this.mProfile.getDeviceforConnId(connID), characteristic,
                        svcId.getInstanceID());

                s.characteristics.add(characteristic);
                BleClientService.this.readFirstCharDescriptor(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID));
            }
            else
            {
                BleClientService.ServiceData s = BleClientService.this.getNextServiceData(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        svcId.getInstanceID());
                if (null != s)
                {
                    BleClientService.this.readFirstCharacteristic(
                            BleClientService.this.mProfile.getDeviceforConnId(connID),
                            new BleGattID(s.instanceID, svcId.getUuid(), svcId.getServiceType()));
                }
                else
                {
                    BleClientService.this.onServiceRefreshed(connID);
                }
            }
        }

        public void onGetFirstCharacteristicDescriptor(int connID, int status,
                BluetoothGattID svcId, BluetoothGattID characteristicID,
                BluetoothGattID descriptorID)
        {
            Log.d(BleClientService.TAG, "onGetFirstCharacteristicDescriptor "
                    + characteristicID.toString() + " status = " + status);

            BluetoothDevice device = BleClientService.this.mProfile
                    .getDeviceforConnId(connID);
            if ((device == null)
                    || (BleClientService.this.mProfile.isDeviceDisconnecting(device))) {
                Log.e(BleClientService.TAG,
                        "onGetFirstCharacteristicDescriptor() - Device is disconnecting...");
                return;
            }

            if (status == 0)
            {
                Log.d(BleClientService.TAG,
                        "characteristic ID = " + characteristicID.toString() + "instance ID = "
                                + characteristicID.getInstanceID());

                BleCharacteristic characteristic = findCharacteristic(connID,
                        BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID));

                if (descriptorID.getUuidType() == 16) {
                    String uuid128 = descriptorID.getUuid().toString();
                    if (uuid128.equals("00002900-0000-1000-8000-00805f9b34fb"))
                        characteristic.addDescriptor(new BleExtProperty());
                    else if (uuid128.equals("00002902-0000-1000-8000-00805f9b34fb"))
                        characteristic.addDescriptor(new BleClientConfig());
                    else if (uuid128.equals("00002903-0000-1000-8000-00805f9b34fb"))
                        characteristic.addDescriptor(new BleServerConfig());
                    else if (uuid128.equals("00002904-0000-1000-8000-00805f9b34fb"))
                        characteristic.addDescriptor(new BlePresentationFormat());
                    else if (uuid128.equals("00002901-0000-1000-8000-00805f9b34fb"))
                        characteristic.addDescriptor(new BleUserDescription());
                    else
                        characteristic.addDescriptor(new BleDescriptor(new BleGattID(
                                descriptorID.getUuid())));
                }
                else {
                    switch (descriptorID.getUuid16()) {
                        case 10496:
                            characteristic.addDescriptor(new BleExtProperty());
                            break;
                        case 10498:
                            characteristic.addDescriptor(new BleClientConfig());
                            break;
                        case 10499:
                            characteristic.addDescriptor(new BleServerConfig());
                            break;
                        case 10500:
                            characteristic.addDescriptor(new BlePresentationFormat());
                            break;
                        case 10497:
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
            else
            {
                BleClientService.this.readNextCharacteristic(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID));
            }
        }

        public void onGetNextCharacteristicDescriptor(int connID, int status,
                BluetoothGattID svcId, BluetoothGattID characteristicID,
                BluetoothGattID descriptorID)
        {
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

            if (status == 0)
            {
                Log.d(BleClientService.TAG,
                        "characteristic ID = " + characteristicID.toString() + "instance ID = "
                                + characteristicID.getInstanceID());

                BleCharacteristic characteristic = findCharacteristic(connID,
                        BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID));

                if (descriptorID.getUuidType() == 16) {
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
            else
            {
                BleClientService.this.readNextCharacteristic(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID));
            }
        }

        public void onGetNextCharacteristic(int connID, int status, BluetoothGattID svcId,
                BluetoothGattID characteristicID)
        {
            Log.d(BleClientService.TAG,
                    "onGetNextCharacteristic " + characteristicID.toString() + "instance id = "
                            + characteristicID.getInstanceID() + " status = " + status);

            BluetoothDevice device = BleClientService.this.mProfile
                    .getDeviceforConnId(connID);
            if ((device == null)
                    || (BleClientService.this.mProfile.isDeviceDisconnecting(device))) {
                Log.e(BleClientService.TAG,
                        "onGetNextCharacteristic() - Device is disconnecting...");
                return;
            }

            if (status == 0) {
                BleClientService.ServiceData s = BleClientService.this.getServiceData(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        svcId.getInstanceID());

                Log.d(BleClientService.TAG,
                        "characteristic ID = " + characteristicID.toString() + "instance ID = "
                                + characteristicID.getInstanceID());

                BleCharacteristic characteristic = null;
                if (characteristicID.getUuidType() == 16)
                    characteristic = new BleCharacteristic(new BleGattID(
                            characteristicID.getUuid()));
                else
                    characteristic = new BleCharacteristic(new BleGattID(
                            characteristicID.getUuid16()));
                characteristic.setInstanceID(characteristicID.getInstanceID());

                BleClientService.this.onSetCharacteristicAuthRequirement(
                        BleClientService.this.mProfile.getDeviceforConnId(connID), characteristic,
                        svcId.getInstanceID());

                s.characteristics.add(characteristic);

                BleClientService.this.readFirstCharDescriptor(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID));
            }
            else
            {
                BleClientService.ServiceData s = BleClientService.this.getNextServiceData(
                        BleClientService.this.mProfile.getDeviceforConnId(connID),
                        svcId.getInstanceID());

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
                    BleClientService.this.onServiceRefreshed(connID);
                }
            }
        }

        public void onReadCharacteristicValue(int connID, int status, BluetoothGattID svcId,
                BluetoothGattID characteristicID, byte[] data)
        {
            Log.d(BleClientService.TAG, "onReadCharacteristicValue charID = "
                    + characteristicID.toString() + " status = " + status);

            BleCharacteristic c = findCharacteristic(connID,
                    BleApiHelper.gatt2BleID(svcId), BleApiHelper.gatt2BleID(characteristicID));

            if (c != null) {
                if (status == 0) {
                    c.setValue(data);
                    if (BleClientService.this.mReadDescriptors)
                    {
                        ArrayList descList = c.getDirtyDescQueue();
                        if (!descList.isEmpty()) {
                            BleDescriptor descObj = (BleDescriptor) descList.get(0);
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
                else {
                    BleClientService.this.onReadCharacteristicComplete(status,
                            BleClientService.this.mProfile.getDeviceforConnId(connID), c);
                }
            }
            else
                Log.e(BleClientService.TAG,
                        "onReadCharacteristicValue() - Characteristic not found");
        }

        public void onReadCharDescriptorValue(int connID, int status, BluetoothGattID svcId,
                BluetoothGattID characteristicID, BluetoothGattID descr, byte[] data)
        {
            Log.d(BleClientService.TAG, "onReadCharacteristicExtProp charID = "
                    + characteristicID.toString() + " status = " + status);

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
                            ArrayList descList = c.getDirtyDescQueue();
                            if (!descList.isEmpty()) {
                                BleDescriptor descObj = (BleDescriptor) descList.get(0);
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

            BleClientService.ServiceData s = BleClientService.this.getServiceData(
                    BleClientService.this.mProfile.getDeviceforConnId(connID), instanceID);

            int i;
            for (i = 0; i < s.characteristics.size(); i++) {
                BleCharacteristic cTemp = (BleCharacteristic) s.characteristics.get(i);
                if (c.getID().equals(cTemp.getID()))
                {
                    break;
                }
            }
            if (i + 1 < s.characteristics.size()) {
                Log.d(BleClientService.TAG, "findNextCharacteristic position =  " + i
                        + "connID = " + connID + " next characteristic :"
                        + ((BleCharacteristic) s.characteristics.get(i + 1)).getID().toString());

                return (BleCharacteristic) s.characteristics.get(i + 1);
            }
            return null;
        }

        BleCharacteristic findCharacteristic(int connID, BleGattID svcId,
                BleGattID characteristicID)
        {
            Log.d(BleClientService.TAG,
                    "findCharacteristic charID = [" + characteristicID.toString()
                            + "] instance ID = [" + characteristicID.getInstanceID() + "]");

            BleClientService.ServiceData s = BleClientService.this.getServiceData(
                    BleClientService.this.mProfile.getDeviceforConnId(connID),
                    svcId.getInstanceID());

            for (int i = 0; i < s.characteristics.size(); i++) {
                BleCharacteristic c = (BleCharacteristic) s.characteristics.get(i);
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
                    "findCharacteristic charID = [" + characteristicID.toString()
                            + "] instance ID = [" + characteristicID.getInstanceID() + "]");

            BleClientService.ServiceData s = BleClientService.this.getServiceData(
                    BleClientService.this.mProfile.getDeviceforConnId(connID),
                    svcId.getInstanceID());

            BleCharacteristic charObj = null;
            for (int i = 0; i < s.characteristics.size(); i++) {
                BleCharacteristic c = (BleCharacteristic) s.characteristics.get(i);

                if (c.getID().equals(characteristicID.getUuid())) {
                    Log.d(BleClientService.TAG, "findCharacteristic - found");
                    charObj = c;
                }
            }
            if (charObj != null) {
                for (int i = 0; i < charObj.getAllDescriptors().size(); i++) {
                    BleDescriptor d = (BleDescriptor) charObj.getAllDescriptors().get(i);
                    if (d.getID().equals(descriptorID)) {
                        Log.d(BleClientService.TAG, "findDescriptor - found");
                        return d;
                    }
                }
            }

            return null;
        }

        public void onRegForNotifications(int connId, int status, BluetoothGattID svcId,
                BluetoothGattID charId)
        {
            Log.d(BleClientService.TAG, "onRegForNotifications " + status);
        }

        public void onUnregisterNotifications(int connId, int status, BluetoothGattID svcId,
                BluetoothGattID charId)
        {
            Log.d(BleClientService.TAG, "onUnregisterNotifications" + status);
        }

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

        public ArrayList<BleCharacteristic> characteristics = new ArrayList();

        ServiceData()
        {
        }

    }

}
