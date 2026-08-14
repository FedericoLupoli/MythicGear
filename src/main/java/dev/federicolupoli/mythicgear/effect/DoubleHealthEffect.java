package dev.federicolupoli.mythicgear.effect;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import dev.federicolupoli.mythicgear.Keys;
import dev.federicolupoli.mythicgear.set.Set;

public final class DoubleHealthEffect implements SetEffect {

    private static final NamespacedKey MODIFIER_KEY = new NamespacedKey("mythicgear", "set_health_bonus");

    private final Set set;

    public DoubleHealthEffect(Set set) {
        this.set = set;
    }

    @Override
    public boolean isActive(Player player) {
        int matched = 0;
        for (ItemStack piece : player.getInventory().getArmorContents()) {
            if (piece == null || piece.isEmpty()) {
                continue;
            }
            ItemMeta meta = piece.getItemMeta();
            if (meta == null) {
                continue;
            }
            String set = meta.getPersistentDataContainer().get(Keys.SET_ID, PersistentDataType.STRING);
            if (set != null && set.equals(this.set.id())) {
                matched++;
            }
        }
        return matched >= this.set.pieces().size();
    }

    @Override
    public void apply(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute == null || set.maxHealthBonus() <= 0) {
            return;
        }
        if (attribute.getModifier(MODIFIER_KEY) != null) {
            return;
        }
        attribute.addModifier(new AttributeModifier(MODIFIER_KEY, set.maxHealthBonus(), AttributeModifier.Operation.ADD_NUMBER));
    }

    @Override
    public void remove(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute == null) {
            return;
        }
        attribute.removeModifier(MODIFIER_KEY);
        player.setHealth(Math.min(player.getHealth(), attribute.getBaseValue()));
    }

    @Override
    public void refresh(Player player) {
        if (isActive(player)) {
            apply(player);
        } else {
            remove(player);
        }
    }
}
