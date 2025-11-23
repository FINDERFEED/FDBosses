package com.finderfeed.fdbosses.debug;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdbosses.content.entities.geburah.casts.GeburahRayCastingCircle;
import com.finderfeed.fdbosses.content.entities.geburah.distortion_sphere.DistortionSphereEffect;
import com.finderfeed.fdbosses.content.entities.geburah.distortion_sphere.DistortionSphereEffectHandler;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_bell.GeburahBell;
import com.finderfeed.fdbosses.content.entities.geburah.judgement_bird.JudgementBirdEntity;
import com.finderfeed.fdbosses.content.entities.geburah.sins.GeburahTriggerSinEffectPacket;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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


//            int height = 45 * 2;
//            for (int i = 0; i < height; i++) {
//                BlockPos pos = player.getOnPos().offset(0,-i,0);
//
//                float p = i / (height - 1f);
//
//                int radius = (int) Math.ceil(FDEasings.easeIn(1 - p) * 60);
//
//                float percent = FDMathUtil.lerp(0.2f,0.95f,FDEasings.easeOut(1 - p));
//
//                createGeburahArenaCircleAtPos(level, pos, radius,percent, 60);
//            }

            createGeburahArenaCircleAtPos(level,player.getOnPos().below(), 60, 0.95f, 60);



        }else{
//            DistortionSphereEffectHandler.setDistortionSphereEffect(new DistortionSphereEffect(player.getEyePosition().add(player.getLookAngle().scale(50)),
//                    80,80,1, (float) -100));
        }

        return super.use(level, player, hand);
    }

    private static void createGeburahArenaCircleAtPos(Level level, BlockPos pos, int endRadius, float percent, int wholeRadius){

        float startRadius = percent * endRadius;

        for (int x = -wholeRadius; x <= wholeRadius; x++){
            for (int z = -wholeRadius; z <= wholeRadius; z++){

                Vec3 v = new Vec3(x + 0.001f,0,z + 0.001f);
                double len = v.length();

                float angle = (float) FDMathUtil.angleBetweenVectors(v.normalize(),new Vec3(1,0,0)) % (FDMathUtil.FPI / 2);


                float p;
                if (angle < FDMathUtil.FPI / 4){
                    p = angle / (FDMathUtil.FPI / 4);
                }else{
                    p = 1 - (angle - FDMathUtil.FPI / 4) / (FDMathUtil.FPI / 4);
                }


                float currentRadius = FDMathUtil.lerp(startRadius,endRadius, FDEasings.easeInOut(1 - p));

                BlockPos offsetPos = pos.offset(x,0,z);

                if (len < currentRadius){
                    if (level.getBlockState(offsetPos).isAir()) {
                        level.setBlock(offsetPos, Blocks.END_STONE.defaultBlockState(), 2);
                    }
//                    if (level.random.nextFloat() > 0.7f){
//                        for (int i = 1; i < 4 + level.random.nextInt(2); i++){
//                            level.setBlock(pos.offset(x,-i,z), Blocks.END_STONE.defaultBlockState(), 2);
//                        }
//                    }
                }else{
//                    if (!level.getBlockState(offsetPos).is(Blocks.END_STONE)){
//                        level.setBlock(offsetPos, Blocks.STRUCTURE_VOID.defaultBlockState(),2);
//                    }
                }


            }
        }


    }

}
