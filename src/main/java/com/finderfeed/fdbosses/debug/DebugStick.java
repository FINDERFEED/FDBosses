package com.finderfeed.fdbosses.debug;

import com.finderfeed.fdbosses.client.overlay.BossNoteOverlay;
import com.finderfeed.fdbosses.content.boss_notes.BossNote;
import com.finderfeed.fdbosses.content.entities.debris.DebrisEntity;
import com.finderfeed.fdbosses.content.entities.geburah.distortion_sphere.DistortionSphereEffect;
import com.finderfeed.fdbosses.content.entities.netzach.distorted_slam.DistortedSlamEffect;
import com.finderfeed.fdbosses.content.entities.netzach.distorted_slam.DistortedSlamEffectHandler;
import com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon.clock_o_ball.ClockWyvernClockOBall;
import com.finderfeed.fdbosses.content.entities.netzach.netzach_ghost_crush.NetzachGhostCrush;
import com.finderfeed.fdbosses.content.entities.netzach.qte.NetzachQteAttack;
import com.finderfeed.fdbosses.content.entities.netzach.train_attack.TrainAttack;
import com.finderfeed.fdbosses.content.entities.netzach.train_attack.TrainAttackPatterns;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

        if (!level.isClientSide){
            NetzachGhostCrush.summon(level, player.position().add(player.getLookAngle().multiply(1,0,1).normalize().scale(4)), player.getLookAngle().reverse(), 40, 4, 1);
        }else{
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }


}
