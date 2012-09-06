
package com.broadcom.bt.le.api;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.broadcom.bt.service.gatt.BluetoothGattID;
import com.broadcom.bt.service.gatt.IBluetoothGatt;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstract base class, representing a Bluetooth LE Client Profile. <br>
 * <br>
 * To implement a new low energy profile, an application must extend this class,
 * initialize it with the desired profile ID and the necessary required or
 * optional services. <br>
 * <br>
 * Included services are represented by overriding the BleClientService class. <br>
 * <br>
 * Once this class has been extended for the desired profile, an application may
 * call the connect(BluetoothDevice) or connectBackground(BluetoothDevice)
 * functions to initiate an active or passive connection to the remote device.
 */
public abstract class BleClientProfile
{
    private static final boolean D = true;
    private static final String TAG = "BleClientProfile";
    private ArrayList<BleClientService> mRequiredServices;
    private ArrayList<BleClientService> mOptionalServices;
    private ArrayList<BluetoothDevice> mConnectedDevices;
    private ArrayList<BluetoothDevice> mConnectingDevices;
    private ArrayList<BluetoothDevice> mDisconnectingDevices;
    private BleGattID mAppUuid;
    private byte mClientIf = BleConstants.GATT_SERVICE_PRIMARY;
    private IBluetoothGatt mService;
    private BleClientCallback mCallback;
    private HashMap<Integer, BluetoothDevice> mClientIDToDeviceMap;
    private HashMap<BluetoothDevice, Integer> mDeviceToClientIDMap;
    private ArrayList<BleGattID> mPeerServices = new ArrayList();
    private GattServiceConnection mSvcConn;
    private Context mContext;

    /**
     * Creates a BlueClientProfile given this profile's UUID and client
     * application context.
     * 
     * @param context Application context used to bind the GATT service.
     * @param profileUuid Unique UUID identifying the profile to be implemented.
     */
    public BleClientProfile(Context context, BleGattID profileUuid)
    {
        Log.d(TAG, "new profile" + profileUuid.toString());

        this.mContext = context;
        this.mAppUuid = profileUuid;

        this.mConnectedDevices = new ArrayList<BluetoothDevice>();
        this.mConnectingDevices = new ArrayList<BluetoothDevice>();
        this.mDisconnectingDevices = new ArrayList<BluetoothDevice>();

        this.mClientIDToDeviceMap = new HashMap<Integer, BluetoothDevice>();
        this.mDeviceToClientIDMap = new HashMap<BluetoothDevice, Integer>();

        this.mCallback = new BleClientCallback();
        this.mSvcConn = new GattServiceConnection(context);
    }

    /**
     * Initializes this BleClientProfile object. Takes an array of required
     * services and optional services and saves it in the object for
     * verification when peer connection is made.
     * 
     * @param requiredServices {@link ArrayList} of {@link BleClientService}
     *            derived objects indicating the services required for this
     *            profile to function correctly.
     * @param optionalServices {@link ArrayList} of {@link BleClientService}
     *            derived objects indicating the optional services that profile
     *            might need.
     */
    public void init(ArrayList<BleClientService> requiredServices,
            ArrayList<BleClientService> optionalServices)
    {
        Log.d(TAG, "init (" + this.mAppUuid + ")");

        this.mRequiredServices = requiredServices;
        this.mOptionalServices = optionalServices;

        IBinder b = ServiceManager.getService(BleConstants.BLUETOOTH_LE_SERVICE);
        if (b == null) {
            throw new RuntimeException("Bluetooth Low Energy service not available");
        }
        this.mSvcConn.onServiceConnected(null, b);
    }

    /**
     * Cleans up resources associated with this profile.
     */
    public synchronized void finish()
    {
        if (this.mSvcConn != null) {
            this.mContext.unbindService(this.mSvcConn);
            this.mSvcConn = null;
        }
    }

    @Override
    public void finalize()
    {
        finish();
    }

