package com.lucidaps.xmas;

import java.util.*;

public class PlayerStats {
    private final UUID playerUUID;
    private int totalTreesPlanted;
    private int currentTreeCount;
    private int totalGiftsCollected;
    private int goodGiftsCollected;
    private int badGiftsCollected;
    private int consecutiveGoodGifts;
    private int maxConsecutiveGoodGifts;
    private long firstTreeTimestamp;
    private long firstMagicTreeTimestamp;
    private long lastGiftTimestamp;
    private boolean hasReceivedChristmasLegendary;
    private boolean hasClaimedCrystal;
    private final Set<String> unlockedAchievements;

    public PlayerStats(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.totalTreesPlanted = 0;
        this.currentTreeCount = 0;
        this.totalGiftsCollected = 0;
        this.goodGiftsCollected = 0;
        this.badGiftsCollected = 0;
        this.consecutiveGoodGifts = 0;
        this.maxConsecutiveGoodGifts = 0;
        this.firstTreeTimestamp = 0;
        this.firstMagicTreeTimestamp = 0;
        this.lastGiftTimestamp = 0;
        this.hasReceivedChristmasLegendary = false;
        this.hasClaimedCrystal = false;
        this.unlockedAchievements = new HashSet<>();
    }

    // Getters
    public UUID getPlayerUUID() { return playerUUID; }
    public int getTotalTreesPlanted() { return totalTreesPlanted; }
    public int getCurrentTreeCount() { return currentTreeCount; }
    public int getTotalGiftsCollected() { return totalGiftsCollected; }
    public int getGoodGiftsCollected() { return goodGiftsCollected; }
    public int getBadGiftsCollected() { return badGiftsCollected; }
    public int getConsecutiveGoodGifts() { return consecutiveGoodGifts; }
    public int getMaxConsecutiveGoodGifts() { return maxConsecutiveGoodGifts; }
    public long getFirstTreeTimestamp() { return firstTreeTimestamp; }
    public long getFirstMagicTreeTimestamp() { return firstMagicTreeTimestamp; }
    public long getLastGiftTimestamp() { return lastGiftTimestamp; }
    public boolean hasReceivedChristmasLegendary() { return hasReceivedChristmasLegendary; }
    public boolean hasClaimedCrystal() { return hasClaimedCrystal; }
    public Set<String> getUnlockedAchievements() { return new HashSet<>(unlockedAchievements); }

    // Setters
    public void setTotalTreesPlanted(int count) { this.totalTreesPlanted = count; }
    public void setCurrentTreeCount(int count) { this.currentTreeCount = count; }
    public void setTotalGiftsCollected(int count) { this.totalGiftsCollected = count; }
    public void setGoodGiftsCollected(int count) { this.goodGiftsCollected = count; }
    public void setBadGiftsCollected(int count) { this.badGiftsCollected = count; }
    public void setConsecutiveGoodGifts(int count) { this.consecutiveGoodGifts = count; }
    public void setMaxConsecutiveGoodGifts(int count) { this.maxConsecutiveGoodGifts = count; }
    public void setFirstTreeTimestamp(long timestamp) { this.firstTreeTimestamp = timestamp; }
    public void setFirstMagicTreeTimestamp(long timestamp) { this.firstMagicTreeTimestamp = timestamp; }
    public void setLastGiftTimestamp(long timestamp) { this.lastGiftTimestamp = timestamp; }
    public void setHasReceivedChristmasLegendary(boolean received) { this.hasReceivedChristmasLegendary = received; }
    public void setHasClaimedCrystal(boolean claimed) { this.hasClaimedCrystal = claimed; }

    // Actions
    public void incrementTreesPlanted() {
        this.totalTreesPlanted++;
        this.currentTreeCount++;
        if (firstTreeTimestamp == 0) {
            firstTreeTimestamp = System.currentTimeMillis();
        }
    }

    public void decrementCurrentTreeCount() {
        if (this.currentTreeCount > 0) {
            this.currentTreeCount--;
        }
    }

