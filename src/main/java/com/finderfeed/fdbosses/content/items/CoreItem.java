package com.finderfeed.fdbosses.content.items;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public abstract class CoreItem extends Item {

    public static final String CORE_TAG_NAME = "weapon_core";

    public final ItemCoreDataComponent.CoreType coreType;

    private boolean hasGlint;

    protected Supplier<Component> abilityText;

    public CoreItem(Properties props, ItemCoreDataComponent.CoreType coreType, boolean glint, Supplier<Component> abilityText) {
        super(props);
        this.coreType = coreType;
        this.hasGlint = glint;
        this.abilityText = abilityText;
    }


    public static void setItemCore(ItemCoreDataComponent.CoreType coreType, ItemStack item){
        CompoundTag tag = getItemTag(item);
        tag.putString(CORE_TAG_NAME, coreType.name());
    }

    public static boolean hasCore(ItemStack item){
        return getItemCore(item) != null;
    }

    public static ItemCoreDataComponent.CoreType getItemCore(ItemStack itemStack){
        CompoundTag tag = getItemTagNoCreate(itemStack);

        if (tag == null){
            return null;
        }

        if (tag.contains(CORE_TAG_NAME)){
            return ItemCoreDataComponent.CoreType.valueOf(tag.getString(CORE_TAG_NAME));
        }else{
            return null;
        }
    }

    public static CompoundTag getItemTagNoCreate(ItemStack itemStack){
        return itemStack.getTagElement(FDBosses.MOD_ID);
    }

    public static CompoundTag getItemTag(ItemStack itemStack){
        return itemStack.getOrCreateTagElement(FDBosses.MOD_ID);
    }


    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return hasGlint;
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> components, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_41422_, components, p_41424_);
        components.add(abilityText.get());
        components.add(Component.translatable("fdbosses.word.core_lose").withStyle(ChatFormatting.RED));
    }

    public abstract boolean canBeAppliedTo(ItemStack itemStack);

}
