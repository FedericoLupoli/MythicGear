package dev.federicolupoli.mythicgear;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import dev.federicolupoli.mythicgear.command.MythicGearCommand;
import dev.federicolupoli.mythicgear.effect.EffectManager;
import dev.federicolupoli.mythicgear.item.GearRegistry;
import dev.federicolupoli.mythicgear.listener.AntiExploitListener;
import dev.federicolupoli.mythicgear.listener.CraftListener;
import dev.federicolupoli.mythicgear.listener.EquipmentListener;
import dev.federicolupoli.mythicgear.set.SetRegistry;

public final class MythicGear extends JavaPlugin {

    private GearRegistry registry;
    private SetRegistry setRegistry;
    private EffectManager effectManager;

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
        CraftListener craftListener = new CraftListener(this);
        craftListener.registerRecipe();
        Bukkit.getPluginManager().registerEvents(craftListener, this);
        Bukkit.getPluginManager().registerEvents(new AntiExploitListener(this), this);

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
}
