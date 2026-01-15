package com.finderfeed.fdbosses.content.recipes;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.content.items.CoreItem;
import com.finderfeed.fdbosses.content.items.WeaponCoreItem;
import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdbosses.init.BossRecipeSerializers;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CoreRecipe implements CraftingRecipe {


    private ResourceLocation id;

    public CoreRecipe(ResourceLocation id){
        this.id = id;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingContainer input, Level level) {
        var weaponAndCore = this.getItemAndCore(input);
        return weaponAndCore != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registryAccess) {

        var weaponAndCore = this.getItemAndCore(input);

        if (weaponAndCore == null){
            return ItemStack.EMPTY;
        }

        ItemStack weapon = weaponAndCore.first;
        ItemStack coreStack = weaponAndCore.second;

        if (weapon != null && coreStack != null) {
            ItemStack result = weapon.copy();
            CoreItem.setItemCore(((CoreItem)coreStack.getItem()).coreType, result);
            return result;
        }else{
            return ItemStack.EMPTY;
        }
    }

    private Pair<ItemStack, ItemStack> getItemAndCore(CraftingContainer input){

        List<ItemStack> items = input.getItems().stream().filter(itemstack -> !itemstack.isEmpty()).toList();

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
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BossRecipeSerializers.WEAPON_CORE.get();
    }

    public static class Serializer implements RecipeSerializer<CoreRecipe>{

        public static final NetworkCodec<CoreRecipe> STREAM_CODEC = NetworkCodec.composite(
                NetworkCodec.RESOURCE_LOCATION, CoreRecipe::getId,
                CoreRecipe::new
        );

        @Override
        public CoreRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            return new CoreRecipe(resourceLocation);
        }

        @Override
        public @Nullable CoreRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            return STREAM_CODEC.fromNetwork(friendlyByteBuf);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, CoreRecipe weaponCoreRecipe) {
            STREAM_CODEC.toNetwork(friendlyByteBuf, weaponCoreRecipe);
        }
    }

}
