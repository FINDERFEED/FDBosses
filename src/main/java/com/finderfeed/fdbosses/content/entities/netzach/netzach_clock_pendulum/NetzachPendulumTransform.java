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

        float upPercent = FDEasings.easeIn(attackTimings.getAttackTimingPercent(NetzachClockPendulum.PENDULUM_APPEAR, time));
        float downPercent = FDEasings.easeIn(attackTimings.getAttackTimingPercent(NetzachClockPendulum.PENDULUM_DISAPPEAR, time));

        float length = pendulum.getAttackLength();
        float swingPercent = FDEasings.easeInOut(attackTimings.getAttackTimingPercent(NetzachClockPendulum.PENDULUM_ATTACK, time));

        float offset = FDMathUtil.lerp(-length,length,swingPercent) - length * downPercent * 0.5f;

        float pendulumFromEarthOffset = (1 - IN_AND_OUT.apply(swingPercent)) * 0.5f;
        float height = 20 + pendulumFromEarthOffset;
        float yAngle = pendulum.getViewYRot(partialTicks);
        float zAngle = (float) Math.atan2(offset, -height);

        matrices.translate(0,pendulumFromEarthOffset,0);
        matrices.mulPose(Axis.YP.rotationDegrees((- yAngle)));
        matrices.translate(0,0,offset);
        matrices.mulPose(Axis.XP.rotation(zAngle + FDMathUtil.FPI));
        matrices.translate(0,height * (1 - upPercent) + downPercent * height, 0);

    }

}
