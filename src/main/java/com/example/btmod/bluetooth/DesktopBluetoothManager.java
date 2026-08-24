package com.example.btmod.bluetooth;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.InputStream;
import java.io.OutputStream;

public class DesktopBluetoothManager {
    public static final String BT_UUID_STR = "0000110100001000800000805F9B34FB";
    private static StreamConnectionNotifier serverNotifier;
    private static boolean isRunning = false;

    public static void startServer(ServerConnectionCallback callback) {
        if (isRunning) return;
        isRunning = true;

        new Thread(() -> {
            try {
                String connectionURL = "btspp://localhost:" + BT_UUID_STR + ";name=MinecraftBluetoothMod;authenticate=false;encrypt=false";
                serverNotifier = (StreamConnectionNotifier) Connector.open(connectionURL);
                while (isRunning) {
                    StreamConnection connection = serverNotifier.acceptAndOpen();
                    callback.onClientConnected(connection.openInputStream(), connection.openOutputStream());
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public interface ServerConnectionCallback {
        void onClientConnected(InputStream in, OutputStream out);
    }
}