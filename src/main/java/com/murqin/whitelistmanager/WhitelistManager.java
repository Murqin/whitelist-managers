package com.murqin.whitelistmanager;

import com.murqin.whitelistmanager.commands.WlAdminCommand;
import com.murqin.whitelistmanager.commands.WlCommand;
import com.murqin.whitelistmanager.utils.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main plugin entry point for Whitelist-Managers.
 * Handles initialization, command registrations, and config set up.
 */
public final class WhitelistManager extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // Initialize config manager and load configuration files
        this.configManager = new ConfigManager(this);

        // Instantiating command executors
        WlCommand wlCommand = new WlCommand(this);
        WlAdminCommand wlAdminCommand = new WlAdminCommand(this);

        // Register in-game manager command
        if (getCommand("wl") != null) {
            getCommand("wl").setExecutor(wlCommand);
            getCommand("wl").setTabCompleter(wlCommand);
        }
        
        // Register console administrator command
        if (getCommand("wladmin") != null) {
            getCommand("wladmin").setExecutor(wlAdminCommand);
            getCommand("wladmin").setTabCompleter(wlAdminCommand);
        }

        getLogger().info("Whitelist-Managers plugin has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Whitelist-Managers plugin has been successfully disabled!");
    }

    /**
     * Retrieves the ConfigManager instance managing data and translations.
     * @return The active ConfigManager instance.
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
}
