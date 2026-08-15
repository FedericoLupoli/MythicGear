package dev.federicolupoli.mythicgear.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import dev.federicolupoli.mythicgear.Keys;
import dev.federicolupoli.mythicgear.MythicGear;

public final class DragonSwordListener implements Listener {

    private static final String SWORD_ID = "dragon_sword";
    private static final int COOLDOWN_TICKS = 40;
    private static final NamespacedKey CLOUD_SOURCE_KEY = new NamespacedKey("mythicgear", "dragon_breath_source");

    private final MythicGear plugin;

    public DragonSwordListener(MythicGear plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        if (!isDragonSword(player.getInventory().getItemInMainHand())) {
            return;
        }
        if (player.hasCooldown(Material.NETHERITE_SWORD)) {
            return;
        }
        player.setCooldown(Material.NETHERITE_SWORD, COOLDOWN_TICKS);

        DragonFireball fireball = player.launchProjectile(DragonFireball.class,
                player.getLocation().getDirection().multiply(1.5));
        fireball.setYield(0);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof DragonFireball fireball)) {
            return;
        }
        if (fireball.getShooter() != null && fireball.getShooter() instanceof Player player) {
            spawnBreathCloud(fireball.getLocation(), player);
        }
    }

    @EventHandler
    public void onCloudApply(AreaEffectCloudApplyEvent event) {
        AreaEffectCloud cloud = event.getEntity();
        if (cloud.getParticle() != Particle.DRAGON_BREATH) {
            return;
        }
        String source = cloud.getPersistentDataContainer().get(CLOUD_SOURCE_KEY, PersistentDataType.STRING);
        event.getAffectedEntities().removeIf(entity ->
                isDragonImmune(entity)
                || (source != null && entity.getUniqueId().toString().equals(source)));
    }

    private void spawnBreathCloud(Location location, Player source) {
        AreaEffectCloud cloud = location.getWorld().spawn(location, AreaEffectCloud.class);
        cloud.setRadius(3.0f);
        cloud.setRadiusPerTick(-0.05f);
        cloud.setDuration(200);
        cloud.setWaitTime(10);
        cloud.setParticle(Particle.DRAGON_BREATH, 1.0f);
        cloud.setSource(source);
        cloud.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 1), false);
        cloud.getPersistentDataContainer().set(CLOUD_SOURCE_KEY, PersistentDataType.STRING,
                source.getUniqueId().toString());
    }

    private boolean isDragonImmune(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        for (ItemStack piece : player.getInventory().getArmorContents()) {
            if (isDragonPiece(piece)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDragonPiece(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        String set = meta.getPersistentDataContainer().get(Keys.SET_ID, PersistentDataType.STRING);
        return "dragon".equals(set);
    }

    private boolean isDragonSword(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        String id = meta.getPersistentDataContainer().get(Keys.ITEM_ID, PersistentDataType.STRING);
        return SWORD_ID.equals(id);
    }
}
