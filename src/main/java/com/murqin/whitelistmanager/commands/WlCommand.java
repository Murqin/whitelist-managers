package com.murqin.whitelistmanager.commands;

import com.murqin.whitelistmanager.WhitelistManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WlCommand implements CommandExecutor, TabCompleter {

    private final WhitelistManager plugin;

    public WlCommand(WhitelistManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Enforce player sender with permission check from config.yml
        if (sender instanceof Player player) {
            if (!plugin.getConfigManager().isAllowed(player.getUniqueId())) {
                sender.sendMessage("§cBu komutu kullanmak için yetkiniz bulunmamaktadır!");
                return true;
            }
        } else {
            // Also allow Console to execute /wl if needed, though they have /wladmin
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
                    sender.sendMessage("§e" + targetName + " zaten whitelist'e ekli.");
                } else {
                    targetPlayer.setWhitelisted(true);
                    sender.sendMessage("§a" + targetName + " başarıyla whitelist'e eklendi.");
                }
            }
            case "remove" -> {
                if (!targetPlayer.isWhitelisted()) {
                    sender.sendMessage("§e" + targetName + " zaten whitelist'te ekli değil.");
                } else {
                    targetPlayer.setWhitelisted(false);
                    sender.sendMessage("§a" + targetName + " başarıyla whitelist'ten çıkarıldı.");
                    if (targetPlayer.isOnline() && targetPlayer.getPlayer() != null) {
                        targetPlayer.getPlayer().kick(net.kyori.adventure.text.Component.text("§cWhitelist'ten çıkarıldınız!"));
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

        if (args.length == 1) {
            return Arrays.asList("add", "remove").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}
