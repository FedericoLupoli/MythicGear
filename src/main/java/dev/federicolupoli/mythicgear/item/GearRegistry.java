package dev.federicolupoli.mythicgear.item;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import dev.federicolupoli.mythicgear.Keys;
import dev.federicolupoli.mythicgear.MythicGear;

public final class GearRegistry {

    private final MythicGear plugin;
    private final Map<String, ItemSpec> items = new HashMap<>();

    public GearRegistry(MythicGear plugin) {
        this.plugin = plugin;
    }

    public void load() {
        items.clear();
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "items.yml"));
        for (String id : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(id);
            if (section == null) {
                continue;
            }
            items.put(id, parse(section));
        }
        plugin.getLogger().info("Loaded " + items.size() + " custom items");
    }

    private ItemSpec parse(ConfigurationSection section) {
        String id = section.getName();
        Material material = Material.matchMaterial(section.getString("material", "STONE"));

        Map<Enchantment, Integer> enchants = new HashMap<>();
        ConfigurationSection enchSection = section.getConfigurationSection("enchants");
        if (enchSection != null) {
            for (String key : enchSection.getKeys(false)) {
                Enchantment enchantment = RegistryAccess.registryAccess()
                        .getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(key));
                if (enchantment == null) {
                    plugin.getLogger().warning("Unknown enchantment '" + key + "' in item '" + id + "'");
                    continue;
                }
                enchants.put(enchantment, enchSection.getInt(key));
            }
        }

        List<ItemSpec.ItemModifier> modifiers = new ArrayList<>();
        ConfigurationSection attrSection = section.getConfigurationSection("attributes");
        if (attrSection != null) {
            for (String key : attrSection.getKeys(false)) {
                Attribute attribute = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(key));
                if (attribute == null) {
                    plugin.getLogger().warning("Unknown attribute '" + key + "' in item '" + id + "'");
                    continue;
                }
                ConfigurationSection mod = attrSection.getConfigurationSection(key);
                double amount = mod.getDouble("amount");
                AttributeModifier.Operation operation = AttributeModifier.Operation
                        .valueOf(mod.getString("operation", "ADD_NUMBER").toUpperCase());
                EquipmentSlotGroup slot = EquipmentSlotGroup.getByName(mod.getString("slot", "ANY")
                        .toUpperCase().replace("_", ""));
                NamespacedKey modifierKey = new NamespacedKey("mythicgear", id + "_" + key);
                modifiers.add(new ItemSpec.ItemModifier(attribute,
                        new AttributeModifier(modifierKey, amount, operation, slot)));
            }
        }

        return new ItemSpec(
                id,
                material,
                section.getString("name"),
                section.getStringList("lore"),
                section.getString("set"),
                section.getString("piece"),
                section.getBoolean("glider", false),
                section.getBoolean("no_glint", false),
                section.getString("item_model", null) != null
                        ? NamespacedKey.fromString(section.getString("item_model"))
                        : null,
                enchants,
                modifiers,
                parseEquippable(section.getConfigurationSection("equippable")));
    }

    private ItemSpec.EquippableSpec parseEquippable(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        EquipmentSlot slot = EquipmentSlot.valueOf(section.getString("slot", "CHEST").toUpperCase());
        NamespacedKey model = section.getString("model") != null
                ? NamespacedKey.fromString(section.getString("model"))
                : null;
        NamespacedKey sound = section.getString("sound") != null
                ? NamespacedKey.fromString(section.getString("sound"))
                : null;
        return new ItemSpec.EquippableSpec(slot, model, sound);
    }

    public ItemSpec get(String id) {
        return items.get(id);
    }

    public ItemStack create(String id) {
        ItemSpec spec = items.get(id);
        return spec == null ? null : ItemFactory.create(spec);
    }

    public Set<String> ids() {
        return items.keySet();
    }

    public boolean isCustom(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(Keys.ITEM_ID, PersistentDataType.STRING);
    }
}
