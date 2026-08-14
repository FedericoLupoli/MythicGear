#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SERVER_DIR="${SERVER_DIR:-/home/federico/Scrivania/server}"
PORT="${RP_PORT:-8010}"
PACK_NAME="ObsidianDragon-RP.zip"

cd "$PROJECT_DIR"

echo "==> Building plugin"
./gradlew build --no-daemon -q

echo "==> Deploying plugin jar + configs"
mkdir -p "$SERVER_DIR/plugins"
cp -f build/libs/MythicGear.jar "$SERVER_DIR/plugins/MythicGear.jar"
cp -f src/main/resources/items.yml "$SERVER_DIR/plugins/MythicGear/items.yml"
cp -f src/main/resources/sets.yml "$SERVER_DIR/plugins/MythicGear/sets.yml"

echo "==> Building resource pack"
(cd resourcepack && rm -f "$PROJECT_DIR/$PACK_NAME" && zip -qr "$PROJECT_DIR/$PACK_NAME" pack.mcmeta assets)
cp -f "$PROJECT_DIR/$PACK_NAME" "$SERVER_DIR/$PACK_NAME"

SHA1="$(sha1sum "$SERVER_DIR/$PACK_NAME" | awk '{print $1}')"

echo "==> Ensuring HTTP server on port $PORT"
if ! pgrep -f "http.server $PORT" >/dev/null 2>&1; then
    setsid nohup python3 -m http.server "$PORT" --bind 0.0.0.0 --directory "$SERVER_DIR" \
        >/dev/null 2>&1 < /dev/null &
    echo "    HTTP server started (pid $!)"
else
    echo "    HTTP server already running"
fi

IP="$(ip route get 1.1.1.1 2>/dev/null | grep -oE 'src [0-9.]+' | awk '{print $2}')"
IP="${IP:-127.0.0.1}"
URL="http://$IP:$PORT/$PACK_NAME"

echo "==> Updating server.properties"
sed -i "s|^resource-pack=.*|resource-pack=$URL|" "$SERVER_DIR/server.properties"
sed -i "s|^resource-pack-sha1=.*|resource-pack-sha1=$SHA1|" "$SERVER_DIR/server.properties"

echo
echo "Deployed to $SERVER_DIR"
echo "  plugin:  $SERVER_DIR/plugins/MythicGear.jar"
echo "  pack:    $SERVER_DIR/$PACK_NAME"
echo "  url:     $URL"
echo "  sha1:    $SHA1"
echo
echo "Riavvia il server per applicare il nuovo jar e server.properties."
