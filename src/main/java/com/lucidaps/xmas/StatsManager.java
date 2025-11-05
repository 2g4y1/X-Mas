package com.lucidaps.xmas;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StatsManager {
    private static final ConcurrentHashMap<UUID, PlayerStats> playerStatsMap = new ConcurrentHashMap<>();
    private static File statsFile;

    public static void initialize(File dataFolder) {
        if (dataFolder == null || !dataFolder.exists()) {
            dataFolder = new File("plugins/X-Mas");
            dataFolder.mkdirs();
        }
        statsFile = new File(dataFolder, "player_stats.yml");
        if (!statsFile.exists()) {
            try {
                statsFile.getParentFile().mkdirs();
                statsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadAllStats();
    }

    public static PlayerStats getPlayerStats(UUID playerUUID) {
        return playerStatsMap.computeIfAbsent(playerUUID, PlayerStats::new);
    }

    public static void savePlayerStats(PlayerStats stats) {
        playerStatsMap.put(stats.getPlayerUUID(), stats);
        saveStatsToFile(stats);
    }

    private static void saveStatsToFile(PlayerStats stats) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(statsFile);
        String path = "players." + stats.getPlayerUUID().toString();
        
        Map<String, Object> data = stats.serialize();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            config.set(path + "." + entry.getKey(), entry.getValue());
        }

        try {
            config.save(statsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAllStats() {
        if (statsFile == null) {
            return; // Plugin not properly initialized
        }
        
        FileConfiguration config = new YamlConfiguration();
        
        for (PlayerStats stats : playerStatsMap.values()) {
            String path = "players." + stats.getPlayerUUID().toString();
            Map<String, Object> data = stats.serialize();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                config.set(path + "." + entry.getKey(), entry.getValue());
            }
        }

        try {
            config.save(statsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadAllStats() {
        if (!statsFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(statsFile);
        if (config.getConfigurationSection("players") == null) return;

        for (String uuidString : config.getConfigurationSection("players").getKeys(false)) {
            try {
                String path = "players." + uuidString;
                Map<String, Object> data = new HashMap<>();
                
                for (String key : config.getConfigurationSection(path).getKeys(false)) {
                    data.put(key, config.get(path + "." + key));
                }
                
                PlayerStats stats = PlayerStats.deserialize(data);
                playerStatsMap.put(stats.getPlayerUUID(), stats);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<PlayerStats> getTopPlayersByTrees(int limit) {
        List<PlayerStats> statsList = new ArrayList<>(playerStatsMap.values());
        // Sort by currentTreeCount (active trees only), not totalTreesPlanted
        statsList.sort((a, b) -> Integer.compare(b.getCurrentTreeCount(), a.getCurrentTreeCount()));
        return statsList.subList(0, Math.min(limit, statsList.size()));
    }

    public static List<PlayerStats> getTopPlayersByGifts(int limit) {
        List<PlayerStats> statsList = new ArrayList<>(playerStatsMap.values());
        statsList.sort((a, b) -> Integer.compare(b.getTotalGiftsCollected(), a.getTotalGiftsCollected()));
        return statsList.subList(0, Math.min(limit, statsList.size()));
    }

    public static List<PlayerStats> getFastestToMagicTree(int limit) {
        List<PlayerStats> statsList = new ArrayList<>();
        for (PlayerStats stats : playerStatsMap.values()) {
            if (stats.getFirstMagicTreeTimestamp() > 0) {
                statsList.add(stats);
            }
        }
        statsList.sort((a, b) -> Long.compare(a.getFirstMagicTreeTimestamp(), b.getFirstMagicTreeTimestamp()));
        return statsList.subList(0, Math.min(limit, statsList.size()));
    }

    public static Collection<PlayerStats> getAllStats() {
        return new ArrayList<>(playerStatsMap.values());
    }
}
