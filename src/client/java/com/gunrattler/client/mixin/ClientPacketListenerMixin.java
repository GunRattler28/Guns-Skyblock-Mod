package com.gunrattler.client.mixin;

import com.gunrattler.client.gui.FirmamentStorageScreen;
import com.gunrattler.client.util.StorageCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Unique
    private static int lastRequestedPage = 1; 

    // Inside your ClientPacketListenerMixin.java
    @Inject(method = "handleContainerContent", at = @At("HEAD"))
    private void onContainerContent(ClientboundContainerSetContentPacket packet, CallbackInfo ci) {
        ContainerSetContentPacketAccessor accessor = (ContainerSetContentPacketAccessor) (Object) packet;
        int id = accessor.getContainerId();
        List<ItemStack> items = accessor.getItems();
        
        StorageCache.setContainerItems("enderchest_" + com.gunrattler.client.util.FirmamentState.lastRequestedPage, items.toArray(new ItemStack[0]));
    }

    @Unique
    private static boolean isOpeningCustomScreen = false;

    @Inject(method = "handleOpenScreen", at = @At("HEAD"))
    private void onOpenScreen(ClientboundOpenScreenPacket packet, CallbackInfo ci) {
        if (isOpeningCustomScreen) return;

        String title = packet.getTitle().getString();

        if (title.toLowerCase().contains("ender chest")) {
            System.out.println("[SkyblockMod] Detected Ender Chest open: " + title);
            isOpeningCustomScreen = true;
            Minecraft.getInstance().execute(() -> {
                try {
                    Minecraft.getInstance().setScreenAndShow(new FirmamentStorageScreen());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isOpeningCustomScreen = false;
                }
            });
        }
    }
}