package com.gunrattler.client.mixin;

import com.gunrattler.client.HypixelSkyblockModClient;
import com.gunrattler.client.util.TabParser;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class SlotLockMixin {

    @Unique
    private String getUniqueSlotKey(Slot slot) {
        if (slot == null || slot.container == null) return "";
        return slot.container.getClass().getSimpleName() + ":" + slot.getContainerSlot();
    }

    @Inject(method = "handleContainerInput", at = @At("HEAD"), cancellable = true)
    private void onHandleContainerInput(int containerId, int slotNum, int buttonNum, ContainerInput containerInput, Player player, CallbackInfo ci) {
        if (!TabParser.hasData()) return;

        AbstractContainerMenu menu = player.containerMenu;
        
        if (containerId != menu.containerId) {
            return; 
        }

        if (slotNum < 0 || slotNum >= menu.slots.size()) {
            return;
        }

        Slot clickedSlot = menu.slots.get(slotNum);
        
        String slotKey = getUniqueSlotKey(clickedSlot);
        if (HypixelSkyblockModClient.lockedSlots.contains(slotKey)) {
            ci.cancel();
        }
    }
}