package com.murqin.whitelistmanager.utils;

import com.murqin.whitelistmanager.WhitelistManager;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.*;

/**
 * Handles all configuration operations, including managing Whitelist Managers
 * (storing names and UUIDs) and fetching localized messages from config.yml.
 */
public class ConfigManager {

    private final WhitelistManager plugin;
    private FileConfiguration config;

    /**
     * Initializes the configuration manager and loads config.yml.
     * @param plugin The main plugin instance.
     */
    public ConfigManager(WhitelistManager plugin) {
        this.plugin = plugin;
        reload();
    }

    /**
     * Safely copies the default configuration from resources and reloads it into memory.
     */
    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    /**
     * Retrieves a message from the configuration, translating '&' color codes to standard '§'.
     * @param key The sub-key of the message (under 'messages.').
     * @param defaultValue The fallback string if the key is not defined.
     * @return The colorized message string.
     */
    public String getMessage(String key, String defaultValue) {
        String msg = config.getString("messages." + key);
        if (msg == null) return defaultValue;
        return msg.replace("&", "§");
    }

    /**
     * Fetches the raw list of whitelisted managers from configuration.
     * @return A list of player maps containing "name" and "uuid" keys.
     */
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

    /**
     * Checks if a player is an authorized Whitelist Manager.
     * @param uuid The UUID of the player to check.
     * @return true if authorized, false otherwise.
     */
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

    /**
     * Adds a player to the Whitelist Managers delegation list.
     * If the player already exists but their username has changed, it updates the stored name.
     * @param name The username of the player.
     * @param uuid The unique Mojang UUID of the player.
     * @return true if a new manager was added, false if already existed (even if name was updated).
     */
    public boolean addPlayer(String name, UUID uuid) {
        if (uuid == null || name == null) return false;
        
        List<Map<String, String>> allowedList = getAllowedPlayers();
        boolean exists = false;
        for (Map<String, String> playerMap : allowedList) {
            String storedUuid = playerMap.get("uuid");
            if (uuid.toString().equalsIgnoreCase(storedUuid)) {
                exists = true;
                // Auto-update stored name if it changed
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

    /**
     * Revokes Whitelist Manager status from a player by their username (case-insensitive).
     * @param name The username of the player to remove.
     * @return true if the player was found and removed, false otherwise.
     */
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
