/*
 * Copyright (c) 2012 Naranjo Manuel Francisco
 * Copyright (c) 2011-2012, Code Aurora Forum. All rights reserved.
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

package android.bluetooth.le;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

public class BluetoothAdapterLE {
    private static final String TAG = "BluetoothAdapterLE";
    private static final boolean DBG = false;
    
    private BluetoothAdapter mAdapter;
    
    static {
        Log.i(TAG, "BluetoothAdapterLE static init");
    }
}
