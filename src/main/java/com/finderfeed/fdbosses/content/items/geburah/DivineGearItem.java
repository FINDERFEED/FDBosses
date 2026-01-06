package com.finderfeed.fdbosses.content.items.geburah;

import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossItems;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.AnimatedItemTickListener;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.item.FDItemAnimationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DivineGearItem extends Item implements AnimatedItemTickListener {

    public DivineGearItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {

        var player = ctx.getPlayer();
        if (player instanceof ServerPlayer serverPlayer){
            Direction direction = ctx.getClickedFace();
            if (direction == Direction.UP){
                BlockPos pos = ctx.getClickedPos();
                DivineGear.summon(player, pos.getCenter().add(0,0.5,0));
                if (!player.isCreative()) {
                    player.getCooldowns().addCooldown(BossItems.DIVINE_GEAR.get(), BossConfigs.BOSS_CONFIG.get().itemConfig.divineGearLifetime - 100);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(ctx);
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
