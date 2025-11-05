package com.lucidaps.xmas;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.*;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.Vector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class MagicTree {
    private static final ConcurrentHashMap<Block, UUID> blockAssociation = new ConcurrentHashMap<>();
    private final UUID owner;
    private final Location location;
    private final UUID treeuid;
    TreeLevel level;
    private Map<Material, Integer> levelupRequirements;
    private Set<Block> blocks;
    private long presentCounter;
    private int scheduledPresents;

    public MagicTree(UUID owner, TreeLevel level, Location location) {
        this.treeuid = UUID.randomUUID();
        this.owner = owner;
        this.level = level;
        this.location = location;
        this.levelupRequirements = new HashMap<>(level.getLevelupRequirements());
        if (Main.inProgress)
            build();
        presentCounter = 0;
        scheduledPresents = 0;
    }

    public MagicTree(UUID owner, UUID uid, TreeLevel level, Location location, Map<Material, Integer> levelupRequirements,
                     long presentCounter, int scheduledPresents) {
        this.owner = owner;
        this.treeuid = uid;
        this.level = level;
        this.location = location;
        this.levelupRequirements = new HashMap<>(levelupRequirements);
        this.presentCounter = 0;
        this.presentCounter = presentCounter;
        if (Main.inProgress)
            build();
        this.scheduledPresents = scheduledPresents;
    }

    public static MagicTree getTreeByBlock(Block block) {
        return XMas.getTree(blockAssociation.get(block));
    }

    public static boolean isBlockBelongs(Block block) {
        return blockAssociation.containsKey(block);
    }

    public UUID getOwner() {
        return owner;
    }

    public Player getPlayerOwner() {
        if (Bukkit.getPlayer(owner) != null) {
            return Bukkit.getPlayer(owner);
        }
        return null;
    }

    public TreeLevel getLevel() {
        return level;
    }

    public Location getLocation() {
        return location;
    }

    public Map<Material, Integer> getLevelupRequirements() {
        return levelupRequirements;
    }

    public boolean grow(Material material) {
        if (levelupRequirements.containsKey(material)) {
            if (levelupRequirements.get(material) <= 1) {
                levelupRequirements.remove(material);
            } else {
                levelupRequirements.put(material, levelupRequirements.get(material) - 1);
            }
            for (Block block : blocks) {
                if (block.getType() == Material.SPRUCE_LEAVES || block.getType() == Material.SPRUCE_SAPLING) {
                    Effects.GROW.playEffect(block.getLocation());
                    for (int i = 0; i <= 3; i++)
                        location.getWorld().playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1, Main.RANDOM.nextFloat() + 0.2f);
                }
            }
            save();
            return true;
        }
        return false;
    }

    public void update() {
        if (Main.inProgress) {
            if (level.getGiftDelay() > 0) {
                if (presentCounter == 0) {
                    spawnPresent();
                    
                    // Check if it's Christmas (24-25 December) for 2x spawn rate
                    Calendar cal = Calendar.getInstance();
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int month = cal.get(Calendar.MONTH);
                    boolean isChristmas = (month == Calendar.DECEMBER && (day == 24 || day == 25));
                    
                    // Christmas bonus: 2x spawn rate (half the delay)
                    long delay = isChristmas ? level.getGiftDelay() / 2 : level.getGiftDelay();
                    
                    presentCounter = (long) ((delay * 1.25 - delay * 0.75) + delay * 0.75);
                }
                presentCounter--;
            }
        }
    }

    public void playParticles()
    {
        if (blocks != null && blocks.size() > 0) {
            for (Block block : blocks) {
                if(!block.getWorld().isChunkLoaded(block.getX() / 16, block.getZ() / 16))
                    continue;
                if (block.getType() == Material.SPRUCE_LEAVES) {
                    if (level.getSwagEffect() != null) {
                        level.getSwagEffect().playEffect(block.getLocation());
                    }
                }
                if (block.getType() == Material.SPRUCE_LOG) {
                    if (level.getBodyEffect() != null) {
                        level.getBodyEffect().playEffect(block.getLocation());
                    }
                }
                if (level.getAmbientEffect() != null) {
                    level.getAmbientEffect().playEffect(location.clone().add(0, level.getTreeHeight(), 0));
                }
            }
        }
    }

    public boolean tryLevelUp() {

        if (level.hasNext()) {
            // Check if trying to level up to MAGIC_TREE before December 20
            if (level.nextLevel == TreeLevel.MAGIC_TREE) {
                Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH); // 0-11, DECEMBER = 11
                
                // Block MAGIC_TREE before December 20 of current year
                // Allow after December 20 OR in subsequent years
                boolean beforeDecember = (month < Calendar.DECEMBER);
                boolean earlyDecember = (month == Calendar.DECEMBER && day < 20);
                
                if (beforeDecember || earlyDecember) {
                    Player player = getPlayerOwner();
                    if (player != null) {
                        player.sendMessage(ChatColor.GOLD + "🎄 Der MAGIC_TREE ist ab dem 20. Dezember verfügbar! 🎄");
                    }
                    return false;
                }
            }
            
            if (level.nextLevel.getStructureTemplate().canGrow(location)) {
                levelUp();
                return true;
            }
        }

        return false;
    }

    private void levelUp() {
        unbuild();
        this.level = level.nextLevel;
        this.levelupRequirements = new HashMap<>(level.getLevelupRequirements());
        for (int i = 0; i <= 3; i++) {
            Firework fw = location.getWorld().spawn(location.clone().add(new Vector(-3 + Main.RANDOM.nextInt(6), 3, -3 + Main.RANDOM.nextInt(6))), Firework.class);
            FireworkMeta meta = fw.getFireworkMeta();
            meta.addEffect(FireworkEffect.builder().trail(true).withColor(Color.RED).withFade(Color.LIME).withFlicker().with(Type.BURST).build());
            fw.setFireworkMeta(meta);
            fw.setMetadata("nodamage", new FixedMetadataValue(Main.getInstance(), true));
        }
        build();
        save();
        
        // Check if reached MAGIC_TREE level
        if (this.level == TreeLevel.MAGIC_TREE) {
            PlayerStats stats = StatsManager.getPlayerStats(owner);
            stats.recordMagicTreeReached();
            StatsManager.savePlayerStats(stats);
            
            // Check achievement
            Player player = getPlayerOwner();
            if (player != null) {
                AchievementManager.checkAndUnlock(player, Achievement.MAX_LEVEL);
            }
        }
    }

    public void unbuild() {
        Block block;
        Location loc;
        for (Entry<Block, UUID> cBlock : blockAssociation.entrySet()) {
            if (cBlock.getValue().equals(treeuid)) {
                block = cBlock.getKey();
                loc = block.getLocation();
                loc.getWorld().playEffect(loc, Effect.STEP_SOUND, block.getType());
                block.setType(Material.AIR);
                blockAssociation.remove(block);
            }
        }
        location.clone().add(0, -1, 0).getBlock().setType(Material.GRASS_BLOCK);
    }

    public void build() {
        if (level.getStructureTemplate().canGrow(location)) {
            blocks = level.getStructureTemplate().build(location);
            for (Block block : blocks) {
                blockAssociation.put(block, getTreeUID());
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void spawnPresent() {
        if (!location.getWorld().isChunkLoaded((int) location.getX() / 16, (int) location.getZ() / 16)) {
            if (scheduledPresents + 1 <= 8)
                scheduledPresents++;
            return;
        }

        Location presentLoc = location.clone().add(-1 + Main.RANDOM.nextInt(3), 0, -1 + Main.RANDOM.nextInt(3));
        Block pBlock = presentLoc.getBlock();

        if (!pBlock.getType().isSolid() && pBlock.getType() != Material.SPRUCE_SAPLING) {
            pBlock.setType(Material.PLAYER_HEAD);
            BlockState state = pBlock.getState();

            if (state instanceof Skull) {
                Skull skull = (Skull) state;
                Rotatable skullRotatable = (Rotatable) skull.getBlockData();
                BlockFace face;

                do {
                    face = BlockFace.values()[Main.RANDOM.nextInt(BlockFace.values().length)];
                } while (face == BlockFace.DOWN || face == BlockFace.UP || face == BlockFace.SELF);

                skullRotatable.setRotation(face);

                String textureUrl = Main.getHeads().get(Main.RANDOM.nextInt(Main.getHeads().size()));
                PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
                PlayerTextures textures = profile.getTextures();
                try {
                    textures.setSkin(new URL(textureUrl));
                    profile.setTextures(textures);
                    skull.setPlayerProfile(profile);
                    skull.update(true);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Bukkit.getLogger().warning("[X-Mas] Invalid texture URL: " + textureUrl);
                }
            }
        }
    }

    public boolean canLevelUp() {
        return getLevelupRequirements().size() == 0;
    }

    public UUID getTreeUID() {
        return treeuid;
    }

    public void save() {
        TreeSerializer.saveTree(this);
    }

    public void end() {
        unbuild();
        // Bad code. Need it fast.
        Block bl;
        if ((bl = location.clone().add(1, 0, 0).getBlock()).getType() == Material.PLAYER_HEAD)
            bl.setType(Material.AIR);
        if ((bl = location.clone().add(-1, 0, 0).getBlock()).getType() == Material.PLAYER_HEAD)
            bl.setType(Material.AIR);
        if ((bl = location.clone().add(0, 0, 1).getBlock()).getType() == Material.PLAYER_HEAD)
            bl.setType(Material.AIR);
        if ((bl = location.clone().add(0, 0, -1).getBlock()).getType() == Material.PLAYER_HEAD)
            bl.setType(Material.AIR);

        if ((bl = location.clone().add(1, 0, 1).getBlock()).getType() == Material.PLAYER_HEAD)
            bl.setType(Material.AIR);
        if ((bl = location.clone().add(-1, 0, -1).getBlock()).getType() == Material.PLAYER_HEAD)
            bl.setType(Material.AIR);

        if ((bl = location.clone().add(-1, 0, 1).getBlock()).getType() == Material.PLAYER_HEAD)
            bl.setType(Material.AIR);
        if ((bl = location.clone().add(1, 0, -1).getBlock()).getType() == Material.PLAYER_HEAD)
            bl.setType(Material.AIR);
        if (Main.resourceBack) {
            bl = location.getBlock();
            bl.setType(Material.CHEST);
            Chest chest = (Chest) bl.getState();
            Inventory inv = chest.getInventory();

            // Return invested upgrade materials only (no bonus items)
            TreeLevel cLevel = TreeLevel.SAPLING;
            while (cLevel != level) {
                if (cLevel.getLevelupRequirements() != null && cLevel.getLevelupRequirements().size() > 0) {
                    for (Entry<Material, Integer> currItem : cLevel.getLevelupRequirements().entrySet()) {
                        inv.addItem(new ItemStack(currItem.getKey(), currItem.getValue()));
                    }
                }

                if (cLevel.nextLevel == null)
                    break;
                cLevel = cLevel.nextLevel;
            }

            int count = 0;
            for (Entry<Material, Integer> currItem : level.getLevelupRequirements().entrySet()) {
                if (getLevelupRequirements().containsKey(currItem.getKey()))
                    count = getLevelupRequirements().get(currItem.getKey());
                if (currItem.getValue() - count > 0)
                    inv.addItem(new ItemStack(currItem.getKey(), currItem.getValue() - count));
                count = 0;
            }
        }
        XMas.removeTree(this);
    }

    public long getPresentCounter() {
        return presentCounter;
    }

    public int getScheduledPresents() {
        return scheduledPresents;
    }

    public boolean hasScheduledPresents() {
        return scheduledPresents > 0;
    }

    public void spawnScheduledPresents() {
        for(int i = scheduledPresents; i > 0; i--)
            spawnPresent();
        scheduledPresents = 0;
    }
}
