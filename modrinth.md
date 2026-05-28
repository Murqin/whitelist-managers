# Whitelist-Managers

A lightweight, secure, and zero-dependency Minecraft server plugin for **Paper** and **Purpur** (supporting Minecraft 26.1.2+ and compiled with Java 25).

**Whitelist-Managers** allows server administrators to delegate whitelist management privileges to specific players strictly through the server console. It provides a simple, direct, and OP-safe way to manage who can add or remove players from your server's whitelist.

---

## 🚀 Key Features

*   **Console-Only Delegation (`/wladmin`)**: Permission management can only be modified from the server console. Any in-game attempts, even by OPs, are rejected.
*   **Simple Game Commands (`/wl <add|remove>`)**: Delegated players can manage the server's vanilla whitelist directly.
*   **Automatic Kick**: When a player is un-whitelisted using `/wl remove`, the plugin immediately kicks them from the server using the modern Adventure API.
*   **Anti-Self-Lockout**: Prevents authorized admins from accidentally removing themselves from the whitelist.
*   **UUID-Safe Security**: Works with offline and online UUID validation. Stored usernames are auto-updated to their latest active names.
*   **Ultra Lightweight**: Single-purpose utility with zero database or LuckPerms dependencies.

---

## 🛠️ Commands & Permissions

### Server Console Commands
These commands **only** work from the server terminal console:
*   `/wladmin add <player>` - Appoints a player as a Whitelist Manager.
*   `/wladmin remove <player>` - Revokes a player's Whitelist Manager privileges.
*   `/wladmin list` - Lists all players allowed to run `/wl` commands.
*   `/wladmin reload` - Reloads the configuration file from disk.

### Whitelist Manager Commands
These commands work in-game, but **only** for players authorized in `config.yml`:
*   `/wl add <player>` - Adds the target player to the whitelist.
*   `/wl remove <player>` - Removes the target player from the whitelist and kicks them immediately if online.

---

## ⚙️ Configuration (`config.yml`)

Located at `plugins/Whitelist-Managers/config.yml`. It stores names and UUIDs automatically:

```yaml
# Whitelist Managers configuration file
# Manage this file via console using /wladmin

allowed-players:
  - name: "Murqin"
    uuid: "f8c3de3d-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
```

---

## 📥 Installation

1. Make sure your server is running on **Java 25** (standard for Paper 26.1.2+).
2. Download `whitelist-managers-1.2.0.jar` and drop it into your server's `plugins/` folder.
3. Start or restart the server.
4. From the server console, run `/wladmin add YourName` to authorize yourself!
