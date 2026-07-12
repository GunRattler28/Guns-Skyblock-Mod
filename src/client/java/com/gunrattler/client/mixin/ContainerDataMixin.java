package com.gunrattler.client.mixin;

import com.gunrattler.client.util.StorageCache;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerDataMixin {

    private static final Pattern PAGE_PATTERN = Pattern.compile("(?:page\\s*|\\()(\\d+)");

    @Inject(method = "containerTick", at = @At("HEAD"))
    private void readCurrentContainerSlots(CallbackInfo ci) {
        try {
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
            if (screen.getMenu() == null) return;

            Component titleComponent = screen.getTitle();
            if (titleComponent == null) return;

            String title = titleComponent.getString();
            String lowerTitle = title.toLowerCase(); 
            String cacheKey = null;

            if (title.contains("Ender Chest")) {
                int pageNum = 1;
                
                Matcher matcher = PAGE_PATTERN.matcher(lowerTitle);
                if (matcher.find()) {
                    pageNum = Integer.parseInt(matcher.group(1));
                } else {
                    String numOnly = lowerTitle.replaceAll("[^0-9]", "");
                    if (!numOnly.isEmpty()) {
                        pageNum = Integer.parseInt(numOnly.length() > 1 && lowerTitle.contains("/") ? numOnly.substring(0, 1) : numOnly);
                    }
                }
                
                cacheKey = "enderchest_" + pageNum;
            } else if (title.contains("Backpack") || lowerTitle.contains("backpack")) {
                String num = title.replaceAll("[^0-9]", "");
                cacheKey = "backpack_" + (num.isEmpty() ? "1" : num);
            }

            if (cacheKey != null) {
                for (Slot slot : screen.getMenu().slots) {
                    if (slot.index >= 9 && slot.index < 54) {
                        net.minecraft.world.item.ItemStack item = slot.getItem();
                        String itemName = item.getHoverName().getString();

                        if (itemName.contains("Stained Glass Pane") || itemName.contains("SkyBlock Menu")) {
                            continue; 
                        }
                        
                        StorageCache.updateCache(cacheKey, slot.index, item);
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}