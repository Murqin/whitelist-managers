# Whitelist-Managers

A custom, lightweight, and secure Minecraft plugin designed for Paper and Purpur servers (Minecraft 26.1.2+ / Java 25). 

It allows server administrators to delegate `/whitelist add` and `/whitelist remove` permissions to specific players exclusively through the server console, keeping command control clean and simple.

---

## Features

- **Console-Only Promotion (`/wladmin`)**: Adding or removing whitelist managers can only be done directly from the server console. Any in-game attempts (even by OPs) are strictly blocked.
- **Delegated Commands (`/wl <add|remove>`)**: Authorized players can easily whitelist or un-whitelist other players.
- **Accidental Self-Removal Protection**: Authorized users are blocked from removing themselves from the whitelist, preventing accidental locking and kick-outs.
- **Immediate Player Kick**: When a player is removed from the whitelist via `/wl remove`, they are immediately kicked from the server.
- **Auto-Update Admin Names**: Stored admin usernames in `config.yml` are automatically updated to match their latest active name upon UUID verification.
- **Zero Dependencies**: Lightweight, fast, and does not require LuckPerms or SQL databases.

---

## Commands & Usage

### `/wladmin` (Console Only)
*   `wladmin add <player>`: Grants whitelist manager privileges to a player. Saves their name and UUID in `config.yml`.
*   `wladmin remove <player>`: Revokes whitelist manager privileges from a player.
*   `wladmin list`: Lists all currently authorized whitelist managers.
*   `wladmin reload`: Reloads the plugin configuration.

### `/wl` (Authorized In-game Players)
*   `wl add <player>`: Adds a player to the server's vanilla whitelist.
*   `wl remove <player>`: Removes a player from the whitelist and kicks them immediately if online.

---

## Configuration (`config.yml`)

The list of allowed managers is saved by both their username and UUID to protect against username changes:

```yaml
# Whitelist Managers configuration file
# Managed automatically via /wladmin console commands

allowed-players:
  - name: "Murqin"
    uuid: "f8c3de3d-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
```

---

## Compile & Build

To compile and package the plugin from source, ensure you have **JDK 25** and **Maven** installed, then run:

```bash
mvn clean package
```

The compiled release package will be generated at `target/whitelist-managers-1.2.0.jar`.
