package dev.federicolupoli.mythicgear.effect;

import org.bukkit.entity.Player;

public interface SetEffect {

    boolean isActive(Player player);

    void apply(Player player);

    void remove(Player player);

    void refresh(Player player);
}
