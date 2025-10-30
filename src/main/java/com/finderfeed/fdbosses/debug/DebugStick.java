package com.finderfeed.fdbosses.debug;

import com.finderfeed.fdbosses.content.entities.geburah.sins.GeburahTriggerSinEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class DebugStick extends Item {

    public DebugStick(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (!level.isClientSide){

//            for (var dir : new HorizontalCircleRandomDirections(level.random,12,1f)) {
//                level.addParticle(new ColoredJumpingParticleOptions(new FDColor(1f, 0, 0f, 1f), 5, 0.5f, 1f, -1),
//                        player.getX(), player.getY() + 3, player.getZ(), dir.x * 0.5, player.getRandom().nextFloat() * 1f, dir.z * 0.5);
//
//            }

            PacketDistributor.sendToPlayer((ServerPlayer) player, new GeburahTriggerSinEffectPacket());

        }

        return super.use(level, player, hand);
    }

}
