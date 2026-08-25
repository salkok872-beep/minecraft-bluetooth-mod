package com.salkok.bluetoothmod.android;

import com.salkok.bluetoothmod.BluetoothMod;
import com.salkok.bluetoothmod.bluetooth.BluetoothBridge;
import com.salkok.bluetoothmod.bluetooth.BluetoothConnection;
import com.salkok.bluetoothmod.bluetooth.BluetoothDeviceInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AndroidBluetoothBridge extends BluetoothBridge {

    public static final UUID SERVICE_UUID =
            UUID.fromString("7b6f8e50-3a71-4f13-9f4b-0b6c6c2a2024");

    private static final String SERVICE_NAME = "Minecraft Bluetooth";

    private volatile Object bluetoothAdapter;
    private volatile Object serverSocket;
    private volatile boolean serverRunning;

    private Object getApplicationContext() {
        try {
            Class<?> activityThread =
                    Class.forName("android.app.ActivityThread");

            Method currentApplication =
                    activityThread.getMethod("currentApplication");

            return currentApplication.invoke(null);
        } catch (Throwable e) {
            BluetoothMod.LOGGER.error(
                    "Android Application Context alınamadı.",
                    e
            );
            return null;
        }
    }

    private Object getAdapter() {
        if (bluetoothAdapter != null) {
            return bluetoothAdapter;
        }

        synchronized (this) {
            if (bluetoothAdapter != null) {
                return bluetoothAdapter;
            }

            try {
                Object context = getApplicationContext();

                if (context == null) {
                    return null;
                }

                Class<?> contextClass =
                        Class.forName("android.content.Context");

                Object bluetoothService =
                        contextClass
                                .getField("BLUETOOTH_SERVICE")
                                .get(null);

                Method getSystemService =
                        contextClass.getMethod(
                                "getSystemService",
                                String.class
                        );

                Object manager =
                        getSystemService.invoke(
                                context,
                                bluetoothService
                        );

                if (manager != null) {
                    Method getAdapter =
                            manager.getClass().getMethod("getAdapter");

                    bluetoothAdapter =
                            getAdapter.invoke(manager);
                }

                if (bluetoothAdapter == null) {
                    Class<?> adapterClass =
                            Class.forName(
                                    "android.bluetooth.BluetoothAdapter"
                            );

                    Method getDefaultAdapter =
                            adapterClass.getMethod(
                                    "getDefaultAdapter"
                            );

                    bluetoothAdapter =
                            getDefaultAdapter.invoke(null);
                }

                return bluetoothAdapter;

            } catch (Throwable e) {
                BluetoothMod.LOGGER.error(
                        "BluetoothAdapter alınamadı.",
                        e
                );
                return null;
            }
        }
    }

    private boolean isEnabled(Object adapter) {
        try {
            Method method =
                    adapter.getClass().getMethod("isEnabled");

            Object result = method.invoke(adapter);

            return result instanceof Boolean
                    && (Boolean) result;

        } catch (Throwable e) {
            return false;
        }
    }

    @Override
    public void startServer(int minecraftPort) {

        if (minecraftPort <= 0) {
            BluetoothMod.LOGGER.error(
                    "Geçersiz Minecraft LAN portu: {}",
                    minecraftPort
            );
            return;
        }

        Object adapter = getAdapter();

        if (adapter == null) {
            BluetoothMod.LOGGER.error(
                    "Android BluetoothAdapter bulunamadı."
            );
            return;
        }

        if (!isEnabled(adapter)) {
            BluetoothMod.LOGGER.error(
                    "Bluetooth kapalı. Önce Android Bluetooth'u aç."
            );
            return;
        }

        stopServer();

        serverRunning = true;

        final int port = minecraftPort;

        new Thread(() -> runBluetoothServer(adapter, port),
                "Minecraft-Bluetooth-Server").start();

        requestDiscoverable();

        BluetoothMod.LOGGER.info(
                "Bluetooth Minecraft sunucusu başlatıldı. Minecraft portu: {}",
                port
        );
    }

    private void runBluetoothServer(
            Object adapter,
            int minecraftPort
    ) {

        try {
            Class<?> uuidClass =
                    Class.forName("java.util.UUID");

            Method listenMethod =
                    adapter.getClass().getMethod(
                            "listenUsingRfcommWithServiceRecord",
                            String.class,
                            uuidClass
                    );

            serverSocket =
                    listenMethod.invoke(
                            adapter,
                            SERVICE_NAME,
                            SERVICE_UUID
                    );

            Method acceptMethod =
                    serverSocket.getClass()
                            .getMethod("accept");

            while (serverRunning) {

                Object bluetoothSocket =
                        acceptMethod.invoke(serverSocket);

                if (bluetoothSocket == null) {
                    continue;
                }

                BluetoothMod.LOGGER.info(
                        "Bluetooth oyuncusu bağlandı."
                );

                handleIncomingConnection(
                        bluetoothSocket,
                        minecraftPort
                );
            }

        } catch (Throwable e) {

            if (serverRunning) {
                BluetoothMod.LOGGER.error(
                        "Bluetooth sunucu hatası.",
                        e
                );
            }
        }
    }

    private void handleIncomingConnection(
            Object bluetoothSocket,
            int minecraftPort
    ) {

        new Thread(() -> {

            try {

                InputStream bluetoothIn =
                        (InputStream)
                                bluetoothSocket
                                        .getClass()
                                        .getMethod("getInputStream")
                                        .invoke(bluetoothSocket);

                OutputStream bluetoothOut =
                        (OutputStream)
                                bluetoothSocket
                                        .getClass()
                                        .getMethod("getOutputStream")
                                        .invoke(bluetoothSocket);

                java.net.Socket minecraftSocket =
                        new java.net.Socket(
                                "127.0.0.1",
                                minecraftPort
                        );

                InputStream minecraftIn =
                        minecraftSocket.getInputStream();

                OutputStream minecraftOut =
                        minecraftSocket.getOutputStream();

                pipe(
                        bluetoothIn,
                        minecraftOut,
                        "BT -> Minecraft"
                );

                pipe(
                        minecraftIn,
                        bluetoothOut,
                        "Minecraft -> BT"
                );

            } catch (Throwable e) {

                BluetoothMod.LOGGER.error(
                        "Bluetooth oyuncu tüneli kapandı.",
                        e
                );
            }

        }, "Minecraft-Bluetooth-Player").start();
    }

    private void pipe(
            InputStream input,
            OutputStream output,
            String name
    ) {

        new Thread(() -> {

            byte[] buffer = new byte[16384];

            try {

                int length;

                while ((length = input.read(buffer)) != -1) {

                    output.write(
                            buffer,
                            0,
                            length
                    );

                    output.flush();
                }

            } catch (IOException ignored) {

            } catch (Throwable e) {

                BluetoothMod.LOGGER.error(
                        "Bluetooth pipe hatası: {}",
                        name,
                        e
                );
            }

        }, "Minecraft-Bluetooth-Pipe-" + name).start();
    }

    @Override
    public BluetoothConnection connectToDevice(
            String address
    ) {

        Object adapter = getAdapter();

        if (adapter == null) {
            return null;
        }

        try {

            try {
                Method cancelDiscovery =
                        adapter.getClass()
                                .getMethod("cancelDiscovery");

                cancelDiscovery.invoke(adapter);

            } catch (Throwable ignored) {
            }

            Method getRemoteDevice =
                    adapter.getClass()
                            .getMethod(
                                    "getRemoteDevice",
                                    String.class
                            );

            Object device =
                    getRemoteDevice.invoke(
                            adapter,
                            address
                    );

            Method createSocket =
                    device.getClass()
                            .getMethod(
                                    "createRfcommSocketToServiceRecord",
                                    UUID.class
                            );

            Object socket =
                    createSocket.invoke(
                            device,
                            SERVICE_UUID
                    );

            Method connect =
                    socket.getClass()
                            .getMethod("connect");

            connect.invoke(socket);

            BluetoothMod.LOGGER.info(
                    "Bluetooth bağlantısı kuruldu: {}",
                    address
            );

            return new ReflectiveBluetoothConnection(socket);

        } catch (Throwable e) {

            BluetoothMod.LOGGER.error(
                    "Bluetooth bağlantısı kurulamadı: {}",
                    address,
                    e
            );

            return null;
        }
    }

    @Override
    public List<BluetoothDeviceInfo> getPairedDevices() {

        Object adapter = getAdapter();

        if (adapter == null) {
            return Collections.emptyList();
        }

        try {

            Method getBondedDevices =
                    adapter.getClass()
                            .getMethod("getBondedDevices");

            Object result =
                    getBondedDevices.invoke(adapter);

            if (!(result instanceof Set)) {
                return Collections.emptyList();
            }

            List<BluetoothDeviceInfo> devices =
                    new ArrayList<>();

            for (Object device : (Set<?>) result) {

                Method getName =
                        device.getClass()
                                .getMethod("getName");

                Method getAddress =
                        device.getClass()
                                .getMethod("getAddress");

                String name =
                        String.valueOf(
                                getName.invoke(device)
                        );

                String address =
                        String.valueOf(
                                getAddress.invoke(device)
                        );

                devices.add(
                        new BluetoothDeviceInfo(
                                name,
                                address
                        )
                );
            }

            return devices;

        } catch (Throwable e) {

            BluetoothMod.LOGGER.error(
                    "Bluetooth cihazları alınamadı.",
                    e
            );

            return Collections.emptyList();
        }
    }

    private void requestDiscoverable() {

        try {

            Object context =
                    getApplicationContext();

            if (context == null) {
                return;
            }

            Class<?> intentClass =
                    Class.forName(
                            "android.content.Intent"
                    );

            Object intent =
                    intentClass
                            .getConstructor(String.class)
                            .newInstance(
                                    "android.bluetooth.adapter.action.REQUEST_DISCOVERABLE"
                            );

            Method putExtra =
                    intentClass.getMethod(
                            "putExtra",
                            String.class,
                            int.class
                    );

            putExtra.invoke(
                    intent,
                    "android.bluetooth.adapter.extra.DISCOVERABLE_DURATION",
                    300
            );

            Class<?> contextClass =
                    Class.forName(
                            "android.content.Context"
                    );

            int newTaskFlag =
                    contextClass
                            .getField(
                                    "FLAG_ACTIVITY_NEW_TASK"
                            )
                            .getInt(null);

            Method addFlags =
                    intentClass.getMethod(
                            "addFlags",
                            int.class
                    );

            addFlags.invoke(
                    intent,
                    newTaskFlag
            );

            Method startActivity =
                    contextClass.getMethod(
                            "startActivity",
                            intentClass
                    );

            startActivity.invoke(
                    context,
                    intent
            );

        } catch (Throwable e) {

            BluetoothMod.LOGGER.warn(
                    "Bluetooth keşfedilebilirlik ekranı açılamadı.",
                    e
            );
        }
    }

    @Override
    public void stopServer() {

        serverRunning = false;

        Object socket = serverSocket;
        serverSocket = null;

        if (socket != null) {

            try {

                socket.getClass()
                        .getMethod("close")
                        .invoke(socket);

            } catch (Throwable ignored) {
            }
        }

        BluetoothMod.LOGGER.info(
                "Bluetooth sunucusu durduruldu."
        );
    }

    private static class ReflectiveBluetoothConnection
            implements BluetoothConnection {

        private final Object socket;

        private ReflectiveBluetoothConnection(
                Object socket
        ) {
            this.socket = socket;
        }

        @Override
        public boolean isConnected() {

            try {

                Method method =
                        socket.getClass()
                                .getMethod("isConnected");

                Object result =
                        method.invoke(socket);

                return result instanceof Boolean
                        && (Boolean) result;

            } catch (Throwable e) {
                return false;
            }
        }

        @Override
        public InputStream getInputStream()
                throws IOException {

            try {

                return (InputStream)
                        socket.getClass()
                                .getMethod(
                                        "getInputStream"
                                )
                                .invoke(socket);

            } catch (Throwable e) {

                throw new IOException(
                        "Bluetooth input stream alınamadı.",
                        e
                );
            }
        }

        @Override
        public OutputStream getOutputStream()
                throws IOException {

            try {

                return (OutputStream)
                        socket.getClass()
                                .getMethod(
                                        "getOutputStream"
                                )
                                .invoke(socket);

            } catch (Throwable e) {

                throw new IOException(
                        "Bluetooth output stream alınamadı.",
                        e
                );
            }
        }

        @Override
        public void close() {

            try {

                socket.getClass()
                        .getMethod("close")
                        .invoke(socket);

            } catch (Throwable ignored) {
            }
        }
    }
    }
