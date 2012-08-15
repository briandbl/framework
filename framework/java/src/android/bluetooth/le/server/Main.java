package android.bluetooth.le.server;

import android.app.ActivityManagerNative;
import android.app.ActivityThread;
import android.app.IActivityManager;
import android.content.Context;
import android.os.IServiceManager;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceManagerNative;
import android.util.Log;

import com.android.internal.os.BinderInternal;

class ServerThread extends Thread {
    public static final String DBUS_SOCKET="/dev/socket/dbus";

    private static final String TAG = "btle-Server";
    private static final boolean DBG = false;
    
    @Override
    public void run(){
        /**
         * Not in use right now.
         */
        Looper.prepareMainLooper();

        Process.setThreadPriority(
                Process.THREAD_PRIORITY_FOREGROUND);
        
        BinderInternal.disableBackgroundScheduling(true);
        Process.setCanSelfBackground(false);
        
        try {
            IServiceManager sServiceManager;
            sServiceManager = ServiceManagerNative.asInterface(BinderInternal.getContextObject());
            Log.v(TAG, "Got s manager " + sServiceManager);
        } catch (Exception e){
            Log.e(TAG, "error", e);
        }
        
        try {
            for (String s: ServiceManager.listServices())
                Log.v(TAG, s);
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
       
        
        try {
            Log.i(TAG, "Started Bluetooth Service");
        } catch ( RuntimeException e) {
            Log.e(TAG, "failed to start service", e);
        }

        Looper.loop();
    }
};


public class Main {
    private static final String TAG="btle-server";

    public static void main(String[] args)
    {
   
        Looper.prepareMainLooper();
        try { 
            ActivityThread at = ActivityThread.systemMain();
            Context c = at.getSystemContext();
            
            BluetoothGatt s = new BluetoothGatt(c);
            ServiceManager.addService("btle", s);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Log.v(TAG, "registered");
        Looper.loop();
    }
}
