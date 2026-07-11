package com.gunrattler.client.mixin;

import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(ClientboundContainerSetContentPacket.class)
public interface ContainerSetContentPacketAccessor {
    @Accessor("containerId")
    int getContainerId();

    @Accessor("items")
    List<ItemStack> getItems();
}