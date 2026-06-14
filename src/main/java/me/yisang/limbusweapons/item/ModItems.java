package me.yisang.limbusweapons.item;

import me.yisang.limbusweapons.LimbusWeaponsMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    // ── 莊嚴哀悼 ─────────────────────────────────────────────────────────────
    public static final Item SOLEMN_LAMENT_BLACK = new SolemnLamentItem(true,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
    public static final Item SOLEMN_LAMENT_WHITE = new SolemnLamentItem(false,
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC));

    // ── 蝴蝶石英（彈藥）──────────────────────────────────────────────────────
    public static final Item BUTTERFLY_QUARTZ = new ButterflyQuartzItem(
            new Item.Settings().maxCount(64).rarity(Rarity.UNCOMMON));

    // ── 聖宣盾牌 ─────────────────────────────────────────────────────────────
    public static final Item SOLEMN_SHIELD = new SolemnShieldItem(
            new Item.Settings().maxCount(1).rarity(Rarity.RARE));

    // ── 擬態 (+12 攻擊, -3.2 速度) ───────────────────────────────────────────
    public static final Item MIMICRY = new MimicryItem(
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
                    .component(DataComponentTypes.ATTRIBUTE_MODIFIERS,
                            weaponModifiers("mimicry", 12.0, -3.2)));

    // ── DaCapo (+7 攻擊, -2.4 速度；實際傷害由事件處理) ──────────────────────
    public static final Item DACAPO = new DaCapoItem(
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
                    .component(DataComponentTypes.ATTRIBUTE_MODIFIERS,
                            weaponModifiers("dacapo", 7.0, -2.4)));

    // ── 環指筆刷 (+8 攻擊, -2.4 速度) ────────────────────────────────────────
    public static final Item RING_BRUSH = new RingBrushItem(
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
                    .component(DataComponentTypes.ATTRIBUTE_MODIFIERS,
                            weaponModifiers("ring_brush", 8.0, -2.4)));

    // ── 工廠方法 ──────────────────────────────────────────────────────────────

    private static AttributeModifiersComponent weaponModifiers(String id, double damage, double speed) {
        return AttributeModifiersComponent.builder()
                .add(EntityAttributes.ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                Identifier.of(LimbusWeaponsMod.MOD_ID, id + "_damage"),
                                damage,
                                EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ATTACK_SPEED,
                        new EntityAttributeModifier(
                                Identifier.of(LimbusWeaponsMod.MOD_ID, id + "_speed"),
                                speed,
                                EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                .build();
    }

    // ── 註冊 ──────────────────────────────────────────────────────────────────

    public static void register() {
        reg("solemn_lament_black", SOLEMN_LAMENT_BLACK);
        reg("solemn_lament_white", SOLEMN_LAMENT_WHITE);
        reg("butterfly_quartz",    BUTTERFLY_QUARTZ);
        reg("solemn_shield",       SOLEMN_SHIELD);
        reg("mimicry",             MIMICRY);
        reg("dacapo",              DACAPO);
        reg("ring_brush",          RING_BRUSH);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(e -> {
            e.add(SOLEMN_LAMENT_BLACK);
            e.add(SOLEMN_LAMENT_WHITE);
            e.add(BUTTERFLY_QUARTZ);
            e.add(SOLEMN_SHIELD);
            e.add(MIMICRY);
            e.add(DACAPO);
            e.add(RING_BRUSH);
        });
    }

    private static void reg(String name, Item item) {
        Registry.register(Registries.ITEM, Identifier.of(LimbusWeaponsMod.MOD_ID, name), item);
    }
}
