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
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static Item MOD_ICON;
    public static Item SOLEMN_LAMENT_BLACK;
    public static Item SOLEMN_LAMENT_WHITE;
    public static Item BUTTERFLY_QUARTZ;
    public static Item SOLEMN_SHIELD;
    public static Item MIMICRY;
    public static Item DACAPO;
    public static Item RING_BRUSH;
    public static Item TIANTUI_STAR;
    public static Item TIGER_MARK;
    public static Item SAVAGE_TIGER_MARK;
    public static Item CHATUHU;
    public static Item TWILIGHT;
    public static Item APOCALYPSE_BIRD;

    public static void register() {
        MOD_ICON = reg("mod_icon", new Item(key("mod_icon")));

        SOLEMN_LAMENT_BLACK = reg("solemn_lament_black",
                new SolemnLamentItem(true,
                        key("solemn_lament_black").maxCount(1).rarity(Rarity.EPIC)));

        SOLEMN_LAMENT_WHITE = reg("solemn_lament_white",
                new SolemnLamentItem(false,
                        key("solemn_lament_white").maxCount(1).rarity(Rarity.EPIC)));

        BUTTERFLY_QUARTZ = reg("butterfly_quartz",
                new ButterflyQuartzItem(
                        key("butterfly_quartz").maxCount(64).rarity(Rarity.UNCOMMON)));

        SOLEMN_SHIELD = reg("solemn_shield",
                new SolemnShieldItem(
                        key("solemn_shield").maxCount(1).rarity(Rarity.RARE)));

        MIMICRY = reg("mimicry",
                new MimicryItem(
                        key("mimicry").maxCount(1).rarity(Rarity.EPIC)
                                .component(DataComponentTypes.ATTRIBUTE_MODIFIERS,
                                        weaponModifiers("mimicry", 12.0, -3.2))));

        DACAPO = reg("dacapo",
                new DaCapoItem(
                        key("dacapo").maxCount(1).rarity(Rarity.EPIC)
                                .component(DataComponentTypes.ATTRIBUTE_MODIFIERS,
                                        weaponModifiers("dacapo", 7.0, -2.4))));

        RING_BRUSH = reg("ring_brush",
                new RingBrushItem(
                        key("ring_brush").maxCount(1).rarity(Rarity.EPIC)
                                .component(DataComponentTypes.ATTRIBUTE_MODIFIERS,
                                        weaponModifiers("ring_brush", 8.0, -2.4))));

        TIANTUI_STAR = reg("tiantui_star",
                new TiantuiStarItem(
                        key("tiantui_star").maxCount(1).rarity(Rarity.EPIC)
                                .component(DataComponentTypes.ATTRIBUTE_MODIFIERS,
                                        weaponModifiers("tiantui_star", 8.0, -2.4))));

        TIGER_MARK = reg("tiger_mark",
                new Item(key("tiger_mark").maxCount(64).rarity(Rarity.UNCOMMON)));

        SAVAGE_TIGER_MARK = reg("savage_tiger_mark",
                new Item(key("savage_tiger_mark").maxCount(64).rarity(Rarity.RARE)));

        CHATUHU = reg("chatuhu",
                new ChatuhuPackItem(key("chatuhu").maxCount(16).rarity(Rarity.EPIC)));

        TWILIGHT = reg("twilight",
                new TwilightItem(
                        key("twilight").maxCount(1).rarity(Rarity.EPIC)
                                .component(DataComponentTypes.ATTRIBUTE_MODIFIERS,
                                        twilightModifiers())));

        APOCALYPSE_BIRD = reg("apocalypse_bird",
                new ApocalypseBirdPackItem(key("apocalypse_bird").maxCount(16).rarity(Rarity.EPIC)));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(e -> {
            e.add(SOLEMN_LAMENT_BLACK);
            e.add(SOLEMN_LAMENT_WHITE);
            e.add(BUTTERFLY_QUARTZ);
            e.add(SOLEMN_SHIELD);
            e.add(MIMICRY);
            e.add(DACAPO);
            e.add(RING_BRUSH);
            e.add(TIANTUI_STAR);
            e.add(TIGER_MARK);
            e.add(SAVAGE_TIGER_MARK);
            e.add(CHATUHU);
            e.add(TWILIGHT);
            e.add(APOCALYPSE_BIRD);
        });
    }

    /** 薄暝：攻擊+9、攻速-2.4，並加大實體互動距離 +1.5。 */
    private static AttributeModifiersComponent twilightModifiers() {
        return AttributeModifiersComponent.builder()
                .add(EntityAttributes.ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                Identifier.of(LimbusWeaponsMod.MOD_ID, "twilight_damage"),
                                9.0, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ATTACK_SPEED,
                        new EntityAttributeModifier(
                                Identifier.of(LimbusWeaponsMod.MOD_ID, "twilight_speed"),
                                -2.4, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ENTITY_INTERACTION_RANGE,
                        new EntityAttributeModifier(
                                Identifier.of(LimbusWeaponsMod.MOD_ID, "twilight_reach"),
                                1.5, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                .build();
    }

    private static Item.Settings key(String name) {
        return new Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM,
                        Identifier.of(LimbusWeaponsMod.MOD_ID, name)));
    }

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

    private static <T extends Item> T reg(String name, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(LimbusWeaponsMod.MOD_ID, name), item);
    }
}
