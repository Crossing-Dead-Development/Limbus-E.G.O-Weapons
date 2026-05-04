package me.yisang.limbus;

import java.util.UUID; // 必須有這行，否則 UUID 會報紅字[cite: 5]
import java.util.Map;  // 必須有這行[cite: 5]
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.*;
import org.bukkit.event.Listener;

public class ringbrush implements EGOWeapon, Listener {
    private final LimbusEGOWeapons plugin;
    // 用來記錄每個玩家最後擊中的目標與時間
    private final Map<UUID, TargetInfo> lastHitTargets = new HashMap<>();
    private final Random random = new Random();

    private final PotionEffectType[] negativeEffects = {
            PotionEffectType.BLINDNESS, PotionEffectType.SLOWNESS,
            PotionEffectType.POISON, PotionEffectType.WEAKNESS,
            PotionEffectType.WITHER
    };

    public ringbrush(LimbusEGOWeapons plugin) { this.plugin = plugin; }

    @Override
    public String getId() { return "brush"; }

    @Override
    public void give(Player player) {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.translateHexColorCodes("&#FFFFFF環指筆刷"));
            meta.setLore(List.of(plugin.translateHexColorCodes("&#FF9500不及格。")));
            meta.setCustomModelData(1001);
            meta.setUnbreakable(true);
            meta.setItemModel(NamespacedKey.fromString("ringbrush:ring_brush"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(meta);
        }
        player.getInventory().addItem(item);
    }

    // 這裡是核心：處理右鍵點擊實體的邏輯
    public void handleInteractEntity(Player player, LivingEntity target) {
        long now = System.currentTimeMillis();
        UUID playerUUID = player.getUniqueId();

        // 檢查是否在 1.5 秒內再次回頭點擊同一個目標
        if (lastHitTargets.containsKey(playerUUID)) {
            TargetInfo info = lastHitTargets.get(playerUUID);
            if (now - info.timeMillis < 1500 && info.targetUUID.equals(target.getUniqueId())) {
                // 觸發「回頭補刀」：造成兩次傷害與特效[cite: 6]
                applyEffect(player, target, 2);
                lastHitTargets.remove(playerUUID); // 觸發完就清除
                return;
            }
        }

        // 第一次擊中：造成一次傷害並衝刺[cite: 6]
        applyEffect(player, target, 1);
        player.setVelocity(player.getLocation().getDirection().multiply(1.2).setY(0.2));
        lastHitTargets.put(playerUUID, new TargetInfo(target.getUniqueId(), now));
    }

    private void applyEffect(Player player, LivingEntity target, int times) {
        for (int i = 0; i < times; i++) {
            target.addPotionEffect(new PotionEffect(negativeEffects[random.nextInt(negativeEffects.length)], 80, 1));
            target.damage(3.5, player);
            // 筆刷專屬的彩色色粉粒子特效[cite: 6]
            target.getWorld().spawnParticle(Particle.DUST, target.getLocation().add(0, 1, 0), 20,
                    new Particle.DustOptions(Color.fromRGB(random.nextInt(255), random.nextInt(100), random.nextInt(100)), 1.5f));
        }
    }

        private record TargetInfo(UUID targetUUID, long timeMillis) {}
    } // 類別結束