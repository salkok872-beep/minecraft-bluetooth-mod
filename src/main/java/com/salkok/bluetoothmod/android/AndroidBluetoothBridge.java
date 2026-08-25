package com.salkok.bluetoothmod.android;

import com.salkok.bluetoothmod.bluetooth.*;
import com.salkok.bluetoothmod.BluetoothMod;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class AndroidBluetoothBridge extends BluetoothBridge {
    public static final UUID MC_BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    public void startServer() {
        new Thread(() -> {
            try {
                BluetoothMod.LOGGER.info("Android RFCOMM dinleyicisi baslatildi.");
            } catch (Exception e) {
                BluetoothMod.LOGGER.error("Android Bluetooth sunucu hatasi: ", e);
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