    /**
     * Returns whether this profile has been successfully registered with the
     * Bluetooth stack.
     * 
     * @see {@link #registerProfile()}
     */
    public boolean isProfileRegistered()
    {
        Log.d(TAG, "isProfileRegistered (" + this.mAppUuid + ")");
        return this.mClientIf != BleConstants.GATT_SERVICE_PRIMARY;
    }

    /**
     * Registers this profile with the Bluetooth stack.
     * 
     * @return {@link BleConstants#GATT_SUCCESS} if the profile was successfully
     *         registered or {@link BleConstants#SERVICE_UNAVAILABLE} if the
     *         profile has already been registered by another application.
     */
    public int registerProfile()
    {
        int ret = BleConstants.GATT_SUCCESS;
        Log.d(TAG, "registerProfile (" + this.mAppUuid + ")");

        if (this.mClientIf == BleConstants.GATT_SERVICE_PRIMARY)
        {
            try
            {
                this.mService.registerApp(this.mAppUuid, this.mCallback);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                ret = BleConstants.SERVICE_UNAVAILABLE;
            }
        }

        return ret;
    }

    /**
     * Unregister this profile with the Bluetooth stack.
     */
    public void deregisterProfile()
    {
        Log.d(TAG, "deregisterProfile (" + this.mAppUuid + ")");

        if (this.mClientIf != BleConstants.GATT_SERVICE_PRIMARY)
            try {
                this.mService.unregisterApp(this.mClientIf);
            } catch (RemoteException e) {
                Log.e(TAG, "deregisterProfile() - " + e.toString());
            }
    }

