package com.gunrattler.client.mixin;

import com.gunrattler.client.util.StorageCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class EnderChestInterceptMixin {

    @Unique private final int SLOT_SIZE = 18;
    @Unique private final int PANEL_PADDING = 7;
    @Unique private final int PANEL_WIDTH = (9 * SLOT_SIZE) + (PANEL_PADDING * 2);
    @Unique private final int PANEL_HEIGHT = (5 * SLOT_SIZE) + 18 + PANEL_PADDING;

    @Unique
    private Component getScreenTitle() {
        return ((net.minecraft.client.gui.screens.Screen) (Object) this).getTitle();
    }

    @Inject(method = {"findSlot", "getSlotAt"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void killVanillaGhostSlots(double mouseX, double mouseY, CallbackInfoReturnable<Slot> cir) {
        Component titleComponent = this.getScreenTitle();
        if (titleComponent != null) {
            String title = titleComponent.getString().toLowerCase();
            if (title.contains("ender chest") || title.contains("enderchest") || title.contains("ednerchest")) {
                cir.setReturnValue(null);
            }
        }
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void drawFirmamentStorageOverlay(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        try {
            Component currentTitle = this.getScreenTitle();
            if (currentTitle == null) return;

            String titleText = currentTitle.getString().toLowerCase();
            if (!titleText.contains("ender chest") && !titleText.contains("enderchest") && !titleText.contains("ednerchest")) return;

            Minecraft mc = Minecraft.getInstance();
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();
            Font font = mc.font;

            graphics.fill(0, 0, screenWidth, screenHeight, 0xD0101010);

            int totalCols = 3;
            int startX = (screenWidth - (totalCols * PANEL_WIDTH)) / 2;
            int startY = (screenHeight - PANEL_HEIGHT - 100) / 2;

            ItemStack hoveredCustomStack = null;

            for (int pageIndex = 0; pageIndex < 3; pageIndex++) {
                int panelX = startX + (pageIndex * PANEL_WIDTH);
                int panelY = startY;

                graphics.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xFFC6C6C6);
                graphics.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + 2, 0xFFFFFFFF);
                graphics.fill(panelX, panelY, panelX + 2, panelY + PANEL_HEIGHT, 0xFFFFFFFF);
                graphics.fill(panelX + PANEL_WIDTH - 2, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xFF555555);
                graphics.fill(panelX, panelY + PANEL_HEIGHT - 2, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xFF555555);

                String pageName = "Ender Chest Page #" + (pageIndex + 1);
                graphics.text(font, Component.literal("§8" + pageName).getVisualOrderText(), panelX + PANEL_PADDING, panelY + 6, 0xFF404040);

                String cacheKey = "enderchest_" + (pageIndex + 1);
                ItemStack[] items = StorageCache.getContainerItems(cacheKey);

                for (int i = 0; i < 45; i++) {
                    int slotCol = i % 9;
                    int slotRow = i / 9;
                    int slotX = panelX + PANEL_PADDING + (slotCol * SLOT_SIZE);
                    int slotY = panelY + 18 + (slotRow * SLOT_SIZE);

                    graphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF8B8B8B);
                    graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0xFF373737);
                    graphics.fill(slotX + 1, slotY + 1, slotX + 16, slotY + 16, 0xFF8B8B8B);

                    ItemStack stack = (items != null && i < items.length && items[i] != null) ? items[i] : ItemStack.EMPTY;
                    if (!stack.isEmpty()) {
                        graphics.item(stack, slotX + 1, slotY + 1);
                        if (mouseX >= slotX && mouseX <= slotX + 18 && mouseY >= slotY && mouseY <= slotY + 18) {
                            graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0x40FFFFFF);
                            hoveredCustomStack = stack;
                        }
                    }
                }
            }

            int invPanelX = (screenWidth - PANEL_WIDTH) / 2;
            int invPanelY = startY + PANEL_HEIGHT + 15;
            int invPanelHeight = (4 * SLOT_SIZE) + 12;

            graphics.fill(invPanelX, invPanelY, invPanelX + PANEL_WIDTH, invPanelY + invPanelHeight, 0xFFC6C6C6);
            graphics.fill(invPanelX, invPanelY, invPanelX + PANEL_WIDTH, invPanelY + 2, 0xFFFFFFFF);
            graphics.fill(invPanelX, invPanelY, invPanelX + 2, invPanelY + invPanelHeight, 0xFFFFFFFF);
            graphics.fill(invPanelX + PANEL_WIDTH - 2, invPanelY, invPanelX + PANEL_WIDTH, invPanelY + invPanelHeight, 0xFF555555);
            graphics.fill(invPanelX, invPanelY + invPanelHeight - 2, invPanelX + PANEL_WIDTH, invPanelY + invPanelHeight, 0xFF555555);

            if (mc.player != null) {
                for (int i = 9; i < 36; i++) {
                    int slotCol = (i - 9) % 9;
                    int slotRow = (i - 9) / 9;
                    int slotX = invPanelX + PANEL_PADDING + (slotCol * SLOT_SIZE);
                    int slotY = invPanelY + 6 + (slotRow * SLOT_SIZE);

                    graphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF8B8B8B);
                    graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0xFF373737);
                    graphics.fill(slotX + 1, slotY + 1, slotX + 16, slotY + 16, 0xFF8B8B8B);

                    ItemStack stack = mc.player.getInventory().getItem(i);
                    if (!stack.isEmpty()) {
                        graphics.item(stack, slotX + 1, slotY + 1);
                    }
                }

                for (int i = 0; i < 9; i++) {
                    int slotX = invPanelX + PANEL_PADDING + (i * SLOT_SIZE);
                    int slotY = invPanelY + 10 + (3 * SLOT_SIZE);

                    graphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF8B8B8B);
                    graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0xFF373737);
                    graphics.fill(slotX + 1, slotY + 1, slotX + 16, slotY + 16, 0xFF8B8B8B);

                    ItemStack stack = mc.player.getInventory().getItem(i);
                    if (!stack.isEmpty()) {
                        graphics.item(stack, slotX + 1, slotY + 1);
                    }
                }
            }

            if (hoveredCustomStack != null) {
                String itemName = hoveredCustomStack.getHoverName().getString();
                int textWidth = font.width(itemName);
                int tooltipX = mouseX + 12;
                int tooltipY = mouseY - 4;

                graphics.fill(tooltipX - 4, tooltipY - 4, tooltipX + textWidth + 4, tooltipY + 12, 0xF0101010);
                graphics.text(font, hoveredCustomStack.getHoverName().getVisualOrderText(), tooltipX, tooltipY, 0xFFFFFFFF);
            }

            // --- FLOATING CURSOR DRAW ---
            if (screen.getMenu() != null && !screen.getMenu().getCarried().isEmpty()) {
                graphics.item(screen.getMenu().getCarried(), mouseX - 8, mouseY - 8);
            }

            ci.cancel(); 

        } catch (Exception ignored) {}
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void handleUnifiedClicks(net.minecraft.client.input.MouseButtonEvent event, boolean handled, CallbackInfoReturnable<Boolean> cir) {
        try {
            double mouseX = event.x();
            double mouseY = event.y();
            int button = event.button();

            Component currentTitle = this.getScreenTitle();
            if (currentTitle == null) return;

            String titleText = currentTitle.getString().toLowerCase();
            if (!titleText.contains("ender chest") && !titleText.contains("enderchest") && !titleText.contains("ednerchest")) return;

            Minecraft mc = Minecraft.getInstance();
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;

            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();

            int totalCols = 3;
            int startX = (screenWidth - (totalCols * PANEL_WIDTH)) / 2;
            int startY = (screenHeight - PANEL_HEIGHT - 100) / 2;

            int activePageNum = 1; 
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\d+").matcher(titleText);
            if (m.find()) activePageNum = Integer.parseInt(m.group());

            // 1. Locate Minecraft's internal slotClicked method dynamically
            java.lang.reflect.Method slotClickedMethod = null;
            for (java.lang.reflect.Method method : AbstractContainerScreen.class.getDeclaredMethods()) {
                Class<?>[] params = method.getParameterTypes();
                // Look for slotClicked(Slot slot, int slotId, int button, ClickType clickType)
                if (params.length == 4 && params[1] == int.class && params[2] == int.class && params[3].isEnum()) {
                    slotClickedMethod = method;
                    slotClickedMethod.setAccessible(true);
                    break;
                }
            }

            if (slotClickedMethod == null) return;

            // Extract the standard PICKUP click enum type
            Class<?> clickTypeEnum = slotClickedMethod.getParameterTypes()[3];
            Object clickTypePickup = Enum.valueOf((Class<Enum>) clickTypeEnum, "PICKUP");

            // 2. Check Container Page Navigation / Container Slots
            for (int pageIndex = 0; pageIndex < 3; pageIndex++) {
                int panelX = startX + (pageIndex * PANEL_WIDTH);
                int panelY = startY;

                if (mouseX >= panelX && mouseX <= (panelX + PANEL_WIDTH) && mouseY >= panelY && mouseY <= (panelY + PANEL_HEIGHT)) {
                    int targetPage = pageIndex + 1;

                    if (targetPage == activePageNum) {
                        for (int i = 0; i < 45; i++) {
                            int slotCol = i % 9;
                            int slotRow = i / 9;
                            int slotX = panelX + PANEL_PADDING + (slotCol * SLOT_SIZE);
                            int slotY = panelY + 18 + (slotRow * SLOT_SIZE);

                            if (mouseX >= slotX && mouseX <= slotX + 18 && mouseY >= slotY && mouseY <= slotY + 18) {
                                int actualServerSlotId = i + 9; 
                                Slot serverSlot = screen.getMenu().getSlot(actualServerSlotId);
                                if (serverSlot != null) {
                                    // Direct call to slotClicked bypasses coordinates positioning bugs
                                    slotClickedMethod.invoke(screen, serverSlot, serverSlot.index, button, clickTypePickup);
                                    cir.setReturnValue(true);
                                    return;
                                }
                            }
                        }
                    } else {
                        if (mc.player != null && mc.player.connection != null) {
                            mc.getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0F));
                            mc.player.connection.sendCommand("ec " + targetPage);
                            cir.setReturnValue(true); 
                            return;
                        }
                    }
                }
            }

            // 3. Check Player Inventory Box Clicks
            int invPanelX = (screenWidth - PANEL_WIDTH) / 2;
            int invPanelY = startY + PANEL_HEIGHT + 15;

            // Main Player Inventory Loop
            for (int i = 9; i < 36; i++) {
                int slotCol = (i - 9) % 9;
                int slotRow = (i - 9) / 9;
                int slotX = invPanelX + PANEL_PADDING + (slotCol * SLOT_SIZE);
                int slotY = invPanelY + (slotRow * SLOT_SIZE);

                if (mouseX >= slotX && mouseX <= slotX + 18 && mouseY >= slotY && mouseY <= slotY + 18) {
                    int serverSlotIndex = 54 + (i - 9); 
                    if (serverSlotIndex < screen.getMenu().slots.size()) {
                        Slot serverSlot = screen.getMenu().getSlot(serverSlotIndex);
                        if (serverSlot != null) {
                            slotClickedMethod.invoke(screen, serverSlot, serverSlot.index, button, clickTypePickup);
                            cir.setReturnValue(true);
                            return;
                        }
                    }
                }
            }

            // Hotbar Loop
            for (int i = 0; i < 9; i++) {
                int slotX = invPanelX + PANEL_PADDING + (i * SLOT_SIZE);
                int slotY = invPanelY + 10 + (3 * SLOT_SIZE);

                if (mouseX >= slotX && mouseX <= slotX + 18 && mouseY >= slotY && mouseY <= slotY + 18) {
                    int serverSlotIndex = 54 + 27 + i; 
                    if (serverSlotIndex < screen.getMenu().slots.size()) {
                        Slot serverSlot = screen.getMenu().getSlot(serverSlotIndex);
                        if (serverSlot != null) {
                            slotClickedMethod.invoke(screen, serverSlot, serverSlot.index, button, clickTypePickup);
                            cir.setReturnValue(true);
                            return;
                        }
                    }
                }
            }

        } catch (Exception ignored) {}
    }
}