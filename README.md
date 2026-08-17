# MythicGear

[![Release](https://img.shields.io/github/v/release/FedericoLupoli/MythicGear)](https://github.com/FedericoLupoli/MythicGear/releases)

**MythicGear** is a [Paper](https://papermc.io) plugin that brings powerful, fully-custom equipment sets to your Minecraft server. It is built around the **Obsidian Dragon** set: netherite armor with an armor + elytra hybrid chestplate, a signature weapon, and full-set bonuses — all configurable.

## Features

### Obsidian Dragon set
- **4 netherite armor pieces** plus the Obsidian Dragon Sword, each with custom stats, enchantments and lore.
- **Armor + elytra hybrid** — the chestplate doubles as an elytra (glider) and renders *both* the netherite armor and the wings, thanks to a custom equipment model.
- **Full-set bonus** — wearing the complete armor set grants **+20 max health** (double life).
- **Dragon Breath ability** — right-click the Obsidian Dragon Sword to launch a dragon fireball that leaves a lingering cloud of dragon breath on impact.
- **Dragon Breath immunity** — players wearing any piece of the set are immune to dragon breath, both from the sword and from the Ender Dragon.

### Mage set
- **4 netherite armor pieces** (Protection 4) styled as a dark open-front bathrobe with gold trim.
- **4 magical staffs** (BOW material, right-click in air, require full Mage set):
  - **Fire Staff** — shoots a burst of 5 fire projectiles with spread.
  - **Lightning Staff** — strikes lightning at the targeted block (range 50).
  - **Healing Staff** — grants Regeneration II (5s) + Absorption (10s).
  - **Flight Staff** — toggles creative flight (revoked on disconnect without OP).
- Staffs are designed as medieval magical wooden staffs with glowing crystal orbs.

### General
- **Explicit per-piece attributes** — armor, armor toughness, knockback resistance and movement speed, fully declared in `items.yml`.
- **Crafting recipes** for the armor pieces.
- **Anti-exploit** — MythicGear items cannot be renamed or repaired in anvils/grindstones.
- **Built-in resource pack** — custom equipment models, automatically served by the server.

## Requirements

- **Paper 26.2** or compatible
- **Java 21+**

## Installation

1. Download the latest `MythicGear.jar` from the [Releases](https://github.com/FedericoLupoli/MythicGear/releases) page.
2. Place it in your server's `plugins/` folder.
3. (Recommended) Install the resource pack — see [Resource pack](#resource-pack).
4. Restart the server.

### Resource pack

The custom equipment model ships as a resource pack (`ObsidianDragon-RP.zip`, available on the Releases page). The server can push it to clients automatically via `server.properties`:

```
resource-pack=http://<host>:8010/ObsidianDragon-RP.zip
resource-pack-sha1=<sha1-of-the-file>
```

Set `require-resource-pack=true` to force the pack, or have players install the zip manually under their resource pack folder.

## Commands

| Command | Description |
|---|---|
| `/mythicgear give <item> [player]` | Give a single item |
| `/mythicgear giveset <set> [player]` | Give a whole set (all its pieces) |
| `/mythicgear list` | List all items, grouped by set |
| `/mythicgear reload` | Reload items, sets, effects and recipes |

Alias: `/mg`. All commands require the `mythicgear.admin` permission (default: OP).

## Configuration

All items and sets are defined in the plugin's data folder (`plugins/MythicGear/`):

- `items.yml` — item definitions (material, name, lore, enchantments, attributes, equipment slot, glider...).
- `sets.yml` — set composition and bonuses.

```yaml
# items.yml
dragon_sword:
  material: NETHERITE_SWORD
  name: "<red>Spada del Drago d'Ossidiana"
  lore:
    - "<gray>Set <red>Drago d'Ossidiana"
    - "<gray>Click destro: <aqua>soffio di drago"
  set: dragon
  piece: sword
  enchants:
    sharpness: 10
    fire_aspect: 2
    unbreaking: 5
    mending: 1
  attributes:
    attack_damage:
      amount: 4
      operation: ADD_NUMBER
      slot: MAINHAND

fire_staff:
  material: BOW
  name: "<gold>Staffa del Fuoco"
  lore:
    - "<gray>Set <dark_purple>Mago"
    - "<gray>Click destro: <red>raffica di fuoco"
  set: mage
  piece: staff
  item_model: mythicgear:fire_staff
  no_glint: true

# sets.yml
dragon:
  name: "Drago d'Ossidiana"
  pieces:
    - dragon_helmet
    - dragon_chestplate
    - dragon_leggings
    - dragon_boots
  max_health_bonus: 20

mage:
  name: "Mago"
  pieces:
    - mage_helmet
    - mage_chestplate
    - mage_leggings
    - mage_boots
  max_health_bonus: 0
```

> **Note for Paper 26.2:** attribute modifiers added through the API *replace* the material's default modifiers. Always declare the base stats (`armor`, `armor_toughness`, `knockback_resistance`) explicitly for every piece.

> **Note:** equipment slot groups use the names `MAINHAND` / `OFFHAND` (no underscore) in Paper 26.2. `MAIN_HAND` is also accepted and normalized automatically.

## Building from source

```bash
git clone https://github.com/FedericoLupoli/MythicGear.git
cd MythicGear/
./gradlew build
```

The plugin jar is generated in `build/libs/`.

`deploy.sh` automates build, deploy and resource pack serving:

```bash
./deploy.sh                      # default server dir ($HOME/Scrivania/server)
./deploy.sh /path/to/server      # explicit server dir
SERVER_DIR=/path/to/server ./deploy.sh
RP_PORT=9000 ./deploy.sh         # different HTTP port
```

See `.deployrc.example` for all available options.

## Changelog

See [changelog.md](changelog.md).

## License

All rights reserved.
