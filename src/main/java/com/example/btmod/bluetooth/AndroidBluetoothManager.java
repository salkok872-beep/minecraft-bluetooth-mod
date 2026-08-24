package com.example.btmod.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class AndroidBluetoothManager {
    private static final UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String APP_NAME = "MinecraftBluetoothMod";
    private static BluetoothServerSocket serverSocket;
    private static boolean isRunning = false;

    public static void startServer(ServerConnectionCallback callback) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) return;
        isRunning = true;

        new Thread(() -> {
            try {
                serverSocket = adapter.listenUsingRfcommWithServiceRecord(APP_NAME, BT_UUID);
                while (isRunning) {
                    BluetoothSocket socket = serverSocket.accept();
                    if (socket != null) {
                        callback.onClientConnected(socket.getInputStream(), socket.getOutputStream());
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public interface ServerConnectionCallback {
        void onClientConnected(InputStream in, OutputStream out);
    }
}