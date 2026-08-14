package dev.federicolupoli.mythicgear.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import dev.federicolupoli.mythicgear.Keys;

public final class ItemFactory {

    private ItemFactory() {
    }

    public static ItemStack create(ItemSpec spec) {
        ItemStack item = new ItemStack(spec.material());
        ItemMeta meta = item.getItemMeta();
        MiniMessage mm = MiniMessage.miniMessage();

        meta.customName(mm.deserialize(spec.name()));

        List<Component> lore = new ArrayList<>(spec.lore().size());
        for (String line : spec.lore()) {
            lore.add(mm.deserialize(line));
        }
        meta.lore(lore);

        for (Map.Entry<Enchantment, Integer> entry : spec.enchants().entrySet()) {
            meta.addEnchant(entry.getKey(), entry.getValue(), true);
        }

        for (ItemSpec.ItemModifier itemModifier : spec.modifiers()) {
            meta.addAttributeModifier(itemModifier.attribute(), itemModifier.modifier());
        }

        meta.setGlider(spec.glider());

        meta.getPersistentDataContainer().set(Keys.ITEM_ID, PersistentDataType.STRING, spec.id());
        meta.getPersistentDataContainer().set(Keys.SET_ID, PersistentDataType.STRING, spec.set());
        meta.getPersistentDataContainer().set(Keys.PIECE, PersistentDataType.STRING, spec.piece());

        item.setItemMeta(meta);

        if (spec.equippable() != null) {
            ItemSpec.EquippableSpec equippable = spec.equippable();
            Equippable.Builder builder = Equippable.equippable(equippable.slot());
            if (equippable.model() != null) {
                builder.assetId(equippable.model());
            }
            if (equippable.sound() != null) {
                builder.equipSound(equippable.sound());
            }
            item.setData(DataComponentTypes.EQUIPPABLE, builder.build());
        }

        return item;
    }
}
