/*
 * Copyright (c) 2012 Naranjo Manuel Francisco.
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
package android.bluetooth.le.server;

import android.app.ActivityThread;
import android.content.Context;
import android.os.Looper;
import android.os.ServiceManager;
import android.util.Log;

import com.android.internal.os.BinderInternal;

import dalvik.system.VMRuntime;

class ServerThread extends Thread {
    public static final String DBUS_SOCKET = "/dev/socket/dbus";

    private static final String TAG = "btle-Server";
    private static final boolean DBG = false;

    @Override
    public void run() {
        Looper.prepareMainLooper();

        android.os.Process.setThreadPriority(
                android.os.Process.THREAD_PRIORITY_FOREGROUND);

        BinderInternal.disableBackgroundScheduling(true);
        android.os.Process.setCanSelfBackground(false);

        try {
            ActivityThread at = ActivityThread.systemMain();
            Context c = at.getSystemContext();

            BluetoothGatt s = new BluetoothGatt();
            ServiceManager.addService(BluetoothGatt.BLUETOOTH_LE_SERVICE, s);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "failed to start", e);
            System.exit(1);
        }
        Looper.loop();
    }
};

public class Main {
    private static final String TAG = "btle-server";

    public static void main(String[] args) throws InterruptedException
    {
        if (args.length>0 && args[0].equals("--version")){
            System.out.println("Framework Version: " + BluetoothGatt.FRAMEWORK_VERSION);
            System.out.println("API Version: " + BluetoothGatt.API_LEVEL);
            System.exit(0);
            return;
        }
        dalvik.system.VMRuntime.getRuntime().clearGrowthLimit();

        // The system server has to run all of the time, so it needs to be
        // as efficient as possible with its memory usage.
        VMRuntime.getRuntime().setTargetHeapUtilization(0.8f);
        Thread thr = new ServerThread();
        thr.setName("com.manuelnaranjo.btle.ServerThread");
        thr.run();
    }
}
