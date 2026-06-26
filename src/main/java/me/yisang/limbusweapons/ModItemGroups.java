package me.yisang.limbusweapons;

import me.yisang.limbusweapons.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final RegistryKey<ItemGroup> LIMBUS_GROUP_KEY = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            Identifier.of(LimbusWeaponsMod.MOD_ID, "limbus_ego_weapons")
    );

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, LIMBUS_GROUP_KEY,
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(ModItems.MOD_ICON))
                        .displayName(Text.translatable("itemGroup.limbusweapons.limbus_ego_weapons"))
                        .entries((context, entries) -> {
                            entries.add(ModItems.SOLEMN_LAMENT_BLACK);
                            entries.add(ModItems.SOLEMN_LAMENT_WHITE);
                            entries.add(ModItems.BUTTERFLY_QUARTZ);
                            entries.add(ModItems.SOLEMN_SHIELD);
                            entries.add(ModItems.MIMICRY);
                            entries.add(ModItems.DACAPO);
                            entries.add(ModItems.RING_BRUSH);
                        })
                        .build()
        );
    }
}
