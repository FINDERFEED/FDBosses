package com.finderfeed.fdbosses.content.recipes;

import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.content.items.CoreItem;
import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdbosses.init.BossRecipeSerializers;
import com.finderfeed.fdlib.data_structures.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;

public class CoreRecipe implements CraftingRecipe {


    public CoreRecipe(){
    }



    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {

        var weaponAndCore = this.getItemAndCore(input);

        return weaponAndCore != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {

        var weaponAndCore = this.getItemAndCore(input);

        if (weaponAndCore == null){
            return ItemStack.EMPTY;
        }

        ItemStack weapon = weaponAndCore.first;
        ItemStack coreStack = weaponAndCore.second;

        if (weapon != null && coreStack != null) {
            ItemStack result = weapon.copy();
            result.set(BossDataComponents.ITEM_CORE.get(), new ItemCoreDataComponent(((CoreItem)coreStack.getItem()).coreType));
            return result;
        }else{
            return ItemStack.EMPTY;
        }
    }

    private Pair<ItemStack, ItemStack> getItemAndCore(CraftingInput input){

        List<ItemStack> items = input.items().stream().filter(itemstack -> !itemstack.isEmpty()).toList();

        if (items.size() != 2){
            return null;
        }

        ItemStack theItem = null;
        ItemStack coreStack = null;

        ItemStack firstStack = items.get(0);
        ItemStack secondStack = items.get(1);
        if (firstStack.getItem() instanceof CoreItem){
            coreStack = firstStack;
            theItem = secondStack;
        }else if (secondStack.getItem() instanceof CoreItem){
            coreStack = secondStack;
            theItem = firstStack;
        }

        if (coreStack == null) return null;

        CoreItem coreItem = (CoreItem) coreStack.getItem();


        if (coreItem.canBeAppliedTo(theItem)){
            return new Pair<>(theItem, coreStack);
        }else{
            return null;
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
        return BossRecipeSerializers.WEAPON_CORE.get();
    }

    public static class Serializer implements RecipeSerializer<CoreRecipe>{

        private static final CoreRecipe CORE_RECIPE = new CoreRecipe();

        public static final MapCodec<CoreRecipe> CODEC = MapCodec.unit(CORE_RECIPE);

        public static final StreamCodec<RegistryFriendlyByteBuf, CoreRecipe> STREAM_CODEC = StreamCodec.unit(CORE_RECIPE);

        @Override
        public MapCodec<CoreRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CoreRecipe> streamCodec() {
            return STREAM_CODEC;
        }

    }

}
