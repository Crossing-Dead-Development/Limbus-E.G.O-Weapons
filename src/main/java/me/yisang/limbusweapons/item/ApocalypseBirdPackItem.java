package me.yisang.limbusweapons.item;

import me.yisang.limbusweapons.event.WeaponEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/** 終末鳥：右鍵開啟 → 獲得 1 把薄暝，自身消失。 */
public class ApocalypseBirdPackItem extends Item {

    public ApocalypseBirdPackItem(Settings settings) { super(settings); }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
        if (!world.isClient && world instanceof ServerWorld sw) {
            ItemStack stack = user.getStackInHand(hand);
            WeaponEvents.openApocalypseBirdPack(user, sw, stack);
        }
        return ActionResult.SUCCESS;
    }
}
