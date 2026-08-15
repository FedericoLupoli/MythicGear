package dev.federicolupoli.mythicgear.listener;

import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import dev.federicolupoli.mythicgear.MythicGear;

public final class CraftListener implements Listener {

    private final MythicGear plugin;
    private final NamespacedKey recipeKey;

    public CraftListener(MythicGear plugin) {
        this.plugin = plugin;
        this.recipeKey = new NamespacedKey(plugin, "dragon_chestplate");
    }

    public void registerRecipe() {
        ItemStack result = plugin.getRegistry().create("dragon_chestplate");
        if (result == null) {
            return;
        }
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, result);
        recipe.shape("EC");
        recipe.setIngredient('E', Material.ELYTRA);
        recipe.setIngredient('C', Material.NETHERITE_CHESTPLATE);
        Bukkit.addRecipe(recipe);
        plugin.getLogger().info("Registered recipe " + recipeKey.asString());
    }

    public void unregisterRecipe() {
        Bukkit.removeRecipe(recipeKey);
        plugin.getLogger().info("Unregistered recipe " + recipeKey.asString());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPrepare(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (!(recipe instanceof CraftingRecipe craftingRecipe)) {
            return;
        }
        if (!craftingRecipe.getKey().equals(recipeKey)) {
            return;
        }

        ItemStack chestplate = null;
        boolean hasElytra = false;
        for (ItemStack ingredient : event.getInventory().getMatrix()) {
            if (ingredient == null || ingredient.isEmpty()) {
                continue;
            }
            if (ingredient.getType() == Material.NETHERITE_CHESTPLATE) {
                if (plugin.getRegistry().isCustom(ingredient)) {
                    event.getInventory().setResult(null);
                    return;
                }
                chestplate = ingredient.clone();
            } else if (ingredient.getType() == Material.ELYTRA) {
                hasElytra = true;
            }
        }

        if (chestplate == null || !hasElytra) {
            return;
        }

        ItemStack result = plugin.getRegistry().create("dragon_chestplate");
        if (result == null) {
            return;
        }

        final ItemStack enchantSource = chestplate;
        result.editMeta(meta -> {
            for (Map.Entry<Enchantment, Integer> entry : enchantSource.getEnchantments().entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        });
        event.getInventory().setResult(result);
    }
}
