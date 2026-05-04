package me.yisang.limbus;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class solemnlament {
    private final LimbusEGOWeapons plugin;
    // --- 新增：定義隱形印章的 Key ---
    private final NamespacedKey ITEM_ID_KEY;

    public solemnlament(LimbusEGOWeapons plugin) {
        this.plugin = plugin;
        // 初始化 Key
        this.ITEM_ID_KEY = new NamespacedKey(plugin, "item_id");
    }

    // --- 輔助判斷工具 ---
    public boolean hasId(ItemStack item, String id) {
        if (item == null || !item.hasItemMeta()) return false;
        // 獲取印章數值
        String value = item.getItemMeta().getPersistentDataContainer().get(ITEM_ID_KEY, PersistentDataType.STRING);
        // 確保 value 不是 null 且完全匹配 id[cite: 3]
        return value != null && value.equals(id);
    }

    public boolean isButterfly(ItemStack item) {
        if (item == null || item.getType() != Material.QUARTZ) return false;
        return hasId(item, "butterfly"); // 直接用印章判定，最準確！[cite: 3, 5]
    }

    public boolean isSolemnLament(ItemStack item) {
        return hasId(item, "solemn_lament");
    }

    // 更新：現在只認印章，不再理會 model 字串裡有沒有 "butterflies"
    public boolean hasButterflyQuartz(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isButterfly(item)) return true;
        }
        return false;
    }

    // --- 事件處理邏輯 ---
    public void handleShoot(EntityShootBowEvent event, Player player, String model) {
        if (!hasButterflyQuartz(player)) {
            event.setCancelled(true);
            return;
        }

        player.getWorld().playSound(player.getLocation(), "solemnlament:solemn.shoot", 1.0f, 1.0f);
    }

    public void handleArrowHit(EntityDamageByEntityEvent event, Arrow arrow, Player shooter) {
        arrow.getWorld().playSound(arrow.getLocation(), "solemnlament:solemn.hit", 1.0f, 1.0f);
        arrow.getWorld().spawnParticle(Particle.SQUID_INK, arrow.getLocation(), 15, 0.1, 0.1, 0.1, 0.05);

        if (event.getEntity() instanceof LivingEntity target) {
            ItemStack weapon = shooter.getInventory().getItemInMainHand();
            // 改用印章判斷武器，避免名稱顏色代碼干擾
            if (isSolemnLament(weapon)) {
                String modelStr = (weapon.getItemMeta().getItemModel() != null) ? weapon.getItemMeta().getItemModel().toString() : "";
                if (modelStr.contains("black")) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 80, 1));
                } else if (modelStr.contains("white")) {
                    event.setDamage(event.getDamage() * 0.5);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
                }
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    target.setArrowsInBody(0);
                    arrow.remove();
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    public void handleShieldTick(Player player, ItemStack item) {
        if (item == null || !item.hasItemMeta() || item.getItemMeta().getItemModel() == null) return;
        if (item.getItemMeta().getItemModel().toString().contains("solemn_lament_shield")) {
            player.getWorld().spawnParticle(Particle.WHITE_ASH, player.getLocation().add(0, 1, 0), 8, 0.4, 0.4, 0.4, 0.02);
            player.getNearbyEntities(5, 5, 5).forEach(e -> {
                if (e instanceof LivingEntity target && !e.equals(player)) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 1));
                }
            });
        }
    }

    // --- 給予物品邏輯 ---
    public void give(Player player, String type) {
        switch (type.toLowerCase()) {
            case "black" -> giveItem(player, Material.BOW, 1002, "&#333333&l莊嚴哀悼", "&x&F&F&F&F&F&F人&x&D&1&D&1&D&1死&x&A&3&A&3&A&3後&x&7&4&7&4&7&4會&x&4&6&4&6&4&6去&x&7&4&7&4&7&4往&x&A&3&A&3&A&3何&x&D&1&D&1&D&1方&x&F&F&F&F&F&F？", "solemnlament:solemn_lament_black", "solemn_lament");
            case "white" -> giveItem(player, Material.CROSSBOW, 1003, "&#FFFFFF&l莊嚴哀悼", "&x&F&F&F&F&F&F人&x&D&1&D&1&D&1死&x&A&3&A&3&A&3後&x&7&4&7&4&7&4會&x&4&6&4&6&4&6去&x&7&4&7&4&7&4往&x&A&3&A&3&A&3何&x&D&1&D&1&D&1方&x&F&F&F&F&F&F？", "solemnlament:solemn_lament_white", "solemn_lament");
            case "butterflies" -> giveItem(player, Material.QUARTZ, 1004, "&#FFFFFF生&#D8D8D8蝶&#B1B1B1、&#8A8A8A亡&#636363蝶", "&x&F&F&F&F&F&F人&x&D&1&D&1&D&1死&x&A&3&A&3&A&3後&x&7&4&7&4&7&4會&x&4&6&4&6&4&6去&x&7&4&7&4&7&4往&x&A&3&A&3&A&3何&x&D&1&D&1&D&1方&x&F&F&F&F&F&F？", "solemnlament:butterflies", "butterfly");
            case "shield" -> giveItem(player, Material.SHIELD, 1005, "&#FFFFFF&l聖宣", "&x&F&F&F&F&F&F人&x&D&1&D&1&D&1死&x&A&3&A&3&A&3後&x&7&4&7&4&7&4會&x&4&6&4&6&4&6去&x&7&4&7&4&7&4往&x&A&3&A&3&A&3何&x&D&1&D&1&D&1方&x&F&F&F&F&F&F？", "solemnlament:solemn_lament_shield", "solemn_shield");
        }
    }

    private void giveItem(Player player, Material material, int cmdData, String name, String lore, String model, String id) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.translateHexColorCodes(name));
            meta.setLore(List.of(plugin.translateHexColorCodes(lore)));
            meta.setCustomModelData(cmdData);
            meta.setItemModel(NamespacedKey.fromString(model));
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);

            // 在這裡蓋上隱形印章
            meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, id);

            item.setItemMeta(meta);
        }
        player.getInventory().addItem(item);
    }
    public void handleShootManual(Player player, ItemStack bow, String model) {
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        arrow.setVelocity(player.getLocation().getDirection().multiply(3.0)); // 模擬弩的初速
        arrow.setDamage(8.0);
        // --- 優化標籤邏輯 ---
        // 統一使用字串標籤，這樣 onHit 才能判斷顏色
        if (model.contains("black")) {
            arrow.setMetadata("solemn_arrow", new FixedMetadataValue(plugin, "black"));
        } else {
            arrow.setMetadata("solemn_arrow", new FixedMetadataValue(plugin, "white"));
        }

        // --- 補上粒子特效 ---
        // 既然手動生成了箭，就要手動啟動粒子追蹤任務
        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isDead() || !arrow.isValid() || arrow.isOnGround()) {
                    this.cancel();
                    return;
                }
                // 莊嚴哀悼的標誌性粒子
                arrow.getWorld().spawnParticle(Particle.SQUID_INK, arrow.getLocation(), 2, 0.02, 0.02, 0.02, 0.01);
                arrow.getWorld().spawnParticle(Particle.WHITE_ASH, arrow.getLocation(), 4, 0.05, 0.05, 0.05, 0.01);
            }
        }.runTaskTimer(plugin, 0L, 1L);

        player.getWorld().playSound(player.getLocation(), "solemnlament:solemn.shoot", 0.8f, 1.0f);
    }
}