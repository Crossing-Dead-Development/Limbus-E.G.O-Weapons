package me.yisang.limbusweapons.item;

import me.yisang.limbusweapons.event.WeaponEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.item.consume.UseAction;
import net.minecraft.world.World;

/**
 * 莊嚴哀悼 — 拉弓式:
 *  按住右鍵蓄力 → 放開發射蝴蝶彈幕。
 *  拉弓越滿 → 彈丸速度越快;未達最小拉弓時間放開不消耗彈藥。
 *  isBlack=true → 8 傷害 + 凋零;false → 4 傷害 + 失明。
 */
public class SolemnLamentItem extends Item {
    public final boolean isBlack;
    private static final int MIN_DRAW_TICKS = 5;   // 0.25s 起手最小拉弓時間
    private static final int MAX_DRAW_TICKS = 20;  // 1.0s 拉滿

    public SolemnLamentItem(boolean isBlack, Settings settings) {
        super(settings);
        this.isBlack = isBlack;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
        // 沒箭不能拉弓 (創造模式除外)
        if (WeaponEvents.findButterfly(user) == null && !user.getAbilities().creativeMode) {
            return ActionResult.FAIL;
        }
        user.setCurrentHand(hand);
        if (!world.isClient) {
            WeaponEvents.onSolemnLamentDraw(user, world);
        }
        return ActionResult.CONSUME;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return false;
        if (world.isClient) return true;

        int used = getMaxUseTime(stack, user) - remainingUseTicks;
        if (used < MIN_DRAW_TICKS) return false;

        float power = Math.min((float) used / MAX_DRAW_TICKS, 1.0f);
        WeaponEvents.handleSolemnLamentFire(player, (ServerWorld) world, this, power);
        return true;
    }
}
