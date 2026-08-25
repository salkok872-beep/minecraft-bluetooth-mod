package com.salkok.bluetoothmod.bluetooth;

import com.salkok.bluetoothmod.BluetoothMod;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BluetoothTunnel {

    private ServerSocket localProxyServer;
    private volatile boolean running;

    public int startLocalProxy(
            BluetoothConnection btConnection
    ) {

        if (btConnection == null ||
                !btConnection.isConnected()) {
            return -1;
        }

        try {

            localProxyServer =
                    new ServerSocket(0);

            int localPort =
                    localProxyServer.getLocalPort();

            running = true;

            new Thread(() -> {

                try {

                    Socket minecraftSocket =
                            localProxyServer.accept();

                    bridgeStreams(
                            minecraftSocket.getInputStream(),
                            btConnection.getOutputStream()
                    );

                    bridgeStreams(
                            btConnection.getInputStream(),
                            minecraftSocket.getOutputStream()
                    );

                } catch (Exception e) {

                    if (running) {
                        BluetoothMod.LOGGER.error(
                                "Bluetooth client tünel hatası.",
                                e
                        );
                    }

                }

            }, "Minecraft-Bluetooth-LocalProxy").start();

            return localPort;

        } catch (Exception e) {

            BluetoothMod.LOGGER.error(
                    "Yerel Bluetooth proxy başlatılamadı.",
                    e
            );

            return -1;
        }
    }

    private void bridgeStreams(
            InputStream input,
            OutputStream output
    ) {

        new Thread(() -> {

            byte[] buffer =
                    new byte[16384];

            try {

                int read;

                while (running &&
                        (read = input.read(buffer)) != -1) {

                    output.write(
                            buffer,
                            0,
                            read
                    );

                    output.flush();
                }

            } catch (Exception ignored) {
            }

        }, "Minecraft-Bluetooth-Stream").start();
    }

    public void stop() {

        running = false;

        try {

            if (localProxyServer != null) {
                localProxyServer.close();
            }

        } catch (Exception ignored) {
        }

        localProxyServer = null;
    }
                            }