    public void recordGoodGift() {
        this.totalGiftsCollected++;
        this.goodGiftsCollected++;
        this.consecutiveGoodGifts++;
        this.lastGiftTimestamp = System.currentTimeMillis();
        if (this.consecutiveGoodGifts > this.maxConsecutiveGoodGifts) {
            this.maxConsecutiveGoodGifts = this.consecutiveGoodGifts;
        }
    }

    public void recordBadGift() {
        this.totalGiftsCollected++;
        this.badGiftsCollected++;
        this.consecutiveGoodGifts = 0;
        this.lastGiftTimestamp = System.currentTimeMillis();
    }

    public void recordMagicTreeReached() {
        if (firstMagicTreeTimestamp == 0) {
            firstMagicTreeTimestamp = System.currentTimeMillis();
        }
    }

    public void unlockAchievement(String achievementKey) {
        unlockedAchievements.add(achievementKey);
    }

    public boolean hasAchievement(String achievementKey) {
        return unlockedAchievements.contains(achievementKey);
    }

    // Serialization
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("uuid", playerUUID.toString());
        data.put("totalTreesPlanted", totalTreesPlanted);
        data.put("currentTreeCount", currentTreeCount);
        data.put("totalGiftsCollected", totalGiftsCollected);
        data.put("goodGiftsCollected", goodGiftsCollected);
        data.put("badGiftsCollected", badGiftsCollected);
        data.put("consecutiveGoodGifts", consecutiveGoodGifts);
        data.put("maxConsecutiveGoodGifts", maxConsecutiveGoodGifts);
        data.put("firstTreeTimestamp", firstTreeTimestamp);
        data.put("firstMagicTreeTimestamp", firstMagicTreeTimestamp);
        data.put("lastGiftTimestamp", lastGiftTimestamp);
        data.put("hasReceivedChristmasLegendary", hasReceivedChristmasLegendary);
        data.put("hasClaimedCrystal", hasClaimedCrystal);
        data.put("achievements", new ArrayList<>(unlockedAchievements));
        return data;
    }

    public static PlayerStats deserialize(Map<String, Object> data) {
        UUID uuid = UUID.fromString((String) data.get("uuid"));
        PlayerStats stats = new PlayerStats(uuid);
        
        stats.setTotalTreesPlanted((int) data.getOrDefault("totalTreesPlanted", 0));
        stats.setCurrentTreeCount((int) data.getOrDefault("currentTreeCount", 0));
        stats.setTotalGiftsCollected((int) data.getOrDefault("totalGiftsCollected", 0));
        stats.setGoodGiftsCollected((int) data.getOrDefault("goodGiftsCollected", 0));
        stats.setBadGiftsCollected((int) data.getOrDefault("badGiftsCollected", 0));
        stats.setConsecutiveGoodGifts((int) data.getOrDefault("consecutiveGoodGifts", 0));
        stats.setMaxConsecutiveGoodGifts((int) data.getOrDefault("maxConsecutiveGoodGifts", 0));
        
        // Safe conversion from Number to long (handles both Integer and Long)
        Object firstTreeTime = data.getOrDefault("firstTreeTimestamp", 0L);
        stats.setFirstTreeTimestamp(firstTreeTime instanceof Number ? ((Number) firstTreeTime).longValue() : 0L);
        
        Object firstMagicTime = data.getOrDefault("firstMagicTreeTimestamp", 0L);
        stats.setFirstMagicTreeTimestamp(firstMagicTime instanceof Number ? ((Number) firstMagicTime).longValue() : 0L);
        
        Object lastGiftTime = data.getOrDefault("lastGiftTimestamp", 0L);
        stats.setLastGiftTimestamp(lastGiftTime instanceof Number ? ((Number) lastGiftTime).longValue() : 0L);
        
        stats.setHasReceivedChristmasLegendary((boolean) data.getOrDefault("hasReceivedChristmasLegendary", false));
        stats.setHasClaimedCrystal((boolean) data.getOrDefault("hasClaimedCrystal", false));
        
        @SuppressWarnings("unchecked")
        List<String> achievements = (List<String>) data.getOrDefault("achievements", new ArrayList<>());
        achievements.forEach(stats::unlockAchievement);
        
        return stats;
    }
}
