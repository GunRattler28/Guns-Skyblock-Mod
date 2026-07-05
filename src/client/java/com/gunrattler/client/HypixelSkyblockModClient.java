package com.gunrattler.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.Set;

public class HypixelSkyblockModClient implements ClientModInitializer {

    public static final Set<String> lockedSlots = new HashSet<>();

    private static class PriceEntry {
        final long buyPrice;
        final long sellPrice;

        PriceEntry(long buyPrice, long sellPrice) {
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
        }
    }

    private static final Map<String, PriceEntry> bazaarCache = new ConcurrentHashMap<>();
    private static final Map<String, Long> lbinCache = new ConcurrentHashMap<>();
    
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
            
    private static String statusIndicator = "Connecting to market trackers...";

    @Override
    public void onInitializeClient() {
        fetchBazaar();
        fetchLbin();

        ItemTooltipCallback.EVENT.register((ItemStack stack, Item.TooltipContext context, TooltipFlag type, List<Component> lines) -> {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            
            if (customData != null) {
                CompoundTag rootTag = customData.copyTag();
                
                if (rootTag.contains("id")) {
                    rootTag.getString("id").ifPresent(skyblockId -> {
                        String targetId = skyblockId.toUpperCase();
                        
                        PriceEntry bzEntry = bazaarCache.get(targetId);
                        Long ahPrice = lbinCache.get(targetId);

                        int count = stack.getCount();
                        
                        // Universal input parsing completely clean of Java version pointer regressions
                        boolean isHoldingShift = false;
                        Minecraft mc = Minecraft.getInstance();
                        if (mc != null && mc.options != null && mc.options.keyShift != null) {
                            isHoldingShift = mc.options.keyShift.isDown();
                        }

                        if (bzEntry != null) {
                            long finalBuy = isHoldingShift ? bzEntry.buyPrice * count : bzEntry.buyPrice;
                            long finalSell = isHoldingShift ? bzEntry.sellPrice * count : bzEntry.sellPrice;
                            String modeTag = isHoldingShift ? " Stack" : "";

                            lines.add(Component.literal("§6Bazaar Insta-Buy" + modeTag + ": §e" + formatPrice(finalBuy) + " coins"));
                            lines.add(Component.literal("§dBazaar Insta-Sell" + modeTag + ": §a" + formatPrice(finalSell) + " coins"));
                            
                            if (count > 1 && !isHoldingShift) {
                                lines.add(Component.literal("§8Hold [SHIFT] for entire stack prices"));
                            }
                        } 
                        else if (ahPrice != null && ahPrice > 0) {
                            long finalAh = isHoldingShift ? ahPrice * count : ahPrice;
                            String prefix = isHoldingShift ? "§dLowest BIN Stack: " : "§dLowest BIN: ";
                            
                            lines.add(Component.literal(prefix + "§e" + formatPrice(finalAh) + " coins"));
                            
                            if (count > 1 && !isHoldingShift) {
                                lines.add(Component.literal("§8Hold [SHIFT] for entire stack price"));
                            }
                        } else {
                            lines.add(Component.literal("§7ID: " + skyblockId + " §8(" + statusIndicator + ")"));
                        }
                    });
                }
            }
        });
    }

    private void fetchBazaar() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.hypixel.net/v2/skyblock/bazaar"))
                .header("User-Agent", "Mozilla/5.0")
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> {
                    if (res != null && res.statusCode() == 200) {
                        parseBazaarEngine(res.body());
                        statusIndicator = "Active Sync";
                    }
                });
    }

    private void fetchLbin() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://lb.tricked.dev/lowestbins.json"))
                .header("User-Agent", "Mozilla/5.0 MatrixPriceMod")
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> {
                    if (res != null && res.statusCode() == 200) {
                        parseLbinEngine(res.body());
                        statusIndicator = "Active Sync";
                    } else {
                        statusIndicator = "AH Endpoint Error";
                    }
                });
    }

    private void parseBazaarEngine(String json) {
        try {
            Pattern pattern = Pattern.compile("\"product_id\"\\s*:\\s*\"([^\"]+)\".*?\"buyPrice\"\\s*:\\s*([0-9.]+).*?\"sellPrice\"\\s*:\\s*([0-9.]+)");
            Matcher matcher = pattern.matcher(json);
            int matchedItems = 0;

            while (matcher.find()) {
                String id = matcher.group(1).toUpperCase();
                try {
                    double buyVal = Double.parseDouble(matcher.group(2));
                    double sellVal = Double.parseDouble(matcher.group(3));
                    if (buyVal > 0 || sellVal > 0) {
                        bazaarCache.put(id, new PriceEntry((long) buyVal, (long) sellVal));
                        matchedItems++;
                    }
                } catch (NumberFormatException ignored) {}
            }
            System.out.println("[Price Mod] Bazaar parsing complete: " + matchedItems + " entries mapped.");
        } catch (Exception e) {
            System.out.println("[Price Mod] Bazaar parser failed: " + e.getMessage());
        }
    }

    private void parseLbinEngine(String json) {
        try {
            Pattern pattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*([0-9.]+)");
            Matcher matcher = pattern.matcher(json);
            int matchedItems = 0;

            while (matcher.find()) {
                String id = matcher.group(1).toUpperCase();
                try {
                    double val = Double.parseDouble(matcher.group(2));
                    if (val > 0) {
                        lbinCache.put(id, (long) val);
                        matchedItems++;
                    }
                } catch (NumberFormatException ignored) {}
            }
            System.out.println("[Price Mod] LBIN parsing complete: " + matchedItems + " entries mapped.");
        } catch (Exception e) {
            System.out.println("[Price Mod] LBIN parser failed: " + e.getMessage());
        }
    }

    private String formatPrice(long price) {
        if (price >= 1_000_000_000) return String.format("%.2fB", price / 1_000_000_000.0);
        if (price >= 1_000_000) return String.format("%.2fM", price / 1_000_000.0);
        if (price >= 1_000) return String.format("%.1fK", price / 1_000.0);
        return String.valueOf(price);
    }
}