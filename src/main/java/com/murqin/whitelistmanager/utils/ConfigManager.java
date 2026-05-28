package com.murqin.whitelistmanager.utils;

import com.murqin.whitelistmanager.WhitelistManager;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.*;

public class ConfigManager {

    private final WhitelistManager plugin;
    private FileConfiguration config;

    public ConfigManager(WhitelistManager plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> getAllowedPlayers() {
        List<?> rawList = config.getList("allowed-players");
        List<Map<String, String>> allowedList = new ArrayList<>();
        if (rawList != null) {
            for (Object obj : rawList) {
                if (obj instanceof Map) {
                    try {
                        allowedList.add((Map<String, String>) obj);
                    } catch (Exception ignored) {}
                }
            }
        }
        return allowedList;
    }

    public boolean isAllowed(UUID uuid) {
        if (uuid == null) return false;
        String uuidStr = uuid.toString();
        for (Map<String, String> playerMap : getAllowedPlayers()) {
            String storedUuid = playerMap.get("uuid");
            if (uuidStr.equalsIgnoreCase(storedUuid)) {
                return true;
            }
        }
        return false;
    }

    public boolean addPlayer(String name, UUID uuid) {
        if (uuid == null || name == null) return false;
        
        List<Map<String, String>> allowedList = getAllowedPlayers();
        boolean exists = false;
        for (Map<String, String> playerMap : allowedList) {
            String storedUuid = playerMap.get("uuid");
            if (uuid.toString().equalsIgnoreCase(storedUuid)) {
                exists = true;
                // If name changed, update it!
                if (!name.equalsIgnoreCase(playerMap.get("name"))) {
                    playerMap.put("name", name);
                    config.set("allowed-players", allowedList);
                    plugin.saveConfig();
                }
                break;
            }
        }
        
        if (exists) return false;

        Map<String, String> newPlayer = new HashMap<>();
        newPlayer.put("name", name);
        newPlayer.put("uuid", uuid.toString());
        allowedList.add(newPlayer);

        config.set("allowed-players", allowedList);
        plugin.saveConfig();
        return true;
    }

    public boolean removePlayer(String name) {
        if (name == null) return false;
        List<Map<String, String>> allowedList = getAllowedPlayers();
        boolean removed = false;

        Iterator<Map<String, String>> iterator = allowedList.iterator();
        while (iterator.hasNext()) {
            Map<String, String> playerMap = iterator.next();
            String storedName = playerMap.get("name");
            if (name.equalsIgnoreCase(storedName)) {
                iterator.remove();
                removed = true;
            }
        }

        if (removed) {
            config.set("allowed-players", allowedList);
            plugin.saveConfig();
        }
        return removed;
    }
}
