package com.lucidaps.xmas;

import org.bukkit.Chunk;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.profile.PlayerTextures;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.jetbrains.annotations.Nullable;
import com.lucidaps.xmas.utils.LocationUtils;
import com.lucidaps.xmas.utils.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.lucidaps.xmas.Main.RANDOM;

class XMas {

    private static final ConcurrentHashMap<UUID, MagicTree> trees = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, List<MagicTree>> trees_byChunk = new ConcurrentHashMap<>();
    public static ItemStack XMAS_CRYSTAL;

    public static void createMagicTree(Player player, Location loc) {
        MagicTree tree = new MagicTree(player.getUniqueId(), TreeLevel.SAPLING, loc);
        trees.put(tree.getTreeUID(), tree);
        trees_byChunk.computeIfAbsent(LocationUtils.getChunkKey(tree.getLocation()), aLong -> new ArrayList<>()).add(tree);
        tree.save();
        
        // Track stats
        PlayerStats stats = StatsManager.getPlayerStats(player.getUniqueId());
        stats.incrementTreesPlanted();
        StatsManager.savePlayerStats(stats);
        
        // Check achievements
        AchievementManager.checkAndUnlock(player, Achievement.FIRST_TREE);
        AchievementManager.checkAndUnlock(player, Achievement.TREE_MASTER);
    }

    public static void addMagicTree(MagicTree tree) {
        trees.put(tree.getTreeUID(), tree);
        tree.build();
    }

    public static Collection<MagicTree> getAllTrees() {
        return trees.values();
    }

    @Nullable
    public static Collection<MagicTree> getAllTreesInChunk(Chunk chunk) {
        return trees_byChunk.get(LocationUtils.getChunkKey(chunk));
    }

    public static void removeTree(MagicTree tree) {
        tree.unbuild();
        TreeSerializer.removeTree(tree);
        trees.remove(tree.getTreeUID());
        trees_byChunk.remove(LocationUtils.getChunkKey(tree.getLocation()));
        
        // Update stats
        PlayerStats stats = StatsManager.getPlayerStats(tree.getOwner());
        stats.decrementCurrentTreeCount();
        StatsManager.savePlayerStats(stats);
    }

    public static void processPresent(Block block, Player player) {
        if (block.getType() == Material.PLAYER_HEAD) {
            Skull skull = (Skull) block.getState();
            PlayerProfile profile = skull.getPlayerProfile();

            if (profile != null) {
                PlayerTextures textures = profile.getTextures();
                if (textures != null && textures.getSkin() != null) {
                    String skinUrl = textures.getSkin().toString();

                    if (Main.getHeads().contains(skinUrl)) {
                        Location loc = block.getLocation();
                        World world = loc.getWorld();
                        if (world != null) {
                            PlayerStats stats = StatsManager.getPlayerStats(player.getUniqueId());
                            
                            // Check cooldown: max 1 gift per 3 hours
                            long currentTime = System.currentTimeMillis();
                            long lastGiftTime = stats.getLastGiftTimestamp();
                            long cooldownMillis = 3 * 60 * 60 * 1000; // 3 hours in milliseconds
                            
                            if (lastGiftTime > 0 && (currentTime - lastGiftTime) < cooldownMillis) {
                                long remainingMinutes = (cooldownMillis - (currentTime - lastGiftTime)) / 60000;
                                long remainingHours = remainingMinutes / 60;
                                long remainingMins = remainingMinutes % 60;
                                player.sendMessage(ChatColor.RED + "⏰ Du kannst nur alle 3 Stunden ein Geschenk öffnen! Warte noch " 
                                    + remainingHours + "h " + remainingMins + "min.");
                                // Don't remove the present, let them try again later
                                return;
                            }
                            
                            ItemStack gift;
                            boolean isGoodGift;
                            
                            // Check dates
                            Calendar cal = Calendar.getInstance();
                            int day = cal.get(Calendar.DAY_OF_MONTH);
                            int month = cal.get(Calendar.MONTH);
                            boolean isChristmas = (month == Calendar.DECEMBER && (day == 24 || day == 25));
                            boolean isBeforeDecember10 = (month < Calendar.DECEMBER) || (month == Calendar.DECEMBER && day < 10);
                            
                            // Guaranteed legendary on Christmas if not received yet
                            if (isChristmas && !stats.hasReceivedChristmasLegendary()) {
                                gift = GiftManager.getChristmasLegendaryGift(RANDOM);
                                isGoodGift = true;
                                stats.setHasReceivedChristmasLegendary(true);
                                player.sendMessage(ChatColor.GOLD + "🎄 " + ChatColor.BOLD + "FROHE WEIHNACHTEN!" + ChatColor.GOLD + " Du erhältst ein legendäres Geschenk! 🎁");
                            } else {
                                // 2x spawn rate on Christmas (handled in MagicTree)
                                // Normal weighted gift system
                                if (RANDOM.nextFloat() < Main.LUCK_CHANCE || !Main.LUCK_CHANCE_ENABLED) {
                                    gift = GiftManager.getRandomWeightedGift(RANDOM);
                                    isGoodGift = GiftManager.isGoodGift(gift);
                                    
                                    // Before December 10: halve item amounts
                                    if (isBeforeDecember10 && gift != null) {
                                        int currentAmount = gift.getAmount();
                                        int halvedAmount = Math.max(1, currentAmount / 2);
                                        gift.setAmount(halvedAmount);
                                    }
                                    
                                    if (isGoodGift) {
                                        stats.recordGoodGift();
                                        GiftRarity rarity = GiftManager.determineRarity(gift);
                                        Effects.TREE_SWAG.playEffect(loc);
                                        
                                        String message = ChatColor.GREEN + "Du hast ein " + rarity.getDisplayName() + ChatColor.GREEN + " Geschenk erhalten!";
                                        if (isBeforeDecember10) {
                                            message += ChatColor.YELLOW + " (Halbe Menge vor dem 10. Dezember)";
                                        }
                                        TextUtils.sendMessage(player, message);
                                        
                                        // Check achievements
                                        AchievementManager.checkAndUnlock(player, Achievement.LUCKY_STREAK);
                                        AchievementManager.checkAndUnlock(player, Achievement.GIFT_COLLECTOR);
                                    } else {
                                        stats.recordBadGift();
                                        Effects.SMOKE.playEffect(loc);
                                        TextUtils.sendMessage(player, LocaleManager.GIFT_FAIL);
                                    }
                                } else {
                                    gift = new ItemStack(Material.COAL);
                                    isGoodGift = false;
                                    stats.recordBadGift();
                                    Effects.SMOKE.playEffect(loc);
                                    TextUtils.sendMessage(player, LocaleManager.GIFT_FAIL);
                                }
                            }
                            
                            // Remove block AFTER processing to prevent exploits
                            block.setType(Material.AIR);
                            world.dropItemNaturally(loc, gift);
                            StatsManager.savePlayerStats(stats);
                        }
                    }
                }
            }
        }
    }

    public static List<MagicTree> getTreesPlayerOwn(Player player) {
        List<MagicTree> own = new ArrayList<>();
        for (MagicTree cTree : getAllTrees())
            if (cTree.getOwner().equals(player.getUniqueId()))
                own.add(cTree);
        return own;
    }

    public static MagicTree getTree(UUID treeUID) {
        return trees.get(treeUID);
    }
}
