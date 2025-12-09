package com.finderfeed.fdbosses.content.items;

import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.function.Supplier;

public abstract class CoreItem extends Item {

    public final ItemCoreDataComponent.CoreType coreType;

    private boolean hasGlint;

    protected Supplier<Component> abilityText;

    public CoreItem(Properties props, ItemCoreDataComponent.CoreType coreType, boolean glint, Supplier<Component> abilityText) {
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
        components.add(abilityText.get());
        components.add(Component.translatable("fdbosses.word.core_lose").withStyle(ChatFormatting.RED));
    }

    public abstract boolean canBeAppliedTo(ItemStack itemStack);

}
