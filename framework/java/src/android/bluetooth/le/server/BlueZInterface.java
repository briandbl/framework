/**
 * 
 */

package android.bluetooth.le.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.server.handlers.DeviceFoundHandler;
import android.util.Log;

import com.broadcom.bt.le.api.BleConstants;
import com.broadcom.bt.service.gatt.BluetoothGattID;

import org.bluez.Adapter;
import org.bluez.Characteristic;
import org.bluez.Device;
import org.bluez.Error;
import org.bluez.Error.DoesNotExist;
import org.bluez.Error.InvalidArguments;
import org.bluez.Manager;
import org.bluez.Service;
import org.bluez.Watcher;
import org.freedesktop.DBus;
import org.freedesktop.DBus.NameOwnerChanged;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * @author manuel
 */
public class BlueZInterface {
    public interface Listener {
        public void deviceDiscovered(String address, String name, short rssi);

        public void serviceDiscovered(int connID, String address, String uuid, String path);

        // public void deviceCreated(String path);

        public void serviceDiscoveredFinished(int connID, int status);

        public void characteristicsSolved(int connID, String serPath,
                List<Path> charPath, List<BluetoothGattID> uuids);

        public void valueChanged(String charPath, byte[] value);
    }

    private static final String DBUS_BLUEZ = "org.bluez";

    static String TAG = "BlueZInterface";
    private DBusConnection bus = null;
    private Manager manager = null;
    private Adapter adapter = null;
    private Listener listener = null;
    private CharacteristicWatcher mWatcher = null;
    private List<String> mWatchedServices = null;

    public BlueZInterface(Listener listener) {
        super();
        this.listener = listener;
    }

    @SuppressWarnings("rawtypes")
    private Map<Class, DBusSigHandler> listeners = new HashMap<Class, DBusSigHandler>();

    public synchronized Listener getListener() {
        return listener;
    }

    public synchronized void setListener(Listener l) {
        this.listener = l;
    }

