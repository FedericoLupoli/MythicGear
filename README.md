# MythicGear

Plugin Paper per Minecraft che aggiunge un set di equipaggiamento custom con effetti da set completo. Progettato per un'esperienza semi-vanilla.

**Set attuale: Drago d'Ossidiana** — armature in netherite con un pettorale che combina armatura ed elytra.

## Funzionalità

- **Set "Drago d'Ossidiana"** (4 pezzi: elmo, pettorale, gambiere, stivali)
- **Bonus full set**: vita raddoppiata (+20 max health)
- **Pettorale ibrido armatura + elytra**: indossandolo si plana (glider) e si vede sia l'armatura netherite sia l'elytra grazie a un modello `equipment` custom
- **Statistiche esplicite per pezzo**: armor, armor_toughness, knockback_resistance, movement_speed
- **Incantesimi personalizzati** per ogni pezzo
- **Ricette di crafting** per i pezzi del set
- **Anti-exploit**: gli item non possono essere rinominati o riparati in incudine/grindstone
- **Resource pack** incluso per il modello custom, caricato automaticamente dal server

## Requisiti

- **Paper 26.2** (o compatibile)
- Java 21+

## Installazione

1. Copia `MythicGear.jar` nella cartella `plugins/` del server.
2. (Consigliato) Posiziona `ObsidianDragon-RP.zip` nella cartella del server e configura il caricamento automatico del resource pack (vedi sotto).
3. Riavvia il server.

### Resource pack automatico

Il server può inviare il resource pack automaticamente ai giocatori. In `server.properties`:

```
resource-pack=http://<indirizzo>:8010/ObsidianDragon-RP.zip
resource-pack-sha1=<sha1-del-file>
```

Per forzare il caricamento senza dialoghi:

```
require-resource-pack=true
```

In alternativa, i giocatori possono installare `ObsidianDragon-RP.zip` manualmente nei loro pacchetti di risorse.

## Comandi

| Comando | Descrizione | Permesso |
|---|---|---|
| `/mythicgear give <item> [giocatore]` | Dà un item del set | `mythicgear.admin` |
| `/mythicgear list` | Elenca gli item disponibili | `mythicgear.admin` |

Alias: `/mg`

Permesso: `mythicgear.admin` (default: op)

## Configurazione

### `items.yml`

Definisce gli item del set. Esempio:

```yaml
dragon_leggings:
  material: NETHERITE_LEGGINGS
  name: "<red>Gambiere del Drago d'Ossidiana"
  lore:
    - "<gray>Set <red>Drago d'Ossidiana"
    - "<gray>Full set: <red>doppia vita"
  set: dragon
  piece: leggings
  glider: false
  enchants:
    protection: 10
    unbreaking: 5
  attributes:
    armor:
      amount: 6
      operation: ADD_NUMBER
      slot: LEGS
    armor_toughness:
      amount: 3
      operation: ADD_NUMBER
      slot: LEGS
    knockback_resistance:
      amount: 0.1
      operation: ADD_NUMBER
      slot: LEGS
  equippable:
    slot: CHEST
    model: mythicgear:dragon_chestplate
    sound: minecraft:item.armor.equip_elytra
```

> Nota: gli attributi base (armor, armor_toughness, knockback_resistance) vanno dichiarati esplicitamente: in Paper 26.2 i modifier aggiunti via API sovrascrivono quelli di default del materiale.

### `sets.yml`

Definisce i set e i relativi bonus:

```yaml
dragon:
  name: "Drago d'Ossidiana"
  pieces:
    - dragon_helmet
    - dragon_chestplate
    - dragon_leggings
    - dragon_boots
  max_health_bonus: 20
```

## Modello custom (resource pack)

La cartella `resourcepack/` contiene il modello `equipment` che unisce armatura netherite ed elytra:

- `assets/mythicgear/equipment/dragon_chestplate.json` — layer `humanoid` (armatura netherite) + layer `wings` (elytra)
- Le texture sono quelle vanilla (nessun file PNG extra)

## Sviluppo

```bash
./gradlew build          # compila il plugin
./deploy.sh              # build + deploy in Scrivania/server + resource pack automatico
```

`deploy.sh`:
1. compila il plugin e lo copia in `plugins/`;
2. sincronizza `items.yml`/`sets.yml`;
3. rigenera `ObsidianDragon-RP.zip`;
4. avvia un HTTP server e aggiorna `server.properties` con URL e sha1.

## Changelog

Vedi [changelog.md](changelog.md).

## Licenza

Tutti i diritti riservati.
