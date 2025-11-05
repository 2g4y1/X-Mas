package com.lucidaps.xmas;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Achievement {
    FIRST_TREE(
        "first_tree",
        "Erster Baum",
        "Pflanze deinen ersten Weihnachtsbaum",
        Material.SPRUCE_SAPLING,
        ChatColor.GREEN
    ),
    MAX_LEVEL(
        "max_level",
        "Maximale Höhe",
        "Erreiche den MAGIC_TREE Level",
        Material.GLOWSTONE,
        ChatColor.GOLD
    ),
    GIFT_COLLECTOR(
        "gift_collector",
        "Geschenk-Sammler",
        "Sammle 100 Geschenke",
        Material.CHEST,
        ChatColor.YELLOW
    ),
    LUCKY_STREAK(
        "lucky_streak",
        "Glückspilz",
        "Erhalte 10 gute Geschenke in Folge",
        Material.EMERALD,
        ChatColor.AQUA
    ),
    TREE_MASTER(
        "tree_master",
        "Baumeister",
        "Baue 5 Bäume gleichzeitig",
        Material.DIAMOND,
        ChatColor.LIGHT_PURPLE
    );

    private final String key;
    private final String name;
    private final String description;
    private final Material icon;
    private final ChatColor color;

    Achievement(String key, String name, String description, Material icon, ChatColor color) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.color = color;
    }

    public String getKey() { return key; }
    public String getName() { return color + name; }
    public String getDescription() { return ChatColor.GRAY + description; }
    public Material getIcon() { return icon; }
    public ChatColor getColor() { return color; }

    public static Achievement fromKey(String key) {
        for (Achievement achievement : values()) {
            if (achievement.getKey().equals(key)) {
                return achievement;
            }
        }
        return null;
    }
}
