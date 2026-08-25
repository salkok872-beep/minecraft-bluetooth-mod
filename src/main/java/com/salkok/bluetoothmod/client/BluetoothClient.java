package com.salkok.bluetoothmod.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class BluetoothClient {
    public static void openDeviceSelectionScreen(MinecraftClient client, Screen parent) {
        if (client != null) {
            client.execute(() -> client.setScreen(new BluetoothDeviceSelectionScreen(parent)));
        }
    }
}
