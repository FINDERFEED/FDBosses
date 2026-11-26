package com.finderfeed.fdbosses.debug;

import com.finderfeed.fdbosses.content.entities.netzach.netzach_cane_attack.NetzachCaneVisualStrike;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DebugStick extends Item {

    public DebugStick(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (!level.isClientSide){
            NetzachCaneVisualStrike.summon(level, player.getEyePosition(), player.getLookAngle(), 10, 10, 10);
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }


}
