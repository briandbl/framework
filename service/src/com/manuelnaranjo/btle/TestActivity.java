
package com.manuelnaranjo.btle;

import android.app.Activity;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ServiceManager;
import android.util.Log;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.IBluetooth;
import android.bluetooth.le.IBluetoothLE;

public class TestActivity extends Activity {

    public static final String BLUETOOTH_DBUS = "android.permission.BLUETOOTH_DBUS";

    static final String TAG = "BTLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IBinder b = ServiceManager.getService("btle");
        
        if (b != null) {
            IBluetoothLE service = IBluetoothLE.Stub.asInterface(b);
            sAdapter = new BluetoothAdapter(service);
        }
        try {
            IBinder b = (Binder)super.getSystemService("btle");
             bt = IBluetoothLE.Stub.asInterface(b);
        } catch (Exception e) {
            Log.e(TAG, "test");
        }
    }
}
