package com.lucidaps.xmas;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GiftManager {
    private static final Map<GiftRarity, List<ItemStack>> giftsByRarity = new EnumMap<>(GiftRarity.class);
    
    static {
        // Initialize empty lists
        for (GiftRarity rarity : GiftRarity.values()) {
            giftsByRarity.put(rarity, new ArrayList<>());
        }
        
        // Default gifts if none are configured
        initializeDefaultGifts();
    }

    private static void initializeDefaultGifts() {
        // COMMON (70%)
        addGift(GiftRarity.COMMON, new ItemStack(Material.IRON_INGOT, 4));
        addGift(GiftRarity.COMMON, new ItemStack(Material.GOLD_INGOT, 2));
        addGift(GiftRarity.COMMON, new ItemStack(Material.REDSTONE, 16));
        addGift(GiftRarity.COMMON, new ItemStack(Material.LAPIS_LAZULI, 8));
        addGift(GiftRarity.COMMON, new ItemStack(Material.COAL, 32));
        addGift(GiftRarity.COMMON, new ItemStack(Material.COPPER_INGOT, 8));

        // RARE (20%)
        addGift(GiftRarity.RARE, new ItemStack(Material.DIAMOND, 2));
        addGift(GiftRarity.RARE, new ItemStack(Material.EMERALD, 3));
        addGift(GiftRarity.RARE, new ItemStack(Material.GOLD_BLOCK, 1));
        addGift(GiftRarity.RARE, new ItemStack(Material.IRON_BLOCK, 2));
        addGift(GiftRarity.RARE, new ItemStack(Material.ENCHANTED_BOOK, 1));

        // EPIC (8%)
        addGift(GiftRarity.EPIC, new ItemStack(Material.DIAMOND, 8));
        addGift(GiftRarity.EPIC, new ItemStack(Material.EMERALD, 10));
        addGift(GiftRarity.EPIC, new ItemStack(Material.NETHERITE_SCRAP, 2));
        addGift(GiftRarity.EPIC, new ItemStack(Material.DIAMOND_BLOCK, 2));
        addGift(GiftRarity.EPIC, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1));

        // LEGENDARY (2%)
        addGift(GiftRarity.LEGENDARY, new ItemStack(Material.NETHERITE_INGOT, 1));
        addGift(GiftRarity.LEGENDARY, new ItemStack(Material.DIAMOND_BLOCK, 5));
        addGift(GiftRarity.LEGENDARY, new ItemStack(Material.EMERALD_BLOCK, 3));
        addGift(GiftRarity.LEGENDARY, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 3));
        addGift(GiftRarity.LEGENDARY, new ItemStack(Material.ELYTRA, 1));
    }

    public static void addGift(GiftRarity rarity, ItemStack item) {
        giftsByRarity.get(rarity).add(item);
    }

    public static void clearGifts(GiftRarity rarity) {
        giftsByRarity.get(rarity).clear();
    }

    public static ItemStack getRandomGift(Random random, GiftRarity rarity) {
        List<ItemStack> gifts = giftsByRarity.get(rarity);
        if (gifts.isEmpty()) {
            return new ItemStack(Material.COAL); // Fallback
        }
        return gifts.get(random.nextInt(gifts.size())).clone();
    }

    public static ItemStack getRandomWeightedGift(Random random) {
        GiftRarity rarity = GiftRarity.getRandom(random);
        return getRandomGift(random, rarity);
    }

    public static ItemStack getChristmasLegendaryGift(Random random) {
        return getRandomGift(random, GiftRarity.LEGENDARY);
    }

    public static boolean isGoodGift(ItemStack item) {
        return item.getType() != Material.COAL && item.getType() != Material.AIR;
    }

    public static GiftRarity determineRarity(ItemStack item) {
        for (Map.Entry<GiftRarity, List<ItemStack>> entry : giftsByRarity.entrySet()) {
            for (ItemStack gift : entry.getValue()) {
                if (gift.getType() == item.getType()) {
                    return entry.getKey();
                }
            }
        }
        return GiftRarity.COMMON;
    }
}
