package me.yisang.limbusweapons.item;

import net.minecraft.item.Item;

// 聖宣：持有時每 5 tick 在周圍 5 格內的生物施加緩速，並發射白灰粒子
// 效果邏輯在 WeaponEvents.tickShieldAura
public class SolemnShieldItem extends Item {
    public SolemnShieldItem(Settings settings) {
        super(settings);
    }
}
