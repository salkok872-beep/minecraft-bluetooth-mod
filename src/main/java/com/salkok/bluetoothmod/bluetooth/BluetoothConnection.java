package com.salkok.bluetoothmod.bluetooth;

import java.io.InputStream;
import java.io.OutputStream;

public interface BluetoothConnection {
    boolean isConnected();
    InputStream getInputStream();
    OutputStream getOutputStream();
    void close();
}
