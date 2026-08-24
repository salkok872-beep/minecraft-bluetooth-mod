package com.example.btmod.mixin;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
    protected MultiplayerScreenMixin(Text title) { super(title); }

    @Inject(method = "init", at = @At("TAIL"))
    private void scanBluetoothServers(CallbackInfo ci) {
        // Arka planda eşleşen cihazları tarayıp UI'a ekleme mantığı
    }
}