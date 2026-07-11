package com.gunrattler.client.mixin;

import com.gunrattler.client.gui.FirmamentStorageScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ScreenInjectionMixin {

    @Inject(method = "setScreenAndShow", at = @At("HEAD"), cancellable = true)
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        // 1. Log the full class path of EVERY screen that tries to open
        System.out.println("DEBUG: Screen opening: " + screen.getClass().getName());

        // 2. We will check if it's a container, but we won't stop it from printing even if it's not.
        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            System.out.println("DEBUG: --- It is a container! Title: [" + containerScreen.getTitle().getString() + "] ---");
        }
    }
}