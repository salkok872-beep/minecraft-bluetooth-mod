package com.example.btmod.bluetooth;

import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothBridge {
    private static Boolean isAndroid = null;

    public static boolean isAndroidEnvironment() {
        if (isAndroid == null) {
            try {
                Class.forName("android.bluetooth.BluetoothAdapter");
                isAndroid = true;
            } catch (ClassNotFoundException e) {
                isAndroid = false;
            }
        }
        return isAndroid;
    }

    public static void startBluetoothHost(ConnectionCallback callback) {
        if (isAndroidEnvironment()) {
            AndroidBluetoothManager.startServer(callback::onConnectionEstablished);
        } else {
            DesktopBluetoothManager.startServer(callback::onConnectionEstablished);
        }
    }

    public interface ConnectionCallback {
        void onConnectionEstablished(InputStream in, OutputStream out);
    }
}