package com.gunrattler.client.util;

import net.minecraft.world.item.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class StorageCache {
    // Maps a page key (like "EC_PAGE_1") to an array of 54 item slots
    private static final Map<String, ItemStack[]> cachedInventories = new HashMap<>();

    public static void setContainerItems(String key, ItemStack[] items) {
        cachedInventories.put(key, items);
    }

    public static ItemStack[] getContainerItems(String key) {
        return cachedInventories.get(key);
    }

    public static boolean hasPage(String key) {
        return cachedInventories.containsKey(key);
    }

    /**
     * Updates a single slot inside a cached inventory page.
     * If the page doesn't exist in the cache yet, it initializes it.
     */
    public static void updateCache(String pageKey, int slotIndex, ItemStack item) {
        if (!cachedInventories.containsKey(pageKey)) {
            cachedInventories.put(pageKey, new ItemStack[54]);
        }

        ItemStack[] items = cachedInventories.get(pageKey);
        
        if (slotIndex >= 0 && slotIndex < items.length) {
            items[slotIndex] = item != null ? item.copy() : ItemStack.EMPTY;
        }
    }
}