package com.gunrattler.client.gui;

import com.gunrattler.client.HypixelSkyblockModClient;
import com.gunrattler.client.util.StorageCache;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class FirmamentStorageScreen extends Screen {

    private final int SLOT_SIZE = 18;
    private final int PANEL_PADDING = 7;
    private final int PANEL_WIDTH = (9 * SLOT_SIZE) + (PANEL_PADDING * 2);
    private final int PANEL_HEIGHT = (6 * SLOT_SIZE) + 18 + PANEL_PADDING;

    // Added the lock texture identifier
    private static final Identifier LOCK_TEXTURE = Identifier.fromNamespaceAndPath("hypixel-skyblock-mod", "textures/gui/lockedslot.png");

    public FirmamentStorageScreen() {
        super(Component.literal("Firmament Storage"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xD0101010);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        // Center a single row of 3 pages side-by-side
        int totalCols = 3;
        int startX = (this.width - (totalCols * PANEL_WIDTH)) / 2;
        int startY = (this.height - PANEL_HEIGHT - 100) / 2; // Lifted slightly to make room for player inventory

        ItemStack hoveredStack = null;

        for (int pageIndex = 0; pageIndex < 3; pageIndex++) {
            int panelX = startX + (pageIndex * PANEL_WIDTH);
            int panelY = startY;

            // Gray panel background
            graphics.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xFFC6C6C6);
            graphics.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + 2, 0xFFFFFFFF);
            graphics.fill(panelX, panelY, panelX + 2, panelY + PANEL_HEIGHT, 0xFFFFFFFF);
            graphics.fill(panelX + PANEL_WIDTH - 2, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xFF555555);
            graphics.fill(panelX, panelY + PANEL_HEIGHT - 2, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xFF555555);

            String pageName = "Ender Chest Page #" + (pageIndex + 1);
            graphics.text(this.font, Component.literal("§7" + pageName).getVisualOrderText(), panelX + PANEL_PADDING, panelY + 6, 0xFF404040);

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

                ItemStack stack = items != null && i < items.length && items[i] != null ? items[i] : ItemStack.EMPTY;
                
                if (!stack.isEmpty()) {
                    graphics.item(stack, slotX + 1, slotY + 1);
                    
                    // --- RENDER PADLOCK FOR ENDER CHEST PAGES ---
                    // "SimpleContainer" is the usual class for generic chests.
                    String ecSlotKey = "SimpleContainer:" + i;
                    if (HypixelSkyblockModClient.lockedSlots.contains(ecSlotKey)) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, LOCK_TEXTURE, slotX + 1, slotY + 1, 0.0f, 0.0f, 16, 16, 16, 16);
                    }

                    if (mouseX >= slotX && mouseX <= slotX + 18 && mouseY >= slotY && mouseY <= slotY + 18) {
                        hoveredStack = stack;
                        graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0x80FFFFFF);
                    }
                }
            }
        }

        int invPanelX = (this.width - PANEL_WIDTH) / 2;
        int invPanelY = startY + PANEL_HEIGHT + 15;
        int invPanelHeight = (4 * SLOT_SIZE) + 12;

        graphics.fill(invPanelX, invPanelY, invPanelX + PANEL_WIDTH, invPanelY + invPanelHeight, 0xFFC6C6C6);
        graphics.fill(invPanelX, invPanelY, invPanelX + PANEL_WIDTH, invPanelY + 2, 0xFFFFFFFF);
        graphics.fill(invPanelX, invPanelY, invPanelX + 2, invPanelY + invPanelHeight, 0xFFFFFFFF);
        graphics.fill(invPanelX + PANEL_WIDTH - 2, invPanelY, invPanelX + PANEL_WIDTH, invPanelY + invPanelHeight, 0xFF555555);
        graphics.fill(invPanelX, invPanelY + invPanelHeight - 2, invPanelX + PANEL_WIDTH, invPanelY + invPanelHeight, 0xFF555555);

        for (int i = 9; i < 36; i++) {
            int slotCol = (i - 9) % 9;
            int slotRow = (i - 9) / 9;
            int slotX = invPanelX + PANEL_PADDING + (slotCol * SLOT_SIZE);
            int slotY = invPanelY + 6 + (slotRow * SLOT_SIZE);

            graphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF8B8B8B);
            graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0xFF373737);
            graphics.fill(slotX + 1, slotY + 1, slotX + 16, slotY + 16, 0xFF8B8B8B);

            if (this.minecraft != null && this.minecraft.player != null) {
                ItemStack stack = this.minecraft.player.getInventory().getItem(i);
                if (!stack.isEmpty()) {
                    graphics.item(stack, slotX + 1, slotY + 1);

                    // --- RENDER PADLOCK FOR PLAYER INVENTORY ---
                    String invSlotKey = "Inventory:" + i;
                    if (HypixelSkyblockModClient.lockedSlots.contains(invSlotKey)) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, LOCK_TEXTURE, slotX + 1, slotY + 1, 0.0f, 0.0f, 16, 16, 16, 16);
                    }

                    if (mouseX >= slotX && mouseX <= slotX + 18 && mouseY >= slotY && mouseY <= slotY + 18) {
                        hoveredStack = stack;
                        graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0x80FFFFFF);
                    }
                }
            }
        }

        // Hotbar
        for (int i = 0; i < 9; i++) {
            int slotX = invPanelX + PANEL_PADDING + (i * SLOT_SIZE);
            int slotY = invPanelY + 10 + (3 * SLOT_SIZE);

            graphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF8B8B8B);
            graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0xFF373737);
            graphics.fill(slotX + 1, slotY + 1, slotX + 16, slotY + 16, 0xFF8B8B8B);

            if (this.minecraft != null && this.minecraft.player != null) {
                ItemStack stack = this.minecraft.player.getInventory().getItem(i);
                if (!stack.isEmpty()) {
                    graphics.item(stack, slotX + 1, slotY + 1);

                    // --- RENDER PADLOCK FOR HOTBAR ---
                    String hotbarSlotKey = "Inventory:" + i;
                    if (HypixelSkyblockModClient.lockedSlots.contains(hotbarSlotKey)) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, LOCK_TEXTURE, slotX + 1, slotY + 1, 0.0f, 0.0f, 16, 16, 16, 16);
                    }

                    if (mouseX >= slotX && mouseX <= slotX + 18 && mouseY >= slotY && mouseY <= slotY + 18) {
                        hoveredStack = stack;
                        graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0x80FFFFFF);
                    }
                }
            }
        }

        // --- TOOLTIPS ---
        if (hoveredStack != null && !hoveredStack.isEmpty() && this.minecraft != null && this.minecraft.level != null) {
            List<Component> tooltipLines = hoveredStack.getTooltipLines(
                TooltipContext.of(this.minecraft.level),
                this.minecraft.player,
                TooltipFlag.Default.NORMAL
            );

            List<ClientTooltipComponent> clientLines = tooltipLines.stream()
                .map(Component::getVisualOrderText)
                .map(ClientTooltipComponent::create)
                .toList();

            graphics.tooltip(
                this.font, 
                clientLines, 
                mouseX, 
                mouseY, 
                (screenWidth, screenHeight, mX, mY, width, height) -> new org.joml.Vector2i(mX + 12, mY - 12),
                Identifier.fromNamespaceAndPath("minecraft", "tooltip")
            );
        }
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_E || event.key() == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) { 
            double mouseX = event.x();
            double mouseY = event.y();

            int totalCols = 3;
            int startX = (this.width - (totalCols * PANEL_WIDTH)) / 2;
            int startY = (this.height - PANEL_HEIGHT - 100) / 2;

            for (int pageIndex = 0; pageIndex < 3; pageIndex++) {
                int panelX = startX + (pageIndex * PANEL_WIDTH);
                int panelY = startY;

                if (mouseX >= panelX && mouseX <= (panelX + PANEL_WIDTH) &&
                    mouseY >= panelY && mouseY <= (panelY + PANEL_HEIGHT)) {
                    
                    int targetPage = pageIndex + 1;
                    
                    if (this.minecraft != null && this.minecraft.player != null) {
                        this.minecraft.getSoundManager().play(
                            net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                                net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0F
                            )
                        );

                        String command = "ec " + targetPage;
                        this.minecraft.player.connection.sendCommand(command);
                        
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}