package com.murqin.whitelistmanager.commands;

import com.murqin.whitelistmanager.WhitelistManager;
import com.murqin.whitelistmanager.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Handles the '/wladmin' command tree, restricted exclusively to the server console.
 * Allows adding, removing, listing, and reloading whitelist managers.
 */
public class WlAdminCommand implements CommandExecutor, TabCompleter {

    private final WhitelistManager plugin;

    /**
     * Initializes the command executor.
     * @param plugin The main plugin instance.
     */
    public WlAdminCommand(WhitelistManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ConfigManager cm = plugin.getConfigManager();

        // Enforce Console only sender
        if (!(sender instanceof ConsoleCommandSender)) {
            String msg = cm.getMessage("console-only", "§c[WL-Admin] This command can only be executed from the server console!");
            sender.sendMessage(msg);
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "add" -> {
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /wladmin add <player>");
                    return true;
                }
                String targetName = args[1];
                @SuppressWarnings("deprecation")
                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetName);
                UUID uuid = targetPlayer.getUniqueId();
                String correctName = targetPlayer.getName();
                if (correctName == null) {
                    correctName = targetName;
                }

                boolean added = cm.addPlayer(correctName, uuid);
                if (added) {
                    String msg = cm.getMessage("admin-added", "§a[WL-Admin] %player% (%uuid%) has been promoted to Whitelist Manager.")
                            .replace("%player%", correctName)
                            .replace("%uuid%", uuid.toString());
                    sender.sendMessage(msg);
                } else {
                    String msg = cm.getMessage("admin-already-exists", "§e[WL-Admin] This player is already a Whitelist Manager.");
                    sender.sendMessage(msg);
                }
            }
            case "remove" -> {
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /wladmin remove <player>");
                    return true;
                }
                String targetName = args[1];
                boolean removed = cm.removePlayer(targetName);
                if (removed) {
                    String msg = cm.getMessage("admin-removed", "§a[WL-Admin] %player%'s Whitelist Manager privileges have been revoked.")
                            .replace("%player%", targetName);
                    sender.sendMessage(msg);
                } else {
                    String msg = cm.getMessage("admin-not-found", "§c[WL-Admin] No Whitelist Manager found with the name %player%.")
                            .replace("%player%", targetName);
                    sender.sendMessage(msg);
                }
            }
            case "list" -> {
                List<Map<String, String>> players = cm.getAllowedPlayers();
                if (players.isEmpty()) {
                    sender.sendMessage("§e[WL-Admin] There are no authorized Whitelist Managers.");
                    return true;
                }
                sender.sendMessage("§6§l=== Whitelist Managers List ===");
                for (Map<String, String> p : players) {
                    sender.sendMessage("§f- §a" + p.get("name") + " §7(" + p.get("uuid") + ")");
                }
            }
            case "reload" -> {
                cm.reload();
                String msg = cm.getMessage("config-reloaded", "§a[WL-Admin] Configuration file has been successfully reloaded.");
                sender.sendMessage(msg);
            }
            default -> sendUsage(sender);
        }

        return true;
    }

    /**
     * Sends usage guide to console.
     */
    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§6§l=== WLAdmin Command List ===");
        sender.sendMessage("§a/wladmin add <player> §7- Promotes player to Whitelist Manager.");
        sender.sendMessage("§a/wladmin remove <player> §7- Revokes Whitelist Manager privileges.");
        sender.sendMessage("§a/wladmin list §7- Lists all authorized managers.");
        sender.sendMessage("§a/wladmin reload §7- Reloads config.yml.");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            return Collections.emptyList();
        }

        // Sub-commands tab completion
        if (args.length == 1) {
            List<String> list = Arrays.asList("add", "remove", "list", "reload");
            return list.stream().filter(s -> s.startsWith(args[0].toLowerCase())).toList();
        }

        // Suggest online players for '/wladmin add <tab>' to make promotion easy
        if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            List<String> onlineNames = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                onlineNames.add(p.getName());
            }
            return onlineNames.stream().filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        }

        // Suggest currently authorized admins for '/wladmin remove <tab>'
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            List<String> names = new ArrayList<>();
            for (Map<String, String> p : plugin.getConfigManager().getAllowedPlayers()) {
                names.add(p.get("name"));
            }
            return names.stream().filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        }

        return Collections.emptyList();
    }
}
