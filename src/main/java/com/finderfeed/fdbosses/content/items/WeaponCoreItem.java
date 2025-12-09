package com.finderfeed.fdbosses.content.items;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class WeaponCoreItem extends CoreItem {

    public WeaponCoreItem(Properties props, ItemCoreDataComponent.CoreType coreType, boolean glint, Supplier<Component> abilityText) {
        super(props, coreType, glint, abilityText);
    }

    @Override
    public boolean canBeAppliedTo(ItemStack itemStack) {
        return BossUtil.itemContainsModifierForAttribute(itemStack, Attributes.ATTACK_DAMAGE);
    }

}
