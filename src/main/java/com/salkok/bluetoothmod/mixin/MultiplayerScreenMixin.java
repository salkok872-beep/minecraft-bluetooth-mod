package com.salkok.bluetoothmod.mixin;

import com.salkok.bluetoothmod.client.BluetoothClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addBluetoothJoinButton(CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Bluetooth ile Katıl"), 
            button -> BluetoothClient.openDeviceSelectionScreen(this.client, this)
        ).dimensions(this.width / 2 - 100, this.height - 26, 200, 20).build());
    }
}
