# 🛡️ Whitelist-Managers

[![Minecraft](https://img.shields.io/badge/Minecraft-26.1.2%2B-red.svg?style=flat-square&color=c71d22)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-25%2B-blue.svg?style=flat-square&color=e58e26)](https://oracle.com/java)
[![Platform](https://img.shields.io/badge/Platform-Paper%20%7C%20Purpur-orange.svg?style=flat-square&color=f39c12)](https://papermc.io)
[![Version](https://img.shields.io/badge/Version-1.3.0-green.svg?style=flat-square&color=2ecc71)](https://github.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square&color=3498db)](LICENSE)

An ultra-lightweight, high-performance, and secure whitelist management utility built explicitly for modern Minecraft servers (**Paper & Purpur**). 

**Whitelist-Managers** bridges the gap between server administration and player delegation, permitting server owners to delegate `/whitelist add` and `/whitelist remove` capabilities to specific trusted players (Managers) strictly through the server console, eliminating the risks of OP-privilege escalation and configuration bypasses.

---

## ✨ Features

- **🔐 Console-Locked Administration (`/wladmin`)**: Delegation can *only* be managed directly from the server console (`ConsoleCommandSender`). Any in-game attempts (including OPs and command blocks) are immediately intercepted and rejected.
- **🛡️ Accidental Self-Removal Protection**: Whitelist managers are automatically prevented from removing themselves from the whitelist, avoiding accidental locking, connection termination, and lockout cycles.
- **⚡ Seamless Active-Connection Kicking**: When an operator un-whitelists a player using `/wl remove`, the player is instantly disconnected from the server using Paper's native Kyori Adventure API.
- **🔄 Dynamic Name & UUID Synchronization**: Whitelist managers are stored using unique Mojang UUIDs. In-game usernames stored in `config.yml` are dynamically updated if a manager changes their Minecraft username.
- **🔍 Context-Aware Intelligent Tab-Completion**:
  - `/wl remove <tab>` dynamically suggests only currently whitelisted players on the server.
  - `/wladmin remove <tab>` suggests only currently promoted whitelist managers.
- **🌱 Zero Dependencies**: Completely standalone. No LuckPerms, external databases, or heavy libraries required. Extremely lightweight performance impact.

---

## 📋 Server Requirements

| Requirement | Supported Version | Details |
| :--- | :--- | :--- |
| **Server Software** | `Paper`, `Purpur` or forks | Supports Paper API `26.1.2` and above |
| **Java Runtime** | `Java 25` or higher | Compiled targeting standard JDK 25 specifications |
| **Network Type** | `Online Mode` / `Offline Mode` | UUID-compliant. Requires a secure login plugin if in Offline mode |

---

## 💬 Commands & Permissions Reference

### Administrator Commands (Console-Only)
> [!NOTE]
> These commands are restricted solely to the server terminal.

| Command | Arguments | Description |
| :--- | :--- | :--- |
| `/wladmin add` | `<player>` | Promotes a player to Whitelist Manager, registering their UUID in `config.yml` |
| `/wladmin remove` | `<player>` | Revokes Whitelist Manager status and updates configuration |
| `/wladmin list` | *None* | Lists all currently authorized Whitelist Managers |
| `/wladmin reload` | *None* | Reloads configuration files dynamically from disk |

### Manager Commands (Delegated Players)
> [!IMPORTANT]
> Accessible in-game only by players registered in `config.yml`. No permissions nodes needed.

| Command | Arguments | Description |
| :--- | :--- | :--- |
| `/wl add` | `<player>` | Adds target player to vanilla whitelist |
| `/wl remove` | `<player>` | Removes target player from whitelist and kicks them immediately if online |

---

## ⚙️ Configuration Schema (`config.yml`)

The whitelist managers database is persisted in `plugins/Whitelist-Managers/config.yml`. Both the player name and Mojang UUID are recorded to maintain strict reference integrity:

```yaml
# ==========================================
#        Whitelist-Managers Configuration
# ==========================================
#
# NOTE: It is highly recommended to manage this file
# exclusively via the server console using '/wladmin'.

allowed-players:
  - name: "Murqin"
    uuid: "f8c3de3d-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
```

---

## 🛠️ Compilation & Development

Building the plugin requires **Maven** and **JDK 25** (or above).

### 1. Clone & Set Up Directory
Ensure your files are arranged in the standard Maven directory structure.

### 2. Package with Maven
Execute the standard compile and package workflow:
```bash
mvn clean package
```

### 3. Retrieve Output Artifact
The build target will compile and package a shaded `.jar` containing all metadata under:
`target/whitelist-managers-1.3.0.jar`

---

## 📄 License
This project is licensed under the [MIT License](LICENSE) - see the LICENSE file for details.
