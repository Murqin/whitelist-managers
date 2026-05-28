package com.murqin.whitelistmanager.commands;

import com.murqin.whitelistmanager.WhitelistManager;
import com.murqin.whitelistmanager.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Handles the game-side '/wl' command tree, allowing authorized whitelist managers
 * to add/remove players to/from the server's vanilla whitelist.
 */
public class WlCommand implements CommandExecutor, TabCompleter {

    private final WhitelistManager plugin;

    /**
     * Initializes the command executor.
     * @param plugin The main plugin instance.
     */
    public WlCommand(WhitelistManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ConfigManager cm = plugin.getConfigManager();

        // Enforce player sender with permission check from config.yml
        if (sender instanceof Player player) {
            if (!cm.isAllowed(player.getUniqueId())) {
                String msg = cm.getMessage("no-permission", "§cBu komutu kullanmak için yetkiniz bulunmamaktadır!");
                sender.sendMessage(msg);
                return true;
            }
        } else {
            // Advise console to use /wladmin
            sender.sendMessage("§cKonsol zaten whitelist komutuna sahiptir, lütfen /wladmin kullanın.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cKullanım: /wl <add|remove> <oyuncu>");
            return true;
        }

        String sub = args[0].toLowerCase();
        String targetName = args[1];

        @SuppressWarnings("deprecation")
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetName);

        switch (sub) {
            case "add" -> {
                if (targetPlayer.isWhitelisted()) {
                    String msg = cm.getMessage("whitelist-already-added", "§e%player% zaten whitelist'e ekli.")
                            .replace("%player%", targetName);
                    sender.sendMessage(msg);
                } else {
                    targetPlayer.setWhitelisted(true);
                    String msg = cm.getMessage("whitelist-added", "§a%player% başarıyla whitelist'e eklendi.")
                            .replace("%player%", targetName);
                    sender.sendMessage(msg);
                }
            }
            case "remove" -> {
                // Prevent self-lockout: a manager cannot remove themselves from the whitelist
                if (sender instanceof Player p && targetPlayer.getUniqueId().equals(p.getUniqueId())) {
                    String msg = cm.getMessage("self-removal-denied", "§cKendi kendinizi whitelist'ten çıkaramazsınız!");
                    sender.sendMessage(msg);
                    return true;
                }

                if (!targetPlayer.isWhitelisted()) {
                    String msg = cm.getMessage("whitelist-already-removed", "§e%player% zaten whitelist'te ekli değil.")
                            .replace("%player%", targetName);
                    sender.sendMessage(msg);
                } else {
                    targetPlayer.setWhitelisted(false);
                    String msg = cm.getMessage("whitelist-removed", "§a%player% başarıyla whitelist'ten çıkarıldı.")
                            .replace("%player%", targetName);
                    sender.sendMessage(msg);

                    // Kick the player instantly if they are online using Adventure API
                    if (targetPlayer.isOnline() && targetPlayer.getPlayer() != null) {
                        String kickMsg = cm.getMessage("kick-reason", "§cWhitelist'ten çıkarıldınız!");
                        targetPlayer.getPlayer().kick(net.kyori.adventure.text.Component.text(kickMsg));
                    }
                }
            }
            default -> sender.sendMessage("§cKullanım: /wl <add|remove> <oyuncu>");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!plugin.getConfigManager().isAllowed(player.getUniqueId())) {
                return Collections.emptyList();
            }
        }

        // Sub-commands tab completion
        if (args.length == 1) {
            return Arrays.asList("add", "remove").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        // Suggest currently whitelisted players for '/wl remove <tab>' to make removal simple
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            List<String> whitelistedNames = new ArrayList<>();
            for (OfflinePlayer op : Bukkit.getWhitelistedPlayers()) {
                String name = op.getName();
                if (name != null) {
                    whitelistedNames.add(name);
                }
            }
            return whitelistedNames.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}
