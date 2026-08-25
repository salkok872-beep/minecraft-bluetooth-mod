package com.salkok.bluetoothmod.host;

import com.salkok.bluetoothmod.bluetooth.BluetoothBridge;
import com.salkok.bluetoothmod.BluetoothMod;

public class BluetoothHost {
    private static final BluetoothHost INSTANCE = new BluetoothHost();
    private boolean isRunning = false;

    public static BluetoothHost getInstance() {
        return INSTANCE;
    }

    public void startHostServer() {
        if (isRunning) return;
        isRunning = true;
        BluetoothMod.LOGGER.info("Bluetooth Host yayini baslatildi.");
        BluetoothBridge.getInstance().startServer();
    }
}
