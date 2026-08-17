package dev.federicolupoli.mythicgear.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import dev.federicolupoli.mythicgear.Keys;

public final class FlightStaffListener implements Listener {

    private static final String STAFF_ID = "flight_staff";
    private static final int COOLDOWN_TICKS = 20;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!isFlightStaff(player.getInventory().getItemInMainHand())) {
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

        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage("\u00A7aVolo disattivato.");
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.sendMessage("\u00A7aVolo attivato!");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.getAllowFlight() && !player.isOp()) {
            player.setAllowFlight(false);
            player.setFlying(false);
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

    private boolean isFlightStaff(ItemStack item) {
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
