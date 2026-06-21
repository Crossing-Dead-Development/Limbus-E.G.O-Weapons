package me.yisang.limbusweapons.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 客戶端消音：莊嚴哀悼（Paper 伺服器上為真實 BOW）射擊時，客戶端會預測播放
 * 原版 entity.arrow.shoot。此 mixin 在聲音播放入口攔下：若該音效是箭矢發射音，
 * 且其位置附近（含自己）有玩家手持莊嚴哀悼，就取消，只留下武器的自訂音效。
 *
 * 用 item_model 元件（Paper 外掛設為 solemnlament:solemn_lament_*）辨識，
 * 因此在 Paper 伺服器上也有效；不影響普通弓。
 */
@Mixin(SoundManager.class)
public class SoundManagerMixin {

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V",
            at = @At("HEAD"), cancellable = true)
    private void limbus$muteSolemnBow(SoundInstance sound, CallbackInfo ci) {
        if (sound == null) return;
        Identifier id = sound.getId();
        if (id == null || !id.equals(Identifier.ofVanilla("entity.arrow.shoot"))) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;

        double sx = sound.getX(), sy = sound.getY(), sz = sound.getZ();
        for (AbstractClientPlayerEntity p : mc.world.getPlayers()) {
            if (p.squaredDistanceTo(sx, sy, sz) > 25.0) continue; // 5 格內
            if (isSolemn(p.getMainHandStack()) || isSolemn(p.getOffHandStack())) {
                ci.cancel();
                return;
            }
        }
    }

    private static boolean isSolemn(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        Identifier model = stack.get(DataComponentTypes.ITEM_MODEL);
        return model != null && model.getPath().contains("solemn_lament");
    }
}
