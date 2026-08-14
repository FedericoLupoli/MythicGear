#!/usr/bin/env bash
# ============================================================================
# MythicGear deploy
#
# Compila il plugin, lo deploya in una cartella server, rigenera il resource
# pack e configura il caricamento automatico da parte del client.
#
# Configurazione (in ordine di priorita': argomento > variabile d'ambiente
# > .deployrc > default):
#   SERVER_DIR  cartella del server (default: $HOME/Scrivania/server)
#   RP_PORT     porta dell'HTTP server (default: 8010)
#   RP_HOST     host annunciato nel resource-pack (default: auto-detect IP)
#   RP_URL      URL completo del pack (default: http://<host>:<port>/<pack>)
#   PACK_NAME   nome del file zip del pack (default: ObsidianDragon-RP.zip)
#
# Esempi:
#   ./deploy.sh /percorso/al/server
#   SERVER_DIR=/percorso/al/server ./deploy.sh
#   RP_PORT=9000 ./deploy.sh
# ============================================================================
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

PACK_NAME="${PACK_NAME:-ObsidianDragon-RP.zip}"
SERVER_DIR="${SERVER_DIR:-}"
RP_PORT="${RP_PORT:-8010}"
RP_HOST="${RP_HOST:-}"
RP_URL="${RP_URL:-}"

# Configurazione utente opzionale (vedi .deployrc.example)
if [ -f "$PROJECT_DIR/.deployrc" ]; then
    # shellcheck source=/dev/null
    source "$PROJECT_DIR/.deployrc"
fi

# Priorita': primo argomento = cartella server
if [ "$#" -gt 0 ]; then
    SERVER_DIR="$1"
fi
SERVER_DIR="${SERVER_DIR:-$HOME/Scrivania/server}"

cd "$PROJECT_DIR"

# ---------------------------------------------------------------------------
# 1. Build
# ---------------------------------------------------------------------------
echo "==> Building plugin"
./gradlew build --no-daemon -q

# ---------------------------------------------------------------------------
# 2. Plugin jar + configs
# ---------------------------------------------------------------------------
echo "==> Deploying plugin jar + configs"
mkdir -p "$SERVER_DIR/plugins"
cp -f build/libs/MythicGear.jar "$SERVER_DIR/plugins/MythicGear.jar"
mkdir -p "$SERVER_DIR/plugins/MythicGear"
cp -f src/main/resources/items.yml "$SERVER_DIR/plugins/MythicGear/items.yml"
cp -f src/main/resources/sets.yml "$SERVER_DIR/plugins/MythicGear/sets.yml"

# ---------------------------------------------------------------------------
# 3. Resource pack
# ---------------------------------------------------------------------------
echo "==> Building resource pack"
(cd resourcepack && rm -f "$PROJECT_DIR/$PACK_NAME" && zip -qr "$PROJECT_DIR/$PACK_NAME" pack.mcmeta assets)
cp -f "$PROJECT_DIR/$PACK_NAME" "$SERVER_DIR/$PACK_NAME"

SHA1="$(sha1sum "$SERVER_DIR/$PACK_NAME" | awk '{print $1}')"

# ---------------------------------------------------------------------------
# 4. HTTP server per il resource pack
# ---------------------------------------------------------------------------
if ! curl -sf --max-time 3 "http://127.0.0.1:$RP_PORT/$PACK_NAME" -o /dev/null 2>/dev/null; then
    if command -v python3 >/dev/null 2>&1; then
        setsid nohup python3 -m http.server "$RP_PORT" --bind 0.0.0.0 --directory "$SERVER_DIR" \
            >/dev/null 2>&1 < /dev/null &
        echo "    HTTP server started on port $RP_PORT (pid $!)"
        sleep 1
    else
        echo "    WARNING: python3 non trovato, il resource pack non viene servito."
        echo "    Configura manualmente resource-pack e resource-pack-sha1 in server.properties."
    fi
else
    echo "    HTTP server already serving on port $RP_PORT"
fi

# ---------------------------------------------------------------------------
# 5. Aggiorna server.properties
# ---------------------------------------------------------------------------
detect_host() {
    local ip=""
    if command -v ip >/dev/null 2>&1; then
        ip="$(ip route get 1.1.1.1 2>/dev/null | sed -n 's/.*src \([0-9.]*\).*/\1/p' | head -n 1)"
    fi
    if [ -z "$ip" ] && command -v hostname >/dev/null 2>&1; then
        ip="$(hostname -I 2>/dev/null | awk '{print $1}')"
    fi
    if [ -z "$ip" ] && command -v hostname >/dev/null 2>&1; then
        ip="$(hostname 2>/dev/null)"
    fi
    echo "${ip:-127.0.0.1}"
}

set_prop() {
    local file="$1" key="$2" value="$3"
    if [ -f "$file" ] && grep -q "^${key}=" "$file" 2>/dev/null; then
        sed -i "s|^${key}=.*|${key}=${value}|" "$file"
    else
        printf '%s=%s\n' "$key" "$value" >> "$file"
    fi
}

PROPS="$SERVER_DIR/server.properties"
if [ -n "$RP_URL" ]; then
    URL="$RP_URL"
else
    URL="http://$(detect_host):$RP_PORT/$PACK_NAME"
fi

echo "==> Updating server.properties"
set_prop "$PROPS" resource-pack "$URL"
set_prop "$PROPS" resource-pack-sha1 "$SHA1"

echo
echo "Deployed to $SERVER_DIR"
echo "  plugin:  $SERVER_DIR/plugins/MythicGear.jar"
echo "  pack:    $SERVER_DIR/$PACK_NAME"
echo "  url:     $URL"
echo "  sha1:    $SHA1"
echo
echo "Riavvia il server per applicare il nuovo jar e server.properties."
