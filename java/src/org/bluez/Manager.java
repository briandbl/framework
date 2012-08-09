package org.bluez;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.Variant;

import java.util.Map;

public interface Manager extends DBusInterface{

    /*
     * Methods from manager-api.txt
     */
    
    @SuppressWarnings("rawtypes")
    public Map<String, Variant> GetProperties() throws 
            Error.DoesNotExist, Error.InvalidArguments;
    
    public String DefaultAdapter() throws
            Error.InvalidArguments, Error.NoSuchAdapter;
    
    public String FindAdapter(String pattern) throws
            Error.InvalidArguments, Error.NoSuchAdapter;
    
    /*
     * Signals from manager-api.txt
     */
    Signals     PropertyChanged(string name, variant value)
                AdapterAdded(object adapter)
                AdapterRemoved(object adapter)
                DefaultAdapterChanged(object adapter)
    
    /*
     * Properties from manager-api.txt
     */
    Properties
}
