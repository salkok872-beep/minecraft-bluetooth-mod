package com.example.btmod;

import com.example.btmod.bluetooth.BluetoothBridge;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluetoothMod implements ModInitializer {
    public static final String MOD_ID = "btmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static BluetoothMod instance;
    private boolean isBluetoothHostActive = false;

    @Override
    public void onInitialize() {
        instance = this;
        LOGGER.info("[BT-Mod] Mod başlatılıyor...");

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            if (isBluetoothHostActive) stopBluetoothHost();
        });
    }

    public void startBluetoothHost() {
        if (isBluetoothHostActive) return;
        isBluetoothHostActive = true;
        BluetoothBridge.startBluetoothHost((in, out) -> {
            LOGGER.info("[BT-Mod] Bağlantı başarılı! Soket kuruldu.");
        });
    }

    public void stopBluetoothHost() {
        isBluetoothHostActive = false;
    }

    public static BluetoothMod getInstance() { return instance; }
    public boolean isBluetoothHostActive() { return isBluetoothHostActive; }
}