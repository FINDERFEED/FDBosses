package com.finderfeed.fdbosses.content.items;

import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import net.minecraft.world.item.Item;

public class WeaponCoreItem extends Item {

    public final ItemCoreDataComponent.CoreType coreType;

    public WeaponCoreItem(Properties props, ItemCoreDataComponent.CoreType coreType) {
        super(props);
        this.coreType = coreType;
    }

}
