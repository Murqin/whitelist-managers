package com.murqin.whitelistmanager;

import com.murqin.whitelistmanager.commands.WlAdminCommand;
import com.murqin.whitelistmanager.commands.WlCommand;
import com.murqin.whitelistmanager.utils.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class WhitelistManager extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // Initialize config manager
        this.configManager = new ConfigManager(this);

        // Register commands
        WlCommand wlCommand = new WlCommand(this);
        WlAdminCommand wlAdminCommand = new WlAdminCommand(this);

        if (getCommand("wl") != null) {
            getCommand("wl").setExecutor(wlCommand);
            getCommand("wl").setTabCompleter(wlCommand);
        }
        if (getCommand("wladmin") != null) {
            getCommand("wladmin").setExecutor(wlAdminCommand);
            getCommand("wladmin").setTabCompleter(wlAdminCommand);
        }

        getLogger().info("Whitelist-Manager plugin aktif edildi!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Whitelist-Manager plugin aktif pasif edildi!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
