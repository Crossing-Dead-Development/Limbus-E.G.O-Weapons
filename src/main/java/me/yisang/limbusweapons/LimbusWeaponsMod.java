package me.yisang.limbusweapons;

import me.yisang.limbusweapons.command.EgoCommand;
import me.yisang.limbusweapons.event.WeaponEvents;
import me.yisang.limbusweapons.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimbusWeaponsMod implements ModInitializer {

    public static final String MOD_ID = "limbusweapons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModSounds.register();
        ModItems.register();
        WeaponEvents.register();
        EgoCommand.register();
        LOGGER.info("Limbus E.G.O Weapons (Fabric) loaded.");
    }
}
