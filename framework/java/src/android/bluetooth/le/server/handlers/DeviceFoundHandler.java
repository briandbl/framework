/*
 * Copyright (c) 2012 Naranjo Manuel Francisco
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.bluetooth.le.server.handlers;

import android.bluetooth.le.server.BlueZInterface;
import android.util.Log;

import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.Variant;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DeviceFoundHandler implements DBusSigHandler<org.bluez.Adapter.DeviceFound> {
    private static final String TAG = "DB-Found";
    private final BlueZInterface mInterface;
    private Set<String> bdedr_devices = new HashSet<String>();
    private Set<String> le_devices = new HashSet<String>();

    public DeviceFoundHandler(BlueZInterface blueZInterface) {
        mInterface = blueZInterface;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void handle(org.bluez.Adapter.DeviceFound s) {
        try {
            String address = s.address;
            if (bdedr_devices.contains(address))
                // this is handled by default Android libs
                return;
            Map<String, Variant> values = s.values;
            Set<String> keys = values.keySet();
            if (!le_devices.contains(address)) {
                // time to tell if it's an LE device or not
                if (keys.contains("Icon") && keys.contains("LegacyPairing"))
                {
                    // handled by default Android libs
                    bdedr_devices.add(address);
                    return;
                }
                le_devices.add(address);
            }
            
            short rssi = 0;
            if (keys.contains("Rssi"))
                rssi = ((Integer)values.get("Rssi").getValue()).shortValue();
            
            String name = "Not Known";
            if (keys.contains("Name"))
                name = values.get("Name").getValue().toString();
            
            mInterface.getListener().deviceDiscovered(address, name, rssi);
        } catch (Exception e) {
            Log.e(TAG, "ignoring", e);
        }
    }
}
