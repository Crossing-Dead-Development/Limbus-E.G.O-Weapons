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
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;
import org.bukkit.event.Listener;

public class dacapo implements EGOWeapon, Listener {
    private final LimbusEGOWeapons plugin;

    public dacapo(LimbusEGOWeapons plugin) { this.plugin = plugin; }

    @Override
    public String getId() { return "dacapo"; }

    @Override
    public void give(Player player) {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.translateHexColorCodes("&#FFFFFFDaCapo"));
            meta.setLore(List.of(plugin.translateHexColorCodes("&x&F&F&F&F&F&F來自廢墟的最華麗的演出，即將拉開帷幕！")));
            meta.setCustomModelData(1007);
            meta.setUnbreakable(true);
            meta.setItemModel(NamespacedKey.fromString("dacapo:dacapo"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(meta);
        }
        player.getInventory().addItem(item);
    }

    @Override
    public void handleMelee(EntityDamageByEntityEvent event, Player attacker) {
        // 關鍵修正：如果這傷害是由 DaCapo 效果產生的，直接跳過，不要再觸發一次演出
        if (attacker.hasMetadata("lsmp_custom_damage")) return;

        if (event.getEntity() instanceof LivingEntity target) {
            event.setCancelled(true);
            boolean special = Math.random() < 0.40;

            new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    // 檢查玩家手持的是否還是 DaCapo，以及目標是否存活[cite: 8]
                    ItemStack hand = attacker.getInventory().getItemInMainHand();
                    if (count >= (special ? 3 : 5) || !target.isValid() || hand.getType() == Material.AIR) {
                        this.cancel();
                        return;
                    }

                    playNote(attacker, target, special ? 17.0 : 4.0, special);

                    // 範圍傷害邏輯[cite: 8]
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
        // 1. 在造成傷害前，先貼上身分標籤
        p.setMetadata("lsmp_custom_damage", new FixedMetadataValue(plugin, true));

        try {
            // 2. 執行傷害行為
            v.damage(d, p);
            v.setNoDamageTicks(0); // 讓傷害可以連續觸發，不被無敵幀擋住

            // 3. 演出視覺與聽覺效果
            v.getWorld().spawnParticle(Particle.DUST, v.getLocation().add(0, 1, 0), 15,
                    new Particle.DustOptions(s ? Color.WHITE : Color.GRAY, 1.2f));
            v.getWorld().playSound(v.getLocation(), s ? "block.anvil.place" : "block.note_block.harp", 0.8f, 1.5f);
        } finally {
            // 4. 使用 finally 塊，確保不論傷害計算是否噴錯，最後都會移除標籤
            // 這樣能防止標籤殘留導致玩家之後的正常攻擊也沒傷害
            p.removeMetadata("lsmp_custom_damage", plugin);
        }
    }
}