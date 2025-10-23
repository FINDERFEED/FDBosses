package com.finderfeed.fdbosses.content.entities.geburah.justice_hammer;

import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class JusticeHammerAttackRenderer implements FDFreeEntityRenderer<JusticeHammerAttack> {

    @Override
    public void render(JusticeHammerAttack justiceHammerAttack, float yaw, float pticks, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {

        float length = JusticeHammerAttack.ATTACK_LENGTH;
        float width = JusticeHammerAttack.ATTACK_WIDTH;

        float p = JusticeHammerAttack.EASING2.apply(justiceHammerAttack.tickCount + pticks);

        if (p == 0) return;

        VertexConsumer vertex = multiBufferSource.getBuffer(RenderType.lightning());

        float r = 0.3f;
        float g = 0.7f;
        float b = 1f;

        float ySize = 0.7f * p;

        poseStack.pushPose();
        poseStack.translate(0,ySize + 0.01,0);


        float alpha = p;

        QuadRenderer.start(vertex)
                .pose(poseStack)
                .direction(justiceHammerAttack.getLookAngle())
                .offsetOnDirection(length/2)
                .sizeX(width/2)
                .sizeY(ySize)
                .renderBack()
                .color1(r,g,b,0f)
                .color2(r,g,b,0f)
                .color3(r,g,b,alpha)
                .color4(r,g,b,alpha)
                .render();

        QuadRenderer.start(vertex)
                .pose(poseStack)
                .direction(justiceHammerAttack.getLookAngle())
                .offsetOnDirection(-length/2)
                .sizeX(width/2)
                .sizeY(ySize)
                .color1(r,g,b,0f)
                .color2(r,g,b,0f)
                .color3(r,g,b,alpha)
                .color4(r,g,b,alpha)
                .renderBack()
                .render();

        QuadRenderer.start(vertex)
                .pose(poseStack)
                .direction(justiceHammerAttack.getLookAngle().yRot(FDMathUtil.FPI / 2))
                .offsetOnDirection(-width/2)
                .sizeX(length/2)
                .sizeY(ySize)
                .color1(r,g,b,0f)
                .color2(r,g,b,0f)
                .color3(r,g,b,alpha)
                .color4(r,g,b,alpha)
                .renderBack()
                .render();

        QuadRenderer.start(vertex)
                .pose(poseStack)
                .direction(justiceHammerAttack.getLookAngle().yRot(FDMathUtil.FPI / 2))
                .offsetOnDirection(width/2)
                .sizeX(length/2)
                .sizeY(ySize)
                .color1(r,g,b,0f)
                .color2(r,g,b,0f)
                .color3(r,g,b,alpha)
                .color4(r,g,b,alpha)
                .renderBack()
                .render();

        poseStack.popPose();

        poseStack.pushPose();

        QuadRenderer.start(vertex)
                .pose(poseStack)
                .rotationDegrees(-justiceHammerAttack.getYRot())
                .offsetOnDirection(0.01f)
                .sizeX(width/2)
                .sizeY(length/2)
                .color(r,g,b,alpha)
                .render();

        poseStack.popPose();



    }

}
