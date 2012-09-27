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

import org.freedesktop.DBus.Method;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;

import java.util.List;
import java.util.Map;

/**
 * 
 * Interface for describing an org.bluez.Watcher watcher
 * 
 * @author Manuel Naranjo <naranjo.manuel@gmail.com>
 *
 */
public interface Watcher extends DBusInterface{

    @Method.NoReply()
    @SuppressWarnings("rawtypes")
    void ValueChanged(Path characteristic,  Map<String, Variant> values);
    
    @Method.NoReply()
    void RawValueChanged(Path characteristic, List<Byte> values);
    
    public static String PATH="/com/manuelnaranjo/le/watcher";
}
