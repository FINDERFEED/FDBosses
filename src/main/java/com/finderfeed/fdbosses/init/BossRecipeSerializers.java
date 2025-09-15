package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.recipes.WeaponCoreRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, FDBosses.MOD_ID);

    public static final Supplier<WeaponCoreRecipe.Serializer> WEAPON_CORE = RECIPE_SERIALIZERS.register("weapon_core", WeaponCoreRecipe.Serializer::new);

}
