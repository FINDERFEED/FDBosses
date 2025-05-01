package com.finderfeed.fdbosses.content.entities.chesed_boss;

import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.renderer.FDFreeEntityRenderer;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.renderers.QuadRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Random;

public class ChesedRenderer implements FDFreeEntityRenderer<ChesedEntity> {

    @Override
    public void render(ChesedEntity chesedEntity, float yaw, float pticks, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {

        this.renderRayEffect(chesedEntity, yaw, pticks, poseStack, multiBufferSource, light);
        this.renderRockfallRayEffect(chesedEntity, yaw, pticks, poseStack, multiBufferSource, light);
        this.renderFinalAttackCracks(chesedEntity, yaw, pticks, poseStack, multiBufferSource, light);

    }

    public static final ResourceLocation CHESED_RAY_PREPARE = FDBosses.location("textures/util/chesed_ray_prepare.png");

    public static final ResourceLocation FINAL_ATTACK_CRACKS = FDBosses.location("textures/util/chesed_wall_crack.png");

    private void renderFinalAttackCracks(ChesedEntity chesedEntity, float yaw, float pticks, PoseStack poseStack, MultiBufferSource multiBufferSource, int light){

        var system = chesedEntity.getSystem();

        var ticker = system.getTicker(ChesedEntity.FINAL_ATTACK_LAYER);

        if (ticker == null) return;

        float elapsedTime = ticker.getElapsedTime();

        float offset = 149;

        float inTime = ChesedEntity.FINAL_ATTACK_RAY_ROTATION_DURATION - 3;

        float p = Mth.clamp((elapsedTime - offset + pticks) / inTime,0, 1);

        if (p == 0 || elapsedTime > 190) return;

        p = FDEasings.easeInOut(p);

        float crackHeightRad = 1f;

        int cracksAmount = 80;

        float radius = 35.4f;

        float angle = FDMathUtil.FPI * 2f / cracksAmount;

        float passedAngle = FDMathUtil.FPI * 2f * p;

        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.eyes(FINAL_ATTACK_CRACKS));


        int id = 0;

        poseStack.pushPose();

        poseStack.translate(0,1.4,0);

        Random random = new Random(234234);

        for (float i = 0; i < passedAngle - angle;i += angle, id++){

            poseStack.pushPose();

            Matrix4f matrix4f = poseStack.last().pose();

            poseStack.mulPose(Axis.YP.rotation(id * angle + (float)Math.toRadians(-chesedEntity.getYRot() - 90)));

            int texId = 0;

            if (id % 2 == 0){
                texId = random.nextInt(3) + 1;
            }


            vertexConsumer.addVertex(matrix4f,radius, -crackHeightRad, 0).setColor(1f,1f,1f,1f).setUv(texId * 0.25f + 0.25f,0).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(poseStack.last(), -1,0,0);


            vertexConsumer.addVertex(matrix4f,radius, crackHeightRad, 0).setColor(1f,1f,1f,1f).setUv(texId * 0.25f + 0.25f,1).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(poseStack.last(), -1,0,0);

            poseStack.mulPose(Axis.YP.rotation(angle));

            matrix4f = poseStack.last().pose();


            vertexConsumer.addVertex(matrix4f,radius, crackHeightRad, 0).setColor(1f,1f,1f,1f).setUv(texId * 0.25f,1).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(poseStack.last(), -1,0,0);

            vertexConsumer.addVertex(matrix4f,radius, -crackHeightRad, 0).setColor(1f,1f,1f,1f).setUv(texId * 0.25f,0).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(poseStack.last(), -1,0,0);

            poseStack.popPose();
        }


        poseStack.popPose();


    }

