package me.yisang.limbusweapons.item;

import net.minecraft.item.Item;

// DaCapo：攻擊取消預設傷害，改為 5 連擊（普通）或 3 連擊（特殊 40%）
// 特效邏輯在 WeaponEvents.handleDaCapo
public class DaCapoItem extends Item {
    public DaCapoItem(Settings settings) {
        super(settings);
    }
}
