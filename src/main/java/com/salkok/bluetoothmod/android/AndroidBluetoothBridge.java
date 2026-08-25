package com.salkok.bluetoothmod.android;

import com.salkok.bluetoothmod.BluetoothMod;
import com.salkok.bluetoothmod.bluetooth.BluetoothBridge;
import com.salkok.bluetoothmod.bluetooth.BluetoothConnection;
import com.salkok.bluetoothmod.bluetooth.BluetoothDeviceInfo;
import com.salkok.bluetoothmod.bluetooth.BluetoothTunnel;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public class AndroidBluetoothBridge extends BluetoothBridge {
    public static final UUID MC_BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Object bluetoothAdapter;

    public AndroidBluetoothBridge() {
        try {
            Class<?> adapterClass = Class.forName("android.bluetooth.BluetoothAdapter");
            Method getDefaultAdapter = adapterClass.getMethod("getDefaultAdapter");
            this.bluetoothAdapter = getDefaultAdapter.invoke(null);
        } catch (Exception e) {
            BluetoothMod.LOGGER.warn("Android Bluetooth Adapter yuklenemedi: " + e.getMessage());
        }
    }

    @Override
    public void startServer() {
        if (bluetoothAdapter == null) return;
        new Thread(() -> {
            try {
                Method listenMethod = bluetoothAdapter.getClass().getMethod("listenUsingRfcommWithServiceRecord", String.class, UUID.class);
                Object serverSocket = listenMethod.invoke(bluetoothAdapter, "MinecraftBT", MC_BT_UUID);
                BluetoothMod.LOGGER.info("Android Bluetooth Dinleyici Baslatildi.");

                Method acceptMethod = serverSocket.getClass().getMethod("accept");
                while (true) {
                    Object socket = acceptMethod.invoke(serverSocket);
                    if (socket != null) {
                        BluetoothMod.LOGGER.info("Android Cihaz Baglandi!");
                        BluetoothConnection conn = new ReflectionAndroidConnection(socket);
                        BluetoothTunnel tunnel = new BluetoothTunnel();
                        int localPort = tunnel.startLocalProxy(conn);
                        BluetoothMod.LOGGER.info("Android Bluetooth Tuneli Port " + localPort + " uzerinde aktif.");
                    }
                }
            } catch (Exception e) {
                BluetoothMod.LOGGER.error("Android Bluetooth Sunucu Hatasi: ", e);
            }
        }).start();
    }

    @Override
    public BluetoothConnection connectToDevice(String address) {
        if (bluetoothAdapter == null) return null;
        try {
            Method getRemoteDevice = bluetoothAdapter.getClass().getMethod("getRemoteDevice", String.class);
            Object device = getRemoteDevice.invoke(bluetoothAdapter, address);

            Method createRfcomm = device.getClass().getMethod("createRfcommSocketToServiceRecord", UUID.class);
            Object socket = createRfcomm.invoke(device, MC_BT_UUID);

            Method cancelDiscovery = bluetoothAdapter.getClass().getMethod("cancelDiscovery");
            cancelDiscovery.invoke(bluetoothAdapter);

            Method connectMethod = socket.getClass().getMethod("connect");
            connectMethod.invoke(socket);

            return new ReflectionAndroidConnection(socket);
        } catch (Exception e) {
            BluetoothMod.LOGGER.error("Android Bluetooth Baglanti Hatasi (" + address + "): ", e);
            return null;
        }
    }

    @Override
    public List<BluetoothDeviceInfo> getPairedDevices() {
        List<BluetoothDeviceInfo> list = new ArrayList<>();
        if (bluetoothAdapter == null) return list;
        try {
            Method getBondedDevices = bluetoothAdapter.getClass().getMethod("getBondedDevices");
            Set<?> bondedDevices = (Set<?>) getBondedDevices.invoke(bluetoothAdapter);
            if (bondedDevices != null) {
                for (Object dev : bondedDevices) {
                    Method getName = dev.getClass().getMethod("getName");
                    Method getAddress = dev.getClass().getMethod("getAddress");
                    String name = (String) getName.invoke(dev);
                    String address = (String) getAddress.invoke(dev);
                    list.add(new BluetoothDeviceInfo(name, address));
                }
            }
        } catch (Exception e) {
            BluetoothMod.LOGGER.error("Eslesmis cihazlar alinamadi: ", e);
        }
        return list;
    }

    private static class ReflectionAndroidConnection implements BluetoothConnection {
        private final Object socket;

        public ReflectionAndroidConnection(Object socket) {
            this.socket = socket;
        }

        @Override
        public boolean isConnected() {
            try {
                Method isConnected = socket.getClass().getMethod("isConnected");
                return (boolean) isConnected.invoke(socket);
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public InputStream getInputStream() throws IOException {
            try {
                Method getInputStream = socket.getClass().getMethod("getInputStream");
                return (InputStream) getInputStream.invoke(socket);
            } catch (Exception e) {
                if (e instanceof IOException) {
                    throw (IOException) e;
                }
                throw new IOException(e);
            }
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            try {
                Method getOutputStream = socket.getClass().getMethod("getOutputStream");
                return (OutputStream) getOutputStream.invoke(socket);
            } catch (Exception e) {
                if (e instanceof IOException) {
                    throw (IOException) e;
                }
                throw new IOException(e);
            }
        }

        @Override
        public void close() {
            try {
                Method close = socket.getClass().getMethod("close");
                close.invoke(socket);
            } catch (Exception ignored) {}
        }
    }
}
