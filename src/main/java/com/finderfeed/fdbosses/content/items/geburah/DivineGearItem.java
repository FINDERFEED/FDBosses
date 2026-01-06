package com.finderfeed.fdbosses.content.items.geburah;

import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.AnimatedItemTickListener;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.FDItemAnimationHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DivineGearItem extends Item implements AnimatedItemTickListener {

    public DivineGearItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide){

        }
        return super.use(level, player, hand);
    }

    @Override
    public void animatedItemTick(ItemStack itemStack) {
        var animSystem = FDItemAnimationHandler.getItemAnimationSystem(itemStack);
        if (animSystem != null){
            animSystem.startAnimation("IDLE", AnimationTicker.builder(BossAnims.DIVINE_GEAR_ITEM_IDLE)
                    .build());
        }
    }

}
