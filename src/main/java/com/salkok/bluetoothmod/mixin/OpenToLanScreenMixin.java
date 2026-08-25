package com.salkok.bluetoothmod.mixin;

import com.salkok.bluetoothmod.host.BluetoothHost;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpenToLanScreen.class)
public class OpenToLanScreenMixin extends Screen {
    private boolean bluetoothEnabled = false;
    private ButtonWidget btnBluetooth;

    protected OpenToLanScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addBluetoothButton(CallbackInfo ci) {
        this.btnBluetooth = this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Bluetooth'a Ac: KAPALI"), 
            button -> {
                this.bluetoothEnabled = !this.bluetoothEnabled;
                button.setMessage(Text.literal("Bluetooth'a Ac: " + (this.bluetoothEnabled ? "ACIK" : "KAPALI")));
                if (this.bluetoothEnabled) {
                    BluetoothHost.getInstance().startHostServer();
                }
            }
        ).dimensions(this.width / 2 - 155, this.height / 4 + 120, 150, 20).build());
    }
}
