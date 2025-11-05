package com.lucidaps.xmas;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class AchievementManager {

    public static void checkAndUnlock(Player player, Achievement achievement) {
        PlayerStats stats = StatsManager.getPlayerStats(player.getUniqueId());
        
        if (stats.hasAchievement(achievement.getKey())) {
            return; // Already unlocked
        }

        boolean shouldUnlock = false;

        switch (achievement) {
            case FIRST_TREE:
                shouldUnlock = stats.getTotalTreesPlanted() >= 1;
                break;
            case MAX_LEVEL:
                shouldUnlock = stats.getFirstMagicTreeTimestamp() > 0;
                break;
            case GIFT_COLLECTOR:
                shouldUnlock = stats.getTotalGiftsCollected() >= 100;
                break;
            case LUCKY_STREAK:
                shouldUnlock = stats.getMaxConsecutiveGoodGifts() >= 10;
                break;
            case TREE_MASTER:
                shouldUnlock = stats.getCurrentTreeCount() >= 5;
                break;
        }

        if (shouldUnlock) {
            unlockAchievement(player, achievement, stats);
        }
    }

    private static void unlockAchievement(Player player, Achievement achievement, PlayerStats stats) {
        stats.unlockAchievement(achievement.getKey());
        StatsManager.savePlayerStats(stats);

        // Broadcast to player
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage(ChatColor.YELLOW + "           ✦ " + ChatColor.BOLD + "ACHIEVEMENT UNLOCKED" + ChatColor.YELLOW + " ✦");
        player.sendMessage("");
        player.sendMessage("           " + achievement.getName());
        player.sendMessage("           " + achievement.getDescription());
        player.sendMessage(ChatColor.GOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("");

        // Play sound
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);

        // Broadcast to server (optional)
        if (achievement == Achievement.MAX_LEVEL || achievement == Achievement.GIFT_COLLECTOR) {
            Bukkit.broadcastMessage(achievement.getColor() + player.getName() + ChatColor.YELLOW + " hat das Achievement " + achievement.getName() + ChatColor.YELLOW + " freigeschaltet!");
        }
    }

    public static void checkAllAchievements(Player player) {
        for (Achievement achievement : Achievement.values()) {
            checkAndUnlock(player, achievement);
        }
    }
}
