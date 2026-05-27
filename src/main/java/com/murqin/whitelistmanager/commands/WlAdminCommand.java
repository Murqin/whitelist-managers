package com.murqin.whitelistmanager.commands;

import com.murqin.whitelistmanager.WhitelistManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WlAdminCommand implements CommandExecutor, TabCompleter {

    private final WhitelistManager plugin;

    public WlAdminCommand(WhitelistManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Enforce console only sender
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§c[WL-Admin] Bu komut sadece sunucu konsolundan çalıştırılabilir!");
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
                    sender.sendMessage("§cKullanım: /wladmin add <oyuncu>");
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

                boolean added = plugin.getConfigManager().addPlayer(correctName, uuid);
                if (added) {
                    sender.sendMessage("§a[WL-Admin] " + correctName + " (" + uuid + ") adlı oyuncuya whitelist yetkisi verildi.");
                } else {
                    sender.sendMessage("§e[WL-Admin] Bu oyuncu zaten yetki listesinde bulunuyor.");
                }
            }
            case "remove" -> {
                if (args.length < 2) {
                    sender.sendMessage("§cKullanım: /wladmin remove <oyuncu>");
                    return true;
                }
                String targetName = args[1];
                boolean removed = plugin.getConfigManager().removePlayer(targetName);
                if (removed) {
                    sender.sendMessage("§a[WL-Admin] " + targetName + " adlı oyuncunun whitelist yetkisi geri alındı.");
                } else {
                    sender.sendMessage("§c[WL-Admin] Yetki listesinde " + targetName + " adında bir oyuncu bulunamadı.");
                }
            }
            case "list" -> {
                List<Map<String, String>> players = plugin.getConfigManager().getAllowedPlayers();
                if (players.isEmpty()) {
                    sender.sendMessage("§e[WL-Admin] Yetkilendirilmiş herhangi bir oyuncu bulunmuyor.");
                    return true;
                }
                sender.sendMessage("§6§l=== Yetkili Oyuncu Listesi ===");
                for (Map<String, String> p : players) {
                    sender.sendMessage("§f- §a" + p.get("name") + " §7(" + p.get("uuid") + ")");
                }
            }
            case "reload" -> {
                plugin.getConfigManager().reload();
                sender.sendMessage("§a[WL-Admin] Konfigürasyon dosyası başarıyla yenilendi.");
            }
            default -> sendUsage(sender);
        }

        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§6§l=== WLAdmin Komut Listesi ===");
        sender.sendMessage("§a/wladmin add <oyuncu> §7- Oyuncuya whitelist yetkisi verir.");
        sender.sendMessage("§a/wladmin remove <oyuncu> §7- Oyuncunun whitelist yetkisini siler.");
        sender.sendMessage("§a/wladmin list §7- Yetkili tüm oyuncuları listeler.");
        sender.sendMessage("§a/wladmin reload §7- Config'i yeniler.");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> list = Arrays.asList("add", "remove", "list", "reload");
            return list.stream().filter(s -> s.startsWith(args[0].toLowerCase())).toList();
        }

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
