package dev.federicolupoli.mythicgear.item;

import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;

public record ItemSpec(
        String id,
        Material material,
        String name,
        List<String> lore,
        String set,
        String piece,
        boolean glider,
        boolean noGlint,
        NamespacedKey itemModel,
        Map<Enchantment, Integer> enchants,
        List<ItemModifier> modifiers,
        EquippableSpec equippable) {

    public record ItemModifier(Attribute attribute, AttributeModifier modifier) {
    }

    public record EquippableSpec(EquipmentSlot slot, NamespacedKey model, NamespacedKey sound) {
    }
}
