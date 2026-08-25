package com.salkok.bluetoothmod.bluetooth;

import java.util.List;

public abstract class BluetoothBridge {

    public abstract void startServer(int minecraftPort);

    public abstract BluetoothConnection connectToDevice(String address);

    public abstract List<BluetoothDeviceInfo> getPairedDevices();

    public abstract void stopServer();
}
