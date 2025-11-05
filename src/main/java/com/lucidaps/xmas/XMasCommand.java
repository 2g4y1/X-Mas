package com.lucidaps.xmas;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.bukkit.ChatColor.*;

public class XMasCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    private XMasCommand(Main plugin) {
        this.plugin = plugin;
    }

    public static void register(Main plugin) {
        plugin.getCommand("xmas").setExecutor(new XMasCommand(plugin));
        plugin.getCommand("xmas").setTabCompleter(new XMasCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0) {
            String action = args[0].toLowerCase();
            switch (action) {
                case "help":
                    sendHelpMessage(sender);
                    break;
                case "give":
                    if (!sender.hasPermission("xmas.admin")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                        return true;
                    }
                    if (args.length > 1) {
                        String name = args[1];
                        Player player = Bukkit.getPlayer(name);
                        if (player != null) {
                            player.getInventory().addItem(XMas.XMAS_CRYSTAL);
                            sender.sendMessage(ChatColor.GREEN + "X-Mas Crystal gegeben an " + player.getName());
                        } else {
                            sender.sendMessage(LocaleManager.COMMAND_PLAYER_OFFLINE);
                        }
                    } else {
                        sender.sendMessage(LocaleManager.COMMAND_NO_PLAYER_NAME);
                    }
                    break;
                case "end":
                    if (!sender.hasPermission("xmas.admin")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                        return true;
                    }
                    plugin.end();
                    break;
                case "gifts":
                    if (!sender.hasPermission("xmas.admin")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                        return true;
                    }
                    Random random = new Random();
                    for (MagicTree magicTree : XMas.getAllTrees()) {
                        for (int i = 0; i < 3 + random.nextInt(4); i++) {
                            magicTree.spawnPresent();
                        }
                    }
                    Bukkit.broadcastMessage(LocaleManager.COMMAND_GIVEAWAY);
                    break;
                case "reload":
                    if (sender.hasPermission("xmas.admin")) {
                        plugin.reloadPluginConfig();
                        sender.sendMessage(ChatColor.GREEN + "X-Mas configuration reloaded!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    }
                    break;
                case "addhand":
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
                        return true;
                    }
                    if (!sender.hasPermission("xmas.admin")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                        return true;
                    }
                    Player player = (Player) sender;
                    ItemStack item = player.getInventory().getItemInMainHand();

                    if (item == null || item.getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You must hold an item in your main hand to add it as a gift.");
                        return true;
                    }

                    plugin.addGiftItem(item);
                    player.sendMessage(ChatColor.GREEN + "The item in your hand has been added to the gift list!");
                    break;
                case "stats":
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
                        return true;
                    }
                    sendPlayerStats((Player) sender);
                    break;
                case "top":
                    if (args.length > 1) {
                        String category = args[1].toLowerCase();
                        sendTopPlayers(sender, category);
                    } else {
                        sendTopPlayers(sender, "trees");
                    }
                    break;
                case "achievements":
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
                        return true;
                    }
                    sendAchievements((Player) sender);
                    break;
                case "gui":
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
                        return true;
                    }
                    Player p = (Player) sender;
                    List<MagicTree> trees = XMas.getTreesPlayerOwn(p);
                    if (trees.isEmpty()) {
                        p.sendMessage(ChatColor.RED + "Du hast keine Weihnachtsbäume!");
                        return true;
                    }
                    TreeGUI.openTreeStatusGUI(p, trees.get(0));
                    break;
                default:
                    return false;
            }
        } else {
            sendStatus(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        // Base commands for all players
        List<String> baseCommands = Arrays.asList("help", "stats", "top", "achievements", "gui");
        
        // Admin commands
        List<String> adminCommands = Arrays.asList("give", "end", "gifts", "reload", "addhand");
        
        List<String> allCommands = new ArrayList<>(baseCommands);
        if (sender.hasPermission("xmas.admin")) {
            allCommands.addAll(adminCommands);
        }

        if (args.length == 1) {
            for (String subCommand : allCommands) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give") && sender.hasPermission("xmas.admin")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(player.getName());
                    }
                }
            } else if (args[0].equalsIgnoreCase("top")) {
                List<String> categories = Arrays.asList("trees", "gifts", "fastest");
                for (String cat : categories) {
                    if (cat.startsWith(args[1].toLowerCase())) {
                        completions.add(cat);
                    }
                }
            }
        }

        return completions;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬ " + ChatColor.RED + "X-Mas Commands" + ChatColor.GOLD + " ▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sender.sendMessage(ChatColor.YELLOW + "/xmas" + ChatColor.GRAY + " - Plugin Status");
        sender.sendMessage(ChatColor.YELLOW + "/xmas help" + ChatColor.GRAY + " - Zeige diese Hilfe");
        sender.sendMessage(ChatColor.YELLOW + "/xmas gui" + ChatColor.GRAY + " - Öffne Baum-Status GUI");
        sender.sendMessage(ChatColor.YELLOW + "/xmas stats" + ChatColor.GRAY + " - Deine Statistiken");
        sender.sendMessage(ChatColor.YELLOW + "/xmas top [trees|gifts|fastest]" + ChatColor.GRAY + " - Top 10 Rangliste");
        sender.sendMessage(ChatColor.YELLOW + "/xmas achievements" + ChatColor.GRAY + " - Deine Achievements");
        if (sender.hasPermission("xmas.admin")) {
            sender.sendMessage(ChatColor.RED + "Admin Commands:");
            sender.sendMessage(ChatColor.YELLOW + "/xmas give <player>" + ChatColor.GRAY + " - Gebe X-Mas Crystal");
            sender.sendMessage(ChatColor.YELLOW + "/xmas addhand" + ChatColor.GRAY + " - Füge Item als Geschenk hinzu");
            sender.sendMessage(ChatColor.YELLOW + "/xmas reload" + ChatColor.GRAY + " - Config neu laden");
            sender.sendMessage(ChatColor.YELLOW + "/xmas end" + ChatColor.GRAY + " - Event beenden");
            sender.sendMessage(ChatColor.YELLOW + "/xmas gifts" + ChatColor.GRAY + " - Spawne Geschenke für alle");
        }
        sender.sendMessage(ChatColor.GOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private void sendPlayerStats(Player player) {
        PlayerStats stats = StatsManager.getPlayerStats(player.getUniqueId());
        
        player.sendMessage(ChatColor.GOLD + "▬▬▬▬▬▬▬ " + ChatColor.GREEN + "Deine Statistiken" + ChatColor.GOLD + " ▬▬▬▬▬▬▬");
        player.sendMessage(ChatColor.YELLOW + "Gepflanzte Bäume: " + ChatColor.WHITE + stats.getTotalTreesPlanted());
        player.sendMessage(ChatColor.YELLOW + "Aktive Bäume: " + ChatColor.WHITE + stats.getCurrentTreeCount());
        player.sendMessage(ChatColor.YELLOW + "Geschenke gesammelt: " + ChatColor.WHITE + stats.getTotalGiftsCollected());
        player.sendMessage(ChatColor.YELLOW + "  ➥ Gute: " + ChatColor.GREEN + stats.getGoodGiftsCollected());
        player.sendMessage(ChatColor.YELLOW + "  ➥ Schlechte: " + ChatColor.RED + stats.getBadGiftsCollected());
        player.sendMessage(ChatColor.YELLOW + "Glückssträhne: " + ChatColor.AQUA + stats.getConsecutiveGoodGifts() + " " + ChatColor.GRAY + "(Max: " + stats.getMaxConsecutiveGoodGifts() + ")");
        player.sendMessage(ChatColor.YELLOW + "Achievements: " + ChatColor.WHITE + stats.getUnlockedAchievements().size() + "/5");
        player.sendMessage(ChatColor.GOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private void sendTopPlayers(CommandSender sender, String category) {
        List<PlayerStats> topPlayers;
        String title;
        
        switch (category.toLowerCase()) {
            case "gifts":
                topPlayers = StatsManager.getTopPlayersByGifts(10);
                title = "Top 10 - Meiste Geschenke";
                break;
            case "fastest":
                topPlayers = StatsManager.getFastestToMagicTree(10);
                title = "Top 10 - Schnellste zu MAGIC_TREE";
                break;
            case "trees":
            default:
                topPlayers = StatsManager.getTopPlayersByTrees(10);
                title = "Top 10 - Meiste aktive Bäume";
                break;
        }
        
        sender.sendMessage(ChatColor.GOLD + "▬▬▬▬▬▬▬ " + ChatColor.GREEN + title + ChatColor.GOLD + " ▬▬▬▬▬▬▬");
        
        if (topPlayers.isEmpty()) {
            sender.sendMessage(ChatColor.GRAY + "Keine Daten verfügbar");
        } else {
            int rank = 1;
            for (PlayerStats stats : topPlayers) {
                String playerName = Bukkit.getOfflinePlayer(stats.getPlayerUUID()).getName();
                if (playerName == null) playerName = "Unknown";
                
                String value;
                switch (category.toLowerCase()) {
                    case "gifts":
                        value = stats.getTotalGiftsCollected() + " Geschenke";
                        break;
                    case "fastest":
                        long timeTaken = stats.getFirstMagicTreeTimestamp() - stats.getFirstTreeTimestamp();
                        long minutes = timeTaken / (1000 * 60);
                        value = minutes + " Minuten";
                        break;
                    default:
                        value = stats.getCurrentTreeCount() + " aktive Bäume";
                        break;
                }
                
                ChatColor rankColor = rank == 1 ? ChatColor.GOLD : rank == 2 ? ChatColor.YELLOW : rank == 3 ? ChatColor.GRAY : ChatColor.WHITE;
                sender.sendMessage(rankColor + "#" + rank + " " + ChatColor.WHITE + playerName + ChatColor.GRAY + " - " + ChatColor.GREEN + value);
                rank++;
            }
        }
        
        sender.sendMessage(ChatColor.GOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private void sendAchievements(Player player) {
        PlayerStats stats = StatsManager.getPlayerStats(player.getUniqueId());
        
        player.sendMessage(ChatColor.GOLD + "▬▬▬▬▬▬▬ " + ChatColor.GREEN + "Deine Achievements" + ChatColor.GOLD + " ▬▬▬▬▬▬▬");
        
        for (Achievement achievement : Achievement.values()) {
            boolean unlocked = stats.hasAchievement(achievement.getKey());
            String status = unlocked ? ChatColor.GREEN + "✓" : ChatColor.DARK_GRAY + "✗";
            String name = unlocked ? achievement.getName() : ChatColor.GRAY + achievement.getName().substring(2); // Remove color code
            player.sendMessage(status + " " + name);
            if (!unlocked) {
                player.sendMessage("  " + ChatColor.DARK_GRAY + achievement.getDescription().substring(2));
            }
        }
        
        player.sendMessage(ChatColor.GOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private void sendStatus(CommandSender sender) {
        int treeCount = XMas.getAllTrees().size();
        Set<UUID> owners = new HashSet<>();
        for (MagicTree magicTree : XMas.getAllTrees()) {
            owners.add(magicTree.getOwner());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk-mm-ss");

        sender.sendMessage(DARK_GREEN + LocaleManager.PLUGIN_NAME + " " + plugin.getDescription().getVersion() + " Plugin Status");
        sender.sendMessage("");
        sender.sendMessage(GRAY + "Event Status: " + (Main.inProgress ? DARK_GREEN + "In Progress" : RED + "Holidays End"));
        if (Main.inProgress) {
            sender.sendMessage(DARK_GREEN + "Current Time: " + GREEN + sdf.format(System.currentTimeMillis()));
            sender.sendMessage(DARK_GREEN + "Holidays end: " + RED + sdf.format(Main.endTime));
        }
        sender.sendMessage(GREEN + "Auto-End: " + (Main.autoEnd ? DARK_GREEN + "Yes" : RED + "No") + GREEN + "    |    " + "Resource Back: " + (Main.resourceBack ? DARK_GREEN + "Yes" : "No"));
        sender.sendMessage("");
        sender.sendMessage(DARK_GREEN + "There are " + GREEN + treeCount + DARK_GREEN + " magic trees owned by " + RED + owners.size() + DARK_GREEN + " players");
        sender.sendMessage(DARK_GREEN + "Use " + RED + "/xmas help" + DARK_GREEN + " for command list");
    }
}
