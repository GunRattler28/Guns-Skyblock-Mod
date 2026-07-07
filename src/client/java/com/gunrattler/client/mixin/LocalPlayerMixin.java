package com.gunrattler.client.mixin;

import com.gunrattler.client.HypixelSkyblockModClient;
import com.gunrattler.client.util.TabParser;

import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void blockCoreDrop(boolean dropStack, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (!TabParser.hasData()) return;
            LocalPlayer player = (LocalPlayer) (Object) this;
            Object inventory = player.getInventory();
            
            if (inventory != null) {
                int selectedSlot = 0;
                
                try {
                    Method getSelected = inventory.getClass().getMethod("getSelectedSlot");
                    selectedSlot = (int) getSelected.invoke(inventory);
                } catch (Exception e) {
                    try {
                        Field selectedField = inventory.getClass().getDeclaredField("selected");
                        selectedField.setAccessible(true);
                        selectedSlot = selectedField.getInt(inventory);
                    } catch (Exception e2) {
                        for (Field f : inventory.getClass().getDeclaredFields()) {
                            if (f.getType() == int.class) {
                                f.setAccessible(true);
                                int val = f.getInt(inventory);
                                if (val >= 0 && val <= 8) {
                                    selectedSlot = val;
                                    break;
                                }
                            }
                        }
                    }
                }
                String slotKey = "Inventory:" + selectedSlot;
                
                if (HypixelSkyblockModClient.lockedSlots.contains(slotKey)) {
                    cir.setReturnValue(false);
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.player != null) {
                        mc.player.sendSystemMessage(Component.literal("§cSlot is locked!"));
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}