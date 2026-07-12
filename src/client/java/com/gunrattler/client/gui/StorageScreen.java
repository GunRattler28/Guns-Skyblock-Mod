package com.gunrattler.client.gui;

import com.gunrattler.client.HypixelSkyblockModClient;
import com.gunrattler.client.util.StoragePageState;
import com.gunrattler.client.util.StorageCache;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class StorageScreen extends Screen {

    private final int SLOT_SIZE = 18;
    private final int PANEL_PADDING = 7;
    private final int PANEL_WIDTH = (9 * SLOT_SIZE) + (PANEL_PADDING * 2);
    private final int PANEL_HEIGHT = (5 * SLOT_SIZE) + 22 + PANEL_PADDING;

    private static final Identifier LOCK_TEXTURE = Identifier.fromNamespaceAndPath("hypixel-skyblock-mod", "textures/gui/lockedslot.png");

    public StorageScreen() {
        super(Component.literal("Storage Screen"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xD0101010);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        int totalCols = 3;
        int startX = (this.width - (totalCols * PANEL_WIDTH)) / 2;
        int startY = (this.height - PANEL_HEIGHT - 100) / 2;

        ItemStack hoveredStack = null;

        for (int pageIndex = 0; pageIndex < 3; pageIndex++) {
            int panelX = startX + (pageIndex * PANEL_WIDTH);
            int panelY = startY;

            graphics.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xFFC6C6C6);
            graphics.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + 2, 0xFFFFFFFF);
            graphics.fill(panelX, panelY, panelX + 2, panelY + PANEL_HEIGHT, 0xFFFFFFFF);
            graphics.fill(panelX + PANEL_WIDTH - 2, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xFF555555);
            graphics.fill(panelX, panelY + PANEL_HEIGHT - 2, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xFF555555);

            int displayPageNum = pageIndex + 1;
            String pageName = "Ender Chest Page #" + displayPageNum;
            if (StoragePageState.lastRequestedPage == displayPageNum) {
                pageName += " §c(Active)";
            }
            graphics.text(this.font, Component.literal("§7" + pageName).getVisualOrderText(), panelX + PANEL_PADDING, panelY + 6, 0xFF404040);

            String cacheKey = "enderchest_" + displayPageNum;
            ItemStack[] items = StorageCache.getContainerItems(cacheKey);

            for (int i = 9; i < 54; i++) {
                int slotCol = i % 9;
                int slotRow = (i - 9) / 9;

                int slotX = panelX + PANEL_PADDING + (slotCol * SLOT_SIZE);
                int slotY = panelY + 22 + (slotRow * SLOT_SIZE);

                graphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF8B8B8B);
                graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0xFF373737);
                graphics.fill(slotX + 1, slotY + 1, slotX + 16, slotY + 16, 0xFF8B8B8B);

                ItemStack stack = items != null && i < items.length && items[i] != null ? items[i] : ItemStack.EMPTY;
                
                if (!stack.isEmpty()) {
                    graphics.item(stack, slotX + 1, slotY + 1);
                    graphics.itemDecorations(this.font, stack, slotX + 1, slotY + 1);
                    String ecSlotKey = "SimpleContainer:" + i;
                    if (HypixelSkyblockModClient.lockedSlots.contains(ecSlotKey)) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, LOCK_TEXTURE, slotX + 1, slotY + 1, 0.0f, 0.0f, 16, 16, 16, 16);
                    }
                }

                if (mouseX >= slotX && mouseX <= slotX + 18 && mouseY >= slotY && mouseY <= slotY + 18) {
                    if (!stack.isEmpty()) hoveredStack = stack;
                    graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0x80FFFFFF);
                }
            }
        }

        int invPanelX = (this.width - PANEL_WIDTH) / 2;
        int invPanelY = startY + PANEL_HEIGHT + 15;
        int invPanelHeight = (4 * SLOT_SIZE) + 10 + PANEL_PADDING;

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
                    graphics.itemDecorations(this.font, stack, slotX + 1, slotY + 1);

                    String invSlotKey = "Inventory:" + i;
                    if (HypixelSkyblockModClient.lockedSlots.contains(invSlotKey)) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, LOCK_TEXTURE, slotX + 1, slotY + 1, 0.0f, 0.0f, 16, 16, 16, 16);
                    }
                }

                if (mouseX >= slotX && mouseX <= slotX + 18 && mouseY >= slotY && mouseY <= slotY + 18) {
                    if (!stack.isEmpty()) hoveredStack = stack;
                    graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0x80FFFFFF);
                }
            }
        }

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
                    graphics.itemDecorations(this.font, stack, slotX + 1, slotY + 1);
                    String hotbarSlotKey = "Inventory:" + i;
                    if (HypixelSkyblockModClient.lockedSlots.contains(hotbarSlotKey)) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, LOCK_TEXTURE, slotX + 1, slotY + 1, 0.0f, 0.0f, 16, 16, 16, 16);
                    }
                }

                if (mouseX >= slotX && mouseX <= slotX + 18 && mouseY >= slotY && mouseY <= slotY + 18) {
                    if (!stack.isEmpty()) hoveredStack = stack;
                    graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0x80FFFFFF);
                }
            }
        }

        if (this.minecraft != null && this.minecraft.player != null && this.minecraft.player.containerMenu != null) {
            ItemStack carriedStack = this.minecraft.player.containerMenu.getCarried();
            if (!carriedStack.isEmpty()) {
                graphics.item(carriedStack, mouseX - 8, mouseY - 8);
                graphics.itemDecorations(this.font, carriedStack, mouseX - 8, mouseY - 8);
            }
        }

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
        if (this.minecraft == null || this.minecraft.player == null || this.minecraft.gameMode == null || this.minecraft.player.containerMenu == null) {
            return super.mouseClicked(event, doubleClick);
        }

        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        int totalCols = 3;
        int startX = (this.width - (totalCols * PANEL_WIDTH)) / 2;
        int startY = (this.height - PANEL_HEIGHT - 100) / 2;
        int containerId = this.minecraft.player.containerMenu.containerId;

        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        com.mojang.blaze3d.platform.Window window = mc.getWindow();

        boolean isShiftDown = com.mojang.blaze3d.platform.InputConstants.isKeyDown(window, org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT) 
                        || com.mojang.blaze3d.platform.InputConstants.isKeyDown(window, org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT);

        ContainerInput inputType = isShiftDown ? ContainerInput.QUICK_MOVE : ContainerInput.PICKUP;

        for (int pageIndex = 0; pageIndex < 3; pageIndex++) {
            int panelX = startX + (pageIndex * PANEL_WIDTH);
            int panelY = startY;

            if (mouseX >= panelX && mouseX <= (panelX + PANEL_WIDTH) &&
                mouseY >= panelY && mouseY <= (panelY + PANEL_HEIGHT)) {
                
                int targetPage = pageIndex + 1;

                for (int i = 9; i < 54; i++) {
                    int slotCol = i % 9;
                    int slotRow = (i - 9) / 9;
                    int slotX = panelX + PANEL_PADDING + (slotCol * SLOT_SIZE);
                    int slotY = panelY + 22 + (slotRow * SLOT_SIZE);

                    if (mouseX >= slotX && mouseX <= slotX + 18 && mouseY >= slotY && mouseY <= slotY + 18) {
                        String ecSlotKey = "SimpleContainer:" + i;
                        if (HypixelSkyblockModClient.lockedSlots.contains(ecSlotKey)) {
                            this.minecraft.player.sendSystemMessage(Component.literal("§cSlot is locked!"));
                            return true;
                        }

                        if (StoragePageState.lastRequestedPage == targetPage) {
                            this.minecraft.gameMode.handleContainerInput(containerId, i, button, inputType, this.minecraft.player);
                        } else {
                            StoragePageState.lastRequestedPage = targetPage;
                            this.minecraft.player.connection.sendCommand("ec " + targetPage);
                        }
                        return true;
                    }
                }

                if (StoragePageState.lastRequestedPage != targetPage) {
                    StoragePageState.lastRequestedPage = targetPage;
                    this.minecraft.player.connection.sendCommand("ec " + targetPage);
                    return true;
                }
            }
        }

        int invPanelX = (this.width - PANEL_WIDTH) / 2;
        int invPanelY = startY + PANEL_HEIGHT + 15;

        for (int i = 9; i < 36; i++) {
            int slotCol = (i - 9) % 9;
            int slotRow = (i - 9) / 9;
            int slotX = invPanelX + PANEL_PADDING + (slotCol * SLOT_SIZE);
            int slotY = invPanelY + 6 + (slotRow * SLOT_SIZE);

            if (mouseX >= slotX && mouseX <= slotX + 18 && mouseY >= slotY && mouseY <= slotY + 18) {
                String invSlotKey = "Inventory:" + i;
                if (HypixelSkyblockModClient.lockedSlots.contains(invSlotKey)) {
                    this.minecraft.player.sendSystemMessage(Component.literal("§cSlot is locked!"));
                    return true;
                }

                int serverSlotId = i + 45;
                this.minecraft.gameMode.handleContainerInput(containerId, serverSlotId, button, inputType, this.minecraft.player);
                return true;
            }
        }

        for (int i = 0; i < 9; i++) {
            int slotX = invPanelX + PANEL_PADDING + (i * SLOT_SIZE);
            int slotY = invPanelY + 10 + (3 * SLOT_SIZE);

            if (mouseX >= slotX && mouseX <= slotX + 18 && mouseY >= slotY && mouseY <= slotY + 18) {
                String hotbarSlotKey = "Inventory:" + i;
                if (HypixelSkyblockModClient.lockedSlots.contains(hotbarSlotKey)) {
                    this.minecraft.player.sendSystemMessage(Component.literal("§cSlot is locked!"));
                    return true;
                }

                int serverSlotId = i + 81;
                this.minecraft.gameMode.handleContainerInput(containerId, serverSlotId, button, inputType, this.minecraft.player);
                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        
        if (this.minecraft != null && this.minecraft.player != null && this.minecraft.player.containerMenu != null) {
            String activeCacheKey = "enderchest_" + StoragePageState.lastRequestedPage;
            
            for (int i = 0; i < 54; i++) {
                if (i < this.minecraft.player.containerMenu.slots.size()) {
                    ItemStack serverStack = this.minecraft.player.containerMenu.slots.get(i).getItem();
                    StorageCache.updateCache(activeCacheKey, i, serverStack);
                }
            }
        }
    }
}