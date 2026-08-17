package dev.federicolupoli.mythicgear;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import dev.federicolupoli.mythicgear.command.MythicGearCommand;
import dev.federicolupoli.mythicgear.effect.EffectManager;
import dev.federicolupoli.mythicgear.item.GearRegistry;
import dev.federicolupoli.mythicgear.listener.AntiExploitListener;
import dev.federicolupoli.mythicgear.listener.CraftListener;
import dev.federicolupoli.mythicgear.listener.DragonSwordListener;
import dev.federicolupoli.mythicgear.listener.EquipmentListener;
import dev.federicolupoli.mythicgear.listener.FireStaffListener;
import dev.federicolupoli.mythicgear.listener.LightningStaffListener;
import dev.federicolupoli.mythicgear.listener.HealingStaffListener;
import dev.federicolupoli.mythicgear.listener.FlightStaffListener;
import dev.federicolupoli.mythicgear.set.SetRegistry;

public final class MythicGear extends JavaPlugin {

    private GearRegistry registry;
    private SetRegistry setRegistry;
    private EffectManager effectManager;
    private CraftListener craftListener;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        saveResource("items.yml", false);
        saveResource("sets.yml", false);

        registry = new GearRegistry(this);
        registry.load();

        setRegistry = new SetRegistry(this);
        setRegistry.load();

        effectManager = new EffectManager(this);
        effectManager.load();

        Bukkit.getPluginManager().registerEvents(new EquipmentListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DragonSwordListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FireStaffListener(), this);
        Bukkit.getPluginManager().registerEvents(new LightningStaffListener(), this);
        Bukkit.getPluginManager().registerEvents(new HealingStaffListener(), this);
        Bukkit.getPluginManager().registerEvents(new FlightStaffListener(), this);
        craftListener = new CraftListener(this);
        craftListener.registerRecipe();
        Bukkit.getPluginManager().registerEvents(craftListener, this);
        Bukkit.getPluginManager().registerEvents(new AntiExploitListener(this), this);

        updateChecker = new UpdateChecker(this);
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                updateChecker.check(event.getPlayer());
            }
        }, this);

        MythicGearCommand command = new MythicGearCommand(this);
        PluginCommand pluginCommand = getCommand("mythicgear");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }

        getLogger().info("MythicGear enabled.");
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            effectManager.removeAll(player);
        }
        getLogger().info("MythicGear disabled.");
    }

    public GearRegistry getRegistry() {
        return registry;
    }

    public SetRegistry getSetRegistry() {
        return setRegistry;
    }

    public EffectManager getEffectManager() {
        return effectManager;
    }

    public void reload() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            effectManager.removeAll(player);
        }
        registry.load();
        setRegistry.load();
        effectManager.load();
        craftListener.unregisterRecipe();
        craftListener.registerRecipe();
        for (Player player : Bukkit.getOnlinePlayers()) {
            effectManager.refresh(player);
        }
        getLogger().info("MythicGear config reloaded.");
    }
}
