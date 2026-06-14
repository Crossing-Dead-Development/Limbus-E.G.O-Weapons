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

    public static Item SOLEMN_LAMENT_BLACK;
    public static Item SOLEMN_LAMENT_WHITE;
    public static Item BUTTERFLY_QUARTZ;
    public static Item SOLEMN_SHIELD;
    public static Item MIMICRY;
    public static Item DACAPO;
    public static Item RING_BRUSH;

    public static void register() {
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
