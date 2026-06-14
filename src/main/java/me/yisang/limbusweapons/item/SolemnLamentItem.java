package me.yisang.limbusweapons.item;

import me.yisang.limbusweapons.event.WeaponEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

// 莊嚴哀悼：右鍵消耗蝴蝶石英，發射彈幕
// isBlack=true → 8 傷害 + 凋零；false → 4 傷害 + 失明
public class SolemnLamentItem extends Item {
    public final boolean isBlack;

    public SolemnLamentItem(boolean isBlack, Settings settings) {
        super(settings);
        this.isBlack = isBlack;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
        if (world.isClient) return ActionResult.SUCCESS;

        boolean fired = WeaponEvents.handleSolemnLamentUse(
                user, (net.minecraft.server.world.ServerWorld) world, this);
        return fired ? ActionResult.SUCCESS : ActionResult.FAIL;
    }
}
