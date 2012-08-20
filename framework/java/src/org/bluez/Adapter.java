
package org.bluez;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.Map;

/**
 * Interface for connecting to org.bluez.Adapter
 * 
 * @author Manuel Naranjo <naranjo.manuel@gmail.com>
 */
public interface Adapter extends DBusInterface {

    /*
     * Methods from adapter-api.txt
     */

    /**
     * Returns all properties for the adapter. String Address [readonly] String
     * Name [readwrite] Uint32 Class [readonly] boolean Powered [readwrite]
     * boolean Discoverable [readwrite] boolean Pairable [readwrite] Uint32
     * PaireableTimeout [readwrite] Uint32 DiscoverableTimeout [readwrite]
     * boolean Discovering [readonly] List<Path> Devices [readonly] List<String>
     * UUIDs [readonly]
     * 
     * @throws Error.InvalidArguments
     */
    @SuppressWarnings("rawtypes")
    public Map<String, Variant> GetProperties() throws
            Error.DoesNotExist, Error.InvalidArguments;

    @SuppressWarnings("rawtypes")
    public void SetProperty(String name, Variant value) throws
            Error.InvalidArguments;

    public void RequestSession() throws Error.Rejected;

    public void ReleaseSession() throws Error.DoesNotExist;

    public void StartDiscovery() throws Error.NotReady, Error.Failed;

    public void StopDiscovery() throws Error.NotReady, Error.Failed,
            Error.NotAuthorized;

    public Path FindDevice(String address) throws
            Error.DoesNotExist, Error.InvalidArguments;

    public Path CreateDevice(String address) throws Error.InvalidArguments,
            Error.Failed;

    public Path CreatePairedDevice(String address, Path agent,
            String capability) throws Error.InvalidArguments, Error.Failed;

    public void CancelDeviceCreation(String address) throws
            Error.InvalidArguments, Error.NotInProgress;

    public void RemoveDevice(Path device) throws Error.InvalidArguments,
            Error.Failed;

    public void RegisterAgent(Path agent, String capability) throws
            Error.AlreadyExists, Error.Failed;

    public void UnregisterAgent(Path agent) throws Error.DoesNotExist;

    /*
     * signals
     */
    @SuppressWarnings("rawtypes")
    public class PropertyChanged extends DBusSignal {
        public String name;
        public Variant value;

        public PropertyChanged(String path, String name, Variant value)
                throws DBusException {
            super(path, name, value);
            this.name = name;
            this.value = value;
        }
    }

    @SuppressWarnings("rawtypes")
    public class DeviceFound extends DBusSignal {
        public String address;
        public Map<String, Variant> values;

        public DeviceFound(String path, String address,
                Map<String, Variant> values) throws DBusException {
            super(path, address, values);
            this.address = address;
            this.values = values;
        }
    }

    public class DeviceDisappeared extends DBusSignal {
        public String address;

        public DeviceDisappeared(String path, String address)
                throws DBusException {
            super(path, address);
            this.address = address;
        }
    }

    public class DeviceCreated extends DBusSignal {
        public Path device;

        public DeviceCreated(String path, Path device)
                throws DBusException {
            super(path, device);
            this.device = device;
        }
    }

    public class DeviceRemoved extends DBusSignal {
        public Path device;

        public DeviceRemoved(String path, Path device)
                throws DBusException {
            super(path, device);
            this.device = device;
        }
    }
}
