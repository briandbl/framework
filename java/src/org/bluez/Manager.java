package org.bluez;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

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
    public class PropertyChanged<T> extends DBusSignal {
        String name;
        Variant<T> value;
           
        public PropertyChanged(String path, String name, Variant<T> value) throws DBusException{
            super(path, name, value);
            this.name = name;
            this.value = value;
        }
    }
    
    public class AdapterAdded extends DBusSignal {
        Path value;
        
        
        public AdapterAdded(String path, Path value) throws DBusException{
            super(path, value);
            this.value = value;
        }
    }
    
    public class AdapterRemoved extends DBusSignal {
        Path value;
        
        
        public AdapterRemoved(String path, Path value) throws DBusException{
            super(path, value);
            this.value = value;
        }
    }    

    public class DefaultAdapterChanged extends DBusSignal {
        Path value;
        
        
        public DefaultAdapterChanged(String path, Path value) throws DBusException{
            super(path, value);
            this.value = value;
        }
    } 
}
