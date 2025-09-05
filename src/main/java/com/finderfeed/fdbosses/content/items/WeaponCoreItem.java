package com.finderfeed.fdbosses.content.items;

import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WeaponCoreItem extends Item {

    public final ItemCoreDataComponent.CoreType coreType;

    private boolean hasGlint;

    public WeaponCoreItem(Properties props, ItemCoreDataComponent.CoreType coreType, boolean glint) {
        super(props);
        this.coreType = coreType;
        this.hasGlint = glint;
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return hasGlint;
    }
}
