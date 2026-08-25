package com.salkok.bluetoothmod.client;

import com.salkok.bluetoothmod.android.AndroidBluetoothBridge;
import com.salkok.bluetoothmod.bluetooth.BluetoothConnection;
import com.salkok.bluetoothmod.bluetooth.BluetoothDeviceInfo;
import com.salkok.bluetoothmod.bluetooth.BluetoothTunnel;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

import java.util.List;

public class BluetoothDeviceSelectionScreen
        extends Screen {

    private final Screen parent;

    private final AndroidBluetoothBridge bridge;

    public BluetoothDeviceSelectionScreen(
            Screen parent
    ) {

        super(
                Text.literal(
                        "Bluetooth Dünyaları"
                )
        );

        this.parent = parent;
        this.bridge =
                new AndroidBluetoothBridge();
    }

    @Override
    protected void init() {

        int y = 45;

        List<BluetoothDeviceInfo> devices =
                bridge.getPairedDevices();

        if (devices.isEmpty()) {

            this.addDrawableChild(
                    ButtonWidget.builder(
                            Text.literal(
                                    "Eşleşmiş cihaz bulunamadı"
                            ),
                            button -> {
                            }
                    ).dimensions(
                            this.width / 2 - 150,
                            y,
                            300,
                            20
                    ).build()
            );

            y += 28;

        } else {

            for (BluetoothDeviceInfo device :
                    devices) {

                this.addDrawableChild(
                        ButtonWidget.builder(
                                Text.literal(
                                        device.getName()
                                                +
                                        "  ["
                                                +
                                        device.getAddress()
                                                +
                                        "]"
                                ),
                                button ->
                                        connectToBluetoothDevice(
                                                device
                                        )
                        ).dimensions(
                                this.width / 2 - 150,
                                y,
                                300,
                                20
                        ).build()
                );

                y += 24;
            }
        }

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Geri"),
                        button ->
                                this.client.setScreen(
                                        this.parent
                                )
                ).dimensions(
                        this.width / 2 - 100,
                        this.height - 30,
                        200,
                        20
                ).build()
        );
    }

    private void connectToBluetoothDevice(
            BluetoothDeviceInfo device
    ) {

        new Thread(() -> {

            BluetoothConnection connection =
                    bridge.connectToDevice(
                            device.getAddress()
                    );

            if (connection == null ||
                    !connection.isConnected()) {

                return;
            }

            BluetoothTunnel tunnel =
                    new BluetoothTunnel();

            int localPort =
                    tunnel.startLocalProxy(
                            connection
                    );

            if (localPort == -1) {
                connection.close();
                return;
            }

            this.client.execute(() -> {

                ServerAddress address =
                        new ServerAddress(
                                "127.0.0.1",
                                localPort
                        );

                ServerInfo info =
                        new ServerInfo(
                                "Bluetooth World",
                                "127.0.0.1:" + localPort,
                                false
                        );

                ConnectScreen.connect(
                        this,
                        this.client,
                        address,
                        info,
                        false
                );
            });

        }, "Minecraft-Bluetooth-Connect").start();
    }

    @Override
    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
    ) {

        this.renderBackground(context);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                this.width / 2,
                15,
                0xFFFFFFFF
        );

        super.render(
                context,
                mouseX,
                mouseY,
                delta
        );
    }
                                }
