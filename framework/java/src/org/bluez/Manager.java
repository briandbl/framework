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
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.Map;

/**
 * 
 * Interface for connecting to org.bluez.Manager
 * 
 * @author Manuel Naranjo <naranjo.manuel@gmail.com>
 *
 */
public interface Manager extends DBusInterface{
    
    /*
     * Methods from manager-api.txt
     */

    /**
     * 
     * Dict with:
     * List<Path> Adapters [readonly]
     *
     */
    @SuppressWarnings("rawtypes")
    public Map<String, Variant> GetProperties() throws 
            Error.DoesNotExist, Error.InvalidArguments;

    
    public Path DefaultAdapter() throws
            Error.InvalidArguments, Error.NoSuchAdapter;
    
    public Path FindAdapter(String pattern) throws
            Error.InvalidArguments, Error.NoSuchAdapter;
    
    /*
     * Signals from manager-api.txt
     */
    
    @SuppressWarnings("rawtypes")
    public class PropertyChanged extends DBusSignal {
        public String name;
        public Variant value;
           
        public PropertyChanged(String path, String name, Variant value) 
                throws DBusException{
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
