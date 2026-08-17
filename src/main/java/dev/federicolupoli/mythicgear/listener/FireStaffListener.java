package dev.federicolupoli.mythicgear.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import dev.federicolupoli.mythicgear.Keys;

public final class FireStaffListener implements Listener {

    private static final String STAFF_ID = "fire_staff";
    private static final int COOLDOWN_TICKS = 40;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!isFireStaff(player.getInventory().getItemInMainHand())) {
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

        Vector dir = player.getLocation().getDirection();
        Location eye = player.getEyeLocation();
        double spread = Math.toRadians(5);
        for (int i = 0; i < 5; i++) {
            double angle = (i - 2) * spread;
            Vector vel = dir.clone().rotateAroundY(angle).multiply(2.0);
            SmallFireball fireball = player.getWorld().spawn(
                    eye.clone().add(dir.clone().multiply(1.5)),
                    SmallFireball.class);
            fireball.setDirection(vel);
            fireball.setShooter(player);
            fireball.setYield(0);
            fireball.setIsIncendiary(true);
        }
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

    private boolean isFireStaff(ItemStack item) {
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
