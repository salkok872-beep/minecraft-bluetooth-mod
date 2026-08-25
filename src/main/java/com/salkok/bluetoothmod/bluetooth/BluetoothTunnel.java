package com.salkok.bluetoothmod.bluetooth;

import com.salkok.bluetoothmod.BluetoothMod;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BluetoothTunnel {
    private ServerSocket localProxyServer;
    private boolean running = false;

    public int startLocalProxy(BluetoothConnection btConnection) {
        try {
            localProxyServer = new ServerSocket(0);
            int localPort = localProxyServer.getLocalPort();
            running = true;

            new Thread(() -> {
                while (running) {
                    try {
                        Socket mcSocket = localProxyServer.accept();
                        bridgeStreams(mcSocket.getInputStream(), btConnection.getOutputStream());
                        bridgeStreams(btConnection.getInputStream(), mcSocket.getOutputStream());
                    } catch (Exception e) {
                        BluetoothMod.LOGGER.error("Tünel aktarım hatası: ", e);
                    }
                }
            }).start();

            return localPort;
        } catch (Exception e) {
            BluetoothMod.LOGGER.error("Yerel Tünel Başlatılamadı: ", e);
            return -1;
        }
    }

    private void bridgeStreams(InputStream in, OutputStream out) {
        new Thread(() -> {
            byte[] buffer = new byte[8192];
            int read;
            try {
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                    out.flush();
                }
            } catch (Exception ignored) {}
        }).start();
    }

    public void stop() {
        running = false;
        try {
            if (localProxyServer != null) localProxyServer.close();
        } catch (Exception ignored) {}
    }
}
