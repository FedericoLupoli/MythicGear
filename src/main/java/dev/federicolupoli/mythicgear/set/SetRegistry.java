package dev.federicolupoli.mythicgear.set;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import dev.federicolupoli.mythicgear.MythicGear;

public final class SetRegistry {

    private final MythicGear plugin;
    private final Map<String, Set> sets = new HashMap<>();

    public SetRegistry(MythicGear plugin) {
        this.plugin = plugin;
    }

    public void load() {
        sets.clear();
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "sets.yml"));
        for (String id : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(id);
            if (section == null) {
                continue;
            }
            List<String> pieces = new ArrayList<>(section.getStringList("pieces"));
            sets.put(id, new Set(id, section.getString("name", id), pieces,
                    section.getDouble("max_health_bonus", 0)));
        }
        plugin.getLogger().info("Loaded " + sets.size() + " sets");
    }

    public Set get(String id) {
        return sets.get(id);
    }

    public Map<String, Set> all() {
        return sets;
    }
}
