package com.finderfeed.fdbosses.content.recipes;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.content.items.WeaponCoreItem;
import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdbosses.init.BossItems;
import com.finderfeed.fdbosses.init.BossRecipeSerializers;
import com.finderfeed.fdlib.data_structures.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class WeaponCoreRecipe implements CraftingRecipe {

    public final ItemCoreDataComponent.CoreType coreType;

    public WeaponCoreRecipe(ItemCoreDataComponent.CoreType coreType){
        this.coreType = coreType;
    }

    protected WeaponCoreRecipe(String s){
        this.coreType = ItemCoreDataComponent.CoreType.valueOf(s);
    }


    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {

        var weaponAndCore = this.getWeaponAndCore(input);

        return weaponAndCore != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {

        var weaponAndCore = this.getWeaponAndCore(input);

        if (weaponAndCore == null){
            return ItemStack.EMPTY;
        }

        ItemStack weapon = weaponAndCore.first;
        ItemStack coreStack = weaponAndCore.second;

        if (weapon != null && coreStack != null) {
            ItemStack result = weapon.copy();
            result.set(BossDataComponents.ITEM_CORE.get(), new ItemCoreDataComponent(coreType));
            return result;
        }else{
            return ItemStack.EMPTY;
        }
    }

    private Pair<ItemStack, ItemStack> getWeaponAndCore(CraftingInput input){
        ItemStack weapon = null;
        ItemStack coreStack = null;

        for (ItemStack itemStack : input.items()){
            Item item = itemStack.getItem();
            if (!(item instanceof WeaponCoreItem) && BossUtil.itemContainsModifierForAttribute(itemStack, Attributes.ATTACK_DAMAGE)){
                if (weapon != null){
                    return null;
                }
                if (itemStack.has(BossDataComponents.ITEM_CORE)){
                    ItemCoreDataComponent.CoreType coreType = itemStack.get(BossDataComponents.ITEM_CORE).getCoreType();
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
    public ItemStack getResultItem(HolderLookup.Provider result) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BossRecipeSerializers.WEAPON_CORE.get();
    }

    public static class Serializer implements RecipeSerializer<WeaponCoreRecipe>{

        public static final MapCodec<WeaponCoreRecipe> CODEC = RecordCodecBuilder.mapCodec(p->p.group(
                Codec.STRING.fieldOf("core_type").forGetter(v->v.coreType.name())
        ).apply(p, WeaponCoreRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, WeaponCoreRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,v->v.coreType.name(),
                WeaponCoreRecipe::new
        );

        @Override
        public MapCodec<WeaponCoreRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WeaponCoreRecipe> streamCodec() {
            return STREAM_CODEC;
        }

    }

}
