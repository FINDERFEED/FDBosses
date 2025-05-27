package com.finderfeed.fdbosses.content.recipes;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdbosses.init.BossItems;
import com.finderfeed.fdbosses.init.BossRecipeSerializers;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class LightningCoreRecipe implements CraftingRecipe {

    public static final LightningCoreRecipe UNIT = new LightningCoreRecipe();

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {

        ItemStack weapon = null;
        ItemStack lightningCore = null;

        for (ItemStack itemStack : input.items()){

            Item item = itemStack.getItem();
            if (item != BossItems.LIGHTNING_CORE && BossUtil.itemContainsModifierForAttribute(itemStack, Attributes.ATTACK_DAMAGE)){

                boolean skip = true;

                if (itemStack.has(BossDataComponents.ITEM_CORE)){
                    ItemCoreDataComponent.CoreType coreType = itemStack.get(BossDataComponents.ITEM_CORE).getCoreType();
                    if (coreType != ItemCoreDataComponent.CoreType.LIGHTNING){
                        skip = false;
                    }
                }else{
                    skip = false;
                }

                if (!skip) {
                    if (weapon == null) {
                        weapon = itemStack;
                    } else {
                        weapon = null;
                        break;
                    }
                }

            }else if (item == BossItems.LIGHTNING_CORE.get()){

                if (lightningCore == null){
                    lightningCore = itemStack;
                }else{
                    lightningCore = null;
                    break;
                }
            }else if (!itemStack.isEmpty()){
                lightningCore = null;
                break;
            }
        }

        return lightningCore != null && weapon != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {

        ItemStack weapon = null;
        ItemStack lightningCore = null;

        for (ItemStack itemStack : input.items()){

            Item item = itemStack.getItem();
            if (item != BossItems.LIGHTNING_CORE && BossUtil.itemContainsModifierForAttribute(itemStack, Attributes.ATTACK_DAMAGE)){
                boolean skip = true;

                if (itemStack.has(BossDataComponents.ITEM_CORE)){
                    ItemCoreDataComponent.CoreType coreType = itemStack.get(BossDataComponents.ITEM_CORE).getCoreType();
                    if (coreType != ItemCoreDataComponent.CoreType.LIGHTNING){
                        skip = false;
                    }
                }else{
                    skip = false;
                }

                if (!skip) {
                    if (weapon == null) {
                        weapon = itemStack;
                    } else {
                        weapon = null;
                        break;
                    }
                }

            }else if (item == BossItems.LIGHTNING_CORE.get()){

                if (lightningCore == null){
                    lightningCore = itemStack;
                }else{
                    lightningCore = null;
                    break;
                }
            }else if (!itemStack.isEmpty()){
                lightningCore = null;
                break;
            }
        }

        if (weapon != null && lightningCore != null) {
            ItemStack result = weapon.copy();
            result.set(BossDataComponents.ITEM_CORE.get(), new ItemCoreDataComponent(ItemCoreDataComponent.CoreType.LIGHTNING));
            return result;
        }else{
            return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider result) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BossRecipeSerializers.LIGHTNING_CORE_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<LightningCoreRecipe>{

        @Override
        public MapCodec<LightningCoreRecipe> codec() {
            return MapCodec.unit(UNIT);
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, LightningCoreRecipe> streamCodec() {
            return StreamCodec.unit(UNIT);
        }

    }

}
