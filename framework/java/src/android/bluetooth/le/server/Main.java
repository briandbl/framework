
package android.bluetooth.le.server;

import android.app.ActivityThread;
import android.content.Context;
import android.os.Looper;
import android.os.ServiceManager;
import android.util.Log;

import dalvik.system.VMRuntime;

class ServerThread extends Thread {
    public static final String DBUS_SOCKET = "/dev/socket/dbus";

    private static final String TAG = "btle-Server";
    private static final boolean DBG = false;

    @Override
    public void run() {
        /**
         * Not in use right now.
         */
        Looper.prepareMainLooper();

        //Process.setThreadPriority(
        //        Process.THREAD_PRIORITY_FOREGROUND);

        //BinderInternal.disableBackgroundScheduling(true);
        //Process.setCanSelfBackground(false);

        try {
            ActivityThread at = ActivityThread.systemMain();
            Context c = at.getSystemContext();

            BluetoothGatt s = new BluetoothGatt(c);
            ServiceManager.addService(BluetoothGatt.BLUETOOTH_LE_SERVICE, s);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            Log.i(TAG, "Started Bluetooth Service");
        } catch (RuntimeException e) {
            Log.e(TAG, "failed to start service", e);
        }

        Looper.loop();
    }
};

public class Main {
    private static final String TAG = "btle-server";

    public static void main(String[] args) throws InterruptedException
    {
        dalvik.system.VMRuntime.getRuntime().clearGrowthLimit();

        // The system server has to run all of the time, so it needs to be
        // as efficient as possible with its memory usage.
        VMRuntime.getRuntime().setTargetHeapUtilization(0.8f);
        Thread thr = new ServerThread();
        thr.setName("com.manuelnaranjo.btle.ServerThread");
        thr.run();
    }
}
