package com.example.btmod.mixin;

import com.example.btmod.BluetoothMod;
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
    private static boolean isBluetoothEnabled = false;
    private static boolean isLocked = false;
    private ButtonWidget btButton;

    protected OpenToLanScreenMixin(Text title) { super(title); }

    @Inject(method = "init", at = @At("TAIL"))
    private void addBluetoothButton(CallbackInfo ci) {
        this.btButton = ButtonWidget.builder(
            Text.literal("Bluetooth: " + (isBluetoothEnabled ? "AÇIK" : "KAPALI")),
            button -> {
                if (!isLocked) {
                    isBluetoothEnabled = !isBluetoothEnabled;
                    button.setMessage(Text.literal("Bluetooth: " + (isBluetoothEnabled ? "AÇIK" : "KAPALI")));
                }
            }
        ).dimensions(this.width / 2 - 155, this.height / 4 + 120, 150, 20).build();

        if (isLocked) this.btButton.active = false;
        this.addDrawableChild(this.btButton);
    }

    @Inject(method = "openToLan", at = @At("HEAD"))
    private void onOpenToLan(CallbackInfo ci) {
        if (isBluetoothEnabled) {
            isLocked = true;
            BluetoothMod.getInstance().startBluetoothHost();
        }
    }
}