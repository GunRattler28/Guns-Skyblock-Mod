package com.gunrattler.client.feature;

import com.gunrattler.client.util.TabParser;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class CustomScoreboard {

    public static void register() {
        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath("hypixel-skyblock-mod", "stats_overlay"),
            (graphics, deltaTracker) -> {
                Minecraft client = Minecraft.getInstance();

                if (client.getDebugOverlay().showDebugScreen()) {
                    return;
                }

                if (client.getCurrentServer() == null) {
                    return; 
                }
                
                String serverIp = client.getCurrentServer().ip.toLowerCase();
                if (!serverIp.contains("hypixel.net")) {
                    return; 
                }

                try {
                    TabParser.updateData();
                } catch (Exception e) {
                    // Fail-safe wrapper
                }

                if (!TabParser.hasData()) {
                    return;
                }

                Font font = client.font;
                int fixedWidth = 120;

                List<Supplier<String>> dataSuppliers = Arrays.asList(
                    () -> "§b§lSkyblock",
                    () -> "§r ",
                    () -> TabParser.getSbDate().isEmpty() ? null : "§b" + TabParser.getSbDate(),
                    () -> TabParser.getSbTime().isEmpty() ? null : "§e" + TabParser.getSbTime(),
                    () -> TabParser.getMainArea().isEmpty() ? null : TabParser.getMainArea(),
                    () -> TabParser.getSubArea().isEmpty() ? null : TabParser.getSubArea(),
                    () -> "§r  ", 
                    () -> TabParser.getPurse().isEmpty() ? null : "§6" + TabParser.getPurse(),
                    () -> TabParser.getBank().isEmpty() ? null : "§d" + TabParser.getBank(),
                    () -> (TabParser.getMithrilPowderRaw() > 0 || TabParser.getGemstonePowderRaw() > 0 || TabParser.getGlacitePowderRaw() > 0) ? "§r" : null,
                    () -> (TabParser.getMithrilPowderRaw() > 0 || TabParser.getGemstonePowderRaw() > 0 || TabParser.getGlacitePowderRaw() > 0) ? "§9§lPowder:" : null,
                    () -> TabParser.getMithrilPowderRaw() == 0 ? null : "§2  " + TabParser.getMithrilPowder(),
                    () -> TabParser.getGemstonePowderRaw() == 0 ? null : "§d  " + TabParser.getGemstonePowder(),
                    () -> TabParser.getGlacitePowderRaw() == 0 ? null : "§b  " + TabParser.getGlacitePowder(),
                    () -> TabParser.getObjectiveLine().isEmpty() ? null : "§r     ", 
                    () -> TabParser.getObjectiveLine().isEmpty() ? null : TabParser.getObjectiveLine(),
                    () -> TabParser.getObjectiveSubtitleLine().isEmpty() ? null : TabParser.getObjectiveSubtitleLine(),
                    () -> TabParser.getSlayerName().isEmpty() ? null : "§r      ",
                    () -> TabParser.getSlayerName().isEmpty() ? null : "§4§l" + TabParser.getSlayerName(),
                    () -> TabParser.getSlayerProgress().isEmpty() ? null : "  " + TabParser.getSlayerProgress()
                );

                List<FormattedCharSequence> wrappedLines = new ArrayList<>();
                for (Supplier<String> supplier : dataSuppliers) {
                    String data = supplier.get();
                    if (data != null && !data.isEmpty() && !data.equals("None")) {
                        net.minecraft.network.chat.Component component = net.minecraft.network.chat.Component.literal(data);
                        wrappedLines.addAll(font.split(component, fixedWidth));
                    }
                }

                if (wrappedLines.isEmpty()) return;

                int screenWidth = client.getWindow().getGuiScaledWidth();
                int screenHeight = client.getWindow().getGuiScaledHeight();
                
                int padding = 5;
                int lineSpacing = 10;
                int totalTextHeight = wrappedLines.size() * lineSpacing;

                int xPosition = screenWidth - fixedWidth - padding;
                int startYPosition = (screenHeight - totalTextHeight) / 2;

                int boxRight = xPosition + fixedWidth;
                int boxBottom = startYPosition + totalTextHeight;

                renderLeftRoundedBox(graphics, xPosition - padding, startYPosition - padding, boxRight + padding, boxBottom + padding, 0x44333333);

                int currentY = startYPosition;
                for (FormattedCharSequence line : wrappedLines) {
                    graphics.text(font, line, xPosition, currentY, 0xFFFFFFFF);
                    currentY += lineSpacing;
                }
            }
        );
    }

    private static void renderLeftRoundedBox(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int color) {
        graphics.fill(x1 + 4, y1, x2, y2, color);
        graphics.fill(x1 + 2, y1 + 1, x1 + 4, y2 - 1, color);
        graphics.fill(x1 + 1, y1 + 2, x1 + 2, y2 - 2, color);
        graphics.fill(x1, y1 + 4, x1 + 1, y2 - 4, color);
    }
}