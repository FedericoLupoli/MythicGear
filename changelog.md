# Changelog

## [1.0.0] - 2026-08-14

Prima release del plugin MythicGear.

### Aggiunto

- **Set "Drago d'Ossidiana"** completo di 4 pezzi: elmo, pettorale, gambiere e stivali (netherite).
- **Bonus full set**: vita raddoppiata (+20 max health) tramite `max_health_bonus` in `sets.yml`, gestito dinamicamente in base ai pezzi indossati.
- **Pettorale ibrido armatura + elytra**:
  - componente `glider` per planare;
  - componente `equippable` per l'indossamento nello slot petto;
  - **modello equipment custom** (resource pack) che unisce il layer dell'armatura netherite (`humanoid`) al layer delle ali (`wings`), così si vede sia l'armatura sia l'elytra.
- **Statistiche esplicite per pezzo** in `items.yml`: armor, armor_toughness, knockback_resistance (+movement_speed sugli stivali). Totale set: 20 punti armatura, 12 toughness, 0.4 knockback resistance, +5% velocità.
- **Incantesimi personalizzati** per pezzo (protection, unbreaking, respiration, aqua_affinity, fire_protection, feather_falling, depth_strider).
- **Comando `/mythicgear`** (alias `/mg`) con sottocomandi `give` e `list` e tab completion per item e giocatori.
- **Ricette di crafting** registrate per i pezzi del set.
- **Protezione anti-exploit**: gli item MythicGear non possono essere rinominati o riparati in incudine/grindstone.
- **Resource pack** (`resourcepack/`) con modello equipment custom in namespace `mythicgear`.
- **Automazione deploy** (`deploy.sh`): build del plugin, sincronizzazione dei config, rigenerazione del resource pack, calcolo sha1 e aggiornamento automatico di `server.properties` (`resource-pack` + `resource-pack-sha1`), con HTTP server per il caricamento automatico del pack da parte del client.

### Corretto

- **Armatura mancante su gambiere e stivali**: in Paper 26.2 i modifier aggiunti via `meta.addAttributeModifier()` sostituiscono i default del materiale, quindi i pezzi con bonus aggiuntivi perdevano i punti armatura netherite. Ora tutti i base stats sono dichiarati esplicitamente in `items.yml` per ogni pezzo, garantendo 20/20 punti armatura con il set completo.
