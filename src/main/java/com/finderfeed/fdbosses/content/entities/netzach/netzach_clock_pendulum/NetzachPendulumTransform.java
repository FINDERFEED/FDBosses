package com.finderfeed.fdbosses.content.entities.netzach.netzach_clock_pendulum;

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

        float attackTime = 30;
        float wholeTime = NetzachClockPendulum.PREPARE_TIME * 2 + attackTime;



        float time = (pendulum.tickCount + partialTicks) % wholeTime;

        float upDownPercent = 0;
        if (time <= NetzachClockPendulum.PREPARE_TIME){
            upDownPercent = 1 - FDEasings.easeIn(time / NetzachClockPendulum.PREPARE_TIME);
        }else if (time >= NetzachClockPendulum.PREPARE_TIME + attackTime){
            upDownPercent = (time - NetzachClockPendulum.PREPARE_TIME - attackTime) / NetzachClockPendulum.PREPARE_TIME;
            upDownPercent = FDEasings.easeIn(upDownPercent);
        }


        float length = pendulum.getAttackLength();
        float swingPercent = FDEasings.easeInOut(Mth.clamp((time - NetzachClockPendulum.PREPARE_TIME) / attackTime,0,1));

        float offset = FDMathUtil.lerp(-length,length,swingPercent);

        float pendulumFromEarthOffset = (1 - IN_AND_OUT.apply(swingPercent)) * 0.5f;
        float height = 20 + pendulumFromEarthOffset;
        float yAngle = pendulum.getViewYRot(partialTicks);
        float zAngle = (float) Math.atan2(offset, -height);

        matrices.translate(0,pendulumFromEarthOffset,0);
        matrices.mulPose(Axis.YN.rotationDegrees((180 - yAngle)));
        matrices.translate(0,0,offset);
        matrices.mulPose(Axis.XP.rotation(zAngle + FDMathUtil.FPI));
        matrices.translate(0,height * upDownPercent, 0);

    }

}
