package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.content.entities.geburah.sins.PlayerSinsHandler;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSins;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ExplosiveCrystalItem extends Item {

    public ExplosiveCrystalItem(Properties props) {
        super(props.stacksTo(1).durability(300));
    }

    @Override
    public void inventoryTick(ItemStack item, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        super.inventoryTick(item, level, entity, p_41407_, p_41408_);

        if (!level.isClientSide){
            item.setDamageValue(item.getDamageValue() + 1);
            if (item.getDamageValue() == item.getMaxDamage()){
                if (entity instanceof ServerPlayer player) {
                    PlayerSinsHandler.sin(player, 10);
                }
                item.setCount(0);
            }
        }

    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

}
