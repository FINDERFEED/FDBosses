package com.finderfeed.fdbosses.debug;

import com.finderfeed.fdbosses.content.entities.netzach.netzach_cane_attack.NetzachCaneAttack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class DebugStick extends Item {

    public DebugStick(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (!level.isClientSide){
            if (player.pick(100, 1, false) instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK){
                var block = hit.getBlockPos();
                var shape = level.getBlockState(block).getShape(level, block);
                Vec3 center = block.getBottomCenter().add(0, shape.bounds().maxY, 0);
                NetzachCaneAttack.summon(level, center, player.getLookAngle(), 10, 15, 40);
            }
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }


}
