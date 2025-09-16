package com.finderfeed.fdbosses.content.recipes;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.content.items.WeaponCoreItem;
import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdbosses.init.BossRecipeSerializers;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
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

public class WeaponCoreRecipe implements CraftingRecipe {

    public final ItemCoreDataComponent.CoreType coreType;

    private ResourceLocation id;

    public WeaponCoreRecipe(ResourceLocation id, ItemCoreDataComponent.CoreType coreType){
        this.coreType = coreType;
        this.id = id;
    }

    protected WeaponCoreRecipe(ResourceLocation location, String s){
        this.coreType = ItemCoreDataComponent.CoreType.valueOf(s);
        this.id = location;
    }


    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingContainer input, Level level) {

        var weaponAndCore = this.getWeaponAndCore(input);

        return weaponAndCore != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registryAccess) {

        var weaponAndCore = this.getWeaponAndCore(input);

        if (weaponAndCore == null){
            return ItemStack.EMPTY;
        }

        ItemStack weapon = weaponAndCore.first;
        ItemStack coreStack = weaponAndCore.second;

        if (weapon != null && coreStack != null) {
            ItemStack result = weapon.copy();
            WeaponCoreItem.setItemCore(coreType, result);
            return result;
        }else{
            return ItemStack.EMPTY;
        }
    }

    private Pair<ItemStack, ItemStack> getWeaponAndCore(CraftingContainer input){
        ItemStack weapon = null;
        ItemStack coreStack = null;

        for (ItemStack itemStack : input.getItems()){
            Item item = itemStack.getItem();
            if (!(item instanceof WeaponCoreItem) && BossUtil.itemContainsModifierForAttribute(itemStack, Attributes.ATTACK_DAMAGE)){
                if (weapon != null){
                    return null;
                }

                ItemCoreDataComponent.CoreType coreType = WeaponCoreItem.getItemCore(itemStack);

                if (coreType != null){
                    if (coreType == this.coreType){
                        return null;
                    }
                }
                weapon = itemStack;
            }else if (item instanceof WeaponCoreItem weaponCoreItem){
                if (coreStack != null || weaponCoreItem.coreType != this.coreType){
                    return null;
                }
                coreStack = itemStack;
            }else{
                if (!itemStack.isEmpty()) {
                    return null;
                }
            }

        }

        if (weapon == null || coreStack == null){
            return null;
        }

        return new Pair<>(weapon, coreStack);
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

    public static class Serializer implements RecipeSerializer<WeaponCoreRecipe>{

        public static final NetworkCodec<WeaponCoreRecipe> STREAM_CODEC = NetworkCodec.composite(
                NetworkCodec.RESOURCE_LOCATION, WeaponCoreRecipe::getId,
                NetworkCodec.STRING,v->v.coreType.name(),
                WeaponCoreRecipe::new
        );

        @Override
        public WeaponCoreRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            String coreTypeName = jsonObject.get("core_type").getAsString();
            return new WeaponCoreRecipe(resourceLocation, coreTypeName);
        }

        @Override
        public @Nullable WeaponCoreRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            return STREAM_CODEC.fromNetwork(friendlyByteBuf);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, WeaponCoreRecipe weaponCoreRecipe) {
            STREAM_CODEC.toNetwork(friendlyByteBuf, weaponCoreRecipe);
        }
    }

}
