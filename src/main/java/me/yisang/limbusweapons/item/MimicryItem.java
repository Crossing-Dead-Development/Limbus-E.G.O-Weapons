package me.yisang.limbusweapons.item;

import net.minecraft.item.Item;

// 擬態：普通攻擊 10% 暴擊（+40~90 傷害）＋ 25% 吸血
// 特效邏輯在 WeaponEvents.handleMimicry
public class MimicryItem extends Item {
    public MimicryItem(Settings settings) {
        super(settings);
    }
}
