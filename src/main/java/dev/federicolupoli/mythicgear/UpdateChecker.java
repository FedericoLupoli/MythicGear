package dev.federicolupoli.mythicgear;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class UpdateChecker {

    private static final String API_URL =
            "https://api.github.com/repos/FedericoLupoli/MythicGear/releases/latest";
    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final JavaPlugin plugin;
    private String latestVersion;

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void check(Player player) {
        if (!player.hasPermission("mythicgear.alert")) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String remote = fetchLatestVersion();
            if (remote == null) {
                return;
            }
            latestVersion = remote;
            String local = plugin.getDescription().getVersion();
            if (isNewer(remote, local)) {
                Component msg = MM.deserialize(
                        "<gold>[MythicGear] <yellow>Nuova versione disponibile: <white>"
                        + remote
                        + " <yellow>- <aqua>https://github.com/FedericoLupoli/MythicGear/releases");
                Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(msg));
            }
        });
    }

    private String fetchLatestVersion() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            if (conn.getResponseCode() != 200) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            String body = sb.toString();
            int idx = body.indexOf("\"tag_name\"");
            if (idx < 0) {
                return null;
            }
            int start = body.indexOf('"', idx + 11) + 1;
            int end = body.indexOf('"', start);
            return body.substring(start, end).replace("v", "");
        } catch (Exception e) {
            plugin.getLogger().warning("Update check failed: " + e.getMessage());
            return null;
        }
    }

    private boolean isNewer(String remote, String local) {
        String[] r = remote.split("\\.");
        String[] l = local.split("\\.");
        int len = Math.max(r.length, l.length);
        for (int i = 0; i < len; i++) {
            int rv = i < r.length ? parseInt(r[i]) : 0;
            int lv = i < l.length ? parseInt(l[i]) : 0;
            if (rv > lv) return true;
            if (rv < lv) return false;
        }
        return false;
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
