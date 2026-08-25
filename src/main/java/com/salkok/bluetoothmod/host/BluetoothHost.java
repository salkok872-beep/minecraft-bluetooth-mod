package com.salkok.bluetoothmod.host;

import com.salkok.bluetoothmod.android.AndroidBluetoothBridge;

public class BluetoothHost {
    private static BluetoothHost instance;
    private final AndroidBluetoothBridge bridge;

    private BluetoothHost() {
        this.bridge = new AndroidBluetoothBridge();
    }

    public static BluetoothHost getInstance() {
        if (instance == null) {
            instance = new BluetoothHost();
        }
        return instance;
    }

    public void startHostServer() {
        bridge.startServer();
    }
}
