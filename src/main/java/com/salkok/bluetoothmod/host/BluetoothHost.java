package com.salkok.bluetoothmod.host;

import com.salkok.bluetoothmod.android.AndroidBluetoothBridge;

public class BluetoothHost {

    private static final BluetoothHost INSTANCE =
            new BluetoothHost();

    private final AndroidBluetoothBridge bridge;

    private volatile boolean enabled;

    private BluetoothHost() {
        this.bridge =
                new AndroidBluetoothBridge();
    }

    public static BluetoothHost getInstance() {
        return INSTANCE;
    }

    public void setEnabled(boolean enabled) {

        this.enabled = enabled;

        if (!enabled) {
            bridge.stopServer();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void startHostServer(int minecraftPort) {

        if (!enabled) {
            return;
        }

        bridge.startServer(minecraftPort);
    }

    public void stopHostServer() {
        bridge.stopServer();
    }
                }
