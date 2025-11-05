package com.lucidaps.xmas;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TreeGUI {

    public static void openTreeStatusGUI(Player player, MagicTree tree) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.DARK_GREEN + "🎄 Weihnachtsbaum Status");

        // Tree Info (Slot 4)
        ItemStack treeInfo = createTreeInfoItem(tree);
        gui.setItem(4, treeInfo);

        // Level Progress (Slot 13)
        ItemStack levelProgress = createLevelProgressItem(tree);
        gui.setItem(13, levelProgress);

        // Required Items (Slots 19-25)
        if (tree.getLevel().hasNext()) {
            int slot = 19;
            for (Map.Entry<Material, Integer> entry : tree.getLevelupRequirements().entrySet()) {
                ItemStack requiredItem = createRequiredItemStack(entry.getKey(), entry.getValue(), 
                    tree.getLevel().getLevelupRequirements().get(entry.getKey()));
                gui.setItem(slot++, requiredItem);
                if (slot > 25) break;
            }
        }

        // Statistics (Slots 36-44)
        PlayerStats stats = StatsManager.getPlayerStats(tree.getOwner());
        gui.setItem(36, createStatsItem("Gesammelte Geschenke", String.valueOf(stats.getTotalGiftsCollected()), Material.CHEST));
        gui.setItem(37, createStatsItem("Gute Geschenke", String.valueOf(stats.getGoodGiftsCollected()), Material.EMERALD));
        gui.setItem(38, createStatsItem("Schlechte Geschenke", String.valueOf(stats.getBadGiftsCollected()), Material.COAL));
        gui.setItem(39, createStatsItem("Gepflanzte Bäume", String.valueOf(stats.getTotalTreesPlanted()), Material.SPRUCE_SAPLING));
        gui.setItem(40, createStatsItem("Aktive Bäume", String.valueOf(stats.getCurrentTreeCount()), Material.SPRUCE_LOG));
        gui.setItem(41, createStatsItem("Glückssträhne", stats.getConsecutiveGoodGifts() + "/" + stats.getMaxConsecutiveGoodGifts(), Material.GOLD_INGOT));

        // Gift Timer (Slot 49)
        ItemStack giftTimer = createGiftTimerItem(tree);
        gui.setItem(49, giftTimer);

        // Fill empty slots with glass panes
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        
        for (int i = 0; i < 54; i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }

        player.openInventory(gui);
    }

    private static ItemStack createTreeInfoItem(MagicTree tree) {
        ItemStack item = new ItemStack(Material.SPRUCE_LOG);
        ItemMeta meta = item.getItemMeta();
        
        String levelName = tree.getLevel().getName().toUpperCase();
        ChatColor levelColor = ChatColor.GREEN;
        
        switch (levelName) {
            case "SAPLING":
                levelColor = ChatColor.GRAY;
                break;
            case "SMALL_TREE":
                levelColor = ChatColor.GREEN;
                break;
            case "TREE":
                levelColor = ChatColor.AQUA;
                break;
            case "MAGIC_TREE":
                levelColor = ChatColor.GOLD;
                break;
        }
        
        meta.setDisplayName(levelColor + "" + ChatColor.BOLD + "Dein Weihnachtsbaum");
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━━");
        lore.add(ChatColor.YELLOW + "Aktuelles Level: " + levelColor + levelName);
        lore.add("");
        
        if (tree.getLevel().hasNext()) {
            lore.add(ChatColor.GREEN + "✓ Nächstes Level verfügbar!");
        } else {
            lore.add(ChatColor.GOLD + "★ MAXIMALES LEVEL ERREICHT! ★");
        }
        
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━━");
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    private static ItemStack createLevelProgressItem(MagicTree tree) {
        ItemStack item;
        ItemMeta meta;
        
        if (!tree.getLevel().hasNext()) {
            item = new ItemStack(Material.GLOWSTONE);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "MAXIMALES LEVEL!");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Dein Baum ist komplett ausgewachsen!");
            lore.add(ChatColor.YELLOW + "Sammle weiter Geschenke!");
            meta.setLore(lore);
        } else {
            item = new ItemStack(Material.EXPERIENCE_BOTTLE);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Level-Fortschritt");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━━");
            
            int totalRequired = 0;
            int totalCollected = 0;
            
            Map<Material, Integer> originalReqs = tree.getLevel().getLevelupRequirements();
            Map<Material, Integer> currentReqs = tree.getLevelupRequirements();
            
            for (Map.Entry<Material, Integer> entry : originalReqs.entrySet()) {
                totalRequired += entry.getValue();
                int stillNeeded = currentReqs.getOrDefault(entry.getKey(), 0);
                totalCollected += (entry.getValue() - stillNeeded);
            }
            
            int percentage = totalRequired > 0 ? (totalCollected * 100 / totalRequired) : 100;
            
            lore.add(ChatColor.YELLOW + "Fortschritt: " + ChatColor.GREEN + percentage + "%");
            lore.add(createProgressBar(percentage));
            lore.add("");
            lore.add(ChatColor.GRAY + "Items gesammelt: " + ChatColor.WHITE + totalCollected + "/" + totalRequired);
            
            if (tree.canLevelUp()) {
                lore.add("");
                lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "✓ BEREIT ZUM AUFLEVELN!");
                lore.add(ChatColor.GRAY + "Rechtsklick auf den Baum!");
            }
            
            lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━━");
            meta.setLore(lore);
        }
        
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createRequiredItemStack(Material material, int stillNeeded, int originalAmount) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        int collected = originalAmount - stillNeeded;
        int percentage = (collected * 100 / originalAmount);
        
        ChatColor color = percentage == 100 ? ChatColor.GREEN : percentage >= 50 ? ChatColor.YELLOW : ChatColor.RED;
        
        meta.setDisplayName(color + "" + ChatColor.BOLD + material.name());
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━━");
        lore.add(ChatColor.YELLOW + "Fortschritt: " + color + percentage + "%");
        lore.add(createProgressBar(percentage));
        lore.add("");
        lore.add(ChatColor.GRAY + "Gesammelt: " + ChatColor.WHITE + collected + "/" + originalAmount);
        lore.add(ChatColor.GRAY + "Benötigt: " + ChatColor.RED + stillNeeded);
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━━");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    private static ItemStack createStatsItem(String name, String value, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + name);
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━━");
        lore.add(ChatColor.YELLOW + "Wert: " + ChatColor.WHITE + value);
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━━");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    private static ItemStack createGiftTimerItem(MagicTree tree) {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Nächstes Geschenk");
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━━");
        
        long presentCounter = tree.getPresentCounter();
        long delay = tree.getLevel().getGiftDelay();
        
        if (delay > 0 && presentCounter > 0) {
            int secondsLeft = (int) (presentCounter * Main.UPDATE_SPEED / 20);
            int minutes = secondsLeft / 60;
            int seconds = secondsLeft % 60;
            
            lore.add(ChatColor.YELLOW + "Zeit bis zum nächsten Geschenk:");
            lore.add(ChatColor.WHITE + "" + minutes + "m " + seconds + "s");
        } else if (delay > 0) {
            lore.add(ChatColor.GREEN + "Geschenk wird bald gespawnt!");
        } else {
            lore.add(ChatColor.RED + "Dieser Baum spawnt keine Geschenke");
        }
        
        lore.add(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━━");
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    private static String createProgressBar(int percentage) {
        int bars = 20;
        int filled = (percentage * bars) / 100;
        
        StringBuilder progressBar = new StringBuilder();
        progressBar.append(ChatColor.GRAY).append("[");
        
        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                progressBar.append(ChatColor.GREEN).append("█");
            } else {
                progressBar.append(ChatColor.DARK_GRAY).append("█");
            }
        }
        
        progressBar.append(ChatColor.GRAY).append("]");
        return progressBar.toString();
    }
}
