package me.yisang.limbusweapons.item;

import me.yisang.limbusweapons.event.WeaponEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

// 莊嚴哀悼：長按右鍵蓄力（弓式舉臂），放開發射彈幕
// isBlack=true → 8 傷害 + 凋零；false → 4 傷害 + 失明
public class SolemnLamentItem extends Item {
    public final boolean isBlack;

    public SolemnLamentItem(boolean isBlack, Settings settings) {
        super(settings);
        this.isBlack = isBlack;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
        // 沒有彈藥就不舉臂
        if (!user.getAbilities().creativeMode && WeaponEvents.findButterfly(user) == null) {
            return ActionResult.FAIL;
        }
        if (!world.isClient) {
            WeaponEvents.onSolemnLamentDraw(user, world);
        }
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return false;
        if (world.isClient) return false;
        int drawTicks = getMaxUseTime(stack, user) - remainingUseTicks;
        if (drawTicks < 5) return false;
        WeaponEvents.handleSolemnLamentFire(player, (ServerWorld) world, this);
        return true;
    }
}
