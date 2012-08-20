
package android.bluetooth.le.server;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;

import com.broadcom.bt.le.api.BleAdapter;
import com.broadcom.bt.le.api.BleConstants;
import com.broadcom.bt.le.api.IBleCharacteristicDataCallback;
import com.broadcom.bt.le.api.IBleClientCallback;
import com.broadcom.bt.le.api.IBleProfileEventCallback;
import com.broadcom.bt.le.api.IBleServiceCallback;
import com.broadcom.bt.service.gatt.BluetoothGattCharDescrID;
import com.broadcom.bt.service.gatt.BluetoothGattCharID;
import com.broadcom.bt.service.gatt.BluetoothGattID;
import com.broadcom.bt.service.gatt.BluetoothGattInclSrvcID;
import com.broadcom.bt.service.gatt.IBluetoothGatt;

import org.freedesktop.dbus.Variant;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BluetoothGatt extends IBluetoothGatt.Stub implements BlueZInterface.Listener {
    private BlueZInterface iface;
    private Context mContext;
    private BluetoothAdapter mAdapter;
    private IActivityManager mAm;
    private static String TAG = "BT-GATT";

    public static final String BLUETOOTH_PERM = "android.permission.BLUETOOTH";

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            if (i.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int s = i.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                if (s == BluetoothAdapter.STATE_ON) {
                    Log.v(TAG, "enabling my interface");
                    Thread a= new Thread(){
                        public void run(){
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            BluetoothGatt.this.iface.Start(true);
                        }
                    };
                    a.start();
                       
                } else if (s == BluetoothAdapter.STATE_OFF) {
                    Log.v(TAG, "bluez is down");
                    BluetoothGatt.this.iface.Stop();
                }
            }

        }
    };

    public BluetoothGatt(Context ctx) {
        Log.v(TAG, "new bluetoothGatt");
        iface = new BlueZInterface();
        iface.setListener(this);
        iface.Start();
        mContext = ctx;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mAm = ActivityManagerNative.getDefault();
        registerBroadcastReceiver(mReceiver,
                new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    public byte getDeviceType(String address) throws RemoteException {
        @SuppressWarnings("rawtypes")
        Map<String, Variant> prop = iface.getDeviceProperties(address);
        if (prop == null)
            return BleConstants.GATT_UNDEFINED;

        /**
         * LE provides - Address, RSSI, Name, Paired Broadcaster UUIDs Class 0
         * or no class at all<br>
         * <br>
         * BD/EDR provides: Address Class Icon RSSI Name Alias LegacyPairing
         * Paired UUIDs
         */
        if (prop.containsKey("Icon") && prop.containsKey("Icon") && prop.containsKey("Alias")
                && prop.containsKey("LegacyPairing"))
            return BleAdapter.DEVICE_TYPE_BREDR;

        return BleAdapter.DEVICE_TYPE_BLE;
    }

    private class IntentReceiver extends IIntentReceiver.Stub {
        private boolean mFinished = false;

        public synchronized void performReceive(
                Intent intent, int rc, String data, Bundle ext, boolean ord,
                boolean sticky) {
            String line = "Broadcast completed: result=" + rc;
            if (data != null)
                line = line + ", data=\"" + data + "\"";
            if (ext != null)
                line = line + ", extras: " + ext;
            System.out.println(line);
            mFinished = true;
            notifyAll();
        }

        public synchronized void waitForFinish() {
            try {
                while (!mFinished)
                    wait();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private class IBroadcastReceiver extends IIntentReceiver.Stub {
        private BroadcastReceiver receiver = null;

        public IBroadcastReceiver(BroadcastReceiver receiver) {
            super();
            this.receiver = receiver;
        }

        public synchronized void performReceive(
                Intent intent, int rc, String data, Bundle ext, boolean ord,
                boolean sticky) {
            String line = "Broadcast received: " + intent;
            System.out.println(line);
            receiver.onReceive(null, intent);
            notifyAll();
        }
    }

    private void broadcastIntent(Intent i) {
        IntentReceiver receiver = new IntentReceiver();
        System.out.println("Broadcasting: " + i);
        try {
            mAm.broadcastIntent(null, i, null, null, 0, null, null, null, true, false,
                    0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        receiver.waitForFinish();
    }

    private void registerBroadcastReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        Log.v(TAG, "registering broadcast receiver");
        IBroadcastReceiver ireceiver = new IBroadcastReceiver(receiver);
        try {
            mAm.registerReceiver(null, null, ireceiver, filter, null);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.v(TAG, "registered");
    }

    @Override
    public void deviceDiscovered(String address, String name, short rssi) {
        Intent intent = new Intent(BluetoothDevice.ACTION_FOUND);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, mAdapter.getRemoteDevice(address));
        intent.putExtra(BluetoothDevice.EXTRA_NAME, name);
        intent.putExtra(BluetoothDevice.EXTRA_RSSI, rssi);
        intent.putExtra(BleAdapter.EXTRA_DEVICE_TYPE, BleAdapter.DEVICE_TYPE_BLE);
        broadcastIntent(intent);
    }
    
    @Override
    public void getUUIDs(String address) throws RemoteException {
        List<String> uuids = iface.getUUIDs(address);
        for (String u: uuids){
            Intent intent = new Intent(BleAdapter.ACTION_UUID);
            intent.putExtra(BleAdapter.EXTRA_DEVICE, mAdapter.getRemoteDevice(address));
            intent.putExtra(BleAdapter.EXTRA_UUID, new ParcelUuid(UUID.fromString(u)));
            broadcastIntent(intent);
        }
    }

    @Override
    public void registerApp(BluetoothGattID appUuid, IBleClientCallback callback)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterApp(byte interfaceID) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setEncryption(String address, byte action) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setScanParameters(int scanInterval, int scanWindow) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void filterEnable(boolean p) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void filterEnableBDA(boolean enable, int addr_type, String address)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearManufacturerData() throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void filterManufacturerData(int company, byte[] data1, byte[] data2, byte[] data3,
            byte[] data4) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void filterManufacturerDataBDA(int company, byte[] data1, byte[] data2, byte[] data3,
            byte[] data4, boolean has_bda, int addr_type, String address) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void observe(boolean start, int duration) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void open(byte interfaceID, String remote, boolean foreground) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void close(byte interfaceID, String remote, int clientID, boolean foreground)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerServiceDataCallback(int connID, BluetoothGattID serviceID, String address,
            IBleCharacteristicDataCallback callback) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void searchService(int connID, BluetoothGattID serviceID) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void getFirstChar(int connID, BluetoothGattID serviceID, BluetoothGattID id)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void getNextChar(int connID, BluetoothGattCharID charID, BluetoothGattID id)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void getFirstCharDescr(int connID, BluetoothGattCharID charID, BluetoothGattID id)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void getNextCharDescr(int connID, BluetoothGattCharDescrID charDescrID,
            BluetoothGattID id) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void getFirstIncludedService(int connID, BluetoothGattID serviceID, BluetoothGattID id2)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void getNextIncludedService(int connID, BluetoothGattInclSrvcID includedServiceID,
            BluetoothGattID id) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void readChar(int connID, BluetoothGattCharID charID, byte authReq)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void readCharDescr(int connID, BluetoothGattCharDescrID charDescID, byte authReq)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeCharValue(int connID, BluetoothGattCharID CharID, int writeType, byte authReq,
            byte[] value) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeCharDescrValue(int connID, BluetoothGattCharDescrID descID, int writeType,
            byte authReq, byte[] value) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendIndConfirm(int connID, BluetoothGattCharID charID) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void prepareWrite(int paramInt1, BluetoothGattCharID charID, int paramInt2,
            int paramInt3, byte[] paramArrayOfByte) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void executeWrite(int paramInt, boolean paramBoolean) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerForNotifications(byte interfaceID, String address,
            BluetoothGattCharID charID) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deregisterForNotifications(byte interfaceID, String address,
            BluetoothGattCharID charID) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerServerServiceCallback(BluetoothGattID id1, BluetoothGattID id2,
            IBleServiceCallback callback) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerServerProfileCallback(BluetoothGattID id, IBleProfileEventCallback callback)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterServerServiceCallback(int paramInt) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterServerProfileCallback(int paramInt) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void GATTServer_CreateService(byte paramByte, BluetoothGattID id, int paramInt)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void GATTServer_AddIncludedService(int paramInt1, int paramInt2) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void GATTServer_AddCharacteristic(int paramInt1, BluetoothGattID id, int paramInt2,
            int paramInt3, boolean paramBoolean, int paramInt4) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void GATTServer_AddCharDescriptor(int paramInt1, int paramInt2, BluetoothGattID id)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void GATTServer_DeleteService(int paramInt) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void GATTServer_StartService(int paramInt, byte paramByte) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void GATTServer_StopService(int paramInt) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void GATTServer_HandleValueIndication(int paramInt1, int paramInt2,
            byte[] paramArrayOfByte) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void GATTServer_HandleValueNotification(int paramInt1, int paramInt2,
            byte[] paramArrayOfByte) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void GATTServer_SendRsp(int paramInt1, int paramInt2, byte paramByte1, int paramInt3,
            int paramInt4, byte[] paramArrayOfByte, byte paramByte2, boolean paramBoolean)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void GATTServer_Open(byte paramByte, String paramString, boolean paramBoolean)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void GATTServer_CancelOpen(byte paramByte, String paramString, boolean paramBoolean)
            throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void GATTServer_Close(int paramInt) throws RemoteException {
        // TODO Auto-generated method stub

    }
}
