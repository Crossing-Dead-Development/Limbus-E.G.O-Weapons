package me.yisang.limbus;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;
import org.bukkit.event.Listener;

public class dacapo implements EGOWeapon, Listener {
    private final LimbusEGOWeapons plugin;

    public dacapo(LimbusEGOWeapons plugin) { this.plugin = plugin; }

    @Override
    public String getId() { return "dacapo"; }

    @Override
    public ItemStack createItem() {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.translateHexColorCodes("&#FFFFFFDaCapo"));
            meta.setLore(List.of(plugin.translateHexColorCodes("&x&F&F&F&F&F&F來自廢墟的最華麗的演出，即將拉開帷幕！")));
            meta.setCustomModelData(1007);
            meta.setUnbreakable(true);
            meta.setItemModel(NamespacedKey.fromString("dacapo:dacapo"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.getPersistentDataContainer().set(
                    plugin.getItemIdKey(), PersistentDataType.STRING, "dacapo");
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public void handleMelee(EntityDamageByEntityEvent event, Player attacker) {
        if (attacker.hasMetadata("lsmp_custom_damage")) return;

        if (event.getEntity() instanceof LivingEntity target) {
            event.setCancelled(true);
            boolean special = Math.random() < 0.40;

            new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    // 修正：用 PDC 確認玩家手上還拿著 DaCapo，而非只判斷是否空手
                    ItemStack hand = attacker.getInventory().getItemInMainHand();
                    boolean stillHolding = plugin.hasItemId(hand, "dacapo");

                    if (count >= (special ? 3 : 5) || !target.isValid() || !stillHolding) {
                        this.cancel();
                        return;
                    }

                    playNote(attacker, target, special ? 17.0 : 4.0, special);

                    target.getNearbyEntities(3.5, 3.5, 3.5).forEach(e -> {
                        if (e instanceof LivingEntity v && !e.equals(attacker) && !e.equals(target)) {
                            if (!(e instanceof Player) && !(e instanceof Tameable t && t.isTamed())) {
                                playNote(attacker, v, (special ? 17.0 : 4.0) * 0.7, special);
                            }
                        }
                    });
                    count++;
                }
            }.runTaskTimer(plugin, 0L, special ? 4L : 2L);
        }
    }

    private void playNote(Player p, LivingEntity v, double d, boolean s) {
        p.setMetadata("lsmp_custom_damage", new FixedMetadataValue(plugin, true));
        try {
            v.damage(d, p);
            v.setNoDamageTicks(0);
            v.getWorld().spawnParticle(Particle.DUST, v.getLocation().add(0, 1, 0), 15,
                    new Particle.DustOptions(s ? Color.WHITE : Color.GRAY, 1.2f));
            v.getWorld().playSound(v.getLocation(),
                    s ? "block.anvil.place" : "block.note_block.harp", 0.8f, 1.5f);
        } finally {
            p.removeMetadata("lsmp_custom_damage", plugin);
        }
    }
}
