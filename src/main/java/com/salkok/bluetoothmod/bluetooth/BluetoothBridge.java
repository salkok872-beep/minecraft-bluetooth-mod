package com.salkok.bluetoothmod.bluetooth;

import java.util.List;

public abstract class BluetoothBridge {
    public abstract void startServer();
    public abstract BluetoothConnection connectToDevice(String address);
    public abstract List<BluetoothDeviceInfo> getPairedDevices();
}
