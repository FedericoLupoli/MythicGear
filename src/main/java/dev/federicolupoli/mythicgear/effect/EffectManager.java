package dev.federicolupoli.mythicgear.effect;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import dev.federicolupoli.mythicgear.MythicGear;
import dev.federicolupoli.mythicgear.set.Set;

public final class EffectManager {

    private final MythicGear plugin;
    private final List<SetEffect> effects = new ArrayList<>();

    public EffectManager(MythicGear plugin) {
        this.plugin = plugin;
    }

    public void load() {
        effects.clear();
        for (Set set : plugin.getSetRegistry().all().values()) {
            if (set.maxHealthBonus() > 0) {
                effects.add(new DoubleHealthEffect(set));
            }
        }
        plugin.getLogger().info("Registered " + effects.size() + " set effects");
    }

    public void refresh(Player player) {
        for (SetEffect effect : effects) {
            effect.refresh(player);
        }
    }

    public void removeAll(Player player) {
        for (SetEffect effect : effects) {
            effect.remove(player);
        }
    }
}
