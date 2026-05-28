# Whitelist Manager Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a custom Minecraft plugin for Paper/Purpur servers using Java 25 that delegates whitelist permissions via console-only commands to specific players, storing them by UUID in a config file.

**Architecture:** Use a clean command delegation pattern. `WlAdminCommand` handles server-console only administration, storing authorized UUIDs in `config.yml`. `WlCommand` handles game-side whitelisting, checking permissions against `config.yml` and modifying Minecraft's native whitelist system directly using the Bukkit API.

**Tech Stack:** Java 25, Paper-API (latest 26.1.2-R0.1-SNAPSHOT), Maven.

---

### Task 1: Project Setup and pom.xml Configuration

**Files:**
- Create: `pom.xml`
- Create: `src/main/resources/plugin.yml`

- [ ] **Step 1: Create the pom.xml targeting Java 25**

Create the file `/home/murqin/Documents/Minecraft_Plugins/Whitelist-Manager/pom.xml` with target compile version 25.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.murqin</groupId>
    <artifactId>whitelist-manager</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>Whitelist-Manager</name>
    <description>Console-delegated whitelist management plugin</description>
    <properties>
        <java.version>25</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <repositories>
        <repository>
            <id>papermc</id>
            <url>https://repo.papermc.io/repository/maven-public/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>io.papermc.paper</groupId>
            <artifactId>paper-api</artifactId>
            <version>26.1.2-R0.1-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                    <release>${java.version}</release>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.3</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </build>
    </build>
</project>
```

- [ ] **Step 2: Create resources/plugin.yml**

Create the file `/home/murqin/Documents/Minecraft_Plugins/Whitelist-Manager/src/main/resources/plugin.yml`.

```yaml
name: Whitelist-Manager
version: 1.0-SNAPSHOT
main: com.murqin.whitelistmanager.WhitelistManager
api-version: '26'
author: Murqin
description: Console-delegated whitelist management plugin.
commands:
  wl:
    description: Oyun ici whitelist ekleme/cikarma komutu.
    usage: /wl <add|remove> <oyuncu>
  wladmin:
    description: Konsoldan whitelist yetkililerini yonetme komutu.
    usage: /wladmin <add|remove|list|reload>
```

- [ ] **Step 3: Verify Maven settings by compiling project stub**

Run command: `mvn clean compile`
Expected: Succeeds (compiles successfully without code).

- [ ] **Step 4: Commit task**

```bash
git add pom.xml src/main/resources/plugin.yml
git commit -m "chore: setup maven project structure and plugin.yml"
```

---

### Task 2: ConfigManager Class Implementation

**Files:**
- Create: `src/main/java/com/murqin/whitelistmanager/utils/ConfigManager.java`

- [ ] **Step 1: Write ConfigManager class**

Create `/home/murqin/Documents/Minecraft_Plugins/Whitelist-Manager/src/main/java/com/murqin/whitelistmanager/utils/ConfigManager.java` to manage configuration reading, saving, and querying.

```java
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
        if (isAllowed(uuid)) return false;

        List<Map<String, String>> allowedList = getAllowedPlayers();
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
```

- [ ] **Step 2: Commit task**

```bash
git add src/main/java/com/murqin/whitelistmanager/utils/ConfigManager.java
git commit -m "feat: implement ConfigManager for UUID-based configuration storage"
```

---

### Task 3: WlAdminCommand Class Implementation

**Files:**
- Create: `src/main/java/com/murqin/whitelistmanager/commands/WlAdminCommand.java`

- [ ] **Step 1: Write WlAdminCommand class**

Create `/home/murqin/Documents/Minecraft_Plugins/Whitelist-Manager/src/main/java/com/murqin/whitelistmanager/commands/WlAdminCommand.java`.

```java
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
```

- [ ] **Step 2: Commit task**

```bash
git add src/main/java/com/murqin/whitelistmanager/commands/WlAdminCommand.java
git commit -m "feat: implement console-only WlAdminCommand"
```

---

### Task 4: WlCommand Class Implementation

**Files:**
- Create: `src/main/java/com/murqin/whitelistmanager/commands/WlCommand.java`

- [ ] **Step 1: Write WlCommand class**

Create `/home/murqin/Documents/Minecraft_Plugins/Whitelist-Manager/src/main/java/com/murqin/whitelistmanager/commands/WlCommand.java`.

```java
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
```

- [ ] **Step 2: Commit task**

```bash
git add src/main/java/com/murqin/whitelistmanager/commands/WlCommand.java
git commit -m "feat: implement game-side WlCommand checking config.yml authorization"
```

---

### Task 5: WhitelistManager Class Implementation and Configuration Initialization

**Files:**
- Create: `src/main/java/com/murqin/whitelistmanager/WhitelistManager.java`
- Create: `src/main/resources/config.yml`

- [ ] **Step 1: Write main WhitelistManager class**

Create `/home/murqin/Documents/Minecraft_Plugins/Whitelist-Manager/src/main/java/com/murqin/whitelistmanager/WhitelistManager.java`.

```java
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
```

- [ ] **Step 2: Create default config.yml file**

Create `/home/murqin/Documents/Minecraft_Plugins/Whitelist-Manager/src/main/resources/config.yml`.

```yaml
# Whitelist Manager Configuration
# Allowed players list. Stored as:
# allowed-players:
#   - name: "Murqin"
#     uuid: "f8c3de3d-xxxx-xxxx-xxxx-xxxxxxxxxxxx"

allowed-players: []
```

- [ ] **Step 3: Compile and package build**

Run command: `mvn clean package`
Expected: Succeeds and produces a JAR file in `target/whitelist-manager-1.0-SNAPSHOT.jar`.

- [ ] **Step 4: Commit task**

```bash
git add src/main/java/com/murqin/whitelistmanager/WhitelistManager.java src/main/resources/config.yml
git commit -m "feat: implement main WhitelistManager entry point and compile project successfully"
```
