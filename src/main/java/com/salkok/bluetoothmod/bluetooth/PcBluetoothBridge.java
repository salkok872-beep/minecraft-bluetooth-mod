package com.salkok.bluetoothmod.bluetooth;

import com.salkok.bluetoothmod.BluetoothMod;

import java.util.Collections;
import java.util.List;

public class PcBluetoothBridge
        extends BluetoothBridge {

    @Override
    public void startServer(int minecraftPort) {

        BluetoothMod.LOGGER.warn(
                "PC Bluetooth backend henüz etkin değil."
        );

        BluetoothMod.LOGGER.warn(
                "Android/PojavLauncher RFCOMM backend kullanılacak."
        );
    }

    @Override
    public BluetoothConnection connectToDevice(
            String address
    ) {

        BluetoothMod.LOGGER.warn(
                "PC Bluetooth bağlantısı henüz desteklenmiyor: {}",
                address
        );

        return null;
    }

    @Override
    public List<BluetoothDeviceInfo>
    getPairedDevices() {

        return Collections.emptyList();
    }

    @Override
    public void stopServer() {
    }
            }
