package me.yisang.limbus;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface EGOWeapon {
    String getId(); // 返回代號，如 "mimicry"
    void give(Player player); // 處理物品發放與屬性設定
    default void handleMelee(EntityDamageByEntityEvent event, Player attacker) {} // 近戰邏輯
}