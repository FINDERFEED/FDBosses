package com.finderfeed.fdbosses.debug;

import com.finderfeed.fdbosses.content.entities.geburah.distortion_sphere.DistortionSphereEffect;
import com.finderfeed.fdbosses.content.entities.netzach.distorted_slam.DistortedSlamEffect;
import com.finderfeed.fdbosses.content.entities.netzach.distorted_slam.DistortedSlamEffectHandler;
import com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon.clock_o_ball.ClockWyvernClockOBall;
import com.finderfeed.fdbosses.content.entities.netzach.train_attack.TrainAttack;
import com.finderfeed.fdbosses.content.entities.netzach.train_attack.TrainAttackPatterns;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class DebugStick extends Item {

    public DebugStick(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        int attackTime = 100;

        if (!level.isClientSide){
            TrainAttack.summon(level, player.position(), TrainAttackPatterns.MANY_TURNS, 0.75f, 40, 10);
        }else{

        }

        return super.use(level, player, hand);
    }


}
