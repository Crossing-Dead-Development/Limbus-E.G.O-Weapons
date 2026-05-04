package me.yisang.limbus;

import org.bukkit.*;
import org.bukkit.attribute.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import org.bukkit.event.Listener;

public class mimicry implements EGOWeapon, Listener {
    private final LimbusEGOWeapons plugin;

    public mimicry(LimbusEGOWeapons plugin) { this.plugin = plugin; }

    @Override
    public String getId() { return "mimicry"; }

    @Override
    public void give(Player player) {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.translateHexColorCodes("&#FF0000擬態"));
            meta.setLore(List.of(plugin.translateHexColorCodes("&x&F&F&0&0&0&0而那裡有許多聲音齊聲哭喊著同一個字──「主管」")));
            meta.setCustomModelData(1006);
            meta.setUnbreakable(true);
            meta.setItemModel(NamespacedKey.fromString("mimicry:mimicry"));
            meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(new NamespacedKey(plugin, "mimicry_dmg"), 12.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
            meta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(new NamespacedKey(plugin, "mimicry_spd"), -3.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(meta);
        }
        player.getInventory().addItem(item);
    }

    @Override
    public void handleMelee(EntityDamageByEntityEvent event, Player attacker) {
        double damage = event.getFinalDamage();
        if (Math.random() < 0.10) {
            double bonus = 40.0 + (Math.random() * 50.0);
            event.setDamage(event.getDamage() + bonus);
            damage += bonus;
            attacker.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, event.getEntity().getLocation(), 1);
        }
        double heal = damage * 0.25;
        attacker.setHealth(Math.min(attacker.getAttribute(Attribute.MAX_HEALTH).getValue(), attacker.getHealth() + heal));
    }
}