package me.yisang.limbusweapons.item;

import me.yisang.limbusweapons.event.WeaponEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

// 環指筆刷：右鍵生物施加隨機負面效果；第 1.5 秒內對同一目標右鍵第二次觸發雙層效果
public class RingBrushItem extends Item {
    public RingBrushItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
        if (user.getWorld().isClient) return ActionResult.SUCCESS;
        WeaponEvents.handleRingBrush(user, entity);
        return ActionResult.SUCCESS;
    }
}
