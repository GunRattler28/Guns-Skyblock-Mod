package com.gunrattler.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.ItemStack;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StorageCache {
    private static final Map<String, ItemStack[]> cachedInventories = new HashMap<>();
    
    private static Set<Integer> lockedSlots = new HashSet<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("hypixel-skyblock-locks.json");

    public static void setContainerItems(String key, ItemStack[] items) {
        cachedInventories.put(key, items);
    }

    public static ItemStack[] getContainerItems(String key) {
        return cachedInventories.get(key);
    }

    public static boolean hasPage(String key) {
        return cachedInventories.containsKey(key);
    }

    public static void updateCache(String pageKey, int slotIndex, ItemStack item) {
        if (!cachedInventories.containsKey(pageKey)) {
            cachedInventories.put(pageKey, new ItemStack[54]);
        }

        ItemStack[] items = cachedInventories.get(pageKey);
        
        if (slotIndex >= 0 && slotIndex < items.length) {
            items[slotIndex] = item != null ? item.copy() : ItemStack.EMPTY;
        }
    }

    public static boolean isSlotLocked(int slotIndex) {
        return lockedSlots.contains(slotIndex);
    }

    public static void toggleSlotLock(int slotIndex) {
        if (lockedSlots.contains(slotIndex)) {
            lockedSlots.remove(slotIndex);
        } else {
            lockedSlots.add(slotIndex);
        }
        
        saveLocks(); 
    }

    public static void loadLocks() {
        if (!Files.exists(CONFIG_PATH)) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            Type type = new TypeToken<HashSet<Integer>>(){}.getType();
            Set<Integer> loaded = GSON.fromJson(reader, type);
            
            if (loaded != null) {
                lockedSlots = loaded;
            }
        } catch (Exception e) {
            System.err.println("Failed to load Hypixel Skyblock locked slots: " + e.getMessage());
        }
    }

    private static void saveLocks() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(lockedSlots, writer);
            }
        } catch (Exception e) {
            System.err.println("Failed to save Hypixel Skyblock locked slots: " + e.getMessage());
        }
    }
}