# Changelog

## Versioning (convenzione)

Formato `X.Y.Z`:

- **X** — major updates;
- **Y** — incrementato a ogni nuovo **armor set** aggiunto;
- **Z** — fix e feature minori tra le varie armature.

(Salvo diverse indicazioni specifiche.)

## [1.1.1] - 2026-08-15

### Aggiunto

- **Spada del Drago d'Ossidiana** (`dragon_sword`): danno 12, incantesimi alti, parte del set dragon (`set: dragon`, `piece: sword`). Inclusa in `/mythicgear list` e `/mythicgear giveset dragon`. Ottenibile solo via comando, nessuna ricetta.
- **Abilità "soffio di drago"**: click destro con la spada lancia una palla di fuoco da drago che a impatto crea una nube di dragon breath (danno istantaneo), con cooldown 2s; il lanciatore non subisce il danno del proprio soffio.
- **Immunità al dragon breath**: chi indossa un qualsiasi pezzo del set dragon è immune al dragon breath (sia quello della spada sia quello dell'ender dragon).

### Corretto

- **Avvio plugin con slot `MAIN_HAND`**: in Paper 26.2 le costanti `EquipmentSlotGroup` sono `MAINHAND`/`OFFHAND` (senza underscore). Il parsing ora normalizza gli underscore, accettando entrambe le scritture.
- **Errore particle dragon breath**: in 26.2 `Particle.DRAGON_BREATH` richiede un parametro `Float` (scala), ora passato esplicitamente.

## [1.1.0] - 2026-08-15

### Aggiunto

- **Comando `/mythicgear giveset <set> [giocatore]`**: dà tutti i pezzi del set con un solo comando (con tab completion su set e giocatori).
- **Comando `/mythicgear reload`**: ricarica `items.yml`, `sets.yml` e gli effetti, e ri-registra la ricetta, senza riavviare il server. Agli eventuali giocatori online gli effetti vengono rimossi e riapplicati.

### Corretto

- **Resource pack incompatibile**: il client 26.2 dichiara `pack_format: 88` (`resource_major` nel `version.json` del client jar); il pack usava `91` e risultava "incompatibile". Ora è `88`.

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