    /**
     * Sets the desired encryption level for an active connection. This function
     * should only be called after a connection to a remote device has been
     * established.
     * 
     * @param device Remote device with a currently active
     * @param action Encryption level for the active connection
     */
    public void setEncryption(BluetoothDevice device, byte action)
    {
        try
        {
            this.mService.setEncryption(device.getAddress(), action);
        } catch (RemoteException e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Defines how aggressive the local devices scans for remote LE devices when
     * a background connection has been requested.
     * 
     * @param scanInterval
     * @param scanWindow
     */
    public void setScanParameters(int scanInterval, int scanWindow)
    {
        try
        {
            this.mService.setScanParameters(scanInterval, scanWindow);
        } catch (RemoteException e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Initiates a GATT connection to the remote device. If the connection is
     * established successfully, remote services are enumerated automatically.
     * 
     * @param device remote Bluetooth device
     * @return {@link BleConstants#GATT_SUCCESS} if the connection is initiated
     *         successfully.
     * @see {@link #onDeviceConnected(BluetoothDevice)},
     *      {@link #disconnect(BluetoothDevice)}
     */
    public int connect(BluetoothDevice device)
    {
        Log.d(TAG, "connect (" + this.mAppUuid + ")" + device.getAddress());

        int ret = BleConstants.GATT_SUCCESS;

        synchronized (this.mConnectingDevices) {
            this.mConnectingDevices.add(device);
        }

        synchronized (this.mDisconnectingDevices) {
            this.mDisconnectingDevices.remove(device);
        }
        try
        {
            this.mService.open(this.mClientIf, device.getAddress(), true);
        } catch (RemoteException e) {
            Log.e(TAG, e.toString());
            ret = BleConstants.GATT_ERROR;
        }

        return ret;
    }

    /**
     * Prepares a background connection to a remote Bluetooth device. <br>
     * <br>
     * A background connection allows the remote device to initiate a connection
     * to this profile. After calling
     * {@link #connectBackground(BluetoothDevice)}, the local device will
     * periodically scan for incoming connections from the specified device. An
     * application must call
     * {@link #cancelBackgroundConnection(BluetoothDevice)} to stop scanning.
     * 
     * @see {@link #onDeviceConnected(BluetoothDevice)},
     *      {@link #cancelBackgroundConnection(BluetoothDevice)}
     * @param device remote device
     * @return {@link BleConstants#GATT_SUCCESS} or
     *         {@link BleConstants#GATT_ERROR}
     */
    public int connectBackground(BluetoothDevice device)
    {
        Log.d(TAG,
                "connectBackground (" + this.mAppUuid + ")" + device.getAddress());

        int ret = BleConstants.GATT_SUCCESS;

        synchronized (this.mConnectingDevices) {
            this.mConnectingDevices.add(device);
        }

        synchronized (this.mDisconnectingDevices) {
            this.mDisconnectingDevices.remove(device);
        }
        try
        {
            this.mService.open(this.mClientIf, device.getAddress(), false);
        } catch (RemoteException e) {
            Log.e(TAG, e.toString());
            ret = BleConstants.GATT_ERROR;
        }

        return ret;
    }

    /**
     * Stops listening for connection attempts initiated by a remote device.
     * 
     * @param device remote Bluetooth device
     * @return {@link BleConstants#GATT_SUCCESS} or
     *         {@link BleConstants#GATT_ERROR}.
     */
    public int cancelBackgroundConnection(BluetoothDevice device)
    {
        Log.d(TAG, "cancelBackgroundConnection (" + this.mAppUuid
                + ") - device " + device.getAddress());

        int ret = BleConstants.GATT_SUCCESS;
        try
        {
            this.mService.close(this.mClientIf, device.getAddress(), 0, false);
        } catch (RemoteException e) {
            Log.e(TAG, e.toString());
            ret = BleConstants.GATT_ERROR;
        }

        return ret;
    }

    /**
     * Disconnects an established GATT connection to a remote device.
     * 
     * @param device Bluetooth device
     * @return {@link BleConstants#GATT_SUCCESS} or
     *         {@link BleConstants#GATT_ERROR}.
     */
    public int disconnect(BluetoothDevice device)
    {
        Log.d(TAG,
                "disconnect (" + this.mAppUuid + ") - device " + device.getAddress());

        synchronized (this.mDisconnectingDevices) {
            this.mDisconnectingDevices.add(device);
        }

        int ret = BleConstants.GATT_SUCCESS;
        try
        {
            this.mService.close(this.mClientIf,
                    device.getAddress(),
                    ((Integer) this.mDeviceToClientIDMap.get(device)).intValue(),
                    true);
        } catch (RemoteException e) {
            Log.e(TAG, e.toString());
            ret = BleConstants.GATT_ERROR;
        }
        return ret;
    }

    /**
     * Refreshes this client profile. For each service included in the profile,
     * all the characteristics are retrieved and read.
     * 
     * @see {@link BleClientService#refresh(BluetoothDevice)}
     * @param device remote device
     * @return {@link BleConstants#GATT_SUCCESS} or
     *         {@link BleConstants#GATT_ERROR}.
     */
    public int refresh(BluetoothDevice device)
    {
        Log.d(TAG,
                "refresh (" + this.mAppUuid + ") - address = " + device.getAddress());

        if (isDeviceDisconnecting(device)) {
            Log.d(TAG, "refresh (" + this.mAppUuid
                    + ") - Device unavailable!");
            return BleConstants.GATT_ERROR;
        }

        this.mRequiredServices.get(BleConstants.GATT_SERVICE_PRIMARY).refresh(device);

        return BleConstants.GATT_SUCCESS;
    }

    /**
     * Refreshes a specific service included in this profile. Gets all the
     * characteristics and values for this service.
     */
    public int refreshService(BluetoothDevice device, BleClientService service)
    {
        Log.d(TAG, "refreshService (" + this.mAppUuid + ") address = s "
                + device.getAddress() + "service = " + service.getServiceId());

        return 0;
    }

    /**
     * Returns an array of all remote devices currently connected to this
     * profile.
     * 
     * @return array of {@link BluetoothDevice}
     */
    public BluetoothDevice[] getConnectedDevices()
    {
        return (BluetoothDevice[]) this.mConnectedDevices.toArray(new BluetoothDevice[0]);
    }

    /**
     * Looks up a given Bluetooth device address in the list of connected
     * devices. Returns a {@link BluetoothDevice} object if the remote device is
     * connected or null if the device could not be found.
     * 
     * @param address remote device
     * @return {@link BluetoothDevice} or null
     */
    public BluetoothDevice findConnectedDevice(String address)
    {
        BluetoothDevice ret = null;
        synchronized (this.mConnectedDevices) {
            for (int i = 0; i != this.mConnectedDevices.size(); i++) {
                BluetoothDevice d = (BluetoothDevice) this.mConnectedDevices.get(i);
                if (address.equalsIgnoreCase(d.getAddress())) {
                    ret = d;
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * Returns an array of remote devices that are currently connecting or
     * awaiting a connection.
     * 
     * @see {@link #connectBackground(BluetoothDevice)}
     */
    public BluetoothDevice[] getPendingConnections()
    {
        return (BluetoothDevice[]) this.mConnectingDevices.toArray(new BluetoothDevice[0]);
    }

    /**
     * Find a remote device, given its Bluetooth device address, in the list of
     * devices awaiting a connection.
     * 
     * @see {@link #connectBackground(BluetoothDevice)}
     * @param address remote address
     * @return {@link BluetoothDevice} or null
     */
    public BluetoothDevice findDeviceWaitingForConnection(String address)
    {
        BluetoothDevice ret = null;
        synchronized (this.mConnectingDevices) {
            for (int i = 0; i < this.mConnectingDevices.size(); i++) {
                BluetoothDevice d = (BluetoothDevice) this.mConnectingDevices.get(i);
                if (address.equalsIgnoreCase(d.getAddress())) {
                    ret = d;
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * @hide
     * @return
     */
    IBluetoothGatt getGattService()
    {
        return this.mService;
    }

    /**
     * @hide
     * @param d
     * @return
     */
    int getConnIdForDevice(BluetoothDevice d)
    {
        if (!this.mDeviceToClientIDMap.containsKey(d)) {
            return 65535;
        }

        return ((Integer) this.mDeviceToClientIDMap.get(d)).intValue();
    }

    /**
     * @hide
     * @return
     */
    byte getClientIf()
    {
        return this.mClientIf;
    }

    /**
     * @hide
     * @param connId
     * @return
     */
    BluetoothDevice getDeviceforConnId(int connId)
    {
        return (BluetoothDevice) this.mClientIDToDeviceMap.get(new Integer(connId));
    }

    /** @hide */
    boolean isDeviceDisconnecting(BluetoothDevice device) {
        return this.mDisconnectingDevices.indexOf(device) != -1;
    }

    /**
     * @hide
     */
    void onServiceRefreshed(BleClientService s, BluetoothDevice device)
    {
        int i = this.mRequiredServices.indexOf(s);
        if (i + 1 < this.mRequiredServices.size()) {
            Log.d(TAG, "Refreshing next service");
            ((BleClientService) this.mRequiredServices.get(i + 1)).refresh(device);
        } else {
            onRefreshed(device);
        }
    }

    /**
     * Callback invoked when the profile has been initialized and has
     * successfully connected to the GATT service. The default behaviour of this
     * function is to call {@link #registerProfile()} next.
     * 
     * @see {@link #init(ArrayList, ArrayList)}, {@link #registerProfile()}
     * @param success
     */
    public void onInitialized(boolean success)
    {
        Log.d(TAG, "onInitialized");
        if (success)
            registerProfile();
    }

    /**
     * Called when the profile has been registered with the Bluetooth stack.
     * After this callback has been invoked, the application is ready to receive
     * {@param #onDeviceConnected(BluetoothDevice)}, {@param
     * #onDeviceDisconnected(BluetoothDevice)} and related callbacks.
     * 
     * @see {@link #registerProfile()}
     */
    public void onProfileRegistered()
    {
        Log.d(TAG, "onProfileRegistered");
    }

    /**
     * Invoked when a profile has been de-registered from the Bluetooth stack.
     * 
     * @see {@link #deregisterProfile()}
     */
    public void onProfileDeregistered()
    {
        Log.d(TAG, "onProfileDeregistered");
    }

    /**
     * Invoked when a remote device has connected to this profile. This callback
     * may be triggered when a remote device is connected directly using the
     * {@link #connect(BluetoothDevice)} function or when a remote device
     * initiates a connection to the local device. By default, this function
     * refreshes the services on the remote device.
     * 
     * @see {@link #connect(BluetoothDevice)},
     *      {@link #connectBackground(BluetoothDevice)},
     *      {@link #refresh(BluetoothDevice)}
     * @param device Identifies the remote the device that got connected.
     */
    public void onDeviceConnected(BluetoothDevice device)
    {
        Log.d(TAG, "onDeviceConnected");
        refresh(device);
    }

    /**
     * Called when the profile is disconnected from the peer.
     * 
     * @param device Identifies the remote the device that is no longer
     *            connected.
     * @see {@link #disconnect(BluetoothDevice)}
     */
    public void onDeviceDisconnected(BluetoothDevice device)
    {
        Log.d(TAG, "onDeviceDisconnected");
    }

    /**
     * Callback triggered when the profile has refreshed all the services and
     * updated all characteristics.
     * 
     * @see {@link #refresh(BluetoothDevice)}
     */
    public void onRefreshed(BluetoothDevice device)
    {
        Log.d(TAG, "onRefreshed");
    }

    private class GattServiceConnection
            implements ServiceConnection
    {
        private Context context;

        private GattServiceConnection(Context c) {
            this.context = c;
        }

        public void onServiceConnected(ComponentName name, IBinder service)
        {
            Log.d(TAG, "Connected to GattService!");

            if (service != null)
                try {
                    BleClientProfile.this.mService = IBluetoothGatt.Stub.asInterface(service);

                    for (int i = 0; i < BleClientProfile.this.mRequiredServices.size(); i++) {
                        BleClientProfile.this.mRequiredServices.get(i)
                                .setProfile(BleClientProfile.this);
                    }

                    if (BleClientProfile.this.mOptionalServices != null) {
                        for (int i = 0; i < BleClientProfile.this.mOptionalServices.size(); i++) {
                            BleClientProfile.this.mOptionalServices
                                    .get(i).setProfile(BleClientProfile.this);
                        }
                    }

                    BleClientProfile.this.onInitialized(true);
                } catch (Throwable t) {
                    Log.e(TAG, "Unable to get Binder to GattService", t);
                    BleClientProfile.this.onInitialized(false);
                }
        }

        public void onServiceDisconnected(ComponentName name)
        {
            Log.d(TAG, "Disconnected from GattService!");
        }

    }

    class BleClientCallback extends IBleClientCallback.Stub
    {
        BleClientCallback()
        {
        }

        public void onAppRegistered(byte status, byte client_if)
        {
            Log.d(TAG, "BleClientCallback::onAppRegistered ("
                    + BleClientProfile.this.mAppUuid + ") status = " + status + " client_if = "
                    + client_if);

            BleClientProfile.this.mClientIf = client_if;
            BleClientProfile.this.onProfileRegistered();
        }

        public void onAppDeregistered(byte client_if) {
            Log.d(TAG, "BleClientCallback::onAppDeregistered ("
                    + BleClientProfile.this.mAppUuid + ") client_if = " + client_if);

            BleClientProfile.this.mClientIf = BleConstants.GATT_SERVICE_PRIMARY;
            BleClientProfile.this.onProfileDeregistered();
        }

        public void onConnected(String deviceAddress, int connID) {
            Log.d(TAG, "BleClientCallback::OnConnected ("
                    + BleClientProfile.this.mAppUuid + ") " + deviceAddress + "connID = " + connID);

            BluetoothDevice d = BleClientProfile.this
                    .findDeviceWaitingForConnection(deviceAddress);

            if (null == d)
            {
                d = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                synchronized (BleClientProfile.this.mConnectedDevices) {
                    BleClientProfile.this.mConnectedDevices.add(d);
                }

                synchronized (BleClientProfile.this.mConnectingDevices) {
                    BleClientProfile.this.mConnectingDevices.remove(d);
                }

                synchronized (BleClientProfile.this.mDisconnectingDevices) {
                    BleClientProfile.this.mDisconnectingDevices.remove(d);
                }
            }

            if (d.getBondState() == BluetoothDevice.BOND_BONDED) {
                Log.d(TAG,
                        "onConnected device is bonded start encrypt the  link");
                BleClientProfile.this.setEncryption(d, (byte) 1);
            }
            BleClientProfile.this.mClientIDToDeviceMap.put(new Integer(connID), d);
            BleClientProfile.this.mDeviceToClientIDMap.put(d, new Integer(connID));

            BleClientProfile.this.mPeerServices.clear();
            try
            {
                BleClientProfile.this.mService.searchService(connID, null);
            } catch (RemoteException e) {
                Log.d(TAG, "Error calling searchService " + e.toString());
            }
        }

        public void onDisconnected(int connID, String deviceAddress)
        {
            Log.d(TAG, "BleClientCallback::onDisconnected ("
                    + BleClientProfile.this.mAppUuid + ") connID = " + connID);

            BleClientProfile.this
                    .onDeviceDisconnected((BluetoothDevice) BleClientProfile.this.mClientIDToDeviceMap
                            .get(new Integer(connID)));

            BluetoothDevice d = (BluetoothDevice) BleClientProfile.this.mClientIDToDeviceMap
                    .get(new Integer(connID));

            BleClientProfile.this.mDeviceToClientIDMap.remove(d);
            BleClientProfile.this.mClientIDToDeviceMap.remove(new Integer(connID));
            BleClientProfile.this.mConnectedDevices.remove(d);
            BleClientProfile.this.mDisconnectingDevices.remove(d);
        }

        public void onSearchResult(int connID, BluetoothGattID srvcId) {
            Log.d(TAG, "BleClientCallback::onSearchResult ("
                    + BleClientProfile.this.mAppUuid + ") connID = " + connID + " svcId: id = "
                    + srvcId.toString() + " inst id = " + srvcId.getInstanceID());

            BleClientProfile.this.mPeerServices.add(BleApiHelper.gatt2BleID(srvcId));
        }

        public void onSearchCompleted(int connID, int status) {
            Log.d(TAG, "BleClientCallback::onSearchCompleted ("
                    + mAppUuid + ") connID = " + connID + " status = "
                    + status);

            int nServicesFound = 0;

            if (mRequiredServices == null) {
                Log.d(TAG, "mRequiredServices is null");
                return;
            }

            if (mPeerServices == null) {
                Log.d(TAG, "mPeerServices is null");
                return;
            }

            for (int i = 0; i < mRequiredServices.size(); i++) {
                for (int j = 0; j < mPeerServices.size(); j++) {
                    Log.v(TAG, "comparing " + mRequiredServices.get(i).getServiceId());
                    Log.v(TAG, "with " + mPeerServices.get(j));
                    if (mPeerServices.get(j).equals(mRequiredServices.get(i).getServiceId())) {
                        mRequiredServices.get(i).setInstanceID(
                                mClientIDToDeviceMap.get(connID),
                                mPeerServices.get(j).getInstanceID());
                        nServicesFound++;
                        break;
                    }
                }
            }

            Log.d(TAG, "BleClientCallback::onSearchResult - found "
                    + nServicesFound + " out of " + mRequiredServices.size()
                    + " services needed for this profile");
            BluetoothDevice device = mClientIDToDeviceMap.get(connID);

            if (device == null) {
                Log.d(TAG,
                        "No bluetooth device in the device map for connid = " + connID);
            }
            else if (isDeviceDisconnecting(device)) {
                Log.d(TAG, "Device disconnecting...");
            }
            else if (nServicesFound == mRequiredServices.size()) {
                Log.d(TAG, "the num of Srvs found match the required srv size ");
                onDeviceConnected(device);
            } else {
                Log.d(TAG, "the num of Srvs found DOES NOT match the required srv size ");
            }
        }
    }
}
