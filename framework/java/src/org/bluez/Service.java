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
import org.freedesktop.dbus.DBusInterfaceName;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;

import java.util.List;
import java.util.Map;

/**
 * Interface for connecting to org.bluez.Service
 * 
 * comes from doc/attribute-api.txt on ics-mr0-release
 * 
 * DBus Path:
 * - remote: [prefix]/{hci0}/{device0}/{service0, service1, ...}
 * - local: [prefix]/{hci0}/{service0, service1, ...}
 * 
 * @author Manuel Naranjo <naranjo.manuel@gmail.com>
 */
@DBusInterfaceName("org.bluez.Characteristic")
public interface Service extends DBusInterface {
    /**
     * Update characteristic value. For a remote characteristic, this method
     * triggers a GATT characteristic value write procedure setting a new value
     * for this characteristic. The GATT sub-procedure is automatically selected
     * based on the characteristic properties and value length.
     */
    public void SetValue(List<Byte> value);

    @SuppressWarnings("rawtypes")
    /**
     * Returns all properties for the interface.
     * 
     * String Name (mandatory) [readonly]
     *      General name of service
     * String Description (optional) [readonly]
     *      Description of service
     * String UUID (mandatory) [readonly]
     *      UUID of service. Service class value for SDP and GATT
     *      UUID for attribute based services.
     * List<Path> Characteristics [readonly]
     *      This list contains the characteristics owned by this
     *      specific service and other characteristics from service
     *      includes. That way no complicated service includes array
     *      is needed.
     */
    public Map<String, Variant> GetProperties();

    /**
     * Discover all characteristics that belongs in this service. When it
     * returns all the characteristics paths will be already registered. It will
     * return the characteristics paths as soon as they are discovered. After
     * that it will try to read all values.
     */
    public List<Path> DiscoverCharacteristics();

    /**
     * Register a watcher to monitor characteristic changes. A watcher will be
     * registered for this service and will notify about any changed
     * characteristics in the service. This also notifies about any included
     * characteristics.
     */
    public void RegisterCharacteristicsWatcher(Path agent);

    /**
     * Unregister a watcher.
     */
    public void UnregisterCharacteristicsWatcher(Path agent);
}
