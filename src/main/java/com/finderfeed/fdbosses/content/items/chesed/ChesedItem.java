package com.finderfeed.fdbosses.content.items.chesed;

import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.AnimatedItemTickListener;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.FDItemAnimationHandler;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.FDServerItemAnimations;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class ChesedItem extends Item implements AnimatedItemTickListener {

    public ChesedItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide){
            FDServerItemAnimations.startItemAnimation(player, "USE", AnimationTicker.builder(BossAnims.CHESED_ITEM_USE)
                            .setToNullTransitionTime(0)
                    .build(), hand);
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }

    @Override
    public int getUseDuration(ItemStack p_41454_, LivingEntity p_344979_) {
        return 72000;
    }

    @Override
    public void onUseTick(Level p_41428_, LivingEntity entity, ItemStack p_41430_, int p_41431_) {

        super.onUseTick(p_41428_, entity, p_41430_, p_41431_);
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {

        super.onStopUsing(stack, entity, count);

    }

    @Override
    public void animatedItemTick(ItemStack itemStack) {
        var animSystem = FDItemAnimationHandler.getItemAnimationSystem(itemStack);
        animSystem.startAnimation("IDLE", AnimationTicker.builder(BossAnims.CHESED_ITEM_IDLE.get())
                .build());
    }

}
