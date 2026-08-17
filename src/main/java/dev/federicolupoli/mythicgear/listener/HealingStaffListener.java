package dev.federicolupoli.mythicgear.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import dev.federicolupoli.mythicgear.Keys;

public final class HealingStaffListener implements Listener {

    private static final String STAFF_ID = "healing_staff";
    private static final int COOLDOWN_TICKS = 60;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!isHealingStaff(player.getInventory().getItemInMainHand())) {
            return;
        }
        if (!hasFullMageSet(player)) {
            player.sendMessage("\u00A7cDevi indossare il set del Mago per usare questa staffa!");
            return;
        }
        if (player.hasCooldown(Material.BOW)) {
            return;
        }
        event.setCancelled(true);
        player.setCooldown(Material.BOW, COOLDOWN_TICKS);

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 0));
    }

    private boolean hasFullMageSet(Player player) {
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
            if ("mage".equals(set)) {
                matched++;
            }
        }
        return matched >= 4;
    }

    private boolean isHealingStaff(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        String id = meta.getPersistentDataContainer().get(Keys.ITEM_ID, PersistentDataType.STRING);
        return STAFF_ID.equals(id);
    }
}