    private void renderRockfallRayEffect(ChesedEntity chesedEntity, float yaw, float pticks, PoseStack poseStack, MultiBufferSource multiBufferSource, int light){

        var system = chesedEntity.getSystem();

        var ticker = system.getTicker(ChesedEntity.ROCKFALL_TICKER);

        if (ticker != null){

            float elapsedTime = ticker.getTime(pticks);

            float offset = 20;
            float prepareTime = 27f;
            float offsetP = Mth.clamp((elapsedTime - offset) / prepareTime,0, 1f);

            float easedP = FDEasings.easeOut(offsetP);

            float rotation = 360 * FDEasings.easeOutBack(offsetP);
            float alpha = easedP;
            float directionOffset = -1 * easedP;
            float sizeMod = 0;

            if (elapsedTime - offset - prepareTime > 0){

                float awayTime = 7f * ticker.getSpeedModifier();


                float offsetP2 = Math.clamp((elapsedTime - offset - prepareTime) / awayTime, 0, 1);

                float easedP2 = FDEasings.easeOut(offsetP2);

                alpha = 1 - offsetP2;
                directionOffset = 5 * (easedP2);
                sizeMod = offsetP2 * 2;

            }

            VertexConsumer consumer = multiBufferSource.getBuffer(RenderType.entityTranslucentEmissive(CHESED_RAY_PREPARE));

            Vec3 dir = new Vec3(0,1,0);

            QuadRenderer.start(consumer)
                    .color(1,1,1,alpha)
                    .direction(dir)
                    .pose(poseStack)
                    .offsetOnDirection(2 - directionOffset)
                    .translate(0,1.5f,0)
                    .size(1.5f + sizeMod)
                    .rotationDegrees(rotation)
                    .render();

            QuadRenderer.start(consumer)
                    .color(1,1,1,alpha)
                    .direction(dir)
                    .pose(poseStack)
                    .offsetOnDirection(3 - directionOffset)
                    .translate(0,1.5f,0)
                    .size(0.75f + sizeMod)
                    .rotationDegrees(-rotation)
                    .render();


            float offset2 = 45;

            float offset2P = FDEasings.easeOut(Mth.clamp((elapsedTime - offset2) / 10,0, 1f));

            if (offset2P > 0 && offset2P != 1){

                QuadRenderer.start(consumer)
                        .color(1,1,1,1 - offset2P)
                        .offsetOnDirection(30)
                        .size(offset2P * 40)
                        .pose(poseStack)
                        .render();

                QuadRenderer.start(consumer)
                        .color(1,1,1,1 - offset2P)
                        .offsetOnDirection(30)
                        .size(offset2P * 20)
                        .pose(poseStack)
                        .render();


            }


        }

    }

    private void renderRayEffect(ChesedEntity chesedEntity, float yaw, float pticks, PoseStack poseStack, MultiBufferSource multiBufferSource, int light){

        var system = chesedEntity.getSystem();

        var ticker = system.getTicker(ChesedEntity.RAY_ATTACK_LAYER);

        if (ticker != null){

            float elapsedTime = ticker.getTime(pticks);

            float offset = 10;
            float prepareTime = 27f;
            float offsetP = Mth.clamp((elapsedTime - offset) / prepareTime,0, 1f);

            float easedP = FDEasings.easeOut(offsetP);

            float rotation = 360 * FDEasings.easeOutBack(offsetP);
            float alpha = easedP;
            float directionOffset = 0.5f * (1 - easedP);
            float sizeMod = 0;

            if (elapsedTime - offset - prepareTime > 0){

                float awayTime = 7f * ticker.getSpeedModifier();


                float offsetP2 = Math.clamp((elapsedTime - offset - prepareTime) / awayTime, 0, 1);

                float easedP2 = FDEasings.easeOut(offsetP2);

                alpha = 1 - offsetP2;
                directionOffset = 5 * (easedP2);
                sizeMod = offsetP2 * 2;

            }

            VertexConsumer consumer = multiBufferSource.getBuffer(RenderType.entityTranslucentEmissive(CHESED_RAY_PREPARE));

            Vec3 dir = new Vec3(1,0,0).yRot((float)Math.toRadians(-yaw - 90));

            QuadRenderer.start(consumer)
                    .color(1,1,1,alpha)
                    .direction(dir)
                    .pose(poseStack)
                    .offsetOnDirection(2 - directionOffset)
                    .translate(0,1.5f,0)
                    .size(1.5f + sizeMod)
                    .rotationDegrees(rotation)
                    .render();

            QuadRenderer.start(consumer)
                    .color(1,1,1,alpha)
                    .direction(dir)
                    .pose(poseStack)
                    .offsetOnDirection(3 - directionOffset)
                    .translate(0,1.5f,0)
                    .size(0.75f + sizeMod)
                    .rotationDegrees(-rotation)
                    .render();


        }




    }

}
