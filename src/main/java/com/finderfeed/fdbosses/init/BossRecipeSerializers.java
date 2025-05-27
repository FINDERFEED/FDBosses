package com.finderfeed.fdbosses.init;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.recipes.LightningCoreRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BossRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, FDBosses.MOD_ID);

    public static final Supplier<LightningCoreRecipe.Serializer> LIGHTNING_CORE_RECIPE = RECIPE_SERIALIZERS.register("lightning_core", LightningCoreRecipe.Serializer::new);

}
