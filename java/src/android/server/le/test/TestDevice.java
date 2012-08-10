
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
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class DeviceDisappearedHandler implements DBusSigHandler<DeviceDisappeared>{
    @Override
    public void handle(DeviceDisappeared s) {
        TestDevice.info("disappeared: " + s.address);
    }
}

class DeviceRemovedHandler implements DBusSigHandler<DeviceRemoved>{
    @Override
    public void handle(DeviceRemoved s) {
        TestDevice.info("removed: " + s.device);
    }
}

public class TestDevice{
    private DBusConnection dbus;
    private Manager manager;
    private Adapter adapter;
    private List<String> pending = new ArrayList<String>();
    
    @SuppressWarnings("rawtypes")
    private Map<Class, DBusSigHandler> listeners = 
            new HashMap<Class, DBusSigHandler>();
    
    public TestDevice(){
    }
    
    public synchronized void Created(String addr){
        if (pending.contains(addr))
            pending.remove(addr);
        info("paired " + addr);
    }
    
    @SuppressWarnings("rawtypes")
    public void ConnectDBus() throws DBusException{
        info("getting on bus");
        dbus = DBusConnection.getConnection(DBusConnection.SYSTEM);
        info("got bus");

        info("registering signals");
        DBusSigHandler t;
 
        t =  new DeviceCreatedHandler();
        this.listeners.put(Adapter.DeviceCreated.class, t);
        dbus.addSigHandler(Adapter.DeviceCreated.class, 
                (DeviceCreatedHandler)t);
        
        t = new DeviceRemovedHandler();
        this.listeners.put(Adapter.DeviceRemoved.class, t);
        dbus.addSigHandler(Adapter.DeviceRemoved.class, 
                (DeviceRemovedHandler)t);
        
        //ManagerPropertyChangedHandler mpch = new ManagerPropertyChangedHandler();
        info ("dbus handlers registered");
    }
    
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public void DisconnectDBus() throws DBusException{
        
        Iterator<Map.Entry<Class, DBusSigHandler>> it = 
                listeners.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Class, DBusSigHandler> pairs = 
                    (Map.Entry<Class, DBusSigHandler>)it.next();
            
            dbus.removeSigHandler(pairs.getKey(), pairs.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        
        listeners.clear();
        
        adapter = null;
        manager = null;
        dbus.disconnect();
        dbus = null;
    }
    
    public void ConnectToBlueZ() throws DBusException{
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
        pending.add(addr);
        adapter.CreateDevice(addr);
    }
    
    class DeviceCreatedHandler implements DBusSigHandler<DeviceCreated>{

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
        try {
            test.ConnectDBus();
            test.ConnectToBlueZ();
            for (String addr: args)
                try {
                    info("proccessing " + addr);
                    test.process(addr);
                } catch (Exception e) {
                    error("Something failed", e);
                }
            while(test.pending.size()>0)
                Thread.sleep(100);
            test.DisconnectDBus();
        } catch (Exception e){
            error("Something failed", e);
        }
    }
}
