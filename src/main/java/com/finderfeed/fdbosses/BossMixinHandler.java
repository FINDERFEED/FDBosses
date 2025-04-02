package com.finderfeed.fdbosses;

import com.finderfeed.fdbosses.content.data_components.ItemCoreDataComponent;
import com.finderfeed.fdbosses.init.BossDataComponents;
import com.finderfeed.fdbosses.init.BossItems;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class BossMixinHandler {

    public static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftingContainer, ResultContainer resultContainer, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci){
        if (!level.isClientSide){

            ServerPlayer serverplayer = (ServerPlayer) player;


            ItemStack weapon = null;
            ItemStack lightningCore = null;

            for (ItemStack itemStack : craftingContainer.getItems()){

                Item item = itemStack.getItem();
                if (item != BossItems.LIGHTNING_CORE && BossUtil.itemContainsModifierForAttribute(itemStack, Attributes.ATTACK_DAMAGE)){
                    if (weapon == null){
                        weapon = itemStack;
                    }else{
                        weapon = null;
                        break;
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

            if (lightningCore != null && weapon != null) {

                ItemStack result = weapon.copy();
                result.set(BossDataComponents.ITEM_CORE.get(),new ItemCoreDataComponent(ItemCoreDataComponent.CoreType.LIGHTNING));

                resultContainer.setItem(0, result);
                menu.setRemoteSlot(0, result);
                serverplayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, result));

                ci.cancel();
            }
        }
    }

}
