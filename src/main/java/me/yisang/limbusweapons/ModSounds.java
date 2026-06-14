package me.yisang.limbusweapons;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    // 使用 solemnlament 命名空間，與資源包的 sounds.json 對應
    public static final SoundEvent SOLEMN_LOAD  = reg("solemnlament", "solemn.load");
    public static final SoundEvent SOLEMN_SHOOT = reg("solemnlament", "solemn.shoot");
    public static final SoundEvent SOLEMN_HIT   = reg("solemnlament", "solemn.hit");

    private static SoundEvent reg(String namespace, String path) {
        Identifier id = Identifier.of(namespace, path);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {
        // 觸發靜態初始化即可
    }
}
