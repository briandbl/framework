
package android.server.le.test;

import android.bluetooth.BluetoothClass.Device;

import org.bluez.Adapter;
import org.bluez.Adapter.DeviceCreated;
import org.bluez.Error.InvalidArguments;
import org.bluez.Manager;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestDevice {
    private DBusConnection dbus;
    private Manager manager;
    private Adapter adapter;
    private List<String> pending = new ArrayList<String>();

    @SuppressWarnings("rawtypes")
    private Map<Class, DBusSigHandler> listeners =
            new HashMap<Class, DBusSigHandler>();

    public TestDevice() {
    }

    void DumpDeviceProperties(org.bluez.Device dev) {
        Map<String, Variant> p = dev.GetProperties();

        for (String k : p.keySet())
            info(k + ":\t" + p.get(k).toString());

        //try {
        //    for (Path n : dev.ListNodes())
        //        info(n.toString());
        //} catch (Exception e) {
        //    info("Failed to list nodes " + e.getMessage());
        //}

        /*try {
            Map<UInt32, String> ser = dev.DiscoverServices("");
            Iterator<Map.Entry<UInt32, String>> it =
                    ser.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UInt32, String> pairs =
                        (Map.Entry<UInt32, String>) it.next();

                info("" + pairs.getKey() + ": " + pairs.getValue());
                it.remove(); // avoids a ConcurrentModificationException
            }
        } catch (org.bluez.Error.InProgress e) {
            info("In progress can't discover services again");
        }*/
    }

    public synchronized void Created(String path) {
        info("created " + path);
        try {
            org.bluez.Device dev;
            dev = dbus.getRemoteObject("org.bluez", path, org.bluez.Device.class);

            Variant v = dev.GetProperties().get("Address");
            String addr = (String) v.getValue();
            info("created " + addr);

            DumpDeviceProperties(dev);

            if (pending.contains(addr))
                pending.remove(addr);
        } catch (DBusException e) {
            error("Failed while processing", e);
        }
    }

    @SuppressWarnings("rawtypes")
    public void ConnectDBus() throws DBusException {
        info("getting on bus");
        dbus = DBusConnection.getConnection(DBusConnection.SYSTEM);
        info("got bus");

        info("registering signals");
        DBusSigHandler t;

        t = new DeviceCreatedHandler();
        this.listeners.put(Adapter.DeviceCreated.class, t);
        dbus.addSigHandler(Adapter.DeviceCreated.class,
                (DeviceCreatedHandler) t);

        t = new DeviceRemovedHandler();
        this.listeners.put(Adapter.DeviceRemoved.class, t);
        dbus.addSigHandler(Adapter.DeviceRemoved.class,
                (DeviceRemovedHandler) t);

        // ManagerPropertyChangedHandler mpch = new
        // ManagerPropertyChangedHandler();
        info("dbus handlers registered");
    }

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public void DisconnectDBus() throws DBusException {

        Iterator<Map.Entry<Class, DBusSigHandler>> it =
                listeners.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Class, DBusSigHandler> pairs =
                    (Map.Entry<Class, DBusSigHandler>) it.next();

            dbus.removeSigHandler(pairs.getKey(), pairs.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }

        listeners.clear();

        adapter = null;
        manager = null;
        dbus.disconnect();
        dbus = null;
    }

    public void ConnectToBlueZ() throws DBusException {
        manager = dbus.getRemoteObject("org.bluez", "/", Manager.class);
        info("got manager");
        info("adapter path is " + manager.DefaultAdapter().toString());
        adapter = dbus.getRemoteObject("org.bluez",
                manager.DefaultAdapter().toString(),
                Adapter.class);
    }

    static void info(String s) {
        System.out.println(s);
    }

    static void error(String s, Throwable t) {
        System.err.println(s);
        t.printStackTrace();
    }

    @SuppressWarnings("rawtypes")
    public void process(String addr) throws Exception {
        try {
            pending.add(addr);
            info("creating: " + addr);
            adapter.CreateDevice(addr);
            return;
        } catch (org.bluez.Error.AlreadyExists e) {
            info("device already exists");
            Path d = adapter.FindDevice(addr);
            org.bluez.Device dev;
            dev = dbus.getRemoteObject("org.bluez", d.getPath(), org.bluez.Device.class);
            DumpDeviceProperties(dev);
            pending.remove(addr);
        }
    }

    class DeviceCreatedHandler implements DBusSigHandler<DeviceCreated> {

        @Override
        public void handle(DeviceCreated s) {
            TestDevice.info("created: " + s.device);
            TestDevice.this.Created(s.device.toString());
        }
    }

    public static void main(String[] args)
    {
        info("Starting test");

        TestDevice test = new TestDevice();
        int i = 0;
        try {
            test.ConnectDBus();
            test.ConnectToBlueZ();
            for (String addr : args)
                try {
                    info("proccessing " + addr);
                    test.process(addr);
                } catch (Exception e) {
                    error("Something failed", e);
                }
            while (i<1000){
                Thread.sleep(100);
                if (test.pending.size()==0)
                    i++;
            }
            test.DisconnectDBus();
        } catch (Exception e) {
            error("Something failed", e);
        }

        info("Test completed");
        System.exit(0);
    }
}
