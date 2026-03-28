package com.finderfeed.fdbosses.content.entities.netzach.netzach_clock_pendulum;

import com.finderfeed.fdbosses.content.util.AttackTimings;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDEntityTransformation;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class NetzachPendulumTransform implements FDEntityTransformation<NetzachClockPendulum> {

    public static final ComplexEasingFunction IN_AND_OUT = ComplexEasingFunction.builder()
            .addArea(0.5f, FDEasings::easeOut)
            .addArea(0.5f, FDEasings::reversedEaseOut)
            .build();

    @Override
    public void apply(NetzachClockPendulum pendulum, PoseStack matrices, float partialTicks) {

        AttackTimings attackTimings = pendulum.getEntityData().get(NetzachClockPendulum.PENDULUM_ATTACK_TIMINGS);


        float time = pendulum.tickCount + partialTicks;

        float appearPercent = attackTimings.getAttackTimingPercent(NetzachClockPendulum.PENDULUM_APPEAR, time);
        float upPercent = FDEasings.easeIn(appearPercent);
        float downPercent = FDEasings.easeIn(attackTimings.getAttackTimingPercent(NetzachClockPendulum.PENDULUM_DISAPPEAR, time));

        float length = pendulum.getAttackLength();
        float swingPercent = pendulum.getSwingPercent(time);


        float offset = FDMathUtil.lerp(0,length * 2,swingPercent) - length * downPercent * 0.5f - length * pendulumLandingEasing(appearPercent);

        float pendulumFromEarthOffset = (1 - IN_AND_OUT.apply(swingPercent)) * 0.5f;
        float height = pendulum.getAttackLength() * 2 + pendulumFromEarthOffset;
        float yAngle = pendulum.getViewYRot(partialTicks);
        float zAngle = (float) Math.atan2(offset, -height);

        matrices.translate(0,pendulumFromEarthOffset,0);
        matrices.mulPose(Axis.YP.rotationDegrees((- yAngle)));
        matrices.translate(0,0,offset);
        matrices.mulPose(Axis.XP.rotation(zAngle + FDMathUtil.FPI));

//        if (attackTimings.isTimeForAttack(NetzachClockPendulum.PENDULUM_WAIT_1, time)) {
//            float wait1Percent = FDEasings.reversedEaseOut(attackTimings.getAttackTimingPercent(NetzachClockPendulum.PENDULUM_WAIT_1, time));
//            float someLittleShake = (float)Math.sin(time * 10) * wait1Percent * 5f;
//            matrices.mulPose(Axis.YP.rotationDegrees(someLittleShake));
//        }
        matrices.translate(0,height * (1 - upPercent) + downPercent * height, 0);

    }

    private float pendulumLandingEasing(float val){

        val = FDEasings.easeIn(val);

        if (val < 0.707){
            val =  2 * val * val;
        }else{
            val =  -9.6518f * (float) Math.pow(val - 0.8535, 2) + 1.206f;
        }

        return val;
    }

}
