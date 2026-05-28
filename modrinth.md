# 🛡️ Whitelist-Managers

[![Minecraft](https://img.shields.io/badge/Minecraft-26.1.2%2B-red.svg?style=flat-square&color=c71d22)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-25%2B-blue.svg?style=flat-square&color=e58e26)](https://oracle.com/java)
[![Platform](https://img.shields.io/badge/Platform-Paper%20%7C%20Purpur-orange.svg?style=flat-square&color=f39c12)](https://papermc.io)
[![Version](https://img.shields.io/badge/Version-1.4.0-green.svg?style=flat-square&color=2ecc71)](https://github.com)

**Whitelist-Managers** is a lightweight, robust, and zero-dependency administration tool built exclusively for modern **Paper** and **Purpur** servers (compiled with **Java 25**).

It provides server administrators with a simple, secure, and OP-safe mechanism to delegate whitelist management (`/whitelist add` and `/whitelist remove` equivalents) to trusted players (Managers) without giving them full operator status or raw vanilla permission nodes.

---

## 🚀 Key Features

*   **🔒 Console-Locked Promotion (`/wladmin`)**: Granting or revoking whitelist manager privileges can *only* be done directly from the server console terminal. Any in-game attempts, even by OPs, are rejected.
*   **🚫 Self-Removal Protection**: Prevents authorized whitelist managers from accidentally removing themselves from the whitelist, avoiding accidental lockout and connection loops.
*   **⚡ Connection-Kicking on Removal**: When an authorized manager removes a player using `/wl remove`, the target player is immediately kicked from the server using the modern Adventure API.
*   **🔄 UUID-Safe Synchronization**: Keeps track of managers using Mojang UUIDs. In-game usernames stored in `config.yml` are dynamically updated if a manager changes their Minecraft username.
*   **💡 Intelligent Tab-Completion**:
    *   `/wl remove <tab>` dynamically suggests only currently whitelisted players on the server.
    *   `/wladmin remove <tab>` suggests only currently promoted whitelist managers.
*   **🌱 Ultra-Lightweight & Clean**: Standalone plugin with zero database or LuckPerms dependencies. Extremely high-performance impact.

---

## 🛠️ Commands & Usage

### 💻 Administrator Commands (Console-Only)
These commands are restricted solely to the server terminal:
*   `/wladmin add <player>` — Appoints a player as a Whitelist Manager, registering their UUID in `config.yml`.
*   `/wladmin remove <player>` — Revokes a player's Whitelist Manager privileges.
*   `/wladmin list` — Lists all players currently allowed to run `/wl` commands.
*   `/wladmin reload` — Reloads the configuration file from disk.

### 🎮 Whitelist Manager Commands (In-game)
Accessible in-game only by players registered in `config.yml`:
*   `/wl add <player>` — Adds the target player to the vanilla whitelist.
*   `/wl remove <player>` — Removes the target player from the whitelist and kicks them immediately if online.

---

## ⚙️ Configuration (`config.yml`)

Located at `plugins/Whitelist-Managers/config.yml`. Both the player name and Mojang UUID are automatically recorded:

```yaml
# Whitelist Managers configuration file
# Manage this file via console using /wladmin

allowed-players:
  - name: "Murqin"
    uuid: "f8c3de3d-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
```

---

## 📥 Quick Installation Guide

1. Ensure your server is running on **Java 25** (standard for Paper 26.1.2+).
2. Download `whitelist-managers-1.4.0.jar` and drop it into your server's `plugins/` folder.
3. Start or restart your server.
4. From the server console terminal, run `/wladmin add YourName` to promote yourself!
