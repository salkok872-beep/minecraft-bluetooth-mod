package com.salkok.bluetoothmod.client;

import com.salkok.bluetoothmod.BluetoothMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class BluetoothClient {

    public static void openDeviceSelectionScreen(MinecraftClient client, Screen parent) {
        BluetoothMod.LOGGER.info("Cihaz secim ekrani aciliyor.");
    }

    public static void connect(String deviceAddress) {
        BluetoothMod.LOGGER.info("Bluetooth baglantisi kuruluyor: " + deviceAddress);
    }
}
