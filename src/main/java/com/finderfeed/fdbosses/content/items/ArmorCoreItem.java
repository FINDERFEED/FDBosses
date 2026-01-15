package com.finderfeed.fdbosses.content.items;

import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

public class ArmorCoreItem extends CoreItem{

    public ArmorCoreItem(Properties props, ItemCoreDataComponent.CoreType coreType, boolean glint, Supplier<Component> abilityText) {
        super(props, coreType, glint, abilityText);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level p_339594_, List<Component> components, TooltipFlag p_41424_) {
        super.appendHoverText(stack, p_339594_, components, p_41424_);
    }

    @Override
    public boolean canBeAppliedTo(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item instanceof Equipable;
    }

}
