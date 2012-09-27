/************************************************************************************
 *
 *  Copyright (C) 2012      Naranjo Manuel Francisco <naranjo.manuel@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ************************************************************************************/
package org.bluez;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.List;
import java.util.Map;

/**
 * Interface for connecting to org.bluez.Adapter DBus Path: - [variable
 * prefix]/{hci0,hci1,...}/dev_XX_XX_XX_XX_XX_XX
 * 
 * @author Manuel Naranjo <naranjo.manuel@gmail.com>
 */
public interface Device extends DBusInterface {

    /*
     * Methods from device-api.txt
     */

    /**
     * Properties from device 
     * 
     * String Address [readonly] 
     * String Name [readonly]
     * String Icon [readonly] 
     * Uint32 Class [readonly] 
     * List<String> UUIDs [readonly] 
     * List<Path> Services [readonly] 
     * boolean Paired [readonly]
     * boolean Connected [readonly] 
     * boolean Trusted [readwrite] 
     * boolean Blocked [readwrite] 
     * String Alias [readwrite] 
     * List<Path> Nodes [readonly] 
     * Path Adapter [readonly] 
     * boolean LegacyPairing [readonly]
     * 
     * @throws Error.InvalidArguments
     */
    @SuppressWarnings("rawtypes")
    public Map<String, Variant> GetProperties() throws
            Error.DoesNotExist, Error.InvalidArguments;

    /**
     * Changes the value of the specified property. Only properties that are
     * listed a read-write are changeable. On success this will emit a
     * PropertyChanged signal.
     */
    @SuppressWarnings("rawtypes")
    public void SetProperty(String name, Variant value) throws
            Error.InvalidArguments, Error.DoesNotExist;

    /**
     * This method starts the service discovery to retrieve remote service
     * records. The pattern parameter can be used to specify specific UUIDs. And
     * empty string will look for the public browse group. The return value is a
     * dictionary with the record handles as keys and the service record in XML
     * format as values. The key is uint32 and the value a string for this
     * dictionary.
     */
    public Map<UInt32, String> DiscoverServices(String pattern)
            throws Error.NotReady, Error.Failed, Error.InProgress;

    /**
     * This method will cancel any previous DiscoverServices transaction
     */
    public void CancelDiscovery() throws Error.NotReady,
            Error.Failed, Error.NotAuthorized;

    /**
     * This method disconnects a specific remote device by terminating the
     * low-level ACL connection. The use of this method should be restricted to
     * administrator use. A DisconnectRequested signal will be sent and the
     * actual disconnection will only happen 2 seconds later. This enables
     * upper-level applications to terminate their connections gracefully before
     * the ACL connection is terminated.
     */
    public void Disconnect() throws Error.NotConnected;

    /**
     * Returns list of device node object paths.
     */
    public List<Path> ListNodes() throws Error.InvalidArguments,
            Error.Failed, Error.OutOfMemory;

    /**
     * Creates a persistent device node binding with a remote device. The actual
     * support for the specified UUID depends if the device driver has support
     * for persistent binding. At the moment only RFCOMM TTY nodes are
     * supported.
     */
    public Path CreateNode(String uuid) throws Error.InvalidArguments,
            Error.NotSupported;

    /**
     * Removes a persistent device node binding.
     */
    public void RemoveNode(Path node) throws Error.InvalidArguments,
            Error.DoesNotExist;

    /*
     * signals
     */

    /**
     * This signal indicates a changed value of the given property.
     */
    @SuppressWarnings("rawtypes")
    public class PropertyChanged extends DBusSignal {
        String name;
        Variant value;

        public PropertyChanged(String path, String name, Variant value)
                throws DBusException {
            super(path, name, value);
            this.name = name;
            this.value = value;
        }
    }

    /**
     * This signal will be sent when a low level disconnection to a remote
     * device has been requested. The actual disconnection will happen 2 seconds
     * later.
     */
    public class DisconnectRequested extends DBusSignal {
        public DisconnectRequested(String p) throws DBusException {
            super(p);
        }
    }
    
    /**
     * Parameter is object path of created device node.
     */
    public class NodeCreated extends DBusSignal {
        Path name;

        public NodeCreated(String path, Path name)
                throws DBusException {
            super(path, name);
            this.name = name;
        }
    }
    
    /**
     * Parameter is object path of removed device node.
     */
    public class NodeRemoved extends DBusSignal {
        Path name;

        public NodeRemoved(String path, Path name)
                throws DBusException {
            super(path, name);
            this.name = name;
        }
    }

}
