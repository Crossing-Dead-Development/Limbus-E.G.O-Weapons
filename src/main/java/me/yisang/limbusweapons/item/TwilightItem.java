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

/**
 * 薄暝 Twilight — 終末鳥 E.G.O。
 * 近戰瀕死增傷 + 部分真實傷害（傷害計算在 WeaponEvents.onAttack）。
 * 潛行右鍵：蓄力 1.5 秒 → 暮光斬（前方扇形波）。
 */
public class TwilightItem extends Item {

    private static final int CHARGE_TICKS = 30; // 1.5 秒

    public TwilightItem(Settings settings) { super(settings); }

    @Override public UseAction getUseAction(ItemStack stack) { return UseAction.SPEAR; }

    @Override public int getMaxUseTime(ItemStack stack, LivingEntity user) { return 72000; }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
        if (!user.isSneaking()) return ActionResult.PASS;
        if (!world.isClient && world instanceof ServerWorld sw) {
            if (!WeaponEvents.twilightSpecialReady(user)) return ActionResult.FAIL;
            WeaponEvents.twilightChargeStart(user, sw);
        }
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        int drawTicks = getMaxUseTime(stack, user) - remainingUseTicks;
        if (!world.isClient && world instanceof ServerWorld sw) {
            WeaponEvents.twilightChargeTick(player, sw);
            if (drawTicks >= CHARGE_TICKS) {
                WeaponEvents.twilightSlash(player, sw);
                player.stopUsingItem();
            }
        }
    }
}
