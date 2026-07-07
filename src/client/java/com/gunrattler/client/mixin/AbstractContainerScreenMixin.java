package com.gunrattler.client.mixin;

import com.gunrattler.client.HypixelSkyblockModClient;
import com.gunrattler.client.util.TabParser;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.lang.reflect.Field;


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
        // Only render the lock texture if we are actually actively on SkyBlock
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
            // SKYBLOCK CHECK: Ignore the lock/unlock hotkey entirely outside of SkyBlock
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
        // SKYBLOCK CHECK: Only enforce the lock block if on SkyBlock
        if (TabParser.hasData() && isLocked && mc.options.keyDrop.matches(event)) {
            cir.setReturnValue(true);
            mc.player.sendSystemMessage(Component.literal("Slot is locked!"));
            return;
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClick(MouseButtonEvent event, boolean handled, CallbackInfoReturnable<Boolean> cir) {
        // SKYBLOCK CHECK: Only intercept mouse clicks on SkyBlock
        if (TabParser.hasData() && this.hoveredSlot != null) {
            String slotKey = getUniqueSlotKey(this.hoveredSlot);
            if (HypixelSkyblockModClient.lockedSlots.contains(slotKey)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "handleSlotStateChanged", at = @At("HEAD"), cancellable = true, remap = false)
    private void onSlotAction(int slotId, int button, boolean actionTypeFlag, CallbackInfo ci) {
        try {
            if (!TabParser.hasData()) return;

            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
            if (screen.getMenu() != null) {
                Slot slot = screen.getMenu().getSlot(slotId);
                if (slot != null && HypixelSkyblockModClient.lockedSlots.contains(getUniqueSlotKey(slot))) {
                    ci.cancel(); 
                }
            }
        } catch (Exception ignored) {}
    }

    @Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
    private void blockDropOnClose(CallbackInfo ci) {
        try {
            if (!TabParser.hasData()) return;

            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                AbstractContainerMenu menu = mc.player.containerMenu;
                if (menu != null && !menu.getCarried().isEmpty()) {
                    Slot targetSlot = this.hoveredSlot;
                    if (targetSlot == null) {
                        Field field = AbstractContainerScreen.class.getDeclaredField("hoveredSlot");
                        field.setAccessible(true);
                        targetSlot = (Slot) field.get(this);
                    }
                    if (targetSlot != null && HypixelSkyblockModClient.lockedSlots.contains(getUniqueSlotKey(targetSlot))) {
                        ci.cancel();
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}