
package android.server.le.test;

import org.bluez.Adapter;
import org.bluez.Adapter.DeviceCreated;
import org.bluez.Adapter.DeviceDisappeared;
import org.bluez.Adapter.DeviceFound;
import org.bluez.Adapter.DeviceRemoved;
import org.bluez.Manager;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.Variant;

import java.util.Map;

class DeviceFoundHandler implements DBusSigHandler<DeviceFound>{
    @SuppressWarnings("rawtypes")
    @Override
    public void handle(DeviceFound s) {
        TestManager.info("Found " + s.address);
        Map<String, Variant> val = s.values;
        for (String k: val.keySet())
            TestManager.info("prop " + k + ", " + val.get(k).toString());

    }    
}

class DeviceCreatedHandler implements DBusSigHandler<DeviceCreated>{
    @Override
    public void handle(DeviceCreated s) {
        TestManager.info("created: " + s.device);
    }
}

class DeviceDisappearedHandler implements DBusSigHandler<DeviceDisappeared>{
    @Override
    public void handle(DeviceDisappeared s) {
        TestManager.info("disappeared: " + s.address);
    }
}

class DeviceRemovedHandler implements DBusSigHandler<DeviceRemoved>{
    @Override
    public void handle(DeviceRemoved s) {
        TestManager.info("removed: " + s.device);
    }
}

class AdapterPropertyChangedHandler implements DBusSigHandler<Adapter.PropertyChanged>{
    @Override
    public void handle(Adapter.PropertyChanged s) {
        TestManager.info("adapter changed: " + s.name + ", " + s.value.toString() );
    }
}

class ManagerPropertyChangedHandler implements DBusSigHandler<Manager.PropertyChanged>{
    @Override
    public void handle(Manager.PropertyChanged s) {
        TestManager.info("adapter changed: " + s.name + ", " + s.value.toString() );
    }
}

public class TestManager{
    static void info(String s) {
        System.out.println(s);
    }

    static void error(String s, Throwable t) {
        System.err.println(s);
        t.printStackTrace();
    }

    @SuppressWarnings("rawtypes")
    static void process() throws Exception {
        DBusConnection dbus;
        Manager manager;
        Adapter adapter;
        Map<String, Variant> prop;

        info("getting on bus");
        dbus = DBusConnection.getConnection(DBusConnection.SYSTEM);
        info("got bus");
        manager = dbus.getRemoteObject("org.bluez", "/", Manager.class);
        info("got manager");
        info("adapter path is " + manager.DefaultAdapter().toString());
        adapter = dbus.getRemoteObject("org.bluez",
                manager.DefaultAdapter().toString(),
                Adapter.class);
        prop = adapter.GetProperties();
        info("got adapter");

        info("Keys: ");
        for (String k : prop.keySet())
            info("\t" + k + ", " + prop.get(k).getValue());

        if (prop.containsKey("Name"))
            info("Device name " + (String) prop.get("Name").getValue());
        
        AdapterPropertyChangedHandler apch =  new AdapterPropertyChangedHandler();
        DeviceCreatedHandler dch = new DeviceCreatedHandler();
        DeviceDisappearedHandler ddh = new DeviceDisappearedHandler();
        DeviceFoundHandler dfh = new DeviceFoundHandler();
        DeviceRemovedHandler drh = new DeviceRemovedHandler();
        ManagerPropertyChangedHandler mpch = new ManagerPropertyChangedHandler();
        
        info ("adding signal handlers");
        dbus.addSigHandler(Adapter.DeviceCreated.class, dch);
        dbus.addSigHandler(Adapter.DeviceDisappeared.class, ddh);
        dbus.addSigHandler(Adapter.DeviceFound.class, dfh);
        dbus.addSigHandler(Adapter.DeviceRemoved.class, drh);
        dbus.addSigHandler(Adapter.PropertyChanged.class, apch);
        dbus.addSigHandler(Manager.PropertyChanged.class, mpch);
        
        adapter.StartDiscovery();
        
        while(true)
            Thread.sleep(10);
    }
    


    public static void main(String[] args)
    {
        info("Starting test");
        try {
            process();
        } catch (Exception e) {
            error("Something failed", e);
        }
    }
}
