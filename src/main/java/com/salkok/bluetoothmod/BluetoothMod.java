package com.salkok.bluetoothmod;

import com.salkok.bluetoothmod.host.BluetoothHost;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluetoothMod implements ModInitializer {

    public static final String MOD_ID =
            "bluetoothmod";

    public static final Logger LOGGER =
            LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        LOGGER.info(
                "Minecraft Bluetooth Multiplayer yükleniyor."
        );

        ServerLifecycleEvents.SERVER_STARTED.register(
                server -> {

                    if (!server.isRemote()) {
                        return;
                    }

                    BluetoothHost host =
                            BluetoothHost.getInstance();

                    if (!host.isEnabled()) {
                        return;
                    }

                    int port =
                            server.getServerPort();

                    if (port <= 0) {

                        LOGGER.error(
                                "Minecraft LAN portu alınamadı."
                        );

                        return;
                    }

                    LOGGER.info(
                            "Minecraft LAN sunucusu hazır. Port: {}",
                            port
                    );

                    host.startHostServer(port);
                }
        );

        ServerLifecycleEvents.SERVER_STOPPING.register(
                server -> {

                    if (server.isRemote()) {

                        BluetoothHost
                                .getInstance()
                                .stopHostServer();
                    }
                }
        );

        LOGGER.info(
                "Minecraft Bluetooth Multiplayer yüklendi."
        );
    }
                        }
