package dev.federicolupoli.mythicgear.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import dev.federicolupoli.mythicgear.MythicGear;

public final class EquipmentListener implements Listener {

    private final MythicGear plugin;

    public EquipmentListener(MythicGear plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onArmorChange(PlayerArmorChangeEvent event) {
        plugin.getEffectManager().refresh(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getEffectManager().refresh(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        plugin.getEffectManager().refresh(event.getPlayer());
    }
}
