
package android.bluetooth.le.server;

import android.app.ActivityThread;
import android.content.Context;
import android.content.Intent;
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
