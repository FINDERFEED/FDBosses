package com.finderfeed.fdbosses.debug;

import com.finderfeed.fdbosses.content.entities.geburah.casts.GeburahRayCastingCircle;
import com.finderfeed.fdbosses.content.entities.geburah.distortion_sphere.DistortionSphereEffect;
import com.finderfeed.fdbosses.content.entities.geburah.distortion_sphere.DistortionSphereEffectHandler;
import com.finderfeed.fdbosses.content.entities.geburah.judgement_bird.JudgementBirdEntity;
import com.finderfeed.fdbosses.content.entities.geburah.sins.GeburahTriggerSinEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class DebugStick extends Item {

    public DebugStick(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (!level.isClientSide){

//            PacketDistributor.sendToPlayer((ServerPlayer) player, new GeburahTriggerSinEffectPacket());

//            Vec3 sppos = player.position().add(0,5,0);
//
//            JudgementBirdEntity.summon(level, sppos, new AABB(
//                    sppos.x -  10,
//                    sppos.y - 2,
//                    sppos.z - 10,
//                    sppos.x + 10,
//                    sppos.y + 2,
//                    sppos.z + 10
//            ));

//            GeburahRayCastingCircle.summon(level, player.getEyePosition().add(player.getLookAngle()), player.getLookAngle());

        }else{
            DistortionSphereEffectHandler.setDistortionSphereEffect(new DistortionSphereEffect(player.getEyePosition().add(player.getLookAngle().scale(50)),
                    80,80,1, (float) -100));
        }

        return super.use(level, player, hand);
    }

}
