package com.finderfeed.fdbosses.content.items;

import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class WeaponCoreItem extends Item {

    public final ItemCoreDataComponent.CoreType coreType;

    private boolean hasGlint;

    private Component abilityText;

    public WeaponCoreItem(Properties props, ItemCoreDataComponent.CoreType coreType, boolean glint, Component abilityText) {
        super(props);
        this.coreType = coreType;
        this.hasGlint = glint;
        this.abilityText = abilityText;
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return hasGlint;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext p_339594_, List<Component> components, TooltipFlag p_41424_) {
        super.appendHoverText(stack, p_339594_, components, p_41424_);
        components.add(abilityText);
        components.add(Component.translatable("fdbosses.word.core_lose").withStyle(ChatFormatting.RED));
    }
}
