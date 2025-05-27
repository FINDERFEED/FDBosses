package com.finderfeed.fdbosses.mixin;

import com.finderfeed.fdbosses.BossMixinHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
public class CraftingMenuMixin {

//
//    @Inject(method = "slotChangedCraftingGrid", at = @At("HEAD"), cancellable = true)
//    private static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftingContainer, ResultContainer resultContainer, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci){
////        BossMixinHandler.slotChangedCraftingGrid(menu, level, player, craftingContainer, resultContainer, recipe, ci);
//    }


}