    public synchronized void removeListener() {
        this.listener = null;
    }

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    private synchronized void disconnectDBus() {
        adapter = null;
        manager = null;

        if (bus == null)
            return;

        // there's no way we will not have a bus but a watcher
        if (mWatcher != null) {
            for (String p : mWatchedServices) {
                try {
                    Service s;
                    s = bus.getRemoteObject(DBUS_BLUEZ, p, Service.class);
                    s.UnregisterCharacteristicsWatcher(new Path(Watcher.PATH));
                } catch (Exception e) {
                    Log.e(TAG, "failure while unregistering watcher", e);
                }
            }
            mWatchedServices = null;
            bus.unExportObject(Watcher.PATH);
            mWatcher = null;
        }

        Iterator<Map.Entry<Class, DBusSigHandler>> it =
                listeners.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Class, DBusSigHandler> pairs =
                    (Map.Entry<Class, DBusSigHandler>) it.next();

            try {
                bus.removeSigHandler(pairs.getKey(), pairs.getValue());
            } catch (DBusException e) {
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        listeners.clear();
        bus.disconnect();
        bus = null;
        Log.i(TAG, "disconnected from bus");
    }

    private synchronized void connectDBus() {
        if (bus != null) {
            Log.i(TAG, "DBus is connected, no need to connect again");
            return;
        }

        try {
            Log.i(TAG, "getting on bus");
            bus = DBusConnection.getConnection(DBusConnection.SYSTEM);

            Log.i(TAG, "registering signals");
            // DBusSigHandler s = new DBusOwnerNameChanged(this);
            // bus.addSigHandler(NameOwnerChanged.class, s);
            // listeners.put(NameOwnerChanged.class, s);
            Log.i(TAG, "dbus handlers registered");
            return;
        } catch (DBusException e) {
            Log.e(TAG, "Failed to get on bus", e);
        }
        Stop();
    }

    private synchronized Manager getBluezManager() {
        if (bus == null)
            return null;

        Manager out;
        try {
            out = bus.getRemoteObject(DBUS_BLUEZ, "/", Manager.class);
        } catch (DBusException e) {
            Log.e(TAG, "BlueZ isn't available", e);
            return null;
        } catch (DBus.Error.ServiceUnknown e1) {
            Log.e(TAG, "BlueZ isn't ready");
            return null;
        }

        Log.d(TAG, "Got manager object");
        return out;
    }

    public synchronized void resetBlueZConnection() {
        this.adapter = null;
        this.manager = null;
    }

    private synchronized Adapter getBluezAdapter() {
        if (bus == null)
            return null;

        if (manager == null)
            return null;

        Adapter out;
        try {
            String p = manager.DefaultAdapter().toString();
            out = bus.getRemoteObject(DBUS_BLUEZ, p, Adapter.class);
        } catch (DBusException e) {
            Log.e(TAG, "BlueZ issue", e);
            return null;
        } catch (DBus.Error.ServiceUnknown e1) {
            Log.e(TAG, "BlueZ isn't ready");
            return null;
        }

        Log.d(TAG, "Got manager object");
        return out;
    }

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public synchronized boolean Start(boolean disconnect) {
        if (disconnect == true)
            disconnectDBus();
        connectDBus();
        manager = getBluezManager();
        adapter = getBluezAdapter();

        try {
            DBusSigHandler s = new DeviceFoundHandler(this);
            bus.addSigHandler(org.bluez.Adapter.DeviceFound.class, s);
            listeners.put(NameOwnerChanged.class, s);
        } catch (DBusException e) {
            Log.e(TAG, "failed registering for dbus signal", e);
        }

        return adapter != null;
    }

    public synchronized boolean Start() {
        return Start(false);
    }

    public synchronized void Stop() {
        this.disconnectDBus();
    }

    public synchronized boolean Status() {
        return adapter != null;
    }

    private String createDevice(String address) throws DBusExecutionException {
        Path d = adapter.CreateDevice(address);
        if (d == null)
            return null;
        return d.toString();
    }

    private String findDevice(String address) throws Error.InvalidArguments {
        try {
            Path d = adapter.FindDevice(address);
            if (d != null)
                return d.toString();
            return createDevice(address);
        } catch (Error.DoesNotExist e1) {
            return createDevice(address);
        }
    }

    class BlueZConnectionError extends Exception {
    }

    public Device getDevice(String address) throws BlueZConnectionError, DBusException {
        checkBlueZStatus();

        String path = findDevice(address);

        if (path == null)
            return null;

        return bus.getRemoteObject(DBUS_BLUEZ, path, Device.class);
    }

    private void checkBlueZStatus() throws BlueZConnectionError {
        if (Status() == false) {
            if (Start() == false) {
                throw new RuntimeException("Failed to connect to BlueZ");
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public Map<String, Variant> getDeviceProperties(String address) throws BlueZConnectionError,
            DBusException, Error.DoesNotExist, Error.InvalidArguments {

        checkBlueZStatus();

        Device d;
        d = getDevice(address);

        if (d == null)
            return null;

        Map<String, Variant> o = d.GetProperties();
        Log.v(TAG, "device " + o);
        return o;
    }

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    public List<String> getUUIDs(String address) throws DoesNotExist, InvalidArguments,
            BlueZConnectionError, DBusException {
        checkBlueZStatus();

        ArrayList<String> out = new ArrayList<String>();
        Map<String, Variant> temp = getDeviceProperties(address);

        if (temp.containsKey("UUIDs")) {
            List<String> t = (List<String>) temp.get("UUIDs").getValue();
            for (String k : t)
                out.add(k);
        }
        return out;
    }

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public Map<String, String> getServicesPathForID(String address, BluetoothGattID serviceID)
            throws DoesNotExist, InvalidArguments, BlueZConnectionError, DBusException {
        checkBlueZStatus();

        Map<String, String> out = new HashMap<String, String>();
        Map<String, Variant> temp = getDeviceProperties(address);

        if (temp.containsKey("Services")) {
            List<Path> services = (List<Path>) temp.get("Services").getValue();
            for (Path p : services) {
                Service s;
                try {
                    s = bus.getRemoteObject(DBUS_BLUEZ, p.toString(), Service.class);
                    Map<String, Variant> sprop = s.GetProperties();
                    Log.d(TAG, "service " + sprop);
                    if (!sprop.containsKey("UUID")) {
                        Log.d(TAG, "no uuid, can't compare");
                        continue;
                    }

                    String s_uuid = sprop.get("UUID").getValue().toString().trim();
                    Log.d(TAG, "service " + p.toString() + " " + s_uuid);

                    if (s_uuid.length() > 8 && !s_uuid.endsWith("1000-8000-00805f9b34fb")) {
                        Log.e(TAG, "128b UUID with wrong ending");
                        continue;
                    }

                    Log.d(TAG, "possible match");
                    BluetoothGattID id;
                    if (s_uuid.length() <= 8) {
                        id = BluetoothGattID.getUuuid128FromUuid16(
                                Integer.valueOf(s_uuid, 16));
                        Log.d(TAG, "16b uuid: " + id);
                    } else {
                        try {
                            id = new BluetoothGattID(s_uuid);
                            Log.d(TAG, "128b uuid: " + id);
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, "error parsing 128b uuid", e);
                            continue;
                        }
                    }

                    if (serviceID == null)
                        Log.v(TAG, "serviceID is null, I will match anyhing");

                    Log.v(TAG, "compairing " + serviceID);
                    Log.v(TAG, "with " + id);
                    if (serviceID == null || serviceID.equals(id)) {
                        Log.d(TAG, "match!");
                        out.put(p.toString(), id.toString());
                    }

                } catch (DBusException e) {
                    Log.e(TAG, "error while resolving services", e);
                    return null;
                }

            }
        }
        return out;
    }

    public void getServices(int connID, String address, BluetoothGattID serviceID)
            throws BlueZConnectionError, DoesNotExist, InvalidArguments, DBusException {
        checkBlueZStatus();

        Map<String, String> paths = getServicesPathForID(address, serviceID);

        if (paths == null) {
            listener.serviceDiscoveredFinished(connID, BleConstants.GATT_ERROR);
            return;
        }

        for (Entry<String, String> p : paths.entrySet()) {
            listener.serviceDiscovered(connID, address, p.getValue(), p.getKey());
        }
        listener.serviceDiscoveredFinished(connID, BleConstants.GATT_SUCCESS);
    }

    @SuppressWarnings("rawtypes")
    public Map<BluetoothGattID, String> getCharacteristicsForService(String serPath)
            throws BlueZConnectionError, DBusException {
        checkBlueZStatus();

        Map<BluetoothGattID, String> out = new HashMap<BluetoothGattID, String>();
        Service s;
        List<Path> cha = null;

        s = bus.getRemoteObject(DBUS_BLUEZ, serPath, Service.class);
        cha = s.DiscoverCharacteristics();
        for (Path p : cha) {
            Characteristic c = bus.getRemoteObject(DBUS_BLUEZ, p.toString(),
                    Characteristic.class);
            Map<String, Variant> prop = c.GetProperties();
            Log.v(TAG, "Char " + prop);
            BluetoothGattID id = null;
            if (prop.containsKey("UUID")) {
                Object u = c.GetProperties().get("UUID").getValue();
                Log.v(TAG, "uuid " + u);
                id = new BluetoothGattID(u.toString());
            } else {
                Log.e(TAG, "oops no uuid");
            }
            out.put(id, p.toString());
        }

        return out;
    }

    public void getCharacteristicsForService(int connID, String address, String serPath)
            throws BlueZConnectionError, DBusException {

        Map<BluetoothGattID, String> m = this.getCharacteristicsForService(serPath);
        List<Path> cha = new Vector<Path>();
        List<BluetoothGattID> uuids = new Vector<BluetoothGattID>();

        for (Entry<BluetoothGattID, String> e : m.entrySet()) {
            cha.add(new Path(e.getValue()));
            uuids.add(e.getKey());
        }

        listener.characteristicsSolved(connID, serPath, cha, uuids);
    }

    @SuppressWarnings("rawtypes")
    public Object GetCharacteristicValue(String path, String key) throws DBusException {
        Characteristic c;
        c = bus.getRemoteObject(DBUS_BLUEZ, path,
                Characteristic.class);
        Map<String, Variant> p = c.GetProperties();
        Log.v(TAG, "get char value char " + p);
        if (!p.containsKey(key))
            return null;
        return p.get(key).getValue();
    }

    public byte[] GetCharacteristicValueValue(String path) throws DBusException {
        Object o = GetCharacteristicValue(path, "Value");
        Log.v(TAG, "got char value " + o);
        byte[] d = (byte[]) o;
        String t = "";
        if (d != null)
            for (int i = 0; i < d.length; i++)
                t += " " + Integer.toHexString(d[i]);
        else
            t = "NULL";
        Log.v(TAG, "got char value " + t);
        return d;
    }

    class CharacteristicWatcher implements org.bluez.Watcher {

        private void newValue(String characteristic_path) {
            byte[] value;
            try {
                value = GetCharacteristicValueValue(characteristic_path);
            } catch (DBusException e) {
                Log.e(TAG, "failed getting characteristic value, can't notify", e);
                return;
            }
            try {
                BlueZInterface.this.listener.valueChanged(characteristic_path, value);
            } catch (Exception e) {
                Log.e(TAG, "failed while notifying user space", e);
            }
        }

        @Override
        @SuppressWarnings("rawtypes")
        public void ValueChanged(Path characteristic, Map<String, Variant> values) {
            Log.v(TAG, "CharacteristicWatcher.ValueChanged " + characteristic);
            newValue(characteristic.getPath());
        }

        @Override
        public void RawValueChanged(Path characteristic, List<Byte> values) {
            Log.v(TAG, "CharacteristicWatcher.RawValueChanged " + characteristic);
            newValue(characteristic.getPath());
        }

        @Override
        public boolean isRemote() {
            return false;
        }

    }

    public boolean writeCharacteristicValue(String path, byte[] value) throws DBusException,
            BlueZConnectionError {
        checkBlueZStatus();
        Characteristic c = bus.getRemoteObject(DBUS_BLUEZ, path,
                Characteristic.class);
        String t = "";
        for (int i = 0; i < value.length; i++)
            t += " " + Integer.toHexString(value[i]);
        Log.v(TAG, "setting Value to " + t);
        c.SetProperty("Value", new Variant<byte[]>(value));
        return true;
    }

    public enum REGISTER_RET_VALUES {
        ERROR,
        ALL_READY_REGISTERED,
        REGISTERED
    };

    public REGISTER_RET_VALUES registerCharacteristicWatcher(String charPath) throws BlueZConnectionError {
        checkBlueZStatus();

        if (mWatcher == null) {
            Log.v(TAG, "Exporting characteristic watcher");
            mWatcher = new CharacteristicWatcher();
            try {
                bus.exportObject(Watcher.PATH, mWatcher);
            } catch (DBusException e) {
                Log.e(TAG, "Failed to export watcher", e);
                return REGISTER_RET_VALUES.ERROR;
            }
            Log.v(TAG, "Exported");

            mWatchedServices = new Vector<String>();
        }

        String[] p = charPath.split("/");
        String serPath = "";
        for (int i = 1; i < p.length - 1; i++) {
            serPath += "/" + p[i];
        }

        if (mWatchedServices.contains(serPath))
            return REGISTER_RET_VALUES.ALL_READY_REGISTERED;
        Service s = null;

        try {
            s = bus.getRemoteObject(DBUS_BLUEZ, serPath, Service.class);
        } catch (Exception e) {
            Log.e(TAG, "failed getting service object", e);
            return REGISTER_RET_VALUES.ERROR;
        }
        s.RegisterCharacteristicsWatcher(new Path(Watcher.PATH));
        mWatchedServices.add(serPath);
        return REGISTER_RET_VALUES.REGISTERED;
    }

}
