package com.salkok.bluetoothmod.bluetooth;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public interface BluetoothConnection {
    boolean isConnected();
    InputStream getInputStream() throws IOException;
    OutputStream getOutputStream() throws IOException;
    void close();
}
