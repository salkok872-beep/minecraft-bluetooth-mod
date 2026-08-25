package com.salkok.bluetoothmod.bluetooth;

import com.salkok.bluetoothmod.android.AndroidBluetoothBridge;
import com.salkok.bluetoothmod.BluetoothMod;
import java.util.List;

public abstract class BluetoothBridge {
    private static BluetoothBridge INSTANCE;

    public static BluetoothBridge getInstance() {
        if (INSTANCE == null) {
            try {
                Class.forName("android.os.Build");
                INSTANCE = new AndroidBluetoothBridge();
                BluetoothMod.LOGGER.info("Android Bluetooth surucusu yuklendi.");
            } catch (ClassNotFoundException e) {
                INSTANCE = new PcBluetoothBridge();
                BluetoothMod.LOGGER.info("PC Bluetooth surucusu yuklendi.");
            }
        }
        return INSTANCE;
    }

    public abstract void startServer();
    public abstract BluetoothConnection connectToDevice(String address);
    public abstract List<BluetoothDeviceInfo> getPairedDevices();
}
