package com.gunrattler.client.mixin;

import com.gunrattler.client.util.TabParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class EnderChestInterceptMixin {

    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Shadow protected int imageWidth;
    @Shadow protected int imageHeight;

    @Unique private static final int PANEL_WIDTH = 124;
    @Unique private static final int SLOT_SIZE = 22;
    @Unique private static final int SLOT_GAP = 4;
    @Unique private static final int GRID_COLS = 4;

    @Unique
    private Component getScreenTitle() {
        return ((net.minecraft.client.gui.screens.Screen) (Object) this).getTitle();
    }

    // 1. RENDERING LOGIC: This belongs in extractRenderState where 'graphics' is available
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void drawFirmamentStorageOverlay(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        try {
            Component currentTitle = this.getScreenTitle();
            if (!TabParser.hasData() || currentTitle == null) return;

            String titleText = currentTitle.getString();
            boolean isEnderChest = titleText.contains("Ender Chest");
            boolean isBackpack = titleText.contains("Backpack");
            boolean isStorageMenu = titleText.equalsIgnoreCase("Storage");

            if (!isEnderChest && !isBackpack && !isStorageMenu) return;

            Minecraft mc = Minecraft.getInstance();
            int screenWidth = mc.getWindow().getGuiScaledWidth();

            int startX = this.leftPos + this.imageWidth + 6;
            if (startX + PANEL_WIDTH > screenWidth) return;

            int startY = this.topPos;
            Font font = mc.font;

            graphics.fill(startX, startY, startX + PANEL_WIDTH, startY + this.imageHeight, 0xD0101010);
            graphics.fill(startX, startY, startX + PANEL_WIDTH, startY + 1, 0xFF555555);
            graphics.fill(startX, startY + this.imageHeight - 1, startX + PANEL_WIDTH, startY + this.imageHeight, 0xFF555555);
            graphics.fill(startX, startY, startX + 1, startY + this.imageHeight, 0xFF555555);
            graphics.fill(startX + PANEL_WIDTH - 1, startY, startX + PANEL_WIDTH, startY + this.imageHeight, 0xFF555555);

            // 2. Render Header Title
            String headerText = "STORAGE";
            int headerX = startX + (PANEL_WIDTH - font.width(headerText)) / 2;
            graphics.text(font, Component.literal("§6§l" + headerText).getVisualOrderText(), headerX, startY + 8, 0xFFFFFFFF);
            graphics.fill(startX + 8, startY + 20, startX + PANEL_WIDTH - 8, startY + 21, 0x44FFFFFF);

            int currentY = startY + 28;

            // 3. Render Button: Main Storage Menu
            boolean hoverStorage = mouseX >= startX + 8 && mouseX <= startX + PANEL_WIDTH - 8 && mouseY >= currentY && mouseY <= currentY + 18;
            int storageBg = isStorageMenu ? 0x6600AAFF : (hoverStorage ? 0x44FFFFFF : 0x22FFFFFF);
            graphics.fill(startX + 8, currentY, startX + PANEL_WIDTH - 8, currentY + 18, storageBg);
            graphics.text(font, Component.literal(isStorageMenu ? "§b➤ Storage" : "§7Storage").getVisualOrderText(), startX + 14, currentY + 5, 0xFFFFFFFF);
            
            currentY += 24;

            // 4. Render Button: Ender Chest
            boolean hoverEC = mouseX >= startX + 8 && mouseX <= startX + PANEL_WIDTH - 8 && mouseY >= currentY && mouseY <= currentY + 18;
            int ecBg = isEnderChest ? 0x66AA00AA : (hoverEC ? 0x44FFFFFF : 0x22FFFFFF);
            graphics.fill(startX + 8, currentY, startX + PANEL_WIDTH - 8, currentY + 18, ecBg);
            graphics.text(font, Component.literal(isEnderChest ? "§d➤ Ender Chest" : "§7Ender Chest").getVisualOrderText(), startX + 14, currentY + 5, 0xFFFFFFFF);

            currentY += 28;

            // 5. Render Label: Backpacks Grid Header
            graphics.text(font, Component.literal("§eBackpacks:").getVisualOrderText(), startX + 8, currentY, 0xFFFFFFFF);
            currentY += 12;

            // 6. Render the 18 Backpack Slots Grid
            int gridStartX = startX + (PANEL_WIDTH - ((SLOT_SIZE * GRID_COLS) + (SLOT_GAP * (GRID_COLS - 1)))) / 2;
            
            for (int i = 1; i <= 18; i++) {
                int index = i - 1;
                int col = index % GRID_COLS;
                int row = index / GRID_COLS;

                int slotX = gridStartX + (col * (SLOT_SIZE + SLOT_GAP));
                int slotY = currentY + (row * (SLOT_SIZE + SLOT_GAP));

                boolean isThisBackpack = titleText.contains("Backpack (Slot #" + i + ")");
                boolean hoverSlot = mouseX >= slotX && mouseX <= slotX + SLOT_SIZE && mouseY >= slotY && mouseY <= slotY + SLOT_SIZE;

                // Determine slot styling based on state
                int slotColor = isThisBackpack ? 0x6655FF55 : (hoverSlot ? 0x55FFFFFF : 0x15FFFFFF);
                int borderColor = isThisBackpack ? 0xFF55FF55 : (hoverSlot ? 0xFFFFFFFF : 0xFF444444);

                // Draw Slot Box & Inner Border Layout
                graphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, slotColor);
                graphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + 1, borderColor); // top
                graphics.fill(slotX, slotY + SLOT_SIZE - 1, slotX + SLOT_SIZE, slotY + SLOT_SIZE, borderColor);
                graphics.fill(slotX, slotY, slotX + 1, slotY + SLOT_SIZE, borderColor);
                graphics.fill(slotX + SLOT_SIZE - 1, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, borderColor);

                String numStr = String.valueOf(i);
                int numX = slotX + (SLOT_SIZE - font.width(numStr)) / 2;
                int numY = slotY + (SLOT_SIZE - 8) / 2;
                graphics.text(font, Component.literal(isThisBackpack ? "§a§l" + numStr : "§7" + numStr).getVisualOrderText(), numX, numY, 0xFFFFFFFF);
            }
        } catch (Exception ignored) {}
    }

    // 2. CLICK INTERACTION LOGIC: Cleaned up with modern 26.2 MouseButtonEvent layout
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void handleFirmamentNavigationClicks(MouseButtonEvent event, boolean processed, CallbackInfoReturnable<Boolean> cir) {
        try {
            Component currentTitle = this.getScreenTitle();
            if (!TabParser.hasData() || currentTitle == null) return;

            String titleText = currentTitle.getString();
            if (!(titleText.contains("Ender Chest") || titleText.contains("Backpack") || titleText.equalsIgnoreCase("Storage"))) return;

            // Extract coordinates using correct 26.2 getters
            double mouseX = event.x();
            double mouseY = event.y();

            Minecraft mc = Minecraft.getInstance();
            int screenWidth = mc.getWindow().getGuiScaledWidth();

            int startX = this.leftPos + this.imageWidth + 6;
            if (startX + PANEL_WIDTH > screenWidth) return;

            int startY = this.topPos;
            if (mc.player == null || mc.player.connection == null) return;

            int storageY = startY + 28;
            if (mouseX >= startX + 8 && mouseX <= startX + PANEL_WIDTH - 8 && mouseY >= storageY && mouseY <= storageY + 18) {
                mc.player.connection.sendCommand("storage");
                cir.setReturnValue(true);
                return;
            }

            int ecY = startY + 52;
            if (mouseX >= startX + 8 && mouseX <= startX + PANEL_WIDTH - 8 && mouseY >= ecY && mouseY <= ecY + 18) {
                mc.player.connection.sendCommand("ec");
                cir.setReturnValue(true);
                return;
            }

            int gridStartY = startY + 98;
            int gridStartX = startX + (PANEL_WIDTH - ((SLOT_SIZE * GRID_COLS) + (SLOT_GAP * (GRID_COLS - 1)))) / 2;

            for (int i = 1; i <= 18; i++) {
                int index = i - 1;
                int col = index % GRID_COLS;
                int row = index / GRID_COLS;

                int slotX = gridStartX + (col * (SLOT_SIZE + SLOT_GAP));
                int slotY = gridStartY + (row * (SLOT_SIZE + SLOT_GAP));

                if (mouseX >= slotX && mouseX <= slotX + SLOT_SIZE && mouseY >= slotY && mouseY <= slotY + SLOT_SIZE) {
                    mc.player.connection.sendCommand("bp " + i);
                    cir.setReturnValue(true);
                    return;
                }
            }
        } catch (Exception ignored) {}
    }
}