package com.lucidaps.xmas;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum GiftRarity {
    COMMON(ChatColor.WHITE, "Gewöhnlich", 70.0),
    RARE(ChatColor.BLUE, "Selten", 20.0),
    EPIC(ChatColor.DARK_PURPLE, "Episch", 8.0),
    LEGENDARY(ChatColor.GOLD, "Legendär", 2.0);

    private final ChatColor color;
    private final String displayName;
    private final double weight;

    GiftRarity(ChatColor color, String displayName, double weight) {
        this.color = color;
        this.displayName = displayName;
        this.weight = weight;
    }

    public ChatColor getColor() { return color; }
    public String getDisplayName() { return color + displayName; }
    public double getWeight() { return weight; }

    public static GiftRarity getRandom(Random random) {
        double totalWeight = 0.0;
        for (GiftRarity rarity : values()) {
            totalWeight += rarity.weight;
        }

        double randomValue = random.nextDouble() * totalWeight;
        double currentWeight = 0.0;

        for (GiftRarity rarity : values()) {
            currentWeight += rarity.weight;
            if (randomValue <= currentWeight) {
                return rarity;
            }
        }

        return COMMON;
    }
}
