package com.salkok.bluetoothmod.client;

import com.salkok.bluetoothmod.android.AndroidBluetoothBridge;
import com.salkok.bluetoothmod.bluetooth.BluetoothConnection;
import com.salkok.bluetoothmod.bluetooth.BluetoothDeviceInfo;
import com.salkok.bluetoothmod.bluetooth.BluetoothTunnel;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

import java.lang.reflect.Method;
import java.util.List;

public class BluetoothDeviceSelectionScreen extends Screen {
    private final Screen parent;
    private final AndroidBluetoothBridge bridge;

    public BluetoothDeviceSelectionScreen(Screen parent) {
        super(Text.literal("Bluetooth Cihazlari"));
        this.parent = parent;
        this.bridge = new AndroidBluetoothBridge();
    }

    @Override
    protected void init() {
        int y = 40;
        List<BluetoothDeviceInfo> devices = bridge.getPairedDevices();

        if (devices.isEmpty()) {
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Eslesmis Cihaz Bulunamadi"),
                b -> {}
            ).dimensions(this.width / 2 - 100, y, 200, 20).build());
        } else {
            for (BluetoothDeviceInfo dev : devices) {
                this.addDrawableChild(ButtonWidget.builder(
                    Text.literal(dev.getName() + " (" + dev.getAddress() + ")"),
                    button -> connectToBluetoothDevice(dev.getAddress())
                ).dimensions(this.width / 2 - 150, y, 300, 20).build());
                y += 24;
            }
        }

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Geri"),
            button -> this.client.setScreen(this.parent)
        ).dimensions(this.width / 2 - 100, this.height - 30, 200, 20).build());
    }

    private void connectToBluetoothDevice(String address) {
        new Thread(() -> {
            BluetoothConnection conn = bridge.connectToDevice(address);
            if (conn != null && conn.isConnected()) {
                BluetoothTunnel tunnel = new BluetoothTunnel();
                int localPort = tunnel.startLocalProxy(conn);
                if (localPort != -1) {
                    this.client.execute(() -> {
                        try {
                            ServerAddress serverAddress = new ServerAddress("127.0.0.1", localPort);
                            ServerInfo serverInfo = new ServerInfo("Bluetooth World", "127.0.0.1:" + localPort, false);

                            Class<?> connectScreenClass = Class.forName("net.minecraft.client.gui.screen.multiplayer.ConnectScreen");
                            Method connectMethod = null;
                            for (Method m : connectScreenClass.getDeclaredMethods()) {
                                if (m.getName().equals("connect") || m.getName().equals("method_19800")) {
                                    connectMethod = m;
                                    break;
                                }
                            }

                            if (connectMethod != null) {
                                connectMethod.setAccessible(true);
                                if (connectMethod.getParameterCount() == 5) {
                                    connectMethod.invoke(null, this.parent, this.client, serverAddress, serverInfo, false);
                                } else if (connectMethod.getParameterCount() == 6) {
                                    connectMethod.invoke(null, this.parent, this.client, serverAddress, serverInfo, false, null);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
