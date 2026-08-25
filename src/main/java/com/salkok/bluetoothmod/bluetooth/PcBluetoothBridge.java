package com.salkok.bluetoothmod.bluetooth;

import com.salkok.bluetoothmod.BluetoothMod;
import java.util.ArrayList;
import java.util.List;

public class PcBluetoothBridge extends BluetoothBridge {

    @Override
    public void startServer() {
        new Thread(() -> {
            try {
                BluetoothMod.LOGGER.info("PC Bluetooth RFCOMM dinleyicisi baslatildi.");
            } catch (Exception e) {
                BluetoothMod.LOGGER.error("PC Bluetooth sunucu hatasi: ", e);
            }
        }).start();
    }

    @Override
    public BluetoothConnection connectToDevice(String address) {
        return null;
    }

    @Override
    public List<BluetoothDeviceInfo> getPairedDevices() {
        return new ArrayList<>();
    }
}
