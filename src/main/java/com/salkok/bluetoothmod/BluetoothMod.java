package com.salkok.bluetoothmod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluetoothMod implements ModInitializer {
    public static final String MOD_ID = "minecraft-bluetooth-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Bluetooth Multiplayer Mod initialized successfully!");
    }
}

