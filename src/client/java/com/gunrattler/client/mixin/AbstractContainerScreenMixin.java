package com.gunrattler.client.mixin;

import com.gunrattler.client.HypixelSkyblockModClient;
import com.gunrattler.client.util.TabParser;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Shadow protected Slot hoveredSlot;
    
    @Unique
    private String getUniqueSlotKey(Slot slot) {
        if (slot == null || slot.container == null) return "";
        return slot.container.getClass().getSimpleName() + ":" + slot.getContainerSlot();
    }

    @Unique
    private static final Identifier LOCK_TEXTURE = Identifier.fromNamespaceAndPath("hypixel-skyblock-mod", "textures/gui/lockedslot.png");

    @Inject(method = "extractSlot", at = @At("TAIL"))
    private void renderLockOverlay(GuiGraphicsExtractor graphics, Slot slot, int argX, int argY, CallbackInfo ci) {
        if (TabParser.hasData() && slot != null && HypixelSkyblockModClient.lockedSlots.contains(getUniqueSlotKey(slot))) {
            int x = slot.x;
            int y = slot.y;
            graphics.blit(RenderPipelines.GUI_TEXTURED, LOCK_TEXTURE, x, y, 0.0f, 0.0f, 16, 16, 16, 16);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(net.minecraft.client.input.KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (this.hoveredSlot == null) return;
        
        String slotKey = getUniqueSlotKey(this.hoveredSlot);
        boolean isLocked = HypixelSkyblockModClient.lockedSlots.contains(slotKey);

        if (event.key() == GLFW.GLFW_KEY_L) {
            if (!TabParser.hasData()) return;

            if (isLocked) {
                HypixelSkyblockModClient.unlockSlot(slotKey);
            } else {
                HypixelSkyblockModClient.lockSlot(slotKey);
            }
            cir.setReturnValue(true);
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (TabParser.hasData() && isLocked && mc.options.keyDrop.matches(event)) {
            cir.setReturnValue(true);
            mc.player.sendSystemMessage(Component.literal("Slot is locked!"));
            return;
        }
    }
}